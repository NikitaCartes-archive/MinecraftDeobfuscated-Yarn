package net.minecraft.entity.ai.goal;

import net.minecraft.class_4051;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.World;

public class VillagerBreedGoal extends Goal {
	private static final class_4051 field_18088 = new class_4051().method_18418(8.0).method_18421().method_18417();
	private final VillagerEntity owner;
	private VillagerEntity mate;
	private final World world;
	private int delay;
	private VillageProperties foundVillage;

	public VillagerBreedGoal(VillagerEntity villagerEntity) {
		this.owner = villagerEntity;
		this.world = villagerEntity.world;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.owner.getBreedingAge() != 0) {
			return false;
		} else if (this.owner.getRand().nextInt(500) != 0) {
			return false;
		} else {
			this.foundVillage = this.world.getVillageManager().getNearestVillage(new BlockPos(this.owner), 0);
			if (this.foundVillage == null) {
				return false;
			} else if (this.needsMoreVillager() && this.owner.isWillingToMate(true)) {
				Entity entity = this.world
					.method_18465(VillagerEntity.class, field_18088, this.owner, this.owner.x, this.owner.y, this.owner.z, this.owner.getBoundingBox().expand(8.0, 3.0, 8.0));
				if (entity == null) {
					return false;
				} else {
					this.mate = (VillagerEntity)entity;
					return this.mate.getBreedingAge() == 0 && this.mate.isWillingToMate(true);
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void start() {
		this.delay = 300;
		this.owner.setInMating(true);
	}

	@Override
	public void onRemove() {
		this.foundVillage = null;
		this.mate = null;
		this.owner.setInMating(false);
	}

	@Override
	public boolean shouldContinue() {
		return this.delay >= 0 && this.needsMoreVillager() && this.owner.getBreedingAge() == 0 && this.owner.isWillingToMate(false);
	}

	@Override
	public void tick() {
		this.delay--;
		this.owner.getLookControl().lookAt(this.mate, 10.0F, 30.0F);
		if (this.owner.squaredDistanceTo(this.mate) > 2.25) {
			this.owner.getNavigation().startMovingTo(this.mate, 0.25);
		} else if (this.delay == 0 && this.mate.isInMating()) {
			this.spawnBabyVillager();
		}

		if (this.owner.getRand().nextInt(35) == 0) {
			this.world.summonParticle(this.owner, (byte)12);
		}
	}

	private boolean needsMoreVillager() {
		if (!this.foundVillage.hasRecentDeath()) {
			return false;
		} else {
			int i = (int)((double)((float)this.foundVillage.getDoorCount()) * 0.35);
			return this.foundVillage.getPopulationSize() < i;
		}
	}

	private void spawnBabyVillager() {
		VillagerEntity villagerEntity = this.owner.method_7225(this.mate);
		if (villagerEntity != null) {
			this.mate.setBreedingAge(6000);
			this.owner.setBreedingAge(6000);
			this.mate.setWillingToMate(false);
			this.owner.setWillingToMate(false);
			villagerEntity.setBreedingAge(-24000);
			villagerEntity.setPositionAndAngles(this.owner.x, this.owner.y, this.owner.z, 0.0F, 0.0F);
			this.world.spawnEntity(villagerEntity);
			this.world.summonParticle(villagerEntity, (byte)12);
		}
	}
}
