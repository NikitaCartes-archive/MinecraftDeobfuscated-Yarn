package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.TagHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeatures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSaveHandlerImpl implements ChunkSaveHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<ChunkPos, CompoundTag> chunkTagCache = Maps.<ChunkPos, CompoundTag>newHashMap();
	private final File file;
	private final DataFixer dataFixer;
	private FeatureUpdater featureUpdater;
	private boolean logIfAllChunksSaved;

	public ChunkSaveHandlerImpl(File file, DataFixer dataFixer) {
		this.file = file;
		this.dataFixer = dataFixer;
	}

	@Nullable
	private CompoundTag getChunkTag(IWorld iWorld, int i, int j) throws IOException {
		return this.getChunkTag(iWorld.getDimension().getType(), iWorld.getPersistentStateManager(), i, j);
	}

	@Nullable
	private CompoundTag getChunkTag(DimensionType dimensionType, @Nullable PersistentStateManager persistentStateManager, int i, int j) throws IOException {
		CompoundTag compoundTag = (CompoundTag)this.chunkTagCache.get(new ChunkPos(i, j));
		if (compoundTag != null) {
			return compoundTag;
		} else {
			DataInputStream dataInputStream = RegionFileCache.getChunkDataInputStream(this.file, i, j);
			if (dataInputStream == null) {
				return null;
			} else {
				CompoundTag compoundTag2 = NbtIo.read(dataInputStream);
				dataInputStream.close();
				int k = compoundTag2.containsKey("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
				if (k < 1493) {
					compoundTag2 = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag2, k, 1493);
					if (compoundTag2.getCompound("Level").getBoolean("hasLegacyStructureData")) {
						this.getFeatureUpdater(dimensionType, persistentStateManager);
						compoundTag2 = this.featureUpdater.getUpdatedReferences(compoundTag2);
					}
				}

				compoundTag2 = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag2, Math.max(1493, k));
				if (k < SharedConstants.getGameVersion().getWorldVersion()) {
					compoundTag2.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
					this.cacheChunkTag(new ChunkPos(i, j), compoundTag2);
				}

				return compoundTag2;
			}
		}
	}

	public void getFeatureUpdater(DimensionType dimensionType, @Nullable PersistentStateManager persistentStateManager) {
		if (this.featureUpdater == null) {
			this.featureUpdater = FeatureUpdater.create(dimensionType, persistentStateManager);
		}
	}

	@Nullable
	@Override
	public ProtoChunk readChunk(IWorld iWorld, int i, int j) {
		try {
			StructureManager structureManager = iWorld.getSaveHandler().getStructureManager();
			ChunkGenerator<?> chunkGenerator = iWorld.getChunkManager().getChunkGenerator();
			BiomeSource biomeSource = chunkGenerator.getBiomeSource();
			CompoundTag compoundTag = this.getChunkTag(iWorld, i, j);
			if (compoundTag == null) {
				return null;
			} else {
				ChunkStatus.ChunkType chunkType = this.getEntityStorageMode(compoundTag);
				boolean bl = compoundTag.containsKey("Level", 10) && compoundTag.getCompound("Level").containsKey("Status", 8);
				if (!bl) {
					LOGGER.error("Chunk file at {},{} is missing level data, skipping", i, j);
					return null;
				} else {
					CompoundTag compoundTag2 = compoundTag.getCompound("Level");
					int k = compoundTag2.getInt("xPos");
					int l = compoundTag2.getInt("zPos");
					if (k != i || l != j) {
						LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", i, j, i, j, k, l);
					}

					Biome[] biomes = new Biome[256];
					BlockPos.Mutable mutable = new BlockPos.Mutable();
					if (compoundTag2.containsKey("Biomes", 11)) {
						int[] is = compoundTag2.getIntArray("Biomes");

						for (int m = 0; m < is.length; m++) {
							biomes[m] = Registry.BIOME.getInt(is[m]);
							if (biomes[m] == null) {
								biomes[m] = biomeSource.getBiome(mutable.set((m & 15) + (i << 4), 0, (m >> 4 & 15) + (j << 4)));
							}
						}
					} else {
						for (int n = 0; n < biomes.length; n++) {
							biomes[n] = biomeSource.getBiome(mutable.set((n & 15) + (i << 4), 0, (n >> 4 & 15) + (j << 4)));
						}
					}

					UpgradeData upgradeData = compoundTag2.containsKey("UpgradeData", 10)
						? new UpgradeData(compoundTag2.getCompound("UpgradeData"))
						: UpgradeData.NO_UPGRADE_DATA;
					ChunkPos chunkPos = new ChunkPos(i, j);
					ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
						block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos, compoundTag2.getList("ToBeTicked", 9)
					);
					ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
						fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9)
					);
					boolean bl2 = compoundTag2.getBoolean("isLightOn");
					ListTag listTag = compoundTag2.getList("Sections", 10);
					ChunkSection[] chunkSections = readChunkSections(iWorld, i, j, listTag, bl2);
					long o = compoundTag2.getLong("InhabitedTime");
					Chunk chunk;
					if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
						chunk = new WorldChunk(
							iWorld.getWorld(),
							i,
							j,
							biomes,
							upgradeData,
							chunkTickScheduler,
							chunkTickScheduler2,
							o,
							chunkSections,
							worldChunk -> method_12386(compoundTag2, worldChunk)
						);
					} else {
						ProtoChunk protoChunk = new ProtoChunk(i, j, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
						chunk = protoChunk;
						protoChunk.setBiomeArray(biomes);
						protoChunk.setInhabitedTime(o);
						protoChunk.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
						if (!bl2 && protoChunk.getStatus().isAfter(ChunkStatus.LIGHT)) {
							for (BlockPos blockPos : BlockPos.iterateBoxPositions(i << 4, 0, j << 4, (i + 1 << 4) - 1, 255, (j + 1 << 4) - 1)) {
								if (chunk.getBlockState(blockPos).getLuminance() != 0) {
									protoChunk.addLightSource(blockPos);
								}
							}
						}
					}

					chunk.setLightOn(bl2);
					CompoundTag compoundTag3 = compoundTag2.getCompound("Heightmaps");
					EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

					for (Heightmap.Type type : Heightmap.Type.values()) {
						String string = type.getName();
						if (compoundTag3.containsKey(string, 12)) {
							if (chunkType == ChunkStatus.ChunkType.PROTOCHUNK || type.method_16136()) {
								chunk.setHeightmap(type, compoundTag3.getLongArray(string));
							}
						} else if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK && type.method_16136()) {
							enumSet.add(type);
						}
					}

					Heightmap.populateHeightmaps(chunk, enumSet);
					CompoundTag compoundTag4 = compoundTag2.getCompound("Structures");
					chunk.setStructureStarts(this.readStructureStarts(chunkGenerator, structureManager, biomeSource, compoundTag4));
					chunk.setStructureReferences(this.readStructureReferences(compoundTag4));
					if (compoundTag2.getBoolean("shouldSave")) {
						chunk.setShouldSave(true);
					}

					ListTag listTag2 = compoundTag2.getList("PostProcessing", 9);

					for (int p = 0; p < listTag2.size(); p++) {
						ListTag listTag3 = listTag2.getListTag(p);

						for (int q = 0; q < listTag3.size(); q++) {
							chunk.markBlockForPostProcessing(listTag3.getShort(q), p);
						}
					}

					if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
						return new ReadOnlyChunk((WorldChunk)chunk);
					} else {
						ProtoChunk protoChunk2 = (ProtoChunk)chunk;
						ListTag listTag3 = compoundTag2.getList("Entities", 10);

						for (int q = 0; q < listTag3.size(); q++) {
							protoChunk2.addEntity(listTag3.getCompoundTag(q));
						}

						ListTag listTag4 = compoundTag2.getList("TileEntities", 10);

						for (int r = 0; r < listTag4.size(); r++) {
							CompoundTag compoundTag5 = listTag4.getCompoundTag(r);
							chunk.addPendingBlockEntityTag(compoundTag5);
						}

						ListTag listTag5 = compoundTag2.getList("Lights", 9);

						for (int s = 0; s < listTag5.size(); s++) {
							ListTag listTag6 = listTag5.getListTag(s);

							for (int t = 0; t < listTag6.size(); t++) {
								protoChunk2.addLightSource(listTag6.getShort(t), s);
							}
						}

						CompoundTag compoundTag5 = compoundTag2.getCompound("CarvingMasks");

						for (String string2 : compoundTag5.getKeys()) {
							GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
							protoChunk2.setCarvingMask(carver, BitSet.valueOf(compoundTag5.getByteArray(string2)));
						}

						return protoChunk2;
					}
				}
			}
		} catch (CrashException var37) {
			throw var37;
		} catch (Exception var38) {
			LOGGER.error("Couldn't load chunk", (Throwable)var38);
			return null;
		}
	}

	@Override
	public void saveChunk(World world, Chunk chunk) throws IOException, SessionLockException {
		world.checkSessionLock();

		try {
			CompoundTag compoundTag = new CompoundTag();
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
			ChunkPos chunkPos = chunk.getPos();
			compoundTag.put("Level", compoundTag2);
			if (chunk.getStatus().getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
				CompoundTag compoundTag3 = this.getChunkTag(world, chunkPos.x, chunkPos.z);
				if (compoundTag3 != null && this.getEntityStorageMode(compoundTag3) == ChunkStatus.ChunkType.LEVELCHUNK) {
					return;
				}

				if (chunk.getStatus() == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
					return;
				}
			}

			int i = chunkPos.x;
			int j = chunkPos.z;
			compoundTag2.putInt("xPos", i);
			compoundTag2.putInt("zPos", j);
			compoundTag2.putLong("LastUpdate", world.getTime());
			compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
			compoundTag2.putString("Status", chunk.getStatus().getName());
			UpgradeData upgradeData = chunk.getUpgradeData();
			if (!upgradeData.method_12349()) {
				compoundTag2.put("UpgradeData", upgradeData.toTag());
			}

			ChunkSection[] chunkSections = chunk.getSectionArray();
			ListTag listTag = this.writeSectionsTag(world, i, j, chunkSections);
			compoundTag2.put("Sections", listTag);
			if (chunk.isLightOn()) {
				compoundTag2.putBoolean("isLightOn", true);
			}

			Biome[] biomes = chunk.getBiomeArray();
			int[] is = biomes != null ? new int[biomes.length] : new int[0];
			if (biomes != null) {
				for (int k = 0; k < biomes.length; k++) {
					is[k] = Registry.BIOME.getRawId(biomes[k]);
				}
			}

			compoundTag2.putIntArray("Biomes", is);
			ListTag listTag2 = new ListTag();

			for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
				BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag4 = new CompoundTag();
					blockEntity.toTag(compoundTag4);
					if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
						compoundTag4.putBoolean("keepPacked", false);
					}

					listTag2.add(compoundTag4);
				} else {
					CompoundTag compoundTag4 = chunk.getBlockEntityTagAt(blockPos);
					if (compoundTag4 != null) {
						if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
							compoundTag4.putBoolean("keepPacked", true);
						}

						listTag2.add(compoundTag4);
					}
				}
			}

			compoundTag2.put("TileEntities", listTag2);
			ListTag listTag3 = new ListTag();
			if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
				WorldChunk worldChunk = (WorldChunk)chunk;
				worldChunk.method_12232(false);

				for (int l = 0; l < worldChunk.getEntitySectionArray().length; l++) {
					for (Entity entity : worldChunk.getEntitySectionArray()[l]) {
						CompoundTag compoundTag5 = new CompoundTag();
						if (entity.saveToTag(compoundTag5)) {
							worldChunk.method_12232(true);
							listTag3.add(compoundTag5);
						}
					}
				}
			} else {
				ProtoChunk protoChunk = (ProtoChunk)chunk;
				listTag3.addAll(protoChunk.getEntities());

				for (BlockPos blockPos2 : chunk.getBlockEntityPositions()) {
					BlockEntity blockEntity2 = chunk.getBlockEntity(blockPos2);
					if (blockEntity2 != null) {
						CompoundTag compoundTag5 = new CompoundTag();
						blockEntity2.toTag(compoundTag5);
						listTag2.add(compoundTag5);
					} else {
						listTag2.add(chunk.getBlockEntityTagAt(blockPos2));
					}
				}

				compoundTag2.put("Lights", shortListsToNbt(protoChunk.method_12296()));
				CompoundTag compoundTag6 = new CompoundTag();

				for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
					compoundTag6.putByteArray(carver.toString(), chunk.getCarvingMask(carver).toByteArray());
				}

				compoundTag2.put("CarvingMasks", compoundTag6);
			}

			compoundTag2.put("Entities", listTag3);
			if (world.getBlockTickScheduler() instanceof ServerTickScheduler) {
				compoundTag2.put("TileTicks", ((ServerTickScheduler)world.getBlockTickScheduler()).toTag(chunkPos));
			}

			if (chunk.getBlockTickScheduler() instanceof ChunkTickScheduler) {
				compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)chunk.getBlockTickScheduler()).toNbt());
			}

			if (world.getFluidTickScheduler() instanceof ServerTickScheduler) {
				compoundTag2.put("LiquidTicks", ((ServerTickScheduler)world.getFluidTickScheduler()).toTag(chunkPos));
			}

			if (chunk.getFluidTickScheduler() instanceof ChunkTickScheduler) {
				compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)chunk.getFluidTickScheduler()).toNbt());
			}

			compoundTag2.put("PostProcessing", shortListsToNbt(chunk.getPostProcessingLists()));
			CompoundTag compoundTag7 = new CompoundTag();

			for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
				if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK || ((Heightmap.Type)entry.getKey()).method_16136()) {
					compoundTag7.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
				}
			}

			compoundTag2.put("Heightmaps", compoundTag7);
			compoundTag2.put("Structures", this.writeStructuresTag(i, j, chunk.getStructureStarts(), chunk.getStructureReferences()));
			this.cacheChunkTag(chunkPos, compoundTag);
		} catch (Exception var21) {
			LOGGER.error("Failed to save chunk", (Throwable)var21);
		}
	}

	private void cacheChunkTag(ChunkPos chunkPos, CompoundTag compoundTag) {
		this.chunkTagCache.put(chunkPos, compoundTag);
	}

	@Override
	public boolean saveNextChunk() {
		Iterator<Entry<ChunkPos, CompoundTag>> iterator = this.chunkTagCache.entrySet().iterator();
		if (!iterator.hasNext()) {
			if (this.logIfAllChunksSaved) {
				LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.file.getName());
			}

			return false;
		} else {
			Entry<ChunkPos, CompoundTag> entry = (Entry<ChunkPos, CompoundTag>)iterator.next();
			iterator.remove();
			ChunkPos chunkPos = (ChunkPos)entry.getKey();
			CompoundTag compoundTag = (CompoundTag)entry.getValue();
			if (compoundTag == null) {
				return true;
			} else {
				try {
					DataOutputStream dataOutputStream = RegionFileCache.getChunkDataOutputStream(this.file, chunkPos.x, chunkPos.z);
					NbtIo.write(compoundTag, dataOutputStream);
					dataOutputStream.close();
					if (this.featureUpdater != null) {
						this.featureUpdater.markResolved(chunkPos.toLong());
					}
				} catch (Exception var6) {
					LOGGER.error("Failed to save chunk", (Throwable)var6);
				}

				return true;
			}
		}
	}

	private ChunkStatus.ChunkType getEntityStorageMode(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			ChunkStatus chunkStatus = ChunkStatus.get(compoundTag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.PROTOCHUNK;
	}

	@Override
	public void saveAllChunks() {
		try {
			this.logIfAllChunksSaved = true;

			while (this.saveNextChunk()) {
			}
		} finally {
			this.logIfAllChunksSaved = false;
		}
	}

	public static void method_12386(CompoundTag compoundTag, WorldChunk worldChunk) {
		ListTag listTag = compoundTag.getList("Entities", 10);
		World world = worldChunk.getWorld();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			readEntityAndAddToChunk(compoundTag2, world, worldChunk);
			worldChunk.method_12232(true);
		}

		ListTag listTag2 = compoundTag.getList("TileEntities", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			CompoundTag compoundTag3 = listTag2.getCompoundTag(j);
			boolean bl = compoundTag3.getBoolean("keepPacked");
			if (bl) {
				worldChunk.addPendingBlockEntityTag(compoundTag3);
			} else {
				BlockEntity blockEntity = BlockEntity.createFromTag(compoundTag3);
				if (blockEntity != null) {
					worldChunk.addBlockEntity(blockEntity);
				}
			}
		}

		if (compoundTag.containsKey("TileTicks", 9) && world.getBlockTickScheduler() instanceof ServerTickScheduler) {
			((ServerTickScheduler)world.getBlockTickScheduler()).fromTag(compoundTag.getList("TileTicks", 10));
		}

		if (compoundTag.containsKey("LiquidTicks", 9) && world.getFluidTickScheduler() instanceof ServerTickScheduler) {
			((ServerTickScheduler)world.getFluidTickScheduler()).fromTag(compoundTag.getList("LiquidTicks", 10));
		}
	}

	private ListTag writeSectionsTag(World world, int i, int j, ChunkSection[] chunkSections) {
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();

		for (int k = -1; k < 17; k++) {
			int l = k;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == l)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK_LIGHT).getChunkLightArray(i, l, j);
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY_LIGHT).getChunkLightArray(i, l, j);
			if (chunkSection != WorldChunk.EMPTY_SECTION || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Y", (byte)(l & 0xFF));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().write(compoundTag, "Palette", "BlockStates");
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					compoundTag.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					compoundTag.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	private static ChunkSection[] readChunkSections(IWorld iWorld, int i, int j, ListTag listTag, boolean bl) {
		int k = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = iWorld.getDimension().hasSkyLight();
		ChunkManager chunkManager = iWorld.getChunkManager();

		for (int l = 0; l < listTag.size(); l++) {
			CompoundTag compoundTag = listTag.getCompoundTag(l);
			int m = compoundTag.getByte("Y");
			if (compoundTag.containsKey("Palette", 9) && compoundTag.containsKey("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(m << 4);
				chunkSection.getContainer().read(compoundTag.getList("Palette", 10), compoundTag.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				chunkSections[m] = chunkSection;
			}

			if (bl) {
				if (compoundTag.containsKey("BlockLight", 7)) {
					chunkManager.getLightingProvider().setSection(LightType.BLOCK_LIGHT, i, m, j, new ChunkNibbleArray(compoundTag.getByteArray("BlockLight")));
				}

				if (bl2 && compoundTag.containsKey("SkyLight", 7)) {
					chunkManager.getLightingProvider().setSection(LightType.SKY_LIGHT, i, m, j, new ChunkNibbleArray(compoundTag.getByteArray("SkyLight")));
				}
			}
		}

		return chunkSections;
	}

	private CompoundTag writeStructuresTag(int i, int j, Map<String, StructureStart> map, Map<String, LongSet> map2) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<String, StructureStart> entry : map.entrySet()) {
			compoundTag2.put((String)entry.getKey(), ((StructureStart)entry.getValue()).toTag(i, j));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			compoundTag3.put((String)entry2.getKey(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private Map<String, StructureStart> readStructureStarts(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag
	) {
		Map<String, StructureStart> map = Maps.<String, StructureStart>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("Starts");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, StructureFeatures.method_14842(chunkGenerator, structureManager, biomeSource, compoundTag2.getCompound(string)));
		}

		return map;
	}

	private Map<String, LongSet> readStructureReferences(CompoundTag compoundTag) {
		Map<String, LongSet> map = Maps.<String, LongSet>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("References");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, new LongOpenHashSet(compoundTag2.getLongArray(string)));
		}

		return map;
	}

	public static ListTag shortListsToNbt(ShortList[] shortLists) {
		ListTag listTag = new ListTag();

		for (ShortList shortList : shortLists) {
			ListTag listTag2 = new ListTag();
			if (shortList != null) {
				for (Short short_ : shortList) {
					listTag2.add(new ShortTag(short_));
				}
			}

			listTag.add(listTag2);
		}

		return listTag;
	}

	@Nullable
	private static Entity readEntity(CompoundTag compoundTag, World world, Function<Entity, Entity> function) {
		Entity entity = readEntityWithoutPassengers(compoundTag, world);
		if (entity == null) {
			return null;
		} else {
			entity = (Entity)function.apply(entity);
			if (entity != null && compoundTag.containsKey("Passengers", 9)) {
				ListTag listTag = compoundTag.getList("Passengers", 10);

				for (int i = 0; i < listTag.size(); i++) {
					Entity entity2 = readEntity(listTag.getCompoundTag(i), world, function);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}
	}

	@Nullable
	public static Entity readEntityAndAddToChunk(CompoundTag compoundTag, World world, WorldChunk worldChunk) {
		return readEntity(compoundTag, world, entity -> {
			worldChunk.addEntity(entity);
			return entity;
		});
	}

	@Nullable
	public static Entity readEntity(CompoundTag compoundTag, World world, double d, double e, double f, boolean bl) {
		return readEntity(compoundTag, world, entity -> {
			entity.setPositionAndAngles(d, e, f, entity.yaw, entity.pitch);
			return bl && !world.spawnEntity(entity) ? null : entity;
		});
	}

	@Nullable
	public static Entity readEntity(CompoundTag compoundTag, World world, boolean bl) {
		return readEntity(compoundTag, world, entity -> bl && !world.spawnEntity(entity) ? null : entity);
	}

	@Nullable
	protected static Entity readEntityWithoutPassengers(CompoundTag compoundTag, World world) {
		try {
			return EntityType.fromTag(compoundTag, world);
		} catch (RuntimeException var3) {
			LOGGER.warn("Exception loading entity: ", (Throwable)var3);
			return null;
		}
	}

	public static void spawnEntityAndPassengers(Entity entity, IWorld iWorld) {
		if (iWorld.spawnEntity(entity) && entity.hasPassengers()) {
			for (Entity entity2 : entity.getPassengerList()) {
				spawnEntityAndPassengers(entity2, iWorld);
			}
		}
	}

	public boolean upgradeChunk(ChunkPos chunkPos, DimensionType dimensionType, PersistentStateManager persistentStateManager) {
		boolean bl = false;

		try {
			this.getChunkTag(dimensionType, persistentStateManager, chunkPos.x, chunkPos.z);

			while (this.saveNextChunk()) {
				bl = true;
			}
		} catch (IOException var6) {
		}

		return bl;
	}

	@Override
	public void close() {
		while (this.saveNextChunk()) {
		}
	}
}
