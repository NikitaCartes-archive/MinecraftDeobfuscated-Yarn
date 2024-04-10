package net.minecraft.client.texture;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public interface Scaling {
	Codec<Scaling> CODEC = Scaling.Type.CODEC.dispatch(Scaling::getType, Scaling.Type::getCodec);
	Scaling STRETCH = new Scaling.Stretch();

	Scaling.Type getType();

	@Environment(EnvType.CLIENT)
	public static record NineSlice(int width, int height, Scaling.NineSlice.Border border) implements Scaling {
		public static final MapCodec<Scaling.NineSlice> CODEC = RecordCodecBuilder.<Scaling.NineSlice>mapCodec(
				instance -> instance.group(
							Codecs.POSITIVE_INT.fieldOf("width").forGetter(Scaling.NineSlice::width),
							Codecs.POSITIVE_INT.fieldOf("height").forGetter(Scaling.NineSlice::height),
							Scaling.NineSlice.Border.CODEC.fieldOf("border").forGetter(Scaling.NineSlice::border)
						)
						.apply(instance, Scaling.NineSlice::new)
			)
			.validate(Scaling.NineSlice::validate);

		private static DataResult<Scaling.NineSlice> validate(Scaling.NineSlice nineSlice) {
			Scaling.NineSlice.Border border = nineSlice.border();
			if (border.left() + border.right() >= nineSlice.width()) {
				return DataResult.error(() -> "Nine-sliced texture has no horizontal center slice: " + border.left() + " + " + border.right() + " >= " + nineSlice.width());
			} else {
				return border.top() + border.bottom() >= nineSlice.height()
					? DataResult.error(() -> "Nine-sliced texture has no vertical center slice: " + border.top() + " + " + border.bottom() + " >= " + nineSlice.height())
					: DataResult.success(nineSlice);
			}
		}

		@Override
		public Scaling.Type getType() {
			return Scaling.Type.NINE_SLICE;
		}

		@Environment(EnvType.CLIENT)
		public static record Border(int left, int top, int right, int bottom) {
			private static final Codec<Scaling.NineSlice.Border> UNIFORM_SIDE_SIZES_CODEC = Codecs.POSITIVE_INT
				.flatComapMap(size -> new Scaling.NineSlice.Border(size, size, size, size), border -> {
					OptionalInt optionalInt = border.getUniformSideSize();
					return optionalInt.isPresent() ? DataResult.success(optionalInt.getAsInt()) : DataResult.error(() -> "Border has different side sizes");
				});
			private static final Codec<Scaling.NineSlice.Border> DIFFERENT_SIDE_SIZES_CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
							Codecs.NONNEGATIVE_INT.fieldOf("left").forGetter(Scaling.NineSlice.Border::left),
							Codecs.NONNEGATIVE_INT.fieldOf("top").forGetter(Scaling.NineSlice.Border::top),
							Codecs.NONNEGATIVE_INT.fieldOf("right").forGetter(Scaling.NineSlice.Border::right),
							Codecs.NONNEGATIVE_INT.fieldOf("bottom").forGetter(Scaling.NineSlice.Border::bottom)
						)
						.apply(instance, Scaling.NineSlice.Border::new)
			);
			static final Codec<Scaling.NineSlice.Border> CODEC = Codec.either(UNIFORM_SIDE_SIZES_CODEC, DIFFERENT_SIDE_SIZES_CODEC)
				.xmap(Either::unwrap, border -> border.getUniformSideSize().isPresent() ? Either.left(border) : Either.right(border));

			private OptionalInt getUniformSideSize() {
				return this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom() ? OptionalInt.of(this.left()) : OptionalInt.empty();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Stretch() implements Scaling {
		public static final MapCodec<Scaling.Stretch> CODEC = MapCodec.unit(Scaling.Stretch::new);

		@Override
		public Scaling.Type getType() {
			return Scaling.Type.STRETCH;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Tile(int width, int height) implements Scaling {
		public static final MapCodec<Scaling.Tile> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codecs.POSITIVE_INT.fieldOf("width").forGetter(Scaling.Tile::width), Codecs.POSITIVE_INT.fieldOf("height").forGetter(Scaling.Tile::height)
					)
					.apply(instance, Scaling.Tile::new)
		);

		@Override
		public Scaling.Type getType() {
			return Scaling.Type.TILE;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type implements StringIdentifiable {
		STRETCH("stretch", Scaling.Stretch.CODEC),
		TILE("tile", Scaling.Tile.CODEC),
		NINE_SLICE("nine_slice", Scaling.NineSlice.CODEC);

		public static final Codec<Scaling.Type> CODEC = StringIdentifiable.createCodec(Scaling.Type::values);
		private final String name;
		private final MapCodec<? extends Scaling> codec;

		private Type(final String name, final MapCodec<? extends Scaling> codec) {
			this.name = name;
			this.codec = codec;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public MapCodec<? extends Scaling> getCodec() {
			return this.codec;
		}
	}
}
