/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KilledByCrossbowCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("killed_by_crossbow");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate[] entityPredicates = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
        return new Conditions(entityPredicates, intRange);
    }

    public void trigger(ServerPlayerEntity player, Collection<Entity> victims, int amount) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, victims, amount));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate[] victims;
        private final NumberRange.IntRange uniqueEntityTypes;

        public Conditions(EntityPredicate[] victims, NumberRange.IntRange uniqueEntityTypes) {
            super(ID);
            this.victims = victims;
            this.uniqueEntityTypes = uniqueEntityTypes;
        }

        public static Conditions create(EntityPredicate.Builder ... builders) {
            EntityPredicate[] entityPredicates = new EntityPredicate[builders.length];
            for (int i = 0; i < builders.length; ++i) {
                EntityPredicate.Builder builder = builders[i];
                entityPredicates[i] = builder.build();
            }
            return new Conditions(entityPredicates, NumberRange.IntRange.ANY);
        }

        public static Conditions create(NumberRange.IntRange uniqueEntityTypes) {
            EntityPredicate[] entityPredicates = new EntityPredicate[]{};
            return new Conditions(entityPredicates, uniqueEntityTypes);
        }

        public boolean matches(ServerPlayerEntity player, Collection<Entity> victims, int amount) {
            if (this.victims.length > 0) {
                ArrayList<Entity> list = Lists.newArrayList(victims);
                for (EntityPredicate entityPredicate : this.victims) {
                    boolean bl = false;
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Entity entity = (Entity)iterator.next();
                        if (!entityPredicate.test(player, entity)) continue;
                        iterator.remove();
                        bl = true;
                        break;
                    }
                    if (bl) continue;
                    return false;
                }
            }
            if (this.uniqueEntityTypes != NumberRange.IntRange.ANY) {
                HashSet<EntityType<?>> set = Sets.newHashSet();
                for (Entity entity2 : victims) {
                    set.add(entity2.getType());
                }
                return this.uniqueEntityTypes.test(set.size()) && this.uniqueEntityTypes.test(amount);
            }
            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
            jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
            return jsonObject;
        }
    }
}

