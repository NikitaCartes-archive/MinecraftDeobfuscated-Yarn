/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.HeightContext;

public abstract class YOffset {
    public static final Codec<YOffset> OFFSET_CODEC = Codecs.xor(Fixed.CODEC, Codecs.xor(AboveBottom.CODEC, BelowTop.CODEC)).xmap(YOffset::fromEither, YOffset::map);
    private static final YOffset BOTTOM = YOffset.aboveBottom(0);
    private static final YOffset TOP = YOffset.belowTop(0);
    private final int offset;

    protected YOffset(int offset) {
        this.offset = offset;
    }

    public static YOffset fixed(int offset) {
        return new Fixed(offset);
    }

    public static YOffset aboveBottom(int offset) {
        return new AboveBottom(offset);
    }

    public static YOffset belowTop(int offset) {
        return new BelowTop(offset);
    }

    public static YOffset getBottom() {
        return BOTTOM;
    }

    public static YOffset getTop() {
        return TOP;
    }

    private static YOffset fromEither(Either<Fixed, Either<AboveBottom, BelowTop>> either2) {
        return either2.map(Function.identity(), either -> (YOffset)either.map(Function.identity(), Function.identity()));
    }

    private static Either<Fixed, Either<AboveBottom, BelowTop>> map(YOffset yOffset) {
        if (yOffset instanceof Fixed) {
            return Either.left((Fixed)yOffset);
        }
        return Either.right(yOffset instanceof AboveBottom ? Either.left((AboveBottom)yOffset) : Either.right((BelowTop)yOffset));
    }

    protected int getOffset() {
        return this.offset;
    }

    public abstract int getY(HeightContext var1);

    static final class Fixed
    extends YOffset {
        public static final Codec<Fixed> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("absolute")).xmap(Fixed::new, YOffset::getOffset).codec();

        protected Fixed(int i) {
            super(i);
        }

        @Override
        public int getY(HeightContext context) {
            return this.getOffset();
        }

        public String toString() {
            return this.getOffset() + " absolute";
        }
    }

    static final class AboveBottom
    extends YOffset {
        public static final Codec<AboveBottom> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("above_bottom")).xmap(AboveBottom::new, YOffset::getOffset).codec();

        protected AboveBottom(int i) {
            super(i);
        }

        @Override
        public int getY(HeightContext context) {
            return context.getMinY() + this.getOffset();
        }

        public String toString() {
            return this.getOffset() + " above bottom";
        }
    }

    static final class BelowTop
    extends YOffset {
        public static final Codec<BelowTop> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("below_top")).xmap(BelowTop::new, YOffset::getOffset).codec();

        protected BelowTop(int i) {
            super(i);
        }

        @Override
        public int getY(HeightContext context) {
            return context.getMaxY() - 1 + context.getMinY() - this.getOffset();
        }

        public String toString() {
            return this.getOffset() + " below top";
        }
    }
}

