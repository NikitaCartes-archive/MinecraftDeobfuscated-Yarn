package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class IronGolemEntity extends GolemEntity implements Angerable {
	/**
	 * The tracked flags of iron golems. Only has the {@code 1} bit for whether a
	 * golem is {@linkplain #isPlayerCreated() created by a player}.
	 */
	protected static final TrackedData<Byte> IRON_GOLEM_FLAGS = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int HEALTH_PER_INGOT = 25;
	private int attackTicksLeft;
	private int lookingAtVillagerTicksLeft;
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
	private int angerTime;
	@Nullable
	private UUID angryAt;

	public IronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
		this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
		this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
		this.goalSelector.add(5, new IronGolemLookGoal(this));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
		this.targetSelector
			.add(3, new ActiveTargetGoal(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
		this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(IRON_GOLEM_FLAGS, (byte)0);
	}

	public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
	}

	@Override
	protected int getNextAirUnderwater(int air) {
		return air;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
			this.setTarget((LivingEntity)entity);
		}

		super.pushAway(entity);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.attackTicksLeft > 0) {
			this.attackTicksLeft--;
		}

		if (this.lookingAtVillagerTicksLeft > 0) {
			this.lookingAtVillagerTicksLeft--;
		}

		if (this.getVelocity().horizontalLengthSquared() > 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = MathHelper.floor(this.getX());
			int j = MathHelper.floor(this.getY() - 0.2F);
			int k = MathHelper.floor(this.getZ());
			BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				this.world
					.addParticle(
						new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
						this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						this.getY() + 0.1,
						this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0
					);
			}
		}

		if (!this.world.isClient) {
			this.tickAngerLogic((ServerWorld)this.world, true);
		}
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		if (this.isPlayerCreated() && type == EntityType.PLAYER) {
			return false;
		} else {
			return type == EntityType.CREEPER ? false : super.canTarget(type);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("PlayerCreated", this.isPlayerCreated());
		this.writeAngerToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setPlayerCreated(nbt.getBoolean("PlayerCreated"));
		this.readAngerFromNbt(this.world, nbt);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	@Override
	public void setAngerTime(int ticks) {
		this.angerTime = ticks;
	}

	@Override
	public int getAngerTime() {
		return this.angerTime;
	}

	@Override
	public void setAngryAt(@Nullable UUID uuid) {
		this.angryAt = uuid;
	}

	@Nullable
	@Override
	public UUID getAngryAt() {
		return this.angryAt;
	}

	private float getAttackDamage() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.attackTicksLeft = 10;
		this.world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		float f = this.getAttackDamage();
		float g = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
		boolean bl = target.damage(DamageSource.mob(this), g);
		if (bl) {
			target.setVelocity(target.getVelocity().add(0.0, 0.4F, 0.0));
			this.applyDamageEffects(this, target);
		}

		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return bl;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		IronGolemEntity.Crack crack = this.getCrack();
		boolean bl = super.damage(source, amount);
		if (bl && this.getCrack() != crack) {
			this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		}

		return bl;
	}

	public IronGolemEntity.Crack getCrack() {
		return IronGolemEntity.Crack.from(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.attackTicksLeft = 10;
			this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		} else if (status == EntityStatuses.LOOK_AT_VILLAGER) {
			this.lookingAtVillagerTicksLeft = 400;
		} else if (status == EntityStatuses.STOP_LOOKING_AT_VILLAGER) {
			this.lookingAtVillagerTicksLeft = 0;
		} else {
			super.handleStatus(status);
		}
	}

	public int getAttackTicksLeft() {
		return this.attackTicksLeft;
	}

	public void setLookingAtVillager(boolean lookingAtVillager) {
		if (lookingAtVillager) {
			this.lookingAtVillagerTicksLeft = 400;
			this.world.sendEntityStatus(this, EntityStatuses.LOOK_AT_VILLAGER);
		} else {
			this.lookingAtVillagerTicksLeft = 0;
			this.world.sendEntityStatus(this, EntityStatuses.STOP_LOOKING_AT_VILLAGER);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_IRON_GOLEM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isOf(Items.IRON_INGOT)) {
			return ActionResult.PASS;
		} else {
			float f = this.getHealth();
			this.heal(25.0F);
			if (this.getHealth() == f) {
				return ActionResult.PASS;
			} else {
				float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
				this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
				this.emitGameEvent(GameEvent.MOB_INTERACT, this.getCameraBlockPos());
				if (!player.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				return ActionResult.success(this.world.isClient);
			}
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
	}

	public int getLookingAtVillagerTicks() {
		return this.lookingAtVillagerTicksLeft;
	}

	public boolean isPlayerCreated() {
		return (this.dataTracker.get(IRON_GOLEM_FLAGS) & 1) != 0;
	}

	public void setPlayerCreated(boolean playerCreated) {
		byte b = this.dataTracker.get(IRON_GOLEM_FLAGS);
		if (playerCreated) {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b & -2));
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		BlockPos blockPos = this.getBlockPos();
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = world.getBlockState(blockPos2);
		if (!blockState.hasSolidTopSurface(world, blockPos2, this)) {
			return false;
		} else {
			for (int i = 1; i < 3; i++) {
				BlockPos blockPos3 = blockPos.up(i);
				BlockState blockState2 = world.getBlockState(blockPos3);
				if (!SpawnHelper.isClearForSpawn(world, blockPos3, blockState2, blockState2.getFluidState(), EntityType.IRON_GOLEM)) {
					return false;
				}
			}

			return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState(), EntityType.IRON_GOLEM)
				&& world.intersectsEntities(this);
		}
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.875F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	public static enum Crack {
		NONE(1.0F),
		LOW(0.75F),
		MEDIUM(0.5F),
		HIGH(0.25F);

		private static final List<IronGolemEntity.Crack> VALUES = (List<IronGolemEntity.Crack>)Stream.of(values())
			.sorted(Comparator.comparingDouble(crack -> (double)crack.maxHealthFraction))
			.collect(ImmutableList.toImmutableList());
		private final float maxHealthFraction;

		private Crack(float maxHealthFraction) {
			this.maxHealthFraction = maxHealthFraction;
		}

		public static IronGolemEntity.Crack from(float healthFraction) {
			for (IronGolemEntity.Crack crack : VALUES) {
				if (healthFraction < crack.maxHealthFraction) {
					return crack;
				}
			}

			return NONE;
		}
	}
}
