package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegion implements IWorld {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Chunk> chunks;
	private final int centerChunkX;
	private final int centerChunkZ;
	private final int width;
	private final ServerWorld world;
	private final long seed;
	private final int seaLevel;
	private final LevelProperties levelProperties;
	private final Random random;
	private final Dimension dimension;
	private final ChunkGeneratorConfig generatorSettings;
	private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(blockPos -> this.getChunk(blockPos).getBlockTickScheduler());
	private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(blockPos -> this.getChunk(blockPos).getFluidTickScheduler());
	private final BiomeAccess biomeAccess;
	private final ChunkPos field_23640;
	private final ChunkPos field_23641;

	public ChunkRegion(ServerWorld world, List<Chunk> chunks) {
		int i = MathHelper.floor(Math.sqrt((double)chunks.size()));
		if (i * i != chunks.size()) {
			throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Cache size is not a square."));
		} else {
			ChunkPos chunkPos = ((Chunk)chunks.get(chunks.size() / 2)).getPos();
			this.chunks = chunks;
			this.centerChunkX = chunkPos.x;
			this.centerChunkZ = chunkPos.z;
			this.width = i;
			this.world = world;
			this.seed = world.getSeed();
			this.generatorSettings = world.getChunkManager().getChunkGenerator().getConfig();
			this.seaLevel = world.getSeaLevel();
			this.levelProperties = world.getLevelProperties();
			this.random = world.getRandom();
			this.dimension = world.getDimension();
			this.biomeAccess = new BiomeAccess(this, LevelProperties.sha256Hash(this.seed), this.dimension.getType().getBiomeAccessType());
			this.field_23640 = ((Chunk)chunks.get(0)).getPos();
			this.field_23641 = ((Chunk)chunks.get(chunks.size() - 1)).getPos();
		}
	}

	public int getCenterChunkX() {
		return this.centerChunkX;
	}

	public int getCenterChunkZ() {
		return this.centerChunkZ;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
	}

	@Nullable
	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		if (this.isChunkLoaded(chunkX, chunkZ)) {
			int i = chunkX - this.field_23640.x;
			int j = chunkZ - this.field_23640.z;
			Chunk chunk = (Chunk)this.chunks.get(i + j * this.width);
			if (chunk.getStatus().isAtLeast(leastStatus)) {
				return chunk;
			}
		} else {
			Chunk chunk = null;
		}

		if (!create) {
			return null;
		} else {
			LOGGER.error("Requested chunk : {} {}", chunkX, chunkZ);
			LOGGER.error("Region bounds : {} {} | {} {}", this.field_23640.x, this.field_23640.z, this.field_23641.x, this.field_23641.z);
			return new EmptyChunk(this.world, new ChunkPos(chunkX, chunkZ));
		}
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return chunkX >= this.field_23640.x && chunkX <= this.field_23641.x && chunkZ >= this.field_23640.z && chunkZ <= this.field_23641.z;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4).getBlockState(pos);
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

	@Environment(EnvType.CLIENT)
	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.world.getLightingProvider();
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity) {
		BlockState blockState = this.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			if (drop) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
				Block.dropStacks(blockState, this.world, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
			}

			return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
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
			CompoundTag compoundTag = chunk.getBlockEntityTagAt(pos);
			BlockState blockState = chunk.getBlockState(pos);
			if (compoundTag != null) {
				if ("DUMMY".equals(compoundTag.getString("id"))) {
					Block block = blockState.getBlock();
					if (!(block instanceof BlockEntityProvider)) {
						return null;
					}

					blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
				} else {
					blockEntity = BlockEntity.createFromTag(blockState, compoundTag);
				}

				if (blockEntity != null) {
					chunk.setBlockEntity(pos, blockEntity);
					return blockEntity;
				}
			}

			if (blockState.getBlock() instanceof BlockEntityProvider) {
				LOGGER.warn("Tried to access a block entity before it was created. {}", pos);
			}

			return null;
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		Chunk chunk = this.getChunk(pos);
		BlockState blockState = chunk.setBlockState(pos, state, false);
		if (blockState != null) {
			this.world.onBlockChanged(pos, blockState, state);
		}

		Block block = state.getBlock();
		if (block.hasBlockEntity()) {
			if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
				chunk.setBlockEntity(pos, ((BlockEntityProvider)block).createBlockEntity(this));
			} else {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putInt("x", pos.getX());
				compoundTag.putInt("y", pos.getY());
				compoundTag.putInt("z", pos.getZ());
				compoundTag.putString("id", "DUMMY");
				chunk.addPendingBlockEntityTag(compoundTag);
			}
		} else if (blockState != null && blockState.getBlock().hasBlockEntity()) {
			chunk.removeBlockEntity(pos);
		}

		if (state.shouldPostProcess(this, pos)) {
			this.markBlockForPostProcessing(pos);
		}

		return true;
	}

	private void markBlockForPostProcessing(BlockPos blockPos) {
		this.getChunk(blockPos).markBlockForPostProcessing(blockPos);
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		int i = MathHelper.floor(entity.getX() / 16.0);
		int j = MathHelper.floor(entity.getZ() / 16.0);
		this.getChunk(i, j).addEntity(entity);
		return true;
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
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
	public ServerWorld getWorld() {
		return this.world;
	}

	@Override
	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		if (!this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
		}
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
		return this.seaLevel;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		return this.getChunk(x >> 4, z >> 4).sampleHeightmap(heightmap, x & 15, z & 15) + 1;
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	@Override
	public void playLevelEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
	}

	@Override
	public Dimension getDimension() {
		return this.dimension;
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	@Override
	public <T extends Entity> List<T> getEntities(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<PlayerEntity> getPlayers() {
		return Collections.emptyList();
	}
}
