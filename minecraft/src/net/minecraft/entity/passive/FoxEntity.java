package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_4051;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.MoveToVillageCenterGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
import net.minecraft.particle.ItemStackParticleParameters;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FoxEntity extends AnimalEntity {
	private static final TrackedData<Integer> field_17949 = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> field_17950 = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> field_17951 = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Optional<UUID>> field_17952 = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isValid();
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
		this.field_6206 = new FoxEntity.FoxLookControl();
		this.field_6207 = new FoxEntity.FoxMoveControl();
		this.method_5941(PathNodeType.field_5, 0.0F);
		this.method_5941(PathNodeType.field_17, 0.0F);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_17951, Optional.empty());
		this.field_6011.startTracking(field_17952, Optional.empty());
		this.field_6011.startTracking(field_17949, 0);
		this.field_6011.startTracking(field_17950, (byte)0);
	}

	@Override
	protected void initGoals() {
		this.followChickenAndRabbitGoal = new FollowTargetGoal(
			this, AnimalEntity.class, 10, false, false, livingEntity -> livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity
		);
		this.followBabyTurtleGoal = new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
		this.followFishGoal = new FollowTargetGoal(this, FishEntity.class, 20, false, false, livingEntity -> livingEntity instanceof SchoolingFishEntity);
		this.field_6201.add(0, new FoxEntity.FoxSwimGoal());
		this.field_6201.add(1, new FoxEntity.StopWanderingGoal());
		this.field_6201.add(2, new FoxEntity.EscapeWhenNotAggresiveGoal(2.2));
		this.field_6201
			.add(
				3,
				new FleeEntityGoal(
					this, PlayerEntity.class, 16.0F, 1.6, 1.4, livingEntity -> NOTICEABLE_PLAYER_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid())
				)
			);
		this.field_6201.add(3, new FleeEntityGoal(this, WolfEntity.class, 8.0F, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR::test));
		this.field_6201.add(4, new FoxEntity.MoveToHuntGoal());
		this.field_6201.add(5, new FoxEntity.JumpChasingGoal());
		this.field_6201.add(5, new FoxEntity.MateGoal(1.0));
		this.field_6201.add(5, new FoxEntity.AvoidDaylightGoal(1.25));
		this.field_6201.add(6, new FoxEntity.AttackGoal(1.0, true));
		this.field_6201.add(6, new FoxEntity.DelayedCalmDownGoal());
		this.field_6201.add(7, new FoxEntity.class_4052(this, 1.25));
		this.field_6201.add(8, new FoxEntity.GoToVillageGoal(32, 200));
		this.field_6201.add(9, new FoxEntity.EatSweetBerriesGoal(1.2F, 12, 2));
		this.field_6201.add(9, new PounceAtTargetGoal(this, 0.4F));
		this.field_6201.add(10, new class_1394(this, 1.0));
		this.field_6201.add(10, new FoxEntity.PickupItemGoal());
		this.field_6201.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
		this.field_6201.add(12, new FoxEntity.SitDownAndLookAroundGoal());
		this.field_6185
			.add(
				3,
				new FoxEntity.DefendFriendGoal(
					LivingEntity.class, false, false, livingEntity -> JUST_ATTACKED_SOMETHING_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid())
				)
			);
	}

	@Override
	public SoundEvent method_18869(ItemStack itemStack) {
		return SoundEvents.field_18060;
	}

	@Override
	public void updateMovement() {
		if (!this.field_6002.isClient && this.isValid() && this.method_6034()) {
			this.eatingTime++;
			ItemStack itemStack = this.method_6118(EquipmentSlot.HAND_MAIN);
			if (this.method_18430(itemStack)) {
				if (this.eatingTime > 600) {
					itemStack.method_7910(this.field_6002, this);
					this.eatingTime = 0;
				} else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
					this.method_5783(this.method_18869(itemStack), 1.0F, 1.0F);
					this.field_6002.summonParticle(this, (byte)45);
				}
			}

			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null || !livingEntity.isValid()) {
				this.setCrouching(false);
				this.setRollingHead(false);
			}
		}

		if (this.isSleeping() || this.method_6062()) {
			this.field_6282 = false;
			this.movementInputSideways = 0.0F;
			this.movementInputForward = 0.0F;
			this.field_6267 = 0.0F;
		}

		super.updateMovement();
		if (this.isAggressive() && this.random.nextFloat() < 0.05F) {
			this.method_5783(SoundEvents.field_18055, 1.0F, 1.0F);
		}
	}

	@Override
	protected boolean method_6062() {
		return this.getHealth() <= 0.0F;
	}

	private boolean method_18430(ItemStack itemStack) {
		return itemStack.getItem().method_19263() && this.getTarget() == null && this.onGround && !this.isSleeping();
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

			this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 45) {
			ItemStack itemStack = this.method_6118(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				for (int i = 0; i < 8; i++) {
					Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
						.rotateX(-this.pitch * (float) (Math.PI / 180.0))
						.rotateY(-this.yaw * (float) (Math.PI / 180.0));
					this.field_6002
						.method_8406(
							new ItemStackParticleParameters(ParticleTypes.field_11218, itemStack),
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
			super.method_5711(b);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
		this.method_6127().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	public FoxEntity method_18260(PassiveEntity passiveEntity) {
		FoxEntity foxEntity = EntityType.field_17943.method_5883(this.field_6002);
		foxEntity.setType(this.random.nextBoolean() ? this.getType() : ((FoxEntity)passiveEntity).getType());
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
		if (this.getType() == FoxEntity.Type.field_17996) {
			this.field_6185.add(4, this.followChickenAndRabbitGoal);
			this.field_6185.add(4, this.followBabyTurtleGoal);
			this.field_6185.add(6, this.followFishGoal);
		} else {
			this.field_6185.add(4, this.followFishGoal);
			this.field_6185.add(6, this.followChickenAndRabbitGoal);
			this.field_6185.add(6, this.followBabyTurtleGoal);
		}
	}

	@Override
	protected void method_6475(PlayerEntity playerEntity, ItemStack itemStack) {
		if (this.method_6481(itemStack)) {
			this.method_5783(this.method_18869(itemStack), 1.0F, 1.0F);
		}

		super.method_6475(playerEntity, itemStack);
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return this.isChild() ? entitySize.height : 0.4F;
	}

	public FoxEntity.Type getType() {
		return FoxEntity.Type.fromId(this.field_6011.get(field_17949));
	}

	private void setType(FoxEntity.Type type) {
		this.field_6011.set(field_17949, type.getId());
	}

	private List<UUID> getTrustedUuids() {
		List<UUID> list = Lists.<UUID>newArrayList();
		list.add(this.field_6011.get(field_17951).orElse(null));
		list.add(this.field_6011.get(field_17952).orElse(null));
		return list;
	}

	private void addTrustedUuid(@Nullable UUID uUID) {
		if (this.field_6011.get(field_17951).isPresent()) {
			this.field_6011.set(field_17952, Optional.ofNullable(uUID));
		} else {
			this.field_6011.set(field_17951, Optional.ofNullable(uUID));
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		List<UUID> list = this.getTrustedUuids();
		ListTag listTag = new ListTag();

		for (UUID uUID : list) {
			if (uUID != null) {
				listTag.add(TagHelper.serializeUuid(uUID));
			}
		}

		compoundTag.method_10566("TrustedUUIDs", listTag);
		compoundTag.putBoolean("Sleeping", this.isSleeping());
		compoundTag.putString("Type", this.getType().getKey());
		compoundTag.putBoolean("Sitting", this.isSitting());
		compoundTag.putBoolean("Crouching", this.isCrouching());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		ListTag listTag = compoundTag.method_10554("TrustedUUIDs", 10);

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
		return this.getFlag(1);
	}

	public void setSitting(boolean bl) {
		this.setFlag(1, bl);
	}

	public boolean isWalking() {
		return this.getFlag(64);
	}

	private void setWalking(boolean bl) {
		this.setFlag(64, bl);
	}

	private boolean isAggressive() {
		return this.getFlag(128);
	}

	private void setAggressive(boolean bl) {
		this.setFlag(128, bl);
	}

	@Override
	public boolean isSleeping() {
		return this.getFlag(32);
	}

	private void setSleeping(boolean bl) {
		this.setFlag(32, bl);
	}

	private void setFlag(int i, boolean bl) {
		if (bl) {
			this.field_6011.set(field_17950, (byte)(this.field_6011.get(field_17950) | i));
		} else {
			this.field_6011.set(field_17950, (byte)(this.field_6011.get(field_17950) & ~i));
		}
	}

	private boolean getFlag(int i) {
		return (this.field_6011.get(field_17950) & i) != 0;
	}

	@Override
	public boolean method_18397(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
		return !this.method_6118(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.HAND_MAIN && super.method_18397(itemStack);
	}

	@Override
	protected boolean method_5939(ItemStack itemStack) {
		Item item = itemStack.getItem();
		ItemStack itemStack2 = this.method_6118(EquipmentSlot.HAND_MAIN);
		return itemStack2.isEmpty() || this.eatingTime > 0 && item.method_19263() && !itemStack2.getItem().method_19263();
	}

	private void method_18289(ItemStack itemStack) {
		if (!itemStack.isEmpty() && !this.field_6002.isClient) {
			ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x + this.method_5720().x, this.y + 1.0, this.z + this.method_5720().z, itemStack);
			itemEntity.setPickupDelay(40);
			itemEntity.setThrower(this.getUuid());
			this.method_5783(SoundEvents.field_18054, 1.0F, 1.0F);
			this.field_6002.spawnEntity(itemEntity);
		}
	}

	private void method_18291(ItemStack itemStack) {
		ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y, this.z, itemStack);
		this.field_6002.spawnEntity(itemEntity);
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.method_6983();
		if (this.method_5939(itemStack)) {
			int i = itemStack.getAmount();
			if (i > 1) {
				this.method_18291(itemStack.split(i - 1));
			}

			this.method_18289(this.method_6118(EquipmentSlot.HAND_MAIN));
			this.method_5673(EquipmentSlot.HAND_MAIN, itemStack.split(1));
			this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
			this.pickUpEntity(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
			this.eatingTime = 0;
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.method_6034()) {
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
				this.field_6002.method_8535(2001, blockPos, Block.method_9507(blockState));
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
	public boolean method_6481(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_16998;
	}

	@Override
	protected void method_18249(PlayerEntity playerEntity, PassiveEntity passiveEntity) {
		((FoxEntity)passiveEntity).addTrustedUuid(playerEntity.getUuid());
	}

	@Environment(EnvType.CLIENT)
	public boolean isChasing() {
		return this.getFlag(16);
	}

	public void setChasing(boolean bl) {
		this.setFlag(16, bl);
	}

	public boolean isFullyCrouched() {
		return this.extraRollingHeight == 3.0F;
	}

	public void setCrouching(boolean bl) {
		this.setFlag(4, bl);
	}

	public boolean isCrouching() {
		return this.getFlag(4);
	}

	public void setRollingHead(boolean bl) {
		this.setFlag(8, bl);
	}

	public boolean isRollingHead() {
		return this.getFlag(8);
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
					.method_8465(
						null, this.x, this.y, this.z, blockSoundGroup.method_10594(), this.method_5634(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F
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
		SoundEvent soundEvent = this.method_5994();
		if (soundEvent == SoundEvents.field_18265) {
			this.method_5783(soundEvent, 2.0F, this.getSoundPitch());
		} else {
			super.playAmbientSound();
		}
	}

	@Nullable
	@Override
	protected SoundEvent method_5994() {
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
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_18061;
	}

	@Nullable
	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_18059;
	}

	private boolean canTrust(UUID uUID) {
		return this.getTrustedUuids().contains(uUID);
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
		protected void method_6288(LivingEntity livingEntity, double d) {
			double e = this.method_6289(livingEntity);
			if (d <= e && this.field_6505 <= 0) {
				this.field_6505 = 20;
				this.entity.attack(livingEntity);
				FoxEntity.this.method_5783(SoundEvents.field_18058, 1.0F, 1.0F);
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
			if (FoxEntity.this.isSleeping() || this.owner.getTarget() != null) {
				return false;
			} else if (FoxEntity.this.field_6002.isThundering()) {
				return true;
			} else if (this.timer > 0) {
				this.timer--;
				return false;
			} else {
				this.timer = 100;
				BlockPos blockPos = new BlockPos(this.owner);
				return FoxEntity.this.field_6002.isDaylight()
					&& FoxEntity.this.field_6002.method_8311(blockPos)
					&& !((ServerWorld)FoxEntity.this.field_6002).method_19500(blockPos)
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
		private final class_4051 field_18102 = new class_4051().method_18418(12.0).method_18422().method_18420(FoxEntity.this.new WorriableEntityFilter());

		private CalmDownGoal() {
		}

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = new BlockPos(FoxEntity.this);
			return !FoxEntity.this.field_6002.method_8311(blockPos) && FoxEntity.this.method_6149(blockPos) >= 0.0F;
		}

		protected boolean canCalmDown() {
			return !FoxEntity.this.field_6002
				.method_18466(LivingEntity.class, this.field_18102, FoxEntity.this, FoxEntity.this.method_5829().expand(12.0, 6.0, 12.0))
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
			if (this.reciprocalChance > 0 && this.entity.getRand().nextInt(this.reciprocalChance) != 0) {
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
							return i != this.lastAttackedTime && this.method_6328(this.offender, class_4051.field_18092);
						}
					}
				}

				return false;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setTarget(this.offender);
			this.field_6644 = this.offender;
			if (this.friend != null) {
				this.lastAttackedTime = this.friend.getLastAttackedTime();
			}

			FoxEntity.this.method_5783(SoundEvents.field_18055, 1.0F, 1.0F);
			FoxEntity.this.setAggressive(true);
			FoxEntity.this.wakeUp();
			super.start();
		}
	}

	class DelayedCalmDownGoal extends FoxEntity.CalmDownGoal {
		private int timer = FoxEntity.this.random.nextInt(140);

		public DelayedCalmDownGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406, Goal.class_4134.field_18407));
		}

		@Override
		public boolean canStart() {
			return this.method_18432() || FoxEntity.this.isSleeping();
		}

		@Override
		public boolean shouldContinue() {
			return this.method_18432();
		}

		private boolean method_18432() {
			if (this.timer > 0) {
				this.timer--;
				return false;
			} else {
				return FoxEntity.this.field_6002.isDaylight() && this.isAtFavoredLocation() && !this.canCalmDown();
			}
		}

		@Override
		public void onRemove() {
			this.timer = FoxEntity.this.random.nextInt(140);
			FoxEntity.this.stopActions();
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.doJump(false);
			FoxEntity.this.setSleeping(true);
			FoxEntity.this.method_5942().stop();
			FoxEntity.this.method_5962().moveTo(FoxEntity.this.x, FoxEntity.this.y, FoxEntity.this.z, 0.0);
		}
	}

	public class EatSweetBerriesGoal extends MoveToTargetPosGoal {
		protected int timer;

		public EatSweetBerriesGoal(double d, int i, int j) {
			super(FoxEntity.this, d, i, j);
		}

		@Override
		public double getDesiredSquaredDistanceToTarget() {
			return 4.0;
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
				FoxEntity.this.method_5783(SoundEvents.field_18063, 1.0F, 1.0F);
			}

			super.tick();
		}

		protected void eatSweetBerry() {
			if (FoxEntity.this.field_6002.getGameRules().getBoolean("mobGriefing")) {
				BlockState blockState = FoxEntity.this.field_6002.method_8320(this.field_6512);
				if (blockState.getBlock() == Blocks.field_16999) {
					int i = (Integer)blockState.method_11654(SweetBerryBushBlock.field_17000);
					blockState.method_11657(SweetBerryBushBlock.field_17000, Integer.valueOf(1));
					int j = 1 + FoxEntity.this.field_6002.random.nextInt(2) + (i == 3 ? 1 : 0);
					ItemStack itemStack = FoxEntity.this.method_6118(EquipmentSlot.HAND_MAIN);
					if (itemStack.isEmpty()) {
						FoxEntity.this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_16998));
						j--;
					}

					if (j > 0) {
						Block.method_9577(FoxEntity.this.field_6002, this.field_6512, new ItemStack(Items.field_16998, j));
					}

					FoxEntity.this.method_5783(SoundEvents.field_17617, 1.0F, 1.0F);
					FoxEntity.this.field_6002.method_8652(this.field_6512, blockState.method_11657(SweetBerryBushBlock.field_17000, Integer.valueOf(1)), 2);
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
				if (this.active) {
					this.active = false;
					double d = this.lookX - this.entity.x;
					double e = this.lookY - (this.entity.y + (double)this.entity.getStandingEyeHeight());
					double f = this.lookZ - this.entity.z;
					double g = (double)MathHelper.sqrt(d * d + f * f);
					float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
					float i = (float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI));
					this.entity.pitch = this.method_6229(this.entity.pitch, i, this.field_6358);
					this.entity.headYaw = this.method_6229(this.entity.headYaw, h, this.field_6359);
				} else {
					this.entity.headYaw = this.method_6229(this.entity.headYaw, this.entity.field_6283, 10.0F);
				}
			}
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
			return FoxEntity.this.isInsideWater() && FoxEntity.this.method_5861() > 0.25 || FoxEntity.this.isTouchingLava();
		}
	}

	class GoToVillageGoal extends MoveToVillageCenterGoal {
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
				if (livingEntity != null && livingEntity.isValid()) {
					if (livingEntity.method_5755() != livingEntity.method_5735()) {
						return false;
					} else {
						boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
						if (!bl) {
							FoxEntity.this.method_5942().method_6349(livingEntity);
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
			double d = FoxEntity.this.method_18798().y;
			return (!(d * d < 0.03F) || FoxEntity.this.pitch == 0.0F || !(Math.abs(FoxEntity.this.pitch) < 10.0F) || !FoxEntity.this.onGround)
				&& !FoxEntity.this.isWalking();
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public void start() {
			FoxEntity.this.doJump(true);
			FoxEntity.this.setChasing(true);
			FoxEntity.this.setRollingHead(false);
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			FoxEntity.this.method_5988().lookAt(livingEntity, 30.0F, 30.0F);
			Vec3d vec3d = new Vec3d(livingEntity.x - FoxEntity.this.x, livingEntity.y - FoxEntity.this.y, livingEntity.z - FoxEntity.this.z).normalize();
			FoxEntity.this.method_18799(FoxEntity.this.method_18798().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
			FoxEntity.this.method_5942().stop();
		}

		@Override
		public void onRemove() {
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.setChasing(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null) {
				FoxEntity.this.method_5988().lookAt(livingEntity, 30.0F, 30.0F);
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
				FoxEntity.this.attack(livingEntity);
			} else if (FoxEntity.this.pitch > 0.0F
				&& FoxEntity.this.onGround
				&& FoxEntity.this.method_18798().y != 0.0
				&& FoxEntity.this.field_6002.method_8320(new BlockPos(FoxEntity.this)).getBlock() == Blocks.field_10477) {
				if (FoxEntity.this.pitch < 0.0F) {
					FoxEntity.this.pitch = 0.0F;
				} else {
					FoxEntity.this.setTarget(null);
					FoxEntity.this.setWalking(true);
				}
			}
		}
	}

	class MateGoal extends AnimalMateGoal {
		public MateGoal(double d) {
			super(FoxEntity.this, d);
		}

		@Override
		public void start() {
			((FoxEntity)this.field_6404).stopActions();
			((FoxEntity)this.field_6406).stopActions();
			super.start();
		}

		@Override
		protected void method_6249() {
			FoxEntity foxEntity = (FoxEntity)this.field_6404.createChild(this.field_6406);
			if (foxEntity != null) {
				ServerPlayerEntity serverPlayerEntity = this.field_6404.method_6478();
				ServerPlayerEntity serverPlayerEntity2 = this.field_6406.method_6478();
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
					serverPlayerEntity3.method_7281(Stats.field_15410);
					Criterions.BRED_ANIMALS.method_855(serverPlayerEntity3, this.field_6404, this.field_6406, foxEntity);
				}

				int i = 6000;
				this.field_6404.setBreedingAge(6000);
				this.field_6406.setBreedingAge(6000);
				this.field_6404.resetLoveTicks();
				this.field_6406.resetLoveTicks();
				foxEntity.setBreedingAge(-24000);
				foxEntity.setPositionAndAngles(this.field_6404.x, this.field_6404.y, this.field_6404.z, 0.0F, 0.0F);
				this.field_6405.spawnEntity(foxEntity);
				Random random = this.field_6404.getRand();
				this.method_6251(random);
				if (this.field_6405.getGameRules().getBoolean("doMobLoot")) {
					this.field_6405.spawnEntity(new ExperienceOrbEntity(this.field_6405, this.field_6404.x, this.field_6404.y, this.field_6404.z, random.nextInt(7) + 1));
				}
			}
		}
	}

	class MoveToHuntGoal extends Goal {
		public MoveToHuntGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			if (FoxEntity.this.isSleeping()) {
				return false;
			} else {
				LivingEntity livingEntity = FoxEntity.this.getTarget();
				return livingEntity != null
					&& FoxEntity.CHICKEN_AND_RABBIT_FILTER.test(livingEntity)
					&& FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0
					&& !FoxEntity.this.isCrouching()
					&& !FoxEntity.this.isRollingHead()
					&& !FoxEntity.this.field_6282;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
		}

		@Override
		public void onRemove() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
				FoxEntity.this.setRollingHead(true);
				FoxEntity.this.setCrouching(true);
				FoxEntity.this.method_5942().stop();
				FoxEntity.this.method_5988().lookAt(livingEntity, (float)FoxEntity.this.method_5986(), (float)FoxEntity.this.method_5978());
			} else {
				FoxEntity.this.setRollingHead(false);
				FoxEntity.this.setCrouching(false);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			FoxEntity.this.method_5988().lookAt(livingEntity, (float)FoxEntity.this.method_5986(), (float)FoxEntity.this.method_5978());
			if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
				FoxEntity.this.setRollingHead(true);
				FoxEntity.this.setCrouching(true);
				FoxEntity.this.method_5942().stop();
			} else {
				FoxEntity.this.method_5942().startMovingTo(livingEntity, 1.5);
			}
		}
	}

	class PickupItemGoal extends Goal {
		public PickupItemGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			if (!FoxEntity.this.method_6118(EquipmentSlot.HAND_MAIN).isEmpty()) {
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
				return !list.isEmpty() && FoxEntity.this.method_6118(EquipmentSlot.HAND_MAIN).isEmpty();
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = FoxEntity.this.field_6002
				.method_8390(ItemEntity.class, FoxEntity.this.method_5829().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			ItemStack itemStack = FoxEntity.this.method_6118(EquipmentSlot.HAND_MAIN);
			if (itemStack.isEmpty() && !list.isEmpty()) {
				FoxEntity.this.method_5942().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = FoxEntity.this.field_6002
				.method_8390(ItemEntity.class, FoxEntity.this.method_5829().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			if (!list.isEmpty()) {
				FoxEntity.this.method_5942().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}
	}

	class SitDownAndLookAroundGoal extends FoxEntity.CalmDownGoal {
		private double lookX;
		private double lookZ;
		private int timer;
		private int counter;

		public SitDownAndLookAroundGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.getAttacker() == null
				&& FoxEntity.this.getRand().nextFloat() < 0.02F
				&& !FoxEntity.this.isSleeping()
				&& FoxEntity.this.getTarget() == null
				&& FoxEntity.this.method_5942().isIdle()
				&& !this.canCalmDown();
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
			FoxEntity.this.method_5942().stop();
		}

		@Override
		public void onRemove() {
			FoxEntity.this.setSitting(false);
		}

		@Override
		public void tick() {
			this.timer--;
			if (this.timer <= 0) {
				this.counter--;
				this.chooseNewAngle();
			}

			FoxEntity.this.method_5988()
				.lookAt(
					FoxEntity.this.x + this.lookX,
					FoxEntity.this.y + (double)FoxEntity.this.getStandingEyeHeight(),
					FoxEntity.this.z + this.lookZ,
					(float)FoxEntity.this.method_5986(),
					(float)FoxEntity.this.method_5978()
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
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18406, Goal.class_4134.field_18407, Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.isWalking() && FoxEntity.this.pitch > 0.0F;
		}

		@Override
		public void start() {
			this.timer = 40;
		}

		@Override
		public void tick() {
			this.timer--;
			if (this.timer <= 0) {
				FoxEntity.this.setWalking(false);
			}
		}
	}

	public static enum Type {
		field_17996(0, "red", Biomes.field_9420, Biomes.field_9428, Biomes.field_9422, Biomes.field_9477, Biomes.field_9416, Biomes.field_9429, Biomes.field_9404),
		field_17997(1, "snow", Biomes.field_9454, Biomes.field_9425, Biomes.field_9437);

		private static final FoxEntity.Type[] field_17998 = (FoxEntity.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(FoxEntity.Type::getId))
			.toArray(FoxEntity.Type[]::new);
		private static final Map<String, FoxEntity.Type> byName = (Map<String, FoxEntity.Type>)Arrays.stream(values())
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
			return (FoxEntity.Type)byName.getOrDefault(string, field_17996);
		}

		public static FoxEntity.Type fromId(int i) {
			if (i < 0 || i > field_17998.length) {
				i = 0;
			}

			return field_17998[i];
		}

		public static FoxEntity.Type method_18313(Biome biome) {
			return field_17997.getBiomes().contains(biome) ? field_17997 : field_17996;
		}
	}

	public class WorriableEntityFilter implements Predicate<LivingEntity> {
		public boolean method_18303(LivingEntity livingEntity) {
			if (livingEntity instanceof FoxEntity) {
				return false;
			} else if (!(livingEntity instanceof ChickenEntity) && !(livingEntity instanceof RabbitEntity) && !(livingEntity instanceof HostileEntity)) {
				if (!(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative()) {
					return FoxEntity.this.canTrust(livingEntity.getUuid()) ? false : !livingEntity.isSleeping() && !livingEntity.isSneaking();
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
	}

	class class_4052 extends FollowParentGoal {
		private final FoxEntity field_18104;

		public class_4052(FoxEntity foxEntity2, double d) {
			super(foxEntity2, d);
			this.field_18104 = foxEntity2;
		}

		@Override
		public boolean canStart() {
			return !this.field_18104.isAggressive() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_18104.isAggressive() && super.shouldContinue();
		}

		@Override
		public void start() {
			this.field_18104.stopActions();
			super.start();
		}
	}
}
