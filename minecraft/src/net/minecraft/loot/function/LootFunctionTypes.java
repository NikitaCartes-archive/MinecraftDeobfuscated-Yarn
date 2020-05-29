package net.minecraft.loot.function;

import java.util.function.BiFunction;
import net.minecraft.class_5330;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializable;
import net.minecraft.util.registry.Registry;

public class LootFunctionTypes {
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
	public static final LootFunctionType SET_COUNT = register("set_count", new SetCountLootFunction.Factory());
	public static final LootFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", new EnchantWithLevelsLootFunction.Factory());
	public static final LootFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", new EnchantRandomlyLootFunction.Factory());
	public static final LootFunctionType SET_NBT = register("set_nbt", new SetNbtLootFunction.Builder());
	public static final LootFunctionType FURNACE_SMELT = register("furnace_smelt", new FurnaceSmeltLootFunction.class_5340());
	public static final LootFunctionType LOOTING_ENCHANT = register("looting_enchant", new LootingEnchantLootFunction.Factory());
	public static final LootFunctionType SET_DAMAGE = register("set_damage", new SetDamageLootFunction.Factory());
	public static final LootFunctionType SET_ATTRIBUTES = register("set_attributes", new SetAttributesLootFunction.Factory());
	public static final LootFunctionType SET_NAME = register("set_name", new SetNameLootFunction.Factory());
	public static final LootFunctionType EXPLORATION_MAP = register("exploration_map", new ExplorationMapLootFunction.Factory());
	public static final LootFunctionType SET_STEW_EFFECT = register("set_stew_effect", new SetStewEffectLootFunction.Factory());
	public static final LootFunctionType COPY_NAME = register("copy_name", new CopyNameLootFunction.Factory());
	public static final LootFunctionType SET_CONTENTS = register("set_contents", new SetContentsLootFunction.Factory());
	public static final LootFunctionType LIMIT_COUNT = register("limit_count", new LimitCountLootFunction.Factory());
	public static final LootFunctionType APPLY_BONUS = register("apply_bonus", new ApplyBonusLootFunction.Factory());
	public static final LootFunctionType SET_LOOT_TABLE = register("set_loot_table", new SetLootTableLootFunction.Factory());
	public static final LootFunctionType EXPLOSION_DECAY = register("explosion_decay", new ExplosionDecayLootFunction.Factory());
	public static final LootFunctionType SET_LORE = register("set_lore", new SetLoreLootFunction.Factory());
	public static final LootFunctionType FILL_PLAYER_HEAD = register("fill_player_head", new FillPlayerHeadLootFunction.Factory());
	public static final LootFunctionType COPY_NBT = register("copy_nbt", new CopyNbtLootFunction.Factory());
	public static final LootFunctionType COPY_STATE = register("copy_state", new CopyStateFunction.Factory());

	private static LootFunctionType register(String id, JsonSerializable<? extends LootFunction> jsonSerializable) {
		return Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(jsonSerializable));
	}

	public static Object method_29322() {
		return class_5330.<LootFunction, LootFunctionType>method_29306(Registry.LOOT_FUNCTION_TYPE, "function", "function", LootFunction::method_29321)
			.method_29307();
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
				return (stack, context) -> (ItemStack)biFunction2.apply(biFunction.apply(stack, context), context);
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
