package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class class_1394 extends class_1379 {
	protected final float field_6626;

	public class_1394(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 0.001F);
	}

	public class_1394(MobEntityWithAi mobEntityWithAi, double d, float f) {
		super(mobEntityWithAi, d);
		this.field_6626 = f;
	}

	@Nullable
	@Override
	protected Vec3d method_6302() {
		if (this.field_6566.method_5816()) {
			Vec3d vec3d = class_1414.method_6378(this.field_6566, 15, 7);
			return vec3d == null ? super.method_6302() : vec3d;
		} else {
			return this.field_6566.getRand().nextFloat() >= this.field_6626 ? class_1414.method_6378(this.field_6566, 10, 7) : super.method_6302();
		}
	}
}
