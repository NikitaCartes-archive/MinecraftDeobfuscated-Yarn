package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class PlayerEntity extends LivingEntity {
	public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6F, 1.8F);
	private static final Map<EntityPose, EntityDimensions> POSE_DIMENSIONS = ImmutableMap.<EntityPose, EntityDimensions>builder()
		.put(EntityPose.STANDING, STANDING_DIMENSIONS)
		.put(EntityPose.SLEEPING, SLEEPING_DIMENSIONS)
		.put(EntityPose.FALL_FLYING, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.SWIMMING, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.SPIN_ATTACK, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.CROUCHING, EntityDimensions.changing(0.6F, 1.5F))
		.put(EntityPose.DYING, EntityDimensions.fixed(0.2F, 0.2F))
		.build();
	private static final TrackedData<Float> ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	private long shoulderEntityAddedTime;
	public final PlayerInventory inventory = new PlayerInventory(this);
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
	public final PlayerAbilities abilities = new PlayerAbilities();
	public int experienceLevel;
	public int totalExperience;
	public float experienceProgress;
	protected int enchantmentTableSeed;
	protected final float field_7509 = 0.02F;
	private int lastPlayedLevelUpSoundTime;
	private final GameProfile gameProfile;
	@Environment(EnvType.CLIENT)
	private boolean reducedDebugInfo;
	private ItemStack selectedItem = ItemStack.EMPTY;
	private final ItemCooldownManager itemCooldownManager = this.createCooldownManager();
	@Nullable
	public FishingBobberEntity fishHook;

	public PlayerEntity(World world, GameProfile profile) {
		super(EntityType.PLAYER, world);
		this.setUuid(getUuidFromProfile(profile));
		this.gameProfile = profile;
		this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !world.isClient, this);
		this.currentScreenHandler = this.playerScreenHandler;
		BlockPos blockPos = world.getSpawnPos();
		this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
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
			return itemStack.isEmpty() || !itemStack.canDestroy(world.getTagManager(), new CachedBlockPosition(world, pos, false));
		}
	}

	public static DefaultAttributeContainer.Builder createPlayerAttributes() {
		return LivingEntity.createLivingAttributes()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
			.add(EntityAttributes.GENERIC_ATTACK_SPEED)
			.add(EntityAttributes.GENERIC_LUCK);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ABSORPTION_AMOUNT, 0.0F);
		this.dataTracker.startTracking(SCORE, 0);
		this.dataTracker.startTracking(PLAYER_MODEL_PARTS, (byte)0);
		this.dataTracker.startTracking(MAIN_ARM, (byte)1);
		this.dataTracker.startTracking(LEFT_SHOULDER_ENTITY, new CompoundTag());
		this.dataTracker.startTracking(RIGHT_SHOULDER_ENTITY, new CompoundTag());
	}

	@Override
	public void tick() {
		this.noClip = this.isSpectator();
		if (this.isSpectator()) {
			this.onGround = false;
		}

		if (this.experiencePickUpDelay > 0) {
			this.experiencePickUpDelay--;
		}

		if (this.isSleeping()) {
			this.sleepTimer++;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.world.isClient && this.world.isDay()) {
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
		if (!this.world.isClient && this.currentScreenHandler != null && !this.currentScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.currentScreenHandler = this.playerScreenHandler;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.updateCapeAngles();
		if (!this.world.isClient) {
			this.hungerManager.update(this);
			this.incrementStat(Stats.PLAY_ONE_MINUTE);
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
			this.updatePosition(d, this.getY(), e);
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
		this.updateSize();
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
		this.isSubmergedInWater = this.isSubmergedIn(FluidTags.WATER, true);
		return this.isSubmergedInWater;
	}

	private void updateTurtleHelmet() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (itemStack.getItem() == Items.TURTLE_HELMET && !this.isSubmergedIn(FluidTags.WATER)) {
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

	protected void updateSize() {
		if (this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
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
			if (this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(entityPose)) {
				entityPose2 = entityPose;
			} else if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
				entityPose2 = EntityPose.CROUCHING;
			} else {
				entityPose2 = EntityPose.SWIMMING;
			}

			this.setPose(entityPose2);
		}
	}

	@Override
	public int getMaxNetherPortalTime() {
		return this.abilities.invulnerable ? 1 : 80;
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
	public int getDefaultNetherPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(SoundEvent sound, float volume, float pitch) {
		this.world.playSound(this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
	}

	public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.PLAYERS;
	}

	@Override
	protected int getBurningDuration() {
		return 20;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 9) {
			this.consumeItem();
		} else if (status == 23) {
			this.reducedDebugInfo = false;
		} else if (status == 22) {
			this.reducedDebugInfo = true;
		} else if (status == 43) {
			this.spawnParticles(ParticleTypes.CLOUD);
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	private void spawnParticles(ParticleEffect parameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
		}
	}

	protected void closeHandledScreen() {
		this.currentScreenHandler = this.playerScreenHandler;
	}

	@Override
	public void tickRiding() {
		if (this.shouldDismount() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.getX();
			double e = this.getY();
			double f = this.getZ();
			float g = this.yaw;
			float h = this.pitch;
			super.tickRiding();
			this.prevStrideDistance = this.strideDistance;
			this.strideDistance = 0.0F;
			this.increaseRidingMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
			if (this.getVehicle() instanceof PigEntity) {
				this.pitch = h;
				this.yaw = g;
				this.bodyYaw = ((PigEntity)this.getVehicle()).bodyYaw;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void afterSpawn() {
		this.setPose(EntityPose.STANDING);
		super.afterSpawn();
		this.setHealth(this.getMaximumHealth());
		this.deathTime = 0;
	}

	@Override
	protected void tickNewAi() {
		super.tickNewAi();
		this.tickHandSwing();
		this.headYaw = this.yaw;
	}

	@Override
	public void tickMovement() {
		if (this.abilityResyncCountdown > 0) {
			this.abilityResyncCountdown--;
		}

		if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
			if (this.getHealth() < this.getMaximumHealth() && this.age % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
				this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.prevStrideDistance = this.strideDistance;
		super.tickMovement();
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (!this.world.isClient) {
			entityAttributeInstance.setBaseValue((double)this.abilities.getWalkSpeed());
		}

		this.flyingSpeed = 0.02F;
		if (this.isSprinting()) {
			this.flyingSpeed = (float)((double)this.flyingSpeed + 0.005999999865889549);
		}

		this.setMovementSpeed((float)entityAttributeInstance.getValue());
		float f;
		if (this.onGround && !(this.getHealth() <= 0.0F) && !this.isSwimming()) {
			f = Math.min(0.1F, MathHelper.sqrt(squaredHorizontalLength(this.getVelocity())));
		} else {
			f = 0.0F;
		}

		this.strideDistance = this.strideDistance + (f - this.strideDistance) * 0.4F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			Box box;
			if (this.hasVehicle() && !this.getVehicle().removed) {
				box = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
			} else {
				box = this.getBoundingBox().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.world.getEntities(this, box);

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (!entity.removed) {
					this.collideWithEntity(entity);
				}
			}
		}

		this.updateShoulderEntity(this.getShoulderEntityLeft());
		this.updateShoulderEntity(this.getShoulderEntityRight());
		if (!this.world.isClient && (this.fallDistance > 0.5F || this.isTouchingWater()) || this.abilities.flying || this.isSleeping()) {
			this.dropShoulderEntities();
		}
	}

	private void updateShoulderEntity(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && (!compoundTag.contains("Silent") || !compoundTag.getBoolean("Silent")) && this.world.random.nextInt(200) == 0) {
			String string = compoundTag.getString("id");
			EntityType.get(string)
				.filter(entityType -> entityType == EntityType.PARROT)
				.ifPresent(
					entityType -> {
						if (!ParrotEntity.imitateNearbyMob(this.world, this)) {
							this.world
								.playSound(
									null,
									this.getX(),
									this.getY(),
									this.getZ(),
									ParrotEntity.getRandomSound(this.world, this.world.random),
									this.getSoundCategory(),
									1.0F,
									ParrotEntity.getSoundPitch(this.world.random)
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

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		this.refreshPosition();
		if (!this.isSpectator()) {
			this.drop(source);
		}

		if (source != null) {
			this.setVelocity(
				(double)(-MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F),
				0.1F,
				(double)(-MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F)
			);
		} else {
			this.setVelocity(0.0, 0.1, 0.0);
		}

		this.incrementStat(Stats.DEATHS);
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		this.extinguish();
		this.setFlag(0, false);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			this.vanishCursedItems();
			this.inventory.dropAll();
		}
	}

	protected void vanishCursedItems() {
		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);
			if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
				this.inventory.removeStack(i);
			}
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		if (source == DamageSource.ON_FIRE) {
			return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
		} else if (source == DamageSource.DROWN) {
			return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
		} else {
			return source == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.ENTITY_PLAYER_HURT;
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	public boolean dropSelectedItem(boolean dropEntireStack) {
		return this.dropItem(
				this.inventory
					.removeStack(
						this.inventory.selectedSlot, dropEntireStack && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1
					),
				false,
				true
			)
			!= null;
	}

	@Nullable
	public ItemEntity dropItem(ItemStack stack, boolean retainOwnership) {
		return this.dropItem(stack, false, retainOwnership);
	}

	/**
	 * @param throwRandomly If true, the item will be thrown in a random direction from the entity regardless of which direction the tntity is facing
	 */
	@Nullable
	public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
		if (stack.isEmpty()) {
			return null;
		} else {
			if (this.world.isClient) {
				this.swingHand(Hand.MAIN_HAND);
			}

			double d = this.getEyeY() - 0.3F;
			ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), d, this.getZ(), stack);
			itemEntity.setPickupDelay(40);
			if (retainOwnership) {
				itemEntity.setThrower(this.getUuid());
			}

			if (throwRandomly) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.2F, (double)(MathHelper.cos(g) * f));
			} else {
				float f = 0.3F;
				float g = MathHelper.sin(this.pitch * (float) (Math.PI / 180.0));
				float h = MathHelper.cos(this.pitch * (float) (Math.PI / 180.0));
				float i = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
				float j = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
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
			int i = EnchantmentHelper.getEfficiency(this);
			ItemStack itemStack = this.getMainHandStack();
			if (i > 0 && !itemStack.isEmpty()) {
				f += (float)(i * i + 1);
			}
		}

		if (StatusEffectUtil.hasHaste(this)) {
			f *= 1.0F + (float)(StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2F;
		}

		if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
			float g;
			switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
				case 0:
					g = 0.3F;
					break;
				case 1:
					g = 0.09F;
					break;
				case 2:
					g = 0.0027F;
					break;
				case 3:
				default:
					g = 8.1E-4F;
			}

			f *= g;
		}

		if (this.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean isUsingEffectiveTool(BlockState block) {
		return block.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(block);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setUuid(getUuidFromProfile(this.gameProfile));
		ListTag listTag = tag.getList("Inventory", 10);
		this.inventory.deserialize(listTag);
		this.inventory.selectedSlot = tag.getInt("SelectedItemSlot");
		this.sleepTimer = tag.getShort("SleepTimer");
		this.experienceProgress = tag.getFloat("XpP");
		this.experienceLevel = tag.getInt("XpLevel");
		this.totalExperience = tag.getInt("XpTotal");
		this.enchantmentTableSeed = tag.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(tag.getInt("Score"));
		this.hungerManager.fromTag(tag);
		this.abilities.deserialize(tag);
		if (tag.contains("EnderItems", 9)) {
			this.enderChestInventory.readTags(tag.getList("EnderItems", 10));
		}

		if (tag.contains("ShoulderEntityLeft", 10)) {
			this.setShoulderEntityLeft(tag.getCompound("ShoulderEntityLeft"));
		}

		if (tag.contains("ShoulderEntityRight", 10)) {
			this.setShoulderEntityRight(tag.getCompound("ShoulderEntityRight"));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		tag.put("Inventory", this.inventory.serialize(new ListTag()));
		tag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		tag.putShort("SleepTimer", (short)this.sleepTimer);
		tag.putFloat("XpP", this.experienceProgress);
		tag.putInt("XpLevel", this.experienceLevel);
		tag.putInt("XpTotal", this.totalExperience);
		tag.putInt("XpSeed", this.enchantmentTableSeed);
		tag.putInt("Score", this.getScore());
		this.hungerManager.toTag(tag);
		this.abilities.serialize(tag);
		tag.put("EnderItems", this.enderChestInventory.getTags());
		if (!this.getShoulderEntityLeft().isEmpty()) {
			tag.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
		}

		if (!this.getShoulderEntityRight().isEmpty()) {
			tag.put("ShoulderEntityRight", this.getShoulderEntityRight());
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		if (super.isInvulnerableTo(damageSource)) {
			return true;
		} else if (damageSource == DamageSource.DROWN) {
			return !this.world.getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
		} else if (damageSource == DamageSource.FALL) {
			return !this.world.getGameRules().getBoolean(GameRules.FALL_DAMAGE);
		} else {
			return damageSource.isFire() ? !this.world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE) : false;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.abilities.invulnerable && !source.isOutOfWorld()) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				this.dropShoulderEntities();
				if (source.isScaledWithDifficulty()) {
					if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
						amount = 0.0F;
					}

					if (this.world.getDifficulty() == Difficulty.EASY) {
						amount = Math.min(amount / 2.0F + 1.0F, amount);
					}

					if (this.world.getDifficulty() == Difficulty.HARD) {
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
		if (attacker.getMainHandStack().getItem() instanceof AxeItem) {
			this.disableShield(true);
		}
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
		this.inventory.damageArmor(source, amount);
	}

	@Override
	protected void damageShield(float amount) {
		if (amount >= 3.0F && this.activeItemStack.getItem() == Items.SHIELD) {
			int i = 1 + MathHelper.floor(amount);
			Hand hand = this.getActiveHand();
			this.activeItemStack.damage(i, this, playerEntity -> playerEntity.sendToolBreakStatus(hand));
			if (this.activeItemStack.isEmpty()) {
				if (hand == Hand.MAIN_HAND) {
					this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				} else {
					this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}

				this.activeItemStack = ItemStack.EMPTY;
				this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
			}
		}
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			amount = this.applyArmorToDamage(source, amount);
			amount = this.applyEnchantmentsToDamage(source, amount);
			float var8 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var8));
			float g = amount - var8;
			if (g > 0.0F && g < 3.4028235E37F) {
				this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
			}

			if (var8 != 0.0F) {
				this.addExhaustion(source.getExhaustion());
				float h = this.getHealth();
				this.setHealth(this.getHealth() - var8);
				this.getDamageTracker().onDamage(source, h, var8);
				if (var8 < 3.4028235E37F) {
					this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(var8 * 10.0F));
				}
			}
		}
	}

	@Override
	protected boolean method_27303() {
		return !this.abilities.flying && super.method_27303();
	}

	public void openEditSignScreen(SignBlockEntity sign) {
	}

	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
	}

	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
	}

	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
	}

	public void openJigsawScreen(JigsawBlockEntity jigsaw) {
	}

	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
	}

	public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
		return OptionalInt.empty();
	}

	public void sendTradeOffers(int syncId, TraderOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
	}

	public void openEditBookScreen(ItemStack book, Hand hand) {
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
			if (entity.interact(this, hand)) {
				if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
					itemStack.setCount(itemStack2.getCount());
				}

				return ActionResult.SUCCESS;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.useOnEntity(this, (LivingEntity)entity, hand)) {
						if (itemStack.isEmpty() && !this.abilities.creativeMode) {
							this.setStackInHand(hand, ItemStack.EMPTY);
						}

						return ActionResult.SUCCESS;
					}
				}

				return ActionResult.PASS;
			}
		}
	}

	@Override
	public double getHeightOffset() {
		return -0.35;
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		this.ridingCooldown = 0;
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.isSleeping();
	}

	@Override
	protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
		if ((type == MovementType.SELF || type == MovementType.PLAYER) && this.onGround && this.clipAtLedge()) {
			double d = movement.x;
			double e = movement.z;
			double f = 0.05;

			while (d != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), 0.0))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}
			}

			while (e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(0.0, (double)(-this.stepHeight), e))) {
				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			while (d != 0.0 && e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), e))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}

				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			movement = new Vec3d(d, movement.y, e);
		}

		return movement;
	}

	public void attack(Entity target) {
		if (target.isAttackable()) {
			if (!target.handleAttack(this)) {
				float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
				float g;
				if (target instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
				} else {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
				}

				float h = this.getAttackCooldownProgress(0.5F);
				f *= 0.2F + h * h * 0.8F;
				g *= h;
				this.resetLastAttackedTicks();
				if (f > 0.0F || g > 0.0F) {
					boolean bl = h > 0.9F;
					boolean bl2 = false;
					int i = 0;
					i += EnchantmentHelper.getKnockback(this);
					if (this.isSprinting() && bl) {
						this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
						i++;
						bl2 = true;
					}

					boolean bl3 = bl
						&& this.fallDistance > 0.0F
						&& !this.onGround
						&& !this.isClimbing()
						&& !this.isTouchingWater()
						&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
						&& !this.hasVehicle()
						&& target instanceof LivingEntity;
					bl3 = bl3 && !this.isSprinting();
					if (bl3) {
						f *= 1.5F;
					}

					f += g;
					boolean bl4 = false;
					double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
					if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed()) {
						ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
						if (itemStack.getItem() instanceof SwordItem) {
							bl4 = true;
						}
					}

					float j = 0.0F;
					boolean bl5 = false;
					int k = EnchantmentHelper.getFireAspect(this);
					if (target instanceof LivingEntity) {
						j = ((LivingEntity)target).getHealth();
						if (k > 0 && !target.isOnFire()) {
							bl5 = true;
							target.setOnFireFor(1);
						}
					}

					Vec3d vec3d = target.getVelocity();
					boolean bl6 = target.damage(DamageSource.player(this), f);
					if (bl6) {
						if (i > 0) {
							if (target instanceof LivingEntity) {
								((LivingEntity)target)
									.takeKnockback(
										(float)i * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
							} else {
								target.addVelocity(
									(double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (float)i * 0.5F),
									0.1,
									(double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (float)i * 0.5F)
								);
							}

							this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
							this.setSprinting(false);
						}

						if (bl4) {
							float l = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;

							for (LivingEntity livingEntity : this.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
								if (livingEntity != this
									&& livingEntity != target
									&& !this.isTeammate(livingEntity)
									&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
									&& this.squaredDistanceTo(livingEntity) < 9.0) {
									livingEntity.takeKnockback(
										0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
									livingEntity.damage(DamageSource.player(this), l);
								}
							}

							this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
							this.spawnSweepAttackParticles();
						}

						if (target instanceof ServerPlayerEntity && target.velocityModified) {
							((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
							target.velocityModified = false;
							target.setVelocity(vec3d);
						}

						if (bl3) {
							this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
							this.addCritParticles(target);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
							} else {
								this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (g > 0.0F) {
							this.addEnchantedHitParticles(target);
						}

						this.onAttacking(target);
						if (target instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity)target, this);
						}

						EnchantmentHelper.onTargetDamaged(this, target);
						ItemStack itemStack2 = this.getMainHandStack();
						Entity entity = target;
						if (target instanceof EnderDragonPart) {
							entity = ((EnderDragonPart)target).owner;
						}

						if (!this.world.isClient && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
							itemStack2.postHit((LivingEntity)entity, this);
							if (itemStack2.isEmpty()) {
								this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
							}
						}

						if (target instanceof LivingEntity) {
							float m = j - ((LivingEntity)target).getHealth();
							this.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));
							if (k > 0) {
								target.setOnFireFor(k * 4);
							}

							if (this.world instanceof ServerWorld && m > 2.0F) {
								int n = (int)((double)m * 0.5);
								((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), n, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.addExhaustion(0.1F);
					} else {
						this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
						if (bl5) {
							target.extinguish();
						}
					}
				}
			}
		}
	}

	@Override
	protected void attackLivingEntity(LivingEntity target) {
		this.attack(target);
	}

	public void disableShield(boolean sprinting) {
		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
		if (sprinting) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			this.getItemCooldownManager().set(Items.SHIELD, 100);
			this.clearActiveItem();
			this.world.sendEntityStatus(this, (byte)30);
		}
	}

	public void addCritParticles(Entity target) {
	}

	public void addEnchantedHitParticles(Entity target) {
	}

	public void spawnSweepAttackParticles() {
		double d = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)));
		double e = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public void requestRespawn() {
	}

	@Override
	public void remove() {
		super.remove();
		this.playerScreenHandler.close(this);
		if (this.currentScreenHandler != null) {
			this.currentScreenHandler.close(this);
		}
	}

	public boolean isMainPlayer() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
		this.sleep(pos);
		this.sleepTimer = 0;
		return Either.right(Unit.INSTANCE);
	}

	public void wakeUp(boolean bl, boolean updateSleepingPlayers) {
		super.wakeUp();
		if (this.world instanceof ServerWorld && updateSleepingPlayers) {
			((ServerWorld)this.world).updateSleepingPlayers();
		}

		this.sleepTimer = bl ? 0 : 100;
	}

	@Override
	public void wakeUp() {
		this.wakeUp(true, true);
	}

	public static Optional<Vec3d> findRespawnPosition(ServerWorld world, BlockPos pos, boolean bl, boolean bl2) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof RespawnAnchorBlock && (Integer)blockState.get(RespawnAnchorBlock.CHARGES) > 0 && RespawnAnchorBlock.method_27353(world)) {
			Optional<Vec3d> optional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
			if (!bl2 && optional.isPresent()) {
				world.setBlockState(pos, blockState.with(RespawnAnchorBlock.CHARGES, Integer.valueOf((Integer)blockState.get(RespawnAnchorBlock.CHARGES) - 1)), 3);
			}

			return optional;
		} else if (block instanceof BedBlock && BedBlock.method_27352(world, pos)) {
			return BedBlock.findWakeUpPosition(EntityType.PLAYER, world, pos, 0);
		} else if (!bl) {
			return Optional.empty();
		} else {
			boolean bl3 = block.canMobSpawnInside();
			boolean bl4 = world.getBlockState(pos.up()).getBlock().canMobSpawnInside();
			return bl3 && bl4 ? Optional.of(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5)) : Optional.empty();
		}
	}

	public boolean isSleepingLongEnough() {
		return this.isSleeping() && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void sendMessage(Text message, boolean actionBar) {
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

	public int unlockRecipes(Collection<Recipe<?>> recipes) {
		return 0;
	}

	public void unlockRecipes(Identifier[] ids) {
	}

	public int lockRecipes(Collection<Recipe<?>> recipes) {
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
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		if (this.isSwimming() && !this.hasVehicle()) {
			double g = this.getRotationVector().y;
			double h = g < -0.2 ? 0.085 : 0.06;
			if (g <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
				Vec3d vec3d = this.getVelocity();
				this.setVelocity(vec3d.add(0.0, (g - vec3d.y) * h, 0.0));
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double g = this.getVelocity().y;
			float i = this.flyingSpeed;
			this.flyingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.travel(movementInput);
			Vec3d vec3d2 = this.getVelocity();
			this.setVelocity(vec3d2.x, g * 0.6, vec3d2.z);
			this.flyingSpeed = i;
			this.fallDistance = 0.0F;
			this.setFlag(7, false);
		} else {
			super.travel(movementInput);
		}

		this.increaseTravelMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
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
		return !this.world.getBlockState(pos).shouldSuffocate(this.world, pos);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
	}

	public void increaseTravelMotionStats(double dx, double dy, double dz) {
		if (!this.hasVehicle()) {
			if (this.isSwimming()) {
				int i = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.SWIM_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isSubmergedIn(FluidTags.WATER, true)) {
				int i = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isTouchingWater()) {
				int i = Math.round(MathHelper.sqrt(dx * dx + dz * dz) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (dy > 0.0) {
					this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(dy * 100.0));
				}
			} else if (this.onGround) {
				int i = Math.round(MathHelper.sqrt(dx * dx + dz * dz) * 100.0F);
				if (i > 0) {
					if (this.isSprinting()) {
						this.increaseStat(Stats.SPRINT_ONE_CM, i);
						this.addExhaustion(0.1F * (float)i * 0.01F);
					} else if (this.isInSneakingPose()) {
						this.increaseStat(Stats.CROUCH_ONE_CM, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					} else {
						this.increaseStat(Stats.WALK_ONE_CM, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					}
				}
			} else if (this.isFallFlying()) {
				int i = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
				this.increaseStat(Stats.AVIATE_ONE_CM, i);
			} else {
				int i = Math.round(MathHelper.sqrt(dx * dx + dz * dz) * 100.0F);
				if (i > 25) {
					this.increaseStat(Stats.FLY_ONE_CM, i);
				}
			}
		}
	}

	private void increaseRidingMotionStats(double dx, double dy, double dz) {
		if (this.hasVehicle()) {
			int i = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
			if (i > 0) {
				if (this.getVehicle() instanceof AbstractMinecartEntity) {
					this.increaseStat(Stats.MINECART_ONE_CM, i);
				} else if (this.getVehicle() instanceof BoatEntity) {
					this.increaseStat(Stats.BOAT_ONE_CM, i);
				} else if (this.getVehicle() instanceof PigEntity) {
					this.increaseStat(Stats.PIG_ONE_CM, i);
				} else if (this.getVehicle() instanceof HorseBaseEntity) {
					this.increaseStat(Stats.HORSE_ONE_CM, i);
				}
			}
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		if (this.abilities.allowFlying) {
			return false;
		} else {
			if (fallDistance >= 2.0F) {
				this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)fallDistance * 100.0));
			}

			return super.handleFallDamage(fallDistance, damageMultiplier);
		}
	}

	public boolean checkFallFlying() {
		if (!this.onGround && !this.isFallFlying() && !this.isTouchingWater()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
				this.startFallFlying();
				return true;
			}
		}

		return false;
	}

	public void startFallFlying() {
		this.setFlag(7, true);
	}

	public void stopFallFlying() {
		this.setFlag(7, true);
		this.setFlag(7, false);
	}

	@Override
	protected void onSwimmingStart() {
		if (!this.isSpectator()) {
			super.onSwimmingStart();
		}
	}

	@Override
	protected SoundEvent getFallSound(int distance) {
		return distance > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
	}

	@Override
	public void onKilledOther(LivingEntity other) {
		this.incrementStat(Stats.KILLED.getOrCreateStat(other.getType()));
	}

	@Override
	public void slowMovement(BlockState state, Vec3d multiplier) {
		if (!this.abilities.flying) {
			super.slowMovement(state, multiplier);
		}
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
			this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
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
			if (!this.world.isClient) {
				this.hungerManager.addExhaustion(exhaustion);
			}
		}
	}

	public HungerManager getHungerManager() {
		return this.hungerManager;
	}

	public boolean canConsume(boolean ignoreHunger) {
		return this.abilities.invulnerable || ignoreHunger || this.hungerManager.isNotFull();
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaximumHealth();
	}

	public boolean canModifyBlocks() {
		return this.abilities.allowModifyWorld;
	}

	public boolean canPlaceOn(BlockPos pos, Direction facing, ItemStack stack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else {
			BlockPos blockPos = pos.offset(facing.getOpposite());
			CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos, false);
			return stack.canPlaceOn(this.world.getTagManager(), cachedBlockPosition);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
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

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderName() {
		return true;
	}

	@Override
	protected boolean canClimb() {
		return !this.abilities.flying && (!this.onGround || !this.isSneaky());
	}

	public void sendAbilitiesUpdate() {
	}

	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public Text getName() {
		return new LiteralText(this.gameProfile.getName());
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
			return slot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.armor.get(slot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			this.onEquipStack(stack);
			this.inventory.main.set(this.inventory.selectedSlot, stack);
		} else if (slot == EquipmentSlot.OFFHAND) {
			this.onEquipStack(stack);
			this.inventory.offHand.set(0, stack);
		} else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
			this.onEquipStack(stack);
			this.inventory.armor.set(slot.getEntitySlotId(), stack);
		}
	}

	public boolean giveItemStack(ItemStack stack) {
		this.onEquipStack(stack);
		return this.inventory.insertStack(stack);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return Lists.<ItemStack>newArrayList(this.getMainHandStack(), this.getOffHandStack());
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.inventory.armor;
	}

	public boolean addShoulderEntity(CompoundTag tag) {
		if (this.hasVehicle() || !this.onGround || this.isTouchingWater()) {
			return false;
		} else if (this.getShoulderEntityLeft().isEmpty()) {
			this.setShoulderEntityLeft(tag);
			this.shoulderEntityAddedTime = this.world.getTime();
			return true;
		} else if (this.getShoulderEntityRight().isEmpty()) {
			this.setShoulderEntityRight(tag);
			this.shoulderEntityAddedTime = this.world.getTime();
			return true;
		} else {
			return false;
		}
	}

	protected void dropShoulderEntities() {
		if (this.shoulderEntityAddedTime + 20L < this.world.getTime()) {
			this.dropShoulderEntity(this.getShoulderEntityLeft());
			this.setShoulderEntityLeft(new CompoundTag());
			this.dropShoulderEntity(this.getShoulderEntityRight());
			this.setShoulderEntityRight(new CompoundTag());
		}
	}

	private void dropShoulderEntity(CompoundTag entityNbt) {
		if (!this.world.isClient && !entityNbt.isEmpty()) {
			EntityType.getEntityFromTag(entityNbt, this.world).ifPresent(entity -> {
				if (entity instanceof TameableEntity) {
					((TameableEntity)entity).setOwnerUuid(this.uuid);
				}

				entity.updatePosition(this.getX(), this.getY() + 0.7F, this.getZ());
				((ServerWorld)this.world).tryLoadEntity(entity);
			});
		}
	}

	@Override
	public abstract boolean isSpectator();

	@Override
	public boolean isSwimming() {
		return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
	}

	public abstract boolean isCreative();

	@Override
	public boolean canFly() {
		return !this.abilities.flying;
	}

	public Scoreboard getScoreboard() {
		return this.world.getScoreboard();
	}

	@Override
	public Text getDisplayName() {
		MutableText mutableText = Team.modifyText(this.getScoreboardTeam(), this.getName());
		return this.addTellClickEvent(mutableText);
	}

	public Text getNameAndUuid() {
		return new LiteralText("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
	}

	private MutableText addTellClickEvent(MutableText component) {
		String string = this.getGameProfile().getName();
		return component.styled(
			style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " "))
					.setHoverEvent(this.getHoverEvent())
					.withInsertion(string)
		);
	}

	@Override
	public String getEntityName() {
		return this.getGameProfile().getName();
	}

	@Override
	public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		switch (pose) {
			case SWIMMING:
			case FALL_FLYING:
			case SPIN_ATTACK:
				return 0.4F;
			case CROUCHING:
				return 1.27F;
			default:
				return 1.62F;
		}
	}

	@Override
	public void setAbsorptionAmount(float amount) {
		if (amount < 0.0F) {
			amount = 0.0F;
		}

		this.getDataTracker().set(ABSORPTION_AMOUNT, amount);
	}

	@Override
	public float getAbsorptionAmount() {
		return this.getDataTracker().get(ABSORPTION_AMOUNT);
	}

	public static UUID getUuidFromProfile(GameProfile profile) {
		UUID uUID = profile.getId();
		if (uUID == null) {
			uUID = getOfflinePlayerUuid(profile.getName());
		}

		return uUID;
	}

	public static UUID getOfflinePlayerUuid(String nickname) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
	}

	@Environment(EnvType.CLIENT)
	public boolean isPartVisible(PlayerModelPart modelPart) {
		return (this.getDataTracker().get(PLAYER_MODEL_PARTS) & modelPart.getBitFlag()) == modelPart.getBitFlag();
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot >= 0 && slot < this.inventory.main.size()) {
			this.inventory.setStack(slot, item);
			return true;
		} else {
			EquipmentSlot equipmentSlot;
			if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.HEAD;
			} else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.CHEST;
			} else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.LEGS;
			} else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.FEET;
			} else {
				equipmentSlot = null;
			}

			if (slot == 98) {
				this.equipStack(EquipmentSlot.MAINHAND, item);
				return true;
			} else if (slot == 99) {
				this.equipStack(EquipmentSlot.OFFHAND, item);
				return true;
			} else if (equipmentSlot == null) {
				int i = slot - 200;
				if (i >= 0 && i < this.enderChestInventory.size()) {
					this.enderChestInventory.setStack(i, item);
					return true;
				} else {
					return false;
				}
			} else {
				if (!item.isEmpty()) {
					if (!(item.getItem() instanceof ArmorItem) && !(item.getItem() instanceof ElytraItem)) {
						if (equipmentSlot != EquipmentSlot.HEAD) {
							return false;
						}
					} else if (MobEntity.getPreferredEquipmentSlot(item) != equipmentSlot) {
						return false;
					}
				}

				this.inventory.setStack(equipmentSlot.getEntitySlotId() + this.inventory.main.size(), item);
				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean getReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public void setReducedDebugInfo(boolean reducedDebugInfo) {
		this.reducedDebugInfo = reducedDebugInfo;
	}

	@Override
	public Arm getMainArm() {
		return this.dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
	}

	public void setMainArm(Arm arm) {
		this.dataTracker.set(MAIN_ARM, (byte)(arm == Arm.LEFT ? 0 : 1));
	}

	public CompoundTag getShoulderEntityLeft() {
		return this.dataTracker.get(LEFT_SHOULDER_ENTITY);
	}

	protected void setShoulderEntityLeft(CompoundTag entityTag) {
		this.dataTracker.set(LEFT_SHOULDER_ENTITY, entityTag);
	}

	public CompoundTag getShoulderEntityRight() {
		return this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
	}

	protected void setShoulderEntityRight(CompoundTag entityTag) {
		this.dataTracker.set(RIGHT_SHOULDER_ENTITY, entityTag);
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
	public boolean canPickUp(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		return this.getEquippedStack(equipmentSlot).isEmpty();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return (EntityDimensions)POSE_DIMENSIONS.getOrDefault(pose, STANDING_DIMENSIONS);
	}

	@Override
	public ImmutableList<EntityPose> getPoses() {
		return ImmutableList.of(EntityPose.STANDING, EntityPose.CROUCHING, EntityPose.SWIMMING);
	}

	@Override
	public ItemStack getArrowType(ItemStack stack) {
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
	public ItemStack eatFood(World world, ItemStack stack) {
		this.getHungerManager().eat(stack.getItem(), stack);
		this.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		world.playSound(
			null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F
		);
		if (this instanceof ServerPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)this, stack);
		}

		return super.eatFood(world, stack);
	}

	public static enum SleepFailureReason {
		NOT_POSSIBLE_HERE,
		NOT_POSSIBLE_NOW(new TranslatableText("block.minecraft.bed.no_sleep")),
		TOO_FAR_AWAY(new TranslatableText("block.minecraft.bed.too_far_away")),
		OBSTRUCTED(new TranslatableText("block.minecraft.bed.obstructed")),
		OTHER_PROBLEM,
		NOT_SAFE(new TranslatableText("block.minecraft.bed.not_safe"));

		@Nullable
		private final Text text;

		private SleepFailureReason() {
			this.text = null;
		}

		private SleepFailureReason(Text text) {
			this.text = text;
		}

		@Nullable
		public Text toText() {
			return this.text;
		}
	}
}
