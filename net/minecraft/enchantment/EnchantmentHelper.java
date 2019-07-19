/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class EnchantmentHelper {
    public static int getLevel(Enchantment enchantment, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
        ListTag listTag = itemStack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Identifier identifier2 = Identifier.tryParse(compoundTag.getString("id"));
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            return compoundTag.getInt("lvl");
        }
        return 0;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
        LinkedHashMap<Enchantment, Integer> map = Maps.newLinkedHashMap();
        ListTag listTag = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> map.put((Enchantment)enchantment, compoundTag.getInt("lvl")));
        }
        return map;
    }

    public static void set(Map<Enchantment, Integer> map, ItemStack itemStack) {
        ListTag listTag = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int i = entry.getValue();
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
            compoundTag.putShort("lvl", (short)i);
            listTag.add(compoundTag);
            if (itemStack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(itemStack, new InfoEnchantment(enchantment, i));
        }
        if (listTag.isEmpty()) {
            itemStack.removeSubTag("Enchantments");
        } else if (itemStack.getItem() != Items.ENCHANTED_BOOK) {
            itemStack.putSubTag("Enchantments", listTag);
        }
    }

    private static void accept(Consumer consumer, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        ListTag listTag = itemStack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getCompound(i).getString("id");
            int j = listTag.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(enchantment -> consumer.accept((Enchantment)enchantment, j));
        }
    }

    private static void accept(Consumer consumer, Iterable<ItemStack> iterable) {
        for (ItemStack itemStack : iterable) {
            EnchantmentHelper.accept(consumer, itemStack);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> iterable, DamageSource damageSource) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.accept((Enchantment enchantment, int i) -> mutableInt.add(enchantment.getProtectionAmount(i, damageSource)), iterable);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack itemStack, EntityGroup entityGroup) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.accept((Enchantment enchantment, int i) -> mutableFloat.add(enchantment.getAttackDamage(i, entityGroup)), itemStack);
        return mutableFloat.floatValue();
    }

    public static float getSweepingMultiplier(LivingEntity livingEntity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, livingEntity);
        if (i > 0) {
            return SweepingEnchantment.getMultiplier(i);
        }
        return 0.0f;
    }

    public static void onUserDamaged(LivingEntity livingEntity, Entity entity) {
        Consumer consumer = (enchantment, i) -> enchantment.onUserDamaged(livingEntity, entity, i);
        if (livingEntity != null) {
            EnchantmentHelper.accept(consumer, livingEntity.getItemsEquipped());
        }
        if (entity instanceof PlayerEntity) {
            EnchantmentHelper.accept(consumer, livingEntity.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity livingEntity, Entity entity) {
        Consumer consumer = (enchantment, i) -> enchantment.onTargetDamaged(livingEntity, entity, i);
        if (livingEntity != null) {
            EnchantmentHelper.accept(consumer, livingEntity.getItemsEquipped());
        }
        if (livingEntity instanceof PlayerEntity) {
            EnchantmentHelper.accept(consumer, livingEntity.getMainHandStack());
        }
    }

    public static int getEquipmentLevel(Enchantment enchantment, LivingEntity livingEntity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(livingEntity).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack itemStack : iterable) {
            int j = EnchantmentHelper.getLevel(enchantment, itemStack);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockback(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.KNOCKBACK, livingEntity);
    }

    public static int getFireAspect(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_ASPECT, livingEntity);
    }

    public static int getRespiration(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.RESPIRATION, livingEntity);
    }

    public static int getDepthStrider(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.DEPTH_STRIDER, livingEntity);
    }

    public static int getEfficiency(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.EFFICIENCY, livingEntity);
    }

    public static int getLuckOfTheSea(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.LUCK_OF_THE_SEA, itemStack);
    }

    public static int getLure(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.LURE, itemStack);
    }

    public static int getLooting(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, livingEntity);
    }

    public static boolean hasAquaAffinity(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, livingEntity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity livingEntity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, livingEntity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.BINDING_CURSE, itemStack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.VANISHING_CURSE, itemStack) > 0;
    }

    public static int getLoyalty(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.LOYALTY, itemStack);
    }

    public static int getRiptide(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.RIPTIDE, itemStack);
    }

    public static boolean hasChanneling(ItemStack itemStack) {
        return EnchantmentHelper.getLevel(Enchantments.CHANNELING, itemStack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomEnchantedEquipment(Enchantment enchantment, LivingEntity livingEntity) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(livingEntity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || EnchantmentHelper.getLevel(enchantment, itemStack) <= 0) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(livingEntity.getRandom().nextInt(list.size()));
    }

    public static int calculateEnchantmentPower(Random random, int i, int j, ItemStack itemStack) {
        Item item = itemStack.getItem();
        int k = item.getEnchantability();
        if (k <= 0) {
            return 0;
        }
        if (j > 15) {
            j = 15;
        }
        int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);
        if (i == 0) {
            return Math.max(l / 3, 1);
        }
        if (i == 1) {
            return l * 2 / 3 + 1;
        }
        return Math.max(l, j * 2);
    }

    public static ItemStack enchant(Random random, ItemStack itemStack, int i, boolean bl) {
        boolean bl2;
        List<InfoEnchantment> list = EnchantmentHelper.getEnchantments(random, itemStack, i, bl);
        boolean bl3 = bl2 = itemStack.getItem() == Items.BOOK;
        if (bl2) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (InfoEnchantment infoEnchantment : list) {
            if (bl2) {
                EnchantedBookItem.addEnchantment(itemStack, infoEnchantment);
                continue;
            }
            itemStack.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
        }
        return itemStack;
    }

    public static List<InfoEnchantment> getEnchantments(Random random, ItemStack itemStack, int i, boolean bl) {
        ArrayList<InfoEnchantment> list = Lists.newArrayList();
        Item item = itemStack.getItem();
        int j = item.getEnchantability();
        if (j <= 0) {
            return list;
        }
        i += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<InfoEnchantment> list2 = EnchantmentHelper.getHighestApplicableEnchantmentsAtPower(i = MathHelper.clamp(Math.round((float)i + (float)i * f), 1, Integer.MAX_VALUE), itemStack, bl);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= i) {
                EnchantmentHelper.remove(list2, Util.method_20793(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                i /= 2;
            }
        }
        return list;
    }

    public static void remove(List<InfoEnchantment> list, InfoEnchantment infoEnchantment) {
        Iterator<InfoEnchantment> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (infoEnchantment.enchantment.isDifferent(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean contains(Collection<Enchantment> collection, Enchantment enchantment) {
        for (Enchantment enchantment2 : collection) {
            if (enchantment2.isDifferent(enchantment)) continue;
            return false;
        }
        return true;
    }

    public static List<InfoEnchantment> getHighestApplicableEnchantmentsAtPower(int i, ItemStack itemStack, boolean bl) {
        ArrayList<InfoEnchantment> list = Lists.newArrayList();
        Item item = itemStack.getItem();
        boolean bl2 = itemStack.getItem() == Items.BOOK;
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasure() && !bl || !enchantment.type.isAcceptableItem(item) && !bl2) continue;
            for (int j = enchantment.getMaximumLevel(); j > enchantment.getMinimumLevel() - 1; --j) {
                if (i < enchantment.getMinimumPower(j) || i > enchantment.getMaximumPower(j)) continue;
                list.add(new InfoEnchantment(enchantment, j));
                continue block0;
            }
        }
        return list;
    }

    @FunctionalInterface
    static interface Consumer {
        public void accept(Enchantment var1, int var2);
    }
}

