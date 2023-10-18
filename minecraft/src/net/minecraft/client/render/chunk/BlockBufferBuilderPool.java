package net.minecraft.client.render.chunk;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BlockBufferBuilderPool {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_46903 = 4;
	private final Queue<BlockBufferBuilderStorage> availableBuilders;
	private volatile int availableBuilderCount;

	private BlockBufferBuilderPool(List<BlockBufferBuilderStorage> availableBuilders) {
		this.availableBuilders = Queues.<BlockBufferBuilderStorage>newArrayDeque(availableBuilders);
		this.availableBuilderCount = this.availableBuilders.size();
	}

	public static BlockBufferBuilderPool allocate(int max) {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / BlockBufferBuilderStorage.EXPECTED_TOTAL_SIZE);
		int j = Math.max(1, Math.min(max, i));
		List<BlockBufferBuilderStorage> list = new ArrayList(j);

		try {
			for (int k = 0; k < j; k++) {
				list.add(new BlockBufferBuilderStorage());
			}
		} catch (OutOfMemoryError var7) {
			LOGGER.warn("Allocated only {}/{} buffers", list.size(), j);
			int l = Math.min(list.size() * 2 / 3, list.size() - 1);

			for (int m = 0; m < l; m++) {
				((BlockBufferBuilderStorage)list.remove(list.size() - 1)).close();
			}
		}

		return new BlockBufferBuilderPool(list);
	}

	@Nullable
	public BlockBufferBuilderStorage acquire() {
		BlockBufferBuilderStorage blockBufferBuilderStorage = (BlockBufferBuilderStorage)this.availableBuilders.poll();
		if (blockBufferBuilderStorage != null) {
			this.availableBuilderCount = this.availableBuilders.size();
			return blockBufferBuilderStorage;
		} else {
			return null;
		}
	}

	public void release(BlockBufferBuilderStorage builders) {
		this.availableBuilders.add(builders);
		this.availableBuilderCount = this.availableBuilders.size();
	}

	public boolean hasNoAvailableBuilder() {
		return this.availableBuilders.isEmpty();
	}

	public int getAvailableBuilderCount() {
		return this.availableBuilderCount;
	}
}
