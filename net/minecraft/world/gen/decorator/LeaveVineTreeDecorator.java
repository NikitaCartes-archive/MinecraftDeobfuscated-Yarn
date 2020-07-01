/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator
extends TreeDecorator {
    public static final Codec<LeaveVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final LeaveVineTreeDecorator INSTANCE = new LeaveVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    @Override
    public void generate(ServerWorldAccess serverWorldAccess, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        leavesPositions.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(4) == 0 && Feature.isAir(serverWorldAccess, blockPos2 = blockPos.west())) {
                this.method_23467(serverWorldAccess, blockPos2, VineBlock.EAST, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(serverWorldAccess, blockPos2 = blockPos.east())) {
                this.method_23467(serverWorldAccess, blockPos2, VineBlock.WEST, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(serverWorldAccess, blockPos2 = blockPos.north())) {
                this.method_23467(serverWorldAccess, blockPos2, VineBlock.SOUTH, set, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(serverWorldAccess, blockPos2 = blockPos.south())) {
                this.method_23467(serverWorldAccess, blockPos2, VineBlock.NORTH, set, box);
            }
        });
    }

    private void method_23467(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty, Set<BlockPos> set, BlockBox blockBox) {
        this.placeVine(modifiableTestableWorld, blockPos, booleanProperty, set, blockBox);
        blockPos = blockPos.down();
        for (int i = 4; Feature.isAir(modifiableTestableWorld, blockPos) && i > 0; --i) {
            this.placeVine(modifiableTestableWorld, blockPos, booleanProperty, set, blockBox);
            blockPos = blockPos.down();
        }
    }
}

