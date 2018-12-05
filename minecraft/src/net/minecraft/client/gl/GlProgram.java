package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_278;
import net.minecraft.class_2973;
import net.minecraft.class_3679;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GlProgram implements class_3679, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final class_278 dummyUniform = new class_278();
	private static GlProgram activeProgram;
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

	public GlProgram(ResourceManager resourceManager, String string) throws IOException {
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
						class_2973 lv = class_2973.method_12856(var24);
						lv.method_12854("samplers[" + i + "]");
						throw lv;
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
						class_2973 lv2 = class_2973.method_12856(var23);
						lv2.method_12854("attributes[" + j + "]");
						throw lv2;
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
						class_2973 lv3 = class_2973.method_12856(var22);
						lv3.method_12854("uniforms[" + k + "]");
						throw lv3;
					}

					k++;
				}
			}

			this.blendState = method_16035(JsonHelper.getObject(jsonObject, "blend", null));
			this.useCullFace = JsonHelper.getBoolean(jsonObject, "cull", true);
			this.vertexShader = method_16036(resourceManager, GlShader.Type.VERTEX, string2);
			this.fragmentShader = method_16036(resourceManager, GlShader.Type.FRAGMENT, string3);
			this.programRef = GlProgramManager.getInstance().createProgram();
			GlProgramManager.getInstance().linkProgram(this);
			this.finalizeUniformsAndSamplers();
			if (this.attribNames != null) {
				for (String string4 : this.attribNames) {
					int l = GLX.glGetAttribLocation(this.programRef, string4);
					this.attribLocs.add(l);
				}
			}
		} catch (Exception var25) {
			class_2973 lv4 = class_2973.method_12856(var25);
			lv4.method_12855(identifier.getPath());
			throw lv4;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.markUniformStateDirty();
	}

	public static GlShader method_16036(ResourceManager resourceManager, GlShader.Type type, String string) throws IOException {
		GlShader glShader = (GlShader)type.getNameObjectMap().get(string);
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

	public static GlBlendState method_16035(JsonObject jsonObject) {
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
			if (JsonHelper.method_15289(jsonObject, "func")) {
				i = GlBlendState.getFuncFromString(jsonObject.get("func").getAsString());
				if (i != 32774) {
					bl = false;
				}
			}

			if (JsonHelper.method_15289(jsonObject, "srcrgb")) {
				j = GlBlendState.getComponentFromString(jsonObject.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (JsonHelper.method_15289(jsonObject, "dstrgb")) {
				k = GlBlendState.getComponentFromString(jsonObject.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (JsonHelper.method_15289(jsonObject, "srcalpha")) {
				l = GlBlendState.getComponentFromString(jsonObject.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (JsonHelper.method_15289(jsonObject, "dstalpha")) {
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

		GlProgramManager.getInstance().deleteProgram(this);
	}

	public void disable() {
		GLX.glUseProgram(0);
		activeProgramRef = -1;
		activeProgram = null;

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager.activeTexture(GLX.GL_TEXTURE0 + i);
				GlStateManager.bindTexture(0);
			}
		}
	}

	public void enable() {
		this.uniformStateDirty = false;
		activeProgram = this;
		this.blendState.enable();
		if (this.programRef != activeProgramRef) {
			GLX.glUseProgram(this.programRef);
			activeProgramRef = this.programRef;
		}

		if (this.useCullFace) {
			GlStateManager.enableCull();
		} else {
			GlStateManager.disableCull();
		}

		for (int i = 0; i < this.samplerShaderLocs.size(); i++) {
			if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
				GlStateManager.activeTexture(GLX.GL_TEXTURE0 + i);
				GlStateManager.enableTexture();
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
					GlStateManager.bindTexture(j);
					GLX.glUniform1i(GLX.glGetUniformLocation(this.programRef, (CharSequence)this.samplerNames.get(i)), i);
				}
			}
		}

		for (GlUniform glUniform : this.uniformData) {
			glUniform.upload();
		}
	}

	@Override
	public void markUniformStateDirty() {
		this.uniformStateDirty = true;
	}

	@Nullable
	public GlUniform getUniformByName(String string) {
		return (GlUniform)this.uniformByName.get(string);
	}

	public class_278 getUniformByNameOrDummy(String string) {
		GlUniform glUniform = this.getUniformByName(string);
		return (class_278)(glUniform == null ? dummyUniform : glUniform);
	}

	private void finalizeUniformsAndSamplers() {
		int i = 0;

		for (int j = 0; i < this.samplerNames.size(); j++) {
			String string = (String)this.samplerNames.get(i);
			int k = GLX.glGetUniformLocation(this.programRef, string);
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
			int k = GLX.glGetUniformLocation(this.programRef, string);
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
		if (!JsonHelper.method_15289(jsonObject, "file")) {
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
		this.markUniformStateDirty();
	}

	private void addUniform(JsonElement jsonElement) throws class_2973 {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
		int j = JsonHelper.getInt(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new class_2973("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement2 : jsonArray) {
				try {
					fs[k] = JsonHelper.asFloat(jsonElement2, "value");
				} catch (Exception var13) {
					class_2973 lv = class_2973.method_12856(var13);
					lv.method_12854("values[" + k + "]");
					throw lv;
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
				glUniform.method_1248((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
			} else if (i <= 7) {
				glUniform.method_1252(fs[0], fs[1], fs[2], fs[3]);
			} else {
				glUniform.method_1253(fs);
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
