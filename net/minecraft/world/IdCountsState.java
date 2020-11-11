/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class IdCountsState
extends PersistentState {
    private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<String>();

    public IdCountsState() {
        this.idCounts.defaultReturnValue(-1);
    }

    public static IdCountsState method_32360(CompoundTag compoundTag) {
        IdCountsState idCountsState = new IdCountsState();
        for (String string : compoundTag.getKeys()) {
            if (!compoundTag.contains(string, 99)) continue;
            idCountsState.idCounts.put(string, compoundTag.getInt(string));
        }
        return idCountsState;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        for (Object2IntMap.Entry entry : this.idCounts.object2IntEntrySet()) {
            tag.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return tag;
    }

    public int getNextMapId() {
        int i = this.idCounts.getInt("map") + 1;
        this.idCounts.put("map", i);
        this.markDirty();
        return i;
    }
}

