package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.DebugRendererInfoManager;
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
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.Unit;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
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
	private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<>();
	private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
	private final Queue<Entity> entitiesToLoad = Queues.<Entity>newArrayDeque();
	private final List<ServerPlayerEntity> players = Lists.<ServerPlayerEntity>newArrayList();
	boolean ticking;
	private final MinecraftServer server;
	private final WorldSaveHandler worldSaveHandler;
	public boolean savingDisabled;
	private boolean allPlayersSleeping;
	private int idleTimeout;
	private final PortalForcer portalForcer;
	private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<>(
		this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::tickBlock
	);
	private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<>(
		this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this::tickFluid
	);
	private final Set<EntityNavigation> entityNavigations = Sets.<EntityNavigation>newHashSet();
	protected final RaidManager raidManager;
	private final ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions = new ObjectLinkedOpenHashSet<>();
	private boolean insideTick;
	@Nullable
	private final WanderingTraderManager wanderingTraderManager;

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
					() -> minecraftServer.getWorld(DimensionType.OVERWORLD).getPersistentStateManager()
				),
			profiler,
			false
		);
		this.worldSaveHandler = worldSaveHandler;
		this.server = minecraftServer;
		this.portalForcer = new PortalForcer(this);
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
		this.getWorldBorder().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
		this.raidManager = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.dimension));
		if (!minecraftServer.isSinglePlayer()) {
			this.getLevelProperties().setGameMode(minecraftServer.getDefaultGameMode());
		}

		this.wanderingTraderManager = this.dimension.getType() == DimensionType.OVERWORLD ? new WanderingTraderManager(this) : null;
	}

	@Override
	public Biome method_22387(int i, int j, int k) {
		return this.method_14178().getChunkGenerator().getBiomeSource().getBiome(i, j, k);
	}

	public void tick(BooleanSupplier booleanSupplier) {
		Profiler profiler = this.getProfiler();
		this.insideTick = true;
		profiler.push("world border");
		this.getWorldBorder().tick();
		profiler.swap("weather");
		boolean bl = this.isRaining();
		if (this.dimension.hasSkyLight()) {
			if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				int i = this.properties.getClearWeatherTime();
				int j = this.properties.getThunderTime();
				int k = this.properties.getRainTime();
				boolean bl2 = this.properties.isThundering();
				boolean bl3 = this.properties.isRaining();
				if (i > 0) {
					i--;
					j = bl2 ? 0 : 1;
					k = bl3 ? 0 : 1;
					bl2 = false;
					bl3 = false;
				} else {
					if (j > 0) {
						if (--j == 0) {
							bl2 = !bl2;
						}
					} else if (bl2) {
						j = this.random.nextInt(12000) + 3600;
					} else {
						j = this.random.nextInt(168000) + 12000;
					}

					if (k > 0) {
						if (--k == 0) {
							bl3 = !bl3;
						}
					} else if (bl3) {
						k = this.random.nextInt(12000) + 12000;
					} else {
						k = this.random.nextInt(168000) + 12000;
					}
				}

				this.properties.setThunderTime(j);
				this.properties.setRainTime(k);
				this.properties.setClearWeatherTime(i);
				this.properties.setThundering(bl2);
				this.properties.setRaining(bl3);
			}

			this.thunderGradientPrev = this.thunderGradient;
			if (this.properties.isThundering()) {
				this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
			} else {
				this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
			}

			this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
			this.rainGradientPrev = this.rainGradient;
			if (this.properties.isRaining()) {
				this.rainGradient = (float)((double)this.rainGradient + 0.01);
			} else {
				this.rainGradient = (float)((double)this.rainGradient - 0.01);
			}

			this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
		}

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

		if (this.getLevelProperties().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
			this.getLevelProperties().setDifficulty(Difficulty.HARD);
		}

		if (this.allPlayersSleeping
			&& this.players.stream().noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough())) {
			this.allPlayersSleeping = false;
			if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
				long l = this.properties.getTimeOfDay() + 24000L;
				this.setTimeOfDay(l - l % 24000L);
			}

			this.players.stream().filter(LivingEntity::isSleeping).forEach(serverPlayerEntity -> serverPlayerEntity.wakeUp(false, false));
			if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				this.resetWeather();
			}
		}

		this.calculateAmbientDarkness();
		this.tickTime();
		profiler.swap("chunkSource");
		this.method_14178().tick(booleanSupplier);
		profiler.swap("tickPending");
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.blockTickScheduler.tick();
			this.fluidTickScheduler.tick();
		}

		profiler.swap("raid");
		this.raidManager.tick();
		if (this.wanderingTraderManager != null) {
			this.wanderingTraderManager.tick();
		}

		profiler.swap("blockEvents");
		this.sendBlockActions();
		this.insideTick = false;
		profiler.swap("entities");
		boolean bl4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
		if (bl4) {
			this.resetIdleTimeout();
		}

		if (bl4 || this.idleTimeout++ < 300) {
			this.dimension.update();
			profiler.push("global");

			for (int j = 0; j < this.globalEntities.size(); j++) {
				Entity entity = (Entity)this.globalEntities.get(j);
				this.tickEntity(entityx -> {
					entityx.age++;
					entityx.tick();
				}, entity);
				if (entity.removed) {
					this.globalEntities.remove(j--);
				}
			}

			profiler.swap("regular");
			this.ticking = true;
			ObjectIterator<Entry<Entity>> objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
				Entity entity2 = (Entity)entry.getValue();
				Entity entity3 = entity2.getVehicle();
				if (!this.server.shouldSpawnAnimals() && (entity2 instanceof AnimalEntity || entity2 instanceof WaterCreatureEntity)) {
					entity2.remove();
				}

				if (!this.server.shouldSpawnNpcs() && entity2 instanceof Npc) {
					entity2.remove();
				}

				if (entity3 != null) {
					if (!entity3.removed && entity3.hasPassenger(entity2)) {
						continue;
					}

					entity2.stopRiding();
				}

				profiler.push("tick");
				if (!entity2.removed && !(entity2 instanceof EnderDragonPart)) {
					this.tickEntity(this::tickEntity, entity2);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity2.removed) {
					this.removeEntityFromChunk(entity2);
					objectIterator.remove();
					this.unloadEntity(entity2);
				}

				profiler.pop();
			}

			this.ticking = false;

			Entity entity;
			while ((entity = (Entity)this.entitiesToLoad.poll()) != null) {
				this.loadEntityUnchecked(entity);
			}

			profiler.pop();
			this.tickBlockEntities();
		}

		profiler.pop();
	}

	public void tickChunk(WorldChunk worldChunk, int i) {
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
				boolean bl2 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
				if (bl2) {
					SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this);
					skeletonHorseEntity.setTrapped(true);
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
				this.setBlockState(blockPos2, Blocks.ICE.getDefaultState());
			}

			if (bl && biome.canSetIce(this, blockPos)) {
				this.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
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

						FluidState fluidState = blockState.getFluidState();
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
		Box box = new Box(blockPos2, new BlockPos(blockPos2.getX(), this.getHeight(), blockPos2.getZ())).expand(3.0);
		List<LivingEntity> list = this.getEntities(
			LivingEntity.class, box, livingEntity -> livingEntity != null && livingEntity.isAlive() && this.isSkyVisible(livingEntity.getBlockPos())
		);
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).getBlockPos();
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

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				if (serverPlayerEntity.isSpectator()) {
					i++;
				} else if (serverPlayerEntity.isSleeping()) {
					j++;
				}
			}

			this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
		}
	}

	public ServerScoreboard method_14170() {
		return this.server.getScoreboard();
	}

	private void resetWeather() {
		this.properties.setRainTime(0);
		this.properties.setRaining(false);
		this.properties.setThunderTime(0);
		this.properties.setThundering(false);
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

	public void resetIdleTimeout() {
		this.idleTimeout = 0;
	}

	private void tickFluid(ScheduledTick<Fluid> scheduledTick) {
		FluidState fluidState = this.getFluidState(scheduledTick.pos);
		if (fluidState.getFluid() == scheduledTick.getObject()) {
			fluidState.onScheduledTick(this, scheduledTick.pos);
		}
	}

	private void tickBlock(ScheduledTick<Block> scheduledTick) {
		BlockState blockState = this.getBlockState(scheduledTick.pos);
		if (blockState.getBlock() == scheduledTick.getObject()) {
			blockState.scheduledTick(this, scheduledTick.pos, this.random);
		}
	}

	public void tickEntity(Entity entity) {
		if (entity instanceof PlayerEntity || this.method_14178().shouldTickEntity(entity)) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.updateNeeded) {
				entity.age++;
				this.getProfiler().push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString()));
				entity.tick();
				this.getProfiler().pop();
			}

			this.checkChunk(entity);
			if (entity.updateNeeded) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.method_18763(entity, entity2);
				}
			}
		}
	}

	public void method_18763(Entity entity, Entity entity2) {
		if (entity2.removed || entity2.getVehicle() != entity) {
			entity2.stopRiding();
		} else if (entity2 instanceof PlayerEntity || this.method_14178().shouldTickEntity(entity2)) {
			entity2.prevRenderX = entity2.x;
			entity2.prevRenderY = entity2.y;
			entity2.prevRenderZ = entity2.z;
			entity2.prevYaw = entity2.yaw;
			entity2.prevPitch = entity2.pitch;
			if (entity2.updateNeeded) {
				entity2.age++;
				entity2.tickRiding();
			}

			this.checkChunk(entity2);
			if (entity2.updateNeeded) {
				for (Entity entity3 : entity2.getPassengerList()) {
					this.method_18763(entity2, entity3);
				}
			}
		}
	}

	public void checkChunk(Entity entity) {
		this.getProfiler().push("chunkCheck");
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.y / 16.0);
		int k = MathHelper.floor(entity.z / 16.0);
		if (!entity.updateNeeded || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
			if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
				this.method_8497(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
			}

			if (!entity.teleportRequested() && !this.isChunkLoaded(i, k)) {
				entity.updateNeeded = false;
			} else {
				this.method_8497(i, k).addEntity(entity);
			}
		}

		this.getProfiler().pop();
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
		return !this.server.isSpawnProtected(this, blockPos, playerEntity) && this.getWorldBorder().contains(blockPos);
	}

	public void init(LevelInfo levelInfo) {
		if (!this.dimension.canPlayersSleep()) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up(this.method_14178().getChunkGenerator().getSpawnHeight()));
		} else if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up());
		} else {
			BiomeSource biomeSource = this.method_14178().getChunkGenerator().getBiomeSource();
			List<Biome> list = biomeSource.getSpawnBiomes();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = biomeSource.locateBiome(0, this.getSeaLevel(), 0, 256, list, random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.VALID_SPAWN.values()) {
				if (biomeSource.getTopMaterials().contains(block.getDefaultState())) {
					bl = true;
					break;
				}
			}

			this.properties.setSpawnPos(chunkPos.getCenterBlockPos().add(8, this.method_14178().getChunkGenerator().getSpawnHeight(), 8));
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
				this, (ChunkGenerator<? extends ChunkGeneratorConfig>)this.method_14178().getChunkGenerator(), this.random, blockPos, FeatureConfig.DEFAULT
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
		ServerChunkManager serverChunkManager = this.method_14178();
		if (!bl2) {
			if (progressListener != null) {
				progressListener.method_15412(new TranslatableText("menu.savingLevel"));
			}

			this.saveLevel();
			if (progressListener != null) {
				progressListener.method_15414(new TranslatableText("menu.savingChunks"));
			}

			serverChunkManager.save(bl);
		}
	}

	protected void saveLevel() throws SessionLockException {
		this.checkSessionLock();
		this.dimension.saveWorldData();
		this.method_14178().getPersistentStateManager().save();
	}

	public List<Entity> getEntities(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.<Entity>newArrayList();
		ServerChunkManager serverChunkManager = this.method_14178();

		for (Entity entity : this.entitiesById.values()) {
			if ((entityType == null || entity.getType() == entityType)
				&& serverChunkManager.isChunkLoaded(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4)
				&& predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public List<EnderDragonEntity> getAliveEnderDragons() {
		List<EnderDragonEntity> list = Lists.<EnderDragonEntity>newArrayList();

		for (Entity entity : this.entitiesById.values()) {
			if (entity instanceof EnderDragonEntity && entity.isAlive()) {
				list.add((EnderDragonEntity)entity);
			}
		}

		return list;
	}

	public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate) {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (predicate.test(serverPlayerEntity)) {
				list.add(serverPlayerEntity);
			}
		}

		return list;
	}

	@Nullable
	public ServerPlayerEntity getRandomAlivePlayer() {
		List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
		return list.isEmpty() ? null : (ServerPlayerEntity)list.get(this.random.nextInt(list.size()));
	}

	public Object2IntMap<EntityCategory> getMobCountsByCategory() {
		Object2IntMap<EntityCategory> object2IntMap = new Object2IntOpenHashMap<>();

		for (Entity entity : this.entitiesById.values()) {
			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				if (mobEntity.isPersistent() || mobEntity.cannotDespawn()) {
					continue;
				}
			}

			EntityCategory entityCategory = entity.getType().getCategory();
			if (entityCategory != EntityCategory.MISC && this.method_14178().method_20727(entity)) {
				object2IntMap.mergeInt(entityCategory, 1, Integer::sum);
			}
		}

		return object2IntMap;
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.addEntity(entity);
	}

	public boolean method_18768(Entity entity) {
		return this.addEntity(entity);
	}

	public void method_18769(Entity entity) {
		boolean bl = entity.teleporting;
		entity.teleporting = true;
		this.method_18768(entity);
		entity.teleporting = bl;
		this.checkChunk(entity);
	}

	public void method_18207(ServerPlayerEntity serverPlayerEntity) {
		this.addPlayer(serverPlayerEntity);
		this.checkChunk(serverPlayerEntity);
	}

	public void method_18211(ServerPlayerEntity serverPlayerEntity) {
		this.addPlayer(serverPlayerEntity);
		this.checkChunk(serverPlayerEntity);
	}

	public void method_18213(ServerPlayerEntity serverPlayerEntity) {
		this.addPlayer(serverPlayerEntity);
	}

	public void respawnPlayer(ServerPlayerEntity serverPlayerEntity) {
		this.addPlayer(serverPlayerEntity);
	}

	private void addPlayer(ServerPlayerEntity serverPlayerEntity) {
		Entity entity = (Entity)this.entitiesByUuid.get(serverPlayerEntity.getUuid());
		if (entity != null) {
			LOGGER.warn("Force-added player with duplicate UUID {}", serverPlayerEntity.getUuid().toString());
			entity.detach();
			this.removePlayer((ServerPlayerEntity)entity);
		}

		this.players.add(serverPlayerEntity);
		this.updatePlayersSleeping();
		Chunk chunk = this.getChunk(MathHelper.floor(serverPlayerEntity.x / 16.0), MathHelper.floor(serverPlayerEntity.z / 16.0), ChunkStatus.FULL, true);
		if (chunk instanceof WorldChunk) {
			chunk.addEntity(serverPlayerEntity);
		}

		this.loadEntityUnchecked(serverPlayerEntity);
	}

	private boolean addEntity(Entity entity) {
		if (entity.removed) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		} else if (this.checkUuid(entity)) {
			return false;
		} else {
			Chunk chunk = this.getChunk(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, entity.teleporting);
			if (!(chunk instanceof WorldChunk)) {
				return false;
			} else {
				chunk.addEntity(entity);
				this.loadEntityUnchecked(entity);
				return true;
			}
		}
	}

	public boolean loadEntity(Entity entity) {
		if (this.checkUuid(entity)) {
			return false;
		} else {
			this.loadEntityUnchecked(entity);
			return true;
		}
	}

	private boolean checkUuid(Entity entity) {
		Entity entity2 = (Entity)this.entitiesByUuid.get(entity.getUuid());
		if (entity2 == null) {
			return false;
		} else {
			LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.getType()), entity.getUuid().toString());
			return true;
		}
	}

	public void unloadEntities(WorldChunk worldChunk) {
		this.unloadedBlockEntities.addAll(worldChunk.getBlockEntities().values());
		TypeFilterableList[] var2 = worldChunk.getEntitySectionArray();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; var4++) {
			for (Entity entity : var2[var4]) {
				if (!(entity instanceof ServerPlayerEntity)) {
					if (this.ticking) {
						throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
					}

					this.entitiesById.remove(entity.getEntityId());
					this.unloadEntity(entity);
				}
			}
		}
	}

	public void unloadEntity(Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
				enderDragonPart.remove();
			}
		}

		this.entitiesByUuid.remove(entity.getUuid());
		this.method_14178().unloadEntity(entity);
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			this.players.remove(serverPlayerEntity);
		}

		this.method_14170().resetEntityScore(entity);
		if (entity instanceof MobEntity) {
			this.entityNavigations.remove(((MobEntity)entity).getNavigation());
		}
	}

	private void loadEntityUnchecked(Entity entity) {
		if (this.ticking) {
			this.entitiesToLoad.add(entity);
		} else {
			this.entitiesById.put(entity.getEntityId(), entity);
			if (entity instanceof EnderDragonEntity) {
				for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
					this.entitiesById.put(enderDragonPart.getEntityId(), enderDragonPart);
				}
			}

			this.entitiesByUuid.put(entity.getUuid(), entity);
			this.method_14178().loadEntity(entity);
			if (entity instanceof MobEntity) {
				this.entityNavigations.add(((MobEntity)entity).getNavigation());
			}
		}
	}

	public void removeEntity(Entity entity) {
		if (this.ticking) {
			throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
		} else {
			this.removeEntityFromChunk(entity);
			this.entitiesById.remove(entity.getEntityId());
			this.unloadEntity(entity);
		}
	}

	private void removeEntityFromChunk(Entity entity) {
		Chunk chunk = this.getChunk(entity.chunkX, entity.chunkZ, ChunkStatus.FULL, false);
		if (chunk instanceof WorldChunk) {
			((WorldChunk)chunk).remove(entity);
		}
	}

	public void removePlayer(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.remove();
		this.removeEntity(serverPlayerEntity);
		this.updatePlayersSleeping();
	}

	public void addLightning(LightningEntity lightningEntity) {
		this.globalEntities.add(lightningEntity);
		this.server
			.getPlayerManager()
			.sendToAround(
				null, lightningEntity.x, lightningEntity.y, lightningEntity.z, 512.0, this.dimension.getType(), new EntitySpawnGlobalS2CPacket(lightningEntity)
			);
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
	public void playLevelEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
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
		this.method_14178().markForUpdate(blockPos);
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
		VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos);
		if (VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
			for (EntityNavigation entityNavigation : this.entityNavigations) {
				if (!entityNavigation.shouldRecalculatePath()) {
					entityNavigation.method_18053(blockPos);
				}
			}
		}
	}

	@Override
	public void sendEntityStatus(Entity entity, byte b) {
		this.method_14178().sendToNearbyPlayers(entity, new EntityStatusS2CPacket(entity, b));
	}

	public ServerChunkManager method_14178() {
		return (ServerChunkManager)super.getChunkManager();
	}

	@Override
	public Explosion createExplosion(
		@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType
	) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, destructionType);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (destructionType == Explosion.DestructionType.NONE) {
			explosion.clearAffectedBlocks();
		}

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				serverPlayerEntity.networkHandler
					.sendPacket(new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(serverPlayerEntity)));
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

	public ServerTickScheduler<Block> method_14196() {
		return this.blockTickScheduler;
	}

	public ServerTickScheduler<Fluid> method_14179() {
		return this.fluidTickScheduler;
	}

	@Nonnull
	@Override
	public MinecraftServer getServer() {
		return this.server;
	}

	public PortalForcer getPortalForcer() {
		return this.portalForcer;
	}

	public StructureManager getStructureManager() {
		return this.worldSaveHandler.getStructureManager();
	}

	public <T extends ParticleEffect> int spawnParticles(T particleEffect, double d, double e, double f, int i, double g, double h, double j, double k) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particleEffect, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		int l = 0;

		for (int m = 0; m < this.players.size(); m++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(m);
			if (this.sendToPlayerIfNearby(serverPlayerEntity, false, d, e, f, particleS2CPacket)) {
				l++;
			}
		}

		return l;
	}

	public <T extends ParticleEffect> boolean spawnParticles(
		ServerPlayerEntity serverPlayerEntity, T particleEffect, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		Packet<?> packet = new ParticleS2CPacket(particleEffect, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.sendToPlayerIfNearby(serverPlayerEntity, bl, d, e, f, packet);
	}

	private boolean sendToPlayerIfNearby(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		if (serverPlayerEntity.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = serverPlayerEntity.getBlockPos();
			if (blockPos.isWithinDistance(new Vec3d(d, e, f), bl ? 512.0 : 32.0)) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
				return true;
			} else {
				return false;
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
	public BlockPos locateStructure(String string, BlockPos blockPos, int i, boolean bl) {
		return this.method_14178().getChunkGenerator().locateStructure(this, string, blockPos, i, bl);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.server.getRecipeManager();
	}

	@Override
	public RegistryTagManager getTagManager() {
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
		return this.method_14178().getPersistentStateManager();
	}

	@Nullable
	@Override
	public MapState getMapState(String string) {
		return this.getServer().getWorld(DimensionType.OVERWORLD).getPersistentStateManager().get(() -> new MapState(string), string);
	}

	@Override
	public void putMapState(MapState mapState) {
		this.getServer().getWorld(DimensionType.OVERWORLD).getPersistentStateManager().set(mapState);
	}

	@Override
	public int getNextMapId() {
		return this.getServer()
			.getWorld(DimensionType.OVERWORLD)
			.getPersistentStateManager()
			.<IdCountsState>getOrCreate(IdCountsState::new, "idcounts")
			.getNextMapId();
	}

	@Override
	public void setSpawnPos(BlockPos blockPos) {
		ChunkPos chunkPos = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
		super.setSpawnPos(blockPos);
		this.method_14178().removeTicket(ChunkTicketType.START, chunkPos, 11, Unit.INSTANCE);
		this.method_14178().addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);
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
				this.method_8497(i, j);
			}
		} else {
			bl2 = forcedChunkState.getChunks().remove(l);
		}

		forcedChunkState.setDirty(bl2);
		if (bl2) {
			this.method_14178().setChunkForced(chunkPos, bl);
		}

		return bl2;
	}

	@Override
	public List<ServerPlayerEntity> getPlayers() {
		return this.players;
	}

	@Override
	public void onBlockChanged(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		Optional<PointOfInterestType> optional = PointOfInterestType.from(blockState);
		Optional<PointOfInterestType> optional2 = PointOfInterestType.from(blockState2);
		if (!Objects.equals(optional, optional2)) {
			BlockPos blockPos2 = blockPos.toImmutable();
			optional.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
					this.getPointOfInterestStorage().remove(blockPos2);
					DebugRendererInfoManager.method_19777(this, blockPos2);
				}));
			optional2.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
					this.getPointOfInterestStorage().add(blockPos2, pointOfInterestType);
					DebugRendererInfoManager.method_19776(this, blockPos2);
				}));
		}
	}

	public PointOfInterestStorage getPointOfInterestStorage() {
		return this.method_14178().getPointOfInterestStorage();
	}

	public boolean isNearOccupiedPointOfInterest(BlockPos blockPos) {
		return this.isNearOccupiedPointOfInterest(blockPos, 1);
	}

	public boolean isNearOccupiedPointOfInterest(ChunkSectionPos chunkSectionPos) {
		return this.isNearOccupiedPointOfInterest(chunkSectionPos.getCenterPos());
	}

	public boolean isNearOccupiedPointOfInterest(BlockPos blockPos, int i) {
		return i > 6 ? false : this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPos)) <= i;
	}

	public int getOccupiedPointOfInterestDistance(ChunkSectionPos chunkSectionPos) {
		return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(chunkSectionPos);
	}

	public RaidManager getRaidManager() {
		return this.raidManager;
	}

	@Nullable
	public Raid getRaidAt(BlockPos blockPos) {
		return this.raidManager.getRaidAt(blockPos, 9216);
	}

	public boolean hasRaidAt(BlockPos blockPos) {
		return this.getRaidAt(blockPos) != null;
	}

	public void handleInteraction(EntityInteraction entityInteraction, Entity entity, InteractionObserver interactionObserver) {
		interactionObserver.onInteractionWith(entityInteraction, entity);
	}

	public void method_21625(Path path) throws IOException {
		ThreadedAnvilChunkStorage threadedAnvilChunkStorage = this.method_14178().threadedAnvilChunkStorage;
		Writer writer = Files.newBufferedWriter(path.resolve("stats.txt"));
		Throwable path2 = null;

		try {
			writer.write(String.format("spawning_chunks: %d\n", threadedAnvilChunkStorage.getTicketManager().getLevelCount()));

			for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<EntityCategory> entry : this.getMobCountsByCategory().object2IntEntrySet()) {
				writer.write(String.format("spawn_count.%s: %d\n", ((EntityCategory)entry.getKey()).getName(), entry.getIntValue()));
			}

			writer.write(String.format("entities: %d\n", this.entitiesById.size()));
			writer.write(String.format("block_entities: %d\n", this.blockEntities.size()));
			writer.write(String.format("block_ticks: %d\n", this.method_14196().method_20825()));
			writer.write(String.format("fluid_ticks: %d\n", this.method_14179().method_20825()));
			writer.write("distance_manager: " + threadedAnvilChunkStorage.getTicketManager().method_21683() + "\n");
			writer.write(String.format("pending_tasks: %d\n", this.method_14178().method_21694()));
		} catch (Throwable var164) {
			path2 = var164;
			throw var164;
		} finally {
			if (writer != null) {
				if (path2 != null) {
					try {
						writer.close();
					} catch (Throwable var153) {
						path2.addSuppressed(var153);
					}
				} else {
					writer.close();
				}
			}
		}

		CrashReport crashReport = new CrashReport("Level dump", new Exception("dummy"));
		this.addDetailsToCrashReport(crashReport);
		Writer writer2 = Files.newBufferedWriter(path.resolve("example_crash.txt"));
		Throwable var169 = null;

		try {
			writer2.write(crashReport.asString());
		} catch (Throwable var158) {
			var169 = var158;
			throw var158;
		} finally {
			if (writer2 != null) {
				if (var169 != null) {
					try {
						writer2.close();
					} catch (Throwable var152) {
						var169.addSuppressed(var152);
					}
				} else {
					writer2.close();
				}
			}
		}

		Path path2x = path.resolve("chunks.csv");
		Writer writer3 = Files.newBufferedWriter(path2x);
		Throwable var172 = null;

		try {
			threadedAnvilChunkStorage.exportChunks(writer3);
		} catch (Throwable var157) {
			var172 = var157;
			throw var157;
		} finally {
			if (writer3 != null) {
				if (var172 != null) {
					try {
						writer3.close();
					} catch (Throwable var151) {
						var172.addSuppressed(var151);
					}
				} else {
					writer3.close();
				}
			}
		}

		Path path3 = path.resolve("entities.csv");
		Writer writer4 = Files.newBufferedWriter(path3);
		Throwable path5 = null;

		try {
			exportEntities(writer4, this.entitiesById.values());
		} catch (Throwable var156) {
			path5 = var156;
			throw var156;
		} finally {
			if (writer4 != null) {
				if (path5 != null) {
					try {
						writer4.close();
					} catch (Throwable var150) {
						path5.addSuppressed(var150);
					}
				} else {
					writer4.close();
				}
			}
		}

		Path path4 = path.resolve("global_entities.csv");
		Writer writer5 = Files.newBufferedWriter(path4);
		Throwable writer6 = null;

		try {
			exportEntities(writer5, this.globalEntities);
		} catch (Throwable var155) {
			writer6 = var155;
			throw var155;
		} finally {
			if (writer5 != null) {
				if (writer6 != null) {
					try {
						writer5.close();
					} catch (Throwable var149) {
						writer6.addSuppressed(var149);
					}
				} else {
					writer5.close();
				}
			}
		}

		Path path5x = path.resolve("block_entities.csv");
		Writer writer6x = Files.newBufferedWriter(path5x);
		Throwable var9 = null;

		try {
			this.exportBlockEntities(writer6x);
		} catch (Throwable var154) {
			var9 = var154;
			throw var154;
		} finally {
			if (writer6x != null) {
				if (var9 != null) {
					try {
						writer6x.close();
					} catch (Throwable var148) {
						var9.addSuppressed(var148);
					}
				} else {
					writer6x.close();
				}
			}
		}
	}

	private static void exportEntities(Writer writer, Iterable<Entity> iterable) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader()
			.addColumn("x")
			.addColumn("y")
			.addColumn("z")
			.addColumn("uuid")
			.addColumn("type")
			.addColumn("alive")
			.addColumn("display_name")
			.addColumn("custom_name")
			.startBody(writer);

		for (Entity entity : iterable) {
			Text text = entity.getCustomName();
			Text text2 = entity.getDisplayName();
			csvWriter.printRow(
				entity.x,
				entity.y,
				entity.z,
				entity.getUuid(),
				Registry.ENTITY_TYPE.getId(entity.getType()),
				entity.isAlive(),
				text2.getString(),
				text != null ? text.getString() : null
			);
		}
	}

	private void exportBlockEntities(Writer writer) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("type").startBody(writer);

		for (BlockEntity blockEntity : this.blockEntities) {
			BlockPos blockPos = blockEntity.getPos();
			csvWriter.printRow(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()));
		}
	}
}
