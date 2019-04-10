package net.minecraft.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEntity extends LivingEntity {
	private static final Rotation DEFAULT_HEAD_ROTATION = new Rotation(0.0F, 0.0F, 0.0F);
	private static final Rotation DEFAULT_BODY_ROTATION = new Rotation(0.0F, 0.0F, 0.0F);
	private static final Rotation DEFAULT_LEFT_ARM_ROTATION = new Rotation(-10.0F, 0.0F, -10.0F);
	private static final Rotation DEFAULT_RIGHT_ARM_ROTATION = new Rotation(-15.0F, 0.0F, 10.0F);
	private static final Rotation DEFAULT_LEFT_LEG_ROTATION = new Rotation(-1.0F, 0.0F, -1.0F);
	private static final Rotation DEFAULT_RIGHT_LEG_ROTATION = new Rotation(1.0F, 0.0F, 1.0F);
	public static final TrackedData<Byte> ARMOR_STAND_FLAGS = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Rotation> TRACKER_HEAD_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> TRACKER_BODY_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> TRACKER_LEFT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> TRACKER_RIGHT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> TRACKER_LEFT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> TRACKER_RIGHT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE = entity -> entity instanceof AbstractMinecartEntity
			&& ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.field_7674;
	private final DefaultedList<ItemStack> heldItems = DefaultedList.create(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> armorItems = DefaultedList.create(4, ItemStack.EMPTY);
	private boolean field_7111;
	public long field_7112;
	private int disabledSlots;
	private Rotation headRotation = DEFAULT_HEAD_ROTATION;
	private Rotation bodyRotation = DEFAULT_BODY_ROTATION;
	private Rotation leftArmRotation = DEFAULT_LEFT_ARM_ROTATION;
	private Rotation rightArmRotation = DEFAULT_RIGHT_ARM_ROTATION;
	private Rotation leftLegRotation = DEFAULT_LEFT_LEG_ROTATION;
	private Rotation rightLegRotation = DEFAULT_RIGHT_LEG_ROTATION;

	public ArmorStandEntity(EntityType<? extends ArmorStandEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 0.0F;
	}

	public ArmorStandEntity(World world, double d, double e, double f) {
		this(EntityType.ARMOR_STAND, world);
		this.setPosition(d, e, f);
	}

	@Override
	public void refreshSize() {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		super.refreshSize();
		this.setPosition(d, e, f);
	}

	private boolean canClip() {
		return !this.isMarker() && !this.isUnaffectedByGravity();
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && this.canClip();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ARMOR_STAND_FLAGS, (byte)0);
		this.dataTracker.startTracking(TRACKER_HEAD_ROTATION, DEFAULT_HEAD_ROTATION);
		this.dataTracker.startTracking(TRACKER_BODY_ROTATION, DEFAULT_BODY_ROTATION);
		this.dataTracker.startTracking(TRACKER_LEFT_ARM_ROTATION, DEFAULT_LEFT_ARM_ROTATION);
		this.dataTracker.startTracking(TRACKER_RIGHT_ARM_ROTATION, DEFAULT_RIGHT_ARM_ROTATION);
		this.dataTracker.startTracking(TRACKER_LEFT_LEG_ROTATION, DEFAULT_LEFT_LEG_ROTATION);
		this.dataTracker.startTracking(TRACKER_RIGHT_LEG_ROTATION, DEFAULT_RIGHT_LEG_ROTATION);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.heldItems;
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.armorItems;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot.getType()) {
			case HAND:
				return this.heldItems.get(equipmentSlot.getEntitySlotId());
			case ARMOR:
				return this.armorItems.get(equipmentSlot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		switch (equipmentSlot.getType()) {
			case HAND:
				this.onEquipStack(itemStack);
				this.heldItems.set(equipmentSlot.getEntitySlotId(), itemStack);
				break;
			case ARMOR:
				this.onEquipStack(itemStack);
				this.armorItems.set(equipmentSlot.getEntitySlotId(), itemStack);
		}
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
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

		if (!itemStack.isEmpty() && !MobEntity.canEquipmentSlotContain(equipmentSlot, itemStack) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.setEquippedStack(equipmentSlot, itemStack);
			return true;
		}
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return this.getEquippedStack(equipmentSlot).isEmpty() && !this.method_6915(equipmentSlot);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
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

		for (ItemStack itemStack2 : this.heldItems) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (!itemStack2.isEmpty()) {
				itemStack2.toTag(compoundTag3);
			}

			listTag2.add(compoundTag3);
		}

		compoundTag.put("HandItems", listTag2);
		compoundTag.putBoolean("Invisible", this.isInvisible());
		compoundTag.putBoolean("Small", this.isSmall());
		compoundTag.putBoolean("ShowArms", this.shouldShowArms());
		compoundTag.putInt("DisabledSlots", this.disabledSlots);
		compoundTag.putBoolean("NoBasePlate", this.shouldHideBasePlate());
		if (this.isMarker()) {
			compoundTag.putBoolean("Marker", this.isMarker());
		}

		compoundTag.put("Pose", this.serializePose());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("ArmorItems", 9)) {
			ListTag listTag = compoundTag.getList("ArmorItems", 10);

			for (int i = 0; i < this.armorItems.size(); i++) {
				this.armorItems.set(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("HandItems", 9)) {
			ListTag listTag = compoundTag.getList("HandItems", 10);

			for (int i = 0; i < this.heldItems.size(); i++) {
				this.heldItems.set(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
			}
		}

		this.setInvisible(compoundTag.getBoolean("Invisible"));
		this.setSmall(compoundTag.getBoolean("Small"));
		this.setShowArms(compoundTag.getBoolean("ShowArms"));
		this.disabledSlots = compoundTag.getInt("DisabledSlots");
		this.setHideBasePlate(compoundTag.getBoolean("NoBasePlate"));
		this.setMarker(compoundTag.getBoolean("Marker"));
		this.noClip = !this.canClip();
		CompoundTag compoundTag2 = compoundTag.getCompound("Pose");
		this.deserializePose(compoundTag2);
	}

	private void deserializePose(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Head", 5);
		this.setHeadRotation(listTag.isEmpty() ? DEFAULT_HEAD_ROTATION : new Rotation(listTag));
		ListTag listTag2 = compoundTag.getList("Body", 5);
		this.setBodyRotation(listTag2.isEmpty() ? DEFAULT_BODY_ROTATION : new Rotation(listTag2));
		ListTag listTag3 = compoundTag.getList("LeftArm", 5);
		this.setLeftArmRotation(listTag3.isEmpty() ? DEFAULT_LEFT_ARM_ROTATION : new Rotation(listTag3));
		ListTag listTag4 = compoundTag.getList("RightArm", 5);
		this.setRightArmRotation(listTag4.isEmpty() ? DEFAULT_RIGHT_ARM_ROTATION : new Rotation(listTag4));
		ListTag listTag5 = compoundTag.getList("LeftLeg", 5);
		this.setLeftLegRotation(listTag5.isEmpty() ? DEFAULT_LEFT_LEG_ROTATION : new Rotation(listTag5));
		ListTag listTag6 = compoundTag.getList("RightLeg", 5);
		this.setRightLegRotation(listTag6.isEmpty() ? DEFAULT_RIGHT_LEG_ROTATION : new Rotation(listTag6));
	}

	private CompoundTag serializePose() {
		CompoundTag compoundTag = new CompoundTag();
		if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
			compoundTag.put("Head", this.headRotation.serialize());
		}

		if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
			compoundTag.put("Body", this.bodyRotation.serialize());
		}

		if (!DEFAULT_LEFT_ARM_ROTATION.equals(this.leftArmRotation)) {
			compoundTag.put("LeftArm", this.leftArmRotation.serialize());
		}

		if (!DEFAULT_RIGHT_ARM_ROTATION.equals(this.rightArmRotation)) {
			compoundTag.put("RightArm", this.rightArmRotation.serialize());
		}

		if (!DEFAULT_LEFT_LEG_ROTATION.equals(this.leftLegRotation)) {
			compoundTag.put("LeftLeg", this.leftLegRotation.serialize());
		}

		if (!DEFAULT_RIGHT_LEG_ROTATION.equals(this.rightLegRotation)) {
			compoundTag.put("RightLeg", this.rightLegRotation.serialize());
		}

		return compoundTag;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	protected void doPushLogic() {
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), RIDEABLE_MINECART_PREDICATE);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (this.squaredDistanceTo(entity) <= 0.2) {
				entity.pushAwayFrom(this);
			}
		}
	}

	@Override
	public ActionResult interactAt(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (this.isMarker() || itemStack.getItem() == Items.field_8448) {
			return ActionResult.PASS;
		} else if (!this.world.isClient && !playerEntity.isSpectator()) {
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
			if (itemStack.isEmpty()) {
				EquipmentSlot equipmentSlot2 = this.method_6916(vec3d);
				EquipmentSlot equipmentSlot3 = this.method_6915(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
				if (this.isEquippedStackValid(equipmentSlot3)) {
					this.method_6904(playerEntity, equipmentSlot3, itemStack, hand);
				}
			} else {
				if (this.method_6915(equipmentSlot)) {
					return ActionResult.field_5814;
				}

				if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
					return ActionResult.field_5814;
				}

				this.method_6904(playerEntity, equipmentSlot, itemStack, hand);
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.field_5812;
		}
	}

	protected EquipmentSlot method_6916(Vec3d vec3d) {
		EquipmentSlot equipmentSlot = EquipmentSlot.HAND_MAIN;
		boolean bl = this.isSmall();
		double d = bl ? vec3d.y * 2.0 : vec3d.y;
		EquipmentSlot equipmentSlot2 = EquipmentSlot.FEET;
		if (d >= 0.1 && d < 0.1 + (bl ? 0.8 : 0.45) && this.isEquippedStackValid(equipmentSlot2)) {
			equipmentSlot = EquipmentSlot.FEET;
		} else if (d >= 0.9 + (bl ? 0.3 : 0.0) && d < 0.9 + (bl ? 1.0 : 0.7) && this.isEquippedStackValid(EquipmentSlot.CHEST)) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (d >= 0.4 && d < 0.4 + (bl ? 1.0 : 0.8) && this.isEquippedStackValid(EquipmentSlot.LEGS)) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else if (d >= 1.6 && this.isEquippedStackValid(EquipmentSlot.HEAD)) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (!this.isEquippedStackValid(EquipmentSlot.HAND_MAIN) && this.isEquippedStackValid(EquipmentSlot.HAND_OFF)) {
			equipmentSlot = EquipmentSlot.HAND_OFF;
		}

		return equipmentSlot;
	}

	public boolean method_6915(EquipmentSlot equipmentSlot) {
		return (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId()) != 0 || equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms();
	}

	private void method_6904(PlayerEntity playerEntity, EquipmentSlot equipmentSlot, ItemStack itemStack, Hand hand) {
		ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 8) == 0) {
			if (!itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 16) == 0) {
				if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
					ItemStack itemStack3 = itemStack.copy();
					itemStack3.setAmount(1);
					this.setEquippedStack(equipmentSlot, itemStack3);
				} else if (itemStack.isEmpty() || itemStack.getAmount() <= 1) {
					this.setEquippedStack(equipmentSlot, itemStack);
					playerEntity.setStackInHand(hand, itemStack2);
				} else if (itemStack2.isEmpty()) {
					ItemStack itemStack3 = itemStack.copy();
					itemStack3.setAmount(1);
					this.setEquippedStack(equipmentSlot, itemStack3);
					itemStack.subtractAmount(1);
				}
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.world.isClient || this.removed) {
			return false;
		} else if (DamageSource.OUT_OF_WORLD.equals(damageSource)) {
			this.remove();
			return false;
		} else if (this.isInvulnerableTo(damageSource) || this.field_7111 || this.isMarker()) {
			return false;
		} else if (damageSource.isExplosive()) {
			this.method_6908(damageSource);
			this.remove();
			return false;
		} else if (DamageSource.IN_FIRE.equals(damageSource)) {
			if (this.isOnFire()) {
				this.method_6905(damageSource, 0.15F);
			} else {
				this.setOnFireFor(5);
			}

			return false;
		} else if (DamageSource.ON_FIRE.equals(damageSource) && this.getHealth() > 0.5F) {
			this.method_6905(damageSource, 4.0F);
			return false;
		} else {
			boolean bl = damageSource.getSource() instanceof ProjectileEntity;
			boolean bl2 = bl && ((ProjectileEntity)damageSource.getSource()).getPierceLevel() > 0;
			boolean bl3 = "player".equals(damageSource.getName());
			if (!bl3 && !bl) {
				return false;
			} else if (damageSource.getAttacker() instanceof PlayerEntity && !((PlayerEntity)damageSource.getAttacker()).abilities.allowModifyWorld) {
				return false;
			} else if (damageSource.isSourceCreativePlayer()) {
				this.method_6920();
				this.method_6898();
				this.remove();
				return bl2;
			} else {
				long l = this.world.getTime();
				if (l - this.field_7112 > 5L && !bl) {
					this.world.sendEntityStatus(this, (byte)32);
					this.field_7112 = l;
				} else {
					this.method_6924(damageSource);
					this.method_6898();
					this.remove();
				}

				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 32) {
			if (this.world.isClient) {
				this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14897, this.getSoundCategory(), 0.3F, 1.0F, false);
				this.field_7112 = this.world.getTime();
			}
		} else {
			super.handleStatus(b);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 4.0;
		if (Double.isNaN(e) || e == 0.0) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	private void method_6898() {
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world)
				.spawnParticles(
					new BlockStateParticleParameters(ParticleTypes.field_11217, Blocks.field_10161.getDefaultState()),
					this.x,
					this.y + (double)this.getHeight() / 1.5,
					this.z,
					10,
					(double)(this.getWidth() / 4.0F),
					(double)(this.getHeight() / 4.0F),
					(double)(this.getWidth() / 4.0F),
					0.05
				);
		}
	}

	private void method_6905(DamageSource damageSource, float f) {
		float g = this.getHealth();
		g -= f;
		if (g <= 0.5F) {
			this.method_6908(damageSource);
			this.remove();
		} else {
			this.setHealth(g);
		}
	}

	private void method_6924(DamageSource damageSource) {
		Block.dropStack(this.world, new BlockPos(this), new ItemStack(Items.field_8694));
		this.method_6908(damageSource);
	}

	private void method_6908(DamageSource damageSource) {
		this.method_6920();
		this.drop(damageSource);

		for (int i = 0; i < this.heldItems.size(); i++) {
			ItemStack itemStack = this.heldItems.get(i);
			if (!itemStack.isEmpty()) {
				Block.dropStack(this.world, new BlockPos(this).up(), itemStack);
				this.heldItems.set(i, ItemStack.EMPTY);
			}
		}

		for (int ix = 0; ix < this.armorItems.size(); ix++) {
			ItemStack itemStack = this.armorItems.get(ix);
			if (!itemStack.isEmpty()) {
				Block.dropStack(this.world, new BlockPos(this).up(), itemStack);
				this.armorItems.set(ix, ItemStack.EMPTY);
			}
		}
	}

	private void method_6920() {
		this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_15118, this.getSoundCategory(), 1.0F, 1.0F);
	}

	@Override
	protected float method_6031(float f, float g) {
		this.field_6220 = this.prevYaw;
		this.field_6283 = this.yaw;
		return 0.0F;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * (this.isChild() ? 0.5F : 0.9F);
	}

	@Override
	public double getHeightOffset() {
		return this.isMarker() ? 0.0 : 0.1F;
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.canClip()) {
			super.travel(vec3d);
		}
	}

	@Override
	public void setYaw(float f) {
		this.field_6220 = this.prevYaw = f;
		this.prevHeadYaw = this.headYaw = f;
	}

	@Override
	public void setHeadYaw(float f) {
		this.field_6220 = this.prevYaw = f;
		this.prevHeadYaw = this.headYaw = f;
	}

	@Override
	public void tick() {
		super.tick();
		Rotation rotation = this.dataTracker.get(TRACKER_HEAD_ROTATION);
		if (!this.headRotation.equals(rotation)) {
			this.setHeadRotation(rotation);
		}

		Rotation rotation2 = this.dataTracker.get(TRACKER_BODY_ROTATION);
		if (!this.bodyRotation.equals(rotation2)) {
			this.setBodyRotation(rotation2);
		}

		Rotation rotation3 = this.dataTracker.get(TRACKER_LEFT_ARM_ROTATION);
		if (!this.leftArmRotation.equals(rotation3)) {
			this.setLeftArmRotation(rotation3);
		}

		Rotation rotation4 = this.dataTracker.get(TRACKER_RIGHT_ARM_ROTATION);
		if (!this.rightArmRotation.equals(rotation4)) {
			this.setRightArmRotation(rotation4);
		}

		Rotation rotation5 = this.dataTracker.get(TRACKER_LEFT_LEG_ROTATION);
		if (!this.leftLegRotation.equals(rotation5)) {
			this.setLeftLegRotation(rotation5);
		}

		Rotation rotation6 = this.dataTracker.get(TRACKER_RIGHT_LEG_ROTATION);
		if (!this.rightLegRotation.equals(rotation6)) {
			this.setRightLegRotation(rotation6);
		}
	}

	@Override
	protected void updatePotionVisibility() {
		this.setInvisible(this.field_7111);
	}

	@Override
	public void setInvisible(boolean bl) {
		this.field_7111 = bl;
		super.setInvisible(bl);
	}

	@Override
	public boolean isChild() {
		return this.isSmall();
	}

	@Override
	public void kill() {
		this.remove();
	}

	@Override
	public boolean isImmuneToExplosion() {
		return this.isInvisible();
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return this.isMarker() ? PistonBehavior.field_15975 : super.getPistonBehavior();
	}

	private void setSmall(boolean bl) {
		this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 1, bl));
	}

	public boolean isSmall() {
		return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 1) != 0;
	}

	private void setShowArms(boolean bl) {
		this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 4, bl));
	}

	public boolean shouldShowArms() {
		return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 4) != 0;
	}

	private void setHideBasePlate(boolean bl) {
		this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 8, bl));
	}

	public boolean shouldHideBasePlate() {
		return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 8) != 0;
	}

	private void setMarker(boolean bl) {
		this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 16, bl));
	}

	public boolean isMarker() {
		return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 16) != 0;
	}

	private byte setBitField(byte b, int i, boolean bl) {
		if (bl) {
			b = (byte)(b | i);
		} else {
			b = (byte)(b & ~i);
		}

		return b;
	}

	public void setHeadRotation(Rotation rotation) {
		this.headRotation = rotation;
		this.dataTracker.set(TRACKER_HEAD_ROTATION, rotation);
	}

	public void setBodyRotation(Rotation rotation) {
		this.bodyRotation = rotation;
		this.dataTracker.set(TRACKER_BODY_ROTATION, rotation);
	}

	public void setLeftArmRotation(Rotation rotation) {
		this.leftArmRotation = rotation;
		this.dataTracker.set(TRACKER_LEFT_ARM_ROTATION, rotation);
	}

	public void setRightArmRotation(Rotation rotation) {
		this.rightArmRotation = rotation;
		this.dataTracker.set(TRACKER_RIGHT_ARM_ROTATION, rotation);
	}

	public void setLeftLegRotation(Rotation rotation) {
		this.leftLegRotation = rotation;
		this.dataTracker.set(TRACKER_LEFT_LEG_ROTATION, rotation);
	}

	public void setRightLegRotation(Rotation rotation) {
		this.rightLegRotation = rotation;
		this.dataTracker.set(TRACKER_RIGHT_LEG_ROTATION, rotation);
	}

	public Rotation getHeadRotation() {
		return this.headRotation;
	}

	public Rotation getBodyRotation() {
		return this.bodyRotation;
	}

	@Environment(EnvType.CLIENT)
	public Rotation getLeftArmRotation() {
		return this.leftArmRotation;
	}

	@Environment(EnvType.CLIENT)
	public Rotation getRightArmRotation() {
		return this.rightArmRotation;
	}

	@Environment(EnvType.CLIENT)
	public Rotation getLeftLegRotation() {
		return this.leftLegRotation;
	}

	@Environment(EnvType.CLIENT)
	public Rotation getRightLegRotation() {
		return this.rightLegRotation;
	}

	@Override
	public boolean collides() {
		return super.collides() && !this.isMarker();
	}

	@Override
	public AbsoluteHand getMainHand() {
		return AbsoluteHand.field_6183;
	}

	@Override
	protected SoundEvent getFallSound(int i) {
		return SoundEvents.field_15186;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14897;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15118;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
	}

	@Override
	public boolean method_6086() {
		return false;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (ARMOR_STAND_FLAGS.equals(trackedData)) {
			this.refreshSize();
			this.field_6033 = !this.isMarker();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public boolean method_6102() {
		return false;
	}

	@Override
	public EntitySize getSize(EntityPose entityPose) {
		float f = this.isMarker() ? 0.0F : (this.isChild() ? 0.5F : 1.0F);
		return this.getType().getDefaultSize().scaled(f);
	}
}
