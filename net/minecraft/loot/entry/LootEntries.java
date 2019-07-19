/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

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
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.SequenceEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootEntries {
    private static final Map<Identifier, LootEntry.Serializer<?>> idSerializers = Maps.newHashMap();
    private static final Map<Class<?>, LootEntry.Serializer<?>> classSerializers = Maps.newHashMap();

    private static void register(LootEntry.Serializer<?> serializer) {
        idSerializers.put(serializer.getIdentifier(), serializer);
        classSerializers.put(serializer.getType(), serializer);
    }

    static {
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("alternatives"), AlternativeEntry.class, AlternativeEntry::new));
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("sequence"), SequenceEntry.class, SequenceEntry::new));
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("group"), GroupEntry.class, GroupEntry::new));
        LootEntries.register(new EmptyEntry.Serializer());
        LootEntries.register(new ItemEntry.Serializer());
        LootEntries.register(new LootTableEntry.Serializer());
        LootEntries.register(new DynamicEntry.Serializer());
        LootEntries.register(new TagEntry.Serializer());
    }

    public static class Serializer
    implements JsonDeserializer<LootEntry>,
    JsonSerializer<LootEntry> {
        @Override
        public LootEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "type"));
            LootEntry.Serializer serializer = (LootEntry.Serializer)idSerializers.get(identifier);
            if (serializer == null) {
                throw new JsonParseException("Unknown item type: " + identifier);
            }
            LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            return serializer.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }

        @Override
        public JsonElement serialize(LootEntry lootEntry, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            LootEntry.Serializer<LootEntry> serializer = Serializer.getSerializer(lootEntry.getClass());
            jsonObject.addProperty("type", serializer.getIdentifier().toString());
            if (!ArrayUtils.isEmpty(lootEntry.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize(lootEntry.conditions));
            }
            serializer.toJson(jsonObject, lootEntry, jsonSerializationContext);
            return jsonObject;
        }

        private static LootEntry.Serializer<LootEntry> getSerializer(Class<?> class_) {
            LootEntry.Serializer serializer = (LootEntry.Serializer)classSerializers.get(class_);
            if (serializer == null) {
                throw new JsonParseException("Unknown item type: " + class_);
            }
            return serializer;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootEntry)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

