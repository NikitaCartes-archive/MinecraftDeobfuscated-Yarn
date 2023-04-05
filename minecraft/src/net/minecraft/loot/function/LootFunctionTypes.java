package net.minecraft.loot.function;

import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class LootFunctionTypes {
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
	public static final LootFunctionType SET_COUNT = register("set_count", new SetCountLootFunction.Serializer());
	public static final LootFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", new EnchantWithLevelsLootFunction.Serializer());
	public static final LootFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", new EnchantRandomlyLootFunction.Serializer());
	public static final LootFunctionType SET_ENCHANTMENTS = register("set_enchantments", new SetEnchantmentsLootFunction.Serializer());
	public static final LootFunctionType SET_NBT = register("set_nbt", new SetNbtLootFunction.Serializer());
	public static final LootFunctionType FURNACE_SMELT = register("furnace_smelt", new FurnaceSmeltLootFunction.Serializer());
	public static final LootFunctionType LOOTING_ENCHANT = register("looting_enchant", new LootingEnchantLootFunction.Serializer());
	public static final LootFunctionType SET_DAMAGE = register("set_damage", new SetDamageLootFunction.Serializer());
	public static final LootFunctionType SET_ATTRIBUTES = register("set_attributes", new SetAttributesLootFunction.Serializer());
	public static final LootFunctionType SET_NAME = register("set_name", new SetNameLootFunction.Serializer());
	public static final LootFunctionType EXPLORATION_MAP = register("exploration_map", new ExplorationMapLootFunction.Serializer());
	public static final LootFunctionType SET_STEW_EFFECT = register("set_stew_effect", new SetStewEffectLootFunction.Serializer());
	public static final LootFunctionType COPY_NAME = register("copy_name", new CopyNameLootFunction.Serializer());
	public static final LootFunctionType SET_CONTENTS = register("set_contents", new SetContentsLootFunction.Serializer());
	public static final LootFunctionType LIMIT_COUNT = register("limit_count", new LimitCountLootFunction.Serializer());
	public static final LootFunctionType APPLY_BONUS = register("apply_bonus", new ApplyBonusLootFunction.Serializer());
	public static final LootFunctionType SET_LOOT_TABLE = register("set_loot_table", new SetLootTableLootFunction.Serializer());
	public static final LootFunctionType EXPLOSION_DECAY = register("explosion_decay", new ExplosionDecayLootFunction.Serializer());
	public static final LootFunctionType SET_LORE = register("set_lore", new SetLoreLootFunction.Serializer());
	public static final LootFunctionType FILL_PLAYER_HEAD = register("fill_player_head", new FillPlayerHeadLootFunction.Serializer());
	public static final LootFunctionType COPY_NBT = register("copy_nbt", new CopyNbtLootFunction.Serializer());
	public static final LootFunctionType COPY_STATE = register("copy_state", new CopyStateFunction.Serializer());
	public static final LootFunctionType SET_BANNER_PATTERN = register("set_banner_pattern", new SetBannerPatternFunction.Serializer());
	public static final LootFunctionType SET_POTION = register("set_potion", new SetPotionLootFunction.Serializer());
	public static final LootFunctionType SET_INSTRUMENT = register("set_instrument", new SetInstrumentLootFunction.Serializer());
	public static final LootFunctionType REFERENCE = register("reference", new ReferenceLootFunction.Serializer());

	private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
		return Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootFunction, LootFunctionType>createSerializerBuilder(Registries.LOOT_FUNCTION_TYPE, "function", "function", LootFunction::getType)
			.build();
	}

	public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] lootFunctions) {
		switch (lootFunctions.length) {
			case 0:
				return NOOP;
			case 1:
				return lootFunctions[0];
			case 2:
				BiFunction<ItemStack, LootContext, ItemStack> biFunction = lootFunctions[0];
				BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = lootFunctions[1];
				return (stack, context) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply(stack, context), context);
			default:
				return (stack, context) -> {
					for (BiFunction<ItemStack, LootContext, ItemStack> biFunctionx : lootFunctions) {
						stack = (ItemStack)biFunctionx.apply(stack, context);
					}

					return stack;
				};
		}
	}
}
