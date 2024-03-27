package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
	private static final float field_38222 = 0.15F;

	/**
	 * Gets the level of an enchantment on an item stack.
	 */
	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
		return itemEnchantmentsComponent.getLevel(enchantment);
	}

	public static ItemEnchantmentsComponent apply(ItemStack stack, java.util.function.Consumer<ItemEnchantmentsComponent.Builder> applier) {
		DataComponentType<ItemEnchantmentsComponent> dataComponentType = getEnchantmentsComponentType(stack);
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.get(dataComponentType);
		if (itemEnchantmentsComponent == null) {
			return ItemEnchantmentsComponent.DEFAULT;
		} else {
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(itemEnchantmentsComponent);
			applier.accept(builder);
			ItemEnchantmentsComponent itemEnchantmentsComponent2 = builder.build();
			stack.set(dataComponentType, itemEnchantmentsComponent2);
			return itemEnchantmentsComponent2;
		}
	}

	public static boolean canHaveEnchantments(ItemStack stack) {
		return stack.contains(getEnchantmentsComponentType(stack));
	}

	public static void set(ItemStack stack, ItemEnchantmentsComponent enchantments) {
		stack.set(getEnchantmentsComponentType(stack), enchantments);
	}

	public static ItemEnchantmentsComponent getEnchantments(ItemStack stack) {
		return stack.getOrDefault(getEnchantmentsComponentType(stack), ItemEnchantmentsComponent.DEFAULT);
	}

	private static DataComponentType<ItemEnchantmentsComponent> getEnchantmentsComponentType(ItemStack stack) {
		return stack.isOf(Items.ENCHANTED_BOOK) ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS;
	}

	public static boolean hasEnchantments(ItemStack stack) {
		return !stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty()
			|| !stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty();
	}

	public static float getSweepingMultiplier(int level) {
		return 1.0F - 1.0F / (float)(level + 1);
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, ItemStack stack) {
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

		for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentsMap()) {
			consumer.accept((Enchantment)((RegistryEntry)entry.getKey()).value(), entry.getIntValue());
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			forEachEnchantment(consumer, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
		MutableInt mutableInt = new MutableInt();
		forEachEnchantment((enchantment, level) -> mutableInt.add(enchantment.getProtectionAmount(level, source)), equipment);
		return mutableInt.intValue();
	}

	public static float getAttackDamage(ItemStack stack, @Nullable EntityType<?> entityType) {
		MutableFloat mutableFloat = new MutableFloat();
		forEachEnchantment((enchantment, level) -> mutableFloat.add(enchantment.getAttackDamage(level, entityType)), stack);
		return mutableFloat.floatValue();
	}

	public static float getSweepingMultiplier(LivingEntity entity) {
		int i = getEquipmentLevel(Enchantments.SWEEPING_EDGE, entity);
		return i > 0 ? getSweepingMultiplier(i) : 0.0F;
	}

	public static float getBreachFactor(@Nullable Entity entity, float f) {
		if (entity instanceof LivingEntity livingEntity) {
			int i = getEquipmentLevel(Enchantments.BREACH, livingEntity);
			if (i > 0) {
				return BreachEnchantment.getFactor((float)i, f);
			}
		}

		return f;
	}

	public static void onUserDamaged(LivingEntity user, Entity attacker) {
		EnchantmentHelper.Consumer consumer = (enchantment, level) -> enchantment.onUserDamaged(user, attacker, level);
		if (user != null) {
			forEachEnchantment(consumer, user.getEquippedItems());
		}

		if (attacker instanceof PlayerEntity) {
			forEachEnchantment(consumer, user.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity target) {
		EnchantmentHelper.Consumer consumer = (enchantment, level) -> enchantment.onTargetDamaged(user, target, level);
		if (user != null) {
			forEachEnchantment(consumer, user.getEquippedItems());
		}

		if (user instanceof PlayerEntity) {
			forEachEnchantment(consumer, user.getMainHandStack());
		}
	}

	public static void onAttack(LivingEntity attacker, Entity target, ItemEnchantmentsComponent enchantments) {
		for (Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentsMap()) {
			((Enchantment)((RegistryEntry)entry.getKey()).value()).onAttack(attacker, target, entry.getIntValue());
		}
	}

	/**
	 * {@return the highest level of the passed enchantment in the enchantment's
	 * applicable equipment slots' item stacks}
	 * 
	 * @param entity the entity whose equipment slots are checked
	 * @param enchantment the enchantment
	 */
	public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.getEquipment(entity).values();
		if (iterable == null) {
			return 0;
		} else {
			int i = 0;

			for (ItemStack itemStack : iterable) {
				int j = getLevel(enchantment, itemStack);
				if (j > i) {
					i = j;
				}
			}

			return i;
		}
	}

	public static float getSwiftSneakSpeedBoost(LivingEntity entity) {
		return (float)getEquipmentLevel(Enchantments.SWIFT_SNEAK, entity) * 0.15F;
	}

	public static int getKnockback(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.KNOCKBACK, entity);
	}

	public static int getFireAspect(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.FIRE_ASPECT, entity);
	}

	public static int getRespiration(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.RESPIRATION, entity);
	}

	public static int getDepthStrider(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.DEPTH_STRIDER, entity);
	}

	public static int getEfficiency(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.EFFICIENCY, entity);
	}

	public static int getLuckOfTheSea(ItemStack stack) {
		return getLevel(Enchantments.LUCK_OF_THE_SEA, stack);
	}

	public static int getLure(ItemStack stack) {
		return getLevel(Enchantments.LURE, stack);
	}

	public static int getLooting(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.LOOTING, entity);
	}

	public static boolean hasAquaAffinity(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
	}

	public static boolean hasFrostWalker(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
	}

	public static boolean hasSoulSpeed(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
	}

	public static boolean hasBindingCurse(ItemStack stack) {
		return getLevel(Enchantments.BINDING_CURSE, stack) > 0;
	}

	public static boolean hasVanishingCurse(ItemStack stack) {
		return getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
	}

	public static boolean hasSilkTouch(ItemStack stack) {
		return getLevel(Enchantments.SILK_TOUCH, stack) > 0;
	}

	public static int getLoyalty(ItemStack stack) {
		return getLevel(Enchantments.LOYALTY, stack);
	}

	public static int getRiptide(ItemStack stack) {
		return getLevel(Enchantments.RIPTIDE, stack);
	}

	public static boolean hasChanneling(ItemStack stack) {
		return getLevel(Enchantments.CHANNELING, stack) > 0;
	}

	/**
	 * {@return a pair of an equipment slot and the item stack in the supplied
	 * entity's slot} It indicates the item stack has the enchantment supplied.
	 * 
	 * <p>If multiple equipment slots' item stacks are valid, a random pair is
	 * returned.
	 * 
	 * @param enchantment the enchantment the equipped item stack must have
	 * @param entity the entity to choose equipments from
	 */
	@Nullable
	public static java.util.Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
		return chooseEquipmentWith(enchantment, entity, stack -> true);
	}

	/**
	 * {@return a pair of an equipment slot and the item stack in the supplied
	 * entity's slot} It indicates the item stack has the enchantment supplied
	 * and fulfills the extra condition.
	 * 
	 * <p>If multiple equipment slots' item stacks are valid, a random pair is
	 * returned.
	 * 
	 * @param condition extra conditions for the item stack to pass for selection
	 * @param enchantment the enchantment the equipped item stack must have
	 * @param entity the entity to choose equipments from
	 */
	@Nullable
	public static java.util.Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
		Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
		if (map.isEmpty()) {
			return null;
		} else {
			List<java.util.Map.Entry<EquipmentSlot, ItemStack>> list = Lists.<java.util.Map.Entry<EquipmentSlot, ItemStack>>newArrayList();

			for (java.util.Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
				ItemStack itemStack = (ItemStack)entry.getValue();
				if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0 && condition.test(itemStack)) {
					list.add(entry);
				}
			}

			return list.isEmpty() ? null : (java.util.Map.Entry)list.get(entity.getRandom().nextInt(list.size()));
		}
	}

	/**
	 * {@return the required experience level for an enchanting option in the
	 * enchanting table's screen, or the enchantment screen}
	 * 
	 * @param bookshelfCount the number of bookshelves
	 * @param stack the item stack to enchant
	 * @param random the random, which guarantees consistent results with the same seed
	 * @param slotIndex the index of the enchanting option
	 */
	public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return 0;
		} else {
			if (bookshelfCount > 15) {
				bookshelfCount = 15;
			}

			int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
			if (slotIndex == 0) {
				return Math.max(j / 3, 1);
			} else {
				return slotIndex == 1 ? j * 2 / 3 + 1 : Math.max(j, bookshelfCount * 2);
			}
		}
	}

	/**
	 * Enchants the {@code target} item stack and returns it.
	 */
	public static ItemStack enchant(FeatureSet enabledFeatures, Random random, ItemStack stack, int level, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = generateEnchantments(enabledFeatures, random, stack, level, treasureAllowed);
		if (stack.isOf(Items.BOOK)) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
		}

		for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
			stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
		}

		return stack;
	}

	/**
	 * Generate the enchantments for enchanting the {@code stack}.
	 */
	public static List<EnchantmentLevelEntry> generateEnchantments(FeatureSet enabledFeatures, Random random, ItemStack stack, int level, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return list;
		} else {
			level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
			List<EnchantmentLevelEntry> list2 = getPossibleEntries(enabledFeatures, level, stack, treasureAllowed);
			if (!list2.isEmpty()) {
				Weighting.getRandom(random, list2).ifPresent(list::add);

				while (random.nextInt(50) <= level) {
					if (!list.isEmpty()) {
						removeConflicts(list2, Util.getLast(list));
					}

					if (list2.isEmpty()) {
						break;
					}

					Weighting.getRandom(random, list2).ifPresent(list::add);
					level /= 2;
				}
			}

			return list;
		}
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
			if (!pickedEntry.enchantment.canCombine(((EnchantmentLevelEntry)iterator.next()).enchantment)) {
				iterator.remove();
			}
		}
	}

	/**
	 * {@return whether the {@code candidate} enchantment is compatible with the
	 * {@code existing} enchantments}
	 */
	public static boolean isCompatible(Collection<RegistryEntry<Enchantment>> existing, Enchantment candidate) {
		for (RegistryEntry<Enchantment> registryEntry : existing) {
			if (!registryEntry.value().canCombine(candidate)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets all the possible entries for enchanting the {@code stack} at the
	 * given {@code power}.
	 */
	public static List<EnchantmentLevelEntry> getPossibleEntries(FeatureSet enabledFeatures, int level, ItemStack stack, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
		boolean bl = stack.isOf(Items.BOOK);

		for (Enchantment enchantment : Registries.ENCHANTMENT) {
			if (enchantment.isEnabled(enabledFeatures)
				&& (!enchantment.isTreasure() || treasureAllowed)
				&& enchantment.isAvailableForRandomSelection()
				&& (bl || enchantment.isAcceptableItem(stack) && enchantment.isPrimaryItem(stack))) {
				for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
					if (level >= enchantment.getMinPower(i) && level <= enchantment.getMaxPower(i)) {
						list.add(new EnchantmentLevelEntry(enchantment, i));
						break;
					}
				}
			}
		}

		return list;
	}

	@FunctionalInterface
	interface Consumer {
		void accept(Enchantment enchantment, int level);
	}
}
