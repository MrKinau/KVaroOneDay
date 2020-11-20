package systems.kinau.kvaro.listener

import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import systems.kinau.kvaro.KVaroPlugin

fun KVaroPlugin.registerListener() {
    val kickReason = """§cDer Server ist nur zwischen ${varoConfig.config.startLoginTime} und ${varoConfig.config.endLoginTime} Uhr offen

Ist halt einfach dumm, wenn man es zu anderen Zeiten versucht

§eFun fact: Jeder fünfte Österreicher hält sich für nicht kompetent!"""


    events {
        event<PlayerLoginEvent> {
            if (result == PlayerLoginEvent.Result.ALLOWED && !timesManager.canJoin(player))
                disallow(PlayerLoginEvent.Result.KICK_OTHER, kickReason)
        }

        event<PlayerJoinEvent> {
            if (varoData.config.started) return@event
            player.gameMode = GameMode.ADVENTURE
            player.sendMessage("§c§lHALT! §bDas Varo hat noch nicht begonnen. Du bist nun im Adventuremode")
        }

        event<PlayerQuitEvent> {
            discordManager.sendLogoutMessage(player)
            timesManager.logout(player)
        }

        event<PlayerDeathEvent> {
            if (!varoData.config.started) return@event
            if (deathMessage != null) discordManager.sendDeathMessage(deathMessage!!)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                Bukkit.getBanList(BanList.Type.NAME).addBan(entity.name, "§cDu bist ausgeschieden, weil du zu schlecht warst!", null, null)
                entity.kickPlayer("§cJa lol ey, weg vom Fenster. Schade aber auch!\n\n§c$deathMessage")
            }, 20L)
        }

        event<EntityDamageEvent> {
            if (entity is Player && !varoData.config.started && cause != EntityDamageEvent.DamageCause.VOID)
                isCancelled = true
        }

        event<EntityDamageByEntityEvent> {
            if (damager is Player && !varoData.config.started && !damager.isOp)
                isCancelled = true
        }

    }
}