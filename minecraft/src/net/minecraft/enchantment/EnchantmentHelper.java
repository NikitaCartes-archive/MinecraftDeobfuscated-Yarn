package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
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

public class EnchantmentHelper {
	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
			ListTag listTag = stack.getEnchantments();

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				Identifier identifier2 = Identifier.tryParse(compoundTag.getString("id"));
				if (identifier2 != null && identifier2.equals(identifier)) {
					return MathHelper.clamp(compoundTag.getInt("lvl"), 0, 255);
				}
			}

			return 0;
		}
	}

	public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
		ListTag listTag = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentTag(stack) : stack.getEnchantments();
		return getEnchantments(listTag);
	}

	public static Map<Enchantment, Integer> getEnchantments(ListTag tag) {
		Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newLinkedHashMap();

		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
			Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> {
				Integer var10000 = (Integer)map.put(enchantment, compoundTag.getInt("lvl"));
			});
		}

		return map;
	}

	public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		ListTag listTag = new ListTag();

		for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (enchantment != null) {
				int i = (Integer)entry.getValue();
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
				compoundTag.putShort("lvl", (short)i);
				listTag.add(compoundTag);
				if (stack.getItem() == Items.ENCHANTED_BOOK) {
					EnchantedBookItem.addEnchantment(stack, new InfoEnchantment(enchantment, i));
				}
			}
		}

		if (listTag.isEmpty()) {
			stack.removeSubTag("Enchantments");
		} else if (stack.getItem() != Items.ENCHANTED_BOOK) {
			stack.putSubTag("Enchantments", listTag);
		}
	}

	private static void accept(EnchantmentHelper.Consumer enchantmentHandler, ItemStack stack) {
		if (!stack.isEmpty()) {
			ListTag listTag = stack.getEnchantments();

			for (int i = 0; i < listTag.size(); i++) {
				String string = listTag.getCompound(i).getString("id");
				int j = listTag.getCompound(i).getInt("lvl");
				Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(enchantment -> enchantmentHandler.accept(enchantment, j));
			}
		}
	}

	private static void accept(EnchantmentHelper.Consumer enchantmentHandler, Iterable<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			accept(enchantmentHandler, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
		MutableInt mutableInt = new MutableInt();
		accept((enchantment, i) -> mutableInt.add(enchantment.getProtectionAmount(i, source)), equipment);
		return mutableInt.intValue();
	}

	public static float getAttackDamage(ItemStack stack, EntityGroup group) {
		MutableFloat mutableFloat = new MutableFloat();
		accept((enchantment, i) -> mutableFloat.add(enchantment.getAttackDamage(i, group)), stack);
		return mutableFloat.floatValue();
	}

	public static float getSweepingMultiplier(LivingEntity entity) {
		int i = getEquipmentLevel(Enchantments.SWEEPING, entity);
		return i > 0 ? SweepingEnchantment.getMultiplier(i) : 0.0F;
	}

	public static void onUserDamaged(LivingEntity user, Entity attacker) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onUserDamaged(user, attacker, i);
		if (user != null) {
			accept(consumer, user.getItemsEquipped());
		}

		if (attacker instanceof PlayerEntity) {
			accept(consumer, user.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity target) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onTargetDamaged(user, target, i);
		if (user != null) {
			accept(consumer, user.getItemsEquipped());
		}

		if (user instanceof PlayerEntity) {
			accept(consumer, user.getMainHandStack());
		}
	}

	public static int getEquipmentLevel(Enchantment ench, LivingEntity entity) {
		Iterable<ItemStack> iterable = ench.getEquipment(entity).values();
		if (iterable == null) {
			return 0;
		} else {
			int i = 0;

			for (ItemStack itemStack : iterable) {
				int j = getLevel(ench, itemStack);
				if (j > i) {
					i = j;
				}
			}

			return i;
		}
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

	public static boolean hasBindingCurse(ItemStack stack) {
		return getLevel(Enchantments.BINDING_CURSE, stack) > 0;
	}

	public static boolean hasVanishingCurse(ItemStack stack) {
		return getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
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

	@Nullable
	public static Entry<EquipmentSlot, ItemStack> getRandomEnchantedEquipment(Enchantment enchantment, LivingEntity livingEntity) {
		Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(livingEntity);
		if (map.isEmpty()) {
			return null;
		} else {
			List<Entry<EquipmentSlot, ItemStack>> list = Lists.<Entry<EquipmentSlot, ItemStack>>newArrayList();

			for (Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
				ItemStack itemStack = (ItemStack)entry.getValue();
				if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0) {
					list.add(entry);
				}
			}

			return list.isEmpty() ? null : (Entry)list.get(livingEntity.getRandom().nextInt(list.size()));
		}
	}

	public static int calculateEnchantmentPower(Random random, int num, int enchantmentPower, ItemStack rstack) {
		Item item = rstack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return 0;
		} else {
			if (enchantmentPower > 15) {
				enchantmentPower = 15;
			}

			int j = random.nextInt(8) + 1 + (enchantmentPower >> 1) + random.nextInt(enchantmentPower + 1);
			if (num == 0) {
				return Math.max(j / 3, 1);
			} else {
				return num == 1 ? j * 2 / 3 + 1 : Math.max(j, enchantmentPower * 2);
			}
		}
	}

	public static ItemStack enchant(Random random, ItemStack target, int level, boolean hasTreasure) {
		List<InfoEnchantment> list = getEnchantments(random, target, level, hasTreasure);
		boolean bl = target.getItem() == Items.BOOK;
		if (bl) {
			target = new ItemStack(Items.ENCHANTED_BOOK);
		}

		for (InfoEnchantment infoEnchantment : list) {
			if (bl) {
				EnchantedBookItem.addEnchantment(target, infoEnchantment);
			} else {
				target.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
			}
		}

		return target;
	}

	public static List<InfoEnchantment> getEnchantments(Random random, ItemStack stack, int level, boolean hasTreasure) {
		List<InfoEnchantment> list = Lists.<InfoEnchantment>newArrayList();
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return list;
		} else {
			level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
			List<InfoEnchantment> list2 = getHighestApplicableEnchantmentsAtPower(level, stack, hasTreasure);
			if (!list2.isEmpty()) {
				list.add(WeightedPicker.getRandom(random, list2));

				while (random.nextInt(50) <= level) {
					remove(list2, Util.getLast(list));
					if (list2.isEmpty()) {
						break;
					}

					list.add(WeightedPicker.getRandom(random, list2));
					level /= 2;
				}
			}

			return list;
		}
	}

	public static void remove(List<InfoEnchantment> infos, InfoEnchantment info) {
		Iterator<InfoEnchantment> iterator = infos.iterator();

		while (iterator.hasNext()) {
			if (!info.enchantment.isDifferent(((InfoEnchantment)iterator.next()).enchantment)) {
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

	public static List<InfoEnchantment> getHighestApplicableEnchantmentsAtPower(int power, ItemStack stack, boolean bl) {
		List<InfoEnchantment> list = Lists.<InfoEnchantment>newArrayList();
		Item item = stack.getItem();
		boolean bl2 = stack.getItem() == Items.BOOK;

		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if ((!enchantment.isTreasure() || bl) && (enchantment.type.isAcceptableItem(item) || bl2)) {
				for (int i = enchantment.getMaximumLevel(); i > enchantment.getMinimumLevel() - 1; i--) {
					if (power >= enchantment.getMinimumPower(i) && power <= enchantment.getMaximumPower(i)) {
						list.add(new InfoEnchantment(enchantment, i));
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
