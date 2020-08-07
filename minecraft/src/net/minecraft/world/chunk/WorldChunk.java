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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.collection.TypeFilterableList;
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
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk implements Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	@Nullable
	public static final ChunkSection EMPTY_SECTION = null;
	private final ChunkSection[] sections = new ChunkSection[16];
	private BiomeArray biomeArray;
	private final Map<BlockPos, CompoundTag> pendingBlockEntityTags = Maps.<BlockPos, CompoundTag>newHashMap();
	private boolean loadedToWorld;
	private final World world;
	private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
	private final UpgradeData upgradeData;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.<BlockPos, BlockEntity>newHashMap();
	private final TypeFilterableList<Entity>[] entitySections;
	private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
	private final Map<StructureFeature<?>, LongSet> structureReferences = Maps.<StructureFeature<?>, LongSet>newHashMap();
	private final ShortList[] postProcessingLists = new ShortList[16];
	private TickScheduler<Block> blockTickScheduler;
	private TickScheduler<Fluid> fluidTickScheduler;
	private boolean unsaved;
	private long lastSaveTime;
	private volatile boolean shouldSave;
	private long inhabitedTime;
	@Nullable
	private Supplier<ChunkHolder.LevelType> levelTypeProvider;
	@Nullable
	private Consumer<WorldChunk> loadToWorldConsumer;
	private final ChunkPos pos;
	private volatile boolean lightOn;

	public WorldChunk(World world, ChunkPos pos, BiomeArray biomes) {
		this(world, pos, biomes, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
	}

	public WorldChunk(
		World world,
		ChunkPos pos,
		BiomeArray biomes,
		UpgradeData upgradeData,
		TickScheduler<Block> blockTickScheduler,
		TickScheduler<Fluid> fluidTickScheduler,
		long inhabitedTime,
		@Nullable ChunkSection[] sections,
		@Nullable Consumer<WorldChunk> loadToWorldConsumer
	) {
		this.entitySections = new TypeFilterableList[16];
		this.world = world;
		this.pos = pos;
		this.upgradeData = upgradeData;

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (ChunkStatus.field_12803.getHeightmapTypes().contains(type)) {
				this.heightmaps.put(type, new Heightmap(this, type));
			}
		}

		for (int i = 0; i < this.entitySections.length; i++) {
			this.entitySections[i] = new TypeFilterableList<>(Entity.class);
		}

		this.biomeArray = biomes;
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
		this.inhabitedTime = inhabitedTime;
		this.loadToWorldConsumer = loadToWorldConsumer;
		if (sections != null) {
			if (this.sections.length == sections.length) {
				System.arraycopy(sections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sections.length, this.sections.length);
			}
		}
	}

	public WorldChunk(World world, ProtoChunk protoChunk) {
		this(
			world,
			protoChunk.getPos(),
			protoChunk.getBiomeArray(),
			protoChunk.getUpgradeData(),
			protoChunk.method_12303(),
			protoChunk.method_12313(),
			protoChunk.getInhabitedTime(),
			protoChunk.getSectionArray(),
			null
		);

		for (CompoundTag compoundTag : protoChunk.getEntities()) {
			EntityType.loadEntityWithPassengers(compoundTag, world, entity -> {
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
			if (ChunkStatus.field_12803.getHeightmapTypes().contains(entry.getKey())) {
				this.getHeightmap((Heightmap.Type)entry.getKey()).setTo(((Heightmap)entry.getValue()).asLongArray());
			}
		}

		this.setLightOn(protoChunk.isLightOn());
		this.shouldSave = true;
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.pendingBlockEntityTags.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.sections;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (this.world.isDebugWorld()) {
			BlockState blockState = null;
			if (j == 60) {
				blockState = Blocks.field_10499.getDefaultState();
			}

			if (j == 70) {
				blockState = DebugChunkGenerator.getBlockState(i, k);
			}

			return blockState == null ? Blocks.field_10124.getDefaultState() : blockState;
		} else {
			try {
				if (j >= 0 && j >> 4 < this.sections.length) {
					ChunkSection chunkSection = this.sections[j >> 4];
					if (!ChunkSection.isEmpty(chunkSection)) {
						return chunkSection.getBlockState(i & 15, j & 15, k & 15);
					}
				}

				return Blocks.field_10124.getDefaultState();
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Getting block state");
				CrashReportSection crashReportSection = crashReport.addElement("Block being got");
				crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(i, j, k)));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
	}

	public FluidState getFluidState(int x, int y, int z) {
		try {
			if (y >= 0 && y >> 4 < this.sections.length) {
				ChunkSection chunkSection = this.sections[y >> 4];
				if (!ChunkSection.isEmpty(chunkSection)) {
					return chunkSection.getFluidState(x & 15, y & 15, z & 15);
				}
			}

			return Fluids.field_15906.getDefaultState();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
			CrashReportSection crashReportSection = crashReport.addElement("Block being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(x, y, z)));
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.sections[j >> 4];
		if (chunkSection == EMPTY_SECTION) {
			if (state.isAir()) {
				return null;
			}

			chunkSection = new ChunkSection(j >> 4 << 4);
			this.sections[j >> 4] = chunkSection;
		}

		boolean bl = chunkSection.isEmpty();
		BlockState blockState = chunkSection.setBlockState(i, j & 15, k, state);
		if (blockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block2 = blockState.getBlock();
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13197)).trackUpdate(i, j, k, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13203)).trackUpdate(i, j, k, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13200)).trackUpdate(i, j, k, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.field_13202)).trackUpdate(i, j, k, state);
			boolean bl2 = chunkSection.isEmpty();
			if (bl != bl2) {
				this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
			}

			if (!this.world.isClient) {
				blockState.onStateReplaced(this.world, pos, state, moved);
			} else if (block2 != block && block2 instanceof BlockEntityProvider) {
				this.world.removeBlockEntity(pos);
			}

			if (!chunkSection.getBlockState(i, j & 15, k).isOf(block)) {
				return null;
			} else {
				if (block2 instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.field_12859);
					if (blockEntity != null) {
						blockEntity.resetBlock();
					}
				}

				if (!this.world.isClient) {
					state.onBlockAdded(this.world, pos, blockState, moved);
				}

				if (block instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.field_12859);
					if (blockEntity == null) {
						blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
						this.world.setBlockEntity(pos, blockEntity);
					} else {
						blockEntity.resetBlock();
					}
				}

				this.shouldSave = true;
				return blockState;
			}
		}
	}

	@Nullable
	public LightingProvider getLightingProvider() {
		return this.world.getChunkManager().getLightingProvider();
	}

	@Override
	public void addEntity(Entity entity) {
		this.unsaved = true;
		int i = MathHelper.floor(entity.getX() / 16.0);
		int j = MathHelper.floor(entity.getZ() / 16.0);
		if (i != this.pos.x || j != this.pos.z) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.pos.x, this.pos.z, entity);
			entity.removed = true;
		}

		int k = MathHelper.floor(entity.getY() / 16.0);
		if (k < 0) {
			k = 0;
		}

		if (k >= this.entitySections.length) {
			k = this.entitySections.length - 1;
		}

		entity.updateNeeded = true;
		entity.chunkX = this.pos.x;
		entity.chunkY = k;
		entity.chunkZ = this.pos.z;
		this.entitySections[k].add(entity);
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		((Heightmap)this.heightmaps.get(type)).setTo(heightmap);
	}

	public void remove(Entity entity) {
		this.remove(entity, entity.chunkY);
	}

	public void remove(Entity entity, int section) {
		if (section < 0) {
			section = 0;
		}

		if (section >= this.entitySections.length) {
			section = this.entitySections.length - 1;
		}

		this.entitySections[section].remove(entity);
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		return ((Heightmap)this.heightmaps.get(type)).get(x & 15, z & 15) - 1;
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		Block block = blockState.getBlock();
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).createBlockEntity(this.world);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getBlockEntity(pos, WorldChunk.CreationType.field_12859);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(pos);
		if (blockEntity == null) {
			CompoundTag compoundTag = (CompoundTag)this.pendingBlockEntityTags.remove(pos);
			if (compoundTag != null) {
				BlockEntity blockEntity2 = this.loadBlockEntity(pos, compoundTag);
				if (blockEntity2 != null) {
					return blockEntity2;
				}
			}
		}

		if (blockEntity == null) {
			if (creationType == WorldChunk.CreationType.field_12860) {
				blockEntity = this.createBlockEntity(pos);
				this.world.setBlockEntity(pos, blockEntity);
			}
		} else if (blockEntity.isRemoved()) {
			this.blockEntities.remove(pos);
			return null;
		}

		return blockEntity;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.setBlockEntity(blockEntity.getPos(), blockEntity);
		if (this.loadedToWorld || this.world.isClient()) {
			this.world.setBlockEntity(blockEntity.getPos(), blockEntity);
		}
	}

	@Override
	public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
		if (this.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
			blockEntity.setLocation(this.world, pos);
			blockEntity.cancelRemoval();
			BlockEntity blockEntity2 = (BlockEntity)this.blockEntities.put(pos.toImmutable(), blockEntity);
			if (blockEntity2 != null && blockEntity2 != blockEntity) {
				blockEntity2.markRemoved();
			}
		}
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag tag) {
		this.pendingBlockEntityTags.put(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), tag);
	}

	@Nullable
	@Override
	public CompoundTag getPackedBlockEntityTag(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		if (blockEntity != null && !blockEntity.isRemoved()) {
			CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
			compoundTag.putBoolean("keepPacked", false);
			return compoundTag;
		} else {
			CompoundTag compoundTag = (CompoundTag)this.pendingBlockEntityTags.get(pos);
			if (compoundTag != null) {
				compoundTag = compoundTag.method_10553();
				compoundTag.putBoolean("keepPacked", true);
			}

			return compoundTag;
		}
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		if (this.loadedToWorld || this.world.isClient()) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(pos);
			if (blockEntity != null) {
				blockEntity.markRemoved();
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

	public void collectOtherEntities(@Nullable Entity except, Box box, List<Entity> entityList, @Nullable Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			TypeFilterableList<Entity> typeFilterableList = this.entitySections[k];
			List<Entity> list = typeFilterableList.method_29903();
			int l = list.size();

			for (int m = 0; m < l; m++) {
				Entity entity = (Entity)list.get(m);
				if (entity.getBoundingBox().intersects(box) && entity != except) {
					if (predicate == null || predicate.test(entity)) {
						entityList.add(entity);
					}

					if (entity instanceof EnderDragonEntity) {
						for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
							if (enderDragonPart != except && enderDragonPart.getBoundingBox().intersects(box) && (predicate == null || predicate.test(enderDragonPart))) {
								entityList.add(enderDragonPart);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Collects a list of entities and appends them to the given list according to the specified criteria.
	 * 
	 * <strong>Warning:<strong> If {@code null} is passed as the entity type filter, care should be
	 * taken that the type argument {@code T} is set to {@link Entity}, otherwise heap pollution in
	 * the output list or {@link ClassCastException} can occur.
	 * 
	 * @param type the entity type of the entities to collect, or {@code null} to collect entities of all types.
	 * @param box the box within which collected entities must be
	 * @param result a list in which to store the collected entities
	 * @param predicate a predicate which entities must satisfy in order to be collected
	 */
	public <T extends Entity> void collectEntities(@Nullable EntityType<?> type, Box box, List<? super T> result, Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			for (Entity entity : this.entitySections[k].getAllOfType(Entity.class)) {
				if ((type == null || entity.getType() == type) && entity.getBoundingBox().intersects(box) && predicate.test(entity)) {
					result.add(entity);
				}
			}
		}
	}

	/**
	 * Collects a list of entities of some runtime type and appends them to the given list. The runtime
	 * class of each collected entity will be the same as or a subclass of the given class.
	 * 
	 * @param entityClass the class object representing the type collected entities must extend
	 * @param box the box in which to collect entities
	 * @param result a list in which to store the collected entities
	 * @param predicate a predicate which entities must satisfy in order to be collected
	 */
	public <T extends Entity> void collectEntitiesByClass(Class<? extends T> entityClass, Box box, List<T> result, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
		j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

		for (int k = i; k <= j; k++) {
			for (T entity : this.entitySections[k].getAllOfType(entityClass)) {
				if (entity.getBoundingBox().intersects(box) && (predicate == null || predicate.test(entity))) {
					result.add(entity);
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
	public void loadFromPacket(@Nullable BiomeArray biomes, PacketByteBuf buf, CompoundTag tag, int verticalStripBitmask) {
		boolean bl = biomes != null;
		Predicate<BlockPos> predicate = bl ? pos -> true : pos -> (verticalStripBitmask & 1 << (pos.getY() >> 4)) != 0;
		Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate).forEach(this.world::removeBlockEntity);

		for (int i = 0; i < this.sections.length; i++) {
			ChunkSection chunkSection = this.sections[i];
			if ((verticalStripBitmask & 1 << i) == 0) {
				if (bl && chunkSection != EMPTY_SECTION) {
					this.sections[i] = EMPTY_SECTION;
				}
			} else {
				if (chunkSection == EMPTY_SECTION) {
					chunkSection = new ChunkSection(i << 4);
					this.sections[i] = chunkSection;
				}

				chunkSection.fromPacket(buf);
			}
		}

		if (biomes != null) {
			this.biomeArray = biomes;
		}

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (tag.contains(string, 12)) {
				this.setHeightmap(type, tag.getLongArray(string));
			}
		}

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			blockEntity.resetBlock();
		}
	}

	@Override
	public BiomeArray getBiomeArray() {
		return this.biomeArray;
	}

	public void setLoadedToWorld(boolean loaded) {
		this.loadedToWorld = loaded;
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
	public CompoundTag getBlockEntityTag(BlockPos pos) {
		return (CompoundTag)this.pendingBlockEntityTags.get(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return StreamSupport.stream(BlockPos.iterate(this.pos.getStartX(), 0, this.pos.getStartZ(), this.pos.getEndX(), 255, this.pos.getEndZ()).spliterator(), false)
			.filter(blockPos -> this.getBlockState(blockPos).getLuminance() != 0);
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
	public void setShouldSave(boolean shouldSave) {
		this.shouldSave = shouldSave;
	}

	@Override
	public boolean needsSaving() {
		return this.shouldSave || this.unsaved && this.world.getTime() != this.lastSaveTime;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	@Override
	public void setLastSaveTime(long lastSaveTime) {
		this.lastSaveTime = lastSaveTime;
	}

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return (StructureStart<?>)this.structureStarts.get(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
		this.structureStarts.put(structure, start);
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return this.structureStarts;
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
		this.structureStarts.clear();
		this.structureStarts.putAll(structureStarts);
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return (LongSet)this.structureReferences.computeIfAbsent(structure, structurex -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
		((LongSet)this.structureReferences.computeIfAbsent(structure, structurex -> new LongOpenHashSet())).add(reference);
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return this.structureReferences;
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
		this.structureReferences.clear();
		this.structureReferences.putAll(structureReferences);
	}

	@Override
	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	@Override
	public void setInhabitedTime(long inhabitedTime) {
		this.inhabitedTime = inhabitedTime;
	}

	public void runPostProcessing() {
		ChunkPos chunkPos = this.getPos();

		for (int i = 0; i < this.postProcessingLists.length; i++) {
			if (this.postProcessingLists[i] != null) {
				for (Short short_ : this.postProcessingLists[i]) {
					BlockPos blockPos = ProtoChunk.joinBlockPos(short_, i, chunkPos);
					BlockState blockState = this.getBlockState(blockPos);
					BlockState blockState2 = Block.postProcessState(blockState, this.world, blockPos);
					this.world.setBlockState(blockPos, blockState2, 20);
				}

				this.postProcessingLists[i].clear();
			}
		}

		this.disableTickSchedulers();

		for (BlockPos blockPos2 : Sets.newHashSet(this.pendingBlockEntityTags.keySet())) {
			this.getBlockEntity(blockPos2);
		}

		this.pendingBlockEntityTags.clear();
		this.upgradeData.upgrade(this);
	}

	@Nullable
	private BlockEntity loadBlockEntity(BlockPos pos, CompoundTag tag) {
		BlockState blockState = this.getBlockState(pos);
		BlockEntity blockEntity;
		if ("DUMMY".equals(tag.getString("id"))) {
			Block block = blockState.getBlock();
			if (block instanceof BlockEntityProvider) {
				blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
			} else {
				blockEntity = null;
				LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pos, blockState);
			}
		} else {
			blockEntity = BlockEntity.createFromTag(blockState, tag);
		}

		if (blockEntity != null) {
			blockEntity.setLocation(this.world, pos);
			this.addBlockEntity(blockEntity);
		} else {
			LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", blockState, pos);
		}

		return blockEntity;
	}

	@Override
	public UpgradeData getUpgradeData() {
		return this.upgradeData;
	}

	@Override
	public ShortList[] getPostProcessingLists() {
		return this.postProcessingLists;
	}

	public void disableTickSchedulers() {
		if (this.blockTickScheduler instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.getBlockTickScheduler(), blockPos -> this.getBlockState(blockPos).getBlock());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		} else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
			((SimpleTickScheduler)this.blockTickScheduler).scheduleTo(this.world.getBlockTickScheduler());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		}

		if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.getFluidTickScheduler(), blockPos -> this.getFluidState(blockPos).getFluid());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		} else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
			((SimpleTickScheduler)this.fluidTickScheduler).scheduleTo(this.world.getFluidTickScheduler());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		}
	}

	public void enableTickSchedulers(ServerWorld world) {
		if (this.blockTickScheduler == DummyClientTickScheduler.get()) {
			this.blockTickScheduler = new SimpleTickScheduler<>(
				Registry.BLOCK::getId, world.method_14196().getScheduledTicksInChunk(this.pos, true, false), world.getTime()
			);
			this.setShouldSave(true);
		}

		if (this.fluidTickScheduler == DummyClientTickScheduler.get()) {
			this.fluidTickScheduler = new SimpleTickScheduler<>(
				Registry.FLUID::getId, world.method_14179().getScheduledTicksInChunk(this.pos, true, false), world.getTime()
			);
			this.setShouldSave(true);
		}
	}

	@Override
	public ChunkStatus getStatus() {
		return ChunkStatus.field_12803;
	}

	public ChunkHolder.LevelType getLevelType() {
		return this.levelTypeProvider == null ? ChunkHolder.LevelType.field_13876 : (ChunkHolder.LevelType)this.levelTypeProvider.get();
	}

	public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> levelTypeProvider) {
		this.levelTypeProvider = levelTypeProvider;
	}

	@Override
	public boolean isLightOn() {
		return this.lightOn;
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.lightOn = lightOn;
		this.setShouldSave(true);
	}

	public static enum CreationType {
		field_12860,
		field_12861,
		field_12859;
	}
}
