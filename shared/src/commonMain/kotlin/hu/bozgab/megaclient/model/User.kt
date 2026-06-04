package hu.bozgab.megaclient.model

data class User(val id: Long, val name: String, val token: String, val expiration: Long)
