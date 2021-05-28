package systems.kinau.kvaro

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.serialization.architecture.config
import org.bukkit.*
import org.bukkit.scheduler.BukkitTask
import systems.kinau.kvaro.config.VaroConfig
import systems.kinau.kvaro.config.VaroData
import systems.kinau.kvaro.listener.LabymodKiller
import systems.kinau.kvaro.listener.registerListener
import systems.kinau.kvaro.manager.DiscordManager
import systems.kinau.kvaro.manager.FightManager
import systems.kinau.kvaro.manager.ScoreboardManager
import systems.kinau.kvaro.manager.VaroManager
import systems.kinau.kvaro.tasks.GameCountdownTask

class KVaroPlugin : KotlinPlugin() {

    val varoData = config(
            "varoData.yml",
            VaroData(),
            VaroData.serializer()
    )

    val varoConfig = config(
            "varoConfig.yml",
            VaroConfig(),
            VaroConfig.serializer()
    )

    var startTask: BukkitTask? = null
    var gameTask: BukkitTask? = null
    var gameCountdown: GameCountdownTask? = null

    lateinit var varoManager: VaroManager
    lateinit var discordManager: DiscordManager
    lateinit var fightManager: FightManager
    lateinit var scoreboardManager: ScoreboardManager

    override fun onPluginEnable() {
        varoManager = VaroManager(this)
        discordManager = DiscordManager(this)
        fightManager = FightManager(this)
        scoreboardManager = ScoreboardManager(this)

        Bukkit.getOnlinePlayers().forEach { p ->
            scoreboardManager.showScoreboard(p)
        }

        if (varoData.config.started) {
            gameCountdown = GameCountdownTask(this, varoData.config.secondsPassed)
            gameTask = Bukkit.getScheduler().runTaskTimer(this, gameCountdown!!, 0, 20)
        }

        val recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            val recipe = recipeIterator.next()
            if (recipe.result.type == Material.BREWING_STAND)
                recipeIterator.remove()
        }

        if (varoConfig.config.arenaLocs.isNotEmpty()) {
            if (Bukkit.getWorld(varoConfig.config.arenaLocs[0].world) == null) {
                Bukkit.createWorld(WorldCreator(varoConfig.config.arenaLocs[0].world))
            }
        }

        registerCommands()
        registerListener()

        Bukkit.getServer().messenger.registerIncomingPluginChannel(this, "labymod3:main", LabymodKiller())

        // Generate arena world
        WorldCreator("arena").environment(World.Environment.NORMAL).type(WorldType.FLAT).generateStructures(false).createWorld()
    }

}