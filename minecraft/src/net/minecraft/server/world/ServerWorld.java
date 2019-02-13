package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1419;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.parts.EntityPart;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Tickable;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.Void;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.WorldVillageManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Entity> globalEntities = Lists.<Entity>newArrayList();
	private final List<Entity> regularEntities = Lists.<Entity>newArrayList();
	private final IntHashMap<Entity> entitiesById = new IntHashMap<>();
	private final List<Entity> entitiesToUnload = Lists.<Entity>newArrayList();
	private final List<BlockEntity> blockEntitiesToUnload = Lists.<BlockEntity>newArrayList();
	private final MinecraftServer server;
	private final WorldSaveHandler worldSaveHandler;
	private final EntityTracker entityTracker;
	private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
	public boolean savingDisabled;
	private boolean allPlayersSleeping;
	private int idleTimeout;
	private final PortalForcer portalForcer;
	private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<>(
		this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::method_14189
	);
	private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<>(
		this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this::method_14171
	);
	private final List<EntityNavigation> field_17912 = Lists.<EntityNavigation>newArrayList();
	protected final class_1419 field_13958 = new class_1419(this);
	private final ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions = new ObjectLinkedOpenHashSet<>();
	private boolean insideTick;

	public ServerWorld(
		MinecraftServer minecraftServer,
		Executor executor,
		WorldSaveHandler worldSaveHandler,
		LevelProperties levelProperties,
		DimensionType dimensionType,
		Profiler profiler,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		super(
			levelProperties,
			dimensionType,
			(world, dimension) -> new ServerChunkManager(
					(ServerWorld)world,
					worldSaveHandler.getWorldDir(),
					worldSaveHandler.getDataFixer(),
					worldSaveHandler.getStructureManager(),
					executor,
					dimension.createChunkGenerator(),
					minecraftServer.getPlayerManager().getViewDistance(),
					worldGenerationProgressListener,
					() -> minecraftServer.getWorld(DimensionType.field_13072).getPersistentStateManager()
				),
			profiler,
			false
		);
		this.worldSaveHandler = worldSaveHandler;
		this.server = minecraftServer;
		this.entityTracker = new EntityTracker(this);
		this.portalForcer = new PortalForcer(this);
		this.updateAmbientDarkness();
		this.initWeatherGradients();
		this.getWorldBorder().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
		this.raidManager = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.dimension));
		PersistentStateManager persistentStateManager = (this.dimension.getType() == DimensionType.field_13072
				? this
				: this.getServer().getWorld(DimensionType.field_13072))
			.getPersistentStateManager();
		this.villageManager = persistentStateManager.getOrCreate(() -> new WorldVillageManager(this), WorldVillageManager.getPersistentDataKey(this.dimension));
		this.villageManager.initRaids();
		if (!minecraftServer.isSinglePlayer()) {
			this.getLevelProperties().setGameMode(minecraftServer.getDefaultGameMode());
		}
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.insideTick = true;
		super.tick(booleanSupplier);
		if (this.getLevelProperties().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
			this.getLevelProperties().setDifficulty(Difficulty.HARD);
		}

		if (this.shouldSkipToMorning()) {
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long l = this.properties.getTimeOfDay() + 24000L;
				this.setTimeOfDay(l - l % 24000L);
			}

			this.onSkippedToMorning();
		}

		int i = this.calculateAmbientDarkness(1.0F);
		if (i != this.getAmbientDarkness()) {
			this.setAmbientDarkness(i);
		}

		this.tickTime();
		this.getProfiler().push("chunkSource");
		this.getChunkManager().tick(booleanSupplier);
		this.getProfiler().swap("tickPending");
		this.tickScheduledTicks();
		this.getProfiler().swap("village");
		this.villageManager.tick();
		this.field_13958.method_6445();
		this.getProfiler().swap("portalForcer");
		this.portalForcer.tick(this.getTime());
		this.getProfiler().swap("raid");
		this.raidManager.tick();
		this.getProfiler().pop();
		this.sendBlockActions();
		this.insideTick = false;
	}

	public void method_18203(WorldChunk worldChunk, int i) {
		ChunkPos chunkPos = worldChunk.getPos();
		boolean bl = this.isRaining();
		int j = chunkPos.getStartX();
		int k = chunkPos.getStartZ();
		Profiler profiler = this.getProfiler();
		profiler.push("thunder");
		if (bl && this.isThundering() && this.random.nextInt(100000) == 0) {
			BlockPos blockPos = this.method_18210(this.getRandomPosInChunk(j, 0, k, 15));
			if (this.hasRain(blockPos)) {
				LocalDifficulty localDifficulty = this.getLocalDifficulty(blockPos);
				boolean bl2 = this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
				if (bl2) {
					SkeletonHorseEntity skeletonHorseEntity = new SkeletonHorseEntity(this);
					skeletonHorseEntity.method_6813(true);
					skeletonHorseEntity.setBreedingAge(0);
					skeletonHorseEntity.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
					this.spawnEntity(skeletonHorseEntity);
				}

				this.addLightning(new LightningEntity(this, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, bl2));
			}
		}

		profiler.swap("iceandsnow");
		if (this.random.nextInt(16) == 0) {
			BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, this.getRandomPosInChunk(j, 0, k, 15));
			BlockPos blockPos2 = blockPos.down();
			Biome biome = this.getBiome(blockPos);
			if (biome.canSetSnow(this, blockPos2)) {
				this.setBlockState(blockPos2, Blocks.field_10295.getDefaultState());
			}

			if (bl && biome.canSetIce(this, blockPos)) {
				this.setBlockState(blockPos, Blocks.field_10477.getDefaultState());
			}

			if (bl && this.getBiome(blockPos2).getPrecipitation() == Biome.Precipitation.RAIN) {
				this.getBlockState(blockPos2).getBlock().onRainTick(this, blockPos2);
			}
		}

		profiler.swap("tickBlocks");
		if (i > 0) {
			for (ChunkSection chunkSection : worldChunk.getSectionArray()) {
				if (chunkSection != WorldChunk.EMPTY_SECTION && chunkSection.hasRandomTicks()) {
					int l = chunkSection.getYOffset();

					for (int m = 0; m < i; m++) {
						BlockPos blockPos3 = this.getRandomPosInChunk(j, l, k, 15);
						profiler.push("randomTick");
						BlockState blockState = chunkSection.getBlockState(blockPos3.getX() - j, blockPos3.getY() - l, blockPos3.getZ() - k);
						if (blockState.hasRandomTicks()) {
							blockState.onRandomTick(this, blockPos3, this.random);
						}

						FluidState fluidState = chunkSection.getFluidState(blockPos3.getX() - j, blockPos3.getY() - l, blockPos3.getZ() - k);
						if (fluidState.hasRandomTicks()) {
							fluidState.onRandomTick(this, blockPos3, this.random);
						}

						profiler.pop();
					}
				}
			}
		}

		profiler.pop();
	}

	protected BlockPos method_18210(BlockPos blockPos) {
		BlockPos blockPos2 = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos);
		BoundingBox boundingBox = new BoundingBox(blockPos2, new BlockPos(blockPos2.getX(), this.getHeight(), blockPos2.getZ())).expand(3.0);
		List<LivingEntity> list = this.getEntities(
			LivingEntity.class, boundingBox, livingEntity -> livingEntity != null && livingEntity.isValid() && this.isSkyVisible(livingEntity.getPos())
		);
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).getPos();
		} else {
			if (blockPos2.getY() == -1) {
				blockPos2 = blockPos2.up(2);
			}

			return blockPos2;
		}
	}

	public boolean isInsideTick() {
		return this.insideTick;
	}

	public void updatePlayersSleeping() {
		this.allPlayersSleeping = false;
		if (!this.players.isEmpty()) {
			int i = 0;
			int j = 0;

			for (PlayerEntity playerEntity : this.players) {
				if (playerEntity.isSpectator()) {
					i++;
				} else if (playerEntity.isSleeping()) {
					j++;
				}
			}

			this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
		}
	}

	public ServerScoreboard getScoreboard() {
		return this.server.getScoreboard();
	}

	protected void onSkippedToMorning() {
		this.allPlayersSleeping = false;

		for (PlayerEntity playerEntity : (List)this.players.stream().filter(PlayerEntity::isSleeping).collect(Collectors.toList())) {
			playerEntity.wakeUp(false, false, true);
		}

		if (this.getGameRules().getBoolean("doWeatherCycle")) {
			this.resetWeather();
		}
	}

	private void resetWeather() {
		this.properties.setRainTime(0);
		this.properties.setRaining(false);
		this.properties.setThunderTime(0);
		this.properties.setThundering(false);
	}

	public boolean shouldSkipToMorning() {
		if (this.allPlayersSleeping && !this.isClient) {
			for (PlayerEntity playerEntity : this.players) {
				if (!playerEntity.isSpectator() && !playerEntity.isSleepingLongEnough()) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setDefaultSpawnClient() {
		if (this.properties.getSpawnY() <= 0) {
			this.properties.setSpawnY(this.getSeaLevel() + 1);
		}

		int i = this.properties.getSpawnX();
		int j = this.properties.getSpawnZ();
		int k = 0;

		while (this.getTopNonAirState(new BlockPos(i, 0, j)).isAir()) {
			i += this.random.nextInt(8) - this.random.nextInt(8);
			j += this.random.nextInt(8) - this.random.nextInt(8);
			if (++k == 10000) {
				break;
			}
		}

		this.properties.setSpawnX(i);
		this.properties.setSpawnZ(j);
	}

	public void tickEntities() {
		if (this.players.isEmpty() && this.getForcedChunks().isEmpty()) {
			if (this.idleTimeout++ >= 300) {
				return;
			}
		} else {
			this.resetIdleTimeout();
		}

		this.dimension.update();
		Profiler profiler = this.getProfiler();
		profiler.push("entities");
		profiler.push("global");

		for (int i = 0; i < this.globalEntities.size(); i++) {
			Entity entity = (Entity)this.globalEntities.get(i);

			try {
				entity.age++;
				entity.update();
			} catch (Throwable var10) {
				CrashReport crashReport = CrashReport.create(var10, "Ticking entity");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
				if (entity == null) {
					crashReportSection.add("Entity", "~~NULL~~");
				} else {
					entity.populateCrashReport(crashReportSection);
				}

				throw new CrashException(crashReport);
			}

			if (entity.invalid) {
				this.globalEntities.remove(i--);
			}
		}

		profiler.swap("remove");
		this.method_18208();
		this.method_8541();
		profiler.swap("regular");

		for (int i = 0; i < this.regularEntities.size(); i++) {
			Entity entity = (Entity)this.regularEntities.get(i);
			Entity entity2 = entity.getRiddenEntity();
			if (entity2 != null) {
				if (!entity2.invalid && entity2.hasPassenger(entity)) {
					continue;
				}

				entity.stopRiding();
			}

			profiler.push("tick");
			if (!entity.invalid && !(entity instanceof ServerPlayerEntity)) {
				try {
					this.tickEntity(entity);
				} catch (Throwable var9) {
					CrashReport crashReport2 = CrashReport.create(var9, "Ticking entity");
					CrashReportSection crashReportSection2 = crashReport2.addElement("Entity being ticked");
					entity.populateCrashReport(crashReportSection2);
					throw new CrashException(crashReport2);
				}
			}

			profiler.pop();
			profiler.push("remove");
			if (this.removeEntityIfInvalid(entity)) {
				i--;
			}

			profiler.pop();
		}

		profiler.swap("blockEntities");
		if (!this.blockEntitiesToUnload.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.blockEntitiesToUnload);
			this.blockEntities.removeAll(this.blockEntitiesToUnload);
			this.blockEntitiesToUnload.clear();
		}

		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isInvalid() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.getPos();
				if (this.isBlockLoaded(blockPos) && this.getWorldBorder().contains(blockPos)) {
					try {
						profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntityType.getId(blockEntity.getType()))));
						((Tickable)blockEntity).tick();
						profiler.pop();
					} catch (Throwable var8) {
						CrashReport crashReport2 = CrashReport.create(var8, "Ticking block entity");
						CrashReportSection crashReportSection2 = crashReport2.addElement("Block entity being ticked");
						blockEntity.populateCrashReport(crashReportSection2);
						throw new CrashException(crashReport2);
					}
				}
			}

			if (blockEntity.isInvalid()) {
				iterator.remove();
				this.blockEntities.remove(blockEntity);
				if (this.isBlockLoaded(blockEntity.getPos())) {
					this.getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
				}
			}
		}

		this.iteratingTickingBlockEntities = false;
		profiler.swap("pendingBlockEntities");
		if (!this.pendingBlockEntities.isEmpty()) {
			for (int j = 0; j < this.pendingBlockEntities.size(); j++) {
				BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(j);
				if (!blockEntity2.isInvalid()) {
					if (!this.blockEntities.contains(blockEntity2)) {
						this.addBlockEntity(blockEntity2);
					}

					if (this.isBlockLoaded(blockEntity2.getPos())) {
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
		profiler.pop();
	}

	private boolean removeEntityIfInvalid(Entity entity) {
		if (entity.invalid) {
			int i = entity.chunkX;
			int j = entity.chunkZ;
			if (entity.field_6016 && this.isChunkLoaded(i, j)) {
				this.getWorldChunk(i, j).remove(entity);
			}

			this.regularEntities.remove(entity);
			this.method_8539(entity);
			return true;
		} else {
			return false;
		}
	}

	private void method_18208() {
		this.regularEntities.removeAll(this.entitiesToUnload);

		for (int i = 0; i < this.entitiesToUnload.size(); i++) {
			Entity entity = (Entity)this.entitiesToUnload.get(i);
			int j = entity.chunkX;
			int k = entity.chunkZ;
			if (entity.field_6016 && this.isChunkLoaded(j, k)) {
				this.getWorldChunk(j, k).remove(entity);
			}
		}

		for (int ix = 0; ix < this.entitiesToUnload.size(); ix++) {
			this.method_8539((Entity)this.entitiesToUnload.get(ix));
		}

		this.entitiesToUnload.clear();
	}

	public void method_18202(WorldChunk worldChunk) {
		worldChunk.setLoadedToWorld(false);

		for (BlockEntity blockEntity : worldChunk.getBlockEntityMap().values()) {
			this.unloadBlockEntity(blockEntity);
		}

		for (TypeFilterableList<Entity> typeFilterableList : worldChunk.getEntitySectionArray()) {
			this.unloadEntities(typeFilterableList);
		}
	}

	public void unloadEntities(Collection<Entity> collection) {
		this.entitiesToUnload.addAll(collection);
	}

	public void unloadBlockEntity(BlockEntity blockEntity) {
		this.blockEntitiesToUnload.add(blockEntity);
	}

	protected void method_8541() {
		this.getProfiler().swap("players");

		for (int i = 0; i < this.players.size(); i++) {
			Entity entity = (Entity)this.players.get(i);
			Entity entity2 = entity.getRiddenEntity();
			if (entity2 != null) {
				if (!entity2.invalid && entity2.hasPassenger(entity)) {
					continue;
				}

				entity.stopRiding();
			}

			this.getProfiler().push("tick");
			if (!entity.invalid) {
				try {
					this.tickEntity(entity);
				} catch (Throwable var7) {
					CrashReport crashReport = CrashReport.create(var7, "Ticking player");
					CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
					entity.populateCrashReport(crashReportSection);
					throw new CrashException(crashReport);
				}
			}

			this.getProfiler().pop();
			this.getProfiler().push("remove");
			this.removeEntityIfInvalid(entity);
			this.getProfiler().pop();
		}
	}

	public void resetIdleTimeout() {
		this.idleTimeout = 0;
	}

	public void tickScheduledTicks() {
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.blockTickScheduler.tick();
			this.fluidTickScheduler.tick();
		}
	}

	private void method_14171(ScheduledTick<Fluid> scheduledTick) {
		FluidState fluidState = this.getFluidState(scheduledTick.pos);
		if (fluidState.getFluid() == scheduledTick.getObject()) {
			fluidState.onScheduledTick(this, scheduledTick.pos);
		}
	}

	private void method_14189(ScheduledTick<Block> scheduledTick) {
		BlockState blockState = this.getBlockState(scheduledTick.pos);
		if (blockState.getBlock() == scheduledTick.getObject()) {
			blockState.scheduledTick(this, scheduledTick.pos, this.random);
		}
	}

	@Override
	public void tickEntity(Entity entity) {
		if (!this.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
			entity.invalidate();
		}

		if (!this.shouldSpawnNpcs() && entity instanceof Npc) {
			entity.invalidate();
		}

		super.tickEntity(entity);
	}

	@Override
	public void updateChunkEntities(Entity entity) {
		if (!this.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
			entity.invalidate();
		}

		if (!this.shouldSpawnNpcs() && entity instanceof Npc) {
			entity.invalidate();
		}

		super.updateChunkEntities(entity);
	}

	private boolean shouldSpawnNpcs() {
		return this.server.shouldSpawnNpcs();
	}

	private boolean shouldSpawnAnimals() {
		return this.server.shouldSpawnAnimals();
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
		return !this.server.isSpawnProtected(this, blockPos, playerEntity) && this.getWorldBorder().contains(blockPos);
	}

	public void init(LevelInfo levelInfo) {
		if (!this.dimension.canPlayersSleep()) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up(this.chunkManager.getChunkGenerator().getSpawnHeight()));
		} else if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up());
		} else {
			BiomeSource biomeSource = this.chunkManager.getChunkGenerator().getBiomeSource();
			List<Biome> list = biomeSource.getSpawnBiomes();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = biomeSource.locateBiome(0, 0, 256, list, random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.field_15478.values()) {
				if (biomeSource.getTopMaterials().contains(block.getDefaultState())) {
					bl = true;
					break;
				}
			}

			this.properties.setSpawnPos(chunkPos.getCenterBlockPos().add(8, this.chunkManager.getChunkGenerator().getSpawnHeight(), 8));
			int i = 0;
			int j = 0;
			int k = 0;
			int l = -1;
			int m = 32;

			for (int n = 0; n < 1024; n++) {
				if (i > -16 && i <= 16 && j > -16 && j <= 16) {
					BlockPos blockPos2 = this.dimension.getSpawningBlockInChunk(new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl);
					if (blockPos2 != null) {
						this.properties.setSpawnPos(blockPos2);
						break;
					}
				}

				if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
					int o = k;
					k = -l;
					l = o;
				}

				i += k;
				j += l;
			}

			if (levelInfo.hasBonusChest()) {
				this.placeBonusChest();
			}
		}
	}

	protected void placeBonusChest() {
		BonusChestFeature bonusChestFeature = Feature.BONUS_CHEST;

		for (int i = 0; i < 10; i++) {
			int j = this.properties.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
			int k = this.properties.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
			BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
			if (bonusChestFeature.method_12817(
				this, (ChunkGenerator<? extends ChunkGeneratorConfig>)this.chunkManager.getChunkGenerator(), this.random, blockPos, FeatureConfig.DEFAULT
			)) {
				break;
			}
		}
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return this.dimension.getForcedSpawnPoint();
	}

	public void save(@Nullable ProgressListener progressListener, boolean bl, boolean bl2) throws SessionLockException {
		ServerChunkManager serverChunkManager = this.getChunkManager();
		if (!bl2) {
			if (progressListener != null) {
				progressListener.method_15412(new TranslatableTextComponent("menu.savingLevel"));
			}

			this.saveLevel();
			if (progressListener != null) {
				progressListener.method_15414(new TranslatableTextComponent("menu.savingChunks"));
			}

			serverChunkManager.save(bl);
		}
	}

	protected void saveLevel() throws SessionLockException {
		this.checkSessionLock();
		this.dimension.saveWorldData();
		this.getChunkManager().getPersistentStateManager().save();
	}

	public List<Entity> method_18198(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();

		for (Entity entity : this.regularEntities) {
			if ((entityType == null || entity.getType() == entityType) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public <T extends Entity> List<T> method_18205(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.<T>newArrayList();

		for (Entity entity : this.regularEntities) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public Object2IntMap<EntityCategory> method_18219() {
		Object2IntMap<EntityCategory> object2IntMap = new Object2IntOpenHashMap<>();

		for (Entity entity : this.regularEntities) {
			if (!(entity instanceof MobEntity) || !((MobEntity)entity).isPersistent()) {
				EntityCategory entityCategory = entity.getType().getEntityClass();
				if (entityCategory != EntityCategory.field_17715) {
					object2IntMap.computeInt(entityCategory, (entityCategoryx, integer) -> 1 + (integer == null ? 0 : integer));
				}
			}
		}

		return object2IntMap;
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.method_18209(entity, true);
	}

	public boolean method_18197(Entity entity, boolean bl) {
		return this.method_18209(entity, bl);
	}

	public void method_18207(ServerPlayerEntity serverPlayerEntity) {
		this.method_18209(serverPlayerEntity, true);
	}

	public void method_18211(ServerPlayerEntity serverPlayerEntity) {
		this.method_18209(serverPlayerEntity, true);
	}

	public void method_18213(ServerPlayerEntity serverPlayerEntity) {
		this.method_18209(serverPlayerEntity, true);
	}

	public void method_18215(ServerPlayerEntity serverPlayerEntity) {
		this.method_18209(serverPlayerEntity, true);
	}

	public void method_18214(Entity entity) {
		this.method_18209(entity, true);
	}

	private boolean method_18209(Entity entity, boolean bl) {
		if (!this.method_14175(entity)) {
			return false;
		} else {
			Chunk chunk;
			if (bl) {
				chunk = this.getChunk(
					MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, entity.teleporting || entity instanceof PlayerEntity
				);
				if (!(chunk instanceof WorldChunk)) {
					return false;
				}
			} else {
				chunk = null;
			}

			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				this.players.add(playerEntity);
				this.updatePlayersSleeping();
			}

			if (chunk != null) {
				chunk.addEntity(entity);
			}

			this.regularEntities.add(entity);
			this.getEntityTracker().add(entity);
			if (entity instanceof MobEntity) {
				this.field_17912.add(((MobEntity)entity).getNavigation());
			}

			this.entitiesById.put(entity.getEntityId(), entity);
			this.entitiesByUuid.put(entity.getUuid(), entity);
			EntityPart[] entityParts = entity.getParts();
			if (entityParts != null) {
				for (Entity entity2 : entityParts) {
					this.entitiesById.put(entity2.getEntityId(), entity2);
				}
			}

			return true;
		}
	}

	private boolean method_14175(Entity entity) {
		if (entity.invalid) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		} else {
			UUID uUID = entity.getUuid();
			if (this.entitiesByUuid.containsKey(uUID)) {
				Entity entity2 = (Entity)this.entitiesByUuid.get(uUID);
				if (this.entitiesToUnload.contains(entity2)) {
					this.entitiesToUnload.remove(entity2);
				} else {
					if (!(entity instanceof PlayerEntity)) {
						LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.getType()), uUID.toString());
						return false;
					}

					LOGGER.warn("Force-added player with duplicate UUID {}", uUID.toString());
				}

				this.method_18217(entity2);
			}

			return true;
		}
	}

	@Override
	public void setBlockBreakingProgress(int i, BlockPos blockPos, int j) {
		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			if (serverPlayerEntity != null && serverPlayerEntity.world == this && serverPlayerEntity.getEntityId() != i) {
				double d = (double)blockPos.getX() - serverPlayerEntity.x;
				double e = (double)blockPos.getY() - serverPlayerEntity.y;
				double f = (double)blockPos.getZ() - serverPlayerEntity.z;
				if (d * d + e * e + f * f < 1024.0) {
					serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(i, blockPos, j));
				}
			}
		}
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h) {
		this.server
			.getPlayerManager()
			.sendToAround(
				playerEntity, d, e, f, g > 1.0F ? (double)(16.0F * g) : 16.0, this.dimension.getType(), new PlaySoundS2CPacket(soundEvent, soundCategory, d, e, f, g, h)
			);
	}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.server
			.getPlayerManager()
			.sendToAround(
				playerEntity,
				entity.x,
				entity.y,
				entity.z,
				f > 1.0F ? (double)(16.0F * f) : 16.0,
				this.dimension.getType(),
				new PlaySoundFromEntityS2CPacket(soundEvent, soundCategory, entity, f, g)
			);
	}

	@Override
	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
		this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(i, blockPos, j, true));
	}

	@Override
	public void playEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		this.server
			.getPlayerManager()
			.sendToAround(
				playerEntity,
				(double)blockPos.getX(),
				(double)blockPos.getY(),
				(double)blockPos.getZ(),
				64.0,
				this.dimension.getType(),
				new WorldEventS2CPacket(i, blockPos, j, false)
			);
	}

	@Override
	public void updateListeners(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.getChunkManager().markForUpdate(blockPos);
		if (this.method_18204(blockPos, blockState, blockState2)) {
			for (EntityNavigation entityNavigation : this.field_17912) {
				if (entityNavigation != null && !entityNavigation.shouldRecalculatePath()) {
					entityNavigation.method_18053(blockPos);
				}
			}
		}
	}

	private boolean method_18204(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
		VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos);
		return VoxelShapes.compareShapes(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME);
	}

	public void method_18216(Entity entity) {
		if (entity.hasPassengers()) {
			entity.removeAllPassengers();
		}

		if (entity.hasVehicle()) {
			entity.stopRiding();
		}

		entity.invalidate();
		if (entity instanceof PlayerEntity) {
			this.players.remove(entity);
			this.updatePlayersSleeping();
			this.method_8539(entity);
		}
	}

	public void method_18217(Entity entity) {
		entity.setInWorld(false);
		entity.invalidate();
		if (entity instanceof PlayerEntity) {
			this.players.remove(entity);
			this.updatePlayersSleeping();
		}

		this.removeEntityIfInvalid(entity);
	}

	protected void method_8539(Entity entity) {
		this.getEntityTracker().remove(entity);
		this.getScoreboard().resetEntityScore(entity);
		if (entity instanceof MobEntity) {
			this.field_17912.remove(((MobEntity)entity).getNavigation());
		}

		this.entitiesById.remove(entity.getEntityId());
		this.entitiesByUuid.remove(entity.getUuid());
		EntityPart[] entityParts = entity.getParts();
		if (entityParts != null) {
			for (Entity entity2 : entityParts) {
				this.entitiesById.remove(entity2.getEntityId());
			}
		}
	}

	public boolean addLightning(LightningEntity lightningEntity) {
		this.globalEntities.add(lightningEntity);
		this.server
			.getPlayerManager()
			.sendToAround(
				null, lightningEntity.x, lightningEntity.y, lightningEntity.z, 512.0, this.dimension.getType(), new EntitySpawnGlobalS2CPacket(lightningEntity)
			);
		return true;
	}

	@Override
	public void summonParticle(Entity entity, byte b) {
		this.getEntityTracker().sendToTrackingPlayersAndSelf(entity, new EntityStatusS2CPacket(entity, b));
	}

	public ServerChunkManager getChunkManager() {
		return (ServerChunkManager)super.getChunkManager();
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<WorldChunk> getChunkSyncIfServerThread(int i, int j, boolean bl) {
		return this.getChunkManager()
			.getChunkSyncIfServerThread(i, j, ChunkStatus.FULL, bl)
			.thenApply(either -> either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
	}

	@Override
	public Explosion createExplosion(@Nullable Entity entity, DamageSource damageSource, double d, double e, double f, float g, boolean bl, boolean bl2) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, bl2);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (!bl2) {
			explosion.clearAffectedBlocks();
		}

		for (PlayerEntity playerEntity : this.players) {
			if (playerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				((ServerPlayerEntity)playerEntity)
					.networkHandler
					.sendPacket(new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(playerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void addBlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.pendingBlockActions.add(new BlockAction(blockPos, block, i, j));
	}

	private void sendBlockActions() {
		while (!this.pendingBlockActions.isEmpty()) {
			BlockAction blockAction = this.pendingBlockActions.removeFirst();
			if (this.method_14174(blockAction)) {
				this.server
					.getPlayerManager()
					.sendToAround(
						null,
						(double)blockAction.getPos().getX(),
						(double)blockAction.getPos().getY(),
						(double)blockAction.getPos().getZ(),
						64.0,
						this.dimension.getType(),
						new BlockActionS2CPacket(blockAction.getPos(), blockAction.getBlock(), blockAction.getType(), blockAction.getData())
					);
			}
		}
	}

	private boolean method_14174(BlockAction blockAction) {
		BlockState blockState = this.getBlockState(blockAction.getPos());
		return blockState.getBlock() == blockAction.getBlock()
			? blockState.onBlockAction(this, blockAction.getPos(), blockAction.getType(), blockAction.getData())
			: false;
	}

	@Override
	public void close() throws IOException {
		this.chunkManager.close();
		super.close();
	}

	@Override
	protected void updateWeather() {
		boolean bl = this.isRaining();
		super.updateWeather();
		if (this.rainGradientPrev != this.rainGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.getType());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.getType());
		}

		if (bl != this.isRaining()) {
			if (bl) {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0F));
			} else {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0F));
			}

			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
		}
	}

	public ServerTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	public ServerTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Nonnull
	@Override
	public MinecraftServer getServer() {
		return this.server;
	}

	public EntityTracker getEntityTracker() {
		return this.entityTracker;
	}

	public PortalForcer getPortalForcer() {
		return this.portalForcer;
	}

	public StructureManager getStructureManager() {
		return this.worldSaveHandler.getStructureManager();
	}

	public <T extends ParticleParameters> int method_14199(T particleParameters, double d, double e, double f, int i, double g, double h, double j, double k) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(
			particleParameters, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i
		);
		int l = 0;

		for (int m = 0; m < this.players.size(); m++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(m);
			if (this.method_14191(serverPlayerEntity, false, d, e, f, particleS2CPacket)) {
				l++;
			}
		}

		return l;
	}

	public <T extends ParticleParameters> boolean method_14166(
		ServerPlayerEntity serverPlayerEntity, T particleParameters, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		Packet<?> packet = new ParticleS2CPacket(particleParameters, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.method_14191(serverPlayerEntity, bl, d, e, f, packet);
	}

	private boolean method_14191(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		if (serverPlayerEntity.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = serverPlayerEntity.getPos();
			double g = blockPos.squaredDistanceTo(d, e, f);
			if (!(g <= 1024.0) && (!bl || !(g <= 262144.0))) {
				return false;
			} else {
				serverPlayerEntity.networkHandler.sendPacket(packet);
				return true;
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int i) {
		return this.entitiesById.get(i);
	}

	@Nullable
	public Entity getEntity(UUID uUID) {
		return (Entity)this.entitiesByUuid.get(uUID);
	}

	@Nullable
	@Override
	public BlockPos locateStructure(String string, BlockPos blockPos, int i, boolean bl) {
		return this.getChunkManager().getChunkGenerator().locateStructure(this, string, blockPos, i, bl);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.server.getRecipeManager();
	}

	@Override
	public TagManager getTagManager() {
		return this.server.getTagManager();
	}

	@Override
	public void setTime(long l) {
		super.setTime(l);
		this.properties.getScheduledEvents().processEvents(this.server, l);
	}

	@Override
	public boolean isSavingDisabled() {
		return this.savingDisabled;
	}

	public void checkSessionLock() throws SessionLockException {
		this.worldSaveHandler.checkSessionLock();
	}

	public WorldSaveHandler getSaveHandler() {
		return this.worldSaveHandler;
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.getChunkManager().getPersistentStateManager();
	}

	@Nullable
	@Override
	public MapState getMapState(String string) {
		return this.getServer().getWorld(DimensionType.field_13072).getPersistentStateManager().get(() -> new MapState(string), string);
	}

	@Override
	public void putMapState(MapState mapState) {
		this.getServer().getWorld(DimensionType.field_13072).getPersistentStateManager().set(mapState);
	}

	@Override
	public int getNextMapId() {
		return this.getServer()
			.getWorld(DimensionType.field_13072)
			.getPersistentStateManager()
			.<IdCountsState>getOrCreate(IdCountsState::new, "idcounts")
			.getNextMapId();
	}

	@Override
	public void setSpawnPos(BlockPos blockPos) {
		ChunkPos chunkPos = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
		super.setSpawnPos(blockPos);
		this.getChunkManager().removeTicket(ChunkTicketType.START, chunkPos, 11, Void.INSTANCE);
		this.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Void.INSTANCE);
	}

	public LongSet getForcedChunks() {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
		return (LongSet)(forcedChunkState != null ? LongSets.unmodifiable(forcedChunkState.getChunks()) : LongSets.EMPTY_SET);
	}

	public boolean setChunkForced(int i, int j, boolean bl) {
		ForcedChunkState forcedChunkState = this.getPersistentStateManager().getOrCreate(ForcedChunkState::new, "chunks");
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		boolean bl2;
		if (bl) {
			bl2 = forcedChunkState.getChunks().add(l);
			if (bl2) {
				this.getWorldChunk(i, j);
			}
		} else {
			bl2 = forcedChunkState.getChunks().remove(l);
		}

		forcedChunkState.setDirty(bl2);
		if (bl2) {
			this.getChunkManager().setChunkForced(chunkPos, bl);
		}

		return bl2;
	}
}
