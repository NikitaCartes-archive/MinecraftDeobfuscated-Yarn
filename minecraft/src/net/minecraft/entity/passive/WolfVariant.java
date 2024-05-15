package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public final class WolfVariant {
	public static final Codec<WolfVariant> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("wild_texture").forGetter(wolfVariant -> wolfVariant.wildId),
					Identifier.CODEC.fieldOf("tame_texture").forGetter(wolfVariant -> wolfVariant.tameId),
					Identifier.CODEC.fieldOf("angry_texture").forGetter(wolfVariant -> wolfVariant.angryId),
					RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(WolfVariant::getBiomes)
				)
				.apply(instance, WolfVariant::new)
	);
	public static final PacketCodec<RegistryByteBuf, WolfVariant> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC,
		WolfVariant::getWildTextureId,
		Identifier.PACKET_CODEC,
		WolfVariant::getTameTextureId,
		Identifier.PACKET_CODEC,
		WolfVariant::getAngryTextureId,
		PacketCodecs.registryEntryList(RegistryKeys.BIOME),
		WolfVariant::getBiomes,
		WolfVariant::new
	);
	public static final Codec<RegistryEntry<WolfVariant>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.WOLF_VARIANT, CODEC);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<WolfVariant>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.WOLF_VARIANT, PACKET_CODEC
	);
	private final Identifier wildId;
	private final Identifier tameId;
	private final Identifier angryId;
	private final Identifier wildTextureId;
	private final Identifier tameTextureId;
	private final Identifier angryTextureId;
	private final RegistryEntryList<Biome> biomes;

	public WolfVariant(Identifier wildId, Identifier tameId, Identifier angryId, RegistryEntryList<Biome> biomes) {
		this.wildId = wildId;
		this.wildTextureId = getTextureId(wildId);
		this.tameId = tameId;
		this.tameTextureId = getTextureId(tameId);
		this.angryId = angryId;
		this.angryTextureId = getTextureId(angryId);
		this.biomes = biomes;
	}

	private static Identifier getTextureId(Identifier id) {
		return id.withPath((UnaryOperator<String>)(oldPath -> "textures/" + oldPath + ".png"));
	}

	public Identifier getWildTextureId() {
		return this.wildTextureId;
	}

	public Identifier getTameTextureId() {
		return this.tameTextureId;
	}

	public Identifier getAngryTextureId() {
		return this.angryTextureId;
	}

	public RegistryEntryList<Biome> getBiomes() {
		return this.biomes;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			return !(o instanceof WolfVariant wolfVariant)
				? false
				: Objects.equals(this.wildId, wolfVariant.wildId)
					&& Objects.equals(this.tameId, wolfVariant.tameId)
					&& Objects.equals(this.angryId, wolfVariant.angryId)
					&& Objects.equals(this.biomes, wolfVariant.biomes);
		}
	}

	public int hashCode() {
		int i = 1;
		i = 31 * i + this.wildId.hashCode();
		i = 31 * i + this.tameId.hashCode();
		i = 31 * i + this.angryId.hashCode();
		return 31 * i + this.biomes.hashCode();
	}
}
