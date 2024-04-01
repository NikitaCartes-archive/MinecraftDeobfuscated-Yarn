package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class EatGrassGoal extends Goal {
	private static final int MAX_TIMER = 40;
	private static final Predicate<BlockState> SHORT_GRASS_PREDICATE = BlockStatePredicate.forBlock(Blocks.SHORT_GRASS);
	private final MobEntity mob;
	private final World world;
	private int timer;

	public EatGrassGoal(MobEntity mob) {
		this.mob = mob;
		this.world = mob.getWorld();
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
	}

	@Override
	public boolean canStart() {
		if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
			return false;
		} else {
			BlockPos blockPos = this.mob.getBlockPos();
			return SHORT_GRASS_PREDICATE.test(this.world.getBlockState(blockPos))
				? true
				: this.world.getBlockState(blockPos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON);
		}
	}

	@Override
	public void start() {
		this.timer = this.getTickCount(40);
		this.world.sendEntityStatus(this.mob, EntityStatuses.SET_SHEEP_EAT_GRASS_TIMER_OR_PRIME_TNT_MINECART);
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
		if (this.timer == this.getTickCount(4)) {
			BlockPos blockPos = this.mob.getBlockPos();
			if (SHORT_GRASS_PREDICATE.test(this.world.getBlockState(blockPos))) {
				if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					this.world.breakBlock(blockPos, false);
				}

				this.mob.onEatingGrass();
			} else {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = this.world.getBlockState(blockPos2);
				if (blockState.isIn(BlockTags.ANIMALS_SPAWNABLE_ON)) {
					if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
						this.world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos2, Block.getRawIdFromState(blockState));
						this.world.setBlockState(blockPos2, (this.world.isPotato() ? Blocks.TERRE_DE_POMME : Blocks.DIRT).getDefaultState(), Block.NOTIFY_LISTENERS);
					}

					if (blockState.isOf(Blocks.CORRUPTED_PEELGRASS_BLOCK)) {
						this.mob.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 20));
					}

					this.mob.onEatingGrass();
				}
			}
		}
	}
}
