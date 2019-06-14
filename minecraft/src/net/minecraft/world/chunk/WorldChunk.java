package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk implements Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ChunkSection field_12852 = null;
	private final ChunkSection[] field_12840 = new ChunkSection[16];
	private final Biome[] biomeArray;
	private final Map<BlockPos, CompoundTag> pendingBlockEntityTags = Maps.<BlockPos, CompoundTag>newHashMap();
	private boolean loadedToWorld;
	private final World world;
	private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
	private final UpgradeData field_12849;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.<BlockPos, BlockEntity>newHashMap();
	private final TypeFilterableList<Entity>[] entitySections;
	private final Map<String, StructureStart> structureStarts = Maps.<String, StructureStart>newHashMap();
	private final Map<String, LongSet> structureReferences = Maps.<String, LongSet>newHashMap();
	private final ShortList[] postProcessingLists = new ShortList[16];
	private TickScheduler<Block> blockTickScheduler;
	private TickScheduler<Fluid> fluidTickScheduler;
	private boolean field_12837;
	private long lastSaveTime;
	private volatile boolean shouldSave;
	private long inhabitedTime;
	@Nullable
	private Supplier<ChunkHolder.LevelType> levelTypeProvider;
	@Nullable
	private Consumer<WorldChunk> loadToWorldConsumer;
	private final ChunkPos pos;
	private volatile boolean isLightOn;

	public WorldChunk(World world, ChunkPos chunkPos, Biome[] biomes) {
		this(world, chunkPos, biomes, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
	}

	public WorldChunk(
		World world,
		ChunkPos chunkPos,
		Biome[] biomes,
		UpgradeData upgradeData,
		TickScheduler<Block> tickScheduler,
		TickScheduler<Fluid> tickScheduler2,
		long l,
		@Nullable ChunkSection[] chunkSections,
		@Nullable Consumer<WorldChunk> consumer
	) {
		this.entitySections = new TypeFilterableList[16];
		this.world = world;
		this.pos = chunkPos;
		this.field_12849 = upgradeData;

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (ChunkStatus.field_12803.isSurfaceGenerated().contains(type)) {
				this.heightmaps.put(type, new Heightmap(this, type));
			}
		}

		for (int i = 0; i < this.entitySections.length; i++) {
			this.entitySections[i] = new TypeFilterableList<>(Entity.class);
		}

		this.biomeArray = biomes;
		this.blockTickScheduler = tickScheduler;
		this.fluidTickScheduler = tickScheduler2;
		this.inhabitedTime = l;
		this.loadToWorldConsumer = consumer;
		if (chunkSections != null) {
			if (this.field_12840.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.field_12840, 0, this.field_12840.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.field_12840.length);
			}
		}
	}

	public WorldChunk(World world, ProtoChunk protoChunk) {
		this(
			world,
			protoChunk.getPos(),
			protoChunk.getBiomeArray(),
			protoChunk.method_12003(),
			protoChunk.method_12303(),
			protoChunk.method_12313(),
			protoChunk.getInhabitedTime(),
			protoChunk.method_12006(),
			null
		);

		for (CompoundTag compoundTag : protoChunk.getEntities()) {
			EntityType.method_17842(compoundTag, world, entity -> {
				this.addEntity(entity);
				return entity;
			});
		}

		for (BlockEntity blockEntity : protoChunk.getBlockEntities().values()) {
			this.addBlockEntity(blockEntity);
		}

		this.pendingBlockEntityTags.putAll(protoChunk.getBlockEntityTags());

		for (int i = 0; i < protoChunk.getPostProcessingLists().length; i++) {
			this.postProcessingLists[i] = protoChunk.getPostProcessingLists()[i];
		}

		this.setStructureStarts(protoChunk.getStructureStarts());
		this.setStructureReferences(protoChunk.getStructureReferences());

		for (Entry<Heightmap.Type, Heightmap> entry : protoChunk.getHeightmaps()) {
			if (ChunkStatus.field_12803.isSurfaceGenerated().contains(entry.getKey())) {
				this.method_12032((Heightmap.Type)entry.getKey()).setTo(((Heightmap)entry.getValue()).asLongArray());
			}
		}

		this.setLightOn(protoChunk.isLightOn());
		this.shouldSave = true;
	}

	@Override
	public Heightmap method_12032(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.pendingBlockEntityTags.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	@Override
	public ChunkSection[] method_12006() {
		return this.field_12840;
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (this.world.method_8527() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			BlockState blockState = null;
			if (j == 60) {
				blockState = Blocks.field_10499.method_9564();
			}

			if (j == 70) {
				blockState = DebugChunkGenerator.getBlockState(i, k);
			}

			return blockState == null ? Blocks.field_10124.method_9564() : blockState;
		} else {
			try {
				if (j >= 0 && j >> 4 < this.field_12840.length) {
					ChunkSection chunkSection = this.field_12840[j >> 4];
					if (!ChunkSection.isEmpty(chunkSection)) {
						return chunkSection.getBlockState(i & 15, j & 15, k & 15);
					}
				}

				return Blocks.field_10124.method_9564();
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Getting block state");
				CrashReportSection crashReportSection = crashReport.addElement("Block being got");
				crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(i, j, k)));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		return this.method_12234(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public FluidState method_12234(int i, int j, int k) {
		try {
			if (j >= 0 && j >> 4 < this.field_12840.length) {
				ChunkSection chunkSection = this.field_12840[j >> 4];
				if (!ChunkSection.isEmpty(chunkSection)) {
					return chunkSection.method_12255(i & 15, j & 15, k & 15);
				}
			}

			return Fluids.field_15906.method_15785();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
			CrashReportSection crashReportSection = crashReport.addElement("Block being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(i, j, k)));
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		ChunkSection chunkSection = this.field_12840[j >> 4];
		if (chunkSection == field_12852) {
			if (blockState.isAir()) {
				return null;
			}

			chunkSection = new ChunkSection(j >> 4 << 4);
			this.field_12840[j >> 4] = chunkSection;
		}

		boolean bl2 = chunkSection.isEmpty();
		BlockState blockState2 = chunkSection.setBlockState(i, j & 15, k, blockState);
		if (blockState2 == blockState) {
			return null;
		} else {
			Block block = blockState.getBlock();
			Block block2 = blockState2.getBlock();
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13197)).trackUpdate(i, j, k, blockState);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13203)).trackUpdate(i, j, k, blockState);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13200)).trackUpdate(i, j, k, blockState);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13202)).trackUpdate(i, j, k, blockState);
			boolean bl3 = chunkSection.isEmpty();
			if (bl2 != bl3) {
				this.world.method_8398().method_12130().updateSectionStatus(blockPos, bl3);
			}

			if (!this.world.isClient) {
				blockState2.onBlockRemoved(this.world, blockPos, blockState, bl);
			} else if (block2 != block && block2 instanceof BlockEntityProvider) {
				this.world.removeBlockEntity(blockPos);
			}

			if (chunkSection.getBlockState(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (block2 instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(blockPos, WorldChunk.CreationType.field_12859);
					if (blockEntity != null) {
						blockEntity.resetBlock();
					}
				}

				if (!this.world.isClient) {
					blockState.onBlockAdded(this.world, blockPos, blockState2, bl);
				}

				if (block instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(blockPos, WorldChunk.CreationType.field_12859);
					if (blockEntity == null) {
						blockEntity = ((BlockEntityProvider)block).method_10123(this.world);
						this.world.method_8526(blockPos, blockEntity);
					} else {
						blockEntity.resetBlock();
					}
				}

				this.shouldSave = true;
				return blockState2;
			}
		}
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.world.method_8398().method_12130();
	}

	public int getLightLevel(BlockPos blockPos, int i) {
		return this.getLightLevel(blockPos, i, this.world.method_8597().hasSkyLight());
	}

	@Override
	public void addEntity(Entity entity) {
		this.field_12837 = true;
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		if (i != this.pos.x || j != this.pos.z) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.pos.x, this.pos.z, entity);
			entity.removed = true;
		}

		int k = MathHelper.floor(entity.y / 16.0);
		if (k < 0) {
			k = 0;
		}

		if (k >= this.entitySections.length) {
			k = this.entitySections.length - 1;
		}

		entity.field_6016 = true;
		entity.chunkX = this.pos.x;
		entity.chunkY = k;
		entity.chunkZ = this.pos.z;
		this.entitySections[k].add(entity);
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] ls) {
		((Heightmap)this.heightmaps.get(type)).setTo(ls);
	}

	public void remove(Entity entity) {
		this.remove(entity, entity.chunkY);
	}

	public void remove(Entity entity, int i) {
		if (i < 0) {
			i = 0;
		}

		if (i >= this.entitySections.length) {
			i = this.entitySections.length - 1;
		}

		this.entitySections[i].remove(entity);
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int i, int j) {
		return ((Heightmap)this.heightmaps.get(type)).get(i & 15, j & 15) - 1;
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos blockPos) {
		BlockState blockState = this.method_8320(blockPos);
		Block block = blockState.getBlock();
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).method_10123(this.world);
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		return this.getBlockEntity(blockPos, WorldChunk.CreationType.field_12859);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos blockPos, WorldChunk.CreationType creationType) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(blockPos);
		if (blockEntity == null) {
			CompoundTag compoundTag = (CompoundTag)this.pendingBlockEntityTags.remove(blockPos);
			if (compoundTag != null) {
				BlockEntity blockEntity2 = this.loadBlockEntity(blockPos, compoundTag);
				if (blockEntity2 != null) {
					return blockEntity2;
				}
			}
		}

		if (blockEntity == null) {
			if (creationType == WorldChunk.CreationType.field_12860) {
				blockEntity = this.createBlockEntity(blockPos);
				this.world.method_8526(blockPos, blockEntity);
			}
		} else if (blockEntity.isInvalid()) {
			this.blockEntities.remove(blockPos);
			return null;
		}

		return blockEntity;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.setBlockEntity(blockEntity.getPos(), blockEntity);
		if (this.loadedToWorld || this.world.isClient()) {
			this.world.method_8526(blockEntity.getPos(), blockEntity);
		}
	}

	@Override
	public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
		if (this.method_8320(blockPos).getBlock() instanceof BlockEntityProvider) {
			blockEntity.setWorld(this.world);
			blockEntity.setPos(blockPos);
			blockEntity.validate();
			BlockEntity blockEntity2 = (BlockEntity)this.blockEntities.put(blockPos.toImmutable(), blockEntity);
			if (blockEntity2 != null && blockEntity2 != blockEntity) {
				blockEntity2.invalidate();
			}
		}
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag compoundTag) {
		this.pendingBlockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
	}

	@Nullable
	@Override
	public CompoundTag method_20598(BlockPos blockPos) {
		BlockEntity blockEntity = this.method_8321(blockPos);
		if (blockEntity != null && !blockEntity.isInvalid()) {
			CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
			compoundTag.putBoolean("keepPacked", false);
			return compoundTag;
		} else {
			CompoundTag compoundTag = (CompoundTag)this.pendingBlockEntityTags.get(blockPos);
			if (compoundTag != null) {
				compoundTag = compoundTag.method_10553();
				compoundTag.putBoolean("keepPacked", true);
			}

			return compoundTag;
		}
	}

	@Override
	public void removeBlockEntity(BlockPos blockPos) {
		if (this.loadedToWorld || this.world.isClient()) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(blockPos);
			if (blockEntity != null) {
				blockEntity.invalidate();
			}
		}
	}

	public void loadToWorld() {
		if (this.loadToWorldConsumer != null) {
			this.loadToWorldConsumer.accept(this);
			this.loadToWorldConsumer = null;
		}
	}

	public void markDirty() {
		this.shouldSave = true;
	}

	public void method_12205(@Nullable Entity entity, Box box, List<Entity> list, @Nullable Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			if (!this.entitySections[k].isEmpty()) {
				for (Entity entity2 : this.entitySections[k]) {
					if (entity2.method_5829().intersects(box) && entity2 != entity) {
						if (predicate == null || predicate.test(entity2)) {
							list.add(entity2);
						}

						if (entity2 instanceof EnderDragonEntity) {
							for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity2).method_5690()) {
								if (enderDragonPart != entity && enderDragonPart.method_5829().intersects(box) && (predicate == null || predicate.test(enderDragonPart))) {
									list.add(enderDragonPart);
								}
							}
						}
					}
				}
			}
		}
	}

	public void method_18029(@Nullable EntityType<?> entityType, Box box, List<Entity> list, Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			for (Entity entity : this.entitySections[k].getAllOfType(Entity.class)) {
				if ((entityType == null || entity.getType() == entityType) && entity.method_5829().intersects(box) && predicate.test(entity)) {
					list.add(entity);
				}
			}
		}
	}

	public <T extends Entity> void method_12210(Class<? extends T> class_, Box box, List<T> list, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			for (T entity : this.entitySections[k].getAllOfType(class_)) {
				if (entity.method_5829().intersects(box) && (predicate == null || predicate.test(entity))) {
					list.add(entity);
				}
			}
		}
	}

	public boolean isEmpty() {
		return false;
	}

	@Override
	public ChunkPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public void loadFromPacket(PacketByteBuf packetByteBuf, CompoundTag compoundTag, int i, boolean bl) {
		Predicate<BlockPos> predicate = bl ? blockPos -> true : blockPos -> (i & 1 << (blockPos.getY() >> 4)) != 0;
		Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate).forEach(this.world::removeBlockEntity);

		for (int j = 0; j < this.field_12840.length; j++) {
			ChunkSection chunkSection = this.field_12840[j];
			if ((i & 1 << j) == 0) {
				if (bl && chunkSection != field_12852) {
					this.field_12840[j] = field_12852;
				}
			} else {
				if (chunkSection == field_12852) {
					chunkSection = new ChunkSection(j << 4);
					this.field_12840[j] = chunkSection;
				}

				chunkSection.fromPacket(packetByteBuf);
			}
		}

		if (bl) {
			for (int jx = 0; jx < this.biomeArray.length; jx++) {
				this.biomeArray[jx] = Registry.BIOME.get(packetByteBuf.readInt());
			}
		}

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (compoundTag.containsKey(string, 12)) {
				this.setHeightmap(type, compoundTag.getLongArray(string));
			}
		}

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			blockEntity.resetBlock();
		}
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.biomeArray;
	}

	public void setLoadedToWorld(boolean bl) {
		this.loadedToWorld = bl;
	}

	public World getWorld() {
		return this.world;
	}

	@Override
	public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
		return Collections.unmodifiableSet(this.heightmaps.entrySet());
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public TypeFilterableList<Entity>[] getEntitySectionArray() {
		return this.entitySections;
	}

	@Override
	public CompoundTag getBlockEntityTagAt(BlockPos blockPos) {
		return (CompoundTag)this.pendingBlockEntityTags.get(blockPos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return StreamSupport.stream(BlockPos.iterate(this.pos.getStartX(), 0, this.pos.getStartZ(), this.pos.getEndX(), 255, this.pos.getEndZ()).spliterator(), false)
			.filter(blockPos -> this.method_8320(blockPos).getLuminance() != 0);
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Override
	public void setShouldSave(boolean bl) {
		this.shouldSave = bl;
	}

	@Override
	public boolean needsSaving() {
		return this.shouldSave || this.field_12837 && this.world.getTime() != this.lastSaveTime;
	}

	public void method_12232(boolean bl) {
		this.field_12837 = bl;
	}

	@Override
	public void setLastSaveTime(long l) {
		this.lastSaveTime = l;
	}

	@Nullable
	@Override
	public StructureStart method_12181(String string) {
		return (StructureStart)this.structureStarts.get(string);
	}

	@Override
	public void method_12184(String string, StructureStart structureStart) {
		this.structureStarts.put(string, structureStart);
	}

	@Override
	public Map<String, StructureStart> getStructureStarts() {
		return this.structureStarts;
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> map) {
		this.structureStarts.clear();
		this.structureStarts.putAll(map);
	}

	@Override
	public LongSet getStructureReferences(String string) {
		return (LongSet)this.structureReferences.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(String string, long l) {
		((LongSet)this.structureReferences.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return this.structureReferences;
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> map) {
		this.structureReferences.clear();
		this.structureReferences.putAll(map);
	}

	@Override
	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	@Override
	public void setInhabitedTime(long l) {
		this.inhabitedTime = l;
	}

	public void runPostProcessing() {
		ChunkPos chunkPos = this.getPos();

		for (int i = 0; i < this.postProcessingLists.length; i++) {
			if (this.postProcessingLists[i] != null) {
				for (Short short_ : this.postProcessingLists[i]) {
					BlockPos blockPos = ProtoChunk.joinBlockPos(short_, i, chunkPos);
					BlockState blockState = this.method_8320(blockPos);
					BlockState blockState2 = Block.method_9510(blockState, this.world, blockPos);
					this.world.method_8652(blockPos, blockState2, 20);
				}

				this.postProcessingLists[i].clear();
			}
		}

		this.method_20530();

		for (BlockPos blockPos2 : Sets.newHashSet(this.pendingBlockEntityTags.keySet())) {
			this.method_8321(blockPos2);
		}

		this.pendingBlockEntityTags.clear();
		this.field_12849.method_12356(this);
	}

	@Nullable
	private BlockEntity loadBlockEntity(BlockPos blockPos, CompoundTag compoundTag) {
		BlockEntity blockEntity;
		if ("DUMMY".equals(compoundTag.getString("id"))) {
			Block block = this.method_8320(blockPos).getBlock();
			if (block instanceof BlockEntityProvider) {
				blockEntity = ((BlockEntityProvider)block).method_10123(this.world);
			} else {
				blockEntity = null;
				LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockPos, this.method_8320(blockPos));
			}
		} else {
			blockEntity = BlockEntity.createFromTag(compoundTag);
		}

		if (blockEntity != null) {
			blockEntity.setPos(blockPos);
			this.addBlockEntity(blockEntity);
		} else {
			LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.method_8320(blockPos), blockPos);
		}

		return blockEntity;
	}

	@Override
	public UpgradeData method_12003() {
		return this.field_12849;
	}

	@Override
	public ShortList[] getPostProcessingLists() {
		return this.postProcessingLists;
	}

	public void method_20530() {
		if (this.blockTickScheduler instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.method_8397(), blockPos -> this.method_8320(blockPos).getBlock());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		} else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
			this.world.method_8397().method_20470(((SimpleTickScheduler)this.blockTickScheduler).stream());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		}

		if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.method_8405(), blockPos -> this.method_8316(blockPos).getFluid());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		} else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
			this.world.method_8405().method_20470(((SimpleTickScheduler)this.fluidTickScheduler).stream());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		}
	}

	public void method_20471(ServerWorld serverWorld) {
		if (this.blockTickScheduler == DummyClientTickScheduler.get()) {
			this.blockTickScheduler = new SimpleTickScheduler<>(Registry.BLOCK::getId, serverWorld.method_14196().getScheduledTicksInChunk(this.pos, true, false));
			this.setShouldSave(true);
		}

		if (this.fluidTickScheduler == DummyClientTickScheduler.get()) {
			this.fluidTickScheduler = new SimpleTickScheduler<>(Registry.FLUID::getId, serverWorld.method_14179().getScheduledTicksInChunk(this.pos, true, false));
			this.setShouldSave(true);
		}
	}

	@Override
	public ChunkStatus method_12009() {
		return ChunkStatus.field_12803;
	}

	public ChunkHolder.LevelType getLevelType() {
		return this.levelTypeProvider == null ? ChunkHolder.LevelType.field_13876 : (ChunkHolder.LevelType)this.levelTypeProvider.get();
	}

	public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> supplier) {
		this.levelTypeProvider = supplier;
	}

	@Override
	public void method_17032(LightingProvider lightingProvider) {
	}

	@Override
	public boolean isLightOn() {
		return this.isLightOn;
	}

	@Override
	public void setLightOn(boolean bl) {
		this.isLightOn = bl;
		this.setShouldSave(true);
	}

	public static enum CreationType {
		field_12860,
		field_12861,
		field_12859;
	}
}
