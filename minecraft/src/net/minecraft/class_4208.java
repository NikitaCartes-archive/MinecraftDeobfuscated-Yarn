package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class class_4208 implements class_4213 {
	private final DimensionType field_18790;
	private final BlockPos field_18791;

	private class_4208(DimensionType dimensionType, BlockPos blockPos) {
		this.field_18790 = dimensionType;
		this.field_18791 = blockPos;
	}

	public static class_4208 method_19443(DimensionType dimensionType, BlockPos blockPos) {
		return new class_4208(dimensionType, blockPos);
	}

	public static class_4208 method_19444(Dynamic<?> dynamic) {
		return (class_4208)dynamic.get("dimension")
			.map(DimensionType::method_19298)
			.flatMap(dimensionType -> dynamic.get("pos").map(BlockPos::method_19438).map(blockPos -> new class_4208(dimensionType, blockPos)))
			.orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
	}

	public DimensionType method_19442() {
		return this.field_18790;
	}

	public BlockPos method_19446() {
		return this.field_18791;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4208 lv = (class_4208)object;
			return Objects.equals(this.field_18790, lv.field_18790) && Objects.equals(this.field_18791, lv.field_18791);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_18790, this.field_18791});
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("dimension"), this.field_18790.method_19508(dynamicOps), dynamicOps.createString("pos"), this.field_18791.method_19508(dynamicOps)
			)
		);
	}
}
