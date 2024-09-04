package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public record ModelOverride(Identifier modelId, List<ModelOverride.Condition> conditions) {
	public ModelOverride(Identifier modelId, List<ModelOverride.Condition> conditions) {
		conditions = List.copyOf(conditions);
		this.modelId = modelId;
		this.conditions = conditions;
	}

	@Environment(EnvType.CLIENT)
	public static record Condition(Identifier type, float threshold) {
	}

	@Environment(EnvType.CLIENT)
	protected static class Deserializer implements JsonDeserializer<ModelOverride> {
		public ModelOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = Identifier.of(JsonHelper.getString(jsonObject, "model"));
			List<ModelOverride.Condition> list = this.deserializeMinPropertyValues(jsonObject);
			return new ModelOverride(identifier, list);
		}

		protected List<ModelOverride.Condition> deserializeMinPropertyValues(JsonObject object) {
			Map<Identifier, Float> map = Maps.<Identifier, Float>newLinkedHashMap();
			JsonObject jsonObject = JsonHelper.getObject(object, "predicate");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				map.put(Identifier.of((String)entry.getKey()), JsonHelper.asFloat((JsonElement)entry.getValue(), (String)entry.getKey()));
			}

			return (List<ModelOverride.Condition>)map.entrySet()
				.stream()
				.map(entryx -> new ModelOverride.Condition((Identifier)entryx.getKey(), (Float)entryx.getValue()))
				.collect(ImmutableList.toImmutableList());
		}
	}
}
