package systems.kinau.kvaro.tasks

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import systems.kinau.kvaro.KVaroPlugin
import systems.kinau.kvaro.utils.TimeUtils

class GameCountdownTask(override val plugin: KVaroPlugin, private var tick: Int = 0): Runnable, WithPlugin<KVaroPlugin> {

    override fun run() {
        if (plugin.varoConfig.config.worldborderTimeToEnd - tick < 0) {
            plugin.gameTask?.cancel()
            return
        }

        Bukkit.getOnlinePlayers().forEach { p ->
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(getActionbar()))
            if (!p.isGlowing && tick > plugin.varoConfig.config.glowingStart)
                p.isGlowing = true
        }

        val timeUntilSafePhaseEnds = plugin.varoConfig.config.safeTime - tick;
        if (timeUntilSafePhaseEnds <= 60) {
            if (timeUntilSafePhaseEnds % 10 == 0 || timeUntilSafePhaseEnds < 10) {
                if (timeUntilSafePhaseEnds > 1)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Die Schutzzeit endet in §l" + timeUntilSafePhaseEnds + " Sekunden")
                else if (timeUntilSafePhaseEnds == 1)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Die Schutzzeit endet in §l" + timeUntilSafePhaseEnds + " Sekunde")
                else if (timeUntilSafePhaseEnds == 0)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Die Schutzzeit endet §lJETZT")
            }
        }

        val timeToDeathMatch = plugin.varoConfig.config.worldborderTimeToEnd - tick;
        if (timeToDeathMatch <= 60) {
            if (timeToDeathMatch % 10 == 0 || timeToDeathMatch < 10) {
                if (timeToDeathMatch > 1)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Das Deathmatch startet in §l" + timeToDeathMatch + " Sekunden")
                else if (timeToDeathMatch == 1)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Das Deathmatch startet in §l" + timeToDeathMatch + " Sekunde")
                else if (timeToDeathMatch == 0)
                    Bukkit.broadcastMessage(ChatColor.of("#12b525").toString() + "Das Deathmatch startet §lJETZT")
            }
            if (timeToDeathMatch == 0) {
                plugin.varoManager.setWorldBorder(Int.MAX_VALUE, Int.MAX_VALUE)
                val locs = plugin.varoConfig.config.arenaLocs.shuffled()
                val teamsLocMap = HashMap<String, Location>()
                Bukkit.getScoreboardManager()?.mainScoreboard?.teams?.forEachIndexed { i, team ->
                    teamsLocMap[team.name] = locs[i].toBukkitLoc()
                }
                Bukkit.getOnlinePlayers().forEach { p ->
                    p.isGlowing = false
                    val team = Bukkit.getScoreboardManager()?.mainScoreboard?.getEntryTeam(p.name) ?: return@forEach
                    if (teamsLocMap.containsKey(team.name)) {
                        p.teleport(teamsLocMap[team.name]!!)
                    } else
                        System.err.println("Could not teleport ${p.name}, because the player has no valid team")
                }
            }
        }

        if (tick == plugin.varoConfig.config.worldborderTimeToStart) {
            plugin.varoManager.setWorldBorder(plugin.varoConfig.config.worldborderStartSize, plugin.varoConfig.config.worldborderEndSize, plugin.varoConfig.config.worldborderTimeToEnd - tick)
        }

        if (tick % 10 == 0) {
            plugin.varoData.config.secondsPassed = tick;
            plugin.varoData.save()
        }

        tick++
    }

    fun isSafeTime(): Boolean {
        return tick <= plugin.varoConfig.config.safeTime
    }

    private fun getActionbar(): String {
        val prefix: String;
        if (isSafeTime()) {
            prefix = ChatColor.of("#12b525").toString() + "Schutzzeit: "
            return prefix + TimeUtils.formatTime(plugin.varoConfig.config.safeTime - tick)
        } else {
            prefix = ChatColor.of("#b53d12").toString() + "Deathmatch: "
            return prefix + TimeUtils.formatTime(plugin.varoConfig.config.worldborderTimeToEnd - tick)
        }
    }
}