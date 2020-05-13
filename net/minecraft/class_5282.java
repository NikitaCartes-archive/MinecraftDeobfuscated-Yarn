/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_5282
extends AbstractCriterion<class_5283> {
    private static final Identifier field_24502 = new Identifier("player_generates_container_loot");

    @Override
    public Identifier getId() {
        return field_24502;
    }

    @Override
    protected class_5283 conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "loot_table"));
        return new class_5283(extended, identifier);
    }

    public void method_27993(ServerPlayerEntity serverPlayerEntity, Identifier identifier) {
        this.test(serverPlayerEntity, arg -> arg.method_27996(identifier));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class class_5283
    extends AbstractCriterionConditions {
        private final Identifier field_24503;

        public class_5283(EntityPredicate.Extended extended, Identifier identifier) {
            super(field_24502, extended);
            this.field_24503 = identifier;
        }

        public static class_5283 method_27995(Identifier identifier) {
            return new class_5283(EntityPredicate.Extended.EMPTY, identifier);
        }

        public boolean method_27996(Identifier identifier) {
            return this.field_24503.equals(identifier);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("loot_table", this.field_24503.toString());
            return jsonObject;
        }
    }
}

