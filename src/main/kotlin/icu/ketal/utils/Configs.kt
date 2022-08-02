package icu.ketal.utils

import io.netty.util.internal.SystemPropertyUtil

const val PORT = 5300 // release port
const val PORT_DEV = 5301 // test port
const val FILE_STORE_PATH = "cicool/face"
const val FILE_SIZE_LIMIT = 1024 * 1024 // 1Mb
val APPID: String = SystemPropertyUtil.get("AppId")
val APP_SECRET: String = SystemPropertyUtil.get("AppSecret")
