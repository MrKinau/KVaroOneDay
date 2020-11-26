package systems.kinau.kvaro.manager

import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.WithPlugin
import br.com.devsrsouza.kotlinbukkitapi.serialization.architecture.config
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import systems.kinau.kvaro.KVaroPlugin
import systems.kinau.kvaro.config.TimesData
import systems.kinau.kvaro.tasks.LogOffTask
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

class TimesManager(override val plugin: KVaroPlugin) : WithPlugin<KVaroPlugin> {

    val timesData = plugin.config(
            "timesData.yml",
            TimesData(),
            TimesData.serializer()
    )

    var todayStart = Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(plugin.varoConfig.config.startLoginTime, 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).offset).toEpochMilli())
    var todayEnd = Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(plugin.varoConfig.config.endLoginTime, 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).offset).toEpochMilli())

    private val loginTimes: MutableMap<UUID, Long> = mutableMapOf()
    private val logOffBukkitTasks: MutableMap<UUID, BukkitTask> = mutableMapOf()
    private val logOffTasks: MutableMap<UUID, LogOffTask> = mutableMapOf()
    public val damageDealt: MutableList<UUID> = mutableListOf();

    init {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            updateStartStop()
        }, 100, 100)

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            updateOnlineTime()
        }, 20, 20)
    }

    fun canJoin(player: Player) : Boolean {
        val timestamp = Timestamp(System.currentTimeMillis())
        if (!(timestamp.after(todayStart) && timestamp.before(todayEnd))) {
            return Bukkit.getOfflinePlayer(player.uniqueId).isOp
        } else {
            plugin.discordManager.sendLoginMessage(player)
            if (plugin.varoData.config.started) {
                if (timesData.config.savedTimes.containsKey(player.uniqueId.toString()))
                    loginTimes[player.uniqueId] = System.currentTimeMillis() - (timesData.config.savedTimes[player.uniqueId.toString()]
                            ?: error("player has no savedTime"))
                else
                    loginTimes[player.uniqueId] = timestamp.time
            }
            return true
        }
    }

    fun logout(player: Player) {
        damageDealt.remove(player.uniqueId)
        if (loginTimes.containsKey(player.uniqueId)) {
            timesData.config.savedTimes[player.uniqueId.toString()] = System.currentTimeMillis() - loginTimes[player.uniqueId]!!
            timesData.save()
            loginTimes.remove(player.uniqueId)
        }
        if (logOffTasks.containsKey(player.uniqueId)) {
            logOffBukkitTasks[player.uniqueId]?.cancel()
            logOffBukkitTasks.remove(player.uniqueId)
            logOffTasks.remove(player.uniqueId)
        }
    }

    fun resetTimes() {
        loginTimes.clear()
        timesData.config.savedTimes.clear()
        timesData.save()
        logOffBukkitTasks.forEach { (_, task) -> task.cancel() }
        logOffBukkitTasks.clear()
        logOffTasks.clear()
    }

    fun varoStart() {
        Bukkit.getOnlinePlayers().forEach { player ->
            loginTimes[player.uniqueId] = System.currentTimeMillis()
        }
        timesData.config.savedTimes.clear()
        timesData.save()
    }

    private fun updateStartStop() {
        todayStart = Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(plugin.varoConfig.config.startLoginTime, 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).offset).toEpochMilli())
        todayEnd = Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(plugin.varoConfig.config.endLoginTime, 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).offset).toEpochMilli())
    }

    private fun updateOnlineTime() {
        val checkTime = (plugin.varoConfig.config.playTime * 60 * 1000) - 30_000
        Bukkit.getOnlinePlayers().forEach { player ->
            if (loginTimes.containsKey(player.uniqueId) && !logOffBukkitTasks.containsKey(player.uniqueId)) {
                if (loginTimes[player.uniqueId]!!.plus(checkTime) <= System.currentTimeMillis()) {
                    logOffTasks[player.uniqueId] = LogOffTask(plugin, player, {
                        logOffTasks.remove(player.uniqueId)
                        logOffBukkitTasks[player.uniqueId]?.cancel()
                        logOffBukkitTasks.remove(player.uniqueId)
                        loginTimes.remove(player.uniqueId)
                    })
                    logOffBukkitTasks[player.uniqueId] = Bukkit.getScheduler().runTaskTimer(plugin, logOffTasks[player.uniqueId]!!, 20, 20)
                }
            }
        }
    }

}