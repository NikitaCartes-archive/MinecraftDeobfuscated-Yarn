/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
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
import java.util.stream.Collectors;
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
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
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
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
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
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockBox;
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
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerWorld
extends World {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Entity> globalEntities = Lists.newArrayList();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<Entity>();
    private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
    private final Queue<Entity> entitiesToLoad = Queues.newArrayDeque();
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    boolean ticking;
    private final MinecraftServer server;
    private final WorldSaveHandler worldSaveHandler;
    public boolean savingDisabled;
    private boolean allPlayersSleeping;
    private int idleTimeout;
    private final PortalForcer portalForcer;
    private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<Block>(this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::tickBlock);
    private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<Fluid>(this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this::tickFluid);
    private final Set<EntityNavigation> entityNavigations = Sets.newHashSet();
    protected final RaidManager raidManager;
    private final ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions = new ObjectLinkedOpenHashSet();
    private boolean insideTick;
    @Nullable
    private final WanderingTraderManager wanderingTraderManager;

    public ServerWorld(MinecraftServer server, Executor workerExecutor, WorldSaveHandler worldSaveHandler, LevelProperties properties, DimensionType dimensionType, Profiler profiler, WorldGenerationProgressListener worldGenerationProgressListener) {
        super(properties, dimensionType, (world, dimension) -> new ServerChunkManager((ServerWorld)world, worldSaveHandler.getWorldDir(), worldSaveHandler.getDataFixer(), worldSaveHandler.getStructureManager(), workerExecutor, dimension.createChunkGenerator(), server.getPlayerManager().getViewDistance(), worldGenerationProgressListener, () -> server.getWorld(DimensionType.OVERWORLD).getPersistentStateManager()), profiler, false);
        this.worldSaveHandler = worldSaveHandler;
        this.server = server;
        this.portalForcer = new PortalForcer(this);
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
        this.getWorldBorder().setMaxWorldBorderRadius(server.getMaxWorldBorderRadius());
        this.raidManager = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.dimension));
        if (!server.isSinglePlayer()) {
            this.getLevelProperties().setGameMode(server.getDefaultGameMode());
        }
        this.wanderingTraderManager = this.dimension.getType() == DimensionType.OVERWORLD ? new WanderingTraderManager(this) : null;
    }

    @Override
    public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.getChunkManager().getChunkGenerator().getBiomeSource().getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public void tick(BooleanSupplier booleanSupplier) {
        boolean bl4;
        int j;
        Profiler profiler = this.getProfiler();
        this.insideTick = true;
        profiler.push("world border");
        this.getWorldBorder().tick();
        profiler.swap("weather");
        boolean bl = this.isRaining();
        if (this.dimension.hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                int i = this.properties.getClearWeatherTime();
                j = this.properties.getThunderTime();
                int k = this.properties.getRainTime();
                boolean bl2 = this.properties.isThundering();
                boolean bl3 = this.properties.isRaining();
                if (i > 0) {
                    --i;
                    j = bl2 ? 0 : 1;
                    k = bl3 ? 0 : 1;
                    bl2 = false;
                    bl3 = false;
                } else {
                    if (j > 0) {
                        if (--j == 0) {
                            bl2 = !bl2;
                        }
                    } else {
                        j = bl2 ? this.random.nextInt(12000) + 3600 : this.random.nextInt(168000) + 12000;
                    }
                    if (k > 0) {
                        if (--k == 0) {
                            bl3 = !bl3;
                        }
                    } else {
                        k = bl3 ? this.random.nextInt(12000) + 12000 : this.random.nextInt(168000) + 12000;
                    }
                }
                this.properties.setThunderTime(j);
                this.properties.setRainTime(k);
                this.properties.setClearWeatherTime(i);
                this.properties.setThundering(bl2);
                this.properties.setRaining(bl3);
            }
            this.thunderGradientPrev = this.thunderGradient;
            this.thunderGradient = this.properties.isThundering() ? (float)((double)this.thunderGradient + 0.01) : (float)((double)this.thunderGradient - 0.01);
            this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0f, 1.0f);
            this.rainGradientPrev = this.rainGradient;
            this.rainGradient = this.properties.isRaining() ? (float)((double)this.rainGradient + 0.01) : (float)((double)this.rainGradient - 0.01);
            this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0f, 1.0f);
        }
        if (this.rainGradientPrev != this.rainGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.getType());
        }
        if (this.thunderGradientPrev != this.thunderGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.getType());
        }
        if (bl != this.isRaining()) {
            if (bl) {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0f));
            } else {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0f));
            }
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
        }
        if (this.getLevelProperties().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
            this.getLevelProperties().setDifficulty(Difficulty.HARD);
        }
        if (this.allPlayersSleeping && this.players.stream().noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough())) {
            this.allPlayersSleeping = false;
            if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                long l = this.properties.getTimeOfDay() + 24000L;
                this.setTimeOfDay(l - l % 24000L);
            }
            this.method_23660();
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                this.resetWeather();
            }
        }
        this.calculateAmbientDarkness();
        this.tickTime();
        profiler.swap("chunkSource");
        this.getChunkManager().tick(booleanSupplier);
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
        boolean bl2 = bl4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if (bl4) {
            this.resetIdleTimeout();
        }
        if (bl4 || this.idleTimeout++ < 300) {
            Entity entity2;
            this.dimension.update();
            profiler.push("global");
            for (j = 0; j < this.globalEntities.size(); ++j) {
                Entity entity3 = this.globalEntities.get(j);
                this.tickEntity(entity -> {
                    ++entity.age;
                    entity.tick();
                }, entity3);
                if (!entity3.removed) continue;
                this.globalEntities.remove(j--);
            }
            profiler.swap("regular");
            this.ticking = true;
            Iterator objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
                Entity entity22 = (Entity)entry.getValue();
                Entity entity3 = entity22.getVehicle();
                if (!this.server.shouldSpawnAnimals() && (entity22 instanceof AnimalEntity || entity22 instanceof WaterCreatureEntity)) {
                    entity22.remove();
                }
                if (!this.server.shouldSpawnNpcs() && entity22 instanceof Npc) {
                    entity22.remove();
                }
                profiler.push("checkDespawn");
                if (!entity22.removed) {
                    entity22.checkDespawn();
                }
                profiler.pop();
                if (entity3 != null) {
                    if (!entity3.removed && entity3.hasPassenger(entity22)) continue;
                    entity22.stopRiding();
                }
                profiler.push("tick");
                if (!entity22.removed && !(entity22 instanceof EnderDragonPart)) {
                    this.tickEntity(this::tickEntity, entity22);
                }
                profiler.pop();
                profiler.push("remove");
                if (entity22.removed) {
                    this.removeEntityFromChunk(entity22);
                    objectIterator.remove();
                    this.unloadEntity(entity22);
                }
                profiler.pop();
            }
            this.ticking = false;
            while ((entity2 = this.entitiesToLoad.poll()) != null) {
                this.loadEntityUnchecked(entity2);
            }
            profiler.pop();
            this.tickBlockEntities();
        }
        profiler.pop();
    }

    private void method_23660() {
        this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach(serverPlayerEntity -> serverPlayerEntity.wakeUp(false, false));
    }

    public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
        BlockPos blockPos;
        ChunkPos chunkPos = chunk.getPos();
        boolean bl = this.isRaining();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        Profiler profiler = this.getProfiler();
        profiler.push("thunder");
        if (bl && this.isThundering() && this.random.nextInt(100000) == 0 && this.hasRain(blockPos = this.method_18210(this.getRandomPosInChunk(i, 0, j, 15)))) {
            boolean bl2;
            LocalDifficulty localDifficulty = this.getLocalDifficulty(blockPos);
            boolean bl3 = bl2 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
            if (bl2) {
                SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this);
                skeletonHorseEntity.setTrapped(true);
                skeletonHorseEntity.setBreedingAge(0);
                skeletonHorseEntity.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                this.spawnEntity(skeletonHorseEntity);
            }
            this.addLightning(new LightningEntity(this, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, bl2));
        }
        profiler.swap("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, this.getRandomPosInChunk(i, 0, j, 15));
            BlockPos blockPos2 = blockPos.down();
            Biome biome = this.getBiome(blockPos);
            if (biome.canSetSnow(this, blockPos2)) {
                this.setBlockState(blockPos2, Blocks.ICE.getDefaultState());
            }
            if (bl && biome.canSetIce(this, blockPos)) {
                this.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
            }
            if (bl && this.getBiome(blockPos2).getPrecipitation() == Biome.Precipitation.RAIN) {
                this.getBlockState(blockPos2).getBlock().rainTick(this, blockPos2);
            }
        }
        profiler.swap("tickBlocks");
        if (randomTickSpeed > 0) {
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                if (chunkSection == WorldChunk.EMPTY_SECTION || !chunkSection.hasRandomTicks()) continue;
                int k = chunkSection.getYOffset();
                for (int l = 0; l < randomTickSpeed; ++l) {
                    FluidState fluidState;
                    BlockPos blockPos3 = this.getRandomPosInChunk(i, k, j, 15);
                    profiler.push("randomTick");
                    BlockState blockState = chunkSection.getBlockState(blockPos3.getX() - i, blockPos3.getY() - k, blockPos3.getZ() - j);
                    if (blockState.hasRandomTicks()) {
                        blockState.randomTick(this, blockPos3, this.random);
                    }
                    if ((fluidState = blockState.getFluidState()).hasRandomTicks()) {
                        fluidState.onRandomTick(this, blockPos3, this.random);
                    }
                    profiler.pop();
                }
            }
        }
        profiler.pop();
    }

    protected BlockPos method_18210(BlockPos pos) {
        BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
        Box box = new Box(blockPos, new BlockPos(blockPos.getX(), this.getHeight(), blockPos.getZ())).expand(3.0);
        List<LivingEntity> list = this.getEntities(LivingEntity.class, box, (? super T livingEntity) -> livingEntity != null && livingEntity.isAlive() && this.isSkyVisible(livingEntity.getBlockPos()));
        if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size())).getBlockPos();
        }
        if (blockPos.getY() == -1) {
            blockPos = blockPos.up(2);
        }
        return blockPos;
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
                    ++i;
                    continue;
                }
                if (!serverPlayerEntity.isSleeping()) continue;
                ++j;
            }
            this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
        }
    }

    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }

    private void resetWeather() {
        this.properties.setRainTime(0);
        this.properties.setRaining(false);
        this.properties.setThunderTime(0);
        this.properties.setThundering(false);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
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
            if (++k != 10000) continue;
        }
        this.properties.setSpawnX(i);
        this.properties.setSpawnZ(j);
    }

    public void resetIdleTimeout() {
        this.idleTimeout = 0;
    }

    private void tickFluid(ScheduledTick<Fluid> tick) {
        FluidState fluidState = this.getFluidState(tick.pos);
        if (fluidState.getFluid() == tick.getObject()) {
            fluidState.onScheduledTick(this, tick.pos);
        }
    }

    private void tickBlock(ScheduledTick<Block> tick) {
        BlockState blockState = this.getBlockState(tick.pos);
        if (blockState.getBlock() == tick.getObject()) {
            blockState.scheduledTick(this, tick.pos, this.random);
        }
    }

    public void tickEntity(Entity entity) {
        if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
            return;
        }
        entity.resetPosition(entity.getX(), entity.getY(), entity.getZ());
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
        if (entity.updateNeeded) {
            ++entity.age;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString());
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

    public void method_18763(Entity entity, Entity entity2) {
        if (entity2.removed || entity2.getVehicle() != entity) {
            entity2.stopRiding();
            return;
        }
        if (!(entity2 instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity2)) {
            return;
        }
        entity2.resetPosition(entity2.getX(), entity2.getY(), entity2.getZ());
        entity2.prevYaw = entity2.yaw;
        entity2.prevPitch = entity2.pitch;
        if (entity2.updateNeeded) {
            ++entity2.age;
            entity2.tickRiding();
        }
        this.checkChunk(entity2);
        if (entity2.updateNeeded) {
            for (Entity entity3 : entity2.getPassengerList()) {
                this.method_18763(entity2, entity3);
            }
        }
    }

    public void checkChunk(Entity entity) {
        this.getProfiler().push("chunkCheck");
        int i = MathHelper.floor(entity.getX() / 16.0);
        int j = MathHelper.floor(entity.getY() / 16.0);
        int k = MathHelper.floor(entity.getZ() / 16.0);
        if (!entity.updateNeeded || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
            if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
                this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
            }
            if (entity.teleportRequested() || this.isChunkLoaded(i, k)) {
                this.getChunk(i, k).addEntity(entity);
            } else {
                entity.updateNeeded = false;
            }
        }
        this.getProfiler().pop();
    }

    @Override
    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        return !this.server.isSpawnProtected(this, pos, player) && this.getWorldBorder().contains(pos);
    }

    public void init(LevelInfo levelInfo) {
        ChunkPos chunkPos;
        if (!this.dimension.canPlayersSleep()) {
            this.properties.setSpawnPos(BlockPos.ORIGIN.up(this.getChunkManager().getChunkGenerator().getSpawnHeight()));
            return;
        }
        if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.properties.setSpawnPos(BlockPos.ORIGIN.up());
            return;
        }
        BiomeSource biomeSource = this.getChunkManager().getChunkGenerator().getBiomeSource();
        List<Biome> list = biomeSource.getSpawnBiomes();
        Random random = new Random(this.getSeed());
        BlockPos blockPos = biomeSource.locateBiome(0, this.getSeaLevel(), 0, 256, list, random);
        ChunkPos chunkPos2 = chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
        if (blockPos == null) {
            LOGGER.warn("Unable to find spawn biome");
        }
        boolean bl = false;
        for (Block block : BlockTags.VALID_SPAWN.values()) {
            if (!biomeSource.getTopMaterials().contains(block.getDefaultState())) continue;
            bl = true;
            break;
        }
        this.properties.setSpawnPos(chunkPos.getCenterBlockPos().add(8, this.getChunkManager().getChunkGenerator().getSpawnHeight(), 8));
        int i = 0;
        int j = 0;
        int k = 0;
        int l = -1;
        int m = 32;
        for (int n = 0; n < 1024; ++n) {
            BlockPos blockPos2;
            if (i > -16 && i <= 16 && j > -16 && j <= 16 && (blockPos2 = this.dimension.getSpawningBlockInChunk(new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl)) != null) {
                this.properties.setSpawnPos(blockPos2);
                break;
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

    protected void placeBonusChest() {
        ConfiguredFeature<DefaultFeatureConfig, ?> configuredFeature = Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT);
        configuredFeature.generate(this, this.getChunkManager().getChunkGenerator(), this.random, new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ()));
    }

    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return this.dimension.getForcedSpawnPoint();
    }

    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean bl) throws SessionLockException {
        ServerChunkManager serverChunkManager = this.getChunkManager();
        if (bl) {
            return;
        }
        if (progressListener != null) {
            progressListener.method_15412(new TranslatableText("menu.savingLevel", new Object[0]));
        }
        this.saveLevel();
        if (progressListener != null) {
            progressListener.method_15414(new TranslatableText("menu.savingChunks", new Object[0]));
        }
        serverChunkManager.save(flush);
    }

    protected void saveLevel() throws SessionLockException {
        this.checkSessionLock();
        this.dimension.saveWorldData();
        this.getChunkManager().getPersistentStateManager().save();
    }

    public List<Entity> getEntities(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
        ArrayList<Entity> list = Lists.newArrayList();
        ServerChunkManager serverChunkManager = this.getChunkManager();
        for (Entity entity : this.entitiesById.values()) {
            if (entityType != null && entity.getType() != entityType || !serverChunkManager.isChunkLoaded(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4) || !predicate.test(entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public List<EnderDragonEntity> getAliveEnderDragons() {
        ArrayList<EnderDragonEntity> list = Lists.newArrayList();
        for (Entity entity : this.entitiesById.values()) {
            if (!(entity instanceof EnderDragonEntity) || !entity.isAlive()) continue;
            list.add((EnderDragonEntity)entity);
        }
        return list;
    }

    public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate) {
        ArrayList<ServerPlayerEntity> list = Lists.newArrayList();
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!predicate.test(serverPlayerEntity)) continue;
            list.add(serverPlayerEntity);
        }
        return list;
    }

    @Nullable
    public ServerPlayerEntity getRandomAlivePlayer() {
        List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(this.random.nextInt(list.size()));
    }

    public Object2IntMap<EntityCategory> getMobCountsByCategory() {
        Object2IntOpenHashMap<EntityCategory> object2IntMap = new Object2IntOpenHashMap<EntityCategory>();
        for (Entity entity : this.entitiesById.values()) {
            EntityCategory entityCategory;
            MobEntity mobEntity;
            if (entity instanceof MobEntity && ((mobEntity = (MobEntity)entity).isPersistent() || mobEntity.cannotDespawn()) || (entityCategory = entity.getType().getCategory()) == EntityCategory.MISC || !this.getChunkManager().method_20727(entity)) continue;
            object2IntMap.mergeInt(entityCategory, 1, Integer::sum);
        }
        return object2IntMap;
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return this.addEntity(entity);
    }

    public boolean tryLoadEntity(Entity entity) {
        return this.addEntity(entity);
    }

    public void onDimensionChanged(Entity entity) {
        boolean bl = entity.teleporting;
        entity.teleporting = true;
        this.tryLoadEntity(entity);
        entity.teleporting = bl;
        this.checkChunk(entity);
    }

    public void onPlayerTeleport(ServerPlayerEntity player) {
        this.addPlayer(player);
        this.checkChunk(player);
    }

    public void onPlayerChangeDimension(ServerPlayerEntity player) {
        this.addPlayer(player);
        this.checkChunk(player);
    }

    public void onPlayerConnected(ServerPlayerEntity player) {
        this.addPlayer(player);
    }

    public void onPlayerRespawned(ServerPlayerEntity player) {
        this.addPlayer(player);
    }

    private void addPlayer(ServerPlayerEntity player) {
        Entity entity = this.entitiesByUuid.get(player.getUuid());
        if (entity != null) {
            LOGGER.warn("Force-added player with duplicate UUID {}", (Object)player.getUuid().toString());
            entity.detach();
            this.removePlayer((ServerPlayerEntity)entity);
        }
        this.players.add(player);
        this.updatePlayersSleeping();
        Chunk chunk = this.getChunk(MathHelper.floor(player.getX() / 16.0), MathHelper.floor(player.getZ() / 16.0), ChunkStatus.FULL, true);
        if (chunk instanceof WorldChunk) {
            chunk.addEntity(player);
        }
        this.loadEntityUnchecked(player);
    }

    private boolean addEntity(Entity entity) {
        if (entity.removed) {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getId(entity.getType()));
            return false;
        }
        if (this.checkUuid(entity)) {
            return false;
        }
        Chunk chunk = this.getChunk(MathHelper.floor(entity.getX() / 16.0), MathHelper.floor(entity.getZ() / 16.0), ChunkStatus.FULL, entity.teleporting);
        if (!(chunk instanceof WorldChunk)) {
            return false;
        }
        chunk.addEntity(entity);
        this.loadEntityUnchecked(entity);
        return true;
    }

    public boolean loadEntity(Entity entity) {
        if (this.checkUuid(entity)) {
            return false;
        }
        this.loadEntityUnchecked(entity);
        return true;
    }

    private boolean checkUuid(Entity entity) {
        Entity entity2 = this.entitiesByUuid.get(entity.getUuid());
        if (entity2 == null) {
            return false;
        }
        LOGGER.warn("Keeping entity {} that already exists with UUID {}", (Object)EntityType.getId(entity2.getType()), (Object)entity.getUuid().toString());
        return true;
    }

    public void unloadEntities(WorldChunk chunk) {
        this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
        for (TypeFilterableList<Entity> typeFilterableList : chunk.getEntitySectionArray()) {
            for (Entity entity : typeFilterableList) {
                if (entity instanceof ServerPlayerEntity) continue;
                if (this.ticking) {
                    throw Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
                }
                this.entitiesById.remove(entity.getEntityId());
                this.unloadEntity(entity);
            }
        }
    }

    public void unloadEntity(Entity entity) {
        if (entity instanceof EnderDragonEntity) {
            for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
                enderDragonPart.remove();
            }
        }
        this.entitiesByUuid.remove(entity.getUuid());
        this.getChunkManager().unloadEntity(entity);
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.players.remove(serverPlayerEntity);
        }
        this.getScoreboard().resetEntityScore(entity);
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
                for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
                    this.entitiesById.put(enderDragonPart.getEntityId(), (Entity)enderDragonPart);
                }
            }
            this.entitiesByUuid.put(entity.getUuid(), entity);
            this.getChunkManager().loadEntity(entity);
            if (entity instanceof MobEntity) {
                this.entityNavigations.add(((MobEntity)entity).getNavigation());
            }
        }
    }

    public void removeEntity(Entity entity) {
        if (this.ticking) {
            throw Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
        }
        this.removeEntityFromChunk(entity);
        this.entitiesById.remove(entity.getEntityId());
        this.unloadEntity(entity);
    }

    private void removeEntityFromChunk(Entity entity) {
        Chunk chunk = this.getChunk(entity.chunkX, entity.chunkZ, ChunkStatus.FULL, false);
        if (chunk instanceof WorldChunk) {
            ((WorldChunk)chunk).remove(entity);
        }
    }

    public void removePlayer(ServerPlayerEntity player) {
        player.remove();
        this.removeEntity(player);
        this.updatePlayersSleeping();
    }

    public void addLightning(LightningEntity lightningEntity) {
        this.globalEntities.add(lightningEntity);
        this.server.getPlayerManager().sendToAround(null, lightningEntity.getX(), lightningEntity.getY(), lightningEntity.getZ(), 512.0, this.dimension.getType(), new EntitySpawnGlobalS2CPacket(lightningEntity));
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
            double f;
            double e;
            double d;
            if (serverPlayerEntity == null || serverPlayerEntity.world != this || serverPlayerEntity.getEntityId() == entityId || !((d = (double)pos.getX() - serverPlayerEntity.getX()) * d + (e = (double)pos.getY() - serverPlayerEntity.getY()) * e + (f = (double)pos.getZ() - serverPlayerEntity.getZ()) * f < 1024.0)) continue;
            serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
        }
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.server.getPlayerManager().sendToAround(player, x, y, z, volume > 1.0f ? (double)(16.0f * volume) : 16.0, this.dimension.getType(), new PlaySoundS2CPacket(sound, category, x, y, z, volume, pitch));
    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        this.server.getPlayerManager().sendToAround(playerEntity, entity.getX(), entity.getY(), entity.getZ(), volume > 1.0f ? (double)(16.0f * volume) : 16.0, this.dimension.getType(), new PlaySoundFromEntityS2CPacket(soundEvent, soundCategory, entity, volume, pitch));
    }

    @Override
    public void playGlobalEvent(int type, BlockPos pos, int data) {
        this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(type, pos, data, true));
    }

    @Override
    public void playLevelEvent(@Nullable PlayerEntity player, int eventId, BlockPos blockPos, int data) {
        this.server.getPlayerManager().sendToAround(player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 64.0, this.dimension.getType(), new WorldEventS2CPacket(eventId, blockPos, data, false));
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.getChunkManager().markForUpdate(pos);
        VoxelShape voxelShape = oldState.getCollisionShape(this, pos);
        VoxelShape voxelShape2 = newState.getCollisionShape(this, pos);
        if (!VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
            return;
        }
        for (EntityNavigation entityNavigation : this.entityNavigations) {
            if (entityNavigation.shouldRecalculatePath()) continue;
            entityNavigation.method_18053(pos);
        }
    }

    @Override
    public void sendEntityStatus(Entity entity, byte status) {
        this.getChunkManager().sendToNearbyPlayers(entity, new EntityStatusS2CPacket(entity, status));
    }

    @Override
    public ServerChunkManager getChunkManager() {
        return (ServerChunkManager)super.getChunkManager();
    }

    @Override
    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        Explosion explosion = new Explosion(this, entity, x, y, z, power, createFire, destructionType);
        if (damageSource != null) {
            explosion.setDamageSource(damageSource);
        }
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(false);
        if (destructionType == Explosion.DestructionType.NONE) {
            explosion.clearAffectedBlocks();
        }
        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (!(serverPlayerEntity.squaredDistanceTo(x, y, z) < 4096.0)) continue;
            serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(x, y, z, power, explosion.getAffectedBlocks(), explosion.getAffectedPlayers().get(serverPlayerEntity)));
        }
        return explosion;
    }

    @Override
    public void addBlockAction(BlockPos pos, Block block, int type, int data) {
        this.pendingBlockActions.add(new BlockAction(pos, block, type, data));
    }

    private void sendBlockActions() {
        while (!this.pendingBlockActions.isEmpty()) {
            BlockAction blockAction = this.pendingBlockActions.removeFirst();
            if (!this.method_14174(blockAction)) continue;
            this.server.getPlayerManager().sendToAround(null, blockAction.getPos().getX(), blockAction.getPos().getY(), blockAction.getPos().getZ(), 64.0, this.dimension.getType(), new BlockActionS2CPacket(blockAction.getPos(), blockAction.getBlock(), blockAction.getType(), blockAction.getData()));
        }
    }

    private boolean method_14174(BlockAction blockAction) {
        BlockState blockState = this.getBlockState(blockAction.getPos());
        if (blockState.getBlock() == blockAction.getBlock()) {
            return blockState.onBlockAction(this, blockAction.getPos(), blockAction.getType(), blockAction.getData());
        }
        return false;
    }

    public ServerTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    public ServerTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    @NotNull
    public MinecraftServer getServer() {
        return this.server;
    }

    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }

    public StructureManager getStructureManager() {
        return this.worldSaveHandler.getStructureManager();
    }

    public <T extends ParticleEffect> int spawnParticles(T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;
        for (int j = 0; j < this.players.size(); ++j) {
            ServerPlayerEntity serverPlayerEntity = this.players.get(j);
            if (!this.sendToPlayerIfNearby(serverPlayerEntity, false, x, y, z, particleS2CPacket)) continue;
            ++i;
        }
        return i;
    }

    public <T extends ParticleEffect> boolean spawnParticles(ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        ParticleS2CPacket packet = new ParticleS2CPacket(particle, force, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet);
    }

    private boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet) {
        if (player.getServerWorld() != this) {
            return false;
        }
        BlockPos blockPos = player.getBlockPos();
        if (blockPos.isWithinDistance(new Vec3d(x, y, z), force ? 512.0 : 32.0)) {
            player.networkHandler.sendPacket(packet);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getEntityById(int id) {
        return (Entity)this.entitiesById.get(id);
    }

    @Nullable
    public Entity getEntity(UUID uUID) {
        return this.entitiesByUuid.get(uUID);
    }

    @Nullable
    public BlockPos locateStructure(String string, BlockPos blockPos, int i, boolean bl) {
        return this.getChunkManager().getChunkGenerator().locateStructure(this, string, blockPos, i, bl);
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
    public void setTime(long time) {
        super.setTime(time);
        this.properties.getScheduledEvents().processEvents(this.server, time);
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

    @Override
    @Nullable
    public MapState getMapState(String id) {
        return this.getServer().getWorld(DimensionType.OVERWORLD).getPersistentStateManager().get(() -> new MapState(id), id);
    }

    @Override
    public void putMapState(MapState mapState) {
        this.getServer().getWorld(DimensionType.OVERWORLD).getPersistentStateManager().set(mapState);
    }

    @Override
    public int getNextMapId() {
        return this.getServer().getWorld(DimensionType.OVERWORLD).getPersistentStateManager().getOrCreate(IdCountsState::new, "idcounts").getNextMapId();
    }

    @Override
    public void setSpawnPos(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
        super.setSpawnPos(pos);
        this.getChunkManager().removeTicket(ChunkTicketType.START, chunkPos, 11, Unit.INSTANCE);
        this.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(pos), 11, Unit.INSTANCE);
    }

    public LongSet getForcedChunks() {
        ForcedChunkState forcedChunkState = this.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
        return forcedChunkState != null ? LongSets.unmodifiable(forcedChunkState.getChunks()) : LongSets.EMPTY_SET;
    }

    public boolean setChunkForced(int x, int z, boolean forced) {
        boolean bl;
        ForcedChunkState forcedChunkState = this.getPersistentStateManager().getOrCreate(ForcedChunkState::new, "chunks");
        ChunkPos chunkPos = new ChunkPos(x, z);
        long l = chunkPos.toLong();
        if (forced) {
            bl = forcedChunkState.getChunks().add(l);
            if (bl) {
                this.getChunk(x, z);
            }
        } else {
            bl = forcedChunkState.getChunks().remove(l);
        }
        forcedChunkState.setDirty(bl);
        if (bl) {
            this.getChunkManager().setChunkForced(chunkPos, forced);
        }
        return bl;
    }

    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }

    @Override
    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        Optional<PointOfInterestType> optional2;
        Optional<PointOfInterestType> optional = PointOfInterestType.from(oldBlock);
        if (Objects.equals(optional, optional2 = PointOfInterestType.from(newBlock))) {
            return;
        }
        BlockPos blockPos = pos.toImmutable();
        optional.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().remove(blockPos);
            DebugRendererInfoManager.method_19777(this, blockPos);
        }));
        optional2.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().add(blockPos, (PointOfInterestType)pointOfInterestType);
            DebugRendererInfoManager.method_19776(this, blockPos);
        }));
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.getChunkManager().getPointOfInterestStorage();
    }

    public boolean isNearOccupiedPointOfInterest(BlockPos pos) {
        return this.isNearOccupiedPointOfInterest(pos, 1);
    }

    public boolean isNearOccupiedPointOfInterest(ChunkSectionPos chunkSectionPos) {
        return this.isNearOccupiedPointOfInterest(chunkSectionPos.getCenterPos());
    }

    public boolean isNearOccupiedPointOfInterest(BlockPos pos, int maxDistance) {
        if (maxDistance > 6) {
            return false;
        }
        return this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(pos)) <= maxDistance;
    }

    public int getOccupiedPointOfInterestDistance(ChunkSectionPos pos) {
        return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(pos);
    }

    public RaidManager getRaidManager() {
        return this.raidManager;
    }

    @Nullable
    public Raid getRaidAt(BlockPos pos) {
        return this.raidManager.getRaidAt(pos, 9216);
    }

    public boolean hasRaidAt(BlockPos pos) {
        return this.getRaidAt(pos) != null;
    }

    public void handleInteraction(EntityInteraction interaction, Entity entity, InteractionObserver observer) {
        observer.onInteractionWith(interaction, entity);
    }

    public void method_21625(Path path) throws IOException {
        ThreadedAnvilChunkStorage threadedAnvilChunkStorage = this.getChunkManager().threadedAnvilChunkStorage;
        try (BufferedWriter writer = Files.newBufferedWriter(path.resolve("stats.txt"), new OpenOption[0]);){
            writer.write(String.format("spawning_chunks: %d\n", threadedAnvilChunkStorage.getTicketManager().getLevelCount()));
            for (Object2IntMap.Entry entry : this.getMobCountsByCategory().object2IntEntrySet()) {
                writer.write(String.format("spawn_count.%s: %d\n", ((EntityCategory)((Object)entry.getKey())).getName(), entry.getIntValue()));
            }
            writer.write(String.format("entities: %d\n", this.entitiesById.size()));
            writer.write(String.format("block_entities: %d\n", this.blockEntities.size()));
            writer.write(String.format("block_ticks: %d\n", ((ServerTickScheduler)this.getBlockTickScheduler()).method_20825()));
            writer.write(String.format("fluid_ticks: %d\n", ((ServerTickScheduler)this.getFluidTickScheduler()).method_20825()));
            writer.write("distance_manager: " + threadedAnvilChunkStorage.getTicketManager().method_21683() + "\n");
            writer.write(String.format("pending_tasks: %d\n", this.getChunkManager().method_21694()));
        }
        CrashReport crashReport = new CrashReport("Level dump", new Exception("dummy"));
        this.addDetailsToCrashReport(crashReport);
        BufferedWriter writer2 = Files.newBufferedWriter(path.resolve("example_crash.txt"), new OpenOption[0]);
        Object object = null;
        try {
            writer2.write(crashReport.asString());
        } catch (Throwable throwable) {
            object = throwable;
            throw throwable;
        } finally {
            if (writer2 != null) {
                if (object != null) {
                    try {
                        ((Writer)writer2).close();
                    } catch (Throwable throwable) {
                        ((Throwable)object).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)writer2).close();
                }
            }
        }
        Path path2 = path.resolve("chunks.csv");
        Throwable throwable = null;
        try (BufferedWriter writer3 = Files.newBufferedWriter(path2, new OpenOption[0]);){
            threadedAnvilChunkStorage.exportChunks(writer3);
        } catch (Throwable throwable2) {
            Throwable throwable3 = throwable2;
            throw throwable2;
        }
        Path path3 = path.resolve("entities.csv");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path3, new OpenOption[0]);){
            ServerWorld.exportEntities(bufferedWriter, this.entitiesById.values());
        }
        Path path4 = path.resolve("global_entities.csv");
        try (BufferedWriter writer5 = Files.newBufferedWriter(path4, new OpenOption[0]);){
            ServerWorld.exportEntities(writer5, this.globalEntities);
        }
        Path path5 = path.resolve("block_entities.csv");
        try (BufferedWriter writer6 = Files.newBufferedWriter(path5, new OpenOption[0]);){
            this.exportBlockEntities(writer6);
        }
    }

    private static void exportEntities(Writer writer, Iterable<Entity> iterable) throws IOException {
        CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").startBody(writer);
        for (Entity entity : iterable) {
            Text text = entity.getCustomName();
            Text text2 = entity.getDisplayName();
            csvWriter.printRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUuid(), Registry.ENTITY_TYPE.getId(entity.getType()), entity.isAlive(), text2.getString(), text != null ? text.getString() : null);
        }
    }

    private void exportBlockEntities(Writer writer) throws IOException {
        CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("type").startBody(writer);
        for (BlockEntity blockEntity : this.blockEntities) {
            BlockPos blockPos = blockEntity.getPos();
            csvWriter.printRow(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Registry.BLOCK_ENTITY.getId(blockEntity.getType()));
        }
    }

    @VisibleForTesting
    public void method_23658(BlockBox blockBox) {
        this.pendingBlockActions.removeIf(blockAction -> blockBox.contains(blockAction.getPos()));
    }

    @Override
    public /* synthetic */ Scoreboard getScoreboard() {
        return this.getScoreboard();
    }

    @Override
    public /* synthetic */ ChunkManager getChunkManager() {
        return this.getChunkManager();
    }

    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

