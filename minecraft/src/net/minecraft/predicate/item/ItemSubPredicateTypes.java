package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ItemSubPredicateTypes {
	public static final ItemSubPredicate.Type<DamagePredicate> DAMAGE = register("damage", DamagePredicate.CODEC);
	public static final ItemSubPredicate.Type<EnchantmentsPredicate.Enchantments> ENCHANTMENTS = register("enchantments", EnchantmentsPredicate.Enchantments.CODEC);
	public static final ItemSubPredicate.Type<EnchantmentsPredicate.StoredEnchantments> STORED_ENCHANTMENTS = register(
		"stored_enchantments", EnchantmentsPredicate.StoredEnchantments.CODEC
	);
	public static final ItemSubPredicate.Type<PotionContentsPredicate> POTION_CONTENTS = register("potion_contents", PotionContentsPredicate.CODEC);
	public static final ItemSubPredicate.Type<CustomDataPredicate> CUSTOM_DATA = register("custom_data", CustomDataPredicate.CODEC);

	private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String id, Codec<T> codec) {
		return Registry.register(Registries.ITEM_SUB_PREDICATE_TYPE, id, new ItemSubPredicate.Type<>(codec));
	}

	public static ItemSubPredicate.Type<?> getDefault(Registry<ItemSubPredicate.Type<?>> registry) {
		return DAMAGE;
	}
}
