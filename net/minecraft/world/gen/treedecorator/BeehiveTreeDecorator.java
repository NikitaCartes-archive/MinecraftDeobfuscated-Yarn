/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class BeehiveTreeDecorator
extends TreeDecorator {
    public static final Codec<BeehiveTreeDecorator> CODEC = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(BeehiveTreeDecorator::new, decorator -> Float.valueOf(decorator.probability)).codec();
    private final float probability;

    public BeehiveTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.BEEHIVE;
    }

    @Override
    public void generate(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list) {
        if (random.nextFloat() >= this.probability) {
            return;
        }
        Direction direction = BeehiveBlock.getRandomGenerationDirection(random);
        int i = !list.isEmpty() ? Math.max(list.get(0).getY() - 1, leavesPositions.get(0).getY()) : Math.min(leavesPositions.get(0).getY() + 1 + random.nextInt(3), leavesPositions.get(leavesPositions.size() - 1).getY());
        List list2 = leavesPositions.stream().filter(pos -> pos.getY() == i).collect(Collectors.toList());
        if (list2.isEmpty()) {
            return;
        }
        BlockPos blockPos = (BlockPos)list2.get(random.nextInt(list2.size()));
        BlockPos blockPos2 = blockPos.offset(direction);
        if (!Feature.isAir(testableWorld, blockPos2) || !Feature.isAir(testableWorld, blockPos2.offset(Direction.SOUTH))) {
            return;
        }
        biConsumer.accept(blockPos2, (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH));
        testableWorld.getBlockEntity(blockPos2, BlockEntityType.BEEHIVE).ifPresent(beehiveBlockEntity -> {
            int i = 2 + random.nextInt(2);
            for (int j = 0; j < i; ++j) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(EntityType.BEE).toString());
                beehiveBlockEntity.addBee(nbtCompound, random.nextInt(599), false);
            }
        });
    }
}

