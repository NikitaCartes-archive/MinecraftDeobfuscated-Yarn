package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.world.ServerWorld;

public class Batches {
	private static final int BATCH_SIZE = 50;

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions, ServerWorld world) {
		Map<String, List<TestFunction>> map = (Map<String, List<TestFunction>>)testFunctions.stream().collect(Collectors.groupingBy(TestFunction::batchId));
		return map.entrySet()
			.stream()
			.flatMap(
				entry -> {
					String string = (String)entry.getKey();
					List<TestFunction> list = (List<TestFunction>)entry.getValue();
					return Streams.mapWithIndex(
						Lists.partition(list, 50).stream(),
						(states, index) -> create(states.stream().map(testFunction -> createState(testFunction, 0, world)).toList(), string, index)
					);
				}
			)
			.toList();
	}

	public static GameTestState createState(TestFunction testFunction, int rotationSteps, ServerWorld world) {
		return new GameTestState(testFunction, StructureTestUtil.getRotation(rotationSteps), world, TestAttemptConfig.once());
	}

	public static TestRunContext.Batcher defaultBatcher() {
		return batcher(50);
	}

	public static TestRunContext.Batcher batcher(int batchSize) {
		return states -> {
			Map<String, List<GameTestState>> map = (Map<String, List<GameTestState>>)states.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(state -> state.getTestFunction().batchId()));
			return map.entrySet().stream().flatMap(entry -> {
				String string = (String)entry.getKey();
				List<GameTestState> list = (List<GameTestState>)entry.getValue();
				return Streams.mapWithIndex(Lists.partition(list, batchSize).stream(), (statesx, index) -> create(List.copyOf(statesx), string, index));
			}).toList();
		};
	}

	public static GameTestBatch create(Collection<GameTestState> states, String batchId, long index) {
		Consumer<ServerWorld> consumer = TestFunctions.getBeforeBatchConsumer(batchId);
		Consumer<ServerWorld> consumer2 = TestFunctions.getAfterBatchConsumer(batchId);
		return new GameTestBatch(batchId + ":" + index, states, consumer, consumer2);
	}
}
