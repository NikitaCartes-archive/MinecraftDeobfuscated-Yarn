package net.minecraft.entity.mob;

import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreezeEntity extends HostileEntity {
	private static final int field_47271 = 20;
	private static final int field_47272 = 1;
	private static final int field_47273 = 20;
	private static final int field_47274 = 3;
	private static final int field_47275 = 5;
	private static final int field_47276 = 10;
	private static final float field_47278 = 3.0F;
	private static final int field_47813 = 1;
	private static final int field_47814 = 80;
	public AnimationState field_47269 = new AnimationState();
	public AnimationState slidingAnimationState = new AnimationState();
	public AnimationState field_47816 = new AnimationState();
	public AnimationState inhalingAnimationState = new AnimationState();
	public AnimationState shootingAnimationState = new AnimationState();
	public AnimationState field_47270 = new AnimationState();
	private int longJumpingParticleAddCount = 0;
	private int ticksUntilWhirlSound = 0;
	private static final ProjectileDeflection PROJECTILE_DEFLECTOR = (projectile, hitEntity, random) -> {
		hitEntity.getWorld().playSoundFromEntity(null, hitEntity, SoundEvents.ENTITY_BREEZE_DEFLECT, hitEntity.getSoundCategory(), 1.0F, 1.0F);
		ProjectileDeflection.SIMPLE.deflect(projectile, hitEntity, random);
	};

	public static DefaultAttributeContainer.Builder createBreezeAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.63F)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
	}

	public BreezeEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
		this.experiencePoints = 10;
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return BreezeBrain.create(this, this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<BreezeEntity> getBrain() {
		return (Brain<BreezeEntity>)super.getBrain();
	}

	@Override
	protected Brain.Profile<BreezeEntity> createBrainProfile() {
		return Brain.createProfile(BreezeBrain.MEMORY_MODULES, BreezeBrain.SENSORS);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (this.getWorld().isClient() && POSE.equals(data)) {
			this.stopAnimations();
			EntityPose entityPose = this.getPose();
			switch (entityPose) {
				case SHOOTING:
					this.shootingAnimationState.startIfNotRunning(this.age);
					break;
				case INHALING:
					this.inhalingAnimationState.startIfNotRunning(this.age);
					break;
				case SLIDING:
					this.slidingAnimationState.startIfNotRunning(this.age);
			}
		}

		super.onTrackedDataSet(data);
	}

	private void stopAnimations() {
		this.shootingAnimationState.stop();
		this.field_47269.stop();
		this.field_47270.stop();
		this.inhalingAnimationState.stop();
	}

	@Override
	public void tick() {
		EntityPose entityPose = this.getPose();
		switch (entityPose) {
			case SHOOTING:
			case INHALING:
			case STANDING:
				this.resetLongJumpingParticleAddCount().addBlockParticles(1 + this.getRandom().nextInt(1));
				break;
			case SLIDING:
				this.addBlockParticles(20);
				break;
			case LONG_JUMPING:
				this.addLongJumpingParticles();
		}

		if (entityPose != EntityPose.SLIDING && this.slidingAnimationState.isRunning()) {
			this.field_47816.start(this.age);
			this.slidingAnimationState.stop();
		}

		this.ticksUntilWhirlSound = this.ticksUntilWhirlSound == 0 ? this.random.nextBetween(1, 80) : this.ticksUntilWhirlSound - 1;
		if (this.ticksUntilWhirlSound == 0) {
			this.playWhirlSound();
		}

		super.tick();
	}

	public BreezeEntity resetLongJumpingParticleAddCount() {
		this.longJumpingParticleAddCount = 0;
		return this;
	}

	public void addLongJumpingParticles() {
		if (++this.longJumpingParticleAddCount <= 5) {
			BlockState blockState = !this.getBlockStateAtPos().isAir() ? this.getBlockStateAtPos() : this.getSteppingBlockState();
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = this.getPos().add(vec3d).add(0.0, 0.1F, 0.0);

			for (int i = 0; i < 3; i++) {
				this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d2.x, vec3d2.y, vec3d2.z, 0.0, 0.0, 0.0);
			}
		}
	}

	public void addBlockParticles(int count) {
		if (!this.hasVehicle()) {
			Vec3d vec3d = this.getBoundingBox().getCenter();
			Vec3d vec3d2 = new Vec3d(vec3d.x, this.getPos().y, vec3d.z);
			BlockState blockState = !this.getBlockStateAtPos().isAir() ? this.getBlockStateAtPos() : this.getSteppingBlockState();
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				for (int i = 0; i < count; i++) {
					this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d2.x, vec3d2.y, vec3d2.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	public void playAmbientSound() {
		if (this.getTarget() == null || !this.isOnGround()) {
			this.getWorld().playSoundFromEntity(this, this.getAmbientSound(), this.getSoundCategory(), 1.0F, 1.0F);
		}
	}

	public void playWhirlSound() {
		float f = 0.7F + 0.4F * this.random.nextFloat();
		float g = 0.8F + 0.2F * this.random.nextFloat();
		this.getWorld().playSoundFromEntity(this, SoundEvents.ENTITY_BREEZE_WHIRL, this.getSoundCategory(), g, f);
	}

	@Override
	public ProjectileDeflection getProjectileDeflection(ProjectileEntity projectile) {
		if (projectile.getType() != EntityType.BREEZE_WIND_CHARGE && projectile.getType() != EntityType.WIND_CHARGE) {
			return this.getType().isIn(EntityTypeTags.DEFLECTS_PROJECTILES) ? PROJECTILE_DEFLECTOR : ProjectileDeflection.NONE;
		} else {
			return ProjectileDeflection.NONE;
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BREEZE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BREEZE_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isOnGround() ? SoundEvents.ENTITY_BREEZE_IDLE_GROUND : SoundEvents.ENTITY_BREEZE_IDLE_AIR;
	}

	public Optional<LivingEntity> getHurtBy() {
		return this.getBrain()
			.getOptionalRegisteredMemory(MemoryModuleType.HURT_BY)
			.map(DamageSource::getAttacker)
			.filter(attacker -> attacker instanceof LivingEntity)
			.map(livingAttacker -> (LivingEntity)livingAttacker);
	}

	public boolean isWithinShortRange(Vec3d pos) {
		Vec3d vec3d = this.getBlockPos().toCenterPos();
		return pos.isWithinRangeOf(vec3d, 4.0, 10.0);
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("breezeBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().swap("breezeActivityUpdate");
		BreezeBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
		DebugInfoSender.sendBreezeDebugData(this);
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		return type == EntityType.PLAYER || type == EntityType.IRON_GOLEM;
	}

	@Override
	public int getMaxHeadRotation() {
		return 30;
	}

	@Override
	public int getMaxLookYawChange() {
		return 25;
	}

	public double getChargeY() {
		return this.getEyeY() - 0.4;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return damageSource.getAttacker() instanceof BreezeEntity || super.isInvulnerableTo(damageSource);
	}

	@Override
	public double getSwimHeight() {
		return (double)this.getStandingEyeHeight();
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (fallDistance > 3.0F) {
			this.playSound(SoundEvents.ENTITY_BREEZE_LAND, 1.0F, 1.0F);
		}

		return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.getTargetInBrain();
	}
}
