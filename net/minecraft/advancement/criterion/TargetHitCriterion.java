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
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("target_hit");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("signal_strength"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("projectile"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("shooter"));
        return new Conditions(intRange, entityPredicate, entityPredicate2);
    }

    public void trigger(ServerPlayerEntity player, Entity entity, Vec3d vec3d, int i) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.method_24952(player, entity, vec3d, i));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange signalStrength;
        private final EntityPredicate projectile;
        private final EntityPredicate shooter;

        public Conditions(NumberRange.IntRange signalStrength, EntityPredicate projectile, EntityPredicate shooter) {
            super(ID);
            this.signalStrength = signalStrength;
            this.projectile = projectile;
            this.shooter = shooter;
        }

        public static Conditions create(NumberRange.IntRange signalStrength) {
            return new Conditions(signalStrength, EntityPredicate.ANY, EntityPredicate.ANY);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("signal_strength", this.signalStrength.toJson());
            jsonObject.add("projectile", this.projectile.toJson());
            jsonObject.add("shooter", this.shooter.toJson());
            return jsonObject;
        }

        public boolean method_24952(ServerPlayerEntity serverPlayerEntity, Entity entity, Vec3d vec3d, int i) {
            if (!this.signalStrength.test(i)) {
                return false;
            }
            if (!this.projectile.test(serverPlayerEntity, entity)) {
                return false;
            }
            return this.shooter.test(serverPlayerEntity.getServerWorld(), vec3d, serverPlayerEntity);
        }
    }
}

