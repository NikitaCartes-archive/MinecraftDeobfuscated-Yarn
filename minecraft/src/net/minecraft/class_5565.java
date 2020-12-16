package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5565 implements class_5571<Entity> {
	private static final Logger field_27232 = LogManager.getLogger();
	private final ServerWorld field_27233;
	private final StorageIoWorker field_27234;
	private final LongSet field_27235 = new LongOpenHashSet();
	private final Executor field_27236;
	protected final DataFixer field_27231;

	public class_5565(ServerWorld serverWorld, File file, DataFixer dataFixer, boolean bl, Executor executor) {
		this.field_27233 = serverWorld;
		this.field_27231 = dataFixer;
		this.field_27236 = executor;
		this.field_27234 = new StorageIoWorker(file, bl, "entities");
	}

	@Override
	public CompletableFuture<class_5566<Entity>> method_31759(ChunkPos chunkPos) {
		return this.field_27235.contains(chunkPos.toLong())
			? CompletableFuture.completedFuture(method_31735(chunkPos))
			: this.field_27234.method_31738(chunkPos).thenApplyAsync(compoundTag -> {
				if (compoundTag == null) {
					this.field_27235.add(chunkPos.toLong());
					return method_31735(chunkPos);
				} else {
					try {
						ChunkPos chunkPos2 = getChunkPos(compoundTag);
						if (!Objects.equals(chunkPos, chunkPos2)) {
							field_27232.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", chunkPos, chunkPos, chunkPos2);
						}
					} catch (Exception var6) {
						field_27232.warn("Failed to parse chunk {} position info", chunkPos, var6);
					}

					CompoundTag compoundTag2 = this.method_31737(compoundTag);
					ListTag listTag = compoundTag2.getList("Entities", 10);
					List<Entity> list = (List<Entity>)EntityType.method_31489(listTag, this.field_27233).collect(ImmutableList.toImmutableList());
					return new class_5566(chunkPos, list);
				}
			}, this.field_27236);
	}

	private static ChunkPos getChunkPos(CompoundTag tag) {
		int[] is = tag.getIntArray("Position");
		return new ChunkPos(is[0], is[1]);
	}

	private static void putChunkPos(CompoundTag tag, ChunkPos chunkPos) {
		tag.put("Position", new IntArrayTag(new int[]{chunkPos.x, chunkPos.z}));
	}

	private static class_5566<Entity> method_31735(ChunkPos chunkPos) {
		return new class_5566<>(chunkPos, ImmutableList.of());
	}

	@Override
	public void method_31760(class_5566<Entity> arg) {
		ChunkPos chunkPos = arg.method_31741();
		if (arg.method_31743()) {
			if (this.field_27235.add(chunkPos.toLong())) {
				this.field_27234.setResult(chunkPos, null);
			}
		} else {
			ListTag listTag = new ListTag();
			arg.method_31742().forEach(entity -> {
				CompoundTag compoundTagx = new CompoundTag();
				if (entity.saveToTag(compoundTagx)) {
					listTag.add(compoundTagx);
				}
			});
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
			compoundTag.put("Entities", listTag);
			putChunkPos(compoundTag, chunkPos);
			this.field_27234.setResult(chunkPos, compoundTag).exceptionally(throwable -> {
				field_27232.error("Failed to store chunk {}", chunkPos, throwable);
				return null;
			});
			this.field_27235.remove(chunkPos.toLong());
		}
	}

	@Override
	public void method_31758() {
		this.field_27234.completeAll().join();
	}

	private CompoundTag method_31737(CompoundTag compoundTag) {
		int i = getDataVersion(compoundTag);
		return NbtHelper.update(this.field_27231, DataFixTypes.ENTITY_CHUNK, compoundTag, i);
	}

	public static int getDataVersion(CompoundTag tag) {
		return tag.contains("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
	}

	@Override
	public void close() throws IOException {
		this.field_27234.close();
	}
}
