/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion
extends AbstractCriterion<Conditions> {
    public static final Identifier ID = new Identifier("tick");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9140(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new Conditions();
    }

    public void handle(ServerPlayerEntity serverPlayerEntity) {
        this.grant(serverPlayerEntity.getAdvancementManager());
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9140(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        public Conditions() {
            super(ID);
        }
    }
}

