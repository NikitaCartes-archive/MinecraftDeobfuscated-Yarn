package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class MegaSpudEntity extends PathAwareEntity implements SkinOverlayOwner, Monster {
	private static final TrackedData<Integer> SIZE = DataTracker.registerData(MegaSpudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> field_50436 = DataTracker.registerData(MegaSpudEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Boolean> field_50437 = DataTracker.registerData(MegaSpudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int field_50438 = 1024;
	public float field_50434;
	public float field_50435;
	public float field_50445;
	private boolean wasOnGround;
	private MegaSpudEntity.class_9505 field_50440;
	private final List<MobEntity> field_50441 = new ArrayList();
	private final ServerBossBar bossBar;
	private final List<Runnable> field_50443 = new ArrayList();
	private static final ParticleEffect field_50444 = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.POISONOUS_POTATO));

	public MegaSpudEntity(EntityType<? extends MegaSpudEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new MegaSpudEntity.MegaSpudMoveControl(this);
		this.field_50440 = MegaSpudEntity.class_9505.CHICKEN;
		this.experiencePoints = 50;
		this.bossBar = new ServerBossBar(this, BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.bossBar.setDarkenSky(false);
		this.reinitDimensions();
	}

	@Override
	public void checkDespawn() {
	}

	public static DefaultAttributeContainer.Builder createMegaSpudAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 1024.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
			.add(EntityAttributes.GENERIC_JUMP_STRENGTH, 0.62F)
			.add(EntityAttributes.GENERIC_ARMOR, 5.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(7, new MegaSpudEntity.class_9501(this));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(0, new SwimGoal(this) {
			@Override
			public void tick() {
				if (MegaSpudEntity.this.getRandom().nextFloat() < 0.8F) {
					MegaSpudEntity.this.jump();
				}
			}
		});
		this.goalSelector.add(7, new LookAroundGoal(this));
		this.goalSelector.add(3, new MegaSpudEntity.class_9504(this));
		this.goalSelector.add(5, new MegaSpudEntity.class_9502(this));
		this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, false));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		boolean bl = false;
		if (!this.field_50441.isEmpty()) {
			bl = true;
			this.field_50441.removeIf(mobEntity -> mobEntity.isRemoved() || mobEntity.isDead());
		}

		if (!this.field_50443.isEmpty()) {
			bl = true;
			if (this.random.nextFloat() < 0.05F) {
				((Runnable)this.field_50443.remove(0)).run();
				if (this.field_50443.isEmpty()) {
					this.playSound(SoundEvents.ENTITY_MEGASPUD_CHALLENGE, this.getSoundVolume(), 1.0F);
				}
			}
		}

		if (this.field_50441.isEmpty() && this.field_50443.isEmpty()) {
			this.method_58908(false);
			if (bl) {
				this.playSound(SoundEvents.ENTITY_MEGASPUD_UPSET, this.getSoundVolume(), 1.0F);
			}
		}

		if ((!this.hasPositionTarget() || this.getPositionTarget().getSquaredDistance(this.getPos()) > 16384.0) && !this.getBlockPos().equals(BlockPos.ORIGIN)) {
			this.setPositionTarget(this.getBlockPos(), 3);
		}

		MegaSpudEntity.class_9505 lv = this.field_50440.method_58914(this.getHealth());
		if (this.field_50440 != lv) {
			this.setHealth(this.field_50440.method_58917());
			Box box = this.getBoundingBox();
			Vec3d vec3d = this.getPos();
			ItemScatterer.spawn(this.getWorld(), vec3d.x, vec3d.y, vec3d.z, Items.CORRUPTED_POTATO_PEELS.getDefaultStack());

			for (int i = 0; i < 100; i++) {
				Vec3d vec3d2 = vec3d.add(
					(double)this.random.nextBetweenInclusive(-5.0F, 5.0F),
					(double)this.random.nextBetweenInclusive(0.0F, 10.0F),
					(double)this.random.nextBetweenInclusive(-5.0F, 5.0F)
				);
				if (box.contains(vec3d2)) {
					ItemScatterer.spawn(this.getWorld(), vec3d2.x, vec3d2.y, vec3d2.z, Items.CORRUPTED_POTATO_PEELS.getDefaultStack());
				}
			}

			ServerWorld serverWorld = (ServerWorld)this.getWorld();
			Team team = this.getScoreboardTeam();
			this.method_58891(this.field_50440, serverWorld, team);
			MegaSpudEntity.class_9505 lv2 = this.field_50440;

			for (int j = 1; j <= this.field_50440.ordinal(); j++) {
				this.field_50443.add((Runnable)() -> this.method_58891(lv2, serverWorld, team));
			}

			this.field_50440 = lv;
			this.setSize(this.field_50440.field_50469);
			this.method_58908(true);
			this.bossBar.setName(this.getDisplayName());
		}

		this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
		this.bossBar.method_58785(this.getPos(), 64);
	}

	private void method_58891(MegaSpudEntity.class_9505 arg, ServerWorld serverWorld, Team team) {
		BlockPos blockPos = this.getPositionTarget()
			.add(this.random.nextInt(5) - this.random.nextInt(5), this.random.nextInt(5), this.random.nextInt(5) - this.random.nextInt(5));
		MobEntity mobEntity = arg.method_58916().create(this.getWorld());
		if (mobEntity != null) {
			mobEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
			mobEntity.initialize(serverWorld, this.getWorld().getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null);
			if (team != null) {
				serverWorld.getScoreboard().addScoreHolderToTeam(mobEntity.getNameForScoreboard(), team);
			}

			serverWorld.spawnEntityAndPassengers(mobEntity);
			serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Emitter.of(this));
			mobEntity.setPersistent();
			mobEntity.setPositionTarget(this.getPositionTarget(), 8);
			this.playSound(SoundEvents.ENTITY_MEGASPUD_SUMMON, this.getSoundVolume(), 1.0F);
			serverWorld.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB_AT_SPAWN_POS, blockPos, 0);
			serverWorld.spawnParticles(ParticleTypes.TRIAL_SPAWNER_DETECTION, mobEntity.getX(), mobEntity.getBodyY(0.5), mobEntity.getZ(), 100, 0.5, 0.5, 0.5, 0.0);
			this.field_50441.add(mobEntity);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.damage(source, amount);
		} else {
			amount = Math.min(amount, 100.0F);
			if (!this.shouldRenderOverlay()) {
				return super.damage(source, amount);
			} else {
				if (source.getAttacker() instanceof PlayerEntity) {
					for (MobEntity mobEntity : this.field_50441) {
						mobEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200), this);
					}
				}

				if (source.getAttacker() != null && !source.isOf(DamageTypes.THORNS)) {
					source.getAttacker().damage(this.getWorld().getDamageSources().potatoMagic(), amount);
				}

				return false;
			}
		}
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		super.onStartedTrackingBy(player);
		this.bossBar.addPlayer(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		super.onStoppedTrackingBy(player);
		this.bossBar.removePlayer(player);
	}

	@Override
	public boolean isPotato() {
		return true;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SIZE, MegaSpudEntity.class_9505.CHICKEN.field_50469);
		builder.add(field_50436, (byte)0);
		builder.add(field_50437, false);
	}

	public void method_58908(boolean bl) {
		this.dataTracker.set(field_50437, bl);
	}

	@Override
	public boolean isInvulnerable() {
		return super.isInvulnerable() || this.shouldRenderOverlay();
	}

	@VisibleForTesting
	public void setSize(int size) {
		this.dataTracker.set(SIZE, size);
		this.refreshPosition();
		this.calculateDimensions();
	}

	public int getSize() {
		return this.dataTracker.get(SIZE);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Size", this.getSize() - 1);
		nbt.putBoolean("wasOnGround", this.wasOnGround);
		nbt.putInt("homeX", this.getPositionTarget().getX());
		nbt.putInt("homeY", this.getPositionTarget().getY());
		nbt.putInt("homeZ", this.getPositionTarget().getZ());
		if (this.hasCustomName()) {
			this.bossBar.setName(this.getDisplayName());
		}
	}

	@Override
	protected Text getDefaultName() {
		return this.field_50440.method_58918();
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setSize(nbt.getInt("Size") + 1);
		super.readCustomDataFromNbt(nbt);
		this.wasOnGround = nbt.getBoolean("wasOnGround");
		this.setPositionTarget(new BlockPos(nbt.getInt("homeX"), nbt.getInt("homeY"), nbt.getInt("homeZ")), 3);

		while (this.field_50440 != this.field_50440.method_58914(this.getHealth())) {
			this.field_50440 = this.field_50440.method_58913();
		}

		this.bossBar.setName(this.getDisplayName());
	}

	public boolean method_58909() {
		return this.getSize() <= 1;
	}

	protected ParticleEffect method_58903() {
		return field_50444;
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return this.getSize() > 0;
	}

	@Override
	public void tick() {
		this.field_50435 = this.field_50435 + (this.field_50434 - this.field_50435) * 0.5F;
		this.field_50445 = this.field_50435;
		super.tick();
		if (this.isOnGround() && !this.wasOnGround) {
			float f = this.getDimensions(this.getPose()).width() * 2.0F;
			float g = f / 2.0F;

			for (int i = 0; (float)i < f * 16.0F; i++) {
				float h = this.random.nextFloat() * (float) (Math.PI * 2);
				float j = this.random.nextFloat() * 0.5F + 0.5F;
				float k = MathHelper.sin(h) * g * j;
				float l = MathHelper.cos(h) * g * j;
				this.getWorld().addParticle(this.method_58903(), this.getX() + (double)k, this.getY(), this.getZ() + (double)l, 0.0, 0.0, 0.0);
			}

			this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.field_50434 = -0.5F;
		} else if (!this.isOnGround() && this.wasOnGround) {
			this.field_50434 = 1.0F;
		}

		this.wasOnGround = this.isOnGround();
		this.method_58904();
	}

	protected void method_58904() {
		this.field_50434 *= 0.6F;
	}

	protected int method_58896() {
		return this.random.nextInt(20) + 10;
	}

	@Override
	public void calculateDimensions() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.calculateDimensions();
		this.setPosition(d, e, f);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SIZE.equals(data)) {
			this.calculateDimensions();
			this.setYaw(this.headYaw);
			this.bodyYaw = this.headYaw;
			if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
				this.onSwimmingStart();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public EntityType<? extends MegaSpudEntity> getType() {
		return (EntityType<? extends MegaSpudEntity>)super.getType();
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		for (MobEntity mobEntity : this.field_50441) {
			mobEntity.remove(reason);
		}

		super.remove(reason);
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		super.pushAwayFrom(entity);
		if (entity instanceof IronGolemEntity && this.method_58897()) {
			this.method_58905((LivingEntity)entity);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.method_58897()) {
			this.method_58905(player);
		}
	}

	protected void method_58905(LivingEntity livingEntity) {
		if (this.isAlive()) {
			int i = this.getSize();
			if (this.squaredDistanceTo(livingEntity) < 0.6 * (double)i * 0.6 * (double)i
				&& this.canSee(livingEntity)
				&& livingEntity.damage(this.getDamageSources().mobAttack(this), this.method_58898())) {
				this.applyDamageEffects(this, livingEntity);
			}
		}
	}

	@Override
	protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vec3d(0.0, (double)dimensions.height() - 0.015625 * (double)this.getSize() * (double)scaleFactor, 0.0);
	}

	protected boolean method_58897() {
		return !this.method_58909() && this.canMoveVoluntarily();
	}

	protected float method_58898() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_MEGASPUD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_MEGASPUD_DEATH;
	}

	protected SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_MEGASPUD_JUMP_HI;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_MEGASPUD_IDLE;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F + 0.4F * (float)this.getSize();
	}

	@Override
	public int getMaxLookPitchChange() {
		return 0;
	}

	protected boolean method_58900() {
		return this.getSize() > 0;
	}

	@Override
	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		Vec3d vec3d2 = this.getRotationVector();
		float f = this.getJumpVelocity();
		float g = this.isInWalkTargetRange() ? 0.0F : f;
		this.setVelocity(vec3d.x + vec3d2.x * (double)g, (double)f, vec3d.z + vec3d2.z * (double)g);
		this.velocityDirty = true;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		this.setSize(MegaSpudEntity.class_9505.CHICKEN.field_50469);
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	public boolean shouldRenderOverlay() {
		return this.dataTracker.get(field_50437);
	}

	float method_58902() {
		float f = this.method_58909() ? 1.4F : 0.8F;
		return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
	}

	protected SoundEvent method_58901() {
		return this.method_58909() ? SoundEvents.ENTITY_MEGASPUD_JUMP_HI : SoundEvents.ENTITY_MEGASPUD_JUMP;
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return super.getBaseDimensions(pose).scaled((float)this.getSize());
	}

	static class MegaSpudMoveControl extends MoveControl {
		private float field_50451;
		private int field_50452;
		private final MegaSpudEntity megaSpud;
		private boolean field_50454;

		public MegaSpudMoveControl(MegaSpudEntity megaSpud) {
			super(megaSpud);
			this.megaSpud = megaSpud;
			this.field_50451 = 180.0F * megaSpud.getYaw() / (float) Math.PI;
		}

		public void method_58911(float f, boolean bl) {
			this.field_50451 = f;
			this.field_50454 = bl;
		}

		public void method_58910(double d) {
			this.speed = d;
			this.state = MoveControl.State.MOVE_TO;
		}

		@Override
		public void tick() {
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.field_50451, 90.0F));
			this.entity.headYaw = this.entity.getYaw();
			this.entity.bodyYaw = this.entity.getYaw();
			if (this.state != MoveControl.State.MOVE_TO) {
				this.entity.setForwardSpeed(0.0F);
			} else {
				this.state = MoveControl.State.WAIT;
				if (this.entity.isOnGround()) {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
					if (this.field_50452-- <= 0) {
						this.field_50452 = this.megaSpud.method_58896();
						if (this.field_50454) {
							this.field_50452 /= 3;
						}

						this.megaSpud.getJumpControl().setActive();
						if (this.megaSpud.method_58900()) {
							this.megaSpud.playSound(this.megaSpud.method_58901(), this.megaSpud.getSoundVolume(), this.megaSpud.method_58902());
						}
					} else {
						this.megaSpud.sidewaysSpeed = 0.0F;
						this.megaSpud.forwardSpeed = 0.0F;
						this.entity.setMovementSpeed(0.0F);
					}
				} else {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
				}
			}
		}
	}

	static class class_9501 extends Goal {
		private final MegaSpudEntity field_50449;
		public int field_50448;

		public class_9501(MegaSpudEntity megaSpudEntity) {
			this.field_50449 = megaSpudEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_50449.getTarget() != null;
		}

		@Override
		public void start() {
			this.field_50448 = 0;
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.field_50449.getTarget();
			if (livingEntity != null) {
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.field_50449) < 4096.0 && this.field_50449.canSee(livingEntity)) {
					World world = this.field_50449.getWorld();
					this.field_50448++;
					if (this.field_50448 == 10 && !this.field_50449.isSilent()) {
						this.field_50449.playSoundIfNotSilent(SoundEvents.ENTITY_MEGASPUD_FIREBALL);
					}

					if (this.field_50448 == 20) {
						Box box = this.field_50449.getBoundingBox().expand(0.5);
						Vec3d vec3d = this.field_50449.getEyePos();
						Vec3d vec3d2 = livingEntity.getEyePos().subtract(vec3d).normalize().multiply(0.1);
						Vec3d vec3d3 = vec3d;

						while (box.contains(vec3d3)) {
							vec3d3 = vec3d3.add(vec3d2);
						}

						FireballEntity fireballEntity = new FireballEntity(world, this.field_50449, vec3d2.x, vec3d2.y, vec3d2.z, 2, false);
						fireballEntity.setPosition(vec3d3);
						world.spawnEntity(fireballEntity);
						this.field_50448 = -40;
					}
				} else if (this.field_50448 > 0) {
					this.field_50448--;
				}
			}
		}
	}

	public static class class_9502 extends Goal {
		private final MobEntity field_50450;

		public class_9502(MobEntity mobEntity) {
			this.field_50450 = mobEntity;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return !this.field_50450.hasVehicle();
		}

		@Override
		public void tick() {
			if (this.field_50450.getMoveControl() instanceof MegaSpudEntity.MegaSpudMoveControl megaSpudMoveControl) {
				megaSpudMoveControl.method_58910(1.0);
			}
		}
	}

	static class class_9504 extends Goal {
		private final MegaSpudEntity field_50455;
		private float field_50456;
		private int field_50457;

		public class_9504(MegaSpudEntity megaSpudEntity) {
			this.field_50455 = megaSpudEntity;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return (
					this.field_50455.isOnGround()
						|| this.field_50455.isTouchingWater()
						|| this.field_50455.isInLava()
						|| this.field_50455.hasStatusEffect(StatusEffects.LEVITATION)
				)
				&& this.field_50455.getMoveControl() instanceof MegaSpudEntity.MegaSpudMoveControl;
		}

		@Override
		public void tick() {
			if (--this.field_50457 <= 0) {
				this.field_50457 = this.getTickCount(40 + this.field_50455.getRandom().nextInt(60));
				if (this.field_50455.getTarget() != null && this.field_50455.random.nextFloat() < 0.4F) {
					this.field_50456 = this.method_58912(this.field_50455.getTarget().getPos()) + 90.0F;
				} else if (this.field_50455.hasPositionTarget() && !this.field_50455.isInWalkTargetRange()) {
					Vec3d vec3d = Vec3d.ofBottomCenter(this.field_50455.getPositionTarget());
					this.field_50456 = this.method_58912(vec3d) + 60.0F;
				} else {
					this.field_50456 = (float)this.field_50455.getRandom().nextInt(360);
				}
			}

			if (this.field_50455.getMoveControl() instanceof MegaSpudEntity.MegaSpudMoveControl megaSpudMoveControl) {
				megaSpudMoveControl.method_58911(this.field_50456 + 20.0F - (float)this.field_50455.random.nextInt(40), false);
			}
		}

		private float method_58912(Vec3d vec3d) {
			return (float)MathHelper.atan2(this.field_50455.getZ() - vec3d.z, this.field_50455.getX() - vec3d.x) * (180.0F / (float)Math.PI);
		}
	}

	static enum class_9505 {
		CHICKEN(10, 1.0F, EntityType.CHICKEN),
		ARMADILLO(9, 0.9F, EntityType.ARMADILLO),
		ZOMBIE(8, 0.8F, EntityType.POISONOUS_POTATO_ZOMBIE),
		SPIDER(7, 0.7F, EntityType.SPIDER),
		STRAY(6, 0.6F, EntityType.STRAY),
		CREEPER(5, 0.5F, EntityType.CREEPER),
		BRUTE(4, 0.4F, EntityType.PIGLIN_BRUTE),
		GHAST(3, 0.3F, EntityType.GHAST),
		PLAGUEWHALE(2, 0.2F, EntityType.PLAGUEWHALE),
		GIANT(1, 0.1F, EntityType.GIANT),
		END(1, -1.0F, EntityType.FROG);

		final int field_50469;
		private final float field_50470;
		private final EntityType<? extends MobEntity> field_50471;
		private Text field_50472 = EntityType.MEGA_SPUD.getName();

		private class_9505(int j, float f, EntityType<? extends MobEntity> entityType) {
			this.field_50469 = j;
			this.field_50470 = f;
			this.field_50471 = entityType;
		}

		public MegaSpudEntity.class_9505 method_58913() {
			int i = this.ordinal() + 1;
			return i >= values().length ? this : values()[i];
		}

		@Nullable
		public MegaSpudEntity.class_9505 method_58915() {
			int i = this.ordinal() - 1;
			return i < 0 ? null : values()[i];
		}

		public EntityType<? extends MobEntity> method_58916() {
			return this.field_50471;
		}

		public float method_58917() {
			return this.field_50470 * 1024.0F;
		}

		public MegaSpudEntity.class_9505 method_58914(float f) {
			return f < this.method_58917() ? this.method_58913() : this;
		}

		public Text method_58918() {
			return this.field_50472;
		}

		static {
			for (MegaSpudEntity.class_9505 lv : values()) {
				MegaSpudEntity.class_9505 lv2 = lv.method_58915();
				if (lv2 == null) {
					lv.field_50472 = EntityType.MEGA_SPUD.getName();
				} else {
					lv.field_50472 = Text.translatable("entity.minecraft.mega_spud." + Registries.ENTITY_TYPE.getId(lv2.method_58916()).getPath());
				}
			}
		}
	}
}
