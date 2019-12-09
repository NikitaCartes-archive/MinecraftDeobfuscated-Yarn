/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class CopyNbtLootFunction
extends ConditionalLootFunction {
    private final Source source;
    private final List<Operation> operations;
    private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
    private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = blockEntity -> blockEntity.toTag(new CompoundTag());

    private CopyNbtLootFunction(LootCondition[] conditions, Source source, List<Operation> operations) {
        super(conditions);
        this.source = source;
        this.operations = ImmutableList.copyOf(operations);
    }

    private static NbtPathArgumentType.NbtPath parseNbtPath(String nbtPath) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(nbtPath));
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException("Failed to parse path " + nbtPath, commandSyntaxException);
        }
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Tag tag = this.source.getter.apply(context);
        if (tag != null) {
            this.operations.forEach(operation -> operation.execute(stack::getOrCreateTag, tag));
        }
        return stack;
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

        @Override
        public void toJson(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, copyNbtLootFunction, jsonSerializationContext);
            jsonObject.addProperty("source", ((CopyNbtLootFunction)copyNbtLootFunction).source.name);
            JsonArray jsonArray = new JsonArray();
            copyNbtLootFunction.operations.stream().map(Operation::toJson).forEach(jsonArray::add);
            jsonObject.add("ops", jsonArray);
        }

        @Override
        public CopyNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
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

        private <T> Source(String name, LootContextParameter<T> parameter, Function<? super T, Tag> operator) {
            this.name = name;
            this.parameter = parameter;
            this.getter = context -> {
                Object object = context.get(parameter);
                return object != null ? (Tag)operator.apply((Object)object) : null;
            };
        }

        public static Source get(String name) {
            for (Source source : Source.values()) {
                if (!source.name.equals(name)) continue;
                return source;
            }
            throw new IllegalArgumentException("Invalid tag source " + name);
        }
    }

    public static enum Operator {
        REPLACE("replace"){

            @Override
            public void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException {
                tragetPath.put(itemTag, Iterables.getLast(sourceTags)::copy);
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException {
                List<Tag> list = tragetPath.getOrInit(itemTag, ListTag::new);
                list.forEach(foundTag -> {
                    if (foundTag instanceof ListTag) {
                        sourceTags.forEach(listTag -> ((ListTag)foundTag).add(listTag.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException {
                List<Tag> list = tragetPath.getOrInit(itemTag, CompoundTag::new);
                list.forEach(foundTag -> {
                    if (foundTag instanceof CompoundTag) {
                        sourceTags.forEach(compoundTag -> {
                            if (compoundTag instanceof CompoundTag) {
                                ((CompoundTag)foundTag).copyFrom((CompoundTag)compoundTag);
                            }
                        });
                    }
                });
            }
        };

        private final String name;

        public abstract void merge(Tag var1, NbtPathArgumentType.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        private Operator(String name) {
            this.name = name;
        }

        public static Operator get(String name) {
            for (Operator operator : Operator.values()) {
                if (!operator.name.equals(name)) continue;
                return operator;
            }
            throw new IllegalArgumentException("Invalid merge strategy" + name);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Source source;
        private final List<Operation> operations = Lists.newArrayList();

        private Builder(Source source) {
            this.source = source;
        }

        public Builder withOperation(String source, String target, Operator operator) {
            this.operations.add(new Operation(source, target, operator));
            return this;
        }

        public Builder withOperation(String source, String target) {
            return this.withOperation(source, target, Operator.REPLACE);
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    static class Operation {
        private final String sourcePath;
        private final NbtPathArgumentType.NbtPath parsedSourcePath;
        private final String targetPath;
        private final NbtPathArgumentType.NbtPath parsedTargetPath;
        private final Operator operator;

        private Operation(String source, String target, Operator operator) {
            this.sourcePath = source;
            this.parsedSourcePath = CopyNbtLootFunction.parseNbtPath(source);
            this.targetPath = target;
            this.parsedTargetPath = CopyNbtLootFunction.parseNbtPath(target);
            this.operator = operator;
        }

        public void execute(Supplier<Tag> itemTagTagGetter, Tag sourceEntityTag) {
            try {
                List<Tag> list = this.parsedSourcePath.get(sourceEntityTag);
                if (!list.isEmpty()) {
                    this.operator.merge(itemTagTagGetter.get(), this.parsedTargetPath, list);
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

        public static Operation fromJson(JsonObject json) {
            String string = JsonHelper.getString(json, "source");
            String string2 = JsonHelper.getString(json, "target");
            Operator operator = Operator.get(JsonHelper.getString(json, "op"));
            return new Operation(string, string2, operator);
        }
    }
}

