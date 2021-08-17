package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLightingProvider extends LightingProvider implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final TaskExecutor<Runnable> processor;
	private final ObjectList<Pair<ServerLightingProvider.Stage, Runnable>> pendingTasks = new ObjectArrayList<>();
	private final ThreadedAnvilChunkStorage chunkStorage;
	private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor;
	private volatile int taskBatchSize = 5;
	private final AtomicBoolean ticking = new AtomicBoolean();

	public ServerLightingProvider(
		ChunkProvider chunkProvider,
		ThreadedAnvilChunkStorage chunkStorage,
		boolean hasBlockLight,
		TaskExecutor<Runnable> processor,
		MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor
	) {
		super(chunkProvider, true, hasBlockLight);
		this.chunkStorage = chunkStorage;
		this.executor = executor;
		this.processor = processor;
	}

	public void close() {
	}

	@Override
	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
	}

	@Override
	public void addLightSource(BlockPos pos, int level) {
		throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
	}

	@Override
	public void checkBlock(BlockPos pos) {
		BlockPos blockPos = pos.toImmutable();
		this.enqueue(
			ChunkSectionPos.getSectionCoord(pos.getX()),
			ChunkSectionPos.getSectionCoord(pos.getZ()),
			ServerLightingProvider.Stage.POST_UPDATE,
			Util.debugRunnable((Runnable)(() -> super.checkBlock(blockPos)), (Supplier<String>)(() -> "checkBlock " + blockPos))
		);
	}

	protected void updateChunkStatus(ChunkPos pos) {
		this.enqueue(pos.x, pos.z, () -> 0, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable((Runnable)(() -> {
			super.setRetainData(pos, false);
			super.setColumnEnabled(pos, false);

			for (int i = this.getBottomY(); i < this.getTopY(); i++) {
				super.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, i), null, true);
				super.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, i), null, true);
			}

			for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); i++) {
				super.setSectionStatus(ChunkSectionPos.from(pos, i), true);
			}
		}), (Supplier<String>)(() -> "updateChunkStatus " + pos + " true")));
	}

	@Override
	public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
		this.enqueue(
			pos.getSectionX(),
			pos.getSectionZ(),
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			Util.debugRunnable((Runnable)(() -> super.setSectionStatus(pos, notReady)), (Supplier<String>)(() -> "updateSectionStatus " + pos + " " + notReady))
		);
	}

	@Override
	public void setColumnEnabled(ChunkPos pos, boolean retainData) {
		this.enqueue(
			pos.x,
			pos.z,
			ServerLightingProvider.Stage.PRE_UPDATE,
			Util.debugRunnable((Runnable)(() -> super.setColumnEnabled(pos, retainData)), (Supplier<String>)(() -> "enableLight " + pos + " " + retainData))
		);
	}

	@Override
	public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles, boolean bl) {
		this.enqueue(
			pos.getSectionX(),
			pos.getSectionZ(),
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			Util.debugRunnable((Runnable)(() -> super.enqueueSectionData(lightType, pos, nibbles, bl)), (Supplier<String>)(() -> "queueData " + pos))
		);
	}

	private void enqueue(int x, int z, ServerLightingProvider.Stage stage, Runnable task) {
		this.enqueue(x, z, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
	}

	private void enqueue(int x, int z, IntSupplier completedLevelSupplier, ServerLightingProvider.Stage stage, Runnable task) {
		this.executor.send(ChunkTaskPrioritySystem.createMessage(() -> {
			this.pendingTasks.add(Pair.of(stage, task));
			if (this.pendingTasks.size() >= this.taskBatchSize) {
				this.runTasks();
			}
		}, ChunkPos.toLong(x, z), completedLevelSupplier));
	}

	@Override
	public void setRetainData(ChunkPos pos, boolean retainData) {
		this.enqueue(
			pos.x,
			pos.z,
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			Util.debugRunnable((Runnable)(() -> super.setRetainData(pos, retainData)), (Supplier<String>)(() -> "retainData " + pos))
		);
	}

	public CompletableFuture<Chunk> light(Chunk chunk, boolean excludeBlocks) {
		ChunkPos chunkPos = chunk.getPos();
		chunk.setLightOn(false);
		this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable((Runnable)(() -> {
			ChunkSection[] chunkSections = chunk.getSectionArray();

			for (int i = 0; i < chunk.countVerticalSections(); i++) {
				ChunkSection chunkSection = chunkSections[i];
				if (!ChunkSection.isEmpty(chunkSection)) {
					int j = this.world.sectionIndexToCoord(i);
					super.setSectionStatus(ChunkSectionPos.from(chunkPos, j), false);
				}
			}

			super.setColumnEnabled(chunkPos, true);
			if (!excludeBlocks) {
				chunk.getLightSourcesStream().forEach(pos -> super.addLightSource(pos, chunk.getLuminance(pos)));
			}

			this.chunkStorage.releaseLightTicket(chunkPos);
		}), (Supplier<String>)(() -> "lightChunk " + chunkPos + " " + excludeBlocks)));
		return CompletableFuture.supplyAsync(() -> {
			chunk.setLightOn(true);
			super.setRetainData(chunkPos, false);
			return chunk;
		}, runnable -> this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.POST_UPDATE, runnable));
	}

	public void tick() {
		if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.ticking.compareAndSet(false, true)) {
			this.processor.send(() -> {
				this.runTasks();
				this.ticking.set(false);
			});
		}
	}

	private void runTasks() {
		int i = Math.min(this.pendingTasks.size(), this.taskBatchSize);
		ObjectListIterator<Pair<ServerLightingProvider.Stage, Runnable>> objectListIterator = this.pendingTasks.iterator();

		int j;
		for (j = 0; objectListIterator.hasNext() && j < i; j++) {
			Pair<ServerLightingProvider.Stage, Runnable> pair = (Pair<ServerLightingProvider.Stage, Runnable>)objectListIterator.next();
			if (pair.getFirst() == ServerLightingProvider.Stage.PRE_UPDATE) {
				pair.getSecond().run();
			}
		}

		objectListIterator.back(j);
		super.doLightUpdates(Integer.MAX_VALUE, true, true);

		for (int var5 = 0; objectListIterator.hasNext() && var5 < i; var5++) {
			Pair<ServerLightingProvider.Stage, Runnable> pair = (Pair<ServerLightingProvider.Stage, Runnable>)objectListIterator.next();
			if (pair.getFirst() == ServerLightingProvider.Stage.POST_UPDATE) {
				pair.getSecond().run();
			}

			objectListIterator.remove();
		}
	}

	public void setTaskBatchSize(int taskBatchSize) {
		this.taskBatchSize = taskBatchSize;
	}

	static enum Stage {
		PRE_UPDATE,
		POST_UPDATE;
	}
}
