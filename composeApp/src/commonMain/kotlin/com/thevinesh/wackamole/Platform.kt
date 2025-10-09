package com.thevinesh.wackamole

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform