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
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class class_279 implements AutoCloseable {
	private final class_276 field_1499;
	private final class_3300 field_1502;
	private final String field_1494;
	private final List<class_283> field_1497 = Lists.<class_283>newArrayList();
	private final Map<String, class_276> field_1495 = Maps.<String, class_276>newHashMap();
	private final List<class_276> field_1496 = Lists.<class_276>newArrayList();
	private class_1159 field_1498;
	private int field_1493;
	private int field_1492;
	private float field_1501;
	private float field_1500;

	public class_279(class_1060 arg, class_3300 arg2, class_276 arg3, class_2960 arg4) throws IOException, JsonSyntaxException {
		this.field_1502 = arg2;
		this.field_1499 = arg3;
		this.field_1501 = 0.0F;
		this.field_1500 = 0.0F;
		this.field_1493 = arg3.field_1480;
		this.field_1492 = arg3.field_1477;
		this.field_1494 = arg4.toString();
		this.method_1267();
		this.method_1256(arg, arg4);
	}

	private void method_1256(class_1060 arg, class_2960 arg2) throws IOException, JsonSyntaxException {
		class_3298 lv = null;

		try {
			lv = this.field_1502.method_14486(arg2);
			JsonObject jsonObject = class_3518.method_15255(new InputStreamReader(lv.method_14482(), StandardCharsets.UTF_8));
			if (class_3518.method_15264(jsonObject, "targets")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.method_1265(jsonElement);
					} catch (Exception var17) {
						class_2973 lv2 = class_2973.method_12856(var17);
						lv2.method_12854("targets[" + i + "]");
						throw lv2;
					}

					i++;
				}
			}

			if (class_3518.method_15264(jsonObject, "passes")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("passes");
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					try {
						this.method_1257(arg, jsonElement);
					} catch (Exception var16) {
						class_2973 lv2 = class_2973.method_12856(var16);
						lv2.method_12854("passes[" + i + "]");
						throw lv2;
					}

					i++;
				}
			}
		} catch (Exception var18) {
			class_2973 lv3 = class_2973.method_12856(var18);
			lv3.method_12855(arg2.method_12832());
			throw lv3;
		} finally {
			IOUtils.closeQuietly(lv);
		}
	}

	private void method_1265(JsonElement jsonElement) throws class_2973 {
		if (class_3518.method_15286(jsonElement)) {
			this.method_1261(jsonElement.getAsString(), this.field_1493, this.field_1492);
		} else {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "target");
			String string = class_3518.method_15265(jsonObject, "name");
			int i = class_3518.method_15282(jsonObject, "width", this.field_1493);
			int j = class_3518.method_15282(jsonObject, "height", this.field_1492);
			if (this.field_1495.containsKey(string)) {
				throw new class_2973(string + " is already defined");
			}

			this.method_1261(string, i, j);
		}
	}

	private void method_1257(class_1060 arg, JsonElement jsonElement) throws IOException {
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "pass");
		String string = class_3518.method_15265(jsonObject, "name");
		String string2 = class_3518.method_15265(jsonObject, "intarget");
		String string3 = class_3518.method_15265(jsonObject, "outtarget");
		class_276 lv = this.method_1266(string2);
		class_276 lv2 = this.method_1266(string3);
		if (lv == null) {
			throw new class_2973("Input target '" + string2 + "' does not exist");
		} else if (lv2 == null) {
			throw new class_2973("Output target '" + string3 + "' does not exist");
		} else {
			class_283 lv3 = this.method_1262(string, lv, lv2);
			JsonArray jsonArray = class_3518.method_15292(jsonObject, "auxtargets", null);
			if (jsonArray != null) {
				int i = 0;

				for (JsonElement jsonElement2 : jsonArray) {
					try {
						JsonObject jsonObject2 = class_3518.method_15295(jsonElement2, "auxtarget");
						String string4 = class_3518.method_15265(jsonObject2, "name");
						String string5 = class_3518.method_15265(jsonObject2, "id");
						class_276 lv4 = this.method_1266(string5);
						if (lv4 == null) {
							class_2960 lv5 = new class_2960("textures/effect/" + string5 + ".png");
							class_3298 lv6 = null;

							try {
								lv6 = this.field_1502.method_14486(lv5);
							} catch (FileNotFoundException var29) {
								throw new class_2973("Render target or texture '" + string5 + "' does not exist");
							} finally {
								IOUtils.closeQuietly(lv6);
							}

							arg.method_4618(lv5);
							class_1062 lv7 = arg.method_4619(lv5);
							int j = class_3518.method_15260(jsonObject2, "width");
							int k = class_3518.method_15260(jsonObject2, "height");
							boolean bl = class_3518.method_15270(jsonObject2, "bilinear");
							if (bl) {
								GlStateManager.texParameter(3553, 10241, 9729);
								GlStateManager.texParameter(3553, 10240, 9729);
							} else {
								GlStateManager.texParameter(3553, 10241, 9728);
								GlStateManager.texParameter(3553, 10240, 9728);
							}

							lv3.method_1292(string4, lv7.method_4624(), j, k);
						} else {
							lv3.method_1292(string4, lv4, lv4.field_1482, lv4.field_1481);
						}
					} catch (Exception var31) {
						class_2973 lv8 = class_2973.method_12856(var31);
						lv8.method_12854("auxtargets[" + i + "]");
						throw lv8;
					}

					i++;
				}
			}

			JsonArray jsonArray2 = class_3518.method_15292(jsonObject, "uniforms", null);
			if (jsonArray2 != null) {
				int l = 0;

				for (JsonElement jsonElement3 : jsonArray2) {
					try {
						this.method_1263(jsonElement3);
					} catch (Exception var28) {
						class_2973 lv9 = class_2973.method_12856(var28);
						lv9.method_12854("uniforms[" + l + "]");
						throw lv9;
					}

					l++;
				}
			}
		}
	}

	private void method_1263(JsonElement jsonElement) throws class_2973 {
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "uniform");
		String string = class_3518.method_15265(jsonObject, "name");
		class_284 lv = ((class_283)this.field_1497.get(this.field_1497.size() - 1)).method_1295().method_1271(string);
		if (lv == null) {
			throw new class_2973("Uniform '" + string + "' does not exist");
		} else {
			float[] fs = new float[4];
			int i = 0;

			for (JsonElement jsonElement2 : class_3518.method_15261(jsonObject, "values")) {
				try {
					fs[i] = class_3518.method_15269(jsonElement2, "value");
				} catch (Exception var12) {
					class_2973 lv2 = class_2973.method_12856(var12);
					lv2.method_12854("values[" + i + "]");
					throw lv2;
				}

				i++;
			}

			switch (i) {
				case 0:
				default:
					break;
				case 1:
					lv.method_1251(fs[0]);
					break;
				case 2:
					lv.method_1255(fs[0], fs[1]);
					break;
				case 3:
					lv.method_1249(fs[0], fs[1], fs[2]);
					break;
				case 4:
					lv.method_1254(fs[0], fs[1], fs[2], fs[3]);
			}
		}
	}

	public class_276 method_1264(String string) {
		return (class_276)this.field_1495.get(string);
	}

	public void method_1261(String string, int i, int j) {
		class_276 lv = new class_276(i, j, true, class_310.field_1703);
		lv.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
		this.field_1495.put(string, lv);
		if (i == this.field_1493 && j == this.field_1492) {
			this.field_1496.add(lv);
		}
	}

	public void close() {
		for (class_276 lv : this.field_1495.values()) {
			lv.method_1238();
		}

		for (class_283 lv2 : this.field_1497) {
			lv2.close();
		}

		this.field_1497.clear();
	}

	public class_283 method_1262(String string, class_276 arg, class_276 arg2) throws IOException {
		class_283 lv = new class_283(this.field_1502, string, arg, arg2);
		this.field_1497.add(this.field_1497.size(), lv);
		return lv;
	}

	private void method_1267() {
		this.field_1498 = class_1159.method_4933((float)this.field_1499.field_1482, (float)this.field_1499.field_1481, 0.1F, 1000.0F);
	}

	public void method_1259(int i, int j) {
		this.field_1493 = this.field_1499.field_1482;
		this.field_1492 = this.field_1499.field_1481;
		this.method_1267();

		for (class_283 lv : this.field_1497) {
			lv.method_1291(this.field_1498);
		}

		for (class_276 lv2 : this.field_1496) {
			lv2.method_1234(i, j, class_310.field_1703);
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

		for (class_283 lv : this.field_1497) {
			lv.method_1293(this.field_1501 / 20.0F);
		}
	}

	public final String method_1260() {
		return this.field_1494;
	}

	private class_276 method_1266(String string) {
		if (string == null) {
			return null;
		} else {
			return string.equals("minecraft:main") ? this.field_1499 : (class_276)this.field_1495.get(string);
		}
	}
}
