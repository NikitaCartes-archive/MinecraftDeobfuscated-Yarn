package net.minecraft.entity.decoration.painting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record PaintingVariant(int width, int height, Identifier assetId, Optional<Text> title, Optional<Text> author) {
	public static final Codec<PaintingVariant> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.rangedInt(1, 16).fieldOf("width").forGetter(PaintingVariant::width),
					Codecs.rangedInt(1, 16).fieldOf("height").forGetter(PaintingVariant::height),
					Identifier.CODEC.fieldOf("asset_id").forGetter(PaintingVariant::assetId),
					TextCodecs.CODEC.optionalFieldOf("title").forGetter(PaintingVariant::title),
					TextCodecs.CODEC.optionalFieldOf("author").forGetter(PaintingVariant::author)
				)
				.apply(instance, PaintingVariant::new)
	);
	public static final PacketCodec<RegistryByteBuf, PaintingVariant> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		PaintingVariant::width,
		PacketCodecs.VAR_INT,
		PaintingVariant::height,
		Identifier.PACKET_CODEC,
		PaintingVariant::assetId,
		TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
		PaintingVariant::title,
		TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
		PaintingVariant::author,
		PaintingVariant::new
	);
	public static final Codec<RegistryEntry<PaintingVariant>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PAINTING_VARIANT, CODEC);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<PaintingVariant>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.PAINTING_VARIANT, PACKET_CODEC
	);

	public int getArea() {
		return this.width() * this.height();
	}
}
