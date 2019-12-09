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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
        return new Conditions(damagePredicate);
    }

    public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, source, dealt, taken, blocked));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DamagePredicate damage;

        public Conditions(DamagePredicate damage) {
            super(ID);
            this.damage = damage;
        }

        public static Conditions create(DamagePredicate.Builder builder) {
            return new Conditions(builder.build());
        }

        public boolean matches(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
            return this.damage.test(player, source, dealt, taken, blocked);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("damage", this.damage.serialize());
            return jsonObject;
        }
    }
}

