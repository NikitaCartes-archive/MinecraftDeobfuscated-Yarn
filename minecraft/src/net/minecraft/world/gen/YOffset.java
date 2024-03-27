package net.minecraft.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.world.dimension.DimensionType;

public interface YOffset {
	Codec<YOffset> OFFSET_CODEC = Codec.xor(YOffset.Fixed.CODEC, Codec.xor(YOffset.AboveBottom.CODEC, YOffset.BelowTop.CODEC))
		.xmap(YOffset::fromEither, YOffset::map);
	YOffset BOTTOM = aboveBottom(0);
	YOffset TOP = belowTop(0);

	static YOffset fixed(int offset) {
		return new YOffset.Fixed(offset);
	}

	static YOffset aboveBottom(int offset) {
		return new YOffset.AboveBottom(offset);
	}

	static YOffset belowTop(int offset) {
		return new YOffset.BelowTop(offset);
	}

	static YOffset getBottom() {
		return BOTTOM;
	}

	static YOffset getTop() {
		return TOP;
	}

	private static YOffset fromEither(Either<YOffset.Fixed, Either<YOffset.AboveBottom, YOffset.BelowTop>> either) {
		return either.map(Function.identity(), Either::unwrap);
	}

	private static Either<YOffset.Fixed, Either<YOffset.AboveBottom, YOffset.BelowTop>> map(YOffset yOffset) {
		return yOffset instanceof YOffset.Fixed
			? Either.left((YOffset.Fixed)yOffset)
			: Either.right(yOffset instanceof YOffset.AboveBottom ? Either.left((YOffset.AboveBottom)yOffset) : Either.right((YOffset.BelowTop)yOffset));
	}

	int getY(HeightContext context);

	public static record AboveBottom(int offset) implements YOffset {
		public static final Codec<YOffset.AboveBottom> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("above_bottom")
			.<YOffset.AboveBottom>xmap(YOffset.AboveBottom::new, YOffset.AboveBottom::offset)
			.codec();

		@Override
		public int getY(HeightContext context) {
			return context.getMinY() + this.offset;
		}

		public String toString() {
			return this.offset + " above bottom";
		}
	}

	public static record BelowTop(int offset) implements YOffset {
		public static final Codec<YOffset.BelowTop> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("below_top")
			.<YOffset.BelowTop>xmap(YOffset.BelowTop::new, YOffset.BelowTop::offset)
			.codec();

		@Override
		public int getY(HeightContext context) {
			return context.getHeight() - 1 + context.getMinY() - this.offset;
		}

		public String toString() {
			return this.offset + " below top";
		}
	}

	public static record Fixed(int y) implements YOffset {
		public static final Codec<YOffset.Fixed> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("absolute")
			.<YOffset.Fixed>xmap(YOffset.Fixed::new, YOffset.Fixed::y)
			.codec();

		@Override
		public int getY(HeightContext context) {
			return this.y;
		}

		public String toString() {
			return this.y + " absolute";
		}
	}
}
