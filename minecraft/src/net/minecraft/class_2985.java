package net.minecraft;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2985 {
	private static final Logger field_13394 = LogManager.getLogger();
	private static final Gson field_13395 = new GsonBuilder()
		.registerTypeAdapter(class_167.class, new class_167.class_168())
		.registerTypeAdapter(class_2960.class, new class_2960.class_2961())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<class_2960, class_167>> field_13392 = new TypeToken<Map<class_2960, class_167>>() {
	};
	private final MinecraftServer field_13397;
	private final File field_13393;
	private final Map<class_161, class_167> field_13389 = Maps.<class_161, class_167>newLinkedHashMap();
	private final Set<class_161> field_13390 = Sets.<class_161>newLinkedHashSet();
	private final Set<class_161> field_13386 = Sets.<class_161>newLinkedHashSet();
	private final Set<class_161> field_13388 = Sets.<class_161>newLinkedHashSet();
	private class_3222 field_13391;
	@Nullable
	private class_161 field_13387;
	private boolean field_13396 = true;

	public class_2985(MinecraftServer minecraftServer, File file, class_3222 arg) {
		this.field_13397 = minecraftServer;
		this.field_13393 = file;
		this.field_13391 = arg;
		this.method_12873();
	}

	public void method_12875(class_3222 arg) {
		this.field_13391 = arg;
	}

	public void method_12881() {
		for (class_179<?> lv : class_174.method_766()) {
			lv.method_791(this);
		}
	}

	public void method_12886() {
		this.method_12881();
		this.field_13389.clear();
		this.field_13390.clear();
		this.field_13386.clear();
		this.field_13388.clear();
		this.field_13396 = true;
		this.field_13387 = null;
		this.method_12873();
	}

	private void method_12889() {
		for (class_161 lv : this.field_13397.method_3851().method_12893()) {
			this.method_12874(lv);
		}
	}

	private void method_12887() {
		List<class_161> list = Lists.<class_161>newArrayList();

		for (Entry<class_161, class_167> entry : this.field_13389.entrySet()) {
			if (((class_167)entry.getValue()).method_740()) {
				list.add(entry.getKey());
				this.field_13388.add(entry.getKey());
			}
		}

		for (class_161 lv : list) {
			this.method_12885(lv);
		}
	}

	private void method_12872() {
		for (class_161 lv : this.field_13397.method_3851().method_12893()) {
			if (lv.method_682().isEmpty()) {
				this.method_12878(lv, "");
				lv.method_691().method_748(this.field_13391);
			}
		}
	}

	private void method_12873() {
		if (this.field_13393.isFile()) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.field_13393, StandardCharsets.UTF_8)));
				Throwable var2 = null;

				try {
					jsonReader.setLenient(false);
					Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
					if (!dynamic.get("DataVersion").asNumber().isPresent()) {
						dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
					}

					dynamic = this.field_13397
						.method_3855()
						.update(class_4284.field_19220.method_20329(), dynamic, dynamic.get("DataVersion").asInt(0), class_155.method_16673().getWorldVersion());
					dynamic = dynamic.remove("DataVersion");
					Map<class_2960, class_167> map = field_13395.getAdapter(field_13392).fromJsonTree(dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					Stream<Entry<class_2960, class_167>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

					for (Entry<class_2960, class_167> entry : (List)stream.collect(Collectors.toList())) {
						class_161 lv = this.field_13397.method_3851().method_12896((class_2960)entry.getKey());
						if (lv == null) {
							field_13394.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.field_13393);
						} else {
							this.method_12884(lv, (class_167)entry.getValue());
						}
					}
				} catch (Throwable var18) {
					var2 = var18;
					throw var18;
				} finally {
					if (jsonReader != null) {
						if (var2 != null) {
							try {
								jsonReader.close();
							} catch (Throwable var17) {
								var2.addSuppressed(var17);
							}
						} else {
							jsonReader.close();
						}
					}
				}
			} catch (JsonParseException var20) {
				field_13394.error("Couldn't parse player advancements in {}", this.field_13393, var20);
			} catch (IOException var21) {
				field_13394.error("Couldn't access player advancements in {}", this.field_13393, var21);
			}
		}

		this.method_12872();
		this.method_12887();
		this.method_12889();
	}

	public void method_12890() {
		Map<class_2960, class_167> map = Maps.<class_2960, class_167>newHashMap();

		for (Entry<class_161, class_167> entry : this.field_13389.entrySet()) {
			class_167 lv = (class_167)entry.getValue();
			if (lv.method_742()) {
				map.put(((class_161)entry.getKey()).method_688(), lv);
			}
		}

		if (this.field_13393.getParentFile() != null) {
			this.field_13393.getParentFile().mkdirs();
		}

		JsonElement jsonElement = field_13395.toJsonTree(map);
		jsonElement.getAsJsonObject().addProperty("DataVersion", class_155.method_16673().getWorldVersion());

		try {
			OutputStream outputStream = new FileOutputStream(this.field_13393);
			Throwable var38 = null;

			try {
				Writer writer = new OutputStreamWriter(outputStream, Charsets.UTF_8.newEncoder());
				Throwable var6 = null;

				try {
					field_13395.toJson(jsonElement, writer);
				} catch (Throwable var31) {
					var6 = var31;
					throw var31;
				} finally {
					if (writer != null) {
						if (var6 != null) {
							try {
								writer.close();
							} catch (Throwable var30) {
								var6.addSuppressed(var30);
							}
						} else {
							writer.close();
						}
					}
				}
			} catch (Throwable var33) {
				var38 = var33;
				throw var33;
			} finally {
				if (outputStream != null) {
					if (var38 != null) {
						try {
							outputStream.close();
						} catch (Throwable var29) {
							var38.addSuppressed(var29);
						}
					} else {
						outputStream.close();
					}
				}
			}
		} catch (IOException var35) {
			field_13394.error("Couldn't save player advancements to {}", this.field_13393, var35);
		}
	}

	public boolean method_12878(class_161 arg, String string) {
		boolean bl = false;
		class_167 lv = this.method_12882(arg);
		boolean bl2 = lv.method_740();
		if (lv.method_743(string)) {
			this.method_12880(arg);
			this.field_13388.add(arg);
			bl = true;
			if (!bl2 && lv.method_740()) {
				arg.method_691().method_748(this.field_13391);
				if (arg.method_686() != null && arg.method_686().method_808() && this.field_13391.field_6002.method_8450().method_8355(class_1928.field_19409)) {
					this.field_13397
						.method_3760()
						.method_14593(new class_2588("chat.type.advancement." + arg.method_686().method_815().method_831(), this.field_13391.method_5476(), arg.method_684()));
				}
			}
		}

		if (lv.method_740()) {
			this.method_12885(arg);
		}

		return bl;
	}

	public boolean method_12883(class_161 arg, String string) {
		boolean bl = false;
		class_167 lv = this.method_12882(arg);
		if (lv.method_729(string)) {
			this.method_12874(arg);
			this.field_13388.add(arg);
			bl = true;
		}

		if (!lv.method_742()) {
			this.method_12885(arg);
		}

		return bl;
	}

	private void method_12874(class_161 arg) {
		class_167 lv = this.method_12882(arg);
		if (!lv.method_740()) {
			for (Entry<String, class_175> entry : arg.method_682().entrySet()) {
				class_178 lv2 = lv.method_737((String)entry.getKey());
				if (lv2 != null && !lv2.method_784()) {
					class_184 lv3 = ((class_175)entry.getValue()).method_774();
					if (lv3 != null) {
						class_179<class_184> lv4 = class_174.method_765(lv3.method_806());
						if (lv4 != null) {
							lv4.method_792(this, new class_179.class_180<>(lv3, arg, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void method_12880(class_161 arg) {
		class_167 lv = this.method_12882(arg);

		for (Entry<String, class_175> entry : arg.method_682().entrySet()) {
			class_178 lv2 = lv.method_737((String)entry.getKey());
			if (lv2 != null && (lv2.method_784() || lv.method_740())) {
				class_184 lv3 = ((class_175)entry.getValue()).method_774();
				if (lv3 != null) {
					class_179<class_184> lv4 = class_174.method_765(lv3.method_806());
					if (lv4 != null) {
						lv4.method_793(this, new class_179.class_180<>(lv3, arg, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void method_12876(class_3222 arg) {
		if (this.field_13396 || !this.field_13386.isEmpty() || !this.field_13388.isEmpty()) {
			Map<class_2960, class_167> map = Maps.<class_2960, class_167>newHashMap();
			Set<class_161> set = Sets.<class_161>newLinkedHashSet();
			Set<class_2960> set2 = Sets.<class_2960>newLinkedHashSet();

			for (class_161 lv : this.field_13388) {
				if (this.field_13390.contains(lv)) {
					map.put(lv.method_688(), this.field_13389.get(lv));
				}
			}

			for (class_161 lvx : this.field_13386) {
				if (this.field_13390.contains(lvx)) {
					set.add(lvx);
				} else {
					set2.add(lvx.method_688());
				}
			}

			if (this.field_13396 || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				arg.field_13987.method_14364(new class_2779(this.field_13396, set, set2, map));
				this.field_13386.clear();
				this.field_13388.clear();
			}
		}

		this.field_13396 = false;
	}

	public void method_12888(@Nullable class_161 arg) {
		class_161 lv = this.field_13387;
		if (arg != null && arg.method_687() == null && arg.method_686() != null) {
			this.field_13387 = arg;
		} else {
			this.field_13387 = null;
		}

		if (lv != this.field_13387) {
			this.field_13391.field_13987.method_14364(new class_2729(this.field_13387 == null ? null : this.field_13387.method_688()));
		}
	}

	public class_167 method_12882(class_161 arg) {
		class_167 lv = (class_167)this.field_13389.get(arg);
		if (lv == null) {
			lv = new class_167();
			this.method_12884(arg, lv);
		}

		return lv;
	}

	private void method_12884(class_161 arg, class_167 arg2) {
		arg2.method_727(arg.method_682(), arg.method_680());
		this.field_13389.put(arg, arg2);
	}

	private void method_12885(class_161 arg) {
		boolean bl = this.method_12879(arg);
		boolean bl2 = this.field_13390.contains(arg);
		if (bl && !bl2) {
			this.field_13390.add(arg);
			this.field_13386.add(arg);
			if (this.field_13389.containsKey(arg)) {
				this.field_13388.add(arg);
			}
		} else if (!bl && bl2) {
			this.field_13390.remove(arg);
			this.field_13386.add(arg);
		}

		if (bl != bl2 && arg.method_687() != null) {
			this.method_12885(arg.method_687());
		}

		for (class_161 lv : arg.method_681()) {
			this.method_12885(lv);
		}
	}

	private boolean method_12879(class_161 arg) {
		for (int i = 0; arg != null && i <= 2; i++) {
			if (i == 0 && this.method_12877(arg)) {
				return true;
			}

			if (arg.method_686() == null) {
				return false;
			}

			class_167 lv = this.method_12882(arg);
			if (lv.method_740()) {
				return true;
			}

			if (arg.method_686().method_824()) {
				return false;
			}

			arg = arg.method_687();
		}

		return false;
	}

	private boolean method_12877(class_161 arg) {
		class_167 lv = this.method_12882(arg);
		if (lv.method_740()) {
			return true;
		} else {
			for (class_161 lv2 : arg.method_681()) {
				if (this.method_12877(lv2)) {
					return true;
				}
			}

			return false;
		}
	}
}
