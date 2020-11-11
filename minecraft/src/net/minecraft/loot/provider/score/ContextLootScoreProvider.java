package net.minecraft.loot.provider.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class ContextLootScoreProvider implements LootScoreProvider {
	private final LootContext.EntityTarget field_27930;

	private ContextLootScoreProvider(LootContext.EntityTarget entityTarget) {
		this.field_27930 = entityTarget;
	}

	@Override
	public LootScoreProviderType getType() {
		return LootScoreProviderTypes.CONTEXT;
	}

	@Nullable
	@Override
	public String getName(LootContext context) {
		Entity entity = context.get(this.field_27930.getParameter());
		return entity != null ? entity.getEntityName() : null;
	}

	@Override
	public Set<LootContextParameter<?>> method_32477() {
		return ImmutableSet.of(this.field_27930.getParameter());
	}

	public static class class_5665 implements JsonSerializing.CustomSerializer<ContextLootScoreProvider> {
		public JsonElement toJson(ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(contextLootScoreProvider.field_27930);
		}

		public ContextLootScoreProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
			LootContext.EntityTarget entityTarget = jsonDeserializationContext.deserialize(jsonElement, LootContext.EntityTarget.class);
			return new ContextLootScoreProvider(entityTarget);
		}
	}

	public static class class_5666 implements JsonSerializer<ContextLootScoreProvider> {
		public void toJson(JsonObject jsonObject, ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("target", contextLootScoreProvider.field_27930.name());
		}

		public ContextLootScoreProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "target", jsonDeserializationContext, LootContext.EntityTarget.class);
			return new ContextLootScoreProvider(entityTarget);
		}
	}
}
