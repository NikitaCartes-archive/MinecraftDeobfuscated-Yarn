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
	private final JsonGlProgram program;
	public final GlFramebuffer input;
	public final GlFramebuffer output;
	private final List<Object> samplerValues = Lists.<Object>newArrayList();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerWidths = Lists.<Integer>newArrayList();
	private final List<Integer> samplerHeights = Lists.<Integer>newArrayList();
	private Matrix4f projectionMatrix;

	public PostProcessShader(ResourceManager resourceManager, String string, GlFramebuffer glFramebuffer, GlFramebuffer glFramebuffer2) throws IOException {
		this.program = new JsonGlProgram(resourceManager, string);
		this.input = glFramebuffer;
		this.output = glFramebuffer2;
	}

	public void close() {
		this.program.close();
	}

	public void addAuxTarget(String string, Object object, int i, int j) {
		this.samplerNames.add(this.samplerNames.size(), string);
		this.samplerValues.add(this.samplerValues.size(), object);
		this.samplerWidths.add(this.samplerWidths.size(), i);
		this.samplerHeights.add(this.samplerHeights.size(), j);
	}

	private void setGlState() {
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
		this.setGlState();
		this.input.endWrite();
		float g = (float)this.output.texWidth;
		float h = (float)this.output.texHeight;
		GlStateManager.viewport(0, 0, (int)g, (int)h);
		this.program.bindSampler("DiffuseSampler", this.input);

		for (int i = 0; i < this.samplerValues.size(); i++) {
			this.program.bindSampler((String)this.samplerNames.get(i), this.samplerValues.get(i));
			this.program
				.getUniformByNameOrDummy("AuxSize" + i)
				.set((float)((Integer)this.samplerWidths.get(i)).intValue(), (float)((Integer)this.samplerHeights.get(i)).intValue());
		}

		this.program.getUniformByNameOrDummy("ProjMat").method_1250(this.projectionMatrix);
		this.program.getUniformByNameOrDummy("InSize").set((float)this.input.texWidth, (float)this.input.texHeight);
		this.program.getUniformByNameOrDummy("OutSize").set(g, h);
		this.program.getUniformByNameOrDummy("Time").set(f);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.program
			.getUniformByNameOrDummy("ScreenSize")
			.set((float)minecraftClient.window.getFramebufferWidth(), (float)minecraftClient.window.getFramebufferHeight());
		this.program.enable();
		this.output.clear(MinecraftClient.IS_SYSTEM_MAC);
		this.output.beginWrite(false);
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(7, VertexFormats.field_1576);
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

		for (Object object : this.samplerValues) {
			if (object instanceof GlFramebuffer) {
				((GlFramebuffer)object).endRead();
			}
		}
	}

	public JsonGlProgram getProgram() {
		return this.program;
	}
}
