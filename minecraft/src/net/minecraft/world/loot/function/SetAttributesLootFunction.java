package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SetAttributesLootFunction extends ConditionalLootFunction {
	private final List<SetAttributesLootFunction.Attribute> field_1105;

	private SetAttributesLootFunction(LootCondition[] lootConditions, List<SetAttributesLootFunction.Attribute> list) {
		super(lootConditions);
		this.field_1105 = ImmutableList.copyOf(list);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Random random = lootContext.getRandom();

		for (SetAttributesLootFunction.Attribute attribute : this.field_1105) {
			UUID uUID = attribute.id;
			if (uUID == null) {
				uUID = UUID.randomUUID();
			}

			EquipmentSlot equipmentSlot = attribute.slots[random.nextInt(attribute.slots.length)];
			itemStack.addAttributeModifier(
				attribute.attribute,
				new EntityAttributeModifier(uUID, attribute.name, (double)attribute.amountRange.nextFloat(random), attribute.field_1109),
				equipmentSlot
			);
		}

		return itemStack;
	}

	static class Attribute {
		private final String name;
		private final String attribute;
		private final EntityAttributeModifier.Operation field_1109;
		private final UniformLootTableRange amountRange;
		@Nullable
		private final UUID id;
		private final EquipmentSlot[] slots;

		private Attribute(
			String string,
			String string2,
			EntityAttributeModifier.Operation operation,
			UniformLootTableRange uniformLootTableRange,
			EquipmentSlot[] equipmentSlots,
			@Nullable UUID uUID
		) {
			this.name = string;
			this.attribute = string2;
			this.field_1109 = operation;
			this.amountRange = uniformLootTableRange;
			this.id = uUID;
			this.slots = equipmentSlots;
		}

		public JsonObject serialize(JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", this.name);
			jsonObject.addProperty("attribute", this.attribute);
			jsonObject.addProperty("operation", method_612(this.field_1109));
			jsonObject.add("amount", jsonSerializationContext.serialize(this.amountRange));
			if (this.id != null) {
				jsonObject.addProperty("id", this.id.toString());
			}

			if (this.slots.length == 1) {
				jsonObject.addProperty("slot", this.slots[0].getName());
			} else {
				JsonArray jsonArray = new JsonArray();

				for (EquipmentSlot equipmentSlot : this.slots) {
					jsonArray.add(new JsonPrimitive(equipmentSlot.getName()));
				}

				jsonObject.add("slot", jsonArray);
			}

			return jsonObject;
		}

		public static SetAttributesLootFunction.Attribute deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = JsonHelper.getString(jsonObject, "name");
			String string2 = JsonHelper.getString(jsonObject, "attribute");
			EntityAttributeModifier.Operation operation = method_609(JsonHelper.getString(jsonObject, "operation"));
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(jsonObject, "amount", jsonDeserializationContext, UniformLootTableRange.class);
			UUID uUID = null;
			EquipmentSlot[] equipmentSlots;
			if (JsonHelper.hasString(jsonObject, "slot")) {
				equipmentSlots = new EquipmentSlot[]{EquipmentSlot.byName(JsonHelper.getString(jsonObject, "slot"))};
			} else {
				if (!JsonHelper.hasArray(jsonObject, "slot")) {
					throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
				}

				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "slot");
				equipmentSlots = new EquipmentSlot[jsonArray.size()];
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					equipmentSlots[i++] = EquipmentSlot.byName(JsonHelper.asString(jsonElement, "slot"));
				}

				if (equipmentSlots.length == 0) {
					throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
				}
			}

			if (jsonObject.has("id")) {
				String string3 = JsonHelper.getString(jsonObject, "id");

				try {
					uUID = UUID.fromString(string3);
				} catch (IllegalArgumentException var12) {
					throw new JsonSyntaxException("Invalid attribute modifier id '" + string3 + "' (must be UUID format, with dashes)");
				}
			}

			return new SetAttributesLootFunction.Attribute(string, string2, operation, uniformLootTableRange, equipmentSlots, uUID);
		}

		private static String method_612(EntityAttributeModifier.Operation operation) {
			switch (operation) {
				case field_6328:
					return "addition";
				case field_6330:
					return "multiply_base";
				case field_6331:
					return "multiply_total";
				default:
					throw new IllegalArgumentException("Unknown operation " + operation);
			}
		}

		private static EntityAttributeModifier.Operation method_609(String string) {
			switch (string) {
				case "addition":
					return EntityAttributeModifier.Operation.field_6328;
				case "multiply_base":
					return EntityAttributeModifier.Operation.field_6330;
				case "multiply_total":
					return EntityAttributeModifier.Operation.field_6331;
				default:
					throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
			}
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetAttributesLootFunction> {
		public Factory() {
			super(new Identifier("set_attributes"), SetAttributesLootFunction.class);
		}

		public void method_618(JsonObject jsonObject, SetAttributesLootFunction setAttributesLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setAttributesLootFunction, jsonSerializationContext);
			JsonArray jsonArray = new JsonArray();

			for (SetAttributesLootFunction.Attribute attribute : setAttributesLootFunction.field_1105) {
				jsonArray.add(attribute.serialize(jsonSerializationContext));
			}

			jsonObject.add("modifiers", jsonArray);
		}

		public SetAttributesLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "modifiers");
			List<SetAttributesLootFunction.Attribute> list = Lists.<SetAttributesLootFunction.Attribute>newArrayListWithExpectedSize(jsonArray.size());

			for (JsonElement jsonElement : jsonArray) {
				list.add(SetAttributesLootFunction.Attribute.deserialize(JsonHelper.asObject(jsonElement, "modifier"), jsonDeserializationContext));
			}

			if (list.isEmpty()) {
				throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
			} else {
				return new SetAttributesLootFunction(lootConditions, list);
			}
		}
	}
}
