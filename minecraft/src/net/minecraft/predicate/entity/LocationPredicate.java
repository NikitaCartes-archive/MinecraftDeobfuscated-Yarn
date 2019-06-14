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
	public static final LocationPredicate ANY = new LocationPredicate(
		NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, null
	);
	private final NumberRange.FloatRange x;
	private final NumberRange.FloatRange y;
	private final NumberRange.FloatRange z;
	@Nullable
	private final Biome field_9683;
	@Nullable
	private final StructureFeature<?> field_9687;
	@Nullable
	private final DimensionType field_9686;

	public LocationPredicate(
		NumberRange.FloatRange floatRange,
		NumberRange.FloatRange floatRange2,
		NumberRange.FloatRange floatRange3,
		@Nullable Biome biome,
		@Nullable StructureFeature<?> structureFeature,
		@Nullable DimensionType dimensionType
	) {
		this.x = floatRange;
		this.y = floatRange2;
		this.z = floatRange3;
		this.field_9683 = biome;
		this.field_9687 = structureFeature;
		this.field_9686 = dimensionType;
	}

	public static LocationPredicate method_9022(Biome biome) {
		return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, biome, null, null);
	}

	public static LocationPredicate method_9016(DimensionType dimensionType) {
		return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, dimensionType);
	}

	public static LocationPredicate method_9017(StructureFeature<?> structureFeature) {
		return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, structureFeature, null);
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
		} else if (this.field_9686 != null && this.field_9686 != serverWorld.field_9247.method_12460()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)f, (double)g, (double)h);
			return this.field_9683 != null && this.field_9683 != serverWorld.method_8310(blockPos)
				? false
				: this.field_9687 == null || this.field_9687.isInsideStructure(serverWorld, blockPos);
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

			if (this.field_9686 != null) {
				jsonObject.addProperty("dimension", DimensionType.getId(this.field_9686).toString());
			}

			if (this.field_9687 != null) {
				jsonObject.addProperty("feature", (String)Feature.STRUCTURES.inverse().get(this.field_9687));
			}

			if (this.field_9683 != null) {
				jsonObject.addProperty("biome", Registry.BIOME.getId(this.field_9683).toString());
			}

			return jsonObject;
		}
	}

	public static LocationPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "location");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
			NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject2.get("x"));
			NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject2.get("y"));
			NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject2.get("z"));
			DimensionType dimensionType = jsonObject.has("dimension") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "dimension"))) : null;
			StructureFeature<?> structureFeature = jsonObject.has("feature")
				? (StructureFeature)Feature.STRUCTURES.get(JsonHelper.getString(jsonObject, "feature"))
				: null;
			Biome biome = null;
			if (jsonObject.has("biome")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "biome"));
				biome = (Biome)Registry.BIOME.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown biome '" + identifier + "'"));
			}

			return new LocationPredicate(floatRange, floatRange2, floatRange3, biome, structureFeature, dimensionType);
		} else {
			return ANY;
		}
	}

	public static class Builder {
		private NumberRange.FloatRange x = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange y = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange z = NumberRange.FloatRange.ANY;
		@Nullable
		private Biome field_9690;
		@Nullable
		private StructureFeature<?> field_9688;
		@Nullable
		private DimensionType field_9691;

		public LocationPredicate.Builder method_9024(@Nullable Biome biome) {
			this.field_9690 = biome;
			return this;
		}

		public LocationPredicate build() {
			return new LocationPredicate(this.x, this.y, this.z, this.field_9690, this.field_9688, this.field_9691);
		}
	}
}
