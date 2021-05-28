package systems.kinau.kvaro.utils

import java.text.DecimalFormat

object TimeUtils {

    private val formatter = DecimalFormat("00")

    fun formatTime(time: Int): String {
        val hours = time / 3600
        val mins = (time / 60) % 60
        val secs = time % 60
        return if (hours > 0)
            formatter.format(hours) + ":" + formatter.format(mins) + ":" + formatter.format(secs)
        else
            formatter.format(mins) + ":" + formatter.format(secs)
    }
}