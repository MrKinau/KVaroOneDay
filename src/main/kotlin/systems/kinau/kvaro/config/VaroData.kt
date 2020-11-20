package systems.kinau.kvaro.config

import kotlinx.serialization.Serializable

@Serializable
class VaroData {

    var started: Boolean = false;
    var nextWorldborderChange: Long = -1;

}