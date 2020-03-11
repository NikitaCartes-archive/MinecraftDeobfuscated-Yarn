/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.decorator.TreeDecoratorType;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator
implements DynamicSerializable {
    protected final TreeDecoratorType<?> type;

    protected TreeDecorator(TreeDecoratorType<?> type) {
        this.type = type;
    }

    public abstract void generate(IWorld var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BlockBox var6);

    protected void placeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty, Set<BlockPos> set, BlockBox box) {
        this.setBlockStateAndEncompassPosition(world, pos, (BlockState)Blocks.VINE.getDefaultState().with(directionProperty, true), set, box);
    }

    protected void setBlockStateAndEncompassPosition(ModifiableWorld world, BlockPos pos, BlockState state, Set<BlockPos> set, BlockBox box) {
        world.setBlockState(pos, state, 19);
        set.add(pos);
        box.encompass(new BlockBox(pos, pos));
    }
}

