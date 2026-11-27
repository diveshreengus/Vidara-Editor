package com.vidara.editor.template
import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val TEMPLATE_SCHEMA_VERSION = 1

@Serializable data class TextPlaceholder(val id: String, val defaultText: String, val startMs: Long, val durationMs: Long)
@Serializable data class MediaPlaceholder(val id: String, val kind: String, val durationMs: Long)

@Serializable
data class TemplateManifest(
    val schemaVersion: Int, val id: String, val name: String, val category: String,
    val thumbnail: String? = null,
    val width: Int, val height: Int, val fps: Int, val durationMs: Long,
    val mediaPlaceholders: List<MediaPlaceholder> = emptyList(),
    val textPlaceholders: List<TextPlaceholder> = emptyList(),
    val colorPresets: List<String> = emptyList(),
    val requiredAssets: List<String> = emptyList(),
)

object TemplateValidator {
    fun validate(t: TemplateManifest): List<String> = buildList {
        if (t.schemaVersion > TEMPLATE_SCHEMA_VERSION) add("Needs newer app (schema ${t.schemaVersion})")
        if (t.id.isBlank()) add("Missing id")
        if (t.width <= 0 || t.height <= 0) add("Bad canvas size")
        if (t.fps !in 1..120) add("Bad frame rate")
        if (t.durationMs <= 0) add("Bad duration")
        t.textPlaceholders.forEach { if (it.startMs + it.durationMs > t.durationMs) add("Text ${it.id} exceeds duration") }
    }
}

object TemplateRepository {
    private val json = Json { ignoreUnknownKeys = true }
    /** Loads packaged templates from assets/templates/*.json — never from the network. */
    fun loadInstalled(c: Context): List<TemplateManifest> =
        (c.assets.list("templates") ?: emptyArray()).filter { it.endsWith(".json") }.mapNotNull { f ->
            runCatching {
                json.decodeFromString<TemplateManifest>(c.assets.open("templates/$f").bufferedReader().use { it.readText() })
            }.getOrNull()?.takeIf { TemplateValidator.validate(it).isEmpty() }
        }
}
