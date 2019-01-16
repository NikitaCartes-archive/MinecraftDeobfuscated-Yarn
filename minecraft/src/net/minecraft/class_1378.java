package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_1378 extends class_1379 {
	public class_1378(MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, i);
	}

	@Nullable
	@Override
	protected Vec3d method_6302() {
		Vec3d vec3d = class_1414.method_6375(this.field_6566, 10, 7);
		int i = 0;

		while (
			vec3d != null
				&& !this.field_6566.world.getBlockState(new BlockPos(vec3d)).canPlaceAtSide(this.field_6566.world, new BlockPos(vec3d), BlockPlacementEnvironment.field_48)
				&& i++ < 10
		) {
			vec3d = class_1414.method_6375(this.field_6566, 10, 7);
		}

		return vec3d;
	}
}
