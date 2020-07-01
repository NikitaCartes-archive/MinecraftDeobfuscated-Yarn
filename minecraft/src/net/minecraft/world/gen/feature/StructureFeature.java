package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> {
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = HashBiMap.create();
	private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.<StructureFeature<?>, GenerationStep.Feature>newHashMap();
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<DefaultFeatureConfig> PILLAGER_OUTPOST = register(
		"Pillager_Outpost", new PillagerOutpostFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = register(
		"Mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> MANSION = register(
		"Mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = register(
		"Jungle_Pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = register(
		"Desert_Pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> IGLOO = register(
		"Igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = register(
		"Ruined_Portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = register(
		"Shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final SwampHutFeature SWAMP_HUT = register(
		"Swamp_Hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = register(
		"Stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS
	);
	public static final StructureFeature<DefaultFeatureConfig> MONUMENT = register(
		"Monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = register(
		"Ocean_Ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> FORTRESS = register(
		"Fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<DefaultFeatureConfig> END_CITY = register(
		"EndCity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = register(
		"Buried_Treasure", new BuriedTreasureFeature(BuriedTreasureFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = register(
		"Village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> NETHER_FOSSIL = register(
		"Nether_Fossil", new NetherFossilFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<BastionRemnantFeatureConfig> BASTION_REMNANT = register(
		"Bastion_Remnant", new BastionRemnantFeature(BastionRemnantFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final List<StructureFeature<?>> field_24861 = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
	private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;

	private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
		STRUCTURES.put(name.toLowerCase(Locale.ROOT), structureFeature);
		STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
	}

	public StructureFeature(Codec<C> codec) {
		this.codec = codec.fieldOf("config")
			.<ConfiguredStructureFeature<C, StructureFeature<C>>>xmap(
				featureConfig -> new ConfiguredStructureFeature<>(this, (C)featureConfig), configuredStructureFeature -> configuredStructureFeature.config
			)
			.codec();
	}

	public GenerationStep.Feature method_28663() {
		return (GenerationStep.Feature)STRUCTURE_TO_GENERATION_STEP.get(this);
	}

	public static void method_28664() {
	}

	@Nullable
	public static StructureStart<?> method_28660(StructureManager structureManager, CompoundTag compoundTag, long l) {
		String string = compoundTag.getString("id");
		if ("INVALID".equals(string)) {
			return StructureStart.DEFAULT;
		} else {
			StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
			if (structureFeature == null) {
				LOGGER.error("Unknown feature id: {}", string);
				return null;
			} else {
				int i = compoundTag.getInt("ChunkX");
				int j = compoundTag.getInt("ChunkZ");
				int k = compoundTag.getInt("references");
				BlockBox blockBox = compoundTag.contains("BB") ? new BlockBox(compoundTag.getIntArray("BB")) : BlockBox.empty();
				ListTag listTag = compoundTag.getList("Children", 10);

				try {
					StructureStart<?> structureStart = structureFeature.method_28656(i, j, blockBox, k, l);

					for (int m = 0; m < listTag.size(); m++) {
						CompoundTag compoundTag2 = listTag.getCompound(m);
						String string2 = compoundTag2.getString("id");
						StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
						if (structurePieceType == null) {
							LOGGER.error("Unknown structure piece id: {}", string2);
						} else {
							try {
								StructurePiece structurePiece = structurePieceType.load(structureManager, compoundTag2);
								structureStart.getChildren().add(structurePiece);
							} catch (Exception var17) {
								LOGGER.error("Exception loading structure piece with id {}", string2, var17);
							}
						}
					}

					return structureStart;
				} catch (Exception var18) {
					LOGGER.error("Failed Start with id {}", string, var18);
					return null;
				}
			}
		}
	}

	public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
		return this.codec;
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config) {
		return new ConfiguredStructureFeature<>(this, config);
	}

	@Nullable
	public BlockPos locateStructure(
		WorldView worldView, StructureAccessor structureAccessor, BlockPos blockPos, int i, boolean skipExistingChunks, long l, StructureConfig structureConfig
	) {
		int j = structureConfig.getSpacing();
		int k = blockPos.getX() >> 4;
		int m = blockPos.getZ() >> 4;
		int n = 0;

		for (ChunkRandom chunkRandom = new ChunkRandom(); n <= i; n++) {
			for (int o = -n; o <= n; o++) {
				boolean bl = o == -n || o == n;

				for (int p = -n; p <= n; p++) {
					boolean bl2 = p == -n || p == n;
					if (bl || bl2) {
						int q = k + j * o;
						int r = m + j * p;
						ChunkPos chunkPos = this.method_27218(structureConfig, l, chunkRandom, q, r);
						Chunk chunk = worldView.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
						StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), this, chunk);
						if (structureStart != null && structureStart.hasChildren()) {
							if (skipExistingChunks && structureStart.isInExistingChunk()) {
								structureStart.incrementReferences();
								return structureStart.getPos();
							}

							if (!skipExistingChunks) {
								return structureStart.getPos();
							}
						}

						if (n == 0) {
							break;
						}
					}
				}

				if (n == 0) {
					break;
				}
			}
		}

		return null;
	}

	protected boolean method_27219() {
		return true;
	}

	public final ChunkPos method_27218(StructureConfig structureConfig, long l, ChunkRandom chunkRandom, int i, int j) {
		int k = structureConfig.getSpacing();
		int m = structureConfig.getSeparation();
		int n = Math.floorDiv(i, k);
		int o = Math.floorDiv(j, k);
		chunkRandom.setRegionSeed(l, n, o, structureConfig.getSalt());
		int p;
		int q;
		if (this.method_27219()) {
			p = chunkRandom.nextInt(k - m);
			q = chunkRandom.nextInt(k - m);
		} else {
			p = (chunkRandom.nextInt(k - m) + chunkRandom.nextInt(k - m)) / 2;
			q = (chunkRandom.nextInt(k - m) + chunkRandom.nextInt(k - m)) / 2;
		}

		return new ChunkPos(n * k + p, o * k + q);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, C featureConfig
	) {
		return true;
	}

	private StructureStart<C> method_28656(int i, int j, BlockBox blockBox, int k, long l) {
		return this.getStructureStartFactory().create(this, i, j, blockBox, k, l);
	}

	public StructureStart<?> method_28657(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long l,
		ChunkPos chunkPos,
		Biome biome,
		int i,
		ChunkRandom chunkRandom,
		StructureConfig structureConfig,
		C featureConfig
	) {
		ChunkPos chunkPos2 = this.method_27218(structureConfig, l, chunkRandom, chunkPos.x, chunkPos.z);
		if (chunkPos.x == chunkPos2.x
			&& chunkPos.z == chunkPos2.z
			&& this.shouldStartAt(chunkGenerator, biomeSource, l, chunkRandom, chunkPos.x, chunkPos.z, biome, chunkPos2, featureConfig)) {
			StructureStart<C> structureStart = this.method_28656(chunkPos.x, chunkPos.z, BlockBox.empty(), i, l);
			structureStart.init(chunkGenerator, structureManager, chunkPos.x, chunkPos.z, biome, featureConfig);
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public abstract StructureFeature.StructureStartFactory<C> getStructureStartFactory();

	public String getName() {
		return (String)STRUCTURES.inverse().get(this);
	}

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return Collections.emptyList();
	}

	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return Collections.emptyList();
	}

	public interface StructureStartFactory<C extends FeatureConfig> {
		StructureStart<C> create(StructureFeature<C> feature, int x, int z, BlockBox box, int i, long l);
	}
}
