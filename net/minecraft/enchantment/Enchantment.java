/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public abstract class Enchantment {
    private final EquipmentSlot[] slotTypes;
    private final Weight weight;
    @Nullable
    public EnchantmentTarget type;
    @Nullable
    protected String translationName;

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Enchantment byRawId(int i) {
        return (Enchantment)Registry.ENCHANTMENT.get(i);
    }

    protected Enchantment(Weight weight, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) {
        this.weight = weight;
        this.type = enchantmentTarget;
        this.slotTypes = equipmentSlots;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity livingEntity) {
        EnumMap<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot equipmentSlot : this.slotTypes) {
            ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            map.put(equipmentSlot, itemStack);
        }
        return map;
    }

    public Weight getWeight() {
        return this.weight;
    }

    public int getMinimumLevel() {
        return 1;
    }

    public int getMaximumLevel() {
        return 1;
    }

    public int getMinimumPower(int i) {
        return 1 + i * 10;
    }

    public int getProtectionAmount(int i, DamageSource damageSource) {
        return 0;
    }

    public float getAttackDamage(int i, EntityGroup entityGroup) {
        return 0.0f;
    }

    public final boolean isDifferent(Enchantment enchantment) {
        return this.differs(enchantment) && enchantment.differs(this);
    }

    protected boolean differs(Enchantment enchantment) {
        return this != enchantment;
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationName == null) {
            this.translationName = SystemUtil.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
        }
        return this.translationName;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public Component getTextComponent(int i) {
        TranslatableComponent component = new TranslatableComponent(this.getTranslationKey(), new Object[0]);
        if (this.isCursed()) {
            component.applyFormat(ChatFormat.RED);
        } else {
            component.applyFormat(ChatFormat.GRAY);
        }
        if (i != 1 || this.getMaximumLevel() != 1) {
            component.append(" ").append(new TranslatableComponent("enchantment.level." + i, new Object[0]));
        }
        return component;
    }

    public boolean isAcceptableItem(ItemStack itemStack) {
        return this.type.isAcceptableItem(itemStack.getItem());
    }

    public void onTargetDamaged(LivingEntity livingEntity, Entity entity, int i) {
    }

    public void onUserDamaged(LivingEntity livingEntity, Entity entity, int i) {
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public static enum Weight {
        COMMON(30),
        UNCOMMON(10),
        RARE(3),
        VERY_RARE(1);

        private final int weight;

        private Weight(int j) {
            this.weight = j;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}

