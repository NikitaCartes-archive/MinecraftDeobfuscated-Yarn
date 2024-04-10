package net.minecraft.client.font;

import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;

@Environment(EnvType.CLIENT)
public enum BuiltinEmptyGlyph implements Glyph {
	WHITE(() -> createRectImage(5, 8, (x, y) -> -1)),
	MISSING(() -> {
		int i = 5;
		int j = 8;
		return createRectImage(5, 8, (x, y) -> {
			boolean bl = x == 0 || x + 1 == 5 || y == 0 || y + 1 == 8;
			return bl ? -1 : 0;
		});
	});

	final NativeImage image;

	private static NativeImage createRectImage(int width, int height, BuiltinEmptyGlyph.ColorSupplier colorSupplier) {
		NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				nativeImage.setColor(j, i, colorSupplier.getColor(j, i));
			}
		}

		nativeImage.untrack();
		return nativeImage;
	}

	private BuiltinEmptyGlyph(final Supplier<NativeImage> imageSupplier) {
		this.image = (NativeImage)imageSupplier.get();
	}

	@Override
	public float getAdvance() {
		return (float)(this.image.getWidth() + 1);
	}

	@Override
	public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
		return (GlyphRenderer)function.apply(new RenderableGlyph() {
			@Override
			public int getWidth() {
				return BuiltinEmptyGlyph.this.image.getWidth();
			}

			@Override
			public int getHeight() {
				return BuiltinEmptyGlyph.this.image.getHeight();
			}

			@Override
			public float getOversample() {
				return 1.0F;
			}

			@Override
			public void upload(int x, int y) {
				BuiltinEmptyGlyph.this.image.upload(0, x, y, false);
			}

			@Override
			public boolean hasColor() {
				return true;
			}
		});
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface ColorSupplier {
		int getColor(int x, int y);
	}
}
