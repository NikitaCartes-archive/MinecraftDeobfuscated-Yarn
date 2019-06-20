package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1144 extends class_4080<class_1144.class_4009> {
	public static final class_1111 field_5592 = new class_1111("meta:missing_sound", 1.0F, 1.0F, 1, class_1111.class_1112.field_5474, false, false, 16);
	private static final Logger field_5593 = LogManager.getLogger();
	private static final Gson field_5594 = new GsonBuilder()
		.registerTypeHierarchyAdapter(class_2561.class, new class_2561.class_2562())
		.registerTypeAdapter(class_1110.class, new class_1115())
		.create();
	private static final ParameterizedType field_5591 = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class, class_1110.class};
		}

		public Type getRawType() {
			return Map.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	private final Map<class_2960, class_1146> field_5588 = Maps.<class_2960, class_1146>newHashMap();
	private final class_1140 field_5590;

	public class_1144(class_3300 arg, class_315 arg2) {
		this.field_5590 = new class_1140(this, arg2, arg);
	}

	protected class_1144.class_4009 method_18180(class_3300 arg, class_3695 arg2) {
		class_1144.class_4009 lv = new class_1144.class_4009();
		arg2.method_16065();

		for (String string : arg.method_14487()) {
			arg2.method_15396(string);

			try {
				for (class_3298 lv2 : arg.method_14489(new class_2960(string, "sounds.json"))) {
					arg2.method_15396(lv2.method_14480());

					try {
						arg2.method_15396("parse");
						Map<String, class_1110> map = method_4867(lv2.method_14482());
						arg2.method_15405("register");

						for (Entry<String, class_1110> entry : map.entrySet()) {
							lv.method_18187(new class_2960(string, (String)entry.getKey()), (class_1110)entry.getValue(), arg);
						}

						arg2.method_15407();
					} catch (RuntimeException var12) {
						field_5593.warn("Invalid sounds.json in resourcepack: '{}'", lv2.method_14480(), var12);
					}

					arg2.method_15407();
				}
			} catch (IOException var13) {
			}

			arg2.method_15407();
		}

		arg2.method_16066();
		return lv;
	}

	protected void method_18182(class_1144.class_4009 arg, class_3300 arg2, class_3695 arg3) {
		arg.method_18186(this.field_5588, this.field_5590);

		for (class_2960 lv : this.field_5588.keySet()) {
			class_1146 lv2 = (class_1146)this.field_5588.get(lv);
			if (lv2.method_4886() instanceof class_2588) {
				String string = ((class_2588)lv2.method_4886()).method_11022();
				if (!class_1074.method_4663(string)) {
					field_5593.debug("Missing subtitle {} for event: {}", string, lv);
				}
			}
		}

		if (field_5593.isDebugEnabled()) {
			for (class_2960 lvx : this.field_5588.keySet()) {
				if (!class_2378.field_11156.method_10250(lvx)) {
					field_5593.debug("Not having sound event for: {}", lvx);
				}
			}
		}

		this.field_5590.method_4837();
	}

	@Nullable
	protected static Map<String, class_1110> method_4867(InputStream inputStream) {
		Map var1;
		try {
			var1 = class_3518.method_15297(field_5594, new InputStreamReader(inputStream, StandardCharsets.UTF_8), field_5591);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var1;
	}

	private static boolean method_4868(class_1111 arg, class_2960 arg2, class_3300 arg3) {
		class_2960 lv = arg.method_4766();
		if (!arg3.method_18234(lv)) {
			field_5593.warn("File {} does not exist, cannot add it to event {}", lv, arg2);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	public class_1146 method_4869(class_2960 arg) {
		return (class_1146)this.field_5588.get(arg);
	}

	public Collection<class_2960> method_4864() {
		return this.field_5588.keySet();
	}

	public void method_4873(class_1113 arg) {
		this.field_5590.method_4854(arg);
	}

	public void method_4872(class_1113 arg, int i) {
		this.field_5590.method_4852(arg, i);
	}

	public void method_4876(class_4184 arg) {
		this.field_5590.method_4840(arg);
	}

	public void method_4879() {
		this.field_5590.method_19761();
	}

	public void method_4881() {
		this.field_5590.method_4843();
	}

	public void method_4882() {
		this.field_5590.method_4856();
	}

	public void method_18670(boolean bl) {
		this.field_5590.method_20185(bl);
	}

	public void method_4880() {
		this.field_5590.method_19762();
	}

	public void method_4865(class_3419 arg, float f) {
		if (arg == class_3419.field_15250 && f <= 0.0F) {
			this.method_4881();
		}

		this.field_5590.method_4844(arg, f);
	}

	public void method_4870(class_1113 arg) {
		this.field_5590.method_19753(arg);
	}

	public boolean method_4877(class_1113 arg) {
		return this.field_5590.method_4835(arg);
	}

	public void method_4878(class_1145 arg) {
		this.field_5590.method_4855(arg);
	}

	public void method_4866(class_1145 arg) {
		this.field_5590.method_4847(arg);
	}

	public void method_4875(@Nullable class_2960 arg, @Nullable class_3419 arg2) {
		this.field_5590.method_4838(arg, arg2);
	}

	public String method_20305() {
		return this.field_5590.method_20304();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4009 {
		private final Map<class_2960, class_1146> field_17908 = Maps.<class_2960, class_1146>newHashMap();

		protected class_4009() {
		}

		private void method_18187(class_2960 arg, class_1110 arg2, class_3300 arg3) {
			class_1146 lv = (class_1146)this.field_17908.get(arg);
			boolean bl = lv == null;
			if (bl || arg2.method_4763()) {
				if (!bl) {
					class_1144.field_5593.debug("Replaced sound event location {}", arg);
				}

				lv = new class_1146(arg, arg2.method_4762());
				this.field_17908.put(arg, lv);
			}

			for (final class_1111 lv2 : arg2.method_4761()) {
				final class_2960 lv3 = lv2.method_4767();
				class_1148<class_1111> lv4;
				switch (lv2.method_4768()) {
					case field_5474:
						if (!class_1144.method_4868(lv2, arg, arg3)) {
							continue;
						}

						lv4 = lv2;
						break;
					case field_5473:
						lv4 = new class_1148<class_1111>() {
							@Override
							public int method_4894() {
								class_1146 lv = (class_1146)class_4009.this.field_17908.get(lv3);
								return lv == null ? 0 : lv.method_4894();
							}

							public class_1111 method_4883() {
								class_1146 lv = (class_1146)class_4009.this.field_17908.get(lv3);
								if (lv == null) {
									return class_1144.field_5592;
								} else {
									class_1111 lv2 = lv.method_4887();
									return new class_1111(
										lv2.method_4767().toString(),
										lv2.method_4771() * lv2.method_4771(),
										lv2.method_4772() * lv2.method_4772(),
										lv2.method_4894(),
										class_1111.class_1112.field_5474,
										lv2.method_4769() || lv2.method_4769(),
										lv2.method_4764(),
										lv2.method_4770()
									);
								}
							}

							@Override
							public void method_18188(class_1140 arg) {
								class_1146 lv = (class_1146)class_4009.this.field_17908.get(lv3);
								if (lv != null) {
									lv.method_18188(arg);
								}
							}
						};
						break;
					default:
						throw new IllegalStateException("Unknown SoundEventRegistration type: " + lv2.method_4768());
				}

				lv.method_4885(lv4);
			}
		}

		public void method_18186(Map<class_2960, class_1146> map, class_1140 arg) {
			map.clear();

			for (Entry<class_2960, class_1146> entry : this.field_17908.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
				((class_1146)entry.getValue()).method_18188(arg);
			}
		}
	}
}
