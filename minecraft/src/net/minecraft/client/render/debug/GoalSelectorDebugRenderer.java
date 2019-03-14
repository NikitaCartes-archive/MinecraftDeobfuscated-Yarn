package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private final Map<String, List<GoalSelectorDebugRenderer.GoalSelector>> goalSelectors = Maps.<String, List<GoalSelectorDebugRenderer.GoalSelector>>newHashMap();

	public void setGoalSelectorList(int i, String string, BlockPos blockPos, List<GoalSelectorDebugRenderer.GoalSelector> list) {
		this.goalSelectors.put(string, list);
	}

	public GoalSelectorDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.method_19418();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		this.goalSelectors.forEach((string, list) -> {
			for (int i = 0; i < list.size(); i++) {
				GoalSelectorDebugRenderer.GoalSelector goalSelector = (GoalSelectorDebugRenderer.GoalSelector)list.get(i);
				if (blockPos.distanceTo(goalSelector.field_18782) < 160.0) {
					double d = (double)goalSelector.field_18782.getX() + 0.5;
					double e = (double)goalSelector.field_18782.getY() + 2.0 + (double)i * 0.25;
					double f = (double)goalSelector.field_18782.getZ() + 0.5;
					int j = goalSelector.field_18785 ? -16711936 : -3355444;
					DebugRenderer.method_3714(goalSelector.field_18784, d, e, f, j);
				}
			}
		});
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	public static class GoalSelector {
		public final BlockPos field_18782;
		public final int field_18783;
		public final String field_18784;
		public final boolean field_18785;

		public GoalSelector(BlockPos blockPos, int i, String string, boolean bl) {
			this.field_18782 = blockPos;
			this.field_18783 = i;
			this.field_18784 = string;
			this.field_18785 = bl;
		}
	}
}
