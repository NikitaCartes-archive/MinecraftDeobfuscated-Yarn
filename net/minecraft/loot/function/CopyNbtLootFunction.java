/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
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
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.JsonHelper;

public class CopyNbtLootFunction
extends ConditionalLootFunction {
    private final LootNbtProvider source;
    private final List<Operation> operations;

    private CopyNbtLootFunction(LootCondition[] conditions, LootNbtProvider source, List<Operation> operations) {
        super(conditions);
        this.source = source;
        this.operations = ImmutableList.copyOf(operations);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.COPY_NBT;
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
        return this.source.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        NbtElement nbtElement = this.source.getNbtTag(context);
        if (nbtElement != null) {
            this.operations.forEach(operation -> operation.execute(stack::getOrCreateTag, nbtElement));
        }
        return stack;
    }

    public static Builder builder(LootNbtProvider source) {
        return new Builder(source);
    }

    public static Builder builder(LootContext.EntityTarget target) {
        return new Builder(ContextLootNbtProvider.fromTarget(target));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<CopyNbtLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, copyNbtLootFunction, jsonSerializationContext);
            jsonObject.add("source", jsonSerializationContext.serialize(copyNbtLootFunction.source));
            JsonArray jsonArray = new JsonArray();
            copyNbtLootFunction.operations.stream().map(Operation::toJson).forEach(jsonArray::add);
            jsonObject.add("ops", jsonArray);
        }

        @Override
        public CopyNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootNbtProvider lootNbtProvider = JsonHelper.deserialize(jsonObject, "source", jsonDeserializationContext, LootNbtProvider.class);
            ArrayList<Operation> list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "ops");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
                list.add(Operation.fromJson(jsonObject2));
            }
            return new CopyNbtLootFunction(lootConditions, lootNbtProvider, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static enum Operator {
        REPLACE("replace"){

            @Override
            public void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException {
                targetPath.put(itemTag, Iterables.getLast(sourceTags)::copy);
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(itemTag, NbtList::new);
                list.forEach(foundTag -> {
                    if (foundTag instanceof NbtList) {
                        sourceTags.forEach(listTag -> ((NbtList)foundTag).add(listTag.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(itemTag, NbtCompound::new);
                list.forEach(foundTag -> {
                    if (foundTag instanceof NbtCompound) {
                        sourceTags.forEach(compoundTag -> {
                            if (compoundTag instanceof NbtCompound) {
                                ((NbtCompound)foundTag).copyFrom((NbtCompound)compoundTag);
                            }
                        });
                    }
                });
            }
        };

        private final String name;

        public abstract void merge(NbtElement var1, NbtPathArgumentType.NbtPath var2, List<NbtElement> var3) throws CommandSyntaxException;

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
        private final LootNbtProvider source;
        private final List<Operation> operations = Lists.newArrayList();

        private Builder(LootNbtProvider source) {
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

        public void execute(Supplier<NbtElement> itemTagTagGetter, NbtElement sourceEntityTag) {
            try {
                List<NbtElement> list = this.parsedSourcePath.get(sourceEntityTag);
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

