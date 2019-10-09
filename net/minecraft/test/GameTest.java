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
import net.minecraft.class_4516;
import net.minecraft.class_4693;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TickLimitExceededException;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GameTest {
    private final TestFunction testFunction;
    private BlockPos pos;
    private final ServerWorld world;
    private final Collection<TestListener> listeners = Lists.newArrayList();
    private final int ticksLeft;
    private final Collection<class_4693> field_21452 = Lists.newCopyOnWriteArrayList();
    private Object2LongMap<Runnable> field_21453 = new Object2LongOpenHashMap<Runnable>();
    private long field_21454;
    private long field_21455;
    private boolean started = false;
    private final Stopwatch field_21456 = Stopwatch.createUnstarted();
    private boolean completed = false;
    @Nullable
    private Throwable throwable;

    public GameTest(TestFunction testFunction, ServerWorld serverWorld) {
        this.testFunction = testFunction;
        this.world = serverWorld;
        this.ticksLeft = testFunction.getTickLimit();
    }

    public GameTest(TestFunction testFunction, BlockPos blockPos, ServerWorld serverWorld) {
        this(testFunction, serverWorld);
        this.method_23635(blockPos);
    }

    void method_23635(BlockPos blockPos) {
        this.pos = blockPos;
    }

    void method_23634() {
        this.field_21454 = this.world.getTime() + 1L + this.testFunction.method_23649();
        this.field_21456.start();
    }

    public void tick() {
        if (this.isCompleted()) {
            return;
        }
        this.field_21455 = this.world.getTime() - this.field_21454;
        if (this.field_21455 < 0L) {
            return;
        }
        if (this.field_21455 == 0L) {
            this.method_23639();
        }
        Iterator objectIterator = this.field_21453.object2LongEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2LongMap.Entry entry = (Object2LongMap.Entry)objectIterator.next();
            if (entry.getLongValue() > this.field_21455) continue;
            try {
                ((Runnable)entry.getKey()).run();
            } catch (Exception exception) {
                this.fail(exception);
            }
            objectIterator.remove();
        }
        if (this.field_21455 > (long)this.ticksLeft) {
            if (this.field_21452.isEmpty()) {
                this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
            } else {
                this.field_21452.forEach(arg -> arg.method_23644(this.field_21455));
                if (this.throwable == null) {
                    this.fail(new TickLimitExceededException("No sequences finished"));
                }
            }
        } else {
            this.field_21452.forEach(arg -> arg.method_23643(this.field_21455));
        }
    }

    private void method_23639() {
        if (this.started) {
            throw new IllegalStateException("Test already started");
        }
        this.started = true;
        try {
            this.testFunction.method_22297(new class_4516(this));
        } catch (Exception exception) {
            this.fail(exception);
        }
    }

    public String getStructureName() {
        return this.testFunction.getStructurePath();
    }

    public BlockPos getPos() {
        return this.pos;
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
        return (StructureBlockBlockEntity)this.world.getBlockEntity(this.pos);
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

    private void method_23640() {
        if (!this.completed) {
            this.completed = true;
            this.field_21456.stop();
        }
    }

    public void fail(Throwable throwable) {
        this.method_23640();
        this.throwable = throwable;
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

    public void init(int i) {
        StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.pos, i, this.world, false);
        structureBlockBlockEntity.setStructureName(this.getStructureName());
        StructureTestUtil.placeStartButton(this.pos.add(1, 0, -1), this.world);
        this.listeners.forEach(testListener -> testListener.onStarted(this));
    }

    public boolean isRequired() {
        return this.testFunction.isRequired();
    }

    public boolean isOptional() {
        return !this.testFunction.isRequired();
    }

    public String method_23638() {
        return this.testFunction.getStructureName();
    }
}

