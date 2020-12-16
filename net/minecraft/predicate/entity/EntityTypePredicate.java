/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

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

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement toJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        String string = JsonHelper.asString(json, "type");
        if (string.startsWith("#")) {
            Identifier identifier2 = new Identifier(string.substring(1));
            return new Tagged(ServerTagManagerHolder.getTagManager().getTag(Registry.ENTITY_TYPE_KEY, identifier2, identifier -> new JsonSyntaxException("Unknown entity tag '" + identifier + "'")));
        }
        Identifier identifier3 = new Identifier(string);
        EntityType<?> entityType = Registry.ENTITY_TYPE.getOrEmpty(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + identifier3 + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds())));
        return new Single(entityType);
    }

    public static EntityTypePredicate create(EntityType<?> type) {
        return new Single(type);
    }

    public static EntityTypePredicate create(Tag<EntityType<?>> tag) {
        return new Tagged(tag);
    }

    static class Tagged
    extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;

        public Tagged(Tag<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean matches(EntityType<?> type) {
            return type.isIn(this.tag);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + ServerTagManagerHolder.getTagManager().getTagId(Registry.ENTITY_TYPE_KEY, this.tag, () -> new IllegalStateException("Unknown entity type tag")));
        }
    }

    static class Single
    extends EntityTypePredicate {
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
}

