/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.ApplyBonusLootFunction;
import net.minecraft.world.loot.function.CopyNameLootFunction;
import net.minecraft.world.loot.function.CopyNbtLootFunction;
import net.minecraft.world.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.world.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.world.loot.function.ExplorationMapLootFunction;
import net.minecraft.world.loot.function.ExplosionDecayLootFunction;
import net.minecraft.world.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.world.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.world.loot.function.LimitCountLootFunction;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootingEnchantLootFunction;
import net.minecraft.world.loot.function.SetAttributesLootFunction;
import net.minecraft.world.loot.function.SetContentsLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetDamageLootFunction;
import net.minecraft.world.loot.function.SetLootTableLootFunction;
import net.minecraft.world.loot.function.SetLoreLootFunction;
import net.minecraft.world.loot.function.SetNameLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;
import net.minecraft.world.loot.function.SetStewEffectLootFunction;

public class LootFunctions {
    private static final Map<Identifier, LootFunction.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass = Maps.newHashMap();
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (itemStack, lootContext) -> itemStack;

    public static <T extends LootFunction> void register(LootFunction.Factory<? extends T> factory) {
        Identifier identifier = factory.getId();
        Class<T> class_ = factory.getFunctionClass();
        if (byId.containsKey(identifier)) {
            throw new IllegalArgumentException("Can't re-register item function name " + identifier);
        }
        if (byClass.containsKey(class_)) {
            throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
        }
        byId.put(identifier, factory);
        byClass.put(class_, factory);
    }

    public static LootFunction.Factory<?> get(Identifier identifier) {
        LootFunction.Factory<?> factory = byId.get(identifier);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item function '" + identifier + "'");
        }
        return factory;
    }

    public static <T extends LootFunction> LootFunction.Factory<T> getFactory(T lootFunction) {
        LootFunction.Factory<?> factory = byClass.get(lootFunction.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item function " + lootFunction);
        }
        return factory;
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] biFunctions) {
        switch (biFunctions.length) {
            case 0: {
                return NOOP;
            }
            case 1: {
                return biFunctions[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = biFunctions[0];
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = biFunctions[1];
                return (itemStack, lootContext) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)itemStack, (LootContext)lootContext), (LootContext)lootContext);
            }
        }
        return (itemStack, lootContext) -> {
            for (BiFunction biFunction : biFunctions) {
                itemStack = (ItemStack)biFunction.apply(itemStack, lootContext);
            }
            return itemStack;
        };
    }

    static {
        LootFunctions.register(new SetCountLootFunction.Factory());
        LootFunctions.register(new EnchantWithLevelsLootFunction.Factory());
        LootFunctions.register(new EnchantRandomlyLootFunction.Factory());
        LootFunctions.register(new SetNbtLootFunction.Builder());
        LootFunctions.register(new FurnaceSmeltLootFunction.Factory());
        LootFunctions.register(new LootingEnchantLootFunction.Factory());
        LootFunctions.register(new SetDamageLootFunction.Factory());
        LootFunctions.register(new SetAttributesLootFunction.Factory());
        LootFunctions.register(new SetNameLootFunction.Factory());
        LootFunctions.register(new ExplorationMapLootFunction.Factory());
        LootFunctions.register(new SetStewEffectLootFunction.Factory());
        LootFunctions.register(new CopyNameLootFunction.Factory());
        LootFunctions.register(new SetContentsLootFunction.Factory());
        LootFunctions.register(new LimitCountLootFunction.Factory());
        LootFunctions.register(new ApplyBonusLootFunction.Factory());
        LootFunctions.register(new SetLootTableLootFunction.Factory());
        LootFunctions.register(new ExplosionDecayLootFunction.Factory());
        LootFunctions.register(new SetLoreLootFunction.Factory());
        LootFunctions.register(new FillPlayerHeadLootFunction.Factory());
        LootFunctions.register(new CopyNbtLootFunction.Factory());
    }

    public static class Factory
    implements JsonDeserializer<LootFunction>,
    JsonSerializer<LootFunction> {
        public LootFunction method_596(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            LootFunction.Factory<?> factory;
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "function");
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "function"));
            try {
                factory = LootFunctions.get(identifier);
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new JsonSyntaxException("Unknown function '" + identifier + "'");
            }
            return factory.fromJson(jsonObject, jsonDeserializationContext);
        }

        public JsonElement method_597(LootFunction lootFunction, Type type, JsonSerializationContext jsonSerializationContext) {
            LootFunction.Factory<LootFunction> factory = LootFunctions.getFactory(lootFunction);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("function", factory.getId().toString());
            factory.toJson(jsonObject, lootFunction, jsonSerializationContext);
            return jsonObject;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_597((LootFunction)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_596(jsonElement, type, jsonDeserializationContext);
        }
    }
}

