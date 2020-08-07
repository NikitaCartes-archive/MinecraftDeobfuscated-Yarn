package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.Vec3d;

public class EntityPropertiesLootCondition implements LootCondition {
	private final EntityPredicate predicate;
	private final LootContext.EntityTarget entity;

	private EntityPropertiesLootCondition(EntityPredicate predicate, LootContext.EntityTarget entity) {
		this.predicate = predicate;
		this.entity = entity;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.field_25239;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_24424, this.entity.getParameter());
	}

	public boolean method_914(LootContext lootContext) {
		Entity entity = lootContext.get(this.entity.getParameter());
		Vec3d vec3d = lootContext.get(LootContextParameters.field_24424);
		return this.predicate.test(lootContext.getWorld(), vec3d, entity);
	}

	public static LootCondition.Builder create(LootContext.EntityTarget entity) {
		return builder(entity, EntityPredicate.Builder.create());
	}

	public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate.Builder predicateBuilder) {
		return () -> new EntityPropertiesLootCondition(predicateBuilder.build(), entity);
	}

	public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate predicate) {
		return () -> new EntityPropertiesLootCondition(predicate, entity);
	}

	public static class Serializer implements JsonSerializer<EntityPropertiesLootCondition> {
		public void method_919(JsonObject jsonObject, EntityPropertiesLootCondition entityPropertiesLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", entityPropertiesLootCondition.predicate.toJson());
			jsonObject.add("entity", jsonSerializationContext.serialize(entityPropertiesLootCondition.entity));
		}

		public EntityPropertiesLootCondition method_920(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("predicate"));
			return new EntityPropertiesLootCondition(
				entityPredicate, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class)
			);
		}
	}
}
