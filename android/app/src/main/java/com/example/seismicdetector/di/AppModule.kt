package com.example.seismicdetector.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.seismicdetector.data.AppDatabase
import com.example.seismicdetector.data.DetectionDao
import com.example.seismicdetector.data.NetworkRepository
import com.example.seismicdetector.data.SeismicRepository
import com.example.seismicdetector.domain.CommunityCorrelator
import com.example.seismicdetector.domain.PWaveDetector
import com.example.seismicdetector.domain.RealtimePreprocessor
import com.example.seismicdetector.domain.SeismicMLModel
import com.example.seismicdetector.domain.SeismicSensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "seismic_detector_db"
        ).fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideDetectionDao(database: AppDatabase): DetectionDao {
        return database.detectionDao()
    }

    @Provides
    @Singleton
    fun provideRealtimePreprocessor(): RealtimePreprocessor {
        return RealtimePreprocessor()
    }

    // NetworkRepository, PWaveDetector, and CommunityCorrelator use @Inject constructor
    // and are annotated @Singleton, so Hilt provides them automatically.
    // No explicit @Provides needed here — they are resolved via constructor injection.
}
