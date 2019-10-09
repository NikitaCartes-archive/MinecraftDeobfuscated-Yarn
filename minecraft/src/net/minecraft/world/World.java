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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
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
	private final long unusedWhite = 16777215L;
	private final Thread thread;
	private int ambientDarkness;
	protected int lcgBlockSeed = new Random().nextInt();
	protected final int unusedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	private int ticksSinceLightning;
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
		LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> biFunction, Profiler profiler, boolean bl
	) {
		this.profiler = profiler;
		this.properties = levelProperties;
		this.dimension = dimensionType.create(this);
		this.chunkManager = (ChunkManager)biFunction.apply(this, this.dimension);
		this.isClient = bl;
		this.border = this.dimension.createWorldBorder();
		this.thread = Thread.currentThread();
		this.biomeAccess = new BiomeAccess(
			this, bl ? levelProperties.getSeed() : LevelProperties.sha256Hash(levelProperties.getSeed()), dimensionType.getBiomeAccessType()
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

	public static boolean isValid(BlockPos blockPos) {
		return !isHeightInvalid(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
	}

	public static boolean isHeightInvalid(BlockPos blockPos) {
		return isHeightInvalid(blockPos.getY());
	}

	public static boolean isHeightInvalid(int i) {
		return i < 0 || i >= 256;
	}

	public WorldChunk getWorldChunk(BlockPos blockPos) {
		return this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public WorldChunk method_8497(int i, int j) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
	}

	@Override
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		Chunk chunk = this.chunkManager.getChunk(i, j, chunkStatus, bl);
		if (chunk == null && bl) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		if (isHeightInvalid(blockPos)) {
			return false;
		} else if (!this.isClient && this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		} else {
			WorldChunk worldChunk = this.getWorldChunk(blockPos);
			Block block = blockState.getBlock();
			BlockState blockState2 = worldChunk.setBlockState(blockPos, blockState, (i & 64) != 0);
			if (blockState2 == null) {
				return false;
			} else {
				BlockState blockState3 = this.getBlockState(blockPos);
				if (blockState3 != blockState2
					&& (
						blockState3.getOpacity(this, blockPos) != blockState2.getOpacity(this, blockPos)
							|| blockState3.getLuminance() != blockState2.getLuminance()
							|| blockState3.hasSidedTransparency()
							|| blockState2.hasSidedTransparency()
					)) {
					this.profiler.push("queueCheckLight");
					this.getChunkManager().getLightingProvider().checkBlock(blockPos);
					this.profiler.pop();
				}

				if (blockState3 == blockState) {
					if (blockState2 != blockState3) {
						this.scheduleBlockRender(blockPos, blockState2, blockState3);
					}

					if ((i & 2) != 0
						&& (!this.isClient || (i & 4) == 0)
						&& (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
						this.updateListeners(blockPos, blockState2, blockState, i);
					}

					if (!this.isClient && (i & 1) != 0) {
						this.updateNeighbors(blockPos, blockState2.getBlock());
						if (blockState.hasComparatorOutput()) {
							this.updateHorizontalAdjacent(blockPos, block);
						}
					}

					if ((i & 16) == 0) {
						int j = i & -2;
						blockState2.method_11637(this, blockPos, j);
						blockState.updateNeighborStates(this, blockPos, j);
						blockState.method_11637(this, blockPos, j);
					}

					this.onBlockChanged(blockPos, blockState2, blockState3);
				}

				return true;
			}
		}
	}

	public void onBlockChanged(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
	}

	@Override
	public boolean removeBlock(BlockPos blockPos, boolean bl) {
		FluidState fluidState = this.getFluidState(blockPos);
		return this.setBlockState(blockPos, fluidState.getBlockState(), 3 | (bl ? 64 : 0));
	}

	@Override
	public boolean breakBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity) {
		BlockState blockState = this.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(blockPos);
			this.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
			if (bl) {
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos) : null;
				Block.dropStacks(blockState, this, blockPos, blockEntity, entity, ItemStack.EMPTY);
			}

			return this.setBlockState(blockPos, fluidState.getBlockState(), 3);
		}
	}

	public boolean setBlockState(BlockPos blockPos, BlockState blockState) {
		return this.setBlockState(blockPos, blockState, 3);
	}

	public abstract void updateListeners(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);

	@Override
	public void updateNeighbors(BlockPos blockPos, Block block) {
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.updateNeighborsAlways(blockPos, block);
		}
	}

	public void scheduleBlockRender(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
	}

	public void updateNeighborsAlways(BlockPos blockPos, Block block) {
		this.updateNeighbor(blockPos.west(), block, blockPos);
		this.updateNeighbor(blockPos.east(), block, blockPos);
		this.updateNeighbor(blockPos.method_10074(), block, blockPos);
		this.updateNeighbor(blockPos.up(), block, blockPos);
		this.updateNeighbor(blockPos.north(), block, blockPos);
		this.updateNeighbor(blockPos.south(), block, blockPos);
	}

	public void updateNeighborsExcept(BlockPos blockPos, Block block, Direction direction) {
		if (direction != Direction.WEST) {
			this.updateNeighbor(blockPos.west(), block, blockPos);
		}

		if (direction != Direction.EAST) {
			this.updateNeighbor(blockPos.east(), block, blockPos);
		}

		if (direction != Direction.DOWN) {
			this.updateNeighbor(blockPos.method_10074(), block, blockPos);
		}

		if (direction != Direction.UP) {
			this.updateNeighbor(blockPos.up(), block, blockPos);
		}

		if (direction != Direction.NORTH) {
			this.updateNeighbor(blockPos.north(), block, blockPos);
		}

		if (direction != Direction.SOUTH) {
			this.updateNeighbor(blockPos.south(), block, blockPos);
		}
	}

	public void updateNeighbor(BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(blockPos);

			try {
				blockState.neighborUpdate(this, blockPos, block, blockPos2, false);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", (CrashCallable<String>)(() -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(block), block.getTranslationKey(), block.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.getId(block);
					}
				}));
				CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public int getTopY(Heightmap.Type type, int i, int j) {
		int k;
		if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
			if (this.isChunkLoaded(i >> 4, j >> 4)) {
				k = this.method_8497(i >> 4, j >> 4).sampleHeightmap(type, i & 15, j & 15) + 1;
			} else {
				k = 0;
			}
		} else {
			k = this.getSeaLevel() + 1;
		}

		return k;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.getChunkManager().getLightingProvider();
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			WorldChunk worldChunk = this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
			return worldChunk.getBlockState(blockPos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			WorldChunk worldChunk = this.getWorldChunk(blockPos);
			return worldChunk.getFluidState(blockPos);
		}
	}

	public boolean isDaylight() {
		return this.ambientDarkness < 4;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.playSound(playerEntity, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g);
	}

	public abstract void playSound(
		@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h
	);

	public abstract void playSoundFromEntity(
		@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g
	);

	public void playSound(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
	}

	@Override
	public void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
	}

	@Environment(EnvType.CLIENT)
	public void addParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	public void addImportantParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
	}

	public void addImportantParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Environment(EnvType.CLIENT)
	public float getAmbientLight(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getSkyColor(BlockPos blockPos, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		Biome biome = this.getBiome(blockPos);
		float i = biome.getTemperature(blockPos);
		int j = biome.getSkyColor(i);
		float k = (float)(j >> 16 & 0xFF) / 255.0F;
		float l = (float)(j >> 8 & 0xFF) / 255.0F;
		float m = (float)(j & 0xFF) / 255.0F;
		k *= h;
		l *= h;
		m *= h;
		float n = this.getRainGradient(f);
		if (n > 0.0F) {
			float o = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.6F;
			float p = 1.0F - n * 0.75F;
			k = k * p + o * (1.0F - p);
			l = l * p + o * (1.0F - p);
			m = m * p + o * (1.0F - p);
		}

		float o = this.getThunderGradient(f);
		if (o > 0.0F) {
			float p = (k * 0.3F + l * 0.59F + m * 0.11F) * 0.2F;
			float q = 1.0F - o * 0.75F;
			k = k * q + p * (1.0F - q);
			l = l * q + p * (1.0F - q);
			m = m * q + p * (1.0F - q);
		}

		if (this.ticksSinceLightning > 0) {
			float p = (float)this.ticksSinceLightning - f;
			if (p > 1.0F) {
				p = 1.0F;
			}

			p *= 0.45F;
			k = k * (1.0F - p) + 0.8F * p;
			l = l * (1.0F - p) + 0.8F * p;
			m = m * (1.0F - p) + 1.0F * p;
		}

		return new Vec3d((double)k, (double)l, (double)m);
	}

	public float getSkyAngleRadians(float f) {
		float g = this.getSkyAngle(f);
		return g * (float) (Math.PI * 2);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getCloudColor(float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		float i = 1.0F;
		float j = 1.0F;
		float k = 1.0F;
		float l = this.getRainGradient(f);
		if (l > 0.0F) {
			float m = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.6F;
			float n = 1.0F - l * 0.95F;
			i = i * n + m * (1.0F - n);
			j = j * n + m * (1.0F - n);
			k = k * n + m * (1.0F - n);
		}

		i *= h * 0.9F + 0.1F;
		j *= h * 0.9F + 0.1F;
		k *= h * 0.85F + 0.15F;
		float m = this.getThunderGradient(f);
		if (m > 0.0F) {
			float n = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.2F;
			float o = 1.0F - m * 0.95F;
			i = i * o + n * (1.0F - o);
			j = j * o + n * (1.0F - o);
			k = k * o + n * (1.0F - o);
		}

		return new Vec3d((double)i, (double)j, (double)k);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getFogColor(float f) {
		float g = this.getSkyAngle(f);
		return this.dimension.getFogColor(g, f);
	}

	@Environment(EnvType.CLIENT)
	public float getStarsBrightness(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
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
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
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
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (this.isRegionLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							Block block = this.getBlockState(pooledMutable.method_10113(o, p, q)).getBlock();
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
	public BlockState getBlockState(Box box, Block block) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (this.isRegionLoaded(i, k, m, j, l, n)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
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

	public boolean containsBlockWithMaterial(Box box, Material material) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		MaterialPredicate materialPredicate = MaterialPredicate.create(material);
		return BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).anyMatch(blockPos -> materialPredicate.method_11745(this.getBlockState(blockPos)));
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, Explosion.DestructionType destructionType) {
		return this.createExplosion(entity, null, d, e, f, g, false, destructionType);
	}

	public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
		return this.createExplosion(entity, null, d, e, f, g, bl, destructionType);
	}

	public Explosion createExplosion(
		@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, destructionType);
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
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		if (isHeightInvalid(blockPos)) {
			return null;
		} else if (!this.isClient && Thread.currentThread() != this.thread) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				blockEntity = this.getPendingBlockEntity(blockPos);
			}

			if (blockEntity == null) {
				blockEntity = this.getWorldChunk(blockPos).getBlockEntity(blockPos, WorldChunk.CreationType.IMMEDIATE);
			}

			if (blockEntity == null) {
				blockEntity = this.getPendingBlockEntity(blockPos);
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

	public void setBlockEntity(BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (!isHeightInvalid(blockPos)) {
			if (blockEntity != null && !blockEntity.isRemoved()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.setWorld(this, blockPos);
					Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();

					while (iterator.hasNext()) {
						BlockEntity blockEntity2 = (BlockEntity)iterator.next();
						if (blockEntity2.getPos().equals(blockPos)) {
							blockEntity2.markRemoved();
							iterator.remove();
						}
					}

					this.pendingBlockEntities.add(blockEntity);
				} else {
					this.getWorldChunk(blockPos).setBlockEntity(blockPos, blockEntity);
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

	public boolean canSetBlock(BlockPos blockPos) {
		return isHeightInvalid(blockPos) ? false : this.chunkManager.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	public boolean isTopSolid(BlockPos blockPos, Entity entity) {
		if (isHeightInvalid(blockPos)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
			return chunk == null ? false : chunk.getBlockState(blockPos).hasSolidTopSurface(this, blockPos, entity);
		}
	}

	public void calculateAmbientDarkness() {
		double d = 1.0 - (double)(this.getRainGradient(1.0F) * 5.0F) / 16.0;
		double e = 1.0 - (double)(this.getThunderGradient(1.0F) * 5.0F) / 16.0;
		double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
		this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
	}

	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.getChunkManager().setMobSpawnOptions(bl, bl2);
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
	public BlockView getExistingChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.FULL, false);
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity entity, Box box, @Nullable Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(entity, box, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> entityType, Box box, Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
		List<T> list = Lists.<T>newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
				if (worldChunk != null) {
					worldChunk.getEntities(entityType, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntities(Class<? extends T> class_, Box box, @Nullable Predicate<? super T> predicate) {
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
					worldChunk.getEntities(class_, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Override
	public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> class_, Box box, @Nullable Predicate<? super T> predicate) {
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
					worldChunk.getEntities(class_, box, list, predicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public abstract Entity getEntityById(int i);

	public void markDirty(BlockPos blockPos, BlockEntity blockEntity) {
		if (this.isChunkLoaded(blockPos)) {
			this.getWorldChunk(blockPos).markDirty();
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
		i = Math.max(i, this.getStrongRedstonePower(blockPos.method_10074(), Direction.DOWN));
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

	public boolean isEmittingRedstonePower(BlockPos blockPos, Direction direction) {
		return this.getEmittedRedstonePower(blockPos, direction) > 0;
	}

	public int getEmittedRedstonePower(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.getBlockState(blockPos);
		return blockState.isSimpleFullBlock(this, blockPos)
			? this.getReceivedStrongRedstonePower(blockPos)
			: blockState.getWeakRedstonePower(this, blockPos, direction);
	}

	public boolean isReceivingRedstonePower(BlockPos blockPos) {
		if (this.getEmittedRedstonePower(blockPos.method_10074(), Direction.DOWN) > 0) {
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

	public void setTime(long l) {
		this.properties.setTime(l);
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

	public void setTimeOfDay(long l) {
		this.properties.setTimeOfDay(l);
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

	public void setSpawnPos(BlockPos blockPos) {
		this.properties.setSpawnPos(blockPos);
	}

	public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
		return true;
	}

	public void sendEntityStatus(Entity entity, byte b) {
	}

	@Override
	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}

	public void addBlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.getBlockState(blockPos).onBlockAction(this, blockPos, i, j);
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
	public void setThunderGradient(float f) {
		this.thunderGradientPrev = f;
		this.thunderGradient = f;
	}

	public float getRainGradient(float f) {
		return MathHelper.lerp(f, this.rainGradientPrev, this.rainGradient);
	}

	@Environment(EnvType.CLIENT)
	public void setRainGradient(float f) {
		this.rainGradientPrev = f;
		this.rainGradient = f;
	}

	public boolean isThundering() {
		return this.dimension.hasSkyLight() && !this.dimension.isNether() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos blockPos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.isSkyVisible(blockPos)) {
			return false;
		} else {
			return this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()
				? false
				: this.getBiome(blockPos).getPrecipitation() == Biome.Precipitation.RAIN;
		}
	}

	public boolean hasHighHumidity(BlockPos blockPos) {
		Biome biome = this.getBiome(blockPos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public abstract MapState getMapState(String string);

	public abstract void putMapState(MapState mapState);

	public abstract int getNextMapId();

	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
	}

	public int getEffectiveHeight() {
		return this.dimension.isNether() ? 128 : 256;
	}

	@Environment(EnvType.CLIENT)
	public double getHorizonHeight() {
		return this.properties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection addDetailsToCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.addElement("Affected level", 1);
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

	public abstract void setBlockBreakingProgress(int i, BlockPos blockPos, int j);

	@Environment(EnvType.CLIENT)
	public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
	}

	public abstract Scoreboard getScoreboard();

	public void updateHorizontalAdjacent(BlockPos blockPos, Block block) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (this.isChunkLoaded(blockPos2)) {
				BlockState blockState = this.getBlockState(blockPos2);
				if (blockState.getBlock() == Blocks.COMPARATOR) {
					blockState.neighborUpdate(this, blockPos2, block, blockPos, false);
				} else if (blockState.isSimpleFullBlock(this, blockPos2)) {
					blockPos2 = blockPos2.offset(direction);
					blockState = this.getBlockState(blockPos2);
					if (blockState.getBlock() == Blocks.COMPARATOR) {
						blockState.neighborUpdate(this, blockPos2, block, blockPos, false);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos blockPos) {
		long l = 0L;
		float f = 0.0F;
		if (this.isChunkLoaded(blockPos)) {
			f = this.getMoonSize();
			l = this.getWorldChunk(blockPos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksSinceLightning() {
		return this.ticksSinceLightning;
	}

	public void setTicksSinceLightning(int i) {
		this.ticksSinceLightning = i;
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
	public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> predicate) {
		return predicate.test(this.getBlockState(blockPos));
	}

	public abstract RecipeManager getRecipeManager();

	public abstract RegistryTagManager getTagManager();

	public BlockPos getRandomPosInChunk(int i, int j, int k, int l) {
		this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
		int m = this.lcgBlockSeed >> 2;
		return new BlockPos(i + (m & 15), j + (m >> 16 & l), k + (m >> 8 & 15));
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
