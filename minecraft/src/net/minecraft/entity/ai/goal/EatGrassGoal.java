package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EatGrassGoal extends Goal {
	private static final Predicate<BlockState> field_6423 = BlockStatePredicate.forBlock(Blocks.field_10479);
	private final MobEntity owner;
	private final World world;
	private int timer;

	public EatGrassGoal(MobEntity mobEntity) {
		this.owner = mobEntity;
		this.world = mobEntity.world;
		this.setControlBits(7);
	}

	@Override
	public boolean canStart() {
		if (this.owner.getRand().nextInt(this.owner.isChild() ? 50 : 1000) != 0) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.owner.x, this.owner.y, this.owner.z);
			return field_6423.test(this.world.getBlockState(blockPos)) ? true : this.world.getBlockState(blockPos.down()).getBlock() == Blocks.field_10219;
		}
	}

	@Override
	public void start() {
		this.timer = 40;
		this.world.summonParticle(this.owner, (byte)10);
		this.owner.getNavigation().method_6340();
	}

	@Override
	public void onRemove() {
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
			BlockPos blockPos = new BlockPos(this.owner.x, this.owner.y, this.owner.z);
			if (field_6423.test(this.world.getBlockState(blockPos))) {
				if (this.world.getGameRules().getBoolean("mobGriefing")) {
					this.world.breakBlock(blockPos, false);
				}

				this.owner.method_5983();
			} else {
				BlockPos blockPos2 = blockPos.down();
				if (this.world.getBlockState(blockPos2).getBlock() == Blocks.field_10219) {
					if (this.world.getGameRules().getBoolean("mobGriefing")) {
						this.world.fireWorldEvent(2001, blockPos2, Block.getRawIdFromState(Blocks.field_10219.getDefaultState()));
						this.world.setBlockState(blockPos2, Blocks.field_10566.getDefaultState(), 2);
					}

					this.owner.method_5983();
				}
			}
		}
	}
}
