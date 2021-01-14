package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.StructureWorldAccess;
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
		StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box
	) {
		if (!(random.nextFloat() >= this.probability)) {
			Direction direction = BeehiveBlock.getRandomGenerationDirection(random);
			int i = !leavesPositions.isEmpty()
				? Math.max(((BlockPos)leavesPositions.get(0)).getY() - 1, ((BlockPos)logPositions.get(0)).getY())
				: Math.min(((BlockPos)logPositions.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)logPositions.get(logPositions.size() - 1)).getY());
			List<BlockPos> list = (List<BlockPos>)logPositions.stream().filter(pos -> pos.getY() == i).collect(Collectors.toList());
			if (!list.isEmpty()) {
				BlockPos blockPos = (BlockPos)list.get(random.nextInt(list.size()));
				BlockPos blockPos2 = blockPos.offset(direction);
				if (Feature.isAir(world, blockPos2) && Feature.isAir(world, blockPos2.offset(Direction.SOUTH))) {
					BlockState blockState = Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
					this.setBlockStateAndEncompassPosition(world, blockPos2, blockState, placedStates, box);
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof BeehiveBlockEntity) {
						BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
						int j = 2 + random.nextInt(2);

						for (int k = 0; k < j; k++) {
							BeeEntity beeEntity = new BeeEntity(EntityType.BEE, world.toServerWorld());
							beehiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
						}
					}
				}
			}
		}
	}
}
