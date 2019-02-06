package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageDoor;
import net.minecraft.village.VillageProperties;

public class StayInsideGoal extends Goal {
	private final MobEntityWithAi entity;
	private VillageDoor closestDoor;

	public StayInsideGoal(MobEntityWithAi mobEntityWithAi) {
		this.entity = mobEntityWithAi;
		if (!(mobEntityWithAi.getNavigation() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (this.entity.world.isDaylight()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.entity);
			VillageProperties villageProperties = this.entity.world.getVillageManager().getNearestVillage(blockPos, 16);
			if (villageProperties == null) {
				return false;
			} else {
				this.closestDoor = villageProperties.getClosestDoor(blockPos);
				return this.closestDoor == null ? false : (double)this.closestDoor.squaredDistanceFromInsideTo(blockPos) < 2.25;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.entity.world.isDaylight() ? false : !this.closestDoor.isInvalid() && this.closestDoor.isInside(new BlockPos(this.entity));
	}

	@Override
	public void start() {
		((EntityMobNavigation)this.entity.getNavigation()).setCanPathThroughDoors(false);
		((EntityMobNavigation)this.entity.getNavigation()).setCanEnterOpenDoors(false);
	}

	@Override
	public void onRemove() {
		((EntityMobNavigation)this.entity.getNavigation()).setCanPathThroughDoors(true);
		((EntityMobNavigation)this.entity.getNavigation()).setCanEnterOpenDoors(true);
		this.closestDoor = null;
	}

	@Override
	public void tick() {
		this.closestDoor.entityInsideTick();
	}
}
