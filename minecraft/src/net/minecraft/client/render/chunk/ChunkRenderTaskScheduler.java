package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ChunkRenderTaskScheduler {
	private static final int field_53953 = 2;
	private int remainingPrioritizableTasks = 2;
	private final List<ChunkBuilder.BuiltChunk.Task> queue = new ObjectArrayList<>();

	public synchronized void enqueue(ChunkBuilder.BuiltChunk.Task task) {
		this.queue.add(task);
	}

	@Nullable
	public synchronized ChunkBuilder.BuiltChunk.Task dequeueNearest(Vec3d pos) {
		int i = -1;
		int j = -1;
		double d = Double.MAX_VALUE;
		double e = Double.MAX_VALUE;
		ListIterator<ChunkBuilder.BuiltChunk.Task> listIterator = this.queue.listIterator();

		while (listIterator.hasNext()) {
			int k = listIterator.nextIndex();
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)listIterator.next();
			if (task.cancelled.get()) {
				listIterator.remove();
			} else {
				double f = task.getOrigin().getSquaredDistance(pos);
				if (!task.isPrioritized() && f < d) {
					d = f;
					i = k;
				}

				if (task.isPrioritized() && f < e) {
					e = f;
					j = k;
				}
			}
		}

		boolean bl = j >= 0;
		boolean bl2 = i >= 0;
		if (!bl || bl2 && (this.remainingPrioritizableTasks <= 0 || !(e < d))) {
			this.remainingPrioritizableTasks = 2;
			return this.remove(i);
		} else {
			this.remainingPrioritizableTasks--;
			return this.remove(j);
		}
	}

	public int size() {
		return this.queue.size();
	}

	@Nullable
	private ChunkBuilder.BuiltChunk.Task remove(int index) {
		return index >= 0 ? (ChunkBuilder.BuiltChunk.Task)this.queue.remove(index) : null;
	}

	public synchronized void cancelAll() {
		for (ChunkBuilder.BuiltChunk.Task task : this.queue) {
			task.cancel();
		}

		this.queue.clear();
	}
}
