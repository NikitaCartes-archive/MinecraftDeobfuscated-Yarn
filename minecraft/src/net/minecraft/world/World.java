package net.minecraft.world;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements IWorld, AutoCloseable {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Direction[] DIRECTIONS = Direction.values();
	public final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	public final List<BlockEntity> tickingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> pendingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> unloadedBlockEntities = Lists.<BlockEntity>newArrayList();
	private final Thread thread;
	private int ambientDarkness;
	protected int lcgBlockSeed = new Random().nextInt();
	protected final int unusedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	public final Random random = new Random();
	public final Dimension dimension;
	protected final ChunkManager chunkManager;
	protected final LevelProperties properties;
	private final Profiler profiler;
	public final boolean isClient;
	protected boolean iteratingTickingBlockEntities;
	private final WorldBorder border;
	private final BiomeAccess biomeAccess;

	protected World(
		LevelProperties levelProperties,
		DimensionType dimensionType,
		BiFunction<World, Dimension, ChunkManager> chunkManagerProvider,
		Profiler profiler,
		boolean isClient
	) {
		this.profiler = profiler;
		this.properties = levelProperties;
		this.dimension = dimensionType.create(this);
		this.chunkManager = (ChunkManager)chunkManagerProvider.apply(this, this.dimension);
		this.isClient = isClient;
		this.border = this.dimension.createWorldBorder();
		this.thread = Thread.currentThread();
		this.biomeAccess = new BiomeAccess(
			this, isClient ? levelProperties.getSeed() : LevelProperties.sha256Hash(levelProperties.getSeed()), dimensionType.getBiomeAccessType()
		);
	}

	@Override
	public boolean isClient() {
		return this.isClient;
	}

	@Nullable
	public MinecraftServer getServer() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void setDefaultSpawnClient() {
		this.setSpawnPos(new BlockPos(8, 64, 8));
	}

	public BlockState getTopNonAirState(BlockPos blockPos) {
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());

		while (!this.isAir(blockPos2.up())) {
			blockPos2 = blockPos2.up();
		}

		return this.getBlockState(blockPos2);
	}

	public static boolean isValid(BlockPos pos) {
		return !isHeightInvalid(pos) && pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
	}

	public static boolean isHeightInvalid(BlockPos pos) {
		return isHeightInvalid(pos.getY());
	}

	public static boolean isHeightInvalid(int y) {
		return y < 0 || y >= 256;
	}

	public WorldChunk getWorldChunk(BlockPos blockPos) {
		return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public WorldChunk getChunk(int i, int j) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		Chunk chunk = this.chunkManager.getChunk(chunkX, chunkZ, leastStatus, create);
		if (chunk == null && create) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		if (isHeightInvalid(pos)) {
			return false;
		} else if (!this.isClient && this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		} else {
			WorldChunk worldChunk = this.getWorldChunk(pos);
			Block block = state.getBlock();
			BlockState blockState = worldChunk.setBlockState(pos, state, (flags & 64) != 0);
			if (blockState == null) {
				return false;
			} else {
				BlockState blockState2 = this.getBlockState(pos);
				if (blockState2 != blockState
					&& (
						blockState2.getOpacity(this, pos) != blockState.getOpacity(this, pos)
							|| blockState2.getLuminance() != blockState.getLuminance()
							|| blockState2.hasSidedTransparency()
							|| blockState.hasSidedTransparency()
					)) {
					this.profiler.push("queueCheckLight");
					this.getChunkManager().getLightingProvider().checkBlock(pos);
					this.profiler.pop();
				}

				if (blockState2 == state) {
					if (blockState != blockState2) {
						this.checkBlockRerender(pos, blockState, blockState2);
					}

					if ((flags & 2) != 0
						&& (!this.isClient || (flags & 4) == 0)
						&& (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.updateListeners(pos, blockState, state, flags);
					}

					if (!this.isClient && (flags & 1) != 0) {
						this.updateNeighbors(pos, blockState.getBlock());
						if (state.hasComparatorOutput()) {
							this.updateHorizontalAdjacent(pos, block);
						}
					}

					if ((flags & 16) == 0) {
						int i = flags & -2;
						blockState.method_11637(this, pos, i);
						state.updateNeighborStates(this, pos, i);
						state.method_11637(this, pos, i);
					}

					this.onBlockChanged(pos, blockState, blockState2);
				}

				return true;
			}
		}
	}

	public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		FluidState fluidState = this.getFluidState(pos);
		return this.setBlockState(pos, fluidState.getBlockState(), 3 | (move ? 64 : 0));
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity) {
		BlockState blockState = this.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(pos);
			this.playLevelEvent(2001, pos, Block.getRawIdFromState(blockState));
			if (drop) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
				Block.dropStacks(blockState, this, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
			}

			return this.setBlockState(pos, fluidState.getBlockState(), 3);
		}
	}

	public boolean setBlockState(BlockPos pos, BlockState blockState) {
		return this.setBlockState(pos, blockState, 3);
	}

	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	@Override
	public void updateNeighbors(BlockPos pos, Block block) {
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.updateNeighborsAlways(pos, block);
		}
	}

	public void checkBlockRerender(BlockPos pos, BlockState old, BlockState updated) {
	}

	public void updateNeighborsAlways(BlockPos pos, Block block) {
		this.updateNeighbor(pos.west(), block, pos);
		this.updateNeighbor(pos.east(), block, pos);
		this.updateNeighbor(pos.down(), block, pos);
		this.updateNeighbor(pos.up(), block, pos);
		this.updateNeighbor(pos.north(), block, pos);
		this.updateNeighbor(pos.south(), block, pos);
	}

	public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
		if (direction != Direction.WEST) {
			this.updateNeighbor(pos.west(), sourceBlock, pos);
		}

		if (direction != Direction.EAST) {
			this.updateNeighbor(pos.east(), sourceBlock, pos);
		}

		if (direction != Direction.DOWN) {
			this.updateNeighbor(pos.down(), sourceBlock, pos);
		}

		if (direction != Direction.UP) {
			this.updateNeighbor(pos.up(), sourceBlock, pos);
		}

		if (direction != Direction.NORTH) {
			this.updateNeighbor(pos.north(), sourceBlock, pos);
		}

		if (direction != Direction.SOUTH) {
			this.updateNeighbor(pos.south(), sourceBlock, pos);
		}
	}

	public void updateNeighbor(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(sourcePos);

			try {
				blockState.neighborUpdate(this, sourcePos, sourceBlock, neighborPos, false);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", (CrashCallable<String>)(() -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.getId(sourceBlock);
					}
				}));
				CrashReportSection.addBlockInfo(crashReportSection, sourcePos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		int i;
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (this.isChunkLoaded(x >> 4, z >> 4)) {
				i = this.getChunk(x >> 4, z >> 4).sampleHeightmap(heightmap, x & 15, z & 15) + 1;
			} else {
				i = 0;
			}
		} else {
			i = this.getSeaLevel() + 1;
		}

		return i;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.getChunkManager().getLightingProvider();
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (isHeightInvalid(pos)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
			return worldChunk.getBlockState(pos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (isHeightInvalid(pos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getWorldChunk(pos);
			return worldChunk.getFluidState(pos);
		}
	}

	public boolean isDay() {
		return this.dimension.getType() == DimensionType.OVERWORLD && this.ambientDarkness < 4;
	}

	public boolean isNight() {
		return this.dimension.getType() == DimensionType.OVERWORLD && !this.isDay();
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
		this.playSound(player, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, volume, pitch);
	}

	public abstract void playSound(
		@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch
	);

	public abstract void playSoundFromEntity(
		@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch
	);

	public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory soundCategory, float f, float g, boolean bl) {
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	@Environment(EnvType.CLIENT)
	public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
	}

	public void addImportantParticle(
		ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
	}

	public float getSkyAngleRadians(float f) {
		float g = this.getSkyAngle(f);
		return g * (float) (Math.PI * 2);
	}

	public boolean addBlockEntity(BlockEntity blockEntity) {
		if (this.iteratingTickingBlockEntities) {
			LOGGER.error("Adding block entity while ticking: {} @ {}", () -> Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()), blockEntity::getPos);
		}

		boolean bl = this.blockEntities.add(blockEntity);
		if (bl && blockEntity instanceof Tickable) {
			this.tickingBlockEntities.add(blockEntity);
		}

		if (this.isClient) {
			BlockPos blockPos = blockEntity.getPos();
			BlockState blockState = this.getBlockState(blockPos);
			this.updateListeners(blockPos, blockState, blockState, 2);
		}

		return bl;
	}

	public void addBlockEntities(Collection<BlockEntity> collection) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(collection);
		} else {
			for (BlockEntity blockEntity : collection) {
				this.addBlockEntity(blockEntity);
			}
		}
	}

	public void tickBlockEntities() {
		Profiler profiler = this.getProfiler();
		profiler.push("blockEntities");
		if (!this.unloadedBlockEntities.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
			this.blockEntities.removeAll(this.unloadedBlockEntities);
			this.unloadedBlockEntities.clear();
		}

		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.getPos();
				if (this.chunkManager.shouldTickBlock(blockPos) && this.getWorldBorder().contains(blockPos)) {
					try {
						profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntityType.getId(blockEntity.getType()))));
						if (blockEntity.getType().supports(this.getBlockState(blockPos).getBlock())) {
							((Tickable)blockEntity).tick();
						} else {
							blockEntity.markInvalid();
						}

						profiler.pop();
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Ticking block entity");
						CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
						blockEntity.populateCrashReport(crashReportSection);
						throw new CrashException(crashReport);
					}
				}
			}

			if (blockEntity.isRemoved()) {
				iterator.remove();
				this.blockEntities.remove(blockEntity);
				if (this.isChunkLoaded(blockEntity.getPos())) {
					this.getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
				}
			}
		}

		this.iteratingTickingBlockEntities = false;
		profiler.swap("pendingBlockEntities");
		if (!this.pendingBlockEntities.isEmpty()) {
			for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
				BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(i);
				if (!blockEntity2.isRemoved()) {
					if (!this.blockEntities.contains(blockEntity2)) {
						this.addBlockEntity(blockEntity2);
					}

					if (this.isChunkLoaded(blockEntity2.getPos())) {
						WorldChunk worldChunk = this.getWorldChunk(blockEntity2.getPos());
						BlockState blockState = worldChunk.getBlockState(blockEntity2.getPos());
						worldChunk.setBlockEntity(blockEntity2.getPos(), blockEntity2);
						this.updateListeners(blockEntity2.getPos(), blockState, blockState, 3);
					}
				}
			}

			this.pendingBlockEntities.clear();
		}

		profiler.pop();
	}

	public void tickEntity(Consumer<Entity> consumer, Entity entity) {
		try {
			consumer.accept(entity);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking entity");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
			entity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public boolean isAreaNotEmpty(Box box) {
		int i = MathHelper.floor(box.x1);
		int j = MathHelper.ceil(box.x2);
		int k = MathHelper.floor(box.y1);
		int l = MathHelper.ceil(box.y2);
		int m = MathHelper.floor(box.z1);
		int n = MathHelper.ceil(box.z2);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
						if (!blockState.isAir()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public boolean doesAreaContainFireSource(Box box) {
		int i = MathHelper.floor(box.x1);
		int j = MathHelper.ceil(box.x2);
		int k = MathHelper.floor(box.y1);
		int l = MathHelper.ceil(box.y2);
		int m = MathHelper.floor(box.z1);
		int n = MathHelper.ceil(box.z2);
		if (this.isRegionLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							Block block = this.getBlockState(pooledMutable.set(o, p, q)).getBlock();
							if (block == Blocks.FIRE || block == Blocks.LAVA) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public BlockState getBlockState(Box area, Block block) {
		int i = MathHelper.floor(area.x1);
		int j = MathHelper.ceil(area.x2);
		int k = MathHelper.floor(area.y1);
		int l = MathHelper.ceil(area.y2);
		int m = MathHelper.floor(area.z1);
		int n = MathHelper.ceil(area.z2);
		if (this.isRegionLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
							if (blockState.getBlock() == block) {
								return blockState;
							}
						}
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public boolean containsBlockWithMaterial(Box area, Material material) {
		int i = MathHelper.floor(area.x1);
		int j = MathHelper.ceil(area.x2);
		int k = MathHelper.floor(area.y1);
		int l = MathHelper.ceil(area.y2);
		int m = MathHelper.floor(area.z1);
		int n = MathHelper.ceil(area.z2);
		MaterialPredicate materialPredicate = MaterialPredicate.create(material);
		return BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).anyMatch(blockPos -> materialPredicate.test(this.getBlockState(blockPos)));
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
		return this.createExplosion(entity, null, x, y, z, power, false, destructionType);
	}

	public Explosion createExplosion(
		@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
	) {
		return this.createExplosion(entity, null, x, y, z, power, createFire, destructionType);
	}

	public Explosion createExplosion(
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		double x,
		double y,
		double z,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, x, y, z, power, createFire, destructionType);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	public boolean extinguishFire(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Direction direction) {
		blockPos = blockPos.offset(direction);
		if (this.getBlockState(blockPos).getBlock() == Blocks.FIRE) {
			this.playLevelEvent(playerEntity, 1009, blockPos, 0);
			this.removeBlock(blockPos, false);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public String getDebugString() {
		return this.chunkManager.getDebugString();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		if (isHeightInvalid(pos)) {
			return null;
		} else if (!this.isClient && Thread.currentThread() != this.thread) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				blockEntity = this.getPendingBlockEntity(pos);
			}

			if (blockEntity == null) {
				blockEntity = this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
			}

			if (blockEntity == null) {
				blockEntity = this.getPendingBlockEntity(pos);
			}

			return blockEntity;
		}
	}

	@Nullable
	private BlockEntity getPendingBlockEntity(BlockPos blockPos) {
		for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
			BlockEntity blockEntity = (BlockEntity)this.pendingBlockEntities.get(i);
			if (!blockEntity.isRemoved() && blockEntity.getPos().equals(blockPos)) {
				return blockEntity;
			}
		}

		return null;
	}

	public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
		if (!isHeightInvalid(pos)) {
			if (blockEntity != null && !blockEntity.isRemoved()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.setWorld(this, pos);
					Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();

					while (iterator.hasNext()) {
						BlockEntity blockEntity2 = (BlockEntity)iterator.next();
						if (blockEntity2.getPos().equals(pos)) {
							blockEntity2.markRemoved();
							iterator.remove();
						}
					}

					this.pendingBlockEntities.add(blockEntity);
				} else {
					this.getWorldChunk(pos).setBlockEntity(pos, blockEntity);
					this.addBlockEntity(blockEntity);
				}
			}
		}
	}

	public void removeBlockEntity(BlockPos blockPos) {
		BlockEntity blockEntity = this.getBlockEntity(blockPos);
		if (blockEntity != null && this.iteratingTickingBlockEntities) {
			blockEntity.markRemoved();
			this.pendingBlockEntities.remove(blockEntity);
		} else {
			if (blockEntity != null) {
				this.pendingBlockEntities.remove(blockEntity);
				this.blockEntities.remove(blockEntity);
				this.tickingBlockEntities.remove(blockEntity);
			}

			this.getWorldChunk(blockPos).removeBlockEntity(blockPos);
		}
	}

	public boolean canSetBlock(BlockPos pos) {
		return isHeightInvalid(pos) ? false : this.chunkManager.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public boolean isTopSolid(BlockPos pos, Entity entity) {
		if (isHeightInvalid(pos)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
			return chunk == null ? false : chunk.getBlockState(pos).hasSolidTopSurface(this, pos, entity);
		}
	}

	public void calculateAmbientDarkness() {
		double d = 1.0 - (double)(this.getRainGradient(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.getThunderGradient(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
		this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
	}

	public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
		this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
	}

	protected void initWeatherGradients() {
		if (this.properties.isRaining()) {
			this.rainGradient = 1.0F;
			if (this.properties.isThundering()) {
				this.thunderGradient = 1.0F;
			}
		}
	}

	public void close() throws IOException {
		this.chunkManager.close();
	}

	@Nullable
	@Override
	public BlockView getExistingChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
		this.getProfiler().method_24270("getEntities");
		List<Entity> list = Lists.<Entity>newArrayList();
		int i = MathHelper.floor((box.x1 - 2.0) / 16.0);
		int j = MathHelper.floor((box.x2 + 2.0) / 16.0);
		int k = MathHelper.floor((box.z1 - 2.0) / 16.0);
		int l = MathHelper.floor((box.z2 + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(except, box, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> type, Box box, Predicate<? super T> predicate) {
		this.getProfiler().method_24270("getEntities");
		int i = MathHelper.floor((box.x1 - 2.0) / 16.0);
		int j = MathHelper.ceil((box.x2 + 2.0) / 16.0);
		int k = MathHelper.floor((box.z1 - 2.0) / 16.0);
		int l = MathHelper.ceil((box.z2 + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(type, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntities(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
		this.getProfiler().method_24270("getEntities");
		int i = MathHelper.floor((box.x1 - 2.0) / 16.0);
		int j = MathHelper.ceil((box.x2 + 2.0) / 16.0);
		int k = MathHelper.floor((box.z1 - 2.0) / 16.0);
		int l = MathHelper.ceil((box.z2 + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();
		ChunkManager chunkManager = this.getChunkManager();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = chunkManager.getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(entityClass, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
		this.getProfiler().method_24270("getLoadedEntities");
		int i = MathHelper.floor((box.x1 - 2.0) / 16.0);
		int j = MathHelper.ceil((box.x2 + 2.0) / 16.0);
		int k = MathHelper.floor((box.z1 - 2.0) / 16.0);
		int l = MathHelper.ceil((box.z2 + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();
		ChunkManager chunkManager = this.getChunkManager();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = chunkManager.getWorldChunk(m, n);
				if (worldChunk != null) {
					worldChunk.getEntities(entityClass, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public abstract Entity getEntityById(int id);

	public void markDirty(BlockPos pos, BlockEntity blockEntity) {
		if (this.isChunkLoaded(pos)) {
			this.getWorldChunk(pos).markDirty();
		}
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}

	@Override
	public World getWorld() {
		return this;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.properties.getGeneratorType();
	}

	public int getReceivedStrongRedstonePower(BlockPos blockPos) {
		int i = 0;
		i = Math.max(i, this.getStrongRedstonePower(blockPos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getStrongRedstonePower(blockPos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getStrongRedstonePower(blockPos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getStrongRedstonePower(blockPos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getStrongRedstonePower(blockPos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getStrongRedstonePower(blockPos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
		return this.getEmittedRedstonePower(pos, direction) > 0;
	}

	public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
		BlockState blockState = this.getBlockState(pos);
		return blockState.isSimpleFullBlock(this, pos) ? this.getReceivedStrongRedstonePower(pos) : blockState.getWeakRedstonePower(this, pos, direction);
	}

	public boolean isReceivingRedstonePower(BlockPos blockPos) {
		if (this.getEmittedRedstonePower(blockPos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(blockPos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(blockPos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(blockPos.east(), Direction.EAST) > 0;
		}
	}

	public int getReceivedRedstonePower(BlockPos blockPos) {
		int i = 0;

		for (Direction direction : DIRECTIONS) {
			int j = this.getEmittedRedstonePower(blockPos.offset(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	public void disconnect() {
	}

	public void setTime(long time) {
		this.properties.setTime(time);
	}

	@Override
	public long getSeed() {
		return this.properties.getSeed();
	}

	public long getTime() {
		return this.properties.getTime();
	}

	public long getTimeOfDay() {
		return this.properties.getTimeOfDay();
	}

	public void setTimeOfDay(long time) {
		this.properties.setTimeOfDay(time);
	}

	protected void tickTime() {
		this.setTime(this.properties.getTime() + 1L);
		if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
			this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
		}
	}

	@Override
	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public void setSpawnPos(BlockPos pos) {
		this.properties.setSpawnPos(pos);
	}

	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return true;
	}

	public void sendEntityStatus(Entity entity, byte status) {
	}

	@Override
	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}

	public void addBlockAction(BlockPos pos, Block block, int type, int data) {
		this.getBlockState(pos).onBlockAction(this, pos, type, data);
	}

	@Override
	public LevelProperties getLevelProperties() {
		return this.properties;
	}

	public GameRules getGameRules() {
		return this.properties.getGameRules();
	}

	public float getThunderGradient(float f) {
		return MathHelper.lerp(f, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(f);
	}

	@Environment(EnvType.CLIENT)
	public void setThunderGradient(float thunderGradient) {
		this.thunderGradientPrev = thunderGradient;
		this.thunderGradient = thunderGradient;
	}

	public float getRainGradient(float f) {
		return MathHelper.lerp(f, this.rainGradientPrev, this.rainGradient);
	}

	@Environment(EnvType.CLIENT)
	public void setRainGradient(float rainGradient) {
		this.rainGradientPrev = rainGradient;
		this.rainGradient = rainGradient;
	}

	public boolean isThundering() {
		return this.dimension.hasSkyLight() && !this.dimension.isNether() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos pos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.isSkyVisible(pos)) {
			return false;
		} else {
			return this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()
				? false
				: this.getBiome(pos).getPrecipitation() == Biome.Precipitation.RAIN;
		}
	}

	public boolean hasHighHumidity(BlockPos blockPos) {
		Biome biome = this.getBiome(blockPos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState getMapState(String id);

	public abstract void putMapState(MapState mapState);

	public abstract int getNextMapId();

	public void playGlobalEvent(int type, BlockPos pos, int data) {
	}

	public int getEffectiveHeight() {
		return this.dimension.isNether() ? 128 : 256;
	}

	public CrashReportSection addDetailsToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("All players", (CrashCallable<String>)(() -> this.getPlayers().size() + " total; " + this.getPlayers()));
		crashReportSection.add("Chunk stats", this.chunkManager::getDebugString);
		crashReportSection.add("Level dimension", (CrashCallable<String>)(() -> this.dimension.getType().toString()));

		try {
			this.properties.populateCrashReport(crashReportSection);
		} catch (Throwable var4) {
			crashReportSection.add("Level Data Unobtainable", var4);
		}

		return crashReportSection;
	}

	public abstract void setBlockBreakingInfo(int entityId, BlockPos pos, int progress);

	@Environment(EnvType.CLIENT)
	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable CompoundTag tag) {
	}

	public abstract Scoreboard getScoreboard();

	public void updateHorizontalAdjacent(BlockPos pos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.isChunkLoaded(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (blockState.getBlock() == Blocks.COMPARATOR) {
					blockState.neighborUpdate(this, blockPos, block, pos, false);
				} else if (blockState.isSimpleFullBlock(this, blockPos)) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.COMPARATOR) {
						blockState.neighborUpdate(this, blockPos, block, pos, false);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		long l = 0L;
		float f = 0.0F;
		if (this.isChunkLoaded(pos)) {
			f = this.getMoonSize();
			l = this.getWorldChunk(pos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	public void setLightningTicksLeft(int lightningTicksLeft) {
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.border;
	}

	public void sendPacket(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Override
	public Dimension getDimension() {
		return this.dimension;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(blockPos));
	}

	public abstract RecipeManager getRecipeManager();

	public abstract RegistryTagManager getTagManager();

	public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
		this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
		int j = this.lcgBlockSeed >> 2;
		return new BlockPos(x + (j & 15), y + (j >> 16 & i), z + (j >> 8 & 15));
	}

	public boolean isSavingDisabled() {
		return false;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	@Override
	public BiomeAccess getBiomeAccess() {
		return this.biomeAccess;
	}
}
