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
	final LootContext.EntityTarget target;

	ContextLootScoreProvider(LootContext.EntityTarget entityTarget) {
		this.target = entityTarget;
	}

	public static LootScoreProvider create(LootContext.EntityTarget target) {
		return new ContextLootScoreProvider(target);
	}

	@Override
	public LootScoreProviderType getType() {
		return LootScoreProviderTypes.CONTEXT;
	}

	@Nullable
	@Override
	public String getName(LootContext context) {
		Entity entity = context.get(this.target.getParameter());
		return entity != null ? entity.getEntityName() : null;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.target.getParameter());
	}

	public static class CustomSerializer implements JsonSerializing.ElementSerializer<ContextLootScoreProvider> {
		public JsonElement toJson(ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(contextLootScoreProvider.target);
		}

		public ContextLootScoreProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
			LootContext.EntityTarget entityTarget = jsonDeserializationContext.deserialize(jsonElement, LootContext.EntityTarget.class);
			return new ContextLootScoreProvider(entityTarget);
		}
	}

	public static class Serializer implements JsonSerializer<ContextLootScoreProvider> {
		public void toJson(JsonObject jsonObject, ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("target", contextLootScoreProvider.target.name());
		}

		public ContextLootScoreProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "target", jsonDeserializationContext, LootContext.EntityTarget.class);
			return new ContextLootScoreProvider(entityTarget);
		}
	}
}
