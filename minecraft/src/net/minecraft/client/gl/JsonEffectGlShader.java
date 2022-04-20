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
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class JsonEffectGlShader implements EffectGlShader, AutoCloseable {
	private static final String PROGRAM_DIRECTORY = "shaders/program/";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Uniform DEFAULT_UNIFORM = new Uniform();
	private static final boolean field_32683 = true;
	private static JsonEffectGlShader activeShader;
	private static int activeProgramRef = -1;
	private final Map<String, IntSupplier> samplerBinds = Maps.<String, IntSupplier>newHashMap();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerShaderLocs = Lists.<Integer>newArrayList();
	private final List<GlUniform> uniformData = Lists.<GlUniform>newArrayList();
	private final List<Integer> uniformLocs = Lists.<Integer>newArrayList();
	private final Map<String, GlUniform> uniformByName = Maps.<String, GlUniform>newHashMap();
	private final int programRef;
	private final String name;
	private boolean uniformStateDirty;
	private final GlBlendState blendState;
	private final List<Integer> attribLocs;
	private final List<String> attribNames;
	private final EffectProgram vertexShader;
	private final EffectProgram fragmentShader;

	public JsonEffectGlShader(ResourceManager resource, String name) throws IOException {
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
							ShaderParseException shaderParseException = ShaderParseException.wrap(var20);
							shaderParseException.addFaultyElement("samplers[" + i + "]");
							throw shaderParseException;
						}

						i++;
					}
				}

				JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null);
				if (jsonArray2 != null) {
					int j = 0;
					this.attribLocs = Lists.<Integer>newArrayListWithCapacity(jsonArray2.size());
					this.attribNames = Lists.<String>newArrayListWithCapacity(jsonArray2.size());

					for (JsonElement jsonElement2 : jsonArray2) {
						try {
							this.attribNames.add(JsonHelper.asString(jsonElement2, "attribute"));
						} catch (Exception var19) {
							ShaderParseException shaderParseException2 = ShaderParseException.wrap(var19);
							shaderParseException2.addFaultyElement("attributes[" + j + "]");
							throw shaderParseException2;
						}

						j++;
					}
				} else {
					this.attribLocs = null;
					this.attribNames = null;
				}

				JsonArray jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null);
				if (jsonArray3 != null) {
					int k = 0;

					for (JsonElement jsonElement3 : jsonArray3) {
						try {
							this.addUniform(jsonElement3);
						} catch (Exception var18) {
							ShaderParseException shaderParseException3 = ShaderParseException.wrap(var18);
							shaderParseException3.addFaultyElement("uniforms[" + k + "]");
							throw shaderParseException3;
						}

						k++;
					}
				}

				this.blendState = deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
				this.vertexShader = loadEffect(resource, Program.Type.VERTEX, string);
				this.fragmentShader = loadEffect(resource, Program.Type.FRAGMENT, string2);
				this.programRef = GlProgramManager.createProgram();
				GlProgramManager.linkProgram(this);
				this.finalizeUniformsAndSamplers();
				if (this.attribNames != null) {
					for (String string3 : this.attribNames) {
						int l = GlUniform.getAttribLocation(this.programRef, string3);
						this.attribLocs.add(l);
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
			ShaderParseException shaderParseException4 = ShaderParseException.wrap(var22);
			shaderParseException4.addFaultyFile(identifier.getPath() + " (" + resource2.getResourcePackName() + ")");
			throw shaderParseException4;
		}

		this.markUniformsDirty();
	}

	public static EffectProgram loadEffect(ResourceManager resourceManager, Program.Type type, String name) throws IOException {
		Program program = (Program)type.getProgramCache().get(name);
		if (program != null && !(program instanceof EffectProgram)) {
			throw new InvalidClassException("Program is not of type EffectProgram");
		} else {
			EffectProgram effectProgram;
			if (program == null) {
				Identifier identifier = new Identifier("shaders/program/" + name + type.getFileExtension());
				Resource resource = resourceManager.getResourceOrThrow(identifier);
				InputStream inputStream = resource.getInputStream();

				try {
					effectProgram = EffectProgram.createFromResource(type, name, inputStream, resource.getResourcePackName());
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
				effectProgram = (EffectProgram)program;
			}

			return effectProgram;
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
		activeProgramRef = -1;
		activeShader = null;

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
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
		activeShader = this;
		this.blendState.enable();
		if (this.programRef != activeProgramRef) {
			GlProgramManager.useProgram(this.programRef);
			activeProgramRef = this.programRef;
		}

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			String string = (String)this.samplerNames.get(i);
			IntSupplier intSupplier = (IntSupplier)this.samplerBinds.get(string);
			if (intSupplier != null) {
				RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + i);
				RenderSystem.enableTexture();
				int j = intSupplier.getAsInt();
				if (j != -1) {
					RenderSystem.bindTexture(j);
					GlUniform.uniform1((Integer)this.samplerShaderLocs.get(i), i);
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
			int j = GlUniform.getUniformLocation(this.programRef, string);
			if (j == -1) {
				LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, string);
				this.samplerBinds.remove(string);
				intList.add(i);
			} else {
				this.samplerShaderLocs.add(j);
			}
		}

		for (int ix = intList.size() - 1; ix >= 0; ix--) {
			this.samplerNames.remove(intList.getInt(ix));
		}

		for (GlUniform glUniform : this.uniformData) {
			String string2 = glUniform.getName();
			int k = GlUniform.getUniformLocation(this.programRef, string2);
			if (k == -1) {
				LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, string2);
			} else {
				this.uniformLocs.add(k);
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

			this.uniformData.add(glUniform);
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

	public String getName() {
		return this.name;
	}

	@Override
	public int getProgramRef() {
		return this.programRef;
	}
}
