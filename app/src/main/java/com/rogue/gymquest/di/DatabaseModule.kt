package com.rogue.gymquest.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rogue.gymquest.data.local.database.AppDatabase
import com.rogue.gymquest.data.local.database.defaultMuscleGroupNames
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "gymquest.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val now = System.currentTimeMillis()
                    defaultMuscleGroupNames.forEach { name ->
                        db.execSQL(
                            "INSERT INTO muscle_groups (name, createdAt, updatedAt) VALUES (?, ?, ?)",
                            arrayOf<Any>(name, now, now)
                        )
                    }
                }
            })
            .build()
    }

    single { get<AppDatabase>().muscleGroupDao() }
    single { get<AppDatabase>().exerciseDao() }
    single { get<AppDatabase>().workoutDao() }
    single { get<AppDatabase>().workoutSetDao() }
    single { get<AppDatabase>().bodyWeightLogDao() }
}
