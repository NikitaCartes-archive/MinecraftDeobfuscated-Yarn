package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class TestSet {
	private final Collection<GameTest> tests = Lists.<GameTest>newArrayList();
	@Nullable
	private final Collection<TestListener> listeners = Lists.<TestListener>newArrayList();

	public TestSet() {
	}

	public TestSet(Collection<GameTest> tests) {
		this.tests.addAll(tests);
	}

	public void add(GameTest test) {
		this.tests.add(test);
		this.listeners.forEach(test::addListener);
	}

	public void addListener(TestListener listener) {
		this.listeners.add(listener);
		this.tests.forEach(test -> test.addListener(listener));
	}

	public void addListener(Consumer<GameTest> onFailed) {
		this.addListener(new TestListener() {
			@Override
			public void onStarted(GameTest test) {
			}

			@Override
			public void onPassed(GameTest test) {
			}

			@Override
			public void onFailed(GameTest test) {
				onFailed.accept(test);
			}
		});
	}

	public int getFailedRequiredTestCount() {
		return (int)this.tests.stream().filter(GameTest::isFailed).filter(GameTest::isRequired).count();
	}

	public int getFailedOptionalTestCount() {
		return (int)this.tests.stream().filter(GameTest::isFailed).filter(GameTest::isOptional).count();
	}

	public int getCompletedTestCount() {
		return (int)this.tests.stream().filter(GameTest::isCompleted).count();
	}

	public boolean failed() {
		return this.getFailedRequiredTestCount() > 0;
	}

	public boolean hasFailedOptionalTests() {
		return this.getFailedOptionalTestCount() > 0;
	}

	public int getTestCount() {
		return this.tests.size();
	}

	public boolean isDone() {
		return this.getCompletedTestCount() == this.getTestCount();
	}

	public String getResultString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('[');
		this.tests.forEach(test -> {
			if (!test.isStarted()) {
				stringBuffer.append(' ');
			} else if (test.isPassed()) {
				stringBuffer.append('+');
			} else if (test.isFailed()) {
				stringBuffer.append((char)(test.isRequired() ? 'X' : 'x'));
			} else {
				stringBuffer.append('_');
			}
		});
		stringBuffer.append(']');
		return stringBuffer.toString();
	}

	public String toString() {
		return this.getResultString();
	}
}
