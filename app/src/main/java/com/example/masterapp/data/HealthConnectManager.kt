package com.example.masterapp.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Velocity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectManager(val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    )

    val _isPermissionGranted = MutableStateFlow(false)

    // This is the public Flow (immutable)
    val isPermissionGranted: Flow<Boolean> = _isPermissionGranted.asStateFlow()

    val healthConnectCompatibleApps by lazy {
        val intent = Intent("androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE")

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
        }

        packages.associate {
            val icon = try {
                context.packageManager.getApplicationIcon(it.activityInfo.packageName)
            } catch(e: NotFoundException) {
                null
            }
            val label = context.packageManager.getApplicationLabel(it.activityInfo.applicationInfo)
                .toString()
            it.activityInfo.packageName to
                    MasterAppInfo(
                        packageName = it.activityInfo.packageName,
                        icon = icon,
                        appLabel = label
                    )
        }
    }
    var availability = mutableStateOf(SDK_UNAVAILABLE)
        private set

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        Log.i("Checking", "Checking")
        Log.i("Context", "Context here now what: ${context.applicationContext}")
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("Not installed", "Not installed")
            false
        }
    }

    fun openAppInPlayStoreIfNotInstalled(context: Context, packageName: String) {
        if (!isAppInstalled(context, packageName)) {
            try {
                val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=$packageName")
                    setPackage("com.android.vending")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playStoreIntent)
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
            }
        }
    }

    fun checkAvailability() {
        availability.value = HealthConnectClient.sdkStatus(context)
    }


    init {
        checkAvailability()
    }

    /**
     * Determines whether all the specified permissions are already granted. It is recommended to
     * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
     * permissions are already granted then there is no need to request permissions via
     * [PermissionController.createRequestPermissionResultContract].
     */
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        Log.i("HealthConnectManager", "Checking if all permissions are granted...")
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        Log.i("HealthConnectManager", "Currently granted permissions: $grantedPermissions")

        return if (grantedPermissions.containsAll(permissions)) {
            _isPermissionGranted.value = true
            Log.i("HealthConnectManager", "All permissions are granted!")
            true
        } else {
            _isPermissionGranted.value = false
            Log.i("HealthConnectManager", "Not all permissions are granted.")
            false
        }
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    suspend fun revokeAllPermissions() {
        healthConnectClient.permissionController.revokeAllPermissions()
        _isPermissionGranted.value = false
    }


    suspend fun readSleepRecords(startTime: Instant, endTime: Instant ): List<SleepSessionData> {
        Log.i("Sleep Records", "Reading sleep records")
        val response =
            healthConnectClient.readRecords(ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime,
                    endTime
                )
            ))

        val sleepSessionDataList = mutableListOf<SleepSessionData>()

        for (sleepRecord in response.records) {
            Log.i("Sleep Record", sleepRecord.toString())

            val sleepStageRecords = healthConnectClient
                .readRecords(
                    ReadRecordsRequest(
                        recordType = SleepStageRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(
                            sleepRecord.startTime,
                            sleepRecord.endTime
                        )
                    )
                )
                .records

            val sleepSessionData = SleepSessionData(
                uid = sleepRecord.metadata.id,
                title = sleepRecord.title,
                notes = sleepRecord.notes,
                startTime = sleepRecord.startTime,
                startZoneOffset = sleepRecord.startZoneOffset,
                endTime = sleepRecord.endTime,
                endZoneOffset = sleepRecord.endZoneOffset,
                duration = Duration.between(sleepRecord.startTime, sleepRecord.endTime),
                stages = sleepStageRecords
            )

            sleepSessionDataList.add(sleepSessionData)

            for (stageRecord in sleepStageRecords) {
                Log.i("Sleep Stage Record", stageRecord.toString())
            }
        }

        return sleepSessionDataList
    }


    suspend fun readHeartRateVariabilityRecord(start: Instant, end: Instant): List<HeartRateVariabilityData> {
        val request = ReadRecordsRequest(
            recordType = HeartRateVariabilityRmssdRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)

        return response.records.map { record ->
            HeartRateVariabilityData(
                heartRateVariability = record.heartRateVariabilityMillis,
                id = record.metadata.id,
                time = record.time,
                sourceAppInfo = healthConnectCompatibleApps[record.metadata.dataOrigin.packageName]
            )
        }
    }


    suspend fun readHeartRateRecord(start: Instant, end: Instant): List<HeartRateData> {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)

        return response.records.map { record ->
            HeartRateData(
                id = record.metadata.id,
                startTime = record.startTime,
                endTime = record.endTime,
                samples = record.samples.map { sample ->
                    HeartRateDataSample(
                        beatsPerMinute = sample.beatsPerMinute,
                        time = sample.time
                    )
                },
                sourceAppInfo = healthConnectCompatibleApps[record.metadata.dataOrigin.packageName]
            )
        }
    }


    suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    /**
     * Reads aggregated data and raw data for selected data types, for a given [ExerciseSessionRecord].
     */
    suspend fun readAssociatedSessionData(
        uid: String
    ): ExerciseSessionData {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        // Use the start time and end time from the session, for reading raw and aggregate data.
        val timeRangeFilter = TimeRangeFilter.between(
            startTime = exerciseSession.record.startTime,
            endTime = exerciseSession.record.endTime
        )
        val aggregateDataTypes = setOf(
            ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
            StepsRecord.COUNT_TOTAL,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            HeartRateRecord.BPM_AVG,
            HeartRateRecord.BPM_MAX,
            HeartRateRecord.BPM_MIN,
            SpeedRecord.SPEED_AVG,
            SpeedRecord.SPEED_MAX,
            SpeedRecord.SPEED_MIN
        )
        // Limit the data read to just the application that wrote the session. This may or may not
        // be desirable depending on the use case: In some cases, it may be useful to combine with
        // data written by other apps.
        val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
        val aggregateRequest = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            dataOriginFilter = dataOriginFilter
        )
        val aggregateData = healthConnectClient.aggregate(aggregateRequest)
        val speedData = readData<SpeedRecord>(timeRangeFilter, dataOriginFilter)
        val heartRateData = readData<HeartRateRecord>(timeRangeFilter, dataOriginFilter)

        return ExerciseSessionData(
            uid = uid,
            totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
            totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
            totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
            totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
            minHeartRate = aggregateData[HeartRateRecord.BPM_MIN],
            maxHeartRate = aggregateData[HeartRateRecord.BPM_MAX],
            avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
            heartRateSeries = heartRateData,
            speedRecord = speedData,
            minSpeed = aggregateData[SpeedRecord.SPEED_MIN],
            maxSpeed = aggregateData[SpeedRecord.SPEED_MAX],
            avgSpeed = aggregateData[SpeedRecord.SPEED_AVG],
        )
    }
    /**
     * Reads in existing [WeightRecord]s.
     */
    suspend fun readWeightInputs(start: Instant, end: Instant): List<WeightRecord> {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    /**
     * Returns the weekly average of [WeightRecord]s.
     */
    suspend fun computeWeeklyAverage(start: Instant, end: Instant): Mass? {
        val request = AggregateRequest(
            metrics = setOf(WeightRecord.WEIGHT_AVG),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.aggregate(request)
        return response[WeightRecord.WEIGHT_AVG]
    }

    /**
     * Convenience function to reuse code for reading data.
     */
    private suspend inline fun <reified T : Record> readData(
        timeRangeFilter: TimeRangeFilter,
        dataOriginFilter: Set<androidx.health.connect.client.records.metadata.DataOrigin> = setOf()
    ): List<T> {
        val request = ReadRecordsRequest(
            recordType = T::class,
            dataOriginFilter = dataOriginFilter,
            timeRangeFilter = timeRangeFilter
        )
        return healthConnectClient.readRecords(request).records
    }

    private fun buildHeartRateSeries(
        sessionStartTime: ZonedDateTime,
        sessionEndTime: ZonedDateTime
    ): HeartRateRecord {
        val samples = mutableListOf<HeartRateRecord.Sample>()
        var time = sessionStartTime
        while (time.isBefore(sessionEndTime)) {
            samples.add(
                HeartRateRecord.Sample(
                    time = time.toInstant(),
                    beatsPerMinute = (80 + Random.nextInt(80)).toLong()
                )
            )
            time = time.plusSeconds(30)
        }
        return HeartRateRecord(
            startTime = sessionStartTime.toInstant(),
            startZoneOffset = sessionStartTime.offset,
            endTime = sessionEndTime.toInstant(),
            endZoneOffset = sessionEndTime.offset,
            samples = samples
        )
    }

    private fun buildSpeedSeries(
        sessionStartTime: ZonedDateTime,
        sessionEndTime: ZonedDateTime
    ) = SpeedRecord(
        startTime = sessionStartTime.toInstant(),
        startZoneOffset = sessionStartTime.offset,
        endTime = sessionEndTime.toInstant(),
        endZoneOffset = sessionEndTime.offset,
        samples = listOf(
            SpeedRecord.Sample(
                time = sessionStartTime.toInstant(),
                speed = Velocity.metersPerSecond(2.5)
            ),
            SpeedRecord.Sample(
                time = sessionStartTime.toInstant().plus(5, ChronoUnit.MINUTES),
                speed = Velocity.metersPerSecond(2.7)
            ),
            SpeedRecord.Sample(
                time = sessionStartTime.toInstant().plus(10, ChronoUnit.MINUTES),
                speed = Velocity.metersPerSecond(2.9)
            )
        )
    )
}
