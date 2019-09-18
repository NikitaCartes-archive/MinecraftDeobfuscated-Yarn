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
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("recipe_unlocked");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9106(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
        return new Conditions(identifier);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(recipe));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9106(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Identifier recipe;

        public Conditions(Identifier identifier) {
            super(ID);
            this.recipe = identifier;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("recipe", this.recipe.toString());
            return jsonObject;
        }

        public boolean matches(Recipe<?> recipe) {
            return this.recipe.equals(recipe.getId());
        }
    }
}

