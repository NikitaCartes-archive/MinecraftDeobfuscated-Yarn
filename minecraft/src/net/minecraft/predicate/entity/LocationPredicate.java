package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class LocationPredicate {
	public static final LocationPredicate ANY = new LocationPredicate(NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, null, null, null);
	private final NumberRange.Float x;
	private final NumberRange.Float y;
	private final NumberRange.Float z;
	@Nullable
	private final Biome biome;
	@Nullable
	private final StructureFeature<?> feature;
	@Nullable
	private final DimensionType dimension;

	public LocationPredicate(
		NumberRange.Float float_,
		NumberRange.Float float2,
		NumberRange.Float float3,
		@Nullable Biome biome,
		@Nullable StructureFeature<?> structureFeature,
		@Nullable DimensionType dimensionType
	) {
		this.x = float_;
		this.y = float2;
		this.z = float3;
		this.biome = biome;
		this.feature = structureFeature;
		this.dimension = dimensionType;
	}

	public static LocationPredicate method_9022(Biome biome) {
		return new LocationPredicate(NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, biome, null, null);
	}

	public static LocationPredicate method_9016(DimensionType dimensionType) {
		return new LocationPredicate(NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, null, null, dimensionType);
	}

	public static LocationPredicate method_9017(StructureFeature<?> structureFeature) {
		return new LocationPredicate(NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, null, structureFeature, null);
	}

	public boolean test(ServerWorld serverWorld, double d, double e, double f) {
		return this.test(serverWorld, (float)d, (float)e, (float)f);
	}

	public boolean test(ServerWorld serverWorld, float f, float g, float h) {
		if (!this.x.matches(f)) {
			return false;
		} else if (!this.y.matches(g)) {
			return false;
		} else if (!this.z.matches(h)) {
			return false;
		} else if (this.dimension != null && this.dimension != serverWorld.dimension.getType()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)f, (double)g, (double)h);
			return this.biome != null && this.biome != serverWorld.getBiome(blockPos)
				? false
				: this.feature == null || this.feature.isInsideStructure(serverWorld, blockPos);
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.x.isDummy() || !this.y.isDummy() || !this.z.isDummy()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("x", this.x.serialize());
				jsonObject2.add("y", this.y.serialize());
				jsonObject2.add("z", this.z.serialize());
				jsonObject.add("position", jsonObject2);
			}

			if (this.dimension != null) {
				jsonObject.addProperty("dimension", DimensionType.getId(this.dimension).toString());
			}

			if (this.feature != null) {
				jsonObject.addProperty("feature", (String)Feature.STRUCTURES.inverse().get(this.feature));
			}

			if (this.biome != null) {
				jsonObject.addProperty("biome", Registry.BIOME.getId(this.biome).toString());
			}

			return jsonObject;
		}
	}

	public static LocationPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "location");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
			NumberRange.Float float_ = NumberRange.Float.fromJson(jsonObject2.get("x"));
			NumberRange.Float float2 = NumberRange.Float.fromJson(jsonObject2.get("y"));
			NumberRange.Float float3 = NumberRange.Float.fromJson(jsonObject2.get("z"));
			DimensionType dimensionType = jsonObject.has("dimension") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "dimension"))) : null;
			StructureFeature<?> structureFeature = jsonObject.has("feature")
				? (StructureFeature)Feature.STRUCTURES.get(JsonHelper.getString(jsonObject, "feature"))
				: null;
			Biome biome = null;
			if (jsonObject.has("biome")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "biome"));
				biome = (Biome)Registry.BIOME.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown biome '" + identifier + "'"));
			}

			return new LocationPredicate(float_, float2, float3, biome, structureFeature, dimensionType);
		} else {
			return ANY;
		}
	}

	public static class Builder {
		private NumberRange.Float x = NumberRange.Float.ANY;
		private NumberRange.Float y = NumberRange.Float.ANY;
		private NumberRange.Float z = NumberRange.Float.ANY;
		@Nullable
		private Biome biome;
		@Nullable
		private StructureFeature<?> feature;
		@Nullable
		private DimensionType dimension;

		public LocationPredicate.Builder biome(@Nullable Biome biome) {
			this.biome = biome;
			return this;
		}

		public LocationPredicate build() {
			return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension);
		}
	}
}
