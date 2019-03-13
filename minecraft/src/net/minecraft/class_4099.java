package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4099 implements class_4115 {
	private final BlockPos field_18340;
	private final Vec3d field_18341;

	public class_4099(BlockPos blockPos) {
		this.field_18340 = blockPos;
		this.field_18341 = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Override
	public BlockPos method_18989() {
		return this.field_18340;
	}

	@Override
	public Vec3d method_18991() {
		return this.field_18341;
	}

	@Override
	public boolean method_18990(LivingEntity livingEntity) {
		return true;
	}
}
