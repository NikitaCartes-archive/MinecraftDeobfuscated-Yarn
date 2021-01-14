/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

public class DataCommandStorage {
    private final Map<String, PersistentState> storages = Maps.newHashMap();
    private final PersistentStateManager stateManager;

    public DataCommandStorage(PersistentStateManager stateManager) {
        this.stateManager = stateManager;
    }

    private PersistentState createStorage(String namespace, String saveKey) {
        PersistentState persistentState = new PersistentState(saveKey);
        this.storages.put(namespace, persistentState);
        return persistentState;
    }

    public NbtCompound get(Identifier id) {
        String string2;
        String string = id.getNamespace();
        PersistentState persistentState = this.stateManager.get(() -> this.method_22549(string, string2 = DataCommandStorage.getSaveKey(string)), string2);
        return persistentState != null ? persistentState.get(id.getPath()) : new NbtCompound();
    }

    public void set(Identifier id, NbtCompound nbt) {
        String string = id.getNamespace();
        String string2 = DataCommandStorage.getSaveKey(string);
        this.stateManager.getOrCreate(() -> this.createStorage(string, string2), string2).set(id.getPath(), nbt);
    }

    public Stream<Identifier> getIds() {
        return this.storages.entrySet().stream().flatMap(entry -> ((PersistentState)entry.getValue()).getIds((String)entry.getKey()));
    }

    private static String getSaveKey(String namespace) {
        return "command_storage_" + namespace;
    }

    private /* synthetic */ PersistentState method_22549(String string, String string2) {
        return this.createStorage(string, string2);
    }

    static class PersistentState
    extends net.minecraft.world.PersistentState {
        private final Map<String, NbtCompound> map = Maps.newHashMap();

        public PersistentState(String string) {
            super(string);
        }

        @Override
        public void fromTag(NbtCompound tag) {
            NbtCompound nbtCompound = tag.getCompound("contents");
            for (String string : nbtCompound.getKeys()) {
                this.map.put(string, nbtCompound.getCompound(string));
            }
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            NbtCompound nbtCompound = new NbtCompound();
            this.map.forEach((string, nbtCompound2) -> nbtCompound.put((String)string, nbtCompound2.copy()));
            nbt.put("contents", nbtCompound);
            return nbt;
        }

        public NbtCompound get(String name) {
            NbtCompound nbtCompound = this.map.get(name);
            return nbtCompound != null ? nbtCompound : new NbtCompound();
        }

        public void set(String name, NbtCompound tag) {
            if (tag.isEmpty()) {
                this.map.remove(name);
            } else {
                this.map.put(name, tag);
            }
            this.markDirty();
        }

        public Stream<Identifier> getIds(String namespace) {
            return this.map.keySet().stream().map(string2 -> new Identifier(namespace, (String)string2));
        }
    }
}

