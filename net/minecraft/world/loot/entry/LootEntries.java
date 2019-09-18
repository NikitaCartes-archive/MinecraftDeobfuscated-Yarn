/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.entry.AlternativeEntry;
import net.minecraft.world.loot.entry.CombinedEntry;
import net.minecraft.world.loot.entry.DynamicEntry;
import net.minecraft.world.loot.entry.EmptyEntry;
import net.minecraft.world.loot.entry.GroupEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.entry.LootTableEntry;
import net.minecraft.world.loot.entry.SequenceEntry;
import net.minecraft.world.loot.entry.TagEntry;
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
        public LootEntry method_407(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "type"));
            LootEntry.Serializer serializer = (LootEntry.Serializer)idSerializers.get(identifier);
            if (serializer == null) {
                throw new JsonParseException("Unknown item type: " + identifier);
            }
            class_4570[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new class_4570[0], jsonDeserializationContext, class_4570[].class);
            return serializer.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public JsonElement method_408(LootEntry lootEntry, Type type, JsonSerializationContext jsonSerializationContext) {
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
            return this.method_408((LootEntry)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_407(jsonElement, type, jsonDeserializationContext);
        }
    }
}

