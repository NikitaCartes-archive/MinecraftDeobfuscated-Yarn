package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

public class MoveToRaidCenterGoal<T extends RaiderEntity> extends Goal {
	private final T field_16597;

	public MoveToRaidCenterGoal(T raiderEntity) {
		this.field_16597 = raiderEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		return this.field_16597.getTarget() == null && !this.field_16597.hasPassengers() && this.field_16597.hasActiveRaid();
	}

	@Override
	public boolean shouldContinue() {
		return this.field_16597.field_6002 instanceof ServerWorld && !((ServerWorld)this.field_16597.field_6002).method_19500(new BlockPos(this.field_16597));
	}

	@Override
	public void tick() {
		if (this.field_16597.hasActiveRaid()) {
			Raid raid = this.field_16597.getRaid();
			if (this.field_16597.isPatrolLeader()) {
				this.field_16597.method_16216(raid.method_16495());
			} else {
				RaiderEntity raiderEntity = raid.method_16496(this.field_16597.getWave());
				if (raiderEntity != null && raiderEntity.hasPatrolTarget()) {
					this.field_16597.method_16216(raiderEntity.method_16215());
				}
			}

			this.method_16465(raid);
		}

		if (!this.field_16597.isNavigating() && this.field_16597.hasPatrolTarget()) {
			Vec3d vec3d = new Vec3d(this.field_16597.method_16215());
			Vec3d vec3d2 = new Vec3d(this.field_16597.x, this.field_16597.y, this.field_16597.z);
			Vec3d vec3d3 = vec3d2.subtract(vec3d);
			vec3d = vec3d3.multiply(0.4).add(vec3d);
			Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
			BlockPos blockPos = new BlockPos(vec3d4);
			blockPos = this.field_16597.field_6002.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
			if (!this.field_16597.method_5942().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
				this.moveToAlternativePosition();
			}
		}
	}

	private void method_16465(Raid raid) {
		if (raid.isActive()) {
			Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();
			List<RaiderEntity> list = this.field_16597
				.field_6002
				.method_8390(
					RaiderEntity.class,
					this.field_16597.method_5829().expand(16.0),
					raiderEntityx -> !raiderEntityx.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntityx, raid)
				);
			set.addAll(list);

			for (RaiderEntity raiderEntity : set) {
				raid.method_16516(raid.getGroupsSpawned(), raiderEntity, null, true);
			}
		}
	}

	private void moveToAlternativePosition() {
		Random random = this.field_16597.getRand();
		BlockPos blockPos = this.field_16597
			.field_6002
			.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.field_16597).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.field_16597.method_5942().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
