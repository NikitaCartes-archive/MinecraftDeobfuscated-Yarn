/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.function.SetBannerPatternFunction;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetGoatHornSoundLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootFunctionTypes {
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;
    public static final LootFunctionType SET_COUNT = LootFunctionTypes.register("set_count", new SetCountLootFunction.Serializer());
    public static final LootFunctionType ENCHANT_WITH_LEVELS = LootFunctionTypes.register("enchant_with_levels", new EnchantWithLevelsLootFunction.Serializer());
    public static final LootFunctionType ENCHANT_RANDOMLY = LootFunctionTypes.register("enchant_randomly", new EnchantRandomlyLootFunction.Serializer());
    public static final LootFunctionType SET_ENCHANTMENTS = LootFunctionTypes.register("set_enchantments", new SetEnchantmentsLootFunction.Serializer());
    public static final LootFunctionType SET_NBT = LootFunctionTypes.register("set_nbt", new SetNbtLootFunction.Serializer());
    public static final LootFunctionType FURNACE_SMELT = LootFunctionTypes.register("furnace_smelt", new FurnaceSmeltLootFunction.Serializer());
    public static final LootFunctionType LOOTING_ENCHANT = LootFunctionTypes.register("looting_enchant", new LootingEnchantLootFunction.Serializer());
    public static final LootFunctionType SET_DAMAGE = LootFunctionTypes.register("set_damage", new SetDamageLootFunction.Serializer());
    public static final LootFunctionType SET_ATTRIBUTES = LootFunctionTypes.register("set_attributes", new SetAttributesLootFunction.Serializer());
    public static final LootFunctionType SET_NAME = LootFunctionTypes.register("set_name", new SetNameLootFunction.Serializer());
    public static final LootFunctionType EXPLORATION_MAP = LootFunctionTypes.register("exploration_map", new ExplorationMapLootFunction.Serializer());
    public static final LootFunctionType SET_STEW_EFFECT = LootFunctionTypes.register("set_stew_effect", new SetStewEffectLootFunction.Serializer());
    public static final LootFunctionType COPY_NAME = LootFunctionTypes.register("copy_name", new CopyNameLootFunction.Serializer());
    public static final LootFunctionType SET_CONTENTS = LootFunctionTypes.register("set_contents", new SetContentsLootFunction.Serializer());
    public static final LootFunctionType LIMIT_COUNT = LootFunctionTypes.register("limit_count", new LimitCountLootFunction.Serializer());
    public static final LootFunctionType APPLY_BONUS = LootFunctionTypes.register("apply_bonus", new ApplyBonusLootFunction.Serializer());
    public static final LootFunctionType SET_LOOT_TABLE = LootFunctionTypes.register("set_loot_table", new SetLootTableLootFunction.Serializer());
    public static final LootFunctionType EXPLOSION_DECAY = LootFunctionTypes.register("explosion_decay", new ExplosionDecayLootFunction.Serializer());
    public static final LootFunctionType SET_LORE = LootFunctionTypes.register("set_lore", new SetLoreLootFunction.Serializer());
    public static final LootFunctionType FILL_PLAYER_HEAD = LootFunctionTypes.register("fill_player_head", new FillPlayerHeadLootFunction.Serializer());
    public static final LootFunctionType COPY_NBT = LootFunctionTypes.register("copy_nbt", new CopyNbtLootFunction.Serializer());
    public static final LootFunctionType COPY_STATE = LootFunctionTypes.register("copy_state", new CopyStateFunction.Serializer());
    public static final LootFunctionType SET_BANNER_PATTERN = LootFunctionTypes.register("set_banner_pattern", new SetBannerPatternFunction.Serializer());
    public static final LootFunctionType SET_POTION = LootFunctionTypes.register("set_potion", new SetPotionLootFunction.Serializer());
    public static final LootFunctionType SET_GOAT_HORN_SOUND = LootFunctionTypes.register("set_goat_horn_sound", new SetGoatHornSoundLootFunction.Serializer());

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(jsonSerializer));
    }

    public static Object createGsonSerializer() {
        return JsonSerializing.createSerializerBuilder(Registry.LOOT_FUNCTION_TYPE, "function", "function", LootFunction::getType).build();
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] lootFunctions) {
        switch (lootFunctions.length) {
            case 0: {
                return NOOP;
            }
            case 1: {
                return lootFunctions[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = lootFunctions[0];
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = lootFunctions[1];
                return (stack, context) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)stack, (LootContext)context), (LootContext)context);
            }
        }
        return (stack, context) -> {
            for (BiFunction biFunction : lootFunctions) {
                stack = (ItemStack)biFunction.apply(stack, context);
            }
            return stack;
        };
    }
}

