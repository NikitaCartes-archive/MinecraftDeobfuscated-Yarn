package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class NeighborUpdateDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4622;
	private final Map<Long, Map<BlockPos, Integer>> field_4623 = Maps.newTreeMap(Ordering.natural().reverse());

	NeighborUpdateDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4622 = minecraftClient;
	}

	public void method_3870(long l, BlockPos blockPos) {
		Map<BlockPos, Integer> map = (Map<BlockPos, Integer>)this.field_4623.get(l);
		if (map == null) {
			map = Maps.<BlockPos, Integer>newHashMap();
			this.field_4623.put(l, map);
		}

		Integer integer = (Integer)map.get(blockPos);
		if (integer == null) {
			integer = 0;
		}

		map.put(blockPos, integer + 1);
	}
}
