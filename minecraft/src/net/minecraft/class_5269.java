package net.minecraft;

import net.minecraft.util.math.BlockPos;

public interface class_5269 extends class_5217 {
	void method_27416(int i);

	void method_27417(int i);

	void method_27419(int i);

	default void setSpawnPos(BlockPos blockPos) {
		this.method_27416(blockPos.getX());
		this.method_27417(blockPos.getY());
		this.method_27419(blockPos.getZ());
	}

	void setTime(long l);

	void setTimeOfDay(long l);
}
