/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

public class DataCommandStorage {
    private final Map<String, PersistentState> storages = Maps.newHashMap();
    private final PersistentStateManager stateManager;

    public DataCommandStorage(PersistentStateManager persistentStateManager) {
        this.stateManager = persistentStateManager;
    }

    private PersistentState createStorage(String string, String string2) {
        PersistentState persistentState = new PersistentState(string2);
        this.storages.put(string, persistentState);
        return persistentState;
    }

    public CompoundTag get(Identifier identifier) {
        String string2;
        String string = identifier.getNamespace();
        PersistentState persistentState = this.stateManager.get(() -> this.method_22549(string, string2 = DataCommandStorage.getSaveKey(string)), string2);
        return persistentState != null ? persistentState.get(identifier.getPath()) : new CompoundTag();
    }

    public void set(Identifier identifier, CompoundTag compoundTag) {
        String string = identifier.getNamespace();
        String string2 = DataCommandStorage.getSaveKey(string);
        this.stateManager.getOrCreate(() -> this.createStorage(string, string2), string2).set(identifier.getPath(), compoundTag);
    }

    public Stream<Identifier> getIds() {
        return this.storages.entrySet().stream().flatMap(entry -> ((PersistentState)entry.getValue()).getIds((String)entry.getKey()));
    }

    private static String getSaveKey(String string) {
        return "command_storage_" + string;
    }

    private /* synthetic */ PersistentState method_22549(String string, String string2) {
        return this.createStorage(string, string2);
    }

    static class PersistentState
    extends net.minecraft.world.PersistentState {
        private final Map<String, CompoundTag> map = Maps.newHashMap();

        public PersistentState(String string) {
            super(string);
        }

        @Override
        public void fromTag(CompoundTag compoundTag) {
            CompoundTag compoundTag2 = compoundTag.getCompound("contents");
            for (String string : compoundTag2.getKeys()) {
                this.map.put(string, compoundTag2.getCompound(string));
            }
        }

        @Override
        public CompoundTag toTag(CompoundTag compoundTag) {
            CompoundTag compoundTag22 = new CompoundTag();
            this.map.forEach((string, compoundTag2) -> compoundTag22.put((String)string, compoundTag2.copy()));
            compoundTag.put("contents", compoundTag22);
            return compoundTag;
        }

        public CompoundTag get(String string) {
            CompoundTag compoundTag = this.map.get(string);
            return compoundTag != null ? compoundTag : new CompoundTag();
        }

        public void set(String string, CompoundTag compoundTag) {
            if (compoundTag.isEmpty()) {
                this.map.remove(string);
            } else {
                this.map.put(string, compoundTag);
            }
            this.markDirty();
        }

        public Stream<Identifier> getIds(String string) {
            return this.map.keySet().stream().map(string2 -> new Identifier(string, (String)string2));
        }
    }
}

