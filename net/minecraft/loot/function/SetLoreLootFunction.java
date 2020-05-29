/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class SetLoreLootFunction
extends ConditionalLootFunction {
    private final boolean replace;
    private final List<Text> lore;
    @Nullable
    private final LootContext.EntityTarget entity;

    public SetLoreLootFunction(LootCondition[] conditions, boolean replace, List<Text> lore, @Nullable LootContext.EntityTarget entity) {
        super(conditions);
        this.replace = replace;
        this.lore = ImmutableList.copyOf(lore);
        this.entity = entity;
    }

    @Override
    public LootFunctionType method_29321() {
        return LootFunctionTypes.SET_LORE;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ListTag listTag = this.getLoreForMerge(stack, !this.lore.isEmpty());
        if (listTag != null) {
            if (this.replace) {
                listTag.clear();
            }
            UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, this.entity);
            this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(StringTag::of).forEach(listTag::add);
        }
        return stack;
    }

    @Nullable
    private ListTag getLoreForMerge(ItemStack stack, boolean otherLoreExists) {
        CompoundTag compoundTag2;
        CompoundTag compoundTag;
        if (stack.hasTag()) {
            compoundTag = stack.getTag();
        } else if (otherLoreExists) {
            compoundTag = new CompoundTag();
            stack.setTag(compoundTag);
        } else {
            return null;
        }
        if (compoundTag.contains("display", 10)) {
            compoundTag2 = compoundTag.getCompound("display");
        } else if (otherLoreExists) {
            compoundTag2 = new CompoundTag();
            compoundTag.put("display", compoundTag2);
        } else {
            return null;
        }
        if (compoundTag2.contains("Lore", 9)) {
            return compoundTag2.getList("Lore", 8);
        }
        if (otherLoreExists) {
            ListTag listTag = new ListTag();
            compoundTag2.put("Lore", listTag);
            return listTag;
        }
        return null;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetLoreLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetLoreLootFunction setLoreLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setLoreLootFunction, jsonSerializationContext);
            jsonObject.addProperty("replace", setLoreLootFunction.replace);
            JsonArray jsonArray = new JsonArray();
            for (Text text : setLoreLootFunction.lore) {
                jsonArray.add(Text.Serializer.toJsonTree(text));
            }
            jsonObject.add("lore", jsonArray);
            if (setLoreLootFunction.entity != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize((Object)setLoreLootFunction.entity));
            }
        }

        @Override
        public SetLoreLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
            List list = Streams.stream(JsonHelper.getArray(jsonObject, "lore")).map(Text.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
            return new SetLoreLootFunction(lootConditions, bl, list, entityTarget);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

