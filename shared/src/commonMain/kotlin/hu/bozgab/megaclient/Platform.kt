package hu.bozgab.megaclient

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform