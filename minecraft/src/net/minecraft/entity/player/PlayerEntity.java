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
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BaseBowItem;
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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Void;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public abstract class PlayerEntity extends LivingEntity {
	public static final EntitySize STANDING_SIZE = EntitySize.resizeable(0.6F, 1.8F);
	private static final Map<EntityPose, EntitySize> SIZES = ImmutableMap.<EntityPose, EntitySize>builder()
		.put(EntityPose.field_18076, STANDING_SIZE)
		.put(EntityPose.field_18078, SLEEPING_SIZE)
		.put(EntityPose.field_18077, EntitySize.resizeable(0.6F, 0.6F))
		.put(EntityPose.field_18079, EntitySize.resizeable(0.6F, 0.6F))
		.put(EntityPose.field_18080, EntitySize.resizeable(0.6F, 0.6F))
		.put(EntityPose.field_18081, EntitySize.resizeable(0.6F, 1.65F))
		.put(EntityPose.field_18082, EntitySize.constant(0.2F, 0.2F))
		.build();
	private static final TrackedData<Float> field_7491 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_7511 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> field_7518 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> field_7488 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<CompoundTag> field_7496 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	protected static final TrackedData<CompoundTag> field_7506 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	public final PlayerInventory inventory = new PlayerInventory(this);
	protected EnderChestInventory field_7486 = new EnderChestInventory();
	public final PlayerContainer field_7498;
	public Container field_7512;
	protected HungerManager field_7493 = new HungerManager();
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
	private int sleepTimer;
	private boolean field_7517;
	protected boolean isInWater;
	private BlockPos field_7501;
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
	private final ItemCooldownManager field_7484 = this.method_7265();
	@Nullable
	public FishHookEntity fishHook;

	public PlayerEntity(World world, GameProfile gameProfile) {
		super(EntityType.PLAYER, world);
		this.setUuid(getUuidFromProfile(gameProfile));
		this.gameProfile = gameProfile;
		this.field_7498 = new PlayerContainer(this.inventory, !world.isClient, this);
		this.field_7512 = this.field_7498;
		BlockPos blockPos = world.method_8395();
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6215 = 180.0F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_6127().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1F);
		this.method_6127().register(EntityAttributes.ATTACK_SPEED);
		this.method_6127().register(EntityAttributes.LUCK);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7491, 0.0F);
		this.field_6011.startTracking(field_7511, 0);
		this.field_6011.startTracking(field_7518, (byte)0);
		this.field_6011.startTracking(field_7488, (byte)1);
		this.field_6011.startTracking(field_7496, new CompoundTag());
		this.field_6011.startTracking(field_7506, new CompoundTag());
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

			if (!this.field_6002.isClient && this.field_6002.isDaylight()) {
				this.wakeUp(false, true, true);
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		this.updateBubbleColumn();
		this.updateInWater();
		super.update();
		if (!this.field_6002.isClient && this.field_7512 != null && !this.field_7512.canUse(this)) {
			this.closeGui();
			this.field_7512 = this.field_7498;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.method_7313();
		if (!this.field_6002.isClient) {
			this.field_7493.update(this);
			this.method_7281(Stats.field_15417);
			if (this.isValid()) {
				this.method_7281(Stats.field_15400);
			}

			if (this.isSneaking()) {
				this.method_7281(Stats.field_15422);
			}

			if (!this.isSleeping()) {
				this.method_7281(Stats.field_15429);
			}
		}

		int i = 29999999;
		double d = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
		double e = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
		if (d != this.x || e != this.z) {
			this.setPosition(d, this.y, e);
		}

		this.field_6273++;
		ItemStack itemStack = this.method_6047();
		if (!ItemStack.areEqual(this.field_7525, itemStack)) {
			if (!ItemStack.areEqualIgnoreDurability(this.field_7525, itemStack)) {
				this.method_7350();
			}

			this.field_7525 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
		}

		this.updateTurtleHelmet();
		this.field_7484.update();
		this.updateSize();
	}

	protected boolean updateInWater() {
		this.isInWater = this.method_5744(FluidTags.field_15517, true);
		return this.isInWater;
	}

	private void updateTurtleHelmet() {
		ItemStack itemStack = this.method_6118(EquipmentSlot.HEAD);
		if (itemStack.getItem() == Items.field_8090 && !this.method_5777(FluidTags.field_15517)) {
			this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5923, 200, 0, false, false, true));
		}
	}

	protected ItemCooldownManager method_7265() {
		return new ItemCooldownManager();
	}

	private void updateBubbleColumn() {
		BlockState blockState = this.field_6002.method_8475(this.method_5829().expand(0.0, -0.4F, 0.0).contract(0.001), Blocks.field_10422);
		if (blockState != null) {
			if (!this.field_7517 && !this.field_5953 && blockState.getBlock() == Blocks.field_10422 && !this.isSpectator()) {
				boolean bl = (Boolean)blockState.method_11654(BubbleColumnBlock.field_10680);
				if (bl) {
					this.field_6002.method_8486(this.x, this.y, this.z, SoundEvents.field_14752, this.method_5634(), 1.0F, 1.0F, false);
				} else {
					this.field_6002.method_8486(this.x, this.y, this.z, SoundEvents.field_14570, this.method_5634(), 1.0F, 1.0F, false);
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
		EntityPose entityPose;
		if (this.isFallFlying()) {
			entityPose = EntityPose.field_18077;
		} else if (this.isSleeping()) {
			entityPose = EntityPose.field_18078;
		} else if (this.isSwimming()) {
			entityPose = EntityPose.field_18079;
		} else if (this.isUsingRiptide()) {
			entityPose = EntityPose.field_18080;
		} else if (this.isSneaking()) {
			entityPose = EntityPose.field_18081;
		} else {
			entityPose = EntityPose.field_18076;
		}

		EntityPose entityPose2 = this.method_18376();
		if (entityPose2 != entityPose) {
			EntitySize entitySize = this.method_18377(entityPose);
			BoundingBox boundingBox = this.method_5829();
			boundingBox = new BoundingBox(
				boundingBox.minX,
				boundingBox.minY,
				boundingBox.minZ,
				boundingBox.minX + (double)entitySize.width,
				boundingBox.minY + (double)entitySize.height,
				boundingBox.minZ + (double)entitySize.width
			);
			if (this.field_6002.method_8587(this, boundingBox)) {
				this.method_18380(entityPose);
			}
		}
	}

	@Override
	public int getMaxPortalTime() {
		return this.abilities.invulnerable ? 1 : 80;
	}

	@Override
	protected SoundEvent method_5737() {
		return SoundEvents.field_14998;
	}

	@Override
	protected SoundEvent method_5625() {
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
	public void method_5783(SoundEvent soundEvent, float f, float g) {
		this.field_6002.method_8465(this, this.x, this.y, this.z, soundEvent, this.method_5634(), f, g);
	}

	public void method_17356(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
	}

	@Override
	public SoundCategory method_5634() {
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
			this.field_6002
				.method_8406(
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

	protected void closeGui() {
		this.field_7512 = this.field_7498;
	}

	@Override
	public void updateRiding() {
		if (!this.field_6002.isClient && this.isSneaking() && this.hasVehicle()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.yaw;
			float h = this.pitch;
			super.updateRiding();
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
		this.method_18380(EntityPose.field_18076);
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

		if (this.field_6002.getDifficulty() == Difficulty.PEACEFUL && this.field_6002.getGameRules().getBoolean("naturalRegeneration")) {
			if (this.getHealth() < this.getHealthMaximum() && this.age % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.field_7493.isNotFull() && this.age % 10 == 0) {
				this.field_7493.setFoodLevel(this.field_7493.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.field_7505 = this.field_7483;
		super.updateMovement();
		EntityAttributeInstance entityAttributeInstance = this.method_5996(EntityAttributes.MOVEMENT_SPEED);
		if (!this.field_6002.isClient) {
			entityAttributeInstance.setBaseValue((double)this.abilities.getWalkSpeed());
		}

		this.field_6281 = 0.02F;
		if (this.isSprinting()) {
			this.field_6281 = (float)((double)this.field_6281 + 0.005999999865889549);
		}

		this.setMovementSpeed((float)entityAttributeInstance.getValue());
		float f;
		if (this.onGround && !(this.getHealth() <= 0.0F) && !this.isSwimming()) {
			f = Math.min(0.1F, MathHelper.sqrt(method_17996(this.method_18798())));
		} else {
			f = 0.0F;
		}

		float g;
		if (!this.onGround && !(this.getHealth() <= 0.0F)) {
			g = (float)(Math.atan(-this.method_18798().y * 0.2F) * 15.0);
		} else {
			g = 0.0F;
		}

		this.field_7483 = this.field_7483 + (f - this.field_7483) * 0.4F;
		this.field_6223 = this.field_6223 + (g - this.field_6223) * 0.8F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			BoundingBox boundingBox;
			if (this.hasVehicle() && !this.getRiddenEntity().invalid) {
				boundingBox = this.method_5829().union(this.getRiddenEntity().method_5829()).expand(1.0, 0.0, 1.0);
			} else {
				boundingBox = this.method_5829().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.field_6002.method_8335(this, boundingBox);

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (!entity.invalid) {
					this.collideWithEntity(entity);
				}
			}
		}

		this.method_7267(this.method_7356());
		this.method_7267(this.method_7308());
		if (!this.field_6002.isClient && (this.fallDistance > 0.5F || this.isInsideWater() || this.hasVehicle()) || this.abilities.flying) {
			this.dropShoulderEntities();
		}
	}

	private void method_7267(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && !compoundTag.containsKey("Silent") || !compoundTag.getBoolean("Silent")) {
			String string = compoundTag.getString("id");
			EntityType.get(string).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> ParrotEntity.method_6589(this.field_6002, this));
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.method_5694(this);
	}

	public int getScore() {
		return this.field_6011.get(field_7511);
	}

	public void setScore(int i) {
		this.field_6011.set(field_7511, i);
	}

	public void addScore(int i) {
		int j = this.getScore();
		this.field_6011.set(field_7511, j + i);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		this.setPosition(this.x, this.y, this.z);
		if ("Notch".equals(this.method_5477().getString())) {
			this.method_7329(new ItemStack(Items.field_8279), true, false);
		}

		if (!this.isSpectator()) {
			this.method_16080(damageSource);
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

		this.method_7281(Stats.field_15421);
		this.method_7266(Stats.field_15419.getOrCreateStat(Stats.field_15400));
		this.method_7266(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.extinguish();
		this.setEntityFlag(0, false);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (!this.field_6002.getGameRules().getBoolean("keepInventory")) {
			this.vanishCursedItems();
			this.inventory.dropAll();
		}
	}

	protected void vanishCursedItems() {
		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.method_5438(i);
			if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
				this.inventory.method_5441(i);
			}
		}
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		if (damageSource == DamageSource.ON_FIRE) {
			return SoundEvents.field_14623;
		} else if (damageSource == DamageSource.DROWN) {
			return SoundEvents.field_15205;
		} else {
			return damageSource == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.field_17614 : SoundEvents.field_15115;
		}
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14904;
	}

	@Nullable
	public ItemEntity dropSelectedItem(boolean bl) {
		return this.method_7329(
			this.inventory.method_5434(this.inventory.selectedSlot, bl && !this.inventory.method_7391().isEmpty() ? this.inventory.method_7391().getAmount() : 1),
			false,
			true
		);
	}

	@Nullable
	public ItemEntity method_7328(ItemStack itemStack, boolean bl) {
		return this.method_7329(itemStack, false, bl);
	}

	@Nullable
	public ItemEntity method_7329(ItemStack itemStack, boolean bl, boolean bl2) {
		if (itemStack.isEmpty()) {
			return null;
		} else {
			double d = this.y - 0.3F + (double)this.getStandingEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, d, this.z, itemStack);
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

	public float method_7351(BlockState blockState) {
		float f = this.inventory.method_7370(blockState);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEfficiency(this);
			ItemStack itemStack = this.method_6047();
			if (i > 0 && !itemStack.isEmpty()) {
				f += (float)(i * i + 1);
			}
		}

		if (StatusEffectUtil.method_5576(this)) {
			f *= 1.0F + (float)(StatusEffectUtil.method_5575(this) + 1) * 0.2F;
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

	public boolean method_7305(BlockState blockState) {
		return blockState.method_11620().canBreakByHand() || this.inventory.method_7383(blockState);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setUuid(getUuidFromProfile(this.gameProfile));
		ListTag listTag = compoundTag.method_10554("Inventory", 10);
		this.inventory.method_7397(listTag);
		this.inventory.selectedSlot = compoundTag.getInt("SelectedItemSlot");
		this.sleepTimer = compoundTag.getShort("SleepTimer");
		this.experienceBarProgress = compoundTag.getFloat("XpP");
		this.experience = compoundTag.getInt("XpLevel");
		this.experienceLevel = compoundTag.getInt("XpTotal");
		this.enchantmentTableSeed = compoundTag.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(compoundTag.getInt("Score"));
		if (compoundTag.containsKey("SpawnX", 99) && compoundTag.containsKey("SpawnY", 99) && compoundTag.containsKey("SpawnZ", 99)) {
			this.field_7501 = new BlockPos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
			this.spawnForced = compoundTag.getBoolean("SpawnForced");
		}

		this.field_7493.method_7584(compoundTag);
		this.abilities.method_7249(compoundTag);
		if (compoundTag.containsKey("EnderItems", 9)) {
			this.field_7486.method_7659(compoundTag.method_10554("EnderItems", 10));
		}

		if (compoundTag.containsKey("ShoulderEntityLeft", 10)) {
			this.method_7273(compoundTag.getCompound("ShoulderEntityLeft"));
		}

		if (compoundTag.containsKey("ShoulderEntityRight", 10)) {
			this.method_7345(compoundTag.getCompound("ShoulderEntityRight"));
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.method_10566("Inventory", this.inventory.method_7384(new ListTag()));
		compoundTag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		compoundTag.putShort("SleepTimer", (short)this.sleepTimer);
		compoundTag.putFloat("XpP", this.experienceBarProgress);
		compoundTag.putInt("XpLevel", this.experience);
		compoundTag.putInt("XpTotal", this.experienceLevel);
		compoundTag.putInt("XpSeed", this.enchantmentTableSeed);
		compoundTag.putInt("Score", this.getScore());
		if (this.field_7501 != null) {
			compoundTag.putInt("SpawnX", this.field_7501.getX());
			compoundTag.putInt("SpawnY", this.field_7501.getY());
			compoundTag.putInt("SpawnZ", this.field_7501.getZ());
			compoundTag.putBoolean("SpawnForced", this.spawnForced);
		}

		this.field_7493.method_7582(compoundTag);
		this.abilities.method_7251(compoundTag);
		compoundTag.method_10566("EnderItems", this.field_7486.method_7660());
		if (!this.method_7356().isEmpty()) {
			compoundTag.method_10566("ShoulderEntityLeft", this.method_7356());
		}

		if (!this.method_7308().isEmpty()) {
			compoundTag.method_10566("ShoulderEntityRight", this.method_7308());
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
					if (this.field_6002.getDifficulty() == Difficulty.PEACEFUL) {
						f = 0.0F;
					}

					if (this.field_6002.getDifficulty() == Difficulty.EASY) {
						f = Math.min(f / 2.0F + 1.0F, f);
					}

					if (this.field_6002.getDifficulty() == Difficulty.HARD) {
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
		if (livingEntity.method_6047().getItem() instanceof AxeItem) {
			this.method_7284(true);
		}
	}

	public boolean shouldDamagePlayer(PlayerEntity playerEntity) {
		AbstractScoreboardTeam abstractScoreboardTeam = this.method_5781();
		AbstractScoreboardTeam abstractScoreboardTeam2 = playerEntity.method_5781();
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
	protected void damageShield(float f) {
		if (f >= 3.0F && this.field_6277.getItem() == Items.field_8255) {
			int i = 1 + MathHelper.floor(f);
			this.field_6277.applyDamage(i, this);
			if (this.field_6277.isEmpty()) {
				Hand hand = this.getActiveHand();
				if (hand == Hand.MAIN) {
					this.method_5673(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				} else {
					this.method_5673(EquipmentSlot.HAND_OFF, ItemStack.EMPTY);
				}

				this.field_6277 = ItemStack.EMPTY;
				this.method_5783(SoundEvents.field_15239, 0.8F, 0.8F + this.field_6002.random.nextFloat() * 0.4F);
			}
		}
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
				this.getDamageTracker().method_5547(damageSource, i, var8);
				if (var8 < 3.4028235E37F) {
					this.method_7339(Stats.field_15388, Math.round(var8 * 10.0F));
				}
			}
		}
	}

	public void method_7311(SignBlockEntity signBlockEntity) {
	}

	public void method_7257(CommandBlockExecutor commandBlockExecutor) {
	}

	public void method_7323(CommandBlockBlockEntity commandBlockBlockEntity) {
	}

	public void method_7303(StructureBlockBlockEntity structureBlockBlockEntity) {
	}

	public void method_16354(JigsawBlockEntity jigsawBlockEntity) {
	}

	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
	}

	public OptionalInt openContainer(@Nullable NameableContainerProvider nameableContainerProvider) {
		return OptionalInt.empty();
	}

	public void method_17354(int i, TraderRecipeList traderRecipeList, int j, int k, boolean bl) {
	}

	public void method_7315(ItemStack itemStack, Hand hand) {
	}

	public ActionResult interact(Entity entity, Hand hand) {
		if (this.isSpectator()) {
			if (entity instanceof NameableContainerProvider) {
				this.openContainer((NameableContainerProvider)entity);
			}

			return ActionResult.PASS;
		} else {
			ItemStack itemStack = this.method_5998(hand);
			ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
			if (entity.method_5688(this, hand)) {
				if (this.abilities.creativeMode && itemStack == this.method_5998(hand) && itemStack.getAmount() < itemStack2.getAmount()) {
					itemStack.setAmount(itemStack2.getAmount());
				}

				return ActionResult.field_5812;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.interactWithEntity(this, (LivingEntity)entity, hand)) {
						if (itemStack.isEmpty() && !this.abilities.creativeMode) {
							this.method_6122(hand, ItemStack.EMPTY);
						}

						return ActionResult.field_5812;
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
	protected boolean method_6062() {
		return super.method_6062() || this.isSleeping();
	}

	public void attack(Entity entity) {
		if (entity.method_5732()) {
			if (!entity.method_5698(this)) {
				float f = (float)this.method_5996(EntityAttributes.ATTACK_DAMAGE).getValue();
				float g;
				if (entity instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.method_6047(), ((LivingEntity)entity).method_6046());
				} else {
					g = EnchantmentHelper.getAttackDamage(this.method_6047(), EntityGroup.DEFAULT);
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
						this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14999, this.method_5634(), 1.0F, 1.0F);
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
					if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed()) {
						ItemStack itemStack = this.method_5998(Hand.MAIN);
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

					Vec3d vec3d = entity.method_18798();
					boolean bl6 = entity.damage(DamageSource.method_5532(this), f);
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

							this.method_18799(this.method_18798().multiply(0.6, 1.0, 0.6));
							this.setSprinting(false);
						}

						if (bl4) {
							float l = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;

							for (LivingEntity livingEntity : this.field_6002.method_18467(LivingEntity.class, entity.method_5829().expand(1.0, 0.25, 1.0))) {
								if (livingEntity != this
									&& livingEntity != entity
									&& !this.isTeammate(livingEntity)
									&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
									&& this.squaredDistanceTo(livingEntity) < 9.0) {
									livingEntity.method_6005(
										this, 0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
									livingEntity.damage(DamageSource.method_5532(this), l);
								}
							}

							this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14706, this.method_5634(), 1.0F, 1.0F);
							this.method_7263();
						}

						if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
							((ServerPlayerEntity)entity).field_13987.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							entity.velocityModified = false;
							entity.method_18799(vec3d);
						}

						if (bl3) {
							this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_15016, this.method_5634(), 1.0F, 1.0F);
							this.addCritParticles(entity);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14840, this.method_5634(), 1.0F, 1.0F);
							} else {
								this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14625, this.method_5634(), 1.0F, 1.0F);
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
						ItemStack itemStack2 = this.method_6047();
						Entity entity2 = entity;
						if (entity instanceof EnderDragonPart) {
							entity2 = ((EnderDragonPart)entity).field_7007;
						}

						if (!itemStack2.isEmpty() && entity2 instanceof LivingEntity) {
							itemStack2.onEntityDamaged((LivingEntity)entity2, this);
							if (itemStack2.isEmpty()) {
								this.method_6122(Hand.MAIN, ItemStack.EMPTY);
							}
						}

						if (entity instanceof LivingEntity) {
							float m = j - ((LivingEntity)entity).getHealth();
							this.method_7339(Stats.field_15399, Math.round(m * 10.0F));
							if (k > 0) {
								entity.setOnFireFor(k * 4);
							}

							if (this.field_6002 instanceof ServerWorld && m > 2.0F) {
								int n = (int)((double)m * 0.5);
								((ServerWorld)this.field_6002)
									.method_14199(ParticleTypes.field_11209, entity.x, entity.y + (double)(entity.getHeight() * 0.5F), entity.z, n, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.addExhaustion(0.1F);
					} else {
						this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14914, this.method_5634(), 1.0F, 1.0F);
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

	public void method_7284(boolean bl) {
		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
		if (bl) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			this.method_7357().set(Items.field_8255, 100);
			this.method_6021();
			this.field_6002.summonParticle(this, (byte)30);
		}
	}

	public void addCritParticles(Entity entity) {
	}

	public void addEnchantedHitParticles(Entity entity) {
	}

	public void method_7263() {
		double d = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)));
		double e = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
		if (this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002).method_14199(ParticleTypes.field_11227, this.x + d, this.y + (double)this.getHeight() * 0.5, this.z + e, 0, d, 0.0, e, 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	public void requestRespawn() {
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.field_7498.close(this);
		if (this.field_7512 != null) {
			this.field_7512.close(this);
		}
	}

	public boolean method_7340() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public Either<PlayerEntity.SleepResult, Void> method_7269(BlockPos blockPos) {
		Direction direction = this.field_6002.method_8320(blockPos).method_11654(HorizontalFacingBlock.field_11177);
		if (!this.field_6002.isClient) {
			if (this.isSleeping() || !this.isValid()) {
				return Either.left(PlayerEntity.SleepResult.INVALID_ATTEMPT);
			}

			if (!this.field_6002.field_9247.hasVisibleSky()) {
				return Either.left(PlayerEntity.SleepResult.INVALID_WORLD);
			}

			if (this.field_6002.isDaylight()) {
				return Either.left(PlayerEntity.SleepResult.WRONG_TIME);
			}

			if (!this.method_7264(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
			}

			if (this.method_19205(blockPos, direction)) {
				return Either.left(PlayerEntity.SleepResult.field_18592);
			}

			if (!this.isCreative()) {
				double d = 8.0;
				double e = 5.0;
				List<HostileEntity> list = this.field_6002
					.method_8390(
						HostileEntity.class,
						new BoundingBox(
							(double)blockPos.getX() - 8.0,
							(double)blockPos.getY() - 5.0,
							(double)blockPos.getZ() - 8.0,
							(double)blockPos.getX() + 8.0,
							(double)blockPos.getY() + 5.0,
							(double)blockPos.getZ() + 8.0
						),
						hostileEntity -> hostileEntity.method_7076(this)
					);
				if (!list.isEmpty()) {
					return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
				}
			}
		}

		this.method_18403(blockPos);
		this.sleepTimer = 0;
		if (this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002).updatePlayersSleeping();
		}

		return Either.right(Void.INSTANCE);
	}

	@Override
	public void method_18403(BlockPos blockPos) {
		this.method_7266(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.dropShoulderEntities();
		super.method_18403(blockPos);
	}

	private boolean method_7264(BlockPos blockPos, Direction direction) {
		if (Math.abs(this.x - (double)blockPos.getX()) <= 3.0
			&& Math.abs(this.y - (double)blockPos.getY()) <= 2.0
			&& Math.abs(this.z - (double)blockPos.getZ()) <= 3.0) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
			return Math.abs(this.x - (double)blockPos2.getX()) <= 3.0
				&& Math.abs(this.y - (double)blockPos2.getY()) <= 2.0
				&& Math.abs(this.z - (double)blockPos2.getZ()) <= 3.0;
		}
	}

	private boolean method_19205(BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.up();
		return !this.method_7326(blockPos2) || !this.method_7326(blockPos2.method_10093(direction.getOpposite()));
	}

	public void wakeUp(boolean bl, boolean bl2, boolean bl3) {
		Optional<BlockPos> optional = this.getSleepingPosition();
		super.wakeUp();
		if (this.field_6002 instanceof ServerWorld && bl2) {
			((ServerWorld)this.field_6002).updatePlayersSleeping();
		}

		this.sleepTimer = bl ? 0 : 100;
		if (bl3) {
			optional.ifPresent(blockPos -> this.method_7289(blockPos, false));
		}
	}

	@Override
	public void wakeUp() {
		this.wakeUp(true, true, false);
	}

	@Nullable
	public static BlockPos method_7288(BlockView blockView, BlockPos blockPos, boolean bl) {
		Block block = blockView.method_8320(blockPos).getBlock();
		if (!(block instanceof BedBlock)) {
			if (!bl) {
				return null;
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = blockView.method_8320(blockPos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? blockPos : null;
			}
		} else {
			return BedBlock.method_9484(blockView, blockPos, 0);
		}
	}

	public boolean isSleepingLongEnough() {
		return this.isSleeping() && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void method_7353(TextComponent textComponent, boolean bl) {
	}

	public BlockPos method_7280() {
		return this.field_7501;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void method_7289(BlockPos blockPos, boolean bl) {
		if (blockPos != null) {
			this.field_7501 = blockPos;
			this.spawnForced = bl;
		} else {
			this.field_7501 = null;
			this.spawnForced = false;
		}
	}

	public void method_7281(Identifier identifier) {
		this.method_7259(Stats.field_15419.getOrCreateStat(identifier));
	}

	public void method_7339(Identifier identifier, int i) {
		this.method_7342(Stats.field_15419.getOrCreateStat(identifier), i);
	}

	public void method_7259(Stat<?> stat) {
		this.method_7342(stat, 1);
	}

	public void method_7342(Stat<?> stat, int i) {
	}

	public void method_7266(Stat<?> stat) {
	}

	public int unlockRecipes(Collection<Recipe<?>> collection) {
		return 0;
	}

	public void method_7335(Identifier[] identifiers) {
	}

	public int lockRecipes(Collection<Recipe<?>> collection) {
		return 0;
	}

	@Override
	public void jump() {
		super.jump();
		this.method_7281(Stats.field_15428);
		if (this.isSprinting()) {
			this.addExhaustion(0.2F);
		} else {
			this.addExhaustion(0.05F);
		}
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		if (this.isSwimming() && !this.hasVehicle()) {
			double g = this.method_5720().y;
			double h = g < -0.2 ? 0.085 : 0.06;
			if (g <= 0.0 || this.field_6282 || !this.field_6002.method_8320(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).method_11618().isEmpty()) {
				Vec3d vec3d2 = this.method_18798();
				this.method_18799(vec3d2.add(0.0, (g - vec3d2.y) * h, 0.0));
			}
		}

		if (this.abilities.flying && !this.hasVehicle()) {
			double g = this.method_18798().y;
			float i = this.field_6281;
			this.field_6281 = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.method_6091(vec3d);
			Vec3d vec3d3 = this.method_18798();
			this.setVelocity(vec3d3.x, g * 0.6, vec3d3.z);
			this.field_6281 = i;
			this.fallDistance = 0.0F;
			this.setEntityFlag(7, false);
		} else {
			super.method_6091(vec3d);
		}

		this.method_7282(this.x - d, this.y - e, this.z - f);
	}

	@Override
	public void method_5790() {
		if (this.abilities.flying) {
			this.method_5796(false);
		} else {
			super.method_5790();
		}
	}

	protected boolean method_7326(BlockPos blockPos) {
		return !this.field_6002.method_8320(blockPos).method_11582(this.field_6002, blockPos);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue();
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
	protected SoundEvent method_6041(int i) {
		return i > 4 ? SoundEvents.field_14794 : SoundEvents.field_14778;
	}

	@Override
	public void method_5874(LivingEntity livingEntity) {
		this.method_7259(Stats.field_15403.getOrCreateStat(livingEntity.method_5864()));
	}

	@Override
	public void method_5844(BlockState blockState, Vec3d vec3d) {
		if (!this.abilities.flying) {
			super.method_5844(blockState, vec3d);
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
			this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_14709, this.method_5634(), f * 0.75F, 1.0F);
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
			if (!this.field_6002.isClient) {
				this.field_7493.addExhaustion(f);
			}
		}
	}

	public HungerManager method_7344() {
		return this.field_7493;
	}

	public boolean canConsume(boolean bl) {
		return !this.abilities.invulnerable && (bl || this.field_7493.isNotFull());
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getHealthMaximum();
	}

	public boolean canModifyWorld() {
		return this.abilities.allowModifyWorld;
	}

	public boolean method_7343(BlockPos blockPos, Direction direction, ItemStack itemStack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
			CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.field_6002, blockPos2, false);
			return itemStack.method_7944(this.field_6002.method_8514(), cachedBlockPosition);
		}
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		if (!this.field_6002.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
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

	public void method_7336(GameMode gameMode) {
	}

	@Override
	public TextComponent method_5477() {
		return new StringTextComponent(this.gameProfile.getName());
	}

	public EnderChestInventory method_7274() {
		return this.field_7486;
	}

	@Override
	public ItemStack method_6118(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			return this.inventory.method_7391();
		} else if (equipmentSlot == EquipmentSlot.HAND_OFF) {
			return this.inventory.field_7544.get(0);
		} else {
			return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.field_7548.get(equipmentSlot.getEntitySlotId()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			this.method_6116(itemStack);
			this.inventory.field_7547.set(this.inventory.selectedSlot, itemStack);
		} else if (equipmentSlot == EquipmentSlot.HAND_OFF) {
			this.method_6116(itemStack);
			this.inventory.field_7544.set(0, itemStack);
		} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
			this.method_6116(itemStack);
			this.inventory.field_7548.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	public boolean method_7270(ItemStack itemStack) {
		this.method_6116(itemStack);
		return this.inventory.method_7394(itemStack);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return Lists.<ItemStack>newArrayList(this.method_6047(), this.method_6079());
	}

	@Override
	public Iterable<ItemStack> getItemsArmor() {
		return this.inventory.field_7548;
	}

	public boolean method_7298(CompoundTag compoundTag) {
		if (this.hasVehicle() || !this.onGround || this.isInsideWater()) {
			return false;
		} else if (this.method_7356().isEmpty()) {
			this.method_7273(compoundTag);
			return true;
		} else if (this.method_7308().isEmpty()) {
			this.method_7345(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	protected void dropShoulderEntities() {
		this.method_7296(this.method_7356());
		this.method_7273(new CompoundTag());
		this.method_7296(this.method_7308());
		this.method_7345(new CompoundTag());
	}

	private void method_7296(@Nullable CompoundTag compoundTag) {
		if (!this.field_6002.isClient && !compoundTag.isEmpty()) {
			EntityType.method_5892(compoundTag, this.field_6002).ifPresent(entity -> {
				if (entity instanceof TameableEntity) {
					((TameableEntity)entity).setOwnerUuid(this.uuid);
				}

				entity.setPosition(this.x, this.y + 0.7F, this.z);
				((ServerWorld)this.field_6002).method_18768(entity);
			});
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5756(PlayerEntity playerEntity) {
		if (!this.isInvisible()) {
			return false;
		} else if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractScoreboardTeam abstractScoreboardTeam = this.method_5781();
			return abstractScoreboardTeam == null
				|| playerEntity == null
				|| playerEntity.method_5781() != abstractScoreboardTeam
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

	public Scoreboard method_7327() {
		return this.field_6002.method_8428();
	}

	@Override
	public TextComponent method_5476() {
		TextComponent textComponent = ScoreboardTeam.method_1142(this.method_5781(), this.method_5477());
		return this.method_7299(textComponent);
	}

	public TextComponent method_7306() {
		return new StringTextComponent("").append(this.method_5477()).append(" (").append(this.gameProfile.getId().toString()).append(")");
	}

	private TextComponent method_7299(TextComponent textComponent) {
		String string = this.getGameProfile().getName();
		return textComponent.modifyStyle(
			style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " "))
					.setHoverEvent(this.method_5769())
					.setInsertion(string)
		);
	}

	@Override
	public String getEntityName() {
		return this.getGameProfile().getName();
	}

	@Override
	public float method_18394(EntityPose entityPose, EntitySize entitySize) {
		switch (entityPose) {
			case field_18079:
				return 0.4F;
			case field_18077:
				return 0.4F;
			case field_18081:
				return 1.54F;
			default:
				return 1.62F;
		}
	}

	@Override
	public void setAbsorptionAmount(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.method_5841().set(field_7491, f);
	}

	@Override
	public float getAbsorptionAmount() {
		return this.method_5841().get(field_7491);
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
	public boolean method_7348(PlayerModelPart playerModelPart) {
		return (this.method_5841().get(field_7518) & playerModelPart.getBitFlag()) == playerModelPart.getBitFlag();
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (i >= 0 && i < this.inventory.field_7547.size()) {
			this.inventory.method_5447(i, itemStack);
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
				this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
				return true;
			} else if (i == 99) {
				this.method_5673(EquipmentSlot.HAND_OFF, itemStack);
				return true;
			} else if (equipmentSlot == null) {
				int j = i - 200;
				if (j >= 0 && j < this.field_7486.getInvSize()) {
					this.field_7486.method_5447(j, itemStack);
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
					} else if (MobEntity.method_5953(itemStack) != equipmentSlot) {
						return false;
					}
				}

				this.inventory.method_5447(equipmentSlot.getEntitySlotId() + this.inventory.field_7547.size(), itemStack);
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
		return this.field_6011.get(field_7488) == 0 ? OptionMainHand.field_6182 : OptionMainHand.field_6183;
	}

	public void setMainHand(OptionMainHand optionMainHand) {
		this.field_6011.set(field_7488, (byte)(optionMainHand == OptionMainHand.field_6182 ? 0 : 1));
	}

	public CompoundTag method_7356() {
		return this.field_6011.get(field_7496);
	}

	protected void method_7273(CompoundTag compoundTag) {
		this.field_6011.set(field_7496, compoundTag);
	}

	public CompoundTag method_7308() {
		return this.field_6011.get(field_7506);
	}

	protected void method_7345(CompoundTag compoundTag) {
		this.field_6011.set(field_7506, compoundTag);
	}

	public float method_7279() {
		return (float)(1.0 / this.method_5996(EntityAttributes.ATTACK_SPEED).getValue() * 20.0);
	}

	public float method_7261(float f) {
		return MathHelper.clamp(((float)this.field_6273 + f) / this.method_7279(), 0.0F, 1.0F);
	}

	public void method_7350() {
		this.field_6273 = 0;
	}

	public ItemCooldownManager method_7357() {
		return this.field_7484;
	}

	public float getLuck() {
		return (float)this.method_5996(EntityAttributes.LUCK).getValue();
	}

	public boolean isCreativeLevelTwoOp() {
		return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
	}

	@Override
	public boolean method_18397(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
		return this.method_6118(equipmentSlot).isEmpty();
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		return (EntitySize)SIZES.getOrDefault(entityPose, STANDING_SIZE);
	}

	@Override
	public ItemStack method_18808(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof BaseBowItem)) {
			return ItemStack.EMPTY;
		} else {
			Predicate<ItemStack> predicate = ((BaseBowItem)itemStack.getItem()).method_19268();
			ItemStack itemStack2 = BaseBowItem.method_18815(this, predicate);
			if (!itemStack2.isEmpty()) {
				return itemStack2;
			} else {
				for (int i = 0; i < this.inventory.getInvSize(); i++) {
					ItemStack itemStack3 = this.inventory.method_5438(i);
					if (predicate.test(itemStack3)) {
						return itemStack3;
					}
				}

				return this.abilities.creativeMode ? new ItemStack(Items.field_8107) : ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack method_18866(World world, ItemStack itemStack) {
		this.method_7344().method_7579(itemStack.getItem(), itemStack);
		this.method_7259(Stats.field_15372.getOrCreateStat(itemStack.getItem()));
		if (this instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.method_8821((ServerPlayerEntity)this, itemStack);
		}

		return super.method_18866(world, itemStack);
	}

	public static enum SleepResult {
		INVALID_WORLD,
		WRONG_TIME(new TranslatableTextComponent("block.minecraft.bed.no_sleep")),
		TOO_FAR_AWAY(new TranslatableTextComponent("block.minecraft.bed.too_far_away")),
		field_18592(new TranslatableTextComponent("block.minecraft.bed.obstructed")),
		INVALID_ATTEMPT,
		NOT_SAFE(new TranslatableTextComponent("block.minecraft.bed.not_safe"));

		@Nullable
		private final TextComponent field_18593;

		private SleepResult() {
			this.field_18593 = null;
		}

		private SleepResult(TextComponent textComponent) {
			this.field_18593 = textComponent;
		}

		@Nullable
		public TextComponent method_19206() {
			return this.field_18593;
		}
	}
}
