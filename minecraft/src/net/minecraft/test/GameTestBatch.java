package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;

public class GameTestBatch {
	private final String id;
	private final Collection<TestFunction> testFunctions;
	@Nullable
	private final Consumer<ServerWorld> beforeBatchConsumer;

	public GameTestBatch(String id, Collection<TestFunction> testFunctions, @Nullable Consumer<ServerWorld> worldSetter) {
		if (testFunctions.isEmpty()) {
			throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
		} else {
			this.id = id;
			this.testFunctions = testFunctions;
			this.beforeBatchConsumer = worldSetter;
		}
	}

	public String getId() {
		return this.id;
	}

	public Collection<TestFunction> getTestFunctions() {
		return this.testFunctions;
	}

	public void startBatch(ServerWorld world) {
		if (this.beforeBatchConsumer != null) {
			this.beforeBatchConsumer.accept(world);
		}
	}
}
