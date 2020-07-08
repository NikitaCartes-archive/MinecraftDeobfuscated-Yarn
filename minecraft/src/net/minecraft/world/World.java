package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
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
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements WorldAccess, AutoCloseable {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<RegistryKey<World>> CODEC = Identifier.CODEC.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION), RegistryKey::getValue);
	public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.DIMENSION, new Identifier("overworld"));
	public static final RegistryKey<World> NETHER = RegistryKey.of(Registry.DIMENSION, new Identifier("the_nether"));
	public static final RegistryKey<World> END = RegistryKey.of(Registry.DIMENSION, new Identifier("the_end"));
	private static final Direction[] DIRECTIONS = Direction.values();
	public final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	public final List<BlockEntity> tickingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> pendingBlockEntities = Lists.<BlockEntity>newArrayList();
	protected final List<BlockEntity> unloadedBlockEntities = Lists.<BlockEntity>newArrayList();
	private final Thread thread;
	private final boolean debugWorld;
	private int ambientDarkness;
	protected int lcgBlockSeed = new Random().nextInt();
	protected final int unusedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	public final Random random = new Random();
	private final DimensionType dimension;
	protected final MutableWorldProperties properties;
	private final Supplier<Profiler> profiler;
	public final boolean isClient;
	protected boolean iteratingTickingBlockEntities;
	private final WorldBorder border;
	private final BiomeAccess biomeAccess;
	private final RegistryKey<World> registryKey;
	private final RegistryKey<DimensionType> dimensionRegistryKey;

	protected World(
		MutableWorldProperties properties,
		RegistryKey<World> registryKey,
		RegistryKey<DimensionType> dimensionRegistryKey,
		DimensionType dimension,
		Supplier<Profiler> profiler,
		boolean isClient,
		boolean debugWorld,
		long seed
	) {
		this.profiler = profiler;
		this.properties = properties;
		this.dimension = dimension;
		this.registryKey = registryKey;
		this.dimensionRegistryKey = dimensionRegistryKey;
		this.isClient = isClient;
		if (dimension.isShrunk()) {
			this.border = new WorldBorder() {
				@Override
				public double getCenterX() {
					return super.getCenterX() / 8.0;
				}

				@Override
				public double getCenterZ() {
					return super.getCenterZ() / 8.0;
				}
			};
		} else {
			this.border = new WorldBorder();
		}

		this.thread = Thread.currentThread();
		this.biomeAccess = new BiomeAccess(this, seed, dimension.getBiomeAccessType());
		this.debugWorld = debugWorld;
	}

	@Override
	public boolean isClient() {
		return this.isClient;
	}

	@Nullable
	public MinecraftServer getServer() {
		return null;
	}

	public static boolean method_24794(BlockPos blockPos) {
		return !isHeightInvalid(blockPos) && isValid(blockPos);
	}

	public static boolean method_25953(BlockPos blockPos) {
		return !method_25952(blockPos.getY()) && isValid(blockPos);
	}

	private static boolean isValid(BlockPos pos) {
		return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
	}

	private static boolean method_25952(int i) {
		return i < -20000000 || i >= 20000000;
	}

	public static boolean isHeightInvalid(BlockPos pos) {
		return isHeightInvalid(pos.getY());
	}

	public static boolean isHeightInvalid(int y) {
		return y < 0 || y >= 256;
	}

	public WorldChunk getWorldChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public WorldChunk getChunk(int i, int j) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		Chunk chunk = this.getChunkManager().getChunk(chunkX, chunkZ, leastStatus, create);
		if (chunk == null && create) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		return this.setBlockState(pos, state, flags, 512);
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		if (isHeightInvalid(pos)) {
			return false;
		} else if (!this.isClient && this.isDebugWorld()) {
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
					this.getProfiler().push("queueCheckLight");
					this.getChunkManager().getLightingProvider().checkBlock(pos);
					this.getProfiler().pop();
				}

				if (blockState2 == state) {
					if (blockState != blockState2) {
						this.scheduleBlockRerenderIfNeeded(pos, blockState, blockState2);
					}

					if ((flags & 2) != 0
						&& (!this.isClient || (flags & 4) == 0)
						&& (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.updateListeners(pos, blockState, state, flags);
					}

					if ((flags & 1) != 0) {
						this.updateNeighbors(pos, blockState.getBlock());
						if (!this.isClient && state.hasComparatorOutput()) {
							this.updateComparators(pos, block);
						}
					}

					if ((flags & 16) == 0 && maxUpdateDepth > 0) {
						int i = flags & -34;
						blockState.prepare(this, pos, i, maxUpdateDepth - 1);
						state.updateNeighbors(this, pos, i, maxUpdateDepth - 1);
						state.prepare(this, pos, i, maxUpdateDepth - 1);
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
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		BlockState blockState = this.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(pos);
			if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
				this.syncWorldEvent(2001, pos, Block.getRawIdFromState(blockState));
			}

			if (drop) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
				Block.dropStacks(blockState, this, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
			}

			return this.setBlockState(pos, fluidState.getBlockState(), 3, maxUpdateDepth);
		}
	}

	public boolean setBlockState(BlockPos pos, BlockState state) {
		return this.setBlockState(pos, state, 3);
	}

	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
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
		return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
	}

	public boolean isNight() {
		return !this.getDimension().hasFixedTime() && !this.isDay();
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch);
	}

	public abstract void playSound(
		@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch
	);

	public abstract void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch);

	public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean bl) {
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

	public float getSkyAngleRadians(float tickDelta) {
		float f = this.method_30274(tickDelta);
		return f * (float) (Math.PI * 2);
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

	public void addBlockEntities(Collection<BlockEntity> blockEntities) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(blockEntities);
		} else {
			for (BlockEntity blockEntity : blockEntities) {
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
				if (this.getChunkManager().shouldTickBlock(blockPos) && this.getWorldBorder().contains(blockPos)) {
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

	public void tickEntity(Consumer<Entity> tickConsumer, Entity entity) {
		try {
			tickConsumer.accept(entity);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking entity");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
			entity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
		return this.createExplosion(entity, null, null, x, y, z, power, false, destructionType);
	}

	public Explosion createExplosion(
		@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
	) {
		return this.createExplosion(entity, null, null, x, y, z, power, createFire, destructionType);
	}

	public Explosion createExplosion(
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable ExplosionBehavior explosionBehavior,
		double d,
		double e,
		double f,
		float g,
		boolean bl,
		Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, damageSource, explosionBehavior, d, e, f, g, bl, destructionType);
		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	@Environment(EnvType.CLIENT)
	public String getDebugString() {
		return this.getChunkManager().getDebugString();
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
	private BlockEntity getPendingBlockEntity(BlockPos pos) {
		for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
			BlockEntity blockEntity = (BlockEntity)this.pendingBlockEntities.get(i);
			if (!blockEntity.isRemoved() && blockEntity.getPos().equals(pos)) {
				return blockEntity;
			}
		}

		return null;
	}

	public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
		if (!isHeightInvalid(pos)) {
			if (blockEntity != null && !blockEntity.isRemoved()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.setLocation(this, pos);
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

	public void removeBlockEntity(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		if (blockEntity != null && this.iteratingTickingBlockEntities) {
			blockEntity.markRemoved();
			this.pendingBlockEntities.remove(blockEntity);
		} else {
			if (blockEntity != null) {
				this.pendingBlockEntities.remove(blockEntity);
				this.blockEntities.remove(blockEntity);
				this.tickingBlockEntities.remove(blockEntity);
			}

			this.getWorldChunk(pos).removeBlockEntity(pos);
		}
	}

	public boolean canSetBlock(BlockPos pos) {
		return isHeightInvalid(pos) ? false : this.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
		if (isHeightInvalid(pos)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
			return chunk == null ? false : chunk.getBlockState(pos).hasSolidTopSurface(this, pos, entity, direction);
		}
	}

	public boolean isTopSolid(BlockPos pos, Entity entity) {
		return this.isDirectionSolid(pos, entity, Direction.UP);
	}

	public void calculateAmbientDarkness() {
		double d = 1.0 - (double)(this.getRainGradient(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.getThunderGradient(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.method_30274(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
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
		this.getChunkManager().close();
	}

	@Nullable
	@Override
	public BlockView getExistingChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
		this.getProfiler().visit("getEntities");
		List<Entity> list = Lists.<Entity>newArrayList();
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);
		ChunkManager chunkManager = this.getChunkManager();

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				WorldChunk worldChunk = chunkManager.getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(except, box, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> type, Box box, Predicate<? super T> predicate) {
		this.getProfiler().visit("getEntities");
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
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
		this.getProfiler().visit("getEntities");
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
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
		this.getProfiler().visit("getLoadedEntities");
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
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

	public int getReceivedStrongRedstonePower(BlockPos pos) {
		int i = 0;
		i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST));
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
		int i = blockState.getWeakRedstonePower(this, pos, direction);
		return blockState.isSolidBlock(this, pos) ? Math.max(i, this.getReceivedStrongRedstonePower(pos)) : i;
	}

	public boolean isReceivingRedstonePower(BlockPos pos) {
		if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
		}
	}

	public int getReceivedRedstonePower(BlockPos pos) {
		int i = 0;

		for (Direction direction : DIRECTIONS) {
			int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
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

	public long getTime() {
		return this.properties.getTime();
	}

	public long getTimeOfDay() {
		return this.properties.getTimeOfDay();
	}

	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return true;
	}

	public void sendEntityStatus(Entity entity, byte status) {
	}

	public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
		this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
	}

	@Override
	public WorldProperties getLevelProperties() {
		return this.properties;
	}

	public GameRules getGameRules() {
		return this.properties.getGameRules();
	}

	public float getThunderGradient(float delta) {
		return MathHelper.lerp(delta, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(delta);
	}

	@Environment(EnvType.CLIENT)
	public void setThunderGradient(float thunderGradient) {
		this.thunderGradientPrev = thunderGradient;
		this.thunderGradient = thunderGradient;
	}

	public float getRainGradient(float delta) {
		return MathHelper.lerp(delta, this.rainGradientPrev, this.rainGradient);
	}

	@Environment(EnvType.CLIENT)
	public void setRainGradient(float rainGradient) {
		this.rainGradientPrev = rainGradient;
		this.rainGradient = rainGradient;
	}

	public boolean isThundering() {
		return this.getDimension().hasSkyLight() && !this.getDimension().hasCeiling() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos pos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.isSkyVisible(pos)) {
			return false;
		} else if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
			return false;
		} else {
			Biome biome = this.getBiome(pos);
			return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.getTemperature(pos) >= 0.15F;
		}
	}

	public boolean hasHighHumidity(BlockPos pos) {
		Biome biome = this.getBiome(pos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState getMapState(String id);

	public abstract void putMapState(MapState mapState);

	public abstract int getNextMapId();

	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
	}

	public CrashReportSection addDetailsToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("All players", (CrashCallable<String>)(() -> this.getPlayers().size() + " total; " + this.getPlayers()));
		crashReportSection.add("Chunk stats", this.getChunkManager()::getDebugString);
		crashReportSection.add("Level dimension", (CrashCallable<String>)(() -> this.getRegistryKey().getValue().toString()));

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

	public void updateComparators(BlockPos pos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.isChunkLoaded(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (blockState.isOf(Blocks.COMPARATOR)) {
					blockState.neighborUpdate(this, blockPos, block, pos, false);
				} else if (blockState.isSolidBlock(this, blockPos)) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (blockState.isOf(Blocks.COMPARATOR)) {
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
			f = this.method_30272();
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
	public DimensionType getDimension() {
		return this.dimension;
	}

	public RegistryKey<DimensionType> getDimensionRegistryKey() {
		return this.dimensionRegistryKey;
	}

	public RegistryKey<World> getRegistryKey() {
		return this.registryKey;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	public abstract RecipeManager getRecipeManager();

	public abstract TagManager getTagManager();

	public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
		this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
		int j = this.lcgBlockSeed >> 2;
		return new BlockPos(x + (j & 15), y + (j >> 16 & i), z + (j >> 8 & 15));
	}

	public boolean isSavingDisabled() {
		return false;
	}

	public Profiler getProfiler() {
		return (Profiler)this.profiler.get();
	}

	public Supplier<Profiler> getProfilerSupplier() {
		return this.profiler;
	}

	@Override
	public BiomeAccess getBiomeAccess() {
		return this.biomeAccess;
	}

	public final boolean isDebugWorld() {
		return this.debugWorld;
	}

	public abstract DynamicRegistryManager getRegistryManager();
}
