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
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class PlayerEntity extends LivingEntity {
	public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6F, 1.8F);
	private static final Map<EntityPose, EntityDimensions> POSE_DIMENSIONS = ImmutableMap.<EntityPose, EntityDimensions>builder()
		.put(EntityPose.field_18076, STANDING_DIMENSIONS)
		.put(EntityPose.field_18078, SLEEPING_DIMENSIONS)
		.put(EntityPose.field_18077, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.field_18079, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.field_18080, EntityDimensions.changing(0.6F, 0.6F))
		.put(EntityPose.field_18081, EntityDimensions.changing(0.6F, 1.5F))
		.put(EntityPose.field_18082, EntityDimensions.fixed(0.2F, 0.2F))
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
		super(EntityType.field_6097, world);
		this.setUuid(getUuidFromProfile(gameProfile));
		this.gameProfile = gameProfile;
		this.playerContainer = new PlayerContainer(this.inventory, !world.isClient, this);
		this.container = this.playerContainer;
		BlockPos blockPos = world.getSpawnPos();
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6215 = 180.0F;
	}

	public boolean method_21701(World world, BlockPos blockPos, GameMode gameMode) {
		if (!gameMode.shouldLimitWorldModification()) {
			return false;
		} else if (gameMode == GameMode.field_9219) {
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
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1F);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_SPEED);
		this.getAttributeContainer().register(EntityAttributes.LUCK);
		this.getAttributeContainer().register(EntityAttributes.field_20339);
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
				this.wakeUp(false, true, true);
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
			this.incrementStat(Stats.field_15417);
			if (this.isAlive()) {
				this.incrementStat(Stats.field_15400);
			}

			if (this.isSneaking()) {
				this.incrementStat(Stats.field_15422);
			}

			if (!this.isSleeping()) {
				this.incrementStat(Stats.field_15429);
			}
		}

		int i = 29999999;
		double d = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
		double e = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
		if (d != this.x || e != this.z) {
			this.setPosition(d, this.y, e);
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

	protected boolean updateInWater() {
		this.isInWater = this.isSubmergedIn(FluidTags.field_15517, true);
		return this.isInWater;
	}

	private void updateTurtleHelmet() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6169);
		if (itemStack.getItem() == Items.field_8090 && !this.isInFluid(FluidTags.field_15517)) {
			this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5923, 200, 0, false, false, true));
		}
	}

	protected ItemCooldownManager createCooldownManager() {
		return new ItemCooldownManager();
	}

	private void method_7313() {
		this.field_7524 = this.field_7500;
		this.field_7502 = this.field_7521;
		this.field_7522 = this.field_7499;
		double d = this.x - this.field_7500;
		double e = this.y - this.field_7521;
		double f = this.z - this.field_7499;
		double g = 10.0;
		if (d > 10.0) {
			this.field_7500 = this.x;
			this.field_7524 = this.field_7500;
		}

		if (f > 10.0) {
			this.field_7499 = this.z;
			this.field_7522 = this.field_7499;
		}

		if (e > 10.0) {
			this.field_7521 = this.y;
			this.field_7502 = this.field_7521;
		}

		if (d < -10.0) {
			this.field_7500 = this.x;
			this.field_7524 = this.field_7500;
		}

		if (f < -10.0) {
			this.field_7499 = this.z;
			this.field_7522 = this.field_7499;
		}

		if (e < -10.0) {
			this.field_7521 = this.y;
			this.field_7502 = this.field_7521;
		}

		this.field_7500 += d * 0.25;
		this.field_7499 += f * 0.25;
		this.field_7521 += e * 0.25;
	}

	protected void updateSize() {
		if (this.wouldPoseNotCollide(EntityPose.field_18079)) {
			EntityPose entityPose;
			if (this.isFallFlying()) {
				entityPose = EntityPose.field_18077;
			} else if (this.isSleeping()) {
				entityPose = EntityPose.field_18078;
			} else if (this.isSwimming()) {
				entityPose = EntityPose.field_18079;
			} else if (this.isUsingRiptide()) {
				entityPose = EntityPose.field_18080;
			} else if (this.isSneaking() && !this.abilities.flying) {
				entityPose = EntityPose.field_18081;
			} else {
				entityPose = EntityPose.field_18076;
			}

			EntityPose entityPose2;
			if (this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(entityPose)) {
				entityPose2 = entityPose;
			} else if (this.wouldPoseNotCollide(EntityPose.field_18081)) {
				entityPose2 = EntityPose.field_18081;
			} else {
				entityPose2 = EntityPose.field_18079;
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
		return SoundEvents.field_14998;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.field_14810;
	}

	@Override
	protected SoundEvent getHighSpeedSplashSound() {
		return SoundEvents.field_14876;
	}

	@Override
	public int getDefaultPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(SoundEvent soundEvent, float f, float g) {
		this.world.playSound(this, this.x, this.y, this.z, soundEvent, this.getSoundCategory(), f, g);
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
			this.method_6040();
		} else if (b == 23) {
			this.reducedDebugInfo = false;
		} else if (b == 22) {
			this.reducedDebugInfo = true;
		} else if (b == 43) {
			this.spawnParticles(ParticleTypes.field_11204);
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
			this.world
				.addParticle(
					particleEffect,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	protected void closeContainer() {
		this.container = this.playerContainer;
	}

	@Override
	public void tickRiding() {
		if (!this.world.isClient && this.isSneaking() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.yaw;
			float h = this.pitch;
			super.tickRiding();
			this.field_7505 = this.field_7483;
			this.field_7483 = 0.0F;
			this.method_7260(this.x - d, this.y - e, this.z - f);
			if (this.getVehicle() instanceof PigEntity) {
				this.pitch = h;
				this.yaw = g;
				this.field_6283 = ((PigEntity)this.getVehicle()).field_6283;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void afterSpawn() {
		this.setPose(EntityPose.field_18076);
		super.afterSpawn();
		this.setHealth(this.getHealthMaximum());
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

		if (this.world.getDifficulty() == Difficulty.field_5801 && this.world.getGameRules().getBoolean(GameRules.field_19395)) {
			if (this.getHealth() < this.getHealthMaximum() && this.age % 20 == 0) {
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

		this.field_6281 = 0.02F;
		if (this.isSprinting()) {
			this.field_6281 = (float)((double)this.field_6281 + 0.005999999865889549);
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
		if (!this.world.isClient && (this.fallDistance > 0.5F || this.isInsideWater() || this.hasVehicle()) || this.abilities.flying || this.isSleeping()) {
			this.dropShoulderEntities();
		}
	}

	private void updateShoulderEntity(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && !compoundTag.containsKey("Silent") || !compoundTag.getBoolean("Silent")) {
			String string = compoundTag.getString("id");
			EntityType.get(string).filter(entityType -> entityType == EntityType.field_6104).ifPresent(entityType -> ParrotEntity.playMobSound(this.world, this));
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
		this.setPosition(this.x, this.y, this.z);
		if (!this.isSpectator()) {
			this.drop(damageSource);
		}

		if (damageSource != null) {
			this.setVelocity(
				(double)(-MathHelper.cos((this.field_6271 + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F),
				0.1F,
				(double)(-MathHelper.sin((this.field_6271 + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F)
			);
		} else {
			this.setVelocity(0.0, 0.1, 0.0);
		}

		this.incrementStat(Stats.field_15421);
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15400));
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.extinguish();
		this.setFlag(0, false);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (!this.world.getGameRules().getBoolean(GameRules.field_19389)) {
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
			return SoundEvents.field_14623;
		} else if (damageSource == DamageSource.DROWN) {
			return SoundEvents.field_15205;
		} else {
			return damageSource == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.field_17614 : SoundEvents.field_15115;
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14904;
	}

	@Nullable
	public ItemEntity dropSelectedItem(boolean bl) {
		return this.dropItem(
			this.inventory
				.takeInvStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1),
			false,
			true
		);
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
			double d = this.y - 0.3F + (double)this.getStandingEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, itemStack);
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

		if (this.hasStatusEffect(StatusEffects.field_5901)) {
			float g;
			switch (this.getStatusEffect(StatusEffects.field_5901).getAmplifier()) {
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

		if (this.isInFluid(FluidTags.field_15517) && !EnchantmentHelper.hasAquaAffinity(this)) {
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
		if (compoundTag.containsKey("SpawnX", 99) && compoundTag.containsKey("SpawnY", 99) && compoundTag.containsKey("SpawnZ", 99)) {
			this.spawnPosition = new BlockPos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
			this.spawnForced = compoundTag.getBoolean("SpawnForced");
		}

		this.hungerManager.deserialize(compoundTag);
		this.abilities.deserialize(compoundTag);
		if (compoundTag.containsKey("EnderItems", 9)) {
			this.enderChestInventory.readTags(compoundTag.getList("EnderItems", 10));
		}

		if (compoundTag.containsKey("ShoulderEntityLeft", 10)) {
			this.setShoulderEntityLeft(compoundTag.getCompound("ShoulderEntityLeft"));
		}

		if (compoundTag.containsKey("ShoulderEntityRight", 10)) {
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
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.abilities.invulnerable && !damageSource.doesDamageToCreative()) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				this.dropShoulderEntities();
				if (damageSource.isScaledWithDifficulty()) {
					if (this.world.getDifficulty() == Difficulty.field_5801) {
						f = 0.0F;
					}

					if (this.world.getDifficulty() == Difficulty.field_5805) {
						f = Math.min(f / 2.0F + 1.0F, f);
					}

					if (this.world.getDifficulty() == Difficulty.field_5807) {
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
	}

	@Override
	protected void knockback(LivingEntity livingEntity) {
		super.knockback(livingEntity);
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
		ItemStack itemStack = this.activeItemStack;
		Hand hand = this.getActiveHand();
		if (itemStack.getItem() != Items.field_8255) {
			itemStack = this.getOffHandStack();
			hand = Hand.field_5810;
		}

		if (f >= 3.0F && itemStack.getItem() == Items.field_8255) {
			int i = 1 + MathHelper.floor(f);
			Hand hand2 = hand;
			itemStack.damage(i, this, playerEntity -> playerEntity.sendToolBreakStatus(hand2));
			if (itemStack.isEmpty()) {
				if (hand == Hand.field_5808) {
					this.setEquippedStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
				} else {
					this.setEquippedStack(EquipmentSlot.field_6171, ItemStack.EMPTY);
				}

				this.activeItemStack = ItemStack.EMPTY;
				this.playSound(SoundEvents.field_15239, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
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
				this.increaseStat(Stats.field_15365, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				this.addExhaustion(damageSource.getExhaustion());
				float i = this.getHealth();
				this.setHealth(this.getHealth() - var8);
				this.getDamageTracker().onDamage(damageSource, i, var8);
				if (var8 < 3.4028235E37F) {
					this.increaseStat(Stats.field_15388, Math.round(var8 * 10.0F));
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

			return ActionResult.field_5811;
		} else {
			ItemStack itemStack = this.getStackInHand(hand);
			ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
			if (entity.interact(this, hand)) {
				if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
					itemStack.setCount(itemStack2.getCount());
				}

				return ActionResult.field_5812;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.useOnEntity(this, (LivingEntity)entity, hand)) {
						if (itemStack.isEmpty() && !this.abilities.creativeMode) {
							this.setStackInHand(hand, ItemStack.EMPTY);
						}

						return ActionResult.field_5812;
					}
				}

				return ActionResult.field_5811;
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
	protected boolean cannotMove() {
		return super.cannotMove() || this.isSleeping();
	}

	public void attack(Entity entity) {
		if (entity.isAttackable()) {
			if (!entity.handleAttack(this)) {
				float f = this.getAttackCooldownProgress(1.0F);
				if (!(f < 1.0F)) {
					float g = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
					float h;
					if (entity instanceof LivingEntity) {
						h = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
					} else {
						h = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
					}

					float i = this.method_21753(1.0F);
					this.resetLastAttackedTicks();
					if (g > 0.0F || h > 0.0F) {
						boolean bl = f > 1.95F;
						boolean bl2 = false;
						int j = 0;
						j += EnchantmentHelper.getKnockback(this);
						if (this.isSprinting() && bl) {
							this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14999, this.getSoundCategory(), 1.0F, 1.0F);
							j++;
							bl2 = true;
						}

						boolean bl3 = bl
							&& this.fallDistance > 0.0F
							&& !this.onGround
							&& !this.isClimbing()
							&& !this.isInsideWater()
							&& !this.hasStatusEffect(StatusEffects.field_5919)
							&& !this.hasVehicle()
							&& entity instanceof LivingEntity;
						bl3 = bl3 && !this.isSprinting();
						if (bl3) {
							g *= 1.5F;
						}

						g += h;
						boolean bl4 = false;
						if (bl && !bl3 && !bl2 && this.method_21751()) {
							bl4 = true;
						}

						float k = 0.0F;
						boolean bl5 = false;
						int l = EnchantmentHelper.getFireAspect(this);
						if (entity instanceof LivingEntity) {
							k = ((LivingEntity)entity).getHealth();
							if (l > 0 && !entity.isOnFire()) {
								bl5 = true;
								entity.setOnFireFor(1);
							}
						}

						Vec3d vec3d = entity.getVelocity();
						boolean bl6 = entity.damage(DamageSource.player(this).method_21745(bl3), g);
						if (bl6) {
							if (j > 0) {
								if (entity instanceof LivingEntity) {
									((LivingEntity)entity)
										.takeKnockback(
											this, (float)j * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
										);
								} else {
									entity.addVelocity(
										(double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (float)j * 0.5F),
										0.1,
										(double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (float)j * 0.5F)
									);
								}

								this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
								this.setSprinting(false);
							}

							if (bl4) {
								Box box = entity.getBoundingBox().expand(1.0, 0.25, 1.0);
								this.method_21749(box, i, g, entity);
							}

							if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
								((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
								entity.velocityModified = false;
								entity.setVelocity(vec3d);
							}

							if (bl3) {
								this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_15016, this.getSoundCategory(), 1.0F, 1.0F);
								this.addCritParticles(entity);
							}

							if (!bl3 && !bl4) {
								if (bl) {
									this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14840, this.getSoundCategory(), 1.0F, 1.0F);
								} else {
									this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14625, this.getSoundCategory(), 1.0F, 1.0F);
								}
							}

							if (h > 0.0F) {
								this.addEnchantedHitParticles(entity);
							}

							this.onAttacking(entity);
							if (entity instanceof LivingEntity) {
								EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
							}

							EnchantmentHelper.onTargetDamaged(this, entity);
							ItemStack itemStack = this.getMainHandStack();
							Entity entity2 = entity;
							if (entity instanceof EnderDragonPart) {
								entity2 = ((EnderDragonPart)entity).owner;
							}

							if (!this.world.isClient && !itemStack.isEmpty() && entity2 instanceof LivingEntity) {
								itemStack.postHit((LivingEntity)entity2, this);
								if (itemStack.isEmpty()) {
									this.setStackInHand(Hand.field_5808, ItemStack.EMPTY);
								}
							}

							if (entity instanceof LivingEntity) {
								float m = k - ((LivingEntity)entity).getHealth();
								this.increaseStat(Stats.field_15399, Math.round(m * 10.0F));
								if (l > 0) {
									entity.setOnFireFor(l * 4);
								}

								if (this.world instanceof ServerWorld && m > 2.0F) {
									int n = (int)((double)m * 0.5);
									((ServerWorld)this.world)
										.spawnParticles(ParticleTypes.field_11209, entity.x, entity.y + (double)(entity.getHeight() * 0.5F), entity.z, n, 0.1, 0.0, 0.1, 0.2);
								}
							}

							this.addExhaustion(0.1F);
						} else {
							this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14914, this.getSoundCategory(), 1.0F, 1.0F);
							if (bl5) {
								entity.extinguish();
							}
						}
					}
				}
			}
		}
	}

	public void method_21750() {
		float f = this.getAttackCooldownProgress(1.0F);
		if (!(f < 1.0F)) {
			this.swingHand(Hand.field_5808);
			this.resetLastAttackedTicks();
			float g = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
			boolean bl = f > 1.95F;
			if (bl && g > 0.0F && this.method_21751()) {
				float h = this.method_21753(1.0F);
				double d = 2.0;
				double e = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0))) * 2.0;
				double i = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 2.0;
				Box box = this.getBoundingBox().expand(1.0, 0.25, 1.0).offset(e, 0.0, i);
				this.method_21749(box, h, g, null);
			}
		}
	}

	@Override
	protected void attackLivingEntity(LivingEntity livingEntity) {
		this.attack(livingEntity);
	}

	@Override
	public boolean method_21748(float f) {
		if (this.random.nextFloat() < f) {
			this.getItemCooldownManager().set(Items.field_8255, 100);
			this.clearActiveItem();
			this.world.sendEntityStatus(this, (byte)30);
			return true;
		} else {
			return false;
		}
	}

	public void addCritParticles(Entity entity) {
	}

	public void addEnchantedHitParticles(Entity entity) {
	}

	protected boolean method_21751() {
		double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
		return this.onGround && d < (double)this.getMovementSpeed() && EnchantmentHelper.getSweepingMultiplier(this) > 0.0F;
	}

	public void method_21749(Box box, float f, float g, Entity entity) {
		float h = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * g;

		for (LivingEntity livingEntity : this.world.getEntities(LivingEntity.class, box)) {
			if (livingEntity != this
				&& livingEntity != entity
				&& !this.isTeammate(livingEntity)
				&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())) {
				float i = f + livingEntity.getWidth() * 0.5F;
				if (this.squaredDistanceTo(livingEntity) < (double)(i * i)) {
					livingEntity.takeKnockback(
						this, 0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
					);
					livingEntity.damage(DamageSource.player(this), h);
				}
			}
		}

		this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14706, this.getSoundCategory(), 1.0F, 1.0F);
		if (this.world instanceof ServerWorld) {
			double d = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)));
			double e = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
			((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11227, this.x + d, this.y + (double)this.getHeight() * 0.5, this.z + e, 0, d, 0.0, e, 0.0);
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
				return Either.left(PlayerEntity.SleepFailureReason.field_7531);
			}

			if (!this.world.dimension.hasVisibleSky()) {
				return Either.left(PlayerEntity.SleepFailureReason.field_7528);
			}

			if (this.world.isDaylight()) {
				return Either.left(PlayerEntity.SleepFailureReason.field_7529);
			}

			if (!this.isWithinSleepingRange(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepFailureReason.field_7530);
			}

			if (this.isBedObstructed(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepFailureReason.field_18592);
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
					return Either.left(PlayerEntity.SleepFailureReason.field_7532);
				}
			}
		}

		this.sleep(blockPos);
		this.sleepTimer = 0;
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		return Either.right(Unit.field_17274);
	}

	@Override
	public void sleep(BlockPos blockPos) {
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		super.sleep(blockPos);
	}

	private boolean isWithinSleepingRange(BlockPos blockPos, Direction direction) {
		if (Math.abs(this.x - (double)blockPos.getX()) <= 3.0
			&& Math.abs(this.y - (double)blockPos.getY()) <= 2.0
			&& Math.abs(this.z - (double)blockPos.getZ()) <= 3.0) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
			return Math.abs(this.x - (double)blockPos2.getX()) <= 3.0
				&& Math.abs(this.y - (double)blockPos2.getY()) <= 2.0
				&& Math.abs(this.z - (double)blockPos2.getZ()) <= 3.0;
		}
	}

	private boolean isBedObstructed(BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.up();
		return !this.doesNotSuffocate(blockPos2) || !this.doesNotSuffocate(blockPos2.offset(direction.getOpposite()));
	}

	public void wakeUp(boolean bl, boolean bl2, boolean bl3) {
		Optional<BlockPos> optional = this.getSleepingPosition();
		super.wakeUp();
		if (this.world instanceof ServerWorld && bl2) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		this.sleepTimer = bl ? 0 : 100;
		if (bl3) {
			optional.ifPresent(blockPos -> this.setPlayerSpawn(blockPos, false));
		}
	}

	@Override
	public void wakeUp() {
		this.wakeUp(true, true, false);
	}

	public static Optional<Vec3d> method_7288(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl) {
		Block block = viewableWorld.getBlockState(blockPos).getBlock();
		if (!(block instanceof BedBlock)) {
			if (!bl) {
				return Optional.empty();
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = viewableWorld.getBlockState(blockPos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? Optional.of(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.1, (double)blockPos.getZ() + 0.5)) : Optional.empty();
			}
		} else {
			return BedBlock.findWakeUpPosition(EntityType.field_6097, viewableWorld, blockPos, 0);
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
		this.incrementStat(Stats.field_15419.getOrCreateStat(identifier));
	}

	public void increaseStat(Identifier identifier, int i) {
		this.increaseStat(Stats.field_15419.getOrCreateStat(identifier), i);
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
		this.incrementStat(Stats.field_15428);
		if (this.isSprinting()) {
			this.addExhaustion(0.2F);
		} else {
			this.addExhaustion(0.05F);
		}
	}

	@Override
	public void travel(Vec3d vec3d) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		if (this.isSwimming() && !this.hasVehicle()) {
			double g = this.getRotationVector().y;
			double h = g < -0.2 ? 0.085 : 0.06;
			if (g <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).getFluidState().isEmpty()) {
				Vec3d vec3d2 = this.getVelocity();
				this.setVelocity(vec3d2.add(0.0, (g - vec3d2.y) * h, 0.0));
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double g = this.getVelocity().y;
			float i = this.field_6281;
			this.field_6281 = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.travel(vec3d);
			Vec3d vec3d3 = this.getVelocity();
			this.setVelocity(vec3d3.x, g * 0.6, vec3d3.z);
			this.field_6281 = i;
			this.fallDistance = 0.0F;
			this.setFlag(7, false);
		} else {
			super.travel(vec3d);
		}

		this.method_7282(this.x - d, this.y - e, this.z - f);
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
					this.increaseStat(Stats.field_15423, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isSubmergedIn(FluidTags.field_15517, true)) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.field_15401, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isInsideWater()) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.field_15394, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (e > 0.0) {
					this.increaseStat(Stats.field_15413, (int)Math.round(e * 100.0));
				}
			} else if (this.onGround) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 0) {
					if (this.isSprinting()) {
						this.increaseStat(Stats.field_15364, i);
						this.addExhaustion(0.1F * (float)i * 0.01F);
					} else if (this.isSneaking()) {
						this.increaseStat(Stats.field_15376, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					} else {
						this.increaseStat(Stats.field_15377, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					}
				}
			} else if (this.isFallFlying()) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				this.increaseStat(Stats.field_15374, i);
			} else {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 25) {
					this.increaseStat(Stats.field_15426, i);
				}
			}
		}
	}

	private void method_7260(double d, double e, double f) {
		if (this.hasVehicle()) {
			int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
			if (i > 0) {
				if (this.getVehicle() instanceof AbstractMinecartEntity) {
					this.increaseStat(Stats.field_15409, i);
				} else if (this.getVehicle() instanceof BoatEntity) {
					this.increaseStat(Stats.field_15415, i);
				} else if (this.getVehicle() instanceof PigEntity) {
					this.increaseStat(Stats.field_15387, i);
				} else if (this.getVehicle() instanceof HorseBaseEntity) {
					this.increaseStat(Stats.field_15396, i);
				}
			}
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (!this.abilities.allowFlying) {
			if (f >= 2.0F) {
				this.increaseStat(Stats.field_15386, (int)Math.round((double)f * 100.0));
			}

			super.handleFallDamage(f, g);
		}
	}

	@Override
	protected void onSwimmingStart() {
		if (!this.isSpectator()) {
			super.onSwimmingStart();
		}
	}

	@Override
	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.field_14794 : SoundEvents.field_14778;
	}

	@Override
	public void onKilledOther(LivingEntity livingEntity) {
		this.incrementStat(Stats.field_15403.getOrCreateStat(livingEntity.getType()));
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
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14709, this.getSoundCategory(), f * 0.75F, 1.0F);
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
		return this.getHealth() > 0.0F && this.getHealth() < this.getHealthMaximum();
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
		if (!this.world.getGameRules().getBoolean(GameRules.field_19389) && !this.isSpectator()) {
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
		return !this.abilities.flying;
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
		if (equipmentSlot == EquipmentSlot.field_6173) {
			return this.inventory.getMainHandStack();
		} else if (equipmentSlot == EquipmentSlot.field_6171) {
			return this.inventory.offHand.get(0);
		} else {
			return equipmentSlot.getType() == EquipmentSlot.Type.field_6178 ? this.inventory.armor.get(equipmentSlot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		if (equipmentSlot == EquipmentSlot.field_6173) {
			this.onEquipStack(itemStack);
			this.inventory.main.set(this.inventory.selectedSlot, itemStack);
		} else if (equipmentSlot == EquipmentSlot.field_6171) {
			this.onEquipStack(itemStack);
			this.inventory.offHand.set(0, itemStack);
		} else if (equipmentSlot.getType() == EquipmentSlot.Type.field_6178) {
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

				entity.setPosition(this.x, this.y + 0.7F, this.z);
				((ServerWorld)this.world).method_18768(entity);
			});
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean canSeePlayer(PlayerEntity playerEntity) {
		if (!this.isInvisible()) {
			return false;
		} else if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam == null || playerEntity == null || playerEntity.getScoreboardTeam() != abstractTeam || !abstractTeam.shouldShowFriendlyInvisibles();
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
			style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, "/tell " + string + " "))
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
			case field_18079:
			case field_18077:
			case field_18080:
				return 0.4F;
			case field_18081:
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
			if (i == 100 + EquipmentSlot.field_6169.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.field_6169;
			} else if (i == 100 + EquipmentSlot.field_6174.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.field_6174;
			} else if (i == 100 + EquipmentSlot.field_6172.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.field_6172;
			} else if (i == 100 + EquipmentSlot.field_6166.getEntitySlotId()) {
				equipmentSlot = EquipmentSlot.field_6166;
			} else {
				equipmentSlot = null;
			}

			if (i == 98) {
				this.setEquippedStack(EquipmentSlot.field_6173, itemStack);
				return true;
			} else if (i == 99) {
				this.setEquippedStack(EquipmentSlot.field_6171, itemStack);
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
						if (equipmentSlot != EquipmentSlot.field_6169) {
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
		return this.dataTracker.get(MAIN_ARM) == 0 ? Arm.field_6182 : Arm.field_6183;
	}

	public void setMainArm(Arm arm) {
		this.dataTracker.set(MAIN_ARM, (byte)(arm == Arm.field_6182 ? 0 : 1));
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

	public float method_21752() {
		float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() - 1.5F;
		f = MathHelper.clamp(f, 0.1F, 1024.0F);
		return 1.0F / f * 20.0F;
	}

	@Override
	public float getAttackCooldownProgress(float f) {
		return MathHelper.clamp(((float)this.lastAttackedTicks + f) / this.method_21752(), 0.0F, 2.0F);
	}

	public void resetLastAttackedTicks() {
		this.lastAttackedTicks = 0;
	}

	public float method_21753(float f) {
		float g = 0.0F;
		float h = this.getAttackCooldownProgress(f);
		if (h > 1.95F) {
			g = 0.5F;
		}

		return (float)this.getAttributeInstance(EntityAttributes.field_20339).getValue() + g;
	}

	public ItemCooldownManager getItemCooldownManager() {
		return this.itemCooldownManager;
	}

	@Override
	public boolean method_21747(ItemStack itemStack) {
		return this.itemCooldownManager.isCoolingDown(itemStack.getItem());
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

				return this.abilities.creativeMode ? new ItemStack(Items.field_8107) : ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack eatFood(World world, ItemStack itemStack) {
		this.getHungerManager().eat(itemStack.getItem(), itemStack);
		this.incrementStat(Stats.field_15372.getOrCreateStat(itemStack.getItem()));
		world.playSound(null, this.x, this.y, this.z, SoundEvents.field_19149, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		if (this instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)this, itemStack);
		}

		return super.eatFood(world, itemStack);
	}

	public static enum SleepFailureReason {
		field_7528,
		field_7529(new TranslatableText("block.minecraft.bed.no_sleep")),
		field_7530(new TranslatableText("block.minecraft.bed.too_far_away")),
		field_18592(new TranslatableText("block.minecraft.bed.obstructed")),
		field_7531,
		field_7532(new TranslatableText("block.minecraft.bed.not_safe"));

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
