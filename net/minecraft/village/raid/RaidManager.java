/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class RaidManager
extends PersistentState {
    private static final String RAIDS = "raids";
    private final Map<Integer, Raid> raids = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;
    private int currentTime;

    public RaidManager(ServerWorld world) {
        this.world = world;
        this.nextAvailableId = 1;
        this.markDirty();
    }

    public Raid getRaid(int id) {
        return this.raids.get(id);
    }

    public void tick() {
        ++this.currentTime;
        Iterator<Raid> iterator = this.raids.values().iterator();
        while (iterator.hasNext()) {
            Raid raid = iterator.next();
            if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                raid.invalidate();
            }
            if (raid.hasStopped()) {
                iterator.remove();
                this.markDirty();
                continue;
            }
            raid.tick();
        }
        if (this.currentTime % 200 == 0) {
            this.markDirty();
        }
        DebugInfoSender.sendRaids(this.world, this.raids.values());
    }

    public static boolean isValidRaiderFor(RaiderEntity raider, Raid raid) {
        if (raider != null && raid != null && raid.getWorld() != null) {
            return raider.isAlive() && raider.canJoinRaid() && raider.getDespawnCounter() <= 2400 && raider.world.getDimension() == raid.getWorld().getDimension();
        }
        return false;
    }

    @Nullable
    public Raid startRaid(ServerPlayerEntity player) {
        BlockPos blockPos3;
        if (player.isSpectator()) {
            return null;
        }
        if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            return null;
        }
        DimensionType dimensionType = player.world.getDimension();
        if (!dimensionType.hasRaids()) {
            return null;
        }
        BlockPos blockPos = player.getBlockPos();
        List list = this.world.getPointOfInterestStorage().getInCircle(PointOfInterestType.ALWAYS_TRUE, blockPos, 64, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).collect(Collectors.toList());
        int i = 0;
        Vec3d vec3d = Vec3d.ZERO;
        for (PointOfInterest pointOfInterest : list) {
            BlockPos blockPos2 = pointOfInterest.getPos();
            vec3d = vec3d.add(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            ++i;
        }
        if (i > 0) {
            vec3d = vec3d.multiply(1.0 / (double)i);
            blockPos3 = new BlockPos(vec3d);
        } else {
            blockPos3 = blockPos;
        }
        Raid raid = this.getOrCreateRaid(player.getWorld(), blockPos3);
        boolean bl = false;
        if (!raid.hasStarted()) {
            if (!this.raids.containsKey(raid.getRaidId())) {
                this.raids.put(raid.getRaidId(), raid);
            }
            bl = true;
        } else if (raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
            bl = true;
        } else {
            player.removeStatusEffect(StatusEffects.BAD_OMEN);
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, 43));
        }
        if (bl) {
            raid.start(player);
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, 43));
            if (!raid.hasSpawned()) {
                player.incrementStat(Stats.RAID_TRIGGER);
                Criteria.VOLUNTARY_EXILE.trigger(player);
            }
        }
        this.markDirty();
        return raid;
    }

    private Raid getOrCreateRaid(ServerWorld world, BlockPos pos) {
        Raid raid = world.getRaidAt(pos);
        return raid != null ? raid : new Raid(this.nextId(), world, pos);
    }

    public static RaidManager fromNbt(ServerWorld world, NbtCompound nbt) {
        RaidManager raidManager = new RaidManager(world);
        raidManager.nextAvailableId = nbt.getInt("NextAvailableID");
        raidManager.currentTime = nbt.getInt("Tick");
        NbtList nbtList = nbt.getList("Raids", 10);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Raid raid = new Raid(world, nbtCompound);
            raidManager.raids.put(raid.getRaidId(), raid);
        }
        return raidManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("NextAvailableID", this.nextAvailableId);
        nbt.putInt("Tick", this.currentTime);
        NbtList nbtList = new NbtList();
        for (Raid raid : this.raids.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            raid.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        nbt.put("Raids", nbtList);
        return nbt;
    }

    public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
        if (dimensionTypeEntry.matchesKey(DimensionTypes.THE_END)) {
            return "raids_end";
        }
        return RAIDS;
    }

    private int nextId() {
        return ++this.nextAvailableId;
    }

    @Nullable
    public Raid getRaidAt(BlockPos pos, int searchDistance) {
        Raid raid = null;
        double d = searchDistance;
        for (Raid raid2 : this.raids.values()) {
            double e = raid2.getCenter().getSquaredDistance(pos);
            if (!raid2.isActive() || !(e < d)) continue;
            raid = raid2;
            d = e;
        }
        return raid;
    }
}

