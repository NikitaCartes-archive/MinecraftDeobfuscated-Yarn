package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos implements DynamicSerializable {
	private final DimensionType dimension;
	private final BlockPos pos;

	private GlobalPos(DimensionType dimensionType, BlockPos blockPos) {
		this.dimension = dimensionType;
		this.pos = blockPos;
	}

	public static GlobalPos create(DimensionType dimensionType, BlockPos blockPos) {
		return new GlobalPos(dimensionType, blockPos);
	}

	public static GlobalPos deserialize(Dynamic<?> dynamic) {
		return (GlobalPos)dynamic.get("dimension")
			.map(DimensionType::deserialize)
			.flatMap(dimensionType -> dynamic.get("pos").map(BlockPos::deserialize).map(blockPos -> new GlobalPos(dimensionType, blockPos)))
			.orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
	}

	public DimensionType getDimension() {
		return this.dimension;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			GlobalPos globalPos = (GlobalPos)o;
			return Objects.equals(this.dimension, globalPos.dimension) && Objects.equals(this.pos, globalPos.pos);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.dimension, this.pos});
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createMap(ImmutableMap.of(ops.createString("dimension"), this.dimension.serialize(ops), ops.createString("pos"), this.pos.serialize(ops)));
	}

	public String toString() {
		return this.dimension.toString() + " " + this.pos;
	}
}
