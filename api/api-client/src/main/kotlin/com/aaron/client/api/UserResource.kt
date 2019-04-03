package com.aaron.client.api

import com.aaron.client.api.pojo.User
import com.aaron.common.api.pojo.Result
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Api(tags = ["USER_USER"], description = "用户-用户信息")
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface UserResource {
    @ApiOperation("获取用户信息")
    @Path("/{userId}")
    @GET
    fun get(
        @ApiParam(value = "用户ID", required = true)
        @PathParam("userId")
        userId: Long
    ): Result<User>
}