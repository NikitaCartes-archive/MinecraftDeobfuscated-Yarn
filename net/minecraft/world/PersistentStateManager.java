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
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.TagHelper;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class PersistentStateManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, PersistentState> loadedStates = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final File directory;

    public PersistentStateManager(File file, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.directory = file;
    }

    private File getFile(String string) {
        return new File(this.directory, string + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(Supplier<T> supplier, String string) {
        T persistentState = this.get(supplier, string);
        if (persistentState != null) {
            return persistentState;
        }
        PersistentState persistentState2 = (PersistentState)supplier.get();
        this.set(persistentState2);
        return (T)persistentState2;
    }

    @Nullable
    public <T extends PersistentState> T get(Supplier<T> supplier, String string) {
        PersistentState persistentState = this.loadedStates.get(string);
        if (persistentState == null) {
            try {
                File file = this.getFile(string);
                if (file.exists()) {
                    persistentState = (PersistentState)supplier.get();
                    CompoundTag compoundTag = this.method_17923(string, SharedConstants.getGameVersion().getWorldVersion());
                    persistentState.fromTag(compoundTag.getCompound("data"));
                    this.loadedStates.put(string, persistentState);
                }
            } catch (Exception exception) {
                LOGGER.error("Error loading saved data: {}", (Object)string, (Object)exception);
            }
        }
        return (T)persistentState;
    }

    public void set(PersistentState persistentState) {
        this.loadedStates.put(persistentState.getId(), persistentState);
    }

    public CompoundTag method_17923(String string, int i) throws IOException {
        File file = this.getFile(string);
        try (PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);){
            Object object;
            CompoundTag compoundTag;
            if (this.method_17921(pushbackInputStream)) {
                compoundTag = NbtIo.readCompressed(pushbackInputStream);
            } else {
                DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
                object = null;
                try {
                    compoundTag = NbtIo.read(dataInputStream);
                } catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                } finally {
                    if (dataInputStream != null) {
                        if (object != null) {
                            try {
                                dataInputStream.close();
                            } catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            dataInputStream.close();
                        }
                    }
                }
            }
            int j = compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
            object = TagHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, compoundTag, j, i);
            return object;
        }
    }

    private boolean method_17921(PushbackInputStream pushbackInputStream) throws IOException {
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
        for (PersistentState persistentState : this.loadedStates.values()) {
            persistentState.method_17919(this.getFile(persistentState.getId()));
        }
    }
}

