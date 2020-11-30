package systems.kinau.kvaro.listener

import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
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
            val spawnData = varoConfig.config.spawnPoint.split("/");
            val spawnPoint = Location(Bukkit.getWorld(spawnData[0]), spawnData[1].toDouble(), spawnData[2].toDouble(), spawnData[3].toDouble())
            player.teleport(spawnPoint)
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

        event<EntityDamageByEntityEvent> {
            if (entity is Player && damager is Player && varoData.config.started) {
                println("${damager.name} damaged ${entity.name} with $damage")
                if (!timesManager.damageDealt.contains(damager.uniqueId)) {
                    timesManager.damageDealt.add(damager.uniqueId)
                    discordManager.sendDamageDealt(damager as Player , entity as Player)
                }
            } else if (entity is Player && damager is Projectile && (damager as Projectile).shooter is Player && varoData.config.started) {
                val shooter: Player = (damager as Projectile).shooter as Player
                println("${damager.javaClass.simpleName} from ${shooter.name} damaged ${entity.name} with $damage")
                if (!timesManager.damageDealt.contains(shooter.uniqueId)) {
                    timesManager.damageDealt.add(shooter.uniqueId)
                    discordManager.sendDamageDealt(shooter , entity as Player)
                }
            }
        }

    }
}