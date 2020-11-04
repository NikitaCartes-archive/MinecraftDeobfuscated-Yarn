package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.class_5624;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestUtil {
	public static void startTest(GameTest gameTest, BlockPos blockPos, TestManager testManager) {
		gameTest.startCountdown();
		testManager.start(gameTest);
		gameTest.addListener(new class_5624(gameTest, testManager, blockPos));
		gameTest.init(blockPos, 2);
	}

	public static Collection<GameTest> runTestBatches(
		Collection<GameTestBatch> batches, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i
	) {
		TestRunner testRunner = new TestRunner(batches, pos, blockRotation, serverWorld, testManager, i);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTest> runTestFunctions(
		Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i
	) {
		return runTestBatches(createBatches(testFunctions), pos, blockRotation, serverWorld, testManager, i);
	}

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions) {
		Map<String, Collection<TestFunction>> map = Maps.<String, Collection<TestFunction>>newHashMap();
		testFunctions.forEach(testFunction -> {
			String string = testFunction.getBatchId();
			Collection<TestFunction> collection = (Collection<TestFunction>)map.computeIfAbsent(string, stringx -> Lists.newArrayList());
			collection.add(testFunction);
		});
		return (Collection<GameTestBatch>)map.keySet()
			.stream()
			.flatMap(
				string -> {
					Collection<TestFunction> collection = (Collection<TestFunction>)map.get(string);
					Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(string);
					Consumer<ServerWorld> consumer2 = TestFunctions.method_32244(string);
					MutableInt mutableInt = new MutableInt();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(list -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), list, consumer, consumer2));
				}
			)
			.collect(Collectors.toList());
	}

	public static void clearTests(ServerWorld world, BlockPos pos, TestManager testManager, int radius) {
		testManager.clear();
		BlockPos blockPos = pos.add(-radius, 0, -radius);
		BlockPos blockPos2 = pos.add(radius, 0, radius);
		BlockPos.stream(blockPos, blockPos2).filter(blockPosx -> world.getBlockState(blockPosx).isOf(Blocks.STRUCTURE_BLOCK)).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(blockPosx);
			BlockPos blockPos2x = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.method_29410(structureBlockBlockEntity);
			StructureTestUtil.clearArea(blockBox, blockPos2x.getY(), world);
		});
	}

	public static void clearDebugMarkers(ServerWorld world) {
		DebugInfoSender.clearGameTestMarkers(world);
	}
}
