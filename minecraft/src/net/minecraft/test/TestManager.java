package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class TestManager {
	public static final TestManager INSTANCE = new TestManager();
	private final Collection<GameTestState> tests = Lists.<GameTestState>newCopyOnWriteArrayList();
	@Nullable
	private TestRunContext runContext;

	private TestManager() {
	}

	public void start(GameTestState test) {
		this.tests.add(test);
	}

	public void clear() {
		this.tests.clear();
		if (this.runContext != null) {
			this.runContext.clear();
			this.runContext = null;
		}
	}

	public void setRunContext(TestRunContext runContext) {
		if (this.runContext != null) {
			Util.logErrorOrPause("The runner was already set in GameTestTicker");
		}

		this.runContext = runContext;
	}

	public void tick() {
		if (this.runContext != null) {
			this.tests.forEach(test -> test.tick(this.runContext));
			this.tests.removeIf(GameTestState::isCompleted);
		}
	}
}
