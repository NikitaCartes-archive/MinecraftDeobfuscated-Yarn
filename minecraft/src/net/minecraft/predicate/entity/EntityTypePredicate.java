package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
	public static final EntityTypePredicate ANY = new EntityTypePredicate() {
		@Override
		public boolean matches(EntityType<?> entityType) {
			return true;
		}

		@Override
		public JsonElement toJson() {
			return JsonNull.INSTANCE;
		}
	};
	private static final Joiner COMMA_JOINER = Joiner.on(", ");

	public abstract boolean matches(EntityType<?> entityType);

	public abstract JsonElement toJson();

	public static EntityTypePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			String string = JsonHelper.asString(jsonElement, "type");
			if (string.startsWith("#")) {
				Identifier identifier = new Identifier(string.substring(1));
				Tag<EntityType<?>> tag = EntityTags.method_15082().getOrCreate(identifier);
				return new EntityTypePredicate.Tagged(tag);
			} else {
				Identifier identifier = new Identifier(string);
				EntityType<?> entityType = (EntityType<?>)Registry.ENTITY_TYPE
					.method_17966(identifier)
					.orElseThrow(
						() -> new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds()))
					);
				return new EntityTypePredicate.Single(entityType);
			}
		} else {
			return ANY;
		}
	}

	public static EntityTypePredicate create(EntityType<?> entityType) {
		return new EntityTypePredicate.Single(entityType);
	}

	public static EntityTypePredicate method_8926(Tag<EntityType<?>> tag) {
		return new EntityTypePredicate.Tagged(tag);
	}

	static class Single extends EntityTypePredicate {
		private final EntityType<?> type;

		public Single(EntityType<?> entityType) {
			this.type = entityType;
		}

		@Override
		public boolean matches(EntityType<?> entityType) {
			return this.type == entityType;
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive(Registry.ENTITY_TYPE.method_10221(this.type).toString());
		}
	}

	static class Tagged extends EntityTypePredicate {
		private final Tag<EntityType<?>> field_9610;

		public Tagged(Tag<EntityType<?>> tag) {
			this.field_9610 = tag;
		}

		@Override
		public boolean matches(EntityType<?> entityType) {
			return this.field_9610.contains(entityType);
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive("#" + this.field_9610.getId().toString());
		}
	}
}
