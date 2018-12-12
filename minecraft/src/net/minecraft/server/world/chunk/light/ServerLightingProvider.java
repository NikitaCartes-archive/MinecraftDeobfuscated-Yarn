package net.minecraft.server.world.chunk.light;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntSupplier;
import net.minecraft.class_3846;
import net.minecraft.class_3898;
import net.minecraft.class_3899;
import net.minecraft.class_3900;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLightingProvider extends LightingProvider implements AutoCloseable {
	private static final Logger field_14020 = LogManager.getLogger();
	private final class_3846<Runnable> field_17255;
	private final ObjectList<Pair<ServerLightingProvider.class_3901, Runnable>> field_17256 = new ObjectArrayList<>();
	private final class_3898 field_17257;
	private final class_3900 field_17258;
	private final class_3846<Runnable> field_17259;
	private volatile int field_17260 = 5;

	public ServerLightingProvider(ChunkProvider chunkProvider, class_3898 arg, boolean bl, Executor executor, class_3900 arg2, class_3846<Runnable> arg3) {
		super(chunkProvider, true, bl);
		this.field_17257 = arg;
		this.field_17258 = arg2;
		this.field_17259 = arg3;
		this.field_17255 = class_3846.method_16902(executor, "lightWork");
	}

	public void close() {
		this.field_17255.close();
	}

	@Override
	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		throw new UnsupportedOperationException("Ran authomatically on a different thread!");
	}

	@Override
	public void method_15560(BlockPos blockPos, int i) {
		throw new UnsupportedOperationException("Ran authomatically on a different thread!");
	}

	@Override
	public void enqueueLightUpdate(BlockPos blockPos) {
		this.method_17308(blockPos.getX() >> 4, blockPos.getZ() >> 4, ServerLightingProvider.class_3901.field_17262, () -> super.enqueueLightUpdate(blockPos));
	}

	@Override
	public void scheduleChunkLightUpdate(int i, int j, int k, boolean bl) {
		this.method_17307(i, k, () -> 0, ServerLightingProvider.class_3901.field_17261, () -> super.scheduleChunkLightUpdate(i, j, k, bl));
	}

	@Override
	public void method_15557(int i, int j, boolean bl) {
		this.method_17308(i, j, ServerLightingProvider.class_3901.field_17261, () -> super.method_15557(i, j, bl));
	}

	@Override
	public void setSection(LightType lightType, int i, int j, int k, ChunkNibbleArray chunkNibbleArray) {
		this.method_17308(i, k, ServerLightingProvider.class_3901.field_17261, () -> super.setSection(lightType, i, j, k, chunkNibbleArray));
	}

	private void method_17308(int i, int j, ServerLightingProvider.class_3901 arg, Runnable runnable) {
		this.method_17307(i, j, () -> Math.min(this.field_17257.method_17246(ChunkPos.toLong(i, j)), class_3899.field_17241 - 1), arg, runnable);
	}

	private void method_17307(int i, int j, IntSupplier intSupplier, ServerLightingProvider.class_3901 arg, Runnable runnable) {
		this.field_17258.method_17282(this.field_17259, () -> this.field_17255.method_16901(() -> {
				this.field_17256.add(Pair.of(arg, runnable));
				if (this.field_17256.size() >= this.field_17260) {
					this.method_14277();
				}
			}), new ChunkPos(i, j), intSupplier);
	}

	public CompletableFuture<Chunk> method_17310(Chunk chunk, int i, int j, boolean bl) {
		this.method_17308(i, j, ServerLightingProvider.class_3901.field_17261, () -> {
			if (!bl) {
				super.method_15557(i, j, false);
			}

			ChunkSection[] chunkSections = chunk.getSectionArray();

			for (int k = 0; k < 16; k++) {
				ChunkSection chunkSection = chunkSections[k];
				boolean bl2 = chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty();
				if (!bl2) {
					super.scheduleChunkLightUpdate(i, k, j, false);
				}
			}

			if (!bl) {
				chunk.method_12018().forEach(blockPos -> super.method_15560(blockPos, chunk.getLuminance(blockPos)));
			}
		});
		return CompletableFuture.supplyAsync(() -> chunk, runnable -> this.method_17308(i, j, ServerLightingProvider.class_3901.field_17262, runnable));
	}

	public void method_17303() {
		this.field_17255.method_16901(this::method_14277);
	}

	private void method_14277() {
		if (!this.field_17256.isEmpty() || super.method_15561()) {
			int i = Math.min(this.field_17256.size(), this.field_17260);
			ObjectListIterator<Pair<ServerLightingProvider.class_3901, Runnable>> objectListIterator = this.field_17256.iterator();

			int j;
			for (j = 0; objectListIterator.hasNext() && j < i; j++) {
				Pair<ServerLightingProvider.class_3901, Runnable> pair = (Pair<ServerLightingProvider.class_3901, Runnable>)objectListIterator.next();
				if (pair.getFirst() == ServerLightingProvider.class_3901.field_17261) {
					pair.getSecond().run();
				}
			}

			objectListIterator.back(j);
			super.doLightUpdates(Integer.MAX_VALUE, true, true);

			for (int var5 = 0; objectListIterator.hasNext() && var5 < i; var5++) {
				Pair<ServerLightingProvider.class_3901, Runnable> pair = (Pair<ServerLightingProvider.class_3901, Runnable>)objectListIterator.next();
				if (pair.getFirst() == ServerLightingProvider.class_3901.field_17262) {
					pair.getSecond().run();
				}

				objectListIterator.remove();
			}

			this.method_17303();
		}
	}

	public void method_17304(int i) {
		this.field_17260 = i;
	}

	static enum class_3901 {
		field_17261,
		field_17262;
	}
}
