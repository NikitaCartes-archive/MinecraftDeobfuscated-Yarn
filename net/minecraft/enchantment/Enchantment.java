/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public abstract class Enchantment {
    private final EquipmentSlot[] slotTypes;
    private final Rarity rarity;
    @Nullable
    public EnchantmentTarget type;
    @Nullable
    protected String translationKey;

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Enchantment byRawId(int id) {
        return (Enchantment)Registry.ENCHANTMENT.get(id);
    }

    protected Enchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        this.rarity = weight;
        this.type = type;
        this.slotTypes = slotTypes;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
        EnumMap<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot equipmentSlot : this.slotTypes) {
            ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            map.put(equipmentSlot, itemStack);
        }
        return map;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getMinimumLevel() {
        return 1;
    }

    public int getMaximumLevel() {
        return 1;
    }

    public int getMinimumPower(int level) {
        return 1 + level * 10;
    }

    public int getMaximumPower(int level) {
        return this.getMinimumPower(level) + 5;
    }

    public int getProtectionAmount(int level, DamageSource source) {
        return 0;
    }

    public float getAttackDamage(int level, EntityGroup group) {
        return 0.0f;
    }

    /**
     * Returns whether this enchantment can exist on an item stack with the
     * {@code other} enchantment and the {@code other} enchantment can exist
     * with this enchantment.
     */
    public final boolean canCombine(Enchantment other) {
        return this.canAccept(other) && other.canAccept(this);
    }

    /**
     * Returns whether this enchantment can exist on an item stack with the
     * {@code other} enchantment.
     */
    protected boolean canAccept(Enchantment other) {
        return this != other;
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public Text getName(int level) {
        TranslatableText text = new TranslatableText(this.getTranslationKey(), new Object[0]);
        if (this.isCursed()) {
            text.formatted(Formatting.RED);
        } else {
            text.formatted(Formatting.GRAY);
        }
        if (level != 1 || this.getMaximumLevel() != 1) {
            text.append(" ").append(new TranslatableText("enchantment.level." + level, new Object[0]));
        }
        return text;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return this.type.isAcceptableItem(stack.getItem());
    }

    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
    }

    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    /**
     * Returns whether this enchantment will appear in the enchanted book trade
     * offers of librarian villagers.
     */
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    /**
     * Returns whether this enchantment will appear in the enchanting table or
     * loots with random enchant function.
     */
    public boolean isAvailableForRandomSelection() {
        return true;
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        private Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}

