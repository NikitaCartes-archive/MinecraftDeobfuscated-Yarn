package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;

public class GameTestState {
	private final TestFunction testFunction;
	@Nullable
	private BlockPos pos;
	private final ServerWorld world;
	private final Collection<TestListener> listeners = Lists.<TestListener>newArrayList();
	private final int ticksLeft;
	private final Collection<TimedTaskRunner> timedTaskRunners = Lists.<TimedTaskRunner>newCopyOnWriteArrayList();
	private final Object2LongMap<Runnable> ticksByRunnables = new Object2LongOpenHashMap<>();
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

	public GameTestState(TestFunction testFunction, BlockRotation rotation, ServerWorld world) {
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
		if (!this.isCompleted()) {
			this.tickTests();
			if (this.isCompleted()) {
				if (this.throwable != null) {
					this.listeners.forEach(listener -> listener.onFailed(this));
				} else {
					this.listeners.forEach(listener -> listener.onPassed(this));
				}
			}
		}
	}

	private void tickTests() {
		this.tick = this.world.getTime() - this.expectedStopTime;
		if (this.tick >= 0L) {
			if (this.tick == 0L) {
				this.start();
			}

			ObjectIterator<Entry<Runnable>> objectIterator = this.ticksByRunnables.object2LongEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<Runnable> entry = (Entry<Runnable>)objectIterator.next();
				if (entry.getLongValue() <= this.tick) {
					try {
						((Runnable)entry.getKey()).run();
					} catch (Exception var4) {
						this.fail(var4);
					}

					objectIterator.remove();
				}
			}

			if (this.tick > (long)this.ticksLeft) {
				if (this.timedTaskRunners.isEmpty()) {
					this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
				} else {
					this.timedTaskRunners.forEach(runner -> runner.runReported(this.tick));
					if (this.throwable == null) {
						this.fail(new TickLimitExceededException("No sequences finished"));
					}
				}
			} else {
				this.timedTaskRunners.forEach(runner -> runner.runSilently(this.tick));
			}
		}
	}

	private void start() {
		if (this.started) {
			throw new IllegalStateException("Test already started");
		} else {
			this.started = true;

			try {
				this.testFunction.start(new TestContext(this));
			} catch (Exception var2) {
				this.fail(var2);
			}
		}
	}

	public void runAtTick(long tick, Runnable runnable) {
		this.ticksByRunnables.put(runnable, tick);
	}

	public String getTemplatePath() {
		return this.testFunction.getTemplatePath();
	}

	public BlockPos getPos() {
		return this.pos;
	}

	@Nullable
	public Vec3i getSize() {
		StructureBlockBlockEntity structureBlockBlockEntity = this.getStructureBlockBlockEntity();
		return structureBlockBlockEntity == null ? null : structureBlockBlockEntity.getSize();
	}

	@Nullable
	public Box getBoundingBox() {
		StructureBlockBlockEntity structureBlockBlockEntity = this.getStructureBlockBlockEntity();
		return structureBlockBlockEntity == null ? null : StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
	}

	@Nullable
	private StructureBlockBlockEntity getStructureBlockBlockEntity() {
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

	public long getElapsedMilliseconds() {
		return this.stopwatch.elapsed(TimeUnit.MILLISECONDS);
	}

	private void complete() {
		if (!this.completed) {
			this.completed = true;
			this.stopwatch.stop();
		}
	}

	public void completeIfSuccessful() {
		if (this.throwable == null) {
			this.complete();
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
		return this.getTemplatePath();
	}

	public void addListener(TestListener listener) {
		this.listeners.add(listener);
	}

	public void init(BlockPos pos, int i) {
		this.structureBlockEntity = StructureTestUtil.createStructureTemplate(this.getTemplateName(), pos, this.getRotation(), i, this.world, false);
		this.pos = this.structureBlockEntity.getPos();
		this.structureBlockEntity.setTemplateName(this.getTemplatePath());
		StructureTestUtil.placeStartButton(this.pos, new BlockPos(1, 0, -1), this.getRotation(), this.world);
		this.listeners.forEach(listener -> listener.onStarted(this));
	}

	public void clearArea() {
		if (this.structureBlockEntity == null) {
			throw new IllegalStateException("Expected structure to be initialized, but it was null");
		} else {
			BlockBox blockBox = StructureTestUtil.getStructureBlockBox(this.structureBlockEntity);
			StructureTestUtil.clearArea(blockBox, this.pos.getY(), this.world);
		}
	}

	long getTick() {
		return this.tick;
	}

	TimedTaskRunner createTimedTaskRunner() {
		TimedTaskRunner timedTaskRunner = new TimedTaskRunner(this);
		this.timedTaskRunners.add(timedTaskRunner);
		return timedTaskRunner;
	}

	public boolean isRequired() {
		return this.testFunction.isRequired();
	}

	public boolean isOptional() {
		return !this.testFunction.isRequired();
	}

	public String getTemplateName() {
		return this.testFunction.getTemplateName();
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public TestFunction getTestFunction() {
		return this.testFunction;
	}

	public int getTicksLeft() {
		return this.ticksLeft;
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
