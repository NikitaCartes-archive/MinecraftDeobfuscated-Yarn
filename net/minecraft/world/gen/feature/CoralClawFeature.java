/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralClawFeature
extends CoralFeature {
    public CoralClawFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected boolean spawnCoral(IWorld world, Random random, BlockPos pos, BlockState state) {
        if (!this.spawnCoralPiece(world, random, pos, state)) {
            return false;
        }
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        int i = random.nextInt(2) + 2;
        ArrayList<Direction> list = Lists.newArrayList(direction, direction.rotateYClockwise(), direction.rotateYCounterclockwise());
        Collections.shuffle(list, random);
        List list2 = list.subList(0, i);
        block0: for (Direction direction2 : list2) {
            int l;
            int k;
            Direction direction3;
            BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
            int j = random.nextInt(2) + 1;
            mutable.setOffset(direction2);
            if (direction2 == direction) {
                direction3 = direction;
                k = random.nextInt(3) + 2;
            } else {
                mutable.setOffset(Direction.UP);
                Direction[] directions = new Direction[]{direction2, Direction.UP};
                direction3 = directions[random.nextInt(directions.length)];
                k = random.nextInt(3) + 3;
            }
            for (l = 0; l < j && this.spawnCoralPiece(world, random, mutable, state); ++l) {
                mutable.setOffset(direction3);
            }
            mutable.setOffset(direction3.getOpposite());
            mutable.setOffset(Direction.UP);
            for (l = 0; l < k; ++l) {
                mutable.setOffset(direction);
                if (!this.spawnCoralPiece(world, random, mutable, state)) continue block0;
                if (!(random.nextFloat() < 0.25f)) continue;
                mutable.setOffset(Direction.UP);
            }
        }
        return true;
    }
}

