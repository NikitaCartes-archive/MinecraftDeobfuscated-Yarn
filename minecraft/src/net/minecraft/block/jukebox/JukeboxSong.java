package net.minecraft.block.jukebox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record JukeboxSong(RegistryEntry<SoundEvent> soundEvent, Text description, float lengthInSeconds, int comparatorOutput) {
	public static final Codec<JukeboxSong> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(JukeboxSong::soundEvent),
					TextCodecs.CODEC.fieldOf("description").forGetter(JukeboxSong::description),
					Codecs.POSITIVE_FLOAT.fieldOf("length_in_seconds").forGetter(JukeboxSong::lengthInSeconds),
					Codecs.rangedInt(0, 15).fieldOf("comparator_output").forGetter(JukeboxSong::comparatorOutput)
				)
				.apply(instance, JukeboxSong::new)
	);
	public static final PacketCodec<RegistryByteBuf, JukeboxSong> PACKET_CODEC = PacketCodec.tuple(
		SoundEvent.ENTRY_PACKET_CODEC,
		JukeboxSong::soundEvent,
		TextCodecs.REGISTRY_PACKET_CODEC,
		JukeboxSong::description,
		PacketCodecs.FLOAT,
		JukeboxSong::lengthInSeconds,
		PacketCodecs.VAR_INT,
		JukeboxSong::comparatorOutput,
		JukeboxSong::new
	);
	public static final Codec<RegistryEntry<JukeboxSong>> ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.JUKEBOX_SONG);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<JukeboxSong>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.JUKEBOX_SONG, PACKET_CODEC
	);
	private static final int TICKS_PER_SECOND = 20;

	public int getLengthInTicks() {
		return MathHelper.ceil(this.lengthInSeconds * 20.0F);
	}

	public boolean shouldStopPlaying(long ticksSinceSongStarted) {
		return ticksSinceSongStarted >= (long)(this.getLengthInTicks() + 20);
	}

	public static Optional<RegistryEntry<JukeboxSong>> getSongEntryFromStack(RegistryWrapper.WrapperLookup registryLookup, ItemStack stack) {
		JukeboxPlayableComponent jukeboxPlayableComponent = stack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
		return jukeboxPlayableComponent != null ? jukeboxPlayableComponent.song().getEntry(registryLookup) : Optional.empty();
	}
}
