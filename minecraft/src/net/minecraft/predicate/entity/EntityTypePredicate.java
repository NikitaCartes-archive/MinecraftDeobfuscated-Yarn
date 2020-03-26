package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

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

	public static EntityTypePredicate deserialize(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			String string = JsonHelper.asString(element, "type");
			if (string.startsWith("#")) {
				Identifier identifier = new Identifier(string.substring(1));
				Tag<EntityType<?>> tag = EntityTypeTags.getContainer().getOrCreate(identifier);
				return new EntityTypePredicate.Tagged(tag);
			} else {
				Identifier identifier = new Identifier(string);
				EntityType<?> entityType = (EntityType<?>)Registry.ENTITY_TYPE
					.getOrEmpty(identifier)
					.orElseThrow(
						() -> new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds()))
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

	public static EntityTypePredicate create(Tag<EntityType<?>> tag) {
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
			return new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.type).toString());
		}
	}

	static class Tagged extends EntityTypePredicate {
		private final Tag<EntityType<?>> tag;

		public Tagged(Tag<EntityType<?>> tag) {
			this.tag = tag;
		}

		@Override
		public boolean matches(EntityType<?> type) {
			return this.tag.contains(type);
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive("#" + this.tag.getId().toString());
		}
	}
}
