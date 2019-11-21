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
import net.minecraft.block.BeeHiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class BeehiveTreeDecorator
extends TreeDecorator {
    private final float field_21317;

    public BeehiveTreeDecorator(float f) {
        super(TreeDecoratorType.BEEHIVE);
        this.field_21317 = f;
    }

    public <T> BeehiveTreeDecorator(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0f));
    }

    @Override
    public void generate(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
        if (random.nextFloat() >= this.field_21317) {
            return;
        }
        Direction direction = BeeHiveBlock.field_20418[random.nextInt(BeeHiveBlock.field_20418.length)];
        int i = !list2.isEmpty() ? Math.max(list2.get(0).getY() - 1, list.get(0).getY()) : Math.min(list.get(0).getY() + 1 + random.nextInt(3), list.get(list.size() - 1).getY());
        List list3 = list.stream().filter(blockPos -> blockPos.getY() == i).collect(Collectors.toList());
        BlockPos blockPos2 = (BlockPos)list3.get(random.nextInt(list3.size()));
        BlockPos blockPos22 = blockPos2.offset(direction);
        if (!AbstractTreeFeature.isAir(iWorld, blockPos22) || !AbstractTreeFeature.isAir(iWorld, blockPos22.offset(Direction.SOUTH))) {
            return;
        }
        BlockState blockState = (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeeHiveBlock.FACING, Direction.SOUTH);
        this.method_23470(iWorld, blockPos22, blockState, set, blockBox);
        BlockEntity blockEntity = iWorld.getBlockEntity(blockPos22);
        if (blockEntity instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            int j = 2 + random.nextInt(2);
            for (int k = 0; k < j; ++k) {
                BeeEntity beeEntity = new BeeEntity((EntityType<? extends BeeEntity>)EntityType.BEE, iWorld.getWorld());
                beeHiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
            }
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.field_21319).toString()), dynamicOps.createString("probability"), dynamicOps.createFloat(this.field_21317)))).getValue();
    }
}

