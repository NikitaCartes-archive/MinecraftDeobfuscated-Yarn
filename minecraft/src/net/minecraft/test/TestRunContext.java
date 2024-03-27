package net.minecraft.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;

public class TestRunContext {
	public static final int DEFAULT_TESTS_PER_ROW = 8;
	private static final Logger LOGGER = LogUtils.getLogger();
	final ServerWorld world;
	private final TestManager manager;
	private final List<GameTestState> states;
	private ImmutableList<GameTestBatch> batches;
	final List<BatchListener> batchListeners = Lists.<BatchListener>newArrayList();
	private final List<GameTestState> toBeRetried = Lists.<GameTestState>newArrayList();
	private final TestRunContext.Batcher batcher;
	private boolean stopped = true;
	@Nullable
	GameTestBatch currentBatch;
	private final TestRunContext.TestStructureSpawner reuseSpawner;
	private final TestRunContext.TestStructureSpawner initialSpawner;

	protected TestRunContext(
		TestRunContext.Batcher batcher,
		Collection<GameTestBatch> batches,
		ServerWorld world,
		TestManager manager,
		TestRunContext.TestStructureSpawner reuseSpawner,
		TestRunContext.TestStructureSpawner initialSpawner
	) {
		this.world = world;
		this.manager = manager;
		this.batcher = batcher;
		this.reuseSpawner = reuseSpawner;
		this.initialSpawner = initialSpawner;
		this.batches = ImmutableList.copyOf(batches);
		this.states = (List<GameTestState>)this.batches.stream().flatMap(batch -> batch.states().stream()).collect(Util.toArrayList());
		manager.setRunContext(this);
		this.states.forEach(state -> state.addListener(new StructureTestListener()));
	}

	public List<GameTestState> getStates() {
		return this.states;
	}

	public void start() {
		this.stopped = false;
		this.runBatch(0);
	}

	public void clear() {
		this.stopped = true;
		if (this.currentBatch != null) {
			this.currentBatch.afterBatchFunction().accept(this.world);
		}
	}

	public void retry(GameTestState state) {
		GameTestState gameTestState = state.copy();
		state.streamListeners().forEach(listener -> listener.onRetry(state, gameTestState, this));
		this.states.add(gameTestState);
		this.toBeRetried.add(gameTestState);
		if (this.stopped) {
			this.onFinish();
		}
	}

	void runBatch(int batchIndex) {
		if (batchIndex >= this.batches.size()) {
			this.onFinish();
		} else {
			this.currentBatch = (GameTestBatch)this.batches.get(batchIndex);
			Collection<GameTestState> collection = this.prepareStructures(this.currentBatch.states());
			String string = this.currentBatch.id();
			LOGGER.info("Running test batch '{}' ({} tests)...", string, collection.size());
			this.currentBatch.beforeBatchFunction().accept(this.world);
			this.batchListeners.forEach(listener -> listener.onStarted(this.currentBatch));
			final TestSet testSet = new TestSet();
			collection.forEach(testSet::add);
			testSet.addListener(new TestListener() {
				private void onFinished() {
					if (testSet.isDone()) {
						TestRunContext.this.currentBatch.afterBatchFunction().accept(TestRunContext.this.world);
						TestRunContext.this.batchListeners.forEach(listener -> listener.onFinished(TestRunContext.this.currentBatch));
						LongSet longSet = new LongArraySet(TestRunContext.this.world.getForcedChunks());
						longSet.forEach(chunkPos -> TestRunContext.this.world.setChunkForced(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos), false));
						TestRunContext.this.runBatch(batchIndex + 1);
					}
				}

				@Override
				public void onStarted(GameTestState test) {
				}

				@Override
				public void onPassed(GameTestState test, TestRunContext context) {
					this.onFinished();
				}

				@Override
				public void onFailed(GameTestState test, TestRunContext context) {
					this.onFinished();
				}

				@Override
				public void onRetry(GameTestState prevState, GameTestState nextState, TestRunContext context) {
				}
			});
			collection.forEach(this.manager::start);
		}
	}

	private void onFinish() {
		if (!this.toBeRetried.isEmpty()) {
			LOGGER.info("Starting re-run of tests: {}", this.toBeRetried.stream().map(state -> state.getTestFunction().templatePath()).collect(Collectors.joining(", ")));
			this.batches = ImmutableList.copyOf(this.batcher.batch(this.toBeRetried));
			this.toBeRetried.clear();
			this.stopped = false;
			this.runBatch(0);
		} else {
			this.batches = ImmutableList.of();
			this.stopped = true;
		}
	}

	public void addBatchListener(BatchListener batchListener) {
		this.batchListeners.add(batchListener);
	}

	private Collection<GameTestState> prepareStructures(Collection<GameTestState> oldStates) {
		return oldStates.stream().map(this::prepareStructure).flatMap(Optional::stream).toList();
	}

	private Optional<GameTestState> prepareStructure(GameTestState oldState) {
		return oldState.getPos() == null ? this.initialSpawner.spawnStructure(oldState) : this.reuseSpawner.spawnStructure(oldState);
	}

	public static void clearDebugMarkers(ServerWorld world) {
		DebugInfoSender.clearGameTestMarkers(world);
	}

	public interface Batcher {
		Collection<GameTestBatch> batch(Collection<GameTestState> states);
	}

	public static class Builder {
		private final ServerWorld world;
		private final TestManager manager = TestManager.INSTANCE;
		private final TestRunContext.Batcher batcher = Batches.defaultBatcher();
		private final TestRunContext.TestStructureSpawner reuseSpawner = TestRunContext.TestStructureSpawner.REUSE;
		private TestRunContext.TestStructureSpawner initialSpawner = TestRunContext.TestStructureSpawner.NOOP;
		private final Collection<GameTestBatch> batches;

		private Builder(Collection<GameTestBatch> batches, ServerWorld world) {
			this.batches = batches;
			this.world = world;
		}

		public static TestRunContext.Builder of(Collection<GameTestBatch> batches, ServerWorld world) {
			return new TestRunContext.Builder(batches, world);
		}

		public static TestRunContext.Builder ofStates(Collection<GameTestState> states, ServerWorld world) {
			return of(Batches.defaultBatcher().batch(states), world);
		}

		public TestRunContext.Builder initialSpawner(TestRunContext.TestStructureSpawner initialSpawner) {
			this.initialSpawner = initialSpawner;
			return this;
		}

		public TestRunContext build() {
			return new TestRunContext(this.batcher, this.batches, this.world, this.manager, this.reuseSpawner, this.initialSpawner);
		}
	}

	public interface TestStructureSpawner {
		TestRunContext.TestStructureSpawner REUSE = oldState -> Optional.of(oldState.init().initializeImmediately().startCountdown(1));
		TestRunContext.TestStructureSpawner NOOP = oldState -> Optional.empty();

		Optional<GameTestState> spawnStructure(GameTestState oldState);
	}
}
