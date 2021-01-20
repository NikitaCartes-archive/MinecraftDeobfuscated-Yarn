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
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ArmorStandEntity
extends LivingEntity {
    private static final EulerAngle DEFAULT_HEAD_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_BODY_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_LEFT_ARM_ROTATION = new EulerAngle(-10.0f, 0.0f, -10.0f);
    private static final EulerAngle DEFAULT_RIGHT_ARM_ROTATION = new EulerAngle(-15.0f, 0.0f, 10.0f);
    private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1.0f, 0.0f, -1.0f);
    private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1.0f, 0.0f, 1.0f);
    private static final EntityDimensions MARKER_DIMENSIONS = new EntityDimensions(0.0f, 0.0f, true);
    private static final EntityDimensions SMALL_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scaled(0.5f);
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
    private boolean invisible;
    public long lastHitTime;
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

    public ArmorStandEntity(World world, double x, double y, double z) {
        this((EntityType<? extends ArmorStandEntity>)EntityType.ARMOR_STAND, world);
        this.setPosition(x, y, z);
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
            case HAND: {
                return this.heldItems.get(slot.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(slot.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        switch (slot.getType()) {
            case HAND: {
                this.onEquipStack(stack);
                this.emitGameEvent(null, GameEvent.ARMOR_STAND_ADD_ITEM);
                this.heldItems.set(slot.getEntitySlotId(), stack);
                break;
            }
            case ARMOR: {
                this.onEquipStack(stack);
                this.emitGameEvent(null, GameEvent.ARMOR_STAND_ADD_ITEM);
                this.armorItems.set(slot.getEntitySlotId(), stack);
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty() && !this.isSlotDisabled(equipmentSlot);
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
        int i;
        ListTag listTag;
        super.readCustomDataFromTag(tag);
        if (tag.contains("ArmorItems", 9)) {
            listTag = tag.getList("ArmorItems", 10);
            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
            }
        }
        if (tag.contains("HandItems", 9)) {
            listTag = tag.getList("HandItems", 10);
            for (i = 0; i < this.heldItems.size(); ++i) {
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

    private void deserializePose(CompoundTag tag) {
        ListTag listTag = tag.getList("Head", 5);
        this.setHeadRotation(listTag.isEmpty() ? DEFAULT_HEAD_ROTATION : new EulerAngle(listTag));
        ListTag listTag2 = tag.getList("Body", 5);
        this.setBodyRotation(listTag2.isEmpty() ? DEFAULT_BODY_ROTATION : new EulerAngle(listTag2));
        ListTag listTag3 = tag.getList("LeftArm", 5);
        this.setLeftArmRotation(listTag3.isEmpty() ? DEFAULT_LEFT_ARM_ROTATION : new EulerAngle(listTag3));
        ListTag listTag4 = tag.getList("RightArm", 5);
        this.setRightArmRotation(listTag4.isEmpty() ? DEFAULT_RIGHT_ARM_ROTATION : new EulerAngle(listTag4));
        ListTag listTag5 = tag.getList("LeftLeg", 5);
        this.setLeftLegRotation(listTag5.isEmpty() ? DEFAULT_LEFT_LEG_ROTATION : new EulerAngle(listTag5));
        ListTag listTag6 = tag.getList("RightLeg", 5);
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
        List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox(), RIDEABLE_MINECART_PREDICATE);
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!(this.squaredDistanceTo(entity) <= 0.2)) continue;
            entity.pushAwayFrom(this);
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.isMarker() || itemStack.isOf(Items.NAME_TAG)) {
            return ActionResult.PASS;
        }
        if (player.isSpectator()) {
            return ActionResult.SUCCESS;
        }
        if (player.world.isClient) {
            return ActionResult.CONSUME;
        }
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        if (itemStack.isEmpty()) {
            EquipmentSlot equipmentSlot3;
            EquipmentSlot equipmentSlot2 = this.slotFromPosition(hitPos);
            EquipmentSlot equipmentSlot4 = equipmentSlot3 = this.isSlotDisabled(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
            if (this.hasStackEquipped(equipmentSlot3) && this.equip(player, equipmentSlot3, itemStack, hand)) {
                return ActionResult.SUCCESS;
            }
        } else {
            if (this.isSlotDisabled(equipmentSlot)) {
                return ActionResult.FAIL;
            }
            if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
                return ActionResult.FAIL;
            }
            if (this.equip(player, equipmentSlot, itemStack, hand)) {
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private EquipmentSlot slotFromPosition(Vec3d vec3d) {
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

    private boolean isSlotDisabled(EquipmentSlot slot) {
        return (this.disabledSlots & 1 << slot.getArmorStandSlotId()) != 0 || slot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms();
    }

    private boolean equip(PlayerEntity player, EquipmentSlot slot, ItemStack stack, Hand hand) {
        ItemStack itemStack = this.getEquippedStack(slot);
        if (!itemStack.isEmpty() && (this.disabledSlots & 1 << slot.getArmorStandSlotId() + 8) != 0) {
            return false;
        }
        if (itemStack.isEmpty() && (this.disabledSlots & 1 << slot.getArmorStandSlotId() + 16) != 0) {
            return false;
        }
        if (player.getAbilities().creativeMode && itemStack.isEmpty() && !stack.isEmpty()) {
            ItemStack itemStack2 = stack.copy();
            itemStack2.setCount(1);
            this.equipStack(slot, itemStack2);
            return true;
        }
        if (!stack.isEmpty() && stack.getCount() > 1) {
            if (!itemStack.isEmpty()) {
                return false;
            }
            ItemStack itemStack2 = stack.copy();
            itemStack2.setCount(1);
            this.equipStack(slot, itemStack2);
            stack.decrement(1);
            return true;
        }
        this.equipStack(slot, stack);
        player.setStackInHand(hand, itemStack);
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.world.isClient || this.isRemoved()) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals(source)) {
            this.kill();
            return false;
        }
        if (this.isInvulnerableTo(source) || this.invisible || this.isMarker()) {
            return false;
        }
        if (source.isExplosive()) {
            this.onBreak(source);
            this.kill();
            return false;
        }
        if (DamageSource.IN_FIRE.equals(source)) {
            if (this.isOnFire()) {
                this.updateHealth(source, 0.15f);
            } else {
                this.setOnFireFor(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5f) {
            this.updateHealth(source, 4.0f);
            return false;
        }
        boolean bl = source.getSource() instanceof PersistentProjectileEntity;
        boolean bl2 = bl && ((PersistentProjectileEntity)source.getSource()).getPierceLevel() > 0;
        boolean bl3 = "player".equals(source.getName());
        if (!bl3 && !bl) {
            return false;
        }
        if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity)source.getAttacker()).getAbilities().allowModifyWorld) {
            return false;
        }
        if (source.isSourceCreativePlayer()) {
            this.playBreakSound();
            this.emitGameEvent(source.getAttacker(), GameEvent.ENTITY_HIT);
            this.spawnBreakParticles();
            this.kill();
            return bl2;
        }
        long l = this.world.getTime();
        if (l - this.lastHitTime <= 5L || bl) {
            this.breakAndDropItem(source);
            this.spawnBreakParticles();
            this.kill();
        } else {
            this.world.sendEntityStatus(this, (byte)32);
            this.emitGameEvent(source.getAttacker(), GameEvent.ENTITY_HIT);
            this.lastHitTime = l;
        }
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 32) {
            if (this.world.isClient) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3f, 1.0f, false);
                this.lastHitTime = this.world.getTime();
            }
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d) || d == 0.0) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    private void spawnBreakParticles() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.getX(), this.getBodyY(0.6666666666666666), this.getZ(), 10, this.getWidth() / 4.0f, this.getHeight() / 4.0f, this.getWidth() / 4.0f, 0.05);
        }
    }

    private void updateHealth(DamageSource damageSource, float amount) {
        float f = this.getHealth();
        if ((f -= amount) <= 0.5f) {
            this.onBreak(damageSource);
            this.kill();
        } else {
            this.setHealth(f);
        }
    }

    private void breakAndDropItem(DamageSource damageSource) {
        Block.dropStack(this.world, this.getBlockPos(), new ItemStack(Items.ARMOR_STAND));
        this.onBreak(damageSource);
    }

    private void onBreak(DamageSource damageSource) {
        ItemStack itemStack;
        int i;
        this.playBreakSound();
        this.emitGameEvent(damageSource.getAttacker(), GameEvent.BLOCK_DESTROY);
        this.drop(damageSource);
        for (i = 0; i < this.heldItems.size(); ++i) {
            itemStack = this.heldItems.get(i);
            if (itemStack.isEmpty()) continue;
            Block.dropStack(this.world, this.getBlockPos().up(), itemStack);
            this.heldItems.set(i, ItemStack.EMPTY);
        }
        for (i = 0; i < this.armorItems.size(); ++i) {
            itemStack = this.armorItems.get(i);
            if (itemStack.isEmpty()) continue;
            Block.dropStack(this.world, this.getBlockPos().up(), itemStack);
            this.armorItems.set(i, ItemStack.EMPTY);
        }
    }

    private void playBreakSound() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0f, 1.0f);
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        this.prevBodyYaw = this.prevYaw;
        this.bodyYaw = this.yaw;
        return 0.0f;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * (this.isBaby() ? 0.5f : 0.9f);
    }

    @Override
    public double getHeightOffset() {
        return this.isMarker() ? 0.0 : (double)0.1f;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (!this.canClip()) {
            return;
        }
        super.travel(movementInput);
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
        this.setInvisible(this.invisible);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        super.setInvisible(invisible);
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
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

    private void setSmall(boolean small) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 1, small));
    }

    public boolean isSmall() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 1) != 0;
    }

    private void setShowArms(boolean showArms) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 4, showArms));
    }

    public boolean shouldShowArms() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 4) != 0;
    }

    private void setHideBasePlate(boolean hideBasePlate) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 8, hideBasePlate));
    }

    public boolean shouldHideBasePlate() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 8) != 0;
    }

    private void setMarker(boolean marker) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 16, marker));
    }

    public boolean isMarker() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 0x10) != 0;
    }

    private byte setBitField(byte value, int bitField, boolean set) {
        value = set ? (byte)(value | bitField) : (byte)(value & ~bitField);
        return value;
    }

    public void setHeadRotation(EulerAngle angle) {
        this.headRotation = angle;
        this.dataTracker.set(TRACKER_HEAD_ROTATION, angle);
    }

    public void setBodyRotation(EulerAngle angle) {
        this.bodyRotation = angle;
        this.dataTracker.set(TRACKER_BODY_ROTATION, angle);
    }

    public void setLeftArmRotation(EulerAngle angle) {
        this.leftArmRotation = angle;
        this.dataTracker.set(TRACKER_LEFT_ARM_ROTATION, angle);
    }

    public void setRightArmRotation(EulerAngle angle) {
        this.rightArmRotation = angle;
        this.dataTracker.set(TRACKER_RIGHT_ARM_ROTATION, angle);
    }

    public void setLeftLegRotation(EulerAngle angle) {
        this.leftLegRotation = angle;
        this.dataTracker.set(TRACKER_LEFT_LEG_ROTATION, angle);
    }

    public void setRightLegRotation(EulerAngle angle) {
        this.rightLegRotation = angle;
        this.dataTracker.set(TRACKER_RIGHT_LEG_ROTATION, angle);
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
    public boolean handleAttack(Entity attacker) {
        return attacker instanceof PlayerEntity && !this.world.canPlayerModifyAt((PlayerEntity)attacker, this.getBlockPos());
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected SoundEvent getFallSound(int distance) {
        return SoundEvents.ENTITY_ARMOR_STAND_FALL;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ARMOR_STAND_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ARMOR_STAND_BREAK;
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
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
    public boolean isMobOrPlayer() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.getDimensions(this.isMarker());
    }

    private EntityDimensions getDimensions(boolean marker) {
        if (marker) {
            return MARKER_DIMENSIONS;
        }
        return this.isBaby() ? SMALL_DIMENSIONS : this.getType().getDimensions();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d getClientCameraPosVec(float tickDelta) {
        if (this.isMarker()) {
            Box box = this.getDimensions(false).getBoxAt(this.getPos());
            BlockPos blockPos = this.getBlockPos();
            int i = Integer.MIN_VALUE;
            for (BlockPos blockPos2 : BlockPos.iterate(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ))) {
                int j = Math.max(this.world.getLightLevel(LightType.BLOCK, blockPos2), this.world.getLightLevel(LightType.SKY, blockPos2));
                if (j == 15) {
                    return Vec3d.ofCenter(blockPos2);
                }
                if (j <= i) continue;
                i = j;
                blockPos = blockPos2.toImmutable();
            }
            return Vec3d.ofCenter(blockPos);
        }
        return super.getClientCameraPosVec(tickDelta);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.ARMOR_STAND);
    }
}

