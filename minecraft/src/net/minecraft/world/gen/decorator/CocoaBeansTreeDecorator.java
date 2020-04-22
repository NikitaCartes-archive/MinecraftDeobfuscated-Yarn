package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator extends TreeDecorator {
	private final float field_21318;

	public CocoaBeansTreeDecorator(float f) {
		super(TreeDecoratorType.COCOA);
		this.field_21318 = f;
	}

	public <T> CocoaBeansTreeDecorator(Dynamic<T> dynamic) {
		this(dynamic.get("probability").asFloat(0.0F));
	}

	@Override
	public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		if (!(random.nextFloat() >= this.field_21318)) {
			int i = ((BlockPos)logPositions.get(0)).getY();
			logPositions.stream().filter(blockPos -> blockPos.getY() - i <= 2).forEach(blockPos -> {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (random.nextFloat() <= 0.25F) {
						Direction direction2 = direction.getOpposite();
						BlockPos blockPos2 = blockPos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
						if (Feature.method_27370(world, blockPos2)) {
							BlockState blockState = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(random.nextInt(3))).with(CocoaBlock.FACING, direction);
							this.setBlockStateAndEncompassPosition(world, blockPos2, blockState, set, box);
						}
					}
				}
			});
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
				ops,
				ops.createMap(
					ImmutableMap.of(
						ops.createString("type"),
						ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()),
						ops.createString("probability"),
						ops.createFloat(this.field_21318)
					)
				)
			)
			.getValue();
	}
}
