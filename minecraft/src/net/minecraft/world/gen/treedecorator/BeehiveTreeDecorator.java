package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	private static final Direction field_36346 = Direction.SOUTH;
	private static final Direction[] field_36347 = (Direction[])Direction.Type.HORIZONTAL
		.stream()
		.filter(direction -> direction != field_36346.getOpposite())
		.toArray(Direction[]::new);
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
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions
	) {
		if (!(random.nextFloat() >= this.probability)) {
			int i = !leavesPositions.isEmpty()
				? Math.max(((BlockPos)leavesPositions.get(0)).getY() - 1, ((BlockPos)logPositions.get(0)).getY() + 1)
				: Math.min(((BlockPos)logPositions.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)logPositions.get(logPositions.size() - 1)).getY());
			List<BlockPos> list = (List<BlockPos>)logPositions.stream()
				.filter(pos -> pos.getY() == i)
				.flatMap(blockPos -> Stream.of(field_36347).map(blockPos::offset))
				.collect(Collectors.toList());
			if (!list.isEmpty()) {
				Collections.shuffle(list);
				Optional<BlockPos> optional = list.stream()
					.filter(blockPos -> Feature.isAir(world, blockPos) && Feature.isAir(world, blockPos.offset(field_36346)))
					.findFirst();
				if (!optional.isEmpty()) {
					replacer.accept((BlockPos)optional.get(), Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, field_36346));
					world.getBlockEntity((BlockPos)optional.get(), BlockEntityType.BEEHIVE).ifPresent(blockEntity -> {
						int ix = 2 + random.nextInt(2);

						for (int j = 0; j < ix; j++) {
							NbtCompound nbtCompound = new NbtCompound();
							nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(EntityType.BEE).toString());
							blockEntity.addBee(nbtCompound, random.nextInt(599), false);
						}
					});
				}
			}
		}
	}
}
