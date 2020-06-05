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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.JsonHelper;

public class CopyNbtLootFunction extends ConditionalLootFunction {
	private final CopyNbtLootFunction.Source source;
	private final List<CopyNbtLootFunction.Operation> operations;
	private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
	private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = blockEntity -> blockEntity.toTag(new CompoundTag());

	private CopyNbtLootFunction(LootCondition[] conditions, CopyNbtLootFunction.Source source, List<CopyNbtLootFunction.Operation> operations) {
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
		return ImmutableSet.of(this.source.parameter);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Tag tag = (Tag)this.source.getter.apply(context);
		if (tag != null) {
			this.operations.forEach(operation -> operation.execute(stack::getOrCreateTag, tag));
		}

		return stack;
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

		public void execute(Supplier<Tag> itemTagTagGetter, Tag sourceEntityTag) {
			try {
				List<Tag> list = this.parsedSourcePath.get(sourceEntityTag);
				if (!list.isEmpty()) {
					this.operator.merge((Tag)itemTagTagGetter.get(), this.parsedTargetPath, list);
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
			public void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException {
				tragetPath.put(itemTag, Iterables.getLast(sourceTags)::copy);
			}
		},
		APPEND("append") {
			@Override
			public void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException {
				List<Tag> list = tragetPath.getOrInit(itemTag, ListTag::new);
				list.forEach(foundTag -> {
					if (foundTag instanceof ListTag) {
						sourceTags.forEach(listTag -> ((ListTag)foundTag).add(listTag.copy()));
					}
				});
			}
		},
		MERGE("merge") {
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

		public abstract void merge(Tag itemTag, NbtPathArgumentType.NbtPath tragetPath, List<Tag> sourceTags) throws CommandSyntaxException;

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
			jsonObject.addProperty("source", copyNbtLootFunction.source.name);
			JsonArray jsonArray = new JsonArray();
			copyNbtLootFunction.operations.stream().map(CopyNbtLootFunction.Operation::toJson).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public CopyNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			CopyNbtLootFunction.Source source = CopyNbtLootFunction.Source.get(JsonHelper.getString(jsonObject, "source"));
			List<CopyNbtLootFunction.Operation> list = Lists.<CopyNbtLootFunction.Operation>newArrayList();

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "ops")) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
				list.add(CopyNbtLootFunction.Operation.fromJson(jsonObject2));
			}

			return new CopyNbtLootFunction(lootConditions, source, list);
		}
	}

	public static enum Source {
		THIS("this", LootContextParameters.THIS_ENTITY, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		KILLER("killer", LootContextParameters.KILLER_ENTITY, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER, CopyNbtLootFunction.ENTITY_TAG_GETTER),
		BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY, CopyNbtLootFunction.BLOCK_ENTITY_TAG_GETTER);

		public final String name;
		public final LootContextParameter<?> parameter;
		public final Function<LootContext, Tag> getter;

		private <T> Source(String name, LootContextParameter<T> parameter, Function<? super T, Tag> operator) {
			this.name = name;
			this.parameter = parameter;
			this.getter = context -> {
				T object = context.get(parameter);
				return object != null ? (Tag)operator.apply(object) : null;
			};
		}

		public static CopyNbtLootFunction.Source get(String name) {
			for (CopyNbtLootFunction.Source source : values()) {
				if (source.name.equals(name)) {
					return source;
				}
			}

			throw new IllegalArgumentException("Invalid tag source " + name);
		}
	}
}
