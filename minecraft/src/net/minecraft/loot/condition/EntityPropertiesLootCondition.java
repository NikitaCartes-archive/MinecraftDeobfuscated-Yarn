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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityPropertiesLootCondition implements LootCondition {
	private final EntityPredicate predicate;
	private final LootContext.EntityTarget entity;

	private EntityPropertiesLootCondition(EntityPredicate entityPredicate, LootContext.EntityTarget entityTarget) {
		this.predicate = entityPredicate;
		this.entity = entityTarget;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.POSITION, this.entity.getParameter());
	}

	public boolean method_914(LootContext lootContext) {
		Entity entity = lootContext.get(this.entity.getParameter());
		BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
		return this.predicate.test(lootContext.getWorld(), blockPos != null ? new Vec3d(blockPos) : null, entity);
	}

	public static LootCondition.Builder create(LootContext.EntityTarget entityTarget) {
		return builder(entityTarget, EntityPredicate.Builder.create());
	}

	public static LootCondition.Builder builder(LootContext.EntityTarget entityTarget, EntityPredicate.Builder builder) {
		return () -> new EntityPropertiesLootCondition(builder.build(), entityTarget);
	}

	public static class Factory extends LootCondition.Factory<EntityPropertiesLootCondition> {
		protected Factory() {
			super(new Identifier("entity_properties"), EntityPropertiesLootCondition.class);
		}

		public void method_919(JsonObject jsonObject, EntityPropertiesLootCondition entityPropertiesLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", entityPropertiesLootCondition.predicate.serialize());
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
