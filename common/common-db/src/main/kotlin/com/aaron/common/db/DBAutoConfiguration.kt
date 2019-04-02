package com.aaron.common.db

import com.mysql.jdbc.Driver
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.PropertySource
import org.springframework.core.Ordered
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

/**
 * Created by Aaron Sheng on 2019/4/2.
 */
@Configuration
@PropertySource("classpath:/common-db.properties")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(DataSourceAutoConfiguration::class, JooqAutoConfiguration::class)
@EnableTransactionManagement
class DBAutoConfiguration {

    @Value("\${spring.datasource.url:#{null}}")
    private val datasourceUrl: String? = null
    @Value("\${spring.datasource.username:#{null}}")
    private val datasourceUsername: String? = null
    @Value("\${spring.datasource.password:#{null}}")
    private val datasourcePassword: String? = null

    @Bean
    @Primary
    fun dataSource(): DataSource {
        if (datasourceUrl == null || datasourceUrl!!.isBlank()) {
            throw IllegalArgumentException("Database connection address is not configured")
        }
        return HikariDataSource().apply {
            poolName = "DBPool-Main"
            jdbcUrl = datasourceUrl
            username = datasourceUsername
            password = datasourcePassword
            driverClassName = Driver::class.java.name
            minimumIdle = 10
            maximumPoolSize = 50
            idleTimeout = 60000
        }
    }
}