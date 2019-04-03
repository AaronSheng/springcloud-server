package com.aaron.client.resources

import com.aaron.client.api.UserResource
import com.aaron.client.api.pojo.User
import com.aaron.client.service.UserService
import com.aaron.common.api.pojo.Result
import com.aaron.common.web.RestResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@RestResource
class UserResourceImpl @Autowired constructor(
    private val userService: UserService
) : UserResource {
    override fun get(userId: Long): Result<User> {
        logger.info("User get userId($userId)")
        return Result(userService.getUser(userId))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}