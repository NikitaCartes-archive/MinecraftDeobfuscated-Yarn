package net.minecraft.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableInt;

public class Batches {
	private static final int BATCH_SIZE = 50;

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions, ServerWorld world) {
		Map<String, List<TestFunction>> map = (Map<String, List<TestFunction>>)testFunctions.stream().collect(Collectors.groupingBy(TestFunction::batchId));
		return (Collection<GameTestBatch>)map.entrySet()
			.stream()
			.flatMap(
				entry -> {
					String string = (String)entry.getKey();
					Collection<TestFunction> collection = (Collection<TestFunction>)entry.getValue();
					MutableInt mutableInt = new MutableInt();
					return Streams.stream(Iterables.partition(collection, 50))
						.map(batchedFunctions -> create(batchedFunctions.stream().map(testFunction -> createState(testFunction, 0, world)).toList(), string, mutableInt));
				}
			)
			.collect(ImmutableList.toImmutableList());
	}

	public static GameTestState createState(TestFunction testFunction, int rotationSteps, ServerWorld world) {
		return new GameTestState(testFunction, StructureTestUtil.getRotation(rotationSteps), world, TestAttemptConfig.once());
	}

	public static TestRunContext.Batcher defaultBatcher() {
		return states -> {
			Map<String, List<GameTestState>> map = (Map<String, List<GameTestState>>)states.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(state -> state.getTestFunction().batchId()));
			return (Collection<GameTestBatch>)map.entrySet().stream().flatMap(entry -> {
				String string = (String)entry.getKey();
				Collection<GameTestState> collection = (Collection<GameTestState>)entry.getValue();
				MutableInt mutableInt = new MutableInt();
				return Streams.stream(Iterables.partition(collection, 50)).map(batchStates -> create(ImmutableList.copyOf(batchStates), string, mutableInt));
			}).collect(ImmutableList.toImmutableList());
		};
	}

	private static GameTestBatch create(List<GameTestState> states, String batchId, MutableInt index) {
		Consumer<ServerWorld> consumer = TestFunctions.getBeforeBatchConsumer(batchId);
		Consumer<ServerWorld> consumer2 = TestFunctions.getAfterBatchConsumer(batchId);
		return new GameTestBatch(batchId + ":" + index.incrementAndGet(), states, consumer, consumer2);
	}
}
