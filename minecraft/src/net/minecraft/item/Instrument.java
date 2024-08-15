package net.minecraft.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public record Instrument(RegistryEntry<SoundEvent> soundEvent, float useDuration, float range, Text description) {
	public static final Codec<Instrument> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent),
					Codecs.POSITIVE_FLOAT.fieldOf("use_duration").forGetter(Instrument::useDuration),
					Codecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range),
					TextCodecs.CODEC.fieldOf("description").forGetter(Instrument::description)
				)
				.apply(instance, Instrument::new)
	);
	public static final PacketCodec<RegistryByteBuf, Instrument> PACKET_CODEC = PacketCodec.tuple(
		SoundEvent.ENTRY_PACKET_CODEC,
		Instrument::soundEvent,
		PacketCodecs.FLOAT,
		Instrument::useDuration,
		PacketCodecs.FLOAT,
		Instrument::range,
		TextCodecs.REGISTRY_PACKET_CODEC,
		Instrument::description,
		Instrument::new
	);
	public static final Codec<RegistryEntry<Instrument>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.INSTRUMENT, CODEC);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<Instrument>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.INSTRUMENT, PACKET_CODEC
	);
}
