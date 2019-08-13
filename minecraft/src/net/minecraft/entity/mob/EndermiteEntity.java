package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
	private int lifeTime;
	private boolean playerSpawned;

	public EndermiteEntity(EntityType<? extends EndermiteEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 3;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
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
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15137;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14582;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15230;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14678, 0.15F, 1.0F);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.lifeTime = compoundTag.getInt("Lifetime");
		this.playerSpawned = compoundTag.getBoolean("PlayerSpawned");
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Lifetime", this.lifeTime);
		compoundTag.putBoolean("PlayerSpawned", this.playerSpawned);
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
	public double getHeightOffset() {
		return 0.1;
	}

	public boolean isPlayerSpawned() {
		return this.playerSpawned;
	}

	public void setPlayerSpawned(boolean bl) {
		this.playerSpawned = bl;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleTypes.field_11214,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight(),
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		} else {
			if (!this.isPersistent()) {
				this.lifeTime++;
			}

			if (this.lifeTime >= 2400) {
				this.remove();
			}
		}
	}

	public static boolean method_20674(EntityType<EndermiteEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
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
}
