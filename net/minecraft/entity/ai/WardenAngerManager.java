/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class WardenAngerManager {
    private static final int maxAnger = 150;
    private static final int angerDecreasePerTick = 1;
    public static final Codec<WardenAngerManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.unboundedMap(Codecs.UUID, Codecs.NONNEGATIVE_INT).fieldOf("suspects")).forGetter(angerManager -> angerManager.suspects)).apply((Applicative<WardenAngerManager, ?>)instance, WardenAngerManager::new));
    private Object2IntMap<UUID> suspects;

    public WardenAngerManager(Map<UUID, Integer> suspects) {
        this.suspects = new Object2IntOpenHashMap<UUID>(suspects);
    }

    public void tick() {
        this.suspects.keySet().forEach(uuid2 -> this.suspects.computeInt((UUID)uuid2, (uuid, anger) -> {
            if (anger <= 1) {
                return null;
            }
            return Math.max(0, anger - 1);
        }));
    }

    public int increaseAngerAt(Entity entity, int amount) {
        return this.suspects.computeInt(entity.getUuid(), (uuid, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
    }

    public void removeSuspect(Entity entity) {
        this.suspects.removeInt(entity.getUuid());
    }

    private Optional<Object2IntMap.Entry<UUID>> getPrimeSuspect() {
        return this.suspects.object2IntEntrySet().stream().max(Map.Entry.comparingByValue());
    }

    public int getPrimeSuspectAnger() {
        return this.getPrimeSuspect().map(Map.Entry::getValue).orElse(0);
    }

    public Optional<LivingEntity> getPrimeSuspect(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            return this.getPrimeSuspect().map(Map.Entry::getKey).map(serverWorld::getEntity).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity)suspect);
        }
        return Optional.empty();
    }
}

