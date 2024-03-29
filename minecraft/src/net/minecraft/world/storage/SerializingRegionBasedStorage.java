package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage<R> implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String SECTIONS_KEY = "Sections";
	private final ChunkPosKeyedStorage storageAccess;
	private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap();
	private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
	private final Function<Runnable, Codec<R>> codecFactory;
	private final Function<Runnable, R> factory;
	private final DynamicRegistryManager registryManager;
	protected final HeightLimitView world;

	public SerializingRegionBasedStorage(
		ChunkPosKeyedStorage storageAccess,
		Function<Runnable, Codec<R>> codecFactory,
		Function<Runnable, R> factory,
		DynamicRegistryManager registryManager,
		HeightLimitView world
	) {
		this.storageAccess = storageAccess;
		this.codecFactory = codecFactory;
		this.factory = factory;
		this.registryManager = registryManager;
		this.world = world;
	}

	protected void tick(BooleanSupplier shouldKeepTicking) {
		while(this.hasUnsavedElements() && shouldKeepTicking.getAsBoolean()) {
			ChunkPos chunkPos = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
			this.save(chunkPos);
		}
	}

	public boolean hasUnsavedElements() {
		return !this.unsavedElements.isEmpty();
	}

	@Nullable
	protected Optional<R> getIfLoaded(long pos) {
		return (Optional<R>)this.loadedElements.get(pos);
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

	private void loadDataAt(ChunkPos pos) {
		Optional<NbtCompound> optional = (Optional)this.loadNbt(pos).join();
		RegistryOps<NbtElement> registryOps = this.registryManager.getOps(NbtOps.INSTANCE);
		this.update(pos, registryOps, (NbtCompound)optional.orElse(null));
	}

	private CompletableFuture<Optional<NbtCompound>> loadNbt(ChunkPos pos) {
		return this.storageAccess.read(pos).exceptionally(throwable -> {
			if (throwable instanceof IOException iOException) {
				LOGGER.error("Error reading chunk {} data from disk", pos, iOException);
				return Optional.empty();
			} else {
				throw new CompletionException(throwable);
			}
		});
	}

	private void update(ChunkPos pos, RegistryOps<NbtElement> ops, @Nullable NbtCompound nbt) {
		if (nbt == null) {
			for(int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
				this.loadedElements.put(chunkSectionPosAsLong(pos, i), Optional.empty());
			}
		} else {
			Dynamic<NbtElement> dynamic = new Dynamic<>(ops, nbt);
			int j = getDataVersion(dynamic);
			int k = SharedConstants.getGameVersion().getSaveVersion().getId();
			boolean bl = j != k;
			Dynamic<NbtElement> dynamic2 = this.storageAccess.update(dynamic, j);
			OptionalDynamic<NbtElement> optionalDynamic = dynamic2.get("Sections");

			for(int l = this.world.getBottomSectionCoord(); l < this.world.getTopSectionCoord(); ++l) {
				long m = chunkSectionPosAsLong(pos, l);
				Optional<R> optional = optionalDynamic.get(Integer.toString(l))
					.result()
					.flatMap(dynamicx -> ((Codec)this.codecFactory.apply((Runnable)() -> this.onUpdate(m))).parse(dynamicx).resultOrPartial(LOGGER::error));
				this.loadedElements.put(m, optional);
				optional.ifPresent(sections -> {
					this.onLoad(m);
					if (bl) {
						this.onUpdate(m);
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
			this.storageAccess.set(pos, (NbtCompound)nbtElement);
		} else {
			LOGGER.error("Expected compound tag, got {}", nbtElement);
		}
	}

	private <T> Dynamic<T> serialize(ChunkPos chunkPos, DynamicOps<T> ops) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for(int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
			long l = chunkSectionPosAsLong(chunkPos, i);
			this.unsavedElements.remove(l);
			Optional<R> optional = (Optional)this.loadedElements.get(l);
			if (optional != null && !optional.isEmpty()) {
				DataResult<T> dataResult = ((Codec)this.codecFactory.apply((Runnable)() -> this.onUpdate(l))).encodeStart(ops, optional.get());
				String string = Integer.toString(i);
				dataResult.resultOrPartial(LOGGER::error).ifPresent(object -> map.put(ops.createString(string), object));
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
		Optional<R> optional = (Optional)this.loadedElements.get(pos);
		if (optional != null && !optional.isEmpty()) {
			this.unsavedElements.add(pos);
		} else {
			LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos));
		}
	}

	private static int getDataVersion(Dynamic<?> dynamic) {
		return dynamic.get("DataVersion").asInt(1945);
	}

	public void saveChunk(ChunkPos pos) {
		if (this.hasUnsavedElements()) {
			for(int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); ++i) {
				long l = chunkSectionPosAsLong(pos, i);
				if (this.unsavedElements.contains(l)) {
					this.save(pos);
					return;
				}
			}
		}
	}

	public void close() throws IOException {
		this.storageAccess.close();
	}
}
