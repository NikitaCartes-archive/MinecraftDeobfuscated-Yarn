package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public abstract class MobEntity extends LivingEntity implements Targeter {
	private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int AI_DISABLED_FLAG = 1;
	private static final int LEFT_HANDED_FLAG = 2;
	private static final int ATTACKING_FLAG = 4;
	/**
	 * The minimum additional experience a mob will drop per item of equipment they have.
	 * 
	 * @see MobEntity#getXpToDrop
	 */
	protected static final int MINIMUM_DROPPED_XP_PER_EQUIPMENT = 1;
	private static final Vec3i ITEM_PICK_UP_RANGE_EXPANDER = new Vec3i(1, 0, 1);
	/**
	 * The base chance (before applying local difficulty) that this mob will spawn with equipment.
	 * 
	 * @see MobEntity#initEquipment
	 */
	public static final float BASE_SPAWN_EQUIPMENT_CHANCE = 0.15F;
	/**
	 * Used by Zombies to control the chance that they spawn with the ability to pick up loot.
	 * 
	 * @see ZombieEntity#initialize
	 */
	public static final float DEFAULT_CAN_PICKUP_LOOT_CHANCE = 0.55F;
	/**
	 * The base chance (before applying difficulty) that a mob's equipped armor can become enchanted.
	 * 
	 * @see MobEntity#enchantEquipment
	 */
	public static final float BASE_ENCHANTED_ARMOR_CHANCE = 0.5F;
	/**
	 * The base chance (before applying difficulty) that a mob's equipped item can become enchanted.
	 * 
	 * @see MobEntity#enchantMainHandItem
	 */
	public static final float BASE_ENCHANTED_MAIN_HAND_EQUIPMENT_CHANCE = 0.25F;
	public static final String LEASH_KEY = "leash";
	public static final float DEFAULT_DROP_CHANCE = 0.085F;
	public static final int field_38932 = 2;
	public static final int field_35039 = 2;
	private static final double ATTACK_RANGE = Math.sqrt(2.04F) - 0.6F;
	public int ambientSoundChance;
	protected int experiencePoints;
	protected LookControl lookControl;
	protected MoveControl moveControl;
	protected JumpControl jumpControl;
	private final BodyControl bodyControl;
	protected EntityNavigation navigation;
	/**
	 * Contains actions the entity can perform. These may consume, for example, the target
	 * entity as determined during the {@link MobEntity#targetSelector}'s execution.
	 */
	protected final GoalSelector goalSelector;
	/**
	 * Contains goals used to select this entity's target.
	 * Actions in this queue are executed first so the selected target is available
	 * to the rest of the AI's goals.
	 */
	protected final GoalSelector targetSelector;
	@Nullable
	private LivingEntity target;
	private final MobVisibilityCache visibilityCache;
	private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	protected final float[] handDropChances = new float[2];
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
	protected final float[] armorDropChances = new float[4];
	private ItemStack bodyArmor = ItemStack.EMPTY;
	protected float bodyArmorDropChance;
	private boolean canPickUpLoot;
	private boolean persistent;
	private final Map<PathNodeType, Float> pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
	@Nullable
	private Identifier lootTable;
	private long lootTableSeed;
	@Nullable
	private Entity holdingEntity;
	private int holdingEntityId;
	@Nullable
	private Either<UUID, BlockPos> leashNbt;
	private BlockPos positionTarget = BlockPos.ORIGIN;
	private float positionTargetRange = -1.0F;

	protected MobEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		this.goalSelector = new GoalSelector(world.getProfilerSupplier());
		this.targetSelector = new GoalSelector(world.getProfilerSupplier());
		this.lookControl = new LookControl(this);
		this.moveControl = new MoveControl(this);
		this.jumpControl = new JumpControl(this);
		this.bodyControl = this.createBodyControl();
		this.navigation = this.createNavigation(world);
		this.visibilityCache = new MobVisibilityCache(this);
		Arrays.fill(this.armorDropChances, 0.085F);
		Arrays.fill(this.handDropChances, 0.085F);
		this.bodyArmorDropChance = 0.085F;
		if (world != null && !world.isClient) {
			this.initGoals();
		}
	}

	protected void initGoals() {
	}

	public static DefaultAttributeContainer.Builder createMobAttributes() {
		return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
	}

	protected EntityNavigation createNavigation(World world) {
		return new MobNavigation(this, world);
	}

	/**
	 * When true, causes this entity to take over pathfinding for its controlling passenger.
	 */
	protected boolean movesIndependently() {
		return false;
	}

	public float getPathfindingPenalty(PathNodeType nodeType) {
		MobEntity mobEntity2;
		label17: {
			if (this.getControllingVehicle() instanceof MobEntity mobEntity && mobEntity.movesIndependently()) {
				mobEntity2 = mobEntity;
				break label17;
			}

			mobEntity2 = this;
		}

		Float float_ = (Float)mobEntity2.pathfindingPenalties.get(nodeType);
		return float_ == null ? nodeType.getDefaultPenalty() : float_;
	}

	public void setPathfindingPenalty(PathNodeType nodeType, float penalty) {
		this.pathfindingPenalties.put(nodeType, penalty);
	}

	public void onStartPathfinding() {
	}

	public void onFinishPathfinding() {
	}

	protected BodyControl createBodyControl() {
		return new BodyControl(this);
	}

	public LookControl getLookControl() {
		return this.lookControl;
	}

	public MoveControl getMoveControl() {
		return this.getControllingVehicle() instanceof MobEntity mobEntity ? mobEntity.getMoveControl() : this.moveControl;
	}

	public JumpControl getJumpControl() {
		return this.jumpControl;
	}

	public EntityNavigation getNavigation() {
		return this.getControllingVehicle() instanceof MobEntity mobEntity ? mobEntity.getNavigation() : this.navigation;
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		if (!this.isAiDisabled() && entity instanceof MobEntity mobEntity && entity.shouldControlVehicles()) {
			return mobEntity;
		}

		return null;
	}

	public MobVisibilityCache getVisibilityCache() {
		return this.visibilityCache;
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.target;
	}

	public void setTarget(@Nullable LivingEntity target) {
		this.target = target;
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		return type != EntityType.GHAST;
	}

	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return false;
	}

	public void onEatingGrass() {
		this.emitGameEvent(GameEvent.EAT);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(MOB_FLAGS, (byte)0);
	}

	public int getMinAmbientSoundDelay() {
		return 80;
	}

	public void playAmbientSound() {
		this.playSound(this.getAmbientSound());
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.getWorld().getProfiler().push("mobBaseTick");
		if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
			this.resetSoundDelay();
			this.playAmbientSound();
		}

		this.getWorld().getProfiler().pop();
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
	public int getXpToDrop() {
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

			if (!this.bodyArmor.isEmpty() && this.bodyArmorDropChance <= 1.0F) {
				i += 1 + this.random.nextInt(3);
			}

			return i;
		} else {
			return this.experiencePoints;
		}
	}

	public void playSpawnEffects() {
		if (this.getWorld().isClient) {
			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				double g = 10.0;
				this.getWorld().addParticle(ParticleTypes.POOF, this.offsetX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
			}
		} else {
			this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_SPAWN_EFFECTS);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_SPAWN_EFFECTS) {
			this.playSpawnEffects();
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getWorld().isClient) {
			this.updateLeash();
			if (this.age % 5 == 0) {
				this.updateGoalControls();
			}
		}
	}

	protected void updateGoalControls() {
		boolean bl = !(this.getControllingPassenger() instanceof MobEntity);
		boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
		this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
		this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
		this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
	}

	@Override
	protected float turnHead(float bodyRotation, float headRotation) {
		this.bodyControl.tick();
		return headRotation;
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("CanPickUpLoot", this.canPickUpLoot());
		nbt.putBoolean("PersistenceRequired", this.persistent);
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.armorItems) {
			NbtCompound nbtCompound = new NbtCompound();
			if (!itemStack.isEmpty()) {
				itemStack.writeNbt(nbtCompound);
			}

			nbtList.add(nbtCompound);
		}

		nbt.put("ArmorItems", nbtList);
		NbtList nbtList2 = new NbtList();

		for (float f : this.armorDropChances) {
			nbtList2.add(NbtFloat.of(f));
		}

		nbt.put("ArmorDropChances", nbtList2);
		NbtList nbtList3 = new NbtList();

		for (ItemStack itemStack2 : this.handItems) {
			NbtCompound nbtCompound2 = new NbtCompound();
			if (!itemStack2.isEmpty()) {
				itemStack2.writeNbt(nbtCompound2);
			}

			nbtList3.add(nbtCompound2);
		}

		nbt.put("HandItems", nbtList3);
		NbtList nbtList4 = new NbtList();

		for (float g : this.handDropChances) {
			nbtList4.add(NbtFloat.of(g));
		}

		nbt.put("HandDropChances", nbtList4);
		if (!this.bodyArmor.isEmpty()) {
			nbt.put("body_armor_item", this.bodyArmor.writeNbt(new NbtCompound()));
			nbt.putFloat("body_armor_drop_chance", this.bodyArmorDropChance);
		}

		Either<UUID, BlockPos> either = this.leashNbt;
		if (this.holdingEntity instanceof LivingEntity) {
			either = Either.left(this.holdingEntity.getUuid());
		} else if (this.holdingEntity instanceof AbstractDecorationEntity abstractDecorationEntity) {
			either = Either.right(abstractDecorationEntity.getDecorationBlockPos());
		}

		if (either != null) {
			nbt.put("leash", either.map(uuid -> {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putUuid("UUID", uuid);
				return nbtCompound;
			}, NbtHelper::fromBlockPos));
		}

		nbt.putBoolean("LeftHanded", this.isLeftHanded());
		if (this.lootTable != null) {
			nbt.putString("DeathLootTable", this.lootTable.toString());
			if (this.lootTableSeed != 0L) {
				nbt.putLong("DeathLootTableSeed", this.lootTableSeed);
			}
		}

		if (this.isAiDisabled()) {
			nbt.putBoolean("NoAI", this.isAiDisabled());
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("CanPickUpLoot", NbtElement.BYTE_TYPE)) {
			this.setCanPickUpLoot(nbt.getBoolean("CanPickUpLoot"));
		}

		this.persistent = nbt.getBoolean("PersistenceRequired");
		if (nbt.contains("ArmorItems", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("ArmorItems", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < this.armorItems.size(); i++) {
				this.armorItems.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
			}
		}

		if (nbt.contains("ArmorDropChances", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("ArmorDropChances", NbtElement.FLOAT_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				this.armorDropChances[i] = nbtList.getFloat(i);
			}
		}

		if (nbt.contains("HandItems", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("HandItems", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < this.handItems.size(); i++) {
				this.handItems.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
			}
		}

		if (nbt.contains("HandDropChances", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("HandDropChances", NbtElement.FLOAT_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				this.handDropChances[i] = nbtList.getFloat(i);
			}
		}

		if (nbt.contains("body_armor_item", NbtElement.COMPOUND_TYPE)) {
			this.bodyArmor = ItemStack.fromNbt(nbt.getCompound("body_armor_item"));
			this.bodyArmorDropChance = nbt.getFloat("body_armor_drop_chance");
		}

		if (nbt.contains("leash", NbtElement.COMPOUND_TYPE)) {
			this.leashNbt = Either.left(nbt.getCompound("leash").getUuid("UUID"));
		} else if (nbt.contains("leash", NbtElement.INT_ARRAY_TYPE)) {
			this.leashNbt = (Either<UUID, BlockPos>)NbtHelper.toBlockPos(nbt, "leash").map(Either::right).orElse(null);
		} else {
			this.leashNbt = null;
		}

		this.setLeftHanded(nbt.getBoolean("LeftHanded"));
		if (nbt.contains("DeathLootTable", NbtElement.STRING_TYPE)) {
			this.lootTable = new Identifier(nbt.getString("DeathLootTable"));
			this.lootTableSeed = nbt.getLong("DeathLootTableSeed");
		}

		this.setAiDisabled(nbt.getBoolean("NoAI"));
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
		super.dropLoot(damageSource, causedByPlayer);
		this.lootTable = null;
	}

	@Override
	public final Identifier getLootTable() {
		return this.lootTable == null ? this.getLootTableId() : this.lootTable;
	}

	protected Identifier getLootTableId() {
		return super.getLootTable();
	}

	@Override
	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	public void setForwardSpeed(float forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	public void setUpwardSpeed(float upwardSpeed) {
		this.upwardSpeed = upwardSpeed;
	}

	public void setSidewaysSpeed(float sidewaysSpeed) {
		this.sidewaysSpeed = sidewaysSpeed;
	}

	@Override
	public void setMovementSpeed(float movementSpeed) {
		super.setMovementSpeed(movementSpeed);
		this.setForwardSpeed(movementSpeed);
	}

	public void stopMovement() {
		this.getNavigation().stop();
		this.setSidewaysSpeed(0.0F);
		this.setUpwardSpeed(0.0F);
		this.setMovementSpeed(0.0F);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.getWorld().getProfiler().push("looting");
		if (!this.getWorld().isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
			)
		 {
			Vec3i vec3i = this.getItemPickUpRangeExpander();

			for (ItemEntity itemEntity : this.getWorld()
				.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ()))) {
				if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(itemEntity.getStack())) {
					this.loot(itemEntity);
				}
			}
		}

		this.getWorld().getProfiler().pop();
	}

	protected Vec3i getItemPickUpRangeExpander() {
		return ITEM_PICK_UP_RANGE_EXPANDER;
	}

	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		ItemStack itemStack2 = this.tryEquip(itemStack.copy());
		if (!itemStack2.isEmpty()) {
			this.triggerItemPickedUpByEntityCriteria(item);
			this.sendPickup(item, itemStack2.getCount());
			itemStack.decrement(itemStack2.getCount());
			if (itemStack.isEmpty()) {
				item.discard();
			}
		}
	}

	public ItemStack tryEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(stack);
		ItemStack itemStack = this.getEquippedStack(equipmentSlot);
		boolean bl = this.prefersNewEquipment(stack, itemStack);
		if (equipmentSlot.isArmorSlot() && !bl) {
			equipmentSlot = EquipmentSlot.MAINHAND;
			itemStack = this.getEquippedStack(equipmentSlot);
			bl = itemStack.isEmpty();
		}

		if (bl && this.canPickupItem(stack)) {
			double d = (double)this.getDropChance(equipmentSlot);
			if (!itemStack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
				this.dropStack(itemStack);
			}

			if (equipmentSlot.isArmorSlot() && stack.getCount() > 1) {
				ItemStack itemStack2 = stack.copyWithCount(1);
				this.equipLootStack(equipmentSlot, itemStack2);
				return itemStack2;
			} else {
				this.equipLootStack(equipmentSlot, stack);
				return stack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
		this.equipStack(slot, stack);
		this.updateDropChances(slot);
		this.persistent = true;
	}

	public void updateDropChances(EquipmentSlot slot) {
		switch (slot.getType()) {
			case HAND:
				this.handDropChances[slot.getEntitySlotId()] = 2.0F;
				break;
			case ARMOR:
				this.armorDropChances[slot.getEntitySlotId()] = 2.0F;
				break;
			case BODY:
				this.bodyArmorDropChance = 2.0F;
		}
	}

	protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
		if (oldStack.isEmpty()) {
			return true;
		} else if (newStack.getItem() instanceof SwordItem) {
			if (!(oldStack.getItem() instanceof SwordItem)) {
				return true;
			} else {
				SwordItem swordItem = (SwordItem)newStack.getItem();
				SwordItem swordItem2 = (SwordItem)oldStack.getItem();
				return swordItem.getAttackDamage() != swordItem2.getAttackDamage()
					? swordItem.getAttackDamage() > swordItem2.getAttackDamage()
					: this.prefersNewDamageableItem(newStack, oldStack);
			}
		} else if (newStack.getItem() instanceof BowItem && oldStack.getItem() instanceof BowItem) {
			return this.prefersNewDamageableItem(newStack, oldStack);
		} else if (newStack.getItem() instanceof CrossbowItem && oldStack.getItem() instanceof CrossbowItem) {
			return this.prefersNewDamageableItem(newStack, oldStack);
		} else if (newStack.getItem() instanceof ArmorItem armorItem) {
			if (EnchantmentHelper.hasBindingCurse(oldStack)) {
				return false;
			} else if (!(oldStack.getItem() instanceof ArmorItem)) {
				return true;
			} else {
				ArmorItem armorItem2 = (ArmorItem)oldStack.getItem();
				if (armorItem.getProtection() != armorItem2.getProtection()) {
					return armorItem.getProtection() > armorItem2.getProtection();
				} else {
					return armorItem.getToughness() != armorItem2.getToughness()
						? armorItem.getToughness() > armorItem2.getToughness()
						: this.prefersNewDamageableItem(newStack, oldStack);
				}
			}
		} else {
			if (newStack.getItem() instanceof MiningToolItem) {
				if (oldStack.getItem() instanceof BlockItem) {
					return true;
				}

				if (oldStack.getItem() instanceof MiningToolItem miningToolItem) {
					MiningToolItem miningToolItem2 = (MiningToolItem)newStack.getItem();
					if (miningToolItem2.getAttackDamage() != miningToolItem.getAttackDamage()) {
						return miningToolItem2.getAttackDamage() > miningToolItem.getAttackDamage();
					}

					return this.prefersNewDamageableItem(newStack, oldStack);
				}
			}

			return false;
		}
	}

	public boolean prefersNewDamageableItem(ItemStack newStack, ItemStack oldStack) {
		if (newStack.getDamage() >= oldStack.getDamage() && (!newStack.hasNbt() || oldStack.hasNbt())) {
			return newStack.hasNbt() && oldStack.hasNbt()
				? newStack.getNbt().getKeys().stream().anyMatch(key -> !key.equals("Damage"))
					&& !oldStack.getNbt().getKeys().stream().anyMatch(key -> !key.equals("Damage"))
				: false;
		} else {
			return true;
		}
	}

	public boolean canPickupItem(ItemStack stack) {
		return true;
	}

	public boolean canGather(ItemStack stack) {
		return this.canPickupItem(stack);
	}

	public boolean canImmediatelyDespawn(double distanceSquared) {
		return true;
	}

	public boolean cannotDespawn() {
		return this.hasVehicle();
	}

	protected boolean isDisallowedInPeaceful() {
		return false;
	}

	@Override
	public void checkDespawn() {
		if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
			this.discard();
		} else if (!this.isPersistent() && !this.cannotDespawn()) {
			Entity entity = this.getWorld().getClosestPlayer(this, -1.0);
			if (entity != null) {
				double d = entity.squaredDistanceTo(this);
				int i = this.getType().getSpawnGroup().getImmediateDespawnRange();
				int j = i * i;
				if (d > (double)j && this.canImmediatelyDespawn(d)) {
					this.discard();
				}

				int k = this.getType().getSpawnGroup().getDespawnStartRange();
				int l = k * k;
				if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > (double)l && this.canImmediatelyDespawn(d)) {
					this.discard();
				} else if (d < (double)l) {
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
		Profiler profiler = this.getWorld().getProfiler();
		profiler.push("sensing");
		this.visibilityCache.clear();
		profiler.pop();
		int i = this.getWorld().getServer().getTicks() + this.getId();
		if (i % 2 != 0 && this.age > 1) {
			profiler.push("targetSelector");
			this.targetSelector.tickGoals(false);
			profiler.pop();
			profiler.push("goalSelector");
			this.goalSelector.tickGoals(false);
			profiler.pop();
		} else {
			profiler.push("targetSelector");
			this.targetSelector.tick();
			profiler.pop();
			profiler.push("goalSelector");
			this.goalSelector.tick();
			profiler.pop();
		}

		profiler.push("navigation");
		this.navigation.tick();
		profiler.pop();
		profiler.push("mob tick");
		this.mobTick();
		profiler.pop();
		profiler.push("controls");
		profiler.push("move");
		this.moveControl.tick();
		profiler.swap("look");
		this.lookControl.tick();
		profiler.swap("jump");
		this.jumpControl.tick();
		profiler.pop();
		profiler.pop();
		this.sendAiDebugData();
	}

	protected void sendAiDebugData() {
		DebugInfoSender.sendGoalSelector(this.getWorld(), this, this.goalSelector);
	}

	protected void mobTick() {
	}

	/**
	 * {@return the maximum degrees which the pitch can change when looking}
	 * 
	 * <p>This is used by the look control.
	 * 
	 * <p>It can return from {@code 1} for entities that can hardly raise their head,
	 * like axolotls or dolphins, or {@code 180} for entities that can freely raise
	 * and lower their head, like guardians. The default return value is {@code 40}.
	 */
	public int getMaxLookPitchChange() {
		return 40;
	}

	/**
	 * {@return the maximum degrees which the head yaw can differ from the body yaw}
	 * 
	 * <p>This is used by the body control.
	 * 
	 * <p>It can return from {@code 1} for entities that can hardly rotate their head,
	 * like axolotls or dolphins, or {@code 180} for entities that can freely rotate
	 * their head, like shulkers. The default return value is {@code 75}.
	 */
	public int getMaxHeadRotation() {
		return 75;
	}

	protected void clampHeadYaw() {
		float f = (float)this.getMaxHeadRotation();
		float g = this.getHeadYaw();
		float h = MathHelper.wrapDegrees(this.bodyYaw - g);
		float i = MathHelper.clamp(MathHelper.wrapDegrees(this.bodyYaw - g), -f, f);
		float j = g + h - i;
		this.setHeadYaw(j);
	}

	/**
	 * {@return the maximum degrees which the yaw can change when looking}
	 * 
	 * <p>This is used by the look control.
	 * 
	 * <p>The default return value is {@code 10}.
	 */
	public int getMaxLookYawChange() {
		return 10;
	}

	public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
		double d = targetEntity.getX() - this.getX();
		double e = targetEntity.getZ() - this.getZ();
		double f;
		if (targetEntity instanceof LivingEntity livingEntity) {
			f = livingEntity.getEyeY() - this.getEyeY();
		} else {
			f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
		}

		double g = Math.sqrt(d * d + e * e);
		float h = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float i = (float)(-(MathHelper.atan2(f, g) * 180.0F / (float)Math.PI));
		this.setPitch(this.changeAngle(this.getPitch(), i, maxPitchChange));
		this.setYaw(this.changeAngle(this.getYaw(), h, maxYawChange));
	}

	/**
	 * Changes the angle from {@code from} to {@code to}, or by {@code max} degrees
	 * if {@code to} is too big a change.
	 * 
	 * <p>This is the same as {@link LookControl#changeAngle(float, float, float)}.
	 */
	private float changeAngle(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(to - from);
		if (f > max) {
			f = max;
		}

		if (f < -max) {
			f = -max;
		}

		return from + f;
	}

	public static boolean canMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		BlockPos blockPos = pos.down();
		return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockPos).allowsSpawning(world, blockPos, type);
	}

	public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
		return true;
	}

	public boolean canSpawn(WorldView world) {
		return !world.containsFluid(this.getBoundingBox()) && world.doesNotIntersectEntities(this);
	}

	public int getLimitPerChunk() {
		return 4;
	}

	public boolean spawnsTooManyForEachTry(int count) {
		return false;
	}

	@Override
	public int getSafeFallDistance() {
		if (this.getTarget() == null) {
			return this.getSafeFallDistance(0.0F);
		} else {
			int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
			i -= (3 - this.getWorld().getDifficulty().getId()) * 4;
			if (i < 0) {
				i = 0;
			}

			return this.getSafeFallDistance((float)i);
		}
	}

	@Override
	public Iterable<ItemStack> getHandItems() {
		return this.handItems;
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.armorItems;
	}

	public ItemStack getBodyArmor() {
		return this.bodyArmor;
	}

	public boolean hasArmorSlot() {
		return false;
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot) {
		return true;
	}

	public boolean isWearingBodyArmor() {
		return !this.getEquippedStack(EquipmentSlot.BODY).isEmpty();
	}

	public boolean isHorseArmor(ItemStack stack) {
		return false;
	}

	public void equipBodyArmor(ItemStack stack) {
		this.equipLootStack(EquipmentSlot.BODY, stack);
	}

	@Override
	public Iterable<ItemStack> getAllArmorItems() {
		return (Iterable<ItemStack>)(this.bodyArmor.isEmpty() ? this.armorItems : Iterables.concat(this.armorItems, List.of(this.bodyArmor)));
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		return switch (slot.getType()) {
			case HAND -> (ItemStack)this.handItems.get(slot.getEntitySlotId());
			case ARMOR -> (ItemStack)this.armorItems.get(slot.getEntitySlotId());
			case BODY -> this.bodyArmor;
		};
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		this.processEquippedStack(stack);
		switch (slot.getType()) {
			case HAND:
				this.onEquipStack(slot, this.handItems.set(slot.getEntitySlotId(), stack), stack);
				break;
			case ARMOR:
				this.onEquipStack(slot, this.armorItems.set(slot.getEntitySlotId(), stack), stack);
				break;
			case BODY:
				ItemStack itemStack = this.bodyArmor;
				this.bodyArmor = stack;
				this.onEquipStack(slot, itemStack, stack);
		}
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getEquippedStack(equipmentSlot);
			float f = this.getDropChance(equipmentSlot);
			boolean bl = f > 1.0F;
			if (!itemStack.isEmpty()
				&& !EnchantmentHelper.hasVanishingCurse(itemStack)
				&& (allowDrops || bl)
				&& Math.max(this.random.nextFloat() - (float)lootingMultiplier * 0.01F, 0.0F) < f) {
				if (!bl && itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
				}

				this.dropStack(itemStack);
				this.equipStack(equipmentSlot, ItemStack.EMPTY);
			}
		}
	}

	protected float getDropChance(EquipmentSlot slot) {
		return switch (slot.getType()) {
			case HAND -> this.handDropChances[slot.getEntitySlotId()];
			case ARMOR -> this.armorDropChances[slot.getEntitySlotId()];
			case BODY -> this.bodyArmorDropChance;
		};
	}

	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		if (random.nextFloat() < 0.15F * localDifficulty.getClampedLocalDifficulty()) {
			int i = random.nextInt(2);
			float f = this.getWorld().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
			if (random.nextFloat() < 0.095F) {
				i++;
			}

			if (random.nextFloat() < 0.095F) {
				i++;
			}

			if (random.nextFloat() < 0.095F) {
				i++;
			}

			boolean bl = true;

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
					ItemStack itemStack = this.getEquippedStack(equipmentSlot);
					if (!bl && random.nextFloat() < f) {
						break;
					}

					bl = false;
					if (itemStack.isEmpty()) {
						Item item = getEquipmentForSlot(equipmentSlot, i);
						if (item != null) {
							this.equipStack(equipmentSlot, new ItemStack(item));
						}
					}
				}
			}
		}
	}

	@Nullable
	public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int equipmentLevel) {
		switch (equipmentSlot) {
			case HEAD:
				if (equipmentLevel == 0) {
					return Items.LEATHER_HELMET;
				} else if (equipmentLevel == 1) {
					return Items.GOLDEN_HELMET;
				} else if (equipmentLevel == 2) {
					return Items.CHAINMAIL_HELMET;
				} else if (equipmentLevel == 3) {
					return Items.IRON_HELMET;
				} else if (equipmentLevel == 4) {
					return Items.DIAMOND_HELMET;
				}
			case CHEST:
				if (equipmentLevel == 0) {
					return Items.LEATHER_CHESTPLATE;
				} else if (equipmentLevel == 1) {
					return Items.GOLDEN_CHESTPLATE;
				} else if (equipmentLevel == 2) {
					return Items.CHAINMAIL_CHESTPLATE;
				} else if (equipmentLevel == 3) {
					return Items.IRON_CHESTPLATE;
				} else if (equipmentLevel == 4) {
					return Items.DIAMOND_CHESTPLATE;
				}
			case LEGS:
				if (equipmentLevel == 0) {
					return Items.LEATHER_LEGGINGS;
				} else if (equipmentLevel == 1) {
					return Items.GOLDEN_LEGGINGS;
				} else if (equipmentLevel == 2) {
					return Items.CHAINMAIL_LEGGINGS;
				} else if (equipmentLevel == 3) {
					return Items.IRON_LEGGINGS;
				} else if (equipmentLevel == 4) {
					return Items.DIAMOND_LEGGINGS;
				}
			case FEET:
				if (equipmentLevel == 0) {
					return Items.LEATHER_BOOTS;
				} else if (equipmentLevel == 1) {
					return Items.GOLDEN_BOOTS;
				} else if (equipmentLevel == 2) {
					return Items.CHAINMAIL_BOOTS;
				} else if (equipmentLevel == 3) {
					return Items.IRON_BOOTS;
				} else if (equipmentLevel == 4) {
					return Items.DIAMOND_BOOTS;
				}
			default:
				return null;
		}
	}

	protected void updateEnchantments(Random random, LocalDifficulty localDifficulty) {
		float f = localDifficulty.getClampedLocalDifficulty();
		this.enchantMainHandItem(random, f);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
				this.enchantEquipment(random, f, equipmentSlot);
			}
		}
	}

	protected void enchantMainHandItem(Random random, float power) {
		if (!this.getMainHandStack().isEmpty() && random.nextFloat() < 0.25F * power) {
			this.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(random, this.getMainHandStack(), (int)(5.0F + power * (float)random.nextInt(18)), false));
		}
	}

	protected void enchantEquipment(Random random, float power, EquipmentSlot slot) {
		ItemStack itemStack = this.getEquippedStack(slot);
		if (!itemStack.isEmpty() && random.nextFloat() < 0.5F * power) {
			this.equipStack(slot, EnchantmentHelper.enchant(random, itemStack, (int)(5.0F + power * (float)random.nextInt(18)), false));
		}
	}

	@Nullable
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		Random random = world.getRandom();
		this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)
			.addPersistentModifier(
				new EntityAttributeModifier("Random spawn bonus", random.nextTriangular(0.0, 0.11485000000000001), EntityAttributeModifier.Operation.MULTIPLY_BASE)
			);
		this.setLeftHanded(random.nextFloat() < 0.05F);
		return entityData;
	}

	public void setPersistent() {
		this.persistent = true;
	}

	public void setEquipmentDropChance(EquipmentSlot slot, float chance) {
		switch (slot.getType()) {
			case HAND:
				this.handDropChances[slot.getEntitySlotId()] = chance;
				break;
			case ARMOR:
				this.armorDropChances[slot.getEntitySlotId()] = chance;
				break;
			case BODY:
				this.bodyArmorDropChance = chance;
		}
	}

	public boolean canPickUpLoot() {
		return this.canPickUpLoot;
	}

	public void setCanPickUpLoot(boolean canPickUpLoot) {
		this.canPickUpLoot = canPickUpLoot;
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(stack);
		return this.getEquippedStack(equipmentSlot).isEmpty() && this.canPickUpLoot();
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public final ActionResult interact(PlayerEntity player, Hand hand) {
		if (!this.isAlive()) {
			return ActionResult.PASS;
		} else if (this.getHoldingEntity() == player) {
			this.detachLeash(true, !player.isInCreativeMode());
			this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			ActionResult actionResult = this.interactWithItem(player, hand);
			if (actionResult.isAccepted()) {
				this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
				return actionResult;
			} else {
				actionResult = this.interactMob(player, hand);
				if (actionResult.isAccepted()) {
					this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
					return actionResult;
				} else {
					return super.interact(player, hand);
				}
			}
		}
	}

	private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.LEAD) && this.canBeLeashedBy(player)) {
			this.attachLeash(player, true);
			itemStack.decrement(1);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			if (itemStack.isOf(Items.NAME_TAG)) {
				ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
				if (actionResult.isAccepted()) {
					return actionResult;
				}
			}

			if (itemStack.getItem() instanceof SpawnEggItem) {
				if (this.getWorld() instanceof ServerWorld) {
					SpawnEggItem spawnEggItem = (SpawnEggItem)itemStack.getItem();
					Optional<MobEntity> optional = spawnEggItem.spawnBaby(
						player, this, (EntityType<? extends MobEntity>)this.getType(), (ServerWorld)this.getWorld(), this.getPos(), itemStack
					);
					optional.ifPresent(entity -> this.onPlayerSpawnedChild(player, entity));
					return optional.isPresent() ? ActionResult.SUCCESS : ActionResult.PASS;
				} else {
					return ActionResult.CONSUME;
				}
			} else {
				return ActionResult.PASS;
			}
		}
	}

	protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
	}

	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean isInWalkTargetRange() {
		return this.isInWalkTargetRange(this.getBlockPos());
	}

	public boolean isInWalkTargetRange(BlockPos pos) {
		return this.positionTargetRange == -1.0F ? true : this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
	}

	public void setPositionTarget(BlockPos target, int range) {
		this.positionTarget = target;
		this.positionTargetRange = (float)range;
	}

	public BlockPos getPositionTarget() {
		return this.positionTarget;
	}

	public float getPositionTargetRange() {
		return this.positionTargetRange;
	}

	public void clearPositionTarget() {
		this.positionTargetRange = -1.0F;
	}

	public boolean hasPositionTarget() {
		return this.positionTargetRange != -1.0F;
	}

	/**
	 * Converts this entity to the provided {@code entityType}.
	 * <p>The new entity will keep many of the properties set for this entity,
	 * including its vehicle, its name and whether it is persistent or not.
	 * <p>If {@code keepEquipment} is {@code true}, it will also keep its equipment.
	 * 
	 * @param keepEquipment whether the equipment of this entity should be kept
	 * @param entityType the entity type to convert to
	 */
	@Nullable
	public <T extends MobEntity> T convertTo(EntityType<T> entityType, boolean keepEquipment) {
		if (this.isRemoved()) {
			return null;
		} else {
			T mobEntity = (T)entityType.create(this.getWorld());
			if (mobEntity == null) {
				return null;
			} else {
				mobEntity.copyPositionAndRotation(this);
				mobEntity.setBaby(this.isBaby());
				mobEntity.setAiDisabled(this.isAiDisabled());
				if (this.hasCustomName()) {
					mobEntity.setCustomName(this.getCustomName());
					mobEntity.setCustomNameVisible(this.isCustomNameVisible());
				}

				if (this.isPersistent()) {
					mobEntity.setPersistent();
				}

				mobEntity.setInvulnerable(this.isInvulnerable());
				if (keepEquipment) {
					mobEntity.setCanPickUpLoot(this.canPickUpLoot());

					for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
						ItemStack itemStack = this.getEquippedStack(equipmentSlot);
						if (!itemStack.isEmpty()) {
							mobEntity.equipStack(equipmentSlot, itemStack.copyAndEmpty());
							mobEntity.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
						}
					}
				}

				this.getWorld().spawnEntity(mobEntity);
				if (this.hasVehicle()) {
					Entity entity = this.getVehicle();
					this.stopRiding();
					mobEntity.startRiding(entity, true);
				}

				this.discard();
				return mobEntity;
			}
		}
	}

	protected void updateLeash() {
		if (this.leashNbt != null) {
			this.readLeashNbt();
		}

		if (this.holdingEntity != null) {
			if (!this.isAlive() || !this.holdingEntity.isAlive()) {
				this.detachLeash(true, true);
			}
		}
	}

	public void detachLeash(boolean sendPacket, boolean dropItem) {
		if (this.holdingEntity != null) {
			this.holdingEntity = null;
			this.leashNbt = null;
			if (!this.getWorld().isClient && dropItem) {
				this.dropItem(Items.LEAD);
			}

			if (!this.getWorld().isClient && sendPacket && this.getWorld() instanceof ServerWorld) {
				((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
			}
		}
	}

	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.isLeashed() && !(this instanceof Monster);
	}

	public boolean isLeashed() {
		return this.holdingEntity != null;
	}

	@Nullable
	public Entity getHoldingEntity() {
		if (this.holdingEntity == null && this.holdingEntityId != 0 && this.getWorld().isClient) {
			this.holdingEntity = this.getWorld().getEntityById(this.holdingEntityId);
		}

		return this.holdingEntity;
	}

	public void attachLeash(Entity entity, boolean sendPacket) {
		this.holdingEntity = entity;
		this.leashNbt = null;
		if (!this.getWorld().isClient && sendPacket && this.getWorld() instanceof ServerWorld) {
			((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	public void setHoldingEntityId(int id) {
		this.holdingEntityId = id;
		this.detachLeash(false, false);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		boolean bl = super.startRiding(entity, force);
		if (bl && this.isLeashed()) {
			this.detachLeash(true, true);
		}

		return bl;
	}

	private void readLeashNbt() {
		if (this.leashNbt != null && this.getWorld() instanceof ServerWorld serverWorld) {
			Optional<UUID> optional = this.leashNbt.left();
			Optional<BlockPos> optional2 = this.leashNbt.right();
			if (optional.isPresent()) {
				Entity entity = serverWorld.getEntity((UUID)optional.get());
				if (entity != null) {
					this.attachLeash(entity, true);
					return;
				}
			} else if (optional2.isPresent()) {
				this.attachLeash(LeashKnotEntity.getOrCreate(this.getWorld(), (BlockPos)optional2.get()), true);
				return;
			}

			if (this.age > 100) {
				this.dropItem(Items.LEAD);
				this.leashNbt = null;
			}
		}
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.isAiDisabled();
	}

	public void setAiDisabled(boolean aiDisabled) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, aiDisabled ? (byte)(b | 1) : (byte)(b & -2));
	}

	public void setLeftHanded(boolean leftHanded) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, leftHanded ? (byte)(b | 2) : (byte)(b & -3));
	}

	public void setAttacking(boolean attacking) {
		byte b = this.dataTracker.get(MOB_FLAGS);
		this.dataTracker.set(MOB_FLAGS, attacking ? (byte)(b | 4) : (byte)(b & -5));
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

	public void setBaby(boolean baby) {
	}

	@Override
	public Arm getMainArm() {
		return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
	}

	public boolean isInAttackRange(LivingEntity entity) {
		return this.getAttackBox().intersects(entity.getHitbox());
	}

	/**
	 * Gets the area in which this mob can attack entities whose hitbox intersects it.
	 * 
	 * @see LivingEntity#getHitbox
	 */
	protected Box getAttackBox() {
		Entity entity = this.getVehicle();
		Box box3;
		if (entity != null) {
			Box box = entity.getBoundingBox();
			Box box2 = this.getBoundingBox();
			box3 = new Box(
				Math.min(box2.minX, box.minX), box2.minY, Math.min(box2.minZ, box.minZ), Math.max(box2.maxX, box.maxX), box2.maxY, Math.max(box2.maxZ, box.maxZ)
			);
		} else {
			box3 = this.getBoundingBox();
		}

		return box3.expand(ATTACK_RANGE, 0.0, ATTACK_RANGE);
	}

	@Override
	public boolean tryAttack(Entity target) {
		float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		float g = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
		if (target instanceof LivingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), target.getType());
			g += (float)EnchantmentHelper.getKnockback(this);
		}

		int i = EnchantmentHelper.getFireAspect(this);
		if (i > 0) {
			target.setOnFireFor(i * 4);
		}

		boolean bl = target.damage(this.getDamageSources().mobAttack(this), f);
		if (bl) {
			if (g > 0.0F && target instanceof LivingEntity) {
				((LivingEntity)target)
					.takeKnockback(
						(double)(g * 0.5F),
						(double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
						(double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
					);
				this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
			}

			this.applyDamageEffects(this, target);
			this.onAttacking(target);
		}

		return bl;
	}

	protected boolean isAffectedByDaylight() {
		if (this.getWorld().isDay() && !this.getWorld().isClient) {
			float f = this.getBrightnessAtEyes();
			BlockPos blockPos = BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ());
			boolean bl = this.isWet() || this.inPowderSnow || this.wasInPowderSnow;
			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !bl && this.getWorld().isSkyVisible(blockPos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void swimUpward(TagKey<Fluid> fluid) {
		if (this.getNavigation().canSwim()) {
			super.swimUpward(fluid);
		} else {
			this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
		}
	}

	@VisibleForTesting
	public void clearGoalsAndTasks() {
		this.clearGoals(goal -> true);
		this.getBrain().clear();
	}

	public void clearGoals(Predicate<Goal> predicate) {
		this.goalSelector.clear(predicate);
	}

	@Override
	protected void removeFromDimension() {
		super.removeFromDimension();
		this.detachLeash(true, false);
		this.getEquippedItems().forEach(stack -> {
			if (!stack.isEmpty()) {
				stack.setCount(0);
			}
		});
	}

	@Nullable
	@Override
	public ItemStack getPickBlockStack() {
		SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(this.getType());
		return spawnEggItem == null ? null : new ItemStack(spawnEggItem);
	}
}
