/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LocationPredicate {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final LocationPredicate ANY = new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    @Nullable
    private final RegistryKey<Biome> biome;
    @Nullable
    private final RegistryKey<Structure> feature;
    @Nullable
    private final RegistryKey<World> dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, @Nullable RegistryKey<Biome> biome, @Nullable RegistryKey<Structure> feature, @Nullable RegistryKey<World> dimension, @Nullable Boolean smokey, LightPredicate light, BlockPredicate block, FluidPredicate fluid) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.biome = biome;
        this.feature = feature;
        this.dimension = dimension;
        this.smokey = smokey;
        this.light = light;
        this.block = block;
        this.fluid = fluid;
    }

    public static LocationPredicate biome(RegistryKey<Biome> biome) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, biome, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate dimension(RegistryKey<World> dimension) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, dimension, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate feature(RegistryKey<Structure> feature) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, feature, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate y(NumberRange.FloatRange y) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, y, NumberRange.FloatRange.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean test(ServerWorld world, double x, double y, double z) {
        if (!this.x.test(x)) {
            return false;
        }
        if (!this.y.test(y)) {
            return false;
        }
        if (!this.z.test(z)) {
            return false;
        }
        if (this.dimension != null && this.dimension != world.getRegistryKey()) {
            return false;
        }
        BlockPos blockPos = new BlockPos(x, y, z);
        boolean bl = world.canSetBlock(blockPos);
        if (!(this.biome == null || bl && world.getBiome(blockPos).matchesKey(this.biome))) {
            return false;
        }
        if (!(this.feature == null || bl && world.getStructureAccessor().getStructureContaining(blockPos, this.feature).hasChildren())) {
            return false;
        }
        if (!(this.smokey == null || bl && this.smokey == CampfireBlock.isLitCampfireInRange(world, blockPos))) {
            return false;
        }
        if (!this.light.test(world, blockPos)) {
            return false;
        }
        if (!this.block.test(world, blockPos)) {
            return false;
        }
        return this.fluid.test(world, blockPos);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!(this.x.isDummy() && this.y.isDummy() && this.z.isDummy())) {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("x", this.x.toJson());
            jsonObject2.add("y", this.y.toJson());
            jsonObject2.add("z", this.z.toJson());
            jsonObject.add("position", jsonObject2);
        }
        if (this.dimension != null) {
            World.CODEC.encodeStart(JsonOps.INSTANCE, this.dimension).resultOrPartial(LOGGER::error).ifPresent(json -> jsonObject.add("dimension", (JsonElement)json));
        }
        if (this.feature != null) {
            jsonObject.addProperty("structure", this.feature.getValue().toString());
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

    public static LocationPredicate fromJson(@Nullable JsonElement json) {
        RegistryKey registryKey;
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "location");
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
        NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject2.get("x"));
        NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject2.get("y"));
        NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject2.get("z"));
        RegistryKey registryKey2 = jsonObject.has("dimension") ? (RegistryKey)Identifier.CODEC.parse(JsonOps.INSTANCE, jsonObject.get("dimension")).resultOrPartial(LOGGER::error).map(identifier -> RegistryKey.of(RegistryKeys.WORLD, identifier)).orElse(null) : (registryKey = null);
        RegistryKey registryKey22 = jsonObject.has("structure") ? (RegistryKey)Identifier.CODEC.parse(JsonOps.INSTANCE, jsonObject.get("structure")).resultOrPartial(LOGGER::error).map(identifier -> RegistryKey.of(RegistryKeys.STRUCTURE_WORLDGEN, identifier)).orElse(null) : null;
        RegistryKey<Biome> registryKey3 = null;
        if (jsonObject.has("biome")) {
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "biome"));
            registryKey3 = RegistryKey.of(RegistryKeys.BIOME_WORLDGEN, identifier2);
        }
        Boolean boolean_ = jsonObject.has("smokey") ? Boolean.valueOf(jsonObject.get("smokey").getAsBoolean()) : null;
        LightPredicate lightPredicate = LightPredicate.fromJson(jsonObject.get("light"));
        BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
        FluidPredicate fluidPredicate = FluidPredicate.fromJson(jsonObject.get("fluid"));
        return new LocationPredicate(floatRange, floatRange2, floatRange3, registryKey3, registryKey22, registryKey, boolean_, lightPredicate, blockPredicate, fluidPredicate);
    }

    public static class Builder {
        private NumberRange.FloatRange x = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange y = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange z = NumberRange.FloatRange.ANY;
        @Nullable
        private RegistryKey<Biome> biome;
        @Nullable
        private RegistryKey<Structure> feature;
        @Nullable
        private RegistryKey<World> dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light = LightPredicate.ANY;
        private BlockPredicate block = BlockPredicate.ANY;
        private FluidPredicate fluid = FluidPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder x(NumberRange.FloatRange x) {
            this.x = x;
            return this;
        }

        public Builder y(NumberRange.FloatRange y) {
            this.y = y;
            return this;
        }

        public Builder z(NumberRange.FloatRange z) {
            this.z = z;
            return this;
        }

        public Builder biome(@Nullable RegistryKey<Biome> biome) {
            this.biome = biome;
            return this;
        }

        public Builder feature(@Nullable RegistryKey<Structure> feature) {
            this.feature = feature;
            return this;
        }

        public Builder dimension(@Nullable RegistryKey<World> dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder light(LightPredicate light) {
            this.light = light;
            return this;
        }

        public Builder block(BlockPredicate block) {
            this.block = block;
            return this;
        }

        public Builder fluid(FluidPredicate fluid) {
            this.fluid = fluid;
            return this;
        }

        public Builder smokey(Boolean smokey) {
            this.smokey = smokey;
            return this;
        }

        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}

