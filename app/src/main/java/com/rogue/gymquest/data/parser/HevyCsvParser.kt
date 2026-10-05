package com.rogue.gymquest.data.parser

import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.local.entity.SetType
import java.time.LocalDateTime
import java.time.ZoneId

data class HevyRow(
    val workoutTitle: String,
    val startedAt: Long,
    val finishedAt: Long,
    val description: String?,
    val exerciseName: String,
    val supersetId: String?,
    val exerciseNotes: String?,
    val setType: SetType,
    val weight: Double?,
    val reps: Int?,
    val distanceMeters: Double?,
    val durationSeconds: Int?,
    val rpe: Int?
)

/**
 * Parses a Hevy workout export CSV (columns: title, start_time, end_time, description,
 * exercise_title, superset_id, exercise_notes, set_index, set_type, weight_kg, reps,
 * distance_km, duration_seconds, rpe) into [HevyRow]s.
 */
object HevyCsvParser {

    private val monthAbbreviations = mapOf(
        "jan" to 1, "fev" to 2, "mar" to 3, "abr" to 4, "mai" to 5, "jun" to 6,
        "jul" to 7, "ago" to 8, "set" to 9, "out" to 10, "nov" to 11, "dez" to 12
    )

    private val datePattern = Regex("""^(\d{1,2}) (\p{L}{3}) (\d{4}), (\d{2}):(\d{2})$""")

    fun parse(text: String): List<HevyRow> {
        return CsvParser.parse(text).map { row ->
            HevyRow(
                workoutTitle = row["title"].orEmpty(),
                startedAt = parseHevyDate(row["start_time"].orEmpty()),
                finishedAt = parseHevyDate(row["end_time"].orEmpty()),
                description = row["description"]?.trim()?.ifBlank { null },
                exerciseName = row["exercise_title"].orEmpty(),
                supersetId = row["superset_id"]?.trim()?.ifBlank { null },
                exerciseNotes = row["exercise_notes"]?.trim()?.ifBlank { null },
                setType = parseSetType(row["set_type"]),
                weight = row["weight_kg"]?.trim()?.toDoubleOrNull(),
                reps = row["reps"]?.trim()?.toDoubleOrNull()?.toInt(),
                distanceMeters = row["distance_km"]?.trim()?.toDoubleOrNull()?.let { it * 1000 },
                durationSeconds = row["duration_seconds"]?.trim()?.toDoubleOrNull()?.toInt(),
                rpe = row["rpe"]?.trim()?.toDoubleOrNull()?.let { Math.round(it).toInt() }
            )
        }
    }

    fun inferExerciseType(rows: List<HevyRow>, exerciseName: String): ExerciseType {
        val forExercise = rows.filter { it.exerciseName == exerciseName }
        return when {
            forExercise.any { it.weight != null } -> ExerciseType.WEIGHT_REPS
            forExercise.any { it.distanceMeters != null } -> ExerciseType.DISTANCE_TIME
            forExercise.any { it.durationSeconds != null } -> ExerciseType.TIME
            forExercise.any { it.reps != null } -> ExerciseType.BODYWEIGHT_REPS
            else -> ExerciseType.WEIGHT_REPS
        }
    }

    private fun parseSetType(raw: String?): SetType = when (raw?.trim()?.lowercase()) {
        "warmup" -> SetType.WARMUP
        "dropset" -> SetType.DROP_SET
        "failure" -> SetType.FAILURE
        else -> SetType.NORMAL
    }

    private fun parseHevyDate(text: String): Long {
        val match = datePattern.matchEntire(text.trim())
            ?: throw IllegalArgumentException("Data em formato inesperado: $text")

        val day = match.groupValues[1].toInt()
        val month = monthAbbreviations[match.groupValues[2].lowercase()]
            ?: throw IllegalArgumentException("Mês desconhecido: ${match.groupValues[2]}")
        val year = match.groupValues[3].toInt()
        val hour = match.groupValues[4].toInt()
        val minute = match.groupValues[5].toInt()

        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
