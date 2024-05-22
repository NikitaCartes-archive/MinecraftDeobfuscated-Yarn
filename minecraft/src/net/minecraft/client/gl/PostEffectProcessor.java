package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PostEffectProcessor implements AutoCloseable {
	private static final String MAIN_TARGET_NAME = "minecraft:main";
	private final Framebuffer mainTarget;
	private final ResourceFactory resourceFactory;
	private final String name;
	private final List<PostEffectPass> passes = Lists.<PostEffectPass>newArrayList();
	private final Map<String, Framebuffer> targetsByName = Maps.<String, Framebuffer>newHashMap();
	private final List<Framebuffer> defaultSizedTargets = Lists.<Framebuffer>newArrayList();
	private Matrix4f projectionMatrix;
	private int width;
	private int height;
	private float time;
	private float lastTickDelta;

	public PostEffectProcessor(TextureManager textureManager, ResourceFactory resourceFactory, Framebuffer framebuffer, Identifier id) throws IOException, JsonSyntaxException {
		this.resourceFactory = resourceFactory;
		this.mainTarget = framebuffer;
		this.time = 0.0F;
		this.lastTickDelta = 0.0F;
		this.width = framebuffer.viewportWidth;
		this.height = framebuffer.viewportHeight;
		this.name = id.toString();
		this.setupProjectionMatrix();
		this.parseEffect(textureManager, id);
	}

	private void parseEffect(TextureManager textureManager, Identifier id) throws IOException, JsonSyntaxException {
		Resource resource = this.resourceFactory.getResourceOrThrow(id);

		try {
			Reader reader = resource.getReader();

			try {
				JsonObject jsonObject = JsonHelper.deserialize(reader);
				if (JsonHelper.hasArray(jsonObject, "targets")) {
					JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
					int i = 0;

					for (JsonElement jsonElement : jsonArray) {
						try {
							this.parseTarget(jsonElement);
						} catch (Exception var14) {
							InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var14);
							invalidHierarchicalFileException.addInvalidKey("targets[" + i + "]");
							throw invalidHierarchicalFileException;
						}

						i++;
					}
				}

				if (JsonHelper.hasArray(jsonObject, "passes")) {
					JsonArray jsonArray = jsonObject.getAsJsonArray("passes");
					int i = 0;

					for (JsonElement jsonElement : jsonArray) {
						try {
							this.parsePass(textureManager, jsonElement);
						} catch (Exception var13) {
							InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var13);
							invalidHierarchicalFileException.addInvalidKey("passes[" + i + "]");
							throw invalidHierarchicalFileException;
						}

						i++;
					}
				}
			} catch (Throwable var15) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var12) {
						var15.addSuppressed(var12);
					}
				}

				throw var15;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (Exception var16) {
			InvalidHierarchicalFileException invalidHierarchicalFileException2 = InvalidHierarchicalFileException.wrap(var16);
			invalidHierarchicalFileException2.addInvalidFile(id.getPath() + " (" + resource.getPackId() + ")");
			throw invalidHierarchicalFileException2;
		}
	}

	private void parseTarget(JsonElement jsonTarget) throws InvalidHierarchicalFileException {
		if (JsonHelper.isString(jsonTarget)) {
			this.addTarget(jsonTarget.getAsString(), this.width, this.height);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonTarget, "target");
			String string = JsonHelper.getString(jsonObject, "name");
			int i = JsonHelper.getInt(jsonObject, "width", this.width);
			int j = JsonHelper.getInt(jsonObject, "height", this.height);
			if (this.targetsByName.containsKey(string)) {
				throw new InvalidHierarchicalFileException(string + " is already defined");
			}

			this.addTarget(string, i, j);
		}
	}

	private void parsePass(TextureManager textureManager, JsonElement jsonPass) throws IOException {
		JsonObject jsonObject = JsonHelper.asObject(jsonPass, "pass");
		String string = JsonHelper.getString(jsonObject, "name");
		String string2 = JsonHelper.getString(jsonObject, "intarget");
		String string3 = JsonHelper.getString(jsonObject, "outtarget");
		Framebuffer framebuffer = this.getTarget(string2);
		Framebuffer framebuffer2 = this.getTarget(string3);
		boolean bl = JsonHelper.getBoolean(jsonObject, "use_linear_filter", false);
		if (framebuffer == null) {
			throw new InvalidHierarchicalFileException("Input target '" + string2 + "' does not exist");
		} else if (framebuffer2 == null) {
			throw new InvalidHierarchicalFileException("Output target '" + string3 + "' does not exist");
		} else {
			PostEffectPass postEffectPass = this.addPass(string, framebuffer, framebuffer2, bl);
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "auxtarget");
						String string4 = JsonHelper.getString(jsonObject2, "name");
						String string5 = JsonHelper.getString(jsonObject2, "id");
						boolean bl2;
						String string6;
						if (string5.endsWith(":depth")) {
							bl2 = true;
							string6 = string5.substring(0, string5.lastIndexOf(58));
						} else {
							bl2 = false;
							string6 = string5;
						}

						Framebuffer framebuffer3 = this.getTarget(string6);
						if (framebuffer3 == null) {
							if (bl2) {
								throw new InvalidHierarchicalFileException("Render target '" + string6 + "' can't be used as depth buffer");
							}

							Identifier identifier = Identifier.ofVanilla("textures/effect/" + string6 + ".png");
							this.resourceFactory
								.getResource(identifier)
								.orElseThrow(() -> new InvalidHierarchicalFileException("Render target or texture '" + string6 + "' does not exist"));
							RenderSystem.setShaderTexture(0, identifier);
							textureManager.bindTexture(identifier);
							AbstractTexture abstractTexture = textureManager.getTexture(identifier);
							int j = JsonHelper.getInt(jsonObject2, "width");
							int k = JsonHelper.getInt(jsonObject2, "height");
							boolean bl3 = JsonHelper.getBoolean(jsonObject2, "bilinear");
							if (bl3) {
								RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
								RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
							} else {
								RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
								RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
							}

							postEffectPass.addAuxTarget(string4, abstractTexture::getGlId, j, k);
						} else if (bl2) {
							postEffectPass.addAuxTarget(string4, framebuffer3::getDepthAttachment, framebuffer3.textureWidth, framebuffer3.textureHeight);
						} else {
							postEffectPass.addAuxTarget(string4, framebuffer3::getColorAttachment, framebuffer3.textureWidth, framebuffer3.textureHeight);
						}
					} catch (Exception var27) {
						InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var27);
						invalidHierarchicalFileException.addInvalidKey("auxtargets[" + i + "]");
						throw invalidHierarchicalFileException;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null);
			if (jsonArray2 != null) {
				int l = 0;

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.parseUniform(jsonElement2);
					} catch (Exception var26) {
						InvalidHierarchicalFileException invalidHierarchicalFileException2 = InvalidHierarchicalFileException.wrap(var26);
						invalidHierarchicalFileException2.addInvalidKey("uniforms[" + l + "]");
						throw invalidHierarchicalFileException2;
					}

					l++;
				}
			}
		}
	}

	private void parseUniform(JsonElement jsonUniform) throws InvalidHierarchicalFileException {
		JsonObject jsonObject = JsonHelper.asObject(jsonUniform, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		GlUniform glUniform = ((PostEffectPass)this.passes.get(this.passes.size() - 1)).getProgram().getUniformByName(string);
		if (glUniform == null) {
			throw new InvalidHierarchicalFileException("Uniform '" + string + "' does not exist");
		} else {
			float[] fs = new float[4];
			int i = 0;

			for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "values")) {
				try {
					fs[i] = JsonHelper.asFloat(jsonElement, "value");
				} catch (Exception var12) {
					InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var12);
					invalidHierarchicalFileException.addInvalidKey("values[" + i + "]");
					throw invalidHierarchicalFileException;
				}

				i++;
			}

			switch (i) {
				case 0:
				default:
					break;
				case 1:
					glUniform.set(fs[0]);
					break;
				case 2:
					glUniform.set(fs[0], fs[1]);
					break;
				case 3:
					glUniform.set(fs[0], fs[1], fs[2]);
					break;
				case 4:
					glUniform.setAndFlip(fs[0], fs[1], fs[2], fs[3]);
			}
		}
	}

	public Framebuffer getSecondaryTarget(String name) {
		return (Framebuffer)this.targetsByName.get(name);
	}

	public void addTarget(String name, int width, int height) {
		Framebuffer framebuffer = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.targetsByName.put(name, framebuffer);
		if (width == this.width && height == this.height) {
			this.defaultSizedTargets.add(framebuffer);
		}
	}

	public void close() {
		for (Framebuffer framebuffer : this.targetsByName.values()) {
			framebuffer.delete();
		}

		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.close();
		}

		this.passes.clear();
	}

	public PostEffectPass addPass(String programName, Framebuffer source, Framebuffer dest, boolean linear) throws IOException {
		PostEffectPass postEffectPass = new PostEffectPass(this.resourceFactory, programName, source, dest, linear);
		this.passes.add(this.passes.size(), postEffectPass);
		return postEffectPass;
	}

	private void setupProjectionMatrix() {
		this.projectionMatrix = new Matrix4f().setOrtho(0.0F, (float)this.mainTarget.textureWidth, 0.0F, (float)this.mainTarget.textureHeight, 0.1F, 1000.0F);
	}

	public void setupDimensions(int targetsWidth, int targetsHeight) {
		this.width = this.mainTarget.textureWidth;
		this.height = this.mainTarget.textureHeight;
		this.setupProjectionMatrix();

		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.setProjectionMatrix(this.projectionMatrix);
		}

		for (Framebuffer framebuffer : this.defaultSizedTargets) {
			framebuffer.resize(targetsWidth, targetsHeight, MinecraftClient.IS_SYSTEM_MAC);
		}
	}

	private void setTexFilter(int texFilter) {
		this.mainTarget.setTexFilter(texFilter);

		for (Framebuffer framebuffer : this.targetsByName.values()) {
			framebuffer.setTexFilter(texFilter);
		}
	}

	public void render(float tickDelta) {
		this.time += tickDelta;

		while (this.time > 20.0F) {
			this.time -= 20.0F;
		}

		int i = GlConst.GL_NEAREST;

		for (PostEffectPass postEffectPass : this.passes) {
			int j = postEffectPass.getTexFilter();
			if (i != j) {
				this.setTexFilter(j);
				i = j;
			}

			postEffectPass.render(this.time / 20.0F);
		}

		this.setTexFilter(GlConst.GL_NEAREST);
	}

	public void setUniforms(String name, float value) {
		for (PostEffectPass postEffectPass : this.passes) {
			postEffectPass.getProgram().getUniformByNameOrDummy(name).set(value);
		}
	}

	public final String getName() {
		return this.name;
	}

	@Nullable
	private Framebuffer getTarget(@Nullable String name) {
		if (name == null) {
			return null;
		} else {
			return name.equals("minecraft:main") ? this.mainTarget : (Framebuffer)this.targetsByName.get(name);
		}
	}
}
