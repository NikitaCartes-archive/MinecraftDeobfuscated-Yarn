package net.minecraft.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.class_5868;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.dimension.DimensionType;

public abstract class YOffset {
	public static final Codec<YOffset> OFFSET_CODEC = Codecs.xor(YOffset.Fixed.CODEC, Codecs.xor(YOffset.AboveBottom.CODEC, YOffset.BelowTop.CODEC))
		.xmap(YOffset::fromEither, YOffset::map);
	private static final YOffset BOTTOM = aboveBottom(0);
	private static final YOffset TOP = belowTop(0);
	private final int offset;

	protected YOffset(int offset) {
		this.offset = offset;
	}

	public static YOffset fixed(int offset) {
		return new YOffset.Fixed(offset);
	}

	public static YOffset aboveBottom(int offset) {
		return new YOffset.AboveBottom(offset);
	}

	public static YOffset belowTop(int offset) {
		return new YOffset.BelowTop(offset);
	}

	public static YOffset getBottom() {
		return BOTTOM;
	}

	public static YOffset getTop() {
		return TOP;
	}

	private static YOffset fromEither(Either<YOffset.Fixed, Either<YOffset.AboveBottom, YOffset.BelowTop>> either) {
		return either.map(Function.identity(), eitherx -> eitherx.map(Function.identity(), Function.identity()));
	}

	private static Either<YOffset.Fixed, Either<YOffset.AboveBottom, YOffset.BelowTop>> map(YOffset yOffset) {
		return yOffset instanceof YOffset.Fixed
			? Either.left((YOffset.Fixed)yOffset)
			: Either.right(yOffset instanceof YOffset.AboveBottom ? Either.left((YOffset.AboveBottom)yOffset) : Either.right((YOffset.BelowTop)yOffset));
	}

	protected int getOffset() {
		return this.offset;
	}

	public abstract int getY(class_5868 arg);

	static final class AboveBottom extends YOffset {
		public static final Codec<YOffset.AboveBottom> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("above_bottom")
			.<YOffset.AboveBottom>xmap(YOffset.AboveBottom::new, YOffset::getOffset)
			.codec();

		protected AboveBottom(int i) {
			super(i);
		}

		@Override
		public int getY(class_5868 arg) {
			return arg.getMinY() + this.getOffset();
		}
	}

	static final class BelowTop extends YOffset {
		public static final Codec<YOffset.BelowTop> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("below_top")
			.<YOffset.BelowTop>xmap(YOffset.BelowTop::new, YOffset::getOffset)
			.codec();

		protected BelowTop(int i) {
			super(i);
		}

		@Override
		public int getY(class_5868 arg) {
			return arg.getMaxY() - 1 + arg.getMinY() - this.getOffset();
		}
	}

	static final class Fixed extends YOffset {
		public static final Codec<YOffset.Fixed> CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT)
			.fieldOf("absolute")
			.<YOffset.Fixed>xmap(YOffset.Fixed::new, YOffset::getOffset)
			.codec();

		protected Fixed(int i) {
			super(i);
		}

		@Override
		public int getY(class_5868 arg) {
			return this.getOffset();
		}
	}
}
