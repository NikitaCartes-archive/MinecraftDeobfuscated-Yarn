package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Shader;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sortme.Matrix4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class class_279 implements AutoCloseable {
	private final GlFramebuffer field_1499;
	private final ResourceManager field_1502;
	private final String field_1494;
	private final List<Shader> field_1497 = Lists.<Shader>newArrayList();
	private final Map<String, GlFramebuffer> field_1495 = Maps.<String, GlFramebuffer>newHashMap();
	private final List<GlFramebuffer> field_1496 = Lists.<GlFramebuffer>newArrayList();
	private Matrix4f field_1498;
	private int field_1493;
	private int field_1492;
	private float field_1501;
	private float field_1500;

	public class_279(TextureManager textureManager, ResourceManager resourceManager, GlFramebuffer glFramebuffer, Identifier identifier) throws IOException, JsonSyntaxException {
		this.field_1502 = resourceManager;
		this.field_1499 = glFramebuffer;
		this.field_1501 = 0.0F;
		this.field_1500 = 0.0F;
		this.field_1493 = glFramebuffer.viewWidth;
		this.field_1492 = glFramebuffer.viewHeight;
		this.field_1494 = identifier.toString();
		this.method_1267();
		this.method_1256(textureManager, identifier);
	}

	private void method_1256(TextureManager textureManager, Identifier identifier) throws IOException, JsonSyntaxException {
		Resource resource = null;

		try {
			resource = this.field_1502.getResource(identifier);
			JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
			if (JsonHelper.isArray(jsonObject, "targets")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.method_1265(jsonElement);
					} catch (Exception var17) {
						class_2973 lv = class_2973.method_12856(var17);
						lv.method_12854("targets[" + i + "]");
						throw lv;
					}

					i++;
				}
			}

			if (JsonHelper.isArray(jsonObject, "passes")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("passes");
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.method_1257(textureManager, jsonElement);
					} catch (Exception var16) {
						class_2973 lv = class_2973.method_12856(var16);
						lv.method_12854("passes[" + i + "]");
						throw lv;
					}

					i++;
				}
			}
		} catch (Exception var18) {
			class_2973 lv2 = class_2973.method_12856(var18);
			lv2.method_12855(identifier.getPath());
			throw lv2;
		} finally {
			IOUtils.closeQuietly(resource);
		}
	}

	private void method_1265(JsonElement jsonElement) throws class_2973 {
		if (JsonHelper.isString(jsonElement)) {
			this.method_1261(jsonElement.getAsString(), this.field_1493, this.field_1492);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "target");
			String string = JsonHelper.getString(jsonObject, "name");
			int i = JsonHelper.getInt(jsonObject, "width", this.field_1493);
			int j = JsonHelper.getInt(jsonObject, "height", this.field_1492);
			if (this.field_1495.containsKey(string)) {
				throw new class_2973(string + " is already defined");
			}

			this.method_1261(string, i, j);
		}
	}

	private void method_1257(TextureManager textureManager, JsonElement jsonElement) throws IOException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "pass");
		String string = JsonHelper.getString(jsonObject, "name");
		String string2 = JsonHelper.getString(jsonObject, "intarget");
		String string3 = JsonHelper.getString(jsonObject, "outtarget");
		GlFramebuffer glFramebuffer = this.method_1266(string2);
		GlFramebuffer glFramebuffer2 = this.method_1266(string3);
		if (glFramebuffer == null) {
			throw new class_2973("Input target '" + string2 + "' does not exist");
		} else if (glFramebuffer2 == null) {
			throw new class_2973("Output target '" + string3 + "' does not exist");
		} else {
			Shader shader = this.method_1262(string, glFramebuffer, glFramebuffer2);
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement2 : jsonArray) {
					try {
						JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "auxtarget");
						String string4 = JsonHelper.getString(jsonObject2, "name");
						String string5 = JsonHelper.getString(jsonObject2, "id");
						GlFramebuffer glFramebuffer3 = this.method_1266(string5);
						if (glFramebuffer3 == null) {
							Identifier identifier = new Identifier("textures/effect/" + string5 + ".png");
							Resource resource = null;

							try {
								resource = this.field_1502.getResource(identifier);
							} catch (FileNotFoundException var29) {
								throw new class_2973("Render target or texture '" + string5 + "' does not exist");
							} finally {
								IOUtils.closeQuietly(resource);
							}

							textureManager.bindTexture(identifier);
							Texture texture = textureManager.getTexture(identifier);
							int j = JsonHelper.getInt(jsonObject2, "width");
							int k = JsonHelper.getInt(jsonObject2, "height");
							boolean bl = JsonHelper.getBoolean(jsonObject2, "bilinear");
							if (bl) {
								GlStateManager.texParameter(3553, 10241, 9729);
								GlStateManager.texParameter(3553, 10240, 9729);
							} else {
								GlStateManager.texParameter(3553, 10241, 9728);
								GlStateManager.texParameter(3553, 10240, 9728);
							}

							shader.method_1292(string4, texture.getGlId(), j, k);
						} else {
							shader.method_1292(string4, glFramebuffer3, glFramebuffer3.texWidth, glFramebuffer3.texHeight);
						}
					} catch (Exception var31) {
						class_2973 lv = class_2973.method_12856(var31);
						lv.method_12854("auxtargets[" + i + "]");
						throw lv;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null);
			if (jsonArray2 != null) {
				int l = 0;

				for (JsonElement jsonElement3 : jsonArray2) {
					try {
						this.method_1263(jsonElement3);
					} catch (Exception var28) {
						class_2973 lv2 = class_2973.method_12856(var28);
						lv2.method_12854("uniforms[" + l + "]");
						throw lv2;
					}

					l++;
				}
			}
		}
	}

	private void method_1263(JsonElement jsonElement) throws class_2973 {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
		String string = JsonHelper.getString(jsonObject, "name");
		GlUniform glUniform = ((Shader)this.field_1497.get(this.field_1497.size() - 1)).method_1295().getUniformByName(string);
		if (glUniform == null) {
			throw new class_2973("Uniform '" + string + "' does not exist");
		} else {
			float[] fs = new float[4];
			int i = 0;

			for (JsonElement jsonElement2 : JsonHelper.getArray(jsonObject, "values")) {
				try {
					fs[i] = JsonHelper.asFloat(jsonElement2, "value");
				} catch (Exception var12) {
					class_2973 lv = class_2973.method_12856(var12);
					lv.method_12854("values[" + i + "]");
					throw lv;
				}

				i++;
			}

			switch (i) {
				case 0:
				default:
					break;
				case 1:
					glUniform.method_1251(fs[0]);
					break;
				case 2:
					glUniform.method_1255(fs[0], fs[1]);
					break;
				case 3:
					glUniform.method_1249(fs[0], fs[1], fs[2]);
					break;
				case 4:
					glUniform.method_1254(fs[0], fs[1], fs[2], fs[3]);
			}
		}
	}

	public GlFramebuffer method_1264(String string) {
		return (GlFramebuffer)this.field_1495.get(string);
	}

	public void method_1261(String string, int i, int j) {
		GlFramebuffer glFramebuffer = new GlFramebuffer(i, j, true, MinecraftClient.isSystemMac);
		glFramebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.field_1495.put(string, glFramebuffer);
		if (i == this.field_1493 && j == this.field_1492) {
			this.field_1496.add(glFramebuffer);
		}
	}

	public void close() {
		for (GlFramebuffer glFramebuffer : this.field_1495.values()) {
			glFramebuffer.delete();
		}

		for (Shader shader : this.field_1497) {
			shader.close();
		}

		this.field_1497.clear();
	}

	public Shader method_1262(String string, GlFramebuffer glFramebuffer, GlFramebuffer glFramebuffer2) throws IOException {
		Shader shader = new Shader(this.field_1502, string, glFramebuffer, glFramebuffer2);
		this.field_1497.add(this.field_1497.size(), shader);
		return shader;
	}

	private void method_1267() {
		this.field_1498 = Matrix4f.method_4933((float)this.field_1499.texWidth, (float)this.field_1499.texHeight, 0.1F, 1000.0F);
	}

	public void method_1259(int i, int j) {
		this.field_1493 = this.field_1499.texWidth;
		this.field_1492 = this.field_1499.texHeight;
		this.method_1267();

		for (Shader shader : this.field_1497) {
			shader.method_1291(this.field_1498);
		}

		for (GlFramebuffer glFramebuffer : this.field_1496) {
			glFramebuffer.resize(i, j, MinecraftClient.isSystemMac);
		}
	}

	public void method_1258(float f) {
		if (f < this.field_1500) {
			this.field_1501 = this.field_1501 + (1.0F - this.field_1500);
			this.field_1501 += f;
		} else {
			this.field_1501 = this.field_1501 + (f - this.field_1500);
		}

		this.field_1500 = f;

		while (this.field_1501 > 20.0F) {
			this.field_1501 -= 20.0F;
		}

		for (Shader shader : this.field_1497) {
			shader.method_1293(this.field_1501 / 20.0F);
		}
	}

	public final String method_1260() {
		return this.field_1494;
	}

	private GlFramebuffer method_1266(String string) {
		if (string == null) {
			return null;
		} else {
			return string.equals("minecraft:main") ? this.field_1499 : (GlFramebuffer)this.field_1495.get(string);
		}
	}
}
