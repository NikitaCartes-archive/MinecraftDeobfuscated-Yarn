package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EatGrassGoal extends Goal {
	private static final Predicate<BlockState> GRASS_PREDICATE = BlockStatePredicate.forBlock(Blocks.field_10479);
	private final MobEntity mob;
	private final World field_6421;
	private int timer;

	public EatGrassGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.field_6421 = mobEntity.field_6002;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406, Goal.Control.field_18407));
	}

	@Override
	public boolean canStart() {
		if (this.mob.getRand().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.mob);
			return GRASS_PREDICATE.test(this.field_6421.method_8320(blockPos)) ? true : this.field_6421.method_8320(blockPos.down()).getBlock() == Blocks.field_10219;
		}
	}

	@Override
	public void start() {
		this.timer = 40;
		this.field_6421.sendEntityStatus(this.mob, (byte)10);
		this.mob.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.timer = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.timer > 0;
	}

	public int getTimer() {
		return this.timer;
	}

	@Override
	public void tick() {
		this.timer = Math.max(0, this.timer - 1);
		if (this.timer == 4) {
			BlockPos blockPos = new BlockPos(this.mob);
			if (GRASS_PREDICATE.test(this.field_6421.method_8320(blockPos))) {
				if (this.field_6421.getGameRules().getBoolean(GameRules.field_19388)) {
					this.field_6421.breakBlock(blockPos, false);
				}

				this.mob.onEatingGrass();
			} else {
				BlockPos blockPos2 = blockPos.down();
				if (this.field_6421.method_8320(blockPos2).getBlock() == Blocks.field_10219) {
					if (this.field_6421.getGameRules().getBoolean(GameRules.field_19388)) {
						this.field_6421.playLevelEvent(2001, blockPos2, Block.method_9507(Blocks.field_10219.method_9564()));
						this.field_6421.method_8652(blockPos2, Blocks.field_10566.method_9564(), 2);
					}

					this.mob.onEatingGrass();
				}
			}
		}
	}
}
