package net.minecraft.world.loot.function;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;

public class SetLoreLootFunction extends ConditionalLootFunction {
	private final boolean replace;
	private final List<Text> lore;
	@Nullable
	private final LootContext.EntityTarget entity;

	public SetLoreLootFunction(LootCondition[] lootConditions, boolean bl, List<Text> list, @Nullable LootContext.EntityTarget entityTarget) {
		super(lootConditions);
		this.replace = bl;
		this.lore = ImmutableList.copyOf(list);
		this.entity = entityTarget;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		ListTag listTag = this.getLoreForMerge(itemStack, !this.lore.isEmpty());
		if (listTag != null) {
			if (this.replace) {
				listTag.clear();
			}

			UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(lootContext, this.entity);
			this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(StringTag::new).forEach(listTag::add);
		}

		return itemStack;
	}

	@Nullable
	private ListTag getLoreForMerge(ItemStack itemStack, boolean bl) {
		CompoundTag compoundTag;
		if (itemStack.hasTag()) {
			compoundTag = itemStack.getTag();
		} else {
			if (!bl) {
				return null;
			}

			compoundTag = new CompoundTag();
			itemStack.setTag(compoundTag);
		}

		CompoundTag compoundTag2;
		if (compoundTag.containsKey("display", 10)) {
			compoundTag2 = compoundTag.getCompound("display");
		} else {
			if (!bl) {
				return null;
			}

			compoundTag2 = new CompoundTag();
			compoundTag.put("display", compoundTag2);
		}

		if (compoundTag2.containsKey("Lore", 9)) {
			return compoundTag2.getList("Lore", 8);
		} else if (bl) {
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

		public void method_15969(JsonObject jsonObject, SetLoreLootFunction setLoreLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setLoreLootFunction, jsonSerializationContext);
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

		public SetLoreLootFunction method_15968(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
			List<Text> list = (List<Text>)Streams.stream(JsonHelper.getArray(jsonObject, "lore"))
				.map(Text.Serializer::fromJson)
				.collect(ImmutableList.toImmutableList());
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
			return new SetLoreLootFunction(lootConditions, bl, list, entityTarget);
		}
	}
}
