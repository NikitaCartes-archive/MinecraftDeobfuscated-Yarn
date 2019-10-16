package net.minecraft.entity.player;

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
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.container.PlayerContainer;
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
import net.minecraft.entity.mob.HostileEntity;
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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
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
import net.minecraft.world.WorldView;

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
	protected static final TrackedData<Byte> PLAYER_MODEL_BIT_MASK = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	private long field_19428;
	public final PlayerInventory inventory = new PlayerInventory(this);
	protected EnderChestInventory enderChestInventory = new EnderChestInventory();
	public final PlayerContainer playerContainer;
	public Container container;
	protected HungerManager hungerManager = new HungerManager();
	protected int field_7489;
	public float field_7505;
	public float field_7483;
	public int experiencePickUpDelay;
	public double field_7524;
	public double field_7502;
	public double field_7522;
	public double field_7500;
	public double field_7521;
	public double field_7499;
	private int sleepTimer;
	protected boolean isInWater;
	private BlockPos spawnPosition;
	private boolean spawnForced;
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

	public PlayerEntity(World world, GameProfile gameProfile) {
		super(EntityType.PLAYER, world);
		this.setUuid(getUuidFromProfile(gameProfile));
		this.gameProfile = gameProfile;
		this.playerContainer = new PlayerContainer(this.inventory, !world.isClient, this);
		this.container = this.playerContainer;
		BlockPos blockPos = world.getSpawnPos();
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6215 = 180.0F;
	}

	public boolean canMine(World world, BlockPos blockPos, GameMode gameMode) {
		if (!gameMode.shouldLimitWorldModification()) {
			return false;
		} else if (gameMode == GameMode.SPECTATOR) {
			return true;
		} else if (this.canModifyWorld()) {
			return false;
		} else {
			ItemStack itemStack = this.getMainHandStack();
			return itemStack.isEmpty() || !itemStack.canDestroy(world.getTagManager(), new CachedBlockPosition(world, blockPos, false));
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1F);
		this.getAttributes().register(EntityAttributes.ATTACK_SPEED);
		this.getAttributes().register(EntityAttributes.LUCK);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ABSORPTION_AMOUNT, 0.0F);
		this.dataTracker.startTracking(SCORE, 0);
		this.dataTracker.startTracking(PLAYER_MODEL_BIT_MASK, (byte)0);
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

			if (!this.world.isClient && this.world.isDaylight()) {
				this.wakeUp(false, true);
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		this.updateInWater();
		super.tick();
		if (!this.world.isClient && this.container != null && !this.container.canUse(this)) {
			this.closeContainer();
			this.container = this.playerContainer;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.method_7313();
		if (!this.world.isClient) {
			this.hungerManager.update(this);
			this.incrementStat(Stats.PLAY_ONE_MINUTE);
			if (this.isAlive()) {
				this.incrementStat(Stats.TIME_SINCE_DEATH);
			}

			if (this.method_21751()) {
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
		if (!ItemStack.areEqualIgnoreDamage(this.selectedItem, itemStack)) {
			if (!ItemStack.areItemsEqual(this.selectedItem, itemStack)) {
				this.resetLastAttackedTicks();
			}

			this.selectedItem = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
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

	protected boolean method_21825() {
		return this.isSneaking();
	}

	protected boolean updateInWater() {
		this.isInWater = this.isSubmergedIn(FluidTags.WATER, true);
		return this.isInWater;
	}

	private void updateTurtleHelmet() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (itemStack.getItem() == Items.TURTLE_HELMET && !this.isInFluid(FluidTags.WATER)) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
		}
	}

	protected ItemCooldownManager createCooldownManager() {
		return new ItemCooldownManager();
	}

	private void method_7313() {
		this.field_7524 = this.field_7500;
		this.field_7502 = this.field_7521;
		this.field_7522 = this.field_7499;
		double d = this.getX() - this.field_7500;
		double e = this.getY() - this.field_7521;
		double f = this.getZ() - this.field_7499;
		double g = 10.0;
		if (d > 10.0) {
			this.field_7500 = this.getX();
			this.field_7524 = this.field_7500;
		}

		if (f > 10.0) {
			this.field_7499 = this.getZ();
			this.field_7522 = this.field_7499;
		}

		if (e > 10.0) {
			this.field_7521 = this.getY();
			this.field_7502 = this.field_7521;
		}

		if (d < -10.0) {
			this.field_7500 = this.getX();
			this.field_7524 = this.field_7500;
		}

		if (f < -10.0) {
			this.field_7499 = this.getZ();
			this.field_7522 = this.field_7499;
		}

		if (e < -10.0) {
			this.field_7521 = this.getY();
			this.field_7502 = this.field_7521;
		}

		this.field_7500 += d * 0.25;
		this.field_7499 += f * 0.25;
		this.field_7521 += e * 0.25;
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
	public int getMaxPortalTime() {
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
	public int getDefaultPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(SoundEvent soundEvent, float f, float g) {
		this.world.playSound(this, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), f, g);
	}

	public void playSound(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
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
	public void handleStatus(byte b) {
		if (b == 9) {
			this.consumeItem();
		} else if (b == 23) {
			this.reducedDebugInfo = false;
		} else if (b == 22) {
			this.reducedDebugInfo = true;
		} else if (b == 43) {
			this.spawnParticles(ParticleTypes.CLOUD);
		} else {
			super.handleStatus(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void spawnParticles(ParticleEffect particleEffect) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(particleEffect, this.method_23322(1.0), this.method_23319() + 1.0, this.method_23325(1.0), d, e, f);
		}
	}

	protected void closeContainer() {
		this.container = this.playerContainer;
	}

	@Override
	public void tickRiding() {
		if (!this.world.isClient && this.shouldDismount() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.getX();
			double e = this.getY();
			double f = this.getZ();
			float g = this.yaw;
			float h = this.pitch;
			super.tickRiding();
			this.field_7505 = this.field_7483;
			this.field_7483 = 0.0F;
			this.method_7260(this.getX() - d, this.getY() - e, this.getZ() - f);
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
		if (this.field_7489 > 0) {
			this.field_7489--;
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
		this.field_7505 = this.field_7483;
		super.tickMovement();
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
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

		this.field_7483 = this.field_7483 + (f - this.field_7483) * 0.4F;
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
		if (!this.world.isClient && (this.fallDistance > 0.5F || this.isInsideWater()) || this.abilities.flying || this.isSleeping()) {
			this.dropShoulderEntities();
		}
	}

	private void updateShoulderEntity(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && !compoundTag.contains("Silent") || !compoundTag.getBoolean("Silent")) {
			String string = compoundTag.getString("id");
			EntityType.get(string).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> ParrotEntity.playMobSound(this.world, this));
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.onPlayerCollision(this);
	}

	public int getScore() {
		return this.dataTracker.get(SCORE);
	}

	public void setScore(int i) {
		this.dataTracker.set(SCORE, i);
	}

	public void addScore(int i) {
		int j = this.getScore();
		this.dataTracker.set(SCORE, j + i);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		this.method_23311();
		if (!this.isSpectator()) {
			this.drop(damageSource);
		}

		if (damageSource != null) {
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
		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
				this.inventory.removeInvStack(i);
			}
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (damageSource == DamageSource.ON_FIRE) {
			return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
		} else if (damageSource == DamageSource.DROWN) {
			return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
		} else {
			return damageSource == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.ENTITY_PLAYER_HURT;
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	public boolean dropSelectedItem(boolean bl) {
		return this.dropItem(
				this.inventory
					.takeInvStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1),
				false,
				true
			)
			!= null;
	}

	@Nullable
	public ItemEntity dropItem(ItemStack itemStack, boolean bl) {
		return this.dropItem(itemStack, false, bl);
	}

	@Nullable
	public ItemEntity dropItem(ItemStack itemStack, boolean bl, boolean bl2) {
		if (itemStack.isEmpty()) {
			return null;
		} else {
			double d = this.method_23320() - 0.3F;
			ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), d, this.getZ(), itemStack);
			itemEntity.setPickupDelay(40);
			if (bl2) {
				itemEntity.setThrower(this.getUuid());
			}

			if (bl) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				this.setVelocity((double)(-MathHelper.sin(g) * f), 0.2F, (double)(MathHelper.cos(g) * f));
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

	public float getBlockBreakingSpeed(BlockState blockState) {
		float f = this.inventory.getBlockBreakingSpeed(blockState);
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

		if (this.isInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean isUsingEffectiveTool(BlockState blockState) {
		return blockState.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(blockState);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setUuid(getUuidFromProfile(this.gameProfile));
		ListTag listTag = compoundTag.getList("Inventory", 10);
		this.inventory.deserialize(listTag);
		this.inventory.selectedSlot = compoundTag.getInt("SelectedItemSlot");
		this.sleepTimer = compoundTag.getShort("SleepTimer");
		this.experienceProgress = compoundTag.getFloat("XpP");
		this.experienceLevel = compoundTag.getInt("XpLevel");
		this.totalExperience = compoundTag.getInt("XpTotal");
		this.enchantmentTableSeed = compoundTag.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(compoundTag.getInt("Score"));
		if (compoundTag.contains("SpawnX", 99) && compoundTag.contains("SpawnY", 99) && compoundTag.contains("SpawnZ", 99)) {
			this.spawnPosition = new BlockPos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
			this.spawnForced = compoundTag.getBoolean("SpawnForced");
		}

		this.hungerManager.deserialize(compoundTag);
		this.abilities.deserialize(compoundTag);
		if (compoundTag.contains("EnderItems", 9)) {
			this.enderChestInventory.readTags(compoundTag.getList("EnderItems", 10));
		}

		if (compoundTag.contains("ShoulderEntityLeft", 10)) {
			this.setShoulderEntityLeft(compoundTag.getCompound("ShoulderEntityLeft"));
		}

		if (compoundTag.contains("ShoulderEntityRight", 10)) {
			this.setShoulderEntityRight(compoundTag.getCompound("ShoulderEntityRight"));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Inventory", this.inventory.serialize(new ListTag()));
		compoundTag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		compoundTag.putShort("SleepTimer", (short)this.sleepTimer);
		compoundTag.putFloat("XpP", this.experienceProgress);
		compoundTag.putInt("XpLevel", this.experienceLevel);
		compoundTag.putInt("XpTotal", this.totalExperience);
		compoundTag.putInt("XpSeed", this.enchantmentTableSeed);
		compoundTag.putInt("Score", this.getScore());
		if (this.spawnPosition != null) {
			compoundTag.putInt("SpawnX", this.spawnPosition.getX());
			compoundTag.putInt("SpawnY", this.spawnPosition.getY());
			compoundTag.putInt("SpawnZ", this.spawnPosition.getZ());
			compoundTag.putBoolean("SpawnForced", this.spawnForced);
		}

		this.hungerManager.serialize(compoundTag);
		this.abilities.serialize(compoundTag);
		compoundTag.put("EnderItems", this.enderChestInventory.getTags());
		if (!this.getShoulderEntityLeft().isEmpty()) {
			compoundTag.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
		}

		if (!this.getShoulderEntityRight().isEmpty()) {
			compoundTag.put("ShoulderEntityRight", this.getShoulderEntityRight());
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
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.abilities.invulnerable && !damageSource.isOutOfWorld()) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				this.dropShoulderEntities();
				if (damageSource.isScaledWithDifficulty()) {
					if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
						f = 0.0F;
					}

					if (this.world.getDifficulty() == Difficulty.EASY) {
						f = Math.min(f / 2.0F + 1.0F, f);
					}

					if (this.world.getDifficulty() == Difficulty.HARD) {
						f = f * 3.0F / 2.0F;
					}
				}

				return f == 0.0F ? false : super.damage(damageSource, f);
			}
		}
	}

	@Override
	protected void takeShieldHit(LivingEntity livingEntity) {
		super.takeShieldHit(livingEntity);
		if (livingEntity.getMainHandStack().getItem() instanceof AxeItem) {
			this.disableShield(true);
		}
	}

	public boolean shouldDamagePlayer(PlayerEntity playerEntity) {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		AbstractTeam abstractTeam2 = playerEntity.getScoreboardTeam();
		if (abstractTeam == null) {
			return true;
		} else {
			return !abstractTeam.isEqual(abstractTeam2) ? true : abstractTeam.isFriendlyFireAllowed();
		}
	}

	@Override
	protected void damageArmor(float f) {
		this.inventory.damageArmor(f);
	}

	@Override
	protected void damageShield(float f) {
		if (f >= 3.0F && this.activeItemStack.getItem() == Items.SHIELD) {
			int i = 1 + MathHelper.floor(f);
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
	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			f = this.applyArmorToDamage(damageSource, f);
			f = this.applyEnchantmentsToDamage(damageSource, f);
			float var8 = Math.max(f - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F) {
				this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				this.addExhaustion(damageSource.getExhaustion());
				float i = this.getHealth();
				this.setHealth(this.getHealth() - var8);
				this.getDamageTracker().onDamage(damageSource, i, var8);
				if (var8 < 3.4028235E37F) {
					this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(var8 * 10.0F));
				}
			}
		}
	}

	public void openEditSignScreen(SignBlockEntity signBlockEntity) {
	}

	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
	}

	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
	}

	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlockBlockEntity) {
	}

	public void openJigsawScreen(JigsawBlockEntity jigsawBlockEntity) {
	}

	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
	}

	public OptionalInt openContainer(@Nullable NameableContainerProvider nameableContainerProvider) {
		return OptionalInt.empty();
	}

	public void sendTradeOffers(int i, TraderOfferList traderOfferList, int j, int k, boolean bl, boolean bl2) {
	}

	public void openEditBookScreen(ItemStack itemStack, Hand hand) {
	}

	public ActionResult interact(Entity entity, Hand hand) {
		if (this.isSpectator()) {
			if (entity instanceof NameableContainerProvider) {
				this.openContainer((NameableContainerProvider)entity);
			}

			return ActionResult.PASS;
		} else {
			ItemStack itemStack = this.getStackInHand(hand);
			ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
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
	protected Vec3d adjustMovementForSneaking(Vec3d vec3d, MovementType movementType) {
		if ((movementType == MovementType.SELF || movementType == MovementType.PLAYER) && this.onGround && this.method_21825()) {
			double d = vec3d.x;
			double e = vec3d.z;
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

			vec3d = new Vec3d(d, vec3d.y, e);
		}

		return vec3d;
	}

	public void attack(Entity entity) {
		if (entity.isAttackable()) {
			if (!entity.handleAttack(this)) {
				float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
				float g;
				if (entity instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
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
						&& !this.isInsideWater()
						&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
						&& !this.hasVehicle()
						&& entity instanceof LivingEntity;
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
					if (entity instanceof LivingEntity) {
						j = ((LivingEntity)entity).getHealth();
						if (k > 0 && !entity.isOnFire()) {
							bl5 = true;
							entity.setOnFireFor(1);
						}
					}

					Vec3d vec3d = entity.getVelocity();
					boolean bl6 = entity.damage(DamageSource.player(this), f);
					if (bl6) {
						if (i > 0) {
							if (entity instanceof LivingEntity) {
								((LivingEntity)entity)
									.takeKnockback(
										this, (float)i * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
							} else {
								entity.addVelocity(
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

							for (LivingEntity livingEntity : this.world.getNonSpectatingEntities(LivingEntity.class, entity.getBoundingBox().expand(1.0, 0.25, 1.0))) {
								if (livingEntity != this
									&& livingEntity != entity
									&& !this.isTeammate(livingEntity)
									&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
									&& this.squaredDistanceTo(livingEntity) < 9.0) {
									livingEntity.takeKnockback(
										this, 0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
									livingEntity.damage(DamageSource.player(this), l);
								}
							}

							this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
							this.method_7263();
						}

						if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
							((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							entity.velocityModified = false;
							entity.setVelocity(vec3d);
						}

						if (bl3) {
							this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
							this.addCritParticles(entity);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
							} else {
								this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (g > 0.0F) {
							this.addEnchantedHitParticles(entity);
						}

						this.onAttacking(entity);
						if (entity instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
						}

						EnchantmentHelper.onTargetDamaged(this, entity);
						ItemStack itemStack2 = this.getMainHandStack();
						Entity entity2 = entity;
						if (entity instanceof EnderDragonPart) {
							entity2 = ((EnderDragonPart)entity).owner;
						}

						if (!this.world.isClient && !itemStack2.isEmpty() && entity2 instanceof LivingEntity) {
							itemStack2.postHit((LivingEntity)entity2, this);
							if (itemStack2.isEmpty()) {
								this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
							}
						}

						if (entity instanceof LivingEntity) {
							float m = j - ((LivingEntity)entity).getHealth();
							this.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));
							if (k > 0) {
								entity.setOnFireFor(k * 4);
							}

							if (this.world instanceof ServerWorld && m > 2.0F) {
								int n = (int)((double)m * 0.5);
								((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.method_23323(0.5), entity.getZ(), n, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.addExhaustion(0.1F);
					} else {
						this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
						if (bl5) {
							entity.extinguish();
						}
					}
				}
			}
		}
	}

	@Override
	protected void attackLivingEntity(LivingEntity livingEntity) {
		this.attack(livingEntity);
	}

	public void disableShield(boolean bl) {
		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
		if (bl) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			this.getItemCooldownManager().set(Items.SHIELD, 100);
			this.clearActiveItem();
			this.world.sendEntityStatus(this, (byte)30);
		}
	}

	public void addCritParticles(Entity entity) {
	}

	public void addEnchantedHitParticles(Entity entity) {
	}

	public void method_7263() {
		double d = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)));
		double e = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.method_23323(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public void requestRespawn() {
	}

	@Override
	public void remove() {
		super.remove();
		this.playerContainer.close(this);
		if (this.container != null) {
			this.container.close(this);
		}
	}

	public boolean isMainPlayer() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos blockPos) {
		Direction direction = this.world.getBlockState(blockPos).get(HorizontalFacingBlock.FACING);
		if (!this.world.isClient) {
			if (this.isSleeping() || !this.isAlive()) {
				return Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM);
			}

			if (!this.world.dimension.hasVisibleSky()) {
				return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE);
			}

			if (this.world.isDaylight()) {
				this.setPlayerSpawn(blockPos, false);
				return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW);
			}

			if (!this.isWithinSleepingRange(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepFailureReason.TOO_FAR_AWAY);
			}

			if (this.isBedObstructed(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepFailureReason.OBSTRUCTED);
			}

			if (!this.isCreative()) {
				double d = 8.0;
				double e = 5.0;
				List<HostileEntity> list = this.world
					.getEntities(
						HostileEntity.class,
						new Box(
							(double)blockPos.getX() - 8.0,
							(double)blockPos.getY() - 5.0,
							(double)blockPos.getZ() - 8.0,
							(double)blockPos.getX() + 8.0,
							(double)blockPos.getY() + 5.0,
							(double)blockPos.getZ() + 8.0
						),
						hostileEntity -> hostileEntity.isAngryAt(this)
					);
				if (!list.isEmpty()) {
					return Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE);
				}
			}
		}

		this.sleep(blockPos);
		this.sleepTimer = 0;
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		return Either.right(Unit.INSTANCE);
	}

	@Override
	public void sleep(BlockPos blockPos) {
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		this.setPlayerSpawn(blockPos, false);
		super.sleep(blockPos);
	}

	private boolean isWithinSleepingRange(BlockPos blockPos, Direction direction) {
		if (Math.abs(this.getX() - (double)blockPos.getX()) <= 3.0
			&& Math.abs(this.getY() - (double)blockPos.getY()) <= 2.0
			&& Math.abs(this.getZ() - (double)blockPos.getZ()) <= 3.0) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
			return Math.abs(this.getX() - (double)blockPos2.getX()) <= 3.0
				&& Math.abs(this.getY() - (double)blockPos2.getY()) <= 2.0
				&& Math.abs(this.getZ() - (double)blockPos2.getZ()) <= 3.0;
		}
	}

	private boolean isBedObstructed(BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.up();
		return !this.doesNotSuffocate(blockPos2) || !this.doesNotSuffocate(blockPos2.offset(direction.getOpposite()));
	}

	public void wakeUp(boolean bl, boolean bl2) {
		super.wakeUp();
		if (this.world instanceof ServerWorld && bl2) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		this.sleepTimer = bl ? 0 : 100;
	}

	@Override
	public void wakeUp() {
		this.wakeUp(true, true);
	}

	public static Optional<Vec3d> method_7288(WorldView worldView, BlockPos blockPos, boolean bl) {
		Block block = worldView.getBlockState(blockPos).getBlock();
		if (!(block instanceof BedBlock)) {
			if (!bl) {
				return Optional.empty();
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = worldView.getBlockState(blockPos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? Optional.of(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.1, (double)blockPos.getZ() + 0.5)) : Optional.empty();
			}
		} else {
			return BedBlock.findWakeUpPosition(EntityType.PLAYER, worldView, blockPos, 0);
		}
	}

	public boolean isSleepingLongEnough() {
		return this.isSleeping() && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void addChatMessage(Text text, boolean bl) {
	}

	public BlockPos getSpawnPosition() {
		return this.spawnPosition;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setPlayerSpawn(BlockPos blockPos, boolean bl) {
		if (blockPos != null) {
			this.spawnPosition = blockPos;
			this.spawnForced = bl;
		} else {
			this.spawnPosition = null;
			this.spawnForced = false;
		}
	}

	public void incrementStat(Identifier identifier) {
		this.incrementStat(Stats.CUSTOM.getOrCreateStat(identifier));
	}

	public void increaseStat(Identifier identifier, int i) {
		this.increaseStat(Stats.CUSTOM.getOrCreateStat(identifier), i);
	}

	public void incrementStat(Stat<?> stat) {
		this.increaseStat(stat, 1);
	}

	public void increaseStat(Stat<?> stat, int i) {
	}

	public void resetStat(Stat<?> stat) {
	}

	public int unlockRecipes(Collection<Recipe<?>> collection) {
		return 0;
	}

	public void unlockRecipes(Identifier[] identifiers) {
	}

	public int lockRecipes(Collection<Recipe<?>> collection) {
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
	public void travel(Vec3d vec3d) {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		if (this.isSwimming() && !this.hasVehicle()) {
			double g = this.getRotationVector().y;
			double h = g < -0.2 ? 0.085 : 0.06;
			if (g <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
				Vec3d vec3d2 = this.getVelocity();
				this.setVelocity(vec3d2.add(0.0, (g - vec3d2.y) * h, 0.0));
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double g = this.getVelocity().y;
			float i = this.flyingSpeed;
			this.flyingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.travel(vec3d);
			Vec3d vec3d3 = this.getVelocity();
			this.setVelocity(vec3d3.x, g * 0.6, vec3d3.z);
			this.flyingSpeed = i;
			this.fallDistance = 0.0F;
			this.setFlag(7, false);
		} else {
			super.travel(vec3d);
		}

		this.method_7282(this.getX() - d, this.getY() - e, this.getZ() - f);
	}

	@Override
	public void updateSwimming() {
		if (this.abilities.flying) {
			this.setSwimming(false);
		} else {
			super.updateSwimming();
		}
	}

	protected boolean doesNotSuffocate(BlockPos blockPos) {
		return !this.world.getBlockState(blockPos).canSuffocate(this.world, blockPos);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
	}

	public void method_7282(double d, double e, double f) {
		if (!this.hasVehicle()) {
			if (this.isSwimming()) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.SWIM_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isSubmergedIn(FluidTags.WATER, true)) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isInsideWater()) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (e > 0.0) {
					this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(e * 100.0));
				}
			} else if (this.onGround) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
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
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				this.increaseStat(Stats.AVIATE_ONE_CM, i);
			} else {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 25) {
					this.increaseStat(Stats.FLY_ONE_CM, i);
				}
			}
		}
	}

	private void method_7260(double d, double e, double f) {
		if (this.hasVehicle()) {
			int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
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
	public boolean handleFallDamage(float f, float g) {
		if (this.abilities.allowFlying) {
			return false;
		} else {
			if (f >= 2.0F) {
				this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)f * 100.0));
			}

			return super.handleFallDamage(f, g);
		}
	}

	public boolean method_23668() {
		if (!this.onGround && !this.isFallFlying() && !this.isInsideWater()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
				this.method_23669();
				return true;
			}
		}

		return false;
	}

	public void method_23669() {
		this.setFlag(7, true);
	}

	public void method_23670() {
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
	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
	}

	@Override
	public void onKilledOther(LivingEntity livingEntity) {
		this.incrementStat(Stats.KILLED.getOrCreateStat(livingEntity.getType()));
	}

	@Override
	public void slowMovement(BlockState blockState, Vec3d vec3d) {
		if (!this.abilities.flying) {
			super.slowMovement(blockState, vec3d);
		}
	}

	public void addExperience(int i) {
		this.addScore(i);
		this.experienceProgress = this.experienceProgress + (float)i / (float)this.getNextLevelExperience();
		this.totalExperience = MathHelper.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);

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

	public void applyEnchantmentCosts(ItemStack itemStack, int i) {
		this.experienceLevel -= i;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		this.enchantmentTableSeed = this.random.nextInt();
	}

	public void addExperienceLevels(int i) {
		this.experienceLevel += i;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		if (i > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.age - 100.0F) {
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

	public void addExhaustion(float f) {
		if (!this.abilities.invulnerable) {
			if (!this.world.isClient) {
				this.hungerManager.addExhaustion(f);
			}
		}
	}

	public HungerManager getHungerManager() {
		return this.hungerManager;
	}

	public boolean canConsume(boolean bl) {
		return !this.abilities.invulnerable && (bl || this.hungerManager.isNotFull());
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaximumHealth();
	}

	public boolean canModifyWorld() {
		return this.abilities.allowModifyWorld;
	}

	public boolean canPlaceOn(BlockPos blockPos, Direction direction, ItemStack itemStack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
			CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos2, false);
			return itemStack.canPlaceOn(this.world.getTagManager(), cachedBlockPosition);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
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
		return !this.abilities.flying && (!this.onGround || !this.method_21751());
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
	public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			return this.inventory.getMainHandStack();
		} else if (equipmentSlot == EquipmentSlot.OFFHAND) {
			return this.inventory.offHand.get(0);
		} else {
			return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.armor.get(equipmentSlot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			this.onEquipStack(itemStack);
			this.inventory.main.set(this.inventory.selectedSlot, itemStack);
		} else if (equipmentSlot == EquipmentSlot.OFFHAND) {
			this.onEquipStack(itemStack);
			this.inventory.offHand.set(0, itemStack);
		} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
			this.onEquipStack(itemStack);
			this.inventory.armor.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	public boolean giveItemStack(ItemStack itemStack) {
		this.onEquipStack(itemStack);
		return this.inventory.insertStack(itemStack);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return Lists.<ItemStack>newArrayList(this.getMainHandStack(), this.getOffHandStack());
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.inventory.armor;
	}

	public boolean addShoulderEntity(CompoundTag compoundTag) {
		if (this.hasVehicle() || !this.onGround || this.isInsideWater()) {
			return false;
		} else if (this.getShoulderEntityLeft().isEmpty()) {
			this.setShoulderEntityLeft(compoundTag);
			this.field_19428 = this.world.getTime();
			return true;
		} else if (this.getShoulderEntityRight().isEmpty()) {
			this.setShoulderEntityRight(compoundTag);
			this.field_19428 = this.world.getTime();
			return true;
		} else {
			return false;
		}
	}

	protected void dropShoulderEntities() {
		if (this.field_19428 + 20L < this.world.getTime()) {
			this.method_7296(this.getShoulderEntityLeft());
			this.setShoulderEntityLeft(new CompoundTag());
			this.method_7296(this.getShoulderEntityRight());
			this.setShoulderEntityRight(new CompoundTag());
		}
	}

	private void method_7296(CompoundTag compoundTag) {
		if (!this.world.isClient && !compoundTag.isEmpty()) {
			EntityType.getEntityFromTag(compoundTag, this.world).ifPresent(entity -> {
				if (entity instanceof TameableEntity) {
					((TameableEntity)entity).setOwnerUuid(this.uuid);
				}

				entity.setPosition(this.getX(), this.getY() + 0.7F, this.getZ());
				((ServerWorld)this.world).method_18768(entity);
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
		Text text = Team.modifyText(this.getScoreboardTeam(), this.getName());
		return this.addTellClickEvent(text);
	}

	public Text getNameAndUuid() {
		return new LiteralText("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
	}

	private Text addTellClickEvent(Text text) {
		String string = this.getGameProfile().getName();
		return text.styled(
			style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " "))
					.setHoverEvent(this.getHoverEvent())
					.setInsertion(string)
		);
	}

	@Override
	public String getEntityName() {
		return this.getGameProfile().getName();
	}

	@Override
	public float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		switch (entityPose) {
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
	public void setAbsorptionAmount(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.getDataTracker().set(ABSORPTION_AMOUNT, f);
	}

	@Override
	public float getAbsorptionAmount() {
		return this.getDataTracker().get(ABSORPTION_AMOUNT);
	}

	public static UUID getUuidFromProfile(GameProfile gameProfile) {
		UUID uUID = gameProfile.getId();
		if (uUID == null) {
			uUID = getOfflinePlayerUuid(gameProfile.getName());
		}

		return uUID;
	}

	public static UUID getOfflinePlayerUuid(String string) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
	}

	@Environment(EnvType.CLIENT)
	public boolean isSkinOverlayVisible(PlayerModelPart playerModelPart) {
		return (this.getDataTracker().get(PLAYER_MODEL_BIT_MASK) & playerModelPart.getBitFlag()) == playerModelPart.getBitFlag();
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
		if (i >= 0 && i < this.inventory.main.size()) {
			this.inventory.setInvStack(i, itemStack);
			return true;
		} else {
			EquipmentSlot equipmentSlot;
			if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.HEAD;
			} else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.CHEST;
			} else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.LEGS;
			} else if (i == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.FEET;
			} else {
				equipmentSlot = null;
			}

			if (i == 98) {
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
				return true;
			} else if (i == 99) {
				this.equipStack(EquipmentSlot.OFFHAND, itemStack);
				return true;
			} else if (equipmentSlot == null) {
				int j = i - 200;
				if (j >= 0 && j < this.enderChestInventory.getInvSize()) {
					this.enderChestInventory.setInvStack(j, itemStack);
					return true;
				} else {
					return false;
				}
			} else {
				if (!itemStack.isEmpty()) {
					if (!(itemStack.getItem() instanceof ArmorItem) && !(itemStack.getItem() instanceof ElytraItem)) {
						if (equipmentSlot != EquipmentSlot.HEAD) {
							return false;
						}
					} else if (MobEntity.getPreferredEquipmentSlot(itemStack) != equipmentSlot) {
						return false;
					}
				}

				this.inventory.setInvStack(equipmentSlot.getEntitySlotId() + this.inventory.main.size(), itemStack);
				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean getReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public void setReducedDebugInfo(boolean bl) {
		this.reducedDebugInfo = bl;
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

	protected void setShoulderEntityLeft(CompoundTag compoundTag) {
		this.dataTracker.set(LEFT_SHOULDER_ENTITY, compoundTag);
	}

	public CompoundTag getShoulderEntityRight() {
		return this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
	}

	protected void setShoulderEntityRight(CompoundTag compoundTag) {
		this.dataTracker.set(RIGHT_SHOULDER_ENTITY, compoundTag);
	}

	public float getAttackCooldownProgressPerTick() {
		return (float)(1.0 / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0);
	}

	public float getAttackCooldownProgress(float f) {
		return MathHelper.clamp(((float)this.lastAttackedTicks + f) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
	}

	public void resetLastAttackedTicks() {
		this.lastAttackedTicks = 0;
	}

	public ItemCooldownManager getItemCooldownManager() {
		return this.itemCooldownManager;
	}

	public float getLuck() {
		return (float)this.getAttributeInstance(EntityAttributes.LUCK).getValue();
	}

	public boolean isCreativeLevelTwoOp() {
		return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return this.getEquippedStack(equipmentSlot).isEmpty();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose entityPose) {
		return (EntityDimensions)POSE_DIMENSIONS.getOrDefault(entityPose, STANDING_DIMENSIONS);
	}

	@Override
	public ItemStack getArrowType(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof RangedWeaponItem)) {
			return ItemStack.EMPTY;
		} else {
			Predicate<ItemStack> predicate = ((RangedWeaponItem)itemStack.getItem()).getHeldProjectiles();
			ItemStack itemStack2 = RangedWeaponItem.getHeldProjectile(this, predicate);
			if (!itemStack2.isEmpty()) {
				return itemStack2;
			} else {
				predicate = ((RangedWeaponItem)itemStack.getItem()).getProjectiles();

				for (int i = 0; i < this.inventory.getInvSize(); i++) {
					ItemStack itemStack3 = this.inventory.getInvStack(i);
					if (predicate.test(itemStack3)) {
						return itemStack3;
					}
				}

				return this.abilities.creativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack eatFood(World world, ItemStack itemStack) {
		this.getHungerManager().eat(itemStack.getItem(), itemStack);
		this.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
		world.playSound(
			null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F
		);
		if (this instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.trigger((ServerPlayerEntity)this, itemStack);
		}

		return super.eatFood(world, itemStack);
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
