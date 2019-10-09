package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.class_4516;
import net.minecraft.class_4693;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GameTest {
	private final TestFunction testFunction;
	private BlockPos pos;
	private final ServerWorld world;
	private final Collection<TestListener> listeners = Lists.<TestListener>newArrayList();
	private final int ticksLeft;
	private final Collection<class_4693> field_21452 = Lists.<class_4693>newCopyOnWriteArrayList();
	private Object2LongMap<Runnable> field_21453 = new Object2LongOpenHashMap<>();
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
		if (!this.isCompleted()) {
			this.field_21455 = this.world.getTime() - this.field_21454;
			if (this.field_21455 >= 0L) {
				if (this.field_21455 == 0L) {
					this.method_23639();
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
						this.field_21452.forEach(arg -> arg.method_23644(this.field_21455));
						if (this.throwable == null) {
							this.fail(new TickLimitExceededException("No sequences finished"));
						}
					}
				} else {
					this.field_21452.forEach(arg -> arg.method_23643(this.field_21455));
				}
			}
		}
	}

	private void method_23639() {
		if (this.started) {
			throw new IllegalStateException("Test already started");
		} else {
			this.started = true;

			try {
				this.testFunction.method_22297(new class_4516(this));
			} catch (Exception var2) {
				this.fail(var2);
			}
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
