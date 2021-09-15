/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_6579;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_6580
extends class_6579 {
    public static final Codec<class_6580> field_34711 = RecordCodecBuilder.create(instance -> class_6580.method_38447(instance).apply((Applicative)instance, class_6580::new));
    protected final List<BlockState> field_34712;

    protected static <P extends class_6580> Products.P4<RecordCodecBuilder.Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float, List<BlockState>> method_38447(RecordCodecBuilder.Instance<P> instance) {
        return class_6580.method_38439(instance).and(((MapCodec)Codec.list(BlockState.CODEC).fieldOf("states")).forGetter(arg -> arg.field_34712));
    }

    public class_6580(long l, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float f, List<BlockState> list) {
        super(l, noiseParameters, f);
        this.field_34712 = list;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.NOISE_2D_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        return this.method_38446(this.field_34712, pos, this.field_34709);
    }

    protected BlockState method_38446(List<BlockState> list, BlockPos blockPos, double d) {
        double e = this.method_38441(blockPos, d);
        return this.method_38445(list, e);
    }

    protected BlockState method_38445(List<BlockState> list, double d) {
        double e = MathHelper.clamp((1.0 + d) / 2.0, 0.0, 0.9999);
        return list.get((int)(e * (double)list.size()));
    }
}

