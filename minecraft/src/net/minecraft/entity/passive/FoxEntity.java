package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FoxEntity extends AnimalEntity {
	private static final TrackedData<Integer> TYPE = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> FOX_FLAGS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Optional<UUID>> OTHER_TRUSTED = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive();
	private static final Predicate<Entity> JUST_ATTACKED_SOMETHING_FILTER = entity -> {
		if (!(entity instanceof LivingEntity)) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)entity;
			return livingEntity.getAttacking() != null && livingEntity.getLastAttackTime() < livingEntity.age + 600;
		}
	};
	private static final Predicate<Entity> CHICKEN_AND_RABBIT_FILTER = entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity;
	private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER = entity -> !entity.isSneaking() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
	private Goal followChickenAndRabbitGoal;
	private Goal followBabyTurtleGoal;
	private Goal followFishGoal;
	private float headRollProgress;
	private float lastHeadRollProgress;
	private float extraRollingHeight;
	private float lastExtraRollingHeight;
	private int eatingTime;

	public FoxEntity(EntityType<? extends FoxEntity> entityType, World world) {
		super(entityType, world);
		this.lookControl = new FoxEntity.FoxLookControl();
		this.moveControl = new FoxEntity.FoxMoveControl();
		this.method_5941(PathNodeType.field_5, 0.0F);
		this.method_5941(PathNodeType.field_17, 0.0F);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(OWNER, Optional.empty());
		this.dataTracker.startTracking(OTHER_TRUSTED, Optional.empty());
		this.dataTracker.startTracking(TYPE, 0);
		this.dataTracker.startTracking(FOX_FLAGS, (byte)0);
	}

	@Override
	protected void initGoals() {
		this.followChickenAndRabbitGoal = new FollowTargetGoal(
			this, AnimalEntity.class, 10, false, false, livingEntity -> livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity
		);
		this.followBabyTurtleGoal = new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
		this.followFishGoal = new FollowTargetGoal(this, FishEntity.class, 20, false, false, livingEntity -> livingEntity instanceof SchoolingFishEntity);
		this.goalSelector.add(0, new FoxEntity.FoxSwimGoal());
		this.goalSelector.add(1, new FoxEntity.StopWanderingGoal());
		this.goalSelector.add(2, new FoxEntity.EscapeWhenNotAggresiveGoal(2.2));
		this.goalSelector
			.add(
				3,
				new FleeEntityGoal(
					this,
					PlayerEntity.class,
					16.0F,
					1.6,
					1.4,
					livingEntity -> NOTICEABLE_PLAYER_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid()) && !this.isAggressive()
				)
			);
		this.goalSelector
			.add(3, new FleeEntityGoal(this, WolfEntity.class, 8.0F, 1.6, 1.4, livingEntity -> !((WolfEntity)livingEntity).isTamed() && !this.isAggressive()));
		this.goalSelector.add(4, new FoxEntity.MoveToHuntGoal());
		this.goalSelector.add(5, new FoxEntity.JumpChasingGoal());
		this.goalSelector.add(5, new FoxEntity.MateGoal(1.0));
		this.goalSelector.add(5, new FoxEntity.AvoidDaylightGoal(1.25));
		this.goalSelector.add(6, new FoxEntity.AttackGoal(1.2F, true));
		this.goalSelector.add(6, new FoxEntity.DelayedCalmDownGoal());
		this.goalSelector.add(7, new FoxEntity.FollowParentGoal(this, 1.25));
		this.goalSelector.add(8, new FoxEntity.GoToVillageGoal(32, 200));
		this.goalSelector.add(9, new FoxEntity.EatSweetBerriesGoal(1.2F, 12, 2));
		this.goalSelector.add(9, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(10, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(10, new FoxEntity.PickupItemGoal());
		this.goalSelector.add(11, new FoxEntity.LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
		this.goalSelector.add(12, new FoxEntity.SitDownAndLookAroundGoal());
		this.targetSelector
			.add(
				3,
				new FoxEntity.DefendFriendGoal(
					LivingEntity.class, false, false, livingEntity -> JUST_ATTACKED_SOMETHING_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid())
				)
			);
	}

	@Override
	public SoundEvent getEatSound(ItemStack itemStack) {
		return SoundEvents.field_18060;
	}

	@Override
	public void tickMovement() {
		if (!this.field_6002.isClient && this.isAlive() && this.canMoveVoluntarily()) {
			this.eatingTime++;
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6173);
			if (this.canEat(itemStack)) {
				if (this.eatingTime > 600) {
					ItemStack itemStack2 = itemStack.method_7910(this.field_6002, this);
					if (!itemStack2.isEmpty()) {
						this.setEquippedStack(EquipmentSlot.field_6173, itemStack2);
					}

					this.eatingTime = 0;
				} else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
					this.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
					this.field_6002.sendEntityStatus(this, (byte)45);
				}
			}

			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null || !livingEntity.isAlive()) {
				this.setCrouching(false);
				this.setRollingHead(false);
			}
		}

		if (this.isSleeping() || this.cannotMove()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
			this.field_6267 = 0.0F;
		}

		super.tickMovement();
		if (this.isAggressive() && this.random.nextFloat() < 0.05F) {
			this.playSound(SoundEvents.field_18055, 1.0F, 1.0F);
		}
	}

	@Override
	protected boolean cannotMove() {
		return this.getHealth() <= 0.0F;
	}

	private boolean canEat(ItemStack itemStack) {
		return itemStack.getItem().isFood() && this.getTarget() == null && this.onGround && !this.isSleeping();
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		if (this.random.nextFloat() < 0.2F) {
			float f = this.random.nextFloat();
			ItemStack itemStack;
			if (f < 0.05F) {
				itemStack = new ItemStack(Items.field_8687);
			} else if (f < 0.2F) {
				itemStack = new ItemStack(Items.field_8803);
			} else if (f < 0.4F) {
				itemStack = this.random.nextBoolean() ? new ItemStack(Items.field_8073) : new ItemStack(Items.field_8245);
			} else if (f < 0.6F) {
				itemStack = new ItemStack(Items.field_8861);
			} else if (f < 0.8F) {
				itemStack = new ItemStack(Items.field_8745);
			} else {
				itemStack = new ItemStack(Items.field_8153);
			}

			this.setEquippedStack(EquipmentSlot.field_6173, itemStack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 45) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6173);
			if (!itemStack.isEmpty()) {
				for (int i = 0; i < 8; i++) {
					Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
						.rotateX(-this.pitch * (float) (Math.PI / 180.0))
						.rotateY(-this.yaw * (float) (Math.PI / 180.0));
					this.field_6002
						.addParticle(
							new ItemStackParticleEffect(ParticleTypes.field_11218, itemStack),
							this.x + this.method_5720().x / 2.0,
							this.y,
							this.z + this.method_5720().z / 2.0,
							vec3d.x,
							vec3d.y + 0.05,
							vec3d.z
						);
				}
			}
		} else {
			super.handleStatus(b);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	public FoxEntity method_18260(PassiveEntity passiveEntity) {
		FoxEntity foxEntity = EntityType.field_17943.method_5883(this.field_6002);
		foxEntity.setType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)passiveEntity).getFoxType());
		return foxEntity;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		Biome biome = iWorld.method_8310(new BlockPos(this));
		FoxEntity.Type type = FoxEntity.Type.method_18313(biome);
		boolean bl = false;
		if (entityData instanceof FoxEntity.FoxData) {
			type = ((FoxEntity.FoxData)entityData).type;
			if (((FoxEntity.FoxData)entityData).uses >= 2) {
				bl = true;
			} else {
				((FoxEntity.FoxData)entityData).uses++;
			}
		} else {
			entityData = new FoxEntity.FoxData(type);
			((FoxEntity.FoxData)entityData).uses++;
		}

		this.setType(type);
		if (bl) {
			this.setBreedingAge(-24000);
		}

		this.addTypeSpecificGoals();
		this.initEquipment(localDifficulty);
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	private void addTypeSpecificGoals() {
		if (this.getFoxType() == FoxEntity.Type.field_17996) {
			this.targetSelector.add(4, this.followChickenAndRabbitGoal);
			this.targetSelector.add(4, this.followBabyTurtleGoal);
			this.targetSelector.add(6, this.followFishGoal);
		} else {
			this.targetSelector.add(4, this.followFishGoal);
			this.targetSelector.add(6, this.followChickenAndRabbitGoal);
			this.targetSelector.add(6, this.followBabyTurtleGoal);
		}
	}

	@Override
	protected void eat(PlayerEntity playerEntity, ItemStack itemStack) {
		if (this.isBreedingItem(itemStack)) {
			this.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
		}

		super.eat(playerEntity, itemStack);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return this.isBaby() ? entityDimensions.height * 0.85F : 0.4F;
	}

	public FoxEntity.Type getFoxType() {
		return FoxEntity.Type.fromId(this.dataTracker.get(TYPE));
	}

	private void setType(FoxEntity.Type type) {
		this.dataTracker.set(TYPE, type.getId());
	}

	private List<UUID> getTrustedUuids() {
		List<UUID> list = Lists.<UUID>newArrayList();
		list.add(this.dataTracker.get(OWNER).orElse(null));
		list.add(this.dataTracker.get(OTHER_TRUSTED).orElse(null));
		return list;
	}

	private void addTrustedUuid(@Nullable UUID uUID) {
		if (this.dataTracker.get(OWNER).isPresent()) {
			this.dataTracker.set(OTHER_TRUSTED, Optional.ofNullable(uUID));
		} else {
			this.dataTracker.set(OWNER, Optional.ofNullable(uUID));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		List<UUID> list = this.getTrustedUuids();
		ListTag listTag = new ListTag();

		for (UUID uUID : list) {
			if (uUID != null) {
				listTag.add(TagHelper.serializeUuid(uUID));
			}
		}

		compoundTag.put("TrustedUUIDs", listTag);
		compoundTag.putBoolean("Sleeping", this.isSleeping());
		compoundTag.putString("Type", this.getFoxType().getKey());
		compoundTag.putBoolean("Sitting", this.isSitting());
		compoundTag.putBoolean("Crouching", this.isCrouching());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ListTag listTag = compoundTag.getList("TrustedUUIDs", 10);

		for (int i = 0; i < listTag.size(); i++) {
			this.addTrustedUuid(TagHelper.deserializeUuid(listTag.getCompoundTag(i)));
		}

		this.setSleeping(compoundTag.getBoolean("Sleeping"));
		this.setType(FoxEntity.Type.byName(compoundTag.getString("Type")));
		this.setSitting(compoundTag.getBoolean("Sitting"));
		this.setCrouching(compoundTag.getBoolean("Crouching"));
		this.addTypeSpecificGoals();
	}

	public boolean isSitting() {
		return this.getFoxFlag(1);
	}

	public void setSitting(boolean bl) {
		this.setFoxFlag(1, bl);
	}

	public boolean isWalking() {
		return this.getFoxFlag(64);
	}

	private void setWalking(boolean bl) {
		this.setFoxFlag(64, bl);
	}

	private boolean isAggressive() {
		return this.getFoxFlag(128);
	}

	private void setAggressive(boolean bl) {
		this.setFoxFlag(128, bl);
	}

	@Override
	public boolean isSleeping() {
		return this.getFoxFlag(32);
	}

	private void setSleeping(boolean bl) {
		this.setFoxFlag(32, bl);
	}

	private void setFoxFlag(int i, boolean bl) {
		if (bl) {
			this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) | i));
		} else {
			this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) & ~i));
		}
	}

	private boolean getFoxFlag(int i) {
		return (this.dataTracker.get(FOX_FLAGS) & i) != 0;
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.field_6173 && super.canPickUp(itemStack);
	}

	@Override
	protected boolean canPickupItem(ItemStack itemStack) {
		Item item = itemStack.getItem();
		ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.field_6173);
		return itemStack2.isEmpty() || this.eatingTime > 0 && item.isFood() && !itemStack2.getItem().isFood();
	}

	private void spit(ItemStack itemStack) {
		if (!itemStack.isEmpty() && !this.field_6002.isClient) {
			ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x + this.method_5720().x, this.y + 1.0, this.z + this.method_5720().z, itemStack);
			itemEntity.setPickupDelay(40);
			itemEntity.setThrower(this.getUuid());
			this.playSound(SoundEvents.field_18054, 1.0F, 1.0F);
			this.field_6002.spawnEntity(itemEntity);
		}
	}

	private void dropItem(ItemStack itemStack) {
		ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y, this.z, itemStack);
		this.field_6002.spawnEntity(itemEntity);
	}

	@Override
	protected void loot(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		if (this.canPickupItem(itemStack)) {
			int i = itemStack.getCount();
			if (i > 1) {
				this.dropItem(itemStack.split(i - 1));
			}

			this.spit(this.getEquippedStack(EquipmentSlot.field_6173));
			this.setEquippedStack(EquipmentSlot.field_6173, itemStack.split(1));
			this.handDropChances[EquipmentSlot.field_6173.getEntitySlotId()] = 2.0F;
			this.sendPickup(itemEntity, itemStack.getCount());
			itemEntity.remove();
			this.eatingTime = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.canMoveVoluntarily()) {
			boolean bl = this.isInsideWater();
			if (bl || this.getTarget() != null || this.field_6002.isThundering()) {
				this.wakeUp();
			}

			if (bl || this.isSleeping()) {
				this.setSitting(false);
			}

			if (this.isWalking() && this.field_6002.random.nextFloat() < 0.2F) {
				BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
				BlockState blockState = this.field_6002.method_8320(blockPos);
				this.field_6002.playLevelEvent(2001, blockPos, Block.method_9507(blockState));
			}
		}

		this.lastHeadRollProgress = this.headRollProgress;
		if (this.isRollingHead()) {
			this.headRollProgress = this.headRollProgress + (1.0F - this.headRollProgress) * 0.4F;
		} else {
			this.headRollProgress = this.headRollProgress + (0.0F - this.headRollProgress) * 0.4F;
		}

		this.lastExtraRollingHeight = this.extraRollingHeight;
		if (this.isCrouching()) {
			this.extraRollingHeight += 0.2F;
			if (this.extraRollingHeight > 3.0F) {
				this.extraRollingHeight = 3.0F;
			}
		} else {
			this.extraRollingHeight = 0.0F;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_16998;
	}

	@Override
	protected void onPlayerSpawnedChild(PlayerEntity playerEntity, PassiveEntity passiveEntity) {
		((FoxEntity)passiveEntity).addTrustedUuid(playerEntity.getUuid());
	}

	public boolean isChasing() {
		return this.getFoxFlag(16);
	}

	public void setChasing(boolean bl) {
		this.setFoxFlag(16, bl);
	}

	public boolean isFullyCrouched() {
		return this.extraRollingHeight == 3.0F;
	}

	public void setCrouching(boolean bl) {
		this.setFoxFlag(4, bl);
	}

	public boolean isCrouching() {
		return this.getFoxFlag(4);
	}

	public void setRollingHead(boolean bl) {
		this.setFoxFlag(8, bl);
	}

	public boolean isRollingHead() {
		return this.getFoxFlag(8);
	}

	@Environment(EnvType.CLIENT)
	public float getHeadRoll(float f) {
		return MathHelper.lerp(f, this.lastHeadRollProgress, this.headRollProgress) * 0.11F * (float) Math.PI;
	}

	@Environment(EnvType.CLIENT)
	public float getBodyRotationHeightOffset(float f) {
		return MathHelper.lerp(f, this.lastExtraRollingHeight, this.extraRollingHeight);
	}

	@Override
	public void setTarget(@Nullable LivingEntity livingEntity) {
		if (this.isAggressive() && livingEntity == null) {
			this.setAggressive(false);
		}

		super.setTarget(livingEntity);
	}

	@Override
	public void handleFallDamage(float f, float g) {
		int i = MathHelper.ceil((f - 5.0F) * g);
		if (i > 0) {
			this.damage(DamageSource.FALL, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.getPassengersDeep()) {
					entity.damage(DamageSource.FALL, (float)i);
				}
			}

			BlockState blockState = this.field_6002.method_8320(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			if (!blockState.isAir() && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.field_6002
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						blockSoundGroup.getStepSound(),
						this.getSoundCategory(),
						blockSoundGroup.getVolume() * 0.5F,
						blockSoundGroup.getPitch() * 0.75F
					);
			}
		}
	}

	private void wakeUp() {
		this.setSleeping(false);
	}

	private void stopActions() {
		this.setRollingHead(false);
		this.setCrouching(false);
		this.setSitting(false);
		this.setSleeping(false);
		this.setAggressive(false);
		this.setWalking(false);
	}

	private boolean wantsToPickupItem() {
		return !this.isSleeping() && !this.isSitting() && !this.isWalking();
	}

	@Override
	public void playAmbientSound() {
		SoundEvent soundEvent = this.getAmbientSound();
		if (soundEvent == SoundEvents.field_18265) {
			this.playSound(soundEvent, 2.0F, this.getSoundPitch());
		} else {
			super.playAmbientSound();
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isSleeping()) {
			return SoundEvents.field_18062;
		} else {
			if (!this.field_6002.isDaylight() && this.random.nextFloat() < 0.1F) {
				List<PlayerEntity> list = this.field_6002.method_8390(PlayerEntity.class, this.method_5829().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR);
				if (list.isEmpty()) {
					return SoundEvents.field_18265;
				}
			}

			return SoundEvents.field_18056;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_18061;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_18059;
	}

	private boolean canTrust(UUID uUID) {
		return this.getTrustedUuids().contains(uUID);
	}

	@Override
	protected void drop(DamageSource damageSource) {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6173);
		if (!itemStack.isEmpty()) {
			this.dropStack(itemStack);
			this.setEquippedStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
		}

		super.drop(damageSource);
	}

	public static boolean canJumpChase(FoxEntity foxEntity, LivingEntity livingEntity) {
		double d = livingEntity.z - foxEntity.z;
		double e = livingEntity.x - foxEntity.x;
		double f = d / e;
		int i = 6;

		for (int j = 0; j < 6; j++) {
			double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0F);
			double h = f == 0.0 ? e * (double)((float)j / 6.0F) : g / f;

			for (int k = 1; k < 4; k++) {
				if (!foxEntity.field_6002.method_8320(new BlockPos(foxEntity.x + h, foxEntity.y + (double)k, foxEntity.z + g)).method_11620().isReplaceable()) {
					return false;
				}
			}
		}

		return true;
	}

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(double d, boolean bl) {
			super(FoxEntity.this, d, bl);
		}

		@Override
		protected void attack(LivingEntity livingEntity, double d) {
			double e = this.getSquaredMaxAttackDistance(livingEntity);
			if (d <= e && this.ticksUntilAttack <= 0) {
				this.ticksUntilAttack = 20;
				this.mob.tryAttack(livingEntity);
				FoxEntity.this.playSound(SoundEvents.field_18058, 1.0F, 1.0F);
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setRollingHead(false);
			super.start();
		}

		@Override
		public boolean canStart() {
			return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isWalking() && super.canStart();
		}
	}

	class AvoidDaylightGoal extends EscapeSunlightGoal {
		private int timer = 100;

		public AvoidDaylightGoal(double d) {
			super(FoxEntity.this, d);
		}

		@Override
		public boolean canStart() {
			if (FoxEntity.this.isSleeping() || this.mob.getTarget() != null) {
				return false;
			} else if (FoxEntity.this.field_6002.isThundering()) {
				return true;
			} else if (this.timer > 0) {
				this.timer--;
				return false;
			} else {
				this.timer = 100;
				BlockPos blockPos = new BlockPos(this.mob);
				return FoxEntity.this.field_6002.isDaylight()
					&& FoxEntity.this.field_6002.isSkyVisible(blockPos)
					&& !((ServerWorld)FoxEntity.this.field_6002).isNearOccupiedPointOfInterest(blockPos)
					&& this.method_18250();
			}
		}

		@Override
		public void start() {
			FoxEntity.this.stopActions();
			super.start();
		}
	}

	abstract class CalmDownGoal extends Goal {
		private final TargetPredicate WORRIABLE_ENTITY_PREDICATE = new TargetPredicate()
			.setBaseMaxDistance(12.0)
			.includeHidden()
			.setPredicate(FoxEntity.this.new WorriableEntityFilter());

		private CalmDownGoal() {
		}

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = new BlockPos(FoxEntity.this);
			return !FoxEntity.this.field_6002.isSkyVisible(blockPos) && FoxEntity.this.getPathfindingFavor(blockPos) >= 0.0F;
		}

		protected boolean canCalmDown() {
			return !FoxEntity.this.field_6002
				.method_18466(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.method_5829().expand(12.0, 6.0, 12.0))
				.isEmpty();
		}
	}

	class DefendFriendGoal extends FollowTargetGoal<LivingEntity> {
		@Nullable
		private LivingEntity offender;
		private LivingEntity friend;
		private int lastAttackedTime;

		public DefendFriendGoal(Class<LivingEntity> class_, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
			super(FoxEntity.this, class_, 10, bl, bl2, predicate);
		}

		@Override
		public boolean canStart() {
			if (this.reciprocalChance > 0 && this.mob.getRand().nextInt(this.reciprocalChance) != 0) {
				return false;
			} else {
				for (UUID uUID : FoxEntity.this.getTrustedUuids()) {
					if (uUID != null && FoxEntity.this.field_6002 instanceof ServerWorld) {
						Entity entity = ((ServerWorld)FoxEntity.this.field_6002).getEntity(uUID);
						if (entity instanceof LivingEntity) {
							LivingEntity livingEntity = (LivingEntity)entity;
							this.friend = livingEntity;
							this.offender = livingEntity.getAttacker();
							int i = livingEntity.getLastAttackedTime();
							return i != this.lastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
						}
					}
				}

				return false;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setTarget(this.offender);
			this.targetEntity = this.offender;
			if (this.friend != null) {
				this.lastAttackedTime = this.friend.getLastAttackedTime();
			}

			FoxEntity.this.playSound(SoundEvents.field_18055, 1.0F, 1.0F);
			FoxEntity.this.setAggressive(true);
			FoxEntity.this.wakeUp();
			super.start();
		}
	}

	class DelayedCalmDownGoal extends FoxEntity.CalmDownGoal {
		private int timer = FoxEntity.this.random.nextInt(140);

		public DelayedCalmDownGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406, Goal.Control.field_18407));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.sidewaysSpeed == 0.0F && FoxEntity.this.upwardSpeed == 0.0F && FoxEntity.this.forwardSpeed == 0.0F
				? this.canNotCalmDown() || FoxEntity.this.isSleeping()
				: false;
		}

		@Override
		public boolean shouldContinue() {
			return this.canNotCalmDown();
		}

		private boolean canNotCalmDown() {
			if (this.timer > 0) {
				this.timer--;
				return false;
			} else {
				return FoxEntity.this.field_6002.isDaylight() && this.isAtFavoredLocation() && !this.canCalmDown();
			}
		}

		@Override
		public void stop() {
			this.timer = FoxEntity.this.random.nextInt(140);
			FoxEntity.this.stopActions();
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.setJumping(false);
			FoxEntity.this.setSleeping(true);
			FoxEntity.this.getNavigation().stop();
			FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.x, FoxEntity.this.y, FoxEntity.this.z, 0.0);
		}
	}

	public class EatSweetBerriesGoal extends MoveToTargetPosGoal {
		protected int timer;

		public EatSweetBerriesGoal(double d, int i, int j) {
			super(FoxEntity.this, d, i, j);
		}

		@Override
		public double getDesiredSquaredDistanceToTarget() {
			return 2.0;
		}

		@Override
		public boolean shouldResetPath() {
			return this.tryingTime % 100 == 0;
		}

		@Override
		protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
			BlockState blockState = viewableWorld.method_8320(blockPos);
			return blockState.getBlock() == Blocks.field_16999 && (Integer)blockState.method_11654(SweetBerryBushBlock.field_17000) >= 2;
		}

		@Override
		public void tick() {
			if (this.hasReached()) {
				if (this.timer >= 40) {
					this.eatSweetBerry();
				} else {
					this.timer++;
				}
			} else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05F) {
				FoxEntity.this.playSound(SoundEvents.field_18063, 1.0F, 1.0F);
			}

			super.tick();
		}

		protected void eatSweetBerry() {
			if (FoxEntity.this.field_6002.getGameRules().getBoolean(GameRules.field_19388)) {
				BlockState blockState = FoxEntity.this.field_6002.method_8320(this.targetPos);
				if (blockState.getBlock() == Blocks.field_16999) {
					int i = (Integer)blockState.method_11654(SweetBerryBushBlock.field_17000);
					blockState.method_11657(SweetBerryBushBlock.field_17000, Integer.valueOf(1));
					int j = 1 + FoxEntity.this.field_6002.random.nextInt(2) + (i == 3 ? 1 : 0);
					ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.field_6173);
					if (itemStack.isEmpty()) {
						FoxEntity.this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_16998));
						j--;
					}

					if (j > 0) {
						Block.dropStack(FoxEntity.this.field_6002, this.targetPos, new ItemStack(Items.field_16998, j));
					}

					FoxEntity.this.playSound(SoundEvents.field_17617, 1.0F, 1.0F);
					FoxEntity.this.field_6002.method_8652(this.targetPos, blockState.method_11657(SweetBerryBushBlock.field_17000, Integer.valueOf(1)), 2);
				}
			}
		}

		@Override
		public boolean canStart() {
			return !FoxEntity.this.isSleeping() && super.canStart();
		}

		@Override
		public void start() {
			this.timer = 0;
			FoxEntity.this.setSitting(false);
			super.start();
		}
	}

	class EscapeWhenNotAggresiveGoal extends EscapeDangerGoal {
		public EscapeWhenNotAggresiveGoal(double d) {
			super(FoxEntity.this, d);
		}

		@Override
		public boolean canStart() {
			return !FoxEntity.this.isAggressive() && super.canStart();
		}
	}

	class FollowParentGoal extends net.minecraft.entity.ai.goal.FollowParentGoal {
		private final FoxEntity fox;

		public FollowParentGoal(FoxEntity foxEntity2, double d) {
			super(foxEntity2, d);
			this.fox = foxEntity2;
		}

		@Override
		public boolean canStart() {
			return !this.fox.isAggressive() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.fox.isAggressive() && super.shouldContinue();
		}

		@Override
		public void start() {
			this.fox.stopActions();
			super.start();
		}
	}

	public static class FoxData implements EntityData {
		public final FoxEntity.Type type;
		public int uses;

		public FoxData(FoxEntity.Type type) {
			this.type = type;
		}
	}

	public class FoxLookControl extends LookControl {
		public FoxLookControl() {
			super(FoxEntity.this);
		}

		@Override
		public void tick() {
			if (!FoxEntity.this.isSleeping()) {
				super.tick();
			}
		}

		@Override
		protected boolean method_20433() {
			return !FoxEntity.this.isChasing() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isRollingHead() & !FoxEntity.this.isWalking();
		}
	}

	class FoxMoveControl extends MoveControl {
		public FoxMoveControl() {
			super(FoxEntity.this);
		}

		@Override
		public void tick() {
			if (FoxEntity.this.wantsToPickupItem()) {
				super.tick();
			}
		}
	}

	class FoxSwimGoal extends SwimGoal {
		public FoxSwimGoal() {
			super(FoxEntity.this);
		}

		@Override
		public void start() {
			super.start();
			FoxEntity.this.stopActions();
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.isInsideWater() && FoxEntity.this.getWaterHeight() > 0.25 || FoxEntity.this.isInLava();
		}
	}

	class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal {
		public GoToVillageGoal(int i, int j) {
			super(FoxEntity.this, j);
		}

		@Override
		public void start() {
			FoxEntity.this.stopActions();
			super.start();
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.canGoToVillage();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.canGoToVillage();
		}

		private boolean canGoToVillage() {
			return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
		}
	}

	public class JumpChasingGoal extends DiveJumpingGoal {
		@Override
		public boolean canStart() {
			if (!FoxEntity.this.isFullyCrouched()) {
				return false;
			} else {
				LivingEntity livingEntity = FoxEntity.this.getTarget();
				if (livingEntity != null && livingEntity.isAlive()) {
					if (livingEntity.getMovementDirection() != livingEntity.getHorizontalFacing()) {
						return false;
					} else {
						boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
						if (!bl) {
							FoxEntity.this.getNavigation().method_6349(livingEntity);
							FoxEntity.this.setCrouching(false);
							FoxEntity.this.setRollingHead(false);
						}

						return bl;
					}
				} else {
					return false;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null && livingEntity.isAlive()) {
				double d = FoxEntity.this.method_18798().y;
				return (!(d * d < 0.05F) || !(Math.abs(FoxEntity.this.pitch) < 15.0F) || !FoxEntity.this.onGround) && !FoxEntity.this.isWalking();
			} else {
				return false;
			}
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public void start() {
			FoxEntity.this.setJumping(true);
			FoxEntity.this.setChasing(true);
			FoxEntity.this.setRollingHead(false);
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
			Vec3d vec3d = new Vec3d(livingEntity.x - FoxEntity.this.x, livingEntity.y - FoxEntity.this.y, livingEntity.z - FoxEntity.this.z).normalize();
			FoxEntity.this.method_18799(FoxEntity.this.method_18798().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
			FoxEntity.this.getNavigation().stop();
		}

		@Override
		public void stop() {
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.extraRollingHeight = 0.0F;
			FoxEntity.this.lastExtraRollingHeight = 0.0F;
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.setChasing(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null) {
				FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
			}

			if (!FoxEntity.this.isWalking()) {
				Vec3d vec3d = FoxEntity.this.method_18798();
				if (vec3d.y * vec3d.y < 0.03F && FoxEntity.this.pitch != 0.0F) {
					FoxEntity.this.pitch = this.updatePitch(FoxEntity.this.pitch, 0.0F, 0.2F);
				} else {
					double d = Math.sqrt(Entity.method_17996(vec3d));
					double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 180.0F / (float)Math.PI;
					FoxEntity.this.pitch = (float)e;
				}
			}

			if (livingEntity != null && FoxEntity.this.distanceTo(livingEntity) <= 2.0F) {
				FoxEntity.this.tryAttack(livingEntity);
			} else if (FoxEntity.this.pitch > 0.0F
				&& FoxEntity.this.onGround
				&& (float)FoxEntity.this.method_18798().y != 0.0F
				&& FoxEntity.this.field_6002.method_8320(new BlockPos(FoxEntity.this)).getBlock() == Blocks.field_10477) {
				FoxEntity.this.pitch = 60.0F;
				FoxEntity.this.setTarget(null);
				FoxEntity.this.setWalking(true);
			}
		}
	}

	class LookAtEntityGoal extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
		public LookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f) {
			super(mobEntity, class_, f);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
		}
	}

	class MateGoal extends AnimalMateGoal {
		public MateGoal(double d) {
			super(FoxEntity.this, d);
		}

		@Override
		public void start() {
			((FoxEntity)this.animal).stopActions();
			((FoxEntity)this.mate).stopActions();
			super.start();
		}

		@Override
		protected void breed() {
			FoxEntity foxEntity = (FoxEntity)this.animal.createChild(this.mate);
			if (foxEntity != null) {
				ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
				ServerPlayerEntity serverPlayerEntity2 = this.mate.getLovingPlayer();
				ServerPlayerEntity serverPlayerEntity3 = serverPlayerEntity;
				if (serverPlayerEntity != null) {
					foxEntity.addTrustedUuid(serverPlayerEntity.getUuid());
				} else {
					serverPlayerEntity3 = serverPlayerEntity2;
				}

				if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
					foxEntity.addTrustedUuid(serverPlayerEntity2.getUuid());
				}

				if (serverPlayerEntity3 != null) {
					serverPlayerEntity3.incrementStat(Stats.field_15410);
					Criterions.BRED_ANIMALS.handle(serverPlayerEntity3, this.animal, this.mate, foxEntity);
				}

				int i = 6000;
				this.animal.setBreedingAge(6000);
				this.mate.setBreedingAge(6000);
				this.animal.resetLoveTicks();
				this.mate.resetLoveTicks();
				foxEntity.setBreedingAge(-24000);
				foxEntity.setPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0F, 0.0F);
				this.field_6405.spawnEntity(foxEntity);
				this.field_6405.sendEntityStatus(this.animal, (byte)18);
				if (this.field_6405.getGameRules().getBoolean(GameRules.field_19391)) {
					this.field_6405.spawnEntity(new ExperienceOrbEntity(this.field_6405, this.animal.x, this.animal.y, this.animal.z, this.animal.getRand().nextInt(7) + 1));
				}
			}
		}
	}

	class MoveToHuntGoal extends Goal {
		public MoveToHuntGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		}

		@Override
		public boolean canStart() {
			if (FoxEntity.this.isSleeping()) {
				return false;
			} else {
				LivingEntity livingEntity = FoxEntity.this.getTarget();
				return livingEntity != null
					&& livingEntity.isAlive()
					&& FoxEntity.CHICKEN_AND_RABBIT_FILTER.test(livingEntity)
					&& FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0
					&& !FoxEntity.this.isCrouching()
					&& !FoxEntity.this.isRollingHead()
					&& !FoxEntity.this.jumping;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
			FoxEntity.this.setWalking(false);
		}

		@Override
		public void stop() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
				FoxEntity.this.setRollingHead(true);
				FoxEntity.this.setCrouching(true);
				FoxEntity.this.getNavigation().stop();
				FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.method_5986(), (float)FoxEntity.this.getLookPitchSpeed());
			} else {
				FoxEntity.this.setRollingHead(false);
				FoxEntity.this.setCrouching(false);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.method_5986(), (float)FoxEntity.this.getLookPitchSpeed());
			if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
				FoxEntity.this.setRollingHead(true);
				FoxEntity.this.setCrouching(true);
				FoxEntity.this.getNavigation().stop();
			} else {
				FoxEntity.this.getNavigation().startMovingTo(livingEntity, 1.5);
			}
		}
	}

	class PickupItemGoal extends Goal {
		public PickupItemGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			if (!FoxEntity.this.getEquippedStack(EquipmentSlot.field_6173).isEmpty()) {
				return false;
			} else if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
				return false;
			} else if (!FoxEntity.this.wantsToPickupItem()) {
				return false;
			} else if (FoxEntity.this.getRand().nextInt(10) != 0) {
				return false;
			} else {
				List<ItemEntity> list = FoxEntity.this.field_6002
					.method_8390(ItemEntity.class, FoxEntity.this.method_5829().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
				return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.field_6173).isEmpty();
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = FoxEntity.this.field_6002
				.method_8390(ItemEntity.class, FoxEntity.this.method_5829().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.field_6173);
			if (itemStack.isEmpty() && !list.isEmpty()) {
				FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = FoxEntity.this.field_6002
				.method_8390(ItemEntity.class, FoxEntity.this.method_5829().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			if (!list.isEmpty()) {
				FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}
	}

	class SitDownAndLookAroundGoal extends FoxEntity.CalmDownGoal {
		private double lookX;
		private double lookZ;
		private int timer;
		private int counter;

		public SitDownAndLookAroundGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.getAttacker() == null
				&& FoxEntity.this.getRand().nextFloat() < 0.02F
				&& !FoxEntity.this.isSleeping()
				&& FoxEntity.this.getTarget() == null
				&& FoxEntity.this.getNavigation().isIdle()
				&& !this.canCalmDown()
				&& !FoxEntity.this.isChasing()
				&& !FoxEntity.this.isCrouching();
		}

		@Override
		public boolean shouldContinue() {
			return this.counter > 0;
		}

		@Override
		public void start() {
			this.chooseNewAngle();
			this.counter = 2 + FoxEntity.this.getRand().nextInt(3);
			FoxEntity.this.setSitting(true);
			FoxEntity.this.getNavigation().stop();
		}

		@Override
		public void stop() {
			FoxEntity.this.setSitting(false);
		}

		@Override
		public void tick() {
			this.timer--;
			if (this.timer <= 0) {
				this.counter--;
				this.chooseNewAngle();
			}

			FoxEntity.this.getLookControl()
				.lookAt(
					FoxEntity.this.x + this.lookX,
					FoxEntity.this.y + (double)FoxEntity.this.getStandingEyeHeight(),
					FoxEntity.this.z + this.lookZ,
					(float)FoxEntity.this.method_5986(),
					(float)FoxEntity.this.getLookPitchSpeed()
				);
		}

		private void chooseNewAngle() {
			double d = (Math.PI * 2) * FoxEntity.this.getRand().nextDouble();
			this.lookX = Math.cos(d);
			this.lookZ = Math.sin(d);
			this.timer = 80 + FoxEntity.this.getRand().nextInt(20);
		}
	}

	class StopWanderingGoal extends Goal {
		int timer;

		public StopWanderingGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18406, Goal.Control.field_18407, Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.isWalking();
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart() && this.timer > 0;
		}

		@Override
		public void start() {
			this.timer = 40;
		}

		@Override
		public void stop() {
			FoxEntity.this.setWalking(false);
		}

		@Override
		public void tick() {
			this.timer--;
		}
	}

	public static enum Type {
		field_17996(0, "red", Biomes.field_9420, Biomes.field_9428, Biomes.field_9422, Biomes.field_9477, Biomes.field_9416, Biomes.field_9429, Biomes.field_9404),
		field_17997(1, "snow", Biomes.field_9454, Biomes.field_9425, Biomes.field_9437);

		private static final FoxEntity.Type[] TYPES = (FoxEntity.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(FoxEntity.Type::getId))
			.toArray(FoxEntity.Type[]::new);
		private static final Map<String, FoxEntity.Type> NAME_TYPE_MAP = (Map<String, FoxEntity.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(FoxEntity.Type::getKey, type -> type));
		private final int id;
		private final String key;
		private final List<Biome> biomes;

		private Type(int j, String string2, Biome... biomes) {
			this.id = j;
			this.key = string2;
			this.biomes = Arrays.asList(biomes);
		}

		public String getKey() {
			return this.key;
		}

		public List<Biome> getBiomes() {
			return this.biomes;
		}

		public int getId() {
			return this.id;
		}

		public static FoxEntity.Type byName(String string) {
			return (FoxEntity.Type)NAME_TYPE_MAP.getOrDefault(string, field_17996);
		}

		public static FoxEntity.Type fromId(int i) {
			if (i < 0 || i > TYPES.length) {
				i = 0;
			}

			return TYPES[i];
		}

		public static FoxEntity.Type method_18313(Biome biome) {
			return field_17997.getBiomes().contains(biome) ? field_17997 : field_17996;
		}
	}

	public class WorriableEntityFilter implements Predicate<LivingEntity> {
		public boolean method_18303(LivingEntity livingEntity) {
			if (livingEntity instanceof FoxEntity) {
				return false;
			} else if (livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity || livingEntity instanceof HostileEntity) {
				return true;
			} else if (livingEntity instanceof TameableEntity) {
				return !((TameableEntity)livingEntity).isTamed();
			} else if (!(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative()) {
				return FoxEntity.this.canTrust(livingEntity.getUuid()) ? false : !livingEntity.isSleeping() && !livingEntity.isSneaking();
			} else {
				return false;
			}
		}
	}
}
