package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectProcessor {
	public static final Identifier MAIN = Identifier.ofVanilla("main");
	private final List<PostEffectPass> passes;
	private final Map<Identifier, PostEffectPipeline.Targets> internalTargets;
	private final Set<Identifier> externalTargets;

	private PostEffectProcessor(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets) {
		this.passes = passes;
		this.internalTargets = internalTargets;
		this.externalTargets = externalTargets;
	}

	public static PostEffectProcessor parseEffect(
		PostEffectPipeline pipeline, TextureManager textureManager, ShaderLoader shaderLoader, Set<Identifier> availableExternalTargets
	) throws ShaderLoader.LoadException {
		Stream<Identifier> stream = pipeline.passes().stream().flatMap(passx -> passx.inputs().stream()).flatMap(input -> input.getTargetId().stream());
		Set<Identifier> set = (Set<Identifier>)stream.filter(target -> !pipeline.internalTargets().containsKey(target)).collect(Collectors.toSet());
		Set<Identifier> set2 = Sets.<Identifier>difference(set, availableExternalTargets);
		if (!set2.isEmpty()) {
			throw new ShaderLoader.LoadException("Referenced external targets are not available in this context: " + set2);
		} else {
			Builder<PostEffectPass> builder = ImmutableList.builder();

			for (PostEffectPipeline.Pass pass : pipeline.passes()) {
				builder.add(parsePass(textureManager, shaderLoader, pass));
			}

			return new PostEffectProcessor(builder.build(), pipeline.internalTargets(), set);
		}
	}

	// $VF: Inserted dummy exception handlers to handle obfuscated exceptions
	private static PostEffectPass parsePass(TextureManager textureManager, ShaderLoader shaderLoader, PostEffectPipeline.Pass pass) throws ShaderLoader.LoadException {
		Identifier identifier = pass.program();
		ShaderProgram shaderProgram = shaderLoader.getOrCreateProgram(new ShaderProgramKey(identifier, VertexFormats.POSITION, Defines.EMPTY));
		if (shaderProgram == null) {
			throw new ShaderLoader.LoadException("Shader '" + identifier + "' could not be loaded");
		} else {
			for (PostEffectPipeline.Uniform uniform : pass.uniforms()) {
				String string = uniform.name();
				if (shaderProgram.getUniform(string) == null) {
					throw new ShaderLoader.LoadException("Uniform '" + string + "' does not exist for " + identifier);
				}
			}

			String string2 = identifier.toString();
			PostEffectPass postEffectPass = new PostEffectPass(string2, shaderProgram, pass.outputTarget(), pass.uniforms());

			for (PostEffectPipeline.Input input : pass.inputs()) {
				Objects.requireNonNull(input);
				Throwable var45;
				switch (input) {
					case PostEffectPipeline.TextureSampler var11:
						PostEffectPipeline.TextureSampler var53 = var11;

						try {
							var54 = var53.samplerName();
						} catch (Throwable var31) {
							var45 = var31;
							boolean var66 = false;
							break;
						}

						String var36 = var54;
						PostEffectPipeline.TextureSampler var55 = var11;

						try {
							var56 = var55.location();
						} catch (Throwable var30) {
							var45 = var30;
							boolean var67 = false;
							break;
						}

						Identifier var37 = var56;
						PostEffectPipeline.TextureSampler var57 = var11;

						try {
							var58 = var57.width();
						} catch (Throwable var29) {
							var45 = var29;
							boolean var68 = false;
							break;
						}

						int var38 = var58;
						PostEffectPipeline.TextureSampler var59 = var11;

						try {
							var60 = var59.height();
						} catch (Throwable var28) {
							var45 = var28;
							boolean var69 = false;
							break;
						}

						int var39 = var60;
						PostEffectPipeline.TextureSampler var61 = var11;

						try {
							var62 = var61.bilinear();
						} catch (Throwable var27) {
							var45 = var27;
							boolean var70 = false;
							break;
						}

						boolean var40 = var62;
						AbstractTexture abstractTexturex = textureManager.getTexture(var37.withPath((UnaryOperator<String>)(name -> "textures/effect/" + name + ".png")));
						abstractTexturex.setFilter(var40, false);
						postEffectPass.addSampler(new PostEffectPass.TextureSampler(var36, abstractTexturex, var38, var39));
						continue;
					case PostEffectPipeline.TargetSampler abstractTexture:
						PostEffectPipeline.TargetSampler var10000 = abstractTexture;

						try {
							var46 = var10000.samplerName();
						} catch (Throwable var26) {
							var45 = var26;
							boolean var10001 = false;
							break;
						}

						String var22 = var46;
						PostEffectPipeline.TargetSampler var47 = abstractTexture;

						try {
							var48 = var47.targetId();
						} catch (Throwable var25) {
							var45 = var25;
							boolean var63 = false;
							break;
						}

						Identifier var42 = var48;
						PostEffectPipeline.TargetSampler var49 = abstractTexture;

						try {
							var50 = var49.useDepthBuffer();
						} catch (Throwable var24) {
							var45 = var24;
							boolean var64 = false;
							break;
						}

						boolean var43 = var50;
						PostEffectPipeline.TargetSampler var51 = abstractTexture;

						try {
							var52 = var51.bilinear();
						} catch (Throwable var23) {
							var45 = var23;
							boolean var65 = false;
							break;
						}

						boolean var44 = var52;
						postEffectPass.addSampler(new PostEffectPass.TargetSampler(var22, var42, var43, var44));
						continue;
					default:
						throw new MatchException(null, null);
				}

				Throwable var35 = var45;
				throw new MatchException(var35.toString(), var35);
			}

			return postEffectPass;
		}
	}

	// $VF: Inserted dummy exception handlers to handle obfuscated exceptions
	public void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet) {
		Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float)textureWidth, 0.0F, (float)textureHeight, 0.1F, 1000.0F);
		Map<Identifier, Handle<Framebuffer>> map = new HashMap(this.internalTargets.size() + this.externalTargets.size());

		for (Identifier identifier : this.externalTargets) {
			map.put(identifier, framebufferSet.getOrThrow(identifier));
		}

		for (Entry<Identifier, PostEffectPipeline.Targets> entry : this.internalTargets.entrySet()) {
			Identifier identifier2 = (Identifier)entry.getKey();
			PostEffectPipeline.Targets var35;
			Objects.requireNonNull(var35);
			Object var11 = var35;

			var35 = (PostEffectPipeline.Targets)entry.getValue();
			SimpleFramebufferFactory simpleFramebufferFactory = switch (var11) {
				case PostEffectPipeline.CustomSized var13 -> {
					PostEffectPipeline.CustomSized var29 = var13;

					int var26;
					label56: {
						label76: {
							try {
								var31 = var29.width();
							} catch (Throwable var18) {
								var30 = var18;
								boolean var10001 = false;
								break label76;
							}

							var26 = var31;
							PostEffectPipeline.CustomSized var32 = var13;

							try {
								var33 = var32.height();
								break label56;
							} catch (Throwable var17) {
								var30 = var17;
								boolean var34 = false;
							}
						}

						Throwable var20 = var30;
						throw new MatchException(var20.toString(), var20);
					}

					int var27 = var33;
					yield new SimpleFramebufferFactory(var26, var27, true);
				}
				case PostEffectPipeline.ScreenSized var16 -> new SimpleFramebufferFactory(textureWidth, textureHeight, true);
				default -> throw new MatchException(null, null);
			};
			map.put(identifier2, builder.createResourceHandle(identifier2.toString(), simpleFramebufferFactory));
		}

		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.render(builder, map, matrix4f);
		}

		for (Identifier identifier : this.externalTargets) {
			framebufferSet.set(identifier, (Handle<Framebuffer>)map.get(identifier));
		}
	}

	@Deprecated
	public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
		FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
		PostEffectProcessor.FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(
			MAIN, frameGraphBuilder.createObjectNode("main", framebuffer)
		);
		this.render(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
		frameGraphBuilder.run(objectAllocator);
	}

	public void setUniforms(String name, float value) {
		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.getProgram().getUniformOrDefault(name).set(value);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface FramebufferSet {
		static PostEffectProcessor.FramebufferSet singleton(Identifier id, Handle<Framebuffer> framebuffer) {
			return new PostEffectProcessor.FramebufferSet() {
				private Handle<Framebuffer> framebuffer = framebuffer;

				@Override
				public void set(Identifier id, Handle<Framebuffer> framebuffer) {
					if (id.equals(id)) {
						this.framebuffer = framebuffer;
					} else {
						throw new IllegalArgumentException("No target with id " + id);
					}
				}

				@Nullable
				@Override
				public Handle<Framebuffer> get(Identifier id) {
					return id.equals(id) ? this.framebuffer : null;
				}
			};
		}

		void set(Identifier id, Handle<Framebuffer> framebuffer);

		@Nullable
		Handle<Framebuffer> get(Identifier id);

		default Handle<Framebuffer> getOrThrow(Identifier id) {
			Handle<Framebuffer> handle = this.get(id);
			if (handle == null) {
				throw new IllegalArgumentException("Missing target with id " + id);
			} else {
				return handle;
			}
		}
	}
}
