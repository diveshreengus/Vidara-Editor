package com.vidara.editor.data
import com.vidara.editor.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val dao: ProjectDao,
    private val json: Json,
    private val mediaDir: File,
) {
    fun observe(): Flow<List<ProjectEntity>> = dao.observeAll()

    suspend fun create(name: String): ProjectFile {
        val p = ProjectFile(id = Timeline.newId(), name = name)
        save(p); return p
    }

    suspend fun get(id: String): ProjectFile? =
        dao.get(id)?.let { runCatching { ProjectMigrations.migrate(json.decodeFromString<ProjectFile>(it.json)) }.getOrNull() }

    suspend fun save(p: ProjectFile) =
        dao.upsert(ProjectEntity(p.id, p.name, System.currentTimeMillis(), json.encodeToString(p)))

    suspend fun delete(id: String) = dao.delete(id)

    /** Missing-media detection: clips whose managed source file no longer exists. */
    fun missingMedia(p: ProjectFile): List<Clip> =
        p.tracks.flatMap { it.clips }.filter { it.sourcePath != null && !File(it.sourcePath).exists() }

    fun mediaDir(): File = mediaDir
}
