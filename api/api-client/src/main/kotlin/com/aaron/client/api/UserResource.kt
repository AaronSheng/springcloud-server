package com.aaron.client.api

import com.aaron.client.api.pojo.User
import com.aaron.common.api.pojo.Result
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Api(tags = ["USER_USER"], description = "用户-用户信息")
@RequestMapping("/users")
interface UserResource {
    @ApiOperation("获取用户信息")
    @GetMapping("/{userId}")
    fun get(
        @ApiParam(value = "用户ID", required = true)
        @PathVariable("userId") userId: Long
    ): Result<User>
}