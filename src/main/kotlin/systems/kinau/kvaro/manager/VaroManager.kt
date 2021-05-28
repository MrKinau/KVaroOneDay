package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin
import systems.kinau.kvaro.tasks.GameCountdownTask

class VaroManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    init {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            dealWorldBorderDamage()
        }, 0, 20)
    }

    fun start() {
        plugin.varoData.config.started = true
        plugin.varoData.save()

        setWorldBorder(
            plugin.varoConfig.config.worldborderEndSize,
            plugin.varoConfig.config.worldborderStartSize,
            10
        )

        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.SURVIVAL
            player.health = 20.0
            player.foodLevel = 20
            player.saturation = 5.0F
        }

        plugin.discordManager.sendStartMessage()

        plugin.gameCountdown = GameCountdownTask(plugin)
        plugin.gameTask = Bukkit.getScheduler().runTaskTimer(plugin, plugin.gameCountdown!!, 0, 20)
    }

    fun setWorldBorder(start: Int, end: Int, time: Int = 0) {
        Bukkit.getWorlds().forEach { world ->
            world.worldBorder.damageAmount = 0.0
            world.worldBorder.damageBuffer = 0.0
            world.worldBorder.warningDistance = 0
            world.worldBorder.warningTime = 0
            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
            world.worldBorder.size = start.toDouble()
            world.worldBorder.setSize(end.toDouble(), time.toLong())
        }
    }

    private fun dealWorldBorderDamage() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (isOutsideOfBorder(player)) {
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    player.damage(1.3)
                })
            }
        }
    }

    private fun isOutsideOfBorder(p: Player): Boolean {
        val loc = p.location
        val border = p.world.worldBorder
        val size = border.size / 2
        val center = border.center
        val x = loc.x - center.x
        val z = loc.z - center.z
        return x > size || -x > size || z > size || -z > size
    }

}