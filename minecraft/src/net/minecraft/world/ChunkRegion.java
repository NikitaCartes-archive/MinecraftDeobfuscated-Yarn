package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
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
	private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(blockPos -> this.method_16955(blockPos).getBlockTickScheduler());
	private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(blockPos -> this.method_16955(blockPos).getFluidTickScheduler());

	public ChunkRegion(ServerWorld serverWorld, List<Chunk> list) {
		int i = MathHelper.floor(Math.sqrt((double)list.size()));
		if (i * i != list.size()) {
			throw new IllegalStateException("Cache size is not a square.");
		} else {
			ChunkPos chunkPos = ((Chunk)list.get(list.size() / 2)).getPos();
			this.chunks = list;
			this.centerChunkX = chunkPos.x;
			this.centerChunkZ = chunkPos.z;
			this.width = i;
			this.world = serverWorld;
			this.seed = serverWorld.getSeed();
			this.generatorSettings = serverWorld.method_14178().getChunkGenerator().method_12109();
			this.seaLevel = serverWorld.getSeaLevel();
			this.levelProperties = serverWorld.method_8401();
			this.random = serverWorld.getRandom();
			this.dimension = serverWorld.method_8597();
		}
	}

	public int getCenterChunkX() {
		return this.centerChunkX;
	}

	public int getCenterChunkZ() {
		return this.centerChunkZ;
	}

	@Override
	public Chunk method_8392(int i, int j) {
		return this.method_16956(i, j, ChunkStatus.EMPTY);
	}

	@Nullable
	@Override
	public Chunk method_8402(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		Chunk chunk;
		if (this.isChunkLoaded(i, j)) {
			ChunkPos chunkPos = ((Chunk)this.chunks.get(0)).getPos();
			int k = i - chunkPos.x;
			int l = j - chunkPos.z;
			chunk = (Chunk)this.chunks.get(k + l * this.width);
			if (chunk.method_12009().isAfter(chunkStatus)) {
				return chunk;
			}
		} else {
			chunk = null;
		}

		if (!bl) {
			return null;
		} else {
			Chunk chunk2 = (Chunk)this.chunks.get(0);
			Chunk chunk3 = (Chunk)this.chunks.get(this.chunks.size() - 1);
			LOGGER.error("Requested chunk : {} {}", i, j);
			LOGGER.error("Region bounds : {} {} | {} {}", chunk2.getPos().x, chunk2.getPos().z, chunk3.getPos().x, chunk3.getPos().z);
			if (chunk != null) {
				throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkStatus, chunk.method_12009(), i, j));
			} else {
				throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
			}
		}
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		Chunk chunk = (Chunk)this.chunks.get(0);
		Chunk chunk2 = (Chunk)this.chunks.get(this.chunks.size() - 1);
		return i >= chunk.getPos().x && i <= chunk2.getPos().x && j >= chunk.getPos().z && j <= chunk2.getPos().z;
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		return this.method_8392(blockPos.getX() >> 4, blockPos.getZ() >> 4).method_8320(blockPos);
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		return this.method_16955(blockPos).method_8316(blockPos);
	}

	@Nullable
	@Override
	public PlayerEntity method_8604(double d, double e, double f, double g, Predicate<Entity> predicate) {
		return null;
	}

	@Override
	public int getAmbientDarkness() {
		return 0;
	}

	@Override
	public Biome method_8310(BlockPos blockPos) {
		Biome biome = this.method_16955(blockPos).getBiomeArray()[blockPos.getX() & 15 | (blockPos.getZ() & 15) << 4];
		if (biome == null) {
			throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
		} else {
			return biome;
		}
	}

	@Override
	public int method_8314(LightType lightType, BlockPos blockPos) {
		return this.method_8398().method_12130().get(lightType).method_15543(blockPos);
	}

	@Override
	public int method_8624(BlockPos blockPos, int i) {
		return this.method_16955(blockPos).method_12035(blockPos, i, this.method_8597().hasSkyLight());
	}

	@Override
	public boolean method_8651(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.method_8320(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			if (bl) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.method_8321(blockPos) : null;
				Block.method_9610(blockState, this.world, blockPos, blockEntity);
			}

			return this.method_8652(blockPos, Blocks.field_10124.method_9564(), 3);
		}
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		Chunk chunk = this.method_16955(blockPos);
		BlockEntity blockEntity = chunk.method_8321(blockPos);
		if (blockEntity != null) {
			return blockEntity;
		} else {
			CompoundTag compoundTag = chunk.method_12024(blockPos);
			if (compoundTag != null) {
				if ("DUMMY".equals(compoundTag.getString("id"))) {
					Block block = this.method_8320(blockPos).getBlock();
					if (!(block instanceof BlockEntityProvider)) {
						return null;
					}

					blockEntity = ((BlockEntityProvider)block).method_10123(this.world);
				} else {
					blockEntity = BlockEntity.method_11005(compoundTag);
				}

				if (blockEntity != null) {
					chunk.method_12007(blockPos, blockEntity);
					return blockEntity;
				}
			}

			if (chunk.method_8320(blockPos).getBlock() instanceof BlockEntityProvider) {
				LOGGER.warn("Tried to access a block entity before it was created. {}", blockPos);
			}

			return null;
		}
	}

	@Override
	public boolean method_8652(BlockPos blockPos, BlockState blockState, int i) {
		Chunk chunk = this.method_16955(blockPos);
		BlockState blockState2 = chunk.method_12010(blockPos, blockState, false);
		if (blockState2 != null) {
			this.world.method_19282(blockPos, blockState2, blockState);
		}

		Block block = blockState.getBlock();
		if (block.hasBlockEntity()) {
			if (chunk.method_12009().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
				chunk.method_12007(blockPos, ((BlockEntityProvider)block).method_10123(this));
			} else {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putInt("x", blockPos.getX());
				compoundTag.putInt("y", blockPos.getY());
				compoundTag.putInt("z", blockPos.getZ());
				compoundTag.putString("id", "DUMMY");
				chunk.method_12042(compoundTag);
			}
		} else if (blockState2 != null && blockState2.getBlock().hasBlockEntity()) {
			chunk.method_12041(blockPos);
		}

		if (blockState.method_11601(this, blockPos)) {
			this.markBlockForPostProcessing(blockPos);
		}

		return true;
	}

	private void markBlockForPostProcessing(BlockPos blockPos) {
		this.method_16955(blockPos).method_12039(blockPos);
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		this.method_8392(i, j).addEntity(entity);
		return true;
	}

	@Override
	public boolean method_8650(BlockPos blockPos) {
		return this.method_8652(blockPos, Blocks.field_10124.method_9564(), 3);
	}

	@Override
	public WorldBorder method_8621() {
		return this.world.method_8621();
	}

	@Override
	public boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return true;
	}

	@Override
	public int method_8596(BlockPos blockPos, Direction direction) {
		return this.method_8320(blockPos).method_11577(this, blockPos, direction);
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Deprecated
	public ServerWorld method_19506() {
		return this.world;
	}

	@Override
	public LevelProperties method_8401() {
		return this.levelProperties;
	}

	@Override
	public LocalDifficulty method_8404(BlockPos blockPos) {
		if (!this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.method_8391());
		}
	}

	@Override
	public ChunkManager method_8398() {
		return this.world.method_14178();
	}

	@Override
	public long getSeed() {
		return this.seed;
	}

	@Override
	public TickScheduler<Block> method_8397() {
		return this.blockTickScheduler;
	}

	@Override
	public TickScheduler<Fluid> method_8405() {
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
	public void method_8408(BlockPos blockPos, Block block) {
	}

	@Override
	public int method_8589(Heightmap.Type type, int i, int j) {
		return this.method_8392(i >> 4, j >> 4).method_12005(type, i & 15, j & 15) + 1;
	}

	@Override
	public void method_8396(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
	}

	@Override
	public void method_8406(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public BlockPos method_8395() {
		return this.world.method_8395();
	}

	@Override
	public Dimension method_8597() {
		return this.dimension;
	}

	@Override
	public boolean method_16358(BlockPos blockPos, Predicate<BlockState> predicate) {
		return predicate.test(this.method_8320(blockPos));
	}

	@Override
	public <T extends Entity> List<T> method_8390(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<Entity> method_8333(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate) {
		return Collections.emptyList();
	}

	@Override
	public List<PlayerEntity> getPlayers() {
		return Collections.emptyList();
	}

	@Override
	public BlockPos method_8598(Heightmap.Type type, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.method_8589(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}
}
