/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class FireDecorator
extends SimpleDecorator<CountConfig> {
    public FireDecorator(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountConfig countConfig, BlockPos blockPos) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        for (int i = 0; i < random.nextInt(random.nextInt(countConfig.method_30396().getValue(random)) + 1) + 1; ++i) {
            int j = random.nextInt(16) + blockPos.getX();
            int k = random.nextInt(16) + blockPos.getZ();
            int l = random.nextInt(120) + 4;
            list.add(new BlockPos(j, l, k));
        }
        return list.stream();
    }
}

