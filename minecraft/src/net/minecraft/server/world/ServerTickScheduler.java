package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
	private final TreeSet<ScheduledTick<T>> field_19341 = Sets.newTreeSet(ScheduledTick.method_20597());
	private final ServerWorld world;
	private final Queue<ScheduledTick<T>> ticksCurrent = Queues.<ScheduledTick<T>>newArrayDeque();
	private final List<ScheduledTick<T>> field_19338 = Lists.<ScheduledTick<T>>newArrayList();
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
		int i = this.field_19341.size();
		if (i != this.ticksScheduled.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			ServerChunkManager serverChunkManager = this.world.method_14178();
			Iterator<ScheduledTick<T>> iterator = this.field_19341.iterator();
			this.world.getProfiler().push("cleaning");

			while (i > 0 && iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				if (scheduledTick.time > this.world.getTime()) {
					break;
				}

				if (serverChunkManager.shouldTickBlock(scheduledTick.pos)) {
					iterator.remove();
					this.ticksScheduled.remove(scheduledTick);
					this.ticksCurrent.add(scheduledTick);
					i--;
				}
			}

			this.world.getProfiler().swap("ticking");

			ScheduledTick<T> scheduledTickx;
			while ((scheduledTickx = (ScheduledTick<T>)this.ticksCurrent.poll()) != null) {
				if (serverChunkManager.shouldTickBlock(scheduledTickx.pos)) {
					try {
						this.field_19338.add(scheduledTickx);
						this.tickConsumer.accept(scheduledTickx);
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
						CrashReportSection.addBlockInfo(crashReportSection, scheduledTickx.pos, null);
						throw new CrashException(crashReport);
					}
				} else {
					this.schedule(scheduledTickx.pos, scheduledTickx.getObject(), 0);
				}
			}

			this.world.getProfiler().pop();
			this.field_19338.clear();
			this.ticksCurrent.clear();
		}
	}

	@Override
	public boolean isTicking(BlockPos blockPos, T object) {
		return this.ticksCurrent.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void method_20470(Stream<ScheduledTick<T>> stream) {
		stream.forEach(this::method_20514);
	}

	public List<ScheduledTick<T>> getScheduledTicksInChunk(ChunkPos chunkPos, boolean bl, boolean bl2) {
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.method_8672(new MutableIntBoundingBox(i, 0, k, j, 256, l), bl, bl2);
	}

	public List<ScheduledTick<T>> method_8672(MutableIntBoundingBox mutableIntBoundingBox, boolean bl, boolean bl2) {
		List<ScheduledTick<T>> list = this.method_20596(null, this.field_19341, mutableIntBoundingBox, bl);
		if (bl && list != null) {
			this.ticksScheduled.removeAll(list);
		}

		list = this.method_20596(list, this.ticksCurrent, mutableIntBoundingBox, bl);
		if (!bl2) {
			list = this.method_20596(list, this.field_19338, mutableIntBoundingBox, bl);
		}

		return list == null ? Collections.emptyList() : list;
	}

	@Nullable
	private List<ScheduledTick<T>> method_20596(
		@Nullable List<ScheduledTick<T>> list, Collection<ScheduledTick<T>> collection, MutableIntBoundingBox mutableIntBoundingBox, boolean bl
	) {
		Iterator<ScheduledTick<T>> iterator = collection.iterator();

		while (iterator.hasNext()) {
			ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
			BlockPos blockPos = scheduledTick.pos;
			if (blockPos.getX() >= mutableIntBoundingBox.minX
				&& blockPos.getX() < mutableIntBoundingBox.maxX
				&& blockPos.getZ() >= mutableIntBoundingBox.minZ
				&& blockPos.getZ() < mutableIntBoundingBox.maxZ) {
				if (bl) {
					iterator.remove();
				}

				if (list == null) {
					list = Lists.<ScheduledTick<T>>newArrayList();
				}

				list.add(scheduledTick);
			}
		}

		return list;
	}

	public void method_8666(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos) {
		for (ScheduledTick<T> scheduledTick : this.method_8672(mutableIntBoundingBox, false, false)) {
			if (mutableIntBoundingBox.contains(scheduledTick.pos)) {
				BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
				T object = scheduledTick.getObject();
				this.method_20514(new ScheduledTick<>(blockPos2, object, scheduledTick.time, scheduledTick.field_9320));
			}
		}
	}

	public ListTag toTag(ChunkPos chunkPos) {
		List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(chunkPos, false, true);
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
			compoundTag.putInt("p", scheduledTick.field_9320.getPriorityIndex());
			listTag.add(compoundTag);
		}

		return listTag;
	}

	@Override
	public boolean isScheduled(BlockPos blockPos, T object) {
		return this.ticksScheduled.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void method_8675(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
		if (!this.invalidObjPredicate.test(object)) {
			this.method_20514(new ScheduledTick<>(blockPos, object, (long)i + this.world.getTime(), taskPriority));
		}
	}

	private void method_20514(ScheduledTick<T> scheduledTick) {
		if (!this.ticksScheduled.contains(scheduledTick)) {
			this.ticksScheduled.add(scheduledTick);
			this.field_19341.add(scheduledTick);
		}
	}
}
