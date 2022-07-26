package icu.ketal.utils

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object WechatUtils {
    private val client = HttpClient()

    suspend fun code2Session(code: String): WechatSession {
        return runBlocking {
            client.get("https://api.weixin.qq.com/sns/jscode2session") {
                url {
                    parameters["appid"] = APPID
                    parameters["secret"] = APP_SECRET
                    parameters["js_code"] = code
                    parameters["grant_type"] = "authorization_code"
                }
            }.bodyAsText().decodeToDataClass()
        }
    }
}

@Serializable
data class WechatSession(
    @SerialName("errcode")
    val errCode: Int = 0,
    @SerialName("errmsg")
    val errMsg: String = "",
    @SerialName("session_key")
    val sessionKey: String = "",
    @SerialName("openid")
    val openId: String = "",
    @SerialName("unionid")
    val unionId: String = ""
)
