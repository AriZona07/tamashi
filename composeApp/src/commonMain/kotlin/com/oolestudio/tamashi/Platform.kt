package com.oolestudio.tamashi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform