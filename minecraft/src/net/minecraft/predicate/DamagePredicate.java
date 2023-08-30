package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;

public record DamagePredicate(
	NumberRange.DoubleRange dealt,
	NumberRange.DoubleRange taken,
	Optional<EntityPredicate> sourceEntity,
	Optional<Boolean> blocked,
	Optional<DamageSourcePredicate> source
) {
	public boolean test(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
		if (!this.dealt.test((double)dealt)) {
			return false;
		} else if (!this.taken.test((double)taken)) {
			return false;
		} else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).test(player, source.getAttacker())) {
			return false;
		} else {
			return this.blocked.isPresent() && this.blocked.get() != blocked
				? false
				: !this.source.isPresent() || ((DamageSourcePredicate)this.source.get()).test(player, source);
		}
	}

	public static Optional<DamagePredicate> fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "damage");
			NumberRange.DoubleRange doubleRange = NumberRange.DoubleRange.fromJson(jsonObject.get("dealt"));
			NumberRange.DoubleRange doubleRange2 = NumberRange.DoubleRange.fromJson(jsonObject.get("taken"));
			Optional<Boolean> optional = jsonObject.has("blocked") ? Optional.of(JsonHelper.getBoolean(jsonObject, "blocked")) : Optional.empty();
			Optional<EntityPredicate> optional2 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
			Optional<DamageSourcePredicate> optional3 = DamageSourcePredicate.fromJson(jsonObject.get("type"));
			return doubleRange.isDummy() && doubleRange2.isDummy() && optional2.isEmpty() && optional.isEmpty() && optional3.isEmpty()
				? Optional.empty()
				: Optional.of(new DamagePredicate(doubleRange, doubleRange2, optional2, optional, optional3));
		} else {
			return Optional.empty();
		}
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("dealt", this.dealt.toJson());
		jsonObject.add("taken", this.taken.toJson());
		this.sourceEntity.ifPresent(entityPredicate -> jsonObject.add("source_entity", entityPredicate.toJson()));
		this.source.ifPresent(damageSourcePredicate -> jsonObject.add("type", damageSourcePredicate.toJson()));
		this.blocked.ifPresent(boolean_ -> jsonObject.addProperty("blocked", boolean_));
		return jsonObject;
	}

	public static class Builder {
		private NumberRange.DoubleRange dealt = NumberRange.DoubleRange.ANY;
		private NumberRange.DoubleRange taken = NumberRange.DoubleRange.ANY;
		private Optional<EntityPredicate> sourceEntity = Optional.empty();
		private Optional<Boolean> blocked = Optional.empty();
		private Optional<DamageSourcePredicate> type = Optional.empty();

		public static DamagePredicate.Builder create() {
			return new DamagePredicate.Builder();
		}

		public DamagePredicate.Builder dealt(NumberRange.DoubleRange dealt) {
			this.dealt = dealt;
			return this;
		}

		public DamagePredicate.Builder taken(NumberRange.DoubleRange taken) {
			this.taken = taken;
			return this;
		}

		public DamagePredicate.Builder sourceEntity(EntityPredicate sourceEntity) {
			this.sourceEntity = Optional.of(sourceEntity);
			return this;
		}

		public DamagePredicate.Builder blocked(Boolean blocked) {
			this.blocked = Optional.of(blocked);
			return this;
		}

		public DamagePredicate.Builder type(DamageSourcePredicate type) {
			this.type = Optional.of(type);
			return this;
		}

		public DamagePredicate.Builder type(DamageSourcePredicate.Builder builder) {
			this.type = Optional.of(builder.build());
			return this;
		}

		public DamagePredicate build() {
			return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
		}
	}
}
