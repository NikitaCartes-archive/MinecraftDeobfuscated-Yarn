package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class BeehiveTreeDecorator extends TreeDecorator {
	public static final Codec<BeehiveTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.<BeehiveTreeDecorator>xmap(BeehiveTreeDecorator::new, decorator -> decorator.probability)
		.codec();
	private static final Direction BEE_NEST_FACE = Direction.SOUTH;
	private static final Direction[] GENERATE_DIRECTIONS = (Direction[])Direction.Type.HORIZONTAL
		.stream()
		.filter(direction -> direction != BEE_NEST_FACE.getOpposite())
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		AbstractRandom random,
		List<BlockPos> logPositions,
		List<BlockPos> leavesPositions,
		List<BlockPos> rootPositions
	) {
		if (!(random.nextFloat() >= this.probability)) {
			int i = !leavesPositions.isEmpty()
				? Math.max(((BlockPos)leavesPositions.get(0)).getY() - 1, ((BlockPos)logPositions.get(0)).getY() + 1)
				: Math.min(((BlockPos)logPositions.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)logPositions.get(logPositions.size() - 1)).getY());
			List<BlockPos> list = (List<BlockPos>)logPositions.stream()
				.filter(pos -> pos.getY() == i)
				.flatMap(pos -> Stream.of(GENERATE_DIRECTIONS).map(pos::offset))
				.collect(Collectors.toList());
			if (!list.isEmpty()) {
				Collections.shuffle(list);
				Optional<BlockPos> optional = list.stream().filter(pos -> Feature.isAir(world, pos) && Feature.isAir(world, pos.offset(BEE_NEST_FACE))).findFirst();
				if (!optional.isEmpty()) {
					replacer.accept((BlockPos)optional.get(), Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, BEE_NEST_FACE));
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
