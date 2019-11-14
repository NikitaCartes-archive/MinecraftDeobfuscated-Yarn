/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class ChangedDimensionCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("changed_dimension");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8793(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
        DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
        return new Conditions(dimensionType, dimensionType2);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(dimensionType, dimensionType2));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8793(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final DimensionType from;
        @Nullable
        private final DimensionType to;

        public Conditions(@Nullable DimensionType dimensionType, @Nullable DimensionType dimensionType2) {
            super(ID);
            this.from = dimensionType;
            this.to = dimensionType2;
        }

        public static Conditions to(DimensionType dimensionType) {
            return new Conditions(null, dimensionType);
        }

        public boolean matches(DimensionType dimensionType, DimensionType dimensionType2) {
            if (this.from != null && this.from != dimensionType) {
                return false;
            }
            return this.to == null || this.to == dimensionType2;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.from != null) {
                jsonObject.addProperty("from", DimensionType.getId(this.from).toString());
            }
            if (this.to != null) {
                jsonObject.addProperty("to", DimensionType.getId(this.to).toString());
            }
            return jsonObject;
        }
    }
}

