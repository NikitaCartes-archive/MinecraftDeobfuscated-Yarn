package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<GameTestBatch> batches;
	private final BlockPos pos;
	private final ServerWorld world;
	private final TestManager testManager;
	private final List<GameTest> tests = Lists.<GameTest>newArrayList();
	private final Map<String, Collection<GameTest>> testsByBatch = Maps.<String, Collection<GameTest>>newHashMap();
	private TestSet currentBatchTests;
	private int currentBatchIndex = 0;
	private BlockPos.Mutable reusablePos;
	private int sizeZ = 0;

	public TestRunner(Collection<GameTestBatch> collection, BlockPos blockPos, ServerWorld serverWorld, TestManager testManager) {
		this.batches = Lists.<GameTestBatch>newArrayList(collection);
		this.reusablePos = new BlockPos.Mutable(blockPos);
		this.pos = blockPos;
		this.world = serverWorld;
		this.testManager = testManager;
	}

	private void init() {
		this.batches.forEach(gameTestBatch -> {
			Collection<GameTest> collection = Lists.<GameTest>newArrayList();

			for (TestFunction testFunction : gameTestBatch.getTestFunctions()) {
				BlockPos blockPos = new BlockPos(this.reusablePos);
				StructureTestUtil.method_22250(testFunction.getStructureName(), blockPos, 2, this.world, true);
				GameTest gameTest = new GameTest(testFunction, blockPos, this.world);
				collection.add(gameTest);
				this.tests.add(gameTest);
				this.testsByBatch.put(gameTestBatch.getId(), collection);
				BlockPos blockPos2 = gameTest.getSize();
				int i = blockPos2 == null ? 1 : blockPos2.getX();
				int j = blockPos2 == null ? 1 : blockPos2.getZ();
				this.sizeZ = Math.max(this.sizeZ, j);
				this.reusablePos.setOffset(i + 4, 0, 0);
				if (this.tests.size() % 8 == 0) {
					this.reusablePos.setOffset(0, 0, this.sizeZ + 5);
					this.reusablePos.setX(this.pos.getX());
					this.sizeZ = 0;
				}
			}
		});
	}

	public List<GameTest> getTests() {
		return this.tests;
	}

	public void run() {
		this.init();
		this.runBatch(0);
	}

	private void runBatch(int i) {
		this.currentBatchIndex = i;
		this.currentBatchTests = new TestSet();
		if (i < this.batches.size()) {
			GameTestBatch gameTestBatch = (GameTestBatch)this.batches.get(this.currentBatchIndex);
			gameTestBatch.setWorld(this.world);
			this.runBatch(gameTestBatch);
			String string = gameTestBatch.getId();
			Collection<GameTest> collection = (Collection<GameTest>)this.testsByBatch.get(string);
			LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
			collection.forEach(gameTest -> {
				this.currentBatchTests.add(gameTest);
				this.currentBatchTests.addListener(new TestListener() {
					@Override
					public void onStarted(GameTest gameTest) {
					}

					@Override
					public void onPassed(GameTest gameTest) {
						TestRunner.this.onTestCompleted(gameTest);
					}

					@Override
					public void onFailed(GameTest gameTest) {
						TestRunner.this.onTestCompleted(gameTest);
					}
				});
				TestUtil.startTest(gameTest, this.testManager);
			});
		}
	}

	private void onTestCompleted(GameTest gameTest) {
		if (this.currentBatchTests.isDone()) {
			this.runBatch(this.currentBatchIndex + 1);
		}
	}

	private void runBatch(GameTestBatch gameTestBatch) {
		Collection<GameTest> collection = (Collection<GameTest>)this.testsByBatch.get(gameTestBatch.getId());
		collection.forEach(gameTest -> gameTest.start(2));
	}
}
