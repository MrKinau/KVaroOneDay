package systems.kinau.kvaro

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.serialization.architecture.config
import org.bukkit.scheduler.BukkitTask
import systems.kinau.kvaro.config.VaroConfig
import systems.kinau.kvaro.config.VaroData
import systems.kinau.kvaro.listener.registerListener
import systems.kinau.kvaro.manager.DiscordManager
import systems.kinau.kvaro.manager.TimesManager
import systems.kinau.kvaro.manager.VaroManager

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

    lateinit var varoManager: VaroManager
    lateinit var discordManager: DiscordManager
    lateinit var timesManager: TimesManager

    override fun onPluginEnable() {
        varoManager = VaroManager(this)
        discordManager = DiscordManager(this)
        timesManager = TimesManager(this)
        registerCommands()
        registerListener()
    }
    
    override fun onPluginDisable() {
        timesManager.timesData.save()
    }

}