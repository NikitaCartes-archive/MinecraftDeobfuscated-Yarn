package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ModelItemOverride {
	private final Identifier modelId;
	private final Map<Identifier, Float> minPropertyValues;

	public ModelItemOverride(Identifier modelId, Map<Identifier, Float> minPropertyValues) {
		this.modelId = modelId;
		this.minPropertyValues = minPropertyValues;
	}

	public Identifier getModelId() {
		return this.modelId;
	}

	boolean matches(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
		Item item = stack.getItem();

		for (Entry<Identifier, Float> entry : this.minPropertyValues.entrySet()) {
			ItemPropertyGetter itemPropertyGetter = item.getPropertyGetter((Identifier)entry.getKey());
			if (itemPropertyGetter == null || itemPropertyGetter.call(stack, world, entity) < (Float)entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelItemOverride> {
		protected Deserializer() {
		}

		public ModelItemOverride method_3475(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
			Map<Identifier, Float> map = this.deserializeMinPropertyValues(jsonObject);
			return new ModelItemOverride(identifier, map);
		}

		protected Map<Identifier, Float> deserializeMinPropertyValues(JsonObject object) {
			Map<Identifier, Float> map = Maps.<Identifier, Float>newLinkedHashMap();
			JsonObject jsonObject = JsonHelper.getObject(object, "predicate");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				map.put(new Identifier((String)entry.getKey()), JsonHelper.asFloat((JsonElement)entry.getValue(), (String)entry.getKey()));
			}

			return map;
		}
	}
}
