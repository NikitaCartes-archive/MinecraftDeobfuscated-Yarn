package net.minecraft.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestUtil {
	private static final int MAX_BATCH_SIZE = 100;
	public static final int field_33148 = 2;
	public static final int field_33149 = 5;
	public static final int field_33150 = 6;
	public static final int field_33151 = 8;

	public static void startTest(GameTestState test, BlockPos pos, TestManager testManager) {
		test.startCountdown();
		testManager.start(test);
		test.addListener(new StructureTestListener(test, testManager, pos));
		test.init(pos, 2);
	}

	public static Collection<GameTestState> runTestBatches(
		Collection<GameTestBatch> batches, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int sizeZ
	) {
		TestRunner testRunner = new TestRunner(batches, pos, rotation, world, testManager, sizeZ);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTestState> runTestFunctions(
		Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int sizeZ
	) {
		return runTestBatches(createBatches(testFunctions), pos, rotation, world, testManager, sizeZ);
	}

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions) {
		Map<String, List<TestFunction>> map = (Map<String, List<TestFunction>>)testFunctions.stream().collect(Collectors.groupingBy(TestFunction::getBatchId));
		return (Collection<GameTestBatch>)map.entrySet()
			.stream()
			.flatMap(
				entry -> {
					String string = (String)entry.getKey();
					Consumer<ServerWorld> consumer = TestFunctions.getBeforeBatchConsumer(string);
					Consumer<ServerWorld> consumer2 = TestFunctions.getAfterBatchConsumer(string);
					MutableInt mutableInt = new MutableInt();
					Collection<TestFunction> collection = (Collection<TestFunction>)entry.getValue();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(
							testFunctionsx -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), ImmutableList.<TestFunction>copyOf(testFunctionsx), consumer, consumer2)
						);
				}
			)
			.collect(ImmutableList.toImmutableList());
	}

	public static void clearTests(ServerWorld world, BlockPos pos, TestManager testManager, int radius) {
		testManager.clear();
		BlockPos blockPos = pos.add(-radius, 0, -radius);
		BlockPos blockPos2 = pos.add(radius, 0, radius);
		BlockPos.stream(blockPos, blockPos2).filter(posx -> world.getBlockState(posx).isOf(Blocks.STRUCTURE_BLOCK)).forEach(posx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(posx);
			BlockPos blockPosx = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.getStructureBlockBox(structureBlockBlockEntity);
			StructureTestUtil.clearArea(blockBox, blockPosx.getY(), world);
		});
	}

	public static void clearDebugMarkers(ServerWorld world) {
		DebugInfoSender.clearGameTestMarkers(world);
	}
}
