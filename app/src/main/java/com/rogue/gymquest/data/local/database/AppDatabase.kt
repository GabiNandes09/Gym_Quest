package com.rogue.gymquest.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rogue.gymquest.data.local.dao.BodyWeightLogDao
import com.rogue.gymquest.data.local.dao.ExerciseDao
import com.rogue.gymquest.data.local.dao.MuscleGroupDao
import com.rogue.gymquest.data.local.dao.WorkoutDao
import com.rogue.gymquest.data.local.dao.WorkoutSetDao
import com.rogue.gymquest.data.local.entity.BodyWeightLogEntity
import com.rogue.gymquest.data.local.entity.ExerciseEntity
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity

@Database(
    entities = [
        MuscleGroupEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutSetEntity::class,
        BodyWeightLogEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun muscleGroupDao(): MuscleGroupDao

    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutDao(): WorkoutDao

    abstract fun workoutSetDao(): WorkoutSetDao

    abstract fun bodyWeightLogDao(): BodyWeightLogDao
}
