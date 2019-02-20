package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.gen.Heightmap;

public class MoveToRaidCenterGoal<T extends RaiderEntity> extends Goal {
	private final T owner;

	public MoveToRaidCenterGoal(T raiderEntity) {
		this.owner = raiderEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		return this.owner.getTarget() == null && !this.owner.hasPassengers() && this.owner.hasActiveRaid();
	}

	@Override
	public boolean shouldContinue() {
		VillageProperties villageProperties = this.owner.world.getVillageManager().getNearestVillage(new BlockPos(this.owner), 0);
		return villageProperties == null;
	}

	@Override
	public void tick() {
		if (this.owner.hasActiveRaid()) {
			Raid raid = this.owner.getRaid();
			if (this.owner.isPatrolLeader()) {
				this.owner.setRaidCenter(raid.getCenter());
			} else {
				RaiderEntity raiderEntity = raid.getLeader(this.owner.getWave());
				if (raiderEntity != null && raiderEntity.hasPatrolTarget()) {
					this.owner.setRaidCenter(raiderEntity.getPatrolTarget());
				}
			}

			this.includeFreeRaiders(raid);
		}

		if (!this.owner.isNavigating() && this.owner.hasPatrolTarget()) {
			Vec3d vec3d = new Vec3d(this.owner.getPatrolTarget());
			Vec3d vec3d2 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
			Vec3d vec3d3 = vec3d2.subtract(vec3d);
			vec3d = vec3d3.multiply(0.4).add(vec3d);
			Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
			BlockPos blockPos = new BlockPos(vec3d4);
			blockPos = this.owner.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
			if (!this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
				this.moveToAlternativePosition();
			}
		}
	}

	private void includeFreeRaiders(Raid raid) {
		Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();
		VillageProperties villageProperties = raid.getVillage();
		BoundingBox boundingBox = raid.getVolume();
		if (villageProperties != null) {
			List<RaiderEntity> list = this.owner
				.world
				.method_8390(
					RaiderEntity.class,
					boundingBox == null ? this.owner.getBoundingBox().expand(16.0) : boundingBox,
					raiderEntityx -> !raiderEntityx.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntityx, raid)
				);
			set.addAll(list);

			for (RaiderEntity raiderEntity : set) {
				raid.addRaider(raid.getGroupsSpawned(), raiderEntity, null, true);
			}
		}
	}

	private void moveToAlternativePosition() {
		Random random = this.owner.getRand();
		BlockPos blockPos = this.owner
			.world
			.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.owner).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
