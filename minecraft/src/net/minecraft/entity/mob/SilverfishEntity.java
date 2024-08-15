package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PowderSnowJumpGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SilverfishEntity extends HostileEntity {
	@Nullable
	private SilverfishEntity.CallForHelpGoal callForHelpGoal;

	public SilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.callForHelpGoal = new SilverfishEntity.CallForHelpGoal(this);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new PowderSnowJumpGoal(this, this.getWorld()));
		this.goalSelector.add(3, this.callForHelpGoal);
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new SilverfishEntity.WanderAndInfestGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
	}

	public static DefaultAttributeContainer.Builder createSilverfishAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MAX_HEALTH, 8.0)
			.add(EntityAttributes.MOVEMENT_SPEED, 0.25)
			.add(EntityAttributes.ATTACK_DAMAGE, 1.0);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SILVERFISH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SILVERFISH_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if ((source.getAttacker() != null || source.isIn(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) && this.callForHelpGoal != null) {
				this.callForHelpGoal.onHurt();
			}

			return super.damage(source, amount);
		}
	}

	@Override
	public void tick() {
		this.bodyYaw = this.getYaw();
		super.tick();
	}

	@Override
	public void setBodyYaw(float bodyYaw) {
		this.setYaw(bodyYaw);
		super.setBodyYaw(bodyYaw);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return InfestedBlock.isInfestable(world.getBlockState(pos.down())) ? 10.0F : super.getPathfindingFavor(pos, world);
	}

	public static boolean canSpawn(EntityType<SilverfishEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (!canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random)) {
			return false;
		} else if (SpawnReason.isAnySpawner(spawnReason)) {
			return true;
		} else {
			PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0, true);
			return playerEntity == null;
		}
	}

	static class CallForHelpGoal extends Goal {
		private final SilverfishEntity silverfish;
		private int delay;

		public CallForHelpGoal(SilverfishEntity silverfish) {
			this.silverfish = silverfish;
		}

		public void onHurt() {
			if (this.delay == 0) {
				this.delay = this.getTickCount(20);
			}
		}

		@Override
		public boolean canStart() {
			return this.delay > 0;
		}

		@Override
		public void tick() {
			this.delay--;
			if (this.delay <= 0) {
				World world = this.silverfish.getWorld();
				Random random = this.silverfish.getRandom();
				BlockPos blockPos = this.silverfish.getBlockPos();

				for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
					for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
						for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
							BlockPos blockPos2 = blockPos.add(j, i, k);
							BlockState blockState = world.getBlockState(blockPos2);
							Block block = blockState.getBlock();
							if (block instanceof InfestedBlock) {
								if (world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
									world.breakBlock(blockPos2, true, this.silverfish);
								} else {
									world.setBlockState(blockPos2, ((InfestedBlock)block).toRegularState(world.getBlockState(blockPos2)), Block.NOTIFY_ALL);
								}

								if (random.nextBoolean()) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	static class WanderAndInfestGoal extends WanderAroundGoal {
		@Nullable
		private Direction direction;
		private boolean canInfest;

		public WanderAndInfestGoal(SilverfishEntity silverfish) {
			super(silverfish, 1.0, 10);
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (this.mob.getTarget() != null) {
				return false;
			} else if (!this.mob.getNavigation().isIdle()) {
				return false;
			} else {
				Random random = this.mob.getRandom();
				if (this.mob.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && random.nextInt(toGoalTicks(10)) == 0) {
					this.direction = Direction.random(random);
					BlockPos blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).offset(this.direction);
					BlockState blockState = this.mob.getWorld().getBlockState(blockPos);
					if (InfestedBlock.isInfestable(blockState)) {
						this.canInfest = true;
						return true;
					}
				}

				this.canInfest = false;
				return super.canStart();
			}
		}

		@Override
		public boolean shouldContinue() {
			return this.canInfest ? false : super.shouldContinue();
		}

		@Override
		public void start() {
			if (!this.canInfest) {
				super.start();
			} else {
				WorldAccess worldAccess = this.mob.getWorld();
				BlockPos blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).offset(this.direction);
				BlockState blockState = worldAccess.getBlockState(blockPos);
				if (InfestedBlock.isInfestable(blockState)) {
					worldAccess.setBlockState(blockPos, InfestedBlock.fromRegularState(blockState), Block.NOTIFY_ALL);
					this.mob.playSpawnEffects();
					this.mob.discard();
				}
			}
		}
	}
}
