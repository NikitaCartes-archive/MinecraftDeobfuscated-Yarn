package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class LootFunctionTypes {
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
	public static final Codec<LootFunction> BASE_CODEC = Registries.LOOT_FUNCTION_TYPE
		.getCodec()
		.dispatch("function", LootFunction::getType, LootFunctionType::codec);
	public static final Codec<LootFunction> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(BASE_CODEC, AndLootFunction.INLINE_CODEC));
	public static final Codec<RegistryEntry<LootFunction>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.ITEM_MODIFIER, CODEC);
	public static final LootFunctionType<SetCountLootFunction> SET_COUNT = register("set_count", SetCountLootFunction.CODEC);
	public static final LootFunctionType<SetItemLootFunction> SET_ITEM = register("set_item", SetItemLootFunction.CODEC);
	public static final LootFunctionType<EnchantWithLevelsLootFunction> ENCHANT_WITH_LEVELS = register("enchant_with_levels", EnchantWithLevelsLootFunction.CODEC);
	public static final LootFunctionType<EnchantRandomlyLootFunction> ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);
	public static final LootFunctionType<SetEnchantmentsLootFunction> SET_ENCHANTMENTS = register("set_enchantments", SetEnchantmentsLootFunction.CODEC);
	public static final LootFunctionType<SetCustomDataLootFunction> SET_CUSTOM_DATA = register("set_custom_data", SetCustomDataLootFunction.CODEC);
	public static final LootFunctionType<SetComponentsLootFunction> SET_COMPONENTS = register("set_components", SetComponentsLootFunction.CODEC);
	public static final LootFunctionType<FurnaceSmeltLootFunction> FURNACE_SMELT = register("furnace_smelt", FurnaceSmeltLootFunction.CODEC);
	public static final LootFunctionType<EnchantedCountIncreaseLootFunction> ENCHANTED_COUNT_INCREASE = register(
		"enchanted_count_increase", EnchantedCountIncreaseLootFunction.CODEC
	);
	public static final LootFunctionType<SetDamageLootFunction> SET_DAMAGE = register("set_damage", SetDamageLootFunction.CODEC);
	public static final LootFunctionType<SetAttributesLootFunction> SET_ATTRIBUTES = register("set_attributes", SetAttributesLootFunction.CODEC);
	public static final LootFunctionType<SetNameLootFunction> SET_NAME = register("set_name", SetNameLootFunction.CODEC);
	public static final LootFunctionType<ExplorationMapLootFunction> EXPLORATION_MAP = register("exploration_map", ExplorationMapLootFunction.CODEC);
	public static final LootFunctionType<SetStewEffectLootFunction> SET_STEW_EFFECT = register("set_stew_effect", SetStewEffectLootFunction.CODEC);
	public static final LootFunctionType<CopyNameLootFunction> COPY_NAME = register("copy_name", CopyNameLootFunction.CODEC);
	public static final LootFunctionType<SetContentsLootFunction> SET_CONTENTS = register("set_contents", SetContentsLootFunction.CODEC);
	public static final LootFunctionType<ModifyContentsLootFunction> MODIFY_CONTENTS = register("modify_contents", ModifyContentsLootFunction.CODEC);
	public static final LootFunctionType<FilteredLootFunction> FILTERED = register("filtered", FilteredLootFunction.CODEC);
	public static final LootFunctionType<LimitCountLootFunction> LIMIT_COUNT = register("limit_count", LimitCountLootFunction.CODEC);
	public static final LootFunctionType<ApplyBonusLootFunction> APPLY_BONUS = register("apply_bonus", ApplyBonusLootFunction.CODEC);
	public static final LootFunctionType<SetLootTableLootFunction> SET_LOOT_TABLE = register("set_loot_table", SetLootTableLootFunction.CODEC);
	public static final LootFunctionType<ExplosionDecayLootFunction> EXPLOSION_DECAY = register("explosion_decay", ExplosionDecayLootFunction.CODEC);
	public static final LootFunctionType<SetLoreLootFunction> SET_LORE = register("set_lore", SetLoreLootFunction.CODEC);
	public static final LootFunctionType<FillPlayerHeadLootFunction> FILL_PLAYER_HEAD = register("fill_player_head", FillPlayerHeadLootFunction.CODEC);
	public static final LootFunctionType<CopyNbtLootFunction> COPY_CUSTOM_DATA = register("copy_custom_data", CopyNbtLootFunction.CODEC);
	public static final LootFunctionType<CopyStateLootFunction> COPY_STATE = register("copy_state", CopyStateLootFunction.CODEC);
	public static final LootFunctionType<SetBannerPatternLootFunction> SET_BANNER_PATTERN = register("set_banner_pattern", SetBannerPatternLootFunction.CODEC);
	public static final LootFunctionType<SetPotionLootFunction> SET_POTION = register("set_potion", SetPotionLootFunction.CODEC);
	public static final LootFunctionType<SetInstrumentLootFunction> SET_INSTRUMENT = register("set_instrument", SetInstrumentLootFunction.CODEC);
	public static final LootFunctionType<ReferenceLootFunction> REFERENCE = register("reference", ReferenceLootFunction.CODEC);
	public static final LootFunctionType<AndLootFunction> SEQUENCE = register("sequence", AndLootFunction.CODEC);
	public static final LootFunctionType<CopyComponentsLootFunction> COPY_COMPONENTS = register("copy_components", CopyComponentsLootFunction.CODEC);
	public static final LootFunctionType<SetFireworksLootFunction> SET_FIREWORKS = register("set_fireworks", SetFireworksLootFunction.CODEC);
	public static final LootFunctionType<SetFireworkExplosionLootFunction> SET_FIREWORK_EXPLOSION = register(
		"set_firework_explosion", SetFireworkExplosionLootFunction.CODEC
	);
	public static final LootFunctionType<SetBookCoverLootFunction> SET_BOOK_COVER = register("set_book_cover", SetBookCoverLootFunction.CODEC);
	public static final LootFunctionType<SetWrittenBookPagesLootFunction> SET_WRITTEN_BOOK_PAGES = register(
		"set_written_book_pages", SetWrittenBookPagesLootFunction.CODEC
	);
	public static final LootFunctionType<SetWritableBookPagesLootFunction> SET_WRITABLE_BOOK_PAGES = register(
		"set_writable_book_pages", SetWritableBookPagesLootFunction.CODEC
	);
	public static final LootFunctionType<ToggleTooltipsLootFunction> TOGGLE_TOOLTIPS = register("toggle_tooltips", ToggleTooltipsLootFunction.CODEC);
	public static final LootFunctionType<SetOminousBottleAmplifierLootFunction> SET_OMINOUS_BOTTLE_AMPLIFIER = register(
		"set_ominous_bottle_amplifier", SetOminousBottleAmplifierLootFunction.CODEC
	);
	public static final LootFunctionType<SetCustomModelDataLootFunction> SET_CUSTOM_MODEL_DATA = register(
		"set_custom_model_data", SetCustomModelDataLootFunction.CODEC
	);

	private static <T extends LootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
		return Registry.register(Registries.LOOT_FUNCTION_TYPE, Identifier.ofVanilla(id), new LootFunctionType<>(codec));
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
