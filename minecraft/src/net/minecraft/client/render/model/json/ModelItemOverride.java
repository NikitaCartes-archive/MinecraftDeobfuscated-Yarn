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

	public ModelItemOverride(Identifier identifier, Map<Identifier, Float> map) {
		this.modelId = identifier;
		this.minPropertyValues = map;
	}

	public Identifier getModelId() {
		return this.modelId;
	}

	boolean matches(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		Item item = itemStack.getItem();

		for (Entry<Identifier, Float> entry : this.minPropertyValues.entrySet()) {
			ItemPropertyGetter itemPropertyGetter = item.getProperty((Identifier)entry.getKey());
			if (itemPropertyGetter == null || itemPropertyGetter.call(itemStack, world, livingEntity) < (Float)entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	static class class_800 implements JsonDeserializer<ModelItemOverride> {
		public ModelItemOverride method_3475(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
			Map<Identifier, Float> map = this.method_3474(jsonObject);
			return new ModelItemOverride(identifier, map);
		}

		protected Map<Identifier, Float> method_3474(JsonObject jsonObject) {
			Map<Identifier, Float> map = Maps.<Identifier, Float>newLinkedHashMap();
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "predicate");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				map.put(new Identifier((String)entry.getKey()), JsonHelper.asFloat((JsonElement)entry.getValue(), (String)entry.getKey()));
			}

			return map;
		}
	}
}
