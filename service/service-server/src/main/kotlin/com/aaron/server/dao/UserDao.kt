package com.aaron.server.dao

import com.aaron.model.server.tables.User
import com.aaron.model.server.tables.records.UserRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import javax.ws.rs.NotFoundException

/**
 * Created by Aaron Sheng on 2019/4/2.
 */

@Repository
class UserDao {
    fun get(dslContext: DSLContext, id: Long): UserRecord {
        with(User.USER) {
            return dslContext.selectFrom(this)
                .where(ID.eq(id))
                .fetchOne() ?: throw NotFoundException("User $id not found")
        }
    }

    fun getOrNull(dslContext: DSLContext, id: Long): UserRecord? {
        with(User.USER) {
            return dslContext.selectFrom(this)
                .where(ID.eq(id))
                .fetchOne()
        }
    }

    fun create(dslContext: DSLContext, name: String): Long {
        with(User.USER) {
            val record = dslContext.insertInto(this, NAME)
                .values(name)
                .returning(ID)
                .fetchOne()
            return record.id
        }
    }
}