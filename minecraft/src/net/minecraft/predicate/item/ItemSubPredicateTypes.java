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
	public static final ItemSubPredicate.Type<ContainerPredicate> CONTAINER = register("container", ContainerPredicate.CODEC);
	public static final ItemSubPredicate.Type<BundleContentsPredicate> BUNDLE_CONTENTS = register("bundle_contents", BundleContentsPredicate.CODEC);
	public static final ItemSubPredicate.Type<FireworkExplosionPredicate> FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosionPredicate.CODEC);
	public static final ItemSubPredicate.Type<FireworksPredicate> FIREWORKS = register("fireworks", FireworksPredicate.CODEC);
	public static final ItemSubPredicate.Type<WritableBookContentPredicate> WRITABLE_BOOK_CONTENT = register(
		"writable_book_content", WritableBookContentPredicate.CODEC
	);
	public static final ItemSubPredicate.Type<WrittenBookContentPredicate> WRITTEN_BOOK_CONTENT = register(
		"written_book_content", WrittenBookContentPredicate.CODEC
	);
	public static final ItemSubPredicate.Type<AttributeModifiersPredicate> ATTRIBUTE_MODIFIERS = register("attribute_modifiers", AttributeModifiersPredicate.CODEC);
	public static final ItemSubPredicate.Type<TrimPredicate> TRIM = register("trim", TrimPredicate.CODEC);
	public static final ItemSubPredicate.Type<JukeboxPlayablePredicate> JUKEBOX_PLAYABLE = register("jukebox_playable", JukeboxPlayablePredicate.CODEC);

	private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String id, Codec<T> codec) {
		return Registry.register(Registries.ITEM_SUB_PREDICATE_TYPE, id, new ItemSubPredicate.Type<>(codec));
	}

	public static ItemSubPredicate.Type<?> getDefault(Registry<ItemSubPredicate.Type<?>> registry) {
		return DAMAGE;
	}
}
