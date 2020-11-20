package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import systems.kinau.kvaro.KVaroPlugin
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

class VaroManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    companion object {
        const val INITIAL_WORLDBORDER_SIZE: Int = 5000
    }

    init {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            dealWorldBorderDamage()
        }, 0, 20)

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            updateWorldBorder()
        }, 100, 100)
    }

    fun start() {
        plugin.varoData.config.started = true
        // set worldborder start in 2 days
        plugin.varoData.config.nextWorldborderChange = LocalDate.now(ZoneId.of("Europe/Berlin")).atStartOfDay().plusDays(2).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).offset).toEpochMilli()
        plugin.varoData.save()

        setWorldBorder(INITIAL_WORLDBORDER_SIZE)

        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.SURVIVAL
            player.health = 20.0
            player.foodLevel = 20
            player.saturation = 5.0F
        }

        plugin.timesManager.varoStart()
        plugin.discordManager.sendStartMessage()
    }

    private fun setWorldBorder(size: Int, maxSize: Int = 1000) {
        Bukkit.getWorlds().filter { world ->
            !world.name.endsWith("nether") && !world.name.endsWith("the_end")
        }.forEach { world ->
            world.worldBorder.damageAmount = 0.0
            world.worldBorder.damageBuffer = 0.0
            world.worldBorder.warningDistance = 0
            world.worldBorder.warningTime = 0
            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
            if (size >= maxSize) world.worldBorder.size = size.toDouble()
        }
    }

    private fun changeWorldBorder(sizeDiff: Int, maxSize: Int = 1000) {
        Bukkit.getWorlds().filter { world ->
            !world.name.endsWith("nether") && !world.name.endsWith("the_end")
        }.forEach { world ->
            world.worldBorder.damageAmount = 0.0
            world.worldBorder.damageBuffer = 0.0
            world.worldBorder.warningDistance = 0
            world.worldBorder.warningTime = 0
            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
            val newSize: Double = world.worldBorder.size + sizeDiff
            if (newSize >= maxSize) {
                world.worldBorder.size = newSize
                plugin.discordManager.sendWorldBorderChange(world.name, newSize.toInt())
            }
        }
    }

    private fun updateWorldBorder() {
        val nextChange: Long = plugin.varoData.config.nextWorldborderChange
        if (nextChange < 0) return
        if (System.currentTimeMillis() > nextChange) {
            changeWorldBorder(-plugin.varoConfig.config.dailyBorderDiff)
            plugin.varoData.config.nextWorldborderChange = LocalDate.now().atStartOfDay().plusDays(1).toInstant(OffsetDateTime.now().offset).toEpochMilli()
            plugin.varoData.save()
            plugin.timesManager.resetTimes()
        }
    }

    private fun dealWorldBorderDamage() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (isOutsideOfBorder(player)) {
                Bukkit.getScheduler().callSyncMethod(plugin) {
                    player.damage(1.0)
                }
            }
        }
    }

    private  fun isOutsideOfBorder(p: Player): Boolean {
        val loc = p.location
        val border = p.world.worldBorder
        val size = border.size / 2
        val center = border.center
        val x = loc.x - center.x
        val z = loc.z - center.z
        return x > size || -x > size || z > size || -z > size
    }

}