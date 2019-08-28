/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.class_4516;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TickLimitExceededException;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GameTest {
    private final TestFunction testFunction;
    private final BlockPos blockPos;
    private final ServerWorld world;
    private final Collection<TestListener> listeners = Lists.newArrayList();
    private int ticksLeft;
    private Runnable tickAction;
    private boolean started = false;
    private long startTime = -1L;
    private boolean completed = false;
    private long completionTime = -1L;
    @Nullable
    private Throwable throwable;

    public GameTest(TestFunction testFunction, BlockPos blockPos, ServerWorld serverWorld) {
        this.testFunction = testFunction;
        this.blockPos = blockPos;
        this.world = serverWorld;
        this.ticksLeft = testFunction.getTickLimit();
    }

    public void tick() {
        if (this.isCompleted()) {
            return;
        }
        --this.ticksLeft;
        if (this.ticksLeft <= 0) {
            if (this.tickAction == null) {
                this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
            } else {
                this.finish();
            }
        } else if (this.tickAction != null) {
            this.step();
        }
    }

    public String getStructureName() {
        return this.testFunction.getStructurePath();
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void init(int i) {
        try {
            StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.blockPos, i, this.world, false);
            structureBlockBlockEntity.setStructureName(this.getStructureName());
            StructureTestUtil.placeStartButton(this.blockPos.add(1, 0, -1), this.world);
            this.listeners.forEach(testListener -> testListener.onStarted(this));
            this.testFunction.method_22297(new class_4516(this));
        } catch (RuntimeException runtimeException) {
            this.fail(runtimeException);
        }
    }

    @Nullable
    public BlockPos getSize() {
        StructureBlockBlockEntity structureBlockBlockEntity = this.getBlockEntity();
        if (structureBlockBlockEntity == null) {
            return null;
        }
        return structureBlockBlockEntity.getSize();
    }

    @Nullable
    private StructureBlockBlockEntity getBlockEntity() {
        return (StructureBlockBlockEntity)this.world.getBlockEntity(this.blockPos);
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

    public void pass() {
        this.completed = true;
        this.completionTime = SystemUtil.getMeasuringTimeMs();
        this.throwable = null;
        this.tickAction = null;
        this.listeners.forEach(testListener -> testListener.onPassed(this));
    }

    public void fail(Throwable throwable) {
        this.completed = true;
        this.completionTime = SystemUtil.getMeasuringTimeMs();
        this.throwable = throwable;
        this.tickAction = null;
        this.listeners.forEach(testListener -> testListener.onFailed(this));
    }

    @Nullable
    public Throwable getThrowable() {
        return this.throwable;
    }

    public String toString() {
        return this.getStructureName();
    }

    public void addListener(TestListener testListener) {
        this.listeners.add(testListener);
    }

    private void finish() {
        try {
            this.tickAction.run();
            this.pass();
        } catch (Exception exception) {
            this.fail(exception);
        }
    }

    private void step() {
        try {
            this.tickAction.run();
            this.pass();
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void start(int i) {
        StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.blockPos, i, this.world, false);
        this.started = true;
        this.startTime = SystemUtil.getMeasuringTimeMs();
    }

    public boolean isRequired() {
        return this.testFunction.isRequired();
    }

    public boolean isOptional() {
        return !this.testFunction.isRequired();
    }
}

