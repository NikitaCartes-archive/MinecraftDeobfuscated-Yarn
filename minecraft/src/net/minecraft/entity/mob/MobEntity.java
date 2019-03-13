package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4209;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.ai.goal.Goals;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
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
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public abstract class MobEntity extends LivingEntity {
	private static final TrackedData<Byte> field_6193 = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
	public int ambientSoundChance;
	protected int experiencePoints;
	protected LookControl field_6206;
	protected MoveControl field_6207;
	protected JumpControl field_6204;
	private final BodyControl field_6188;
	protected EntityNavigation field_6189;
	protected final Goals field_6201;
	protected final Goals field_6185;
	private LivingEntity target;
	private final MobVisibilityCache field_6190;
	private final DefaultedList<ItemStack> field_6195 = DefaultedList.create(2, ItemStack.EMPTY);
	protected final float[] handDropChances = new float[2];
	private final DefaultedList<ItemStack> field_6205 = DefaultedList.create(4, ItemStack.EMPTY);
	protected final float[] armorDropChances = new float[4];
	private boolean pickUpLoot;
	private boolean persistent;
	private final Map<PathNodeType, Float> pathNodeTypeWeights = Maps.newEnumMap(PathNodeType.class);
	private Identifier field_6198;
	private long lootTableSeed;
	@Nullable
	private Entity holdingEntity;
	private int field_18279;
	@Nullable
	private CompoundTag field_6192;
	private BlockPos field_18074 = BlockPos.ORIGIN;
	private float field_18075 = -1.0F;

	protected MobEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		this.field_6201 = new Goals(world != null && world.getProfiler() != null ? world.getProfiler() : null);
		this.field_6185 = new Goals(world != null && world.getProfiler() != null ? world.getProfiler() : null);
		this.field_6206 = new LookControl(this);
		this.field_6207 = new MoveControl(this);
		this.field_6204 = new JumpControl(this);
		this.field_6188 = this.method_5963();
		this.field_6189 = this.method_5965(world);
		this.field_6190 = new MobVisibilityCache(this);
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
		this.method_6127().register(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
		this.method_6127().register(EntityAttributes.ATTACK_KNOCKBACK);
	}

	protected EntityNavigation method_5965(World world) {
		return new EntityMobNavigation(this, world);
	}

	public float method_5944(PathNodeType pathNodeType) {
		Float float_ = (Float)this.pathNodeTypeWeights.get(pathNodeType);
		return float_ == null ? pathNodeType.getWeight() : float_;
	}

	public void method_5941(PathNodeType pathNodeType, float f) {
		this.pathNodeTypeWeights.put(pathNodeType, f);
	}

	protected BodyControl method_5963() {
		return new BodyControl(this);
	}

	public LookControl method_5988() {
		return this.field_6206;
	}

	public MoveControl method_5962() {
		if (this.hasVehicle() && this.getRiddenEntity() instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.getRiddenEntity();
			return mobEntity.method_5962();
		} else {
			return this.field_6207;
		}
	}

	public JumpControl method_5993() {
		return this.field_6204;
	}

	public EntityNavigation method_5942() {
		if (this.hasVehicle() && this.getRiddenEntity() instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.getRiddenEntity();
			return mobEntity.method_5942();
		} else {
			return this.field_6189;
		}
	}

	public MobVisibilityCache method_5985() {
		return this.field_6190;
	}

	@Nullable
	public LivingEntity getTarget() {
		return this.target;
	}

	public void setTarget(@Nullable LivingEntity livingEntity) {
		this.target = livingEntity;
	}

	@Override
	public boolean method_5973(EntityType<?> entityType) {
		return entityType != EntityType.GHAST;
	}

	public void method_5983() {
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6193, (byte)0);
	}

	public int getMinAmbientSoundDelay() {
		return 80;
	}

	public void playAmbientSound() {
		SoundEvent soundEvent = this.method_5994();
		if (soundEvent != null) {
			this.method_5783(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public void updateLogic() {
		super.updateLogic();
		this.field_6002.getProfiler().push("mobBaseTick");
		if (this.isValid() && this.random.nextInt(1000) < this.ambientSoundChance++) {
			this.resetSoundDelay();
			this.playAmbientSound();
		}

		this.field_6002.getProfiler().pop();
	}

	@Override
	protected void method_6013(DamageSource damageSource) {
		this.resetSoundDelay();
		super.method_6013(damageSource);
	}

	private void resetSoundDelay() {
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		if (this.experiencePoints > 0) {
			int i = this.experiencePoints;

			for (int j = 0; j < this.field_6205.size(); j++) {
				if (!this.field_6205.get(j).isEmpty() && this.armorDropChances[j] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			for (int jx = 0; jx < this.field_6195.size(); jx++) {
				if (!this.field_6195.get(jx).isEmpty() && this.handDropChances[jx] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			return i;
		} else {
			return this.experiencePoints;
		}
	}

	public void method_5990() {
		if (this.field_6002.isClient) {
			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				double g = 10.0;
				this.field_6002
					.method_8406(
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
			this.field_6002.summonParticle(this, (byte)20);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 20) {
			this.method_5990();
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.field_6002.isClient) {
			this.method_5995();
			if (this.age % 5 == 0) {
				boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
				boolean bl2 = !(this.getRiddenEntity() instanceof BoatEntity);
				this.field_6201.changeBits(Goal.class_4134.field_18405, bl);
				this.field_6201.changeBits(Goal.class_4134.field_18407, bl && bl2);
				this.field_6201.changeBits(Goal.class_4134.field_18406, bl);
			}
		}
	}

	@Override
	protected float method_6031(float f, float g) {
		this.field_6188.method_6224();
		return g;
	}

	@Nullable
	protected SoundEvent method_5994() {
		return null;
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
		compoundTag.putBoolean("PersistenceRequired", this.persistent);
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.field_6205) {
			CompoundTag compoundTag2 = new CompoundTag();
			if (!itemStack.isEmpty()) {
				itemStack.method_7953(compoundTag2);
			}

			listTag.add(compoundTag2);
		}

		compoundTag.method_10566("ArmorItems", listTag);
		ListTag listTag2 = new ListTag();

		for (ItemStack itemStack2 : this.field_6195) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (!itemStack2.isEmpty()) {
				itemStack2.method_7953(compoundTag3);
			}

			listTag2.add(compoundTag3);
		}

		compoundTag.method_10566("HandItems", listTag2);
		ListTag listTag3 = new ListTag();

		for (float f : this.armorDropChances) {
			listTag3.add(new FloatTag(f));
		}

		compoundTag.method_10566("ArmorDropChances", listTag3);
		ListTag listTag4 = new ListTag();

		for (float g : this.handDropChances) {
			listTag4.add(new FloatTag(g));
		}

		compoundTag.method_10566("HandDropChances", listTag4);
		if (this.holdingEntity != null) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (this.holdingEntity instanceof LivingEntity) {
				UUID uUID = this.holdingEntity.getUuid();
				compoundTag3.putUuid("UUID", uUID);
			} else if (this.holdingEntity instanceof AbstractDecorationEntity) {
				BlockPos blockPos = ((AbstractDecorationEntity)this.holdingEntity).method_6896();
				compoundTag3.putInt("X", blockPos.getX());
				compoundTag3.putInt("Y", blockPos.getY());
				compoundTag3.putInt("Z", blockPos.getZ());
			}

			compoundTag.method_10566("Leash", compoundTag3);
		}

		compoundTag.putBoolean("LeftHanded", this.isLeftHanded());
		if (this.field_6198 != null) {
			compoundTag.putString("DeathLootTable", this.field_6198.toString());
			if (this.lootTableSeed != 0L) {
				compoundTag.putLong("DeathLootTableSeed", this.lootTableSeed);
			}
		}

		if (this.isAiDisabled()) {
			compoundTag.putBoolean("NoAI", this.isAiDisabled());
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("CanPickUpLoot", 1)) {
			this.setCanPickUpLoot(compoundTag.getBoolean("CanPickUpLoot"));
		}

		this.persistent = compoundTag.getBoolean("PersistenceRequired");
		if (compoundTag.containsKey("ArmorItems", 9)) {
			ListTag listTag = compoundTag.method_10554("ArmorItems", 10);

			for (int i = 0; i < this.field_6205.size(); i++) {
				this.field_6205.set(i, ItemStack.method_7915(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("HandItems", 9)) {
			ListTag listTag = compoundTag.method_10554("HandItems", 10);

			for (int i = 0; i < this.field_6195.size(); i++) {
				this.field_6195.set(i, ItemStack.method_7915(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("ArmorDropChances", 9)) {
			ListTag listTag = compoundTag.method_10554("ArmorDropChances", 5);

			for (int i = 0; i < listTag.size(); i++) {
				this.armorDropChances[i] = listTag.getFloat(i);
			}
		}

		if (compoundTag.containsKey("HandDropChances", 9)) {
			ListTag listTag = compoundTag.method_10554("HandDropChances", 5);

			for (int i = 0; i < listTag.size(); i++) {
				this.handDropChances[i] = listTag.getFloat(i);
			}
		}

		if (compoundTag.containsKey("Leash", 10)) {
			this.field_6192 = compoundTag.getCompound("Leash");
		}

		this.setLeftHanded(compoundTag.getBoolean("LeftHanded"));
		if (compoundTag.containsKey("DeathLootTable", 8)) {
			this.field_6198 = new Identifier(compoundTag.getString("DeathLootTable"));
			this.lootTableSeed = compoundTag.getLong("DeathLootTableSeed");
		}

		this.setAiDisabled(compoundTag.getBoolean("NoAI"));
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean bl) {
		super.dropLoot(damageSource, bl);
		this.field_6198 = null;
	}

	@Override
	protected LootContext.Builder method_16079(boolean bl, DamageSource damageSource) {
		return super.method_16079(bl, damageSource).setRandom(this.lootTableSeed, this.random);
	}

	@Override
	public final Identifier method_5989() {
		return this.field_6198 == null ? this.method_5991() : this.field_6198;
	}

	protected Identifier method_5991() {
		return super.method_5989();
	}

	public void method_5930(float f) {
		this.movementInputForward = f;
	}

	public void method_5976(float f) {
		this.movementInputUp = f;
	}

	public void method_5938(float f) {
		this.movementInputSideways = f;
	}

	@Override
	public void setMovementSpeed(float f) {
		super.setMovementSpeed(f);
		this.method_5930(f);
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		this.field_6002.getProfiler().push("looting");
		if (!this.field_6002.isClient && this.canPickUpLoot() && this.isValid() && !this.dead && this.field_6002.getGameRules().getBoolean("mobGriefing")) {
			for (ItemEntity itemEntity : this.field_6002.method_18467(ItemEntity.class, this.method_5829().expand(1.0, 0.0, 1.0))) {
				if (!itemEntity.invalid && !itemEntity.method_6983().isEmpty() && !itemEntity.cannotPickup()) {
					this.method_5949(itemEntity);
				}
			}
		}

		this.field_6002.getProfiler().pop();
	}

	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.method_6983();
		EquipmentSlot equipmentSlot = method_5953(itemStack);
		ItemStack itemStack2 = this.method_6118(equipmentSlot);
		boolean bl = this.method_5955(itemStack, itemStack2, equipmentSlot);
		if (bl && this.method_5939(itemStack)) {
			double d = (double)this.method_5929(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.method_5775(itemStack2);
			}

			this.method_5673(equipmentSlot, itemStack);
			switch (equipmentSlot.getType()) {
				case HAND:
					this.handDropChances[equipmentSlot.getEntitySlotId()] = 2.0F;
					break;
				case ARMOR:
					this.armorDropChances[equipmentSlot.getEntitySlotId()] = 2.0F;
			}

			this.persistent = true;
			this.pickUpEntity(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
		}
	}

	protected boolean method_5955(ItemStack itemStack, ItemStack itemStack2, EquipmentSlot equipmentSlot) {
		boolean bl = true;
		if (!itemStack2.isEmpty()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
				if (itemStack.getItem() instanceof SwordItem && !(itemStack2.getItem() instanceof SwordItem)) {
					bl = true;
				} else if (itemStack.getItem() instanceof SwordItem && itemStack2.getItem() instanceof SwordItem) {
					SwordItem swordItem = (SwordItem)itemStack.getItem();
					SwordItem swordItem2 = (SwordItem)itemStack2.getItem();
					if (swordItem.getWeaponDamage() == swordItem2.getWeaponDamage()) {
						bl = itemStack.getDamage() < itemStack2.getDamage() || itemStack.hasTag() && !itemStack2.hasTag();
					} else {
						bl = swordItem.getWeaponDamage() > swordItem2.getWeaponDamage();
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

	protected boolean method_5939(ItemStack itemStack) {
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
			Entity entity = this.field_6002.method_18460(this, -1.0);
			if (entity != null) {
				double d = entity.squaredDistanceTo(this);
				if (d > 16384.0 && this.canImmediatelyDespawn(d)) {
					this.invalidate();
				}

				if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > 1024.0 && this.canImmediatelyDespawn(d)) {
					this.invalidate();
				} else if (d < 1024.0) {
					this.despawnCounter = 0;
				}
			}
		} else {
			this.despawnCounter = 0;
		}
	}

	@Override
	protected final void method_6023() {
		this.despawnCounter++;
		this.field_6002.getProfiler().push("checkDespawn");
		this.checkDespawn();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("sensing");
		this.field_6190.clear();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("targetSelector");
		this.field_6185.tick();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("goalSelector");
		this.field_6201.tick();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("navigation");
		this.field_6189.tick();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("mob tick");
		this.mobTick();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("controls");
		this.field_6002.getProfiler().push("move");
		this.field_6207.tick();
		this.field_6002.getProfiler().swap("look");
		this.field_6206.tick();
		this.field_6002.getProfiler().swap("jump");
		this.field_6204.tick();
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().pop();
		this.method_18409();
	}

	protected void method_18409() {
		class_4209.method_19469(this.field_6002, this, this.field_6201);
	}

	protected void mobTick() {
	}

	public int method_5978() {
		return 40;
	}

	public int method_5986() {
		return 75;
	}

	public void method_5951(Entity entity, float f, float g) {
		double d = entity.x - this.x;
		double e = entity.z - this.z;
		double h;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			h = livingEntity.y + (double)livingEntity.getStandingEyeHeight() - (this.y + (double)this.getStandingEyeHeight());
		} else {
			h = (entity.method_5829().minY + entity.method_5829().maxY) / 2.0 - (this.y + (double)this.getStandingEyeHeight());
		}

		double i = (double)MathHelper.sqrt(d * d + e * e);
		float j = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float k = (float)(-(MathHelper.atan2(h, i) * 180.0F / (float)Math.PI));
		this.pitch = this.method_5960(this.pitch, k, g);
		this.yaw = this.method_5960(this.yaw, j, f);
	}

	private float method_5960(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return iWorld.method_8320(new BlockPos(this).down()).allowsSpawning(this);
	}

	public boolean method_5957(ViewableWorld viewableWorld) {
		return !viewableWorld.method_8599(this.method_5829()) && viewableWorld.method_8606(this);
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
			i -= (3 - this.field_6002.getDifficulty().getId()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.field_6195;
	}

	@Override
	public Iterable<ItemStack> getItemsArmor() {
		return this.field_6205;
	}

	@Override
	public ItemStack method_6118(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot.getType()) {
			case HAND:
				return this.field_6195.get(equipmentSlot.getEntitySlotId());
			case ARMOR:
				return this.field_6205.get(equipmentSlot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		switch (equipmentSlot.getType()) {
			case HAND:
				this.field_6195.set(equipmentSlot.getEntitySlotId(), itemStack);
				break;
			case ARMOR:
				this.field_6205.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.method_6118(equipmentSlot);
			float f = this.method_5929(equipmentSlot);
			boolean bl2 = f > 1.0F;
			if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (bl || bl2) && this.random.nextFloat() - (float)i * 0.01F < f) {
				if (!bl2 && itemStack.hasDurability()) {
					itemStack.setDamage(itemStack.getDurability() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getDurability() - 3, 1))));
				}

				this.method_5775(itemStack);
			}
		}
	}

	protected float method_5929(EquipmentSlot equipmentSlot) {
		float f;
		switch (equipmentSlot.getType()) {
			case HAND:
				f = this.handDropChances[equipmentSlot.getEntitySlotId()];
				break;
			case ARMOR:
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
			float f = this.field_6002.getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
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
				if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
					ItemStack itemStack = this.method_6118(equipmentSlot);
					if (!bl && this.random.nextFloat() < f) {
						break;
					}

					bl = false;
					if (itemStack.isEmpty()) {
						Item item = method_5948(equipmentSlot, i);
						if (item != null) {
							this.method_5673(equipmentSlot, new ItemStack(item));
						}
					}
				}
			}
		}
	}

	public static EquipmentSlot method_5953(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item != Blocks.field_10147.getItem() && (!(item instanceof BlockItem) || !(((BlockItem)item).method_7711() instanceof AbstractSkullBlock))) {
			if (item instanceof ArmorItem) {
				return ((ArmorItem)item).getSlotType();
			} else if (item == Items.field_8833) {
				return EquipmentSlot.CHEST;
			} else {
				return item == Items.field_8255 ? EquipmentSlot.HAND_OFF : EquipmentSlot.HAND_MAIN;
			}
		} else {
			return EquipmentSlot.HEAD;
		}
	}

	@Nullable
	public static Item method_5948(EquipmentSlot equipmentSlot, int i) {
		switch (equipmentSlot) {
			case HEAD:
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
			case CHEST:
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
			case LEGS:
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
			case FEET:
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

	protected void method_5984(LocalDifficulty localDifficulty) {
		float f = localDifficulty.getClampedLocalDifficulty();
		if (!this.method_6047().isEmpty() && this.random.nextFloat() < 0.25F * f) {
			this.method_5673(
				EquipmentSlot.HAND_MAIN, EnchantmentHelper.enchant(this.random, this.method_6047(), (int)(5.0F + f * (float)this.random.nextInt(18)), false)
			);
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack itemStack = this.method_6118(equipmentSlot);
				if (!itemStack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
					this.method_5673(equipmentSlot, EnchantmentHelper.enchant(this.random, itemStack, (int)(5.0F + f * (float)this.random.nextInt(18)), false));
				}
			}
		}
	}

	@Nullable
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.method_5996(EntityAttributes.FOLLOW_RANGE)
			.method_6197(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.field_6330));
		if (this.random.nextFloat() < 0.05F) {
			this.setLeftHanded(true);
		} else {
			this.setLeftHanded(false);
		}

		return entityData;
	}

	public boolean method_5956() {
		return false;
	}

	public void setPersistent() {
		this.persistent = true;
	}

	public void setEquipmentDropChance(EquipmentSlot equipmentSlot, float f) {
		switch (equipmentSlot.getType()) {
			case HAND:
				this.handDropChances[equipmentSlot.getEntitySlotId()] = f;
				break;
			case ARMOR:
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
	public boolean method_18397(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = method_5953(itemStack);
		return this.method_6118(equipmentSlot).isEmpty() && this.canPickUpLoot();
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public final boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		if (!this.isValid()) {
			return false;
		} else if (this.getHoldingEntity() == playerEntity) {
			this.detachLeash(true, !playerEntity.abilities.creativeMode);
			return true;
		} else {
			ItemStack itemStack = playerEntity.method_5998(hand);
			if (itemStack.getItem() == Items.field_8719 && this.method_5931(playerEntity)) {
				this.attachLeash(playerEntity, true);
				itemStack.subtractAmount(1);
				return true;
			} else {
				return this.method_5992(playerEntity, hand) ? true : super.method_5688(playerEntity, hand);
			}
		}
	}

	protected boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	public boolean method_18411() {
		return this.method_18407(new BlockPos(this));
	}

	public boolean method_18407(BlockPos blockPos) {
		return this.field_18075 == -1.0F ? true : this.field_18074.squaredDistanceTo(blockPos) < (double)(this.field_18075 * this.field_18075);
	}

	public void method_18408(BlockPos blockPos, int i) {
		this.field_18074 = blockPos;
		this.field_18075 = (float)i;
	}

	public BlockPos method_18412() {
		return this.field_18074;
	}

	public float method_18413() {
		return this.field_18075;
	}

	public boolean method_18410() {
		return this.field_18075 != -1.0F;
	}

	protected void method_5995() {
		if (this.field_6192 != null) {
			this.deserializeLeashTag();
		}

		if (this.holdingEntity != null) {
			if (!this.isValid() || !this.holdingEntity.isValid()) {
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
			if (!this.field_6002.isClient && bl2) {
				this.method_5706(Items.field_8719);
			}

			if (!this.field_6002.isClient && bl && this.field_6002 instanceof ServerWorld) {
				((ServerWorld)this.field_6002).method_14178().method_18754(this, new EntityAttachS2CPacket(this, null));
			}
		}
	}

	public boolean method_5931(PlayerEntity playerEntity) {
		return !this.isLeashed() && !(this instanceof Monster);
	}

	public boolean isLeashed() {
		return this.holdingEntity != null;
	}

	@Nullable
	public Entity getHoldingEntity() {
		if (this.holdingEntity == null && this.field_18279 != 0 && this.field_6002.isClient) {
			this.holdingEntity = this.field_6002.getEntityById(this.field_18279);
		}

		return this.holdingEntity;
	}

	public void attachLeash(Entity entity, boolean bl) {
		this.holdingEntity = entity;
		this.teleporting = true;
		if (!(this.holdingEntity instanceof PlayerEntity)) {
			this.holdingEntity.teleporting = true;
		}

		if (!this.field_6002.isClient && bl && this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002).method_14178().method_18754(this, new EntityAttachS2CPacket(this, this.holdingEntity));
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_18810(int i) {
		this.field_18279 = i;
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
		if (this.field_6192 != null && this.field_6002 instanceof ServerWorld) {
			if (this.field_6192.hasUuid("UUID")) {
				UUID uUID = this.field_6192.getUuid("UUID");
				Entity entity = ((ServerWorld)this.field_6002).getEntity(uUID);
				if (entity != null) {
					this.attachLeash(entity, true);
				}
			} else if (this.field_6192.containsKey("X", 99) && this.field_6192.containsKey("Y", 99) && this.field_6192.containsKey("Z", 99)) {
				BlockPos blockPos = new BlockPos(this.field_6192.getInt("X"), this.field_6192.getInt("Y"), this.field_6192.getInt("Z"));
				this.attachLeash(LeadKnotEntity.method_6932(this.field_6002, blockPos), true);
			} else {
				this.detachLeash(false, true);
			}

			this.field_6192 = null;
		}
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		EquipmentSlot equipmentSlot;
		if (i == 98) {
			equipmentSlot = EquipmentSlot.HAND_MAIN;
		} else if (i == 99) {
			equipmentSlot = EquipmentSlot.HAND_OFF;
		} else if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else {
			if (i != 100 + EquipmentSlot.FEET.getEntitySlotId()) {
				return false;
			}

			equipmentSlot = EquipmentSlot.FEET;
		}

		if (!itemStack.isEmpty() && !method_5935(equipmentSlot, itemStack) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.method_5673(equipmentSlot, itemStack);
			return true;
		}
	}

	@Override
	public boolean method_5787() {
		return this.method_5956() && super.method_5787();
	}

	public static boolean method_5935(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		EquipmentSlot equipmentSlot2 = method_5953(itemStack);
		return equipmentSlot2 == equipmentSlot
			|| equipmentSlot2 == EquipmentSlot.HAND_MAIN && equipmentSlot == EquipmentSlot.HAND_OFF
			|| equipmentSlot2 == EquipmentSlot.HAND_OFF && equipmentSlot == EquipmentSlot.HAND_MAIN;
	}

	@Override
	public boolean method_6034() {
		return super.method_6034() && !this.isAiDisabled();
	}

	public void setAiDisabled(boolean bl) {
		byte b = this.field_6011.get(field_6193);
		this.field_6011.set(field_6193, bl ? (byte)(b | 1) : (byte)(b & -2));
	}

	public void setLeftHanded(boolean bl) {
		byte b = this.field_6011.get(field_6193);
		this.field_6011.set(field_6193, bl ? (byte)(b | 2) : (byte)(b & -3));
	}

	public boolean isAiDisabled() {
		return (this.field_6011.get(field_6193) & 1) != 0;
	}

	public boolean isLeftHanded() {
		return (this.field_6011.get(field_6193) & 2) != 0;
	}

	@Override
	public OptionMainHand getMainHand() {
		return this.isLeftHanded() ? OptionMainHand.field_6182 : OptionMainHand.field_6183;
	}

	@Override
	public boolean method_18395(LivingEntity livingEntity) {
		return livingEntity.method_5864() == EntityType.PLAYER && ((PlayerEntity)livingEntity).abilities.invulnerable ? false : super.method_18395(livingEntity);
	}

	@Override
	public boolean attack(Entity entity) {
		float f = (float)this.method_5996(EntityAttributes.ATTACK_DAMAGE).getValue();
		float g = (float)this.method_5996(EntityAttributes.ATTACK_KNOCKBACK).getValue();
		if (entity instanceof LivingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.method_6047(), ((LivingEntity)entity).method_6046());
			g += (float)EnchantmentHelper.getKnockback(this);
		}

		int i = EnchantmentHelper.getFireAspect(this);
		if (i > 0) {
			entity.setOnFireFor(i * 4);
		}

		boolean bl = entity.damage(DamageSource.method_5511(this), f);
		if (bl) {
			if (g > 0.0F && entity instanceof LivingEntity) {
				((LivingEntity)entity)
					.method_6005(this, g * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0))));
				this.method_18799(this.method_18798().multiply(0.6, 1.0, 0.6));
			}

			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				ItemStack itemStack = this.method_6047();
				ItemStack itemStack2 = playerEntity.isUsingItem() ? playerEntity.method_6030() : ItemStack.EMPTY;
				if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.getItem() == Items.field_8255) {
					float h = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
					if (this.random.nextFloat() < h) {
						playerEntity.method_7357().set(Items.field_8255, 100);
						this.field_6002.summonParticle(playerEntity, (byte)30);
					}
				}
			}

			this.method_5723(this, entity);
		}

		return bl;
	}

	protected boolean method_5972() {
		if (this.field_6002.isDaylight() && !this.field_6002.isClient) {
			float f = this.method_5718();
			BlockPos blockPos = this.getRiddenEntity() instanceof BoatEntity
				? new BlockPos(this.x, (double)Math.round(this.y), this.z).up()
				: new BlockPos(this.x, (double)Math.round(this.y), this.z);
			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.field_6002.method_8311(blockPos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void method_6010(Tag<Fluid> tag) {
		if (this.method_5942().canSwim()) {
			super.method_6010(tag);
		} else {
			this.method_18799(this.method_18798().add(0.0, 0.3, 0.0));
		}
	}

	public boolean method_18809(Item item) {
		return this.method_6047().getItem() == item || this.method_6079().getItem() == item;
	}
}
