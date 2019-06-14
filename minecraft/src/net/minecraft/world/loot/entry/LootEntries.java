package net.minecraft.world.loot.entry;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootEntries {
	private static final Map<Identifier, LootEntry.Serializer<?>> idSerializers = Maps.<Identifier, LootEntry.Serializer<?>>newHashMap();
	private static final Map<Class<?>, LootEntry.Serializer<?>> classSerializers = Maps.<Class<?>, LootEntry.Serializer<?>>newHashMap();

	private static void method_403(LootEntry.Serializer<?> serializer) {
		idSerializers.put(serializer.getIdentifier(), serializer);
		classSerializers.put(serializer.getType(), serializer);
	}

	static {
		method_403(CombinedEntry.createSerializer(new Identifier("alternatives"), AlternativeEntry.class, AlternativeEntry::new));
		method_403(CombinedEntry.createSerializer(new Identifier("sequence"), SequenceEntry.class, SequenceEntry::new));
		method_403(CombinedEntry.createSerializer(new Identifier("group"), GroupEntry.class, GroupEntry::new));
		method_403(new EmptyEntry.Serializer());
		method_403(new ItemEntry.Serializer());
		method_403(new LootTableEntry.Serializer());
		method_403(new DynamicEntry.Serializer());
		method_403(new TagEntry.Serializer());
	}

	public static class Serializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry> {
		public LootEntry method_407(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "type"));
			LootEntry.Serializer<?> serializer = (LootEntry.Serializer<?>)LootEntries.idSerializers.get(identifier);
			if (serializer == null) {
				throw new JsonParseException("Unknown item type: " + identifier);
			} else {
				LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
				return serializer.method_424(jsonObject, jsonDeserializationContext, lootConditions);
			}
		}

		public JsonElement method_408(LootEntry lootEntry, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			LootEntry.Serializer<LootEntry> serializer = method_406(lootEntry.getClass());
			jsonObject.addProperty("type", serializer.getIdentifier().toString());
			if (!ArrayUtils.isEmpty((Object[])lootEntry.field_988)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(lootEntry.field_988));
			}

			serializer.toJson(jsonObject, lootEntry, jsonSerializationContext);
			return jsonObject;
		}

		private static LootEntry.Serializer<LootEntry> method_406(Class<?> class_) {
			LootEntry.Serializer<?> serializer = (LootEntry.Serializer<?>)LootEntries.classSerializers.get(class_);
			if (serializer == null) {
				throw new JsonParseException("Unknown item type: " + class_);
			} else {
				return (LootEntry.Serializer<LootEntry>)serializer;
			}
		}
	}
}
