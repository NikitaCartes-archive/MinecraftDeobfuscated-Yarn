/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SerializingRegionBasedStorage<R>
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final StorageIoWorker worker;
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<Optional<R>>();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codecFactory;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixType;
    protected final HeightLimitView field_27240;

    public SerializingRegionBasedStorage(File directory, Function<Runnable, Codec<R>> codecFactory, Function<Runnable, R> factory, DataFixer dataFixer, DataFixTypes dataFixTypes, boolean bl, HeightLimitView heightLimitView) {
        this.codecFactory = codecFactory;
        this.factory = factory;
        this.dataFixer = dataFixer;
        this.dataFixType = dataFixTypes;
        this.field_27240 = heightLimitView;
        this.worker = new StorageIoWorker(directory, bl, directory.getName());
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
        int i = ChunkSectionPos.getBlockCoord(pos.getSectionY());
        return this.field_27240.isOutOfHeightLimit(i);
    }

    protected R getOrCreate(long pos) {
        Optional<R> optional = this.get(pos);
        if (optional.isPresent()) {
            return optional.get();
        }
        R object = this.factory.apply(() -> this.onUpdate(pos));
        this.loadedElements.put(pos, Optional.of(object));
        return object;
    }

    private void loadDataAt(ChunkPos chunkPos) {
        this.update(chunkPos, NbtOps.INSTANCE, this.loadNbt(chunkPos));
    }

    @Nullable
    private CompoundTag loadNbt(ChunkPos pos) {
        try {
            return this.worker.getNbt(pos);
        } catch (IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", (Object)pos, (Object)iOException);
            return null;
        }
    }

    private <T> void update(ChunkPos pos, DynamicOps<T> dynamicOps, @Nullable T data) {
        if (data == null) {
            for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); ++i) {
                this.loadedElements.put(ChunkSectionPos.from(pos, i).asLong(), (Optional<R>)Optional.empty());
            }
        } else {
            int k;
            Dynamic<T> dynamic2 = new Dynamic<T>(dynamicOps, data);
            int j = SerializingRegionBasedStorage.getDataVersion(dynamic2);
            boolean bl = j != (k = SharedConstants.getGameVersion().getWorldVersion());
            Dynamic<T> dynamic22 = this.dataFixer.update(this.dataFixType.getTypeReference(), dynamic2, j, k);
            OptionalDynamic<T> optionalDynamic = dynamic22.get("Sections");
            for (int l = this.field_27240.method_32891(); l < this.field_27240.getTopSectionLimit(); ++l) {
                long m = ChunkSectionPos.from(pos, l).asLong();
                Optional optional = optionalDynamic.get(Integer.toString(l)).result().flatMap(dynamic -> this.codecFactory.apply(() -> this.onUpdate(m)).parse(dynamic).resultOrPartial(LOGGER::error));
                this.loadedElements.put(m, (Optional<R>)optional);
                optional.ifPresent(object -> {
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
        HashMap map = Maps.newHashMap();
        for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); ++i) {
            long l = ChunkSectionPos.from(chunkPos, i).asLong();
            this.unsavedElements.remove(l);
            Optional optional = (Optional)this.loadedElements.get(l);
            if (optional == null || !optional.isPresent()) continue;
            DataResult<T> dataResult = this.codecFactory.apply(() -> this.onUpdate(l)).encodeStart(dynamicOps, optional.get());
            String string = Integer.toString(i);
            dataResult.resultOrPartial(LOGGER::error).ifPresent(object -> map.put(dynamicOps.createString(string), object));
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
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void method_20436(ChunkPos chunkPos) {
        if (!this.unsavedElements.isEmpty()) {
            for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); ++i) {
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

