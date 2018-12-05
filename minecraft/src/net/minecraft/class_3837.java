package net.minecraft;

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
import net.minecraft.world.loot.context.Parameter;
import net.minecraft.world.loot.context.Parameters;
import net.minecraft.world.loot.function.ConditionalLootFunction;
import net.minecraft.world.loot.function.LootFunction;

public class class_3837 extends ConditionalLootFunction {
	private final class_3837.class_3840 field_17013;
	private final List<class_3837.class_3839> field_17014;
	private static final Function<Entity, Tag> field_17015 = NbtPredicate::method_9076;
	private static final Function<BlockEntity, Tag> field_17016 = blockEntity -> blockEntity.toTag(new CompoundTag());

	private class_3837(LootCondition[] lootConditions, class_3837.class_3840 arg, List<class_3837.class_3839> list) {
		super(lootConditions);
		this.field_17013 = arg;
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
	public Set<Parameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.field_17013.field_17029);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Tag tag = (Tag)this.field_17013.field_17030.apply(lootContext);
		if (tag != null) {
			this.field_17014.forEach(arg -> arg.method_16860(itemStack::getOrCreateTag, tag));
		}

		return itemStack;
	}

	public static class_3837.class_3838 method_16848(class_3837.class_3840 arg) {
		return new class_3837.class_3838(arg);
	}

	public static class class_3838 extends ConditionalLootFunction.Builder<class_3837.class_3838> {
		private final class_3837.class_3840 field_17017;
		private final List<class_3837.class_3839> field_17018 = Lists.<class_3837.class_3839>newArrayList();

		private class_3838(class_3837.class_3840 arg) {
			this.field_17017 = arg;
		}

		public class_3837.class_3838 method_16857(String string, String string2, class_3837.class_3841 arg) {
			this.field_17018.add(new class_3837.class_3839(string, string2, arg));
			return this;
		}

		public class_3837.class_3838 method_16856(String string, String string2) {
			return this.method_16857(string, string2, class_3837.class_3841.field_17032);
		}

		protected class_3837.class_3838 method_16855() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new class_3837(this.getConditions(), this.field_17017, this.field_17018);
		}
	}

	static class class_3839 {
		private final String field_17019;
		private final NbtPathArgumentType.class_2209 field_17020;
		private final String field_17021;
		private final NbtPathArgumentType.class_2209 field_17022;
		private final class_3837.class_3841 field_17023;

		private class_3839(String string, String string2, class_3837.class_3841 arg) {
			this.field_17019 = string;
			this.field_17020 = class_3837.method_16853(string);
			this.field_17021 = string2;
			this.field_17022 = class_3837.method_16853(string2);
			this.field_17023 = arg;
		}

		public void method_16860(Supplier<Tag> supplier, Tag tag) {
			try {
				List<Tag> list = this.field_17020.method_9366(tag);
				if (!list.isEmpty()) {
					this.field_17023.method_16864((Tag)supplier.get(), this.field_17022, list);
				}
			} catch (CommandSyntaxException var4) {
			}
		}

		public JsonObject method_16858() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("source", this.field_17019);
			jsonObject.addProperty("target", this.field_17021);
			jsonObject.addProperty("op", this.field_17023.field_17035);
			return jsonObject;
		}

		public static class_3837.class_3839 method_16859(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "source");
			String string2 = JsonHelper.getString(jsonObject, "target");
			class_3837.class_3841 lv = class_3837.class_3841.method_16865(JsonHelper.getString(jsonObject, "op"));
			return new class_3837.class_3839(string, string2, lv);
		}
	}

	public static enum class_3840 {
		field_17024("this", Parameters.field_1226, class_3837.field_17015),
		field_17025("killer", Parameters.field_1230, class_3837.field_17015),
		field_17026("killer_player", Parameters.field_1233, class_3837.field_17015),
		field_17027("block_entity", Parameters.field_1228, class_3837.field_17016);

		public final String field_17028;
		public final Parameter<?> field_17029;
		public final Function<LootContext, Tag> field_17030;

		private <T> class_3840(String string2, Parameter<T> parameter, Function<? super T, Tag> function) {
			this.field_17028 = string2;
			this.field_17029 = parameter;
			this.field_17030 = lootContext -> {
				T object = lootContext.get(parameter);
				return object != null ? (Tag)function.apply(object) : null;
			};
		}

		public static class_3837.class_3840 method_16862(String string) {
			for (class_3837.class_3840 lv : values()) {
				if (lv.field_17028.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid tag source " + string);
		}
	}

	public static enum class_3841 {
		field_17032("replace") {
			@Override
			public void method_16864(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
				arg.method_9368(tag, Iterables.<Tag>getLast(list).copy());
			}
		},
		field_17033("append") {
			@Override
			public void method_16864(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
				List<Tag> list2 = arg.method_9367(tag, ListTag::new);
				list2.forEach(tagx -> {
					if (tagx instanceof ListTag) {
						list.forEach(tag2 -> ((ListTag)tagx).add(tag2.copy()));
					}
				});
			}
		},
		field_17034("merge") {
			@Override
			public void method_16864(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
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

		private final String field_17035;

		public abstract void method_16864(Tag tag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException;

		private class_3841(String string2) {
			this.field_17035 = string2;
		}

		public static class_3837.class_3841 method_16865(String string) {
			for (class_3837.class_3841 lv : values()) {
				if (lv.field_17035.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid merge strategy" + string);
		}
	}

	public static class class_3842 extends ConditionalLootFunction.Factory<class_3837> {
		public class_3842() {
			super(new Identifier("copy_nbt"), class_3837.class);
		}

		public void method_16870(JsonObject jsonObject, class_3837 arg, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("source", arg.field_17013.field_17028);
			JsonArray jsonArray = new JsonArray();
			arg.field_17014.stream().map(class_3837.class_3839::method_16858).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public class_3837 method_16871(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			class_3837.class_3840 lv = class_3837.class_3840.method_16862(JsonHelper.getString(jsonObject, "source"));
			List<class_3837.class_3839> list = Lists.<class_3837.class_3839>newArrayList();

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "ops")) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
				list.add(class_3837.class_3839.method_16859(jsonObject2));
			}

			return new class_3837(lootConditions, lv, list);
		}
	}
}
