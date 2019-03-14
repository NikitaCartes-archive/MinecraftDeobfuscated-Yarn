package net.minecraft.world.storage;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
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

	public SerializingRegionBasedStorage(File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function) {
		super(file);
		this.deserializer = biFunction;
		this.factory = function;
	}

	protected void tick(BooleanSupplier booleanSupplier) {
		while (!this.unsavedElements.isEmpty() && booleanSupplier.getAsBoolean()) {
			ChunkPos chunkPos = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
			CompoundTag compoundTag = new CompoundTag();

			for (int i = 0; i < 16; i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				this.unsavedElements.remove(l);
				Optional<R> optional = this.loadedElements.get(l);
				if (optional != null && optional.isPresent()) {
					compoundTag.put(Integer.toString(i), ((DynamicSerializable)optional.get()).serialize(NbtOps.INSTANCE));
				}
			}

			try {
				this.setTagAt(chunkPos, compoundTag);
			} catch (IOException var8) {
				LOGGER.error("Error writing data to disk", (Throwable)var8);
			}
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

			for (int i = 0; i < 16; i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				this.loadedElements.put(l, Optional.empty());
				if (compoundTag != null) {
					try {
						Tag tag = compoundTag.getTag(Integer.toString(i));
						if (tag != null) {
							this.loadedElements.put(l, Optional.of(this.deserializer.apply((Runnable)() -> this.onUpdate(l), new Dynamic<>(NbtOps.INSTANCE, tag))));
							this.onLoad(l);
						}
					} catch (NumberFormatException var7) {
						LOGGER.error("Error reading data from disk", (Throwable)var7);
					}
				}
			}
		} catch (IOException var8) {
			LOGGER.error("Error reading data from disk", (Throwable)var8);
		}
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
}
