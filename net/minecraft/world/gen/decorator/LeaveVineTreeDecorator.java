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
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator
extends TreeDecorator {
    public LeaveVineTreeDecorator() {
        super(TreeDecoratorType.LEAVE_VINE);
    }

    public <T> LeaveVineTreeDecorator(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        leavesPositions.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(4) == 0 && Feature.method_27370(world, blockPos2 = blockPos.west())) {
                this.method_23467(world, blockPos2, VineBlock.EAST, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(world, blockPos2 = blockPos.east())) {
                this.method_23467(world, blockPos2, VineBlock.WEST, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(world, blockPos2 = blockPos.north())) {
                this.method_23467(world, blockPos2, VineBlock.SOUTH, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(world, blockPos2 = blockPos.south())) {
                this.method_23467(world, blockPos2, VineBlock.NORTH, set, box);
            }
        });
    }

    private void method_23467(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty, Set<BlockPos> set, BlockBox blockBox) {
        this.placeVine(modifiableTestableWorld, blockPos, booleanProperty, set, blockBox);
        blockPos = blockPos.down();
        for (int i = 4; Feature.method_27370(modifiableTestableWorld, blockPos) && i > 0; --i) {
            this.placeVine(modifiableTestableWorld, blockPos, booleanProperty, set, blockBox);
            blockPos = blockPos.down();
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString())))).getValue();
    }
}

