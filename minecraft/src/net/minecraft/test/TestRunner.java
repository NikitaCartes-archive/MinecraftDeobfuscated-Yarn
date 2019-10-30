package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockPos pos;
	private final ServerWorld world;
	private final TestManager testManager;
	private final List<GameTest> tests = Lists.<GameTest>newArrayList();
	private final List<Pair<GameTestBatch, Collection<GameTest>>> batches = Lists.<Pair<GameTestBatch, Collection<GameTest>>>newArrayList();
	private TestSet currentBatchTests;
	private int currentBatchIndex = 0;
	private BlockPos.Mutable reusablePos;
	private int sizeZ = 0;

	public TestRunner(Collection<GameTestBatch> batches, BlockPos pos, ServerWorld world, TestManager testManager) {
		this.reusablePos = new BlockPos.Mutable(pos);
		this.pos = pos;
		this.world = world;
		this.testManager = testManager;
		batches.forEach(gameTestBatch -> {
			Collection<GameTest> collection = Lists.<GameTest>newArrayList();

			for (TestFunction testFunction : gameTestBatch.getTestFunctions()) {
				GameTest gameTest = new GameTest(testFunction, world);
				collection.add(gameTest);
				this.tests.add(gameTest);
			}

			this.batches.add(Pair.of(gameTestBatch, collection));
		});
	}

	public List<GameTest> getTests() {
		return this.tests;
	}

	public void run() {
		this.runBatch(0);
	}

	private void runBatch(int index) {
		this.currentBatchIndex = index;
		this.currentBatchTests = new TestSet();
		if (index < this.batches.size()) {
			Pair<GameTestBatch, Collection<GameTest>> pair = (Pair<GameTestBatch, Collection<GameTest>>)this.batches.get(this.currentBatchIndex);
			GameTestBatch gameTestBatch = pair.getFirst();
			Collection<GameTest> collection = pair.getSecond();
			this.method_23632(collection);
			gameTestBatch.setWorld(this.world);
			String string = gameTestBatch.getId();
			LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
			collection.forEach(gameTest -> {
				this.currentBatchTests.add(gameTest);
				this.currentBatchTests.addListener(new TestListener() {
					@Override
					public void onStarted(GameTest test) {
					}

					@Override
					public void onFailed(GameTest test) {
						TestRunner.this.onTestCompleted(test);
					}
				});
				TestUtil.startTest(gameTest, this.testManager);
			});
		}
	}

	private void onTestCompleted(GameTest test) {
		if (this.currentBatchTests.isDone()) {
			this.runBatch(this.currentBatchIndex + 1);
		}
	}

	private void method_23632(Collection<GameTest> collection) {
		int i = 0;

		for (GameTest gameTest : collection) {
			BlockPos blockPos = new BlockPos(this.reusablePos);
			gameTest.setPos(blockPos);
			StructureTestUtil.method_22250(gameTest.getStructureName(), blockPos, 2, this.world, true);
			BlockPos blockPos2 = gameTest.getSize();
			int j = blockPos2 == null ? 1 : blockPos2.getX();
			int k = blockPos2 == null ? 1 : blockPos2.getZ();
			this.sizeZ = Math.max(this.sizeZ, k);
			this.reusablePos.setOffset(j + 4, 0, 0);
			if (i++ % 8 == 0) {
				this.reusablePos.setOffset(0, 0, this.sizeZ + 5);
				this.reusablePos.setX(this.pos.getX());
				this.sizeZ = 0;
			}
		}
	}
}
