/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.RegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SerializingRegionBasedStorage<R extends DynamicSerializable>
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final StorageIoWorker worker;
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<Optional<R>>();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final BiFunction<Runnable, Dynamic<?>, R> deserializer;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixType;

    public SerializingRegionBasedStorage(File directory, BiFunction<Runnable, Dynamic<?>, R> deserializer, Function<Runnable, R> factory, DataFixer dataFixer, DataFixTypes dataFixType) {
        this.deserializer = deserializer;
        this.factory = factory;
        this.dataFixer = dataFixer;
        this.dataFixType = dataFixType;
        this.worker = new StorageIoWorker(new RegionBasedStorage(directory), directory.getName());
    }

    protected void tick(BooleanSupplier shouldKeepTicking) {
        while (!this.unsavedElements.isEmpty() && shouldKeepTicking.getAsBoolean()) {
            ChunkPos chunkPos = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
            this.save(chunkPos);
        }
    }

    @Nullable
    protected Optional<R> getIfLoaded(long pos) {
        return (Optional)this.loadedElements.get(pos);
    }

    protected Optional<R> get(long pos) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
        if (this.isPosInvalid(chunkSectionPos)) {
            return Optional.empty();
        }
        Optional<R> optional = this.getIfLoaded(pos);
        if (optional != null) {
            return optional;
        }
        this.loadDataAt(chunkSectionPos.toChunkPos());
        optional = this.getIfLoaded(pos);
        if (optional == null) {
            throw Util.throwOrPause(new IllegalStateException());
        }
        return optional;
    }

    protected boolean isPosInvalid(ChunkSectionPos pos) {
        return World.isHeightInvalid(ChunkSectionPos.getWorldCoord(pos.getSectionY()));
    }

    protected R getOrCreate(long pos) {
        Optional<R> optional = this.get(pos);
        if (optional.isPresent()) {
            return (R)((DynamicSerializable)optional.get());
        }
        DynamicSerializable dynamicSerializable = (DynamicSerializable)this.factory.apply(() -> this.onUpdate(pos));
        this.loadedElements.put(pos, (Optional<R>)Optional.of(dynamicSerializable));
        return (R)dynamicSerializable;
    }

    private void loadDataAt(ChunkPos chunkPos) {
        this.method_20368(chunkPos, NbtOps.INSTANCE, this.method_20621(chunkPos));
    }

    @Nullable
    private CompoundTag method_20621(ChunkPos chunkPos) {
        try {
            return this.worker.getNbt(chunkPos);
        } catch (IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", (Object)chunkPos, (Object)iOException);
            return null;
        }
    }

    private <T> void method_20368(ChunkPos chunkPos, DynamicOps<T> dynamicOps, @Nullable T object) {
        if (object == null) {
            for (int i = 0; i < 16; ++i) {
                this.loadedElements.put(ChunkSectionPos.from(chunkPos, i).asLong(), (Optional<R>)Optional.empty());
            }
        } else {
            int k;
            Dynamic<T> dynamic2 = new Dynamic<T>(dynamicOps, object);
            int j = SerializingRegionBasedStorage.getDataVersion(dynamic2);
            boolean bl = j != (k = SharedConstants.getGameVersion().getWorldVersion());
            Dynamic<T> dynamic22 = this.dataFixer.update(this.dataFixType.getTypeReference(), dynamic2, j, k);
            OptionalDynamic<T> optionalDynamic = dynamic22.get("Sections");
            for (int l = 0; l < 16; ++l) {
                long m = ChunkSectionPos.from(chunkPos, l).asLong();
                Optional<DynamicSerializable> optional = optionalDynamic.get(Integer.toString(l)).get().map(dynamic -> (DynamicSerializable)this.deserializer.apply(() -> this.onUpdate(m), (Dynamic<?>)dynamic));
                this.loadedElements.put(m, (Optional<R>)optional);
                optional.ifPresent(dynamicSerializable -> {
                    this.onLoad(m);
                    if (bl) {
                        this.onUpdate(m);
                    }
                });
            }
        }
    }

    private void save(ChunkPos chunkPos) {
        Dynamic<Tag> dynamic = this.method_20367(chunkPos, NbtOps.INSTANCE);
        Tag tag = dynamic.getValue();
        if (tag instanceof CompoundTag) {
            this.worker.setResult(chunkPos, (CompoundTag)tag);
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)tag);
        }
    }

    private <T> Dynamic<T> method_20367(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
        HashMap<T, T> map = Maps.newHashMap();
        for (int i = 0; i < 16; ++i) {
            long l = ChunkSectionPos.from(chunkPos, i).asLong();
            this.unsavedElements.remove(l);
            Optional optional = (Optional)this.loadedElements.get(l);
            if (optional == null || !optional.isPresent()) continue;
            map.put(dynamicOps.createString(Integer.toString(i)), ((DynamicSerializable)optional.get()).serialize(dynamicOps));
        }
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Sections"), dynamicOps.createMap(map), dynamicOps.createString("DataVersion"), dynamicOps.createInt(SharedConstants.getGameVersion().getWorldVersion()))));
    }

    protected void onLoad(long pos) {
    }

    protected void onUpdate(long pos) {
        Optional optional = (Optional)this.loadedElements.get(pos);
        if (optional == null || !optional.isPresent()) {
            LOGGER.warn("No data for position: {}", (Object)ChunkSectionPos.from(pos));
            return;
        }
        this.unsavedElements.add(pos);
    }

    private static int getDataVersion(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asNumber().orElse(1945).intValue();
    }

    public void method_20436(ChunkPos chunkPos) {
        if (!this.unsavedElements.isEmpty()) {
            for (int i = 0; i < 16; ++i) {
                long l = ChunkSectionPos.from(chunkPos, i).asLong();
                if (!this.unsavedElements.contains(l)) continue;
                this.save(chunkPos);
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

