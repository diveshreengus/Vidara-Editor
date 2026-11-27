package com.vidara.editor.di
import android.content.Context
import androidx.room.Room
import com.vidara.editor.data.AppDatabase
import com.vidara.editor.data.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun db(@ApplicationContext c: Context): AppDatabase =
        Room.databaseBuilder(c, AppDatabase::class.java, "vidara.db").build()
    @Provides fun dao(db: AppDatabase): ProjectDao = db.projects()
    @Provides @Singleton fun json(): Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    @Provides @Singleton fun mediaDir(@ApplicationContext c: Context): File =
        File(c.filesDir, "media").also { it.mkdirs() }
}
