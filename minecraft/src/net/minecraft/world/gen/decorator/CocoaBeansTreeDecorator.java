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
import net.minecraft.world.gen.feature.AbstractTreeFeature;

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
	public void method_23469(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		if (!(random.nextFloat() >= this.field_21318)) {
			int i = ((BlockPos)list.get(0)).getY();
			list.stream().filter(blockPos -> blockPos.getY() - i <= 2).forEach(blockPos -> {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (random.nextFloat() <= 0.25F) {
						Direction direction2 = direction.getOpposite();
						BlockPos blockPos2 = blockPos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
						if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
							BlockState blockState = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(random.nextInt(3))).with(CocoaBlock.FACING, direction);
							this.method_23470(iWorld, blockPos2, blockState, set, blockBox);
						}
					}
				}
			});
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.field_21319).toString()),
						dynamicOps.createString("probability"),
						dynamicOps.createFloat(this.field_21318)
					)
				)
			)
			.getValue();
	}
}
