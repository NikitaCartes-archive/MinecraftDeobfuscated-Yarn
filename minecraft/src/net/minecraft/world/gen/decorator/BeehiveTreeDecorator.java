package net.minecraft.world.gen.decorator;

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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;

public class BeehiveTreeDecorator extends TreeDecorator {
	public static final Codec<BeehiveTreeDecorator> field_24958 = Codec.FLOAT
		.fieldOf("probability")
		.<BeehiveTreeDecorator>xmap(BeehiveTreeDecorator::new, beehiveTreeDecorator -> beehiveTreeDecorator.chance)
		.codec();
	private final float chance;

	public BeehiveTreeDecorator(float f) {
		this.chance = f;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.BEEHIVE;
	}

	@Override
	public void generate(WorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		if (!(random.nextFloat() >= this.chance)) {
			Direction direction = BeehiveBlock.getRandomGenerationDirection(random);
			int i = !leavesPositions.isEmpty()
				? Math.max(((BlockPos)leavesPositions.get(0)).getY() - 1, ((BlockPos)logPositions.get(0)).getY())
				: Math.min(((BlockPos)logPositions.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)logPositions.get(logPositions.size() - 1)).getY());
			List<BlockPos> list = (List<BlockPos>)logPositions.stream().filter(blockPosx -> blockPosx.getY() == i).collect(Collectors.toList());
			if (!list.isEmpty()) {
				BlockPos blockPos = (BlockPos)list.get(random.nextInt(list.size()));
				BlockPos blockPos2 = blockPos.offset(direction);
				if (Feature.isAir(world, blockPos2) && Feature.isAir(world, blockPos2.offset(Direction.SOUTH))) {
					BlockState blockState = Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
					this.setBlockStateAndEncompassPosition(world, blockPos2, blockState, set, box);
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof BeehiveBlockEntity) {
						BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
						int j = 2 + random.nextInt(2);

						for (int k = 0; k < j; k++) {
							BeeEntity beeEntity = new BeeEntity(EntityType.BEE, world.getWorld());
							beehiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
						}
					}
				}
			}
		}
	}
}
