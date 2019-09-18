package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private final Map<Integer, List<GoalSelectorDebugRenderer.class_4206>> goalSelectors = Maps.<Integer, List<GoalSelectorDebugRenderer.class_4206>>newHashMap();

	@Override
	public void clear() {
		this.goalSelectors.clear();
	}

	public void setGoalSelectorList(int i, List<GoalSelectorDebugRenderer.class_4206> list) {
		this.goalSelectors.put(i, list);
	}

	public GoalSelectorDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4206 {
		public final BlockPos field_18782;
		public final int field_18783;
		public final String field_18784;
		public final boolean field_18785;

		public class_4206(BlockPos blockPos, int i, String string, boolean bl) {
			this.field_18782 = blockPos;
			this.field_18783 = i;
			this.field_18784 = string;
			this.field_18785 = bl;
		}
	}
}
