package net.minecraft.world;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkType;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.MultiTickScheduler;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.slf4j.Logger;

public class ChunkRegion implements StructureWorldAccess {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final BoundedRegionArray<AbstractChunkHolder> chunks;
	private final Chunk centerPos;
	private final ServerWorld world;
	private final long seed;
	private final WorldProperties levelProperties;
	private final Random random;
	private final DimensionType dimension;
	private final MultiTickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getBlockTickScheduler());
	private final MultiTickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getFluidTickScheduler());
	private final BiomeAccess biomeAccess;
	private final ChunkGenerationStep generationStep;
	@Nullable
	private Supplier<String> currentlyGeneratingStructureName;
	private final AtomicLong tickOrder = new AtomicLong();
	private static final Identifier WORLDGEN_REGION_RANDOM_ID = Identifier.ofVanilla("worldgen_region_random");

	public ChunkRegion(ServerWorld world, BoundedRegionArray<AbstractChunkHolder> chunks, ChunkGenerationStep generationStep, Chunk centerPos) {
		this.generationStep = generationStep;
		this.chunks = chunks;
		this.centerPos = centerPos;
		this.world = world;
		this.seed = world.getSeed();
		this.levelProperties = world.getLevelProperties();
		this.random = world.getChunkManager().getNoiseConfig().getOrCreateRandomDeriver(WORLDGEN_REGION_RANDOM_ID).split(this.centerPos.getPos().getStartPos());
		this.dimension = world.getDimension();
		this.biomeAccess = new BiomeAccess(this, BiomeAccess.hashSeed(this.seed));
	}

	public boolean needsBlending(ChunkPos chunkPos, int checkRadius) {
		return this.world.getChunkManager().chunkLoadingManager.needsBlending(chunkPos, checkRadius);
	}

	public ChunkPos getCenterPos() {
		return this.centerPos.getPos();
	}

	@Override
	public void setCurrentlyGeneratingStructureName(@Nullable Supplier<String> structureName) {
		this.currentlyGeneratingStructureName = structureName;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
	}

	@Nullable
	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		int i = this.centerPos.getPos().getChebyshevDistance(chunkX, chunkZ);
		ChunkStatus chunkStatus = i >= this.generationStep.directDependencies().size() ? null : this.generationStep.directDependencies().get(i);
		AbstractChunkHolder abstractChunkHolder;
		if (chunkStatus != null) {
			abstractChunkHolder = this.chunks.get(chunkX, chunkZ);
			if (leastStatus.isAtMost(chunkStatus)) {
				Chunk chunk = abstractChunkHolder.getUncheckedOrNull(chunkStatus);
				if (chunk != null) {
					return chunk;
				}
			}
		} else {
			abstractChunkHolder = null;
		}

		CrashReport crashReport = CrashReport.create(
			new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk"
		);
		CrashReportSection crashReportSection = crashReport.addElement("Chunk request details");
		crashReportSection.add("Requested chunk", String.format(Locale.ROOT, "%d, %d", chunkX, chunkZ));
		crashReportSection.add("Generating status", (CrashCallable<String>)(() -> this.generationStep.targetStatus().getId()));
		crashReportSection.add("Requested status", leastStatus::getId);
		crashReportSection.add(
			"Actual status", (CrashCallable<String>)(() -> abstractChunkHolder == null ? "[out of cache bounds]" : abstractChunkHolder.getActualStatus().getId())
		);
		crashReportSection.add("Maximum allowed status", (CrashCallable<String>)(() -> chunkStatus == null ? "null" : chunkStatus.getId()));
		crashReportSection.add("Dependencies", this.generationStep.directDependencies()::toString);
		crashReportSection.add("Requested distance", i);
		crashReportSection.add("Generating chunk", this.centerPos.getPos()::toString);
		throw new CrashException(crashReport);
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		int i = this.centerPos.getPos().getChebyshevDistance(chunkX, chunkZ);
		return i < this.generationStep.directDependencies().size();
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.getChunk(pos).getFluidState(pos);
	}

	@Nullable
	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, Predicate<Entity> targetPredicate) {
		return null;
	}

	@Override
	public int getAmbientDarkness() {
		return 0;
	}

	@Override
	public BiomeAccess getBiomeAccess() {
		return this.biomeAccess;
	}

	@Override
	public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
		return this.world.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.world.getLightingProvider();
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		BlockState blockState = this.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			if (drop) {
				BlockEntity blockEntity = blockState.hasBlockEntity() ? this.getBlockEntity(pos) : null;
				Block.dropStacks(blockState, this.world, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
			}

			return this.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, maxUpdateDepth);
		}
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		Chunk chunk = this.getChunk(pos);
		BlockEntity blockEntity = chunk.getBlockEntity(pos);
		if (blockEntity != null) {
			return blockEntity;
		} else {
			NbtCompound nbtCompound = chunk.getBlockEntityNbt(pos);
			BlockState blockState = chunk.getBlockState(pos);
			if (nbtCompound != null) {
				if ("DUMMY".equals(nbtCompound.getString("id"))) {
					if (!blockState.hasBlockEntity()) {
						return null;
					}

					blockEntity = ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
				} else {
					blockEntity = BlockEntity.createFromNbt(pos, blockState, nbtCompound, this.world.getRegistryManager());
				}

				if (blockEntity != null) {
					chunk.setBlockEntity(blockEntity);
					return blockEntity;
				}
			}

			if (blockState.hasBlockEntity()) {
				LOGGER.warn("Tried to access a block entity before it was created. {}", pos);
			}

			return null;
		}
	}

	@Override
	public boolean isValidForSetBlock(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		ChunkPos chunkPos = this.getCenterPos();
		int k = Math.abs(chunkPos.x - i);
		int l = Math.abs(chunkPos.z - j);
		if (k <= this.generationStep.blockStateWriteRadius() && l <= this.generationStep.blockStateWriteRadius()) {
			if (this.centerPos.hasBelowZeroRetrogen()) {
				HeightLimitView heightLimitView = this.centerPos.getHeightLimitView();
				if (pos.getY() < heightLimitView.getBottomY() || pos.getY() >= heightLimitView.getTopY()) {
					return false;
				}
			}

			return true;
		} else {
			Util.error(
				"Detected setBlock in a far chunk ["
					+ i
					+ ", "
					+ j
					+ "], pos: "
					+ pos
					+ ", status: "
					+ this.generationStep.targetStatus()
					+ (this.currentlyGeneratingStructureName == null ? "" : ", currently generating: " + (String)this.currentlyGeneratingStructureName.get())
			);
			return false;
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		if (!this.isValidForSetBlock(pos)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(pos);
			BlockState blockState = chunk.setBlockState(pos, state, false);
			if (blockState != null) {
				this.world.onBlockChanged(pos, blockState, state);
			}

			if (state.hasBlockEntity()) {
				if (chunk.getStatus().getChunkType() == ChunkType.LEVELCHUNK) {
					BlockEntity blockEntity = ((BlockEntityProvider)state.getBlock()).createBlockEntity(pos, state);
					if (blockEntity != null) {
						chunk.setBlockEntity(blockEntity);
					} else {
						chunk.removeBlockEntity(pos);
					}
				} else {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putInt("x", pos.getX());
					nbtCompound.putInt("y", pos.getY());
					nbtCompound.putInt("z", pos.getZ());
					nbtCompound.putString("id", "DUMMY");
					chunk.addPendingBlockEntityNbt(nbtCompound);
				}
			} else if (blockState != null && blockState.hasBlockEntity()) {
				chunk.removeBlockEntity(pos);
			}

			if (state.shouldPostProcess(this, pos)) {
				this.markBlockForPostProcessing(pos);
			}

			return true;
		}
	}

	private void markBlockForPostProcessing(BlockPos pos) {
		this.getChunk(pos).markBlockForPostProcessing(pos);
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		int i = ChunkSectionPos.getSectionCoord(entity.getBlockX());
		int j = ChunkSectionPos.getSectionCoord(entity.getBlockZ());
		this.getChunk(i, j).addEntity(entity);
		return true;
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.world.getWorldBorder();
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Deprecated
	@Override
	public ServerWorld toServerWorld() {
		return this.world;
	}

	@Override
	public DynamicRegistryManager getRegistryManager() {
		return this.world.getRegistryManager();
	}

	@Override
	public FeatureSet getEnabledFeatures() {
		return this.world.getEnabledFeatures();
	}

	@Override
	public WorldProperties getLevelProperties() {
		return this.levelProperties;
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		if (!this.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
		}
	}

	@Nullable
	@Override
	public MinecraftServer getServer() {
		return this.world.getServer();
	}

	@Override
	public ChunkManager getChunkManager() {
		return this.world.getChunkManager();
	}

	@Override
	public long getSeed() {
		return this.seed;
	}

	@Override
	public QueryableTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	@Override
	public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Override
	public int getSeaLevel() {
		return this.world.getSeaLevel();
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		return this.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)).sampleHeightmap(heightmap, x & 15, z & 15) + 1;
	}

	@Override
	public void playSound(@Nullable PlayerEntity source, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	@Override
	public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
	}

	@Override
	public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {
	}

	@Override
	public DimensionType getDimension() {
		return this.dimension;
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	@Override
	public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
		return state.test(this.getFluidState(pos));
	}

	@Override
	public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<PlayerEntity> getPlayers() {
		return Collections.emptyList();
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
	public long getTickOrder() {
		return this.tickOrder.getAndIncrement();
	}
}
