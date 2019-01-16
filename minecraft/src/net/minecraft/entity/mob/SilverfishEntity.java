package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.class_1379;
import net.minecraft.class_1399;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StoneInfestedBlock;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
	private SilverfishEntity.class_1616 field_7366;

	public SilverfishEntity(World world) {
		super(EntityType.SILVERFISH, world);
	}

	@Override
	protected void method_5959() {
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
	public float getEyeHeight() {
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
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return StoneInfestedBlock.method_10269(viewableWorld.getBlockState(blockPos.down())) ? 10.0F : super.method_6144(blockPos, viewableWorld);
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		if (super.canSpawn(iWorld, spawnType)) {
			PlayerEntity playerEntity = iWorld.getClosestSurvivalPlayer(this, 5.0);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	static class class_1615 extends class_1379 {
		private Direction field_7368;
		private boolean field_7367;

		public class_1615(SilverfishEntity silverfishEntity) {
			super(silverfishEntity, 1.0, 10);
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			if (this.field_6566.getTarget() != null) {
				return false;
			} else if (!this.field_6566.getNavigation().method_6357()) {
				return false;
			} else {
				Random random = this.field_6566.getRand();
				if (this.field_6566.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
					this.field_7368 = Direction.random(random);
					BlockPos blockPos = new BlockPos(this.field_6566.x, this.field_6566.y + 0.5, this.field_6566.z).offset(this.field_7368);
					BlockState blockState = this.field_6566.world.getBlockState(blockPos);
					if (StoneInfestedBlock.method_10269(blockState)) {
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
				IWorld iWorld = this.field_6566.world;
				BlockPos blockPos = new BlockPos(this.field_6566.x, this.field_6566.y + 0.5, this.field_6566.z).offset(this.field_7368);
				BlockState blockState = iWorld.getBlockState(blockPos);
				if (StoneInfestedBlock.method_10269(blockState)) {
					iWorld.setBlockState(blockPos, StoneInfestedBlock.method_10270(blockState.getBlock()), 3);
					this.field_6566.method_5990();
					this.field_6566.invalidate();
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
							if (block instanceof StoneInfestedBlock) {
								if (world.getGameRules().getBoolean("mobGriefing")) {
									world.breakBlock(blockPos2, true);
								} else {
									world.setBlockState(blockPos2, ((StoneInfestedBlock)block).method_10271().getDefaultState(), 3);
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
