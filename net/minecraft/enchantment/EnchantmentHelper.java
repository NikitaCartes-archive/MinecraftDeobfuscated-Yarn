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
import java.util.function.Predicate;
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
    public static int getLevel(Enchantment enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
        ListTag listTag = stack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Identifier identifier2 = Identifier.tryParse(compoundTag.getString("id"));
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            return MathHelper.clamp(compoundTag.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        ListTag listTag = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentTag(stack) : stack.getEnchantments();
        return EnchantmentHelper.getEnchantments(listTag);
    }

    public static Map<Enchantment, Integer> getEnchantments(ListTag tag) {
        LinkedHashMap<Enchantment, Integer> map = Maps.newLinkedHashMap();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> map.put((Enchantment)enchantment, compoundTag.getInt("lvl")));
        }
        return map;
    }

    public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        ListTag listTag = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int i = entry.getValue();
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
            compoundTag.putShort("lvl", (short)i);
            listTag.add(compoundTag);
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(stack, new InfoEnchantment(enchantment, i));
        }
        if (listTag.isEmpty()) {
            stack.removeSubTag("Enchantments");
        } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.putSubTag("Enchantments", listTag);
        }
    }

    private static void accept(Consumer enchantmentHandler, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ListTag listTag = stack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getCompound(i).getString("id");
            int j = listTag.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(enchantment -> enchantmentHandler.accept((Enchantment)enchantment, j));
        }
    }

    private static void accept(Consumer enchantmentHandler, Iterable<ItemStack> stacks) {
        for (ItemStack itemStack : stacks) {
            EnchantmentHelper.accept(enchantmentHandler, itemStack);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.accept((Enchantment enchantment, int i) -> mutableInt.add(enchantment.getProtectionAmount(i, source)), equipment);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack stack, EntityGroup group) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.accept((Enchantment enchantment, int i) -> mutableFloat.add(enchantment.getAttackDamage(i, group)), stack);
        return mutableFloat.floatValue();
    }

    public static float getSweepingMultiplier(LivingEntity entity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, entity);
        if (i > 0) {
            return SweepingEnchantment.getMultiplier(i);
        }
        return 0.0f;
    }

    public static void onUserDamaged(LivingEntity user, Entity attacker) {
        Consumer consumer = (enchantment, i) -> enchantment.onUserDamaged(user, attacker, i);
        if (user != null) {
            EnchantmentHelper.accept(consumer, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            EnchantmentHelper.accept(consumer, user.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity user, Entity target) {
        Consumer consumer = (enchantment, i) -> enchantment.onTargetDamaged(user, target, i);
        if (user != null) {
            EnchantmentHelper.accept(consumer, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            EnchantmentHelper.accept(consumer, user.getMainHandStack());
        }
    }

    public static int getEquipmentLevel(Enchantment ench, LivingEntity entity) {
        Collection<ItemStack> iterable = ench.getEquipment(entity).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack itemStack : iterable) {
            int j = EnchantmentHelper.getLevel(ench, itemStack);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockback(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.KNOCKBACK, entity);
    }

    public static int getFireAspect(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_ASPECT, entity);
    }

    public static int getRespiration(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.RESPIRATION, entity);
    }

    public static int getDepthStrider(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.DEPTH_STRIDER, entity);
    }

    public static int getEfficiency(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.EFFICIENCY, entity);
    }

    public static int getLuckOfTheSea(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LUCK_OF_THE_SEA, stack);
    }

    public static int getLure(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LURE, stack);
    }

    public static int getLooting(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity);
    }

    public static boolean hasAquaAffinity(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.BINDING_CURSE, stack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
    }

    public static int getLoyalty(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LOYALTY, stack);
    }

    public static int getRiptide(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.RIPTIDE, stack);
    }

    public static boolean hasChanneling(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.CHANNELING, stack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomEnchantedEquipment(Enchantment enchantment, LivingEntity livingEntity) {
        return EnchantmentHelper.method_24365(enchantment, livingEntity, itemStack -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> method_24365(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(livingEntity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || EnchantmentHelper.getLevel(enchantment, itemStack) <= 0 || !predicate.test(itemStack)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(livingEntity.getRandom().nextInt(list.size()));
    }

    public static int calculateEnchantmentPower(Random random, int num, int enchantmentPower, ItemStack rstack) {
        Item item = rstack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return 0;
        }
        if (enchantmentPower > 15) {
            enchantmentPower = 15;
        }
        int j = random.nextInt(8) + 1 + (enchantmentPower >> 1) + random.nextInt(enchantmentPower + 1);
        if (num == 0) {
            return Math.max(j / 3, 1);
        }
        if (num == 1) {
            return j * 2 / 3 + 1;
        }
        return Math.max(j, enchantmentPower * 2);
    }

    public static ItemStack enchant(Random random, ItemStack target, int level, boolean hasTreasure) {
        boolean bl;
        List<InfoEnchantment> list = EnchantmentHelper.getEnchantments(random, target, level, hasTreasure);
        boolean bl2 = bl = target.getItem() == Items.BOOK;
        if (bl) {
            target = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (InfoEnchantment infoEnchantment : list) {
            if (bl) {
                EnchantedBookItem.addEnchantment(target, infoEnchantment);
                continue;
            }
            target.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
        }
        return target;
    }

    public static List<InfoEnchantment> getEnchantments(Random random, ItemStack stack, int level, boolean hasTreasure) {
        ArrayList<InfoEnchantment> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<InfoEnchantment> list2 = EnchantmentHelper.getHighestApplicableEnchantmentsAtPower(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), stack, hasTreasure);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= level) {
                EnchantmentHelper.remove(list2, Util.getLast(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                level /= 2;
            }
        }
        return list;
    }

    public static void remove(List<InfoEnchantment> infos, InfoEnchantment info) {
        Iterator<InfoEnchantment> iterator = infos.iterator();
        while (iterator.hasNext()) {
            if (info.enchantment.isDifferent(iterator.next().enchantment)) continue;
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

    public static List<InfoEnchantment> getHighestApplicableEnchantmentsAtPower(int power, ItemStack stack, boolean bl) {
        ArrayList<InfoEnchantment> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl2 = stack.getItem() == Items.BOOK;
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasure() && !bl || !enchantment.type.isAcceptableItem(item) && !bl2) continue;
            for (int i = enchantment.getMaximumLevel(); i > enchantment.getMinimumLevel() - 1; --i) {
                if (power < enchantment.getMinimumPower(i) || power > enchantment.getMaximumPower(i)) continue;
                list.add(new InfoEnchantment(enchantment, i));
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

