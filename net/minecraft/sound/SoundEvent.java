/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SoundEvent {
    public static final Codec<SoundEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("sound_id")).forGetter(SoundEvent::getId), Codec.FLOAT.optionalFieldOf("range").forGetter(SoundEvent::getStaticDistanceToTravel)).apply((Applicative<SoundEvent, ?>)instance, SoundEvent::of));
    public static final Codec<RegistryEntry<SoundEvent>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.SOUND_EVENT, CODEC);
    private static final float DEFAULT_DISTANCE_TO_TRAVEL = 16.0f;
    private final Identifier id;
    private final float distanceToTravel;
    private final boolean staticDistance;

    private static SoundEvent of(Identifier id, Optional<Float> distanceToTravel) {
        return distanceToTravel.map(float_ -> SoundEvent.of(id, float_.floatValue())).orElseGet(() -> SoundEvent.of(id));
    }

    public static SoundEvent of(Identifier id) {
        return new SoundEvent(id, 16.0f, false);
    }

    public static SoundEvent of(Identifier id, float distanceToTravel) {
        return new SoundEvent(id, distanceToTravel, true);
    }

    private SoundEvent(Identifier id, float distanceToTravel, boolean useStaticDistance) {
        this.id = id;
        this.distanceToTravel = distanceToTravel;
        this.staticDistance = useStaticDistance;
    }

    public Identifier getId() {
        return this.id;
    }

    public float getDistanceToTravel(float volume) {
        if (this.staticDistance) {
            return this.distanceToTravel;
        }
        return volume > 1.0f ? 16.0f * volume : 16.0f;
    }

    private Optional<Float> getStaticDistanceToTravel() {
        return this.staticDistance ? Optional.of(Float.valueOf(this.distanceToTravel)) : Optional.empty();
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeIdentifier(this.id);
        buf.writeOptional(this.getStaticDistanceToTravel(), PacketByteBuf::writeFloat);
    }

    public static SoundEvent fromBuf(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        Optional<Float> optional = buf.readOptional(PacketByteBuf::readFloat);
        return SoundEvent.of(identifier, optional);
    }
}

