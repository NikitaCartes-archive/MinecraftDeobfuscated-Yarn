package net.minecraft.entity.decoration.painting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record PaintingVariant(int width, int height, Identifier assetId) {
	public static final Codec<PaintingVariant> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.rangedInt(1, 16).fieldOf("width").forGetter(PaintingVariant::width),
					Codecs.rangedInt(1, 16).fieldOf("height").forGetter(PaintingVariant::height),
					Identifier.CODEC.fieldOf("asset_id").forGetter(PaintingVariant::assetId)
				)
				.apply(instance, PaintingVariant::new)
	);
	public static final Codec<RegistryEntry<PaintingVariant>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PAINTING_VARIANT, CODEC);

	public int getArea() {
		return this.width() * this.height();
	}
}
