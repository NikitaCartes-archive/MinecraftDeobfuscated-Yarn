package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class OcelotEntity extends AnimalEntity {
	public static final double field_30340 = 0.6;
	public static final double field_30341 = 0.8;
	public static final double field_30342 = 1.33;
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
	private static final TrackedData<Boolean> TRUSTING = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private OcelotEntity.FleeGoal<PlayerEntity> fleeGoal;
	private OcelotEntity.OcelotTemptGoal temptGoal;

	public OcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
		super(entityType, world);
		this.updateFleeing();
	}

	boolean isTrusting() {
		return this.dataTracker.get(TRUSTING);
	}

	private void setTrusting(boolean trusting) {
		this.dataTracker.set(TRUSTING, trusting);
		this.updateFleeing();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Trusting", this.isTrusting());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setTrusting(nbt.getBoolean("Trusting"));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TRUSTING, false);
	}

	@Override
	protected void initGoals() {
		this.temptGoal = new OcelotEntity.OcelotTemptGoal(this, 0.6, TAMING_INGREDIENT, true);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, this.temptGoal);
		this.goalSelector.add(7, new PounceAtTargetGoal(this, 0.3F));
		this.goalSelector.add(8, new AttackGoal(this));
		this.goalSelector.add(9, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(10, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
		this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.targetSelector.add(1, new FollowTargetGoal(this, ChickenEntity.class, false));
		this.targetSelector.add(1, new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	public void mobTick() {
		if (this.getMoveControl().isMoving()) {
			double d = this.getMoveControl().getSpeed();
			if (d == 0.6) {
				this.setPose(EntityPose.CROUCHING);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(true);
			} else {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(false);
			}
		} else {
			this.setPose(EntityPose.STANDING);
			this.setSprinting(false);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isTrusting() && this.age > 2400;
	}

	public static DefaultAttributeContainer.Builder createOcelotAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_OCELOT_AMBIENT;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 900;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_OCELOT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_OCELOT_DEATH;
	}

	private float getAttackDamage() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	public boolean tryAttack(Entity target) {
		return target.damage(DamageSource.mob(this), this.getAttackDamage());
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if ((this.temptGoal == null || this.temptGoal.isActive()) && !this.isTrusting() && this.isBreedingItem(itemStack) && player.squaredDistanceTo(this) < 9.0) {
			this.eat(player, hand, itemStack);
			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setTrusting(true);
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, EntityStatuses.TAME_OCELOT_SUCCESS);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, EntityStatuses.TAME_OCELOT_FAILED);
				}
			}

			return ActionResult.success(this.world.isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.TAME_OCELOT_SUCCESS) {
			this.showEmoteParticle(true);
		} else if (status == EntityStatuses.TAME_OCELOT_FAILED) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(status);
		}
	}

	private void showEmoteParticle(boolean positive) {
		ParticleEffect particleEffect = ParticleTypes.HEART;
		if (!positive) {
			particleEffect = ParticleTypes.SMOKE;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
		}
	}

	protected void updateFleeing() {
		if (this.fleeGoal == null) {
			this.fleeGoal = new OcelotEntity.FleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.fleeGoal);
		if (!this.isTrusting()) {
			this.goalSelector.add(4, this.fleeGoal);
		}
	}

	public OcelotEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.OCELOT.create(serverWorld);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return TAMING_INGREDIENT.test(stack);
	}

	public static boolean canSpawn(EntityType<OcelotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return random.nextInt(3) != 0;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		if (world.intersectsEntities(this) && !world.containsFluid(this.getBoundingBox())) {
			BlockPos blockPos = this.getBlockPos();
			if (blockPos.getY() < world.getSeaLevel()) {
				return false;
			}

			BlockState blockState = world.getBlockState(blockPos.down());
			if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isIn(BlockTags.LEAVES)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(1.0F);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.5F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	@Override
	public boolean bypassesSteppingEffects() {
		return this.getPose() == EntityPose.CROUCHING || super.bypassesSteppingEffects();
	}

	static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final OcelotEntity ocelot;

		public FleeGoal(OcelotEntity ocelot, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
			super(ocelot, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.ocelot = ocelot;
		}

		@Override
		public boolean canStart() {
			return !this.ocelot.isTrusting() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.ocelot.isTrusting() && super.shouldContinue();
		}
	}

	static class OcelotTemptGoal extends TemptGoal {
		private final OcelotEntity ocelot;

		public OcelotTemptGoal(OcelotEntity ocelot, double speed, Ingredient food, boolean canBeScared) {
			super(ocelot, speed, food, canBeScared);
			this.ocelot = ocelot;
		}

		@Override
		protected boolean canBeScared() {
			return super.canBeScared() && !this.ocelot.isTrusting();
		}
	}
}
