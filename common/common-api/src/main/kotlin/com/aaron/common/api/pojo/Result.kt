package com.aaron.common.api.pojo

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by Aaron Sheng on 2018/6/12.
 */
@ApiModel("返回数据")
data class Result<out T>(
    @ApiModelProperty(value = "状态码", required = true)
    val status: Int,
    @ApiModelProperty(value = "错误信息", required = true)
    val message: String? = null,
    @ApiModelProperty(value = "返回数据", required = false)
    val data: T? = null
) {
    constructor(data: T)  : this(0, null, data)
    constructor(status: Int, message: String): this(status, message, null)

    @JsonIgnore
    fun isOk(): Boolean{
        return status == 0
    }

    @JsonIgnore
    fun isNotOk() : Boolean {
        return status != 0
    }
}