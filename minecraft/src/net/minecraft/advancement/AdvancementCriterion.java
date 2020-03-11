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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AdvancementCriterion {
	private final CriterionConditions conditions;

	public AdvancementCriterion(CriterionConditions conditions) {
		this.conditions = conditions;
	}

	public AdvancementCriterion() {
		this.conditions = null;
	}

	public void serialize(PacketByteBuf packetByteBuf) {
	}

	public static AdvancementCriterion deserialize(JsonObject obj, JsonDeserializationContext context) {
		Identifier identifier = new Identifier(JsonHelper.getString(obj, "trigger"));
		Criterion<?> criterion = Criterions.getById(identifier);
		if (criterion == null) {
			throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
		} else {
			CriterionConditions criterionConditions = criterion.conditionsFromJson(JsonHelper.getObject(obj, "conditions", new JsonObject()), context);
			return new AdvancementCriterion(criterionConditions);
		}
	}

	public static AdvancementCriterion createNew(PacketByteBuf buf) {
		return new AdvancementCriterion();
	}

	public static Map<String, AdvancementCriterion> fromJson(JsonObject obj, JsonDeserializationContext context) {
		Map<String, AdvancementCriterion> map = Maps.<String, AdvancementCriterion>newHashMap();

		for (Entry<String, JsonElement> entry : obj.entrySet()) {
			map.put(entry.getKey(), deserialize(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), context));
		}

		return map;
	}

	public static Map<String, AdvancementCriterion> fromPacket(PacketByteBuf buf) {
		Map<String, AdvancementCriterion> map = Maps.<String, AdvancementCriterion>newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			map.put(buf.readString(32767), createNew(buf));
		}

		return map;
	}

	public static void serialize(Map<String, AdvancementCriterion> criteria, PacketByteBuf buf) {
		buf.writeVarInt(criteria.size());

		for (Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
			buf.writeString((String)entry.getKey());
			((AdvancementCriterion)entry.getValue()).serialize(buf);
		}
	}

	@Nullable
	public CriterionConditions getConditions() {
		return this.conditions;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("trigger", this.conditions.getId().toString());
		jsonObject.add("conditions", this.conditions.toJson());
		return jsonObject;
	}
}
