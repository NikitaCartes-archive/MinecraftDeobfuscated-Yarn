package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GameTest {
	private final TestFunction testFunction;
	private BlockPos pos;
	private final ServerWorld world;
	private final Collection<TestListener> listeners = Lists.<TestListener>newArrayList();
	private final int ticksLeft;
	private final Collection<TimedTaskRunner> field_21452 = Lists.<TimedTaskRunner>newCopyOnWriteArrayList();
	private Object2LongMap<Runnable> field_21453 = new Object2LongOpenHashMap<>();
	private long expectedStopTime;
	private long field_21455;
	private boolean started = false;
	private final Stopwatch stopwatch = Stopwatch.createUnstarted();
	private boolean completed = false;
	@Nullable
	private Throwable throwable;

	public GameTest(TestFunction testFunction, ServerWorld world) {
		this.testFunction = testFunction;
		this.world = world;
		this.ticksLeft = testFunction.getTickLimit();
	}

	public GameTest(TestFunction testFunction, BlockPos pos, ServerWorld world) {
		this(testFunction, world);
		this.setPos(pos);
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
			this.field_21455 = this.world.getTime() - this.expectedStopTime;
			if (this.field_21455 >= 0L) {
				if (this.field_21455 == 0L) {
					this.start();
				}

				ObjectIterator<Entry<Runnable>> objectIterator = this.field_21453.object2LongEntrySet().iterator();

				while (objectIterator.hasNext()) {
					Entry<Runnable> entry = (Entry<Runnable>)objectIterator.next();
					if (entry.getLongValue() <= this.field_21455) {
						try {
							((Runnable)entry.getKey()).run();
						} catch (Exception var4) {
							this.fail(var4);
						}

						objectIterator.remove();
					}
				}

				if (this.field_21455 > (long)this.ticksLeft) {
					if (this.field_21452.isEmpty()) {
						this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
					} else {
						this.field_21452.forEach(timedTaskRunner -> timedTaskRunner.runReported(this.field_21455));
						if (this.throwable == null) {
							this.fail(new TickLimitExceededException("No sequences finished"));
						}
					}
				} else {
					this.field_21452.forEach(timedTaskRunner -> timedTaskRunner.runSilently(this.field_21455));
				}
			}
		}
	}

	private void start() {
		if (this.started) {
			throw new IllegalStateException("Test already started");
		} else {
			this.started = true;

			try {
				this.testFunction.start(new StartupParameter(this));
			} catch (Exception var2) {
				this.fail(var2);
			}
		}
	}

	public String getStructurePath() {
		return this.testFunction.getStructurePath();
	}

	public BlockPos getPos() {
		return this.pos;
	}

	@Nullable
	public BlockPos getSize() {
		StructureBlockBlockEntity structureBlockBlockEntity = this.getBlockEntity();
		return structureBlockBlockEntity == null ? null : structureBlockBlockEntity.getSize();
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

	private void complete() {
		if (!this.completed) {
			this.completed = true;
			this.stopwatch.stop();
		}
	}

	public void fail(Throwable throwable) {
		this.complete();
		this.throwable = throwable;
		this.listeners.forEach(testListener -> testListener.onFailed(this));
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

	public void init(int i) {
		StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.pos, i, this.world, false);
		structureBlockBlockEntity.setStructureName(this.getStructurePath());
		StructureTestUtil.placeStartButton(this.pos.add(1, 0, -1), this.world);
		this.listeners.forEach(testListener -> testListener.onStarted(this));
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
}
