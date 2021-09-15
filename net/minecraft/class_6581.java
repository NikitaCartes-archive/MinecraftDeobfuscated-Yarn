/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_6579;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_6581
extends class_6579 {
    public static final Codec<class_6581> field_34713 = RecordCodecBuilder.create(instance -> class_6581.method_38439(instance).and(instance.group(((MapCodec)Codec.floatRange(-1.0f, 1.0f).fieldOf("threshold")).forGetter(arg -> Float.valueOf(arg.field_34714)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("high_chance")).forGetter(arg -> Float.valueOf(arg.field_34715)), ((MapCodec)BlockState.CODEC.fieldOf("default_state")).forGetter(arg -> arg.field_34716), ((MapCodec)Codec.list(BlockState.CODEC).fieldOf("low_states")).forGetter(arg -> arg.field_34717), ((MapCodec)Codec.list(BlockState.CODEC).fieldOf("high_states")).forGetter(arg -> arg.field_34718))).apply((Applicative<class_6581, ?>)instance, class_6581::new));
    private final float field_34714;
    private final float field_34715;
    private final BlockState field_34716;
    private final List<BlockState> field_34717;
    private final List<BlockState> field_34718;

    public class_6581(long l, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float f, float g, float h, BlockState blockState, List<BlockState> list, List<BlockState> list2) {
        super(l, noiseParameters, f);
        this.field_34714 = g;
        this.field_34715 = h;
        this.field_34716 = blockState;
        this.field_34717 = list;
        this.field_34718 = list2;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.NOISE_2D_CUTOFF_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        double d = this.method_38441(pos, this.field_34709);
        if (d < (double)this.field_34714) {
            return Util.getRandom(this.field_34717, random);
        }
        if (random.nextFloat() < this.field_34715) {
            return Util.getRandom(this.field_34718, random);
        }
        return this.field_34716;
    }
}

