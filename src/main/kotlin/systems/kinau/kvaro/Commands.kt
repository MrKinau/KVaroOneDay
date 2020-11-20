package systems.kinau.kvaro

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_16_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import systems.kinau.kvaro.tasks.StartTask
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.math.roundToInt

internal fun KVaroPlugin.registerCommands() {
    registerStartCommand(this)
    registerLeakLocationCommand(this)
}

fun registerStartCommand(plugin: KVaroPlugin) {
    command("start", plugin = plugin) {
        executor {
            if (!sender.isOp) {
                sender.sendMessage("§cHätteste gedacht ich bin dumm, wa? Ne hascht kene Rechte!")
                return@executor
            }

            if (plugin.varoData.config.started) {
                sender.sendMessage("§cHä? Nicht gecheckt, dass es schon angefangen hat?")
                return@executor
            }

            if (plugin.startTask?.isCancelled == true) {
                sender.sendMessage("§cSache ma, der Counter läuft doch gerade noch, wat soll'n det!")
                return@executor
            }

            plugin.startTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, StartTask(plugin), 0, 20L)
        }
    }
}

fun registerLeakLocationCommand(plugin: KVaroPlugin) {
    command("leaklocation", aliases = arrayOf("leak", "punish", "punishment"), plugin = plugin) {
        executor {
            if (sender.isOp) {
                if (args.size == 1) {
                    val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(args[0])
                    if (offlinePlayer.name == null) {
                        sender.sendMessage("§cWow sogar zu dumm zum tippen. GG")
                        return@executor
                    }

                    val compound = getPlayerData(offlinePlayer.uniqueId)
                    val tagList = compound!!.getList("Pos", 6)
                    val x = tagList.h(0)
                    val z = tagList.h(2)
                    val world = compound.getString("Dimension").replace("minecraft:", "")

                    plugin.discordManager.sendLocationLeakMessage(offlinePlayer, world, x.roundToInt(), z.roundToInt())
                    sender.sendMessage("§aKoordinaten geleaked!")
                } else sender.sendMessage("Dumm? /locationleak <Spieler>")
            }
        }
        tabComplete {
            if (args.size == 1)
                Bukkit.getOfflinePlayers()
                        .filter { it.name != null }
                        .map { it.name!! }
                        .filter { it.startsWith(args[0]) }
            else
                listOf()
        }
    }
}

private fun getPlayerData(uuid: UUID): NBTTagCompound? {
    var var1: NBTTagCompound? = null
    try {
        val var2 = File("world/playerdata/", "$uuid.dat")
        if (var2.exists() && var2.isFile) {
            var1 = NBTCompressedStreamTools.a(FileInputStream(var2))
        }
    } catch (var4: Exception) {
        var4.printStackTrace()
    }
    return var1
}