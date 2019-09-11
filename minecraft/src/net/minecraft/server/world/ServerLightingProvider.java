package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.Actor;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
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
	private final MailboxProcessor<Runnable> processor;
	private final ObjectList<Pair<ServerLightingProvider.Stage, Runnable>> pendingTasks = new ObjectArrayList<>();
	private final ThreadedAnvilChunkStorage chunkStorage;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor;
	private volatile int taskBatchSize = 5;
	private final AtomicBoolean field_18812 = new AtomicBoolean();

	public ServerLightingProvider(
		ChunkProvider chunkProvider,
		ThreadedAnvilChunkStorage threadedAnvilChunkStorage,
		boolean bl,
		MailboxProcessor<Runnable> mailboxProcessor,
		Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor
	) {
		super(chunkProvider, true, bl);
		this.chunkStorage = threadedAnvilChunkStorage;
		this.actor = actor;
		this.processor = mailboxProcessor;
	}

	public void close() {
	}

	@Override
	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		throw (UnsupportedOperationException)SystemUtil.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
	}

	@Override
	public void addLightSource(BlockPos blockPos, int i) {
		throw (UnsupportedOperationException)SystemUtil.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
	}

	@Override
	public void checkBlock(BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.toImmutable();
		this.enqueue(
			blockPos.getX() >> 4,
			blockPos.getZ() >> 4,
			ServerLightingProvider.Stage.POST_UPDATE,
			SystemUtil.debugRunnable(() -> super.checkBlock(blockPos2), () -> "checkBlock " + blockPos2)
		);
	}

	protected void updateChunkStatus(ChunkPos chunkPos) {
		this.enqueue(chunkPos.x, chunkPos.z, () -> 0, ServerLightingProvider.Stage.PRE_UPDATE, SystemUtil.debugRunnable(() -> {
			super.setRetainData(chunkPos, false);
			super.setLightEnabled(chunkPos, false);

			for (int i = -1; i < 17; i++) {
				super.queueData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, i), null);
				super.queueData(LightType.SKY, ChunkSectionPos.from(chunkPos, i), null);
			}

			for (int i = 0; i < 16; i++) {
				super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), true);
			}
		}, () -> "updateChunkStatus " + chunkPos + " " + true));
	}

	@Override
	public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
		this.enqueue(
			chunkSectionPos.getSectionX(),
			chunkSectionPos.getSectionZ(),
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			SystemUtil.debugRunnable(() -> super.updateSectionStatus(chunkSectionPos, bl), () -> "updateSectionStatus " + chunkSectionPos + " " + bl)
		);
	}

	@Override
	public void setLightEnabled(ChunkPos chunkPos, boolean bl) {
		this.enqueue(
			chunkPos.x,
			chunkPos.z,
			ServerLightingProvider.Stage.PRE_UPDATE,
			SystemUtil.debugRunnable(() -> super.setLightEnabled(chunkPos, bl), () -> "enableLight " + chunkPos + " " + bl)
		);
	}

	@Override
	public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, @Nullable ChunkNibbleArray chunkNibbleArray) {
		this.enqueue(
			chunkSectionPos.getSectionX(),
			chunkSectionPos.getSectionZ(),
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			SystemUtil.debugRunnable(() -> super.queueData(lightType, chunkSectionPos, chunkNibbleArray), () -> "queueData " + chunkSectionPos)
		);
	}

	private void enqueue(int i, int j, ServerLightingProvider.Stage stage, Runnable runnable) {
		this.enqueue(i, j, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(i, j)), stage, runnable);
	}

	private void enqueue(int i, int j, IntSupplier intSupplier, ServerLightingProvider.Stage stage, Runnable runnable) {
		this.actor.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> {
			this.pendingTasks.add(Pair.of(stage, runnable));
			if (this.pendingTasks.size() >= this.taskBatchSize) {
				this.runTasks();
			}
		}, ChunkPos.toLong(i, j), intSupplier));
	}

	@Override
	public void setRetainData(ChunkPos chunkPos, boolean bl) {
		this.enqueue(
			chunkPos.x,
			chunkPos.z,
			() -> 0,
			ServerLightingProvider.Stage.PRE_UPDATE,
			SystemUtil.debugRunnable(() -> super.setRetainData(chunkPos, bl), () -> "retainData " + chunkPos)
		);
	}

	public CompletableFuture<Chunk> light(Chunk chunk, boolean bl) {
		ChunkPos chunkPos = chunk.getPos();
		chunk.setLightOn(false);
		this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.PRE_UPDATE, SystemUtil.debugRunnable(() -> {
			ChunkSection[] chunkSections = chunk.getSectionArray();

			for (int i = 0; i < 16; i++) {
				ChunkSection chunkSection = chunkSections[i];
				if (!ChunkSection.isEmpty(chunkSection)) {
					super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), false);
				}
			}

			super.setLightEnabled(chunkPos, true);
			if (!bl) {
				chunk.getLightSourcesStream().forEach(blockPos -> super.addLightSource(blockPos, chunk.getLuminance(blockPos)));
			}

			this.chunkStorage.releaseLightTicket(chunkPos);
		}, () -> "lightChunk " + chunkPos + " " + bl));
		return CompletableFuture.supplyAsync(() -> {
			chunk.setLightOn(true);
			super.setRetainData(chunkPos, false);
			return chunk;
		}, runnable -> this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.POST_UPDATE, runnable));
	}

	public void tick() {
		if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.field_18812.compareAndSet(false, true)) {
			this.processor.send(() -> {
				this.runTasks();
				this.field_18812.set(false);
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

	public void setTaskBatchSize(int i) {
		this.taskBatchSize = i;
	}

	static enum Stage {
		PRE_UPDATE,
		POST_UPDATE;
	}
}
