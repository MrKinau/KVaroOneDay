package systems.kinau.kvaro.tasks

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin
import java.util.function.Consumer

class LogOffTask(override val plugin: KVaroPlugin, private val player: Player, private val callBack: Consumer<Boolean>, private var counter: Int = 30): Runnable, WithPlugin<KVaroPlugin> {

    override fun run() {
        if ((counter % 10 == 0 && counter > 0) || counter in 1..9) player.sendMessage("§cDeine Zeit läuft in $counter Sekunden ab!")
        else if (counter == 1) player.sendMessage("§cDeine Zeit läuft in $counter Sekunde ab!")
        else if (counter <= 0) {
            callBack.accept(true)
            player.sendMessage("§4Deine Zeit ist abgelaufen! Disconnecte, wenn du nicht gerade um dein Leben kämpfst!")
            println(player.name + "${player.name} should log off ----")
            plugin.discordManager.sendShouldLogoutMessage(player)
            return
        }
        counter--
    }

}