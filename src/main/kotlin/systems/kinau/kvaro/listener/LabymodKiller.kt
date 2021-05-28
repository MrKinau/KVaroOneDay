package systems.kinau.kvaro.listener

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class LabymodKiller : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        player.kickPlayer("§cBrudi (oder Schwesti), was hatten wir zu Beginn gesagt?\n\n§4§lLabyMod ist nicht erlaubt")
    }
}