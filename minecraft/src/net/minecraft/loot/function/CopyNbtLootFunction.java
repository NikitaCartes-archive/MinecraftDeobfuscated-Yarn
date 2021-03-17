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
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.JsonHelper;

public class CopyNbtLootFunction extends ConditionalLootFunction {
	private final LootNbtProvider source;
	private final List<CopyNbtLootFunction.Operation> operations;

	private CopyNbtLootFunction(LootCondition[] conditions, LootNbtProvider source, List<CopyNbtLootFunction.Operation> operations) {
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
		} catch (CommandSyntaxException var2) {
			throw new IllegalArgumentException("Failed to parse path " + nbtPath, var2);
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

	public static CopyNbtLootFunction.Builder builder(LootNbtProvider source) {
		return new CopyNbtLootFunction.Builder(source);
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyNbtLootFunction.Builder> {
		private final LootNbtProvider source;
		private final List<CopyNbtLootFunction.Operation> operations = Lists.<CopyNbtLootFunction.Operation>newArrayList();

		private Builder(LootNbtProvider source) {
			this.source = source;
		}

		public CopyNbtLootFunction.Builder withOperation(String source, String target, CopyNbtLootFunction.Operator operator) {
			this.operations.add(new CopyNbtLootFunction.Operation(source, target, operator));
			return this;
		}

		public CopyNbtLootFunction.Builder withOperation(String source, String target) {
			return this.withOperation(source, target, CopyNbtLootFunction.Operator.REPLACE);
		}

		protected CopyNbtLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
		}
	}

	static class Operation {
		private final String sourcePath;
		private final NbtPathArgumentType.NbtPath parsedSourcePath;
		private final String targetPath;
		private final NbtPathArgumentType.NbtPath parsedTargetPath;
		private final CopyNbtLootFunction.Operator operator;

		private Operation(String source, String target, CopyNbtLootFunction.Operator operator) {
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
					this.operator.merge((NbtElement)itemTagTagGetter.get(), this.parsedTargetPath, list);
				}
			} catch (CommandSyntaxException var4) {
			}
		}

		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("source", this.sourcePath);
			jsonObject.addProperty("target", this.targetPath);
			jsonObject.addProperty("op", this.operator.name);
			return jsonObject;
		}

		public static CopyNbtLootFunction.Operation fromJson(JsonObject json) {
			String string = JsonHelper.getString(json, "source");
			String string2 = JsonHelper.getString(json, "target");
			CopyNbtLootFunction.Operator operator = CopyNbtLootFunction.Operator.get(JsonHelper.getString(json, "op"));
			return new CopyNbtLootFunction.Operation(string, string2, operator);
		}
	}

	public static enum Operator {
		REPLACE("replace") {
			@Override
			public void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException {
				targetPath.put(itemTag, Iterables.getLast(sourceTags)::copy);
			}
		},
		APPEND("append") {
			@Override
			public void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException {
				List<NbtElement> list = targetPath.getOrInit(itemTag, NbtList::new);
				list.forEach(foundTag -> {
					if (foundTag instanceof NbtList) {
						sourceTags.forEach(listTag -> ((NbtList)foundTag).add(listTag.copy()));
					}
				});
			}
		},
		MERGE("merge") {
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

		public abstract void merge(NbtElement itemTag, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceTags) throws CommandSyntaxException;

		private Operator(String name) {
			this.name = name;
		}

		public static CopyNbtLootFunction.Operator get(String name) {
			for (CopyNbtLootFunction.Operator operator : values()) {
				if (operator.name.equals(name)) {
					return operator;
				}
			}

			throw new IllegalArgumentException("Invalid merge strategy" + name);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<CopyNbtLootFunction> {
		public void toJson(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, copyNbtLootFunction, jsonSerializationContext);
			jsonObject.add("source", jsonSerializationContext.serialize(copyNbtLootFunction.source));
			JsonArray jsonArray = new JsonArray();
			copyNbtLootFunction.operations.stream().map(CopyNbtLootFunction.Operation::toJson).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public CopyNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootNbtProvider lootNbtProvider = JsonHelper.deserialize(jsonObject, "source", jsonDeserializationContext, LootNbtProvider.class);
			List<CopyNbtLootFunction.Operation> list = Lists.<CopyNbtLootFunction.Operation>newArrayList();

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "ops")) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
				list.add(CopyNbtLootFunction.Operation.fromJson(jsonObject2));
			}

			return new CopyNbtLootFunction(lootConditions, lootNbtProvider, list);
		}
	}
}
