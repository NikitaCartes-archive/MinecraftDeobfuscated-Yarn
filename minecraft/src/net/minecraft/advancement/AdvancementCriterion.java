package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class AdvancementCriterion {
	private final CriterionConditions field_1214;

	public AdvancementCriterion(CriterionConditions criterionConditions) {
		this.field_1214 = criterionConditions;
	}

	public AdvancementCriterion() {
		this.field_1214 = null;
	}

	public void serialize(PacketByteBuf packetByteBuf) {
	}

	public static AdvancementCriterion deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "trigger"));
		Criterion<?> criterion = Criterions.method_765(identifier);
		if (criterion == null) {
			throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
		} else {
			CriterionConditions criterionConditions = criterion.method_795(JsonHelper.getObject(jsonObject, "conditions", new JsonObject()), jsonDeserializationContext);
			return new AdvancementCriterion(criterionConditions);
		}
	}

	public static AdvancementCriterion createNew(PacketByteBuf packetByteBuf) {
		return new AdvancementCriterion();
	}

	public static Map<String, AdvancementCriterion> fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Map<String, AdvancementCriterion> map = Maps.<String, AdvancementCriterion>newHashMap();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			map.put(entry.getKey(), deserialize(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), jsonDeserializationContext));
		}

		return map;
	}

	public static Map<String, AdvancementCriterion> fromPacket(PacketByteBuf packetByteBuf) {
		Map<String, AdvancementCriterion> map = Maps.<String, AdvancementCriterion>newHashMap();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			map.put(packetByteBuf.readString(32767), createNew(packetByteBuf));
		}

		return map;
	}

	public static void serialize(Map<String, AdvancementCriterion> map, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(map.size());

		for (Entry<String, AdvancementCriterion> entry : map.entrySet()) {
			packetByteBuf.writeString((String)entry.getKey());
			((AdvancementCriterion)entry.getValue()).serialize(packetByteBuf);
		}
	}

	@Nullable
	public CriterionConditions method_774() {
		return this.field_1214;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("trigger", this.field_1214.getId().toString());
		jsonObject.add("conditions", this.field_1214.toJson());
		return jsonObject;
	}
}
