package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import systems.kinau.kvaro.KVaroPlugin
import java.util.*
import java.util.concurrent.TimeUnit

class FightManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    private val damageDealt: HashMap<Pair<UUID, UUID>, Double> = HashMap()
    private val fightAction: Cache<UUID, UUID> = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build()

    fun addFightAction(damager: UUID, damaged: UUID, damage: Double): Double {
        fightAction.put(damaged, damager)
        return damageDealt.merge(Pair(damaged, damager), damage) { damageBefore, damageToAdd -> damageBefore + damageToAdd } ?: 0.0
    }

    fun getDamageDealt(damager: UUID, damaged: UUID) : Double {
        return damageDealt.getOrDefault(Pair(damager, damaged), 0.0)
    }

    fun countKill(dead: UUID) {
        val killer = fightAction.getIfPresent(dead) ?: return
        val killerPlayer = Bukkit.getPlayer(killer)
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return

        val team: Team? = if (killerPlayer == null) {
            val offlinePlayer = Bukkit.getOfflinePlayer(killer)
            if (offlinePlayer.name == null) {
                println("OFFLINEPLAYER OF $killer NOT FOUND, BUT IT KILLED $dead (NOT COUNTED)")
                return
            }
            scoreboard.getEntryTeam(offlinePlayer.name!!)
        } else {
            scoreboard.getEntryTeam(killerPlayer.name)
        }

        if (team == null) {
            println("TEAM OF $killer IS NULL: COULD NOT COUNT KILL FROM $killer at $dead")
            return
        }

        val kills = plugin.varoData.config.teamKills.merge(team.name, 1, Integer::sum)
        println("${team.name} killed $dead ($kills Kills)")

        plugin.varoData.save()
    }

    fun getTeamKills(player: Player): Int {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return 0
        val team: Team = scoreboard.getEntryTeam(player.name) ?: return 0
        return plugin.varoData.config.teamKills.getOrDefault(team.name, 0)
    }

}