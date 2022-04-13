/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
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
    public void generate(TreeDecorator.Generator generator) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        ObjectArrayList<BlockPos> list2 = generator.getRootPositions();
        ObjectArrayList<BlockPos> list3 = generator.getLogPositions();
        if (list2.isEmpty()) {
            list.addAll(list3);
        } else if (!list3.isEmpty() && ((BlockPos)list2.get(0)).getY() == ((BlockPos)list3.get(0)).getY()) {
            list.addAll(list3);
            list.addAll(list2);
        } else {
            list.addAll(list2);
        }
        if (list.isEmpty()) {
            return;
        }
        int i = ((BlockPos)list.get(0)).getY();
        list.stream().filter(pos -> pos.getY() == i).forEach(pos -> {
            this.setArea(generator, pos.west().north());
            this.setArea(generator, pos.east(2).north());
            this.setArea(generator, pos.west().south(2));
            this.setArea(generator, pos.east(2).south(2));
            for (int i = 0; i < 5; ++i) {
                int j = generator.getRandom().nextInt(64);
                int k = j % 8;
                int l = j / 8;
                if (k != 0 && k != 7 && l != 0 && l != 7) continue;
                this.setArea(generator, pos.add(-3 + k, 0, -3 + l));
            }
        });
    }

    private void setArea(TreeDecorator.Generator generator, BlockPos origin) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) == 2 && Math.abs(j) == 2) continue;
                this.setColumn(generator, origin.add(i, 0, j));
            }
        }
    }

    private void setColumn(TreeDecorator.Generator generator, BlockPos origin) {
        for (int i = 2; i >= -3; --i) {
            BlockPos blockPos = origin.up(i);
            if (Feature.isSoil(generator.getWorld(), blockPos)) {
                generator.replace(blockPos, this.provider.getBlockState(generator.getRandom(), origin));
                break;
            }
            if (!generator.isAir(blockPos) && i < 0) break;
        }
    }
}

