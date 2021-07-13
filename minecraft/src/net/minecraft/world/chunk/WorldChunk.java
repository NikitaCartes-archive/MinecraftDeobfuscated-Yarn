package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk implements Chunk {
	static final Logger LOGGER = LogManager.getLogger();
	private static final BlockEntityTickInvoker EMPTY_BLOCK_ENTITY_TICKER = new BlockEntityTickInvoker() {
		@Override
		public void tick() {
		}

		@Override
		public boolean isRemoved() {
			return true;
		}

		@Override
		public BlockPos getPos() {
			return BlockPos.ORIGIN;
		}

		@Override
		public String getName() {
			return "<null>";
		}
	};
	@Nullable
	public static final ChunkSection EMPTY_SECTION = null;
	private final ChunkSection[] sections;
	private BiomeArray biomeArray;
	private final Map<BlockPos, NbtCompound> pendingBlockEntityNbts = Maps.<BlockPos, NbtCompound>newHashMap();
	private final Map<BlockPos, WorldChunk.WrappedBlockEntityTickInvoker> blockEntityTickers = Maps.<BlockPos, WorldChunk.WrappedBlockEntityTickInvoker>newHashMap();
	private boolean loadedToWorld;
	final World world;
	private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
	private final UpgradeData upgradeData;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.<BlockPos, BlockEntity>newHashMap();
	private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
	private final Map<StructureFeature<?>, LongSet> structureReferences = Maps.<StructureFeature<?>, LongSet>newHashMap();
	private final ShortList[] postProcessingLists;
	private TickScheduler<Block> blockTickScheduler;
	private TickScheduler<Fluid> fluidTickScheduler;
	private volatile boolean shouldSave;
	private long inhabitedTime;
	@Nullable
	private Supplier<ChunkHolder.LevelType> levelTypeProvider;
	@Nullable
	private Consumer<WorldChunk> loadToWorldConsumer;
	private final ChunkPos pos;
	private volatile boolean lightOn;
	private final Int2ObjectMap<GameEventDispatcher> gameEventDispatchers;

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
		this.world = world;
		this.pos = pos;
		this.upgradeData = upgradeData;
		this.gameEventDispatchers = new Int2ObjectOpenHashMap<>();

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (ChunkStatus.FULL.getHeightmapTypes().contains(type)) {
				this.heightmaps.put(type, new Heightmap(this, type));
			}
		}

		this.biomeArray = biomes;
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
		this.inhabitedTime = inhabitedTime;
		this.loadToWorldConsumer = loadToWorldConsumer;
		this.sections = new ChunkSection[world.countVerticalSections()];
		if (sections != null) {
			if (this.sections.length == sections.length) {
				System.arraycopy(sections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sections.length, this.sections.length);
			}
		}

		this.postProcessingLists = new ShortList[world.countVerticalSections()];
	}

	public WorldChunk(ServerWorld serverWorld, ProtoChunk protoChunk, @Nullable Consumer<WorldChunk> consumer) {
		this(
			serverWorld,
			protoChunk.getPos(),
			protoChunk.getBiomeArray(),
			protoChunk.getUpgradeData(),
			protoChunk.getBlockTickScheduler(),
			protoChunk.getFluidTickScheduler(),
			protoChunk.getInhabitedTime(),
			protoChunk.getSectionArray(),
			consumer
		);

		for (BlockEntity blockEntity : protoChunk.getBlockEntities().values()) {
			this.setBlockEntity(blockEntity);
		}

		this.pendingBlockEntityNbts.putAll(protoChunk.getBlockEntityNbts());

		for (int i = 0; i < protoChunk.getPostProcessingLists().length; i++) {
			this.postProcessingLists[i] = protoChunk.getPostProcessingLists()[i];
		}

		this.setStructureStarts(protoChunk.getStructureStarts());
		this.setStructureReferences(protoChunk.getStructureReferences());

		for (Entry<Heightmap.Type, Heightmap> entry : protoChunk.getHeightmaps()) {
			if (ChunkStatus.FULL.getHeightmapTypes().contains(entry.getKey())) {
				this.setHeightmap((Heightmap.Type)entry.getKey(), ((Heightmap)entry.getValue()).asLongArray());
			}
		}

		this.setLightOn(protoChunk.isLightOn());
		this.shouldSave = true;
	}

	@Override
	public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
		return this.gameEventDispatchers.computeIfAbsent(ySectionCoord, sectionCoord -> new SimpleGameEventDispatcher(this.world));
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.pendingBlockEntityNbts.keySet());
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
				blockState = Blocks.BARRIER.getDefaultState();
			}

			if (j == 70) {
				blockState = DebugChunkGenerator.getBlockState(i, k);
			}

			return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
		} else {
			try {
				int l = this.getSectionIndex(j);
				if (l >= 0 && l < this.sections.length) {
					ChunkSection chunkSection = this.sections[l];
					if (!ChunkSection.isEmpty(chunkSection)) {
						return chunkSection.getBlockState(i & 15, j & 15, k & 15);
					}
				}

				return Blocks.AIR.getDefaultState();
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Getting block state");
				CrashReportSection crashReportSection = crashReport.addElement("Block being got");
				crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this, i, j, k)));
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
			int i = this.getSectionIndex(y);
			if (i >= 0 && i < this.sections.length) {
				ChunkSection chunkSection = this.sections[i];
				if (!ChunkSection.isEmpty(chunkSection)) {
					return chunkSection.getFluidState(x & 15, y & 15, z & 15);
				}
			}

			return Fluids.EMPTY.getDefaultState();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
			CrashReportSection crashReportSection = crashReport.addElement("Block being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this, x, y, z)));
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getY();
		int j = this.getSectionIndex(i);
		ChunkSection chunkSection = this.sections[j];
		if (chunkSection == EMPTY_SECTION) {
			if (state.isAir()) {
				return null;
			}

			chunkSection = new ChunkSection(ChunkSectionPos.getSectionCoord(i));
			this.sections[j] = chunkSection;
		}

		boolean bl = chunkSection.isEmpty();
		int k = pos.getX() & 15;
		int l = i & 15;
		int m = pos.getZ() & 15;
		BlockState blockState = chunkSection.setBlockState(k, l, m, state);
		if (blockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(k, i, m, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(k, i, m, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(k, i, m, state);
			((Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(k, i, m, state);
			boolean bl2 = chunkSection.isEmpty();
			if (bl != bl2) {
				this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
			}

			boolean bl3 = blockState.hasBlockEntity();
			if (!this.world.isClient) {
				blockState.onStateReplaced(this.world, pos, state, moved);
			} else if (!blockState.isOf(block) && bl3) {
				this.removeBlockEntity(pos);
			}

			if (!chunkSection.getBlockState(k, l, m).isOf(block)) {
				return null;
			} else {
				if (!this.world.isClient) {
					state.onBlockAdded(this.world, pos, blockState, moved);
				}

				if (state.hasBlockEntity()) {
					BlockEntity blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
					if (blockEntity == null) {
						blockEntity = ((BlockEntityProvider)block).createBlockEntity(pos, state);
						if (blockEntity != null) {
							this.addBlockEntity(blockEntity);
						}
					} else {
						blockEntity.setCachedState(state);
						this.updateTicker(blockEntity);
					}
				}

				this.shouldSave = true;
				return blockState;
			}
		}
	}

	@Deprecated
	@Override
	public void addEntity(Entity entity) {
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
		if (heightmap == null) {
			if (SharedConstants.isDevelopment) {
				LOGGER.error("Unprimed heightmap: " + type + " " + x + " " + z);
			}

			Heightmap.populateHeightmaps(this, EnumSet.of(type));
			heightmap = (Heightmap)this.heightmaps.get(type);
		}

		return heightmap.get(x & 15, z & 15) - 1;
	}

	@Override
	public BlockPos method_35319(Heightmap.Type type) {
		ChunkPos chunkPos = this.getPos();
		int i = this.getBottomY();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); j++) {
			for (int k = chunkPos.getStartZ(); k <= chunkPos.getEndZ(); k++) {
				int l = this.sampleHeightmap(type, j & 15, k & 15);
				if (l > i) {
					i = l;
					mutable.set(j, l, k);
				}
			}
		}

		return mutable.toImmutable();
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		return !blockState.hasBlockEntity() ? null : ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(pos);
		if (blockEntity == null) {
			NbtCompound nbtCompound = (NbtCompound)this.pendingBlockEntityNbts.remove(pos);
			if (nbtCompound != null) {
				BlockEntity blockEntity2 = this.loadBlockEntity(pos, nbtCompound);
				if (blockEntity2 != null) {
					return blockEntity2;
				}
			}
		}

		if (blockEntity == null) {
			if (creationType == WorldChunk.CreationType.IMMEDIATE) {
				blockEntity = this.createBlockEntity(pos);
				if (blockEntity != null) {
					this.addBlockEntity(blockEntity);
				}
			}
		} else if (blockEntity.isRemoved()) {
			this.blockEntities.remove(pos);
			return null;
		}

		return blockEntity;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.setBlockEntity(blockEntity);
		if (this.canTickBlockEntities()) {
			this.updateGameEventListener(blockEntity);
			this.updateTicker(blockEntity);
		}
	}

	private boolean canTickBlockEntities() {
		return this.loadedToWorld || this.world.isClient();
	}

	boolean canTickBlockEntity(BlockPos pos) {
		if (!this.world.getWorldBorder().contains(pos)) {
			return false;
		} else {
			return !(this.world instanceof ServerWorld)
				? true
				: this.getLevelType().isAfter(ChunkHolder.LevelType.TICKING) && ((ServerWorld)this.world).method_37116(ChunkPos.method_37232(pos));
		}
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		BlockPos blockPos = blockEntity.getPos();
		if (this.getBlockState(blockPos).hasBlockEntity()) {
			blockEntity.setWorld(this.world);
			blockEntity.cancelRemoval();
			BlockEntity blockEntity2 = (BlockEntity)this.blockEntities.put(blockPos.toImmutable(), blockEntity);
			if (blockEntity2 != null && blockEntity2 != blockEntity) {
				blockEntity2.markRemoved();
			}
		}
	}

	@Override
	public void addPendingBlockEntityNbt(NbtCompound nbt) {
		this.pendingBlockEntityNbts.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		if (blockEntity != null && !blockEntity.isRemoved()) {
			NbtCompound nbtCompound = blockEntity.writeNbt(new NbtCompound());
			nbtCompound.putBoolean("keepPacked", false);
			return nbtCompound;
		} else {
			NbtCompound nbtCompound = (NbtCompound)this.pendingBlockEntityNbts.get(pos);
			if (nbtCompound != null) {
				nbtCompound = nbtCompound.copy();
				nbtCompound.putBoolean("keepPacked", true);
			}

			return nbtCompound;
		}
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		if (this.canTickBlockEntities()) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(pos);
			if (blockEntity != null) {
				this.removeGameEventListener(blockEntity);
				blockEntity.markRemoved();
			}
		}

		this.removeBlockEntityTicker(pos);
	}

	private <T extends BlockEntity> void removeGameEventListener(T blockEntity) {
		if (!this.world.isClient) {
			Block block = blockEntity.getCachedState().getBlock();
			if (block instanceof BlockEntityProvider) {
				GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(this.world, blockEntity);
				if (gameEventListener != null) {
					int i = ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY());
					GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(i);
					gameEventDispatcher.removeListener(gameEventListener);
					if (gameEventDispatcher.isEmpty()) {
						this.gameEventDispatchers.remove(i);
					}
				}
			}
		}
	}

	private void removeBlockEntityTicker(BlockPos pos) {
		WorldChunk.WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = (WorldChunk.WrappedBlockEntityTickInvoker)this.blockEntityTickers.remove(pos);
		if (wrappedBlockEntityTickInvoker != null) {
			wrappedBlockEntityTickInvoker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER);
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

	public boolean isEmpty() {
		return false;
	}

	@Override
	public ChunkPos getPos() {
		return this.pos;
	}

	public void loadFromPacket(@Nullable BiomeArray biomes, PacketByteBuf buf, NbtCompound nbt, BitSet bitSet) {
		boolean bl = biomes != null;
		if (bl) {
			this.blockEntities.values().forEach(this::removeBlockEntity);
			this.blockEntities.clear();
		} else {
			this.blockEntities.values().removeIf(blockEntity -> {
				int ix = this.getSectionIndex(blockEntity.getPos().getY());
				if (bitSet.get(ix)) {
					blockEntity.markRemoved();
					return true;
				} else {
					return false;
				}
			});
		}

		for (int i = 0; i < this.sections.length; i++) {
			ChunkSection chunkSection = this.sections[i];
			if (!bitSet.get(i)) {
				if (bl && chunkSection != EMPTY_SECTION) {
					this.sections[i] = EMPTY_SECTION;
				}
			} else {
				if (chunkSection == EMPTY_SECTION) {
					chunkSection = new ChunkSection(this.sectionIndexToCoord(i));
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
			if (nbt.contains(string, NbtElement.LONG_ARRAY_TYPE)) {
				this.setHeightmap(type, nbt.getLongArray(string));
			}
		}
	}

	private void removeBlockEntity(BlockEntity blockEntity) {
		blockEntity.markRemoved();
		this.blockEntityTickers.remove(blockEntity.getPos());
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

	@Override
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return (NbtCompound)this.pendingBlockEntityNbts.get(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return StreamSupport.stream(
				BlockPos.iterate(this.pos.getStartX(), this.getBottomY(), this.pos.getStartZ(), this.pos.getEndX(), this.getTopY() - 1, this.pos.getEndZ()).spliterator(),
				false
			)
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
		return this.shouldSave;
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
					BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.sectionIndexToCoord(i), chunkPos);
					BlockState blockState = this.getBlockState(blockPos);
					BlockState blockState2 = Block.postProcessState(blockState, this.world, blockPos);
					this.world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.FORCE_STATE);
				}

				this.postProcessingLists[i].clear();
			}
		}

		this.disableTickSchedulers();

		for (BlockPos blockPos2 : ImmutableList.copyOf(this.pendingBlockEntityNbts.keySet())) {
			this.getBlockEntity(blockPos2);
		}

		this.pendingBlockEntityNbts.clear();
		this.upgradeData.upgrade(this);
	}

	@Nullable
	private BlockEntity loadBlockEntity(BlockPos pos, NbtCompound nbt) {
		BlockState blockState = this.getBlockState(pos);
		BlockEntity blockEntity;
		if ("DUMMY".equals(nbt.getString("id"))) {
			if (blockState.hasBlockEntity()) {
				blockEntity = ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
			} else {
				blockEntity = null;
				LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pos, blockState);
			}
		} else {
			blockEntity = BlockEntity.createFromNbt(pos, blockState, nbt);
		}

		if (blockEntity != null) {
			blockEntity.setWorld(this.world);
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
			((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.getBlockTickScheduler(), pos -> this.getBlockState(pos).getBlock());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		} else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
			((SimpleTickScheduler)this.blockTickScheduler).scheduleTo(this.world.getBlockTickScheduler());
			this.blockTickScheduler = DummyClientTickScheduler.get();
		}

		if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
			((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.getFluidTickScheduler(), pos -> this.getFluidState(pos).getFluid());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		} else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
			((SimpleTickScheduler)this.fluidTickScheduler).scheduleTo(this.world.getFluidTickScheduler());
			this.fluidTickScheduler = DummyClientTickScheduler.get();
		}
	}

	public void enableTickSchedulers(ServerWorld world) {
		if (this.blockTickScheduler == DummyClientTickScheduler.get()) {
			this.blockTickScheduler = new SimpleTickScheduler<>(
				Registry.BLOCK::getId, world.getBlockTickScheduler().getScheduledTicksInChunk(this.pos, true, false), world.getTime()
			);
			this.setShouldSave(true);
		}

		if (this.fluidTickScheduler == DummyClientTickScheduler.get()) {
			this.fluidTickScheduler = new SimpleTickScheduler<>(
				Registry.FLUID::getId, world.getFluidTickScheduler().getScheduledTicksInChunk(this.pos, true, false), world.getTime()
			);
			this.setShouldSave(true);
		}
	}

	@Override
	public int getBottomY() {
		return this.world.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.world.getHeight();
	}

	@Override
	public ChunkStatus getStatus() {
		return ChunkStatus.FULL;
	}

	public ChunkHolder.LevelType getLevelType() {
		return this.levelTypeProvider == null ? ChunkHolder.LevelType.BORDER : (ChunkHolder.LevelType)this.levelTypeProvider.get();
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

	public void removeAllBlockEntities() {
		this.blockEntities.values().forEach(this::removeBlockEntity);
	}

	public void updateAllBlockEntities() {
		this.blockEntities.values().forEach(blockEntity -> {
			this.updateGameEventListener(blockEntity);
			this.updateTicker(blockEntity);
		});
	}

	private <T extends BlockEntity> void updateGameEventListener(T blockEntity) {
		if (!this.world.isClient) {
			Block block = blockEntity.getCachedState().getBlock();
			if (block instanceof BlockEntityProvider) {
				GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(this.world, blockEntity);
				if (gameEventListener != null) {
					GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY()));
					gameEventDispatcher.addListener(gameEventListener);
				}
			}
		}
	}

	private <T extends BlockEntity> void updateTicker(T blockEntity) {
		BlockState blockState = blockEntity.getCachedState();
		BlockEntityTicker<T> blockEntityTicker = blockState.getBlockEntityTicker(this.world, (BlockEntityType<T>)blockEntity.getType());
		if (blockEntityTicker == null) {
			this.removeBlockEntityTicker(blockEntity.getPos());
		} else {
			this.blockEntityTickers.compute(blockEntity.getPos(), (pos, wrappedBlockEntityTickInvoker) -> {
				BlockEntityTickInvoker blockEntityTickInvoker = this.wrapTicker(blockEntity, blockEntityTicker);
				if (wrappedBlockEntityTickInvoker != null) {
					wrappedBlockEntityTickInvoker.setWrapped(blockEntityTickInvoker);
					return wrappedBlockEntityTickInvoker;
				} else if (this.canTickBlockEntities()) {
					WorldChunk.WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker2 = new WorldChunk.WrappedBlockEntityTickInvoker(blockEntityTickInvoker);
					this.world.addBlockEntityTicker(wrappedBlockEntityTickInvoker2);
					return wrappedBlockEntityTickInvoker2;
				} else {
					return null;
				}
			});
		}
	}

	private <T extends BlockEntity> BlockEntityTickInvoker wrapTicker(T blockEntity, BlockEntityTicker<T> blockEntityTicker) {
		return new WorldChunk.DirectBlockEntityTickInvoker<>(blockEntity, blockEntityTicker);
	}

	public static enum CreationType {
		IMMEDIATE,
		QUEUED,
		CHECK;
	}

	class DirectBlockEntityTickInvoker<T extends BlockEntity> implements BlockEntityTickInvoker {
		private final T blockEntity;
		private final BlockEntityTicker<T> ticker;
		private boolean hasWarned;

		DirectBlockEntityTickInvoker(T blockEntity, BlockEntityTicker<T> ticker) {
			this.blockEntity = blockEntity;
			this.ticker = ticker;
		}

		@Override
		public void tick() {
			if (!this.blockEntity.isRemoved() && this.blockEntity.hasWorld()) {
				BlockPos blockPos = this.blockEntity.getPos();
				if (WorldChunk.this.canTickBlockEntity(blockPos)) {
					try {
						Profiler profiler = WorldChunk.this.world.getProfiler();
						profiler.push(this::getName);
						BlockState blockState = WorldChunk.this.getBlockState(blockPos);
						if (this.blockEntity.getType().supports(blockState)) {
							this.ticker.tick(WorldChunk.this.world, this.blockEntity.getPos(), blockState, this.blockEntity);
							this.hasWarned = false;
						} else if (!this.hasWarned) {
							this.hasWarned = true;
							WorldChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", this::getName, this::getPos, () -> blockState);
						}

						profiler.pop();
					} catch (Throwable var5) {
						CrashReport crashReport = CrashReport.create(var5, "Ticking block entity");
						CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
						this.blockEntity.populateCrashReport(crashReportSection);
						throw new CrashException(crashReport);
					}
				}
			}
		}

		@Override
		public boolean isRemoved() {
			return this.blockEntity.isRemoved();
		}

		@Override
		public BlockPos getPos() {
			return this.blockEntity.getPos();
		}

		@Override
		public String getName() {
			return BlockEntityType.getId(this.blockEntity.getType()).toString();
		}

		public String toString() {
			return "Level ticker for " + this.getName() + "@" + this.getPos();
		}
	}

	class WrappedBlockEntityTickInvoker implements BlockEntityTickInvoker {
		private BlockEntityTickInvoker wrapped;

		WrappedBlockEntityTickInvoker(BlockEntityTickInvoker wrapped) {
			this.wrapped = wrapped;
		}

		void setWrapped(BlockEntityTickInvoker wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void tick() {
			this.wrapped.tick();
		}

		@Override
		public boolean isRemoved() {
			return this.wrapped.isRemoved();
		}

		@Override
		public BlockPos getPos() {
			return this.wrapped.getPos();
		}

		@Override
		public String getName() {
			return this.wrapped.getName();
		}

		public String toString() {
			return this.wrapped.toString() + " <wrapped>";
		}
	}
}
