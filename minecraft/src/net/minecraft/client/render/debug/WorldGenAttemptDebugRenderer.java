package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4634;
	private final List<BlockPos> field_4640 = Lists.<BlockPos>newArrayList();
	private final List<Float> field_4635 = Lists.<Float>newArrayList();
	private final List<Float> field_4637 = Lists.<Float>newArrayList();
	private final List<Float> field_4639 = Lists.<Float>newArrayList();
	private final List<Float> field_4636 = Lists.<Float>newArrayList();
	private final List<Float> field_4638 = Lists.<Float>newArrayList();

	public WorldGenAttemptDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4634 = minecraftClient;
	}

	public void method_3872(BlockPos blockPos, float f, float g, float h, float i, float j) {
		this.field_4640.add(blockPos);
		this.field_4635.add(f);
		this.field_4637.add(j);
		this.field_4639.add(g);
		this.field_4636.add(h);
		this.field_4638.add(i);
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4634.player;
		BlockView blockView = this.field_4634.world;
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < this.field_4640.size(); i++) {
			BlockPos blockPos2 = (BlockPos)this.field_4640.get(i);
			Float float_ = (Float)this.field_4635.get(i);
			float h = float_ / 2.0F;
			WorldRenderer.buildBox(
				bufferBuilder,
				(double)((float)blockPos2.getX() + 0.5F - h) - d,
				(double)((float)blockPos2.getY() + 0.5F - h) - e,
				(double)((float)blockPos2.getZ() + 0.5F - h) - g,
				(double)((float)blockPos2.getX() + 0.5F + h) - d,
				(double)((float)blockPos2.getY() + 0.5F + h) - e,
				(double)((float)blockPos2.getZ() + 0.5F + h) - g,
				(Float)this.field_4639.get(i),
				(Float)this.field_4636.get(i),
				(Float)this.field_4638.get(i),
				(Float)this.field_4637.get(i)
			);
		}

		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
