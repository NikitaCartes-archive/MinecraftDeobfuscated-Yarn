package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.ChunkLoadingManager;

public class ChunkLoader {
	private final ChunkLoadingManager chunkLoadingManager;
	private final ChunkPos pos;
	@Nullable
	private ChunkStatus currentlyLoadingStatus = null;
	public final ChunkStatus targetStatus;
	private volatile boolean pendingDisposal;
	private final List<CompletableFuture<OptionalChunk<Chunk>>> futures = new ArrayList();
	private final BoundedRegionArray<AbstractChunkHolder> chunks;
	private boolean allowGeneration;

	private ChunkLoader(ChunkLoadingManager chunkLoadingManager, ChunkStatus targetStatus, ChunkPos pos, BoundedRegionArray<AbstractChunkHolder> chunks) {
		this.chunkLoadingManager = chunkLoadingManager;
		this.targetStatus = targetStatus;
		this.pos = pos;
		this.chunks = chunks;
	}

	public static ChunkLoader create(ChunkLoadingManager chunkLoadingManager, ChunkStatus targetStatus, ChunkPos pos) {
		int i = ChunkGenerationSteps.GENERATION.get(targetStatus).getAdditionalLevel(ChunkStatus.EMPTY);
		BoundedRegionArray<AbstractChunkHolder> boundedRegionArray = BoundedRegionArray.create(
			pos.x, pos.z, i, (x, z) -> chunkLoadingManager.acquire(ChunkPos.toLong(x, z))
		);
		return new ChunkLoader(chunkLoadingManager, targetStatus, pos, boundedRegionArray);
	}

	@Nullable
	public CompletableFuture<?> run() {
		while (true) {
			CompletableFuture<?> completableFuture = this.getLatestPendingFuture();
			if (completableFuture != null) {
				return completableFuture;
			}

			if (this.pendingDisposal || this.currentlyLoadingStatus == this.targetStatus) {
				this.dispose();
				return null;
			}

			this.loadNextStatus();
		}
	}

	private void loadNextStatus() {
		ChunkStatus chunkStatus;
		if (this.currentlyLoadingStatus == null) {
			chunkStatus = ChunkStatus.EMPTY;
		} else if (!this.allowGeneration && this.currentlyLoadingStatus == ChunkStatus.EMPTY && !this.isGenerationUnnecessary()) {
			this.allowGeneration = true;
			chunkStatus = ChunkStatus.EMPTY;
		} else {
			chunkStatus = (ChunkStatus)ChunkStatus.createOrderedList().get(this.currentlyLoadingStatus.getIndex() + 1);
		}

		this.loadAll(chunkStatus, this.allowGeneration);
		this.currentlyLoadingStatus = chunkStatus;
	}

	public void markPendingDisposal() {
		this.pendingDisposal = true;
	}

	private void dispose() {
		AbstractChunkHolder abstractChunkHolder = this.chunks.get(this.pos.x, this.pos.z);
		abstractChunkHolder.clearLoader(this);
		this.chunks.forEach(this.chunkLoadingManager::release);
	}

	private boolean isGenerationUnnecessary() {
		if (this.targetStatus == ChunkStatus.EMPTY) {
			return true;
		} else {
			ChunkStatus chunkStatus = this.chunks.get(this.pos.x, this.pos.z).getActualStatus();
			if (chunkStatus != null && !chunkStatus.isEarlierThan(this.targetStatus)) {
				GenerationDependencies generationDependencies = ChunkGenerationSteps.LOADING.get(this.targetStatus).accumulatedDependencies();
				int i = generationDependencies.getMaxLevel();

				for (int j = this.pos.x - i; j <= this.pos.x + i; j++) {
					for (int k = this.pos.z - i; k <= this.pos.z + i; k++) {
						int l = this.pos.getChebyshevDistance(j, k);
						ChunkStatus chunkStatus2 = generationDependencies.get(l);
						ChunkStatus chunkStatus3 = this.chunks.get(j, k).getActualStatus();
						if (chunkStatus3 == null || chunkStatus3.isEarlierThan(chunkStatus2)) {
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

	public AbstractChunkHolder getHolder() {
		return this.chunks.get(this.pos.x, this.pos.z);
	}

	private void loadAll(ChunkStatus targetStatus, boolean allowGeneration) {
		try (ScopedProfiler scopedProfiler = Profilers.get().scoped("scheduleLayer")) {
			scopedProfiler.addLabel(targetStatus::getId);
			int i = this.getAdditionalLevel(targetStatus, allowGeneration);

			for (int j = this.pos.x - i; j <= this.pos.x + i; j++) {
				for (int k = this.pos.z - i; k <= this.pos.z + i; k++) {
					AbstractChunkHolder abstractChunkHolder = this.chunks.get(j, k);
					if (this.pendingDisposal || !this.load(targetStatus, allowGeneration, abstractChunkHolder)) {
						return;
					}
				}
			}
		}
	}

	private int getAdditionalLevel(ChunkStatus status, boolean generate) {
		ChunkGenerationSteps chunkGenerationSteps = generate ? ChunkGenerationSteps.GENERATION : ChunkGenerationSteps.LOADING;
		return chunkGenerationSteps.get(this.targetStatus).getAdditionalLevel(status);
	}

	private boolean load(ChunkStatus targetStatus, boolean allowGeneration, AbstractChunkHolder chunkHolder) {
		ChunkStatus chunkStatus = chunkHolder.getActualStatus();
		boolean bl = chunkStatus != null && targetStatus.isLaterThan(chunkStatus);
		ChunkGenerationSteps chunkGenerationSteps = bl ? ChunkGenerationSteps.GENERATION : ChunkGenerationSteps.LOADING;
		if (bl && !allowGeneration) {
			throw new IllegalStateException("Can't load chunk, but didn't expect to need to generate");
		} else {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = chunkHolder.generate(
				chunkGenerationSteps.get(targetStatus), this.chunkLoadingManager, this.chunks
			);
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.getNow(null);
			if (optionalChunk == null) {
				this.futures.add(completableFuture);
				return true;
			} else if (optionalChunk.isPresent()) {
				return true;
			} else {
				this.markPendingDisposal();
				return false;
			}
		}
	}

	@Nullable
	private CompletableFuture<?> getLatestPendingFuture() {
		while (!this.futures.isEmpty()) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.futures.getLast();
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.getNow(null);
			if (optionalChunk == null) {
				return completableFuture;
			}

			this.futures.removeLast();
			if (!optionalChunk.isPresent()) {
				this.markPendingDisposal();
			}
		}

		return null;
	}
}
