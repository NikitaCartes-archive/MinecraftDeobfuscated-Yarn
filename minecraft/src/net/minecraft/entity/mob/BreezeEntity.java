package net.minecraft.entity.mob;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
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
	public AnimationState field_47269 = new AnimationState();
	public AnimationState slidingAnimationState = new AnimationState();
	public AnimationState inhalingAnimationState = new AnimationState();
	public AnimationState shootingAnimationState = new AnimationState();
	public AnimationState field_47270 = new AnimationState();
	private int longJumpingParticleAddCount = 0;

	public static DefaultAttributeContainer.Builder createBreezeAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6F)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	public BreezeEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return BreezeBrain.create(this.createBrainProfile().deserialize(dynamic));
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
	public boolean canTarget(LivingEntity target) {
		return target.getType() != EntityType.BREEZE && super.canTarget(target);
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
		this.slidingAnimationState.stop();
	}

	@Override
	public void tick() {
		switch (this.getPose()) {
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

		super.tick();
	}

	public BreezeEntity resetLongJumpingParticleAddCount() {
		this.longJumpingParticleAddCount = 0;
		return this;
	}

	public BreezeEntity addGustDustParticles() {
		Vec3d vec3d = this.getPos().add(0.0, 0.1F, 0.0);

		for (int i = 0; i < 20; i++) {
			this.getWorld().addParticle(ParticleTypes.GUST_DUST, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
		}

		return this;
	}

	public void addLongJumpingParticles() {
		if (++this.longJumpingParticleAddCount <= 5) {
			BlockState blockState = this.getWorld().getBlockState(this.getBlockPos().down());
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = this.getPos().add(vec3d).add(0.0, 0.1F, 0.0);

			for (int i = 0; i < 3; i++) {
				this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d2.x, vec3d2.y, vec3d2.z, 0.0, 0.0, 0.0);
			}
		}
	}

	public void addBlockParticles(int count) {
		Vec3d vec3d = this.getBoundingBox().getCenter();
		Vec3d vec3d2 = new Vec3d(vec3d.x, this.getPos().y, vec3d.z);
		BlockState blockState = this.getWorld().getBlockState(this.getBlockPos().down());
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			for (int i = 0; i < count; i++) {
				this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d2.x, vec3d2.y, vec3d2.z, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public void playAmbientSound() {
		this.getWorld().playSoundFromEntity(this, this.getAmbientSound(), this.getSoundCategory(), 1.0F, 1.0F);
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

	public boolean isWithinLargeRange(Vec3d pos) {
		Vec3d vec3d = this.getBlockPos().toCenterPos();
		return pos.isWithinRangeOf(vec3d, 20.0, 10.0) && !pos.isWithinRangeOf(vec3d, 8.0, 10.0);
	}

	public boolean isWithinMediumRange(Vec3d pos) {
		Vec3d vec3d = this.getBlockPos().toCenterPos();
		return pos.isWithinRangeOf(vec3d, 8.0, 10.0) && !pos.isWithinRangeOf(vec3d, 4.0, 10.0);
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
		return type == EntityType.PLAYER;
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
		return damageSource.isIn(DamageTypeTags.BREEZE_IMMUNE_TO) || damageSource.getAttacker() instanceof BreezeEntity || super.isInvulnerableTo(damageSource);
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
}
