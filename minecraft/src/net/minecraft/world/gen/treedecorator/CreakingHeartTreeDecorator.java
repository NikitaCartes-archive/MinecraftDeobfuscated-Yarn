package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class CreakingHeartTreeDecorator extends TreeDecorator {
	public static final MapCodec<CreakingHeartTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.xmap(CreakingHeartTreeDecorator::new, treeDecorator -> treeDecorator.probability);
	private final float probability;

	public CreakingHeartTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.CREAKING_HEART;
	}

	@Override
	public void generate(TreeDecorator.Generator generator) {
		Random random = generator.getRandom();
		List<BlockPos> list = generator.getLogPositions();
		if (!list.isEmpty()) {
			if (!(random.nextFloat() >= this.probability)) {
				List<BlockPos> list2 = new ArrayList(list);
				Util.shuffle(list2, random);
				Optional<BlockPos> optional = list2.stream().filter(pos -> {
					for (Direction direction : Direction.values()) {
						if (!generator.matches(pos.offset(direction), state -> state.isIn(BlockTags.LOGS))) {
							return false;
						}
					}

					return true;
				}).findFirst();
				if (!optional.isEmpty()) {
					generator.replace((BlockPos)optional.get(), Blocks.CREAKING_HEART.getDefaultState().with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.DORMANT));
				}
			}
		}
	}
}
