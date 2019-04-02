package com.aaron.server.api.pojo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by Aaron Sheng on 2018/6/12.
 */
@ApiModel("用户-用户信息")
data class User(
    @ApiModelProperty("用户ID", required = true)
    val id: Long,
    @ApiModelProperty("用户名", required = true)
    val name: String
)