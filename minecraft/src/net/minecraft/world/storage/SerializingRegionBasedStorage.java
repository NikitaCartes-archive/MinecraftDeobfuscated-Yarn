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
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerializingRegionBasedStorage<R extends DynamicSerializable> extends RegionBasedStorage {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
	private final BiFunction<Runnable, Dynamic<?>, R> deserializer;
	private final Function<Runnable, R> factory;
	private final DataFixer dataFixer;
	private final DataFixTypes dataFixType;

	public SerializingRegionBasedStorage(
		File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function, DataFixer dataFixer, DataFixTypes dataFixTypes
	) {
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
		return this.loadedElements.get(l);
	}

	protected Optional<R> get(long l) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(l);
		if (this.isPosInvalid(chunkSectionPos)) {
			return Optional.empty();
		} else {
			Optional<R> optional = this.getIfLoaded(l);
			if (optional != null) {
				return optional;
			} else {
				this.loadDataAt(chunkSectionPos.toChunkPos());
				optional = this.getIfLoaded(l);
				if (optional == null) {
					throw new IllegalStateException();
				} else {
					return optional;
				}
			}
		}
	}

	protected boolean isPosInvalid(ChunkSectionPos chunkSectionPos) {
		return World.isHeightInvalid(ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkY()));
	}

	protected R getOrCreate(long l) {
		Optional<R> optional = this.get(l);
		if (optional.isPresent()) {
			return (R)optional.get();
		} else {
			R dynamicSerializable = (R)this.factory.apply((Runnable)() -> this.onUpdate(l));
			this.loadedElements.put(l, Optional.of(dynamicSerializable));
			return dynamicSerializable;
		}
	}

	private void loadDataAt(ChunkPos chunkPos) {
		try {
			CompoundTag compoundTag = this.getTagAt(chunkPos);
			this.method_20368(chunkPos, NbtOps.INSTANCE, compoundTag);
		} catch (IOException var3) {
			LOGGER.error("Error reading data from disk", (Throwable)var3);
		}
	}

	private <T> void method_20368(ChunkPos chunkPos, DynamicOps<T> dynamicOps, @Nullable T object) {
		if (object == null) {
			for (int i = 0; i < 16; i++) {
				this.loadedElements.put(ChunkSectionPos.from(chunkPos, i).asLong(), Optional.empty());
			}
		} else {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, object);
			int j = method_20369(dynamic);
			int k = SharedConstants.getGameVersion().getWorldVersion();
			boolean bl = j != k;
			Dynamic<T> dynamic2 = this.dataFixer.update(this.dataFixType.getTypeReference(), dynamic, j, k);
			OptionalDynamic<T> optionalDynamic = dynamic2.get("Sections");

			for (int l = 0; l < 16; l++) {
				long m = ChunkSectionPos.from(chunkPos, l).asLong();
				Optional<R> optional = optionalDynamic.get(Integer.toString(l))
					.get()
					.map(dynamicx -> (DynamicSerializable)this.deserializer.apply((Runnable)() -> this.onUpdate(m), dynamicx));
				this.loadedElements.put(m, optional);
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
			} catch (IOException var5) {
				LOGGER.error("Error writing data to disk", (Throwable)var5);
			}
		} else {
			LOGGER.error("Expected compound tag, got {}", tag);
		}
	}

	private <T> Dynamic<T> method_20367(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for (int i = 0; i < 16; i++) {
			long l = ChunkSectionPos.from(chunkPos, i).asLong();
			this.unsavedElements.remove(l);
			Optional<R> optional = this.loadedElements.get(l);
			if (optional != null && optional.isPresent()) {
				map.put(dynamicOps.createString(Integer.toString(i)), ((DynamicSerializable)optional.get()).serialize(dynamicOps));
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

	protected void onLoad(long l) {
	}

	protected void onUpdate(long l) {
		Optional<R> optional = this.loadedElements.get(l);
		if (optional != null && optional.isPresent()) {
			this.unsavedElements.add(l);
		} else {
			LOGGER.warn("No data for position: {}", ChunkSectionPos.from(l));
		}
	}

	private static int method_20369(Dynamic<?> dynamic) {
		return ((Number)dynamic.get("DataVersion").asNumber().orElse(1945)).intValue();
	}

	public void method_20436(ChunkPos chunkPos) {
		if (!this.unsavedElements.isEmpty()) {
			for (int i = 0; i < 16; i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				if (this.unsavedElements.contains(l)) {
					this.method_20370(chunkPos);
					return;
				}
			}
		}
	}
}
