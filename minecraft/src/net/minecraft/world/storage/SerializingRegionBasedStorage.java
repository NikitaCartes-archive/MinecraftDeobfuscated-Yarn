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
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerializingRegionBasedStorage<R> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final StorageIoWorker worker;
	private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
	private final Function<Runnable, Codec<R>> codecFactory;
	private final Function<Runnable, R> factory;
	private final DataFixer dataFixer;
	private final DataFixTypes dataFixType;
	protected final HeightLimitView field_27240;

	public SerializingRegionBasedStorage(
		File directory,
		Function<Runnable, Codec<R>> codecFactory,
		Function<Runnable, R> factory,
		DataFixer dataFixer,
		DataFixTypes dataFixTypes,
		boolean bl,
		HeightLimitView heightLimitView
	) {
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
		return this.loadedElements.get(pos);
	}

	protected Optional<R> get(long pos) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
		if (this.isPosInvalid(chunkSectionPos)) {
			return Optional.empty();
		} else {
			Optional<R> optional = this.getIfLoaded(pos);
			if (optional != null) {
				return optional;
			} else {
				this.loadDataAt(chunkSectionPos.toChunkPos());
				optional = this.getIfLoaded(pos);
				if (optional == null) {
					throw (IllegalStateException)Util.throwOrPause(new IllegalStateException());
				} else {
					return optional;
				}
			}
		}
	}

	protected boolean isPosInvalid(ChunkSectionPos pos) {
		int i = ChunkSectionPos.getBlockCoord(pos.getSectionY());
		return this.field_27240.isOutOfHeightLimit(i);
	}

	protected R getOrCreate(long pos) {
		Optional<R> optional = this.get(pos);
		if (optional.isPresent()) {
			return (R)optional.get();
		} else {
			R object = (R)this.factory.apply((Runnable)() -> this.onUpdate(pos));
			this.loadedElements.put(pos, Optional.of(object));
			return object;
		}
	}

	private void loadDataAt(ChunkPos chunkPos) {
		this.update(chunkPos, NbtOps.INSTANCE, this.loadNbt(chunkPos));
	}

	@Nullable
	private CompoundTag loadNbt(ChunkPos pos) {
		try {
			return this.worker.getNbt(pos);
		} catch (IOException var3) {
			LOGGER.error("Error reading chunk {} data from disk", pos, var3);
			return null;
		}
	}

	private <T> void update(ChunkPos pos, DynamicOps<T> dynamicOps, @Nullable T data) {
		if (data == null) {
			for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); i++) {
				this.loadedElements.put(ChunkSectionPos.from(pos, i).asLong(), Optional.empty());
			}
		} else {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, data);
			int j = getDataVersion(dynamic);
			int k = SharedConstants.getGameVersion().getWorldVersion();
			boolean bl = j != k;
			Dynamic<T> dynamic2 = this.dataFixer.update(this.dataFixType.getTypeReference(), dynamic, j, k);
			OptionalDynamic<T> optionalDynamic = dynamic2.get("Sections");

			for (int l = this.field_27240.method_32891(); l < this.field_27240.getTopSectionLimit(); l++) {
				long m = ChunkSectionPos.from(pos, l).asLong();
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
		Dynamic<Tag> dynamic = this.method_20367(chunkPos, NbtOps.INSTANCE);
		Tag tag = dynamic.getValue();
		if (tag instanceof CompoundTag) {
			this.worker.setResult(chunkPos, (CompoundTag)tag);
		} else {
			LOGGER.error("Expected compound tag, got {}", tag);
		}
	}

	private <T> Dynamic<T> method_20367(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); i++) {
			long l = ChunkSectionPos.from(chunkPos, i).asLong();
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

	public void method_20436(ChunkPos chunkPos) {
		if (!this.unsavedElements.isEmpty()) {
			for (int i = this.field_27240.method_32891(); i < this.field_27240.getTopSectionLimit(); i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				if (this.unsavedElements.contains(l)) {
					this.save(chunkPos);
					return;
				}
			}
		}
	}

	public void close() throws IOException {
		this.worker.close();
	}
}
