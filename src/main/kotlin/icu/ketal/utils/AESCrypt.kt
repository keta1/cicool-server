package icu.ketal.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypt {
    fun encrypt(key: String, initVector: String, value: String): String? {
        kotlin.runCatching {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())
            return String(Base64.getUrlEncoder().encode(encrypted))
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    fun decrypt(key: String, initVector: String, encrypted: String?): String? {
        kotlin.runCatching {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted))
            return String(original)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    // 使用 sessionKey 解密微信的服务段返回的数据
    fun decryptWechatData(sessionKey: String, initVector: String, value: String): String? {
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
