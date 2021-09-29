package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
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
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
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
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk extends Chunk {
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
	private final Map<BlockPos, WorldChunk.WrappedBlockEntityTickInvoker> blockEntityTickers = Maps.<BlockPos, WorldChunk.WrappedBlockEntityTickInvoker>newHashMap();
	private boolean loadedToWorld;
	final World world;
	@Nullable
	private Supplier<ChunkHolder.LevelType> levelTypeProvider;
	@Nullable
	private Consumer<WorldChunk> loadToWorldConsumer;
	private final Int2ObjectMap<GameEventDispatcher> gameEventDispatchers;

	public WorldChunk(World world, ChunkPos pos) {
		this(world, pos, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
	}

	public WorldChunk(
		World world,
		ChunkPos pos,
		UpgradeData upgradeData,
		TickScheduler<Block> blockTickScheduler,
		TickScheduler<Fluid> fluidTickScheduler,
		long inhabitedTime,
		@Nullable ChunkSection[] sectionArrayInitializer,
		@Nullable Consumer<WorldChunk> loadToWorldConsumer
	) {
		super(
			pos, upgradeData, world, world.getRegistryManager().get(Registry.BIOME_KEY), inhabitedTime, sectionArrayInitializer, blockTickScheduler, fluidTickScheduler
		);
		this.world = world;
		this.gameEventDispatchers = new Int2ObjectOpenHashMap<>();

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (ChunkStatus.FULL.getHeightmapTypes().contains(type)) {
				this.heightmaps.put(type, new Heightmap(this, type));
			}
		}

		this.loadToWorldConsumer = loadToWorldConsumer;
	}

	public WorldChunk(ServerWorld world, ProtoChunk protoChunk, @Nullable Consumer<WorldChunk> loadToWorldConsumer) {
		this(
			world,
			protoChunk.getPos(),
			protoChunk.getUpgradeData(),
			protoChunk.getBlockTickScheduler(),
			protoChunk.getFluidTickScheduler(),
			protoChunk.getInhabitedTime(),
			protoChunk.getSectionArray(),
			loadToWorldConsumer
		);

		for (BlockEntity blockEntity : protoChunk.getBlockEntities().values()) {
			this.setBlockEntity(blockEntity);
		}

		this.blockEntityNbts.putAll(protoChunk.getBlockEntityNbts());

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
		this.needsSaving = true;
	}

	@Override
	public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
		return this.gameEventDispatchers.computeIfAbsent(ySectionCoord, sectionCoord -> new SimpleGameEventDispatcher(this.world));
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
				if (l >= 0 && l < this.sectionArray.length) {
					ChunkSection chunkSection = this.sectionArray[l];
					if (!chunkSection.isEmpty()) {
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
			if (i >= 0 && i < this.sectionArray.length) {
				ChunkSection chunkSection = this.sectionArray[i];
				if (!chunkSection.isEmpty()) {
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
		ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
		boolean bl = chunkSection.isEmpty();
		if (bl && state.isAir()) {
			return null;
		} else {
			int j = pos.getX() & 15;
			int k = i & 15;
			int l = pos.getZ() & 15;
			BlockState blockState = chunkSection.setBlockState(j, k, l, state);
			if (blockState == state) {
				return null;
			} else {
				Block block = state.getBlock();
				((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(j, i, l, state);
				((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(j, i, l, state);
				((Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(j, i, l, state);
				((Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(j, i, l, state);
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

				if (!chunkSection.getBlockState(j, k, l).isOf(block)) {
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

					this.needsSaving = true;
					return blockState;
				}
			}
		}
	}

	@Deprecated
	@Override
	public void addEntity(Entity entity) {
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
			NbtCompound nbtCompound = (NbtCompound)this.blockEntityNbts.remove(pos);
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
			return !(this.world instanceof ServerWorld serverWorld)
				? true
				: this.getLevelType().isAfter(ChunkHolder.LevelType.TICKING) && serverWorld.method_37116(ChunkPos.toLong(pos));
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

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		if (blockEntity != null && !blockEntity.isRemoved()) {
			NbtCompound nbtCompound = blockEntity.createNbtWithIdentifyingData();
			nbtCompound.putBoolean("keepPacked", false);
			return nbtCompound;
		} else {
			NbtCompound nbtCompound = (NbtCompound)this.blockEntityNbts.get(pos);
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

	public boolean isEmpty() {
		return false;
	}

	public void loadFromPacket(PacketByteBuf packetByteBuf, NbtCompound nbtCompound, Consumer<ChunkData.BlockEntityVisitor> consumer) {
		this.clear();

		for (ChunkSection chunkSection : this.sectionArray) {
			chunkSection.fromPacket(packetByteBuf);
		}

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (nbtCompound.contains(string, NbtElement.LONG_ARRAY_TYPE)) {
				this.setHeightmap(type, nbtCompound.getLongArray(string));
			}
		}

		consumer.accept((ChunkData.BlockEntityVisitor)(blockPos, blockEntityType, nbtCompoundx) -> {
			BlockEntity blockEntity = this.getBlockEntity(blockPos, WorldChunk.CreationType.IMMEDIATE);
			if (blockEntity != null && nbtCompoundx != null && blockEntity.getType() == blockEntityType) {
				blockEntity.readNbt(nbtCompoundx);
			}
		});
	}

	public void setLoadedToWorld(boolean loaded) {
		this.loadedToWorld = loaded;
	}

	public World getWorld() {
		return this.world;
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return StreamSupport.stream(
				BlockPos.iterate(this.pos.getStartX(), this.getBottomY(), this.pos.getStartZ(), this.pos.getEndX(), this.getTopY() - 1, this.pos.getEndZ()).spliterator(),
				false
			)
			.filter(blockPos -> this.getBlockState(blockPos).getLuminance() != 0);
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

		for (BlockPos blockPos2 : ImmutableList.copyOf(this.blockEntityNbts.keySet())) {
			this.getBlockEntity(blockPos2);
		}

		this.blockEntityNbts.clear();
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
	public ChunkStatus getStatus() {
		return ChunkStatus.FULL;
	}

	public ChunkHolder.LevelType getLevelType() {
		return this.levelTypeProvider == null ? ChunkHolder.LevelType.BORDER : (ChunkHolder.LevelType)this.levelTypeProvider.get();
	}

	public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> levelTypeProvider) {
		this.levelTypeProvider = levelTypeProvider;
	}

	public void clear() {
		this.blockEntities.values().forEach(BlockEntity::markRemoved);
		this.blockEntities.clear();
		this.blockEntityTickers.values().forEach(ticker -> ticker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER));
		this.blockEntityTickers.clear();
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
