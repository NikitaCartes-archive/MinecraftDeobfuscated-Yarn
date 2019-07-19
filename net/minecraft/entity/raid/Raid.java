/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Raid {
    private static final TranslatableText EVENT_TEXT = new TranslatableText("event.minecraft.raid", new Object[0]);
    private static final TranslatableText VICTORY_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.victory", new Object[0]);
    private static final TranslatableText DEFEAT_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.defeat", new Object[0]);
    private static final Text VICTORY_TITLE = EVENT_TEXT.copy().append(" - ").append(VICTORY_SUFFIX_TEXT);
    private static final Text DEFEAT_TITLE = EVENT_TEXT.copy().append(" - ").append(DEFEAT_SUFFIX_TEXT);
    private final Map<Integer, RaiderEntity> waveToCaptain = Maps.newHashMap();
    private final Map<Integer, Set<RaiderEntity>> waveToRaiders = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerWorld world;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int wavesSpawned;
    private final ServerBossBar bar = new ServerBossBar(EVENT_TEXT, BossBar.Color.RED, BossBar.Style.NOTCHED_10);
    private int postRaidTicks;
    private int preRaidTicks;
    private final Random random = new Random();
    private final int waveCount;
    private Status status;
    private int finishCooldown;
    private Optional<BlockPos> preCalculatedRavagerSpawnLocation = Optional.empty();

    public Raid(int i, ServerWorld serverWorld, BlockPos blockPos) {
        this.id = i;
        this.world = serverWorld;
        this.active = true;
        this.preRaidTicks = 300;
        this.bar.setPercent(0.0f);
        this.center = blockPos;
        this.waveCount = this.getMaxWaves(serverWorld.getDifficulty());
        this.status = Status.ONGOING;
    }

    public Raid(ServerWorld serverWorld, CompoundTag compoundTag) {
        this.world = serverWorld;
        this.id = compoundTag.getInt("Id");
        this.started = compoundTag.getBoolean("Started");
        this.active = compoundTag.getBoolean("Active");
        this.ticksActive = compoundTag.getLong("TicksActive");
        this.badOmenLevel = compoundTag.getInt("BadOmenLevel");
        this.wavesSpawned = compoundTag.getInt("GroupsSpawned");
        this.preRaidTicks = compoundTag.getInt("PreRaidTicks");
        this.postRaidTicks = compoundTag.getInt("PostRaidTicks");
        this.totalHealth = compoundTag.getFloat("TotalHealth");
        this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
        this.waveCount = compoundTag.getInt("NumGroups");
        this.status = Status.fromName(compoundTag.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (compoundTag.contains("HeroesOfTheVillage", 9)) {
            ListTag listTag = compoundTag.getList("HeroesOfTheVillage", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                UUID uUID = compoundTag2.getUuid("UUID");
                this.heroesOfTheVillage.add(uUID);
            }
        }
    }

    public boolean isFinished() {
        return this.hasWon() || this.hasLost();
    }

    public boolean isPreRaid() {
        return this.hasSpawned() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
    }

    public boolean hasSpawned() {
        return this.wavesSpawned > 0;
    }

    public boolean hasStopped() {
        return this.status == Status.STOPPED;
    }

    public boolean hasWon() {
        return this.status == Status.VICTORY;
    }

    public boolean hasLost() {
        return this.status == Status.LOSS;
    }

    public World getWorld() {
        return this.world;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.wavesSpawned;
    }

    private Predicate<ServerPlayerEntity> isInRaidDistance() {
        return serverPlayerEntity -> {
            BlockPos blockPos = new BlockPos((Entity)serverPlayerEntity);
            return serverPlayerEntity.isAlive() && this.world.getRaidAt(blockPos) == this;
        };
    }

    private void updateBarToPlayers() {
        HashSet<ServerPlayerEntity> set = Sets.newHashSet(this.bar.getPlayers());
        List<ServerPlayerEntity> list = this.world.getPlayers(this.isInRaidDistance());
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (set.contains(serverPlayerEntity)) continue;
            this.bar.addPlayer(serverPlayerEntity);
        }
        for (ServerPlayerEntity serverPlayerEntity : set) {
            if (list.contains(serverPlayerEntity)) continue;
            this.bar.removePlayer(serverPlayerEntity);
        }
    }

    public int getMaxAcceptableBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

    public void start(PlayerEntity playerEntity) {
        if (playerEntity.hasStatusEffect(StatusEffects.BAD_OMEN)) {
            this.badOmenLevel += playerEntity.getStatusEffect(StatusEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxAcceptableBadOmenLevel());
        }
        playerEntity.removeStatusEffect(StatusEffects.BAD_OMEN);
    }

    public void invalidate() {
        this.active = false;
        this.bar.clearPlayers();
        this.status = Status.STOPPED;
    }

    public void tick() {
        if (this.hasStopped()) {
            return;
        }
        if (this.status == Status.ONGOING) {
            boolean bl2;
            boolean bl = this.active;
            this.active = this.world.isBlockLoaded(this.center);
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                this.invalidate();
                return;
            }
            if (bl != this.active) {
                this.bar.setVisible(this.active);
            }
            if (!this.active) {
                return;
            }
            if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
                this.method_20511();
            }
            if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
                if (this.wavesSpawned > 0) {
                    this.status = Status.LOSS;
                } else {
                    this.invalidate();
                }
            }
            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
                this.invalidate();
                return;
            }
            int i = this.getRaiderCount();
            if (i == 0 && this.shouldSpawnMoreGroups()) {
                if (this.preRaidTicks > 0) {
                    boolean bl3;
                    bl2 = this.preCalculatedRavagerSpawnLocation.isPresent();
                    boolean bl4 = bl3 = !bl2 && this.preRaidTicks % 5 == 0;
                    if (bl2 && !this.world.getChunkManager().shouldTickChunk(new ChunkPos(this.preCalculatedRavagerSpawnLocation.get()))) {
                        bl3 = true;
                    }
                    if (bl3) {
                        int j = 0;
                        if (this.preRaidTicks < 100) {
                            j = 1;
                        } else if (this.preRaidTicks < 40) {
                            j = 2;
                        }
                        this.preCalculatedRavagerSpawnLocation = this.preCalculateRavagerSpawnLocation(j);
                    }
                    if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
                        this.updateBarToPlayers();
                    }
                    --this.preRaidTicks;
                    this.bar.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0f, 0.0f, 1.0f));
                } else if (this.preRaidTicks == 0 && this.wavesSpawned > 0) {
                    this.preRaidTicks = 300;
                    this.bar.setName(EVENT_TEXT);
                    return;
                }
            }
            if (this.ticksActive % 20L == 0L) {
                this.updateBarToPlayers();
                this.removeObsoleteRaiders();
                if (i > 0) {
                    if (i <= 2) {
                        this.bar.setName(EVENT_TEXT.copy().append(" - ").append(new TranslatableText("event.minecraft.raid.raiders_remaining", i)));
                    } else {
                        this.bar.setName(EVENT_TEXT);
                    }
                } else {
                    this.bar.setName(EVENT_TEXT);
                }
            }
            bl2 = false;
            int k = 0;
            while (this.canSpawnRaiders()) {
                BlockPos blockPos;
                BlockPos blockPos2 = blockPos = this.preCalculatedRavagerSpawnLocation.isPresent() ? this.preCalculatedRavagerSpawnLocation.get() : this.getRavagerSpawnLocation(k, 20);
                if (blockPos != null) {
                    this.started = true;
                    this.spawnNextWave(blockPos);
                    if (!bl2) {
                        this.playRaidHorn(blockPos);
                        bl2 = true;
                    }
                } else {
                    ++k;
                }
                if (k <= 3) continue;
                this.invalidate();
                break;
            }
            if (this.hasStarted() && !this.shouldSpawnMoreGroups() && i == 0) {
                if (this.postRaidTicks < 40) {
                    ++this.postRaidTicks;
                } else {
                    this.status = Status.VICTORY;
                    for (UUID uUID : this.heroesOfTheVillage) {
                        Entity entity = this.world.getEntity(uUID);
                        if (!(entity instanceof LivingEntity) || entity.isSpectator()) continue;
                        LivingEntity livingEntity = (LivingEntity)entity;
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (!(livingEntity instanceof ServerPlayerEntity)) continue;
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
                        serverPlayerEntity.incrementStat(Stats.RAID_WIN);
                        Criterions.HERO_OF_THE_VILLAGE.trigger(serverPlayerEntity);
                    }
                }
            }
            this.markDirty();
        } else if (this.isFinished()) {
            ++this.finishCooldown;
            if (this.finishCooldown >= 600) {
                this.invalidate();
                return;
            }
            if (this.finishCooldown % 20 == 0) {
                this.updateBarToPlayers();
                this.bar.setVisible(true);
                if (this.hasWon()) {
                    this.bar.setPercent(0.0f);
                    this.bar.setName(VICTORY_TITLE);
                } else {
                    this.bar.setName(DEFEAT_TITLE);
                }
            }
        }
    }

    private void method_20511() {
        Stream<ChunkSectionPos> stream = ChunkSectionPos.stream(ChunkSectionPos.from(this.center), 2);
        stream.filter(this.world::isNearOccupiedPointOfInterest).map(ChunkSectionPos::getCenterPos).min(Comparator.comparingDouble(blockPos -> blockPos.getSquaredDistance(this.center))).ifPresent(this::method_20509);
    }

    private Optional<BlockPos> preCalculateRavagerSpawnLocation(int i) {
        for (int j = 0; j < 3; ++j) {
            BlockPos blockPos = this.getRavagerSpawnLocation(i, 1);
            if (blockPos == null) continue;
            return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    private boolean shouldSpawnMoreGroups() {
        if (this.hasExtraWave()) {
            return !this.hasSpawnedExtraWave();
        }
        return !this.hasSpawnedFinalWave();
    }

    private boolean hasSpawnedFinalWave() {
        return this.getGroupsSpawned() == this.waveCount;
    }

    private boolean hasExtraWave() {
        return this.badOmenLevel > 1;
    }

    private boolean hasSpawnedExtraWave() {
        return this.getGroupsSpawned() > this.waveCount;
    }

    private boolean isSpawningExtraWave() {
        return this.hasSpawnedFinalWave() && this.getRaiderCount() == 0 && this.hasExtraWave();
    }

    private void removeObsoleteRaiders() {
        Iterator<Set<RaiderEntity>> iterator = this.waveToRaiders.values().iterator();
        HashSet<RaiderEntity> set = Sets.newHashSet();
        while (iterator.hasNext()) {
            Set<RaiderEntity> set2 = iterator.next();
            for (RaiderEntity raiderEntity : set2) {
                BlockPos blockPos = new BlockPos(raiderEntity);
                if (raiderEntity.removed || raiderEntity.dimension != this.world.getDimension().getType() || this.center.getSquaredDistance(blockPos) >= 12544.0) {
                    set.add(raiderEntity);
                    continue;
                }
                if (raiderEntity.age <= 600) continue;
                if (this.world.getEntity(raiderEntity.getUuid()) == null) {
                    set.add(raiderEntity);
                }
                if (!this.world.isNearOccupiedPointOfInterest(blockPos) && raiderEntity.getDespawnCounter() > 2400) {
                    raiderEntity.setOutOfRaidCounter(raiderEntity.getOutOfRaidCounter() + 1);
                }
                if (raiderEntity.getOutOfRaidCounter() < 30) continue;
                set.add(raiderEntity);
            }
        }
        for (RaiderEntity raiderEntity2 : set) {
            this.removeFromWave(raiderEntity2, true);
        }
    }

    private void playRaidHorn(BlockPos blockPos) {
        float f = 13.0f;
        int i = 64;
        for (PlayerEntity playerEntity : this.world.getPlayers()) {
            Vec3d vec3d = new Vec3d(playerEntity.x, playerEntity.y, playerEntity.z);
            Vec3d vec3d2 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            float g = MathHelper.sqrt((vec3d2.x - vec3d.x) * (vec3d2.x - vec3d.x) + (vec3d2.z - vec3d.z) * (vec3d2.z - vec3d.z));
            double d = vec3d.x + (double)(13.0f / g) * (vec3d2.x - vec3d.x);
            double e = vec3d.z + (double)(13.0f / g) * (vec3d2.z - vec3d.z);
            if (!(g <= 64.0f) && !this.world.isNearOccupiedPointOfInterest(new BlockPos(playerEntity))) continue;
            ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, d, playerEntity.y, e, 64.0f, 1.0f));
        }
    }

    private void spawnNextWave(BlockPos blockPos) {
        boolean bl = false;
        int i = this.wavesSpawned + 1;
        this.totalHealth = 0.0f;
        LocalDifficulty localDifficulty = this.world.getLocalDifficulty(blockPos);
        boolean bl2 = this.isSpawningExtraWave();
        for (Member member : Member.VALUES) {
            int j = this.getCount(member, i, bl2) + this.getBonusCount(member, this.random, i, localDifficulty, bl2);
            int k = 0;
            for (int l = 0; l < j; ++l) {
                RaiderEntity raiderEntity = (RaiderEntity)member.type.create(this.world);
                if (!bl && raiderEntity.canLead()) {
                    raiderEntity.setPatrolLeader(true);
                    this.setWaveCaptain(i, raiderEntity);
                    bl = true;
                }
                this.addRaider(i, raiderEntity, blockPos, false);
                if (member.type != EntityType.RAVAGER) continue;
                RaiderEntity raiderEntity2 = null;
                if (i == this.getMaxWaves(Difficulty.NORMAL)) {
                    raiderEntity2 = EntityType.PILLAGER.create(this.world);
                } else if (i >= this.getMaxWaves(Difficulty.HARD)) {
                    raiderEntity2 = k == 0 ? (RaiderEntity)EntityType.EVOKER.create(this.world) : (RaiderEntity)EntityType.VINDICATOR.create(this.world);
                }
                ++k;
                if (raiderEntity2 == null) continue;
                this.addRaider(i, raiderEntity2, blockPos, false);
                raiderEntity2.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
                raiderEntity2.startRiding(raiderEntity);
            }
        }
        this.preCalculatedRavagerSpawnLocation = Optional.empty();
        ++this.wavesSpawned;
        this.updateBar();
        this.markDirty();
    }

    public void addRaider(int i, RaiderEntity raiderEntity, @Nullable BlockPos blockPos, boolean bl) {
        boolean bl2 = this.addToWave(i, raiderEntity);
        if (bl2) {
            raiderEntity.setRaid(this);
            raiderEntity.setWave(i);
            raiderEntity.setAbleToJoinRaid(true);
            raiderEntity.setOutOfRaidCounter(0);
            if (!bl && blockPos != null) {
                raiderEntity.updatePosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
                raiderEntity.initialize(this.world, this.world.getLocalDifficulty(blockPos), SpawnType.EVENT, null, null);
                raiderEntity.addBonusForWave(i, false);
                raiderEntity.onGround = true;
                this.world.spawnEntity(raiderEntity);
            }
        }
    }

    public void updateBar() {
        this.bar.setPercent(MathHelper.clamp(this.getCurrentRaiderHealth() / this.totalHealth, 0.0f, 1.0f));
    }

    public float getCurrentRaiderHealth() {
        float f = 0.0f;
        for (Set<RaiderEntity> set : this.waveToRaiders.values()) {
            for (RaiderEntity raiderEntity : set) {
                f += raiderEntity.getHealth();
            }
        }
        return f;
    }

    private boolean canSpawnRaiders() {
        return this.preRaidTicks == 0 && (this.wavesSpawned < this.waveCount || this.isSpawningExtraWave()) && this.getRaiderCount() == 0;
    }

    public int getRaiderCount() {
        return this.waveToRaiders.values().stream().mapToInt(Set::size).sum();
    }

    public void removeFromWave(@NotNull RaiderEntity raiderEntity, boolean bl) {
        boolean bl2;
        Set<RaiderEntity> set = this.waveToRaiders.get(raiderEntity.getWave());
        if (set != null && (bl2 = set.remove(raiderEntity))) {
            if (bl) {
                this.totalHealth -= raiderEntity.getHealth();
            }
            raiderEntity.setRaid(null);
            this.updateBar();
            this.markDirty();
        }
    }

    private void markDirty() {
        this.world.getRaidManager().markDirty();
    }

    public static ItemStack getOminousBanner() {
        ItemStack itemStack = new ItemStack(Items.WHITE_BANNER);
        CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
        ListTag listTag = new BannerPattern.Builder().with(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).with(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).with(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).with(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).with(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).with(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).with(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).with(BannerPattern.BORDER, DyeColor.BLACK).build();
        compoundTag.put("Patterns", listTag);
        itemStack.setCustomName(new TranslatableText("block.minecraft.ominous_banner", new Object[0]).formatted(Formatting.GOLD));
        return itemStack;
    }

    @Nullable
    public RaiderEntity getCaptain(int i) {
        return this.waveToCaptain.get(i);
    }

    @Nullable
    private BlockPos getRavagerSpawnLocation(int i, int j) {
        int k = i == 0 ? 2 : 2 - i;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = 0; l < j; ++l) {
            float f = this.world.random.nextFloat() * ((float)Math.PI * 2);
            int m = this.center.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0f * (float)k) + this.world.random.nextInt(5);
            int n = this.center.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0f * (float)k) + this.world.random.nextInt(5);
            int o = this.world.getTop(Heightmap.Type.WORLD_SURFACE, m, n);
            mutable.set(m, o, n);
            if (this.world.isNearOccupiedPointOfInterest(mutable) && i < 2 || !this.world.isAreaLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10) || !this.world.getChunkManager().shouldTickChunk(new ChunkPos(mutable)) || !SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, mutable, EntityType.RAVAGER) && (this.world.getBlockState(mutable.down()).getBlock() != Blocks.SNOW || !this.world.getBlockState(mutable).isAir())) continue;
            return mutable;
        }
        return null;
    }

    private boolean addToWave(int i, RaiderEntity raiderEntity) {
        return this.addToWave(i, raiderEntity, true);
    }

    public boolean addToWave(int i, RaiderEntity raiderEntity, boolean bl) {
        this.waveToRaiders.computeIfAbsent(i, integer -> Sets.newHashSet());
        Set<RaiderEntity> set = this.waveToRaiders.get(i);
        RaiderEntity raiderEntity2 = null;
        for (RaiderEntity raiderEntity3 : set) {
            if (!raiderEntity3.getUuid().equals(raiderEntity.getUuid())) continue;
            raiderEntity2 = raiderEntity3;
            break;
        }
        if (raiderEntity2 != null) {
            set.remove(raiderEntity2);
            set.add(raiderEntity);
        }
        set.add(raiderEntity);
        if (bl) {
            this.totalHealth += raiderEntity.getHealth();
        }
        this.updateBar();
        this.markDirty();
        return true;
    }

    public void setWaveCaptain(int i, RaiderEntity raiderEntity) {
        this.waveToCaptain.put(i, raiderEntity);
        raiderEntity.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
        raiderEntity.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
    }

    public void removeLeader(int i) {
        this.waveToCaptain.remove(i);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    private void method_20509(BlockPos blockPos) {
        this.center = blockPos;
    }

    public int getRaidId() {
        return this.id;
    }

    private int getCount(Member member, int i, boolean bl) {
        return bl ? member.countInWave[this.waveCount] : member.countInWave[i];
    }

    private int getBonusCount(Member member, Random random, int i, LocalDifficulty localDifficulty, boolean bl) {
        int j;
        Difficulty difficulty = localDifficulty.getGlobalDifficulty();
        boolean bl2 = difficulty == Difficulty.EASY;
        boolean bl3 = difficulty == Difficulty.NORMAL;
        switch (member) {
            case WITCH: {
                if (!bl2 && i > 2 && i != 4) {
                    j = 1;
                    break;
                }
                return 0;
            }
            case PILLAGER: 
            case VINDICATOR: {
                if (bl2) {
                    j = random.nextInt(2);
                    break;
                }
                if (bl3) {
                    j = 1;
                    break;
                }
                j = 2;
                break;
            }
            case RAVAGER: {
                j = !bl2 && bl ? 1 : 0;
                break;
            }
            default: {
                return 0;
            }
        }
        return j > 0 ? random.nextInt(j + 1) : 0;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.putInt("Id", this.id);
        compoundTag.putBoolean("Started", this.started);
        compoundTag.putBoolean("Active", this.active);
        compoundTag.putLong("TicksActive", this.ticksActive);
        compoundTag.putInt("BadOmenLevel", this.badOmenLevel);
        compoundTag.putInt("GroupsSpawned", this.wavesSpawned);
        compoundTag.putInt("PreRaidTicks", this.preRaidTicks);
        compoundTag.putInt("PostRaidTicks", this.postRaidTicks);
        compoundTag.putFloat("TotalHealth", this.totalHealth);
        compoundTag.putInt("NumGroups", this.waveCount);
        compoundTag.putString("Status", this.status.getName());
        compoundTag.putInt("CX", this.center.getX());
        compoundTag.putInt("CY", this.center.getY());
        compoundTag.putInt("CZ", this.center.getZ());
        ListTag listTag = new ListTag();
        for (UUID uUID : this.heroesOfTheVillage) {
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putUuid("UUID", uUID);
            listTag.add(compoundTag2);
        }
        compoundTag.put("HeroesOfTheVillage", listTag);
        return compoundTag;
    }

    public int getMaxWaves(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: {
                return 3;
            }
            case NORMAL: {
                return 5;
            }
            case HARD: {
                return 7;
            }
        }
        return 0;
    }

    public float getEnchantmentChance() {
        int i = this.getBadOmenLevel();
        if (i == 2) {
            return 0.1f;
        }
        if (i == 3) {
            return 0.25f;
        }
        if (i == 4) {
            return 0.5f;
        }
        if (i == 5) {
            return 0.75f;
        }
        return 0.0f;
    }

    public void addHero(Entity entity) {
        this.heroesOfTheVillage.add(entity.getUuid());
    }

    static enum Member {
        VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
        EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
        PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
        WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
        RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        private static final Member[] VALUES;
        private final EntityType<? extends RaiderEntity> type;
        private final int[] countInWave;

        private Member(EntityType<? extends RaiderEntity> entityType, int[] is) {
            this.type = entityType;
            this.countInWave = is;
        }

        static {
            VALUES = Member.values();
        }
    }

    static enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final Status[] VALUES;

        private static Status fromName(String string) {
            for (Status status : VALUES) {
                if (!string.equalsIgnoreCase(status.name())) continue;
                return status;
            }
            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            VALUES = Status.values();
        }
    }
}

