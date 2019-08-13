package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SilverfishEntity extends HostileEntity {
	private SilverfishEntity.CallForHelpGoal callForHelpGoal;

	public SilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.callForHelpGoal = new SilverfishEntity.CallForHelpGoal(this);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, this.callForHelpGoal);
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new SilverfishEntity.WanderAndInfestGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 0.1F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14786;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14593;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14673;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_15084, 0.15F, 1.0F);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if ((damageSource instanceof EntityDamageSource || damageSource == DamageSource.MAGIC) && this.callForHelpGoal != null) {
				this.callForHelpGoal.onHurt();
			}

			return super.damage(damageSource, f);
		}
	}

	@Override
	public void tick() {
		this.field_6283 = this.yaw;
		super.tick();
	}

	@Override
	public void setYaw(float f) {
		this.yaw = f;
		super.setYaw(f);
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
		return InfestedBlock.isInfestable(viewableWorld.getBlockState(blockPos.down())) ? 10.0F : super.getPathfindingFavor(blockPos, viewableWorld);
	}

	public static boolean method_20684(EntityType<SilverfishEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		if (method_20681(entityType, iWorld, spawnType, blockPos, random)) {
			PlayerEntity playerEntity = iWorld.getClosestPlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0, true);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	static class CallForHelpGoal extends Goal {
		private final SilverfishEntity silverfish;
		private int delay;

		public CallForHelpGoal(SilverfishEntity silverfishEntity) {
			this.silverfish = silverfishEntity;
		}

		public void onHurt() {
			if (this.delay == 0) {
				this.delay = 20;
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
				World world = this.silverfish.world;
				Random random = this.silverfish.getRand();
				BlockPos blockPos = new BlockPos(this.silverfish);

				for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
					for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
						for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
							BlockPos blockPos2 = blockPos.add(j, i, k);
							BlockState blockState = world.getBlockState(blockPos2);
							Block block = blockState.getBlock();
							if (block instanceof InfestedBlock) {
								if (world.getGameRules().getBoolean(GameRules.field_19388)) {
									world.breakBlock(blockPos2, true);
								} else {
									world.setBlockState(blockPos2, ((InfestedBlock)block).getRegularBlock().getDefaultState(), 3);
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
		private Direction direction;
		private boolean canInfest;

		public WanderAndInfestGoal(SilverfishEntity silverfishEntity) {
			super(silverfishEntity, 1.0, 10);
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			if (this.mob.getTarget() != null) {
				return false;
			} else if (!this.mob.getNavigation().isIdle()) {
				return false;
			} else {
				Random random = this.mob.getRand();
				if (this.mob.world.getGameRules().getBoolean(GameRules.field_19388) && random.nextInt(10) == 0) {
					this.direction = Direction.random(random);
					BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
					BlockState blockState = this.mob.world.getBlockState(blockPos);
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
				IWorld iWorld = this.mob.world;
				BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
				BlockState blockState = iWorld.getBlockState(blockPos);
				if (InfestedBlock.isInfestable(blockState)) {
					iWorld.setBlockState(blockPos, InfestedBlock.fromRegularBlock(blockState.getBlock()), 3);
					this.mob.playSpawnEffects();
					this.mob.remove();
				}
			}
		}
	}
}
