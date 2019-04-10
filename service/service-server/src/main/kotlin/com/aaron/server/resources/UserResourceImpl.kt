package com.aaron.server.resources

import com.aaron.server.api.UserResource
import com.aaron.server.api.pojo.User
import com.aaron.server.service.UserService
import com.aaron.common.api.pojo.Result
import com.aaron.common.web.RestResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@RestController
class UserResourceImpl @Autowired constructor(
    private val userService: UserService
) : UserResource {
    override fun get(userId: Long): Result<User> {
        logger.info("User get userId($userId)")
        return Result(userService.getUser(userId))
    }

    override fun create(name: String): Result<Boolean> {
        logger.info("User create user($name)")
        userService.createUser(name)
        return Result(true)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}