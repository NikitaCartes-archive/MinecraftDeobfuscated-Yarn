package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
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

public class BeehiveTreeDecorator extends TreeDecorator {
	public static final Codec<BeehiveTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.<BeehiveTreeDecorator>xmap(BeehiveTreeDecorator::new, decorator -> decorator.probability)
		.codec();
	private final float probability;

	public BeehiveTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.BEEHIVE;
	}

	@Override
	public void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	) {
		if (!(random.nextFloat() >= this.probability)) {
			Direction direction = BeehiveBlock.getRandomGenerationDirection(random);
			int i = !list.isEmpty()
				? Math.max(((BlockPos)list.get(0)).getY() - 1, ((BlockPos)leavesPositions.get(0)).getY())
				: Math.min(((BlockPos)leavesPositions.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)leavesPositions.get(leavesPositions.size() - 1)).getY());
			List<BlockPos> list2 = (List<BlockPos>)leavesPositions.stream().filter(pos -> pos.getY() == i).collect(Collectors.toList());
			if (!list2.isEmpty()) {
				BlockPos blockPos = (BlockPos)list2.get(random.nextInt(list2.size()));
				BlockPos blockPos2 = blockPos.offset(direction);
				if (Feature.isAir(testableWorld, blockPos2) && Feature.isAir(testableWorld, blockPos2.offset(Direction.SOUTH))) {
					biConsumer.accept(blockPos2, Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH));
					testableWorld.getBlockEntity(blockPos2, BlockEntityType.BEEHIVE).ifPresent(beehiveBlockEntity -> {
						int ix = 2 + random.nextInt(2);

						for (int j = 0; j < ix; j++) {
							NbtCompound nbtCompound = new NbtCompound();
							nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(EntityType.BEE).toString());
							beehiveBlockEntity.addBee(nbtCompound, random.nextInt(599), false);
						}
					});
				}
			}
		}
	}
}
