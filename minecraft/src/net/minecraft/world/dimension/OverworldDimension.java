package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class OverworldDimension extends Dimension {
	public OverworldDimension(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public DimensionType method_12460() {
		return DimensionType.field_13072;
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		LevelGeneratorType levelGeneratorType = this.world.method_8401().getGeneratorType();
		ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> chunkGeneratorType = ChunkGeneratorType.field_12766;
		ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> chunkGeneratorType2 = ChunkGeneratorType.field_12768;
		ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> chunkGeneratorType3 = ChunkGeneratorType.field_12765;
		ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> chunkGeneratorType4 = ChunkGeneratorType.field_12770;
		ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> chunkGeneratorType5 = ChunkGeneratorType.field_12769;
		BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = BiomeSourceType.FIXED;
		BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
		BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> biomeSourceType3 = BiomeSourceType.CHECKERBOARD;
		if (levelGeneratorType == LevelGeneratorType.FLAT) {
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.fromDynamic(
				new Dynamic<>(NbtOps.INSTANCE, this.world.method_8401().getGeneratorOptions())
			);
			FixedBiomeSourceConfig fixedBiomeSourceConfig = biomeSourceType.getConfig().setBiome(flatChunkGeneratorConfig.getBiome());
			return chunkGeneratorType.create(this.world, biomeSourceType.applyConfig(fixedBiomeSourceConfig), flatChunkGeneratorConfig);
		} else if (levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			FixedBiomeSourceConfig fixedBiomeSourceConfig2 = biomeSourceType.getConfig().setBiome(Biomes.field_9451);
			return chunkGeneratorType2.create(this.world, biomeSourceType.applyConfig(fixedBiomeSourceConfig2), chunkGeneratorType2.method_12117());
		} else if (levelGeneratorType != LevelGeneratorType.BUFFET) {
			OverworldChunkGeneratorConfig overworldChunkGeneratorConfig2 = chunkGeneratorType5.method_12117();
			VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig2 = biomeSourceType2.getConfig()
				.method_9002(this.world.method_8401())
				.method_9004(overworldChunkGeneratorConfig2);
			return chunkGeneratorType5.create(this.world, biomeSourceType2.applyConfig(vanillaLayeredBiomeSourceConfig2), overworldChunkGeneratorConfig2);
		} else {
			BiomeSource biomeSource = null;
			JsonElement jsonElement = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.world.method_8401().getGeneratorOptions());
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("biome_source") && jsonObject.getAsJsonObject("biome_source").has("type") && jsonObject.getAsJsonObject("biome_source").has("options")) {
				BiomeSourceType<?, ?> biomeSourceType4 = Registry.BIOME_SOURCE_TYPE
					.get(new Identifier(jsonObject.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString()));
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("biome_source").getAsJsonObject("options");
				Biome[] biomes = new Biome[]{Biomes.field_9423};
				if (jsonObject2.has("biomes")) {
					JsonArray jsonArray = jsonObject2.getAsJsonArray("biomes");
					biomes = jsonArray.size() > 0 ? new Biome[jsonArray.size()] : new Biome[]{Biomes.field_9423};

					for (int i = 0; i < jsonArray.size(); i++) {
						biomes[i] = (Biome)Registry.BIOME.getOrEmpty(new Identifier(jsonArray.get(i).getAsString())).orElse(Biomes.field_9423);
					}
				}

				if (BiomeSourceType.FIXED == biomeSourceType4) {
					FixedBiomeSourceConfig fixedBiomeSourceConfig3 = biomeSourceType.getConfig().setBiome(biomes[0]);
					biomeSource = biomeSourceType.applyConfig(fixedBiomeSourceConfig3);
				}

				if (BiomeSourceType.CHECKERBOARD == biomeSourceType4) {
					int j = jsonObject2.has("size") ? jsonObject2.getAsJsonPrimitive("size").getAsInt() : 2;
					CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig = biomeSourceType3.getConfig().method_8777(biomes).method_8780(j);
					biomeSource = biomeSourceType3.applyConfig(checkerboardBiomeSourceConfig);
				}

				if (BiomeSourceType.VANILLA_LAYERED == biomeSourceType4) {
					VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig = biomeSourceType2.getConfig()
						.method_9004(new OverworldChunkGeneratorConfig())
						.method_9002(this.world.method_8401());
					biomeSource = biomeSourceType2.applyConfig(vanillaLayeredBiomeSourceConfig);
				}
			}

			if (biomeSource == null) {
				biomeSource = biomeSourceType.applyConfig(biomeSourceType.getConfig().setBiome(Biomes.field_9423));
			}

			BlockState blockState = Blocks.field_10340.method_9564();
			BlockState blockState2 = Blocks.field_10382.method_9564();
			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("options")) {
				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
					String string = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
					blockState = Registry.BLOCK.get(new Identifier(string)).method_9564();
				}

				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
					String string = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
					blockState2 = Registry.BLOCK.get(new Identifier(string)).method_9564();
				}
			}

			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("type")) {
				ChunkGeneratorType<?, ?> chunkGeneratorType6 = Registry.CHUNK_GENERATOR_TYPE
					.get(new Identifier(jsonObject.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString()));
				if (ChunkGeneratorType.field_12765 == chunkGeneratorType6) {
					CavesChunkGeneratorConfig cavesChunkGeneratorConfig = chunkGeneratorType3.method_12117();
					cavesChunkGeneratorConfig.setDefaultBlock(blockState);
					cavesChunkGeneratorConfig.setDefaultFluid(blockState2);
					return chunkGeneratorType3.create(this.world, biomeSource, cavesChunkGeneratorConfig);
				}

				if (ChunkGeneratorType.field_12770 == chunkGeneratorType6) {
					FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = chunkGeneratorType4.method_12117();
					floatingIslandsChunkGeneratorConfig.withCenter(new BlockPos(0, 64, 0));
					floatingIslandsChunkGeneratorConfig.setDefaultBlock(blockState);
					floatingIslandsChunkGeneratorConfig.setDefaultFluid(blockState2);
					return chunkGeneratorType4.create(this.world, biomeSource, floatingIslandsChunkGeneratorConfig);
				}
			}

			OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = chunkGeneratorType5.method_12117();
			overworldChunkGeneratorConfig.setDefaultBlock(blockState);
			overworldChunkGeneratorConfig.setDefaultFluid(blockState2);
			return chunkGeneratorType5.create(this.world, biomeSource, overworldChunkGeneratorConfig);
		}
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
		for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); i++) {
			for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); j++) {
				BlockPos blockPos = this.getTopSpawningBlockPosition(i, j, bl);
				if (blockPos != null) {
					return blockPos;
				}
			}
		}

		return null;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
		Biome biome = this.world.method_8310(mutable);
		BlockState blockState = biome.method_8722().getTopMaterial();
		if (bl && !blockState.getBlock().matches(BlockTags.field_15478)) {
			return null;
		} else {
			WorldChunk worldChunk = this.world.method_8497(i >> 4, j >> 4);
			int k = worldChunk.sampleHeightmap(Heightmap.Type.field_13197, i & 15, j & 15);
			if (k < 0) {
				return null;
			} else if (worldChunk.sampleHeightmap(Heightmap.Type.field_13202, i & 15, j & 15) > worldChunk.sampleHeightmap(Heightmap.Type.field_13200, i & 15, j & 15)) {
				return null;
			} else {
				for (int l = k + 1; l >= 0; l--) {
					mutable.set(i, l, j);
					BlockState blockState2 = this.world.method_8320(mutable);
					if (!blockState2.method_11618().isEmpty()) {
						break;
					}

					if (blockState2.equals(blockState)) {
						return mutable.up().toImmutable();
					}
				}

				return null;
			}
		}
	}

	@Override
	public float getSkyAngle(long l, float f) {
		double d = MathHelper.fractionalPart((double)l / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0F;
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d method_12445(float f, float g) {
		float h = MathHelper.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		float i = 0.7529412F;
		float j = 0.84705883F;
		float k = 1.0F;
		i *= h * 0.94F + 0.06F;
		j *= h * 0.94F + 0.06F;
		k *= h * 0.91F + 0.09F;
		return new Vec3d((double)i, (double)j, (double)k);
	}

	@Override
	public boolean canPlayersSleep() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderFog(int i, int j) {
		return false;
	}
}
