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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
        DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
        return new Conditions(dimensionType, dimensionType2);
    }

    public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(from, to));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final DimensionType from;
        @Nullable
        private final DimensionType to;

        public Conditions(@Nullable DimensionType from, @Nullable DimensionType to) {
            super(ID);
            this.from = from;
            this.to = to;
        }

        public static Conditions to(DimensionType to) {
            return new Conditions(null, to);
        }

        public boolean matches(DimensionType from, DimensionType to) {
            if (this.from != null && this.from != from) {
                return false;
            }
            return this.to == null || this.to == to;
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

