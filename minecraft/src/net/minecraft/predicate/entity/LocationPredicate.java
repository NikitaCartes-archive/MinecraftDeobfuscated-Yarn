package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_4550;
import net.minecraft.class_4551;
import net.minecraft.class_4552;
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
		NumberRange.FloatRange.ANY,
		NumberRange.FloatRange.ANY,
		NumberRange.FloatRange.ANY,
		null,
		null,
		null,
		class_4552.field_20712,
		class_4550.field_20692,
		class_4551.field_20708
	);
	private final NumberRange.FloatRange x;
	private final NumberRange.FloatRange y;
	private final NumberRange.FloatRange z;
	@Nullable
	private final Biome biome;
	@Nullable
	private final StructureFeature<?> feature;
	@Nullable
	private final DimensionType dimension;
	private final class_4552 field_20714;
	private final class_4550 field_20715;
	private final class_4551 field_20716;

	public LocationPredicate(
		NumberRange.FloatRange floatRange,
		NumberRange.FloatRange floatRange2,
		NumberRange.FloatRange floatRange3,
		@Nullable Biome biome,
		@Nullable StructureFeature<?> structureFeature,
		@Nullable DimensionType dimensionType,
		class_4552 arg,
		class_4550 arg2,
		class_4551 arg3
	) {
		this.x = floatRange;
		this.y = floatRange2;
		this.z = floatRange3;
		this.biome = biome;
		this.feature = structureFeature;
		this.dimension = dimensionType;
		this.field_20714 = arg;
		this.field_20715 = arg2;
		this.field_20716 = arg3;
	}

	public static LocationPredicate biome(Biome biome) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			biome,
			null,
			null,
			class_4552.field_20712,
			class_4550.field_20692,
			class_4551.field_20708
		);
	}

	public static LocationPredicate dimension(DimensionType dimensionType) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			null,
			null,
			dimensionType,
			class_4552.field_20712,
			class_4550.field_20692,
			class_4551.field_20708
		);
	}

	public static LocationPredicate feature(StructureFeature<?> structureFeature) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			null,
			structureFeature,
			null,
			class_4552.field_20712,
			class_4550.field_20692,
			class_4551.field_20708
		);
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
			if (!serverWorld.canSetBlock(blockPos)) {
				return false;
			} else if (this.biome != null && this.biome != serverWorld.getBiome(blockPos)) {
				return false;
			} else if (this.feature != null && !this.feature.isInsideStructure(serverWorld, blockPos)) {
				return false;
			} else if (!this.field_20714.method_22483(serverWorld, blockPos)) {
				return false;
			} else {
				return !this.field_20715.method_22454(serverWorld, blockPos) ? false : this.field_20716.method_22475(serverWorld, blockPos);
			}
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

			jsonObject.add("light", this.field_20714.method_22481());
			jsonObject.add("block", this.field_20715.method_22452());
			jsonObject.add("fluid", this.field_20716.method_22473());
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

			class_4552 lv = class_4552.method_22482(jsonObject.get("light"));
			class_4550 lv2 = class_4550.method_22453(jsonObject.get("block"));
			class_4551 lv3 = class_4551.method_22474(jsonObject.get("fluid"));
			return new LocationPredicate(floatRange, floatRange2, floatRange3, biome, structureFeature, dimensionType, lv, lv2, lv3);
		} else {
			return ANY;
		}
	}

	public static class Builder {
		private NumberRange.FloatRange x = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange y = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange z = NumberRange.FloatRange.ANY;
		@Nullable
		private Biome biome;
		@Nullable
		private StructureFeature<?> feature;
		@Nullable
		private DimensionType dimension;
		private class_4552 field_20717 = class_4552.field_20712;
		private class_4550 field_20718 = class_4550.field_20692;
		private class_4551 field_20719 = class_4551.field_20708;

		public static LocationPredicate.Builder method_22484() {
			return new LocationPredicate.Builder();
		}

		public LocationPredicate.Builder biome(@Nullable Biome biome) {
			this.biome = biome;
			return this;
		}

		public LocationPredicate build() {
			return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.field_20717, this.field_20718, this.field_20719);
		}
	}
}
