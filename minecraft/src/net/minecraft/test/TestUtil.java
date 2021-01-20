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
	public static void startTest(GameTest test, BlockPos pos, TestManager testManager) {
		test.startCountdown();
		testManager.start(test);
		test.addListener(new StructureTestListener(test, testManager, pos));
		test.init(pos, 2);
	}

	public static Collection<GameTest> runTestBatches(
		Collection<GameTestBatch> batches, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int i
	) {
		TestRunner testRunner = new TestRunner(batches, pos, rotation, world, testManager, i);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTest> runTestFunctions(
		Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int i
	) {
		return runTestBatches(createBatches(testFunctions), pos, rotation, world, testManager, i);
	}

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions) {
		Map<String, List<TestFunction>> map = (Map<String, List<TestFunction>>)testFunctions.stream().collect(Collectors.groupingBy(TestFunction::getBatchId));
		return (Collection<GameTestBatch>)map.entrySet()
			.stream()
			.flatMap(
				entry -> {
					String string = (String)entry.getKey();
					Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(string);
					Consumer<ServerWorld> consumer2 = TestFunctions.method_32244(string);
					MutableInt mutableInt = new MutableInt();
					Collection<TestFunction> collection = (Collection<TestFunction>)entry.getValue();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(list -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), ImmutableList.<TestFunction>copyOf(list), consumer, consumer2));
				}
			)
			.collect(ImmutableList.toImmutableList());
	}

	public static void clearTests(ServerWorld world, BlockPos pos, TestManager testManager, int radius) {
		testManager.clear();
		BlockPos blockPos = pos.add(-radius, 0, -radius);
		BlockPos blockPos2 = pos.add(radius, 0, radius);
		BlockPos.stream(blockPos, blockPos2).filter(blockPosx -> world.getBlockState(blockPosx).isOf(Blocks.STRUCTURE_BLOCK)).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(blockPosx);
			BlockPos blockPos2x = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.getStructureBlockBox(structureBlockBlockEntity);
			StructureTestUtil.clearArea(blockBox, blockPos2x.getY(), world);
		});
	}

	public static void clearDebugMarkers(ServerWorld world) {
		DebugInfoSender.clearGameTestMarkers(world);
	}
}
