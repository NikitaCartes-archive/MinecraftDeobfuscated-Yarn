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
	private final World world;
	private int timer;

	public EatGrassGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.world = mobEntity.world;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406, Goal.Control.field_18407));
	}

	@Override
	public boolean canStart() {
		if (this.mob.getRand().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.mob);
			return GRASS_PREDICATE.test(this.world.getBlockState(blockPos)) ? true : this.world.getBlockState(blockPos.down()).getBlock() == Blocks.field_10219;
		}
	}

	@Override
	public void start() {
		this.timer = 40;
		this.world.sendEntityStatus(this.mob, (byte)10);
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
			if (GRASS_PREDICATE.test(this.world.getBlockState(blockPos))) {
				if (this.world.getGameRules().getBoolean(GameRules.field_19388)) {
					this.world.breakBlock(blockPos, false);
				}

				this.mob.onEatingGrass();
			} else {
				BlockPos blockPos2 = blockPos.down();
				if (this.world.getBlockState(blockPos2).getBlock() == Blocks.field_10219) {
					if (this.world.getGameRules().getBoolean(GameRules.field_19388)) {
						this.world.playLevelEvent(2001, blockPos2, Block.getRawIdFromState(Blocks.field_10219.getDefaultState()));
						this.world.setBlockState(blockPos2, Blocks.field_10566.getDefaultState(), 2);
					}

					this.mob.onEatingGrass();
				}
			}
		}
	}
}
