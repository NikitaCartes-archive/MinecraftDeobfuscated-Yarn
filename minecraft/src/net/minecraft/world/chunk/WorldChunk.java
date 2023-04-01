package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.slf4j.Logger;

public class WorldChunk extends Chunk {
	static final Logger LOGGER = LogUtils.getLogger();
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
	private boolean shouldRenderOnUpdate = false;
	final World world;
	@Nullable
	private Supplier<ChunkHolder.LevelType> levelTypeProvider;
	@Nullable
	private WorldChunk.EntityLoader entityLoader;
	private final Int2ObjectMap<GameEventDispatcher> gameEventDispatchers;
	private final ChunkTickScheduler<Block> blockTickScheduler;
	private final ChunkTickScheduler<Fluid> fluidTickScheduler;

	public WorldChunk(World world, ChunkPos pos) {
		this(world, pos, UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler<>(), new ChunkTickScheduler<>(), 0L, null, null, null);
	}

	public WorldChunk(
		World world,
		ChunkPos pos,
		UpgradeData upgradeData,
		ChunkTickScheduler<Block> blockTickScheduler,
		ChunkTickScheduler<Fluid> fluidTickScheduler,
		long inhabitedTime,
		@Nullable ChunkSection[] sectionArrayInitializer,
		@Nullable WorldChunk.EntityLoader entityLoader,
		@Nullable BlendingData blendingData
	) {
		super(pos, upgradeData, world, world.getRegistryManager().get(RegistryKeys.BIOME), inhabitedTime, sectionArrayInitializer, blendingData);
		this.world = world;
		this.gameEventDispatchers = new Int2ObjectOpenHashMap<>();

		for (Heightmap.Type type : Heightmap.Type.values()) {
			if (ChunkStatus.FULL.getHeightmapTypes().contains(type)) {
				this.heightmaps.put(type, new Heightmap(this, type));
			}
		}

		this.entityLoader = entityLoader;
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
	}

	public WorldChunk(ServerWorld world, ProtoChunk protoChunk, @Nullable WorldChunk.EntityLoader entityLoader) {
		this(
			world,
			protoChunk.getPos(),
			protoChunk.getUpgradeData(),
			protoChunk.getBlockProtoTickScheduler(),
			protoChunk.getFluidProtoTickScheduler(),
			protoChunk.getInhabitedTime(),
			protoChunk.getSectionArray(),
			entityLoader,
			protoChunk.getBlendingData()
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
	public BasicTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	@Override
	public BasicTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Override
	public Chunk.TickSchedulers getTickSchedulers() {
		return new Chunk.TickSchedulers(this.blockTickScheduler, this.fluidTickScheduler);
	}

	@Override
	public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
		return this.world instanceof ServerWorld serverWorld
			? this.gameEventDispatchers
				.computeIfAbsent(ySectionCoord, (Int2ObjectFunction<? extends GameEventDispatcher>)(sectionCoord -> new SimpleGameEventDispatcher(serverWorld)))
			: super.getGameEventDispatcher(ySectionCoord);
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
					if ((Boolean)class_8293.field_43583.get()) {
						blockState.onStateReplaced(this.world, pos, state, moved);
					} else if (blockState.hasBlockEntity() && !blockState.isOf(state.getBlock())) {
						this.world.removeBlockEntity(pos);
					}
				} else if (!blockState.isOf(block) && bl3) {
					this.removeBlockEntity(pos);
				}

				if (!chunkSection.getBlockState(j, k, l).isOf(block)) {
					return null;
				} else {
					if (!this.world.isClient && (Boolean)class_8293.field_43583.get()) {
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
			if (this.world instanceof ServerWorld serverWorld) {
				this.updateGameEventListener(blockEntity, serverWorld);
			}

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
				: this.getLevelType().isAfter(ChunkHolder.LevelType.TICKING) && serverWorld.isChunkLoaded(ChunkPos.toLong(pos));
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
				if (this.world instanceof ServerWorld serverWorld) {
					this.removeGameEventListener(blockEntity, serverWorld);
				}

				blockEntity.markRemoved();
			}
		}

		this.removeBlockEntityTicker(pos);
	}

	private <T extends BlockEntity> void removeGameEventListener(T blockEntity, ServerWorld world) {
		Block block = blockEntity.getCachedState().getBlock();
		if (block instanceof BlockEntityProvider) {
			GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(world, blockEntity);
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

	private void removeBlockEntityTicker(BlockPos pos) {
		WorldChunk.WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = (WorldChunk.WrappedBlockEntityTickInvoker)this.blockEntityTickers.remove(pos);
		if (wrappedBlockEntityTickInvoker != null) {
			wrappedBlockEntityTickInvoker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER);
		}
	}

	public void loadEntities() {
		if (this.entityLoader != null) {
			this.entityLoader.run(this);
			this.entityLoader = null;
		}
	}

	public boolean isEmpty() {
		return false;
	}

	public void loadFromPacket(PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer) {
		this.clear();

		for (ChunkSection chunkSection : this.sectionArray) {
			chunkSection.readDataPacket(buf);
		}

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (nbt.contains(string, NbtElement.LONG_ARRAY_TYPE)) {
				this.setHeightmap(type, nbt.getLongArray(string));
			}
		}

		consumer.accept((ChunkData.BlockEntityVisitor)(pos, blockEntityType, nbtx) -> {
			BlockEntity blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
			if (blockEntity != null && nbtx != null && blockEntity.getType() == blockEntityType) {
				blockEntity.readNbt(nbtx);
			}
		});
	}

	public void loadBiomeFromPacket(PacketByteBuf buf) {
		for (ChunkSection chunkSection : this.sectionArray) {
			chunkSection.readBiomePacket(buf);
		}
	}

	public void setLoadedToWorld(boolean loadedToWorld) {
		this.loadedToWorld = loadedToWorld;
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
					FluidState fluidState = blockState.getFluidState();
					if (!fluidState.isEmpty()) {
						fluidState.onScheduledTick(this.world, blockPos);
					}

					if (!(blockState.getBlock() instanceof FluidBlock)) {
						BlockState blockState2 = Block.postProcessState(blockState, this.world, blockPos);
						this.world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.FORCE_STATE);
					}
				}

				this.postProcessingLists[i].clear();
			}
		}

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

	public void disableTickSchedulers(long time) {
		this.blockTickScheduler.disable(time);
		this.fluidTickScheduler.disable(time);
	}

	public void addChunkTickSchedulers(ServerWorld world) {
		world.getBlockTickScheduler().addChunkTickScheduler(this.pos, this.blockTickScheduler);
		world.getFluidTickScheduler().addChunkTickScheduler(this.pos, this.fluidTickScheduler);
	}

	public void removeChunkTickSchedulers(ServerWorld world) {
		world.getBlockTickScheduler().removeChunkTickScheduler(this.pos);
		world.getFluidTickScheduler().removeChunkTickScheduler(this.pos);
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
			if (this.world instanceof ServerWorld serverWorld) {
				this.updateGameEventListener(blockEntity, serverWorld);
			}

			this.updateTicker(blockEntity);
		});
	}

	private <T extends BlockEntity> void updateGameEventListener(T blockEntity, ServerWorld world) {
		Block block = blockEntity.getCachedState().getBlock();
		if (block instanceof BlockEntityProvider) {
			GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(world, blockEntity);
			if (gameEventListener != null) {
				this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY())).addListener(gameEventListener);
			}
		}
	}

	private <T extends BlockEntity> void updateTicker(T blockEntity) {
		BlockState blockState = blockEntity.getCachedState();
		BlockEntityTicker<T> blockEntityTicker = blockState.getBlockEntityTicker(this.world, (BlockEntityType<T>)blockEntity.getType());
		if (blockEntityTicker == null) {
			this.removeBlockEntityTicker(blockEntity.getPos());
		} else {
			this.blockEntityTickers.compute(blockEntity.getPos(), (pos, ticker) -> {
				BlockEntityTickInvoker blockEntityTickInvoker = this.wrapTicker(blockEntity, blockEntityTicker);
				if (ticker != null) {
					ticker.setWrapped(blockEntityTickInvoker);
					return ticker;
				} else if (this.canTickBlockEntities()) {
					WorldChunk.WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = new WorldChunk.WrappedBlockEntityTickInvoker(blockEntityTickInvoker);
					this.world.addBlockEntityTicker(wrappedBlockEntityTickInvoker);
					return wrappedBlockEntityTickInvoker;
				} else {
					return null;
				}
			});
		}
	}

	private <T extends BlockEntity> BlockEntityTickInvoker wrapTicker(T blockEntity, BlockEntityTicker<T> blockEntityTicker) {
		return new WorldChunk.DirectBlockEntityTickInvoker<>(blockEntity, blockEntityTicker);
	}

	public boolean shouldRenderOnUpdate() {
		return this.shouldRenderOnUpdate;
	}

	public void setShouldRenderOnUpdate(boolean shouldRenderOnUpdate) {
		this.shouldRenderOnUpdate = shouldRenderOnUpdate;
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
							WorldChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", LogUtils.defer(this::getName), LogUtils.defer(this::getPos), blockState);
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

	@FunctionalInterface
	public interface EntityLoader {
		void run(WorldChunk chunk);
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
