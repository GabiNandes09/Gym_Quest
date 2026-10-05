package com.rogue.gymquest.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.room.withTransaction
import com.rogue.gymquest.data.local.dao.BodyWeightLogDao
import com.rogue.gymquest.data.local.dao.ExerciseDao
import com.rogue.gymquest.data.local.dao.MuscleGroupDao
import com.rogue.gymquest.data.local.dao.WorkoutDao
import com.rogue.gymquest.data.local.dao.WorkoutSetDao
import com.rogue.gymquest.data.local.database.AppDatabase
import com.rogue.gymquest.data.local.entity.ExerciseEntity
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.data.local.entity.WorkoutStatus
import com.rogue.gymquest.data.parser.HevyCsvParser
import com.rogue.gymquest.domain.model.BACKUP_SCHEMA_VERSION
import com.rogue.gymquest.domain.model.GymQuestBackup
import com.rogue.gymquest.domain.model.ImportResult
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.File

private val BACKUP_JSON = Json { prettyPrint = true; ignoreUnknownKeys = true }
private const val UNCLASSIFIED_MUSCLE_GROUP = "A Classificar"

class BackupRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val workoutSetDao: WorkoutSetDao,
    private val bodyWeightLogDao: BodyWeightLogDao
) {

    suspend fun exportBackup(): Uri {
        val backup = GymQuestBackup(
            schemaVersion = BACKUP_SCHEMA_VERSION,
            exportedAt = System.currentTimeMillis(),
            muscleGroups = muscleGroupDao.getAll().first(),
            exercises = exerciseDao.getAllRaw().first(),
            workouts = workoutDao.getAll().first(),
            workoutSets = workoutSetDao.getAll().first(),
            bodyWeightLogs = bodyWeightLogDao.getAll().first()
        )

        val json = BACKUP_JSON.encodeToString(GymQuestBackup.serializer(), backup)
        val fileName = "gymquest_backup_${System.currentTimeMillis()}.json"
        val backupDir = File(context.cacheDir, "backups").apply { mkdirs() }
        val file = File(backupDir, fileName)
        file.writeText(json)

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    suspend fun importBackup(uri: Uri): ImportResult {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes().decodeToString()
        } ?: throw IllegalArgumentException("Não foi possível ler o arquivo")

        val backup = BACKUP_JSON.decodeFromString(GymQuestBackup.serializer(), text)

        return database.withTransaction {
            val muscleGroupIdMap = mutableMapOf<Long, Long>()
            val exerciseIdMap = mutableMapOf<Long, Long>()
            val workoutIdMap = mutableMapOf<Long, Long>()
            val workoutSetIdMap = mutableMapOf<Long, Long>()

            backup.muscleGroups.forEach { group ->
                val newId = muscleGroupDao.findByName(group.name)?.id
                    ?: muscleGroupDao.insert(group.copy(id = 0))
                muscleGroupIdMap[group.id] = newId
            }

            backup.exercises.forEach { exercise ->
                val existing = exerciseDao.findByName(exercise.name)
                val newId = if (existing != null) {
                    existing.id
                } else {
                    val newMuscleGroupId = muscleGroupIdMap[exercise.muscleGroupId] ?: return@forEach
                    exerciseDao.insert(exercise.copy(id = 0, muscleGroupId = newMuscleGroupId))
                }
                exerciseIdMap[exercise.id] = newId
            }

            backup.workouts.forEach { workout ->
                workoutIdMap[workout.id] = workoutDao.insert(workout.copy(id = 0))
            }

            backup.workoutSets.forEach { set ->
                val newWorkoutId = workoutIdMap[set.workoutId] ?: return@forEach
                val newExerciseId = exerciseIdMap[set.exerciseId] ?: return@forEach
                val newId = workoutSetDao.insert(
                    set.copy(
                        id = 0,
                        workoutId = newWorkoutId,
                        exerciseId = newExerciseId,
                        supersetGroupId = null
                    )
                )
                workoutSetIdMap[set.id] = newId
            }

            backup.workoutSets.forEach { set ->
                val oldGroupId = set.supersetGroupId ?: return@forEach
                val newSetId = workoutSetIdMap[set.id] ?: return@forEach
                val newGroupId = workoutSetIdMap[oldGroupId] ?: return@forEach
                workoutSetDao.updateSupersetGroup(newSetId, newGroupId)
            }

            backup.bodyWeightLogs.forEach { log ->
                bodyWeightLogDao.insert(log.copy(id = 0))
            }

            ImportResult(
                muscleGroups = muscleGroupIdMap.size,
                exercises = exerciseIdMap.size,
                workouts = workoutIdMap.size,
                workoutSets = workoutSetIdMap.size,
                bodyWeightLogs = backup.bodyWeightLogs.size
            )
        }
    }

    suspend fun importHevyCsv(uri: Uri): ImportResult {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes().decodeToString()
        } ?: throw IllegalArgumentException("Não foi possível ler o arquivo")

        val rows = HevyCsvParser.parse(text)

        return database.withTransaction {
            val now = System.currentTimeMillis()
            val fallbackGroupId = muscleGroupDao.findByName(UNCLASSIFIED_MUSCLE_GROUP)?.id
                ?: muscleGroupDao.insert(
                    MuscleGroupEntity(name = UNCLASSIFIED_MUSCLE_GROUP, createdAt = now, updatedAt = now)
                )

            val exerciseIdByName = mutableMapOf<String, Long>()
            val workoutIdByKey = mutableMapOf<String, Long>()
            val workoutOrderCounter = mutableMapOf<String, Int>()
            val supersetLeaderId = mutableMapOf<Pair<String, String>, Long>()

            var exercisesCreated = 0
            var workoutsCreated = 0
            var setsCreated = 0

            rows.forEach { row ->
                val exerciseId = exerciseIdByName.getOrPut(row.exerciseName) {
                    val existing = exerciseDao.findByName(row.exerciseName)
                    if (existing != null) {
                        existing.id
                    } else {
                        val type = HevyCsvParser.inferExerciseType(rows, row.exerciseName)
                        val newId = exerciseDao.insert(
                            ExerciseEntity(
                                name = row.exerciseName,
                                muscleGroupId = fallbackGroupId,
                                exerciseType = type,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                        exercisesCreated++
                        newId
                    }
                }

                val workoutKey = "${row.workoutTitle}|${row.startedAt}|${row.finishedAt}"
                val workoutId = workoutIdByKey.getOrPut(workoutKey) {
                    val newId = workoutDao.insert(
                        WorkoutEntity(
                            name = row.workoutTitle,
                            date = row.startedAt,
                            startedAt = row.startedAt,
                            finishedAt = row.finishedAt,
                            notes = row.description,
                            status = WorkoutStatus.COMPLETED,
                            createdAt = row.startedAt,
                            updatedAt = row.finishedAt
                        )
                    )
                    workoutOrderCounter[workoutKey] = 0
                    workoutsCreated++
                    newId
                }

                val order = workoutOrderCounter.getValue(workoutKey)
                workoutOrderCounter[workoutKey] = order + 1

                val supersetGroupId = row.supersetId?.let { sid -> supersetLeaderId[workoutKey to sid] }

                val newSetId = workoutSetDao.insert(
                    WorkoutSetEntity(
                        workoutId = workoutId,
                        exerciseId = exerciseId,
                        order = order,
                        setType = row.setType,
                        weight = row.weight,
                        reps = row.reps,
                        durationSeconds = row.durationSeconds,
                        distanceMeters = row.distanceMeters,
                        restTimeSeconds = 0,
                        rpe = row.rpe,
                        notes = row.exerciseNotes,
                        supersetGroupId = supersetGroupId,
                        createdAt = row.startedAt,
                        updatedAt = row.startedAt
                    )
                )
                setsCreated++

                if (row.supersetId != null && supersetGroupId == null) {
                    workoutSetDao.updateSupersetGroup(newSetId, newSetId)
                    supersetLeaderId[workoutKey to row.supersetId] = newSetId
                }
            }

            ImportResult(
                muscleGroups = if (exercisesCreated > 0) 1 else 0,
                exercises = exercisesCreated,
                workouts = workoutsCreated,
                workoutSets = setsCreated,
                bodyWeightLogs = 0
            )
        }
    }
}
