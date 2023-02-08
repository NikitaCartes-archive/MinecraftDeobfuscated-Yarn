/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate EMPTY = Builder.create().build();
    private final List<TagPredicate<DamageType>> tagPredicates;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(List<TagPredicate<DamageType>> tagPredicates, EntityPredicate directEntity, EntityPredicate sourceEntity) {
        this.tagPredicates = tagPredicates;
        this.directEntity = directEntity;
        this.sourceEntity = sourceEntity;
    }

    public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
        return this.test(player.getWorld(), player.getPos(), damageSource);
    }

    public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
        if (this == EMPTY) {
            return true;
        }
        for (TagPredicate<DamageType> tagPredicate : this.tagPredicates) {
            if (tagPredicate.test(damageSource.getTypeRegistryEntry())) continue;
            return false;
        }
        if (!this.directEntity.test(world, pos, damageSource.getSource())) {
            return false;
        }
        return this.sourceEntity.test(world, pos, damageSource.getAttacker());
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement json) {
        List<TagPredicate<DamageType>> list;
        if (json == null || json.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "damage type");
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "tags", null);
        if (jsonArray != null) {
            list = new ArrayList(jsonArray.size());
            for (JsonElement jsonElement : jsonArray) {
                list.add(TagPredicate.fromJson(jsonElement, RegistryKeys.DAMAGE_TYPE));
            }
        } else {
            list = List.of();
        }
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("direct_entity"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        return new DamageSourcePredicate(list, entityPredicate, entityPredicate2);
    }

    public JsonElement toJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!this.tagPredicates.isEmpty()) {
            JsonArray jsonArray = new JsonArray(this.tagPredicates.size());
            for (int i = 0; i < this.tagPredicates.size(); ++i) {
                jsonArray.add(this.tagPredicates.get(i).toJson());
            }
            jsonObject.add("tags", jsonArray);
        }
        jsonObject.add("direct_entity", this.directEntity.toJson());
        jsonObject.add("source_entity", this.sourceEntity.toJson());
        return jsonObject;
    }

    public static class Builder {
        private final ImmutableList.Builder<TagPredicate<DamageType>> tagPredicates = ImmutableList.builder();
        private EntityPredicate directEntity = EntityPredicate.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder tag(TagPredicate<DamageType> tagPredicate) {
            this.tagPredicates.add((Object)tagPredicate);
            return this;
        }

        public Builder directEntity(EntityPredicate entity) {
            this.directEntity = entity;
            return this;
        }

        public Builder directEntity(EntityPredicate.Builder entity) {
            this.directEntity = entity.build();
            return this;
        }

        public Builder sourceEntity(EntityPredicate entity) {
            this.sourceEntity = entity;
            return this;
        }

        public Builder sourceEntity(EntityPredicate.Builder entity) {
            this.sourceEntity = entity.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate((List<TagPredicate<DamageType>>)((Object)this.tagPredicates.build()), this.directEntity, this.sourceEntity);
        }
    }
}

