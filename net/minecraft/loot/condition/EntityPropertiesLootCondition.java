/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityPropertiesLootCondition
implements LootCondition {
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget entity;

    private EntityPropertiesLootCondition(EntityPredicate predicate, LootContext.EntityTarget entity) {
        this.predicate = predicate;
        this.entity = entity;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.POSITION, this.entity.getParameter());
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.get(this.entity.getParameter());
        BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
        return this.predicate.test(lootContext.getWorld(), blockPos != null ? Vec3d.method_24954(blockPos) : null, entity);
    }

    public static LootCondition.Builder create(LootContext.EntityTarget entity) {
        return EntityPropertiesLootCondition.builder(entity, EntityPredicate.Builder.create());
    }

    public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate.Builder predicateBuilder) {
        return () -> new EntityPropertiesLootCondition(predicateBuilder.build(), entity);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<EntityPropertiesLootCondition> {
        protected Factory() {
            super(new Identifier("entity_properties"), EntityPropertiesLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, EntityPropertiesLootCondition entityPropertiesLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", entityPropertiesLootCondition.predicate.toJson());
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)entityPropertiesLootCondition.entity));
        }

        @Override
        public EntityPropertiesLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("predicate"));
            return new EntityPropertiesLootCondition(entityPredicate, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

