package net.minecraft.entity.mob;

import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class CreakingEntity extends HostileEntity {
	private static final TrackedData<Boolean> UNROOTED = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> ACTIVE = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int field_54573 = 20;
	private static final int field_54574 = 1;
	private static final float field_54575 = 2.0F;
	private static final float field_54576 = 32.0F;
	private static final float field_54577 = 144.0F;
	public static final int field_54566 = 40;
	private static final float field_54578 = 0.3F;
	public static final float field_54567 = 0.2F;
	public static final int field_54569 = 16545810;
	public static final int field_54580 = 6250335;
	private int attackAnimationTimer;
	public final AnimationState attackAnimationState = new AnimationState();
	public final AnimationState invulnerableAnimationState = new AnimationState();

	public CreakingEntity(EntityType<? extends CreakingEntity> entityType, World world) {
		super(entityType, world);
		this.lookControl = new CreakingEntity.CreakingLookControl(this);
		this.moveControl = new CreakingEntity.CreakingMoveControl(this);
		this.jumpControl = new CreakingEntity.CreakingJumpControl(this);
		MobNavigation mobNavigation = (MobNavigation)this.getNavigation();
		mobNavigation.setCanSwim(true);
		this.experiencePoints = 0;
	}

	@Override
	protected BodyControl createBodyControl() {
		return new CreakingEntity.CreakingBodyControl(this);
	}

	@Override
	protected Brain.Profile<CreakingEntity> createBrainProfile() {
		return CreakingBrain.createBrainProfile();
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return CreakingBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(UNROOTED, true);
		builder.add(ACTIVE, false);
	}

	public static DefaultAttributeContainer.Builder createCreakingAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MAX_HEALTH, 1.0)
			.add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.ATTACK_DAMAGE, 2.0)
			.add(EntityAttributes.FOLLOW_RANGE, 32.0)
			.add(EntityAttributes.STEP_HEIGHT, 1.0);
	}

	public boolean isUnrooted() {
		return this.dataTracker.get(UNROOTED);
	}

	@Override
	public boolean tryAttack(ServerWorld world, Entity target) {
		if (!(target instanceof LivingEntity)) {
			return false;
		} else {
			this.attackAnimationTimer = 20;
			this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
			return super.tryAttack(world, target);
		}
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && this.isUnrooted();
	}

	@Override
	public Brain<CreakingEntity> getBrain() {
		return (Brain<CreakingEntity>)super.getBrain();
	}

	@Override
	protected void mobTick(ServerWorld world) {
		Profiler profiler = Profilers.get();
		profiler.push("creakingBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		profiler.pop();
		CreakingBrain.updateActivities(this);
	}

	@Override
	public void tickMovement() {
		if (this.attackAnimationTimer > 0) {
			this.attackAnimationTimer--;
		}

		if (!this.getWorld().isClient) {
			boolean bl = this.dataTracker.get(UNROOTED);
			boolean bl2 = this.shouldBeUnrooted();
			if (bl2 != bl) {
				this.emitGameEvent(GameEvent.ENTITY_ACTION);
				if (bl2) {
					this.playSound(SoundEvents.ENTITY_CREAKING_UNFREEZE);
				} else {
					this.stopMovement();
					this.playSound(SoundEvents.ENTITY_CREAKING_FREEZE);
				}
			}

			this.dataTracker.set(UNROOTED, bl2);
		}

		super.tickMovement();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			this.tickAttackAnimation();
		}
	}

	private void tickAttackAnimation() {
		this.attackAnimationState.setRunning(this.attackAnimationTimer > 0, this.age);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.attackAnimationTimer = 20;
			this.playAttackSound();
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void playAttackSound() {
		this.playSound(SoundEvents.ENTITY_CREAKING_ATTACK);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isActive() ? null : SoundEvents.ENTITY_CREAKING_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CREAKING_SWAY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CREAKING_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_CREAKING_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.getTargetInBrain();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public void takeKnockback(double strength, double x, double z) {
		if (this.isUnrooted()) {
			super.takeKnockback(strength, x, z);
		}
	}

	public boolean shouldBeUnrooted() {
		List<PlayerEntity> list = (List<PlayerEntity>)this.brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
		if (list.isEmpty()) {
			if (this.isActive()) {
				this.emitGameEvent(GameEvent.ENTITY_ACTION);
				this.playSound(SoundEvents.ENTITY_CREAKING_DEACTIVATE);
				this.setActive(false);
			}

			return true;
		} else {
			Predicate<LivingEntity> predicate = this.isActive() ? LivingEntity.NOT_WEARING_GAZE_DISGUISE_PREDICATE : entity -> true;

			for (PlayerEntity playerEntity : list) {
				if (this.isEntityLookingAtMe(
					playerEntity, 0.5, false, true, predicate, new DoubleSupplier[]{this::getEyeY, this::getY, () -> (this.getEyeY() + this.getY()) / 2.0}
				)) {
					if (this.isActive()) {
						return false;
					}

					if (playerEntity.squaredDistanceTo(this) < 144.0) {
						this.emitGameEvent(GameEvent.ENTITY_ACTION);
						this.playSound(SoundEvents.ENTITY_CREAKING_ACTIVATE);
						this.setActive(true);
						return false;
					}
				}
			}

			return true;
		}
	}

	public void setActive(boolean active) {
		this.dataTracker.set(ACTIVE, active);
	}

	public boolean isActive() {
		return this.dataTracker.get(ACTIVE);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	class CreakingBodyControl extends BodyControl {
		public CreakingBodyControl(final CreakingEntity creaking) {
			super(creaking);
		}

		@Override
		public void tick() {
			if (CreakingEntity.this.isUnrooted()) {
				super.tick();
			}
		}
	}

	class CreakingJumpControl extends JumpControl {
		public CreakingJumpControl(final CreakingEntity creaking) {
			super(creaking);
		}

		@Override
		public void tick() {
			if (CreakingEntity.this.isUnrooted()) {
				super.tick();
			} else {
				CreakingEntity.this.setJumping(false);
			}
		}
	}

	class CreakingLookControl extends LookControl {
		public CreakingLookControl(final CreakingEntity creaking) {
			super(creaking);
		}

		@Override
		public void tick() {
			if (CreakingEntity.this.isUnrooted()) {
				super.tick();
			}
		}
	}

	class CreakingMoveControl extends MoveControl {
		public CreakingMoveControl(final CreakingEntity creaking) {
			super(creaking);
		}

		@Override
		public void tick() {
			if (CreakingEntity.this.isUnrooted()) {
				super.tick();
			}
		}
	}
}
