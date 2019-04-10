package com.aaron.client.feign

import com.aaron.common.api.pojo.Result
import com.aaron.server.api.pojo.User
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

/**
 * Created by Aaron Sheng on 2019/4/10.
 */
@FeignClient("server")
interface UserFeign {
    @ApiOperation("获取用户信息")
    @GetMapping("/users/{userId}")
    fun get(
        @ApiParam(value = "用户ID", required = true)
        @PathVariable("userId")
        userId: Long
    ): Result<User>

    @ApiOperation("创建用户")
    @PostMapping("/users/")
    fun create(
        @ApiParam(value = "用户名", required = true)
        @RequestParam("name")
        name: String
    ): Result<Boolean>
}