package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;

public class GameTestBatch {
	private final String field_20543;
	private final Collection<TestFunction> field_20544;
	@Nullable
	private final Consumer<ServerWorld> field_20545;

	public GameTestBatch(String string, Collection<TestFunction> collection, @Nullable Consumer<ServerWorld> consumer) {
		if (collection.isEmpty()) {
			throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
		} else {
			this.field_20543 = string;
			this.field_20544 = collection;
			this.field_20545 = consumer;
		}
	}

	public String method_22152() {
		return this.field_20543;
	}

	public Collection<TestFunction> method_22154() {
		return this.field_20544;
	}

	public void method_22153(ServerWorld serverWorld) {
		if (this.field_20545 != null) {
			this.field_20545.accept(serverWorld);
		}
	}
}
