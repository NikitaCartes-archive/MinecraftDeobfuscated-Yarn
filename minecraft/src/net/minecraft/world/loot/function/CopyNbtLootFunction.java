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

public class CopyNbtLootFunction extends ConditionalLootFunction {
	private final CopyNbtLootFunction.Source source;
	private final List<CopyNbtLootFunction.Operation> operations;
	private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
	private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = blockEntity -> blockEntity.toTag(new CompoundTag());

	private CopyNbtLootFunction(LootCondition[] lootConditions, CopyNbtLootFunction.Source source, List<CopyNbtLootFunction.Operation> list) {
		super(lootConditions);
		this.source = source;
		this.operations = ImmutableList.copyOf(list);
	}

	private static NbtPathArgumentType.NbtPath parseNbtPath(String string) {
		try {
			return new NbtPathArgumentType().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			throw new IllegalArgumentException("Failed to parse path " + string, var2);
		}
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.source.field_17029);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Tag tag = (Tag)this.source.getter.apply(lootContext);
		if (tag != null) {
			this.operations.forEach(operation -> operation.execute(itemStack::getOrCreateTag, tag));
		}

		return itemStack;
	}

	public static CopyNbtLootFunction.Builder builder(CopyNbtLootFunction.Source source) {
		return new CopyNbtLootFunction.Builder(source);
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyNbtLootFunction.Builder> {
		private final CopyNbtLootFunction.Source source;
		private final List<CopyNbtLootFunction.Operation> operations = Lists.<CopyNbtLootFunction.Operation>newArrayList();

		private Builder(CopyNbtLootFunction.Source source) {
			this.source = source;
		}

		public CopyNbtLootFunction.Builder withOperation(String string, String string2, CopyNbtLootFunction.Operator operator) {
			this.operations.add(new CopyNbtLootFunction.Operation(string, string2, operator));
			return this;
		}

		public CopyNbtLootFunction.Builder withOperation(String string, String string2) {
			return this.withOperation(string, string2, CopyNbtLootFunction.Operator.REPLACE);
		}

		protected CopyNbtLootFunction.Builder method_16855() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyNbtLootFunction(this.method_526(), this.source, this.operations);
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<CopyNbtLootFunction> {
		public Factory() {
			super(new Identifier("copy_nbt"), CopyNbtLootFunction.class);
		}

		public void method_16870(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, copyNbtLootFunction, jsonSerializationContext);
			jsonObject.addProperty("source", copyNbtLootFunction.source.name);
			JsonArray jsonArray = new JsonArray();
			copyNbtLootFunction.operations.stream().map(CopyNbtLootFunction.Operation::toJson).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public CopyNbtLootFunction method_16871(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			CopyNbtLootFunction.Source source = CopyNbtLootFunction.Source.get(JsonHelper.getString(jsonObject, "source"));
			List<CopyNbtLootFunction.Operation> list = Lists.<CopyNbtLootFunction.Operation>newArrayList();

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "ops")) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
				list.add(CopyNbtLootFunction.Operation.fromJson(jsonObject2));
			}

			return new CopyNbtLootFunction(lootConditions, source, list);
		}
	}

	static class Operation {
		private final String sourcePath;
		private final NbtPathArgumentType.NbtPath parsedSourcePath;
		private final String targetPath;
		private final NbtPathArgumentType.NbtPath parsedTargetPath;
		private final CopyNbtLootFunction.Operator operator;

		private Operation(String string, String string2, CopyNbtLootFunction.Operator operator) {
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
					this.operator.merge((Tag)supplier.get(), this.parsedTargetPath, list);
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

		public static CopyNbtLootFunction.Operation fromJson(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "source");
			String string2 = JsonHelper.getString(jsonObject, "target");
			CopyNbtLootFunction.Operator operator = CopyNbtLootFunction.Operator.get(JsonHelper.getString(jsonObject, "op"));
			return new CopyNbtLootFunction.Operation(string, string2, operator);
		}
	}

	public static enum Operator {
		REPLACE("replace") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
				nbtPath.put(tag, Iterables.getLast(list)::copy);
			}
		},
		APPEND("append") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
				List<Tag> list2 = nbtPath.putIfAbsent(tag, ListTag::new);
				list2.forEach(tagx -> {
					if (tagx instanceof ListTag) {
						list.forEach(tag2 -> ((ListTag)tagx).add(tag2.copy()));
					}
				});
			}
		},
		MERGE("merge") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
				List<Tag> list2 = nbtPath.putIfAbsent(tag, CompoundTag::new);
				list2.forEach(tagx -> {
					if (tagx instanceof CompoundTag) {
						list.forEach(tag2 -> {
							if (tag2 instanceof CompoundTag) {
								((CompoundTag)tagx).copyFrom((CompoundTag)tag2);
							}
						});
					}
				});
			}
		};

		private final String name;

		public abstract void merge(Tag tag, NbtPathArgumentType.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException;

		private Operator(String string2) {
			this.name = string2;
		}

		public static CopyNbtLootFunction.Operator get(String string) {
			for (CopyNbtLootFunction.Operator operator : values()) {
				if (operator.name.equals(string)) {
					return operator;
				}
			}

			throw new IllegalArgumentException("Invalid merge strategy" + string);
		}
	}

	public static enum Source {
		field_17024("this", LootContextParameters.field_1226, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		field_17025("killer", LootContextParameters.field_1230, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		field_17026("killer_player", LootContextParameters.field_1233, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		field_17027("block_entity", LootContextParameters.field_1228, CopyNbtLootFunction.BLOCK_ENTITY_TAG_GETTER);

		public final String name;
		public final LootContextParameter<?> field_17029;
		public final Function<LootContext, Tag> getter;

		private <T> Source(String string2, LootContextParameter<T> lootContextParameter, Function<? super T, Tag> function) {
			this.name = string2;
			this.field_17029 = lootContextParameter;
			this.getter = lootContext -> {
				T object = lootContext.method_296(lootContextParameter);
				return object != null ? (Tag)function.apply(object) : null;
			};
		}

		public static CopyNbtLootFunction.Source get(String string) {
			for (CopyNbtLootFunction.Source source : values()) {
				if (source.name.equals(string)) {
					return source;
				}
			}

			throw new IllegalArgumentException("Invalid tag source " + string);
		}
	}
}
