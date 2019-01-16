package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.resource.ResourceManager;

@Environment(EnvType.CLIENT)
public class PostProcessShader implements AutoCloseable {
	private final GlProgram program;
	public final GlFramebuffer input;
	public final GlFramebuffer output;
	private final List<Object> field_1534 = Lists.<Object>newArrayList();
	private final List<String> field_1539 = Lists.<String>newArrayList();
	private final List<Integer> field_1533 = Lists.<Integer>newArrayList();
	private final List<Integer> field_1537 = Lists.<Integer>newArrayList();
	private Matrix4f projectionMatrix;

	public PostProcessShader(ResourceManager resourceManager, String string, GlFramebuffer glFramebuffer, GlFramebuffer glFramebuffer2) throws IOException {
		this.program = new GlProgram(resourceManager, string);
		this.input = glFramebuffer;
		this.output = glFramebuffer2;
	}

	public void close() {
		this.program.close();
	}

	public void addAuxTarget(String string, Object object, int i, int j) {
		this.field_1539.add(this.field_1539.size(), string);
		this.field_1534.add(this.field_1534.size(), object);
		this.field_1533.add(this.field_1533.size(), i);
		this.field_1537.add(this.field_1537.size(), j);
	}

	private void method_1294() {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableFog();
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture();
		GlStateManager.bindTexture(0);
	}

	public void setProjectionMatrix(Matrix4f matrix4f) {
		this.projectionMatrix = matrix4f;
	}

	public void render(float f) {
		this.method_1294();
		this.input.endWrite();
		float g = (float)this.output.texWidth;
		float h = (float)this.output.texHeight;
		GlStateManager.viewport(0, 0, (int)g, (int)h);
		this.program.bindSampler("DiffuseSampler", this.input);

		for (int i = 0; i < this.field_1534.size(); i++) {
			this.program.bindSampler((String)this.field_1539.get(i), this.field_1534.get(i));
			this.program
				.getUniformByNameOrDummy("AuxSize" + i)
				.put((float)((Integer)this.field_1533.get(i)).intValue(), (float)((Integer)this.field_1537.get(i)).intValue());
		}

		this.program.getUniformByNameOrDummy("ProjMat").put(this.projectionMatrix);
		this.program.getUniformByNameOrDummy("InSize").put((float)this.input.texWidth, (float)this.input.texHeight);
		this.program.getUniformByNameOrDummy("OutSize").put(g, h);
		this.program.getUniformByNameOrDummy("Time").put(f);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.program
			.getUniformByNameOrDummy("ScreenSize")
			.put((float)minecraftClient.window.getFramebufferWidth(), (float)minecraftClient.window.getFramebufferHeight());
		this.program.enable();
		this.output.clear(MinecraftClient.isSystemMac);
		this.output.beginWrite(false);
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)g, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)g, (double)h, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, (double)h, 500.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
		this.program.disable();
		this.output.endWrite();
		this.input.endRead();

		for (Object object : this.field_1534) {
			if (object instanceof GlFramebuffer) {
				((GlFramebuffer)object).endRead();
			}
		}
	}

	public GlProgram getProgram() {
		return this.program;
	}
}
