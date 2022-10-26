/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class AttachedToLeavesTreeDecorator
extends TreeDecorator {
    public static final Codec<AttachedToLeavesTreeDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(treeDecorator -> Float.valueOf(treeDecorator.probability)), ((MapCodec)Codec.intRange(0, 16).fieldOf("exclusion_radius_xz")).forGetter(treeDecorator -> treeDecorator.exclusionRadiusXZ), ((MapCodec)Codec.intRange(0, 16).fieldOf("exclusion_radius_y")).forGetter(treeDecorator -> treeDecorator.exclusionRadiusY), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("block_provider")).forGetter(treeDecorator -> treeDecorator.blockProvider), ((MapCodec)Codec.intRange(1, 16).fieldOf("required_empty_blocks")).forGetter(treeDecorator -> treeDecorator.requiredEmptyBlocks), ((MapCodec)Codecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions")).forGetter(treeDecorator -> treeDecorator.directions)).apply((Applicative<AttachedToLeavesTreeDecorator, ?>)instance, AttachedToLeavesTreeDecorator::new));
    protected final float probability;
    protected final int exclusionRadiusXZ;
    protected final int exclusionRadiusY;
    protected final BlockStateProvider blockProvider;
    protected final int requiredEmptyBlocks;
    protected final List<Direction> directions;

    public AttachedToLeavesTreeDecorator(float probability, int exclusionRadiusXZ, int exclusionRadiusY, BlockStateProvider blockProvider, int requiredEmptyBlocks, List<Direction> directions) {
        this.probability = probability;
        this.exclusionRadiusXZ = exclusionRadiusXZ;
        this.exclusionRadiusY = exclusionRadiusY;
        this.blockProvider = blockProvider;
        this.requiredEmptyBlocks = requiredEmptyBlocks;
        this.directions = directions;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        Random random = generator.getRandom();
        for (BlockPos blockPos : Util.copyShuffled(generator.getLeavesPositions(), random)) {
            Direction direction;
            BlockPos blockPos2 = blockPos.offset(direction = Util.getRandom(this.directions, random));
            if (set.contains(blockPos2) || !(random.nextFloat() < this.probability) || !this.meetsRequiredEmptyBlocks(generator, blockPos, direction)) continue;
            BlockPos blockPos3 = blockPos2.add(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
            BlockPos blockPos4 = blockPos2.add(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
            for (BlockPos blockPos5 : BlockPos.iterate(blockPos3, blockPos4)) {
                set.add(blockPos5.toImmutable());
            }
            generator.replace(blockPos2, this.blockProvider.get(random, blockPos2));
        }
    }

    private boolean meetsRequiredEmptyBlocks(TreeDecorator.Generator generator, BlockPos pos, Direction direction) {
        for (int i = 1; i <= this.requiredEmptyBlocks; ++i) {
            BlockPos blockPos = pos.offset(direction, i);
            if (generator.isAir(blockPos)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.ATTACHED_TO_LEAVES;
    }
}

