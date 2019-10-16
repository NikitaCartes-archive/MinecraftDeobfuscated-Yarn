package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.class_4629;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ColumnPlacer extends class_4629 {
	private final int minSize;
	private final int extraSize;

	public ColumnPlacer(int i, int j) {
		super(BlockPlacerType.COLUMN_PLACER);
		this.minSize = i;
		this.extraSize = j;
	}

	public <T> ColumnPlacer(Dynamic<T> dynamic) {
		this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);

		for (int j = 0; j < i; j++) {
			iWorld.setBlockState(mutable, blockState, 2);
			mutable.setOffset(Direction.UP);
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString(Registry.BLOCK_PLACER_TYPE.getId(this.field_21222).toString()),
						dynamicOps.createString("min_size"),
						dynamicOps.createInt(this.minSize),
						dynamicOps.createString("extra_size"),
						dynamicOps.createInt(this.extraSize)
					)
				)
			)
			.getValue();
	}
}
