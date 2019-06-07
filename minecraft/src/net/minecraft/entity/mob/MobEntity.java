package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public abstract class MobEntity extends LivingEntity {
	private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
	public int ambientSoundChance;
	protected int experiencePoints;
	protected LookControl lookControl;
	protected MoveControl moveControl;
	protected JumpControl jumpControl;
	private final BodyControl bodyControl;
	protected EntityNavigation navigation;
	protected final GoalSelector goalSelector;
	protected final GoalSelector targetSelector;
	private LivingEntity target;
	private final MobVisibilityCache visibilityCache;
	private final DefaultedList<ItemStack> handItems = DefaultedList.create(2, ItemStack.EMPTY);
	protected final float[] handDropChances = new float[2];
	private final DefaultedList<ItemStack> armorItems = DefaultedList.create(4, ItemStack.EMPTY);
	protected final float[] armorDropChances = new float[4];
	private boolean pickUpLoot;
	private boolean persistent;
	private final Map<PathNodeType, Float> pathNodeTypeWeights = Maps.newEnumMap(PathNodeType.class);
	private Identifier lootTable;
	private long lootTableSeed;
	@Nullable
	private Entity holdingEntity;
	private int holdingEntityId;
	@Nullable
	private CompoundTag leashTag;
	private BlockPos walkTarget = BlockPos.ORIGIN;
	private float walkTargetRange = -1.0F;

	protected MobEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		this.goalSelector = new GoalSelector(world != null && world.getProfiler() != null ? world.getProfiler() : null);
		this.targetSelector = new GoalSelector(world != null && world.getProfiler() != null ? world.getProfiler() : null);
		this.lookControl = new LookControl(this);
		this.moveControl = new MoveControl(this);
		this.jumpControl = new JumpControl(this);
		this.bodyControl = this.createBodyControl();
		this.navigation = this.createNavigation(world);
		this.visibilityCache = new MobVisibilityCache(this);
		Arrays.fill(this.armorDropChances, 0.085F);
		Arrays.fill(this.handDropChances, 0.085F);
		if (world != null && !world.isClient) {
			this.initGoals();
		}
	}

	protected void initGoals() {
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_KNOCKBACK);
	}

	protected EntityNavigation createNavigation(World world) {
		return new MobNavigation(this, world);
	}

	public float getPathNodeTypeWeight(PathNodeType pathNodeType) {
		Float float_ = (Float)this.pathNodeTypeWeights.get(pathNodeType);
		return float_ == null ? pathNodeType.getWeight() : float_;
	}

	public void setPathNodeTypeWeight(PathNodeType pathNodeType, float f) {
		this.pathNodeTypeWeights.put(pathNodeType, f);
	}

	protected BodyControl createBodyControl() {
		return new BodyControl(this);
	}

	public LookControl getLookControl() {
		return this.lookControl;
	}

	public MoveControl getMoveControl() {
		if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.getVehicle();
			return mobEntity.getMoveControl();
		} else {
			return this.moveControl;
		}
	}

	public JumpControl getJumpControl() {
		return this.jumpControl;
	}

	public EntityNavigation getNavigation() {
		if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.getVehicle();
			return mobEntity.getNavigation();
		} else {
			return this.navigation;
		}
	}

	public MobVisibilityCache getVisibilityCache() {
		return this.visibilityCache;
	}

	@Nullable
	public LivingEntity getTarget() {
		return this.target;
	}

	public void setTarget(@Nullable LivingEntity livingEntity) {
		this.target = livingEntity;
	}

	@Override
	public boolean canTarget(EntityType<?> entityType) {
		return entityType != EntityType.field_6107;
	}

	public void onEatingGrass() {
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(MOB_FLAGS, (byte)0);
	}

	public int getMinAmbientSoundDelay() {
		return 80;
	}

	public void playAmbientSound() {
		SoundEvent soundEvent = this.getAmbientSound();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.world.getProfiler().push("mobBaseTick");
		if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
			this.resetSoundDelay();
			this.playAmbientSound();
		}

		this.world.getProfiler().pop();
	}

	@Override
	protected void playHurtSound(DamageSource damageSource) {
		this.resetSoundDelay();
		super.playHurtSound(damageSource);
	}

	private void resetSoundDelay() {
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		if (this.experiencePoints > 0) {
			int i = this.experiencePoints;

			for (int j = 0; j < this.armorItems.size(); j++) {
				if (!this.armorItems.get(j).isEmpty() && this.armorDropChances[j] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			for (int jx = 0; jx < this.handItems.size(); jx++) {
				if (!this.handItems.get(jx).isEmpty() && this.handDropChances[jx] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			return i;
		} else {
			return this.experiencePoints;
		}
	}

	public void playSpawnEffects() {
		if (this.world.isClient) {
			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				double g = 10.0;
				this.world
					.addParticle(
						ParticleTypes.field_11203,
						this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth() - d * 10.0,
						this.y + (double)(this.random.nextFloat() * this.getHeight()) - e * 10.0,
						this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth() - f * 10.0,
						d,
						e,
						f
					);
			}
		} else {
			this.world.sendEntityStatus(this, (byte)20);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 20) {
			this.playSpawnEffects();
		} else {
			super.handleStatus(b);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			this.updateLeash();
			if (this.age % 5 == 0) {
				this.method_20417();
			}
		}
	}

	protected void method_20417() {
		boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
		boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
		this.goalSelector.setControlEnabled(Goal.Control.field_18405, bl);
		this.goalSelector.setControlEnabled(Goal.Control.field_18407, bl && bl2);
		this.goalSelector.setControlEnabled(Goal.Control.field_18406, bl);
	}

	@Override
	protected float method_6031(float f, float g) {
		this.bodyControl.method_6224();
		return g;
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
		compoundTag.putBoolean("PersistenceRequired", this.persistent);
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.armorItems) {
			CompoundTag compoundTag2 = new CompoundTag();
			if (!itemStack.isEmpty()) {
				itemStack.toTag(compoundTag2);
			}

			listTag.add(compoundTag2);
		}

		compoundTag.put("ArmorItems", listTag);
		ListTag listTag2 = new ListTag();

		for (ItemStack itemStack2 : this.handItems) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (!itemStack2.isEmpty()) {
				itemStack2.toTag(compoundTag3);
			}

			listTag2.add(compoundTag3);
		}

		compoundTag.put("HandItems", listTag2);
		ListTag listTag3 = new ListTag();

		for (float f : this.armorDropChances) {
			listTag3.add(new FloatTag(f));
		}

		compoundTag.put("ArmorDropChances", listTag3);
		ListTag listTag4 = new ListTag();

		for (float g : this.handDropChances) {
			listTag4.add(new FloatTag(g));
		}

		compoundTag.put("HandDropChances", listTag4);
		if (this.holdingEntity != null) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (this.holdingEntity instanceof LivingEntity) {
				UUID uUID = this.holdingEntity.getUuid();
				compoundTag3.putUuid("UUID", uUID);
			} else if (this.holdingEntity instanceof AbstractDecorationEntity) {
				BlockPos blockPos = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
				compoundTag3.putInt("X", blockPos.getX());
				compoundTag3.putInt("Y", blockPos.getY());
				compoundTag3.putInt("Z", blockPos.getZ());
			}

			compoundTag.put("Leash", compoundTag3);
		}

		compoundTag.putBoolean("LeftHanded", this.isLeftHanded());
		if (this.lootTable != null) {
			compoundTag.putString("DeathLootTable", this.lootTable.toString());
			if (this.lootTableSeed != 0L) {
				compoundTag.putLong("DeathLootTableSeed", this.lootTableSeed);
			}
		}

		if (this.isAiDisabled()) {
			compoundTag.putBoolean("NoAI", this.isAiDisabled());
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("CanPickUpLoot", 1)) {
			this.setCanPickUpLoot(compoundTag.getBoolean("CanPickUpLoot"));
		}

		this.persistent = compoundTag.getBoolean("PersistenceRequired");
		if (compoundTag.containsKey("ArmorItems", 9)) {
			ListTag listTag = compoundTag.getList("ArmorItems", 10);

			for (int i = 0; i < this.armorItems.size(); i++) {
				this.armorItems.set(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("HandItems", 9)) {
			ListTag listTag = compoundTag.getList("HandItems", 10);

			for (int i = 0; i < this.handItems.size(); i++) {
				this.handItems.set(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("ArmorDropChances", 9)) {
			ListTag listTag = compoundTag.getList("ArmorDropChances", 5);

			for (int i = 0; i < listTag.size(); i++) {
				this.armorDropChances[i] = listTag.getFloat(i);
			}
		}

		if (compoundTag.containsKey("HandDropChances", 9)) {
			ListTag listTag = compoundTag.getList("HandDropChances", 5);

			for (int i = 0; i < listTag.size(); i++) {
				this.handDropChances[i] = listTag.getFloat(i);
			}
		}

		if (compoundTag.containsKey("Leash", 10)) {
			this.leashTag = compoundTag.getCompound("Leash");
		}

		this.setLeftHanded(compoundTag.getBoolean("LeftHanded"));
		if (compoundTag.containsKey("DeathLootTable", 8)) {
			this.lootTable = new Identifier(compoundTag.getString("DeathLootTable"));
			this.lootTableSeed = compoundTag.getLong("DeathLootTableSeed");
		}

		this.setAiDisabled(compoundTag.getBoolean("NoAI"));
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean bl) {
		super.dropLoot(damageSource, bl);
		this.lootTable = null;
	}

	@Override
	protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource damageSource) {
		return super.getLootContextBuilder(bl, damageSource).setRandom(this.lootTableSeed, this.random);
	}

	@Override
	public final Identifier getLootTable() {
		return this.lootTable == null ? this.getLootTableId() : this.lootTable;
	}

	protected Identifier getLootTableId() {
		return super.getLootTable();
	}

	public void setForwardSpeed(float f) {
		this.forwardSpeed = f;
	}

	public void setUpwardSpeed(float f) {
		this.upwardSpeed = f;
	}

	public void setSidewaysSpeed(float f) {
		this.sidewaysSpeed = f;
	}

	@Override
	public void setMovementSpeed(float f) {
		super.setMovementSpeed(f);
		this.setForwardSpeed(f);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.world.getProfiler().push("looting");
		if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.field_19388)) {
			for (ItemEntity itemEntity : this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0))) {
				if (!itemEntity.removed && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup()) {
					this.loot(itemEntity);
				}
			}
		}

		this.world.getProfiler().pop();
	}

	protected void loot(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
		boolean bl = this.isBetterItemFor(itemStack, itemStack2, equipmentSlot);
		if (bl && this.canPickupItem(itemStack)) {
			double d = (double)this.getDropChance(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.dropStack(itemStack2);
			}

			this.setEquippedStack(equipmentSlot, itemStack);
			switch (equipmentSlot.getType()) {
				case field_6177:
					this.handDropChances[equipmentSlot.getEntitySlotId()] = 2.0F;
					break;
				case field_6178:
					this.armorDropChances[equipmentSlot.getEntitySlotId()] = 2.0F;
			}

			this.persistent = true;
			this.sendPickup(itemEntity, itemStack.getCount());
			itemEntity.remove();
		}
	}

	protected boolean isBetterItemFor(ItemStack itemStack, ItemStack itemStack2, EquipmentSlot equipmentSlot) {
		boolean bl = true;
		if (!itemStack2.isEmpty()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.field_6177) {
				if (itemStack.getItem() instanceof SwordItem && !(itemStack2.getItem() instanceof SwordItem)) {
					bl = true;
				} else if (itemStack.getItem() instanceof SwordItem && itemStack2.getItem() instanceof SwordItem) {
					SwordItem swordItem = (SwordItem)itemStack.getItem();
					SwordItem swordItem2 = (SwordItem)itemStack2.getItem();
					if (swordItem.getAttackDamage() == swordItem2.getAttackDamage()) {
						bl = itemStack.getDamage() < itemStack2.getDamage() || itemStack.hasTag() && !itemStack2.hasTag();
					} else {
						bl = swordItem.getAttackDamage() > swordItem2.getAttackDamage();
					}
				} else if (itemStack.getItem() instanceof BowItem && itemStack2.getItem() instanceof BowItem) {
					bl = itemStack.hasTag() && !itemStack2.hasTag();
				} else {
					bl = false;
				}
			} else if (itemStack.getItem() instanceof ArmorItem && !(itemStack2.getItem() instanceof ArmorItem)) {
				bl = true;
			} else if (itemStack.getItem() instanceof ArmorItem && itemStack2.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(itemStack2)) {
				ArmorItem armorItem = (ArmorItem)itemStack.getItem();
				ArmorItem armorItem2 = (ArmorItem)itemStack2.getItem();
				if (armorItem.getProtection() == armorItem2.getProtection()) {
					bl = itemStack.getDamage() < itemStack2.getDamage() || itemStack.hasTag() && !itemStack2.hasTag();
				} else {
					bl = armorItem.getProtection() > armorItem2.getProtection();
				}
			} else {
				bl = false;
			}
		}

		return bl;
	}

	protected boolean canPickupItem(ItemStack itemStack) {
		return true;
	}

	public boolean canImmediatelyDespawn(double d) {
		return true;
	}

	protected boolean cannotDespawn() {
		return false;
	}

	protected void checkDespawn() {
		if (!this.isPersistent() && !this.cannotDespawn()) {
			Entity entity = this.world.getClosestPlayer(this, -1.0);
			if (entity != null) {
				double d = entity.squaredDistanceTo(this);
				if (d > 16384.0 && this.canImmediatelyDespawn(d)) {
					this.remove();
				}

				if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > 1024.0 && this.canImmediatelyDespawn(d)) {
					this.remove();
				} else if (d < 1024.0) {
					this.despawnCounter = 0;
				}
			}
		} else {
			this.despawnCounter = 0;
		}
	}

	@Override
	protected final void tickNewAi() {
		this.despawnCounter++;
		this.world.getProfiler().push("checkDespawn");
		this.checkDespawn();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("sensing");
		this.visibilityCache.clear();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("targetSelector");
		this.targetSelector.tick();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("goalSelector");
		this.goalSelector.tick();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("navigation");
		this.navigation.tick();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("mob tick");
		this.mobTick();
		this.world.getProfiler().pop();
		this.world.getProfiler().push("controls");
		this.world.getProfiler().push("move");
		this.moveControl.tick();
		this.world.getProfiler().swap("look");
		this.lookControl.tick();
		this.world.getProfiler().swap("jump");
		this.jumpControl.tick();
		this.world.getProfiler().pop();
		this.world.getProfiler().pop();
		this.sendAiDebugData();
	}

	protected void sendAiDebugData() {
		DebugRendererInfoManager.sendGoalSelector(this.world, this, this.goalSelector);
	}

	protected void mobTick() {
	}

	public int getLookPitchSpeed() {
		return 40;
	}

	public int method_5986() {
		return 75;
	}

	public int getLookYawSpeed() {
		return 10;
	}

	public void lookAtEntity(Entity entity, float f, float g) {
		double d = entity.x - this.x;
		double e = entity.z - this.z;
		double h;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			h = livingEntity.y + (double)livingEntity.getStandingEyeHeight() - (this.y + (double)this.getStandingEyeHeight());
		} else {
			h = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0 - (this.y + (double)this.getStandingEyeHeight());
		}

		double i = (double)MathHelper.sqrt(d * d + e * e);
		float j = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float k = (float)(-(MathHelper.atan2(h, i) * 180.0F / (float)Math.PI));
		this.pitch = this.changeAngle(this.pitch, k, g);
		this.yaw = this.changeAngle(this.yaw, j, f);
	}

	private float changeAngle(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	public static boolean method_20636(EntityType<? extends MobEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		BlockPos blockPos2 = blockPos.down();
		return spawnType == SpawnType.field_16469 || iWorld.getBlockState(blockPos2).allowsSpawning(iWorld, blockPos2, entityType);
	}

	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return true;
	}

	public boolean canSpawn(ViewableWorld viewableWorld) {
		return !viewableWorld.intersectsFluid(this.getBoundingBox()) && viewableWorld.intersectsEntities(this);
	}

	public int getLimitPerChunk() {
		return 4;
	}

	public boolean spawnsTooManyForEachTry(int i) {
		return false;
	}

	@Override
	public int getSafeFallDistance() {
		if (this.getTarget() == null) {
			return 3;
		} else {
			int i = (int)(this.getHealth() - this.getHealthMaximum() * 0.33F);
			i -= (3 - this.world.getDifficulty().getId()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.handItems;
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.armorItems;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot.getType()) {
			case field_6177:
				return this.handItems.get(equipmentSlot.getEntitySlotId());
			case field_6178:
				return this.armorItems.get(equipmentSlot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		switch (equipmentSlot.getType()) {
			case field_6177:
				this.handItems.set(equipmentSlot.getEntitySlotId(), itemStack);
				break;
			case field_6178:
				this.armorItems.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getEquippedStack(equipmentSlot);
			float f = this.getDropChance(equipmentSlot);
			boolean bl2 = f > 1.0F;
			if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (bl || bl2) && this.random.nextFloat() - (float)i * 0.01F < f) {
				if (!bl2 && itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
				}

				this.dropStack(itemStack);
			}
		}
	}

	protected float getDropChance(EquipmentSlot equipmentSlot) {
		float f;
		switch (equipmentSlot.getType()) {
			case field_6177:
				f = this.handDropChances[equipmentSlot.getEntitySlotId()];
				break;
			case field_6178:
				f = this.armorDropChances[equipmentSlot.getEntitySlotId()];
				break;
			default:
				f = 0.0F;
		}

		return f;
	}

	protected void initEquipment(LocalDifficulty localDifficulty) {
		if (this.random.nextFloat() < 0.15F * localDifficulty.getClampedLocalDifficulty()) {
			int i = this.random.nextInt(2);
			float f = this.world.getDifficulty() == Difficulty.field_5807 ? 0.1F : 0.25F;
			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			boolean bl = true;

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (equipmentSlot.getType() == EquipmentSlot.Type.field_6178) {
					ItemStack itemStack = this.getEquippedStack(equipmentSlot);
					if (!bl && this.random.nextFloat() < f) {
						break;
					}

					bl = false;
					if (itemStack.isEmpty()) {
						Item item = getEquipmentForSlot(equipmentSlot, i);
						if (item != null) {
							this.setEquippedStack(equipmentSlot, new ItemStack(item));
						}
					}
				}
			}
		}
	}

	public static EquipmentSlot getPreferredEquipmentSlot(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item != Blocks.field_10147.asItem() && (!(item instanceof BlockItem) || !(((BlockItem)item).getBlock() instanceof AbstractSkullBlock))) {
			if (item instanceof ArmorItem) {
				return ((ArmorItem)item).getSlotType();
			} else if (item == Items.field_8833) {
				return EquipmentSlot.field_6174;
			} else {
				return item == Items.field_8255 ? EquipmentSlot.field_6171 : EquipmentSlot.field_6173;
			}
		} else {
			return EquipmentSlot.field_6169;
		}
	}

	@Nullable
	public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int i) {
		switch (equipmentSlot) {
			case field_6169:
				if (i == 0) {
					return Items.field_8267;
				} else if (i == 1) {
					return Items.field_8862;
				} else if (i == 2) {
					return Items.field_8283;
				} else if (i == 3) {
					return Items.field_8743;
				} else if (i == 4) {
					return Items.field_8805;
				}
			case field_6174:
				if (i == 0) {
					return Items.field_8577;
				} else if (i == 1) {
					return Items.field_8678;
				} else if (i == 2) {
					return Items.field_8873;
				} else if (i == 3) {
					return Items.field_8523;
				} else if (i == 4) {
					return Items.field_8058;
				}
			case field_6172:
				if (i == 0) {
					return Items.field_8570;
				} else if (i == 1) {
					return Items.field_8416;
				} else if (i == 2) {
					return Items.field_8218;
				} else if (i == 3) {
					return Items.field_8396;
				} else if (i == 4) {
					return Items.field_8348;
				}
			case field_6166:
				if (i == 0) {
					return Items.field_8370;
				} else if (i == 1) {
					return Items.field_8753;
				} else if (i == 2) {
					return Items.field_8313;
				} else if (i == 3) {
					return Items.field_8660;
				} else if (i == 4) {
					return Items.field_8285;
				}
			default:
				return null;
		}
	}

	protected void updateEnchantments(LocalDifficulty localDifficulty) {
		float f = localDifficulty.getClampedLocalDifficulty();
		if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25F * f) {
			this.setEquippedStack(
				EquipmentSlot.field_6173, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0F + f * (float)this.random.nextInt(18)), false)
			);
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.field_6178) {
				ItemStack itemStack = this.getEquippedStack(equipmentSlot);
				if (!itemStack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
					this.setEquippedStack(equipmentSlot, EnchantmentHelper.enchant(this.random, itemStack, (int)(5.0F + f * (float)this.random.nextInt(18)), false));
				}
			}
		}
	}

	@Nullable
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE)
			.addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.field_6330));
		if (this.random.nextFloat() < 0.05F) {
			this.setLeftHanded(true);
		} else {
			this.setLeftHanded(false);
		}

		return entityData;
	}

	public boolean canBeControlledByRider() {
		return false;
	}

	public void setPersistent() {
		this.persistent = true;
	}

	public void setEquipmentDropChance(EquipmentSlot equipmentSlot, float f) {
		switch (equipmentSlot.getType()) {
			case field_6177:
				this.handDropChances[equipmentSlot.getEntitySlotId()] = f;
				break;
			case field_6178:
				this.armorDropChances[equipmentSlot.getEntitySlotId()] = f;
		}
	}

	public boolean canPickUpLoot() {
		return this.pickUpLoot;
	}

	public void setCanPickUpLoot(boolean bl) {
		this.pickUpLoot = bl;
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(itemStack);
		return this.getEquippedStack(equipmentSlot).isEmpty() && this.canPickUpLoot();
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public final boolean interact(PlayerEntity playerEntity, Hand hand) {
		if (!this.isAlive()) {
			return false;
		} else if (this.getHoldingEntity() == playerEntity) {
			this.detachLeash(true, !playerEntity.abilities.creativeMode);
			return true;
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.getItem() == Items.field_8719 && this.canBeLeashedBy(playerEntity)) {
				this.attachLeash(playerEntity, true);
				itemStack.decrement(1);
				return true;
			} else {
				return this.interactMob(playerEntity, hand) ? true : super.interact(playerEntity, hand);
			}
		}
	}

	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	public boolean isInWalkTargetRange() {
		return this.isInWalkTargetRange(new BlockPos(this));
	}

	public boolean isInWalkTargetRange(BlockPos blockPos) {
		return this.walkTargetRange == -1.0F ? true : this.walkTarget.getSquaredDistance(blockPos) < (double)(this.walkTargetRange * this.walkTargetRange);
	}

	public void setWalkTarget(BlockPos blockPos, int i) {
		this.walkTarget = blockPos;
		this.walkTargetRange = (float)i;
	}

	public BlockPos getWalkTarget() {
		return this.walkTarget;
	}

	public float getWalkTargetRange() {
		return this.walkTargetRange;
	}

	public boolean hasWalkTargetRange() {
		return this.walkTargetRange != -1.0F;
	}

	protected void updateLeash() {
		if (this.leashTag != null) {
			this.deserializeLeashTag();
		}

		if (this.holdingEntity != null) {
			if (!this.isAlive() || !this.holdingEntity.isAlive()) {
				this.detachLeash(true, true);
			}
		}
	}

	public void detachLeash(boolean bl, boolean bl2) {
		if (this.holdingEntity != null) {
			this.teleporting = false;
			if (!(this.holdingEntity instanceof PlayerEntity)) {
				this.holdingEntity.teleporting = false;
			}

			this.holdingEntity = null;
			if (!this.world.isClient && bl2) {
				this.dropItem(Items.field_8719);
			}

			if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
				((ServerWorld)this.world).method_14178().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
			}
		}
	}

	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return !this.isLeashed() && !(this instanceof Monster);
	}

	public boolean isLeashed() {
		return this.holdingEntity != null;
	}

	@Nullable
	public Entity getHoldingEntity() {
		if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
			this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
		}

		return this.holdingEntity;
	}

	public void attachLeash(Entity entity, boolean bl) {
		this.holdingEntity = entity;
		this.teleporting = true;
		if (!(this.holdingEntity instanceof PlayerEntity)) {
			this.holdingEntity.teleporting = true;
		}

		if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).method_14178().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	@Environment(EnvType.CLIENT)
	public void setHoldingEntityId(int i) {
		this.holdingEntityId = i;
		this.detachLeash(false, false);
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		boolean bl2 = super.startRiding(entity, bl);
		if (bl2 && this.isLeashed()) {
			this.detachLeash(true, true);
		}

		return bl2;
	}

	private void deserializeLeashTag() {
		if (this.leashTag != null && this.world instanceof ServerWorld) {
			if (this.leashTag.hasUuid("UUID")) {
				UUID uUID = this.leashTag.getUuid("UUID");
				Entity entity = ((ServerWorld)this.world).getEntity(uUID);
				if (entity != null) {
					this.attachLeash(entity, true);
				}
			} else if (this.leashTag.containsKey("X", 99) && this.leashTag.containsKey("Y", 99) && this.leashTag.containsKey("Z", 99)) {
				BlockPos blockPos = new BlockPos(this.leashTag.getInt("X"), this.leashTag.getInt("Y"), this.leashTag.getInt("Z"));
				this.attachLeash(LeadKnotEntity.getOrCreate(this.world, blockPos), true);
			} else {
				this.detachLeash(false, true);
			}

			this.leashTag = null;
		}
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
		EquipmentSlot equipmentSlot;
		if (i == 98) {
			equipmentSlot = EquipmentSlot.field_6173;
		} else if (i == 99) {
			equipmentSlot = EquipmentSlot.field_6171;
		} else if (i == 100 + EquipmentSlot.field_6169.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.field_6169;
		} else if (i == 100 + EquipmentSlot.field_6174.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.field_6174;
		} else if (i == 100 + EquipmentSlot.field_6172.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.field_6172;
		} else {
			if (i != 100 + EquipmentSlot.field_6166.getEntitySlotId()) {
				return false;
			}

			equipmentSlot = EquipmentSlot.field_6166;
		}

		if (!itemStack.isEmpty() && !canEquipmentSlotContain(equipmentSlot, itemStack) && equipmentSlot != EquipmentSlot.field_6169) {
			return false;
		} else {
			this.setEquippedStack(equipmentSlot, itemStack);
			return true;
		}
	}

	@Override
	public boolean isLogicalSideForUpdatingMovement() {
		return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
	}

	public static boolean canEquipmentSlotContain(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		EquipmentSlot equipmentSlot2 = getPreferredEquipmentSlot(itemStack);
		return equipmentSlot2 == equipmentSlot
			|| equipmentSlot2 == EquipmentSlot.field_6173 && equipmentSlot == EquipmentSlot.field_6171
			|| equipmentSlot2 == EquipmentSlot.field_6171 && equipmentSlot == EquipmentSlot.field_6173;
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.isAiDisabled();
	}

	public void setAiDisabled(boolean bl) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 1) : (byte)(b & -2));
	}

	public void setLeftHanded(boolean bl) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 2) : (byte)(b & -3));
	}

	public void setAttacking(boolean bl) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 4) : (byte)(b & -5));
	}

	public boolean isAiDisabled() {
		return (this.dataTracker.get(MOB_FLAGS) & 1) != 0;
	}

	public boolean isLeftHanded() {
		return (this.dataTracker.get(MOB_FLAGS) & 2) != 0;
	}

	public boolean isAttacking() {
		return (this.dataTracker.get(MOB_FLAGS) & 4) != 0;
	}

	@Override
	public Arm getMainArm() {
		return this.isLeftHanded() ? Arm.field_6182 : Arm.field_6183;
	}

	@Override
	public boolean canTarget(LivingEntity livingEntity) {
		return livingEntity.getType() == EntityType.field_6097 && ((PlayerEntity)livingEntity).abilities.invulnerable ? false : super.canTarget(livingEntity);
	}

	@Override
	public boolean tryAttack(Entity entity) {
		float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
		float g = (float)this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).getValue();
		if (entity instanceof LivingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
			g += (float)EnchantmentHelper.getKnockback(this);
		}

		int i = EnchantmentHelper.getFireAspect(this);
		if (i > 0) {
			entity.setOnFireFor(i * 4);
		}

		boolean bl = entity.damage(DamageSource.mob(this), f);
		if (bl) {
			if (g > 0.0F && entity instanceof LivingEntity) {
				((LivingEntity)entity)
					.takeKnockback(
						this, g * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
					);
				this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
			}

			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				ItemStack itemStack = this.getMainHandStack();
				ItemStack itemStack2 = playerEntity.isUsingItem() ? playerEntity.getActiveItem() : ItemStack.EMPTY;
				if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.getItem() == Items.field_8255) {
					float h = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
					if (this.random.nextFloat() < h) {
						playerEntity.getItemCooldownManager().set(Items.field_8255, 100);
						this.world.sendEntityStatus(playerEntity, (byte)30);
					}
				}
			}

			this.dealDamage(this, entity);
		}

		return bl;
	}

	protected boolean isInDaylight() {
		if (this.world.isDaylight() && !this.world.isClient) {
			float f = this.getBrightnessAtEyes();
			BlockPos blockPos = this.getVehicle() instanceof BoatEntity
				? new BlockPos(this.x, (double)Math.round(this.y), this.z).up()
				: new BlockPos(this.x, (double)Math.round(this.y), this.z);
			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.isSkyVisible(blockPos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void swimUpward(Tag<Fluid> tag) {
		if (this.getNavigation().canSwim()) {
			super.swimUpward(tag);
		} else {
			this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
		}
	}

	public boolean isHolding(Item item) {
		return this.getMainHandStack().getItem() == item || this.getOffHandStack().getItem() == item;
	}
}
