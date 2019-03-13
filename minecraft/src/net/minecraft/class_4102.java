package net.minecraft;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4102 implements class_4115 {
	private final LivingEntity field_18342;

	public class_4102(LivingEntity livingEntity) {
		this.field_18342 = livingEntity;
	}

	@Override
	public BlockPos method_18989() {
		return new BlockPos(this.field_18342.x, this.field_18342.y, this.field_18342.z);
	}

	@Override
	public Vec3d method_18991() {
		return new Vec3d(this.field_18342.x, this.field_18342.y + (double)this.field_18342.getStandingEyeHeight(), this.field_18342.z);
	}

	@Override
	public boolean method_18990(LivingEntity livingEntity) {
		Optional<List<LivingEntity>> optional = livingEntity.method_18868().method_18904(class_4140.field_18442);
		return this.field_18342.isValid() && optional.isPresent() && ((List)optional.get()).contains(this.field_18342);
	}

	public String toString() {
		return "EntityPosWrapper for " + this.field_18342;
	}
}
