package com.aaron.common.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.types.Expiration
import java.io.Closeable
import java.util.UUID
import java.util.concurrent.TimeUnit

class RedisLock(
    private val redisTemplate: RedisTemplate<String, String>,
    private val lockKey: String,
    private val expiredTimeInSeconds: Long
) : Closeable {
    companion object {
        private val logger = LoggerFactory.getLogger(RedisLock::class.java)

        /**
         * lua脚本cas进行比较再删除
         */
        private val UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end"
    }

    private val lockValue = UUID.randomUUID().toString()

    private var locked = false

    /**
     * 尝试获取锁，直到获取为止
     */
    fun lock() {
        logger.info("RedisLock lock key: $lockKey, value: $lockValue")
        while (true) {
            val success = set(lockKey, lockValue, expiredTimeInSeconds)
            if (success) {
                locked = true
                return
            }
            Thread.sleep(100)
        }
    }

    /**
     * 尝试获取锁 立即返回
     *
     * @return 是否成功获得锁
     */
    fun tryLock(): Boolean {
        logger.info("RedisLock tryLock key: $lockKey, value: $lockValue")
        val success = set(lockKey, lockValue, expiredTimeInSeconds)
        if (success) {
            locked = true
        }
        return success
    }

    /**
     * 重写redisTemplate的set方法
     * 命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
     *
     * @param key 锁的Key
     * @param value 锁里面的值
     * @param seconds 超时时间（秒）
     * @return
     */
    private fun set(key: String, value: String, seconds: Long): Boolean {
        var result = false
        redisTemplate.execute { connection ->
            val setResult = connection.set(
                key.toByteArray(),
                value.toByteArray(),
                Expiration.from(seconds, TimeUnit.SECONDS),
                RedisStringCommands.SetOption.SET_IF_ABSENT
            )
            result = (setResult == true)
        }
        return result
    }

    /**
     * 解锁
     * 可以通过以下修改，让这个锁实现更健壮：
     * 不使用固定的字符串作为键的值，而是设置一个不可猜测（non-guessable）的长随机字符串，作为口令串（token）。
     * 不使用 DEL 命令来释放锁，而是发送一个 Lua 脚本，这个脚本只在客户端传入的值和键的口令串相匹配时，才对键进行删除。
     * 这两个改动可以防止持有过期锁的客户端误删现有锁的情况出现。
     */
    fun unlock(): Boolean {
        logger.info("RedisLock unlock key: $lockKey, value: $lockValue")
        if (locked) {
            var result = false
            redisTemplate.execute { connection ->
                val evalResult = connection.eval<Boolean>(
                    UNLOCK_LUA.toByteArray(),
                    ReturnType.BOOLEAN,
                    1,
                    lockKey.toByteArray(),
                    lockValue.toByteArray()
                )
                result = (evalResult == true)
            }
            if (result) {
                locked = false
            }
            return result
        }
        return true
    }

    override fun close() {
        unlock()
    }
}