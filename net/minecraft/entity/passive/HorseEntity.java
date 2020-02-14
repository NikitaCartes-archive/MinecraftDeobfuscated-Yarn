/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HorseEntity
extends HorseBaseEntity {
    private static final UUID HORSE_ARMOR_BONUS_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String[] HORSE_TEX = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] HORSE_TEX_ID = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] HORSE_MARKING_TEX = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] HORSE_MARKING_TEX_ID = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
    @Nullable
    private String textureLocation;
    private final String[] textureLayers = new String[2];

    public HorseEntity(EntityType<? extends HorseEntity> entityType, World world) {
        super((EntityType<? extends HorseBaseEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
        if (!this.items.getInvStack(1).isEmpty()) {
            tag.put("ArmorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
        }
    }

    public ItemStack getArmorType() {
        return this.getEquippedStack(EquipmentSlot.CHEST);
    }

    private void equipArmor(ItemStack stack) {
        this.equipStack(EquipmentSlot.CHEST, stack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ItemStack itemStack;
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.contains("ArmorItem", 10) && !(itemStack = ItemStack.fromTag(tag.getCompound("ArmorItem"))).isEmpty() && this.canEquip(itemStack)) {
            this.items.setInvStack(1, itemStack);
        }
        this.updateSaddle();
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
        this.clearTextureInfo();
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    private void clearTextureInfo() {
        this.textureLocation = null;
    }

    @Environment(value=EnvType.CLIENT)
    private void initTextureInfo() {
        int i = this.getVariant();
        int j = (i & 0xFF) % 7;
        int k = ((i & 0xFF00) >> 8) % 5;
        this.textureLayers[0] = HORSE_TEX[j];
        this.textureLayers[1] = HORSE_MARKING_TEX[k];
        this.textureLocation = "horse/" + HORSE_TEX_ID[j] + HORSE_MARKING_TEX_ID[k];
    }

    @Environment(value=EnvType.CLIENT)
    public String getTextureLocation() {
        if (this.textureLocation == null) {
            this.initTextureInfo();
        }
        return this.textureLocation;
    }

    @Environment(value=EnvType.CLIENT)
    public String[] getTextureLayers() {
        if (this.textureLocation == null) {
            this.initTextureInfo();
        }
        return this.textureLayers;
    }

    @Override
    protected void updateSaddle() {
        super.updateSaddle();
        this.setArmorTypeFromStack(this.items.getInvStack(1));
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    private void setArmorTypeFromStack(ItemStack stack) {
        this.equipArmor(stack);
        if (!this.world.isClient) {
            int i;
            this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(HORSE_ARMOR_BONUS_UUID);
            if (this.canEquip(stack) && (i = ((HorseArmorItem)stack.getItem()).getBonus()) != 0) {
                this.getAttributeInstance(EntityAttributes.ARMOR).addModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_UUID, "Horse armor bonus", (double)i, EntityAttributeModifier.Operation.ADDITION).setSerialize(false));
            }
        }
    }

    @Override
    public void onInvChange(Inventory inventory) {
        ItemStack itemStack = this.getArmorType();
        super.onInvChange(inventory);
        ItemStack itemStack2 = this.getArmorType();
        if (this.age > 20 && this.canEquip(itemStack2) && itemStack != itemStack2) {
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5f, 1.0f);
        }
    }

    @Override
    protected void playWalkSound(BlockSoundGroup group) {
        super.playWalkSound(group);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, group.getVolume() * 0.6f, group.getPitch());
        }
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(this.getChildHealthBonus());
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(this.getChildMovementSpeedBonus());
        this.getAttributeInstance(JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient && this.dataTracker.isDirty()) {
            this.dataTracker.clearDirty();
            this.clearTextureInfo();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        boolean bl;
        ItemStack itemStack = player.getStackInHand(hand);
        boolean bl2 = bl = !itemStack.isEmpty();
        if (bl && itemStack.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isBaby()) {
            if (this.isTame() && player.shouldCancelInteraction()) {
                this.openInventory(player);
                return true;
            }
            if (this.hasPassengers()) {
                return super.interactMob(player, hand);
            }
        }
        if (bl) {
            boolean bl22;
            if (this.receiveFood(player, itemStack)) {
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
                return true;
            }
            if (itemStack.useOnEntity(player, this, hand)) {
                return true;
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return true;
            }
            boolean bl3 = bl22 = !this.isBaby() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE;
            if (this.canEquip(itemStack) || bl22) {
                this.openInventory(player);
                return true;
            }
        }
        if (this.isBaby()) {
            return super.interactMob(player, hand);
        }
        this.putPlayerOnBack(player);
        return true;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        }
        if (other instanceof DonkeyEntity || other instanceof HorseEntity) {
            return this.canBreed() && ((HorseBaseEntity)other).canBreed();
        }
        return false;
    }

    @Override
    public PassiveEntity createChild(PassiveEntity mate) {
        HorseBaseEntity horseBaseEntity;
        if (mate instanceof DonkeyEntity) {
            horseBaseEntity = EntityType.MULE.create(this.world);
        } else {
            HorseEntity horseEntity = (HorseEntity)mate;
            horseBaseEntity = EntityType.HORSE.create(this.world);
            int i = this.random.nextInt(9);
            int j = i < 4 ? this.getVariant() & 0xFF : (i < 8 ? horseEntity.getVariant() & 0xFF : this.random.nextInt(7));
            int k = this.random.nextInt(5);
            j = k < 2 ? (j |= this.getVariant() & 0xFF00) : (k < 4 ? (j |= horseEntity.getVariant() & 0xFF00) : (j |= this.random.nextInt(5) << 8 & 0xFF00));
            ((HorseEntity)horseBaseEntity).setVariant(j);
        }
        this.setChildAttributes(mate, horseBaseEntity);
        return horseBaseEntity;
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack item) {
        return item.getItem() instanceof HorseArmorItem;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        int i;
        if (entityData instanceof HorseData) {
            i = ((HorseData)entityData).variant;
        } else {
            i = this.random.nextInt(7);
            entityData = new HorseData(i);
        }
        this.setVariant(i | this.random.nextInt(5) << 8);
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    public static class HorseData
    extends PassiveEntity.PassiveData {
        public final int variant;

        public HorseData(int i) {
            this.variant = i;
        }
    }
}

