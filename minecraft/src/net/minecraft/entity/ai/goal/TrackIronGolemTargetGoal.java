package net.minecraft.entity.ai.goal;

import net.minecraft.class_4051;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.village.VillageProperties;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
	private final IronGolemEntity ironGolem;
	private LivingEntity target;

	public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
		super(ironGolemEntity, false, true);
		this.ironGolem = ironGolemEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		VillageProperties villageProperties = this.ironGolem.getVillageProperties();
		if (villageProperties == null) {
			return false;
		} else {
			this.target = villageProperties.getNearestAttacker(this.ironGolem);
			if (this.target instanceof CreeperEntity) {
				return false;
			} else if (this.canTrack(this.target, class_4051.field_18092)) {
				return true;
			} else if (this.entity.getRand().nextInt(20) == 0) {
				this.target = villageProperties.getNearestUnpopularPlayer(this.ironGolem);
				return this.canTrack(this.target, class_4051.field_18092);
			} else {
				return false;
			}
		}
	}

	@Override
	public void start() {
		this.ironGolem.setTarget(this.target);
		super.start();
	}
}
