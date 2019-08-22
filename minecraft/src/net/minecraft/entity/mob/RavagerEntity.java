package net.minecraft.entity.mob;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RavagerEntity extends RaiderEntity {
	private static final Predicate<Entity> IS_NOT_RAVAGER = entity -> entity.isAlive() && !(entity instanceof RavagerEntity);
	private int attackTick;
	private int stunTick;
	private int roarTick;

	public RavagerEntity(EntityType<? extends RavagerEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.experiencePoints = 20;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(4, new RavagerEntity.AttackGoal());
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.add(2, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(3, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(4, new FollowTargetGoal(this, AbstractTraderEntity.class, true));
		this.targetSelector.add(4, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void method_20417() {
		boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity) || this.getPrimaryPassenger().getType().isTaggedWith(EntityTypeTags.RAIDERS);
		boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
		this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
		this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
		this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
		this.goalSelector.setControlEnabled(Goal.Control.TARGET, bl);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(12.0);
		this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("AttackTick", this.attackTick);
		compoundTag.putInt("StunTick", this.stunTick);
		compoundTag.putInt("RoarTick", this.roarTick);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.attackTick = compoundTag.getInt("AttackTick");
		this.stunTick = compoundTag.getInt("StunTick");
		this.roarTick = compoundTag.getInt("RoarTick");
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_RAVAGER_CELEBRATE;
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new RavagerEntity.class_1586(this, world);
	}

	@Override
	public int method_5986() {
		return 45;
	}

	@Override
	public double getMountedHeightOffset() {
		return 2.1;
	}

	@Override
	public boolean canBeControlledByRider() {
		return !this.isAiDisabled() && this.getPrimaryPassenger() instanceof LivingEntity;
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isAlive()) {
			if (this.isImmobile()) {
				this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
			} else {
				double d = this.getTarget() != null ? 0.35 : 0.3;
				double e = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
				this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
			}

			if (this.horizontalCollision && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
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
					BlockState blockState = this.world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (block instanceof LeavesBlock) {
						bl = this.world.breakBlock(blockPos, true) || bl;
					}
				}

				if (!bl && this.onGround) {
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
			double d = this.x - (double)this.getWidth() * Math.sin((double)(this.bodyYaw * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			double e = this.y + (double)this.getHeight() - 0.3;
			double f = this.z + (double)this.getWidth() * Math.cos((double)(this.bodyYaw * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d, e, f, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
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
	protected void knockback(LivingEntity livingEntity) {
		if (this.roarTick == 0) {
			if (this.random.nextDouble() < 0.5) {
				this.stunTick = 40;
				this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
				this.world.sendEntityStatus(this, (byte)39);
				livingEntity.pushAwayFrom(this);
			} else {
				this.knockBack(livingEntity);
			}

			livingEntity.velocityModified = true;
		}
	}

	private void roar() {
		if (this.isAlive()) {
			for (Entity entity : this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(4.0), IS_NOT_RAVAGER)) {
				if (!(entity instanceof IllagerEntity)) {
					entity.damage(DamageSource.mob(this), 6.0F);
				}

				this.knockBack(entity);
			}

			Vec3d vec3d = this.getBoundingBox().getCenter();

			for (int i = 0; i < 40; i++) {
				double d = this.random.nextGaussian() * 0.2;
				double e = this.random.nextGaussian() * 0.2;
				double f = this.random.nextGaussian() * 0.2;
				this.world.addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
			}
		}
	}

	private void knockBack(Entity entity) {
		double d = entity.x - this.x;
		double e = entity.z - this.z;
		double f = Math.max(d * d + e * e, 0.001);
		entity.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 4) {
			this.attackTick = 10;
			this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		} else if (b == 39) {
			this.stunTick = 40;
		}

		super.handleStatus(b);
	}

	@Environment(EnvType.CLIENT)
	public int getAttackTick() {
		return this.attackTick;
	}

	@Environment(EnvType.CLIENT)
	public int getStunTick() {
		return this.stunTick;
	}

	@Environment(EnvType.CLIENT)
	public int getRoarTick() {
		return this.roarTick;
	}

	@Override
	public boolean tryAttack(Entity entity) {
		this.attackTick = 10;
		this.world.sendEntityStatus(this, (byte)4);
		this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
		return super.tryAttack(entity);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_RAVAGER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean canSpawn(ViewableWorld viewableWorld) {
		return !viewableWorld.intersectsFluid(this.getBoundingBox());
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	@Override
	public boolean canLead() {
		return false;
	}

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(RavagerEntity.this, 1.0, true);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
			float f = RavagerEntity.this.getWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + livingEntity.getWidth());
		}
	}

	static class class_1586 extends MobNavigation {
		public class_1586(MobEntity mobEntity, World world) {
			super(mobEntity, world);
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int i) {
			this.nodeMaker = new RavagerEntity.class_1587();
			return new PathNodeNavigator(this.nodeMaker, i);
		}
	}

	static class class_1587 extends LandPathNodeMaker {
		private class_1587() {
		}

		@Override
		protected PathNodeType method_61(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
			return pathNodeType == PathNodeType.LEAVES ? PathNodeType.OPEN : super.method_61(blockView, bl, bl2, blockPos, pathNodeType);
		}
	}
}
