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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("entity_hurt_player");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8902(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
        return new Conditions(damagePredicate);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, damageSource, f, g, bl));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8902(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DamagePredicate damage;

        public Conditions(DamagePredicate damagePredicate) {
            super(ID);
            this.damage = damagePredicate;
        }

        public static Conditions create(DamagePredicate.Builder builder) {
            return new Conditions(builder.build());
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
            return this.damage.test(serverPlayerEntity, damageSource, f, g, bl);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("damage", this.damage.serialize());
            return jsonObject;
        }
    }
}

