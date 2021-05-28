package systems.kinau.kvaro.config

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class Location(
    val world: String = "world",
    val x: Double = 0.0,
    val y: Double = 70.0,
    val z: Double = 0.0,
    val yaw: Float = 0.0F,
    val pitch: Float = 0.0F) {

    fun toBukkitLoc(): Location {
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }
}