package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal extends Goal {
	private final SchoolingFishEntity owner;
	private int moveDelay;
	private int checkSurroundingDelay;

	public FollowGroupLeaderGoal(SchoolingFishEntity schoolingFishEntity) {
		this.owner = schoolingFishEntity;
		this.checkSurroundingDelay = this.getSurroundingSearchDelay(schoolingFishEntity);
	}

	protected int getSurroundingSearchDelay(SchoolingFishEntity schoolingFishEntity) {
		return 200 + schoolingFishEntity.getRand().nextInt(200) % 20;
	}

	@Override
	public boolean canStart() {
		if (this.owner.hasOtherFishInGroup()) {
			return false;
		} else if (this.owner.hasLeader()) {
			return true;
		} else if (this.checkSurroundingDelay > 0) {
			this.checkSurroundingDelay--;
			return false;
		} else {
			this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.owner);
			Predicate<SchoolingFishEntity> predicate = schoolingFishEntityx -> schoolingFishEntityx.canHaveMoreFishInGroup() || !schoolingFishEntityx.hasLeader();
			List<SchoolingFishEntity> list = this.owner.world.method_8390(this.owner.getClass(), this.owner.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
			SchoolingFishEntity schoolingFishEntity = (SchoolingFishEntity)list.stream()
				.filter(SchoolingFishEntity::canHaveMoreFishInGroup)
				.findAny()
				.orElse(this.owner);
			schoolingFishEntity.pullInOtherFish(list.stream().filter(schoolingFishEntityx -> !schoolingFishEntityx.hasLeader()));
			return this.owner.hasLeader();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.owner.hasLeader() && this.owner.isCloseEnoughToLeader();
	}

	@Override
	public void start() {
		this.moveDelay = 0;
	}

	@Override
	public void onRemove() {
		this.owner.leaveGroup();
	}

	@Override
	public void tick() {
		if (--this.moveDelay <= 0) {
			this.moveDelay = 10;
			this.owner.moveTowardLeader();
		}
	}
}
