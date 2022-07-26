package icu.ketal.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResResult(
    var code: Int = 200,
    var msg: String = "ok",
    var data: JsonElement? = null
)

fun buildResult(act: ResResult.() -> Unit): ResResult = ResResult().run {
    act(this)
    this
}

fun webErr(msg: String = "服务错误") = buildResult {
    code = 403
    this.msg = msg
}

val webOk get() = buildResult { }

