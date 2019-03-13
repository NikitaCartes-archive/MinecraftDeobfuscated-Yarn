package net.minecraft;

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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4180<R extends class_4213> extends RegionFileCache {
	private static final Logger field_18691 = LogManager.getLogger();
	private final Long2ObjectMap<Optional<R>> field_18692 = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet field_18693 = new LongLinkedOpenHashSet();
	private final BiFunction<Runnable, Dynamic<?>, R> field_18694;
	private final Function<Runnable, R> field_18695;

	public class_4180(File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function) {
		super(file);
		this.field_18694 = biFunction;
		this.field_18695 = function;
	}

	protected void method_19290(BooleanSupplier booleanSupplier) {
		while (!this.field_18693.isEmpty() && booleanSupplier.getAsBoolean()) {
			ChunkPos chunkPos = ChunkSectionPos.from(this.field_18693.firstLong()).toChunkPos();
			CompoundTag compoundTag = new CompoundTag();

			for (int i = 0; i < 16; i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				this.field_18693.remove(l);
				Optional<R> optional = this.field_18692.get(l);
				if (optional != null && optional.isPresent()) {
					compoundTag.method_10566(Integer.toString(i), ((class_4213)optional.get()).method_19508(NbtOps.INSTANCE));
				}
			}

			try {
				this.method_17910(chunkPos, compoundTag);
			} catch (IOException var8) {
				field_18691.error("Error writing data to disk", (Throwable)var8);
			}
		}
	}

	@Nullable
	protected Optional<R> method_19293(long l) {
		return this.field_18692.get(l);
	}

	protected Optional<R> method_19294(long l) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(l);
		if (this.method_19292(chunkSectionPos)) {
			return Optional.empty();
		} else {
			Optional<R> optional = this.method_19293(l);
			if (optional != null) {
				return optional;
			} else {
				this.method_19289(chunkSectionPos.toChunkPos());
				optional = this.method_19293(l);
				if (optional == null) {
					throw new IllegalStateException();
				} else {
					return optional;
				}
			}
		}
	}

	protected boolean method_19292(ChunkSectionPos chunkSectionPos) {
		return World.isHeightInvalid(ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkY()));
	}

	protected R method_19295(long l) {
		Optional<R> optional = this.method_19294(l);
		if (optional.isPresent()) {
			return (R)optional.get();
		} else {
			R lv = (R)this.field_18695.apply((Runnable)() -> this.method_19288(l));
			this.field_18692.put(l, Optional.of(lv));
			return lv;
		}
	}

	private void method_19289(ChunkPos chunkPos) {
		try {
			CompoundTag compoundTag = this.method_17911(chunkPos);

			for (int i = 0; i < 16; i++) {
				long l = ChunkSectionPos.from(chunkPos, i).asLong();
				this.field_18692.put(l, Optional.empty());
				if (compoundTag != null) {
					try {
						Tag tag = compoundTag.method_10580(Integer.toString(i));
						if (tag != null) {
							this.field_18692.put(l, Optional.of(this.field_18694.apply((Runnable)() -> this.method_19288(l), new Dynamic<>(NbtOps.INSTANCE, tag))));
							this.method_19291(l);
						}
					} catch (NumberFormatException var7) {
						field_18691.error("Error reading data from disk", (Throwable)var7);
					}
				}
			}
		} catch (IOException var8) {
			field_18691.error("Error reading data from disk", (Throwable)var8);
		}
	}

	protected void method_19291(long l) {
	}

	protected void method_19288(long l) {
		Optional<R> optional = this.field_18692.get(l);
		if (optional != null && optional.isPresent()) {
			this.field_18693.add(l);
		} else {
			field_18691.warn("No data for position: {}", ChunkSectionPos.from(l));
		}
	}
}
