package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
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
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectProcessor implements AutoCloseable {
	public static final Identifier MAIN = Identifier.ofVanilla("main");
	private final Identifier id;
	private final List<PostEffectPass> passes;
	private final Map<Identifier, PostEffectPipeline.Targets> internalTargets;
	private final Set<Identifier> externalTargets;
	private float time;

	private PostEffectProcessor(
		Identifier id, List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets
	) {
		this.id = id;
		this.passes = passes;
		this.internalTargets = internalTargets;
		this.externalTargets = externalTargets;
	}

	public static PostEffectProcessor parseEffect(
		ResourceFactory resourceFactory, TextureManager textureManager, Identifier id, Set<Identifier> availableExternalTargets
	) throws IOException, JsonSyntaxException {
		Resource resource = resourceFactory.getResourceOrThrow(id);

		try {
			Reader reader = resource.getReader();

			PostEffectProcessor var18;
			try {
				JsonObject jsonObject = JsonHelper.deserialize(reader);
				PostEffectPipeline postEffectPipeline = PostEffectPipeline.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(JsonSyntaxException::new);
				Stream<Identifier> stream = postEffectPipeline.passes().stream().flatMap(passx -> passx.inputs().stream()).flatMap(input -> input.getTargetId().stream());
				Set<Identifier> set = (Set<Identifier>)stream.filter(identifier -> !postEffectPipeline.internalTargets().containsKey(identifier))
					.collect(Collectors.toSet());
				Set<Identifier> set2 = Sets.<Identifier>difference(set, availableExternalTargets);
				if (!set2.isEmpty()) {
					throw new InvalidHierarchicalFileException("Referenced external targets are not available in this context: " + set2);
				}

				Builder<PostEffectPass> builder = ImmutableList.builder();

				for (PostEffectPipeline.Pass pass : postEffectPipeline.passes()) {
					builder.add(parsePass(resourceFactory, textureManager, pass));
				}

				var18 = new PostEffectProcessor(id, builder.build(), postEffectPipeline.internalTargets(), set);
			} catch (Throwable var15) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var14) {
						var15.addSuppressed(var14);
					}
				}

				throw var15;
			}

			if (reader != null) {
				reader.close();
			}

			return var18;
		} catch (Exception var16) {
			InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var16);
			invalidHierarchicalFileException.addInvalidFile(id.getPath() + " (" + resource.getPackId() + ")");
			throw invalidHierarchicalFileException;
		}
	}

	// $VF: Inserted dummy exception handlers to handle obfuscated exceptions
	private static PostEffectPass parsePass(ResourceFactory resourceFactory, TextureManager textureManager, PostEffectPipeline.Pass pass) throws IOException {
		PostEffectPass postEffectPass = new PostEffectPass(resourceFactory, pass.name(), pass.outputTarget());

		for (PostEffectPipeline.Input input : pass.inputs()) {
			Objects.requireNonNull(input);
			Throwable var43;
			switch (input) {
				case PostEffectPipeline.TextureSampler var8:
					PostEffectPipeline.TextureSampler var51 = var8;

					try {
						var52 = var51.samplerName();
					} catch (Throwable var28) {
						var43 = var28;
						boolean var64 = false;
						break;
					}

					String var33 = var52;
					PostEffectPipeline.TextureSampler var53 = var8;

					try {
						var54 = var53.location();
					} catch (Throwable var27) {
						var43 = var27;
						boolean var65 = false;
						break;
					}

					Identifier var34 = var54;
					Identifier identifier = var34;
					PostEffectPipeline.TextureSampler var55 = var8;

					try {
						var56 = var55.width();
					} catch (Throwable var26) {
						var43 = var26;
						boolean var66 = false;
						break;
					}

					int var35 = var56;
					PostEffectPipeline.TextureSampler var57 = var8;

					try {
						var58 = var57.height();
					} catch (Throwable var25) {
						var43 = var25;
						boolean var67 = false;
						break;
					}

					int var36 = var58;
					PostEffectPipeline.TextureSampler var59 = var8;

					try {
						var60 = var59.bilinear();
					} catch (Throwable var24) {
						var43 = var24;
						boolean var68 = false;
						break;
					}

					boolean var37 = var60;
					Identifier identifier2x = identifier.withPath((UnaryOperator<String>)(name -> "textures/effect/" + name + ".png"));
					resourceFactory.getResource(identifier2x).orElseThrow(() -> new InvalidHierarchicalFileException("Texture '" + identifier + "' does not exist"));
					RenderSystem.setShaderTexture(0, identifier2x);
					textureManager.bindTexture(identifier2x);
					AbstractTexture abstractTexture = textureManager.getTexture(identifier2x);
					if (var37) {
						RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
						RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
					} else {
						RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
						RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
					}

					postEffectPass.addSampler(new PostEffectPass.TextureSampler(var33, abstractTexture, var35, var36));
					continue;
				case PostEffectPipeline.TargetSampler identifier2:
					PostEffectPipeline.TargetSampler var10000 = identifier2;

					try {
						var44 = var10000.samplerName();
					} catch (Throwable var23) {
						var43 = var23;
						boolean var10001 = false;
						break;
					}

					String var19 = var44;
					PostEffectPipeline.TargetSampler var45 = identifier2;

					try {
						var46 = var45.targetId();
					} catch (Throwable var22) {
						var43 = var22;
						boolean var61 = false;
						break;
					}

					Identifier var40 = var46;
					PostEffectPipeline.TargetSampler var47 = identifier2;

					try {
						var48 = var47.useDepthBuffer();
					} catch (Throwable var21) {
						var43 = var21;
						boolean var62 = false;
						break;
					}

					boolean var41 = var48;
					PostEffectPipeline.TargetSampler var49 = identifier2;

					try {
						var50 = var49.bilinear();
					} catch (Throwable var20) {
						var43 = var20;
						boolean var63 = false;
						break;
					}

					boolean var42 = var50;
					postEffectPass.addSampler(new PostEffectPass.TargetSampler(var19, var40, var41, var42));
					continue;
				default:
					throw new MatchException(null, null);
			}

			Throwable var29 = var43;
			throw new MatchException(var29.toString(), var29);
		}

		for (PostEffectPipeline.Uniform uniform : pass.uniforms()) {
			String string3 = uniform.name();
			GlUniform glUniform = postEffectPass.getProgram().getUniformByName(string3);
			if (glUniform == null) {
				throw new InvalidHierarchicalFileException("Uniform '" + string3 + "' does not exist");
			}

			setUniform(glUniform, uniform.values());
		}

		return postEffectPass;
	}

	private static void setUniform(GlUniform uniform, List<Float> elements) {
		switch (elements.size()) {
			case 0:
			default:
				break;
			case 1:
				uniform.set((Float)elements.getFirst());
				break;
			case 2:
				uniform.set((Float)elements.get(0), (Float)elements.get(1));
				break;
			case 3:
				uniform.set((Float)elements.get(0), (Float)elements.get(1), (Float)elements.get(2));
				break;
			case 4:
				uniform.setAndFlip((Float)elements.get(0), (Float)elements.get(1), (Float)elements.get(2), (Float)elements.get(3));
		}
	}

	public void close() {
		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.close();
		}
	}

	// $VF: Inserted dummy exception handlers to handle obfuscated exceptions
	public void method_62234(FrameGraphBuilder frameGraphBuilder, RenderTickCounter renderTickCounter, int i, int j, PostEffectProcessor.FramebufferSet set) {
		Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float)i, 0.0F, (float)j, 0.1F, 1000.0F);
		this.time = this.time + renderTickCounter.getLastDuration();

		while (this.time > 20.0F) {
			this.time -= 20.0F;
		}

		Map<Identifier, Handle<Framebuffer>> map = new HashMap(this.internalTargets.size() + this.externalTargets.size());

		for (Identifier identifier : this.externalTargets) {
			map.put(identifier, set.getOrThrow(identifier));
		}

		for (Entry<Identifier, PostEffectPipeline.Targets> entry : this.internalTargets.entrySet()) {
			Identifier identifier2 = (Identifier)entry.getKey();
			PostEffectPipeline.Targets var36;
			Objects.requireNonNull(var36);
			Object var12 = var36;

			var36 = (PostEffectPipeline.Targets)entry.getValue();
			SimpleFramebufferFactory simpleFramebufferFactory = switch (var12) {
				case PostEffectPipeline.CustomSized var14 -> {
					PostEffectPipeline.CustomSized var30 = var14;

					int var27;
					label59: {
						label85: {
							try {
								var32 = var30.width();
							} catch (Throwable var19) {
								var31 = var19;
								boolean var10001 = false;
								break label85;
							}

							var27 = var32;
							PostEffectPipeline.CustomSized var33 = var14;

							try {
								var34 = var33.height();
								break label59;
							} catch (Throwable var18) {
								var31 = var18;
								boolean var35 = false;
							}
						}

						Throwable var21 = var31;
						throw new MatchException(var21.toString(), var21);
					}

					int var28 = var34;
					yield new SimpleFramebufferFactory(var27, var28, true);
				}
				case PostEffectPipeline.ScreenSized var17 -> new SimpleFramebufferFactory(i, j, true);
				default -> throw new MatchException(null, null);
			};
			map.put(identifier2, frameGraphBuilder.method_61912(identifier2.toString(), simpleFramebufferFactory));
		}

		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.method_62255(frameGraphBuilder, map, matrix4f, this.time / 20.0F);
		}

		for (Identifier identifier : this.externalTargets) {
			set.set(identifier, (Handle<Framebuffer>)map.get(identifier));
		}
	}

	@Deprecated
	public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator, RenderTickCounter renderTickCounter) {
		FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
		PostEffectProcessor.FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(
			MAIN, frameGraphBuilder.createObjectNode("main", framebuffer)
		);
		this.method_62234(frameGraphBuilder, renderTickCounter, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
		frameGraphBuilder.method_61909(objectAllocator);
	}

	public void setUniforms(String name, float value) {
		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.getProgram().getUniformByNameOrDummy(name).set(value);
		}
	}

	public final Identifier method_62231() {
		return this.id;
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
