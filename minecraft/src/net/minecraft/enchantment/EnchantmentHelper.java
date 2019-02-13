package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
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
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
	public static int getLevel(Enchantment enchantment, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		} else {
			Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
			ListTag listTag = itemStack.getEnchantmentList();

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompoundTag(i);
				Identifier identifier2 = Identifier.create(compoundTag.getString("id"));
				if (identifier2 != null && identifier2.equals(identifier)) {
					return compoundTag.getInt("lvl");
				}
			}

			return 0;
		}
	}

	public static Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
		Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newLinkedHashMap();
		ListTag listTag = itemStack.getItem() == Items.field_8598 ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantmentList();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Registry.ENCHANTMENT.getOrEmpty(Identifier.create(compoundTag.getString("id"))).ifPresent(enchantment -> {
				Integer var10000 = (Integer)map.put(enchantment, compoundTag.getInt("lvl"));
			});
		}

		return map;
	}

	public static void set(Map<Enchantment, Integer> map, ItemStack itemStack) {
		ListTag listTag = new ListTag();

		for (Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (enchantment != null) {
				int i = (Integer)entry.getValue();
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
				compoundTag.putShort("lvl", (short)i);
				listTag.add(compoundTag);
				if (itemStack.getItem() == Items.field_8598) {
					EnchantedBookItem.addEnchantment(itemStack, new InfoEnchantment(enchantment, i));
				}
			}
		}

		if (listTag.isEmpty()) {
			itemStack.removeSubTag("Enchantments");
		} else if (itemStack.getItem() != Items.field_8598) {
			itemStack.setChildTag("Enchantments", listTag);
		}
	}

	private static void accept(EnchantmentHelper.Consumer consumer, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			ListTag listTag = itemStack.getEnchantmentList();

			for (int i = 0; i < listTag.size(); i++) {
				String string = listTag.getCompoundTag(i).getString("id");
				int j = listTag.getCompoundTag(i).getInt("lvl");
				Registry.ENCHANTMENT.getOrEmpty(Identifier.create(string)).ifPresent(enchantment -> consumer.accept(enchantment, j));
			}
		}
	}

	private static void accept(EnchantmentHelper.Consumer consumer, Iterable<ItemStack> iterable) {
		for (ItemStack itemStack : iterable) {
			accept(consumer, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> iterable, DamageSource damageSource) {
		MutableInt mutableInt = new MutableInt();
		accept((enchantment, i) -> mutableInt.add(enchantment.getProtectionAmount(i, damageSource)), iterable);
		return mutableInt.intValue();
	}

	public static float getAttackDamage(ItemStack itemStack, EntityGroup entityGroup) {
		MutableFloat mutableFloat = new MutableFloat();
		accept((enchantment, i) -> mutableFloat.add(enchantment.getAttackDamage(i, entityGroup)), itemStack);
		return mutableFloat.floatValue();
	}

	public static float getSweepingMultiplier(LivingEntity livingEntity) {
		int i = getEquipmentLevel(Enchantments.field_9115, livingEntity);
		return i > 0 ? SweepingEnchantment.getMultiplier(i) : 0.0F;
	}

	public static void onUserDamaged(LivingEntity livingEntity, Entity entity) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onUserDamaged(livingEntity, entity, i);
		if (livingEntity != null) {
			accept(consumer, livingEntity.getItemsEquipped());
		}

		if (entity instanceof PlayerEntity) {
			accept(consumer, livingEntity.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity livingEntity, Entity entity) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onTargetDamaged(livingEntity, entity, i);
		if (livingEntity != null) {
			accept(consumer, livingEntity.getItemsEquipped());
		}

		if (livingEntity instanceof PlayerEntity) {
			accept(consumer, livingEntity.getMainHandStack());
		}
	}

	public static int getEquipmentLevel(Enchantment enchantment, LivingEntity livingEntity) {
		Iterable<ItemStack> iterable = enchantment.getEquipment(livingEntity);
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

	public static int getKnockback(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9121, livingEntity);
	}

	public static int getFireAspect(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9124, livingEntity);
	}

	public static int getRespiration(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9127, livingEntity);
	}

	public static int getDepthStrider(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9128, livingEntity);
	}

	public static int getEfficiency(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9131, livingEntity);
	}

	public static int getLuckOfTheSea(ItemStack itemStack) {
		return getLevel(Enchantments.field_9114, itemStack);
	}

	public static int getLure(ItemStack itemStack) {
		return getLevel(Enchantments.field_9100, itemStack);
	}

	public static int getLooting(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9110, livingEntity);
	}

	public static boolean hasAquaAffinity(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9105, livingEntity) > 0;
	}

	public static boolean hasFrostWalker(LivingEntity livingEntity) {
		return getEquipmentLevel(Enchantments.field_9122, livingEntity) > 0;
	}

	public static boolean hasBindingCurse(ItemStack itemStack) {
		return getLevel(Enchantments.field_9113, itemStack) > 0;
	}

	public static boolean hasVanishingCurse(ItemStack itemStack) {
		return getLevel(Enchantments.field_9109, itemStack) > 0;
	}

	public static int getLoyalty(ItemStack itemStack) {
		return getLevel(Enchantments.field_9120, itemStack);
	}

	public static int getRiptide(ItemStack itemStack) {
		return getLevel(Enchantments.field_9104, itemStack);
	}

	public static boolean hasChanneling(ItemStack itemStack) {
		return getLevel(Enchantments.field_9117, itemStack) > 0;
	}

	public static ItemStack getRandomEnchantedEquipment(Enchantment enchantment, LivingEntity livingEntity) {
		List<ItemStack> list = enchantment.getEquipment(livingEntity);
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			List<ItemStack> list2 = Lists.<ItemStack>newArrayList();

			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0) {
					list2.add(itemStack);
				}
			}

			return list2.isEmpty() ? ItemStack.EMPTY : (ItemStack)list2.get(livingEntity.getRand().nextInt(list2.size()));
		}
	}

	public static int calculateEnchantmentPower(Random random, int i, int j, ItemStack itemStack) {
		Item item = itemStack.getItem();
		int k = item.getEnchantability();
		if (k <= 0) {
			return 0;
		} else {
			if (j > 15) {
				j = 15;
			}

			int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);
			if (i == 0) {
				return Math.max(l / 3, 1);
			} else {
				return i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2);
			}
		}
	}

	public static ItemStack enchant(Random random, ItemStack itemStack, int i, boolean bl) {
		List<InfoEnchantment> list = getEnchantments(random, itemStack, i, bl);
		boolean bl2 = itemStack.getItem() == Items.field_8529;
		if (bl2) {
			itemStack = new ItemStack(Items.field_8598);
		}

		for (InfoEnchantment infoEnchantment : list) {
			if (bl2) {
				EnchantedBookItem.addEnchantment(itemStack, infoEnchantment);
			} else {
				itemStack.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
			}
		}

		return itemStack;
	}

	public static List<InfoEnchantment> getEnchantments(Random random, ItemStack itemStack, int i, boolean bl) {
		List<InfoEnchantment> list = Lists.<InfoEnchantment>newArrayList();
		Item item = itemStack.getItem();
		int j = item.getEnchantability();
		if (j <= 0) {
			return list;
		} else {
			i += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			i = MathHelper.clamp(Math.round((float)i + (float)i * f), 1, Integer.MAX_VALUE);
			List<InfoEnchantment> list2 = getHighestApplicableEnchantmentsAtPower(i, itemStack, bl);
			if (!list2.isEmpty()) {
				list.add(WeightedPicker.getRandom(random, list2));

				while (random.nextInt(50) <= i) {
					i = i * 4 / 5 + 1;
					list2 = getHighestApplicableEnchantmentsAtPower(i, itemStack, bl);

					for (InfoEnchantment infoEnchantment : list) {
						remove(list2, infoEnchantment);
					}

					if (list2.isEmpty()) {
						break;
					}

					list.add(WeightedPicker.getRandom(random, list2));
					i /= 2;
				}
			}

			return list;
		}
	}

	public static void remove(List<InfoEnchantment> list, InfoEnchantment infoEnchantment) {
		Iterator<InfoEnchantment> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (!infoEnchantment.enchantment.isDifferent(((InfoEnchantment)iterator.next()).enchantment)) {
				iterator.remove();
			}
		}
	}

	public static boolean contains(Collection<Enchantment> collection, Enchantment enchantment) {
		for (Enchantment enchantment2 : collection) {
			if (!enchantment2.isDifferent(enchantment)) {
				return false;
			}
		}

		return true;
	}

	public static List<InfoEnchantment> getHighestApplicableEnchantmentsAtPower(int i, ItemStack itemStack, boolean bl) {
		List<InfoEnchantment> list = Lists.<InfoEnchantment>newArrayList();
		Item item = itemStack.getItem();
		boolean bl2 = itemStack.getItem() == Items.field_8529;

		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if ((!enchantment.isLootOnly() || bl) && (enchantment.type.isAcceptableItem(item) || bl2)) {
				for (int j = enchantment.getMaximumLevel(); j > enchantment.getMinimumLevel() - 1; j--) {
					if (i >= enchantment.getMinimumPower(j)) {
						list.add(new InfoEnchantment(enchantment, j));
						break;
					}
				}
			}
		}

		return list;
	}

	@FunctionalInterface
	interface Consumer {
		void accept(Enchantment enchantment, int i);
	}
}
