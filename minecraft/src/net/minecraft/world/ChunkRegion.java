package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegion implements StructureWorldAccess {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Chunk> chunks;
	private final ChunkPos centerPos;
	private final int width;
	private final ServerWorld world;
	private final long seed;
	private final WorldProperties levelProperties;
	private final Random random;
	private final DimensionType dimension;
	private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getBlockTickScheduler());
	private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getFluidTickScheduler());
	private final BiomeAccess biomeAccess;
	private final ChunkPos lowerCorner;
	private final ChunkPos upperCorner;
	private final StructureAccessor structureAccessor;

	public ChunkRegion(ServerWorld world, List<Chunk> chunks) {
		int i = MathHelper.floor(Math.sqrt((double)chunks.size()));
		if (i * i != chunks.size()) {
			throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Cache size is not a square."));
		} else {
			ChunkPos chunkPos = ((Chunk)chunks.get(chunks.size() / 2)).getPos();
			this.chunks = chunks;
			this.centerPos = chunkPos;
			this.width = i;
			this.world = world;
			this.seed = world.getSeed();
			this.levelProperties = world.getLevelProperties();
			this.random = world.getRandom();
			this.dimension = world.getDimension();
			this.biomeAccess = new BiomeAccess(this, BiomeAccess.hashSeed(this.seed), world.getDimension().getBiomeAccessType());
			this.lowerCorner = ((Chunk)chunks.get(0)).getPos();
			this.upperCorner = ((Chunk)chunks.get(chunks.size() - 1)).getPos();
			this.structureAccessor = world.getStructureAccessor().forRegion(this);
		}
	}

	public ChunkPos getCenterPos() {
		return this.centerPos;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
	}

	@Nullable
	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		Chunk chunk;
		if (this.isChunkLoaded(chunkX, chunkZ)) {
			int i = chunkX - this.lowerCorner.x;
			int j = chunkZ - this.lowerCorner.z;
			chunk = (Chunk)this.chunks.get(i + j * this.width);
			if (chunk.getStatus().isAtLeast(leastStatus)) {
				return chunk;
			}
		} else {
			chunk = null;
		}

		if (!create) {
			return null;
		} else {
			LOGGER.error("Requested chunk : {} {}", chunkX, chunkZ);
			LOGGER.error("Region bounds : {} {} | {} {}", this.lowerCorner.x, this.lowerCorner.z, this.upperCorner.x, this.upperCorner.z);
			if (chunk != null) {
				throw (RuntimeException)Util.throwOrPause(
					new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", leastStatus, chunk.getStatus(), chunkX, chunkZ))
				);
			} else {
				throw (RuntimeException)Util.throwOrPause(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", chunkX, chunkZ)));
			}
		}
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return chunkX >= this.lowerCorner.x && chunkX <= this.upperCorner.x && chunkZ >= this.lowerCorner.z && chunkZ <= this.upperCorner.z;
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
	public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
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
					blockEntity = BlockEntity.createFromNbt(pos, blockState, nbtCompound);
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
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		Chunk chunk = this.getChunk(pos);
		BlockState blockState = chunk.setBlockState(pos, state, false);
		if (blockState != null) {
			this.world.onBlockChanged(pos, blockState, state);
		}

		if (state.hasBlockEntity()) {
			if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
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
	public TickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
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
	public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	@Override
	public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
	}

	@Override
	public void emitGameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos) {
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
	public Stream<? extends StructureStart<?>> getStructures(ChunkSectionPos pos, StructureFeature<?> feature) {
		return this.structureAccessor.getStructuresWithChildren(pos, feature);
	}

	@Override
	public int getBottomY() {
		return this.world.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.world.getHeight();
	}
}
