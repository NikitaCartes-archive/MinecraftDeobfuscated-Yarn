package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;

public class GameTestBatch {
	private final String id;
	private final Collection<TestFunction> testFunctions;
	@Nullable
	private final Consumer<ServerWorld> worldSetter;
	@Nullable
	private final Consumer<ServerWorld> field_27801;

	public GameTestBatch(String id, Collection<TestFunction> testFunctions, @Nullable Consumer<ServerWorld> worldSetter, @Nullable Consumer<ServerWorld> consumer) {
		if (testFunctions.isEmpty()) {
			throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
		} else {
			this.id = id;
			this.testFunctions = testFunctions;
			this.worldSetter = worldSetter;
			this.field_27801 = consumer;
		}
	}

	public String getId() {
		return this.id;
	}

	public Collection<TestFunction> getTestFunctions() {
		return this.testFunctions;
	}

	public void setWorld(ServerWorld world) {
		if (this.worldSetter != null) {
			this.worldSetter.accept(world);
		}
	}

	public void method_32237(ServerWorld serverWorld) {
		if (this.field_27801 != null) {
			this.field_27801.accept(serverWorld);
		}
	}
}
