/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class IdCountsState
extends PersistentState {
    private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<String>();

    public IdCountsState() {
        super("idcounts");
        this.idCounts.defaultReturnValue(-1);
    }

    @Override
    public void fromTag(NbtCompound tag) {
        this.idCounts.clear();
        for (String string : tag.getKeys()) {
            if (!tag.contains(string, 99)) continue;
            this.idCounts.put(string, tag.getInt(string));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (Object2IntMap.Entry entry : this.idCounts.object2IntEntrySet()) {
            nbt.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return nbt;
    }

    public int getNextMapId() {
        int i = this.idCounts.getInt("map") + 1;
        this.idCounts.put("map", i);
        this.markDirty();
        return i;
    }
}

