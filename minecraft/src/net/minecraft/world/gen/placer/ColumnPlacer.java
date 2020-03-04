package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ColumnPlacer extends BlockPlacer {
	private final int minSize;
	private final int extraSize;

	public ColumnPlacer(int minSize, int extraSize) {
		super(BlockPlacerType.COLUMN_PLACER);
		this.minSize = minSize;
		this.extraSize = extraSize;
	}

	public <T> ColumnPlacer(Dynamic<T> dynamic) {
		this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);

		for (int j = 0; j < i; j++) {
			iWorld.setBlockState(mutable, blockState, 2);
			mutable.setOffset(Direction.UP);
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
				ops,
				ops.createMap(
					ImmutableMap.of(
						ops.createString("type"),
						ops.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString()),
						ops.createString("min_size"),
						ops.createInt(this.minSize),
						ops.createString("extra_size"),
						ops.createInt(this.extraSize)
					)
				)
			)
			.getValue();
	}
}
