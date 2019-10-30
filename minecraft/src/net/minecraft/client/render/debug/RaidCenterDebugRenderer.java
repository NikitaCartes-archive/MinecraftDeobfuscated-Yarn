package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class RaidCenterDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private Collection<BlockPos> raidCenters = Lists.<BlockPos>newArrayList();

	public RaidCenterDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	public void setRaidCenters(Collection<BlockPos> centers) {
		this.raidCenters = centers;
	}

	@Override
	public void render(long limitTime) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.method_23124();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	private void method_23124() {
		BlockPos blockPos = this.method_23125().getBlockPos();

		for (BlockPos blockPos2 : this.raidCenters) {
			if (blockPos.isWithinDistance(blockPos2, 160.0)) {
				method_23122(blockPos2);
			}
		}
	}

	private static void method_23122(BlockPos blockPos) {
		DebugRenderer.drawBox(blockPos.add(-0.5, -0.5, -0.5), blockPos.add(1.5, 1.5, 1.5), 1.0F, 0.0F, 0.0F, 0.15F);
		int i = -65536;
		method_23123("Raid center", blockPos, -65536);
	}

	private static void method_23123(String string, BlockPos blockPos, int i) {
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 1.3;
		double f = (double)blockPos.getZ() + 0.5;
		DebugRenderer.drawString(string, d, e, f, i, 0.04F, true, 0.0F, true);
	}

	private Camera method_23125() {
		return this.client.gameRenderer.getCamera();
	}
}
