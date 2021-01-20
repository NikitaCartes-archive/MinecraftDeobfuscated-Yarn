/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TickLimitExceededException;
import net.minecraft.test.TimedTaskRunner;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GameTest {
    private final TestFunction testFunction;
    @Nullable
    private BlockPos pos;
    private final ServerWorld world;
    private final Collection<TestListener> listeners = Lists.newArrayList();
    private final int ticksLeft;
    private final Collection<TimedTaskRunner> timedTaskRunners = Lists.newCopyOnWriteArrayList();
    private final Object2LongMap<Runnable> field_21453 = new Object2LongOpenHashMap<Runnable>();
    private long expectedStopTime;
    private long tick;
    private boolean started;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private boolean completed;
    private final BlockRotation rotation;
    @Nullable
    private Throwable throwable;
    @Nullable
    private StructureBlockBlockEntity structureBlockEntity;

    public GameTest(TestFunction testFunction, BlockRotation rotation, ServerWorld world) {
        this.testFunction = testFunction;
        this.world = world;
        this.ticksLeft = testFunction.getTickLimit();
        this.rotation = testFunction.getRotation().rotate(rotation);
    }

    void setPos(BlockPos pos) {
        this.pos = pos;
    }

    void startCountdown() {
        this.expectedStopTime = this.world.getTime() + 1L + this.testFunction.getDuration();
        this.stopwatch.start();
    }

    public void tick() {
        if (this.isCompleted()) {
            return;
        }
        this.method_33315();
        if (this.isCompleted()) {
            if (this.throwable != null) {
                this.listeners.forEach(testListener -> testListener.onFailed(this));
            } else {
                this.listeners.forEach(testListener -> testListener.onPassed(this));
            }
        }
    }

    private void method_33315() {
        this.tick = this.world.getTime() - this.expectedStopTime;
        if (this.tick < 0L) {
            return;
        }
        if (this.tick == 0L) {
            this.start();
        }
        Iterator objectIterator = this.field_21453.object2LongEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2LongMap.Entry entry = (Object2LongMap.Entry)objectIterator.next();
            if (entry.getLongValue() > this.tick) continue;
            try {
                ((Runnable)entry.getKey()).run();
            } catch (Exception exception) {
                this.fail(exception);
            }
            objectIterator.remove();
        }
        if (this.tick > (long)this.ticksLeft) {
            if (this.timedTaskRunners.isEmpty()) {
                this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
            } else {
                this.timedTaskRunners.forEach(timedTaskRunner -> timedTaskRunner.runReported(this.tick));
                if (this.throwable == null) {
                    this.fail(new TickLimitExceededException("No sequences finished"));
                }
            }
        } else {
            this.timedTaskRunners.forEach(timedTaskRunner -> timedTaskRunner.runSilently(this.tick));
        }
    }

    private void start() {
        if (this.started) {
            throw new IllegalStateException("Test already started");
        }
        this.started = true;
        try {
            this.testFunction.start(new StartupParameter(this));
        } catch (Exception exception) {
            this.fail(exception);
        }
    }

    public String getStructurePath() {
        return this.testFunction.getStructurePath();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public boolean isPassed() {
        return this.completed && this.throwable == null;
    }

    public boolean isFailed() {
        return this.throwable != null;
    }

    public boolean isStarted() {
        return this.started;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    private void complete() {
        if (!this.completed) {
            this.completed = true;
            this.stopwatch.stop();
        }
    }

    public void fail(Throwable throwable) {
        this.throwable = throwable;
        this.complete();
    }

    @Nullable
    public Throwable getThrowable() {
        return this.throwable;
    }

    public String toString() {
        return this.getStructurePath();
    }

    public void addListener(TestListener listener) {
        this.listeners.add(listener);
    }

    public void init(BlockPos pos, int i) {
        this.structureBlockEntity = StructureTestUtil.createStructure(this.getStructureName(), pos, this.getRotation(), i, this.world, false);
        this.pos = this.structureBlockEntity.getPos();
        this.structureBlockEntity.setStructureName(this.getStructurePath());
        StructureTestUtil.placeStartButton(this.pos, new BlockPos(1, 0, -1), this.getRotation(), this.world);
        this.listeners.forEach(listener -> listener.onStarted(this));
    }

    public void clearArea() {
        if (this.structureBlockEntity == null) {
            throw new IllegalStateException("Expected structure to be initialized, but it was null");
        }
        BlockBox blockBox = StructureTestUtil.getStructureBlockBox(this.structureBlockEntity);
        StructureTestUtil.clearArea(blockBox, this.pos.getY(), this.world);
    }

    public boolean isRequired() {
        return this.testFunction.isRequired();
    }

    public boolean isOptional() {
        return !this.testFunction.isRequired();
    }

    public String getStructureName() {
        return this.testFunction.getStructureName();
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public TestFunction getTestFunction() {
        return this.testFunction;
    }

    public boolean isFlaky() {
        return this.testFunction.isFlaky();
    }

    public int getMaxAttempts() {
        return this.testFunction.getMaxAttempts();
    }

    public int getRequiredSuccesses() {
        return this.testFunction.getRequiredSuccesses();
    }
}

