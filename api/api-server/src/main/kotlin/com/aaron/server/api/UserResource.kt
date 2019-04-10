package com.aaron.server.api

import com.aaron.server.api.pojo.User
import com.aaron.common.api.pojo.Result
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.*

/**
 * Created by Aaron Sheng on 2018/6/12.
 */
@Api(tags = ["USER_USER"], description = "用户-用户信息")
@RequestMapping("/users")
interface UserResource {
    @ApiOperation("获取用户信息")
    @GetMapping("/{userId}")
    fun get(
        @ApiParam(value = "用户ID", required = true)
        @PathVariable("userId")
        userId: Long
    ): Result<User>

    @ApiOperation("创建用户")
    @PostMapping("/")
    fun create(
        @ApiParam(value = "用户名", required = true)
        @RequestParam("name")
        name: String
    ): Result<Boolean>
}