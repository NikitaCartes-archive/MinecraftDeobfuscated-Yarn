package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ReferenceFont(Identifier id) implements FontLoader {
	public static final MapCodec<ReferenceFont> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Identifier.CODEC.fieldOf("id").forGetter(ReferenceFont::id)).apply(instance, ReferenceFont::new)
	);

	@Override
	public FontType getType() {
		return FontType.REFERENCE;
	}

	@Override
	public Either<FontLoader.Loadable, FontLoader.Reference> build() {
		return Either.right(new FontLoader.Reference(this.id));
	}
}
