package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
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
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.entity.parts.EntityPart;
import net.minecraft.entity.parts.IEntityPartDamageDelegate;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public abstract class PlayerEntity extends LivingEntity {
	private static final TrackedData<Float> ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> PLAYER_MODEL_BIT_MASK = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> MAIN_HAND = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	public final PlayerInventory inventory = new PlayerInventory(this);
	protected EnderChestInventory enderChestInventory = new EnderChestInventory();
	public final PlayerContainer containerPlayer;
	public Container container;
	protected HungerManager hungerManager = new HungerManager();
	protected int field_7489;
	public float field_7505;
	public float field_7483;
	public int field_7504;
	public double field_7524;
	public double field_7502;
	public double field_7522;
	public double field_7500;
	public double field_7521;
	public double field_7499;
	protected boolean sleeping;
	public BlockPos sleepingPos;
	private int sleepTimer;
	public float field_7516;
	@Environment(EnvType.CLIENT)
	public float renderOffsetY;
	public float field_7497;
	private boolean field_7517;
	protected boolean field_7490;
	private BlockPos spawnPosition;
	private boolean spawnForced;
	public final PlayerAbilities abilities = new PlayerAbilities();
	public int experience;
	public int experienceLevel;
	public float experienceBarProgress;
	protected int enchantmentTableSeed;
	protected final float field_7509 = 0.02F;
	private int field_7508;
	private final GameProfile gameProfile;
	@Environment(EnvType.CLIENT)
	private boolean reducedDebugInfo;
	private ItemStack field_7525 = ItemStack.EMPTY;
	private final ItemCooldownManager itemCooldownManager = this.createCooldownManager();
	@Nullable
	public FishHookEntity fishHook;

	public PlayerEntity(World world, GameProfile gameProfile) {
		super(EntityType.PLAYER, world);
		this.setUuid(getUuidFromProfile(gameProfile));
		this.gameProfile = gameProfile;
		this.containerPlayer = new PlayerContainer(this.inventory, !world.isClient, this);
		this.container = this.containerPlayer;
		BlockPos blockPos = world.getSpawnPos();
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6215 = 180.0F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1F);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_SPEED);
		this.getAttributeContainer().register(EntityAttributes.LUCK);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ABSORPTION_AMOUNT, 0.0F);
		this.dataTracker.startTracking(SCORE, 0);
		this.dataTracker.startTracking(PLAYER_MODEL_BIT_MASK, (byte)0);
		this.dataTracker.startTracking(MAIN_HAND, (byte)1);
		this.dataTracker.startTracking(LEFT_SHOULDER_ENTITY, new CompoundTag());
		this.dataTracker.startTracking(RIGHT_SHOULDER_ENTITY, new CompoundTag());
	}

	@Override
	public void update() {
		this.noClip = this.isSpectator();
		if (this.isSpectator()) {
			this.onGround = false;
		}

		if (this.field_7504 > 0) {
			this.field_7504--;
		}

		if (this.isSleeping()) {
			this.sleepTimer++;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.world.isClient) {
				if (!this.method_7275()) {
					this.wakeUp(true, true, false);
				} else if (this.world.isDaylight()) {
					this.wakeUp(false, true, true);
				}
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		this.method_7300();
		this.method_7295();
		super.update();
		if (!this.world.isClient && this.container != null && !this.container.canUse(this)) {
			this.closeGui();
			this.container = this.containerPlayer;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.method_7313();
		if (!this.world.isClient) {
			this.hungerManager.update(this);
			this.increaseStat(Stats.field_15417);
			if (this.isValid()) {
				this.increaseStat(Stats.field_15400);
			}

			if (this.isSneaking()) {
				this.increaseStat(Stats.field_15422);
			}

			if (!this.isSleeping()) {
				this.increaseStat(Stats.field_15429);
			}
		}

		int i = 29999999;
		double d = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
		double e = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
		if (d != this.x || e != this.z) {
			this.setPosition(d, this.y, e);
		}

		this.field_6273++;
		ItemStack itemStack = this.getMainHandStack();
		if (!ItemStack.areEqual(this.field_7525, itemStack)) {
			if (!ItemStack.areEqualIgnoreDurability(this.field_7525, itemStack)) {
				this.method_7350();
			}

			this.field_7525 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
		}

		this.method_7330();
		this.itemCooldownManager.update();
		this.updateSize();
	}

	protected boolean method_7295() {
		this.field_7490 = this.method_5744(FluidTags.field_15517, true);
		return this.field_7490;
	}

	private void method_7330() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (itemStack.getItem() == Items.field_8090 && !this.method_5777(FluidTags.field_15517)) {
			this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5923, 200, 0, false, false, true));
		}
	}

	protected ItemCooldownManager createCooldownManager() {
		return new ItemCooldownManager();
	}

	private void method_7300() {
		BlockState blockState = this.world.getBlockState(this.getBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001), Blocks.field_10422);
		if (blockState != null) {
			if (!this.field_7517 && !this.field_5953 && blockState.getBlock() == Blocks.field_10422 && !this.isSpectator()) {
				boolean bl = (Boolean)blockState.get(BubbleColumnBlock.DRAG);
				if (bl) {
					this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14752, this.getSoundCategory(), 1.0F, 1.0F, false);
				} else {
					this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14570, this.getSoundCategory(), 1.0F, 1.0F, false);
				}
			}

			this.field_7517 = true;
		} else {
			this.field_7517 = false;
		}
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
		float f;
		float g;
		if (this.isFallFlying()) {
			f = 0.6F;
			g = 0.6F;
		} else if (this.isSleeping()) {
			f = 0.2F;
			g = 0.2F;
		} else if (this.isSwimming() || this.isUsingRiptide()) {
			f = 0.6F;
			g = 0.6F;
		} else if (this.isSneaking()) {
			f = 0.6F;
			g = 1.65F;
		} else {
			f = 0.6F;
			g = 1.8F;
		}

		if (f != this.getWidth() || g != this.getHeight()) {
			BoundingBox boundingBox = this.getBoundingBox();
			boundingBox = new BoundingBox(
				boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.minX + (double)f, boundingBox.minY + (double)g, boundingBox.minZ + (double)f
			);
			if (this.world.isEntityColliding(this, boundingBox)) {
				this.setSize(f, g);
			}
		}
	}

	@Override
	public int getMaxPortalTime() {
		return this.abilities.invulnerable ? 1 : 80;
	}

	@Override
	protected SoundEvent getSoundSwim() {
		return SoundEvents.field_14998;
	}

	@Override
	protected SoundEvent getSoundSplash() {
		return SoundEvents.field_14810;
	}

	@Override
	protected SoundEvent method_5672() {
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
		return SoundCategory.field_15248;
	}

	@Override
	protected int method_5676() {
		return 20;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 9) {
			this.method_6040();
		} else if (b == 23) {
			this.reducedDebugInfo = false;
		} else if (b == 22) {
			this.reducedDebugInfo = true;
		} else if (b == 43) {
			this.method_16475(ParticleTypes.field_11204);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_16475(ParticleParameters particleParameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean method_6062() {
		return this.getHealth() <= 0.0F || this.isSleeping();
	}

	protected void closeGui() {
		this.container = this.containerPlayer;
	}

	@Override
	public void method_5842() {
		if (!this.world.isClient && this.isSneaking() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.yaw;
			float h = this.pitch;
			super.method_5842();
			this.field_7505 = this.field_7483;
			this.field_7483 = 0.0F;
			this.method_7260(this.x - d, this.y - e, this.z - f);
			if (this.getRiddenEntity() instanceof PigEntity) {
				this.pitch = h;
				this.yaw = g;
				this.field_6283 = ((PigEntity)this.getRiddenEntity()).field_6283;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5823() {
		this.setSize(0.6F, 1.8F);
		super.method_5823();
		this.setHealth(this.getHealthMaximum());
		this.deathCounter = 0;
	}

	@Override
	protected void method_6023() {
		super.method_6023();
		this.method_6119();
		this.headYaw = this.yaw;
	}

	@Override
	public void updateMovement() {
		if (this.field_7489 > 0) {
			this.field_7489--;
		}

		if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
			if (this.getHealth() < this.getHealthMaximum() && this.age % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
				this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.field_7505 = this.field_7483;
		super.updateMovement();
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (!this.world.isClient) {
			entityAttributeInstance.setBaseValue((double)this.abilities.getWalkSpeed());
		}

		this.field_6281 = 0.02F;
		if (this.isSprinting()) {
			this.field_6281 = (float)((double)this.field_6281 + 0.005999999865889549);
		}

		this.method_6125((float)entityAttributeInstance.getValue());
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		float g = (float)(Math.atan(-this.velocityY * 0.2F) * 15.0);
		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F || this.isSwimming()) {
			f = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			g = 0.0F;
		}

		this.field_7483 = this.field_7483 + (f - this.field_7483) * 0.4F;
		this.field_6223 = this.field_6223 + (g - this.field_6223) * 0.8F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			BoundingBox boundingBox;
			if (this.hasVehicle() && !this.getRiddenEntity().invalid) {
				boundingBox = this.getBoundingBox().union(this.getRiddenEntity().getBoundingBox()).expand(1.0, 0.0, 1.0);
			} else {
				boundingBox = this.getBoundingBox().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.world.getVisibleEntities(this, boundingBox);

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (!entity.invalid) {
					this.method_7341(entity);
				}
			}
		}

		this.method_7267(this.getShoulderEntityLeft());
		this.method_7267(this.getShoulderEntityRight());
		if (!this.world.isClient && (this.fallDistance > 0.5F || this.isInsideWater() || this.hasVehicle()) || this.abilities.flying) {
			this.method_7262();
		}
	}

	private void method_7267(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && !compoundTag.containsKey("Silent") || !compoundTag.getBoolean("Silent")) {
			String string = compoundTag.getString("id");
			EntityType.get(string).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> ParrotEntity.method_6589(this.world, this));
		}
	}

	private void method_7341(Entity entity) {
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
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.x, this.y, this.z);
		this.velocityY = 0.1F;
		if ("Notch".equals(this.getName().getString())) {
			this.dropItem(new ItemStack(Items.field_8279), true, false);
		}

		if (!this.isSpectator()) {
			this.method_16080(damageSource);
		}

		if (damageSource != null) {
			this.velocityX = (double)(-MathHelper.cos((this.field_6271 + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F);
			this.velocityZ = (double)(-MathHelper.sin((this.field_6271 + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F);
		} else {
			this.velocityX = 0.0;
			this.velocityZ = 0.0;
		}

		this.increaseStat(Stats.field_15421);
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15400));
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.extinguish();
		this.setEntityFlag(0, false);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (!this.world.getGameRules().getBoolean("keepInventory")) {
			this.method_7293();
			this.inventory.dropAll();
		}
	}

	protected void method_7293() {
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
				.takeInvStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getAmount() : 1),
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
			double d = this.y - 0.3F + (double)this.getEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, itemStack);
			itemEntity.setPickupDelay(40);
			if (bl2) {
				itemEntity.setThrower(this.getUuid());
			}

			if (bl) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				itemEntity.velocityX = (double)(-MathHelper.sin(g) * f);
				itemEntity.velocityZ = (double)(MathHelper.cos(g) * f);
				itemEntity.velocityY = 0.2F;
			} else {
				float f = 0.3F;
				itemEntity.velocityX = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
				itemEntity.velocityZ = (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
				itemEntity.velocityY = (double)(-MathHelper.sin(this.pitch * (float) (Math.PI / 180.0)) * f + 0.1F);
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				f = 0.02F * this.random.nextFloat();
				itemEntity.velocityX = itemEntity.velocityX + Math.cos((double)g) * (double)f;
				itemEntity.velocityY = itemEntity.velocityY + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
				itemEntity.velocityZ = itemEntity.velocityZ + Math.sin((double)g) * (double)f;
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

		if (this.hasPotionEffect(StatusEffects.field_5901)) {
			float g;
			switch (this.getPotionEffect(StatusEffects.field_5901).getAmplifier()) {
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

		if (this.method_5777(FluidTags.field_15517) && !EnchantmentHelper.hasAquaAffinity(this)) {
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
		this.sleeping = compoundTag.getBoolean("Sleeping");
		this.sleepTimer = compoundTag.getShort("SleepTimer");
		this.experienceBarProgress = compoundTag.getFloat("XpP");
		this.experience = compoundTag.getInt("XpLevel");
		this.experienceLevel = compoundTag.getInt("XpTotal");
		this.enchantmentTableSeed = compoundTag.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(compoundTag.getInt("Score"));
		if (this.sleeping) {
			this.sleepingPos = new BlockPos(this);
			this.wakeUp(true, true, false);
		}

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

		if (!this.world.isClient && this.world.getRaidManager() != null && this.method_6088().keySet().contains(StatusEffects.field_16595)) {
			this.world.getRaidManager().addTimestamp(this);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Inventory", this.inventory.serialize(new ListTag()));
		compoundTag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		compoundTag.putBoolean("Sleeping", this.sleeping);
		compoundTag.putShort("SleepTimer", (short)this.sleepTimer);
		compoundTag.putFloat("XpP", this.experienceBarProgress);
		compoundTag.putInt("XpLevel", this.experience);
		compoundTag.putInt("XpTotal", this.experienceLevel);
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
				if (this.isSleeping() && !this.world.isClient) {
					this.wakeUp(true, true, false);
				}

				this.method_7262();
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
	protected void method_6090(LivingEntity livingEntity) {
		super.method_6090(livingEntity);
		if (livingEntity.getMainHandStack().getItem() instanceof AxeItem) {
			this.method_7284(true);
		}
	}

	public boolean shouldDamagePlayer(PlayerEntity playerEntity) {
		AbstractScoreboardTeam abstractScoreboardTeam = this.getScoreboardTeam();
		AbstractScoreboardTeam abstractScoreboardTeam2 = playerEntity.getScoreboardTeam();
		if (abstractScoreboardTeam == null) {
			return true;
		} else {
			return !abstractScoreboardTeam.isEqual(abstractScoreboardTeam2) ? true : abstractScoreboardTeam.isFriendlyFireAllowed();
		}
	}

	@Override
	protected void method_6105(float f) {
		this.inventory.method_7375(f);
	}

	@Override
	protected void method_6056(float f) {
		if (f >= 3.0F && this.activeItemStack.getItem() == Items.field_8255) {
			int i = 1 + MathHelper.floor(f);
			this.activeItemStack.applyDamage(i, this);
			if (this.activeItemStack.isEmpty()) {
				Hand hand = this.getActiveHand();
				if (hand == Hand.MAIN) {
					this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				} else {
					this.setEquippedStack(EquipmentSlot.HAND_OFF, ItemStack.EMPTY);
				}

				this.activeItemStack = ItemStack.EMPTY;
				this.playSound(SoundEvents.field_15239, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
			}
		}
	}

	public float getWornArmorRatio() {
		int i = 0;

		for (ItemStack itemStack : this.inventory.armor) {
			if (!itemStack.isEmpty()) {
				i++;
			}
		}

		return (float)i / (float)this.inventory.armor.size();
	}

	@Override
	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			f = this.method_6132(damageSource, f);
			f = this.method_6036(damageSource, f);
			float var8 = Math.max(f - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F) {
				this.method_7339(Stats.field_15365, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				this.addExhaustion(damageSource.getExhaustion());
				float i = this.getHealth();
				this.setHealth(this.getHealth() - var8);
				this.getDamageTracker().onDamage(damageSource, i, var8);
				if (var8 < 3.4028235E37F) {
					this.method_7339(Stats.field_15388, Math.round(var8 * 10.0F));
				}
			}
		}
	}

	public void openSignEditorGui(SignBlockEntity signBlockEntity) {
	}

	public void openCommandBlockMinecartGui(CommandBlockExecutor commandBlockExecutor) {
	}

	public void openCommandBlockGui(CommandBlockBlockEntity commandBlockBlockEntity) {
	}

	public void openStructureBlockGui(StructureBlockBlockEntity structureBlockBlockEntity) {
	}

	public void openJigsawGui(JigsawBlockEntity jigsawBlockEntity) {
	}

	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
	}

	public OptionalInt openContainer(@Nullable NameableContainerProvider nameableContainerProvider) {
		return OptionalInt.empty();
	}

	public void sendVillagerRecipes(int i, VillagerRecipeList villagerRecipeList) {
	}

	public void openBookEditorGui(ItemStack itemStack, Hand hand) {
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
				if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getAmount() < itemStack2.getAmount()) {
					itemStack.setAmount(itemStack2.getAmount());
				}

				return ActionResult.SUCCESS;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.interactWithEntity(this, (LivingEntity)entity, hand)) {
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

	public void attack(Entity entity) {
		if (entity.method_5732()) {
			if (!entity.method_5698(this)) {
				float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
				float g;
				if (entity instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
				} else {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
				}

				float h = this.method_7261(0.5F);
				f *= 0.2F + h * h * 0.8F;
				g *= h;
				this.method_7350();
				if (f > 0.0F || g > 0.0F) {
					boolean bl = h > 0.9F;
					boolean bl2 = false;
					int i = 0;
					i += EnchantmentHelper.getKnockback(this);
					if (this.isSprinting() && bl) {
						this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14999, this.getSoundCategory(), 1.0F, 1.0F);
						i++;
						bl2 = true;
					}

					boolean bl3 = bl
						&& this.fallDistance > 0.0F
						&& !this.onGround
						&& !this.canClimb()
						&& !this.isInsideWater()
						&& !this.hasPotionEffect(StatusEffects.field_5919)
						&& !this.hasVehicle()
						&& entity instanceof LivingEntity;
					bl3 = bl3 && !this.isSprinting();
					if (bl3) {
						f *= 1.5F;
					}

					f += g;
					boolean bl4 = false;
					double d = (double)(this.field_5973 - this.field_6039);
					if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.method_6029()) {
						ItemStack itemStack = this.getStackInHand(Hand.MAIN);
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

					double e = entity.velocityX;
					double l = entity.velocityY;
					double m = entity.velocityZ;
					boolean bl6 = entity.damage(DamageSource.player(this), f);
					if (bl6) {
						if (i > 0) {
							if (entity instanceof LivingEntity) {
								((LivingEntity)entity)
									.method_6005(
										this, (float)i * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
							} else {
								entity.addVelocity(
									(double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (float)i * 0.5F),
									0.1,
									(double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (float)i * 0.5F)
								);
							}

							this.velocityX *= 0.6;
							this.velocityZ *= 0.6;
							this.setSprinting(false);
						}

						if (bl4) {
							float n = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;

							for (LivingEntity livingEntity : this.world.getVisibleEntities(LivingEntity.class, entity.getBoundingBox().expand(1.0, 0.25, 1.0))) {
								if (livingEntity != this
									&& livingEntity != entity
									&& !this.isTeammate(livingEntity)
									&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
									&& this.squaredDistanceTo(livingEntity) < 9.0) {
									livingEntity.method_6005(
										this, 0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
									livingEntity.damage(DamageSource.player(this), n);
								}
							}

							this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14706, this.getSoundCategory(), 1.0F, 1.0F);
							this.method_7263();
						}

						if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
							((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							entity.velocityModified = false;
							entity.velocityX = e;
							entity.velocityY = l;
							entity.velocityZ = m;
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

						if (g > 0.0F) {
							this.addEnchantedHitParticles(entity);
						}

						this.method_6114(entity);
						if (entity instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
						}

						EnchantmentHelper.onTargetDamaged(this, entity);
						ItemStack itemStack2 = this.getMainHandStack();
						Entity entity2 = entity;
						if (entity instanceof EntityPart) {
							IEntityPartDamageDelegate iEntityPartDamageDelegate = ((EntityPart)entity).damageDelegate;
							if (iEntityPartDamageDelegate instanceof LivingEntity) {
								entity2 = (LivingEntity)iEntityPartDamageDelegate;
							}
						}

						if (!itemStack2.isEmpty() && entity2 instanceof LivingEntity) {
							itemStack2.onEntityDamaged((LivingEntity)entity2, this);
							if (itemStack2.isEmpty()) {
								this.setStackInHand(Hand.MAIN, ItemStack.EMPTY);
							}
						}

						if (entity instanceof LivingEntity) {
							float o = j - ((LivingEntity)entity).getHealth();
							this.method_7339(Stats.field_15399, Math.round(o * 10.0F));
							if (k > 0) {
								entity.setOnFireFor(k * 4);
							}

							if (this.world instanceof ServerWorld && o > 2.0F) {
								int p = (int)((double)o * 0.5);
								((ServerWorld)this.world)
									.method_14199(ParticleTypes.field_11209, entity.x, entity.y + (double)(entity.getHeight() * 0.5F), entity.z, p, 0.1, 0.0, 0.1, 0.2);
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

	@Override
	protected void method_5997(LivingEntity livingEntity) {
		this.attack(livingEntity);
	}

	public void method_7284(boolean bl) {
		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
		if (bl) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			this.getItemCooldownManager().set(Items.field_8255, 100);
			this.method_6021();
			this.world.summonParticle(this, (byte)30);
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
			((ServerWorld)this.world).method_14199(ParticleTypes.field_11227, this.x + d, this.y + (double)this.getHeight() * 0.5, this.z + e, 0, d, 0.0, e, 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public void requestRespawn() {
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.containerPlayer.close(this);
		if (this.container != null) {
			this.container.close(this);
		}
	}

	@Override
	public boolean isInsideWall() {
		return !this.sleeping && super.isInsideWall();
	}

	public boolean method_7340() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public PlayerEntity.SleepResult trySleep(BlockPos blockPos) {
		Direction direction = this.world.getBlockState(blockPos).get(HorizontalFacingBlock.field_11177);
		if (!this.world.isClient) {
			if (this.isSleeping() || !this.isValid()) {
				return PlayerEntity.SleepResult.INVALID_ATTEMPT;
			}

			if (!this.world.dimension.hasVisibleSky()) {
				return PlayerEntity.SleepResult.INVALID_WORLD;
			}

			if (this.world.isDaylight()) {
				return PlayerEntity.SleepResult.WRONG_TIME;
			}

			if (!this.isWithinSleepingRange(blockPos, direction)) {
				return PlayerEntity.SleepResult.TOO_FAR_AWAY;
			}

			if (!this.isCreative()) {
				double d = 8.0;
				double e = 5.0;
				List<HostileEntity> list = this.world
					.getEntities(
						HostileEntity.class,
						new BoundingBox(
							(double)blockPos.getX() - 8.0,
							(double)blockPos.getY() - 5.0,
							(double)blockPos.getZ() - 8.0,
							(double)blockPos.getX() + 8.0,
							(double)blockPos.getY() + 5.0,
							(double)blockPos.getZ() + 8.0
						),
						new PlayerEntity.class_1660(this)
					);
				if (!list.isEmpty()) {
					return PlayerEntity.SleepResult.NOT_SAFE;
				}
			}
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}

		this.method_7262();
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.setSize(0.2F, 0.2F);
		if (this.world.isBlockLoaded(blockPos)) {
			float f = 0.5F + (float)direction.getOffsetX() * 0.4F;
			float g = 0.5F + (float)direction.getOffsetZ() * 0.4F;
			this.method_7312(direction);
			this.setPosition((double)((float)blockPos.getX() + f), (double)((float)blockPos.getY() + 0.6875F), (double)((float)blockPos.getZ() + g));
		} else {
			this.setPosition((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.6875F), (double)((float)blockPos.getZ() + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.sleepingPos = blockPos;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		return PlayerEntity.SleepResult.SUCCESS;
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

	private void method_7312(Direction direction) {
		this.field_7516 = -1.8F * (float)direction.getOffsetX();
		this.field_7497 = -1.8F * (float)direction.getOffsetZ();
	}

	public void wakeUp(boolean bl, boolean bl2, boolean bl3) {
		this.setSize(0.6F, 1.8F);
		BlockState blockState = this.world.getBlockState(this.sleepingPos);
		if (this.sleepingPos != null && blockState.getBlock() instanceof BedBlock) {
			this.world.setBlockState(this.sleepingPos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), 4);
			BlockPos blockPos = BedBlock.method_9484(this.world, this.sleepingPos, 0);
			if (blockPos == null) {
				blockPos = this.sleepingPos.up();
			}

			this.setPosition((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.1F), (double)((float)blockPos.getZ() + 0.5F));
		}

		this.sleeping = false;
		if (this.world instanceof ServerWorld && bl2) {
			((ServerWorld)this.world).updatePlayersSleeping();
		}

		this.sleepTimer = bl ? 0 : 100;
		if (bl3) {
			this.setPlayerSpawn(this.sleepingPos, false);
		}
	}

	private boolean method_7275() {
		return this.world.getBlockState(this.sleepingPos).getBlock() instanceof BedBlock;
	}

	@Nullable
	public static BlockPos method_7288(BlockView blockView, BlockPos blockPos, boolean bl) {
		Block block = blockView.getBlockState(blockPos).getBlock();
		if (!(block instanceof BedBlock)) {
			if (!bl) {
				return null;
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = blockView.getBlockState(blockPos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? blockPos : null;
			}
		} else {
			return BedBlock.method_9484(blockView, blockPos, 0);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_7319() {
		if (this.sleepingPos != null) {
			Direction direction = this.world.getBlockState(this.sleepingPos).get(HorizontalFacingBlock.field_11177);
			switch (direction) {
				case SOUTH:
					return 90.0F;
				case WEST:
					return 0.0F;
				case NORTH:
					return 270.0F;
				case EAST:
					return 180.0F;
			}
		}

		return 0.0F;
	}

	@Override
	public boolean isSleeping() {
		return this.sleeping;
	}

	public boolean isSleepingLongEnough() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void addChatMessage(TextComponent textComponent, boolean bl) {
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

	public void increaseStat(Identifier identifier) {
		this.incrementStat(Stats.field_15419.getOrCreateStat(identifier));
	}

	public void method_7339(Identifier identifier, int i) {
		this.incrementStat(Stats.field_15419.getOrCreateStat(identifier), i);
	}

	public void incrementStat(Stat<?> stat) {
		this.incrementStat(stat, 1);
	}

	public void incrementStat(Stat<?> stat, int i) {
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
	public void method_6043() {
		super.method_6043();
		this.increaseStat(Stats.field_15428);
		if (this.isSprinting()) {
			this.addExhaustion(0.2F);
		} else {
			this.addExhaustion(0.05F);
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		double d = this.x;
		double e = this.y;
		double i = this.z;
		if (this.isSwimming() && !this.hasVehicle()) {
			double j = this.method_5720().y;
			double k = j < -0.2 ? 0.085 : 0.06;
			if (j <= 0.0 || this.field_6282 || !this.world.getBlockState(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).getFluidState().isEmpty()) {
				this.velocityY = this.velocityY + (j - this.velocityY) * k;
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double j = this.velocityY;
			float l = this.field_6281;
			this.field_6281 = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.method_6091(f, g, h);
			this.velocityY = j * 0.6;
			this.field_6281 = l;
			this.fallDistance = 0.0F;
			this.setEntityFlag(7, false);
		} else {
			super.method_6091(f, g, h);
		}

		this.method_7282(this.x - d, this.y - e, this.z - i);
	}

	@Override
	public void method_5790() {
		if (this.abilities.flying) {
			this.method_5796(false);
		} else {
			super.method_5790();
		}
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_7352(BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return this.method_7326(blockPos) && !this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2);
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_7326(BlockPos blockPos) {
		return !this.world.getBlockState(blockPos).isSimpleFullBlock(this.world, blockPos);
	}

	@Override
	public float method_6029() {
		return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
	}

	public void method_7282(double d, double e, double f) {
		if (!this.hasVehicle()) {
			if (this.isSwimming()) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(Stats.field_15423, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.method_5744(FluidTags.field_15517, true)) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(Stats.field_15401, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isInsideWater()) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 0) {
					this.method_7339(Stats.field_15394, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.canClimb()) {
				if (e > 0.0) {
					this.method_7339(Stats.field_15413, (int)Math.round(e * 100.0));
				}
			} else if (this.onGround) {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 0) {
					if (this.isSprinting()) {
						this.method_7339(Stats.field_15364, i);
						this.addExhaustion(0.1F * (float)i * 0.01F);
					} else if (this.isSneaking()) {
						this.method_7339(Stats.field_15376, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					} else {
						this.method_7339(Stats.field_15377, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					}
				}
			} else if (this.isFallFlying()) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				this.method_7339(Stats.field_15374, i);
			} else {
				int i = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (i > 25) {
					this.method_7339(Stats.field_15426, i);
				}
			}
		}
	}

	private void method_7260(double d, double e, double f) {
		if (this.hasVehicle()) {
			int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
			if (i > 0) {
				if (this.getRiddenEntity() instanceof AbstractMinecartEntity) {
					this.method_7339(Stats.field_15409, i);
				} else if (this.getRiddenEntity() instanceof BoatEntity) {
					this.method_7339(Stats.field_15415, i);
				} else if (this.getRiddenEntity() instanceof PigEntity) {
					this.method_7339(Stats.field_15387, i);
				} else if (this.getRiddenEntity() instanceof HorseBaseEntity) {
					this.method_7339(Stats.field_15396, i);
				}
			}
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (!this.abilities.allowFlying) {
			if (f >= 2.0F) {
				this.method_7339(Stats.field_15386, (int)Math.round((double)f * 100.0));
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
	public void method_5874(LivingEntity livingEntity) {
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
		this.experienceBarProgress = this.experienceBarProgress + (float)i / (float)this.method_7349();
		this.experienceLevel = MathHelper.clamp(this.experienceLevel + i, 0, Integer.MAX_VALUE);

		while (this.experienceBarProgress < 0.0F) {
			float f = this.experienceBarProgress * (float)this.method_7349();
			if (this.experience > 0) {
				this.method_7316(-1);
				this.experienceBarProgress = 1.0F + f / (float)this.method_7349();
			} else {
				this.method_7316(-1);
				this.experienceBarProgress = 0.0F;
			}
		}

		while (this.experienceBarProgress >= 1.0F) {
			this.experienceBarProgress = (this.experienceBarProgress - 1.0F) * (float)this.method_7349();
			this.method_7316(1);
			this.experienceBarProgress = this.experienceBarProgress / (float)this.method_7349();
		}
	}

	public int getEnchantmentTableSeed() {
		return this.enchantmentTableSeed;
	}

	public void method_7286(ItemStack itemStack, int i) {
		this.experience -= i;
		if (this.experience < 0) {
			this.experience = 0;
			this.experienceBarProgress = 0.0F;
			this.experienceLevel = 0;
		}

		this.enchantmentTableSeed = this.random.nextInt();
	}

	public void method_7316(int i) {
		this.experience += i;
		if (this.experience < 0) {
			this.experience = 0;
			this.experienceBarProgress = 0.0F;
			this.experienceLevel = 0;
		}

		if (i > 0 && this.experience % 5 == 0 && (float)this.field_7508 < (float)this.age - 100.0F) {
			float f = this.experience > 30 ? 1.0F : (float)this.experience / 30.0F;
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14709, this.getSoundCategory(), f * 0.75F, 1.0F);
			this.field_7508 = this.age;
		}
	}

	public int method_7349() {
		if (this.experience >= 30) {
			return 112 + (this.experience - 30) * 9;
		} else {
			return this.experience >= 15 ? 37 + (this.experience - 15) * 5 : 7 + this.experience * 2;
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

	public boolean canPlaceBlock(BlockPos blockPos, Direction direction, ItemStack itemStack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
			CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos2, false);
			return itemStack.getCustomCanPlace(this.world.getTagManager(), cachedBlockPosition);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
			int i = this.experience * 7;
			return i > 100 ? 100 : i;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean method_6071() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderName() {
		return true;
	}

	@Override
	protected boolean method_5658() {
		return !this.abilities.flying;
	}

	public void method_7355() {
	}

	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public TextComponent getName() {
		return new StringTextComponent(this.gameProfile.getName());
	}

	public EnderChestInventory getEnderChestInventory() {
		return this.enderChestInventory;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			return this.inventory.getMainHandStack();
		} else if (equipmentSlot == EquipmentSlot.HAND_OFF) {
			return this.inventory.offHand.get(0);
		} else {
			return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.armor.get(equipmentSlot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			this.onEquipStack(itemStack);
			this.inventory.main.set(this.inventory.selectedSlot, itemStack);
		} else if (equipmentSlot == EquipmentSlot.HAND_OFF) {
			this.onEquipStack(itemStack);
			this.inventory.offHand.set(0, itemStack);
		} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
			this.onEquipStack(itemStack);
			this.inventory.armor.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	public boolean method_7270(ItemStack itemStack) {
		this.onEquipStack(itemStack);
		return this.inventory.insertStack(itemStack);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return Lists.<ItemStack>newArrayList(this.getMainHandStack(), this.getOffHandStack());
	}

	@Override
	public Iterable<ItemStack> getItemsArmor() {
		return this.inventory.armor;
	}

	public boolean method_7298(CompoundTag compoundTag) {
		if (this.hasVehicle() || !this.onGround || this.isInsideWater()) {
			return false;
		} else if (this.getShoulderEntityLeft().isEmpty()) {
			this.setShoulderEntityLeft(compoundTag);
			return true;
		} else if (this.getShoulderEntityRight().isEmpty()) {
			this.setShoulderEntityRight(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	protected void method_7262() {
		this.method_7296(this.getShoulderEntityLeft());
		this.setShoulderEntityLeft(new CompoundTag());
		this.method_7296(this.getShoulderEntityRight());
		this.setShoulderEntityRight(new CompoundTag());
	}

	private void method_7296(@Nullable CompoundTag compoundTag) {
		if (!this.world.isClient && !compoundTag.isEmpty()) {
			EntityType.getEntityFromTag(compoundTag, this.world).ifPresent(entity -> {
				if (entity instanceof TameableEntity) {
					((TameableEntity)entity).setOwnerUuid(this.uuid);
				}

				entity.setPosition(this.x, this.y + 0.7F, this.z);
				((ServerWorld)this.world).method_18197(entity, true);
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
			AbstractScoreboardTeam abstractScoreboardTeam = this.getScoreboardTeam();
			return abstractScoreboardTeam == null
				|| playerEntity == null
				|| playerEntity.getScoreboardTeam() != abstractScoreboardTeam
				|| !abstractScoreboardTeam.shouldShowFriendlyInvisibles();
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
	public TextComponent getDisplayName() {
		TextComponent textComponent = ScoreboardTeam.method_1142(this.getScoreboardTeam(), this.getName());
		return this.method_7299(textComponent);
	}

	public TextComponent method_7306() {
		return new StringTextComponent("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
	}

	private TextComponent method_7299(TextComponent textComponent) {
		String string = this.getGameProfile().getName();
		return textComponent.modifyStyle(
			style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " "))
					.setHoverEvent(this.getComponentHoverEvent())
					.setInsertion(string)
		);
	}

	@Override
	public String getEntityName() {
		return this.getGameProfile().getName();
	}

	@Override
	public float getEyeHeight() {
		float f = 1.62F;
		if (this.isSleeping()) {
			f = 0.2F;
		} else if (this.isSwimming() || this.isFallFlying() || this.getHeight() == 0.6F) {
			f = 0.4F;
		} else if (this.isSneaking() || this.getHeight() == 1.65F) {
			f -= 0.08F;
		}

		return f;
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
	public boolean method_5758(int i, ItemStack itemStack) {
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
				this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
				return true;
			} else if (i == 99) {
				this.setEquippedStack(EquipmentSlot.HAND_OFF, itemStack);
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
	public OptionMainHand getMainHand() {
		return this.dataTracker.get(MAIN_HAND) == 0 ? OptionMainHand.field_6182 : OptionMainHand.field_6183;
	}

	public void setMainHand(OptionMainHand optionMainHand) {
		this.dataTracker.set(MAIN_HAND, (byte)(optionMainHand == OptionMainHand.field_6182 ? 0 : 1));
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

	public float method_7279() {
		return (float)(1.0 / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0);
	}

	public float method_7261(float f) {
		return MathHelper.clamp(((float)this.field_6273 + f) / this.method_7279(), 0.0F, 1.0F);
	}

	public void method_7350() {
		this.field_6273 = 0;
	}

	public ItemCooldownManager getItemCooldownManager() {
		return this.itemCooldownManager;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	public float getLuck() {
		return (float)this.getAttributeInstance(EntityAttributes.LUCK).getValue();
	}

	public boolean method_7338() {
		return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
	}

	@Override
	protected void method_6020(StatusEffectInstance statusEffectInstance) {
		super.method_6020(statusEffectInstance);
		if (!this.world.isClient && this.world.getRaidManager() != null && statusEffectInstance.getEffectType() == StatusEffects.field_16595) {
			this.world.getRaidManager().addTimestamp(this);
		}
	}

	@Override
	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		super.method_6129(statusEffectInstance);
		if (!this.world.isClient && this.world.getRaidManager() != null && statusEffectInstance.getEffectType() == StatusEffects.field_16595) {
			this.world.getRaidManager().removeTimestamp(this);
		}
	}

	public static enum ChatVisibility {
		FULL(0, "options.chat.visibility.full"),
		COMMANDS(1, "options.chat.visibility.system"),
		HIDDEN(2, "options.chat.visibility.hidden");

		private static final PlayerEntity.ChatVisibility[] field_7534 = (PlayerEntity.ChatVisibility[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(PlayerEntity.ChatVisibility::getId))
			.toArray(PlayerEntity.ChatVisibility[]::new);
		private final int id;
		private final String key;

		private ChatVisibility(int j, String string2) {
			this.id = j;
			this.key = string2;
		}

		public int getId() {
			return this.id;
		}

		@Environment(EnvType.CLIENT)
		public static PlayerEntity.ChatVisibility byId(int i) {
			return field_7534[i % field_7534.length];
		}

		@Environment(EnvType.CLIENT)
		public String getTranslationKey() {
			return this.key;
		}
	}

	public static enum SleepResult {
		SUCCESS,
		INVALID_WORLD,
		WRONG_TIME,
		TOO_FAR_AWAY,
		INVALID_ATTEMPT,
		NOT_SAFE;
	}

	static class class_1660 implements Predicate<HostileEntity> {
		private final PlayerEntity field_7541;

		private class_1660(PlayerEntity playerEntity) {
			this.field_7541 = playerEntity;
		}

		public boolean method_7363(@Nullable HostileEntity hostileEntity) {
			return hostileEntity.method_7076(this.field_7541);
		}
	}
}
