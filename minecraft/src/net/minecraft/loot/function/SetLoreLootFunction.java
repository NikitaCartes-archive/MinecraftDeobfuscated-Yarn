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
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		ListTag listTag = this.getLoreForMerge(stack, !this.lore.isEmpty());
		if (listTag != null) {
			if (this.replace) {
				listTag.clear();
			}

			UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, this.entity);
			this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(StringTag::of).forEach(listTag::add);
		}

		return stack;
	}

	@Nullable
	private ListTag getLoreForMerge(ItemStack stack, boolean otherLoreExists) {
		CompoundTag compoundTag;
		if (stack.hasTag()) {
			compoundTag = stack.getTag();
		} else {
			if (!otherLoreExists) {
				return null;
			}

			compoundTag = new CompoundTag();
			stack.setTag(compoundTag);
		}

		CompoundTag compoundTag2;
		if (compoundTag.contains("display", 10)) {
			compoundTag2 = compoundTag.getCompound("display");
		} else {
			if (!otherLoreExists) {
				return null;
			}

			compoundTag2 = new CompoundTag();
			compoundTag.put("display", compoundTag2);
		}

		if (compoundTag2.contains("Lore", 9)) {
			return compoundTag2.getList("Lore", 8);
		} else if (otherLoreExists) {
			ListTag listTag = new ListTag();
			compoundTag2.put("Lore", listTag);
			return listTag;
		} else {
			return null;
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetLoreLootFunction> {
		public Factory() {
			super(new Identifier("set_lore"), SetLoreLootFunction.class);
		}

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
