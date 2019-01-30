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
	private final List<CopyNbtLootFunction.class_3839> field_17014;
	private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
	private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = blockEntity -> blockEntity.toTag(new CompoundTag());

	private CopyNbtLootFunction(LootCondition[] lootConditions, CopyNbtLootFunction.Source source, List<CopyNbtLootFunction.class_3839> list) {
		super(lootConditions);
		this.source = source;
		this.field_17014 = ImmutableList.copyOf(list);
	}

	private static NbtPathArgumentType.class_2209 method_16853(String string) {
		try {
			return new NbtPathArgumentType().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			throw new IllegalArgumentException("Failed to parse path " + string, var2);
		}
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.source.parameter);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Tag tag = (Tag)this.source.getter.apply(lootContext);
		if (tag != null) {
			this.field_17014.forEach(arg -> arg.method_16860(itemStack::getOrCreateTag, tag));
		}

		return itemStack;
	}

	public static CopyNbtLootFunction.class_3838 method_16848(CopyNbtLootFunction.Source source) {
		return new CopyNbtLootFunction.class_3838(source);
	}

	public static class Factory extends ConditionalLootFunction.Factory<CopyNbtLootFunction> {
		public Factory() {
			super(new Identifier("copy_nbt"), CopyNbtLootFunction.class);
		}

		public void method_16870(JsonObject jsonObject, CopyNbtLootFunction copyNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, copyNbtLootFunction, jsonSerializationContext);
			jsonObject.addProperty("source", copyNbtLootFunction.source.name);
			JsonArray jsonArray = new JsonArray();
			copyNbtLootFunction.field_17014.stream().map(CopyNbtLootFunction.class_3839::method_16858).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public CopyNbtLootFunction method_16871(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			CopyNbtLootFunction.Source source = CopyNbtLootFunction.Source.get(JsonHelper.getString(jsonObject, "source"));
			List<CopyNbtLootFunction.class_3839> list = Lists.<CopyNbtLootFunction.class_3839>newArrayList();

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "ops")) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
				list.add(CopyNbtLootFunction.class_3839.method_16859(jsonObject2));
			}

			return new CopyNbtLootFunction(lootConditions, source, list);
		}
	}

	public static enum MergeStrategy {
		REPLACE("replace") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
				arg.method_9368(tag, Iterables.getLast(list)::copy);
			}
		},
		APPEND("append") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
				List<Tag> list2 = arg.method_9367(tag, ListTag::new);
				list2.forEach(tagx -> {
					if (tagx instanceof ListTag) {
						list.forEach(tag2 -> ((ListTag)tagx).add(tag2.copy()));
					}
				});
			}
		},
		MERGE("merge") {
			@Override
			public void merge(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
				List<Tag> list2 = arg.method_9367(tag, CompoundTag::new);
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

		public abstract void merge(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException;

		private MergeStrategy(String string2) {
			this.name = string2;
		}

		public static CopyNbtLootFunction.MergeStrategy get(String string) {
			for (CopyNbtLootFunction.MergeStrategy mergeStrategy : values()) {
				if (mergeStrategy.name.equals(string)) {
					return mergeStrategy;
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
		public final LootContextParameter<?> parameter;
		public final Function<LootContext, Tag> getter;

		private <T> Source(String string2, LootContextParameter<T> lootContextParameter, Function<? super T, Tag> function) {
			this.name = string2;
			this.parameter = lootContextParameter;
			this.getter = lootContext -> {
				T object = lootContext.get(lootContextParameter);
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

	public static class class_3838 extends ConditionalLootFunction.Builder<CopyNbtLootFunction.class_3838> {
		private final CopyNbtLootFunction.Source field_17017;
		private final List<CopyNbtLootFunction.class_3839> field_17018 = Lists.<CopyNbtLootFunction.class_3839>newArrayList();

		private class_3838(CopyNbtLootFunction.Source source) {
			this.field_17017 = source;
		}

		public CopyNbtLootFunction.class_3838 method_16857(String string, String string2, CopyNbtLootFunction.MergeStrategy mergeStrategy) {
			this.field_17018.add(new CopyNbtLootFunction.class_3839(string, string2, mergeStrategy));
			return this;
		}

		public CopyNbtLootFunction.class_3838 method_16856(String string, String string2) {
			return this.method_16857(string, string2, CopyNbtLootFunction.MergeStrategy.REPLACE);
		}

		protected CopyNbtLootFunction.class_3838 method_16855() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyNbtLootFunction(this.getConditions(), this.field_17017, this.field_17018);
		}
	}

	static class class_3839 {
		private final String field_17019;
		private final NbtPathArgumentType.class_2209 field_17020;
		private final String field_17021;
		private final NbtPathArgumentType.class_2209 field_17022;
		private final CopyNbtLootFunction.MergeStrategy field_17023;

		private class_3839(String string, String string2, CopyNbtLootFunction.MergeStrategy mergeStrategy) {
			this.field_17019 = string;
			this.field_17020 = CopyNbtLootFunction.method_16853(string);
			this.field_17021 = string2;
			this.field_17022 = CopyNbtLootFunction.method_16853(string2);
			this.field_17023 = mergeStrategy;
		}

		public void method_16860(Supplier<Tag> supplier, Tag tag) {
			try {
				List<Tag> list = this.field_17020.method_9366(tag);
				if (!list.isEmpty()) {
					this.field_17023.merge((Tag)supplier.get(), this.field_17022, list);
				}
			} catch (CommandSyntaxException var4) {
			}
		}

		public JsonObject method_16858() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("source", this.field_17019);
			jsonObject.addProperty("target", this.field_17021);
			jsonObject.addProperty("op", this.field_17023.name);
			return jsonObject;
		}

		public static CopyNbtLootFunction.class_3839 method_16859(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "source");
			String string2 = JsonHelper.getString(jsonObject, "target");
			CopyNbtLootFunction.MergeStrategy mergeStrategy = CopyNbtLootFunction.MergeStrategy.get(JsonHelper.getString(jsonObject, "op"));
			return new CopyNbtLootFunction.class_3839(string, string2, mergeStrategy);
		}
	}
}
