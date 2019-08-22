/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.function.ConditionalLootFunction;
import net.minecraft.world.loot.function.LootFunction;

public class CopyNbtLootFunction
extends ConditionalLootFunction {
    private final Source source;
    private final List<Operation> operations;
    private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
    private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = blockEntity -> blockEntity.toTag(new CompoundTag());

    private CopyNbtLootFunction(LootCondition[] lootConditions, Source source, List<Operation> list) {
        super(lootConditions);
        this.source = source;
        this.operations = ImmutableList.copyOf(list);
    }

    private static NbtPathArgumentType.NbtPath parseNbtPath(String string) {
        try {
            return new NbtPathArgumentType().method_9362(new StringReader(string));
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException("Failed to parse path " + string, commandSyntaxException);
        }
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        Tag tag = this.source.getter.apply(lootContext);
        if (tag != null) {
            this.operations.forEach(operation -> operation.execute(itemStack::getOrCreateTag, tag));
        }
        return itemStack;
    }

    public static Builder builder(Source source) {
        return new Builder(source);
    }

    static /* synthetic */ Function method_16851() {
        return ENTITY_TAG_GETTER;
    }

    static /* synthetic */ Function method_16854() {
        return BLOCK_ENTITY_TAG_GETTER;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<CopyNbtLootFunction> {
        public Factory() {
            super(new Identifier("copy_nbt"), CopyNbtLootFunction.class);
        }

        public void method_16870(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.method_529(jsonObject, copyNbtLootFunction, jsonSerializationContext);
            jsonObject.addProperty("source", ((CopyNbtLootFunction)copyNbtLootFunction).source.name);
            JsonArray jsonArray = new JsonArray();
            copyNbtLootFunction.operations.stream().map(Operation::toJson).forEach(jsonArray::add);
            jsonObject.add("ops", jsonArray);
        }

        public CopyNbtLootFunction method_16871(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Source source = Source.get(JsonHelper.getString(jsonObject, "source"));
            ArrayList<Operation> list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "ops");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
                list.add(Operation.fromJson(jsonObject2));
            }
            return new CopyNbtLootFunction(lootConditions, source, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.method_16871(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }

    public static enum Source {
        THIS("this", LootContextParameters.THIS_ENTITY, CopyNbtLootFunction.method_16851()),
        KILLER("killer", LootContextParameters.KILLER_ENTITY, CopyNbtLootFunction.method_16851()),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER, CopyNbtLootFunction.method_16851()),
        BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY, CopyNbtLootFunction.method_16854());

        public final String name;
        public final LootContextParameter<?> parameter;
        public final Function<LootContext, Tag> getter;

        private <T> Source(String string2, LootContextParameter<T> lootContextParameter, Function<? super T, Tag> function) {
            this.name = string2;
            this.parameter = lootContextParameter;
            this.getter = lootContext -> {
                Object object = lootContext.get(lootContextParameter);
                return object != null ? (Tag)function.apply((Object)object) : null;
            };
        }

        public static Source get(String string) {
            for (Source source : Source.values()) {
                if (!source.name.equals(string)) continue;
                return source;
            }
            throw new IllegalArgumentException("Invalid tag source " + string);
        }
    }

    public static enum Operator {
        REPLACE("replace"){

            @Override
            public void merge(Tag tag, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                nbtPath.put(tag, Iterables.getLast(list)::copy);
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(Tag tag2, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = nbtPath.putIfAbsent(tag2, ListTag::new);
                list2.forEach(tag -> {
                    if (tag instanceof ListTag) {
                        list.forEach(tag2 -> ((ListTag)tag).add(tag2.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(Tag tag2, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = nbtPath.putIfAbsent(tag2, CompoundTag::new);
                list2.forEach(tag -> {
                    if (tag instanceof CompoundTag) {
                        list.forEach(tag2 -> {
                            if (tag2 instanceof CompoundTag) {
                                ((CompoundTag)tag).copyFrom((CompoundTag)tag2);
                            }
                        });
                    }
                });
            }
        };

        private final String name;

        public abstract void merge(Tag var1, NbtPathArgumentType.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        private Operator(String string2) {
            this.name = string2;
        }

        public static Operator get(String string) {
            for (Operator operator : Operator.values()) {
                if (!operator.name.equals(string)) continue;
                return operator;
            }
            throw new IllegalArgumentException("Invalid merge strategy" + string);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Source source;
        private final List<Operation> operations = Lists.newArrayList();

        private Builder(Source source) {
            this.source = source;
        }

        public Builder withOperation(String string, String string2, Operator operator) {
            this.operations.add(new Operation(string, string2, operator));
            return this;
        }

        public Builder withOperation(String string, String string2) {
            return this.withOperation(string, string2, Operator.REPLACE);
        }

        protected Builder method_16855() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.method_16855();
        }
    }

    static class Operation {
        private final String sourcePath;
        private final NbtPathArgumentType.NbtPath parsedSourcePath;
        private final String targetPath;
        private final NbtPathArgumentType.NbtPath parsedTargetPath;
        private final Operator operator;

        private Operation(String string, String string2, Operator operator) {
            this.sourcePath = string;
            this.parsedSourcePath = CopyNbtLootFunction.parseNbtPath(string);
            this.targetPath = string2;
            this.parsedTargetPath = CopyNbtLootFunction.parseNbtPath(string2);
            this.operator = operator;
        }

        public void execute(Supplier<Tag> supplier, Tag tag) {
            try {
                List<Tag> list = this.parsedSourcePath.get(tag);
                if (!list.isEmpty()) {
                    this.operator.merge(supplier.get(), this.parsedTargetPath, list);
                }
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("source", this.sourcePath);
            jsonObject.addProperty("target", this.targetPath);
            jsonObject.addProperty("op", this.operator.name);
            return jsonObject;
        }

        public static Operation fromJson(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "source");
            String string2 = JsonHelper.getString(jsonObject, "target");
            Operator operator = Operator.get(JsonHelper.getString(jsonObject, "op"));
            return new Operation(string, string2, operator);
        }
    }
}

