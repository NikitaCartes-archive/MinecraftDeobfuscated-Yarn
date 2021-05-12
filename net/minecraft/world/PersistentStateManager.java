/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class PersistentStateManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, PersistentState> loadedStates = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final File directory;

    public PersistentStateManager(File directory, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.directory = directory;
    }

    private File getFile(String id) {
        return new File(this.directory, id + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(Function<NbtCompound, T> function, Supplier<T> supplier, String string) {
        T persistentState = this.get(function, string);
        if (persistentState != null) {
            return persistentState;
        }
        PersistentState persistentState2 = (PersistentState)supplier.get();
        this.set(string, persistentState2);
        return (T)persistentState2;
    }

    @Nullable
    public <T extends PersistentState> T get(Function<NbtCompound, T> function, String id) {
        PersistentState persistentState = this.loadedStates.get(id);
        if (persistentState == null && !this.loadedStates.containsKey(id)) {
            persistentState = this.readFromFile(function, id);
            this.loadedStates.put(id, persistentState);
        }
        return (T)persistentState;
    }

    @Nullable
    private <T extends PersistentState> T readFromFile(Function<NbtCompound, T> function, String id) {
        try {
            File file = this.getFile(id);
            if (file.exists()) {
                NbtCompound nbtCompound = this.readNbt(id, SharedConstants.getGameVersion().getWorldVersion());
                return (T)((PersistentState)function.apply(nbtCompound.getCompound("data")));
            }
        } catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)id, (Object)exception);
        }
        return null;
    }

    public void set(String string, PersistentState persistentState) {
        this.loadedStates.put(string, persistentState);
    }

    public NbtCompound readNbt(String id, int dataVersion) throws IOException {
        File file = this.getFile(id);
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            NbtCompound nbtCompound;
            try (PushbackInputStream pushbackInputStream = new PushbackInputStream(fileInputStream, 2);){
                NbtCompound nbtCompound2;
                if (this.isCompressed(pushbackInputStream)) {
                    nbtCompound2 = NbtIo.readCompressed(pushbackInputStream);
                } else {
                    try (DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);){
                        nbtCompound2 = NbtIo.read(dataInputStream);
                    }
                }
                int i = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : 1343;
                nbtCompound = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, nbtCompound2, i, dataVersion);
            }
            return nbtCompound;
        }
    }

    private boolean isCompressed(PushbackInputStream pushbackInputStream) throws IOException {
        int j;
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = pushbackInputStream.read(bs, 0, 2);
        if (i == 2 && (j = (bs[1] & 0xFF) << 8 | bs[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (i != 0) {
            pushbackInputStream.unread(bs, 0, i);
        }
        return bl;
    }

    public void save() {
        this.loadedStates.forEach((string, persistentState) -> {
            if (persistentState != null) {
                persistentState.save(this.getFile((String)string));
            }
        });
    }
}

