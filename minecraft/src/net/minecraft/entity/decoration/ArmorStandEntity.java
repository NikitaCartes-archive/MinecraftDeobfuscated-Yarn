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
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
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
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEntity extends LivingEntity {
	private static final EulerAngle DEFAULT_HEAD_ROTATION = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_BODY_ROTATION = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_LEFT_ARM_ROTATION = new EulerAngle(-10.0F, 0.0F, -10.0F);
	private static final EulerAngle DEFAULT_RIGHT_ARM_ROTATION = new EulerAngle(-15.0F, 0.0F, 10.0F);
	private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1.0F, 0.0F, -1.0F);
	private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1.0F, 0.0F, 1.0F);
	public static final TrackedData<Byte> ARMOR_STAND_FLAGS = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<EulerAngle> TRACKER_HEAD_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> TRACKER_BODY_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> TRACKER_LEFT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> TRACKER_RIGHT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> TRACKER_LEFT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> TRACKER_RIGHT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE = entity -> entity instanceof AbstractMinecartEntity
			&& ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE;
	private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private boolean field_7111;
	public long field_7112;
	private int disabledSlots;
	private EulerAngle headRotation = DEFAULT_HEAD_ROTATION;
	private EulerAngle bodyRotation = DEFAULT_BODY_ROTATION;
	private EulerAngle leftArmRotation = DEFAULT_LEFT_ARM_ROTATION;
	private EulerAngle rightArmRotation = DEFAULT_RIGHT_ARM_ROTATION;
	private EulerAngle leftLegRotation = DEFAULT_LEFT_LEG_ROTATION;
	private EulerAngle rightLegRotation = DEFAULT_RIGHT_LEG_ROTATION;

	public ArmorStandEntity(EntityType<? extends ArmorStandEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 0.0F;
	}

	public ArmorStandEntity(World world, double d, double e, double f) {
		this(EntityType.ARMOR_STAND, world);
		this.setPosition(d, e, f);
	}

	@Override
	public boolean isGlowing() {
		return !this.isMarker() && super.isGlowing();
	}

	@Override
	public void calculateDimensions() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.calculateDimensions();
		this.setPosition(d, e, f);
	}

	private boolean canClip() {
		return !this.isMarker() && !this.hasNoGravity();
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
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		switch (slot.getType()) {
			case HAND:
				return this.heldItems.get(slot.getEntitySlotId());
			case ARMOR:
				return this.armorItems.get(slot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		switch (slot.getType()) {
			case HAND:
				this.onEquipStack(stack);
				this.heldItems.set(slot.getEntitySlotId(), stack);
				break;
			case ARMOR:
				this.onEquipStack(stack);
				this.armorItems.set(slot.getEntitySlotId(), stack);
		}
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		EquipmentSlot equipmentSlot;
		if (slot == 98) {
			equipmentSlot = EquipmentSlot.MAINHAND;
		} else if (slot == 99) {
			equipmentSlot = EquipmentSlot.OFFHAND;
		} else if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else {
			if (slot != 100 + EquipmentSlot.FEET.getEntitySlotId()) {
				return false;
			}

			equipmentSlot = EquipmentSlot.FEET;
		}

		if (!item.isEmpty() && !MobEntity.canEquipmentSlotContain(equipmentSlot, item) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.equipStack(equipmentSlot, item);
			return true;
		}
	}

	@Override
	public boolean canPickUp(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		return this.getEquippedStack(equipmentSlot).isEmpty() && !this.method_6915(equipmentSlot);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.armorItems) {
			CompoundTag compoundTag = new CompoundTag();
			if (!itemStack.isEmpty()) {
				itemStack.toTag(compoundTag);
			}

			listTag.add(compoundTag);
		}

		tag.put("ArmorItems", listTag);
		ListTag listTag2 = new ListTag();

		for (ItemStack itemStack2 : this.heldItems) {
			CompoundTag compoundTag2 = new CompoundTag();
			if (!itemStack2.isEmpty()) {
				itemStack2.toTag(compoundTag2);
			}

			listTag2.add(compoundTag2);
		}

		tag.put("HandItems", listTag2);
		tag.putBoolean("Invisible", this.isInvisible());
		tag.putBoolean("Small", this.isSmall());
		tag.putBoolean("ShowArms", this.shouldShowArms());
		tag.putInt("DisabledSlots", this.disabledSlots);
		tag.putBoolean("NoBasePlate", this.shouldHideBasePlate());
		if (this.isMarker()) {
			tag.putBoolean("Marker", this.isMarker());
		}

		tag.put("Pose", this.serializePose());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("ArmorItems", 9)) {
			ListTag listTag = tag.getList("ArmorItems", 10);

			for (int i = 0; i < this.armorItems.size(); i++) {
				this.armorItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
			}
		}

		if (tag.contains("HandItems", 9)) {
			ListTag listTag = tag.getList("HandItems", 10);

			for (int i = 0; i < this.heldItems.size(); i++) {
				this.heldItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
			}
		}

		this.setInvisible(tag.getBoolean("Invisible"));
		this.setSmall(tag.getBoolean("Small"));
		this.setShowArms(tag.getBoolean("ShowArms"));
		this.disabledSlots = tag.getInt("DisabledSlots");
		this.setHideBasePlate(tag.getBoolean("NoBasePlate"));
		this.setMarker(tag.getBoolean("Marker"));
		this.noClip = !this.canClip();
		CompoundTag compoundTag = tag.getCompound("Pose");
		this.deserializePose(compoundTag);
	}

	private void deserializePose(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Head", 5);
		this.setHeadRotation(listTag.isEmpty() ? DEFAULT_HEAD_ROTATION : new EulerAngle(listTag));
		ListTag listTag2 = compoundTag.getList("Body", 5);
		this.setBodyRotation(listTag2.isEmpty() ? DEFAULT_BODY_ROTATION : new EulerAngle(listTag2));
		ListTag listTag3 = compoundTag.getList("LeftArm", 5);
		this.setLeftArmRotation(listTag3.isEmpty() ? DEFAULT_LEFT_ARM_ROTATION : new EulerAngle(listTag3));
		ListTag listTag4 = compoundTag.getList("RightArm", 5);
		this.setRightArmRotation(listTag4.isEmpty() ? DEFAULT_RIGHT_ARM_ROTATION : new EulerAngle(listTag4));
		ListTag listTag5 = compoundTag.getList("LeftLeg", 5);
		this.setLeftLegRotation(listTag5.isEmpty() ? DEFAULT_LEFT_LEG_ROTATION : new EulerAngle(listTag5));
		ListTag listTag6 = compoundTag.getList("RightLeg", 5);
		this.setRightLegRotation(listTag6.isEmpty() ? DEFAULT_RIGHT_LEG_ROTATION : new EulerAngle(listTag6));
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
	protected void tickCramming() {
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), RIDEABLE_MINECART_PREDICATE);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (this.squaredDistanceTo(entity) <= 0.2) {
				entity.pushAwayFrom(this);
			}
		}
	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isMarker() || itemStack.getItem() == Items.NAME_TAG) {
			return ActionResult.PASS;
		} else if (player.isSpectator()) {
			return ActionResult.SUCCESS;
		} else if (player.world.isClient) {
			return ActionResult.CONSUME;
		} else {
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
			if (itemStack.isEmpty()) {
				EquipmentSlot equipmentSlot2 = this.method_6916(hitPos);
				EquipmentSlot equipmentSlot3 = this.method_6915(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
				if (this.hasStackEquipped(equipmentSlot3) && this.method_6904(player, equipmentSlot3, itemStack, hand)) {
					return ActionResult.SUCCESS;
				}
			} else {
				if (this.method_6915(equipmentSlot)) {
					return ActionResult.FAIL;
				}

				if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
					return ActionResult.FAIL;
				}

				if (this.method_6904(player, equipmentSlot, itemStack, hand)) {
					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		}
	}

	private EquipmentSlot method_6916(Vec3d vec3d) {
		EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
		boolean bl = this.isSmall();
		double d = bl ? vec3d.y * 2.0 : vec3d.y;
		EquipmentSlot equipmentSlot2 = EquipmentSlot.FEET;
		if (d >= 0.1 && d < 0.1 + (bl ? 0.8 : 0.45) && this.hasStackEquipped(equipmentSlot2)) {
			equipmentSlot = EquipmentSlot.FEET;
		} else if (d >= 0.9 + (bl ? 0.3 : 0.0) && d < 0.9 + (bl ? 1.0 : 0.7) && this.hasStackEquipped(EquipmentSlot.CHEST)) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (d >= 0.4 && d < 0.4 + (bl ? 1.0 : 0.8) && this.hasStackEquipped(EquipmentSlot.LEGS)) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else if (d >= 1.6 && this.hasStackEquipped(EquipmentSlot.HEAD)) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (!this.hasStackEquipped(EquipmentSlot.MAINHAND) && this.hasStackEquipped(EquipmentSlot.OFFHAND)) {
			equipmentSlot = EquipmentSlot.OFFHAND;
		}

		return equipmentSlot;
	}

	private boolean method_6915(EquipmentSlot equipmentSlot) {
		return (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId()) != 0 || equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms();
	}

	private boolean method_6904(PlayerEntity playerEntity, EquipmentSlot equipmentSlot, ItemStack itemStack, Hand hand) {
		ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
		if (!itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 8) != 0) {
			return false;
		} else if (itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 16) != 0) {
			return false;
		} else if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(1);
			this.equipStack(equipmentSlot, itemStack3);
			return true;
		} else if (itemStack.isEmpty() || itemStack.getCount() <= 1) {
			this.equipStack(equipmentSlot, itemStack);
			playerEntity.setStackInHand(hand, itemStack2);
			return true;
		} else if (!itemStack2.isEmpty()) {
			return false;
		} else {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(1);
			this.equipStack(equipmentSlot, itemStack3);
			itemStack.decrement(1);
			return true;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world.isClient || this.removed) {
			return false;
		} else if (DamageSource.OUT_OF_WORLD.equals(source)) {
			this.remove();
			return false;
		} else if (this.isInvulnerableTo(source) || this.field_7111 || this.isMarker()) {
			return false;
		} else if (source.isExplosive()) {
			this.method_6908(source);
			this.remove();
			return false;
		} else if (DamageSource.IN_FIRE.equals(source)) {
			if (this.isOnFire()) {
				this.method_6905(source, 0.15F);
			} else {
				this.setOnFireFor(5);
			}

			return false;
		} else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
			this.method_6905(source, 4.0F);
			return false;
		} else {
			boolean bl = source.getSource() instanceof ProjectileEntity;
			boolean bl2 = bl && ((ProjectileEntity)source.getSource()).getPierceLevel() > 0;
			boolean bl3 = "player".equals(source.getName());
			if (!bl3 && !bl) {
				return false;
			} else if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity)source.getAttacker()).abilities.allowModifyWorld) {
				return false;
			} else if (source.isSourceCreativePlayer()) {
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
					this.method_6924(source);
					this.method_6898();
					this.remove();
				}

				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 32) {
			if (this.world.isClient) {
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
				this.field_7112 = this.world.getTime();
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d) || d == 0.0) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	private void method_6898() {
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world)
				.spawnParticles(
					new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()),
					this.getX(),
					this.getBodyY(0.6666666666666666),
					this.getZ(),
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
		Block.dropStack(this.world, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
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
		this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
	}

	@Override
	protected float turnHead(float bodyRotation, float headRotation) {
		this.prevBodyYaw = this.prevYaw;
		this.bodyYaw = this.yaw;
		return 0.0F;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * (this.isBaby() ? 0.5F : 0.9F);
	}

	@Override
	public double getHeightOffset() {
		return this.isMarker() ? 0.0 : 0.1F;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.canClip()) {
			super.travel(movementInput);
		}
	}

	@Override
	public void setYaw(float yaw) {
		this.prevBodyYaw = this.prevYaw = yaw;
		this.prevHeadYaw = this.headYaw = yaw;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		this.prevBodyYaw = this.prevYaw = headYaw;
		this.prevHeadYaw = this.headYaw = headYaw;
	}

	@Override
	public void tick() {
		super.tick();
		EulerAngle eulerAngle = this.dataTracker.get(TRACKER_HEAD_ROTATION);
		if (!this.headRotation.equals(eulerAngle)) {
			this.setHeadRotation(eulerAngle);
		}

		EulerAngle eulerAngle2 = this.dataTracker.get(TRACKER_BODY_ROTATION);
		if (!this.bodyRotation.equals(eulerAngle2)) {
			this.setBodyRotation(eulerAngle2);
		}

		EulerAngle eulerAngle3 = this.dataTracker.get(TRACKER_LEFT_ARM_ROTATION);
		if (!this.leftArmRotation.equals(eulerAngle3)) {
			this.setLeftArmRotation(eulerAngle3);
		}

		EulerAngle eulerAngle4 = this.dataTracker.get(TRACKER_RIGHT_ARM_ROTATION);
		if (!this.rightArmRotation.equals(eulerAngle4)) {
			this.setRightArmRotation(eulerAngle4);
		}

		EulerAngle eulerAngle5 = this.dataTracker.get(TRACKER_LEFT_LEG_ROTATION);
		if (!this.leftLegRotation.equals(eulerAngle5)) {
			this.setLeftLegRotation(eulerAngle5);
		}

		EulerAngle eulerAngle6 = this.dataTracker.get(TRACKER_RIGHT_LEG_ROTATION);
		if (!this.rightLegRotation.equals(eulerAngle6)) {
			this.setRightLegRotation(eulerAngle6);
		}
	}

	@Override
	protected void updatePotionVisibility() {
		this.setInvisible(this.field_7111);
	}

	@Override
	public void setInvisible(boolean invisible) {
		this.field_7111 = invisible;
		super.setInvisible(invisible);
	}

	@Override
	public boolean isBaby() {
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
		return this.isMarker() ? PistonBehavior.IGNORE : super.getPistonBehavior();
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

	private byte setBitField(byte value, int bitField, boolean set) {
		if (set) {
			value = (byte)(value | bitField);
		} else {
			value = (byte)(value & ~bitField);
		}

		return value;
	}

	public void setHeadRotation(EulerAngle eulerAngle) {
		this.headRotation = eulerAngle;
		this.dataTracker.set(TRACKER_HEAD_ROTATION, eulerAngle);
	}

	public void setBodyRotation(EulerAngle eulerAngle) {
		this.bodyRotation = eulerAngle;
		this.dataTracker.set(TRACKER_BODY_ROTATION, eulerAngle);
	}

	public void setLeftArmRotation(EulerAngle eulerAngle) {
		this.leftArmRotation = eulerAngle;
		this.dataTracker.set(TRACKER_LEFT_ARM_ROTATION, eulerAngle);
	}

	public void setRightArmRotation(EulerAngle eulerAngle) {
		this.rightArmRotation = eulerAngle;
		this.dataTracker.set(TRACKER_RIGHT_ARM_ROTATION, eulerAngle);
	}

	public void setLeftLegRotation(EulerAngle eulerAngle) {
		this.leftLegRotation = eulerAngle;
		this.dataTracker.set(TRACKER_LEFT_LEG_ROTATION, eulerAngle);
	}

	public void setRightLegRotation(EulerAngle eulerAngle) {
		this.rightLegRotation = eulerAngle;
		this.dataTracker.set(TRACKER_RIGHT_LEG_ROTATION, eulerAngle);
	}

	public EulerAngle getHeadRotation() {
		return this.headRotation;
	}

	public EulerAngle getBodyRotation() {
		return this.bodyRotation;
	}

	@Environment(EnvType.CLIENT)
	public EulerAngle getLeftArmRotation() {
		return this.leftArmRotation;
	}

	@Environment(EnvType.CLIENT)
	public EulerAngle getRightArmRotation() {
		return this.rightArmRotation;
	}

	@Environment(EnvType.CLIENT)
	public EulerAngle getLeftLegRotation() {
		return this.leftLegRotation;
	}

	@Environment(EnvType.CLIENT)
	public EulerAngle getRightLegRotation() {
		return this.rightLegRotation;
	}

	@Override
	public boolean collides() {
		return super.collides() && !this.isMarker();
	}

	@Override
	public Arm getMainArm() {
		return Arm.RIGHT;
	}

	@Override
	protected SoundEvent getFallSound(int distance) {
		return SoundEvents.ENTITY_ARMOR_STAND_FALL;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ARMOR_STAND_HIT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ARMOR_STAND_BREAK;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightning) {
	}

	@Override
	public boolean isAffectedBySplashPotions() {
		return false;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (ARMOR_STAND_FLAGS.equals(data)) {
			this.calculateDimensions();
			this.inanimate = !this.isMarker();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public boolean method_6102() {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		float f = this.isMarker() ? 0.0F : (this.isBaby() ? 0.5F : 1.0F);
		return this.getType().getDimensions().scaled(f);
	}
}
