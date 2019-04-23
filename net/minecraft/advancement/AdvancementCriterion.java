/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class AdvancementCriterion {
    private final CriterionConditions conditions;

    public AdvancementCriterion(CriterionConditions criterionConditions) {
        this.conditions = criterionConditions;
    }

    public AdvancementCriterion() {
        this.conditions = null;
    }

    public void serialize(PacketByteBuf packetByteBuf) {
    }

    public static AdvancementCriterion deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "trigger"));
        Criterion criterion = Criterions.getById(identifier);
        if (criterion == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
        }
        Object criterionConditions = criterion.conditionsFromJson(JsonHelper.getObject(jsonObject, "conditions", new JsonObject()), jsonDeserializationContext);
        return new AdvancementCriterion((CriterionConditions)criterionConditions);
    }

    public static AdvancementCriterion createNew(PacketByteBuf packetByteBuf) {
        return new AdvancementCriterion();
    }

    public static Map<String, AdvancementCriterion> fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        HashMap<String, AdvancementCriterion> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), AdvancementCriterion.deserialize(JsonHelper.asObject(entry.getValue(), "criterion"), jsonDeserializationContext));
        }
        return map;
    }

    public static Map<String, AdvancementCriterion> fromPacket(PacketByteBuf packetByteBuf) {
        HashMap<String, AdvancementCriterion> map = Maps.newHashMap();
        int i = packetByteBuf.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(packetByteBuf.readString(Short.MAX_VALUE), AdvancementCriterion.createNew(packetByteBuf));
        }
        return map;
    }

    public static void serialize(Map<String, AdvancementCriterion> map, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeVarInt(map.size());
        for (Map.Entry<String, AdvancementCriterion> entry : map.entrySet()) {
            packetByteBuf.writeString(entry.getKey());
            entry.getValue().serialize(packetByteBuf);
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

