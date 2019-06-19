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
import net.minecraft.world.Heightmap;

public class MoveToRaidCenterGoal<T extends RaiderEntity> extends Goal {
	private final T actor;

	public MoveToRaidCenterGoal(T raiderEntity) {
		this.actor = raiderEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		return this.actor.getTarget() == null
			&& !this.actor.hasPassengers()
			&& this.actor.hasActiveRaid()
			&& !this.actor.getRaid().isFinished()
			&& !((ServerWorld)this.actor.world).isNearOccupiedPointOfInterest(new BlockPos(this.actor));
	}

	@Override
	public boolean shouldContinue() {
		return this.actor.hasActiveRaid()
			&& !this.actor.getRaid().isFinished()
			&& this.actor.world instanceof ServerWorld
			&& !((ServerWorld)this.actor.world).isNearOccupiedPointOfInterest(new BlockPos(this.actor));
	}

	@Override
	public void tick() {
		if (this.actor.hasActiveRaid()) {
			Raid raid = this.actor.getRaid();
			if (this.actor.age % 20 == 0) {
				this.includeFreeRaiders(raid);
			}

			if (!this.actor.isNavigating()) {
				Vec3d vec3d = new Vec3d(raid.getCenter());
				Vec3d vec3d2 = new Vec3d(this.actor.x, this.actor.y, this.actor.z);
				Vec3d vec3d3 = vec3d2.subtract(vec3d);
				vec3d = vec3d3.multiply(0.4).add(vec3d);
				Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
				BlockPos blockPos = new BlockPos(vec3d4);
				blockPos = this.actor.world.getTopPosition(Heightmap.Type.field_13203, blockPos);
				if (!this.actor.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
					this.moveToAlternativePosition();
				}
			}
		}
	}

	private void includeFreeRaiders(Raid raid) {
		if (raid.isActive()) {
			Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();
			List<RaiderEntity> list = this.actor
				.world
				.getEntities(
					RaiderEntity.class,
					this.actor.getBoundingBox().expand(16.0),
					raiderEntityx -> !raiderEntityx.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntityx, raid)
				);
			set.addAll(list);

			for (RaiderEntity raiderEntity : set) {
				raid.addRaider(raid.getGroupsSpawned(), raiderEntity, null, true);
			}
		}
	}

	private void moveToAlternativePosition() {
		Random random = this.actor.getRand();
		BlockPos blockPos = this.actor
			.world
			.getTopPosition(Heightmap.Type.field_13203, new BlockPos(this.actor).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.actor.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
