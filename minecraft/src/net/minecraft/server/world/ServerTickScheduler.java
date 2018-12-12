package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.chunk.ChunkPos;

public class ServerTickScheduler<T> implements TickScheduler<T> {
	protected final Predicate<T> invalidObjPredicate;
	protected final Function<T, Identifier> idToName;
	protected final Function<Identifier, T> nameToId;
	protected final Set<ScheduledTick<T>> ticksScheduled = Sets.<ScheduledTick<T>>newHashSet();
	protected final TreeSet<ScheduledTick<T>> ticksScheduledOrdered = new TreeSet();
	private final ServerWorld world;
	private final List<ScheduledTick<T>> ticksCurrent = Lists.<ScheduledTick<T>>newArrayList();
	private final Consumer<ScheduledTick<T>> tickConsumer;

	public ServerTickScheduler(
		ServerWorld serverWorld, Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, Consumer<ScheduledTick<T>> consumer
	) {
		this.invalidObjPredicate = predicate;
		this.idToName = function;
		this.nameToId = function2;
		this.world = serverWorld;
		this.tickConsumer = consumer;
	}

	public void tick() {
		int i = this.ticksScheduledOrdered.size();
		if (i != this.ticksScheduled.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			this.world.getProfiler().push("cleaning");

			for (int j = 0; j < i; j++) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)this.ticksScheduledOrdered.first();
				if (scheduledTick.time > this.world.getTime()) {
					break;
				}

				this.ticksScheduledOrdered.remove(scheduledTick);
				this.ticksScheduled.remove(scheduledTick);
				this.ticksCurrent.add(scheduledTick);
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().push("ticking");
			Iterator<ScheduledTick<T>> iterator = this.ticksCurrent.iterator();

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				iterator.remove();
				int k = 0;
				if (this.world.isAreaLoaded(scheduledTick.pos.add(0, 0, 0), scheduledTick.pos.add(0, 0, 0))) {
					try {
						this.tickConsumer.accept(scheduledTick);
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.method_562("Block being ticked");
						CrashReportSection.addBlockInfo(crashReportSection, scheduledTick.pos, null);
						throw new CrashException(crashReport);
					}
				} else {
					this.schedule(scheduledTick.pos, scheduledTick.getObject(), 0);
				}
			}

			this.world.getProfiler().pop();
			this.ticksCurrent.clear();
		}
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return this.ticksCurrent.contains(new ScheduledTick(blockPos, object));
	}

	public List<ScheduledTick<T>> getScheduledTicksInChunk(boolean bl, ChunkPos chunkPos) {
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.getScheduledTicksInBox(new MutableIntBoundingBox(i, 0, k, j, 256, l), bl);
	}

	public List<ScheduledTick<T>> getScheduledTicksInBox(MutableIntBoundingBox mutableIntBoundingBox, boolean bl) {
		List<ScheduledTick<T>> list = null;

		for (int i = 0; i < 2; i++) {
			Iterator<ScheduledTick<T>> iterator;
			if (i == 0) {
				iterator = this.ticksScheduledOrdered.iterator();
			} else {
				iterator = this.ticksCurrent.iterator();
			}

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				BlockPos blockPos = scheduledTick.pos;
				if (blockPos.getX() >= mutableIntBoundingBox.minX
					&& blockPos.getX() < mutableIntBoundingBox.maxX
					&& blockPos.getZ() >= mutableIntBoundingBox.minZ
					&& blockPos.getZ() < mutableIntBoundingBox.maxZ) {
					if (bl) {
						if (i == 0) {
							this.ticksScheduled.remove(scheduledTick);
						}

						iterator.remove();
					}

					if (list == null) {
						list = Lists.<ScheduledTick<T>>newArrayList();
					}

					list.add(scheduledTick);
				}
			}
		}

		return list == null ? Collections.emptyList() : list;
	}

	public void method_8666(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos) {
		for (ScheduledTick<T> scheduledTick : this.getScheduledTicksInBox(mutableIntBoundingBox, false)) {
			if (mutableIntBoundingBox.contains(scheduledTick.pos)) {
				BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
				this.scheduleForced(blockPos2, scheduledTick.getObject(), (int)(scheduledTick.time - this.world.getLevelProperties().getTime()), scheduledTick.priority);
			}
		}
	}

	public ListTag toTag(ChunkPos chunkPos) {
		List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(false, chunkPos);
		long l = this.world.getTime();
		ListTag listTag = new ListTag();

		for (ScheduledTick<T> scheduledTick : list) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("i", ((Identifier)this.idToName.apply(scheduledTick.getObject())).toString());
			compoundTag.putInt("x", scheduledTick.pos.getX());
			compoundTag.putInt("y", scheduledTick.pos.getY());
			compoundTag.putInt("z", scheduledTick.pos.getZ());
			compoundTag.putInt("t", (int)(scheduledTick.time - l));
			compoundTag.putInt("p", scheduledTick.priority.getPriorityIndex());
			listTag.add((Tag)compoundTag);
		}

		return listTag;
	}

	public void fromTag(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			T object = (T)this.nameToId.apply(new Identifier(compoundTag.getString("i")));
			if (object != null) {
				this.scheduleForced(
					new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")),
					object,
					compoundTag.getInt("t"),
					TaskPriority.getByIndex(compoundTag.getInt("p"))
				);
			}
		}
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return this.ticksScheduled.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			if (this.world.isBlockLoaded(blockPos)) {
				this.addScheduledTick(blockPos, object, i, taskPriority);
			}
		}
	}

	protected void scheduleForced(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			this.addScheduledTick(blockPos, object, i, taskPriority);
		}
	}

	private void addScheduledTick(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		ScheduledTick<T> scheduledTick = new ScheduledTick<>(blockPos, object, (long)i + this.world.getTime(), taskPriority);
		if (!this.ticksScheduled.contains(scheduledTick)) {
			this.ticksScheduled.add(scheduledTick);
			this.ticksScheduledOrdered.add(scheduledTick);
		}
	}
}
