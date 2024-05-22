package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Window;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PathUtil;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.slf4j.Logger;

/**
 * Represents a shader program. Also known as a program object that can be
 * created with {@code glCreateProgram}.
 * 
 * <p><strong>Warning:</strong> This class is referred to as a shader in
 * strings. However, this does NOT represent a shader object that can be
 * created with {@code glCreateShader}. {@link ShaderStage} is what
 * represents a shader object.
 * 
 * @see <a href="https://www.khronos.org/opengl/wiki/GLSL_Object#Program_objects">
 * GLSL Object - OpenGL Wiki (Program objects)</a>
 */
@Environment(EnvType.CLIENT)
public class ShaderProgram implements ShaderProgramSetupView, AutoCloseable {
	public static final String SHADERS_DIRECTORY = "shaders";
	private static final String CORE_DIRECTORY = "shaders/core/";
	private static final String INCLUDE_DIRECTORY = "shaders/include/";
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Uniform DEFAULT_UNIFORM = new Uniform();
	private static final boolean field_32780 = true;
	private static ShaderProgram activeProgram;
	private static int activeProgramGlRef = -1;
	private final Map<String, Object> samplers = Maps.<String, Object>newHashMap();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> loadedSamplerIds = Lists.<Integer>newArrayList();
	private final List<GlUniform> uniforms = Lists.<GlUniform>newArrayList();
	private final List<Integer> loadedUniformIds = Lists.<Integer>newArrayList();
	private final Map<String, GlUniform> loadedUniforms = Maps.<String, GlUniform>newHashMap();
	private final int glRef;
	private final String name;
	private boolean dirty;
	private final ShaderStage vertexShader;
	private final ShaderStage fragmentShader;
	private final VertexFormat format;
	@Nullable
	public final GlUniform modelViewMat;
	@Nullable
	public final GlUniform projectionMat;
	@Nullable
	public final GlUniform textureMat;
	@Nullable
	public final GlUniform screenSize;
	@Nullable
	public final GlUniform colorModulator;
	@Nullable
	public final GlUniform light0Direction;
	@Nullable
	public final GlUniform light1Direction;
	@Nullable
	public final GlUniform glintAlpha;
	@Nullable
	public final GlUniform fogStart;
	@Nullable
	public final GlUniform fogEnd;
	@Nullable
	public final GlUniform fogColor;
	@Nullable
	public final GlUniform fogShape;
	@Nullable
	public final GlUniform lineWidth;
	@Nullable
	public final GlUniform gameTime;
	@Nullable
	public final GlUniform chunkOffset;

	public ShaderProgram(ResourceFactory factory, String name, VertexFormat format) throws IOException {
		this.name = name;
		this.format = format;
		Identifier identifier = Identifier.method_60656("shaders/core/" + name + ".json");

		try {
			Reader reader = factory.openAsReader(identifier);

			try {
				JsonObject jsonObject = JsonHelper.deserialize(reader);
				String string = JsonHelper.getString(jsonObject, "vertex");
				String string2 = JsonHelper.getString(jsonObject, "fragment");
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
				if (jsonArray != null) {
					int i = 0;

					for (JsonElement jsonElement : jsonArray) {
						try {
							this.readSampler(jsonElement);
						} catch (Exception var18) {
							InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var18);
							invalidHierarchicalFileException.addInvalidKey("samplers[" + i + "]");
							throw invalidHierarchicalFileException;
						}

						i++;
					}
				}

				JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null);
				if (jsonArray2 != null) {
					int j = 0;

					for (JsonElement jsonElement2 : jsonArray2) {
						try {
							this.addUniform(jsonElement2);
						} catch (Exception var17) {
							InvalidHierarchicalFileException invalidHierarchicalFileException2 = InvalidHierarchicalFileException.wrap(var17);
							invalidHierarchicalFileException2.addInvalidKey("uniforms[" + j + "]");
							throw invalidHierarchicalFileException2;
						}

						j++;
					}
				}

				this.vertexShader = loadShader(factory, ShaderStage.Type.VERTEX, string);
				this.fragmentShader = loadShader(factory, ShaderStage.Type.FRAGMENT, string2);
				this.glRef = GlProgramManager.createProgram();
				int j = 0;

				for (String string3 : format.getAttributeNames()) {
					GlUniform.bindAttribLocation(this.glRef, j, string3);
					j++;
				}

				GlProgramManager.linkProgram(this);
				this.loadReferences();
			} catch (Throwable var19) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var16) {
						var19.addSuppressed(var16);
					}
				}

				throw var19;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (Exception var20) {
			InvalidHierarchicalFileException invalidHierarchicalFileException3 = InvalidHierarchicalFileException.wrap(var20);
			invalidHierarchicalFileException3.addInvalidFile(identifier.getPath());
			throw invalidHierarchicalFileException3;
		}

		this.markUniformsDirty();
		this.modelViewMat = this.getUniform("ModelViewMat");
		this.projectionMat = this.getUniform("ProjMat");
		this.textureMat = this.getUniform("TextureMat");
		this.screenSize = this.getUniform("ScreenSize");
		this.colorModulator = this.getUniform("ColorModulator");
		this.light0Direction = this.getUniform("Light0_Direction");
		this.light1Direction = this.getUniform("Light1_Direction");
		this.glintAlpha = this.getUniform("GlintAlpha");
		this.fogStart = this.getUniform("FogStart");
		this.fogEnd = this.getUniform("FogEnd");
		this.fogColor = this.getUniform("FogColor");
		this.fogShape = this.getUniform("FogShape");
		this.lineWidth = this.getUniform("LineWidth");
		this.gameTime = this.getUniform("GameTime");
		this.chunkOffset = this.getUniform("ChunkOffset");
	}

	private static ShaderStage loadShader(ResourceFactory factory, ShaderStage.Type type, String name) throws IOException {
		ShaderStage shaderStage = (ShaderStage)type.getLoadedShaders().get(name);
		ShaderStage shaderStage2;
		if (shaderStage == null) {
			String string = "shaders/core/" + name + type.getFileExtension();
			Resource resource = factory.getResourceOrThrow(Identifier.method_60656(string));
			InputStream inputStream = resource.getInputStream();

			try {
				final String string2 = PathUtil.getPosixFullPath(string);
				shaderStage2 = ShaderStage.createFromResource(type, name, inputStream, resource.getPackId(), new GlImportProcessor() {
					private final Set<String> visitedImports = Sets.<String>newHashSet();

					@Override
					public String loadImport(boolean inline, String name) {
						name = PathUtil.normalizeToPosix((inline ? string2 : "shaders/include/") + name);
						if (!this.visitedImports.add(name)) {
							return null;
						} else {
							Identifier identifier = Identifier.method_60654(name);

							try {
								Reader reader = factory.openAsReader(identifier);

								String var5;
								try {
									var5 = IOUtils.toString(reader);
								} catch (Throwable var8) {
									if (reader != null) {
										try {
											reader.close();
										} catch (Throwable var7) {
											var8.addSuppressed(var7);
										}
									}

									throw var8;
								}

								if (reader != null) {
									reader.close();
								}

								return var5;
							} catch (IOException var9) {
								ShaderProgram.LOGGER.error("Could not open GLSL import {}: {}", name, var9.getMessage());
								return "#error " + var9.getMessage();
							}
						}
					}
				});
			} catch (Throwable var11) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var10) {
						var11.addSuppressed(var10);
					}
				}

				throw var11;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} else {
			shaderStage2 = shaderStage;
		}

		return shaderStage2;
	}

	public void close() {
		for (GlUniform glUniform : this.uniforms) {
			glUniform.close();
		}

		GlProgramManager.deleteProgram(this);
	}

	public void unbind() {
		RenderSystem.assertOnRenderThread();
		GlProgramManager.useProgram(0);
		activeProgramGlRef = -1;
		activeProgram = null;
		int i = GlStateManager._getActiveTexture();

		for (int j = 0; j < this.loadedSamplerIds.size(); j++) {
			if (this.samplers.get(this.samplerNames.get(j)) != null) {
				GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + j);
				GlStateManager._bindTexture(0);
			}
		}

		GlStateManager._activeTexture(i);
	}

	public void bind() {
		RenderSystem.assertOnRenderThread();
		this.dirty = false;
		activeProgram = this;
		if (this.glRef != activeProgramGlRef) {
			GlProgramManager.useProgram(this.glRef);
			activeProgramGlRef = this.glRef;
		}

		int i = GlStateManager._getActiveTexture();

		for (int j = 0; j < this.loadedSamplerIds.size(); j++) {
			String string = (String)this.samplerNames.get(j);
			if (this.samplers.get(string) != null) {
				int k = GlUniform.getUniformLocation(this.glRef, string);
				GlUniform.uniform1(k, j);
				RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + j);
				Object object = this.samplers.get(string);
				int l = -1;
				if (object instanceof Framebuffer) {
					l = ((Framebuffer)object).getColorAttachment();
				} else if (object instanceof AbstractTexture) {
					l = ((AbstractTexture)object).getGlId();
				} else if (object instanceof Integer) {
					l = (Integer)object;
				}

				if (l != -1) {
					RenderSystem.bindTexture(l);
				}
			}
		}

		GlStateManager._activeTexture(i);

		for (GlUniform glUniform : this.uniforms) {
			glUniform.upload();
		}
	}

	@Override
	public void markUniformsDirty() {
		this.dirty = true;
	}

	@Nullable
	public GlUniform getUniform(String name) {
		RenderSystem.assertOnRenderThread();
		return (GlUniform)this.loadedUniforms.get(name);
	}

	public Uniform getUniformOrDefault(String name) {
		GlUniform glUniform = this.getUniform(name);
		return (Uniform)(glUniform == null ? DEFAULT_UNIFORM : glUniform);
	}

	private void loadReferences() {
		RenderSystem.assertOnRenderThread();
		IntList intList = new IntArrayList();

		for (int i = 0; i < this.samplerNames.size(); i++) {
			String string = (String)this.samplerNames.get(i);
			int j = GlUniform.getUniformLocation(this.glRef, string);
			if (j == -1) {
				LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, string);
				this.samplers.remove(string);
				intList.add(i);
			} else {
				this.loadedSamplerIds.add(j);
			}
		}

		for (int ix = intList.size() - 1; ix >= 0; ix--) {
			int k = intList.getInt(ix);
			this.samplerNames.remove(k);
		}

		for (GlUniform glUniform : this.uniforms) {
			String string2 = glUniform.getName();
			int l = GlUniform.getUniformLocation(this.glRef, string2);
			if (l == -1) {
				LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, string2);
			} else {
				this.loadedUniformIds.add(l);
				glUniform.setLocation(l);
				this.loadedUniforms.put(string2, glUniform);
			}
		}
	}

	private void readSampler(JsonElement json) {
		JsonObject jsonObject = JsonHelper.asObject(json, "sampler");
		String string = JsonHelper.getString(jsonObject, "name");
		if (!JsonHelper.hasString(jsonObject, "file")) {
			this.samplers.put(string, null);
			this.samplerNames.add(string);
		} else {
			this.samplerNames.add(string);
		}
	}

	public void addSampler(String name, Object sampler) {
		this.samplers.put(name, sampler);
		this.markUniformsDirty();
	}

	private void addUniform(JsonElement json) throws InvalidHierarchicalFileException {
		JsonObject jsonObject = JsonHelper.asObject(json, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
		int j = JsonHelper.getInt(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new InvalidHierarchicalFileException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement : jsonArray) {
				try {
					fs[k] = JsonHelper.asFloat(jsonElement, "value");
				} catch (Exception var13) {
					InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var13);
					invalidHierarchicalFileException.addInvalidKey("values[" + k + "]");
					throw invalidHierarchicalFileException;
				}

				k++;
			}

			if (j > 1 && jsonArray.size() == 1) {
				while (k < j) {
					fs[k] = fs[0];
					k++;
				}
			}

			int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
			GlUniform glUniform = new GlUniform(string, i + l, j, this);
			if (i <= 3) {
				glUniform.setForDataType((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
			} else if (i <= 7) {
				glUniform.setForDataType(fs[0], fs[1], fs[2], fs[3]);
			} else {
				glUniform.set(Arrays.copyOfRange(fs, 0, j));
			}

			this.uniforms.add(glUniform);
		}
	}

	@Override
	public ShaderStage getVertexShader() {
		return this.vertexShader;
	}

	@Override
	public ShaderStage getFragmentShader() {
		return this.fragmentShader;
	}

	@Override
	public void attachReferencedShaders() {
		this.fragmentShader.attachTo(this);
		this.vertexShader.attachTo(this);
	}

	public VertexFormat getFormat() {
		return this.format;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int getGlRef() {
		return this.glRef;
	}

	public void method_60897(VertexFormat.DrawMode drawMode, Matrix4f matrix4f, Matrix4f matrix4f2, Window window) {
		for (int i = 0; i < 12; i++) {
			int j = RenderSystem.getShaderTexture(i);
			this.addSampler("Sampler" + i, j);
		}

		if (this.modelViewMat != null) {
			this.modelViewMat.set(matrix4f);
		}

		if (this.projectionMat != null) {
			this.projectionMat.set(matrix4f2);
		}

		if (this.colorModulator != null) {
			this.colorModulator.set(RenderSystem.getShaderColor());
		}

		if (this.glintAlpha != null) {
			this.glintAlpha.set(RenderSystem.getShaderGlintAlpha());
		}

		if (this.fogStart != null) {
			this.fogStart.set(RenderSystem.getShaderFogStart());
		}

		if (this.fogEnd != null) {
			this.fogEnd.set(RenderSystem.getShaderFogEnd());
		}

		if (this.fogColor != null) {
			this.fogColor.set(RenderSystem.getShaderFogColor());
		}

		if (this.fogShape != null) {
			this.fogShape.set(RenderSystem.getShaderFogShape().getId());
		}

		if (this.textureMat != null) {
			this.textureMat.set(RenderSystem.getTextureMatrix());
		}

		if (this.gameTime != null) {
			this.gameTime.set(RenderSystem.getShaderGameTime());
		}

		if (this.screenSize != null) {
			this.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
		}

		if (this.lineWidth != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
			this.lineWidth.set(RenderSystem.getShaderLineWidth());
		}

		RenderSystem.setupShaderLights(this);
	}
}
