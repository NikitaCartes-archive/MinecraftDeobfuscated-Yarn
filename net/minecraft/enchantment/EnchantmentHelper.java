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
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class EnchantmentHelper {
    /**
     * Gets the level of an enchantment on an item stack.
     */
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

    /**
     * Gets the enchantments on an item stack.
     * 
     * <p>For enchanted books, it retrieves from the item stack's stored than
     * regular enchantments.
     * 
     * @see net.minecraft.item.ItemStack#getEnchantments()
     * @see net.minecraft.item.EnchantedBookItem#getEnchantmentTag(net.minecraft.item.ItemStack)
     */
    public static Map<Enchantment, Integer> get(ItemStack stack) {
        ListTag listTag = stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentTag(stack) : stack.getEnchantments();
        return EnchantmentHelper.fromTag(listTag);
    }

    /**
     * Loads enchantments from an NBT list.
     */
    public static Map<Enchantment, Integer> fromTag(ListTag tag) {
        LinkedHashMap<Enchantment, Integer> map = Maps.newLinkedHashMap();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> map.put((Enchantment)enchantment, compoundTag.getInt("lvl")));
        }
        return map;
    }

    /**
     * Sets the enchantments on an item stack.
     * 
     * <p>For enchanted books, it sets the enchantments to the item stack's
     * stored enchantments than regular enchantments.
     * 
     * @see net.minecraft.item.ItemStack#getEnchantments()
     * @see net.minecraft.item.EnchantedBookItem#getEnchantmentTag(net.minecraft.item.ItemStack)
     */
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
            if (!stack.isOf(Items.ENCHANTED_BOOK)) continue;
            EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
        }
        if (listTag.isEmpty()) {
            stack.removeSubTag("Enchantments");
        } else if (!stack.isOf(Items.ENCHANTED_BOOK)) {
            stack.putSubTag("Enchantments", listTag);
        }
    }

    private static void forEachEnchantment(Consumer consumer, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ListTag listTag = stack.getEnchantments();
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getCompound(i).getString("id");
            int j = listTag.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(enchantment -> consumer.accept((Enchantment)enchantment, j));
        }
    }

    private static void forEachEnchantment(Consumer consumer, Iterable<ItemStack> stacks) {
        for (ItemStack itemStack : stacks) {
            EnchantmentHelper.forEachEnchantment(consumer, itemStack);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.forEachEnchantment((Enchantment enchantment, int level) -> mutableInt.add(enchantment.getProtectionAmount(level, source)), equipment);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack stack, EntityGroup group) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.forEachEnchantment((Enchantment enchantment, int level) -> mutableFloat.add(enchantment.getAttackDamage(level, group)), stack);
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
        Consumer consumer = (enchantment, level) -> enchantment.onUserDamaged(user, attacker, level);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity user, Entity target) {
        Consumer consumer = (enchantment, level) -> enchantment.onTargetDamaged(user, target, level);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(consumer, user.getMainHandStack());
        }
    }

    /**
     * Returns the highest level of the passed enchantment in the enchantment's
     * applicable equipment slots' item stacks.
     * 
     * @param enchantment the enchantment
     * @param entity the entity whose equipment slots are checked
     */
    public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(entity).values();
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

    public static boolean hasSoulSpeed(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
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

    /**
     * Returns a pair of an equipment slot and the item stack in the supplied
     * entity's slot, indicating the item stack has the enchantment supplied.
     * 
     * <p>If multiple equipment slots' item stacks are valid, a random pair is
     * returned.
     * 
     * @param enchantment the enchantment the equipped item stack must have
     * @param entity the entity to choose equipments from
     */
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
        return EnchantmentHelper.chooseEquipmentWith(enchantment, entity, stack -> true);
    }

    /**
     * Returns a pair of an equipment slot and the item stack in the supplied
     * entity's slot, indicating the item stack has the enchantment supplied
     * and fulfills the extra condition.
     * 
     * <p>If multiple equipment slots' item stacks are valid, a random pair is
     * returned.
     * 
     * @param enchantment the enchantment the equipped item stack must have
     * @param entity the entity to choose equipments from
     * @param condition extra conditions for the item stack to pass for selection
     */
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || EnchantmentHelper.getLevel(enchantment, itemStack) <= 0 || !condition.test(itemStack)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(entity.getRandom().nextInt(list.size()));
    }

    /**
     * Returns the required experience level for an enchanting option in the
     * enchanting table's screen, or the enchantment screen.
     * 
     * @param random the random, which guarantees consistent results with the same seed
     * @param slotIndex the index of the enchanting option
     * @param bookshelfCount the number of bookshelves
     * @param stack the item stack to enchant
     */
    public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return 0;
        }
        if (bookshelfCount > 15) {
            bookshelfCount = 15;
        }
        int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
        if (slotIndex == 0) {
            return Math.max(j / 3, 1);
        }
        if (slotIndex == 1) {
            return j * 2 / 3 + 1;
        }
        return Math.max(j, bookshelfCount * 2);
    }

    /**
     * Enchants the {@code target} item stack and returns it.
     * 
     * @param random the seed
     * @param target the item stack to enchant
     * @param level the experience level
     * @param treasureAllowed whether treasure enchantments may appear
     */
    public static ItemStack enchant(Random random, ItemStack target, int level, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(random, target, level, treasureAllowed);
        boolean bl = target.isOf(Items.BOOK);
        if (bl) {
            target = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
            if (bl) {
                EnchantedBookItem.addEnchantment(target, enchantmentLevelEntry);
                continue;
            }
            target.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
        }
        return target;
    }

    /**
     * Generate the enchantments for enchanting the {@code stack}.
     */
    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= level) {
                EnchantmentHelper.removeConflicts(list2, Util.getLast(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                level /= 2;
            }
        }
        return list;
    }

    /**
     * Remove entries conflicting with the picked entry from the possible
     * entries.
     * 
     * @param possibleEntries the possible entries
     * @param pickedEntry the picked entry
     */
    public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
        Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();
        while (iterator.hasNext()) {
            if (pickedEntry.enchantment.canCombine(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    /**
     * Returns whether the {@code candidate} enchantment is compatible with the
     * {@code existing} enchantments.
     */
    public static boolean isCompatible(Collection<Enchantment> existing, Enchantment candidate) {
        for (Enchantment enchantment : existing) {
            if (enchantment.canCombine(candidate)) continue;
            return false;
        }
        return true;
    }

    /**
     * Gets all the possible entries for enchanting the {@code stack} at the
     * given {@code power}.
     */
    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !enchantment.type.isAcceptableItem(item) && !bl) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                list.add(new EnchantmentLevelEntry(enchantment, i));
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

