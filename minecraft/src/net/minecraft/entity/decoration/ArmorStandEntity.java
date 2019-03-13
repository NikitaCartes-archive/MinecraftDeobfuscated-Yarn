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
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEntity extends LivingEntity {
	private static final Rotation field_7113 = new Rotation(0.0F, 0.0F, 0.0F);
	private static final Rotation field_7119 = new Rotation(0.0F, 0.0F, 0.0F);
	private static final Rotation field_7124 = new Rotation(-10.0F, 0.0F, -10.0F);
	private static final Rotation field_7115 = new Rotation(-15.0F, 0.0F, 10.0F);
	private static final Rotation field_7121 = new Rotation(-1.0F, 0.0F, -1.0F);
	private static final Rotation field_7117 = new Rotation(1.0F, 0.0F, 1.0F);
	public static final TrackedData<Byte> field_7107 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Rotation> field_7123 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> field_7122 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> field_7116 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> field_7105 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> field_7127 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<Rotation> field_7125 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	private static final Predicate<Entity> field_7102 = entity -> entity instanceof AbstractMinecartEntity
			&& ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.field_7674;
	private final DefaultedList<ItemStack> field_7114 = DefaultedList.create(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> field_7108 = DefaultedList.create(4, ItemStack.EMPTY);
	private boolean field_7111;
	public long field_7112;
	private int disabledSlots;
	private Rotation field_7104 = field_7113;
	private Rotation field_7106 = field_7119;
	private Rotation field_7126 = field_7124;
	private Rotation field_7120 = field_7115;
	private Rotation field_7110 = field_7121;
	private Rotation field_7103 = field_7117;

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

	private boolean method_18059() {
		return !this.isMarker() && !this.isUnaffectedByGravity();
	}

	@Override
	public boolean method_6034() {
		return super.method_6034() && this.method_18059();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7107, (byte)0);
		this.field_6011.startTracking(field_7123, field_7113);
		this.field_6011.startTracking(field_7122, field_7119);
		this.field_6011.startTracking(field_7116, field_7124);
		this.field_6011.startTracking(field_7105, field_7115);
		this.field_6011.startTracking(field_7127, field_7121);
		this.field_6011.startTracking(field_7125, field_7117);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.field_7114;
	}

	@Override
	public Iterable<ItemStack> getItemsArmor() {
		return this.field_7108;
	}

	@Override
	public ItemStack method_6118(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot.getType()) {
			case HAND:
				return this.field_7114.get(equipmentSlot.getEntitySlotId());
			case ARMOR:
				return this.field_7108.get(equipmentSlot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		switch (equipmentSlot.getType()) {
			case HAND:
				this.method_6116(itemStack);
				this.field_7114.set(equipmentSlot.getEntitySlotId(), itemStack);
				break;
			case ARMOR:
				this.method_6116(itemStack);
				this.field_7108.set(equipmentSlot.getEntitySlotId(), itemStack);
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

		if (!itemStack.isEmpty() && !MobEntity.method_5935(equipmentSlot, itemStack) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.method_5673(equipmentSlot, itemStack);
			return true;
		}
	}

	@Override
	public boolean method_18397(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
		return this.method_6118(equipmentSlot).isEmpty() && !this.method_6915(equipmentSlot);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.field_7108) {
			CompoundTag compoundTag2 = new CompoundTag();
			if (!itemStack.isEmpty()) {
				itemStack.method_7953(compoundTag2);
			}

			listTag.add(compoundTag2);
		}

		compoundTag.method_10566("ArmorItems", listTag);
		ListTag listTag2 = new ListTag();

		for (ItemStack itemStack2 : this.field_7114) {
			CompoundTag compoundTag3 = new CompoundTag();
			if (!itemStack2.isEmpty()) {
				itemStack2.method_7953(compoundTag3);
			}

			listTag2.add(compoundTag3);
		}

		compoundTag.method_10566("HandItems", listTag2);
		compoundTag.putBoolean("Invisible", this.isInvisible());
		compoundTag.putBoolean("Small", this.isSmall());
		compoundTag.putBoolean("ShowArms", this.shouldShowArms());
		compoundTag.putInt("DisabledSlots", this.disabledSlots);
		compoundTag.putBoolean("NoBasePlate", this.shouldHideBasePlate());
		if (this.isMarker()) {
			compoundTag.putBoolean("Marker", this.isMarker());
		}

		compoundTag.method_10566("Pose", this.method_6911());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("ArmorItems", 9)) {
			ListTag listTag = compoundTag.method_10554("ArmorItems", 10);

			for (int i = 0; i < this.field_7108.size(); i++) {
				this.field_7108.set(i, ItemStack.method_7915(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("HandItems", 9)) {
			ListTag listTag = compoundTag.method_10554("HandItems", 10);

			for (int i = 0; i < this.field_7114.size(); i++) {
				this.field_7114.set(i, ItemStack.method_7915(listTag.getCompoundTag(i)));
			}
		}

		this.setInvisible(compoundTag.getBoolean("Invisible"));
		this.setSmall(compoundTag.getBoolean("Small"));
		this.setShowArms(compoundTag.getBoolean("ShowArms"));
		this.disabledSlots = compoundTag.getInt("DisabledSlots");
		this.setHideBasePlate(compoundTag.getBoolean("NoBasePlate"));
		this.setMarker(compoundTag.getBoolean("Marker"));
		this.noClip = !this.method_18059();
		CompoundTag compoundTag2 = compoundTag.getCompound("Pose");
		this.method_6928(compoundTag2);
	}

	private void method_6928(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.method_10554("Head", 5);
		this.method_6919(listTag.isEmpty() ? field_7113 : new Rotation(listTag));
		ListTag listTag2 = compoundTag.method_10554("Body", 5);
		this.method_6927(listTag2.isEmpty() ? field_7119 : new Rotation(listTag2));
		ListTag listTag3 = compoundTag.method_10554("LeftArm", 5);
		this.method_6910(listTag3.isEmpty() ? field_7124 : new Rotation(listTag3));
		ListTag listTag4 = compoundTag.method_10554("RightArm", 5);
		this.method_6925(listTag4.isEmpty() ? field_7115 : new Rotation(listTag4));
		ListTag listTag5 = compoundTag.method_10554("LeftLeg", 5);
		this.method_6909(listTag5.isEmpty() ? field_7121 : new Rotation(listTag5));
		ListTag listTag6 = compoundTag.method_10554("RightLeg", 5);
		this.method_6926(listTag6.isEmpty() ? field_7117 : new Rotation(listTag6));
	}

	private CompoundTag method_6911() {
		CompoundTag compoundTag = new CompoundTag();
		if (!field_7113.equals(this.field_7104)) {
			compoundTag.method_10566("Head", this.field_7104.method_10255());
		}

		if (!field_7119.equals(this.field_7106)) {
			compoundTag.method_10566("Body", this.field_7106.method_10255());
		}

		if (!field_7124.equals(this.field_7126)) {
			compoundTag.method_10566("LeftArm", this.field_7126.method_10255());
		}

		if (!field_7115.equals(this.field_7120)) {
			compoundTag.method_10566("RightArm", this.field_7120.method_10255());
		}

		if (!field_7121.equals(this.field_7110)) {
			compoundTag.method_10566("LeftLeg", this.field_7110.method_10255());
		}

		if (!field_7117.equals(this.field_7103)) {
			compoundTag.method_10566("RightLeg", this.field_7103.method_10255());
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
		List<Entity> list = this.field_6002.method_8333(this, this.method_5829(), field_7102);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (this.squaredDistanceTo(entity) <= 0.2) {
				entity.pushAwayFrom(this);
			}
		}
	}

	@Override
	public ActionResult method_5664(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (this.isMarker() || itemStack.getItem() == Items.field_8448) {
			return ActionResult.PASS;
		} else if (!this.field_6002.isClient && !playerEntity.isSpectator()) {
			EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
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
		ItemStack itemStack2 = this.method_6118(equipmentSlot);
		if (itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 8) == 0) {
			if (!itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 16) == 0) {
				if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
					ItemStack itemStack3 = itemStack.copy();
					itemStack3.setAmount(1);
					this.method_5673(equipmentSlot, itemStack3);
				} else if (itemStack.isEmpty() || itemStack.getAmount() <= 1) {
					this.method_5673(equipmentSlot, itemStack);
					playerEntity.method_6122(hand, itemStack2);
				} else if (itemStack2.isEmpty()) {
					ItemStack itemStack3 = itemStack.copy();
					itemStack3.setAmount(1);
					this.method_5673(equipmentSlot, itemStack3);
					itemStack.subtractAmount(1);
				}
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.field_6002.isClient || this.invalid) {
			return false;
		} else if (DamageSource.OUT_OF_WORLD.equals(damageSource)) {
			this.invalidate();
			return false;
		} else if (this.isInvulnerableTo(damageSource) || this.field_7111 || this.isMarker()) {
			return false;
		} else if (damageSource.isExplosive()) {
			this.method_6908(damageSource);
			this.invalidate();
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
			boolean bl = damageSource.method_5526() instanceof ProjectileEntity;
			boolean bl2 = bl && ((ProjectileEntity)damageSource.method_5526()).getPierceLevel() > 0;
			boolean bl3 = "player".equals(damageSource.getName());
			if (!bl3 && !bl) {
				return false;
			} else if (damageSource.method_5529() instanceof PlayerEntity && !((PlayerEntity)damageSource.method_5529()).abilities.allowModifyWorld) {
				return false;
			} else if (damageSource.isSourceCreativePlayer()) {
				this.method_6920();
				this.method_6898();
				this.invalidate();
				return bl2;
			} else {
				long l = this.field_6002.getTime();
				if (l - this.field_7112 > 5L && !bl) {
					this.field_6002.summonParticle(this, (byte)32);
					this.field_7112 = l;
				} else {
					this.method_6924(damageSource);
					this.method_6898();
					this.invalidate();
				}

				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 32) {
			if (this.field_6002.isClient) {
				this.field_6002.method_8486(this.x, this.y, this.z, SoundEvents.field_14897, this.method_5634(), 0.3F, 1.0F, false);
				this.field_7112 = this.field_6002.getTime();
			}
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.method_5829().averageDimension() * 4.0;
		if (Double.isNaN(e) || e == 0.0) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	private void method_6898() {
		if (this.field_6002 instanceof ServerWorld) {
			((ServerWorld)this.field_6002)
				.method_14199(
					new BlockStateParticleParameters(ParticleTypes.field_11217, Blocks.field_10161.method_9564()),
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
			this.invalidate();
		} else {
			this.setHealth(g);
		}
	}

	private void method_6924(DamageSource damageSource) {
		Block.method_9577(this.field_6002, new BlockPos(this), new ItemStack(Items.field_8694));
		this.method_6908(damageSource);
	}

	private void method_6908(DamageSource damageSource) {
		this.method_6920();
		this.method_16080(damageSource);

		for (int i = 0; i < this.field_7114.size(); i++) {
			ItemStack itemStack = this.field_7114.get(i);
			if (!itemStack.isEmpty()) {
				Block.method_9577(this.field_6002, new BlockPos(this).up(), itemStack);
				this.field_7114.set(i, ItemStack.EMPTY);
			}
		}

		for (int ix = 0; ix < this.field_7108.size(); ix++) {
			ItemStack itemStack = this.field_7108.get(ix);
			if (!itemStack.isEmpty()) {
				Block.method_9577(this.field_6002, new BlockPos(this).up(), itemStack);
				this.field_7108.set(ix, ItemStack.EMPTY);
			}
		}
	}

	private void method_6920() {
		this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_15118, this.method_5634(), 1.0F, 1.0F);
	}

	@Override
	protected float method_6031(float f, float g) {
		this.field_6220 = this.prevYaw;
		this.field_6283 = this.yaw;
		return 0.0F;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * (this.isChild() ? 0.5F : 0.9F);
	}

	@Override
	public double getHeightOffset() {
		return this.isMarker() ? 0.0 : 0.1F;
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.method_18059()) {
			super.method_6091(vec3d);
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
	public void update() {
		super.update();
		Rotation rotation = this.field_6011.get(field_7123);
		if (!this.field_7104.equals(rotation)) {
			this.method_6919(rotation);
		}

		Rotation rotation2 = this.field_6011.get(field_7122);
		if (!this.field_7106.equals(rotation2)) {
			this.method_6927(rotation2);
		}

		Rotation rotation3 = this.field_6011.get(field_7116);
		if (!this.field_7126.equals(rotation3)) {
			this.method_6910(rotation3);
		}

		Rotation rotation4 = this.field_6011.get(field_7105);
		if (!this.field_7120.equals(rotation4)) {
			this.method_6925(rotation4);
		}

		Rotation rotation5 = this.field_6011.get(field_7127);
		if (!this.field_7110.equals(rotation5)) {
			this.method_6909(rotation5);
		}

		Rotation rotation6 = this.field_6011.get(field_7125);
		if (!this.field_7103.equals(rotation6)) {
			this.method_6926(rotation6);
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
		this.invalidate();
	}

	@Override
	public boolean isImmuneToExplosion() {
		return this.isInvisible();
	}

	@Override
	public PistonBehavior method_5657() {
		return this.isMarker() ? PistonBehavior.field_15975 : super.method_5657();
	}

	private void setSmall(boolean bl) {
		this.field_6011.set(field_7107, this.setBitField(this.field_6011.get(field_7107), 1, bl));
	}

	public boolean isSmall() {
		return (this.field_6011.get(field_7107) & 1) != 0;
	}

	private void setShowArms(boolean bl) {
		this.field_6011.set(field_7107, this.setBitField(this.field_6011.get(field_7107), 4, bl));
	}

	public boolean shouldShowArms() {
		return (this.field_6011.get(field_7107) & 4) != 0;
	}

	private void setHideBasePlate(boolean bl) {
		this.field_6011.set(field_7107, this.setBitField(this.field_6011.get(field_7107), 8, bl));
	}

	public boolean shouldHideBasePlate() {
		return (this.field_6011.get(field_7107) & 8) != 0;
	}

	private void setMarker(boolean bl) {
		this.field_6011.set(field_7107, this.setBitField(this.field_6011.get(field_7107), 16, bl));
	}

	public boolean isMarker() {
		return (this.field_6011.get(field_7107) & 16) != 0;
	}

	private byte setBitField(byte b, int i, boolean bl) {
		if (bl) {
			b = (byte)(b | i);
		} else {
			b = (byte)(b & ~i);
		}

		return b;
	}

	public void method_6919(Rotation rotation) {
		this.field_7104 = rotation;
		this.field_6011.set(field_7123, rotation);
	}

	public void method_6927(Rotation rotation) {
		this.field_7106 = rotation;
		this.field_6011.set(field_7122, rotation);
	}

	public void method_6910(Rotation rotation) {
		this.field_7126 = rotation;
		this.field_6011.set(field_7116, rotation);
	}

	public void method_6925(Rotation rotation) {
		this.field_7120 = rotation;
		this.field_6011.set(field_7105, rotation);
	}

	public void method_6909(Rotation rotation) {
		this.field_7110 = rotation;
		this.field_6011.set(field_7127, rotation);
	}

	public void method_6926(Rotation rotation) {
		this.field_7103 = rotation;
		this.field_6011.set(field_7125, rotation);
	}

	public Rotation method_6921() {
		return this.field_7104;
	}

	public Rotation method_6923() {
		return this.field_7106;
	}

	@Environment(EnvType.CLIENT)
	public Rotation method_6930() {
		return this.field_7126;
	}

	@Environment(EnvType.CLIENT)
	public Rotation method_6903() {
		return this.field_7120;
	}

	@Environment(EnvType.CLIENT)
	public Rotation method_6917() {
		return this.field_7110;
	}

	@Environment(EnvType.CLIENT)
	public Rotation method_6900() {
		return this.field_7103;
	}

	@Override
	public boolean doesCollide() {
		return super.doesCollide() && !this.isMarker();
	}

	@Override
	public OptionMainHand getMainHand() {
		return OptionMainHand.field_6183;
	}

	@Override
	protected SoundEvent method_6041(int i) {
		return SoundEvents.field_15186;
	}

	@Nullable
	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14897;
	}

	@Nullable
	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15118;
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
	}

	@Override
	public boolean method_6086() {
		return false;
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_7107.equals(trackedData)) {
			this.refreshSize();
			this.field_6033 = !this.isMarker();
		}

		super.method_5674(trackedData);
	}

	@Override
	public boolean method_6102() {
		return false;
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		float f = this.isMarker() ? 0.0F : (this.isChild() ? 0.5F : 1.0F);
		return this.method_5864().getDefaultSize().scaled(f);
	}
}
