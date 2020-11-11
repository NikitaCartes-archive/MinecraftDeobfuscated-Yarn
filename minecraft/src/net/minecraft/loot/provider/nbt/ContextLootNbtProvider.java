package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class ContextLootNbtProvider implements LootNbtProvider {
	private static final ContextLootNbtProvider.class_5648 field_27915 = new ContextLootNbtProvider.class_5648() {
		@Override
		public Tag method_32435(LootContext lootContext) {
			BlockEntity blockEntity = lootContext.get(LootContextParameters.BLOCK_ENTITY);
			return blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
		}

		@Override
		public String method_32434() {
			return "block_entity";
		}

		@Override
		public Set<LootContextParameter<?>> method_32436() {
			return ImmutableSet.of(LootContextParameters.BLOCK_ENTITY);
		}
	};
	public static final ContextLootNbtProvider field_27914 = new ContextLootNbtProvider(field_27915);
	private final ContextLootNbtProvider.class_5648 field_27916;

	private static ContextLootNbtProvider.class_5648 method_32430(LootContext.EntityTarget entityTarget) {
		return new ContextLootNbtProvider.class_5648() {
			@Nullable
			@Override
			public Tag method_32435(LootContext lootContext) {
				Entity entity = lootContext.get(entityTarget.getParameter());
				return entity != null ? NbtPredicate.entityToTag(entity) : null;
			}

			@Override
			public String method_32434() {
				return entityTarget.name();
			}

			@Override
			public Set<LootContextParameter<?>> method_32436() {
				return ImmutableSet.of(entityTarget.getParameter());
			}
		};
	}

	private ContextLootNbtProvider(ContextLootNbtProvider.class_5648 arg) {
		this.field_27916 = arg;
	}

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.CONTEXT;
	}

	@Nullable
	@Override
	public Tag method_32440(LootContext lootContext) {
		return this.field_27916.method_32435(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> method_32441() {
		return this.field_27916.method_32436();
	}

	private static ContextLootNbtProvider method_32431(String string) {
		if (string.equals("block_entity")) {
			return new ContextLootNbtProvider(field_27915);
		} else {
			LootContext.EntityTarget entityTarget = LootContext.EntityTarget.fromString(string);
			return new ContextLootNbtProvider(method_32430(entityTarget));
		}
	}

	public static class CustomSerializer implements JsonSerializing.CustomSerializer<ContextLootNbtProvider> {
		public JsonElement toJson(ContextLootNbtProvider contextLootNbtProvider, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(contextLootNbtProvider.field_27916.method_32434());
		}

		public ContextLootNbtProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
			String string = jsonElement.getAsString();
			return ContextLootNbtProvider.method_32431(string);
		}
	}

	public static class Serializer implements JsonSerializer<ContextLootNbtProvider> {
		public void toJson(JsonObject jsonObject, ContextLootNbtProvider contextLootNbtProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("target", contextLootNbtProvider.field_27916.method_32434());
		}

		public ContextLootNbtProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = JsonHelper.getString(jsonObject, "target");
			return ContextLootNbtProvider.method_32431(string);
		}
	}

	interface class_5648 {
		@Nullable
		Tag method_32435(LootContext lootContext);

		String method_32434();

		Set<LootContextParameter<?>> method_32436();
	}
}
