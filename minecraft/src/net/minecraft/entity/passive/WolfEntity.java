package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class WolfEntity extends TameableEntity implements Angerable, VariantHolder<RegistryEntry<WolfVariant>> {
	private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<RegistryEntry<WolfVariant>> VARIANT = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.WOLF_VARIANT);
	public static final TargetPredicate.EntityPredicate FOLLOW_TAMED_PREDICATE = (entity, serverWorld) -> {
		EntityType<?> entityType = entity.getType();
		return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.FOX;
	};
	private static final float WILD_MAX_HEALTH = 8.0F;
	private static final float TAMED_MAX_HEALTH = 40.0F;
	private static final float field_49237 = 0.125F;
	public static final float field_52477 = (float) (Math.PI / 5);
	private float begAnimationProgress;
	private float lastBegAnimationProgress;
	private boolean furWet;
	private boolean canShakeWaterOff;
	private float shakeProgress;
	private float lastShakeProgress;
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
	@Nullable
	private UUID angryAt;

	public WolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
		super(entityType, world);
		this.setTamed(false, false);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new TameableEntity.TameableEscapeDangerGoal(1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new WolfEntity.AvoidLlamaGoal(this, LlamaEntity.class, 24.0F, 1.5, 1.5));
		this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
		this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(9, new WolfBegGoal(this, 8.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(4, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
		this.targetSelector.add(5, new UntamedActiveTargetGoal(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
		this.targetSelector.add(6, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
		this.targetSelector.add(7, new ActiveTargetGoal(this, AbstractSkeletonEntity.class, false));
		this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));
	}

	public Identifier getTextureId() {
		WolfVariant wolfVariant = this.getVariant().value();
		if (this.isTamed()) {
			return wolfVariant.getTameTextureId();
		} else {
			return this.hasAngerTime() ? wolfVariant.getAngryTextureId() : wolfVariant.getWildTextureId();
		}
	}

	public RegistryEntry<WolfVariant> getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	public void setVariant(RegistryEntry<WolfVariant> registryEntry) {
		this.dataTracker.set(VARIANT, registryEntry);
	}

	public static DefaultAttributeContainer.Builder createWolfAttributes() {
		return AnimalEntity.createAnimalAttributes()
			.add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.MAX_HEALTH, 8.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 4.0);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		DynamicRegistryManager dynamicRegistryManager = this.getRegistryManager();
		Registry<WolfVariant> registry = dynamicRegistryManager.getOrThrow(RegistryKeys.WOLF_VARIANT);
		builder.add(VARIANT, (RegistryEntry<WolfVariant>)registry.getOptional(WolfVariants.DEFAULT).or(registry::getDefaultEntry).orElseThrow());
		builder.add(BEGGING, false);
		builder.add(COLLAR_COLOR, DyeColor.RED.getId());
		builder.add(ANGER_TIME, 0);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("CollarColor", (byte)this.getCollarColor().getId());
		this.getVariant().getKey().ifPresent(registryKey -> nbt.putString("variant", registryKey.getValue().toString()));
		this.writeAngerToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		Optional.ofNullable(Identifier.tryParse(nbt.getString("variant")))
			.map(variantId -> RegistryKey.of(RegistryKeys.WOLF_VARIANT, variantId))
			.flatMap(variantKey -> this.getRegistryManager().getOrThrow(RegistryKeys.WOLF_VARIANT).getOptional(variantKey))
			.ifPresent(this::setVariant);
		if (nbt.contains("CollarColor", NbtElement.NUMBER_TYPE)) {
			this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
		}

		this.readAngerFromNbt(this.getWorld(), nbt);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
		RegistryEntry<WolfVariant> registryEntry2;
		if (entityData instanceof WolfEntity.WolfData wolfData) {
			registryEntry2 = wolfData.variant;
		} else {
			registryEntry2 = WolfVariants.fromBiome(this.getRegistryManager(), registryEntry);
			entityData = new WolfEntity.WolfData(registryEntry2);
		}

		this.setVariant(registryEntry2);
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (this.hasAngerTime()) {
			return SoundEvents.ENTITY_WOLF_GROWL;
		} else if (this.random.nextInt(3) == 0) {
			return this.isTamed() && this.getHealth() < 20.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
		} else {
			return SoundEvents.ENTITY_WOLF_AMBIENT;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.shouldArmorAbsorbDamage(source) ? SoundEvents.ITEM_WOLF_ARMOR_DAMAGE : SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.getWorld().isClient && this.furWet && !this.canShakeWaterOff && !this.isNavigating() && this.isOnGround()) {
			this.canShakeWaterOff = true;
			this.shakeProgress = 0.0F;
			this.lastShakeProgress = 0.0F;
			this.getWorld().sendEntityStatus(this, EntityStatuses.SHAKE_OFF_WATER);
		}

		if (!this.getWorld().isClient) {
			this.tickAngerLogic((ServerWorld)this.getWorld(), true);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isAlive()) {
			this.lastBegAnimationProgress = this.begAnimationProgress;
			if (this.isBegging()) {
				this.begAnimationProgress = this.begAnimationProgress + (1.0F - this.begAnimationProgress) * 0.4F;
			} else {
				this.begAnimationProgress = this.begAnimationProgress + (0.0F - this.begAnimationProgress) * 0.4F;
			}

			if (this.isWet()) {
				this.furWet = true;
				if (this.canShakeWaterOff && !this.getWorld().isClient) {
					this.getWorld().sendEntityStatus(this, EntityStatuses.RESET_WOLF_SHAKE);
					this.resetShake();
				}
			} else if ((this.furWet || this.canShakeWaterOff) && this.canShakeWaterOff) {
				if (this.shakeProgress == 0.0F) {
					this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
					this.emitGameEvent(GameEvent.ENTITY_ACTION);
				}

				this.lastShakeProgress = this.shakeProgress;
				this.shakeProgress += 0.05F;
				if (this.lastShakeProgress >= 2.0F) {
					this.furWet = false;
					this.canShakeWaterOff = false;
					this.lastShakeProgress = 0.0F;
					this.shakeProgress = 0.0F;
				}

				if (this.shakeProgress > 0.4F) {
					float f = (float)this.getY();
					int i = (int)(MathHelper.sin((this.shakeProgress - 0.4F) * (float) Math.PI) * 7.0F);
					Vec3d vec3d = this.getVelocity();

					for (int j = 0; j < i; j++) {
						float g = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						this.getWorld().addParticle(ParticleTypes.SPLASH, this.getX() + (double)g, (double)(f + 0.8F), this.getZ() + (double)h, vec3d.x, vec3d.y, vec3d.z);
					}
				}
			}
		}
	}

	private void resetShake() {
		this.canShakeWaterOff = false;
		this.shakeProgress = 0.0F;
		this.lastShakeProgress = 0.0F;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.furWet = false;
		this.canShakeWaterOff = false;
		this.lastShakeProgress = 0.0F;
		this.shakeProgress = 0.0F;
		super.onDeath(damageSource);
	}

	/**
	 * Returns this wolf's brightness multiplier based on the fur wetness.
	 * <p>
	 * The brightness multiplier represents how much darker the wolf gets while its fur is wet. The multiplier changes (from 0.75 to 1.0 incrementally) when a wolf shakes.
	 * 
	 * @return Brightness as a float value between 0.75 and 1.0.
	 * @see net.minecraft.client.render.entity.model.TintableAnimalModel#setColorMultiplier(float, float, float)
	 * 
	 * @param tickDelta progress for linearly interpolating between the previous and current game state
	 */
	public float getFurWetBrightnessMultiplier(float tickDelta) {
		return !this.furWet ? 1.0F : Math.min(0.75F + MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress) / 2.0F * 0.25F, 1.0F);
	}

	public float getShakeProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress);
	}

	public float getBegAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15F * (float) Math.PI;
	}

	@Override
	public int getMaxLookPitchChange() {
		return this.isInSittingPose() ? 20 : super.getMaxLookPitchChange();
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.isInvulnerableTo(world, source)) {
			return false;
		} else {
			this.setSitting(false);
			return super.damage(world, source, amount);
		}
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot) {
		return true;
	}

	@Override
	protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
		if (!this.shouldArmorAbsorbDamage(source)) {
			super.applyDamage(world, source, amount);
		} else {
			ItemStack itemStack = this.getBodyArmor();
			int i = itemStack.getDamage();
			int j = itemStack.getMaxDamage();
			itemStack.damage(MathHelper.ceil(amount), this, EquipmentSlot.BODY);
			if (Cracks.WOLF_ARMOR.getCrackLevel(i, j) != Cracks.WOLF_ARMOR.getCrackLevel(this.getBodyArmor())) {
				this.playSoundIfNotSilent(SoundEvents.ITEM_WOLF_ARMOR_CRACK);
				world.spawnParticles(
					new ItemStackParticleEffect(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE.getDefaultStack()),
					this.getX(),
					this.getY() + 1.0,
					this.getZ(),
					20,
					0.2,
					0.1,
					0.2,
					0.1
				);
			}
		}
	}

	private boolean shouldArmorAbsorbDamage(DamageSource source) {
		return this.getBodyArmor().isOf(Items.WOLF_ARMOR) && !source.isIn(DamageTypeTags.BYPASSES_WOLF_ARMOR);
	}

	@Override
	protected void updateAttributesForTamed() {
		if (this.isTamed()) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
			this.setHealth(40.0F);
		} else {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		}
	}

	@Override
	protected void damageArmor(DamageSource source, float amount) {
		this.damageEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.BODY});
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.isTamed()) {
			if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
				this.eat(player, hand, itemStack);
				FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
				float f = foodComponent != null ? (float)foodComponent.nutrition() : 1.0F;
				this.heal(2.0F * f);
				return ActionResult.SUCCESS;
			} else {
				if (item instanceof DyeItem dyeItem && this.isOwner(player)) {
					DyeColor dyeColor = dyeItem.getColor();
					if (dyeColor != this.getCollarColor()) {
						this.setCollarColor(dyeColor);
						itemStack.decrementUnlessCreative(1, player);
						return ActionResult.SUCCESS;
					}

					return super.interactMob(player, hand);
				}

				if (this.canEquip(itemStack, EquipmentSlot.BODY) && !this.isWearingBodyArmor() && this.isOwner(player) && !this.isBaby()) {
					this.equipBodyArmor(itemStack.copyWithCount(1));
					itemStack.decrementUnlessCreative(1, player);
					return ActionResult.SUCCESS;
				} else if (itemStack.isOf(Items.SHEARS)
					&& this.isOwner(player)
					&& this.isWearingBodyArmor()
					&& (!EnchantmentHelper.hasAnyEnchantmentsWith(this.getBodyArmor(), EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
					itemStack.damage(1, player, getSlotForHand(hand));
					this.playSoundIfNotSilent(SoundEvents.ITEM_ARMOR_UNEQUIP_WOLF);
					ItemStack itemStack2 = this.getBodyArmor();
					this.equipBodyArmor(ItemStack.EMPTY);
					if (this.getWorld() instanceof ServerWorld serverWorld) {
						this.dropStack(serverWorld, itemStack2);
					}

					return ActionResult.SUCCESS;
				} else if (this.isInSittingPose()
					&& this.isWearingBodyArmor()
					&& this.isOwner(player)
					&& this.getBodyArmor().isDamaged()
					&& this.getBodyArmor().canRepairWith(itemStack)) {
					itemStack.decrement(1);
					this.playSoundIfNotSilent(SoundEvents.ITEM_WOLF_ARMOR_REPAIR);
					ItemStack itemStack2 = this.getBodyArmor();
					int i = (int)((float)itemStack2.getMaxDamage() * 0.125F);
					itemStack2.setDamage(Math.max(0, itemStack2.getDamage() - i));
					return ActionResult.SUCCESS;
				} else {
					ActionResult actionResult = super.interactMob(player, hand);
					if (!actionResult.isAccepted() && this.isOwner(player)) {
						this.setSitting(!this.isSitting());
						this.jumping = false;
						this.navigation.stop();
						this.setTarget(null);
						return ActionResult.SUCCESS.noIncrementStat();
					} else {
						return actionResult;
					}
				}
			}
		} else if (!this.getWorld().isClient && itemStack.isOf(Items.BONE) && !this.hasAngerTime()) {
			itemStack.decrementUnlessCreative(1, player);
			this.tryTame(player);
			return ActionResult.SUCCESS_SERVER;
		} else {
			return super.interactMob(player, hand);
		}
	}

	private void tryTame(PlayerEntity player) {
		if (this.random.nextInt(3) == 0) {
			this.setOwner(player);
			this.navigation.stop();
			this.setTarget(null);
			this.setSitting(true);
			this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
		} else {
			this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.SHAKE_OFF_WATER) {
			this.canShakeWaterOff = true;
			this.shakeProgress = 0.0F;
			this.lastShakeProgress = 0.0F;
		} else if (status == EntityStatuses.RESET_WOLF_SHAKE) {
			this.resetShake();
		} else {
			super.handleStatus(status);
		}
	}

	public float getTailAngle() {
		if (this.hasAngerTime()) {
			return 1.5393804F;
		} else if (this.isTamed()) {
			float f = this.getMaxHealth();
			float g = (f - this.getHealth()) / f;
			return (0.55F - g * 0.4F) * (float) Math.PI;
		} else {
			return (float) (Math.PI / 5);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.WOLF_FOOD);
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	public int getAngerTime() {
		return this.dataTracker.get(ANGER_TIME);
	}

	@Override
	public void setAngerTime(int angerTime) {
		this.dataTracker.set(ANGER_TIME, angerTime);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	@Nullable
	@Override
	public UUID getAngryAt() {
		return this.angryAt;
	}

	@Override
	public void setAngryAt(@Nullable UUID angryAt) {
		this.angryAt = angryAt;
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
	}

	private void setCollarColor(DyeColor color) {
		this.dataTracker.set(COLLAR_COLOR, color.getId());
	}

	@Nullable
	public WolfEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		WolfEntity wolfEntity = EntityType.WOLF.create(serverWorld, SpawnReason.BREEDING);
		if (wolfEntity != null && passiveEntity instanceof WolfEntity wolfEntity2) {
			if (this.random.nextBoolean()) {
				wolfEntity.setVariant(this.getVariant());
			} else {
				wolfEntity.setVariant(wolfEntity2.getVariant());
			}

			if (this.isTamed()) {
				wolfEntity.setOwnerUuid(this.getOwnerUuid());
				wolfEntity.setTamed(true, true);
				if (this.random.nextBoolean()) {
					wolfEntity.setCollarColor(this.getCollarColor());
				} else {
					wolfEntity.setCollarColor(wolfEntity2.getCollarColor());
				}
			}
		}

		return wolfEntity;
	}

	public void setBegging(boolean begging) {
		this.dataTracker.set(BEGGING, begging);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(other instanceof WolfEntity wolfEntity)) {
			return false;
		} else if (!wolfEntity.isTamed()) {
			return false;
		} else {
			return wolfEntity.isInSittingPose() ? false : this.isInLove() && wolfEntity.isInLove();
		}
	}

	public boolean isBegging() {
		return this.dataTracker.get(BEGGING);
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (target instanceof CreeperEntity || target instanceof GhastEntity || target instanceof ArmorStandEntity) {
			return false;
		} else if (target instanceof WolfEntity wolfEntity) {
			return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
		} else {
			if (target instanceof PlayerEntity playerEntity && owner instanceof PlayerEntity playerEntity2 && !playerEntity2.shouldDamagePlayer(playerEntity)) {
				return false;
			}

			if (target instanceof AbstractHorseEntity abstractHorseEntity && abstractHorseEntity.isTame()) {
				return false;
			}

			if (target instanceof TameableEntity tameableEntity && tameableEntity.isTamed()) {
				return false;
			}

			return true;
		}
	}

	@Override
	public boolean canBeLeashed() {
		return !this.hasAngerTime();
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	public static boolean canSpawn(EntityType<WolfEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.WOLVES_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	class AvoidLlamaGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final WolfEntity wolf;

		public AvoidLlamaGoal(final WolfEntity wolf, final Class<T> fleeFromType, final float distance, final double slowSpeed, final double fastSpeed) {
			super(wolf, fleeFromType, distance, slowSpeed, fastSpeed);
			this.wolf = wolf;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.targetEntity instanceof LlamaEntity ? !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity) : false;
		}

		private boolean isScaredOf(LlamaEntity llama) {
			return llama.getStrength() >= WolfEntity.this.random.nextInt(5);
		}

		@Override
		public void start() {
			WolfEntity.this.setTarget(null);
			super.start();
		}

		@Override
		public void tick() {
			WolfEntity.this.setTarget(null);
			super.tick();
		}
	}

	public static class WolfData extends PassiveEntity.PassiveData {
		public final RegistryEntry<WolfVariant> variant;

		public WolfData(RegistryEntry<WolfVariant> variant) {
			super(false);
			this.variant = variant;
		}
	}
}
