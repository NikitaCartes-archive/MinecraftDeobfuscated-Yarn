package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;

public class TestSet {
	private final Collection<GameTest> tests = Lists.<GameTest>newArrayList();
	@Nullable
	private TestListener listener;

	public TestSet() {
	}

	public TestSet(Collection<GameTest> collection) {
		this.tests.addAll(collection);
	}

	public void add(GameTest gameTest) {
		this.tests.add(gameTest);
		if (this.listener != null) {
			gameTest.addListener(this.listener);
		}
	}

	public void addListener(TestListener testListener) {
		this.listener = testListener;
		this.tests.forEach(gameTest -> gameTest.addListener(testListener));
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
				stringBuffer.append('√');
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
