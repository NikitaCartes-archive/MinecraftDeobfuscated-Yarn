package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public record LocationPredicate(
	Optional<LocationPredicate.PositionRange> position,
	Optional<RegistryEntryList<Biome>> biomes,
	Optional<RegistryEntryList<Structure>> structures,
	Optional<RegistryKey<World>> dimension,
	Optional<Boolean> smokey,
	Optional<LightPredicate> light,
	Optional<BlockPredicate> block,
	Optional<FluidPredicate> fluid
) {
	public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(LocationPredicate.PositionRange.CODEC, "position").forGetter(LocationPredicate::position),
					Codecs.createStrictOptionalFieldCodec(RegistryCodecs.entryList(RegistryKeys.BIOME), "biomes").forGetter(LocationPredicate::biomes),
					Codecs.createStrictOptionalFieldCodec(RegistryCodecs.entryList(RegistryKeys.STRUCTURE), "structures").forGetter(LocationPredicate::structures),
					Codecs.createStrictOptionalFieldCodec(RegistryKey.createCodec(RegistryKeys.WORLD), "dimension").forGetter(LocationPredicate::dimension),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "smokey").forGetter(LocationPredicate::smokey),
					Codecs.createStrictOptionalFieldCodec(LightPredicate.CODEC, "light").forGetter(LocationPredicate::light),
					Codecs.createStrictOptionalFieldCodec(BlockPredicate.CODEC, "block").forGetter(LocationPredicate::block),
					Codecs.createStrictOptionalFieldCodec(FluidPredicate.CODEC, "fluid").forGetter(LocationPredicate::fluid)
				)
				.apply(instance, LocationPredicate::new)
	);

	public boolean test(ServerWorld world, double x, double y, double z) {
		if (this.position.isPresent() && !((LocationPredicate.PositionRange)this.position.get()).test(x, y, z)) {
			return false;
		} else if (this.dimension.isPresent() && this.dimension.get() != world.getRegistryKey()) {
			return false;
		} else {
			BlockPos blockPos = BlockPos.ofFloored(x, y, z);
			boolean bl = world.canSetBlock(blockPos);
			if (!this.biomes.isPresent() || bl && ((RegistryEntryList)this.biomes.get()).contains(world.getBiome(blockPos))) {
				if (!this.structures.isPresent()
					|| bl && world.getStructureAccessor().getStructureContaining(blockPos, (RegistryEntryList<Structure>)this.structures.get()).hasChildren()) {
					if (!this.smokey.isPresent() || bl && this.smokey.get() == CampfireBlock.isLitCampfireInRange(world, blockPos)) {
						if (this.light.isPresent() && !((LightPredicate)this.light.get()).test(world, blockPos)) {
							return false;
						} else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).test(world, blockPos)) {
							return false;
						} else {
							return !this.fluid.isPresent() || ((FluidPredicate)this.fluid.get()).test(world, blockPos);
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

	public static class Builder {
		private NumberRange.DoubleRange x = NumberRange.DoubleRange.ANY;
		private NumberRange.DoubleRange y = NumberRange.DoubleRange.ANY;
		private NumberRange.DoubleRange z = NumberRange.DoubleRange.ANY;
		private Optional<RegistryEntryList<Biome>> biome = Optional.empty();
		private Optional<RegistryEntryList<Structure>> feature = Optional.empty();
		private Optional<RegistryKey<World>> dimension = Optional.empty();
		private Optional<Boolean> smokey = Optional.empty();
		private Optional<LightPredicate> light = Optional.empty();
		private Optional<BlockPredicate> block = Optional.empty();
		private Optional<FluidPredicate> fluid = Optional.empty();

		public static LocationPredicate.Builder create() {
			return new LocationPredicate.Builder();
		}

		public static LocationPredicate.Builder createBiome(RegistryEntry<Biome> biome) {
			return create().biome(RegistryEntryList.of(biome));
		}

		public static LocationPredicate.Builder createDimension(RegistryKey<World> dimension) {
			return create().dimension(dimension);
		}

		public static LocationPredicate.Builder createStructure(RegistryEntry<Structure> structure) {
			return create().structure(RegistryEntryList.of(structure));
		}

		public static LocationPredicate.Builder createY(NumberRange.DoubleRange y) {
			return create().y(y);
		}

		public LocationPredicate.Builder x(NumberRange.DoubleRange x) {
			this.x = x;
			return this;
		}

		public LocationPredicate.Builder y(NumberRange.DoubleRange y) {
			this.y = y;
			return this;
		}

		public LocationPredicate.Builder z(NumberRange.DoubleRange z) {
			this.z = z;
			return this;
		}

		public LocationPredicate.Builder biome(RegistryEntryList<Biome> biome) {
			this.biome = Optional.of(biome);
			return this;
		}

		public LocationPredicate.Builder structure(RegistryEntryList<Structure> structure) {
			this.feature = Optional.of(structure);
			return this;
		}

		public LocationPredicate.Builder dimension(RegistryKey<World> dimension) {
			this.dimension = Optional.of(dimension);
			return this;
		}

		public LocationPredicate.Builder light(LightPredicate.Builder light) {
			this.light = Optional.of(light.build());
			return this;
		}

		public LocationPredicate.Builder block(BlockPredicate.Builder block) {
			this.block = Optional.of(block.build());
			return this;
		}

		public LocationPredicate.Builder fluid(FluidPredicate.Builder fluid) {
			this.fluid = Optional.of(fluid.build());
			return this;
		}

		public LocationPredicate.Builder smokey(boolean smokey) {
			this.smokey = Optional.of(smokey);
			return this;
		}

		public LocationPredicate build() {
			Optional<LocationPredicate.PositionRange> optional = LocationPredicate.PositionRange.create(this.x, this.y, this.z);
			return new LocationPredicate(optional, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
		}
	}

	static record PositionRange(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
		public static final Codec<LocationPredicate.PositionRange> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "x", NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::x),
						Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "y", NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::y),
						Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "z", NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::z)
					)
					.apply(instance, LocationPredicate.PositionRange::new)
		);

		static Optional<LocationPredicate.PositionRange> create(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
			return x.isDummy() && y.isDummy() && z.isDummy() ? Optional.empty() : Optional.of(new LocationPredicate.PositionRange(x, y, z));
		}

		public boolean test(double x, double y, double z) {
			return this.x.test(x) && this.y.test(y) && this.z.test(z);
		}
	}
}
