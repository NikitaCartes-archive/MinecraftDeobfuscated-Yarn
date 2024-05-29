package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public abstract class PlayerEntity extends LivingEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Arm DEFAULT_MAIN_ARM = Arm.RIGHT;
	public static final int field_46175 = 0;
	public static final int field_30644 = 20;
	public static final int field_30645 = 100;
	public static final int field_30646 = 10;
	public static final int field_30647 = 200;
	public static final int field_49734 = 499;
	public static final int field_49735 = 500;
	public static final float field_47819 = 4.5F;
	public static final float field_47820 = 3.0F;
	public static final float field_30648 = 1.5F;
	public static final float field_30649 = 0.6F;
	public static final float field_30650 = 0.6F;
	public static final float DEFAULT_EYE_HEIGHT = 1.62F;
	private static final int field_52222 = 40;
	public static final Vec3d VEHICLE_ATTACHMENT_POS = new Vec3d(0.0, 0.6, 0.0);
	public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6F, 1.8F)
		.withEyeHeight(1.62F)
		.withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, VEHICLE_ATTACHMENT_POS));
	private static final Map<EntityPose, EntityDimensions> POSE_DIMENSIONS = ImmutableMap.<EntityPose, EntityDimensions>builder()
		.put(EntityPose.STANDING, STANDING_DIMENSIONS)
		.put(EntityPose.SLEEPING, SLEEPING_DIMENSIONS)
		.put(EntityPose.FALL_FLYING, EntityDimensions.changing(0.6F, 0.6F).withEyeHeight(0.4F))
		.put(EntityPose.SWIMMING, EntityDimensions.changing(0.6F, 0.6F).withEyeHeight(0.4F))
		.put(EntityPose.SPIN_ATTACK, EntityDimensions.changing(0.6F, 0.6F).withEyeHeight(0.4F))
		.put(
			EntityPose.CROUCHING,
			EntityDimensions.changing(0.6F, 1.5F)
				.withEyeHeight(1.27F)
				.withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, VEHICLE_ATTACHMENT_POS))
		)
		.put(EntityPose.DYING, EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(1.62F))
		.build();
	private static final TrackedData<Float> ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	protected static final TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private long shoulderEntityAddedTime;
	final PlayerInventory inventory = new PlayerInventory(this);
	protected EnderChestInventory enderChestInventory = new EnderChestInventory();
	public final PlayerScreenHandler playerScreenHandler;
	public ScreenHandler currentScreenHandler;
	protected HungerManager hungerManager = new HungerManager();
	protected int abilityResyncCountdown;
	public float prevStrideDistance;
	public float strideDistance;
	public int experiencePickUpDelay;
	public double prevCapeX;
	public double prevCapeY;
	public double prevCapeZ;
	public double capeX;
	public double capeY;
	public double capeZ;
	private int sleepTimer;
	protected boolean isSubmergedInWater;
	private final PlayerAbilities abilities = new PlayerAbilities();
	public int experienceLevel;
	public int totalExperience;
	public float experienceProgress;
	protected int enchantmentTableSeed;
	protected final float field_7509 = 0.02F;
	private int lastPlayedLevelUpSoundTime;
	private final GameProfile gameProfile;
	private boolean reducedDebugInfo;
	private ItemStack selectedItem = ItemStack.EMPTY;
	private final ItemCooldownManager itemCooldownManager = this.createCooldownManager();
	private Optional<GlobalPos> lastDeathPos = Optional.empty();
	@Nullable
	public FishingBobberEntity fishHook;
	protected float damageTiltYaw;
	@Nullable
	public Vec3d currentExplosionImpactPos;
	@Nullable
	public Entity explodedBy;
	private boolean ignoreFallDamageFromCurrentExplosion;
	private int currentExplosionResetGraceTime;

	public PlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(EntityType.PLAYER, world);
		this.setUuid(gameProfile.getId());
		this.gameProfile = gameProfile;
		this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !world.isClient, this);
		this.currentScreenHandler = this.playerScreenHandler;
		this.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5, yaw, 0.0F);
		this.field_6215 = 180.0F;
	}

	public boolean isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode) {
		if (!gameMode.isBlockBreakingRestricted()) {
			return false;
		} else if (gameMode == GameMode.SPECTATOR) {
			return true;
		} else if (this.canModifyBlocks()) {
			return false;
		} else {
			ItemStack itemStack = this.getMainHandStack();
			return itemStack.isEmpty() || !itemStack.canBreak(new CachedBlockPosition(world, pos, false));
		}
	}

	public static DefaultAttributeContainer.Builder createPlayerAttributes() {
		return LivingEntity.createLivingAttributes()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
			.add(EntityAttributes.GENERIC_ATTACK_SPEED)
			.add(EntityAttributes.GENERIC_LUCK)
			.add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, 4.5)
			.add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, 3.0)
			.add(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)
			.add(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED)
			.add(EntityAttributes.PLAYER_SNEAKING_SPEED)
			.add(EntityAttributes.PLAYER_MINING_EFFICIENCY)
			.add(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(ABSORPTION_AMOUNT, 0.0F);
		builder.add(SCORE, 0);
		builder.add(PLAYER_MODEL_PARTS, (byte)0);
		builder.add(MAIN_ARM, (byte)DEFAULT_MAIN_ARM.getId());
		builder.add(LEFT_SHOULDER_ENTITY, new NbtCompound());
		builder.add(RIGHT_SHOULDER_ENTITY, new NbtCompound());
	}

	@Override
	public void tick() {
		this.noClip = this.isSpectator();
		if (this.isSpectator()) {
			this.setOnGround(false);
		}

		if (this.experiencePickUpDelay > 0) {
			this.experiencePickUpDelay--;
		}

		if (this.isSleeping()) {
			this.sleepTimer++;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.getWorld().isClient && this.getWorld().isDay()) {
				this.wakeUp(false, true);
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		this.updateWaterSubmersionState();
		super.tick();
		if (!this.getWorld().isClient && this.currentScreenHandler != null && !this.currentScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.currentScreenHandler = this.playerScreenHandler;
		}

		this.updateCapeAngles();
		if (!this.getWorld().isClient) {
			this.hungerManager.update(this);
			this.incrementStat(Stats.PLAY_TIME);
			this.incrementStat(Stats.TOTAL_WORLD_TIME);
			if (this.isAlive()) {
				this.incrementStat(Stats.TIME_SINCE_DEATH);
			}

			if (this.isSneaky()) {
				this.incrementStat(Stats.SNEAK_TIME);
			}

			if (!this.isSleeping()) {
				this.incrementStat(Stats.TIME_SINCE_REST);
			}
		}

		int i = 29999999;
		double d = MathHelper.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
		double e = MathHelper.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
		if (d != this.getX() || e != this.getZ()) {
			this.setPosition(d, this.getY(), e);
		}

		this.lastAttackedTicks++;
		ItemStack itemStack = this.getMainHandStack();
		if (!ItemStack.areEqual(this.selectedItem, itemStack)) {
			if (!ItemStack.areItemsEqual(this.selectedItem, itemStack)) {
				this.resetLastAttackedTicks();
			}

			this.selectedItem = itemStack.copy();
		}

		this.updateTurtleHelmet();
		this.itemCooldownManager.update();
		this.updatePose();
		if (this.currentExplosionResetGraceTime > 0) {
			this.currentExplosionResetGraceTime--;
		}
	}

	@Override
	protected float getMaxRelativeHeadRotation() {
		return this.isBlocking() ? 15.0F : super.getMaxRelativeHeadRotation();
	}

	public boolean shouldCancelInteraction() {
		return this.isSneaking();
	}

	protected boolean shouldDismount() {
		return this.isSneaking();
	}

	protected boolean clipAtLedge() {
		return this.isSneaking();
	}

	protected boolean updateWaterSubmersionState() {
		this.isSubmergedInWater = this.isSubmergedIn(FluidTags.WATER);
		return this.isSubmergedInWater;
	}

	private void updateTurtleHelmet() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (itemStack.isOf(Items.TURTLE_HELMET) && !this.isSubmergedIn(FluidTags.WATER)) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
		}
	}

	protected ItemCooldownManager createCooldownManager() {
		return new ItemCooldownManager();
	}

	private void updateCapeAngles() {
		this.prevCapeX = this.capeX;
		this.prevCapeY = this.capeY;
		this.prevCapeZ = this.capeZ;
		double d = this.getX() - this.capeX;
		double e = this.getY() - this.capeY;
		double f = this.getZ() - this.capeZ;
		double g = 10.0;
		if (d > 10.0) {
			this.capeX = this.getX();
			this.prevCapeX = this.capeX;
		}

		if (f > 10.0) {
			this.capeZ = this.getZ();
			this.prevCapeZ = this.capeZ;
		}

		if (e > 10.0) {
			this.capeY = this.getY();
			this.prevCapeY = this.capeY;
		}

		if (d < -10.0) {
			this.capeX = this.getX();
			this.prevCapeX = this.capeX;
		}

		if (f < -10.0) {
			this.capeZ = this.getZ();
			this.prevCapeZ = this.capeZ;
		}

		if (e < -10.0) {
			this.capeY = this.getY();
			this.prevCapeY = this.capeY;
		}

		this.capeX += d * 0.25;
		this.capeZ += f * 0.25;
		this.capeY += e * 0.25;
	}

	protected void updatePose() {
		if (this.canChangeIntoPose(EntityPose.SWIMMING)) {
			EntityPose entityPose;
			if (this.isFallFlying()) {
				entityPose = EntityPose.FALL_FLYING;
			} else if (this.isSleeping()) {
				entityPose = EntityPose.SLEEPING;
			} else if (this.isSwimming()) {
				entityPose = EntityPose.SWIMMING;
			} else if (this.isUsingRiptide()) {
				entityPose = EntityPose.SPIN_ATTACK;
			} else if (this.isSneaking() && !this.abilities.flying) {
				entityPose = EntityPose.CROUCHING;
			} else {
				entityPose = EntityPose.STANDING;
			}

			EntityPose entityPose2;
			if (this.isSpectator() || this.hasVehicle() || this.canChangeIntoPose(entityPose)) {
				entityPose2 = entityPose;
			} else if (this.canChangeIntoPose(EntityPose.CROUCHING)) {
				entityPose2 = EntityPose.CROUCHING;
			} else {
				entityPose2 = EntityPose.SWIMMING;
			}

			this.setPose(entityPose2);
		}
	}

	protected boolean canChangeIntoPose(EntityPose pose) {
		return this.getWorld().isSpaceEmpty(this, this.getDimensions(pose).getBoxAt(this.getPos()).contract(1.0E-7));
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_PLAYER_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_PLAYER_SPLASH;
	}

	@Override
	protected SoundEvent getHighSpeedSplashSound() {
		return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
	}

	@Override
	public int getDefaultPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(SoundEvent sound, float volume, float pitch) {
		this.getWorld().playSound(this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
	}

	/**
	 * Plays {@code sound} to this player <strong>only</strong>.
	 * 
	 * <p>Use {@link #playSound(SoundEvent, float, float)} to play sound that can be heard by
	 * nearby players. Unlike that method, this method should be called on only one side
	 * (i.e. either the server or the client, alone).
	 */
	public void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.PLAYERS;
	}

	@Override
	protected int getBurningDuration() {
		return 20;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.CONSUME_ITEM) {
			this.consumeItem();
		} else if (status == EntityStatuses.USE_FULL_DEBUG_INFO) {
			this.reducedDebugInfo = false;
		} else if (status == EntityStatuses.USE_REDUCED_DEBUG_INFO) {
			this.reducedDebugInfo = true;
		} else {
			super.handleStatus(status);
		}
	}

	/**
	 * Closes the currently open {@linkplain net.minecraft.client.gui.screen.ingame.HandledScreen
	 * handled screen}.
	 * 
	 * <p>This method can be called on either logical side, and it will synchronize
	 * the closing automatically to the other.
	 */
	protected void closeHandledScreen() {
		this.currentScreenHandler = this.playerScreenHandler;
	}

	/**
	 * Runs closing tasks for the current screen handler and
	 * sets it to the {@link #playerScreenHandler}.
	 */
	protected void onHandledScreenClosed() {
	}

	@Override
	public void tickRiding() {
		if (!this.getWorld().isClient && this.shouldDismount() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			super.tickRiding();
			this.prevStrideDistance = this.strideDistance;
			this.strideDistance = 0.0F;
		}
	}

	@Override
	protected void tickNewAi() {
		super.tickNewAi();
		this.tickHandSwing();
		this.headYaw = this.getYaw();
	}

	@Override
	public void tickMovement() {
		if (this.abilityResyncCountdown > 0) {
			this.abilityResyncCountdown--;
		}

		if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
			if (this.getHealth() < this.getMaxHealth() && this.age % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.hungerManager.getSaturationLevel() < 20.0F && this.age % 20 == 0) {
				this.hungerManager.setSaturationLevel(this.hungerManager.getSaturationLevel() + 1.0F);
			}

			if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
				this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.prevStrideDistance = this.strideDistance;
		super.tickMovement();
		this.setMovementSpeed((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
		float f;
		if (this.isOnGround() && !this.isDead() && !this.isSwimming()) {
			f = Math.min(0.1F, (float)this.getVelocity().horizontalLength());
		} else {
			f = 0.0F;
		}

		this.strideDistance = this.strideDistance + (f - this.strideDistance) * 0.4F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			Box box;
			if (this.hasVehicle() && !this.getVehicle().isRemoved()) {
				box = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
			} else {
				box = this.getBoundingBox().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.getWorld().getOtherEntities(this, box);
			List<Entity> list2 = Lists.<Entity>newArrayList();

			for (Entity entity : list) {
				if (entity.getType() == EntityType.EXPERIENCE_ORB) {
					list2.add(entity);
				} else if (!entity.isRemoved()) {
					this.collideWithEntity(entity);
				}
			}

			if (!list2.isEmpty()) {
				this.collideWithEntity(Util.getRandom(list2, this.random));
			}
		}

		this.updateShoulderEntity(this.getShoulderEntityLeft());
		this.updateShoulderEntity(this.getShoulderEntityRight());
		if (!this.getWorld().isClient && (this.fallDistance > 0.5F || this.isTouchingWater()) || this.abilities.flying || this.isSleeping() || this.inPowderSnow) {
			this.dropShoulderEntities();
		}
	}

	private void updateShoulderEntity(@Nullable NbtCompound entityNbt) {
		if (entityNbt != null && (!entityNbt.contains("Silent") || !entityNbt.getBoolean("Silent")) && this.getWorld().random.nextInt(200) == 0) {
			String string = entityNbt.getString("id");
			EntityType.get(string)
				.filter(entityType -> entityType == EntityType.PARROT)
				.ifPresent(
					parrotType -> {
						if (!ParrotEntity.imitateNearbyMob(this.getWorld(), this)) {
							this.getWorld()
								.playSound(
									null,
									this.getX(),
									this.getY(),
									this.getZ(),
									ParrotEntity.getRandomSound(this.getWorld(), this.getWorld().random),
									this.getSoundCategory(),
									1.0F,
									ParrotEntity.getSoundPitch(this.getWorld().random)
								);
						}
					}
				);
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.onPlayerCollision(this);
	}

	public int getScore() {
		return this.dataTracker.get(SCORE);
	}

	public void setScore(int score) {
		this.dataTracker.set(SCORE, score);
	}

	public void addScore(int score) {
		int i = this.getScore();
		this.dataTracker.set(SCORE, i + score);
	}

	public void useRiptide(int riptideTicks, float riptideAttackDamage, ItemStack stack) {
		this.riptideTicks = riptideTicks;
		this.riptideAttackDamage = riptideAttackDamage;
		this.riptideStack = stack;
		if (!this.getWorld().isClient) {
			this.dropShoulderEntities();
			this.setLivingFlag(LivingEntity.USING_RIPTIDE_FLAG, true);
		}
	}

	@Nonnull
	@Override
	public ItemStack getWeaponStack() {
		return this.isUsingRiptide() && this.riptideStack != null ? this.riptideStack : super.getWeaponStack();
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		this.refreshPosition();
		if (!this.isSpectator() && this.getWorld() instanceof ServerWorld serverWorld) {
			this.drop(serverWorld, damageSource);
		}

		if (damageSource != null) {
			this.setVelocity(
				(double)(-MathHelper.cos((this.getDamageTiltYaw() + this.getYaw()) * (float) (Math.PI / 180.0)) * 0.1F),
				0.1F,
				(double)(-MathHelper.sin((this.getDamageTiltYaw() + this.getYaw()) * (float) (Math.PI / 180.0)) * 0.1F)
			);
		} else {
			this.setVelocity(0.0, 0.1, 0.0);
		}

		this.incrementStat(Stats.DEATHS);
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		this.extinguish();
		this.setOnFire(false);
		this.setLastDeathPos(Optional.of(GlobalPos.create(this.getWorld().getRegistryKey(), this.getBlockPos())));
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (!this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			this.vanishCursedItems();
			this.inventory.dropAll();
		}
	}

	protected void vanishCursedItems() {
		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);
			if (!itemStack.isEmpty() && EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
				this.inventory.removeStack(i);
			}
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return source.getType().effects().getSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	@Nullable
	public ItemEntity dropItem(ItemStack stack, boolean retainOwnership) {
		return this.dropItem(stack, false, retainOwnership);
	}

	/**
	 * @param throwRandomly if true, the item will be thrown in a random direction from the entity regardless of which direction the entity is facing
	 */
	@Nullable
	public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
		if (stack.isEmpty()) {
			return null;
		} else {
			if (this.getWorld().isClient) {
				this.swingHand(Hand.MAIN_HAND);
			}

			double d = this.getEyeY() - 0.3F;
			ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), d, this.getZ(), stack);
			itemEntity.setPickupDelay(40);
			if (retainOwnership) {
				itemEntity.setThrower(this);
			}

			if (throwRandomly) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.2F, (double)(MathHelper.cos(g) * f));
			} else {
				float f = 0.3F;
				float g = MathHelper.sin(this.getPitch() * (float) (Math.PI / 180.0));
				float h = MathHelper.cos(this.getPitch() * (float) (Math.PI / 180.0));
				float i = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
				float j = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
				float k = this.random.nextFloat() * (float) (Math.PI * 2);
				float l = 0.02F * this.random.nextFloat();
				itemEntity.setVelocity(
					(double)(-i * h * 0.3F) + Math.cos((double)k) * (double)l,
					(double)(-g * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
					(double)(j * h * 0.3F) + Math.sin((double)k) * (double)l
				);
			}

			return itemEntity;
		}
	}

	public float getBlockBreakingSpeed(BlockState block) {
		float f = this.inventory.getBlockBreakingSpeed(block);
		if (f > 1.0F) {
			f += (float)this.getAttributeValue(EntityAttributes.PLAYER_MINING_EFFICIENCY);
		}

		if (StatusEffectUtil.hasHaste(this)) {
			f *= 1.0F + (float)(StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2F;
		}

		if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
			f *= switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
				case 0 -> 0.3F;
				case 1 -> 0.09F;
				case 2 -> 0.0027F;
				default -> 8.1E-4F;
			};
		}

		f *= (float)this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
		if (this.isSubmergedIn(FluidTags.WATER)) {
			f *= (float)this.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).getValue();
		}

		if (!this.isOnGround()) {
			f /= 5.0F;
		}

		return f;
	}

	/**
	 * Determines whether the player is able to harvest drops from the specified block state.
	 * If a block requires a special tool, it will check
	 * whether the held item is effective for that block, otherwise
	 * it returns {@code true}.
	 * 
	 * @see net.minecraft.item.ItemStack#isSuitableFor(BlockState)
	 */
	public boolean canHarvest(BlockState state) {
		return !state.isToolRequired() || this.inventory.getMainHandStack().isSuitableFor(state);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setUuid(this.gameProfile.getId());
		NbtList nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
		this.inventory.readNbt(nbtList);
		this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
		this.sleepTimer = nbt.getShort("SleepTimer");
		this.experienceProgress = nbt.getFloat("XpP");
		this.experienceLevel = nbt.getInt("XpLevel");
		this.totalExperience = nbt.getInt("XpTotal");
		this.enchantmentTableSeed = nbt.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(nbt.getInt("Score"));
		this.hungerManager.readNbt(nbt);
		this.abilities.readNbt(nbt);
		this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkSpeed());
		if (nbt.contains("EnderItems", NbtElement.LIST_TYPE)) {
			this.enderChestInventory.readNbtList(nbt.getList("EnderItems", NbtElement.COMPOUND_TYPE), this.getRegistryManager());
		}

		if (nbt.contains("ShoulderEntityLeft", NbtElement.COMPOUND_TYPE)) {
			this.setShoulderEntityLeft(nbt.getCompound("ShoulderEntityLeft"));
		}

		if (nbt.contains("ShoulderEntityRight", NbtElement.COMPOUND_TYPE)) {
			this.setShoulderEntityRight(nbt.getCompound("ShoulderEntityRight"));
		}

		if (nbt.contains("LastDeathLocation", NbtElement.COMPOUND_TYPE)) {
			this.setLastDeathPos(GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("LastDeathLocation")).resultOrPartial(LOGGER::error));
		}

		if (nbt.contains("current_explosion_impact_pos", NbtElement.LIST_TYPE)) {
			Vec3d.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("current_explosion_impact_pos"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(currentExplosionImpactPos -> this.currentExplosionImpactPos = currentExplosionImpactPos);
		}

		this.ignoreFallDamageFromCurrentExplosion = nbt.getBoolean("ignore_fall_damage_from_current_explosion");
		this.currentExplosionResetGraceTime = nbt.getInt("current_impulse_context_reset_grace_time");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		NbtHelper.putDataVersion(nbt);
		nbt.put("Inventory", this.inventory.writeNbt(new NbtList()));
		nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		nbt.putShort("SleepTimer", (short)this.sleepTimer);
		nbt.putFloat("XpP", this.experienceProgress);
		nbt.putInt("XpLevel", this.experienceLevel);
		nbt.putInt("XpTotal", this.totalExperience);
		nbt.putInt("XpSeed", this.enchantmentTableSeed);
		nbt.putInt("Score", this.getScore());
		this.hungerManager.writeNbt(nbt);
		this.abilities.writeNbt(nbt);
		nbt.put("EnderItems", this.enderChestInventory.toNbtList(this.getRegistryManager()));
		if (!this.getShoulderEntityLeft().isEmpty()) {
			nbt.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
		}

		if (!this.getShoulderEntityRight().isEmpty()) {
			nbt.put("ShoulderEntityRight", this.getShoulderEntityRight());
		}

		this.getLastDeathPos()
			.flatMap(pos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(LOGGER::error))
			.ifPresent(pos -> nbt.put("LastDeathLocation", pos));
		if (this.currentExplosionImpactPos != null) {
			nbt.put("current_explosion_impact_pos", Vec3d.CODEC.encodeStart(NbtOps.INSTANCE, this.currentExplosionImpactPos).getOrThrow());
		}

		nbt.putBoolean("ignore_fall_damage_from_current_explosion", this.ignoreFallDamageFromCurrentExplosion);
		nbt.putInt("current_impulse_context_reset_grace_time", this.currentExplosionResetGraceTime);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		if (super.isInvulnerableTo(damageSource)) {
			return true;
		} else if (damageSource.isIn(DamageTypeTags.IS_DROWNING)) {
			return !this.getWorld().getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
		} else if (damageSource.isIn(DamageTypeTags.IS_FALL)) {
			return !this.getWorld().getGameRules().getBoolean(GameRules.FALL_DAMAGE);
		} else if (damageSource.isIn(DamageTypeTags.IS_FIRE)) {
			return !this.getWorld().getGameRules().getBoolean(GameRules.FIRE_DAMAGE);
		} else {
			return damageSource.isIn(DamageTypeTags.IS_FREEZING) ? !this.getWorld().getGameRules().getBoolean(GameRules.FREEZE_DAMAGE) : false;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.abilities.invulnerable && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.isDead()) {
				return false;
			} else {
				if (!this.getWorld().isClient) {
					this.dropShoulderEntities();
				}

				if (source.isScaledWithDifficulty()) {
					if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
						amount = 0.0F;
					}

					if (this.getWorld().getDifficulty() == Difficulty.EASY) {
						amount = Math.min(amount / 2.0F + 1.0F, amount);
					}

					if (this.getWorld().getDifficulty() == Difficulty.HARD) {
						amount = amount * 3.0F / 2.0F;
					}
				}

				return amount == 0.0F ? false : super.damage(source, amount);
			}
		}
	}

	@Override
	protected void takeShieldHit(LivingEntity attacker) {
		super.takeShieldHit(attacker);
		if (attacker.disablesShield()) {
			this.disableShield();
		}
	}

	@Override
	public boolean canTakeDamage() {
		return !this.getAbilities().invulnerable && super.canTakeDamage();
	}

	public boolean shouldDamagePlayer(PlayerEntity player) {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		AbstractTeam abstractTeam2 = player.getScoreboardTeam();
		if (abstractTeam == null) {
			return true;
		} else {
			return !abstractTeam.isEqual(abstractTeam2) ? true : abstractTeam.isFriendlyFireAllowed();
		}
	}

	@Override
	protected void damageArmor(DamageSource source, float amount) {
		this.damageEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
	}

	@Override
	protected void damageHelmet(DamageSource source, float amount) {
		this.damageEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.HEAD});
	}

	@Override
	protected void damageShield(float amount) {
		if (this.activeItemStack.isOf(Items.SHIELD)) {
			if (!this.getWorld().isClient) {
				this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
			}

			if (amount >= 3.0F) {
				int i = 1 + MathHelper.floor(amount);
				Hand hand = this.getActiveHand();
				this.activeItemStack.damage(i, this, getSlotForHand(hand));
				if (this.activeItemStack.isEmpty()) {
					if (hand == Hand.MAIN_HAND) {
						this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					} else {
						this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}

					this.activeItemStack = ItemStack.EMPTY;
					this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
				}
			}
		}
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			amount = this.applyArmorToDamage(source, amount);
			amount = this.modifyAppliedDamage(source, amount);
			float var7 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var7));
			float g = amount - var7;
			if (g > 0.0F && g < 3.4028235E37F) {
				this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
			}

			if (var7 != 0.0F) {
				this.addExhaustion(source.getExhaustion());
				this.getDamageTracker().onDamage(source, var7);
				this.setHealth(this.getHealth() - var7);
				if (var7 < 3.4028235E37F) {
					this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
				}

				this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
			}
		}
	}

	public boolean shouldFilterText() {
		return false;
	}

	public void openEditSignScreen(SignBlockEntity sign, boolean front) {
	}

	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
	}

	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
	}

	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
	}

	public void openJigsawScreen(JigsawBlockEntity jigsaw) {
	}

	public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) {
	}

	public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
		return OptionalInt.empty();
	}

	public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
	}

	/**
	 * Called when the player uses (defaults to right click) a writable or written
	 * book item.
	 * 
	 * <p>This can be called either on the client or the server player. Check {@code
	 * book} for whether this is a written or a writable book.
	 * 
	 * @implNote The writing of a writable book in vanilla is totally controlled by
	 * the client; the server cannot make the client open a book edit screen by
	 * making a server player use a writable book. Only when the client finishes
	 * writing a book it will send a {@linkplain net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket book update C2S packet}.
	 * 
	 * <p>Meanwhile, the reading of a written book is totally controlled and initiated
	 * by the server.
	 * 
	 * @param book the book
	 * @param hand the hand holding the book
	 */
	public void useBook(ItemStack book, Hand hand) {
	}

	public ActionResult interact(Entity entity, Hand hand) {
		if (this.isSpectator()) {
			if (entity instanceof NamedScreenHandlerFactory) {
				this.openHandledScreen((NamedScreenHandlerFactory)entity);
			}

			return ActionResult.PASS;
		} else {
			ItemStack itemStack = this.getStackInHand(hand);
			ItemStack itemStack2 = itemStack.copy();
			ActionResult actionResult = entity.interact(this, hand);
			if (actionResult.isAccepted()) {
				if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
					itemStack.setCount(itemStack2.getCount());
				}

				return actionResult;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					ActionResult actionResult2 = itemStack.useOnEntity(this, (LivingEntity)entity, hand);
					if (actionResult2.isAccepted()) {
						this.getWorld().emitGameEvent(GameEvent.ENTITY_INTERACT, entity.getPos(), GameEvent.Emitter.of(this));
						if (itemStack.isEmpty() && !this.abilities.creativeMode) {
							this.setStackInHand(hand, ItemStack.EMPTY);
						}

						return actionResult2;
					}
				}

				return ActionResult.PASS;
			}
		}
	}

	@Override
	public void dismountVehicle() {
		super.dismountVehicle();
		this.ridingCooldown = 0;
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.isSleeping();
	}

	@Override
	public boolean shouldSwimInFluids() {
		return !this.abilities.flying;
	}

	@Override
	protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
		float f = this.getStepHeight();
		if (!this.abilities.flying && !(movement.y > 0.0) && (type == MovementType.SELF || type == MovementType.PLAYER) && this.clipAtLedge() && this.method_30263(f)
			)
		 {
			double d = movement.x;
			double e = movement.z;
			double g = 0.05;
			double h = Math.signum(d) * 0.05;

			double i;
			for (i = Math.signum(e) * 0.05; d != 0.0 && this.isSpaceAroundPlayerEmpty(d, 0.0, f); d -= h) {
				if (Math.abs(d) <= 0.05) {
					d = 0.0;
					break;
				}
			}

			while (e != 0.0 && this.isSpaceAroundPlayerEmpty(0.0, e, f)) {
				if (Math.abs(e) <= 0.05) {
					e = 0.0;
					break;
				}

				e -= i;
			}

			while (d != 0.0 && e != 0.0 && this.isSpaceAroundPlayerEmpty(d, e, f)) {
				if (Math.abs(d) <= 0.05) {
					d = 0.0;
				} else {
					d -= h;
				}

				if (Math.abs(e) <= 0.05) {
					e = 0.0;
				} else {
					e -= i;
				}
			}

			return new Vec3d(d, movement.y, e);
		} else {
			return movement;
		}
	}

	private boolean method_30263(float f) {
		return this.isOnGround() || this.fallDistance < f && !this.isSpaceAroundPlayerEmpty(0.0, 0.0, f - this.fallDistance);
	}

	private boolean isSpaceAroundPlayerEmpty(double offsetX, double offsetZ, float f) {
		Box box = this.getBoundingBox();
		return this.getWorld()
			.isSpaceEmpty(this, new Box(box.minX + offsetX, box.minY - (double)f - 1.0E-5F, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ));
	}

	public void attack(Entity target) {
		if (target.isAttackable()) {
			if (!target.handleAttack(this)) {
				float f = this.isUsingRiptide() ? this.riptideAttackDamage : (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
				ItemStack itemStack = this.getWeaponStack();
				DamageSource damageSource = this.getDamageSources().playerAttack(this);
				float g = this.getDamageAgainst(target, f, damageSource) - f;
				float h = this.getAttackCooldownProgress(0.5F);
				f *= 0.2F + h * h * 0.8F;
				g *= h;
				this.resetLastAttackedTicks();
				if (target.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE)
					&& target instanceof ProjectileEntity projectileEntity
					&& projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this, this, true)) {
					this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory());
					return;
				}

				if (f > 0.0F || g > 0.0F) {
					boolean bl = h > 0.9F;
					boolean bl2;
					if (this.isSprinting() && bl) {
						this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
						bl2 = true;
					} else {
						bl2 = false;
					}

					f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
					boolean bl3 = bl
						&& this.fallDistance > 0.0F
						&& !this.isOnGround()
						&& !this.isClimbing()
						&& !this.isTouchingWater()
						&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
						&& !this.hasVehicle()
						&& target instanceof LivingEntity
						&& !this.isSprinting();
					if (bl3) {
						f *= 1.5F;
					}

					float i = f + g;
					boolean bl4 = false;
					double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
					if (bl && !bl3 && !bl2 && this.isOnGround() && d < (double)this.getMovementSpeed()) {
						ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
						if (itemStack2.getItem() instanceof SwordItem) {
							bl4 = true;
						}
					}

					float j = 0.0F;
					if (target instanceof LivingEntity livingEntity) {
						j = livingEntity.getHealth();
					}

					Vec3d vec3d = target.getVelocity();
					boolean bl5 = target.damage(damageSource, i);
					if (bl5) {
						float k = this.getKnockbackAgainst(target, damageSource) + (bl2 ? 1.0F : 0.0F);
						if (k > 0.0F) {
							if (target instanceof LivingEntity livingEntity2) {
								livingEntity2.takeKnockback(
									(double)(k * 0.5F),
									(double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
									(double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
								);
							} else {
								target.addVelocity(
									(double)(-MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F),
									0.1,
									(double)(MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F)
								);
							}

							this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
							this.setSprinting(false);
						}

						if (bl4) {
							float l = 1.0F + (float)this.getAttributeValue(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO) * f;

							for (LivingEntity livingEntity3 : this.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
								if (livingEntity3 != this
									&& livingEntity3 != target
									&& !this.isTeammate(livingEntity3)
									&& (!(livingEntity3 instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity3).isMarker())
									&& this.squaredDistanceTo(livingEntity3) < 9.0) {
									float m = this.getDamageAgainst(livingEntity3, l, damageSource) * h;
									livingEntity3.takeKnockback(
										0.4F, (double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
									);
									livingEntity3.damage(damageSource, m);
									if (this.getWorld() instanceof ServerWorld serverWorld) {
										EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity3, damageSource);
									}
								}
							}

							this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
							this.spawnSweepAttackParticles();
						}

						if (target instanceof ServerPlayerEntity && target.velocityModified) {
							((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
							target.velocityModified = false;
							target.setVelocity(vec3d);
						}

						if (bl3) {
							this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
							this.addCritParticles(target);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
							} else {
								this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (g > 0.0F) {
							this.addEnchantedHitParticles(target);
						}

						this.onAttacking(target);
						Entity entity = target;
						if (target instanceof EnderDragonPart) {
							entity = ((EnderDragonPart)target).owner;
						}

						boolean bl6 = false;
						if (this.getWorld() instanceof ServerWorld serverWorld2) {
							if (entity instanceof LivingEntity livingEntity3x) {
								bl6 = itemStack.postHit(livingEntity3x, this);
							}

							EnchantmentHelper.onTargetDamaged(serverWorld2, target, damageSource);
						}

						if (!this.getWorld().isClient && !itemStack.isEmpty() && entity instanceof LivingEntity) {
							if (bl6) {
								itemStack.postDamageEntity((LivingEntity)entity, this);
							}

							if (itemStack.isEmpty()) {
								if (itemStack == this.getMainHandStack()) {
									this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
								} else {
									this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
								}
							}
						}

						if (target instanceof LivingEntity) {
							float n = j - ((LivingEntity)target).getHealth();
							this.increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
							if (this.getWorld() instanceof ServerWorld && n > 2.0F) {
								int o = (int)((double)n * 0.5);
								((ServerWorld)this.getWorld())
									.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), o, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.addExhaustion(0.1F);
					} else {
						this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
					}
				}
			}
		}
	}

	protected float getDamageAgainst(Entity target, float baseDamage, DamageSource damageSource) {
		return baseDamage;
	}

	@Override
	protected void attackLivingEntity(LivingEntity target) {
		this.attack(target);
	}

	public void disableShield() {
		this.getItemCooldownManager().set(Items.SHIELD, 100);
		this.clearActiveItem();
		this.getWorld().sendEntityStatus(this, EntityStatuses.BREAK_SHIELD);
	}

	public void addCritParticles(Entity target) {
	}

	public void addEnchantedHitParticles(Entity target) {
	}

	public void spawnSweepAttackParticles() {
		double d = (double)(-MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)));
		double e = (double)MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
		if (this.getWorld() instanceof ServerWorld) {
			((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
		}
	}

	public void requestRespawn() {
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		super.remove(reason);
		this.playerScreenHandler.onClosed(this);
		if (this.currentScreenHandler != null && this.shouldCloseHandledScreenOnRespawn()) {
			this.onHandledScreenClosed();
		}
	}

	public boolean isMainPlayer() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public PlayerInventory getInventory() {
		return this.inventory;
	}

	public PlayerAbilities getAbilities() {
		return this.abilities;
	}

	@Override
	public boolean isInCreativeMode() {
		return this.abilities.creativeMode;
	}

	/**
	 * Called when a player performs a {@link net.minecraft.screen.slot.SlotActionType#PICKUP
	 * pickup slot action} in a screen handler.
	 * 
	 * @implNote This is used by the client player to trigger bundle tutorials.
	 * 
	 * @param clickType the click type (mouse button used)
	 * @param slotStack the item stack in the clicked slot
	 * @param cursorStack the item stack on the player's cursor
	 */
	public void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType) {
	}

	public boolean shouldCloseHandledScreenOnRespawn() {
		return this.currentScreenHandler != this.playerScreenHandler;
	}

	/**
	 * Tries to start sleeping on a block.
	 * 
	 * @return an {@link com.mojang.datafixers.util.Either.Right Either.Right}
	 * if successful, otherwise an {@link com.mojang.datafixers.util.Either.Left
	 * Either.Left} containing the failure reason
	 * 
	 * @param pos the position of the bed block
	 */
	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
		this.sleep(pos);
		this.sleepTimer = 0;
		return Either.right(Unit.INSTANCE);
	}

	/**
	 * Wakes this player up.
	 * 
	 * @param updateSleepingPlayers if {@code true} and called on the logical server, sends sleeping status updates to all players
	 * @param skipSleepTimer if {@code true}, the {@linkplain #sleepTimer sleep timer} will be set straight to 0 instead of 100
	 */
	public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {
		super.wakeUp();
		if (this.getWorld() instanceof ServerWorld && updateSleepingPlayers) {
			((ServerWorld)this.getWorld()).updateSleepingPlayers();
		}

		this.sleepTimer = skipSleepTimer ? 0 : 100;
	}

	@Override
	public void wakeUp() {
		this.wakeUp(true, true);
	}

	/**
	 * {@return whether this player has been sleeping long enough to count towards
	 * resetting the time of day and weather of the server}
	 */
	public boolean canResetTimeBySleeping() {
		return this.isSleeping() && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	/**
	 * Adds a message to this player's HUD.
	 * 
	 * <p>If it's called on {@link net.minecraft.server.network.ServerPlayerEntity
	 * ServerPlayerEntity}, it sends a message to the client corresponding to
	 * this player so that the client can add a message to their HUD. If it's
	 * called on {@link net.minecraft.client.network.ClientPlayerEntity
	 * ClientPlayerEntity}, it just adds a message to their HUD.
	 * 
	 * @see net.minecraft.server.network.ServerPlayerEntity#sendMessage(Text, boolean)
	 * @see net.minecraft.client.network.ClientPlayerEntity#sendMessage(Text, boolean)
	 * @see net.minecraft.client.gui.hud.ChatHud#addMessage(Text)
	 * @see net.minecraft.client.gui.hud.InGameHud#setOverlayMessage
	 * 
	 * @param message the message to add
	 */
	public void sendMessage(Text message, boolean overlay) {
	}

	public void incrementStat(Identifier stat) {
		this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));
	}

	public void increaseStat(Identifier stat, int amount) {
		this.increaseStat(Stats.CUSTOM.getOrCreateStat(stat), amount);
	}

	public void incrementStat(Stat<?> stat) {
		this.increaseStat(stat, 1);
	}

	public void increaseStat(Stat<?> stat, int amount) {
	}

	public void resetStat(Stat<?> stat) {
	}

	public int unlockRecipes(Collection<RecipeEntry<?>> recipes) {
		return 0;
	}

	public void onRecipeCrafted(RecipeEntry<?> recipe, List<ItemStack> ingredients) {
	}

	public void unlockRecipes(List<Identifier> recipes) {
	}

	public int lockRecipes(Collection<RecipeEntry<?>> recipes) {
		return 0;
	}

	@Override
	public void jump() {
		super.jump();
		this.incrementStat(Stats.JUMP);
		if (this.isSprinting()) {
			this.addExhaustion(0.2F);
		} else {
			this.addExhaustion(0.05F);
		}
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isSwimming() && !this.hasVehicle()) {
			double d = this.getRotationVector().y;
			double e = d < -0.2 ? 0.085 : 0.06;
			if (d <= 0.0
				|| this.jumping
				|| !this.getWorld().getBlockState(BlockPos.ofFloored(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
				Vec3d vec3d = this.getVelocity();
				this.setVelocity(vec3d.add(0.0, (d - vec3d.y) * e, 0.0));
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double d = this.getVelocity().y;
			super.travel(movementInput);
			Vec3d vec3d2 = this.getVelocity();
			this.setVelocity(vec3d2.x, d * 0.6, vec3d2.z);
			this.onLanding();
			this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public void updateSwimming() {
		if (this.abilities.flying) {
			this.setSwimming(false);
		} else {
			super.updateSwimming();
		}
	}

	protected boolean doesNotSuffocate(BlockPos pos) {
		return !this.getWorld().getBlockState(pos).shouldSuffocate(this.getWorld(), pos);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (this.abilities.allowFlying) {
			return false;
		} else {
			if (fallDistance >= 2.0F) {
				this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)fallDistance * 100.0));
			}

			if (this.ignoreFallDamageFromCurrentExplosion && this.currentExplosionImpactPos != null) {
				double d = this.currentExplosionImpactPos.y;
				this.tryClearCurrentExplosion();
				return d < this.getY() ? false : super.handleFallDamage((float)(d - this.getY()), damageMultiplier, damageSource);
			} else {
				return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
			}
		}
	}

	public boolean checkFallFlying() {
		if (!this.isOnGround() && !this.isFallFlying() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
				this.startFallFlying();
				return true;
			}
		}

		return false;
	}

	public void startFallFlying() {
		this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, true);
	}

	public void stopFallFlying() {
		this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, true);
		this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
	}

	@Override
	protected void onSwimmingStart() {
		if (!this.isSpectator()) {
			super.onSwimmingStart();
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (this.isTouchingWater()) {
			this.playSwimSound();
			this.playSecondaryStepSound(state);
		} else {
			BlockPos blockPos = this.getStepSoundPos(pos);
			if (!pos.equals(blockPos)) {
				BlockState blockState = this.getWorld().getBlockState(blockPos);
				if (blockState.isIn(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
					this.playCombinationStepSounds(blockState, state);
				} else {
					super.playStepSound(blockPos, blockState);
				}
			} else {
				super.playStepSound(pos, state);
			}
		}
	}

	@Override
	public LivingEntity.FallSounds getFallSounds() {
		return new LivingEntity.FallSounds(SoundEvents.ENTITY_PLAYER_SMALL_FALL, SoundEvents.ENTITY_PLAYER_BIG_FALL);
	}

	@Override
	public boolean onKilledOther(ServerWorld world, LivingEntity other) {
		this.incrementStat(Stats.KILLED.getOrCreateStat(other.getType()));
		return true;
	}

	@Override
	public void slowMovement(BlockState state, Vec3d multiplier) {
		if (!this.abilities.flying) {
			super.slowMovement(state, multiplier);
		}

		this.tryClearCurrentExplosion();
	}

	public void addExperience(int experience) {
		this.addScore(experience);
		this.experienceProgress = this.experienceProgress + (float)experience / (float)this.getNextLevelExperience();
		this.totalExperience = MathHelper.clamp(this.totalExperience + experience, 0, Integer.MAX_VALUE);

		while (this.experienceProgress < 0.0F) {
			float f = this.experienceProgress * (float)this.getNextLevelExperience();
			if (this.experienceLevel > 0) {
				this.addExperienceLevels(-1);
				this.experienceProgress = 1.0F + f / (float)this.getNextLevelExperience();
			} else {
				this.addExperienceLevels(-1);
				this.experienceProgress = 0.0F;
			}
		}

		while (this.experienceProgress >= 1.0F) {
			this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getNextLevelExperience();
			this.addExperienceLevels(1);
			this.experienceProgress = this.experienceProgress / (float)this.getNextLevelExperience();
		}
	}

	public int getEnchantmentTableSeed() {
		return this.enchantmentTableSeed;
	}

	public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
		this.experienceLevel -= experienceLevels;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		this.enchantmentTableSeed = this.random.nextInt();
	}

	public void addExperienceLevels(int levels) {
		this.experienceLevel += levels;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		if (levels > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.age - 100.0F) {
			float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
			this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
			this.lastPlayedLevelUpSoundTime = this.age;
		}
	}

	public int getNextLevelExperience() {
		if (this.experienceLevel >= 30) {
			return 112 + (this.experienceLevel - 30) * 9;
		} else {
			return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
		}
	}

	public void addExhaustion(float exhaustion) {
		if (!this.abilities.invulnerable) {
			if (!this.getWorld().isClient) {
				this.hungerManager.addExhaustion(exhaustion);
			}
		}
	}

	public Optional<SculkShriekerWarningManager> getSculkShriekerWarningManager() {
		return Optional.empty();
	}

	public HungerManager getHungerManager() {
		return this.hungerManager;
	}

	public boolean canConsume(boolean ignoreHunger) {
		return this.abilities.invulnerable || ignoreHunger || this.hungerManager.isNotFull();
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	public boolean canModifyBlocks() {
		return this.abilities.allowModifyWorld;
	}

	public boolean canPlaceOn(BlockPos pos, Direction facing, ItemStack stack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else {
			BlockPos blockPos = pos.offset(facing.getOpposite());
			CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.getWorld(), blockPos, false);
			return stack.canPlaceOn(cachedBlockPosition);
		}
	}

	@Override
	protected int getXpToDrop() {
		if (!this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
			int i = this.experienceLevel * 7;
			return i > 100 ? 100 : i;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean shouldAlwaysDropXp() {
		return true;
	}

	@Override
	public boolean shouldRenderName() {
		return true;
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return this.abilities.flying || this.isOnGround() && this.isSneaky() ? Entity.MoveEffect.NONE : Entity.MoveEffect.ALL;
	}

	public void sendAbilitiesUpdate() {
	}

	@Override
	public Text getName() {
		return Text.literal(this.gameProfile.getName());
	}

	public EnderChestInventory getEnderChestInventory() {
		return this.enderChestInventory;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND) {
			return this.inventory.getMainHandStack();
		} else if (slot == EquipmentSlot.OFFHAND) {
			return this.inventory.offHand.get(0);
		} else {
			return slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR ? this.inventory.armor.get(slot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	protected boolean isArmorSlot(EquipmentSlot slot) {
		return slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		this.processEquippedStack(stack);
		if (slot == EquipmentSlot.MAINHAND) {
			this.onEquipStack(slot, this.inventory.main.set(this.inventory.selectedSlot, stack), stack);
		} else if (slot == EquipmentSlot.OFFHAND) {
			this.onEquipStack(slot, this.inventory.offHand.set(0, stack), stack);
		} else if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
			this.onEquipStack(slot, this.inventory.armor.set(slot.getEntitySlotId(), stack), stack);
		}
	}

	public boolean giveItemStack(ItemStack stack) {
		return this.inventory.insertStack(stack);
	}

	@Override
	public Iterable<ItemStack> getHandItems() {
		return Lists.<ItemStack>newArrayList(this.getMainHandStack(), this.getOffHandStack());
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.inventory.armor;
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot) {
		return slot != EquipmentSlot.BODY;
	}

	public boolean addShoulderEntity(NbtCompound entityNbt) {
		if (this.hasVehicle() || !this.isOnGround() || this.isTouchingWater() || this.inPowderSnow) {
			return false;
		} else if (this.getShoulderEntityLeft().isEmpty()) {
			this.setShoulderEntityLeft(entityNbt);
			this.shoulderEntityAddedTime = this.getWorld().getTime();
			return true;
		} else if (this.getShoulderEntityRight().isEmpty()) {
			this.setShoulderEntityRight(entityNbt);
			this.shoulderEntityAddedTime = this.getWorld().getTime();
			return true;
		} else {
			return false;
		}
	}

	protected void dropShoulderEntities() {
		if (this.shoulderEntityAddedTime + 20L < this.getWorld().getTime()) {
			this.dropShoulderEntity(this.getShoulderEntityLeft());
			this.setShoulderEntityLeft(new NbtCompound());
			this.dropShoulderEntity(this.getShoulderEntityRight());
			this.setShoulderEntityRight(new NbtCompound());
		}
	}

	private void dropShoulderEntity(NbtCompound entityNbt) {
		if (!this.getWorld().isClient && !entityNbt.isEmpty()) {
			EntityType.getEntityFromNbt(entityNbt, this.getWorld()).ifPresent(entity -> {
				if (entity instanceof TameableEntity) {
					((TameableEntity)entity).setOwnerUuid(this.uuid);
				}

				entity.setPosition(this.getX(), this.getY() + 0.7F, this.getZ());
				((ServerWorld)this.getWorld()).tryLoadEntity(entity);
			});
		}
	}

	@Override
	public abstract boolean isSpectator();

	@Override
	public boolean canBeHitByProjectile() {
		return !this.isSpectator() && super.canBeHitByProjectile();
	}

	@Override
	public boolean isSwimming() {
		return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
	}

	/**
	 * Returns whether this player is in creative mode.
	 */
	public abstract boolean isCreative();

	@Override
	public boolean isPushedByFluids() {
		return !this.abilities.flying;
	}

	public Scoreboard getScoreboard() {
		return this.getWorld().getScoreboard();
	}

	@Override
	public Text getDisplayName() {
		MutableText mutableText = Team.decorateName(this.getScoreboardTeam(), this.getName());
		return this.addTellClickEvent(mutableText);
	}

	private MutableText addTellClickEvent(MutableText component) {
		String string = this.getGameProfile().getName();
		return component.styled(
			style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " "))
					.withHoverEvent(this.getHoverEvent())
					.withInsertion(string)
		);
	}

	@Override
	public String getNameForScoreboard() {
		return this.getGameProfile().getName();
	}

	@Override
	protected void setAbsorptionAmountUnclamped(float absorptionAmount) {
		this.getDataTracker().set(ABSORPTION_AMOUNT, absorptionAmount);
	}

	@Override
	public float getAbsorptionAmount() {
		return this.getDataTracker().get(ABSORPTION_AMOUNT);
	}

	public boolean isPartVisible(PlayerModelPart modelPart) {
		return (this.getDataTracker().get(PLAYER_MODEL_PARTS) & modelPart.getBitFlag()) == modelPart.getBitFlag();
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		if (mappedIndex == 499) {
			return new StackReference() {
				@Override
				public ItemStack get() {
					return PlayerEntity.this.currentScreenHandler.getCursorStack();
				}

				@Override
				public boolean set(ItemStack stack) {
					PlayerEntity.this.currentScreenHandler.setCursorStack(stack);
					return true;
				}
			};
		} else {
			final int i = mappedIndex - 500;
			if (i >= 0 && i < 4) {
				return new StackReference() {
					@Override
					public ItemStack get() {
						return PlayerEntity.this.playerScreenHandler.getCraftingInput().getStack(i);
					}

					@Override
					public boolean set(ItemStack stack) {
						PlayerEntity.this.playerScreenHandler.getCraftingInput().setStack(i, stack);
						PlayerEntity.this.playerScreenHandler.onContentChanged(PlayerEntity.this.inventory);
						return true;
					}
				};
			} else if (mappedIndex >= 0 && mappedIndex < this.inventory.main.size()) {
				return StackReference.of(this.inventory, mappedIndex);
			} else {
				int j = mappedIndex - 200;
				return j >= 0 && j < this.enderChestInventory.size() ? StackReference.of(this.enderChestInventory, j) : super.getStackReference(mappedIndex);
			}
		}
	}

	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public void setReducedDebugInfo(boolean reducedDebugInfo) {
		this.reducedDebugInfo = reducedDebugInfo;
	}

	@Override
	public void setFireTicks(int fireTicks) {
		super.setFireTicks(this.abilities.invulnerable ? Math.min(fireTicks, 1) : fireTicks);
	}

	@Override
	public Arm getMainArm() {
		return this.dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
	}

	public void setMainArm(Arm arm) {
		this.dataTracker.set(MAIN_ARM, (byte)(arm == Arm.LEFT ? 0 : 1));
	}

	public NbtCompound getShoulderEntityLeft() {
		return this.dataTracker.get(LEFT_SHOULDER_ENTITY);
	}

	protected void setShoulderEntityLeft(NbtCompound entityNbt) {
		this.dataTracker.set(LEFT_SHOULDER_ENTITY, entityNbt);
	}

	public NbtCompound getShoulderEntityRight() {
		return this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
	}

	protected void setShoulderEntityRight(NbtCompound entityNbt) {
		this.dataTracker.set(RIGHT_SHOULDER_ENTITY, entityNbt);
	}

	public float getAttackCooldownProgressPerTick() {
		return (float)(1.0 / this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0);
	}

	public float getAttackCooldownProgress(float baseTime) {
		return MathHelper.clamp(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
	}

	public void resetLastAttackedTicks() {
		this.lastAttackedTicks = 0;
	}

	public ItemCooldownManager getItemCooldownManager() {
		return this.itemCooldownManager;
	}

	@Override
	protected float getVelocityMultiplier() {
		return !this.abilities.flying && !this.isFallFlying() ? super.getVelocityMultiplier() : 1.0F;
	}

	public float getLuck() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_LUCK);
	}

	public boolean isCreativeLevelTwoOp() {
		return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = this.getPreferredEquipmentSlot(stack);
		return this.getEquippedStack(equipmentSlot).isEmpty();
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return (EntityDimensions)POSE_DIMENSIONS.getOrDefault(pose, STANDING_DIMENSIONS);
	}

	@Override
	public ImmutableList<EntityPose> getPoses() {
		return ImmutableList.of(EntityPose.STANDING, EntityPose.CROUCHING, EntityPose.SWIMMING);
	}

	@Override
	public ItemStack getProjectileType(ItemStack stack) {
		if (!(stack.getItem() instanceof RangedWeaponItem)) {
			return ItemStack.EMPTY;
		} else {
			Predicate<ItemStack> predicate = ((RangedWeaponItem)stack.getItem()).getHeldProjectiles();
			ItemStack itemStack = RangedWeaponItem.getHeldProjectile(this, predicate);
			if (!itemStack.isEmpty()) {
				return itemStack;
			} else {
				predicate = ((RangedWeaponItem)stack.getItem()).getProjectiles();

				for (int i = 0; i < this.inventory.size(); i++) {
					ItemStack itemStack2 = this.inventory.getStack(i);
					if (predicate.test(itemStack2)) {
						return itemStack2;
					}
				}

				return this.abilities.creativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack eatFood(World world, ItemStack stack, FoodComponent foodComponent) {
		this.getHungerManager().eat(foodComponent);
		this.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		world.playSound(
			null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F
		);
		if (this instanceof ServerPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)this, stack);
		}

		ItemStack itemStack = super.eatFood(world, stack, foodComponent);
		Optional<ItemStack> optional = foodComponent.usingConvertsTo();
		if (optional.isPresent() && !this.isInCreativeMode()) {
			if (itemStack.isEmpty()) {
				return ((ItemStack)optional.get()).copy();
			}

			if (!this.getWorld().isClient()) {
				this.getInventory().insertStack(((ItemStack)optional.get()).copy());
			}
		}

		return itemStack;
	}

	@Override
	public Vec3d getLeashPos(float delta) {
		double d = 0.22 * (this.getMainArm() == Arm.RIGHT ? -1.0 : 1.0);
		float f = MathHelper.lerp(delta * 0.5F, this.getPitch(), this.prevPitch) * (float) (Math.PI / 180.0);
		float g = MathHelper.lerp(delta, this.prevBodyYaw, this.bodyYaw) * (float) (Math.PI / 180.0);
		if (this.isFallFlying() || this.isUsingRiptide()) {
			Vec3d vec3d = this.getRotationVec(delta);
			Vec3d vec3d2 = this.getVelocity();
			double e = vec3d2.horizontalLengthSquared();
			double h = vec3d.horizontalLengthSquared();
			float k;
			if (e > 0.0 && h > 0.0) {
				double i = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(e * h);
				double j = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				k = (float)(Math.signum(j) * Math.acos(i));
			} else {
				k = 0.0F;
			}

			return this.getLerpedPos(delta).add(new Vec3d(d, -0.11, 0.85).rotateZ(-k).rotateX(-f).rotateY(-g));
		} else if (this.isInSwimmingPose()) {
			return this.getLerpedPos(delta).add(new Vec3d(d, 0.2, -0.15).rotateX(-f).rotateY(-g));
		} else {
			double l = this.getBoundingBox().getLengthY() - 1.0;
			double e = this.isInSneakingPose() ? -0.2 : 0.07;
			return this.getLerpedPos(delta).add(new Vec3d(d, l, e).rotateY(-g));
		}
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	public boolean isUsingSpyglass() {
		return this.isUsingItem() && this.getActiveItem().isOf(Items.SPYGLASS);
	}

	@Override
	public boolean shouldSave() {
		return false;
	}

	public Optional<GlobalPos> getLastDeathPos() {
		return this.lastDeathPos;
	}

	public void setLastDeathPos(Optional<GlobalPos> lastDeathPos) {
		this.lastDeathPos = lastDeathPos;
	}

	@Override
	public float getDamageTiltYaw() {
		return this.damageTiltYaw;
	}

	@Override
	public void animateDamage(float yaw) {
		super.animateDamage(yaw);
		this.damageTiltYaw = yaw;
	}

	@Override
	public boolean canSprintAsVehicle() {
		return true;
	}

	@Override
	protected float getOffGroundSpeed() {
		if (this.abilities.flying && !this.hasVehicle()) {
			return this.isSprinting() ? this.abilities.getFlySpeed() * 2.0F : this.abilities.getFlySpeed();
		} else {
			return this.isSprinting() ? 0.025999999F : 0.02F;
		}
	}

	public double getBlockInteractionRange() {
		return this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
	}

	public double getEntityInteractionRange() {
		return this.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
	}

	/**
	 * {@return whether the player can interact with {@code entity}}
	 * 
	 * <p>This returns {@code false} for {@linkplain Entity#isRemoved removed} entities.
	 * 
	 * @param additionalRange the player's additional interaction range added to {@linkplain
	 * #getEntityInteractionRange the default range}
	 */
	public boolean canInteractWithEntity(Entity entity, double additionalRange) {
		return entity.isRemoved() ? false : this.canInteractWithEntityIn(entity.getBoundingBox(), additionalRange);
	}

	/**
	 * {@return whether the player can interact with entity whose bounding box
	 * is {@code box}}
	 * 
	 * @param additionalRange the player's additional interaction range added to {@linkplain
	 * #getEntityInteractionRange the default range}
	 */
	public boolean canInteractWithEntityIn(Box box, double additionalRange) {
		double d = this.getEntityInteractionRange() + additionalRange;
		return box.squaredMagnitude(this.getEyePos()) < d * d;
	}

	/**
	 * {@return whether the player can interact with block at {@code pos}}
	 * 
	 * @param additionalRange the player's additional interaction range added to {@linkplain
	 * #getBlockInteractionRange the default range}
	 */
	public boolean canInteractWithBlockAt(BlockPos pos, double additionalRange) {
		double d = this.getBlockInteractionRange() + additionalRange;
		return new Box(pos).squaredMagnitude(this.getEyePos()) < d * d;
	}

	public void setIgnoreFallDamageFromCurrentExplosion(boolean ignoreFallDamageFromCurrentExplosion) {
		this.ignoreFallDamageFromCurrentExplosion = ignoreFallDamageFromCurrentExplosion;
		if (ignoreFallDamageFromCurrentExplosion) {
			this.currentExplosionResetGraceTime = 40;
		} else {
			this.currentExplosionResetGraceTime = 0;
		}
	}

	public void tryClearCurrentExplosion() {
		if (this.currentExplosionResetGraceTime == 0) {
			this.clearCurrentExplosion();
		}
	}

	public void clearCurrentExplosion() {
		this.currentExplosionResetGraceTime = 0;
		this.explodedBy = null;
		this.currentExplosionImpactPos = null;
		this.ignoreFallDamageFromCurrentExplosion = false;
	}

	/**
	 * A reason why a player cannot start sleeping.
	 */
	public static enum SleepFailureReason {
		NOT_POSSIBLE_HERE,
		NOT_POSSIBLE_NOW(Text.translatable("block.minecraft.bed.no_sleep")),
		TOO_FAR_AWAY(Text.translatable("block.minecraft.bed.too_far_away")),
		OBSTRUCTED(Text.translatable("block.minecraft.bed.obstructed")),
		OTHER_PROBLEM,
		NOT_SAFE(Text.translatable("block.minecraft.bed.not_safe"));

		@Nullable
		private final Text message;

		private SleepFailureReason() {
			this.message = null;
		}

		private SleepFailureReason(final Text message) {
			this.message = message;
		}

		/**
		 * Returns the action bar message that is sent to the player
		 * when sleeping fails with this reason.
		 * 
		 * @return the message, or null if none is sent for this reason
		 */
		@Nullable
		public Text getMessage() {
			return this.message;
		}
	}
}
