package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class LootFunctionTypes {
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
	private static final Codec<LootFunction> BASE_CODEC = Registries.LOOT_FUNCTION_TYPE
		.getCodec()
		.dispatch("function", LootFunction::getType, LootFunctionType::codec);
	public static final Codec<LootFunction> CODEC = Codecs.createLazy(() -> Codecs.alternatively(BASE_CODEC, AndLootFunction.INLINE_CODEC));
	public static final LootFunctionType SET_COUNT = register("set_count", SetCountLootFunction.CODEC);
	public static final LootFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", EnchantWithLevelsLootFunction.CODEC);
	public static final LootFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);
	public static final LootFunctionType SET_ENCHANTMENTS = register("set_enchantments", SetEnchantmentsLootFunction.CODEC);
	public static final LootFunctionType SET_CUSTOM_DATA = register("set_custom_data", SetCustomDataLootFunction.CODEC);
	public static final LootFunctionType SET_COMPONENTS = register("set_components", SetComponentsLootFunction.CODEC);
	public static final LootFunctionType FURNACE_SMELT = register("furnace_smelt", FurnaceSmeltLootFunction.CODEC);
	public static final LootFunctionType LOOTING_ENCHANT = register("looting_enchant", LootingEnchantLootFunction.CODEC);
	public static final LootFunctionType SET_DAMAGE = register("set_damage", SetDamageLootFunction.CODEC);
	public static final LootFunctionType SET_ATTRIBUTES = register("set_attributes", SetAttributesLootFunction.CODEC);
	public static final LootFunctionType SET_NAME = register("set_name", SetNameLootFunction.CODEC);
	public static final LootFunctionType EXPLORATION_MAP = register("exploration_map", ExplorationMapLootFunction.CODEC);
	public static final LootFunctionType SET_STEW_EFFECT = register("set_stew_effect", SetStewEffectLootFunction.CODEC);
	public static final LootFunctionType COPY_NAME = register("copy_name", CopyNameLootFunction.CODEC);
	public static final LootFunctionType SET_CONTENTS = register("set_contents", SetContentsLootFunction.CODEC);
	public static final LootFunctionType LIMIT_COUNT = register("limit_count", LimitCountLootFunction.CODEC);
	public static final LootFunctionType APPLY_BONUS = register("apply_bonus", ApplyBonusLootFunction.CODEC);
	public static final LootFunctionType SET_LOOT_TABLE = register("set_loot_table", SetLootTableLootFunction.CODEC);
	public static final LootFunctionType EXPLOSION_DECAY = register("explosion_decay", ExplosionDecayLootFunction.CODEC);
	public static final LootFunctionType SET_LORE = register("set_lore", SetLoreLootFunction.CODEC);
	public static final LootFunctionType FILL_PLAYER_HEAD = register("fill_player_head", FillPlayerHeadLootFunction.CODEC);
	public static final LootFunctionType COPY_CUSTOM_DATA = register("copy_custom_data", CopyNbtLootFunction.CODEC);
	public static final LootFunctionType COPY_STATE = register("copy_state", CopyStateLootFunction.CODEC);
	public static final LootFunctionType SET_BANNER_PATTERN = register("set_banner_pattern", SetBannerPatternLootFunction.CODEC);
	public static final LootFunctionType SET_POTION = register("set_potion", SetPotionLootFunction.CODEC);
	public static final LootFunctionType SET_INSTRUMENT = register("set_instrument", SetInstrumentLootFunction.CODEC);
	public static final LootFunctionType REFERENCE = register("reference", ReferenceLootFunction.CODEC);
	public static final LootFunctionType SEQUENCE = register("sequence", AndLootFunction.CODEC);
	public static final LootFunctionType COPY_COMPONENTS = register("copy_components", CopyComponentsLootFunction.CODEC);
	public static final LootFunctionType SET_FIREWORKS = register("set_fireworks", SetFireworksLootFunction.CODEC);
	public static final LootFunctionType SET_FIREWORK_EXPLOSION = register("set_firework_explosion", SetFireworkExplosionLootFunction.CODEC);
	public static final LootFunctionType SET_BOOK_COVER = register("set_book_cover", SetBookCoverLootFunction.CODEC);
	public static final LootFunctionType SET_WRITTEN_BOOK_PAGES = register("set_written_book_pages", SetWrittenBookPagesLootFunction.CODEC);
	public static final LootFunctionType SET_WRITABLE_BOOK_PAGES = register("set_writable_book_pages", SetWritableBookPagesLootFunction.CODEC);

	private static LootFunctionType register(String id, Codec<? extends LootFunction> codec) {
		return Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(codec));
	}

	public static BiFunction<ItemStack, LootContext, ItemStack> join(List<? extends BiFunction<ItemStack, LootContext, ItemStack>> terms) {
		List<BiFunction<ItemStack, LootContext, ItemStack>> list = List.copyOf(terms);

		return switch (list.size()) {
			case 0 -> NOOP;
			case 1 -> (BiFunction)list.get(0);
			case 2 -> {
				BiFunction<ItemStack, LootContext, ItemStack> biFunction = (BiFunction<ItemStack, LootContext, ItemStack>)list.get(0);
				BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = (BiFunction<ItemStack, LootContext, ItemStack>)list.get(1);
				yield (stack, context) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply(stack, context), context);
			}
			default -> (stack, context) -> {
			for (BiFunction<ItemStack, LootContext, ItemStack> biFunctionx : list) {
				stack = (ItemStack)biFunctionx.apply(stack, context);
			}

			return stack;
		};
		};
	}
}
