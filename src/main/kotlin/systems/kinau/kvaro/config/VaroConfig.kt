package systems.kinau.kvaro.config

import kotlinx.serialization.Serializable

@Serializable
class VaroConfig {

    var loginWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var logoutWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel")
    var startWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var deathsWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/deathsChannel")
    var worldborderWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var locationLeakWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var damageDealtWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel")

    val discordName: String = "Varo"
    
    val spawnPoint: Location = Location("world", 3.0, 91.0, -52.0, 0.0F, 0.0F)
    val arenaLocs: List<Location> = listOf(Location())

    val worldborderStartSize: Int = 1000
    val worldborderEndSize: Int = 100
    val worldborderTimeToEnd: Int = 7200
    val worldborderTimeToStart: Int = 2700
    val safeTime: Int = 600
    val glowingStart: Int = 5400

}