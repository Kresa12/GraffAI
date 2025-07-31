package com.esa.graffai.di

import android.content.Context
import androidx.room.Room
import com.esa.graffai.data.AppDatabase
import com.esa.graffai.data.local.riwayat.RiwayatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "graffai_db"
        ).build()
    }

    @Provides
    @Singleton
    fun ProvideRiwayatDao(db : AppDatabase) : RiwayatDao = db.riwayatDao()
}