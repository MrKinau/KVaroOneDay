package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.dsl.scoreboard.scoreboard
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin

class ScoreboardManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    fun showScoreboard(player: Player) {
        scoreboard(ChatColor.of("#00cdcd").toString() + "Varo", plugin) {
            line(1, " ") {}
            line(2, "§6Team-Kills") {}
            line(3, "§8» §70") {
                onUpdate {
                    newText = "§8» §7" + plugin.fightManager.getTeamKills(player)
                }
            }
            line(4, "  ") {}
            line(5, "§6Border") {}
            line(6, "§8» §7100x100") {
                onUpdate {
                    val size = Bukkit.getWorld("world")?.worldBorder?.size?.toInt() ?: return@onUpdate
                    val sizeStr = if (size > 10000) "∞" else size.toString()
                    newText = "§8» §7${sizeStr}x${sizeStr}"
                }
            }
            line(7, "  ") {}
            line(8, "§6Made with §c❤") {}
            line(9, "§7by §9MrKinau") {}
            updateLinesDelay = 5
        }.show(player)
    }
}