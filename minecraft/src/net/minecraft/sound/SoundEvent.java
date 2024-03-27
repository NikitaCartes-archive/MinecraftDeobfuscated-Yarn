package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SoundEvent {
	public static final Codec<SoundEvent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("sound_id").forGetter(SoundEvent::getId),
					Codec.FLOAT.lenientOptionalFieldOf("range").forGetter(SoundEvent::getStaticDistanceToTravel)
				)
				.apply(instance, SoundEvent::of)
	);
	public static final Codec<RegistryEntry<SoundEvent>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.SOUND_EVENT, CODEC);
	public static final PacketCodec<ByteBuf, SoundEvent> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, SoundEvent::getId, PacketCodecs.FLOAT.collect(PacketCodecs::optional), SoundEvent::getStaticDistanceToTravel, SoundEvent::of
	);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<SoundEvent>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.SOUND_EVENT, PACKET_CODEC
	);
	private static final float DEFAULT_DISTANCE_TO_TRAVEL = 16.0F;
	private final Identifier id;
	private final float distanceToTravel;
	private final boolean staticDistance;

	private static SoundEvent of(Identifier id, Optional<Float> distanceToTravel) {
		return (SoundEvent)distanceToTravel.map(float_ -> of(id, float_)).orElseGet(() -> of(id));
	}

	public static SoundEvent of(Identifier id) {
		return new SoundEvent(id, 16.0F, false);
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
		} else {
			return volume > 1.0F ? 16.0F * volume : 16.0F;
		}
	}

	private Optional<Float> getStaticDistanceToTravel() {
		return this.staticDistance ? Optional.of(this.distanceToTravel) : Optional.empty();
	}
}
