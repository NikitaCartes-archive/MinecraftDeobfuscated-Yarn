package net.minecraft.client.font;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class UnihexFont implements Font {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_44764 = 16;
	private static final int field_44765 = 2;
	private static final int field_44766 = 32;
	private static final int field_44767 = 64;
	private static final int field_44768 = 96;
	private static final int field_44769 = 128;
	private final GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphs;

	UnihexFont(GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphs) {
		this.glyphs = glyphs;
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		return this.glyphs.get(codePoint);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return this.glyphs.getProvidedGlyphs();
	}

	@VisibleForTesting
	static void addRowPixels(IntBuffer pixelsOut, int row, int left, int right) {
		int i = 32 - left - 1;
		int j = 32 - right - 1;

		for (int k = i; k >= j; k--) {
			if (k < 32 && k >= 0) {
				boolean bl = (row >> k & 1) != 0;
				pixelsOut.put(bl ? -1 : 0);
			} else {
				pixelsOut.put(0);
			}
		}
	}

	static void addGlyphPixels(IntBuffer pixelsOut, UnihexFont.BitmapGlyph glyph, int left, int right) {
		for (int i = 0; i < 16; i++) {
			int j = glyph.getPixels(i);
			addRowPixels(pixelsOut, j, left, right);
		}
	}

	@VisibleForTesting
	static void readLines(InputStream stream, UnihexFont.BitmapGlyphConsumer callback) throws IOException {
		int i = 0;
		ByteList byteList = new ByteArrayList(128);

		while (true) {
			boolean bl = readUntilDelimiter(stream, byteList, 58);
			int j = byteList.size();
			if (j == 0 && !bl) {
				return;
			}

			if (!bl || j != 4 && j != 5 && j != 6) {
				throw new IllegalArgumentException("Invalid entry at line " + i + ": expected 4, 5 or 6 hex digits followed by a colon");
			}

			int k = 0;

			for (int l = 0; l < j; l++) {
				k = k << 4 | getHexDigitValue(i, byteList.getByte(l));
			}

			byteList.clear();
			readUntilDelimiter(stream, byteList, 10);
			int l = byteList.size();

			UnihexFont.BitmapGlyph bitmapGlyph = switch (l) {
				case 32 -> UnihexFont.FontImage8x16.read(i, byteList);
				case 64 -> UnihexFont.FontImage16x16.read(i, byteList);
				case 96 -> UnihexFont.FontImage32x16.read24x16(i, byteList);
				case 128 -> UnihexFont.FontImage32x16.read32x16(i, byteList);
				default -> throw new IllegalArgumentException(
				"Invalid entry at line " + i + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line"
			);
			};
			callback.accept(k, bitmapGlyph);
			i++;
			byteList.clear();
		}
	}

	static int getHexDigitValue(int lineNum, ByteList bytes, int index) {
		return getHexDigitValue(lineNum, bytes.getByte(index));
	}

	private static int getHexDigitValue(int lineNum, byte digit) {
		return switch (digit) {
			case 48 -> 0;
			case 49 -> 1;
			case 50 -> 2;
			case 51 -> 3;
			case 52 -> 4;
			case 53 -> 5;
			case 54 -> 6;
			case 55 -> 7;
			case 56 -> 8;
			case 57 -> 9;
			default -> throw new IllegalArgumentException("Invalid entry at line " + lineNum + ": expected hex digit, got " + (char)digit);
			case 65 -> 10;
			case 66 -> 11;
			case 67 -> 12;
			case 68 -> 13;
			case 69 -> 14;
			case 70 -> 15;
		};
	}

	private static boolean readUntilDelimiter(InputStream stream, ByteList data, int delimiter) throws IOException {
		while (true) {
			int i = stream.read();
			if (i == -1) {
				return false;
			}

			if (i == delimiter) {
				return true;
			}

			data.add((byte)i);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface BitmapGlyph {
		int getPixels(int y);

		int bitWidth();

		default int getNonemptyColumnBitmask() {
			int i = 0;

			for (int j = 0; j < 16; j++) {
				i |= this.getPixels(j);
			}

			return i;
		}

		default int getPackedDimensions() {
			int i = this.getNonemptyColumnBitmask();
			int j = this.bitWidth();
			int k;
			int l;
			if (i == 0) {
				k = 0;
				l = j;
			} else {
				k = Integer.numberOfLeadingZeros(i);
				l = 32 - Integer.numberOfTrailingZeros(i) - 1;
			}

			return UnihexFont.Dimensions.pack(k, l);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface BitmapGlyphConsumer {
		void accept(int codePoint, UnihexFont.BitmapGlyph glyph);
	}

	@Environment(EnvType.CLIENT)
	static record DimensionOverride(int from, int to, UnihexFont.Dimensions dimensions) {
		private static final Codec<UnihexFont.DimensionOverride> NON_VALIDATED_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.CODEPOINT.fieldOf("from").forGetter(UnihexFont.DimensionOverride::from),
						Codecs.CODEPOINT.fieldOf("to").forGetter(UnihexFont.DimensionOverride::to),
						UnihexFont.Dimensions.MAP_CODEC.forGetter(UnihexFont.DimensionOverride::dimensions)
					)
					.apply(instance, UnihexFont.DimensionOverride::new)
		);
		public static final Codec<UnihexFont.DimensionOverride> CODEC = Codecs.validate(
			NON_VALIDATED_CODEC,
			override -> override.from >= override.to
					? DataResult.error(() -> "Invalid range: [" + override.from + ";" + override.to + "]")
					: DataResult.success(override)
		);
	}

	@Environment(EnvType.CLIENT)
	public static record Dimensions(int left, int right) {
		public static final MapCodec<UnihexFont.Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.INT.fieldOf("left").forGetter(UnihexFont.Dimensions::left), Codec.INT.fieldOf("right").forGetter(UnihexFont.Dimensions::right)
					)
					.apply(instance, UnihexFont.Dimensions::new)
		);
		public static final Codec<UnihexFont.Dimensions> CODEC = MAP_CODEC.codec();

		public int packedValue() {
			return pack(this.left, this.right);
		}

		public static int pack(int left, int right) {
			return (left & 0xFF) << 8 | right & 0xFF;
		}

		public static int getLeft(int packed) {
			return (byte)(packed >> 8);
		}

		public static int getRight(int packed) {
			return (byte)packed;
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontImage16x16(short[] contents) implements UnihexFont.BitmapGlyph {
		@Override
		public int getPixels(int y) {
			return this.contents[y] << 16;
		}

		static UnihexFont.BitmapGlyph read(int lineNum, ByteList data) {
			short[] ss = new short[16];
			int i = 0;

			for (int j = 0; j < 16; j++) {
				int k = UnihexFont.getHexDigitValue(lineNum, data, i++);
				int l = UnihexFont.getHexDigitValue(lineNum, data, i++);
				int m = UnihexFont.getHexDigitValue(lineNum, data, i++);
				int n = UnihexFont.getHexDigitValue(lineNum, data, i++);
				short s = (short)(k << 12 | l << 8 | m << 4 | n);
				ss[j] = s;
			}

			return new UnihexFont.FontImage16x16(ss);
		}

		@Override
		public int bitWidth() {
			return 16;
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontImage32x16(int[] contents, int bitWidth) implements UnihexFont.BitmapGlyph {
		private static final int field_44775 = 24;

		@Override
		public int getPixels(int y) {
			return this.contents[y];
		}

		static UnihexFont.BitmapGlyph read24x16(int lineNum, ByteList data) {
			int[] is = new int[16];
			int i = 0;
			int j = 0;

			for (int k = 0; k < 16; k++) {
				int l = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int m = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int n = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int o = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int p = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int q = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int r = l << 20 | m << 16 | n << 12 | o << 8 | p << 4 | q;
				is[k] = r << 8;
				i |= r;
			}

			return new UnihexFont.FontImage32x16(is, 24);
		}

		public static UnihexFont.BitmapGlyph read32x16(int lineNum, ByteList data) {
			int[] is = new int[16];
			int i = 0;
			int j = 0;

			for (int k = 0; k < 16; k++) {
				int l = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int m = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int n = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int o = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int p = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int q = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int r = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int s = UnihexFont.getHexDigitValue(lineNum, data, j++);
				int t = l << 28 | m << 24 | n << 20 | o << 16 | p << 12 | q << 8 | r << 4 | s;
				is[k] = t;
				i |= t;
			}

			return new UnihexFont.FontImage32x16(is, 32);
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontImage8x16(byte[] contents) implements UnihexFont.BitmapGlyph {
		@Override
		public int getPixels(int y) {
			return this.contents[y] << 24;
		}

		static UnihexFont.BitmapGlyph read(int lineNum, ByteList data) {
			byte[] bs = new byte[16];
			int i = 0;

			for (int j = 0; j < 16; j++) {
				int k = UnihexFont.getHexDigitValue(lineNum, data, i++);
				int l = UnihexFont.getHexDigitValue(lineNum, data, i++);
				byte b = (byte)(k << 4 | l);
				bs[j] = b;
			}

			return new UnihexFont.FontImage8x16(bs);
		}

		@Override
		public int bitWidth() {
			return 8;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		public static final MapCodec<UnihexFont.Loader> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC.fieldOf("hex_file").forGetter(loader -> loader.sizes),
						UnihexFont.DimensionOverride.CODEC.listOf().fieldOf("size_overrides").forGetter(loader -> loader.overrides)
					)
					.apply(instance, UnihexFont.Loader::new)
		);
		private final Identifier sizes;
		private final List<UnihexFont.DimensionOverride> overrides;

		private Loader(Identifier sizes, List<UnihexFont.DimensionOverride> overrides) {
			this.sizes = sizes;
			this.overrides = overrides;
		}

		@Override
		public FontType getType() {
			return FontType.UNIHEX;
		}

		@Override
		public Either<FontLoader.Loadable, FontLoader.Reference> build() {
			return Either.left(this::load);
		}

		private Font load(ResourceManager resourceManager) throws IOException {
			InputStream inputStream = resourceManager.open(this.sizes);

			UnihexFont var3;
			try {
				var3 = this.loadHexFile(inputStream);
			} catch (Throwable var6) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var3;
		}

		private UnihexFont loadHexFile(InputStream stream) throws IOException {
			GlyphContainer<UnihexFont.BitmapGlyph> glyphContainer = new GlyphContainer<>(UnihexFont.BitmapGlyph[]::new, UnihexFont.BitmapGlyph[][]::new);
			UnihexFont.BitmapGlyphConsumer bitmapGlyphConsumer = glyphContainer::put;
			ZipInputStream zipInputStream = new ZipInputStream(stream);

			UnihexFont var17;
			try {
				ZipEntry zipEntry;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					String string = zipEntry.getName();
					if (string.endsWith(".hex")) {
						UnihexFont.LOGGER.info("Found {}, loading", string);
						UnihexFont.readLines(new FixedBufferInputStream(zipInputStream), bitmapGlyphConsumer);
					}
				}

				GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphContainer2 = new GlyphContainer<>(
					UnihexFont.UnicodeTextureGlyph[]::new, UnihexFont.UnicodeTextureGlyph[][]::new
				);

				for (UnihexFont.DimensionOverride dimensionOverride : this.overrides) {
					int i = dimensionOverride.from;
					int j = dimensionOverride.to;
					UnihexFont.Dimensions dimensions = dimensionOverride.dimensions;

					for (int k = i; k <= j; k++) {
						UnihexFont.BitmapGlyph bitmapGlyph = glyphContainer.remove(k);
						if (bitmapGlyph != null) {
							glyphContainer2.put(k, new UnihexFont.UnicodeTextureGlyph(bitmapGlyph, dimensions.left, dimensions.right));
						}
					}
				}

				glyphContainer.forEachGlyph((codePoint, glyph) -> {
					int ix = glyph.getPackedDimensions();
					int jx = UnihexFont.Dimensions.getLeft(ix);
					int kx = UnihexFont.Dimensions.getRight(ix);
					glyphContainer2.put(codePoint, new UnihexFont.UnicodeTextureGlyph(glyph, jx, kx));
				});
				var17 = new UnihexFont(glyphContainer2);
			} catch (Throwable var15) {
				try {
					zipInputStream.close();
				} catch (Throwable var14) {
					var15.addSuppressed(var14);
				}

				throw var15;
			}

			zipInputStream.close();
			return var17;
		}
	}

	@Environment(EnvType.CLIENT)
	static record UnicodeTextureGlyph(UnihexFont.BitmapGlyph contents, int left, int right) implements Glyph {

		public int width() {
			return this.right - this.left + 1;
		}

		@Override
		public float getAdvance() {
			return (float)(this.width() / 2 + 1);
		}

		@Override
		public float getShadowOffset() {
			return 0.5F;
		}

		@Override
		public float getBoldOffset() {
			return 0.5F;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(new RenderableGlyph() {
				@Override
				public float getOversample() {
					return 2.0F;
				}

				@Override
				public int getWidth() {
					return UnicodeTextureGlyph.this.width();
				}

				@Override
				public int getHeight() {
					return 16;
				}

				@Override
				public void upload(int x, int y) {
					try (MemoryStack memoryStack = MemoryStack.stackPush()) {
						IntBuffer intBuffer = memoryStack.mallocInt(UnicodeTextureGlyph.this.width() * 16);
						UnihexFont.addGlyphPixels(intBuffer, UnicodeTextureGlyph.this.contents, UnicodeTextureGlyph.this.left, UnicodeTextureGlyph.this.right);
						intBuffer.rewind();
						GlStateManager.upload(0, x, y, UnicodeTextureGlyph.this.width(), 16, NativeImage.Format.RGBA, intBuffer);
					}
				}

				@Override
				public boolean hasColor() {
					return true;
				}
			});
		}
	}
}
