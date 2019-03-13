package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_1414;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

public class MoveToVillageCenterGoal extends Goal {
	private final MobEntityWithAi owner;
	private final int searchRange;
	@Nullable
	private BlockPos field_17947;

	public MoveToVillageCenterGoal(MobEntityWithAi mobEntityWithAi, int i) {
		this.owner = mobEntityWithAi;
		this.searchRange = i;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.owner.hasPassengers()) {
			return false;
		} else if (this.owner.field_6002.isDaylight()) {
			return false;
		} else if (this.owner.getRand().nextInt(this.searchRange) != 0) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.owner.field_6002;
			BlockPos blockPos = new BlockPos(this.owner);
			if (!serverWorld.method_19497(blockPos, 4)) {
				return false;
			} else {
				Vec3d vec3d = class_1414.method_19108(this.owner, 15, 7, blockPosx -> (double)(-serverWorld.method_19498(ChunkSectionPos.from(blockPosx))));
				this.field_17947 = vec3d == null ? null : new BlockPos(vec3d);
				return this.field_17947 != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_17947 != null && !this.owner.method_5942().isIdle() && this.owner.method_5942().method_6355().equals(this.field_17947);
	}

	@Override
	public void tick() {
		if (this.field_17947 != null) {
			EntityNavigation entityNavigation = this.owner.method_5942();
			if (entityNavigation.isIdle() && this.owner.method_5831(this.field_17947) >= 100.0) {
				Vec3d vec3d = new Vec3d(this.field_17947);
				Vec3d vec3d2 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
				Vec3d vec3d3 = vec3d2.subtract(vec3d);
				vec3d = vec3d3.multiply(0.4).add(vec3d);
				Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
				BlockPos blockPos = new BlockPos((int)vec3d4.x, (int)vec3d4.y, (int)vec3d4.z);
				blockPos = this.owner.field_6002.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
				if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
					this.findOtherWaypoint();
				}
			}
		}
	}

	private void findOtherWaypoint() {
		Random random = this.owner.getRand();
		BlockPos blockPos = this.owner
			.field_6002
			.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.owner).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.owner.method_5942().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
