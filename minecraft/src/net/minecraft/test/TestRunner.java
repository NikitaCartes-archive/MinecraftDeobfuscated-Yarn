package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockPos pos;
	private final ServerWorld world;
	private final TestManager testManager;
	private final int sizeZ;
	private final List<GameTest> tests = Lists.<GameTest>newArrayList();
	private final List<Pair<GameTestBatch, Collection<GameTest>>> batches = Lists.<Pair<GameTestBatch, Collection<GameTest>>>newArrayList();
	private final BlockPos.Mutable reusablePos;

	public TestRunner(Collection<GameTestBatch> collection, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i) {
		this.reusablePos = pos.mutableCopy();
		this.pos = pos;
		this.world = serverWorld;
		this.testManager = testManager;
		this.sizeZ = i;
		collection.forEach(gameTestBatch -> {
			Collection<GameTest> collectionx = Lists.<GameTest>newArrayList();

			for (TestFunction testFunction : gameTestBatch.getTestFunctions()) {
				GameTest gameTest = new GameTest(testFunction, blockRotation, serverWorld);
				collectionx.add(gameTest);
				this.tests.add(gameTest);
			}

			this.batches.add(Pair.of(gameTestBatch, collectionx));
		});
	}

	public List<GameTest> getTests() {
		return this.tests;
	}

	public void run() {
		this.runBatch(0);
	}

	private void runBatch(int index) {
		if (index < this.batches.size()) {
			Pair<GameTestBatch, Collection<GameTest>> pair = (Pair<GameTestBatch, Collection<GameTest>>)this.batches.get(index);
			final GameTestBatch gameTestBatch = pair.getFirst();
			Collection<GameTest> collection = pair.getSecond();
			Map<GameTest, BlockPos> map = this.method_29401(collection);
			String string = gameTestBatch.getId();
			LOGGER.info("Running test batch '{}' ({} tests)...", string, collection.size());
			gameTestBatch.setWorld(this.world);
			final TestSet testSet = new TestSet();
			collection.forEach(testSet::add);
			testSet.addListener(new TestListener() {
				private void method_32239() {
					if (testSet.isDone()) {
						gameTestBatch.method_32237(TestRunner.this.world);
						TestRunner.this.runBatch(index + 1);
					}
				}

				@Override
				public void onStarted(GameTest test) {
				}

				@Override
				public void onFailed(GameTest test) {
					this.method_32239();
				}
			});
			collection.forEach(gameTest -> {
				BlockPos blockPos = (BlockPos)map.get(gameTest);
				TestUtil.startTest(gameTest, blockPos, this.testManager);
			});
		}
	}

	private Map<GameTest, BlockPos> method_29401(Collection<GameTest> collection) {
		Map<GameTest, BlockPos> map = Maps.<GameTest, BlockPos>newHashMap();
		int i = 0;
		Box box = new Box(this.reusablePos);

		for (GameTest gameTest : collection) {
			BlockPos blockPos = new BlockPos(this.reusablePos);
			StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(
				gameTest.getStructureName(), blockPos, gameTest.method_29402(), 2, this.world, true
			);
			Box box2 = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
			gameTest.setPos(structureBlockBlockEntity.getPos());
			map.put(gameTest, new BlockPos(this.reusablePos));
			box = box.union(box2);
			this.reusablePos.move((int)box2.getXLength() + 5, 0, 0);
			if (i++ % this.sizeZ == this.sizeZ - 1) {
				this.reusablePos.move(0, 0, (int)box.getZLength() + 6);
				this.reusablePos.setX(this.pos.getX());
				box = new Box(this.reusablePos);
			}
		}

		return map;
	}
}
