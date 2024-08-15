package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9916;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectPass implements AutoCloseable {
	private final JsonEffectShaderProgram program;
	public final Identifier id;
	private final List<PostEffectPass.Sampler> samplers = new ArrayList();

	public PostEffectPass(ResourceFactory resourceFactory, String programName, Identifier id) throws IOException {
		this.program = new JsonEffectShaderProgram(resourceFactory, programName);
		this.id = id;
	}

	public void close() {
		this.program.close();
	}

	public final String getName() {
		return this.program.getName();
	}

	public void addSampler(PostEffectPass.Sampler sampler) {
		this.samplers.add(sampler);
	}

	public void method_62255(FrameGraphBuilder frameGraphBuilder, Map<Identifier, Handle<Framebuffer>> map, Matrix4f matrix4f, float timeSeconds) {
		class_9916 lv = frameGraphBuilder.createStageNode(this.getName());

		for (PostEffectPass.Sampler sampler : this.samplers) {
			sampler.method_62259(lv, map);
		}

		Handle<Framebuffer> handle = (Handle<Framebuffer>)map.computeIfPresent(this.id, (id, handlex) -> lv.method_61933(handlex));
		if (handle == null) {
			throw new IllegalStateException("Missing handle for target " + this.id);
		} else {
			lv.method_61929(
				() -> {
					Framebuffer framebuffer = handle.get();
					RenderSystem.viewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

					for (PostEffectPass.Sampler samplerx : this.samplers) {
						samplerx.bind(this.program, map);
					}

					this.program.getUniformByNameOrDummy("ProjMat").set(matrix4f);
					this.program.getUniformByNameOrDummy("OutSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
					this.program.getUniformByNameOrDummy("Time").set(timeSeconds);
					MinecraftClient minecraftClient = MinecraftClient.getInstance();
					this.program
						.getUniformByNameOrDummy("ScreenSize")
						.set((float)minecraftClient.getWindow().getFramebufferWidth(), (float)minecraftClient.getWindow().getFramebufferHeight());
					this.program.enable();
					framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
					framebuffer.clear();
					framebuffer.beginWrite(false);
					RenderSystem.depthFunc(519);
					BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
					bufferBuilder.vertex(0.0F, 0.0F, 500.0F);
					bufferBuilder.vertex((float)framebuffer.textureWidth, 0.0F, 500.0F);
					bufferBuilder.vertex((float)framebuffer.textureWidth, (float)framebuffer.textureHeight, 500.0F);
					bufferBuilder.vertex(0.0F, (float)framebuffer.textureHeight, 500.0F);
					BufferRenderer.draw(bufferBuilder.end());
					RenderSystem.depthFunc(515);
					this.program.disable();
					framebuffer.endWrite();

					for (PostEffectPass.Sampler sampler2 : this.samplers) {
						sampler2.method_62261(map);
					}
				}
			);
		}
	}

	public JsonEffectShaderProgram getProgram() {
		return this.program;
	}

	@Environment(EnvType.CLIENT)
	public interface Sampler {
		void method_62259(class_9916 arg, Map<Identifier, Handle<Framebuffer>> internalTargets);

		void bind(JsonEffectShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets);

		default void method_62261(Map<Identifier, Handle<Framebuffer>> internalTargets) {
		}
	}

	@Environment(EnvType.CLIENT)
	public static record TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements PostEffectPass.Sampler {
		private Handle<Framebuffer> getTarget(Map<Identifier, Handle<Framebuffer>> internalTargets) {
			Handle<Framebuffer> handle = (Handle<Framebuffer>)internalTargets.get(this.targetId);
			if (handle == null) {
				throw new IllegalStateException("Missing handle for target " + this.targetId);
			} else {
				return handle;
			}
		}

		@Override
		public void method_62259(class_9916 arg, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			arg.method_61928(this.getTarget(internalTargets));
		}

		@Override
		public void bind(JsonEffectShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			Handle<Framebuffer> handle = this.getTarget(internalTargets);
			Framebuffer framebuffer = handle.get();
			framebuffer.setTexFilter(this.bilinear ? 9729 : 9728);
			program.bindSampler(this.samplerName + "Sampler", this.depthBuffer ? framebuffer::getDepthAttachment : framebuffer::getColorAttachment);
			program.getUniformByNameOrDummy(this.samplerName + "Size").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
		}

		@Override
		public void method_62261(Map<Identifier, Handle<Framebuffer>> internalTargets) {
			if (this.bilinear) {
				this.getTarget(internalTargets).get().setTexFilter(9728);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static record TextureSampler(String samplerName, AbstractTexture texture, int width, int height) implements PostEffectPass.Sampler {
		@Override
		public void method_62259(class_9916 arg, Map<Identifier, Handle<Framebuffer>> internalTargets) {
		}

		@Override
		public void bind(JsonEffectShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			program.bindSampler(this.samplerName + "Sampler", this.texture::getGlId);
			program.getUniformByNameOrDummy(this.samplerName + "Size").set((float)this.width, (float)this.height);
		}
	}
}
