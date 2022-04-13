/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralTreeFeature
extends CoralFeature {
    public CoralTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean generateCoral(WorldAccess world, AbstractRandom random, BlockPos pos, BlockState state) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        int i = random.nextInt(3) + 1;
        for (int j = 0; j < i; ++j) {
            if (!this.generateCoralPiece(world, random, mutable, state)) {
                return true;
            }
            mutable.move(Direction.UP);
        }
        BlockPos blockPos = mutable.toImmutable();
        int k = random.nextInt(3) + 2;
        List<Direction> list = Direction.Type.HORIZONTAL.getShuffled(random);
        List<Direction> list2 = list.subList(0, k);
        for (Direction direction : list2) {
            mutable.set(blockPos);
            mutable.move(direction);
            int l = random.nextInt(5) + 2;
            int m = 0;
            for (int n = 0; n < l && this.generateCoralPiece(world, random, mutable, state); ++n) {
                mutable.move(Direction.UP);
                if (n != 0 && (++m < 2 || !(random.nextFloat() < 0.25f))) continue;
                mutable.move(direction);
                m = 0;
            }
        }
        return true;
    }
}

