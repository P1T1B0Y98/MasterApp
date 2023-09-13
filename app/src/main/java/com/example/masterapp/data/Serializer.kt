package com.example.masterapp.data

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializer(forClass = ZonedDateTime::class)
object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString(), formatter)
    }
}

// Note: You'd adjust the serialization for each based on your needs,
// but here's a generic example for Length:
object LengthSerializer : KSerializer<Length> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Length", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Length) {
        encoder.encodeDouble(value.inKilometers)
    }

    override fun deserialize(decoder: Decoder): Length {
        return Length.kilometers(decoder.decodeDouble())
    }
}



object EnergySerializer : KSerializer<Energy> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Energy", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Energy) {
        encoder.encodeDouble(value.inKilocalories) // Serializing using joules for consistency
    }

    override fun deserialize(decoder: Decoder): Energy {
        return Energy.kilocalories(decoder.decodeDouble()) // Deserializing assuming joules was the chosen unit for serialization
    }
}


object CustomVelocitySerializer : KSerializer<Velocity> {
    override val descriptor = PrimitiveSerialDescriptor("Velocity", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Velocity) {
        encoder.encodeVelocity(value)
    }

    override fun deserialize(decoder: Decoder): Velocity {
        return decoder.decodeVelocity()
    }
}

fun Encoder.encodeVelocity(velocity: Velocity) {
    encodeDouble(velocity.inMetersPerSecond)
}

fun Decoder.decodeVelocity(): Velocity {
    return Velocity.metersPerSecond(decodeDouble())
}

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

@Serializer(forClass = ZoneOffset::class)
object ZoneOffsetSerializer : KSerializer<ZoneOffset> {
    override val descriptor = PrimitiveSerialDescriptor("ZoneOffset", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZoneOffset) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): ZoneOffset {
        return ZoneOffset.of(decoder.decodeString())
    }
}

@Serializer(forClass = Duration::class)
object DurationSerializer : KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return Duration.parse(decoder.decodeString())
    }
}

@Serializer(forClass = Drawable::class)
object DrawableSerializer : KSerializer<Drawable?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Drawable", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Drawable?) {
        value?.let {
            val bitmap = (it as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            encoder.encodeString(encoded)
        } ?: encoder.encodeNull()
    }

    override fun deserialize(decoder: Decoder): Drawable? {
        val string = decoder.decodeString()
        val decodeString = Base64.decode(string, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}


