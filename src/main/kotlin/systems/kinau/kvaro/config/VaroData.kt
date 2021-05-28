package systems.kinau.kvaro.config

import kotlinx.serialization.Serializable

@Serializable
class VaroData {

    var started: Boolean = false
    var secondsPassed: Int = -1
    val teamKills: HashMap<String, Int> = HashMap()
}