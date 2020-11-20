package systems.kinau.kvaro.config

import br.com.devsrsouza.kotlinbukkitapi.serialization.architecture.config
import kotlinx.serialization.Serializable

@Serializable
class TimesData {

    val savedTimes: MutableMap<String, Long> = mutableMapOf()
}