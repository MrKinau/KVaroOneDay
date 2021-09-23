package systems.kinau.kvaro.listener

import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import net.md_5.bungee.api.ChatColor
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.scoreboard.Team
import systems.kinau.kvaro.KVaroPlugin

fun KVaroPlugin.registerListener() {

    events {

        event<PlayerJoinEvent> {
            discordManager.sendLoginMessage(player)
            scoreboardManager.showScoreboard(player)

            if (!varoData.config.started) {
                player.gameMode = GameMode.ADVENTURE
                player.sendMessage(ChatColor.of("#ff0000").toString() + "HALT! §r§bDas Varo hat noch nicht begonnen. Du bist nun im Adventuremode")
                player.teleport(varoConfig.config.spawnPoint.toBukkitLoc())
            }

            val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return@event
            val team: Team = scoreboard.getEntryTeam(player.name) ?: return@event
            player.setPlayerListName(team.prefix + player.name + team.suffix)
            player.setDisplayName(team.prefix + player.name + team.suffix)
            player.isGlowing = false
        }

        event<PlayerQuitEvent> {
            discordManager.sendLogoutMessage(player)
        }

        event<PlayerDeathEvent> {
            if (!varoData.config.started) return@event
            if (deathMessage != null) discordManager.sendDeathMessage(deathMessage!!)
            fightManager.countKill(entity.uniqueId)
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
            if (!varoData.config.started) return@event
            if (entity !is Player) return@event
            val playerTeam = Bukkit.getScoreboardManager()?.mainScoreboard?.getEntryTeam(entity.name)
            if (damager is Player) {
                if ((plugin as KVaroPlugin).gameCountdown?.isSafeTime() == true) {
                    isCancelled = true
                    return@event
                }
                val damagerTeam = Bukkit.getScoreboardManager()?.mainScoreboard?.getEntryTeam(damager.name)
                if (playerTeam?.name.equals(damagerTeam?.name)) {
                    isCancelled = true;
                    return@event
                }
                val allDamageDealt = fightManager.addFightAction(damager.uniqueId, entity.uniqueId, damage)
                println("${damager.name} damaged ${entity.name} with $damage (all: $allDamageDealt)")
            } else if (damager is Projectile && (damager as Projectile).shooter is Player) {
                if ((plugin as KVaroPlugin).gameCountdown?.isSafeTime() == true) {
                    isCancelled = true
                    return@event
                }
                val shooter: Player = (damager as Projectile).shooter as Player
                val damagerTeam = Bukkit.getScoreboardManager()?.mainScoreboard?.getEntryTeam(shooter.name)
                if (playerTeam?.name.equals(damagerTeam?.name)) {
                    isCancelled = true;
                    return@event
                }
                val allDamageDealt = fightManager.addFightAction(shooter.uniqueId, entity.uniqueId, damage)
                println("${damager.javaClass.simpleName} from ${shooter.name} damaged ${entity.name} with $damage (all: $allDamageDealt)")
            }
        }

        event<ChunkLoadEvent> {
            if (chunk.contains(Material.BREWING_STAND.createBlockData())) {
                for (x in 0..15) {
                    for (y in 0..255) {
                        for (z in 0..15) {
                            if (chunk.getBlock(x, y, z).type == Material.BREWING_STAND) {
                                chunk.getBlock(x, y, z).type = Material.AIR
                                println("REMOVED ILLEGAL BREWING STAND AT: ${(chunk.x shl 4) + x}/$y/${(chunk.z shl 4) + z}")
                            }
                        }
                    }
                }
            }
        }

        event<ItemSpawnEvent> {
            if (entity.itemStack.type == Material.BREWING_STAND) {
                isCancelled = true
                entity.remove()
            }
        }

        event<PlayerInteractEvent> {
            if (clickedBlock == null) return@event
            if (clickedBlock?.type == Material.BREWING_STAND)
                clickedBlock?.type = Material.AIR
        }

    }
}