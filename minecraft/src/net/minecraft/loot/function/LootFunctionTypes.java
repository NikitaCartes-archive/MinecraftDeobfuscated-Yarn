package net.minecraft.loot.function;

import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootFunctionTypes {
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
	public static final LootFunctionType field_25214 = register("set_count", new SetCountLootFunction.Serializer());
	public static final LootFunctionType field_25215 = register("enchant_with_levels", new EnchantWithLevelsLootFunction.Serializer());
	public static final LootFunctionType field_25216 = register("enchant_randomly", new EnchantRandomlyLootFunction.Serializer());
	public static final LootFunctionType field_25217 = register("set_nbt", new SetNbtLootFunction.Serializer());
	public static final LootFunctionType field_25218 = register("furnace_smelt", new FurnaceSmeltLootFunction.Serializer());
	public static final LootFunctionType field_25219 = register("looting_enchant", new LootingEnchantLootFunction.Serializer());
	public static final LootFunctionType field_25220 = register("set_damage", new SetDamageLootFunction.Serializer());
	public static final LootFunctionType field_25221 = register("set_attributes", new SetAttributesLootFunction.Serializer());
	public static final LootFunctionType field_25222 = register("set_name", new SetNameLootFunction.Serializer());
	public static final LootFunctionType field_25223 = register("exploration_map", new ExplorationMapLootFunction.Serializer());
	public static final LootFunctionType field_25224 = register("set_stew_effect", new SetStewEffectLootFunction.Serializer());
	public static final LootFunctionType field_25225 = register("copy_name", new CopyNameLootFunction.Serializer());
	public static final LootFunctionType field_25226 = register("set_contents", new SetContentsLootFunction.Serializer());
	public static final LootFunctionType field_25227 = register("limit_count", new LimitCountLootFunction.Serializer());
	public static final LootFunctionType field_25228 = register("apply_bonus", new ApplyBonusLootFunction.Serializer());
	public static final LootFunctionType field_25229 = register("set_loot_table", new SetLootTableLootFunction.Serializer());
	public static final LootFunctionType field_25230 = register("explosion_decay", new ExplosionDecayLootFunction.Serializer());
	public static final LootFunctionType field_25231 = register("set_lore", new SetLoreLootFunction.Serializer());
	public static final LootFunctionType field_25232 = register("fill_player_head", new FillPlayerHeadLootFunction.Serializer());
	public static final LootFunctionType field_25233 = register("copy_nbt", new CopyNbtLootFunction.Serializer());
	public static final LootFunctionType field_25234 = register("copy_state", new CopyStateFunction.Serializer());

	private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
		return Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootFunction, LootFunctionType>createTypeHandler(Registry.LOOT_FUNCTION_TYPE, "function", "function", LootFunction::getType)
			.createGsonSerializer();
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
