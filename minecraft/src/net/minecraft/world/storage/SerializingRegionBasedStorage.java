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
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage<R> implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String SECTIONS_KEY = "Sections";
	private final StorageIoWorker worker;
	private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
	private final Function<Runnable, Codec<R>> codecFactory;
	private final Function<Runnable, R> factory;
	private final DataFixer dataFixer;
	private final DataFixTypes dataFixTypes;
	protected final HeightLimitView world;

	public SerializingRegionBasedStorage(
		Path path,
		Function<Runnable, Codec<R>> codecFactory,
		Function<Runnable, R> factory,
		DataFixer dataFixer,
		DataFixTypes dataFixTypes,
		boolean dsync,
		HeightLimitView world
	) {
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
		return this.loadedElements.get(pos);
	}

	protected Optional<R> get(long pos) {
		if (this.isPosInvalid(pos)) {
			return Optional.empty();
		} else {
			Optional<R> optional = this.getIfLoaded(pos);
			if (optional != null) {
				return optional;
			} else {
				this.loadDataAt(ChunkSectionPos.from(pos).toChunkPos());
				optional = this.getIfLoaded(pos);
				if (optional == null) {
					throw (IllegalStateException)Util.throwOrPause(new IllegalStateException());
				} else {
					return optional;
				}
			}
		}
	}

	protected boolean isPosInvalid(long pos) {
		int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(pos));
		return this.world.isOutOfHeightLimit(i);
	}

	protected R getOrCreate(long pos) {
		if (this.isPosInvalid(pos)) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException("sectionPos out of bounds"));
		} else {
			Optional<R> optional = this.get(pos);
			if (optional.isPresent()) {
				return (R)optional.get();
			} else {
				R object = (R)this.factory.apply((Runnable)() -> this.onUpdate(pos));
				this.loadedElements.put(pos, Optional.of(object));
				return object;
			}
		}
	}

	private void loadDataAt(ChunkPos chunkPos) {
		this.update(chunkPos, NbtOps.INSTANCE, this.loadNbt(chunkPos));
	}

	@Nullable
	private NbtCompound loadNbt(ChunkPos pos) {
		try {
			return this.worker.getNbt(pos);
		} catch (IOException var3) {
			LOGGER.error("Error reading chunk {} data from disk", pos, var3);
			return null;
		}
	}

	private <T> void update(ChunkPos pos, DynamicOps<T> dynamicOps, @Nullable T data) {
		if (data == null) {
			for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); i++) {
				this.loadedElements.put(chunkSectionPosAsLong(pos, i), Optional.empty());
			}
		} else {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, data);
			int j = getDataVersion(dynamic);
			int k = SharedConstants.getGameVersion().getWorldVersion();
			boolean bl = j != k;
			Dynamic<T> dynamic2 = this.dataFixer.update(this.dataFixTypes.getTypeReference(), dynamic, j, k);
			OptionalDynamic<T> optionalDynamic = dynamic2.get("Sections");

			for (int l = this.world.getBottomSectionCoord(); l < this.world.getTopSectionCoord(); l++) {
				long m = chunkSectionPosAsLong(pos, l);
				Optional<R> optional = optionalDynamic.get(Integer.toString(l))
					.result()
					.flatMap(dynamicx -> ((Codec)this.codecFactory.apply((Runnable)() -> this.onUpdate(m))).parse(dynamicx).resultOrPartial(LOGGER::error));
				this.loadedElements.put(m, optional);
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
			LOGGER.error("Expected compound tag, got {}", nbtElement);
		}
	}

	private <T> Dynamic<T> method_20367(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); i++) {
			long l = chunkSectionPosAsLong(chunkPos, i);
			this.unsavedElements.remove(l);
			Optional<R> optional = this.loadedElements.get(l);
			if (optional != null && optional.isPresent()) {
				DataResult<T> dataResult = ((Codec)this.codecFactory.apply((Runnable)() -> this.onUpdate(l))).encodeStart(dynamicOps, optional.get());
				String string = Integer.toString(i);
				dataResult.resultOrPartial(LOGGER::error).ifPresent(object -> map.put(dynamicOps.createString(string), object));
			}
		}

		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Sections"),
					dynamicOps.createMap(map),
					dynamicOps.createString("DataVersion"),
					dynamicOps.createInt(SharedConstants.getGameVersion().getWorldVersion())
				)
			)
		);
	}

	private static long chunkSectionPosAsLong(ChunkPos chunkPos, int y) {
		return ChunkSectionPos.asLong(chunkPos.x, y, chunkPos.z);
	}

	protected void onLoad(long pos) {
	}

	protected void onUpdate(long pos) {
		Optional<R> optional = this.loadedElements.get(pos);
		if (optional != null && optional.isPresent()) {
			this.unsavedElements.add(pos);
		} else {
			LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos));
		}
	}

	private static int getDataVersion(Dynamic<?> dynamic) {
		return dynamic.get("DataVersion").asInt(1945);
	}

	public void saveChunk(ChunkPos pos) {
		if (this.method_40020()) {
			for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); i++) {
				long l = chunkSectionPosAsLong(pos, i);
				if (this.unsavedElements.contains(l)) {
					this.save(pos);
					return;
				}
			}
		}
	}

	public void close() throws IOException {
		this.worker.close();
	}
}
