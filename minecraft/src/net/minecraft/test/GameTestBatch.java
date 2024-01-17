package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;

public record GameTestBatch(String id, Collection<GameTestState> states, Consumer<ServerWorld> beforeBatchFunction, Consumer<ServerWorld> afterBatchFunction) {
	public static final String DEFAULT_BATCH = "defaultBatch";

	public GameTestBatch(String id, Collection<GameTestState> testFunctions, Consumer<ServerWorld> beforeBatchConsumer, Consumer<ServerWorld> afterBatchConsumer) {
		if (testFunctions.isEmpty()) {
			throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
		} else {
			this.id = id;
			this.states = testFunctions;
			this.beforeBatchFunction = beforeBatchConsumer;
			this.afterBatchFunction = afterBatchConsumer;
		}
	}
}
