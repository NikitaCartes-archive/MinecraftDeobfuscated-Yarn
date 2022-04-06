/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.slf4j.Logger;

public class TrapezoidHeightProvider
extends HeightProvider {
    public static final Codec<TrapezoidHeightProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("min_inclusive")).forGetter(provider -> provider.minOffset), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("max_inclusive")).forGetter(provider -> provider.maxOffset), Codec.INT.optionalFieldOf("plateau", 0).forGetter(trapezoidHeightProvider -> trapezoidHeightProvider.plateau)).apply((Applicative<TrapezoidHeightProvider, ?>)instance, TrapezoidHeightProvider::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final YOffset minOffset;
    private final YOffset maxOffset;
    private final int plateau;

    private TrapezoidHeightProvider(YOffset minOffset, YOffset maxOffset, int plateau) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.plateau = plateau;
    }

    /**
     * @param maxOffset the maximum offset, inclusive
     * @param minOffset the minimum offset, inclusive
     */
    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset, int plateau) {
        return new TrapezoidHeightProvider(minOffset, maxOffset, plateau);
    }

    /**
     * @param minOffset the minimum offset, inclusive
     * @param maxOffset the maximum offset, inclusive
     */
    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset) {
        return TrapezoidHeightProvider.create(minOffset, maxOffset, 0);
    }

    @Override
    public int get(AbstractRandom abstractRandom, HeightContext context) {
        int j;
        int i = this.minOffset.getY(context);
        if (i > (j = this.maxOffset.getY(context))) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return i;
        }
        int k = j - i;
        if (this.plateau >= k) {
            return MathHelper.nextBetween(abstractRandom, i, j);
        }
        int l = (k - this.plateau) / 2;
        int m = k - l;
        return i + MathHelper.nextBetween(abstractRandom, 0, m) + MathHelper.nextBetween(abstractRandom, 0, l);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }

    public String toString() {
        if (this.plateau == 0) {
            return "triangle (" + this.minOffset + "-" + this.maxOffset + ")";
        }
        return "trapezoid(" + this.plateau + ") in [" + this.minOffset + "-" + this.maxOffset + "]";
    }
}

