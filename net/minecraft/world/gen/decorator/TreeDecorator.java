/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.decorator.TreeDecoratorType;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator {
    public static final Codec<TreeDecorator> field_24962 = Registry.TREE_DECORATOR_TYPE.dispatch(TreeDecorator::method_28893, TreeDecoratorType::method_28894);

    protected abstract TreeDecoratorType<?> method_28893();

    public abstract void generate(WorldAccess var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BlockBox var6);

    protected void placeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty, Set<BlockPos> set, BlockBox box) {
        this.setBlockStateAndEncompassPosition(world, pos, (BlockState)Blocks.VINE.getDefaultState().with(directionProperty, true), set, box);
    }

    protected void setBlockStateAndEncompassPosition(ModifiableWorld world, BlockPos pos, BlockState state, Set<BlockPos> set, BlockBox box) {
        world.setBlockState(pos, state, 19);
        set.add(pos);
        box.encompass(new BlockBox(pos, pos));
    }
}

