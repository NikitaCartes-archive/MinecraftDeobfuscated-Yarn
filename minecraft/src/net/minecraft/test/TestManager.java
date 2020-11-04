package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;

public class TestManager {
	public static final TestManager INSTANCE = new TestManager();
	private final Collection<GameTest> tests = Lists.<GameTest>newCopyOnWriteArrayList();

	public void start(GameTest test) {
		this.tests.add(test);
	}

	public void clear() {
		this.tests.clear();
	}

	public void tick() {
		this.tests.forEach(GameTest::tick);
		this.tests.removeIf(GameTest::isCompleted);
	}
}
