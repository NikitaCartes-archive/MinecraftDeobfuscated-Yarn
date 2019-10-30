/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
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
import org.jetbrains.annotations.Nullable;

public class ArmorStandEntity
extends LivingEntity {
    private static final EulerAngle DEFAULT_HEAD_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_BODY_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_LEFT_ARM_ROTATION = new EulerAngle(-10.0f, 0.0f, -10.0f);
    private static final EulerAngle DEFAULT_RIGHT_ARM_ROTATION = new EulerAngle(-15.0f, 0.0f, 10.0f);
    private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1.0f, 0.0f, -1.0f);
    private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1.0f, 0.0f, 1.0f);
    public static final TrackedData<Byte> ARMOR_STAND_FLAGS = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<EulerAngle> TRACKER_HEAD_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_BODY_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_LEFT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_RIGHT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_LEFT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_RIGHT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE = entity -> entity instanceof AbstractMinecartEntity && ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE;
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
        super((EntityType<? extends LivingEntity>)entityType, world);
        this.stepHeight = 0.0f;
    }

    public ArmorStandEntity(World world, double d, double e, double f) {
        this((EntityType<? extends ArmorStandEntity>)EntityType.ARMOR_STAND, world);
        this.setPosition(d, e, f);
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
    public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                return this.heldItems.get(equipmentSlot.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(equipmentSlot.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                this.onEquipStack(itemStack);
                this.heldItems.set(equipmentSlot.getEntitySlotId(), itemStack);
                break;
            }
            case ARMOR: {
                this.onEquipStack(itemStack);
                this.armorItems.set(equipmentSlot.getEntitySlotId(), itemStack);
            }
        }
    }

    @Override
    public boolean equip(int i, ItemStack itemStack) {
        EquipmentSlot equipmentSlot;
        if (i == 98) {
            equipmentSlot = EquipmentSlot.MAINHAND;
        } else if (i == 99) {
            equipmentSlot = EquipmentSlot.OFFHAND;
        } else if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.HEAD;
        } else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (i == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (itemStack.isEmpty() || MobEntity.canEquipmentSlotContain(equipmentSlot, itemStack) || equipmentSlot == EquipmentSlot.HEAD) {
            this.equipStack(equipmentSlot, itemStack);
            return true;
        }
        return false;
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
        int i;
        ListTag listTag;
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.contains("ArmorItems", 9)) {
            listTag = compoundTag.getList("ArmorItems", 10);
            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
            }
        }
        if (compoundTag.contains("HandItems", 9)) {
            listTag = compoundTag.getList("HandItems", 10);
            for (i = 0; i < this.heldItems.size(); ++i) {
                this.heldItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
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
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!(this.squaredDistanceTo(entity) <= 0.2)) continue;
            entity.pushAwayFrom(this);
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (this.isMarker() || itemStack.getItem() == Items.NAME_TAG) {
            return ActionResult.PASS;
        }
        if (this.world.isClient || playerEntity.isSpectator()) {
            return ActionResult.SUCCESS;
        }
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        if (itemStack.isEmpty()) {
            EquipmentSlot equipmentSlot3;
            EquipmentSlot equipmentSlot2 = this.method_6916(vec3d);
            EquipmentSlot equipmentSlot4 = equipmentSlot3 = this.method_6915(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
            if (this.hasStackEquipped(equipmentSlot3)) {
                this.method_6904(playerEntity, equipmentSlot3, itemStack, hand);
            }
        } else {
            if (this.method_6915(equipmentSlot)) {
                return ActionResult.FAIL;
            }
            if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
                return ActionResult.FAIL;
            }
            this.method_6904(playerEntity, equipmentSlot, itemStack, hand);
        }
        return ActionResult.SUCCESS;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected EquipmentSlot method_6916(Vec3d vec3d) {
        EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
        boolean bl = this.isSmall();
        double d = bl ? vec3d.y * 2.0 : vec3d.y;
        EquipmentSlot equipmentSlot2 = EquipmentSlot.FEET;
        if (d >= 0.1) {
            double d2 = bl ? 0.8 : 0.45;
            if (d < 0.1 + d2 && this.hasStackEquipped(equipmentSlot2)) {
                return EquipmentSlot.FEET;
            }
        }
        double d3 = bl ? 0.3 : 0.0;
        if (d >= 0.9 + d3) {
            double d4 = bl ? 1.0 : 0.7;
            if (d < 0.9 + d4 && this.hasStackEquipped(EquipmentSlot.CHEST)) {
                return EquipmentSlot.CHEST;
            }
        }
        if (d >= 0.4) {
            double d5 = bl ? 1.0 : 0.8;
            if (d < 0.4 + d5 && this.hasStackEquipped(EquipmentSlot.LEGS)) {
                return EquipmentSlot.LEGS;
            }
        }
        if (d >= 1.6 && this.hasStackEquipped(EquipmentSlot.HEAD)) {
            return EquipmentSlot.HEAD;
        }
        if (this.hasStackEquipped(EquipmentSlot.MAINHAND)) return equipmentSlot;
        if (!this.hasStackEquipped(EquipmentSlot.OFFHAND)) return equipmentSlot;
        return EquipmentSlot.OFFHAND;
    }

    public boolean method_6915(EquipmentSlot equipmentSlot) {
        return (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId()) != 0 || equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms();
    }

    private void method_6904(PlayerEntity playerEntity, EquipmentSlot equipmentSlot, ItemStack itemStack, Hand hand) {
        ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
        if (!itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 8) != 0) {
            return;
        }
        if (itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 16) != 0) {
            return;
        }
        if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
            ItemStack itemStack3 = itemStack.copy();
            itemStack3.setCount(1);
            this.equipStack(equipmentSlot, itemStack3);
            return;
        }
        if (!itemStack.isEmpty() && itemStack.getCount() > 1) {
            if (!itemStack2.isEmpty()) {
                return;
            }
            ItemStack itemStack3 = itemStack.copy();
            itemStack3.setCount(1);
            this.equipStack(equipmentSlot, itemStack3);
            itemStack.decrement(1);
            return;
        }
        this.equipStack(equipmentSlot, itemStack);
        playerEntity.setStackInHand(hand, itemStack2);
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.world.isClient || this.removed) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals(damageSource)) {
            this.remove();
            return false;
        }
        if (this.isInvulnerableTo(damageSource) || this.field_7111 || this.isMarker()) {
            return false;
        }
        if (damageSource.isExplosive()) {
            this.method_6908(damageSource);
            this.remove();
            return false;
        }
        if (DamageSource.IN_FIRE.equals(damageSource)) {
            if (this.isOnFire()) {
                this.method_6905(damageSource, 0.15f);
            } else {
                this.setOnFireFor(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals(damageSource) && this.getHealth() > 0.5f) {
            this.method_6905(damageSource, 4.0f);
            return false;
        }
        boolean bl = damageSource.getSource() instanceof ProjectileEntity;
        boolean bl2 = bl && ((ProjectileEntity)damageSource.getSource()).getPierceLevel() > 0;
        boolean bl3 = "player".equals(damageSource.getName());
        if (!bl3 && !bl) {
            return false;
        }
        if (damageSource.getAttacker() instanceof PlayerEntity && !((PlayerEntity)damageSource.getAttacker()).abilities.allowModifyWorld) {
            return false;
        }
        if (damageSource.isSourceCreativePlayer()) {
            this.method_6920();
            this.method_6898();
            this.remove();
            return bl2;
        }
        long l = this.world.getTime();
        if (l - this.field_7112 <= 5L || bl) {
            this.method_6924(damageSource);
            this.method_6898();
            this.remove();
        } else {
            this.world.sendEntityStatus(this, (byte)32);
            this.field_7112 = l;
        }
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 32) {
            if (this.world.isClient) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3f, 1.0f, false);
                this.field_7112 = this.world.getTime();
            }
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e) || e == 0.0) {
            e = 4.0;
        }
        return d < (e *= 64.0) * e;
    }

    private void method_6898() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.getX(), this.getHeightAt(0.6666666666666666), this.getZ(), 10, this.getWidth() / 4.0f, this.getHeight() / 4.0f, this.getWidth() / 4.0f, 0.05);
        }
    }

    private void method_6905(DamageSource damageSource, float f) {
        float g = this.getHealth();
        if ((g -= f) <= 0.5f) {
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
        ItemStack itemStack;
        int i;
        this.method_6920();
        this.drop(damageSource);
        for (i = 0; i < this.heldItems.size(); ++i) {
            itemStack = this.heldItems.get(i);
            if (itemStack.isEmpty()) continue;
            Block.dropStack(this.world, new BlockPos(this).up(), itemStack);
            this.heldItems.set(i, ItemStack.EMPTY);
        }
        for (i = 0; i < this.armorItems.size(); ++i) {
            itemStack = this.armorItems.get(i);
            if (itemStack.isEmpty()) continue;
            Block.dropStack(this.world, new BlockPos(this).up(), itemStack);
            this.armorItems.set(i, ItemStack.EMPTY);
        }
    }

    private void method_6920() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0f, 1.0f);
    }

    @Override
    protected float turnHead(float f, float g) {
        this.prevBodyYaw = this.prevYaw;
        this.bodyYaw = this.yaw;
        return 0.0f;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return entityDimensions.height * (this.isBaby() ? 0.5f : 0.9f);
    }

    @Override
    public double getHeightOffset() {
        return this.isMarker() ? 0.0 : (double)0.1f;
    }

    @Override
    public void travel(Vec3d vec3d) {
        if (!this.canClip()) {
            return;
        }
        super.travel(vec3d);
    }

    @Override
    public void setYaw(float f) {
        this.prevBodyYaw = this.prevYaw = f;
        this.prevHeadYaw = this.headYaw = f;
    }

    @Override
    public void setHeadYaw(float f) {
        this.prevBodyYaw = this.prevYaw = f;
        this.prevHeadYaw = this.headYaw = f;
    }

    @Override
    public void tick() {
        EulerAngle eulerAngle6;
        EulerAngle eulerAngle5;
        EulerAngle eulerAngle4;
        EulerAngle eulerAngle3;
        EulerAngle eulerAngle2;
        super.tick();
        EulerAngle eulerAngle = this.dataTracker.get(TRACKER_HEAD_ROTATION);
        if (!this.headRotation.equals(eulerAngle)) {
            this.setHeadRotation(eulerAngle);
        }
        if (!this.bodyRotation.equals(eulerAngle2 = this.dataTracker.get(TRACKER_BODY_ROTATION))) {
            this.setBodyRotation(eulerAngle2);
        }
        if (!this.leftArmRotation.equals(eulerAngle3 = this.dataTracker.get(TRACKER_LEFT_ARM_ROTATION))) {
            this.setLeftArmRotation(eulerAngle3);
        }
        if (!this.rightArmRotation.equals(eulerAngle4 = this.dataTracker.get(TRACKER_RIGHT_ARM_ROTATION))) {
            this.setRightArmRotation(eulerAngle4);
        }
        if (!this.leftLegRotation.equals(eulerAngle5 = this.dataTracker.get(TRACKER_LEFT_LEG_ROTATION))) {
            this.setLeftLegRotation(eulerAngle5);
        }
        if (!this.rightLegRotation.equals(eulerAngle6 = this.dataTracker.get(TRACKER_RIGHT_LEG_ROTATION))) {
            this.setRightLegRotation(eulerAngle6);
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
        if (this.isMarker()) {
            return PistonBehavior.IGNORE;
        }
        return super.getPistonBehavior();
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
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 0x10) != 0;
    }

    private byte setBitField(byte b, int i, boolean bl) {
        b = bl ? (byte)(b | i) : (byte)(b & ~i);
        return b;
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

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getLeftArmRotation() {
        return this.leftArmRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getRightArmRotation() {
        return this.rightArmRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getLeftLegRotation() {
        return this.leftLegRotation;
    }

    @Environment(value=EnvType.CLIENT)
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
    protected SoundEvent getFallSound(int i) {
        return SoundEvents.ENTITY_ARMOR_STAND_FALL;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_ARMOR_STAND_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ARMOR_STAND_BREAK;
    }

    @Override
    public void onStruckByLightning(LightningEntity lightningEntity) {
    }

    @Override
    public boolean isAffectedBySplashPotions() {
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (ARMOR_STAND_FLAGS.equals(trackedData)) {
            this.calculateDimensions();
            this.inanimate = !this.isMarker();
        }
        super.onTrackedDataSet(trackedData);
    }

    @Override
    public boolean method_6102() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose entityPose) {
        float f = this.isMarker() ? 0.0f : (this.isBaby() ? 0.5f : 1.0f);
        return this.getType().getDimensions().scaled(f);
    }
}

