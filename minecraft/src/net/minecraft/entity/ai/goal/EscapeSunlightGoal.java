package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeSunlightGoal extends Goal {
	protected final MobEntityWithAi owner;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private final World world;

	public EscapeSunlightGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this.owner = mobEntityWithAi;
		this.speed = d;
		this.world = mobEntityWithAi.world;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.world.isDaylight()) {
			return false;
		} else if (!this.owner.isOnFire()) {
			return false;
		} else if (!this.world.isSkyVisible(new BlockPos(this.owner.x, this.owner.getBoundingBox().minY, this.owner.z))) {
			return false;
		} else {
			return !this.owner.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ? false : this.method_18250();
		}
	}

	protected boolean method_18250() {
		Vec3d vec3d = this.locateShadedPos();
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Nullable
	protected Vec3d locateShadedPos() {
		Random random = this.owner.getRand();
		BlockPos blockPos = new BlockPos(this.owner.x, this.owner.getBoundingBox().minY, this.owner.z);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!this.world.isSkyVisible(blockPos2) && this.owner.getPathfindingFavor(blockPos2) < 0.0F) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}
}
