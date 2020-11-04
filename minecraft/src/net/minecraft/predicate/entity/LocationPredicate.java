package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationPredicate {
	private static final Logger field_24732 = LogManager.getLogger();
	public static final LocationPredicate ANY = new LocationPredicate(
		NumberRange.FloatRange.ANY,
		NumberRange.FloatRange.ANY,
		NumberRange.FloatRange.ANY,
		null,
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
	private final RegistryKey<Biome> biome;
	@Nullable
	private final StructureFeature<?> feature;
	@Nullable
	private final RegistryKey<World> dimension;
	@Nullable
	private final Boolean smokey;
	private final LightPredicate light;
	private final BlockPredicate block;
	private final FluidPredicate fluid;

	public LocationPredicate(
		NumberRange.FloatRange x,
		NumberRange.FloatRange y,
		NumberRange.FloatRange z,
		@Nullable RegistryKey<Biome> registryKey,
		@Nullable StructureFeature<?> feature,
		@Nullable RegistryKey<World> dimension,
		@Nullable Boolean smokey,
		LightPredicate light,
		BlockPredicate block,
		FluidPredicate fluid
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.biome = registryKey;
		this.feature = feature;
		this.dimension = dimension;
		this.smokey = smokey;
		this.light = light;
		this.block = block;
		this.fluid = fluid;
	}

	public static LocationPredicate biome(RegistryKey<Biome> registryKey) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			registryKey,
			null,
			null,
			null,
			LightPredicate.ANY,
			BlockPredicate.ANY,
			FluidPredicate.ANY
		);
	}

	public static LocationPredicate dimension(RegistryKey<World> dimension) {
		return new LocationPredicate(
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			NumberRange.FloatRange.ANY,
			null,
			null,
			dimension,
			null,
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
		} else if (this.dimension != null && this.dimension != world.getRegistryKey()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)x, (double)y, (double)z);
			boolean bl = world.canSetBlock(blockPos);
			Optional<RegistryKey<Biome>> optional = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(world.getBiome(blockPos));
			if (!optional.isPresent()) {
				return false;
			} else if (this.biome == null || bl && this.biome == optional.get()) {
				if (this.feature == null || bl && world.getStructureAccessor().getStructureAt(blockPos, true, this.feature).hasChildren()) {
					if (this.smokey == null || bl && this.smokey == CampfireBlock.isLitCampfireInRange(world, blockPos)) {
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
				World.CODEC
					.encodeStart(JsonOps.INSTANCE, this.dimension)
					.resultOrPartial(field_24732::error)
					.ifPresent(jsonElement -> jsonObject.add("dimension", jsonElement));
			}

			if (this.feature != null) {
				jsonObject.addProperty("feature", this.feature.getName());
			}

			if (this.biome != null) {
				jsonObject.addProperty("biome", this.biome.getValue().toString());
			}

			if (this.smokey != null) {
				jsonObject.addProperty("smokey", this.smokey);
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
			RegistryKey<World> registryKey = jsonObject.has("dimension")
				? (RegistryKey)Identifier.CODEC
					.parse(JsonOps.INSTANCE, jsonObject.get("dimension"))
					.resultOrPartial(field_24732::error)
					.map(identifier -> RegistryKey.of(Registry.DIMENSION, identifier))
					.orElse(null)
				: null;
			StructureFeature<?> structureFeature = jsonObject.has("feature")
				? (StructureFeature)StructureFeature.STRUCTURES.get(JsonHelper.getString(jsonObject, "feature"))
				: null;
			RegistryKey<Biome> registryKey2 = null;
			if (jsonObject.has("biome")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "biome"));
				registryKey2 = RegistryKey.of(Registry.BIOME_KEY, identifier);
			}

			Boolean boolean_ = jsonObject.has("smokey") ? jsonObject.get("smokey").getAsBoolean() : null;
			LightPredicate lightPredicate = LightPredicate.fromJson(jsonObject.get("light"));
			BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
			FluidPredicate fluidPredicate = FluidPredicate.fromJson(jsonObject.get("fluid"));
			return new LocationPredicate(
				floatRange, floatRange2, floatRange3, registryKey2, structureFeature, registryKey, boolean_, lightPredicate, blockPredicate, fluidPredicate
			);
		} else {
			return ANY;
		}
	}

	public static class Builder {
		private NumberRange.FloatRange x = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange y = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange z = NumberRange.FloatRange.ANY;
		@Nullable
		private RegistryKey<Biome> biome;
		@Nullable
		private StructureFeature<?> feature;
		@Nullable
		private RegistryKey<World> dimension;
		@Nullable
		private Boolean smokey;
		private LightPredicate light = LightPredicate.ANY;
		private BlockPredicate block = BlockPredicate.ANY;
		private FluidPredicate fluid = FluidPredicate.ANY;

		public static LocationPredicate.Builder create() {
			return new LocationPredicate.Builder();
		}

		public LocationPredicate.Builder biome(@Nullable RegistryKey<Biome> registryKey) {
			this.biome = registryKey;
			return this;
		}

		public LocationPredicate.Builder block(BlockPredicate block) {
			this.block = block;
			return this;
		}

		public LocationPredicate.Builder smokey(Boolean smokey) {
			this.smokey = smokey;
			return this;
		}

		public LocationPredicate build() {
			return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
		}
	}
}
