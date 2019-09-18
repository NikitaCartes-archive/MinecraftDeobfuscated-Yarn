package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class JsonGlProgram implements GlProgram, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Uniform dummyUniform = new Uniform();
	private static JsonGlProgram activeProgram;
	private static int activeProgramRef = -1;
	private final Map<String, Object> samplerBinds = Maps.<String, Object>newHashMap();
	private final List<String> samplerNames = Lists.<String>newArrayList();
	private final List<Integer> samplerShaderLocs = Lists.<Integer>newArrayList();
	private final List<GlUniform> uniformData = Lists.<GlUniform>newArrayList();
	private final List<Integer> uniformLocs = Lists.<Integer>newArrayList();
	private final Map<String, GlUniform> uniformByName = Maps.<String, GlUniform>newHashMap();
	private final int programRef;
	private final String name;
	private final boolean useCullFace;
	private boolean uniformStateDirty;
	private final GlBlendState blendState;
	private final List<Integer> attribLocs;
	private final List<String> attribNames;
	private final GlShader vertexShader;
	private final GlShader fragmentShader;

	public JsonGlProgram(ResourceManager resourceManager, String string) throws IOException {
		Identifier identifier = new Identifier("shaders/program/" + string + ".json");
		this.name = string;
		Resource resource = null;

		try {
			resource = resourceManager.getResource(identifier);
			JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
			String string2 = JsonHelper.getString(jsonObject, "vertex");
			String string3 = JsonHelper.getString(jsonObject, "fragment");
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.addSampler(jsonElement);
					} catch (Exception var24) {
						ShaderParseException shaderParseException = ShaderParseException.wrap(var24);
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
					} catch (Exception var23) {
						ShaderParseException shaderParseException2 = ShaderParseException.wrap(var23);
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
					} catch (Exception var22) {
						ShaderParseException shaderParseException3 = ShaderParseException.wrap(var22);
						shaderParseException3.addFaultyElement("uniforms[" + k + "]");
						throw shaderParseException3;
					}

					k++;
				}
			}

			this.blendState = deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
			this.useCullFace = JsonHelper.getBoolean(jsonObject, "cull", true);
			this.vertexShader = getShader(resourceManager, GlShader.Type.VERTEX, string2);
			this.fragmentShader = getShader(resourceManager, GlShader.Type.FRAGMENT, string3);
			this.programRef = GlProgramManager.createProgram();
			GlProgramManager.linkProgram(this);
			this.finalizeUniformsAndSamplers();
			if (this.attribNames != null) {
				for (String string4 : this.attribNames) {
					int l = GlUniform.getAttribLocation(this.programRef, string4);
					this.attribLocs.add(l);
				}
			}
		} catch (Exception var25) {
			ShaderParseException shaderParseException4 = ShaderParseException.wrap(var25);
			shaderParseException4.addFaultyFile(identifier.getPath());
			throw shaderParseException4;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.markUniformsDirty();
	}

	public static GlShader getShader(ResourceManager resourceManager, GlShader.Type type, String string) throws IOException {
		GlShader glShader = (GlShader)type.getLoadedShaders().get(string);
		if (glShader == null) {
			Identifier identifier = new Identifier("shaders/program/" + string + type.getFileExtension());
			Resource resource = resourceManager.getResource(identifier);

			try {
				glShader = GlShader.createFromResource(type, string, resource.getInputStream());
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}

		return glShader;
	}

	public static GlBlendState deserializeBlendState(JsonObject jsonObject) {
		if (jsonObject == null) {
			return new GlBlendState();
		} else {
			int i = 32774;
			int j = 1;
			int k = 0;
			int l = 1;
			int m = 0;
			boolean bl = true;
			boolean bl2 = false;
			if (JsonHelper.hasString(jsonObject, "func")) {
				i = GlBlendState.getFuncFromString(jsonObject.get("func").getAsString());
				if (i != 32774) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(jsonObject, "srcrgb")) {
				j = GlBlendState.getComponentFromString(jsonObject.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(jsonObject, "dstrgb")) {
				k = GlBlendState.getComponentFromString(jsonObject.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(jsonObject, "srcalpha")) {
				l = GlBlendState.getComponentFromString(jsonObject.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (JsonHelper.hasString(jsonObject, "dstalpha")) {
				m = GlBlendState.getComponentFromString(jsonObject.get("dstalpha").getAsString());
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
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlProgramManager.useProgram(0);
		activeProgramRef = -1;
		activeProgram = null;

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager.activeTexture(33984 + i);
				GlStateManager.bindTexture(0);
			}
		}
	}

	public void enable() {
		RenderSystem.assertThread(RenderSystem::isOnGameThread);
		this.uniformStateDirty = false;
		activeProgram = this;
		this.blendState.enable();
		if (this.programRef != activeProgramRef) {
			GlProgramManager.useProgram(this.programRef);
			activeProgramRef = this.programRef;
		}

		if (this.useCullFace) {
			RenderSystem.enableCull();
		} else {
			RenderSystem.disableCull();
		}

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				RenderSystem.activeTexture(33984 + i);
				RenderSystem.enableTexture();
				Object object = this.samplerBinds.get(this.samplerNames.get(i));
				int j = -1;
				if (object instanceof GlFramebuffer) {
					j = ((GlFramebuffer)object).colorAttachment;
				} else if (object instanceof Texture) {
					j = ((Texture)object).getGlId();
				} else if (object instanceof Integer) {
					j = (Integer)object;
				}

				if (j != -1) {
					RenderSystem.bindTexture(j);
					GlUniform.uniform1(GlUniform.getUniformLocation(this.programRef, (CharSequence)this.samplerNames.get(i)), i);
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
	public GlUniform getUniformByName(String string) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		return (GlUniform)this.uniformByName.get(string);
	}

	public Uniform getUniformByNameOrDummy(String string) {
		RenderSystem.assertThread(RenderSystem::isOnGameThread);
		GlUniform glUniform = this.getUniformByName(string);
		return (Uniform)(glUniform == null ? dummyUniform : glUniform);
	}

	private void finalizeUniformsAndSamplers() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = 0;

		for (int j = 0; i < this.samplerNames.size(); j++) {
			String string = (String)this.samplerNames.get(i);
			int k = GlUniform.getUniformLocation(this.programRef, string);
			if (k == -1) {
				LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", this.name, string);
				this.samplerBinds.remove(string);
				this.samplerNames.remove(j);
				j--;
			} else {
				this.samplerShaderLocs.add(k);
			}

			i++;
		}

		for (GlUniform glUniform : this.uniformData) {
			String string = glUniform.getName();
			int k = GlUniform.getUniformLocation(this.programRef, string);
			if (k == -1) {
				LOGGER.warn("Could not find uniform named {} in the specified shader program.", string);
			} else {
				this.uniformLocs.add(k);
				glUniform.setLoc(k);
				this.uniformByName.put(string, glUniform);
			}
		}
	}

	private void addSampler(JsonElement jsonElement) {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "sampler");
		String string = JsonHelper.getString(jsonObject, "name");
		if (!JsonHelper.hasString(jsonObject, "file")) {
			this.samplerBinds.put(string, null);
			this.samplerNames.add(string);
		} else {
			this.samplerNames.add(string);
		}
	}

	public void bindSampler(String string, Object object) {
		if (this.samplerBinds.containsKey(string)) {
			this.samplerBinds.remove(string);
		}

		this.samplerBinds.put(string, object);
		this.markUniformsDirty();
	}

	private void addUniform(JsonElement jsonElement) throws ShaderParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
		int j = JsonHelper.getInt(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement2 : jsonArray) {
				try {
					fs[k] = JsonHelper.asFloat(jsonElement2, "value");
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
				glUniform.set((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
			} else if (i <= 7) {
				glUniform.setForDataType(fs[0], fs[1], fs[2], fs[3]);
			} else {
				glUniform.set(fs);
			}

			this.uniformData.add(glUniform);
		}
	}

	@Override
	public GlShader getVertexShader() {
		return this.vertexShader;
	}

	@Override
	public GlShader getFragmentShader() {
		return this.fragmentShader;
	}

	@Override
	public int getProgramRef() {
		return this.programRef;
	}
}
