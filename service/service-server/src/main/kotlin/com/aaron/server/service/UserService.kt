package com.aaron.server.service

import com.aaron.server.api.pojo.User
import com.aaron.server.dao.UserDao
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@Service
class UserService @Autowired constructor(
    private val dslContext: DSLContext,
    private val userDao: UserDao
){
    fun getUser(id: Long): User {
        val user = userDao.get(dslContext, id)
        return User(id, user.name)
    }

    fun createUser(name: String): Long {
        return userDao.create(dslContext, name)
    }
}