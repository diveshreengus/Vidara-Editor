package com.vidara.editor.domain
import java.util.UUID

/** Pure timeline operations (unit-testable, no Android dependencies). */
object Timeline {
    const val MIN_CLIP_MS = 100L
    fun newId() = UUID.randomUUID().toString()

    private fun ProjectFile.withPrimary(): ProjectFile =
        if (tracks.any { it.type == TrackType.VIDEO }) this
        else copy(tracks = listOf(Track(newId(), TrackType.VIDEO)) + tracks)

    fun append(p: ProjectFile, clips: List<Clip>): ProjectFile {
        val base = p.withPrimary()
        return base.copy(tracks = base.tracks.map {
            if (it.type == TrackType.VIDEO && !it.locked) it.copy(clips = it.clips + clips) else it
        })
    }

    fun split(p: ProjectFile, positionMs: Long): ProjectFile {
        val track = p.tracks.firstOrNull { it.type == TrackType.VIDEO && !it.locked } ?: return p
        var acc = 0L
        val out = mutableListOf<Clip>()
        var changed = false
        for (c in track.clips) {
            val local = positionMs - acc
            if (!changed && local > MIN_CLIP_MS && local < c.durationMs - MIN_CLIP_MS) {
                out += c.copy(durationMs = local)
                out += c.copy(id = newId(), inMs = c.inMs + local, durationMs = c.durationMs - local)
                changed = true
            } else out += c
            acc += c.durationMs
        }
        if (!changed) return p
        return p.copy(tracks = p.tracks.map { if (it.id == track.id) it.copy(clips = out) else it })
    }

    fun delete(p: ProjectFile, clipId: String): ProjectFile =
        p.copy(tracks = p.tracks.map { t -> t.copy(clips = t.clips.filterNot { it.id == clipId }) })

    fun duplicate(p: ProjectFile, clipId: String): ProjectFile =
        p.copy(tracks = p.tracks.map { t ->
            val i = t.clips.indexOfFirst { it.id == clipId }
            if (i < 0) t else t.copy(clips = t.clips.toMutableList().also { it.add(i + 1, t.clips[i].copy(id = newId())) })
        })
}
