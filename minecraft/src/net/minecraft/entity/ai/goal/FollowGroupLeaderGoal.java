package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal extends Goal {
	private final SchoolingFishEntity fish;
	private int moveDelay;
	private int checkSurroundingDelay;

	public FollowGroupLeaderGoal(SchoolingFishEntity schoolingFishEntity) {
		this.fish = schoolingFishEntity;
		this.checkSurroundingDelay = this.getSurroundingSearchDelay(schoolingFishEntity);
	}

	protected int getSurroundingSearchDelay(SchoolingFishEntity schoolingFishEntity) {
		return 200 + schoolingFishEntity.getRandom().nextInt(200) % 20;
	}

	@Override
	public boolean canStart() {
		if (this.fish.hasOtherFishInGroup()) {
			return false;
		} else if (this.fish.hasLeader()) {
			return true;
		} else if (this.checkSurroundingDelay > 0) {
			this.checkSurroundingDelay--;
			return false;
		} else {
			this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.fish);
			Predicate<SchoolingFishEntity> predicate = schoolingFishEntityx -> schoolingFishEntityx.canHaveMoreFishInGroup() || !schoolingFishEntityx.hasLeader();
			List<SchoolingFishEntity> list = this.fish.world.getEntities(this.fish.getClass(), this.fish.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
			SchoolingFishEntity schoolingFishEntity = (SchoolingFishEntity)list.stream().filter(SchoolingFishEntity::canHaveMoreFishInGroup).findAny().orElse(this.fish);
			schoolingFishEntity.pullInOtherFish(list.stream().filter(schoolingFishEntityx -> !schoolingFishEntityx.hasLeader()));
			return this.fish.hasLeader();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.fish.hasLeader() && this.fish.isCloseEnoughToLeader();
	}

	@Override
	public void start() {
		this.moveDelay = 0;
	}

	@Override
	public void stop() {
		this.fish.leaveGroup();
	}

	@Override
	public void tick() {
		if (--this.moveDelay <= 0) {
			this.moveDelay = 10;
			this.fish.moveTowardLeader();
		}
	}
}
