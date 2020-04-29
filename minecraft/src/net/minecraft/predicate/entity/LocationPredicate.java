package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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
		LightPredicate.ANY,
		BlockPredicate.ANY,
		FluidPredicate.ANY
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
	private final LightPredicate light;
	private final BlockPredicate block;
	private final FluidPredicate fluid;

	public LocationPredicate(
		NumberRange.FloatRange x,
		NumberRange.FloatRange y,
		NumberRange.FloatRange z,
		@Nullable Biome biome,
		@Nullable StructureFeature<?> feature,
		@Nullable DimensionType dimension,
		LightPredicate light,
		BlockPredicate block,
		FluidPredicate fluid
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.biome = biome;
		this.feature = feature;
		this.dimension = dimension;
		this.light = light;
		this.block = block;
		this.fluid = fluid;
	}

	public static LocationPredicate biome(Biome biome) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			biome,
			null,
			null,
			LightPredicate.ANY,
			BlockPredicate.ANY,
			FluidPredicate.ANY
		);
	}

	public static LocationPredicate dimension(DimensionType dimension) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			null,
			null,
			dimension,
			LightPredicate.ANY,
			BlockPredicate.ANY,
			FluidPredicate.ANY
		);
	}

	public static LocationPredicate feature(StructureFeature<?> feature) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			null,
			feature,
			null,
			LightPredicate.ANY,
			BlockPredicate.ANY,
			FluidPredicate.ANY
		);
	}

	public boolean test(ServerWorld world, double x, double y, double z) {
		return this.test(world, (float)x, (float)y, (float)z);
	}

	public boolean test(ServerWorld world, float x, float y, float z) {
		if (!this.x.test(x)) {
			return false;
		} else if (!this.y.test(y)) {
			return false;
		} else if (!this.z.test(z)) {
			return false;
		} else if (this.dimension != null && this.dimension != world.dimension.getType()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)x, (double)y, (double)z);
			boolean bl = world.canSetBlock(blockPos);
			if (this.biome == null || bl && this.biome == world.getBiome(blockPos)) {
				if (this.feature == null || bl && this.feature.isInsideStructure(world, world.getStructureAccessor(), blockPos)) {
					if (!this.light.test(world, blockPos)) {
						return false;
					} else {
						return !this.block.test(world, blockPos) ? false : this.fluid.test(world, blockPos);
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.x.isDummy() || !this.y.isDummy() || !this.z.isDummy()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("x", this.x.toJson());
				jsonObject2.add("y", this.y.toJson());
				jsonObject2.add("z", this.z.toJson());
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

			jsonObject.add("light", this.light.toJson());
			jsonObject.add("block", this.block.toJson());
			jsonObject.add("fluid", this.fluid.toJson());
			return jsonObject;
		}
	}

	public static LocationPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "location");
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

			LightPredicate lightPredicate = LightPredicate.fromJson(jsonObject.get("light"));
			BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
			FluidPredicate fluidPredicate = FluidPredicate.fromJson(jsonObject.get("fluid"));
			return new LocationPredicate(floatRange, floatRange2, floatRange3, biome, structureFeature, dimensionType, lightPredicate, blockPredicate, fluidPredicate);
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
		private LightPredicate light = LightPredicate.ANY;
		private BlockPredicate block = BlockPredicate.ANY;
		private FluidPredicate fluid = FluidPredicate.ANY;

		public static LocationPredicate.Builder create() {
			return new LocationPredicate.Builder();
		}

		public LocationPredicate.Builder biome(@Nullable Biome biome) {
			this.biome = biome;
			return this;
		}

		public LocationPredicate build() {
			return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.light, this.block, this.fluid);
		}
	}
}
