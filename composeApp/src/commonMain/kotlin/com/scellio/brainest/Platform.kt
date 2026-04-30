package com.scellio.brainest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun isIosPlatform(): Boolean
