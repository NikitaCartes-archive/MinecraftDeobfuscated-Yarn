package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.RaiderEntity;
import net.minecraft.sortme.Raid;
import net.minecraft.sortme.RaidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.gen.Heightmap;

public class class_3759<T extends RaiderEntity> extends Goal {
	private final T field_16597;

	public class_3759(T raiderEntity) {
		this.field_16597 = raiderEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		return this.field_16597.getTarget() == null && !this.field_16597.hasPassengers() && this.field_16597.method_16482();
	}

	@Override
	public boolean shouldContinue() {
		VillageProperties villageProperties = this.field_16597.world.getVillageManager().getNearestVillage(new BlockPos(this.field_16597), 0);
		return villageProperties == null;
	}

	@Override
	public void tick() {
		if (this.field_16597.method_16482()) {
			Raid raid = this.field_16597.getRaid();
			if (this.field_16597.method_16219()) {
				this.field_16597.method_16216(raid.method_16495());
			} else {
				RaiderEntity raiderEntity = raid.method_16496(this.field_16597.getWave());
				if (raiderEntity != null && raiderEntity.method_16220()) {
					this.field_16597.method_16216(raiderEntity.method_16215());
				}
			}

			this.method_16465(raid);
		}

		if (!this.field_16597.method_6150() && this.field_16597.method_16220()) {
			Vec3d vec3d = new Vec3d(this.field_16597.method_16215());
			Vec3d vec3d2 = new Vec3d(this.field_16597.x, this.field_16597.y, this.field_16597.z);
			Vec3d vec3d3 = vec3d2.subtract(vec3d);
			vec3d = vec3d3.multiply(0.4).add(vec3d);
			Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
			BlockPos blockPos = new BlockPos((int)vec3d4.x, (int)vec3d4.y, (int)vec3d4.z);
			blockPos = this.field_16597.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
			if (!this.field_16597.getNavigation().method_6337((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
				this.method_16464();
			}
		}
	}

	private void method_16465(Raid raid) {
		Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();
		VillageProperties villageProperties = raid.getVillageProperties();
		BoundingBox boundingBox = raid.method_16498();
		if (villageProperties != null) {
			List<RaiderEntity> list = this.field_16597
				.world
				.getEntities(
					RaiderEntity.class,
					boundingBox == null ? this.field_16597.getBoundingBox().expand(16.0) : boundingBox,
					raiderEntityx -> !raiderEntityx.method_16482() && RaidState.method_16838(raiderEntityx, raid)
				);
			set.addAll(list);

			for (RaiderEntity raiderEntity : set) {
				raid.method_16516(raid.getGroupsSpawned(), raiderEntity, null, true);
			}
		}
	}

	private void method_16464() {
		Random random = this.field_16597.getRand();
		BlockPos blockPos = this.field_16597
			.world
			.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.field_16597).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.field_16597.getNavigation().method_6337((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
