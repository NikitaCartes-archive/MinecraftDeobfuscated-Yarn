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

public interface YOffset {
    public static final Codec<YOffset> OFFSET_CODEC = Codecs.xor(Fixed.CODEC, Codecs.xor(AboveBottom.CODEC, BelowTop.CODEC)).xmap(YOffset::fromEither, YOffset::map);
    public static final YOffset BOTTOM = YOffset.aboveBottom(0);
    public static final YOffset TOP = YOffset.belowTop(0);

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
        return (YOffset)((Object)either2.map(Function.identity(), either -> (Record)either.map(Function.identity(), Function.identity())));
    }

    private static Either<Fixed, Either<AboveBottom, BelowTop>> map(YOffset yOffset) {
        if (yOffset instanceof Fixed) {
            return Either.left((Fixed)yOffset);
        }
        return Either.right(yOffset instanceof AboveBottom ? Either.left((AboveBottom)yOffset) : Either.right((BelowTop)yOffset));
    }

    public int getY(HeightContext var1);

    public record Fixed(int y) implements YOffset
    {
        public static final Codec<Fixed> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("absolute")).xmap(Fixed::new, Fixed::y).codec();

        @Override
        public int getY(HeightContext context) {
            return this.y;
        }

        @Override
        public String toString() {
            return this.y + " absolute";
        }
    }

    public record AboveBottom(int offset) implements YOffset
    {
        public static final Codec<AboveBottom> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("above_bottom")).xmap(AboveBottom::new, AboveBottom::offset).codec();

        @Override
        public int getY(HeightContext context) {
            return context.getMinY() + this.offset;
        }

        @Override
        public String toString() {
            return this.offset + " above bottom";
        }
    }

    public record BelowTop(int offset) implements YOffset
    {
        public static final Codec<BelowTop> CODEC = ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("below_top")).xmap(BelowTop::new, BelowTop::offset).codec();

        @Override
        public int getY(HeightContext context) {
            return context.getHeight() - 1 + context.getMinY() - this.offset;
        }

        @Override
        public String toString() {
            return this.offset + " below top";
        }
    }
}

