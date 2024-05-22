package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
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

public record class_9793(RegistryEntry<SoundEvent> soundEvent, Text description, float lengthInSeconds, int comparatorOutput) {
	public static final Codec<class_9793> field_52027 = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(class_9793::soundEvent),
					TextCodecs.CODEC.fieldOf("description").forGetter(class_9793::description),
					Codecs.POSITIVE_FLOAT.fieldOf("length_in_seconds").forGetter(class_9793::lengthInSeconds),
					Codecs.rangedInt(0, 15).fieldOf("comparator_output").forGetter(class_9793::comparatorOutput)
				)
				.apply(instance, class_9793::new)
	);
	public static final PacketCodec<RegistryByteBuf, class_9793> field_52028 = PacketCodec.tuple(
		SoundEvent.ENTRY_PACKET_CODEC,
		class_9793::soundEvent,
		TextCodecs.REGISTRY_PACKET_CODEC,
		class_9793::description,
		PacketCodecs.FLOAT,
		class_9793::lengthInSeconds,
		PacketCodecs.VAR_INT,
		class_9793::comparatorOutput,
		class_9793::new
	);
	public static final Codec<RegistryEntry<class_9793>> field_52029 = RegistryFixedCodec.of(RegistryKeys.JUKEBOX_SONG);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<class_9793>> field_52030 = PacketCodecs.registryEntry(RegistryKeys.JUKEBOX_SONG, field_52028);
	private static final int field_52031 = 20;

	public int method_60750() {
		return MathHelper.ceil(this.lengthInSeconds * 20.0F);
	}

	public boolean method_60751(long l) {
		return l >= (long)(this.method_60750() + 20);
	}

	public static Optional<RegistryEntry<class_9793>> method_60753(RegistryWrapper.WrapperLookup wrapperLookup, ItemStack itemStack) {
		class_9792 lv = itemStack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
		return lv != null ? lv.song().method_60739(wrapperLookup) : Optional.empty();
	}
}
