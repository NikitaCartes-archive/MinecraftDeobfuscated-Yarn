package net.minecraft.test;

import com.google.common.collect.ImmutableList;
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
	private final List<GameTest> tests;
	private final List<Pair<GameTestBatch, Collection<GameTest>>> batches;
	private final BlockPos.Mutable reusablePos;

	public TestRunner(
		Collection<GameTestBatch> collection, BlockPos blockPos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i
	) {
		this.reusablePos = blockPos.mutableCopy();
		this.pos = blockPos;
		this.world = serverWorld;
		this.testManager = testManager;
		this.sizeZ = i;
		this.batches = (List<Pair<GameTestBatch, Collection<GameTest>>>)collection.stream()
			.map(
				gameTestBatch -> {
					Collection<GameTest> collectionx = (Collection<GameTest>)gameTestBatch.getTestFunctions()
						.stream()
						.map(testFunction -> new GameTest(testFunction, blockRotation, serverWorld))
						.collect(ImmutableList.toImmutableList());
					return Pair.of(gameTestBatch, collectionx);
				}
			)
			.collect(ImmutableList.toImmutableList());
		this.tests = (List<GameTest>)this.batches.stream().flatMap(pair -> ((Collection)pair.getSecond()).stream()).collect(ImmutableList.toImmutableList());
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
				public void method_33317(GameTest gameTest) {
					this.method_32239();
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
