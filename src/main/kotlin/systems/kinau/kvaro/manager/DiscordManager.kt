package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin

class DiscordManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    private val discordName: String = plugin.varoConfig.config.discordName

    fun sendStartMessage() {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.startWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0x00d4ff)
                            .setTitle(WebhookEmbed.EmbedTitle("Varo gestartet", null))
                            .setDescription("\nDas Spiel hat begonnen!")
                            .setThumbnailUrl("https://cdn.discordapp.com/icons/606506021221040148/578e19ba3eb7f0ed0c42990cf5f29ec3.webp?size=512")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendLoginMessage(player: Player) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.loginWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0x3de018)
                            .setTitle(WebhookEmbed.EmbedTitle("${player.name} beigetreten", null))
                            .setDescription("\n${player.name} hat den Server betreten!")
                            .setThumbnailUrl("https://cravatar.eu/helmhead/${player.uniqueId.toString().replace("-", "").toLowerCase()}/256.png")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendLogoutMessage(player: Player) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.logoutWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0xed3859)
                            .setTitle(WebhookEmbed.EmbedTitle("${player.name} verlassen", null))
                            .setDescription("\n${player.name} hat den Server verlassen!")
                            .setThumbnailUrl("https://cravatar.eu/helmhead/${player.uniqueId.toString().replace("-", "").toLowerCase()}/256.png")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendWorldBorderChange(worldName: String, size: Int) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.worldborderWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0xf99922)
                            .setTitle(WebhookEmbed.EmbedTitle("Worldborder angepasst", null))
                            .setDescription("\nDie WorldBorder in \"$worldName\" ist nun auf ${size}x${size} Blöcke angepasst!\n\nEcken bei: -${size / 2}/-${size / 2} und ${size / 2}/${size / 2}")
                            .setThumbnailUrl("https://cdn.discordapp.com/icons/606506021221040148/578e19ba3eb7f0ed0c42990cf5f29ec3.webp?size=512")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendDeathMessage(deathMessage: String) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.deathsWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0xff0000)
                            .setTitle(WebhookEmbed.EmbedTitle("TOOOD! Get rekt!", null))
                            .setDescription("\n" + deathMessage + "\n")
                            .setThumbnailUrl("https://cdn.pixabay.com/photo/2013/07/13/12/32/tombstone-159792_960_720.png")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendLocationLeakMessage(player: OfflinePlayer, world: String, x: Int, z: Int) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.locationLeakWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0xff0000)
                            .setTitle(WebhookEmbed.EmbedTitle("Punishment: Koordinaten-Leak", null))
                            .setDescription("\n${player.name} ist in \"$world\" bei\n\nX=${x}\nZ=${z}\n")
                            .setThumbnailUrl("https://cravatar.eu/helmhead/${player.uniqueId.toString().replace("-", "").toLowerCase()}/256.png")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

    fun sendDamageDealt(damager: Player, damaged: Player) {
        val webHooks: List<WebhookClient> = plugin.varoConfig.config.damageDealtWebhooks.map { WebhookClient.withUrl(it) }
        webHooks.forEach { webhook ->
            webhook.send(WebhookMessageBuilder()
                    .addEmbeds(WebhookEmbedBuilder()
                            .setColor(0xff7200)
                            .setTitle(WebhookEmbed.EmbedTitle("Damageinfo", null))
                            .setDescription("\n${damager.name} hat ${damaged.name} angegriffen!\n")
                            .setThumbnailUrl("https://cravatar.eu/helmhead/${damager.uniqueId.toString().replace("-", "").toLowerCase()}/256.png")
                            .build())
                    .setUsername(discordName)
                    .build())
        }
    }

}