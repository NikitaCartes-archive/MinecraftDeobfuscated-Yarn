package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer implements DebugRenderer.Renderer {
	private final List<BlockPos> field_4640 = Lists.<BlockPos>newArrayList();
	private final List<Float> field_4635 = Lists.<Float>newArrayList();
	private final List<Float> field_4637 = Lists.<Float>newArrayList();
	private final List<Float> field_4639 = Lists.<Float>newArrayList();
	private final List<Float> field_4636 = Lists.<Float>newArrayList();
	private final List<Float> field_4638 = Lists.<Float>newArrayList();

	public void method_3872(BlockPos blockPos, float f, float g, float h, float i, float j) {
		this.field_4640.add(blockPos);
		this.field_4635.add(f);
		this.field_4637.add(j);
		this.field_4639.add(g);
		this.field_4636.add(h);
		this.field_4638.add(i);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < this.field_4640.size(); i++) {
			BlockPos blockPos = (BlockPos)this.field_4640.get(i);
			Float float_ = (Float)this.field_4635.get(i);
			float f = float_ / 2.0F;
			WorldRenderer.drawBox(
				bufferBuilder,
				(double)((float)blockPos.getX() + 0.5F - f) - cameraX,
				(double)((float)blockPos.getY() + 0.5F - f) - cameraY,
				(double)((float)blockPos.getZ() + 0.5F - f) - cameraZ,
				(double)((float)blockPos.getX() + 0.5F + f) - cameraX,
				(double)((float)blockPos.getY() + 0.5F + f) - cameraY,
				(double)((float)blockPos.getZ() + 0.5F + f) - cameraZ,
				(Float)this.field_4639.get(i),
				(Float)this.field_4636.get(i),
				(Float)this.field_4638.get(i),
				(Float)this.field_4637.get(i)
			);
		}

		tessellator.draw();
		RenderSystem.enableTexture();
	}
}
