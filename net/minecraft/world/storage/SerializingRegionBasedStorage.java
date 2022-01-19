/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.storage.StorageIoWorker;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage<R>
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SECTIONS_KEY = "Sections";
    private final StorageIoWorker worker;
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<Optional<R>>();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codecFactory;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixTypes;
    protected final HeightLimitView world;

    public SerializingRegionBasedStorage(Path path, Function<Runnable, Codec<R>> codecFactory, Function<Runnable, R> factory, DataFixer dataFixer, DataFixTypes dataFixTypes, boolean dsync, HeightLimitView world) {
        this.codecFactory = codecFactory;
        this.factory = factory;
        this.dataFixer = dataFixer;
        this.dataFixTypes = dataFixTypes;
        this.world = world;
        this.worker = new StorageIoWorker(path, dsync, path.getFileName().toString());
    }

    protected void tick(BooleanSupplier shouldKeepTicking) {
        while (this.method_40020() && shouldKeepTicking.getAsBoolean()) {
            ChunkPos chunkPos = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
            this.save(chunkPos);
        }
    }

    public boolean method_40020() {
        return !this.unsavedElements.isEmpty();
    }

    @Nullable
    protected Optional<R> getIfLoaded(long pos) {
        return (Optional)this.loadedElements.get(pos);
    }

    protected Optional<R> get(long pos) {
        if (this.isPosInvalid(pos)) {
            return Optional.empty();
        }
        Optional<R> optional = this.getIfLoaded(pos);
        if (optional != null) {
            return optional;
        }
        this.loadDataAt(ChunkSectionPos.from(pos).toChunkPos());
        optional = this.getIfLoaded(pos);
        if (optional == null) {
            throw Util.throwOrPause(new IllegalStateException());
        }
        return optional;
    }

    protected boolean isPosInvalid(long pos) {
        int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(pos));
        return this.world.isOutOfHeightLimit(i);
    }

    protected R getOrCreate(long pos) {
        if (this.isPosInvalid(pos)) {
            throw Util.throwOrPause(new IllegalArgumentException("sectionPos out of bounds"));
        }
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
    private NbtCompound loadNbt(ChunkPos pos) {
        try {
            return this.worker.getNbt(pos);
        } catch (IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", (Object)pos, (Object)iOException);
            return null;
        }
    }

    private <T> void update(ChunkPos pos, DynamicOps<T> dynamicOps, @Nullable T data) {
        if (data == null) {
            for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
                this.loadedElements.put(SerializingRegionBasedStorage.chunkSectionPosAsLong(pos, i), (Optional<R>)Optional.empty());
            }
        } else {
            int k;
            Dynamic<T> dynamic2 = new Dynamic<T>(dynamicOps, data);
            int j = SerializingRegionBasedStorage.getDataVersion(dynamic2);
            boolean bl = j != (k = SharedConstants.getGameVersion().getWorldVersion());
            Dynamic<T> dynamic22 = this.dataFixer.update(this.dataFixTypes.getTypeReference(), dynamic2, j, k);
            OptionalDynamic<T> optionalDynamic = dynamic22.get(SECTIONS_KEY);
            for (int l = this.world.getBottomSectionCoord(); l < this.world.getTopSectionCoord(); ++l) {
                long m = SerializingRegionBasedStorage.chunkSectionPosAsLong(pos, l);
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
        Dynamic<NbtElement> dynamic = this.method_20367(chunkPos, NbtOps.INSTANCE);
        NbtElement nbtElement = dynamic.getValue();
        if (nbtElement instanceof NbtCompound) {
            this.worker.setResult(chunkPos, (NbtCompound)nbtElement);
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)nbtElement);
        }
    }

    private <T> Dynamic<T> method_20367(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
        HashMap map = Maps.newHashMap();
        for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
            long l = SerializingRegionBasedStorage.chunkSectionPosAsLong(chunkPos, i);
            this.unsavedElements.remove(l);
            Optional optional = (Optional)this.loadedElements.get(l);
            if (optional == null || !optional.isPresent()) continue;
            DataResult<T> dataResult = this.codecFactory.apply(() -> this.onUpdate(l)).encodeStart(dynamicOps, optional.get());
            String string = Integer.toString(i);
            dataResult.resultOrPartial(LOGGER::error).ifPresent(object -> map.put(dynamicOps.createString(string), object));
        }
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString(SECTIONS_KEY), dynamicOps.createMap(map), dynamicOps.createString("DataVersion"), dynamicOps.createInt(SharedConstants.getGameVersion().getWorldVersion()))));
    }

    private static long chunkSectionPosAsLong(ChunkPos chunkPos, int y) {
        return ChunkSectionPos.asLong(chunkPos.x, y, chunkPos.z);
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

    public void saveChunk(ChunkPos pos) {
        if (this.method_40020()) {
            for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
                long l = SerializingRegionBasedStorage.chunkSectionPosAsLong(pos, i);
                if (!this.unsavedElements.contains(l)) continue;
                this.save(pos);
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

