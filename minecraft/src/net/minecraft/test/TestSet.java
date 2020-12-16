package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class TestSet {
	private final Collection<GameTest> tests = Lists.<GameTest>newArrayList();
	@Nullable
	private final Collection<TestListener> field_25303 = Lists.<TestListener>newArrayList();

	public TestSet() {
	}

	public TestSet(Collection<GameTest> tests) {
		this.tests.addAll(tests);
	}

	public void add(GameTest test) {
		this.tests.add(test);
		this.field_25303.forEach(test::addListener);
	}

	public void addListener(TestListener listener) {
		this.field_25303.add(listener);
		this.tests.forEach(gameTest -> gameTest.addListener(listener));
	}

	public void method_29407(Consumer<GameTest> consumer) {
		this.addListener(new TestListener() {
			@Override
			public void onStarted(GameTest test) {
			}

			@Override
			public void method_33317(GameTest gameTest) {
			}

			@Override
			public void onFailed(GameTest test) {
				consumer.accept(test);
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
		this.tests.forEach(gameTest -> {
			if (!gameTest.isStarted()) {
				stringBuffer.append(' ');
			} else if (gameTest.isPassed()) {
				stringBuffer.append('+');
			} else if (gameTest.isFailed()) {
				stringBuffer.append((char)(gameTest.isRequired() ? 'X' : 'x'));
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
