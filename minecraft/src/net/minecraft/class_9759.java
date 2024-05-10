package net.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class class_9759 {
	private final class_9760 field_51851;
	private final ChunkPos field_51852;
	@Nullable
	private ChunkStatus field_51853 = null;
	public final ChunkStatus field_51850;
	private volatile boolean field_51854;
	private final List<CompletableFuture<OptionalChunk<Chunk>>> field_51855 = new ArrayList();
	private final class_9762<class_9761> field_51856;
	private boolean field_51857;

	private class_9759(class_9760 arg, ChunkStatus chunkStatus, ChunkPos chunkPos, class_9762<class_9761> arg2) {
		this.field_51851 = arg;
		this.field_51850 = chunkStatus;
		this.field_51852 = chunkPos;
		this.field_51856 = arg2;
	}

	public static class_9759 method_60426(class_9760 arg, ChunkStatus chunkStatus, ChunkPos chunkPos) {
		int i = class_9768.field_51900.method_60518(chunkStatus).method_60559(ChunkStatus.EMPTY);
		class_9762<class_9761> lv = class_9762.method_60483(chunkPos.x, chunkPos.z, i, (ix, j) -> arg.method_60448(ChunkPos.toLong(ix, j)));
		return new class_9759(arg, chunkStatus, chunkPos, lv);
	}

	@Nullable
	public CompletableFuture<?> method_60424() {
		while (true) {
			CompletableFuture<?> completableFuture = this.method_60435();
			if (completableFuture != null) {
				return completableFuture;
			}

			if (this.field_51854 || this.field_51853 == this.field_51850) {
				this.method_60433();
				return null;
			}

			this.method_60432();
		}
	}

	private void method_60432() {
		ChunkStatus chunkStatus;
		if (this.field_51853 == null) {
			chunkStatus = ChunkStatus.EMPTY;
		} else if (!this.field_51857 && this.field_51853 == ChunkStatus.EMPTY && !this.method_60434()) {
			this.field_51857 = true;
			chunkStatus = ChunkStatus.EMPTY;
		} else {
			chunkStatus = (ChunkStatus)ChunkStatus.createOrderedList().get(this.field_51853.getIndex() + 1);
		}

		this.method_60427(chunkStatus, this.field_51857);
		this.field_51853 = chunkStatus;
	}

	public void method_60429() {
		this.field_51854 = true;
	}

	private void method_60433() {
		class_9761 lv = this.field_51856.method_60482(this.field_51852.x, this.field_51852.z);
		lv.method_60453(this);
		this.field_51856.method_60484(this.field_51851::method_60441);
	}

	private boolean method_60434() {
		if (this.field_51850 == ChunkStatus.EMPTY) {
			return true;
		} else {
			ChunkStatus chunkStatus = this.field_51856.method_60482(this.field_51852.x, this.field_51852.z).method_60472();
			if (chunkStatus != null && !chunkStatus.method_60549(this.field_51850)) {
				class_9767 lv = class_9768.field_51901.method_60518(this.field_51850).accumulatedDependencies();
				int i = lv.method_60517();

				for (int j = this.field_51852.x - i; j <= this.field_51852.x + i; j++) {
					for (int k = this.field_51852.z - i; k <= this.field_51852.z + i; k++) {
						int l = this.field_51852.method_60510(j, k);
						ChunkStatus chunkStatus2 = lv.method_60514(l);
						ChunkStatus chunkStatus3 = this.field_51856.method_60482(j, k).method_60472();
						if (chunkStatus3 == null || chunkStatus3.method_60549(chunkStatus2)) {
							return false;
						}
					}
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public class_9761 method_60431() {
		return this.field_51856.method_60482(this.field_51852.x, this.field_51852.z);
	}

	private void method_60427(ChunkStatus chunkStatus, boolean bl) {
		int i = this.method_60430(chunkStatus, bl);

		for (int j = this.field_51852.x - i; j <= this.field_51852.x + i; j++) {
			for (int k = this.field_51852.z - i; k <= this.field_51852.z + i; k++) {
				class_9761 lv = this.field_51856.method_60482(j, k);
				if (this.field_51854 || !this.method_60428(chunkStatus, bl, lv)) {
					return;
				}
			}
		}
	}

	private int method_60430(ChunkStatus chunkStatus, boolean bl) {
		class_9768 lv = bl ? class_9768.field_51900 : class_9768.field_51901;
		return lv.method_60518(this.field_51850).method_60559(chunkStatus);
	}

	private boolean method_60428(ChunkStatus chunkStatus, boolean bl, class_9761 arg) {
		ChunkStatus chunkStatus2 = arg.method_60472();
		boolean bl2 = chunkStatus2 != null && chunkStatus.method_60547(chunkStatus2);
		class_9768 lv = bl2 ? class_9768.field_51900 : class_9768.field_51901;
		if (bl2 && !bl) {
			throw new IllegalStateException("Can't load chunk, but didn't expect to need to generate");
		} else {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = arg.method_60461(lv.method_60518(chunkStatus), this.field_51851, this.field_51856);
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.getNow(null);
			if (optionalChunk == null) {
				this.field_51855.add(completableFuture);
				return true;
			} else if (optionalChunk.isPresent()) {
				return true;
			} else {
				this.method_60429();
				return false;
			}
		}
	}

	@Nullable
	private CompletableFuture<?> method_60435() {
		while (!this.field_51855.isEmpty()) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51855.getLast();
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.getNow(null);
			if (optionalChunk == null) {
				return completableFuture;
			}

			this.field_51855.removeLast();
			if (!optionalChunk.isPresent()) {
				this.method_60429();
			}
		}

		return null;
	}
}
