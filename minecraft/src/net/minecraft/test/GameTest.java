package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4516;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

public class GameTest {
	private final TestFunction testFunction;
	private final BlockPos pos;
	private final ServerWorld world;
	private final Collection<TestListener> listeners = Lists.<TestListener>newArrayList();
	private int ticksLeft;
	private Runnable tickAction;
	private Map<Runnable, Long> field_20691 = Maps.<Runnable, Long>newHashMap();
	private boolean started = false;
	private long startTime = -1L;
	private boolean completed = false;
	private long completionTime = -1L;
	@Nullable
	private Throwable throwable;

	public GameTest(TestFunction testFunction, BlockPos blockPos, ServerWorld serverWorld) {
		this.testFunction = testFunction;
		this.pos = blockPos;
		this.world = serverWorld;
		this.ticksLeft = testFunction.getTickLimit();
	}

	public void tick() {
		if (!this.isCompleted()) {
			Iterator<Entry<Runnable, Long>> iterator = this.field_20691.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Runnable, Long> entry = (Entry<Runnable, Long>)iterator.next();
				if ((Long)entry.getValue() <= this.world.getTime()) {
					try {
						((Runnable)entry.getKey()).run();
					} catch (Exception var4) {
						this.fail(var4);
					}

					iterator.remove();
				}
			}

			this.ticksLeft--;
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
	}

	public String getStructureName() {
		return this.testFunction.getStructurePath();
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public void init(int i) {
		try {
			StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.pos, i, this.world, false);
			structureBlockBlockEntity.setStructureName(this.getStructureName());
			StructureTestUtil.placeStartButton(this.pos.add(1, 0, -1), this.world);
			this.listeners.forEach(testListener -> testListener.onStarted(this));
			this.testFunction.method_22297(new class_4516(this));
		} catch (RuntimeException var3) {
			this.fail(var3);
		}
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
		} catch (Exception var2) {
			this.fail(var2);
		}
	}

	private void step() {
		try {
			this.tickAction.run();
			this.pass();
		} catch (Exception var2) {
		}
	}

	public void start(int i) {
		StructureTestUtil.method_22250(this.testFunction.getStructureName(), this.pos, i, this.world, false);
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
