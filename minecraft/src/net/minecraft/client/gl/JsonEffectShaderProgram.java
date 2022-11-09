package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class JsonEffectShaderProgram implements EffectShaderProgram, AutoCloseable {
	private static final String PROGRAM_DIRECTORY = "shaders/program/";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Uniform DEFAULT_UNIFORM = new Uniform();
	private static final boolean field_32683 = true;
	private static JsonEffectShaderProgram activeProgram;
	private static int activeProgramGlRef = -1;
	private final Map<String, IntSupplier> samplerBinds = Maps.<String, IntSupplier>newHashMap();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerLocations = Lists.<Integer>newArrayList();
	private final List<GlUniform> uniformData = Lists.<GlUniform>newArrayList();
	private final List<Integer> uniformLocations = Lists.<Integer>newArrayList();
	private final Map<String, GlUniform> uniformByName = Maps.<String, GlUniform>newHashMap();
	private final int glRef;
	private final String name;
	private boolean uniformStateDirty;
	private final GlBlendState blendState;
	private final List<Integer> attributeLocations;
	private final List<String> attributeNames;
	private final EffectShaderStage vertexShader;
	private final EffectShaderStage fragmentShader;

	public JsonEffectShaderProgram(ResourceManager resource, String name) throws IOException {
		Identifier identifier = new Identifier("shaders/program/" + name + ".json");
		this.name = name;
		Resource resource2 = resource.getResourceOrThrow(identifier);

		try {
			Reader reader = resource2.getReader();

			try {
				JsonObject jsonObject = JsonHelper.deserialize(reader);
				String string = JsonHelper.getString(jsonObject, "vertex");
				String string2 = JsonHelper.getString(jsonObject, "fragment");
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
				if (jsonArray != null) {
					int i = 0;

					for (JsonElement jsonElement : jsonArray) {
						try {
							this.addSampler(jsonElement);
						} catch (Exception var20) {
							InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var20);
							invalidHierarchicalFileException.addInvalidKey("samplers[" + i + "]");
							throw invalidHierarchicalFileException;
						}

						i++;
					}
				}

				JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null);
				if (jsonArray2 != null) {
					int j = 0;
					this.attributeLocations = Lists.<Integer>newArrayListWithCapacity(jsonArray2.size());
					this.attributeNames = Lists.<String>newArrayListWithCapacity(jsonArray2.size());

					for (JsonElement jsonElement2 : jsonArray2) {
						try {
							this.attributeNames.add(JsonHelper.asString(jsonElement2, "attribute"));
						} catch (Exception var19) {
							InvalidHierarchicalFileException invalidHierarchicalFileException2 = InvalidHierarchicalFileException.wrap(var19);
							invalidHierarchicalFileException2.addInvalidKey("attributes[" + j + "]");
							throw invalidHierarchicalFileException2;
						}

						j++;
					}
				} else {
					this.attributeLocations = null;
					this.attributeNames = null;
				}

				JsonArray jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null);
				if (jsonArray3 != null) {
					int k = 0;

					for (JsonElement jsonElement3 : jsonArray3) {
						try {
							this.addUniform(jsonElement3);
						} catch (Exception var18) {
							InvalidHierarchicalFileException invalidHierarchicalFileException3 = InvalidHierarchicalFileException.wrap(var18);
							invalidHierarchicalFileException3.addInvalidKey("uniforms[" + k + "]");
							throw invalidHierarchicalFileException3;
						}

						k++;
					}
				}

				this.blendState = deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
				this.vertexShader = loadEffect(resource, ShaderStage.Type.VERTEX, string);
				this.fragmentShader = loadEffect(resource, ShaderStage.Type.FRAGMENT, string2);
				this.glRef = GlProgramManager.createProgram();
				GlProgramManager.linkProgram(this);
				this.finalizeUniformsAndSamplers();
				if (this.attributeNames != null) {
					for (String string3 : this.attributeNames) {
						int l = GlUniform.getAttribLocation(this.glRef, string3);
						this.attributeLocations.add(l);
					}
				}
			} catch (Throwable var21) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var17) {
						var21.addSuppressed(var17);
					}
				}

				throw var21;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (Exception var22) {
			InvalidHierarchicalFileException invalidHierarchicalFileException4 = InvalidHierarchicalFileException.wrap(var22);
			invalidHierarchicalFileException4.addInvalidFile(identifier.getPath() + " (" + resource2.getResourcePackName() + ")");
			throw invalidHierarchicalFileException4;
		}

		this.markUniformsDirty();
	}

	public static EffectShaderStage loadEffect(ResourceManager resourceManager, ShaderStage.Type type, String name) throws IOException {
		ShaderStage shaderStage = (ShaderStage)type.getLoadedShaders().get(name);
		if (shaderStage != null && !(shaderStage instanceof EffectShaderStage)) {
			throw new InvalidClassException("Program is not of type EffectProgram");
		} else {
			EffectShaderStage effectShaderStage;
			if (shaderStage == null) {
				Identifier identifier = new Identifier("shaders/program/" + name + type.getFileExtension());
				Resource resource = resourceManager.getResourceOrThrow(identifier);
				InputStream inputStream = resource.getInputStream();

				try {
					effectShaderStage = EffectShaderStage.createFromResource(type, name, inputStream, resource.getResourcePackName());
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
				effectShaderStage = (EffectShaderStage)shaderStage;
			}

			return effectShaderStage;
		}
	}

	public static GlBlendState deserializeBlendState(@Nullable JsonObject json) {
		if (json == null) {
			return new GlBlendState();
		} else {
			int i = GlConst.GL_FUNC_ADD;
			int j = 1;
			int k = 0;
			int l = 1;
			int m = 0;
			boolean bl = true;
			boolean bl2 = false;
			if (JsonHelper.hasString(json, "func")) {
				i = GlBlendState.getModeFromString(json.get("func").getAsString());
				if (i != GlConst.GL_FUNC_ADD) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcrgb")) {
				j = GlBlendState.getFactorFromString(json.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "dstrgb")) {
				k = GlBlendState.getFactorFromString(json.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcalpha")) {
				l = GlBlendState.getFactorFromString(json.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (JsonHelper.hasString(json, "dstalpha")) {
				m = GlBlendState.getFactorFromString(json.get("dstalpha").getAsString());
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
		for (GlUniform glUniform : this.uniformData) {
			glUniform.close();
		}

		GlProgramManager.deleteProgram(this);
	}

	public void disable() {
		RenderSystem.assertOnRenderThread();
		GlProgramManager.useProgram(0);
		activeProgramGlRef = -1;
		activeProgram = null;

		for (int i = 0; i < this.samplerLocations.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + i);
				GlStateManager._disableTexture();
				GlStateManager._bindTexture(0);
			}
		}
	}

	public void enable() {
		RenderSystem.assertOnGameThread();
		this.uniformStateDirty = false;
		activeProgram = this;
		this.blendState.enable();
		if (this.glRef != activeProgramGlRef) {
			GlProgramManager.useProgram(this.glRef);
			activeProgramGlRef = this.glRef;
		}

		for (int i = 0; i < this.samplerLocations.size(); i++) {
			String string = (String)this.samplerNames.get(i);
			IntSupplier intSupplier = (IntSupplier)this.samplerBinds.get(string);
			if (intSupplier != null) {
				RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + i);
				RenderSystem.enableTexture();
				int j = intSupplier.getAsInt();
				if (j != -1) {
					RenderSystem.bindTexture(j);
					GlUniform.uniform1((Integer)this.samplerLocations.get(i), i);
				}
			}
		}

		for (GlUniform glUniform : this.uniformData) {
			glUniform.upload();
		}
	}

	@Override
	public void markUniformsDirty() {
		this.uniformStateDirty = true;
	}

	@Nullable
	public GlUniform getUniformByName(String name) {
		RenderSystem.assertOnRenderThread();
		return (GlUniform)this.uniformByName.get(name);
	}

	public Uniform getUniformByNameOrDummy(String name) {
		RenderSystem.assertOnGameThread();
		GlUniform glUniform = this.getUniformByName(name);
		return (Uniform)(glUniform == null ? DEFAULT_UNIFORM : glUniform);
	}

	private void finalizeUniformsAndSamplers() {
		RenderSystem.assertOnRenderThread();
		IntList intList = new IntArrayList();

		for (int i = 0; i < this.samplerNames.size(); i++) {
			String string = (String)this.samplerNames.get(i);
			int j = GlUniform.getUniformLocation(this.glRef, string);
			if (j == -1) {
				LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, string);
				this.samplerBinds.remove(string);
				intList.add(i);
			} else {
				this.samplerLocations.add(j);
			}
		}

		for (int ix = intList.size() - 1; ix >= 0; ix--) {
			this.samplerNames.remove(intList.getInt(ix));
		}

		for (GlUniform glUniform : this.uniformData) {
			String string2 = glUniform.getName();
			int k = GlUniform.getUniformLocation(this.glRef, string2);
			if (k == -1) {
				LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, string2);
			} else {
				this.uniformLocations.add(k);
				glUniform.setLocation(k);
				this.uniformByName.put(string2, glUniform);
			}
		}
	}

	private void addSampler(JsonElement json) {
		JsonObject jsonObject = JsonHelper.asObject(json, "sampler");
		String string = JsonHelper.getString(jsonObject, "name");
		if (!JsonHelper.hasString(jsonObject, "file")) {
			this.samplerBinds.put(string, null);
			this.samplerNames.add(string);
		} else {
			this.samplerNames.add(string);
		}
	}

	public void bindSampler(String samplerName, IntSupplier intSupplier) {
		if (this.samplerBinds.containsKey(samplerName)) {
			this.samplerBinds.remove(samplerName);
		}

		this.samplerBinds.put(samplerName, intSupplier);
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
				glUniform.set(fs);
			}

			this.uniformData.add(glUniform);
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

	public String getName() {
		return this.name;
	}

	@Override
	public int getGlRef() {
		return this.glRef;
	}
}
