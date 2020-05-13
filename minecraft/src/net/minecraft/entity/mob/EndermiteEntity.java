package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.13F;
	}

	public static DefaultAttributeContainer.Builder createEndermiteAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ENDERMITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMITE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.lifeTime = tag.getInt("Lifetime");
		this.playerSpawned = tag.getBoolean("PlayerSpawned");
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Lifetime", this.lifeTime);
		tag.putBoolean("PlayerSpawned", this.playerSpawned);
	}

	@Override
	public void tick() {
		this.bodyYaw = this.yaw;
		super.tick();
	}

	@Override
	public void setYaw(float yaw) {
		this.yaw = yaw;
		super.setYaw(yaw);
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	public boolean isPlayerSpawned() {
		return this.playerSpawned;
	}

	public void setPlayerSpawned(boolean playerSpawned) {
		this.playerSpawned = playerSpawned;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleTypes.PORTAL,
						this.getParticleX(0.5),
						this.getRandomBodyY(),
						this.getParticleZ(0.5),
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

	public static boolean canSpawn(EntityType<EndermiteEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random)) {
			PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0, true);
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
