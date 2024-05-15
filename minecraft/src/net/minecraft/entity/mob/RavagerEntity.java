package net.minecraft.entity.mob;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class RavagerEntity extends RaiderEntity {
	private static final Predicate<Entity> IS_NOT_RAVAGER = entity -> entity.isAlive() && !(entity instanceof RavagerEntity);
	private static final double field_30480 = 0.3;
	private static final double field_30481 = 0.35;
	private static final int field_30482 = 8356754;
	private static final float STUNNED_PARTICLE_BLUE = 0.57254905F;
	private static final float STUNNED_PARTICLE_GREEN = 0.5137255F;
	private static final float STUNNED_PARTICLE_RED = 0.49803922F;
	private static final int field_30486 = 10;
	public static final int field_30479 = 40;
	private int attackTick;
	private int stunTick;
	private int roarTick;

	public RavagerEntity(EntityType<? extends RavagerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 20;
		this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0F);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.add(2, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(4, new ActiveTargetGoal(this, MerchantEntity.class, true, entity -> !entity.isBaby()));
		this.targetSelector.add(4, new ActiveTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void updateGoalControls() {
		boolean bl = !(this.getControllingPassenger() instanceof MobEntity) || this.getControllingPassenger().getType().isIn(EntityTypeTags.RAIDERS);
		boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
		this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
		this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
		this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
		this.goalSelector.setControlEnabled(Goal.Control.TARGET, bl);
	}

	public static DefaultAttributeContainer.Builder createRavagerAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
			.add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("AttackTick", this.attackTick);
		nbt.putInt("StunTick", this.stunTick);
		nbt.putInt("RoarTick", this.roarTick);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.attackTick = nbt.getInt("AttackTick");
		this.stunTick = nbt.getInt("StunTick");
		this.roarTick = nbt.getInt("RoarTick");
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_RAVAGER_CELEBRATE;
	}

	@Override
	public int getMaxHeadRotation() {
		return 45;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isAlive()) {
			if (this.isImmobile()) {
				this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
			} else {
				double d = this.getTarget() != null ? 0.35 : 0.3;
				double e = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue();
				this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
			}

			if (this.horizontalCollision && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
				boolean bl = false;
				Box box = this.getBoundingBox().expand(0.2);

				for (BlockPos blockPos : BlockPos.iterate(
					MathHelper.floor(box.minX),
					MathHelper.floor(box.minY),
					MathHelper.floor(box.minZ),
					MathHelper.floor(box.maxX),
					MathHelper.floor(box.maxY),
					MathHelper.floor(box.maxZ)
				)) {
					BlockState blockState = this.getWorld().getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (block instanceof LeavesBlock) {
						bl = this.getWorld().breakBlock(blockPos, true, this) || bl;
					}
				}

				if (!bl && this.isOnGround()) {
					this.jump();
				}
			}

			if (this.roarTick > 0) {
				this.roarTick--;
				if (this.roarTick == 10) {
					this.roar();
				}
			}

			if (this.attackTick > 0) {
				this.attackTick--;
			}

			if (this.stunTick > 0) {
				this.stunTick--;
				this.spawnStunnedParticles();
				if (this.stunTick == 0) {
					this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
					this.roarTick = 20;
				}
			}
		}
	}

	private void spawnStunnedParticles() {
		if (this.random.nextInt(6) == 0) {
			double d = this.getX() - (double)this.getWidth() * Math.sin((double)(this.bodyYaw * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			double e = this.getY() + (double)this.getHeight() - 0.3;
			double f = this.getZ() + (double)this.getWidth() * Math.cos((double)(this.bodyYaw * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.49803922F, 0.5137255F, 0.57254905F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
	}

	@Override
	public boolean canSee(Entity entity) {
		return this.stunTick <= 0 && this.roarTick <= 0 ? super.canSee(entity) : false;
	}

	@Override
	protected void knockback(LivingEntity target) {
		if (this.roarTick == 0) {
			if (this.random.nextDouble() < 0.5) {
				this.stunTick = 40;
				this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
				this.getWorld().sendEntityStatus(this, EntityStatuses.STUN_RAVAGER);
				target.pushAwayFrom(this);
			} else {
				this.knockBack(target);
			}

			target.velocityModified = true;
		}
	}

	private void roar() {
		if (this.isAlive()) {
			for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0), IS_NOT_RAVAGER)) {
				if (!(livingEntity instanceof IllagerEntity)) {
					livingEntity.damage(this.getDamageSources().mobAttack(this), 6.0F);
				}

				this.knockBack(livingEntity);
			}

			Vec3d vec3d = this.getBoundingBox().getCenter();

			for (int i = 0; i < 40; i++) {
				double d = this.random.nextGaussian() * 0.2;
				double e = this.random.nextGaussian() * 0.2;
				double f = this.random.nextGaussian() * 0.2;
				this.getWorld().addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
			}

			this.emitGameEvent(GameEvent.ENTITY_ACTION);
		}
	}

	private void knockBack(Entity entity) {
		double d = entity.getX() - this.getX();
		double e = entity.getZ() - this.getZ();
		double f = Math.max(d * d + e * e, 0.001);
		entity.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.attackTick = 10;
			this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		} else if (status == EntityStatuses.STUN_RAVAGER) {
			this.stunTick = 40;
		}

		super.handleStatus(status);
	}

	public int getAttackTick() {
		return this.attackTick;
	}

	public int getStunTick() {
		return this.stunTick;
	}

	public int getRoarTick() {
		return this.roarTick;
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.attackTick = 10;
		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		return super.tryAttack(target);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_RAVAGER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return !world.containsFluid(this.getBoundingBox());
	}

	@Override
	public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
	}

	@Override
	public boolean canLead() {
		return false;
	}

	@Override
	protected Box getAttackBox() {
		Box box = super.getAttackBox();
		return box.contract(0.05, 0.0, 0.05);
	}
}
