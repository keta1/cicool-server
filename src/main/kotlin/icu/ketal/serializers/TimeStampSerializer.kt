package icu.ketal.serializers

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimeStampSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("timestamp", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Instant {
        val epochSeconds = decoder.decodeLong()
        return Instant.fromEpochSeconds(epochSeconds, 0)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSeconds)
    }
}
