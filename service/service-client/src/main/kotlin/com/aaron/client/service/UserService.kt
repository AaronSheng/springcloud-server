package com.aaron.client.service

import com.aaron.client.api.pojo.User
import com.aaron.common.client.Client
import com.aaron.server.api.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Service
class UserService @Autowired constructor(
    private val client: Client
) {
    fun getUser(id: Long): User {
        val user = client.get(UserResource::class).get(id).data!!
        return User(id, user.name)
    }
}