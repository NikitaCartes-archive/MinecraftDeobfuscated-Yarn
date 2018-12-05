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
import net.minecraft.class_2804;
import net.minecraft.class_2839;
import net.minecraft.class_2843;
import net.minecraft.class_3360;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.class_37;
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
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.MinecraftException;
import net.minecraft.util.TagHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
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
	private static final Logger field_13001 = LogManager.getLogger();
	private final Map<ChunkPos, CompoundTag> field_13000 = Maps.<ChunkPos, CompoundTag>newHashMap();
	private final File field_12999;
	private final DataFixer field_13002;
	private class_3360 field_13003;
	private boolean field_12998;

	public ChunkSaveHandlerImpl(File file, DataFixer dataFixer) {
		this.field_12999 = file;
		this.field_13002 = dataFixer;
	}

	@Nullable
	private CompoundTag method_12389(IWorld iWorld, int i, int j) throws IOException {
		return this.method_12398(iWorld.getDimension().getType(), iWorld.method_8646(), i, j);
	}

	@Nullable
	private CompoundTag method_12398(DimensionType dimensionType, @Nullable class_37 arg, int i, int j) throws IOException {
		CompoundTag compoundTag = (CompoundTag)this.field_13000.get(new ChunkPos(i, j));
		if (compoundTag != null) {
			return compoundTag;
		} else {
			DataInputStream dataInputStream = RegionFileCache.getChunkDataInputStream(this.field_12999, i, j);
			if (dataInputStream == null) {
				return null;
			} else {
				CompoundTag compoundTag2 = NbtIo.read(dataInputStream);
				dataInputStream.close();
				int k = compoundTag2.containsKey("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
				if (k < 1493) {
					compoundTag2 = TagHelper.update(this.field_13002, DataFixTypes.CHUNK, compoundTag2, k, 1493);
					if (compoundTag2.getCompound("Level").getBoolean("hasLegacyStructureData")) {
						this.method_12380(dimensionType, arg);
						compoundTag2 = this.field_13003.method_14735(compoundTag2);
					}
				}

				compoundTag2 = TagHelper.update(this.field_13002, DataFixTypes.CHUNK, compoundTag2, Math.max(1493, k));
				if (k < SharedConstants.getGameVersion().getWorldVersion()) {
					compoundTag2.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
					this.method_12390(new ChunkPos(i, j), compoundTag2);
				}

				return compoundTag2;
			}
		}
	}

	public void method_12380(DimensionType dimensionType, @Nullable class_37 arg) {
		if (this.field_13003 == null) {
			this.field_13003 = class_3360.method_14745(dimensionType, arg);
		}
	}

	@Nullable
	@Override
	public class_2839 method_12411(IWorld iWorld, int i, int j) {
		try {
			class_3485 lv = iWorld.getSaveHandler().method_134();
			ChunkGenerator<?> chunkGenerator = iWorld.getChunkManager().getChunkGenerator();
			BiomeSource biomeSource = chunkGenerator.getBiomeSource();
			CompoundTag compoundTag = this.method_12389(iWorld, i, j);
			if (compoundTag == null) {
				return null;
			} else {
				ChunkStatus.class_2808 lv2 = this.method_12377(compoundTag);
				boolean bl = compoundTag.containsKey("Level", 10) && compoundTag.getCompound("Level").containsKey("Status", 8);
				if (!bl) {
					field_13001.error("Chunk file at {},{} is missing level data, skipping", i, j);
					return null;
				} else {
					CompoundTag compoundTag2 = compoundTag.getCompound("Level");
					int k = compoundTag2.getInt("xPos");
					int l = compoundTag2.getInt("zPos");
					if (k != i || l != j) {
						field_13001.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", i, j, i, j, k, l);
					}

					Biome[] biomes = new Biome[256];
					BlockPos.Mutable mutable = new BlockPos.Mutable();
					if (compoundTag2.containsKey("Biomes", 11)) {
						int[] is = compoundTag2.getIntArray("Biomes");

						for (int m = 0; m < is.length; m++) {
							biomes[m] = Registry.BIOME.getInt(is[m]);
							if (biomes[m] == null) {
								biomes[m] = biomeSource.method_8758(mutable.set((m & 15) + (i << 4), 0, (m >> 4 & 15) + (j << 4)));
							}
						}
					} else {
						for (int n = 0; n < biomes.length; n++) {
							biomes[n] = biomeSource.method_8758(mutable.set((n & 15) + (i << 4), 0, (n >> 4 & 15) + (j << 4)));
						}
					}

					class_2843 lv3 = compoundTag2.containsKey("UpgradeData", 10) ? new class_2843(compoundTag2.getCompound("UpgradeData")) : class_2843.field_12950;
					ChunkPos chunkPos = new ChunkPos(i, j);
					ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
						block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos, compoundTag2.getList("ToBeTicked", 9)
					);
					ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
						fluid -> fluid == null || fluid == Fluids.field_15906, Registry.FLUID::getId, Registry.FLUID::get, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9)
					);
					boolean bl2 = compoundTag2.getBoolean("isLightOn");
					ListTag listTag = compoundTag2.getList("Sections", 10);
					ChunkSection[] chunkSections = method_12384(iWorld, i, j, listTag, bl2);
					long o = compoundTag2.getLong("InhabitedTime");
					Chunk chunk;
					if (lv2 == ChunkStatus.class_2808.field_12807) {
						chunk = new WorldChunk(
							iWorld.method_8410(), i, j, biomes, lv3, chunkTickScheduler, chunkTickScheduler2, o, chunkSections, worldChunk -> method_12386(compoundTag2, worldChunk)
						);
					} else {
						class_2839 lv4 = new class_2839(i, j, lv3, chunkSections, chunkTickScheduler, chunkTickScheduler2);
						chunk = lv4;
						lv4.setBiomes(biomes);
						lv4.method_12028(o);
						lv4.method_12308(ChunkStatus.get(compoundTag2.getString("Status")));
						if (!bl2 && lv4.getStatus().isAfter(ChunkStatus.field_12805)) {
							for (BlockPos blockPos : BlockPos.Mutable.iterateBoxPositions(i << 4, 0, j << 4, (i + 1 << 4) - 1, 255, (j + 1 << 4) - 1)) {
								if (chunk.getBlockState(blockPos).getLuminance() != 0) {
									lv4.method_12315(blockPos);
								}
							}
						}
					}

					chunk.method_12020(bl2);
					CompoundTag compoundTag3 = compoundTag2.getCompound("Heightmaps");
					EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

					for (Heightmap.Type type : Heightmap.Type.values()) {
						String string = type.getName();
						if (compoundTag3.containsKey(string, 12)) {
							if (lv2 == ChunkStatus.class_2808.field_12808 || type.method_16136()) {
								chunk.method_12037(type, compoundTag3.getLongArray(string));
							}
						} else if (lv2 == ChunkStatus.class_2808.field_12807 && type.method_16136()) {
							enumSet.add(type);
						}
					}

					Heightmap.method_16684(chunk, enumSet);
					CompoundTag compoundTag4 = compoundTag2.getCompound("Structures");
					chunk.method_12034(this.method_12392(chunkGenerator, lv, biomeSource, compoundTag4));
					chunk.method_12183(this.method_12387(compoundTag4));
					if (compoundTag2.getBoolean("shouldSave")) {
						chunk.method_12008(true);
					}

					ListTag listTag2 = compoundTag2.getList("PostProcessing", 9);

					for (int p = 0; p < listTag2.size(); p++) {
						ListTag listTag3 = listTag2.getListTag(p);

						for (int q = 0; q < listTag3.size(); q++) {
							chunk.method_12029(listTag3.getShort(q), p);
						}
					}

					if (lv2 == ChunkStatus.class_2808.field_12807) {
						return new ReadOnlyChunk((WorldChunk)chunk);
					} else {
						class_2839 lv5 = (class_2839)chunk;
						ListTag listTag3 = compoundTag2.getList("Entities", 10);

						for (int q = 0; q < listTag3.size(); q++) {
							lv5.method_12302(listTag3.getCompoundTag(q));
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
								lv5.method_12304(listTag6.getShort(t), s);
							}
						}

						CompoundTag compoundTag5 = compoundTag2.getCompound("CarvingMasks");

						for (String string2 : compoundTag5.getKeys()) {
							GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
							lv5.method_12307(carver, BitSet.valueOf(compoundTag5.getByteArray(string2)));
						}

						return lv5;
					}
				}
			}
		} catch (CrashException var37) {
			throw var37;
		} catch (Exception var38) {
			field_13001.error("Couldn't load chunk", (Throwable)var38);
			return null;
		}
	}

	@Override
	public void method_12410(World world, Chunk chunk) throws IOException, MinecraftException {
		world.checkSessionLock();

		try {
			CompoundTag compoundTag = new CompoundTag();
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
			ChunkPos chunkPos = chunk.getPos();
			compoundTag.put("Level", compoundTag2);
			if (chunk.getStatus().method_12164() != ChunkStatus.class_2808.field_12807) {
				CompoundTag compoundTag3 = this.method_12389(world, chunkPos.x, chunkPos.z);
				if (compoundTag3 != null && this.method_12377(compoundTag3) == ChunkStatus.class_2808.field_12807) {
					return;
				}

				if (chunk.getStatus() == ChunkStatus.field_12798 && chunk.method_12016().values().stream().noneMatch(class_3449::hasChildren)) {
					return;
				}
			}

			int i = chunkPos.x;
			int j = chunkPos.z;
			compoundTag2.putInt("xPos", i);
			compoundTag2.putInt("zPos", j);
			compoundTag2.putLong("LastUpdate", world.getTime());
			compoundTag2.putLong("InhabitedTime", chunk.method_12033());
			compoundTag2.putString("Status", chunk.getStatus().getName());
			class_2843 lv = chunk.method_12003();
			if (!lv.method_12349()) {
				compoundTag2.put("UpgradeData", lv.method_12350());
			}

			ChunkSection[] chunkSections = chunk.getSectionArray();
			ListTag listTag = this.method_12395(world, i, j, chunkSections);
			compoundTag2.put("Sections", listTag);
			if (chunk.method_12038()) {
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

			for (BlockPos blockPos : chunk.method_12021()) {
				BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag4 = new CompoundTag();
					blockEntity.toTag(compoundTag4);
					if (chunk.getStatus().method_12164() == ChunkStatus.class_2808.field_12807) {
						compoundTag4.putBoolean("keepPacked", false);
					}

					listTag2.add((Tag)compoundTag4);
				} else {
					CompoundTag compoundTag4 = chunk.method_12024(blockPos);
					if (compoundTag4 != null) {
						if (chunk.getStatus().method_12164() == ChunkStatus.class_2808.field_12807) {
							compoundTag4.putBoolean("keepPacked", true);
						}

						listTag2.add((Tag)compoundTag4);
					}
				}
			}

			compoundTag2.put("TileEntities", listTag2);
			ListTag listTag3 = new ListTag();
			if (chunk.getStatus().method_12164() == ChunkStatus.class_2808.field_12807) {
				WorldChunk worldChunk = (WorldChunk)chunk;
				worldChunk.method_12232(false);

				for (int l = 0; l < worldChunk.getEntitySectionArray().length; l++) {
					for (Entity entity : worldChunk.getEntitySectionArray()[l]) {
						CompoundTag compoundTag5 = new CompoundTag();
						if (entity.saveToTag(compoundTag5)) {
							worldChunk.method_12232(true);
							listTag3.add((Tag)compoundTag5);
						}
					}
				}
			} else {
				class_2839 lv2 = (class_2839)chunk;
				listTag3.addAll(lv2.method_12295());

				for (BlockPos blockPos2 : chunk.method_12021()) {
					BlockEntity blockEntity2 = chunk.getBlockEntity(blockPos2);
					if (blockEntity2 != null) {
						CompoundTag compoundTag5 = new CompoundTag();
						blockEntity2.toTag(compoundTag5);
						listTag2.add((Tag)compoundTag5);
					} else {
						listTag2.add((Tag)chunk.method_12024(blockPos2));
					}
				}

				compoundTag2.put("Lights", shortListsToNbt(lv2.method_12296()));
				CompoundTag compoundTag6 = new CompoundTag();

				for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
					compoundTag6.putByteArray(carver.toString(), chunk.method_12025(carver).toByteArray());
				}

				compoundTag2.put("CarvingMasks", compoundTag6);
			}

			compoundTag2.put("Entities", listTag3);
			if (world.getBlockTickScheduler() instanceof ServerTickScheduler) {
				compoundTag2.put("TileTicks", ((ServerTickScheduler)world.getBlockTickScheduler()).toTag(chunkPos));
			}

			if (chunk.method_12013() instanceof ChunkTickScheduler) {
				compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)chunk.method_12013()).toNbt());
			}

			if (world.getFluidTickScheduler() instanceof ServerTickScheduler) {
				compoundTag2.put("LiquidTicks", ((ServerTickScheduler)world.getFluidTickScheduler()).toTag(chunkPos));
			}

			if (chunk.method_12014() instanceof ChunkTickScheduler) {
				compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)chunk.method_12014()).toNbt());
			}

			compoundTag2.put("PostProcessing", shortListsToNbt(chunk.method_12012()));
			CompoundTag compoundTag7 = new CompoundTag();

			for (Entry<Heightmap.Type, Heightmap> entry : chunk.method_12011()) {
				if (chunk.getStatus().method_12164() == ChunkStatus.class_2808.field_12808 || ((Heightmap.Type)entry.getKey()).method_16136()) {
					compoundTag7.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
				}
			}

			compoundTag2.put("Heightmaps", compoundTag7);
			compoundTag2.put("Structures", this.method_12385(i, j, chunk.method_12016(), chunk.method_12179()));
			this.method_12390(chunkPos, compoundTag);
		} catch (Exception var21) {
			field_13001.error("Failed to save chunk", (Throwable)var21);
		}
	}

	private void method_12390(ChunkPos chunkPos, CompoundTag compoundTag) {
		this.field_13000.put(chunkPos, compoundTag);
	}

	@Override
	public boolean method_12412() {
		Iterator<Entry<ChunkPos, CompoundTag>> iterator = this.field_13000.entrySet().iterator();
		if (!iterator.hasNext()) {
			if (this.field_12998) {
				field_13001.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.field_12999.getName());
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
					DataOutputStream dataOutputStream = RegionFileCache.getChunkDataOutputStream(this.field_12999, chunkPos.x, chunkPos.z);
					NbtIo.write(compoundTag, dataOutputStream);
					dataOutputStream.close();
					if (this.field_13003 != null) {
						this.field_13003.method_14744(chunkPos.toLong());
					}
				} catch (Exception var6) {
					field_13001.error("Failed to save chunk", (Throwable)var6);
				}

				return true;
			}
		}
	}

	private ChunkStatus.class_2808 method_12377(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			ChunkStatus chunkStatus = ChunkStatus.get(compoundTag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.method_12164();
			}
		}

		return ChunkStatus.class_2808.field_12808;
	}

	@Override
	public void save() {
		try {
			this.field_12998 = true;

			while (this.method_12412()) {
			}
		} finally {
			this.field_12998 = false;
		}
	}

	public static void method_12386(CompoundTag compoundTag, WorldChunk worldChunk) {
		ListTag listTag = compoundTag.getList("Entities", 10);
		World world = worldChunk.getWorld();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			method_12383(compoundTag2, world, worldChunk);
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

	private ListTag method_12395(World world, int i, int j, ChunkSection[] chunkSections) {
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();

		for (int k = -1; k < 17; k++) {
			int l = k;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == l)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			class_2804 lv = lightingProvider.get(LightType.field_9282).method_15544(i, l, j);
			class_2804 lv2 = lightingProvider.get(LightType.field_9284).method_15544(i, l, j);
			if (chunkSection != WorldChunk.EMPTY_SECTION || lv != null || lv2 != null) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Y", (byte)(l & 0xFF));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().method_12330(compoundTag, "Palette", "BlockStates");
				}

				if (lv != null && !lv.method_12146()) {
					compoundTag.putByteArray("BlockLight", lv.method_12137());
				}

				if (lv2 != null && !lv2.method_12146()) {
					compoundTag.putByteArray("SkyLight", lv2.method_12137());
				}

				listTag.add((Tag)compoundTag);
			}
		}

		return listTag;
	}

	private static ChunkSection[] method_12384(IWorld iWorld, int i, int j, ListTag listTag, boolean bl) {
		int k = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = iWorld.getDimension().method_12451();
		ChunkManager chunkManager = iWorld.getChunkManager();

		for (int l = 0; l < listTag.size(); l++) {
			CompoundTag compoundTag = listTag.getCompoundTag(l);
			int m = compoundTag.getByte("Y");
			if (compoundTag.containsKey("Palette", 9) && compoundTag.containsKey("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(m << 4);
				chunkSection.getContainer().method_12329(compoundTag.getList("Palette", 10), compoundTag.getLongArray("BlockStates"));
				chunkSection.method_12253();
				chunkSections[m] = chunkSection;
			}

			if (bl) {
				if (compoundTag.containsKey("BlockLight", 7)) {
					chunkManager.getLightingProvider().method_15558(LightType.field_9282, i, m, j, new class_2804(compoundTag.getByteArray("BlockLight")));
				}

				if (bl2 && compoundTag.containsKey("SkyLight", 7)) {
					chunkManager.getLightingProvider().method_15558(LightType.field_9284, i, m, j, new class_2804(compoundTag.getByteArray("SkyLight")));
				}
			}
		}

		return chunkSections;
	}

	private CompoundTag method_12385(int i, int j, Map<String, class_3449> map, Map<String, LongSet> map2) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<String, class_3449> entry : map.entrySet()) {
			compoundTag2.put((String)entry.getKey(), ((class_3449)entry.getValue()).method_14972(i, j));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			compoundTag3.put((String)entry2.getKey(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private Map<String, class_3449> method_12392(ChunkGenerator<?> chunkGenerator, class_3485 arg, BiomeSource biomeSource, CompoundTag compoundTag) {
		Map<String, class_3449> map = Maps.<String, class_3449>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("Starts");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, StructureFeatures.method_14842(chunkGenerator, arg, biomeSource, compoundTag2.getCompound(string)));
		}

		return map;
	}

	private Map<String, LongSet> method_12387(CompoundTag compoundTag) {
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
					listTag2.add((Tag)(new ShortTag(short_)));
				}
			}

			listTag.add((Tag)listTag2);
		}

		return listTag;
	}

	@Nullable
	private static Entity method_12382(CompoundTag compoundTag, World world, Function<Entity, Entity> function) {
		Entity entity = method_12379(compoundTag, world);
		if (entity == null) {
			return null;
		} else {
			entity = (Entity)function.apply(entity);
			if (entity != null && compoundTag.containsKey("Passengers", 9)) {
				ListTag listTag = compoundTag.getList("Passengers", 10);

				for (int i = 0; i < listTag.size(); i++) {
					Entity entity2 = method_12382(listTag.getCompoundTag(i), world, function);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}
	}

	@Nullable
	public static Entity method_12383(CompoundTag compoundTag, World world, WorldChunk worldChunk) {
		return method_12382(compoundTag, world, entity -> {
			worldChunk.addEntity(entity);
			return entity;
		});
	}

	@Nullable
	public static Entity method_12399(CompoundTag compoundTag, World world, double d, double e, double f, boolean bl) {
		return method_12382(compoundTag, world, entity -> {
			entity.setPositionAndAngles(d, e, f, entity.yaw, entity.pitch);
			return bl && !world.spawnEntity(entity) ? null : entity;
		});
	}

	@Nullable
	public static Entity method_12378(CompoundTag compoundTag, World world, boolean bl) {
		return method_12382(compoundTag, world, entity -> bl && !world.spawnEntity(entity) ? null : entity);
	}

	@Nullable
	protected static Entity method_12379(CompoundTag compoundTag, World world) {
		try {
			return EntityType.fromTag(compoundTag, world);
		} catch (RuntimeException var3) {
			field_13001.warn("Exception loading entity: ", (Throwable)var3);
			return null;
		}
	}

	public static void method_12394(Entity entity, IWorld iWorld) {
		if (iWorld.spawnEntity(entity) && entity.hasPassengers()) {
			for (Entity entity2 : entity.getPassengerList()) {
				method_12394(entity2, iWorld);
			}
		}
	}

	public boolean method_12375(ChunkPos chunkPos, DimensionType dimensionType, class_37 arg) {
		boolean bl = false;

		try {
			this.method_12398(dimensionType, arg, chunkPos.x, chunkPos.z);

			while (this.method_12412()) {
				bl = true;
			}
		} catch (IOException var6) {
		}

		return bl;
	}

	@Override
	public void close() {
		while (this.method_12412()) {
		}
	}
}
