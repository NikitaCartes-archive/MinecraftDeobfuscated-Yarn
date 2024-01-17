package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;

public record GameTestBatch(String id, Collection<GameTestState> states, Consumer<ServerWorld> beforeBatchFunction, Consumer<ServerWorld> afterBatchFunction) {
	public static final String DEFAULT_BATCH = "defaultBatch";

	public GameTestBatch(String id, Collection<GameTestState> states, Consumer<ServerWorld> beforeBatchFunction, Consumer<ServerWorld> afterBatchFunction) {
		if (states.isEmpty()) {
			throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
		} else {
			this.id = id;
			this.states = states;
			this.beforeBatchFunction = beforeBatchFunction;
			this.afterBatchFunction = afterBatchFunction;
		}
	}
}
