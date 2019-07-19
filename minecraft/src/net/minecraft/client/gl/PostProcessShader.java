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
	public final Framebuffer input;
	public final Framebuffer output;
	private final List<Object> samplerValues = Lists.<Object>newArrayList();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerWidths = Lists.<Integer>newArrayList();
	private final List<Integer> samplerHeights = Lists.<Integer>newArrayList();
	private Matrix4f projectionMatrix;

	public PostProcessShader(ResourceManager resourceManager, String programName, Framebuffer input, Framebuffer output) throws IOException {
		this.program = new JsonGlProgram(resourceManager, programName);
		this.input = input;
		this.output = output;
	}

	public void close() {
		this.program.close();
	}

	public void addAuxTarget(String name, Object target, int width, int height) {
		this.samplerNames.add(this.samplerNames.size(), name);
		this.samplerValues.add(this.samplerValues.size(), target);
		this.samplerWidths.add(this.samplerWidths.size(), width);
		this.samplerHeights.add(this.samplerHeights.size(), height);
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

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public void render(float time) {
		this.setGlState();
		this.input.endWrite();
		float f = (float)this.output.textureWidth;
		float g = (float)this.output.textureHeight;
		GlStateManager.viewport(0, 0, (int)f, (int)g);
		this.program.bindSampler("DiffuseSampler", this.input);

		for (int i = 0; i < this.samplerValues.size(); i++) {
			this.program.bindSampler((String)this.samplerNames.get(i), this.samplerValues.get(i));
			this.program
				.getUniformByNameOrDummy("AuxSize" + i)
				.set((float)((Integer)this.samplerWidths.get(i)).intValue(), (float)((Integer)this.samplerHeights.get(i)).intValue());
		}

		this.program.getUniformByNameOrDummy("ProjMat").set(this.projectionMatrix);
		this.program.getUniformByNameOrDummy("InSize").set((float)this.input.textureWidth, (float)this.input.textureHeight);
		this.program.getUniformByNameOrDummy("OutSize").set(f, g);
		this.program.getUniformByNameOrDummy("Time").set(time);
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
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, (double)g, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, (double)g, 500.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
		this.program.disable();
		this.output.endWrite();
		this.input.endRead();

		for (Object object : this.samplerValues) {
			if (object instanceof Framebuffer) {
				((Framebuffer)object).endRead();
			}
		}
	}

	public JsonGlProgram getProgram() {
		return this.program;
	}
}
