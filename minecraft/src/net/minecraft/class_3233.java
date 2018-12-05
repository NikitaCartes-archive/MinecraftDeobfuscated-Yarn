package net.minecraft;

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
import net.minecraft.particle.Particle;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiTickScheduler;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3233 implements IWorld {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Chunk> field_14098;
	private final int chunkX;
	private final int chunkZ;
	private final int field_14088;
	private final World world;
	private final long seed;
	private final int seaLevel;
	private final LevelProperties levelProperties;
	private final Random random;
	private final Dimension dimension;
	private final ChunkGeneratorSettings field_14095;
	private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(blockPos -> this.method_8399(blockPos).method_12013());
	private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(blockPos -> this.method_8399(blockPos).method_12014());

	public class_3233(World world, List<Chunk> list) {
		int i = MathHelper.floor(Math.sqrt((double)list.size()));
		if (i * i != list.size()) {
			throw new IllegalStateException("Cache size is not a square.");
		} else {
			ChunkPos chunkPos = ((Chunk)list.get(list.size() / 2)).getPos();
			this.field_14098 = list;
			this.chunkX = chunkPos.x;
			this.chunkZ = chunkPos.z;
			this.field_14088 = i;
			this.world = world;
			this.seed = world.getSeed();
			this.field_14095 = world.getChunkManager().getChunkGenerator().getSettings();
			this.seaLevel = world.getSeaLevel();
			this.levelProperties = world.getLevelProperties();
			this.random = world.getRandom();
			this.dimension = world.getDimension();
		}
	}

	public int method_14336() {
		return this.chunkX;
	}

	public int method_14339() {
		return this.chunkZ;
	}

	@Override
	public boolean method_8393(int i, int j) {
		Chunk chunk = (Chunk)this.field_14098.get(0);
		Chunk chunk2 = (Chunk)this.field_14098.get(this.field_14098.size() - 1);
		return i >= chunk.getPos().x && i <= chunk2.getPos().x && j >= chunk.getPos().z && j <= chunk2.getPos().z;
	}

	@Override
	public Chunk getChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.field_12798);
	}

	@Override
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus) {
		Chunk chunk;
		if (this.method_8393(i, j)) {
			ChunkPos chunkPos = ((Chunk)this.field_14098.get(0)).getPos();
			int k = i - chunkPos.x;
			int l = j - chunkPos.z;
			chunk = (Chunk)this.field_14098.get(k + l * this.field_14088);
			if (chunk.getStatus().isAfter(chunkStatus)) {
				return chunk;
			}
		} else {
			chunk = null;
		}

		Chunk chunk2 = (Chunk)this.field_14098.get(0);
		Chunk chunk3 = (Chunk)this.field_14098.get(this.field_14098.size() - 1);
		LOGGER.error("Requested chunk : {} {}", i, j);
		LOGGER.error("Region bounds : {} {} | {} {}", chunk2.getPos().x, chunk2.getPos().z, chunk3.getPos().x, chunk3.getPos().z);
		if (chunk != null) {
			throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkStatus, chunk.getStatus(), i, j));
		} else {
			throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
		}
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).getBlockState(blockPos);
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return this.method_8399(blockPos).getFluidState(blockPos);
	}

	@Nullable
	@Override
	public PlayerEntity getClosestPlayer(double d, double e, double f, double g, Predicate<Entity> predicate) {
		return null;
	}

	@Override
	public int getAmbientDarkness() {
		return 0;
	}

	@Override
	public boolean isAir(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		Biome biome = this.method_8399(blockPos).getBiomeArray()[blockPos.getX() & 15 | (blockPos.getZ() & 15) << 4];
		if (biome == null) {
			throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
		} else {
			return biome;
		}
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.getChunkManager().getLightingProvider().get(lightType).getLightLevel(blockPos);
	}

	@Override
	public int method_8624(BlockPos blockPos, int i) {
		return this.method_8399(blockPos).method_12035(blockPos, i, this.getDimension().method_12451());
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return this.method_8393(i, j);
	}

	@Override
	public boolean breakBlock(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			if (bl) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos) : null;
				Block.dropStacks(blockState, this.world, blockPos, blockEntity);
			}

			return this.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
		}
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		Chunk chunk = this.method_8399(blockPos);
		BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
		if (blockEntity != null) {
			return blockEntity;
		} else {
			CompoundTag compoundTag = chunk.method_12024(blockPos);
			if (compoundTag != null) {
				if ("DUMMY".equals(compoundTag.getString("id"))) {
					Block block = this.getBlockState(blockPos).getBlock();
					if (!(block instanceof BlockEntityProvider)) {
						return null;
					}

					blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
				} else {
					blockEntity = BlockEntity.createFromTag(compoundTag);
				}

				if (blockEntity != null) {
					chunk.setBlockEntity(blockPos, blockEntity);
					return blockEntity;
				}
			}

			if (chunk.getBlockState(blockPos).getBlock() instanceof BlockEntityProvider) {
				LOGGER.warn("Tried to access a block entity before it was created. {}", blockPos);
			}

			return null;
		}
	}

	@Override
	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		Chunk chunk = this.method_8399(blockPos);
		BlockState blockState2 = chunk.setBlockState(blockPos, blockState, false);
		Block block = blockState.getBlock();
		if (block.hasBlockEntity()) {
			if (chunk.getStatus().method_12164() == ChunkStatus.class_2808.field_12807) {
				chunk.setBlockEntity(blockPos, ((BlockEntityProvider)block).createBlockEntity(this));
			} else {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putInt("x", blockPos.getX());
				compoundTag.putInt("y", blockPos.getY());
				compoundTag.putInt("z", blockPos.getZ());
				compoundTag.putString("id", "DUMMY");
				chunk.addPendingBlockEntityTag(compoundTag);
			}
		} else if (blockState2 != null && blockState2.getBlock().hasBlockEntity()) {
			chunk.removeBlockEntity(blockPos);
		}

		if (blockState.method_11601(this, blockPos)) {
			this.method_14338(blockPos);
		}

		return true;
	}

	private void method_14338(BlockPos blockPos) {
		this.method_8399(blockPos).markBlockForPostProcessing(blockPos);
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		this.getChunk(i, j).addEntity(entity);
		return true;
	}

	@Override
	public boolean clearBlockState(BlockPos blockPos) {
		return this.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.world.getWorldBorder();
	}

	@Override
	public boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return true;
	}

	@Override
	public int method_8596(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).method_11577(this, blockPos, direction);
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Deprecated
	@Override
	public World method_8410() {
		return this.world;
	}

	@Override
	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos blockPos) {
		if (!this.method_8393(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.method_8391());
		}
	}

	@Nullable
	@Override
	public class_37 method_8646() {
		return this.world.method_8646();
	}

	@Override
	public ChunkManager getChunkManager() {
		return this.world.getChunkManager();
	}

	@Override
	public WorldSaveHandler getSaveHandler() {
		return this.world.getSaveHandler();
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
	public void updateNeighbors(BlockPos blockPos, Block block) {
	}

	@Override
	public int getTop(Heightmap.Type type, int i, int j) {
		return this.getChunk(i >> 4, j >> 4).sampleHeightmap(type, i & 15, j & 15) + 1;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
	}

	@Override
	public void method_8406(Particle particle, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public BlockPos method_8395() {
		return this.world.method_8395();
	}

	@Override
	public Dimension getDimension() {
		return this.dimension;
	}

	@Override
	public boolean method_16358(BlockPos blockPos, Predicate<BlockState> predicate) {
		return predicate.test(this.getBlockState(blockPos));
	}

	@Override
	public <T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate) {
		return Collections.emptyList();
	}

	@Override
	public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}
}
