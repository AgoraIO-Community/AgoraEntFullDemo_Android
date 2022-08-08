package com.agora.entfulldemo.api.base

import com.agora.entfulldemo.api.ServiceErrorCode
import com.google.gson.annotations.SerializedName

open class BaseResponse<T> : BaseModel() {
    @SerializedName("errorCode", alternate = ["code"])
    var code: Int? = null

    @SerializedName("message", alternate = ["msg"])
    var message: String? = ""

    @SerializedName(value = "obj", alternate = ["result", "data"])
    var data: T? = null

    val isSuccess: Boolean
        get() = ServiceErrorCode.RESPONSE_SUCCESS == code
}
