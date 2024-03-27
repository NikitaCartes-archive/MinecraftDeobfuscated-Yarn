package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BeehiveTreeDecorator extends TreeDecorator {
	public static final MapCodec<BeehiveTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.xmap(BeehiveTreeDecorator::new, decorator -> decorator.probability);
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
	public void generate(TreeDecorator.Generator generator) {
		Random random = generator.getRandom();
		if (!(random.nextFloat() >= this.probability)) {
			List<BlockPos> list = generator.getLeavesPositions();
			List<BlockPos> list2 = generator.getLogPositions();
			int i = !list.isEmpty()
				? Math.max(((BlockPos)list.get(0)).getY() - 1, ((BlockPos)list2.get(0)).getY() + 1)
				: Math.min(((BlockPos)list2.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)list2.get(list2.size() - 1)).getY());
			List<BlockPos> list3 = (List<BlockPos>)list2.stream()
				.filter(pos -> pos.getY() == i)
				.flatMap(pos -> Stream.of(GENERATE_DIRECTIONS).map(pos::offset))
				.collect(Collectors.toList());
			if (!list3.isEmpty()) {
				Collections.shuffle(list3);
				Optional<BlockPos> optional = list3.stream().filter(pos -> generator.isAir(pos) && generator.isAir(pos.offset(BEE_NEST_FACE))).findFirst();
				if (!optional.isEmpty()) {
					generator.replace((BlockPos)optional.get(), Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, BEE_NEST_FACE));
					generator.getWorld().getBlockEntity((BlockPos)optional.get(), BlockEntityType.BEEHIVE).ifPresent(blockEntity -> {
						int ix = 2 + random.nextInt(2);

						for (int j = 0; j < ix; j++) {
							blockEntity.addBee(BeehiveBlockEntity.BeeData.create(random.nextInt(599)));
						}
					});
				}
			}
		}
	}
}
