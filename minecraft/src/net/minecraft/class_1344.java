package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_1344 extends Goal {
	private final MobEntityWithAi field_6419;
	private double field_6417;
	private double field_6416;
	private double field_6415;
	private final double field_6420;
	private final World field_6418;

	public class_1344(MobEntityWithAi mobEntityWithAi, double d) {
		this.field_6419 = mobEntityWithAi;
		this.field_6420 = d;
		this.field_6418 = mobEntityWithAi.world;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6418.isDaylight()) {
			return false;
		} else if (!this.field_6419.isOnFire()) {
			return false;
		} else if (!this.field_6418.isSkyVisible(new BlockPos(this.field_6419.x, this.field_6419.getBoundingBox().minY, this.field_6419.z))) {
			return false;
		} else if (!this.field_6419.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			return false;
		} else {
			Vec3d vec3d = this.method_6257();
			if (vec3d == null) {
				return false;
			} else {
				this.field_6417 = vec3d.x;
				this.field_6416 = vec3d.y;
				this.field_6415 = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6419.getNavigation().method_6357();
	}

	@Override
	public void start() {
		this.field_6419.getNavigation().startMovingTo(this.field_6417, this.field_6416, this.field_6415, this.field_6420);
	}

	@Nullable
	private Vec3d method_6257() {
		Random random = this.field_6419.getRand();
		BlockPos blockPos = new BlockPos(this.field_6419.x, this.field_6419.getBoundingBox().minY, this.field_6419.z);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!this.field_6418.isSkyVisible(blockPos2) && this.field_6419.method_6149(blockPos2) < 0.0F) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}
}
