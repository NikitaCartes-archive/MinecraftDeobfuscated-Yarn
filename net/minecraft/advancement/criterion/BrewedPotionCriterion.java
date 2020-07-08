/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BrewedPotionCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("brewed_potion");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Potion potion = null;
        if (jsonObject.has("potion")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
            potion = Registry.POTION.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier + "'"));
        }
        return new Conditions(extended, potion);
    }

    public void trigger(ServerPlayerEntity player, Potion potion) {
        this.test(player, conditions -> conditions.matches(potion));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Potion potion;

        public Conditions(EntityPredicate.Extended player, @Nullable Potion potion) {
            super(ID, player);
            this.potion = potion;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, null);
        }

        public boolean matches(Potion potion) {
            return this.potion == null || this.potion == potion;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.potion != null) {
                jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
            }
            return jsonObject;
        }
    }
}

