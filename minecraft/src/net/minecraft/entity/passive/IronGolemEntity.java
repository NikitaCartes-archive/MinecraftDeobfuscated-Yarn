package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToEntityTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CollisionView;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;

public class IronGolemEntity extends GolemEntity {
	protected static final TrackedData<Byte> IRON_GOLEM_FLAGS = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int field_6762;
	private int field_6759;

	public IronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(2, new GoToEntityTargetGoal(this, 0.9, 32.0F));
		this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6));
		this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6, false, 4, () -> false));
		this.goalSelector.add(5, new IronGolemLookGoal(this));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.targetSelector
			.add(
				3,
				new FollowTargetGoal(this, MobEntity.class, 5, false, false, livingEntity -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity))
			);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(IRON_GOLEM_FLAGS, (byte)0);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	protected int getNextAirUnderwater(int air) {
		return air;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
			this.setTarget((LivingEntity)entity);
		}

		super.pushAway(entity);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.field_6762 > 0) {
			this.field_6762--;
		}

		if (this.field_6759 > 0) {
			this.field_6759--;
		}

		if (squaredHorizontalLength(this.getVelocity()) > 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				this.world
					.addParticle(
						new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
						this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						this.getBoundingBox().y1 + 0.1,
						this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0
					);
			}
		}
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		if (this.isPlayerCreated() && type == EntityType.PLAYER) {
			return false;
		} else {
			return type == EntityType.CREEPER ? false : super.canTarget(type);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("PlayerCreated", this.isPlayerCreated());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setPlayerCreated(tag.getBoolean("PlayerCreated"));
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.field_6762 = 10;
		this.world.sendEntityStatus(this, (byte)4);
		boolean bl = target.damage(DamageSource.mob(this), (float)(7 + this.random.nextInt(15)));
		if (bl) {
			target.setVelocity(target.getVelocity().add(0.0, 0.4F, 0.0));
			this.dealDamage(this, target);
		}

		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.field_6762 = 10;
			this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		} else if (status == 11) {
			this.field_6759 = 400;
		} else if (status == 34) {
			this.field_6759 = 0;
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_6501() {
		return this.field_6762;
	}

	public void method_6497(boolean bl) {
		if (bl) {
			this.field_6759 = 400;
			this.world.sendEntityStatus(this, (byte)11);
		} else {
			this.field_6759 = 0;
			this.world.sendEntityStatus(this, (byte)34);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_IRON_GOLEM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public int method_6502() {
		return this.field_6759;
	}

	public boolean isPlayerCreated() {
		return (this.dataTracker.get(IRON_GOLEM_FLAGS) & 1) != 0;
	}

	public void setPlayerCreated(boolean playerCreated) {
		byte b = this.dataTracker.get(IRON_GOLEM_FLAGS);
		if (playerCreated) {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b & -2));
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
	}

	@Override
	public boolean canSpawn(CollisionView world) {
		BlockPos blockPos = new BlockPos(this);
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = world.getBlockState(blockPos2);
		if (!blockState.hasSolidTopSurface(world, blockPos2, this)) {
			return false;
		} else {
			for (int i = 1; i < 3; i++) {
				BlockPos blockPos3 = blockPos.up(i);
				BlockState blockState2 = world.getBlockState(blockPos3);
				if (!SpawnHelper.isClearForSpawn(world, blockPos3, blockState2, blockState2.getFluidState())) {
					return false;
				}
			}

			return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState()) && world.intersectsEntities(this);
		}
	}
}
