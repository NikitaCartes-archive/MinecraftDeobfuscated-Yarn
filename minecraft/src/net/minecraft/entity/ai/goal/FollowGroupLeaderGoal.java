package net.minecraft.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal extends Goal {
	private static final int MIN_SEARCH_DELAY = 200;
	private final SchoolingFishEntity fish;
	private int moveDelay;
	private int checkSurroundingDelay;

	public FollowGroupLeaderGoal(SchoolingFishEntity fish) {
		this.fish = fish;
		this.checkSurroundingDelay = this.getSurroundingSearchDelay(fish);
	}

	protected int getSurroundingSearchDelay(SchoolingFishEntity fish) {
		return toGoalTicks(200 + fish.getRandom().nextInt(200) % 20);
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
			Predicate<SchoolingFishEntity> predicate = fish -> fish.canHaveMoreFishInGroup() || !fish.hasLeader();
			List<? extends SchoolingFishEntity> list = this.fish
				.getWorld()
				.getEntitiesByClass(this.fish.getClass(), this.fish.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
			SchoolingFishEntity schoolingFishEntity = DataFixUtils.orElse(list.stream().filter(SchoolingFishEntity::canHaveMoreFishInGroup).findAny(), this.fish);
			schoolingFishEntity.pullInOtherFish(list.stream().filter(fish -> !fish.hasLeader()));
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
			this.moveDelay = this.getTickCount(10);
			this.fish.moveTowardLeader();
		}
	}
}
