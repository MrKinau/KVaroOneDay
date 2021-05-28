package systems.kinau.kvaro

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import net.md_5.bungee.api.ChatColor
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_16_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import systems.kinau.kvaro.tasks.StartTask
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

internal fun KVaroPlugin.registerCommands() {
    registerStartCommand(this)
    registerLeakLocationCommand(this)
    registerSimpleTeamCommand(this)
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

            if (plugin.startTask != null) {
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

fun registerSimpleTeamCommand(plugin: KVaroPlugin) {
    val formatter = DecimalFormat("00")
    command("simpleteam", plugin = plugin) {
        executor {
            if (sender.isOp) {
                if (args.isNotEmpty()) {
                    val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return@executor
                    val nextTeamId = scoreboard.teams.size + 1
                    val teamName = "t" + formatter.format(nextTeamId)
                    val displayName = "T" + formatter.format(nextTeamId)
                    val prefix = "[T" + formatter.format(nextTeamId) + "] "
                    val team = scoreboard.registerNewTeam(teamName)

                    team.displayName = displayName
                    team.prefix = prefix
                    team.suffix = "§r";
                    team.setAllowFriendlyFire(false)
                    args.forEach { arg ->
                        if (arg.startsWith("#")) {
                            team.prefix = ChatColor.of(arg).toString() + prefix + ChatColor.RESET.toString()
                        }
                        team.addEntry(arg)
                    }
                    team.entries.forEach { entry ->
                        Bukkit.getPlayer(entry)?.setPlayerListName(team.prefix + entry + team.suffix)
                        Bukkit.getPlayer(entry)?.setDisplayName(team.prefix + entry + team.suffix)
                    }
                    sender.sendMessage("§aTeam §6$displayName §aerstellt!")
                } else sender.sendMessage("Dumm? /simpleteam <Spieler1> <Spieler2> [#rrggbb]")
            } else sender.sendMessage("§cHätteste gedacht ich bin dumm, wa? Ne hascht kene Rechte!")
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