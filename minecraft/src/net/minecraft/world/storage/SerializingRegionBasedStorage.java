package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage<R, P> implements AutoCloseable {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String SECTIONS_KEY = "Sections";
	private final ChunkPosKeyedStorage storageAccess;
	private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
	private final Codec<P> codec;
	private final Function<R, P> serializer;
	private final BiFunction<P, Runnable, R> deserializer;
	private final Function<Runnable, R> factory;
	private final DynamicRegistryManager registryManager;
	private final ChunkErrorHandler errorHandler;
	protected final HeightLimitView world;
	private final LongSet loadedChunks = new LongOpenHashSet();
	private final Long2ObjectMap<CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>> pendingLoads = new Long2ObjectOpenHashMap<>();
	private final Object lock = new Object();

	public SerializingRegionBasedStorage(
		ChunkPosKeyedStorage storageAccess,
		Codec<P> codec,
		Function<R, P> serializer,
		BiFunction<P, Runnable, R> deserializer,
		Function<Runnable, R> factory,
		DynamicRegistryManager registryManager,
		ChunkErrorHandler errorHandler,
		HeightLimitView world
	) {
		this.storageAccess = storageAccess;
		this.codec = codec;
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.factory = factory;
		this.registryManager = registryManager;
		this.errorHandler = errorHandler;
		this.world = world;
	}

	protected void tick(BooleanSupplier shouldKeepTicking) {
		LongIterator longIterator = this.unsavedElements.iterator();

		while (longIterator.hasNext() && shouldKeepTicking.getAsBoolean()) {
			ChunkPos chunkPos = new ChunkPos(longIterator.nextLong());
			longIterator.remove();
			this.save(chunkPos);
		}

		this.tickPendingLoads();
	}

	private void tickPendingLoads() {
		synchronized (this.lock) {
			Iterator<Entry<CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>>> iterator = Long2ObjectMaps.fastIterator(this.pendingLoads);

			while (iterator.hasNext()) {
				Entry<CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>> entry = (Entry<CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>>)iterator.next();
				Optional<SerializingRegionBasedStorage.LoadResult<P>> optional = (Optional<SerializingRegionBasedStorage.LoadResult<P>>)((CompletableFuture)entry.getValue())
					.getNow(null);
				if (optional != null) {
					long l = entry.getLongKey();
					this.onLoad(new ChunkPos(l), (SerializingRegionBasedStorage.LoadResult<P>)optional.orElse(null));
					iterator.remove();
					this.loadedChunks.add(l);
				}
			}
		}
	}

	public void save() {
		if (!this.unsavedElements.isEmpty()) {
			this.unsavedElements.forEach(chunkPos -> this.save(new ChunkPos(chunkPos)));
			this.unsavedElements.clear();
		}
	}

	public boolean hasUnsavedElements() {
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
				this.loadAndWait(ChunkSectionPos.from(pos).toChunkPos());
				optional = this.getIfLoaded(pos);
				if (optional == null) {
					throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException());
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
			throw (IllegalArgumentException)Util.getFatalOrPause(new IllegalArgumentException("sectionPos out of bounds"));
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

	public CompletableFuture<?> load(ChunkPos chunkPos) {
		synchronized (this.lock) {
			long l = chunkPos.toLong();
			return this.loadedChunks.contains(l)
				? CompletableFuture.completedFuture(null)
				: this.pendingLoads
					.computeIfAbsent(
						l, (Long2ObjectFunction<? extends CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>>)(pos -> this.loadNbt(chunkPos))
					);
		}
	}

	private void loadAndWait(ChunkPos chunkPos) {
		long l = chunkPos.toLong();
		CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>> completableFuture;
		synchronized (this.lock) {
			if (!this.loadedChunks.add(l)) {
				return;
			}

			completableFuture = this.pendingLoads
				.computeIfAbsent(
					l, (Long2ObjectFunction<? extends CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>>>)(pos -> this.loadNbt(chunkPos))
				);
		}

		this.onLoad(chunkPos, (SerializingRegionBasedStorage.LoadResult<P>)((Optional)completableFuture.join()).orElse(null));
		synchronized (this.lock) {
			this.pendingLoads.remove(l);
		}
	}

	private CompletableFuture<Optional<SerializingRegionBasedStorage.LoadResult<P>>> loadNbt(ChunkPos chunkPos) {
		RegistryOps<NbtElement> registryOps = this.registryManager.getOps(NbtOps.INSTANCE);
		return this.storageAccess
			.read(chunkPos)
			.thenApplyAsync(
				chunkNbt -> chunkNbt.map(nbt -> SerializingRegionBasedStorage.LoadResult.fromNbt(this.codec, registryOps, nbt, this.storageAccess, this.world)),
				Util.getMainWorkerExecutor()
			)
			.exceptionally(throwable -> {
				if (throwable instanceof IOException iOException) {
					LOGGER.error("Error reading chunk {} data from disk", chunkPos, iOException);
					this.errorHandler.onChunkLoadFailure(iOException, this.storageAccess.getStorageKey(), chunkPos);
					return Optional.empty();
				} else {
					throw new CompletionException(throwable);
				}
			});
	}

	private void onLoad(ChunkPos chunkPos, @Nullable SerializingRegionBasedStorage.LoadResult<P> result) {
		if (result == null) {
			for (int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); i++) {
				this.loadedElements.put(chunkSectionPosAsLong(chunkPos, i), Optional.empty());
			}
		} else {
			boolean bl = result.versionChanged();

			for (int j = this.world.getBottomSectionCoord(); j <= this.world.getTopSectionCoord(); j++) {
				long l = chunkSectionPosAsLong(chunkPos, j);
				Optional<R> optional = Optional.ofNullable(result.sectionsByY.get(j)).map(section -> this.deserializer.apply(section, (Runnable)() -> this.onUpdate(l)));
				this.loadedElements.put(l, optional);
				optional.ifPresent(object -> {
					this.onLoad(l);
					if (bl) {
						this.onUpdate(l);
					}
				});
			}
		}
	}

	private void save(ChunkPos pos) {
		RegistryOps<NbtElement> registryOps = this.registryManager.getOps(NbtOps.INSTANCE);
		Dynamic<NbtElement> dynamic = this.serialize(pos, registryOps);
		NbtElement nbtElement = dynamic.getValue();
		if (nbtElement instanceof NbtCompound) {
			this.storageAccess.set(pos, (NbtCompound)nbtElement).exceptionally(throwable -> {
				this.errorHandler.onChunkSaveFailure(throwable, this.storageAccess.getStorageKey(), pos);
				return null;
			});
		} else {
			LOGGER.error("Expected compound tag, got {}", nbtElement);
		}
	}

	private <T> Dynamic<T> serialize(ChunkPos chunkPos, DynamicOps<T> ops) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for (int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); i++) {
			long l = chunkSectionPosAsLong(chunkPos, i);
			Optional<R> optional = this.loadedElements.get(l);
			if (optional != null && !optional.isEmpty()) {
				DataResult<T> dataResult = this.codec.encodeStart(ops, (P)this.serializer.apply(optional.get()));
				String string = Integer.toString(i);
				dataResult.resultOrPartial(LOGGER::error).ifPresent(value -> map.put(ops.createString(string), value));
			}
		}

		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("Sections"),
					ops.createMap(map),
					ops.createString("DataVersion"),
					ops.createInt(SharedConstants.getGameVersion().getSaveVersion().getId())
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
		if (optional != null && !optional.isEmpty()) {
			this.unsavedElements.add(ChunkPos.toLong(ChunkSectionPos.unpackX(pos), ChunkSectionPos.unpackZ(pos)));
		} else {
			LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos));
		}
	}

	static int getDataVersion(Dynamic<?> dynamic) {
		return dynamic.get("DataVersion").asInt(1945);
	}

	public void saveChunk(ChunkPos pos) {
		if (this.unsavedElements.remove(pos.toLong())) {
			this.save(pos);
		}
	}

	public void close() throws IOException {
		this.storageAccess.close();
	}

	static record LoadResult<T>(Int2ObjectMap<T> sectionsByY, boolean versionChanged) {

		public static <T> SerializingRegionBasedStorage.LoadResult<T> fromNbt(
			Codec<T> sectionCodec, DynamicOps<NbtElement> ops, NbtElement nbt, ChunkPosKeyedStorage storage, HeightLimitView world
		) {
			Dynamic<NbtElement> dynamic = new Dynamic<>(ops, nbt);
			int i = SerializingRegionBasedStorage.getDataVersion(dynamic);
			int j = SharedConstants.getGameVersion().getSaveVersion().getId();
			boolean bl = i != j;
			Dynamic<NbtElement> dynamic2 = storage.update(dynamic, i);
			OptionalDynamic<NbtElement> optionalDynamic = dynamic2.get("Sections");
			Int2ObjectMap<T> int2ObjectMap = new Int2ObjectOpenHashMap<>();

			for (int k = world.getBottomSectionCoord(); k <= world.getTopSectionCoord(); k++) {
				Optional<T> optional = optionalDynamic.get(Integer.toString(k))
					.result()
					.flatMap(section -> sectionCodec.parse(section).resultOrPartial(SerializingRegionBasedStorage.LOGGER::error));
				if (optional.isPresent()) {
					int2ObjectMap.put(k, (T)optional.get());
				}
			}

			return new SerializingRegionBasedStorage.LoadResult<>(int2ObjectMap, bl);
		}
	}
}
