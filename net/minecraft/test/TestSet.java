/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestListener;
import org.jetbrains.annotations.Nullable;

public class TestSet {
    private static final char NOT_STARTED = ' ';
    private static final char RUNNING = '_';
    private static final char PASS = '+';
    private static final char OPTIONAL_FAIL = 'x';
    private static final char REQUIRED_FAIL = 'X';
    private final Collection<GameTestState> tests = Lists.newArrayList();
    @Nullable
    private final Collection<TestListener> listeners = Lists.newArrayList();

    public TestSet() {
    }

    public TestSet(Collection<GameTestState> tests) {
        this.tests.addAll(tests);
    }

    public void add(GameTestState test) {
        this.tests.add(test);
        this.listeners.forEach(test::addListener);
    }

    public void addListener(TestListener listener) {
        this.listeners.add(listener);
        this.tests.forEach(test -> test.addListener(listener));
    }

    public void addListener(final Consumer<GameTestState> onFailed) {
        this.addListener(new TestListener(){

            @Override
            public void onStarted(GameTestState test) {
            }

            @Override
            public void onPassed(GameTestState test) {
            }

            @Override
            public void onFailed(GameTestState test) {
                onFailed.accept(test);
            }
        });
    }

    public int getFailedRequiredTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isRequired).count();
    }

    public int getFailedOptionalTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isOptional).count();
    }

    public int getCompletedTestCount() {
        return (int)this.tests.stream().filter(GameTestState::isCompleted).count();
    }

    public boolean failed() {
        return this.getFailedRequiredTestCount() > 0;
    }

    public boolean hasFailedOptionalTests() {
        return this.getFailedOptionalTestCount() > 0;
    }

    public Collection<GameTestState> getRequiredTests() {
        return this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isRequired).collect(Collectors.toList());
    }

    public Collection<GameTestState> getOptionalTests() {
        return this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isOptional).collect(Collectors.toList());
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
                stringBuffer.append(test.isRequired() ? (char)'X' : (char)'x');
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

