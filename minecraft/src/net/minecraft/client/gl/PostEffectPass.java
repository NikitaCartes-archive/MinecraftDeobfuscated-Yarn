package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectPass implements AutoCloseable {
	private final JsonEffectShaderProgram program;
	public final Framebuffer input;
	public final Framebuffer output;
	private final List<IntSupplier> samplerValues = Lists.<IntSupplier>newArrayList();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerWidths = Lists.<Integer>newArrayList();
	private final List<Integer> samplerHeights = Lists.<Integer>newArrayList();
	private Matrix4f projectionMatrix;
	private final int texFilter;

	public PostEffectPass(ResourceFactory resourceFactory, String programName, Framebuffer input, Framebuffer output, boolean linear) throws IOException {
		this.program = new JsonEffectShaderProgram(resourceFactory, programName);
		this.input = input;
		this.output = output;
		this.texFilter = linear ? 9729 : 9728;
	}

	public void close() {
		this.program.close();
	}

	public final String getName() {
		return this.program.getName();
	}

	public void addAuxTarget(String name, IntSupplier valueSupplier, int width, int height) {
		this.samplerNames.add(this.samplerNames.size(), name);
		this.samplerValues.add(this.samplerValues.size(), valueSupplier);
		this.samplerWidths.add(this.samplerWidths.size(), width);
		this.samplerHeights.add(this.samplerHeights.size(), height);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public void render(float time) {
		this.input.endWrite();
		float f = (float)this.output.textureWidth;
		float g = (float)this.output.textureHeight;
		RenderSystem.viewport(0, 0, (int)f, (int)g);
		this.program.bindSampler("DiffuseSampler", this.input::getColorAttachment);

		for (int i = 0; i < this.samplerValues.size(); i++) {
			this.program.bindSampler((String)this.samplerNames.get(i), (IntSupplier)this.samplerValues.get(i));
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
			.set((float)minecraftClient.getWindow().getFramebufferWidth(), (float)minecraftClient.getWindow().getFramebufferHeight());
		this.program.enable();
		this.output.clear(MinecraftClient.IS_SYSTEM_MAC);
		this.output.beginWrite(false);
		RenderSystem.depthFunc(519);
		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(0.0F, 0.0F, 500.0F);
		bufferBuilder.vertex(f, 0.0F, 500.0F);
		bufferBuilder.vertex(f, g, 500.0F);
		bufferBuilder.vertex(0.0F, g, 500.0F);
		BufferRenderer.draw(bufferBuilder.end());
		RenderSystem.depthFunc(515);
		this.program.disable();
		this.output.endWrite();
		this.input.endRead();

		for (Object object : this.samplerValues) {
			if (object instanceof Framebuffer) {
				((Framebuffer)object).endRead();
			}
		}
	}

	public JsonEffectShaderProgram getProgram() {
		return this.program;
	}

	public int getTexFilter() {
		return this.texFilter;
	}
}
