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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.jetbrains.annotations.Nullable;

public class SetLoreLootFunction
extends ConditionalLootFunction {
    private final boolean replace;
    private final List<Text> lore;
    @Nullable
    private final LootContext.EntityTarget entity;

    public SetLoreLootFunction(LootCondition[] lootConditions, boolean bl, List<Text> list, @Nullable LootContext.EntityTarget entityTarget) {
        super(lootConditions);
        this.replace = bl;
        this.lore = ImmutableList.copyOf(list);
        this.entity = entityTarget;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.entity != null ? ImmutableSet.of(this.entity.getIdentifier()) : ImmutableSet.of();
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        ListTag listTag = this.getLoreForMerge(itemStack, !this.lore.isEmpty());
        if (listTag != null) {
            if (this.replace) {
                listTag.clear();
            }
            UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(lootContext, this.entity);
            this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(StringTag::new).forEach(listTag::add);
        }
        return itemStack;
    }

    @Nullable
    private ListTag getLoreForMerge(ItemStack itemStack, boolean bl) {
        CompoundTag compoundTag2;
        CompoundTag compoundTag;
        if (itemStack.hasTag()) {
            compoundTag = itemStack.getTag();
        } else if (bl) {
            compoundTag = new CompoundTag();
            itemStack.setTag(compoundTag);
        } else {
            return null;
        }
        if (compoundTag.contains("display", 10)) {
            compoundTag2 = compoundTag.getCompound("display");
        } else if (bl) {
            compoundTag2 = new CompoundTag();
            compoundTag.put("display", compoundTag2);
        } else {
            return null;
        }
        if (compoundTag2.contains("Lore", 9)) {
            return compoundTag2.getList("Lore", 8);
        }
        if (bl) {
            ListTag listTag = new ListTag();
            compoundTag2.put("Lore", listTag);
            return listTag;
        }
        return null;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetLoreLootFunction> {
        public Factory() {
            super(new Identifier("set_lore"), SetLoreLootFunction.class);
        }

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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}

