package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class class_4631 extends class_4629 {
	private final int field_21227;
	private final int field_21228;

	public class_4631(int i, int j) {
		super(class_4630.COLUMN_PLACER);
		this.field_21227 = i;
		this.field_21228 = j;
	}

	public <T> class_4631(Dynamic<T> dynamic) {
		this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int i = this.field_21227 + random.nextInt(random.nextInt(this.field_21228 + 1) + 1);

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
						dynamicOps.createInt(this.field_21227),
						dynamicOps.createString("extra_size"),
						dynamicOps.createInt(this.field_21228)
					)
				)
			)
			.getValue();
	}
}
