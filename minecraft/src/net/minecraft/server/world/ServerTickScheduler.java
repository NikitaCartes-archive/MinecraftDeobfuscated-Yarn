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
	private final ServerWorld field_9301;
	private final List<ScheduledTick<T>> ticksCurrent = Lists.<ScheduledTick<T>>newArrayList();
	private final Consumer<ScheduledTick<T>> tickConsumer;

	public ServerTickScheduler(
		ServerWorld serverWorld, Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, Consumer<ScheduledTick<T>> consumer
	) {
		this.invalidObjPredicate = predicate;
		this.idToName = function;
		this.nameToId = function2;
		this.field_9301 = serverWorld;
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

			this.field_9301.getProfiler().push("cleaning");

			for (int j = 0; j < i; j++) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)this.ticksScheduledOrdered.first();
				if (scheduledTick.time > this.field_9301.getTime()) {
					break;
				}

				this.ticksScheduledOrdered.remove(scheduledTick);
				this.ticksScheduled.remove(scheduledTick);
				this.ticksCurrent.add(scheduledTick);
			}

			this.field_9301.getProfiler().pop();
			this.field_9301.getProfiler().push("ticking");
			Iterator<ScheduledTick<T>> iterator = this.ticksCurrent.iterator();

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				iterator.remove();
				if (this.field_9301.method_8591(scheduledTick.field_9322)) {
					try {
						this.tickConsumer.accept(scheduledTick);
					} catch (Throwable var7) {
						CrashReport crashReport = CrashReport.create(var7, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.method_562("Block being ticked");
						CrashReportSection.method_586(crashReportSection, scheduledTick.field_9322, null);
						throw new CrashException(crashReport);
					}
				} else {
					this.method_8676(scheduledTick.field_9322, scheduledTick.getObject(), 0);
				}
			}

			this.field_9301.getProfiler().pop();
			this.ticksCurrent.clear();
		}
	}

	@Override
	public boolean method_8677(BlockPos blockPos, T object) {
		return this.ticksCurrent.contains(new ScheduledTick(blockPos, object));
	}

	public List<ScheduledTick<T>> getScheduledTicksInChunk(boolean bl, ChunkPos chunkPos) {
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.method_8672(new MutableIntBoundingBox(i, 0, k, j, 256, l), bl);
	}

	public List<ScheduledTick<T>> method_8672(MutableIntBoundingBox mutableIntBoundingBox, boolean bl) {
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
				BlockPos blockPos = scheduledTick.field_9322;
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
		for (ScheduledTick<T> scheduledTick : this.method_8672(mutableIntBoundingBox, false)) {
			if (mutableIntBoundingBox.method_14662(scheduledTick.field_9322)) {
				BlockPos blockPos2 = scheduledTick.field_9322.method_10081(blockPos);
				this.method_8668(blockPos2, scheduledTick.getObject(), (int)(scheduledTick.time - this.field_9301.method_8401().getTime()), scheduledTick.field_9320);
			}
		}
	}

	public ListTag method_8669(ChunkPos chunkPos) {
		List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(false, chunkPos);
		long l = this.field_9301.getTime();
		ListTag listTag = new ListTag();

		for (ScheduledTick<T> scheduledTick : list) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("i", ((Identifier)this.idToName.apply(scheduledTick.getObject())).toString());
			compoundTag.putInt("x", scheduledTick.field_9322.getX());
			compoundTag.putInt("y", scheduledTick.field_9322.getY());
			compoundTag.putInt("z", scheduledTick.field_9322.getZ());
			compoundTag.putInt("t", (int)(scheduledTick.time - l));
			compoundTag.putInt("p", scheduledTick.field_9320.getPriorityIndex());
			listTag.add(compoundTag);
		}

		return listTag;
	}

	public void method_8665(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			T object = (T)this.nameToId.apply(new Identifier(compoundTag.getString("i")));
			if (object != null) {
				this.method_8668(
					new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")),
					object,
					compoundTag.getInt("t"),
					TaskPriority.getByIndex(compoundTag.getInt("p"))
				);
			}
		}
	}

	@Override
	public boolean method_8674(BlockPos blockPos, T object) {
		return this.ticksScheduled.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void method_8675(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			this.method_8667(blockPos, object, i, taskPriority);
		}
	}

	protected void method_8668(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			this.method_8667(blockPos, object, i, taskPriority);
		}
	}

	private void method_8667(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		ScheduledTick<T> scheduledTick = new ScheduledTick<>(blockPos, object, (long)i + this.field_9301.getTime(), taskPriority);
		if (!this.ticksScheduled.contains(scheduledTick)) {
			this.ticksScheduled.add(scheduledTick);
			this.ticksScheduledOrdered.add(scheduledTick);
		}
	}
}
