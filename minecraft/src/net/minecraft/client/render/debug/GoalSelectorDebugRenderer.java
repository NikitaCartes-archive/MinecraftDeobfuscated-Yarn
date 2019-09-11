package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		this.goalSelectors.forEach((integer, list) -> {
			for (int i = 0; i < list.size(); i++) {
				GoalSelectorDebugRenderer.class_4206 lv = (GoalSelectorDebugRenderer.class_4206)list.get(i);
				if (blockPos.isWithinDistance(lv.field_18782, 160.0)) {
					double d = (double)lv.field_18782.getX() + 0.5;
					double e = (double)lv.field_18782.getY() + 2.0 + (double)i * 0.25;
					double f = (double)lv.field_18782.getZ() + 0.5;
					int j = lv.field_18785 ? -16711936 : -3355444;
					DebugRenderer.drawFloatingText(lv.field_18784, d, e, f, j);
				}
			}
		});
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
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
