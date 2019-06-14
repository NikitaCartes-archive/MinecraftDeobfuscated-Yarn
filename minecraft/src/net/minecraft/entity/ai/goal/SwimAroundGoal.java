package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SwimAroundGoal extends WanderAroundGoal {
	public SwimAroundGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, i);
	}

	@Nullable
	@Override
	protected Vec3d method_6302() {
		Vec3d vec3d = PathfindingUtil.method_6375(this.mob, 10, 7);
		int i = 0;

		while (
			vec3d != null
				&& !this.mob.field_6002.method_8320(new BlockPos(vec3d)).method_11609(this.mob.field_6002, new BlockPos(vec3d), BlockPlacementEnvironment.field_48)
				&& i++ < 10
		) {
			vec3d = PathfindingUtil.method_6375(this.mob, 10, 7);
		}

		return vec3d;
	}
}
