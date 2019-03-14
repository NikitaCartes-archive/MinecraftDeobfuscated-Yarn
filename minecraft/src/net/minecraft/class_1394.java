package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class class_1394 extends WanderAroundGoal {
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
	protected Vec3d getWanderTarget() {
		if (this.owner.isInsideWaterOrBubbleColumn()) {
			Vec3d vec3d = AiUtil.method_6378(this.owner, 15, 7);
			return vec3d == null ? super.getWanderTarget() : vec3d;
		} else {
			return this.owner.getRand().nextFloat() >= this.field_6626 ? AiUtil.method_6378(this.owner, 10, 7) : super.getWanderTarget();
		}
	}
}
