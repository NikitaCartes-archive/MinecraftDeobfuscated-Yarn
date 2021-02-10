/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VinesFeature
extends Feature<DefaultFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public VinesFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getPos();
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        block0: for (int i = 64; i < 384; ++i) {
            mutable.set(blockPos);
            mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            mutable.setY(i);
            if (!structureWorldAccess.isAir(mutable)) continue;
            for (Direction direction : DIRECTIONS) {
                if (direction == Direction.DOWN || !VineBlock.shouldConnectTo(structureWorldAccess, mutable, direction)) continue;
                structureWorldAccess.setBlockState(mutable, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true), 2);
                continue block0;
            }
        }
        return true;
    }
}

