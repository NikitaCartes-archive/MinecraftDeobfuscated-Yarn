/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class AlterGroundTreeDecorator
extends TreeDecorator {
    public static final Codec<AlterGroundTreeDecorator> CODEC = ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("provider")).xmap(AlterGroundTreeDecorator::new, decorator -> decorator.provider).codec();
    private final BlockStateProvider provider;

    public AlterGroundTreeDecorator(BlockStateProvider provider) {
        this.provider = provider;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.ALTER_GROUND;
    }

    @Override
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, List<BlockPos> rootPositions) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        if (rootPositions.isEmpty()) {
            list.addAll(logPositions);
        } else if (!logPositions.isEmpty() && rootPositions.get(0).getY() == logPositions.get(0).getY()) {
            list.addAll(logPositions);
            list.addAll(rootPositions);
        } else {
            list.addAll(rootPositions);
        }
        if (list.isEmpty()) {
            return;
        }
        int i = ((BlockPos)list.get(0)).getY();
        list.stream().filter(pos -> pos.getY() == i).forEach(pos -> {
            this.setArea(world, replacer, random, pos.west().north());
            this.setArea(world, replacer, random, pos.east(2).north());
            this.setArea(world, replacer, random, pos.west().south(2));
            this.setArea(world, replacer, random, pos.east(2).south(2));
            for (int i = 0; i < 5; ++i) {
                int j = random.nextInt(64);
                int k = j % 8;
                int l = j / 8;
                if (k != 0 && k != 7 && l != 0 && l != 7) continue;
                this.setArea(world, replacer, random, pos.add(-3 + k, 0, -3 + l));
            }
        });
    }

    private void setArea(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) == 2 && Math.abs(j) == 2) continue;
                this.setColumn(world, replacer, random, pos.add(i, 0, j));
            }
        }
    }

    private void setColumn(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos) {
        for (int i = 2; i >= -3; --i) {
            BlockPos blockPos = pos.up(i);
            if (Feature.isSoil(world, blockPos)) {
                replacer.accept(blockPos, this.provider.getBlockState(random, pos));
                break;
            }
            if (!Feature.isAir(world, blockPos) && i < 0) break;
        }
    }
}

