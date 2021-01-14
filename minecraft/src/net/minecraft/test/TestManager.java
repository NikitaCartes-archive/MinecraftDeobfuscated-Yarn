package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;

public class TestManager {
	public static final TestManager INSTANCE = new TestManager();
	private final Collection<GameTestState> tests = Lists.<GameTestState>newCopyOnWriteArrayList();

	public void start(GameTestState test) {
		this.tests.add(test);
	}

	public void clear() {
		this.tests.clear();
	}

	public void tick() {
		this.tests.forEach(GameTestState::tick);
		this.tests.removeIf(GameTestState::isCompleted);
	}
}
