package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectPass {
	private final String id;
	private final ShaderProgram program;
	private final Identifier outputTargetId;
	private final List<PostEffectPipeline.Uniform> uniforms;
	private final List<PostEffectPass.Sampler> samplers = new ArrayList();

	public PostEffectPass(String id, ShaderProgram program, Identifier outputTargetId, List<PostEffectPipeline.Uniform> uniforms) {
		this.id = id;
		this.program = program;
		this.outputTargetId = outputTargetId;
		this.uniforms = uniforms;
	}

	public void addSampler(PostEffectPass.Sampler sampler) {
		this.samplers.add(sampler);
	}

	public void render(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, Matrix4f projectionMatrix) {
		RenderPass renderPass = builder.createPass(this.id);

		for (PostEffectPass.Sampler sampler : this.samplers) {
			sampler.preRender(renderPass, handles);
		}

		Handle<Framebuffer> handle = (Handle<Framebuffer>)handles.computeIfPresent(this.outputTargetId, (id, handlex) -> renderPass.transfer(handlex));
		if (handle == null) {
			throw new IllegalStateException("Missing handle for target " + this.outputTargetId);
		} else {
			renderPass.setRenderer(() -> {
				Framebuffer framebuffer = handle.get();
				RenderSystem.viewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

				for (PostEffectPass.Sampler samplerx : this.samplers) {
					samplerx.bind(this.program, handles);
				}

				this.program.getUniformOrDefault("OutSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);

				for (PostEffectPipeline.Uniform uniform : this.uniforms) {
					GlUniform glUniform = this.program.getUniform(uniform.name());
					if (glUniform != null) {
						set(glUniform, uniform.values());
					}
				}

				framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				framebuffer.clear();
				framebuffer.beginWrite(false);
				RenderSystem.depthFunc(519);
				RenderSystem.setShader(this.program);
				RenderSystem.backupProjectionMatrix();
				RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_Z);
				BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
				bufferBuilder.vertex(0.0F, 0.0F, 500.0F);
				bufferBuilder.vertex((float)framebuffer.textureWidth, 0.0F, 500.0F);
				bufferBuilder.vertex((float)framebuffer.textureWidth, (float)framebuffer.textureHeight, 500.0F);
				bufferBuilder.vertex(0.0F, (float)framebuffer.textureHeight, 500.0F);
				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
				RenderSystem.depthFunc(515);
				RenderSystem.restoreProjectionMatrix();
				framebuffer.endWrite();

				for (PostEffectPass.Sampler sampler2 : this.samplers) {
					sampler2.postRender(handles);
				}
			});
		}
	}

	private static void set(GlUniform uniform, List<Float> values) {
		switch (values.size()) {
			case 1:
				uniform.set((Float)values.getFirst());
				break;
			case 2:
				uniform.set((Float)values.get(0), (Float)values.get(1));
				break;
			case 3:
				uniform.set((Float)values.get(0), (Float)values.get(1), (Float)values.get(2));
				break;
			case 4:
				uniform.setAndFlip((Float)values.get(0), (Float)values.get(1), (Float)values.get(2), (Float)values.get(3));
		}
	}

	public ShaderProgram getProgram() {
		return this.program;
	}

	@Environment(EnvType.CLIENT)
	public interface Sampler {
		void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets);

		void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets);

		default void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
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
		public void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			pass.dependsOn(this.getTarget(internalTargets));
		}

		@Override
		public void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			Handle<Framebuffer> handle = this.getTarget(internalTargets);
			Framebuffer framebuffer = handle.get();
			framebuffer.setTexFilter(this.bilinear ? 9729 : 9728);
			program.addSamplerTexture(this.samplerName + "Sampler", this.depthBuffer ? framebuffer.getDepthAttachment() : framebuffer.getColorAttachment());
			program.getUniformOrDefault(this.samplerName + "Size").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
		}

		@Override
		public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
			if (this.bilinear) {
				this.getTarget(internalTargets).get().setTexFilter(9728);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static record TextureSampler(String samplerName, AbstractTexture texture, int width, int height) implements PostEffectPass.Sampler {
		@Override
		public void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
		}

		@Override
		public void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
			program.addSamplerTexture(this.samplerName + "Sampler", this.texture.getGlId());
			program.getUniformOrDefault(this.samplerName + "Size").set((float)this.width, (float)this.height);
		}
	}
}
