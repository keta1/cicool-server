package icu.ketal.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypt {
    // 使用 sessionKey 解密微信的服务段返回的数据
    fun decrypt(sessionKey: String, initVector: String, value: String): String? {
        kotlin.runCatching {
            val decoder = Base64.getDecoder()
            val iv = IvParameterSpec(decoder.decode(initVector))
            val skeySpec = SecretKeySpec(decoder.decode(sessionKey), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(decoder.decode(value))
            return String(original)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }
}
