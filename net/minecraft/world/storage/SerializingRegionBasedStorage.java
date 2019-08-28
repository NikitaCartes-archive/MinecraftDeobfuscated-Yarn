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
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.RegionBasedStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SerializingRegionBasedStorage<R extends DynamicSerializable>
extends RegionBasedStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<Optional<R>>();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final BiFunction<Runnable, Dynamic<?>, R> deserializer;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixType;

    public SerializingRegionBasedStorage(File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function, DataFixer dataFixer, DataFixTypes dataFixTypes) {
        super(file);
        this.deserializer = biFunction;
        this.factory = function;
        this.dataFixer = dataFixer;
        this.dataFixType = dataFixTypes;
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        while (!this.unsavedElements.isEmpty() && booleanSupplier.getAsBoolean()) {
            ChunkPos chunkPos = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
            this.method_20370(chunkPos);
        }
    }

    @Nullable
    protected Optional<R> getIfLoaded(long l) {
        return (Optional)this.loadedElements.get(l);
    }

    protected Optional<R> get(long l) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(l);
        if (this.isPosInvalid(chunkSectionPos)) {
            return Optional.empty();
        }
        Optional<R> optional = this.getIfLoaded(l);
        if (optional != null) {
            return optional;
        }
        this.loadDataAt(chunkSectionPos.toChunkPos());
        optional = this.getIfLoaded(l);
        if (optional == null) {
            throw SystemUtil.throwOrPause(new IllegalStateException());
        }
        return optional;
    }

    protected boolean isPosInvalid(ChunkSectionPos chunkSectionPos) {
        return World.isHeightInvalid(ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkY()));
    }

    protected R getOrCreate(long l) {
        Optional<R> optional = this.get(l);
        if (optional.isPresent()) {
            return (R)((DynamicSerializable)optional.get());
        }
        DynamicSerializable dynamicSerializable = (DynamicSerializable)this.factory.apply(() -> this.onUpdate(l));
        this.loadedElements.put(l, (Optional<R>)Optional.of(dynamicSerializable));
        return (R)dynamicSerializable;
    }

    private void loadDataAt(ChunkPos chunkPos) {
        this.method_20368(chunkPos, NbtOps.INSTANCE, this.method_20621(chunkPos));
    }

    @Nullable
    private CompoundTag method_20621(ChunkPos chunkPos) {
        try {
            return this.getTagAt(chunkPos);
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
            int j = SerializingRegionBasedStorage.method_20369(dynamic2);
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

    private void method_20370(ChunkPos chunkPos) {
        Dynamic<Tag> dynamic = this.method_20367(chunkPos, NbtOps.INSTANCE);
        Tag tag = dynamic.getValue();
        if (tag instanceof CompoundTag) {
            try {
                this.setTagAt(chunkPos, (CompoundTag)tag);
            } catch (IOException iOException) {
                LOGGER.error("Error writing data to disk", (Throwable)iOException);
            }
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

    protected void onLoad(long l) {
    }

    protected void onUpdate(long l) {
        Optional optional = (Optional)this.loadedElements.get(l);
        if (optional == null || !optional.isPresent()) {
            LOGGER.warn("No data for position: {}", (Object)ChunkSectionPos.from(l));
            return;
        }
        this.unsavedElements.add(l);
    }

    private static int method_20369(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asNumber().orElse(1945).intValue();
    }

    public void method_20436(ChunkPos chunkPos) {
        if (!this.unsavedElements.isEmpty()) {
            for (int i = 0; i < 16; ++i) {
                long l = ChunkSectionPos.from(chunkPos, i).asLong();
                if (!this.unsavedElements.contains(l)) continue;
                this.method_20370(chunkPos);
                return;
            }
        }
    }
}

