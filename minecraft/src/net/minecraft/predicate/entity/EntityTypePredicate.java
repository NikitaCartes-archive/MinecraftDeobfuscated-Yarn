package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public abstract class EntityTypePredicate {
	public static final EntityTypePredicate ANY = new EntityTypePredicate() {
		@Override
		public boolean matches(EntityType<?> type) {
			return true;
		}

		@Override
		public JsonElement toJson() {
			return JsonNull.INSTANCE;
		}
	};
	private static final Joiner COMMA_JOINER = Joiner.on(", ");

	public abstract boolean matches(EntityType<?> type);

	public abstract JsonElement toJson();

	public static EntityTypePredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			String string = JsonHelper.asString(json, "type");
			if (string.startsWith("#")) {
				Identifier identifier = new Identifier(string.substring(1));
				return new EntityTypePredicate.Tagged(TagKey.of(RegistryKeys.ENTITY_TYPE, identifier));
			} else {
				Identifier identifier = new Identifier(string);
				EntityType<?> entityType = (EntityType<?>)Registries.ENTITY_TYPE
					.getOrEmpty(identifier)
					.orElseThrow(
						() -> new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + COMMA_JOINER.join(Registries.ENTITY_TYPE.getIds()))
					);
				return new EntityTypePredicate.Single(entityType);
			}
		} else {
			return ANY;
		}
	}

	public static EntityTypePredicate create(EntityType<?> type) {
		return new EntityTypePredicate.Single(type);
	}

	public static EntityTypePredicate create(TagKey<EntityType<?>> tag) {
		return new EntityTypePredicate.Tagged(tag);
	}

	static class Single extends EntityTypePredicate {
		private final EntityType<?> type;

		public Single(EntityType<?> type) {
			this.type = type;
		}

		@Override
		public boolean matches(EntityType<?> type) {
			return this.type == type;
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive(Registries.ENTITY_TYPE.getId(this.type).toString());
		}
	}

	static class Tagged extends EntityTypePredicate {
		private final TagKey<EntityType<?>> tag;

		public Tagged(TagKey<EntityType<?>> tag) {
			this.tag = tag;
		}

		@Override
		public boolean matches(EntityType<?> type) {
			return type.isIn(this.tag);
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive("#" + this.tag.id());
		}
	}
}
