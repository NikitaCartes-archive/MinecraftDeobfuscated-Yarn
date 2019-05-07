package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;

public class ServerTickScheduler<T> implements TickScheduler<T> {
	protected final Predicate<T> invalidObjPredicate;
	private final Function<T, Identifier> idToName;
	private final Function<Identifier, T> field_19309;
	private final Set<ScheduledTick<T>> ticksScheduled = Sets.<ScheduledTick<T>>newHashSet();
	private final Set<ScheduledTick<T>> field_19310 = Sets.<ScheduledTick<T>>newTreeSet();
	private final List<ScheduledTick<T>> field_19311 = Lists.<ScheduledTick<T>>newArrayList();
	private final ServerWorld world;
	private final List<ScheduledTick<T>> ticksCurrent = Lists.<ScheduledTick<T>>newArrayList();
	private final Consumer<ScheduledTick<T>> tickConsumer;

	public ServerTickScheduler(
		ServerWorld serverWorld, Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, Consumer<ScheduledTick<T>> consumer
	) {
		this.invalidObjPredicate = predicate;
		this.idToName = function;
		this.field_19309 = function2;
		this.world = serverWorld;
		this.tickConsumer = consumer;
	}

	public void tick() {
		this.method_20514();
		int i = this.field_19310.size();
		if (i != this.ticksScheduled.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			this.world.getProfiler().push("selecting");

			for (ScheduledTick<T> scheduledTick : this.field_19310) {
				if (i < 0 || scheduledTick.time > this.world.getTime()) {
					break;
				}

				if (this.world.method_14178().method_20529(scheduledTick.pos)) {
					this.ticksCurrent.add(scheduledTick);
					i--;
				}
			}

			this.world.getProfiler().swap("ticking");
			Iterator<ScheduledTick<T>> iterator = this.ticksCurrent.iterator();

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				if (this.world.method_14178().method_20529(scheduledTick.pos)) {
					try {
						this.tickConsumer.accept(scheduledTick);
					} catch (Throwable var7) {
						CrashReport crashReport = CrashReport.create(var7, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
						CrashReportSection.addBlockInfo(crashReportSection, scheduledTick.pos, null);
						throw new CrashException(crashReport);
					}
				} else {
					iterator.remove();
				}
			}

			this.world.getProfiler().swap("cleaning");
			this.field_19310.removeAll(this.ticksCurrent);
			this.ticksScheduled.removeAll(this.ticksCurrent);
			this.ticksCurrent.clear();
			this.world.getProfiler().pop();
		}
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return this.ticksCurrent.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void method_20470(Stream<ScheduledTick<T>> stream) {
		stream.forEach(this::method_20513);
	}

	public List<ScheduledTick<T>> getScheduledTicksInChunk(boolean bl, ChunkPos chunkPos) {
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.getScheduledTicks(new MutableIntBoundingBox(i, 0, k, j, 256, l), bl);
	}

	public List<ScheduledTick<T>> getScheduledTicks(MutableIntBoundingBox mutableIntBoundingBox, boolean bl) {
		this.method_20514();
		List<ScheduledTick<T>> list = null;
		Iterator<ScheduledTick<T>> iterator = this.field_19310.iterator();

		while (iterator.hasNext()) {
			ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
			BlockPos blockPos = scheduledTick.pos;
			if (blockPos.getX() >= mutableIntBoundingBox.minX
				&& blockPos.getX() < mutableIntBoundingBox.maxX
				&& blockPos.getZ() >= mutableIntBoundingBox.minZ
				&& blockPos.getZ() < mutableIntBoundingBox.maxZ) {
				if (bl) {
					this.ticksScheduled.remove(scheduledTick);
					iterator.remove();
				}

				if (list == null) {
					list = Lists.<ScheduledTick<T>>newArrayList();
				}

				list.add(scheduledTick);
			}
		}

		return list == null ? Collections.emptyList() : list;
	}

	public void copyScheduledTicks(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos) {
		for (ScheduledTick<T> scheduledTick : this.getScheduledTicks(mutableIntBoundingBox, false)) {
			if (mutableIntBoundingBox.contains(scheduledTick.pos)) {
				BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
				this.schedule(blockPos2, scheduledTick.getObject(), (int)(scheduledTick.time - this.world.getLevelProperties().getTime()), scheduledTick.priority);
			}
		}
	}

	public ListTag toTag(ChunkPos chunkPos) {
		List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(false, chunkPos);
		return serializeScheduledTicks(this.idToName, list, this.world.getTime());
	}

	public static <T> ListTag serializeScheduledTicks(Function<T, Identifier> function, Iterable<ScheduledTick<T>> iterable, long l) {
		ListTag listTag = new ListTag();

		for (ScheduledTick<T> scheduledTick : iterable) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("i", ((Identifier)function.apply(scheduledTick.getObject())).toString());
			compoundTag.putInt("x", scheduledTick.pos.getX());
			compoundTag.putInt("y", scheduledTick.pos.getY());
			compoundTag.putInt("z", scheduledTick.pos.getZ());
			compoundTag.putInt("t", (int)(scheduledTick.time - l));
			compoundTag.putInt("p", scheduledTick.priority.getPriorityIndex());
			listTag.add(compoundTag);
		}

		return listTag;
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return this.ticksScheduled.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			this.method_20513(new ScheduledTick<>(blockPos, object, (long)i + this.world.getTime(), taskPriority));
		}
	}

	private void method_20513(ScheduledTick<T> scheduledTick) {
		this.field_19311.add(scheduledTick);
	}

	private void method_20514() {
		this.field_19311.forEach(scheduledTick -> {
			if (!this.ticksScheduled.contains(scheduledTick)) {
				this.ticksScheduled.add(scheduledTick);
				this.field_19310.add(scheduledTick);
			}
		});
		this.field_19311.clear();
	}
}
