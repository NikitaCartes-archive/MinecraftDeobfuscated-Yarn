package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.World;

public class class_1363 extends Goal {
	private final VillagerEntity field_6494;
	private VillagerEntity field_6493;
	private final World field_6490;
	private int field_6491;
	private VillageProperties field_6492;

	public class_1363(VillagerEntity villagerEntity) {
		this.field_6494 = villagerEntity;
		this.field_6490 = villagerEntity.world;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.field_6494.getBreedingAge() != 0) {
			return false;
		} else if (this.field_6494.getRand().nextInt(500) != 0) {
			return false;
		} else {
			this.field_6492 = this.field_6490.getVillageManager().getNearestVillage(new BlockPos(this.field_6494), 0);
			if (this.field_6492 == null) {
				return false;
			} else if (this.method_6287() && this.field_6494.method_7245(true)) {
				Entity entity = this.field_6490.getClosestVisibleEntityTo(VillagerEntity.class, this.field_6494.getBoundingBox().expand(8.0, 3.0, 8.0), this.field_6494);
				if (entity == null) {
					return false;
				} else {
					this.field_6493 = (VillagerEntity)entity;
					return this.field_6493.getBreedingAge() == 0 && this.field_6493.method_7245(true);
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void start() {
		this.field_6491 = 300;
		this.field_6494.method_7226(true);
	}

	@Override
	public void onRemove() {
		this.field_6492 = null;
		this.field_6493 = null;
		this.field_6494.method_7226(false);
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6491 >= 0 && this.method_6287() && this.field_6494.getBreedingAge() == 0 && this.field_6494.method_7245(false);
	}

	@Override
	public void tick() {
		this.field_6491--;
		this.field_6494.getLookControl().lookAt(this.field_6493, 10.0F, 30.0F);
		if (this.field_6494.squaredDistanceTo(this.field_6493) > 2.25) {
			this.field_6494.getNavigation().method_6335(this.field_6493, 0.25);
		} else if (this.field_6491 == 0 && this.field_6493.method_7241()) {
			this.method_6286();
		}

		if (this.field_6494.getRand().nextInt(35) == 0) {
			this.field_6490.method_8421(this.field_6494, (byte)12);
		}
	}

	private boolean method_6287() {
		if (!this.field_6492.method_6381()) {
			return false;
		} else {
			int i = (int)((double)((float)this.field_6492.getDoorCount()) * 0.35);
			return this.field_6492.getPopulationSize() < i;
		}
	}

	private void method_6286() {
		VillagerEntity villagerEntity = this.field_6494.createChild(this.field_6493);
		this.field_6493.setBreedingAge(6000);
		this.field_6494.setBreedingAge(6000);
		this.field_6493.method_7243(false);
		this.field_6494.method_7243(false);
		villagerEntity.setBreedingAge(-24000);
		villagerEntity.setPositionAndAngles(this.field_6494.x, this.field_6494.y, this.field_6494.z, 0.0F, 0.0F);
		this.field_6490.spawnEntity(villagerEntity);
		this.field_6490.method_8421(villagerEntity, (byte)12);
	}
}
