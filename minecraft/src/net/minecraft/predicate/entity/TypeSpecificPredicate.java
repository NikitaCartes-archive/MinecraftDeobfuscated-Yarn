package net.minecraft.predicate.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public interface TypeSpecificPredicate {
	TypeSpecificPredicate ANY = new TypeSpecificPredicate() {
		@Override
		public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
			return true;
		}

		@Override
		public JsonObject typeSpecificToJson() {
			return new JsonObject();
		}

		@Override
		public TypeSpecificPredicate.Deserializer getDeserializer() {
			return TypeSpecificPredicate.Deserializers.ANY;
		}
	};

	static TypeSpecificPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "type_specific");
			String string = JsonHelper.getString(jsonObject, "type", null);
			if (string == null) {
				return ANY;
			} else {
				TypeSpecificPredicate.Deserializer deserializer = (TypeSpecificPredicate.Deserializer)TypeSpecificPredicate.Deserializers.TYPES.get(string);
				if (deserializer == null) {
					throw new JsonSyntaxException("Unknown sub-predicate type: " + string);
				} else {
					return deserializer.deserialize(jsonObject);
				}
			}
		} else {
			return ANY;
		}
	}

	boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos);

	JsonObject typeSpecificToJson();

	default JsonElement toJson() {
		if (this.getDeserializer() == TypeSpecificPredicate.Deserializers.ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = this.typeSpecificToJson();
			String string = (String)TypeSpecificPredicate.Deserializers.TYPES.inverse().get(this.getDeserializer());
			jsonObject.addProperty("type", string);
			return jsonObject;
		}
	}

	TypeSpecificPredicate.Deserializer getDeserializer();

	static TypeSpecificPredicate cat(CatVariant variant) {
		return TypeSpecificPredicate.Deserializers.CAT.createPredicate(variant);
	}

	static TypeSpecificPredicate frog(FrogVariant variant) {
		return TypeSpecificPredicate.Deserializers.FROG.createPredicate(variant);
	}

	public interface Deserializer {
		TypeSpecificPredicate deserialize(JsonObject json);
	}

	public static final class Deserializers {
		public static final TypeSpecificPredicate.Deserializer ANY = json -> TypeSpecificPredicate.ANY;
		public static final TypeSpecificPredicate.Deserializer LIGHTNING = LightningBoltPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer FISHING_HOOK = FishingHookPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer PLAYER = PlayerPredicate::fromJson;
		public static final TypeSpecificPredicate.Deserializer SLIME = SlimePredicate::fromJson;
		public static final VariantPredicates<CatVariant> CAT = VariantPredicates.create(
			Registries.CAT_VARIANT, entity -> entity instanceof CatEntity catEntity ? Optional.of(catEntity.getVariant()) : Optional.empty()
		);
		public static final VariantPredicates<FrogVariant> FROG = VariantPredicates.create(
			Registries.FROG_VARIANT, entity -> entity instanceof FrogEntity frogEntity ? Optional.of(frogEntity.getVariant()) : Optional.empty()
		);
		public static final BiMap<String, TypeSpecificPredicate.Deserializer> TYPES = ImmutableBiMap.of(
			"any",
			ANY,
			"lightning",
			LIGHTNING,
			"fishing_hook",
			FISHING_HOOK,
			"player",
			PLAYER,
			"slime",
			SLIME,
			"cat",
			CAT.getDeserializer(),
			"frog",
			FROG.getDeserializer()
		);
	}
}
