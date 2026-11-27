package com.vidara.editor.device
import android.app.ActivityManager
import android.content.Context
import android.media.MediaCodecList
import android.os.Environment
import android.os.PowerManager

enum class PerfMode { LOW, BALANCED, HIGH }

data class Capabilities(
    val ramMb: Long, val freeStorageMb: Long, val hasHevcEncoder: Boolean,
    val hasAv1Decoder: Boolean, val thermalStatus: Int, val mode: PerfMode,
)

object DeviceCapabilities {
    fun detect(c: Context): Capabilities {
        val am = c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo().also(am::getMemoryInfo)
        val ramMb = mi.totalMem / (1024 * 1024)
        val free = Environment.getDataDirectory().usableSpace / (1024 * 1024)
        val codecs = MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
        val hevcEnc = codecs.any { it.isEncoder && it.supportedTypes.any { t -> t.equals("video/hevc", true) } }
        val av1Dec = codecs.any { !it.isEncoder && it.supportedTypes.any { t -> t.equals("video/av01", true) } }
        val thermal = (c.getSystemService(Context.POWER_SERVICE) as PowerManager).currentThermalStatus
        val mode = when {
            thermal >= PowerManager.THERMAL_STATUS_SEVERE || ramMb < 4500 -> PerfMode.LOW
            ramMb < 7500 -> PerfMode.BALANCED
            else -> PerfMode.HIGH
        }
        return Capabilities(ramMb, free, hevcEnc, av1Dec, thermal, mode)
    }
}
