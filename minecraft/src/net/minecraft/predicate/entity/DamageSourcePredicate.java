package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class DamageSourcePredicate {
	public static final DamageSourcePredicate EMPTY = DamageSourcePredicate.Builder.create().build();
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
		} else {
			for (TagPredicate<DamageType> tagPredicate : this.tagPredicates) {
				if (!tagPredicate.test(damageSource.getTypeRegistryEntry())) {
					return false;
				}
			}

			return !this.directEntity.test(world, pos, damageSource.getSource()) ? false : this.sourceEntity.test(world, pos, damageSource.getAttacker());
		}
	}

	public static DamageSourcePredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "damage type");
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "tags", null);
			List<TagPredicate<DamageType>> list;
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
		} else {
			return EMPTY;
		}
	}

	public JsonElement toJson() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.tagPredicates.isEmpty()) {
				JsonArray jsonArray = new JsonArray(this.tagPredicates.size());

				for (int i = 0; i < this.tagPredicates.size(); i++) {
					jsonArray.add(((TagPredicate)this.tagPredicates.get(i)).toJson());
				}

				jsonObject.add("tags", jsonArray);
			}

			jsonObject.add("direct_entity", this.directEntity.toJson());
			jsonObject.add("source_entity", this.sourceEntity.toJson());
			return jsonObject;
		}
	}

	public static class Builder {
		private final ImmutableList.Builder<TagPredicate<DamageType>> tagPredicates = ImmutableList.builder();
		private EntityPredicate directEntity = EntityPredicate.ANY;
		private EntityPredicate sourceEntity = EntityPredicate.ANY;

		public static DamageSourcePredicate.Builder create() {
			return new DamageSourcePredicate.Builder();
		}

		public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> tagPredicate) {
			this.tagPredicates.add(tagPredicate);
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate entity) {
			this.directEntity = entity;
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder entity) {
			this.directEntity = entity.build();
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate entity) {
			this.sourceEntity = entity;
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate.Builder entity) {
			this.sourceEntity = entity.build();
			return this;
		}

		public DamageSourcePredicate build() {
			return new DamageSourcePredicate(this.tagPredicates.build(), this.directEntity, this.sourceEntity);
		}
	}
}
