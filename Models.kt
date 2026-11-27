package com.vidara.editor.domain
import kotlinx.serialization.Serializable

const val PROJECT_FORMAT_VERSION = 1

enum class TrackType { VIDEO, OVERLAY, IMAGE, TEXT, SUBTITLE, MUSIC, SFX, VOICEOVER, ADJUSTMENT }

/** Non-destructive: sources are never modified; a clip only references a range of its source. */
@Serializable
data class Clip(
    val id: String,
    val sourcePath: String? = null,
    val isImage: Boolean = false,
    val inMs: Long = 0,
    val durationMs: Long,
    val opacity: Float = 1f,
    val text: String? = null,
)

@Serializable
data class Track(
    val id: String,
    val type: TrackType,
    val locked: Boolean = false,
    val hidden: Boolean = false,
    val muted: Boolean = false,
    val clips: List<Clip> = emptyList(),
)

@Serializable
data class ProjectFile(
    val version: Int = PROJECT_FORMAT_VERSION,
    val id: String,
    val name: String,
    val width: Int = 1920,
    val height: Int = 1080,
    val fps: Int = 30,
    val tracks: List<Track> = emptyList(),
) {
    val durationMs: Long get() = tracks.firstOrNull { it.type == TrackType.VIDEO }?.clips?.sumOf { it.durationMs } ?: 0L
}

/** Project migration entry point: upgrade older JSON versions here as the format evolves. */
object ProjectMigrations {
    fun migrate(p: ProjectFile): ProjectFile = when (p.version) {
        PROJECT_FORMAT_VERSION -> p
        else -> p.copy(version = PROJECT_FORMAT_VERSION)
    }
}
