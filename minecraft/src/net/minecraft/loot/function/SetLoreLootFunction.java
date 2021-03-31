package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class SetLoreLootFunction extends ConditionalLootFunction {
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
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_LORE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		NbtList nbtList = this.getLoreForMerge(stack, !this.lore.isEmpty());
		if (nbtList != null) {
			if (this.replace) {
				nbtList.clear();
			}

			UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, this.entity);
			this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(NbtString::of).forEach(nbtList::add);
		}

		return stack;
	}

	@Nullable
	private NbtList getLoreForMerge(ItemStack stack, boolean otherLoreExists) {
		NbtCompound nbtCompound;
		if (stack.hasTag()) {
			nbtCompound = stack.getTag();
		} else {
			if (!otherLoreExists) {
				return null;
			}

			nbtCompound = new NbtCompound();
			stack.setTag(nbtCompound);
		}

		NbtCompound nbtCompound2;
		if (nbtCompound.contains("display", NbtElement.COMPOUND_TYPE)) {
			nbtCompound2 = nbtCompound.getCompound("display");
		} else {
			if (!otherLoreExists) {
				return null;
			}

			nbtCompound2 = new NbtCompound();
			nbtCompound.put("display", nbtCompound2);
		}

		if (nbtCompound2.contains("Lore", NbtElement.LIST_TYPE)) {
			return nbtCompound2.getList("Lore", NbtElement.STRING_TYPE);
		} else if (otherLoreExists) {
			NbtList nbtList = new NbtList();
			nbtCompound2.put("Lore", nbtList);
			return nbtList;
		} else {
			return null;
		}
	}

	public static SetLoreLootFunction.Builder method_35544() {
		return new SetLoreLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetLoreLootFunction.Builder> {
		private boolean replace;
		private LootContext.EntityTarget target;
		private final List<Text> lore = Lists.<Text>newArrayList();

		public SetLoreLootFunction.Builder replace(boolean replace) {
			this.replace = replace;
			return this;
		}

		public SetLoreLootFunction.Builder target(LootContext.EntityTarget target) {
			this.target = target;
			return this;
		}

		public SetLoreLootFunction.Builder lore(Text lore) {
			this.lore.add(lore);
			return this;
		}

		protected SetLoreLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetLoreLootFunction(this.getConditions(), this.replace, this.lore, this.target);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetLoreLootFunction> {
		public void toJson(JsonObject jsonObject, SetLoreLootFunction setLoreLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setLoreLootFunction, jsonSerializationContext);
			jsonObject.addProperty("replace", setLoreLootFunction.replace);
			JsonArray jsonArray = new JsonArray();

			for (Text text : setLoreLootFunction.lore) {
				jsonArray.add(Text.Serializer.toJsonTree(text));
			}

			jsonObject.add("lore", jsonArray);
			if (setLoreLootFunction.entity != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(setLoreLootFunction.entity));
			}
		}

		public SetLoreLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
			List<Text> list = (List<Text>)Streams.stream(JsonHelper.getArray(jsonObject, "lore"))
				.map(Text.Serializer::fromJson)
				.collect(ImmutableList.toImmutableList());
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
			return new SetLoreLootFunction(lootConditions, bl, list, entityTarget);
		}
	}
}
