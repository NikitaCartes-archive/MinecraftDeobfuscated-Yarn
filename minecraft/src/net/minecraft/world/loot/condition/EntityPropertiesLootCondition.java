package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class EntityPropertiesLootCondition implements LootCondition {
	private final EntityPredicate predicate;
	private final LootContext.EntityTarget entity;

	private EntityPropertiesLootCondition(EntityPredicate entityPredicate, LootContext.EntityTarget entityTarget) {
		this.predicate = entityPredicate;
		this.entity = entityTarget;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1232, this.entity.method_315());
	}

	public boolean method_914(LootContext lootContext) {
		Entity entity = lootContext.method_296(this.entity.method_315());
		BlockPos blockPos = lootContext.method_296(LootContextParameters.field_1232);
		return blockPos != null && this.predicate.method_8909(lootContext.method_299(), new Vec3d(blockPos), entity);
	}

	public static LootCondition.Builder method_15972(LootContext.EntityTarget entityTarget) {
		return method_917(entityTarget, EntityPredicate.Builder.create());
	}

	public static LootCondition.Builder method_917(LootContext.EntityTarget entityTarget, EntityPredicate.Builder builder) {
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
			EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("predicate"));
			return new EntityPropertiesLootCondition(
				entityPredicate, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class)
			);
		}
	}
}
