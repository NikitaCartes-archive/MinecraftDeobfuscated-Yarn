package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Program;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Shader implements GlShader, AutoCloseable {
	private static final String field_32778 = "shaders/core/";
	private static final String field_32779 = "shaders/include/";
	static final Logger LOGGER = LogManager.getLogger();
	private static final Uniform DEFAULT_UNIFORM = new Uniform();
	private static final boolean field_32780 = true;
	private static Shader activeShader;
	private static int activeShaderId = -1;
	private final Map<String, Object> samplers = Maps.<String, Object>newHashMap();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> loadedSamplerIds = Lists.<Integer>newArrayList();
	private final List<GlUniform> uniforms = Lists.<GlUniform>newArrayList();
	private final List<Integer> loadedUniformIds = Lists.<Integer>newArrayList();
	private final Map<String, GlUniform> loadedUniforms = Maps.<String, GlUniform>newHashMap();
	private final int programId;
	private final String name;
	private boolean dirty;
	private final GlBlendState blendState;
	private final List<Integer> loadedAttributeIds;
	private final List<String> attributeNames;
	private final Program vertexShader;
	private final Program fragmentShader;
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
	public final GlUniform fogStart;
	@Nullable
	public final GlUniform fogEnd;
	@Nullable
	public final GlUniform fogColor;
	@Nullable
	public final GlUniform lineWidth;
	@Nullable
	public final GlUniform gameTime;
	@Nullable
	public final GlUniform chunkOffset;

	public Shader(ResourceFactory factory, String name, VertexFormat format) throws IOException {
		this.name = name;
		this.format = format;
		Identifier identifier = new Identifier("shaders/core/" + name + ".json");
		Resource resource = null;

		try {
			resource = factory.getResource(identifier);
			JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
			String string = JsonHelper.getString(jsonObject, "vertex");
			String string2 = JsonHelper.getString(jsonObject, "fragment");
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.readSampler(jsonElement);
					} catch (Exception var25) {
						ShaderParseException shaderParseException = ShaderParseException.wrap(var25);
						shaderParseException.addFaultyElement("samplers[" + i + "]");
						throw shaderParseException;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null);
			if (jsonArray2 != null) {
				int j = 0;
				this.loadedAttributeIds = Lists.<Integer>newArrayListWithCapacity(jsonArray2.size());
				this.attributeNames = Lists.<String>newArrayListWithCapacity(jsonArray2.size());

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.attributeNames.add(JsonHelper.asString(jsonElement2, "attribute"));
					} catch (Exception var24) {
						ShaderParseException shaderParseException2 = ShaderParseException.wrap(var24);
						shaderParseException2.addFaultyElement("attributes[" + j + "]");
						throw shaderParseException2;
					}

					j++;
				}
			} else {
				this.loadedAttributeIds = null;
				this.attributeNames = null;
			}

			JsonArray jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null);
			if (jsonArray3 != null) {
				int k = 0;

				for (JsonElement jsonElement3 : jsonArray3) {
					try {
						this.addUniform(jsonElement3);
					} catch (Exception var23) {
						ShaderParseException shaderParseException3 = ShaderParseException.wrap(var23);
						shaderParseException3.addFaultyElement("uniforms[" + k + "]");
						throw shaderParseException3;
					}

					k++;
				}
			}

			this.blendState = readBlendState(JsonHelper.getObject(jsonObject, "blend", null));
			this.vertexShader = loadProgram(factory, Program.Type.VERTEX, string);
			this.fragmentShader = loadProgram(factory, Program.Type.FRAGMENT, string2);
			this.programId = GlProgramManager.createProgram();
			if (this.attributeNames != null) {
				int k = 0;

				for (String string3 : format.getShaderAttributes()) {
					GlUniform.bindAttribLocation(this.programId, k, string3);
					this.loadedAttributeIds.add(k);
					k++;
				}
			}

			GlProgramManager.linkProgram(this);
			this.loadReferences();
		} catch (Exception var26) {
			ShaderParseException shaderParseException4 = ShaderParseException.wrap(var26);
			shaderParseException4.addFaultyFile(identifier.getPath());
			throw shaderParseException4;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.markUniformsDirty();
		this.modelViewMat = this.getUniform("ModelViewMat");
		this.projectionMat = this.getUniform("ProjMat");
		this.textureMat = this.getUniform("TextureMat");
		this.screenSize = this.getUniform("ScreenSize");
		this.colorModulator = this.getUniform("ColorModulator");
		this.light0Direction = this.getUniform("Light0_Direction");
		this.light1Direction = this.getUniform("Light1_Direction");
		this.fogStart = this.getUniform("FogStart");
		this.fogEnd = this.getUniform("FogEnd");
		this.fogColor = this.getUniform("FogColor");
		this.lineWidth = this.getUniform("LineWidth");
		this.gameTime = this.getUniform("GameTime");
		this.chunkOffset = this.getUniform("ChunkOffset");
	}

	private static Program loadProgram(ResourceFactory factory, Program.Type type, String name) throws IOException {
		Program program = (Program)type.getProgramCache().get(name);
		Program program2;
		if (program == null) {
			String string = "shaders/core/" + name + type.getFileExtension();
			Identifier identifier = new Identifier(string);
			Resource resource = factory.getResource(identifier);
			final String string2 = FileNameUtil.getPosixFullPath(string);

			try {
				program2 = Program.createFromResource(type, name, resource.getInputStream(), resource.getResourcePackName(), new GLImportProcessor() {
					private final Set<String> visitedImports = Sets.<String>newHashSet();

					@Override
					public String loadImport(boolean inline, String name) {
						name = FileNameUtil.normalizeToPosix((inline ? string2 : "shaders/include/") + name);
						if (!this.visitedImports.add(name)) {
							return null;
						} else {
							Identifier identifier = new Identifier(name);

							try {
								Resource resource = factory.getResource(identifier);

								String var5;
								try {
									var5 = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
								} catch (Throwable var8) {
									if (resource != null) {
										try {
											resource.close();
										} catch (Throwable var7) {
											var8.addSuppressed(var7);
										}
									}

									throw var8;
								}

								if (resource != null) {
									resource.close();
								}

								return var5;
							} catch (IOException var9) {
								Shader.LOGGER.error("Could not open GLSL import {}: {}", name, var9.getMessage());
								return "#error " + var9.getMessage();
							}
						}
					}
				});
			} finally {
				IOUtils.closeQuietly(resource);
			}
		} else {
			program2 = program;
		}

		return program2;
	}

	public static GlBlendState readBlendState(JsonObject json) {
		if (json == null) {
			return new GlBlendState();
		} else {
			int i = 32774;
			int j = 1;
			int k = 0;
			int l = 1;
			int m = 0;
			boolean bl = true;
			boolean bl2 = false;
			if (JsonHelper.hasString(json, "func")) {
				i = GlBlendState.getFuncFromString(json.get("func").getAsString());
				if (i != 32774) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcrgb")) {
				j = GlBlendState.getComponentFromString(json.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "dstrgb")) {
				k = GlBlendState.getComponentFromString(json.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcalpha")) {
				l = GlBlendState.getComponentFromString(json.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (JsonHelper.hasString(json, "dstalpha")) {
				m = GlBlendState.getComponentFromString(json.get("dstalpha").getAsString());
				if (m != 0) {
					bl = false;
				}

				bl2 = true;
			}

			if (bl) {
				return new GlBlendState();
			} else {
				return bl2 ? new GlBlendState(j, k, l, m, i) : new GlBlendState(j, k, i);
			}
		}
	}

	public void close() {
		for (GlUniform glUniform : this.uniforms) {
			glUniform.close();
		}

		GlProgramManager.deleteProgram(this);
	}

	public void bind() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlProgramManager.useProgram(0);
		activeShaderId = -1;
		activeShader = null;
		int i = GlStateManager._getActiveTexture();

		for (int j = 0; j < this.loadedSamplerIds.size(); j++) {
			if (this.samplers.get(this.samplerNames.get(j)) != null) {
				GlStateManager._activeTexture(33984 + j);
				GlStateManager._bindTexture(0);
			}
		}

		GlStateManager._activeTexture(i);
	}

	public void upload() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.dirty = false;
		activeShader = this;
		this.blendState.enable();
		if (this.programId != activeShaderId) {
			GlProgramManager.useProgram(this.programId);
			activeShaderId = this.programId;
		}

		int i = GlStateManager._getActiveTexture();

		for (int j = 0; j < this.loadedSamplerIds.size(); j++) {
			String string = (String)this.samplerNames.get(j);
			if (this.samplers.get(string) != null) {
				int k = GlUniform.getUniformLocation(this.programId, string);
				GlUniform.uniform1(k, j);
				RenderSystem.activeTexture(33984 + j);
				RenderSystem.enableTexture();
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
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return (GlUniform)this.loadedUniforms.get(name);
	}

	public Uniform method_35785(String string) {
		RenderSystem.assertThread(RenderSystem::isOnGameThread);
		GlUniform glUniform = this.getUniform(string);
		return (Uniform)(glUniform == null ? DEFAULT_UNIFORM : glUniform);
	}

	private void loadReferences() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		IntList intList = new IntArrayList();

		for (int i = 0; i < this.samplerNames.size(); i++) {
			String string = (String)this.samplerNames.get(i);
			int j = GlUniform.getUniformLocation(this.programId, string);
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
			int l = GlUniform.getUniformLocation(this.programId, string2);
			if (l == -1) {
				LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, string2);
			} else {
				this.loadedUniformIds.add(l);
				glUniform.setLoc(l);
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

	private void addUniform(JsonElement json) throws ShaderParseException {
		JsonObject jsonObject = JsonHelper.asObject(json, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
		int j = JsonHelper.getInt(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement : jsonArray) {
				try {
					fs[k] = JsonHelper.asFloat(jsonElement, "value");
				} catch (Exception var13) {
					ShaderParseException shaderParseException = ShaderParseException.wrap(var13);
					shaderParseException.addFaultyElement("values[" + k + "]");
					throw shaderParseException;
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
				glUniform.set(fs);
			}

			this.uniforms.add(glUniform);
		}
	}

	@Override
	public Program getVertexShader() {
		return this.vertexShader;
	}

	@Override
	public Program getFragmentShader() {
		return this.fragmentShader;
	}

	@Override
	public void attachReferencedShaders() {
		this.fragmentShader.attachTo(this);
		this.vertexShader.attachTo(this);
	}

	public VertexFormat method_35786() {
		return this.format;
	}

	public String method_35787() {
		return this.name;
	}

	@Override
	public int getProgramRef() {
		return this.programId;
	}
}
