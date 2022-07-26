package icu.ketal

import icu.ketal.utils.AESCrypt
import org.junit.Test
import kotlin.test.assertEquals

class AESCryptTest {
    @Test
    fun testAESCrypt() {
        val sessionKey = "tiihtNczf5v6AKRyjwEUhQ=="
        val encryptedData =
            "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZM" +
                    "QmRzooG2xrDcvSnxIMXFufNstNGTyaGS" +
                    "9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+" +
                    "3hVbJSRgv+4lGOETKUQz6OYStslQ142d" +
                    "NCuabNPGBzlooOmB231qMM85d2/fV6Ch" +
                    "evvXvQP8Hkue1poOFtnEtpyxVLW1zAo6" +
                    "/1Xx1COxFvrc2d7UL/lmHInNlxuacJXw" +
                    "u0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn" +
                    "/Hz7saL8xz+W//FRAUid1OksQaQx4CMs" +
                    "8LOddcQhULW4ucetDf96JcR3g0gfRK4P" +
                    "C7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB" +
                    "6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns" +
                    "/8wR2SiRS7MNACwTyrGvt9ts8p12PKFd" +
                    "lqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYV" +
                    "oKlaRv85IfVunYzO0IKXsyl7JCUjCpoG" +
                    "20f0a04COwfneQAGGwd5oa+T8yO5hzuy" +
                    "Db/XcxxmK01EpqOyuxINew=="
        val iv = "r7BXXKkLb8qrSNn05n0qiA=="

        val decrypted = "{\"openId\":\"oGZUI0egBJY1zhBYw2K" +
                "hdUfwVJJE\",\"nickName\":\"Band\",\"gende" +
                "r\":1,\"language\":\"zh_CN\",\"city\":\"G" +
                "uangzhou\",\"province\":\"Guangdong\",\"c" +
                "ountry\":\"CN\",\"avatarUrl\":\"http://wx" +
                ".qlogo.cn/mmopen/vi_32/aSKcBBPpibyKNicHNT" +
                "MM0qJVh8Kjgiak2AHWr8MHM4WgMEm7GFhsf8OYryS" +
                "dbvAMvTsw3mo8ibKicsnfN5pRjl1p8HQ/0\",\"un" +
                "ionId\":\"ocMvos6NjeKLIBqg5Mr9QjxrP1FA\"," +
                "\"watermark\":{\"timestamp\":1477314187," +
                "\"appid\":\"wx4f4bc4dec97d474b\"}}"

        assertEquals(AESCrypt.decrypt(sessionKey, iv, encryptedData), decrypted)
    }
}
