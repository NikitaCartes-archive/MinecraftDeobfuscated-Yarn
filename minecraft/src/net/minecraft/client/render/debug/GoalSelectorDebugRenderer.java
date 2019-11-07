package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private final Map<Integer, List<GoalSelectorDebugRenderer.GoalSelector>> goalSelectors = Maps.<Integer, List<GoalSelectorDebugRenderer.GoalSelector>>newHashMap();

	@Override
	public void clear() {
		this.goalSelectors.clear();
	}

	public void setGoalSelectorList(int i, List<GoalSelectorDebugRenderer.GoalSelector> list) {
		this.goalSelectors.put(i, list);
	}

	public GoalSelectorDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		this.goalSelectors.forEach((integer, list) -> {
			for (int i = 0; i < list.size(); i++) {
				GoalSelectorDebugRenderer.GoalSelector goalSelector = (GoalSelectorDebugRenderer.GoalSelector)list.get(i);
				if (blockPos.isWithinDistance(goalSelector.pos, 160.0)) {
					double dx = (double)goalSelector.pos.getX() + 0.5;
					double ex = (double)goalSelector.pos.getY() + 2.0 + (double)i * 0.25;
					double fx = (double)goalSelector.pos.getZ() + 0.5;
					int j = goalSelector.field_18785 ? -16711936 : -3355444;
					DebugRenderer.drawString(goalSelector.name, dx, ex, fx, j);
				}
			}
		});
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	public static class GoalSelector {
		public final BlockPos pos;
		public final int field_18783;
		public final String name;
		public final boolean field_18785;

		public GoalSelector(BlockPos blockPos, int i, String string, boolean bl) {
			this.pos = blockPos;
			this.field_18783 = i;
			this.name = string;
			this.field_18785 = bl;
		}
	}
}
