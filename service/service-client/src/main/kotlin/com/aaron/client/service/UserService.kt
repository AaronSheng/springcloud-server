package com.aaron.client.service

import com.aaron.client.api.pojo.User
import com.aaron.common.client.Client
import com.aaron.common.redis.RedisLock
import com.aaron.server.api.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Service
class UserService @Autowired constructor(
    private val client: Client,
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun getUser(id: Long): User {
        while (true) {
            val redisLock = RedisLock(redisTemplate, "user_$id", 30)
            redisLock.use {
                if (!redisLock.tryLock()) {
                    return@use
                }
                val user = client.get(UserResource::class).get(id).data!!
                return User(id, user.name)
            }
        }
    }
}