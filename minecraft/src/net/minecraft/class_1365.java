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
	private final MobEntityWithAi field_6498;
	private VillageDoor field_6496;
	private int field_6499 = -1;
	private int field_6497 = -1;

	public class_1365(MobEntityWithAi mobEntityWithAi) {
		this.field_6498 = mobEntityWithAi;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		BlockPos blockPos = new BlockPos(this.field_6498);
		if (this.field_6498 instanceof RaidVictim) {
			RaidVictim raidVictim = (RaidVictim)this.field_6498;
			Raid raid = raidVictim.getRaid();
			if (raid != null && raid.isOnGoing()) {
				VillageProperties villageProperties = raidVictim.getVillage();
				this.field_6496 = villageProperties.getNearestDoor(blockPos);
				if (villageProperties != null && this.field_6496 != null && !this.method_16463()) {
					return true;
				}
			}
		}

		if ((
				!this.field_6498.world.isDaylight()
					|| this.field_6498.world.isRaining() && this.field_6498.world.getBiome(blockPos).getPrecipitation() != Biome.Precipitation.RAIN
			)
			&& this.field_6498.world.dimension.hasSkyLight()) {
			if (this.field_6498.getRand().nextInt(50) != 0) {
				return false;
			} else if (this.method_16463()) {
				return false;
			} else {
				VillageProperties villageProperties2 = this.field_6498.world.getVillageManager().getNearestVillage(blockPos, 14);
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
		return this.field_6499 != -1 && this.field_6498.squaredDistanceTo((double)this.field_6499, this.field_6498.y, (double)this.field_6497) < 4.0;
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6498.getNavigation().method_6357();
	}

	@Override
	public void start() {
		this.field_6499 = -1;
		BlockPos blockPos = this.field_6496.method_6422();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (this.field_6498.squaredDistanceTo(blockPos) > 256.0) {
			Vec3d vec3d = class_1414.method_6373(this.field_6498, 14, 3, new Vec3d((double)i + 0.5, (double)j, (double)k + 0.5));
			if (vec3d != null) {
				this.field_6498.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			}
		} else {
			this.field_6498.getNavigation().startMovingTo((double)i + 0.5, (double)j, (double)k + 0.5, 1.0);
		}
	}

	@Override
	public void onRemove() {
		this.field_6499 = this.field_6496.method_6422().getX();
		this.field_6497 = this.field_6496.method_6422().getZ();
		this.field_6496 = null;
	}
}
