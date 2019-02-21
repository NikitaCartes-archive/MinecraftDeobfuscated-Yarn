package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.class_1399;
import net.minecraft.class_4051;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SilverfishEntity extends HostileEntity {
	private static final class_4051 field_18131 = new class_4051().method_18418(5.0).method_18424();
	private SilverfishEntity.class_1616 field_7366;

	public SilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.field_7366 = new SilverfishEntity.class_1616(this);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, this.field_7366);
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new SilverfishEntity.class_1615(this));
		this.targetSelector.add(1, new class_1399(this).method_6318());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
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
	protected boolean method_5658() {
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
			if ((damageSource instanceof EntityDamageSource || damageSource == DamageSource.MAGIC) && this.field_7366 != null) {
				this.field_7366.method_7136();
			}

			return super.damage(damageSource, f);
		}
	}

	@Override
	public void update() {
		this.field_6283 = this.yaw;
		super.update();
	}

	@Override
	public void setYaw(float f) {
		this.yaw = f;
		super.setYaw(f);
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
		return InfestedBlock.hasRegularBlock(viewableWorld.getBlockState(blockPos.down())) ? 10.0F : super.getPathfindingFavor(blockPos, viewableWorld);
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		if (super.canSpawn(iWorld, spawnType)) {
			PlayerEntity playerEntity = this.world.method_18462(field_18131, this);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	static class class_1615 extends WanderAroundGoal {
		private Direction field_7368;
		private boolean field_7367;

		public class_1615(SilverfishEntity silverfishEntity) {
			super(silverfishEntity, 1.0, 10);
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			if (this.owner.getTarget() != null) {
				return false;
			} else if (!this.owner.getNavigation().isIdle()) {
				return false;
			} else {
				Random random = this.owner.getRand();
				if (this.owner.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
					this.field_7368 = Direction.random(random);
					BlockPos blockPos = new BlockPos(this.owner.x, this.owner.y + 0.5, this.owner.z).offset(this.field_7368);
					BlockState blockState = this.owner.world.getBlockState(blockPos);
					if (InfestedBlock.hasRegularBlock(blockState)) {
						this.field_7367 = true;
						return true;
					}
				}

				this.field_7367 = false;
				return super.canStart();
			}
		}

		@Override
		public boolean shouldContinue() {
			return this.field_7367 ? false : super.shouldContinue();
		}

		@Override
		public void start() {
			if (!this.field_7367) {
				super.start();
			} else {
				IWorld iWorld = this.owner.world;
				BlockPos blockPos = new BlockPos(this.owner.x, this.owner.y + 0.5, this.owner.z).offset(this.field_7368);
				BlockState blockState = iWorld.getBlockState(blockPos);
				if (InfestedBlock.hasRegularBlock(blockState)) {
					iWorld.setBlockState(blockPos, InfestedBlock.getRegularBlock(blockState.getBlock()), 3);
					this.owner.method_5990();
					this.owner.invalidate();
				}
			}
		}
	}

	static class class_1616 extends Goal {
		private final SilverfishEntity field_7370;
		private int field_7369;

		public class_1616(SilverfishEntity silverfishEntity) {
			this.field_7370 = silverfishEntity;
		}

		public void method_7136() {
			if (this.field_7369 == 0) {
				this.field_7369 = 20;
			}
		}

		@Override
		public boolean canStart() {
			return this.field_7369 > 0;
		}

		@Override
		public void tick() {
			this.field_7369--;
			if (this.field_7369 <= 0) {
				World world = this.field_7370.world;
				Random random = this.field_7370.getRand();
				BlockPos blockPos = new BlockPos(this.field_7370);

				for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
					for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
						for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
							BlockPos blockPos2 = blockPos.add(j, i, k);
							BlockState blockState = world.getBlockState(blockPos2);
							Block block = blockState.getBlock();
							if (block instanceof InfestedBlock) {
								if (world.getGameRules().getBoolean("mobGriefing")) {
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
}
