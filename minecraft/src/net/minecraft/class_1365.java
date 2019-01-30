package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidVictim;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageDoor;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.biome.Biome;

public class class_1365 extends Goal {
	private final MobEntityWithAi owner;
	private VillageDoor field_6496;
	private int field_6499 = -1;
	private int field_6497 = -1;

	public class_1365(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		BlockPos blockPos = new BlockPos(this.owner);
		if (this.owner instanceof RaidVictim) {
			RaidVictim raidVictim = (RaidVictim)this.owner;
			Raid raid = raidVictim.getRaid();
			if (raid != null && raid.isOnGoing()) {
				VillageProperties villageProperties = raidVictim.getVillage();
				this.field_6496 = villageProperties.getNearestDoor(blockPos);
				if (villageProperties != null && this.field_6496 != null && !this.method_16463()) {
					return true;
				}
			}
		}

		if ((!this.owner.world.isDaylight() || this.owner.world.isRaining() && this.owner.world.getBiome(blockPos).getPrecipitation() != Biome.Precipitation.RAIN)
			&& this.owner.world.dimension.hasSkyLight()) {
			if (this.owner.getRand().nextInt(50) != 0) {
				return false;
			} else if (this.method_16463()) {
				return false;
			} else {
				VillageProperties villageProperties2 = this.owner.world.getVillageManager().getNearestVillage(blockPos, 14);
				if (villageProperties2 == null) {
					return false;
				} else {
					this.field_6496 = villageProperties2.getNearestDoor(blockPos);
					return this.field_6496 != null;
				}
			}
		} else {
			return false;
		}
	}

	private boolean method_16463() {
		return this.field_6499 != -1 && this.owner.squaredDistanceTo((double)this.field_6499, this.owner.y, (double)this.field_6497) < 4.0;
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.field_6499 = -1;
		BlockPos blockPos = this.field_6496.getInsidePosition();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (this.owner.squaredDistanceTo(blockPos) > 256.0) {
			Vec3d vec3d = class_1414.method_6373(this.owner, 14, 3, new Vec3d((double)i + 0.5, (double)j, (double)k + 0.5));
			if (vec3d != null) {
				this.owner.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			}
		} else {
			this.owner.getNavigation().startMovingTo((double)i + 0.5, (double)j, (double)k + 0.5, 1.0);
		}
	}

	@Override
	public void onRemove() {
		this.field_6499 = this.field_6496.getInsidePosition().getX();
		this.field_6497 = this.field_6496.getInsidePosition().getZ();
		this.field_6496 = null;
	}
}
