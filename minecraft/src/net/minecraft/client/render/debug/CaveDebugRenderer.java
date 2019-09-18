package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4505;
	private final Map<BlockPos, BlockPos> field_4507 = Maps.<BlockPos, BlockPos>newHashMap();
	private final Map<BlockPos, Float> field_4508 = Maps.<BlockPos, Float>newHashMap();
	private final List<BlockPos> field_4506 = Lists.<BlockPos>newArrayList();

	public CaveDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4505 = minecraftClient;
	}

	public void method_3704(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
		for (int i = 0; i < list.size(); i++) {
			this.field_4507.put(list.get(i), blockPos);
			this.field_4508.put(list.get(i), list2.get(i));
		}

		this.field_4506.add(blockPos);
	}
}
