package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.CelebrateRaidWinTask;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
	private int field_44103 = 0;
	public static final TrackedData<Integer> BLOAT_LEVEL = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean field_44104 = false;

	public CowEntity(EntityType<? extends CowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.WHEAT), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0, () -> !this.isAirBlocksApproved()));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	protected boolean isAirBlocksApproved() {
		return class_8293.field_43539.method_50116();
	}

	public int getBloatLevel() {
		return this.dataTracker.get(BLOAT_LEVEL);
	}

	public void setBloatLevel(int bloatLevel) {
		this.dataTracker.set(BLOAT_LEVEL, bloatLevel);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("BloatLevel", this.getBloatLevel());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setBloatLevel(nbt.getInt("BloatLevel"));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BLOAT_LEVEL, 0);
	}

	@Override
	public void onStopWandering() {
		float f = (float)this.getBloatLevel() / 100.0F;
		if (this.getRandom().nextFloat() < f) {
			this.floatUpward();
		}
	}

	private void floatUpward() {
		if (this.onGround && !this.world.isClient) {
			int i = this.getBloatLevel();
			double d = this.world.getGravityModifier();
			if (d == 0.1) {
				if (this.world.isTheMoon()) {
					this.addVelocity(new Vec3d(0.0, (double)((float)i / 46.0F), 0.0));
				} else {
					this.addVelocity(new Vec3d(0.0, (double)((float)i / 6.9F), 0.0));
				}
			} else {
				this.addVelocity(new Vec3d(0.0, (double)((float)i / 4.8F), 0.0));
			}

			int j = (int)((float)i * this.random.nextFloat() / 2.0F);
			this.field_44103 = i - j;
		}
	}

	@Override
	public void tickMovement() {
		if (this.world instanceof ServerWorld serverWorld) {
			if (this.isAirBlocksApproved() && this.getY() > 685.0) {
				this.burst();
				return;
			}

			if (this.field_44103 > 0) {
				this.field_44103--;
				float f = this.getDimensions(EntityPose.STANDING).width / 2.0F;
				serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 10, (double)f, 0.0, (double)f, 0.1);
				this.playSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value(), 0.4F, 0.7F + this.random.nextFloat() * 0.6F);
				this.setBloatLevel(this.getBloatLevel() - 1);
			}
		}

		super.tickMovement();
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BLOAT_LEVEL.equals(data)) {
			this.calculateDimensions();
			this.setBoundingBox(this.calculateBoundingBox());
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getDimensions(EntityPose.STANDING).height - 0.25;
	}

	public float getBloatScale() {
		return 1.0F + (float)this.getBloatLevel() / 40.0F;
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return this.isAirBlocksApproved() ? null : super.getControllingPassenger();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityType.COW.getDimensions().scaled(this.getScaleFactor() * this.getBloatScale());
	}

	private void burst() {
		this.field_44104 = true;
		if (!this.world.isClient) {
			int[] is = new int[20];

			for (int i = 0; i < 20; i++) {
				int j = 161;
				int k = 179;
				int l = 123;
				j -= (int)(this.random.nextFloat() * 32.0F);
				k -= (int)(this.random.nextFloat() * 22.0F);
				l -= (int)(this.random.nextFloat() * 32.0F);
				is[i] = j << 16 | k << 8 | l;
			}

			ItemStack itemStack = CelebrateRaidWinTask.createFirework(0, 2 + this.getBloatLevel() / 12, is);
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(this.world, this, this.getX(), this.getEyeY(), this.getZ(), itemStack);
			this.world.spawnEntity(fireworkRocketEntity);
			this.world.sendEntityStatus(fireworkRocketEntity, EntityStatuses.EXPLODE_FIREWORK_CLIENT);

			for (Entity entity : this.getPassengerList()) {
				if (entity instanceof LivingEntity livingEntity) {
					livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 400, 1, true, false, false));
					livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 1, true, false, false));
				}

				entity.stopRiding();
			}

			fireworkRocketEntity.discard();
		}

		this.kill();
	}

	@Override
	protected void drop(DamageSource source) {
		if (!this.field_44104) {
			super.drop(source);
		}
	}

	public static DefaultAttributeContainer.Builder createCowAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_COW_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_COW_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_COW_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isAirBlocksApproved() && itemStack.isOf(Items.AIR_BLOCK)) {
			int i = this.getBloatLevel();
			if (i < 100) {
				this.setBloatLevel(i + 10);
				player.getStackInHand(hand).decrement(1);
				this.playSound(SoundEvents.ENTITY_CAT_HISS, 1.0F, 0.5F);
				return ActionResult.SUCCESS;
			}
		}

		if (!class_8293.field_43575.method_50116() && itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
			player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
			ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
			player.setStackInHand(hand, itemStack2);
			return ActionResult.success(this.world.isClient);
		} else if (this.isAirBlocksApproved() && hand == Hand.MAIN_HAND && this.getPassengerList().isEmpty()) {
			player.startRiding(this);
			return ActionResult.SUCCESS;
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Nullable
	public CowEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.COW.create(serverWorld);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return !this.isBaby() && !this.isAirBlocksApproved() ? 1.3F : dimensions.height * 0.95F;
	}
}
