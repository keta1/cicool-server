package icu.ketal.utils

import io.netty.util.internal.SystemPropertyUtil

const val PORT = 5300 // release port
const val PORT_DEV = 5301 // test port
val APPID: String = SystemPropertyUtil.get("AppId")
val APP_SECRET: String = SystemPropertyUtil.get("AppSecret")
