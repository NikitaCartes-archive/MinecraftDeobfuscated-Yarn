/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class BeehiveTreeDecorator
extends TreeDecorator {
    private final float chance;

    public BeehiveTreeDecorator(float chance) {
        super(TreeDecoratorType.BEEHIVE);
        this.chance = chance;
    }

    public <T> BeehiveTreeDecorator(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0f));
    }

    @Override
    public void generate(WorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.chance) {
            return;
        }
        Direction direction = BeehiveBlock.getRandomGenerationDirection(random);
        int i = !leavesPositions.isEmpty() ? Math.max(leavesPositions.get(0).getY() - 1, logPositions.get(0).getY()) : Math.min(logPositions.get(0).getY() + 1 + random.nextInt(3), logPositions.get(logPositions.size() - 1).getY());
        List list = logPositions.stream().filter(blockPos -> blockPos.getY() == i).collect(Collectors.toList());
        if (list.isEmpty()) {
            return;
        }
        BlockPos blockPos2 = (BlockPos)list.get(random.nextInt(list.size()));
        BlockPos blockPos22 = blockPos2.offset(direction);
        if (!Feature.method_27370(world, blockPos22) || !Feature.method_27370(world, blockPos22.offset(Direction.SOUTH))) {
            return;
        }
        BlockState blockState = (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
        this.setBlockStateAndEncompassPosition(world, blockPos22, blockState, set, box);
        BlockEntity blockEntity = world.getBlockEntity(blockPos22);
        if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            int j = 2 + random.nextInt(2);
            for (int k = 0; k < j; ++k) {
                BeeEntity beeEntity = new BeeEntity((EntityType<? extends BeeEntity>)EntityType.BEE, world.getWorld());
                beehiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
            }
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()), ops.createString("probability"), ops.createFloat(this.chance)))).getValue();
    }
}

