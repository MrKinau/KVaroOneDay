package systems.kinau.kvaro.tasks

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin
import kotlin.math.ceil

class StartTask(override val plugin: KVaroPlugin, private var counter: Int = 60): Runnable, WithPlugin<KVaroPlugin> {

    private val claims = listOf("%s hat den Start verpennt", "%s wird als erstes sterben", "%s wird den Stegi machen",
            "%s versteht 1.9er PvP nicht", "%s überlebt die 2. Nacht nicht", "%s <TODO: Joke über Leon einfügen>",
            "%s ist vom Himmel gefallen", "fretoger plant schon ein neues Projekt", "%s wird gewinnen", "MinecraftiUndCo ist \"kurz\" afk",
            "%s hat seinen Teampartner vergessen", "%s findet niemals Dias", "%s wird in der Hölle verglühen")

    var currentPlayer: String = ""

    override fun run() {
        if (counter < 0) {
            plugin.startTask?.cancel()
            return
        }
        if (counter == 0)
            Bukkit.getScheduler().callSyncMethod(plugin) { plugin.varoManager.start() }
        if (counter % 5 == 0) currentPlayer = getRandomPlayer()

        Bukkit.getOnlinePlayers().forEach { player: Player? ->
            var claim: String = claims[ceil(counter / 5.0).toInt()]
            if (claim.contains("%s")) claim = String.format(claim, currentPlayer)

            if (counter == 0) {
                plugin.startTask?.cancel()
                player!!.sendTitle("§cLOS!", "§7$claim", 0, 44, 7)
                return@forEach
            }
            player!!.sendTitle("§c$counter", "§7$claim", 0, 24, 0)
        }
        counter--
    }

    private fun getRandomPlayer(): String {
        return Bukkit.getOnlinePlayers().map { it.name }.random()
    }

}