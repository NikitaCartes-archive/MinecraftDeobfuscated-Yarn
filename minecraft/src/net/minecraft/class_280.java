package net.minecraft;

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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_280 implements class_3679, AutoCloseable {
	private static final Logger field_1514 = LogManager.getLogger();
	private static final class_278 field_1520 = new class_278();
	private static class_280 field_1512;
	private static int field_1505 = -1;
	private final Map<String, Object> field_1516 = Maps.<String, Object>newHashMap();
	private final List<String> field_1503 = Lists.<String>newArrayList();
	private final List<Integer> field_1506 = Lists.<Integer>newArrayList();
	private final List<class_284> field_1515 = Lists.<class_284>newArrayList();
	private final List<Integer> field_1507 = Lists.<Integer>newArrayList();
	private final Map<String, class_284> field_1510 = Maps.<String, class_284>newHashMap();
	private final int field_1521;
	private final String field_1509;
	private final boolean field_1513;
	private boolean field_1511;
	private final class_277 field_1517;
	private final List<Integer> field_1518;
	private final List<String> field_1504;
	private final class_281 field_1508;
	private final class_281 field_1519;

	public class_280(class_3300 arg, String string) throws IOException {
		class_2960 lv = new class_2960("shaders/program/" + string + ".json");
		this.field_1509 = string;
		class_3298 lv2 = null;

		try {
			lv2 = arg.method_14486(lv);
			JsonObject jsonObject = class_3518.method_15255(new InputStreamReader(lv2.method_14482(), StandardCharsets.UTF_8));
			String string2 = class_3518.method_15265(jsonObject, "vertex");
			String string3 = class_3518.method_15265(jsonObject, "fragment");
			JsonArray jsonArray = class_3518.method_15292(jsonObject, "samplers", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.method_1276(jsonElement);
					} catch (Exception var24) {
						class_2973 lv3 = class_2973.method_12856(var24);
						lv3.method_12854("samplers[" + i + "]");
						throw lv3;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = class_3518.method_15292(jsonObject, "attributes", null);
			if (jsonArray2 != null) {
				int j = 0;
				this.field_1518 = Lists.<Integer>newArrayListWithCapacity(jsonArray2.size());
				this.field_1504 = Lists.<String>newArrayListWithCapacity(jsonArray2.size());

				for (JsonElement jsonElement2 : jsonArray2) {
					try {
						this.field_1504.add(class_3518.method_15287(jsonElement2, "attribute"));
					} catch (Exception var23) {
						class_2973 lv4 = class_2973.method_12856(var23);
						lv4.method_12854("attributes[" + j + "]");
						throw lv4;
					}

					j++;
				}
			} else {
				this.field_1518 = null;
				this.field_1504 = null;
			}

			JsonArray jsonArray3 = class_3518.method_15292(jsonObject, "uniforms", null);
			if (jsonArray3 != null) {
				int k = 0;

				for (JsonElement jsonElement3 : jsonArray3) {
					try {
						this.method_1272(jsonElement3);
					} catch (Exception var22) {
						class_2973 lv5 = class_2973.method_12856(var22);
						lv5.method_12854("uniforms[" + k + "]");
						throw lv5;
					}

					k++;
				}
			}

			this.field_1517 = method_16035(class_3518.method_15281(jsonObject, "blend", null));
			this.field_1513 = class_3518.method_15258(jsonObject, "cull", true);
			this.field_1508 = method_16036(arg, class_281.class_282.field_1530, string2);
			this.field_1519 = method_16036(arg, class_281.class_282.field_1531, string3);
			this.field_1521 = class_285.method_1308().method_1306();
			class_285.method_1308().method_1307(this);
			this.method_1268();
			if (this.field_1504 != null) {
				for (String string4 : this.field_1504) {
					int l = GLX.glGetAttribLocation(this.field_1521, string4);
					this.field_1518.add(l);
				}
			}
		} catch (Exception var25) {
			class_2973 lv6 = class_2973.method_12856(var25);
			lv6.method_12855(lv.method_12832());
			throw lv6;
		} finally {
			IOUtils.closeQuietly(lv2);
		}

		this.method_1279();
	}

	public static class_281 method_16036(class_3300 arg, class_281.class_282 arg2, String string) throws IOException {
		class_281 lv = (class_281)arg2.method_1289().get(string);
		if (lv == null) {
			class_2960 lv2 = new class_2960("shaders/program/" + string + arg2.method_1284());
			class_3298 lv3 = arg.method_14486(lv2);

			try {
				lv = class_281.method_1283(arg2, string, lv3.method_14482());
			} finally {
				IOUtils.closeQuietly(lv3);
			}
		}

		return lv;
	}

	public static class_277 method_16035(JsonObject jsonObject) {
		if (jsonObject == null) {
			return new class_277();
		} else {
			int i = 32774;
			int j = 1;
			int k = 0;
			int l = 1;
			int m = 0;
			boolean bl = true;
			boolean bl2 = false;
			if (class_3518.method_15289(jsonObject, "func")) {
				i = class_277.method_1247(jsonObject.get("func").getAsString());
				if (i != 32774) {
					bl = false;
				}
			}

			if (class_3518.method_15289(jsonObject, "srcrgb")) {
				j = class_277.method_1243(jsonObject.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (class_3518.method_15289(jsonObject, "dstrgb")) {
				k = class_277.method_1243(jsonObject.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (class_3518.method_15289(jsonObject, "srcalpha")) {
				l = class_277.method_1243(jsonObject.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (class_3518.method_15289(jsonObject, "dstalpha")) {
				m = class_277.method_1243(jsonObject.get("dstalpha").getAsString());
				if (m != 0) {
					bl = false;
				}

				bl2 = true;
			}

			if (bl) {
				return new class_277();
			} else {
				return bl2 ? new class_277(j, k, l, m, i) : new class_277(j, k, i);
			}
		}
	}

	public void close() {
		for (class_284 lv : this.field_1515) {
			lv.close();
		}

		class_285.method_1308().method_1304(this);
	}

	public void method_1273() {
		GLX.glUseProgram(0);
		field_1505 = -1;
		field_1512 = null;

		for (int i = 0; i < this.field_1506.size(); i++) {
			if (this.field_1516.get(this.field_1503.get(i)) != null) {
				GlStateManager.activeTexture(GLX.GL_TEXTURE0 + i);
				GlStateManager.bindTexture(0);
			}
		}
	}

	public void method_1277() {
		this.field_1511 = false;
		field_1512 = this;
		this.field_1517.method_1244();
		if (this.field_1521 != field_1505) {
			GLX.glUseProgram(this.field_1521);
			field_1505 = this.field_1521;
		}

		if (this.field_1513) {
			GlStateManager.enableCull();
		} else {
			GlStateManager.disableCull();
		}

		for (int i = 0; i < this.field_1506.size(); i++) {
			if (this.field_1516.get(this.field_1503.get(i)) != null) {
				GlStateManager.activeTexture(GLX.GL_TEXTURE0 + i);
				GlStateManager.enableTexture();
				Object object = this.field_1516.get(this.field_1503.get(i));
				int j = -1;
				if (object instanceof class_276) {
					j = ((class_276)object).field_1475;
				} else if (object instanceof class_1062) {
					j = ((class_1062)object).method_4624();
				} else if (object instanceof Integer) {
					j = (Integer)object;
				}

				if (j != -1) {
					GlStateManager.bindTexture(j);
					GLX.glUniform1i(GLX.glGetUniformLocation(this.field_1521, (CharSequence)this.field_1503.get(i)), i);
				}
			}
		}

		for (class_284 lv : this.field_1515) {
			lv.method_1300();
		}
	}

	@Override
	public void method_1279() {
		this.field_1511 = true;
	}

	@Nullable
	public class_284 method_1271(String string) {
		return (class_284)this.field_1510.get(string);
	}

	public class_278 method_1275(String string) {
		class_284 lv = this.method_1271(string);
		return (class_278)(lv == null ? field_1520 : lv);
	}

	private void method_1268() {
		int i = 0;

		for (int j = 0; i < this.field_1503.size(); j++) {
			String string = (String)this.field_1503.get(i);
			int k = GLX.glGetUniformLocation(this.field_1521, string);
			if (k == -1) {
				field_1514.warn("Shader {}could not find sampler named {} in the specified shader program.", this.field_1509, string);
				this.field_1516.remove(string);
				this.field_1503.remove(j);
				j--;
			} else {
				this.field_1506.add(k);
			}

			i++;
		}

		for (class_284 lv : this.field_1515) {
			String string = lv.method_1298();
			int k = GLX.glGetUniformLocation(this.field_1521, string);
			if (k == -1) {
				field_1514.warn("Could not find uniform named {} in the specified shader program.", string);
			} else {
				this.field_1507.add(k);
				lv.method_1297(k);
				this.field_1510.put(string, lv);
			}
		}
	}

	private void method_1276(JsonElement jsonElement) {
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "sampler");
		String string = class_3518.method_15265(jsonObject, "name");
		if (!class_3518.method_15289(jsonObject, "file")) {
			this.field_1516.put(string, null);
			this.field_1503.add(string);
		} else {
			this.field_1503.add(string);
		}
	}

	public void method_1269(String string, Object object) {
		if (this.field_1516.containsKey(string)) {
			this.field_1516.remove(string);
		}

		this.field_1516.put(string, object);
		this.method_1279();
	}

	private void method_1272(JsonElement jsonElement) throws class_2973 {
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "uniform");
		String string = class_3518.method_15265(jsonObject, "name");
		int i = class_284.method_1299(class_3518.method_15265(jsonObject, "type"));
		int j = class_3518.method_15260(jsonObject, "count");
		float[] fs = new float[Math.max(j, 16)];
		JsonArray jsonArray = class_3518.method_15261(jsonObject, "values");
		if (jsonArray.size() != j && jsonArray.size() > 1) {
			throw new class_2973("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
		} else {
			int k = 0;

			for (JsonElement jsonElement2 : jsonArray) {
				try {
					fs[k] = class_3518.method_15269(jsonElement2, "value");
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
			class_284 lv2 = new class_284(string, i + l, j, this);
			if (i <= 3) {
				lv2.method_1248((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
			} else if (i <= 7) {
				lv2.method_1252(fs[0], fs[1], fs[2], fs[3]);
			} else {
				lv2.method_1253(fs);
			}

			this.field_1515.add(lv2);
		}
	}

	@Override
	public class_281 method_1274() {
		return this.field_1508;
	}

	@Override
	public class_281 method_1278() {
		return this.field_1519;
	}

	@Override
	public int method_1270() {
		return this.field_1521;
	}
}
