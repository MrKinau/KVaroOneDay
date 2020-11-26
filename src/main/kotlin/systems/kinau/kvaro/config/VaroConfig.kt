package systems.kinau.kvaro.config

import kotlinx.serialization.Serializable

@Serializable
class VaroConfig {

    var loginWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var logoutWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel")
    var shouldLogoutWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel")
    var startWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var deathsWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/deathsChannel")
    var worldborderWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var locationLeakWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel", "https://discordapp.com/api/webhooks/loginChannel")
    var damageDealtWebhooks: List<String> = listOf("https://discordapp.com/api/webhooks/adminChannel")

    val startLoginTime: Int = 14
    val endLoginTime: Int = 22

    val playTime: Int = 20

    val discordName: String = "Varo"
    
    val dailyBorderDiff: Int = -200

    val spawnPoint: String = "world/3/91/-52"
}