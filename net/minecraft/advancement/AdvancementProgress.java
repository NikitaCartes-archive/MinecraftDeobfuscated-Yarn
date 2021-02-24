/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class AdvancementProgress
implements Comparable<AdvancementProgress> {
    private final Map<String, CriterionProgress> criteriaProgresses;
    private String[][] requirements = new String[0][];

    private AdvancementProgress(Map<String, CriterionProgress> map) {
        this.criteriaProgresses = map;
    }

    public AdvancementProgress() {
        this.criteriaProgresses = Maps.newHashMap();
    }

    public void init(Map<String, AdvancementCriterion> criteria, String[][] requirements) {
        Set<String> set = criteria.keySet();
        this.criteriaProgresses.entrySet().removeIf(entry -> !set.contains(entry.getKey()));
        for (String string : set) {
            if (this.criteriaProgresses.containsKey(string)) continue;
            this.criteriaProgresses.put(string, new CriterionProgress());
        }
        this.requirements = requirements;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        }
        for (String[] strings : this.requirements) {
            boolean bl = false;
            for (String string : strings) {
                CriterionProgress criterionProgress = this.getCriterionProgress(string);
                if (criterionProgress == null || !criterionProgress.isObtained()) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    public boolean isAnyObtained() {
        for (CriterionProgress criterionProgress : this.criteriaProgresses.values()) {
            if (!criterionProgress.isObtained()) continue;
            return true;
        }
        return false;
    }

    public boolean obtain(String name) {
        CriterionProgress criterionProgress = this.criteriaProgresses.get(name);
        if (criterionProgress != null && !criterionProgress.isObtained()) {
            criterionProgress.obtain();
            return true;
        }
        return false;
    }

    public boolean reset(String name) {
        CriterionProgress criterionProgress = this.criteriaProgresses.get(name);
        if (criterionProgress != null && criterionProgress.isObtained()) {
            criterionProgress.reset();
            return true;
        }
        return false;
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteriaProgresses + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + '}';
    }

    public void toPacket(PacketByteBuf buf) {
        buf.method_34063(this.criteriaProgresses, PacketByteBuf::writeString, (packetByteBuf, criterionProgress) -> criterionProgress.toPacket((PacketByteBuf)packetByteBuf));
    }

    public static AdvancementProgress fromPacket(PacketByteBuf buf) {
        Map<String, CriterionProgress> map = buf.method_34067(PacketByteBuf::readString, CriterionProgress::fromPacket);
        return new AdvancementProgress(map);
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String name) {
        return this.criteriaProgresses.get(name);
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgressBarPercentage() {
        if (this.criteriaProgresses.isEmpty()) {
            return 0.0f;
        }
        float f = this.requirements.length;
        float g = this.countObtainedRequirements();
        return g / f;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public String getProgressBarFraction() {
        if (this.criteriaProgresses.isEmpty()) {
            return null;
        }
        int i = this.requirements.length;
        if (i <= 1) {
            return null;
        }
        int j = this.countObtainedRequirements();
        return j + "/" + i;
    }

    @Environment(value=EnvType.CLIENT)
    private int countObtainedRequirements() {
        int i = 0;
        for (String[] strings : this.requirements) {
            boolean bl = false;
            for (String string : strings) {
                CriterionProgress criterionProgress = this.getCriterionProgress(string);
                if (criterionProgress == null || !criterionProgress.isObtained()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            ++i;
        }
        return i;
    }

    public Iterable<String> getUnobtainedCriteria() {
        ArrayList<String> list = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
            if (entry.getValue().isObtained()) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    public Iterable<String> getObtainedCriteria() {
        ArrayList<String> list = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
            if (!entry.getValue().isObtained()) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    @Nullable
    public Date getEarliestProgressObtainDate() {
        Date date = null;
        for (CriterionProgress criterionProgress : this.criteriaProgresses.values()) {
            if (!criterionProgress.isObtained() || date != null && !criterionProgress.getObtainedDate().before(date)) continue;
            date = criterionProgress.getObtainedDate();
        }
        return date;
    }

    @Override
    public int compareTo(AdvancementProgress advancementProgress) {
        Date date = this.getEarliestProgressObtainDate();
        Date date2 = advancementProgress.getEarliestProgressObtainDate();
        if (date == null && date2 != null) {
            return 1;
        }
        if (date != null && date2 == null) {
            return -1;
        }
        if (date == null && date2 == null) {
            return 0;
        }
        return date.compareTo(date2);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((AdvancementProgress)object);
    }

    public static class Serializer
    implements JsonDeserializer<AdvancementProgress>,
    JsonSerializer<AdvancementProgress> {
        @Override
        public JsonElement serialize(AdvancementProgress advancementProgress, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry entry : advancementProgress.criteriaProgresses.entrySet()) {
                CriterionProgress criterionProgress = (CriterionProgress)entry.getValue();
                if (!criterionProgress.isObtained()) continue;
                jsonObject2.add((String)entry.getKey(), criterionProgress.toJson());
            }
            if (!jsonObject2.entrySet().isEmpty()) {
                jsonObject.add("criteria", jsonObject2);
            }
            jsonObject.addProperty("done", advancementProgress.isDone());
            return jsonObject;
        }

        @Override
        public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "criteria", new JsonObject());
            AdvancementProgress advancementProgress = new AdvancementProgress();
            for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                String string = entry.getKey();
                advancementProgress.criteriaProgresses.put(string, CriterionProgress.obtainedAt(JsonHelper.asString(entry.getValue(), string)));
            }
            return advancementProgress;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((AdvancementProgress)entry, unused, context);
        }
    }
}

