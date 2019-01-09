package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3360 {
	private static final Logger field_14437 = LogManager.getLogger();
	private static final Map<String, String> field_14435 = class_156.method_654(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Village", "Village");
		hashMap.put("Mineshaft", "Mineshaft");
		hashMap.put("Mansion", "Mansion");
		hashMap.put("Igloo", "Temple");
		hashMap.put("Desert_Pyramid", "Temple");
		hashMap.put("Jungle_Pyramid", "Temple");
		hashMap.put("Swamp_Hut", "Temple");
		hashMap.put("Stronghold", "Stronghold");
		hashMap.put("Monument", "Monument");
		hashMap.put("Fortress", "Fortress");
		hashMap.put("EndCity", "EndCity");
	});
	private static final Map<String, String> field_14436 = class_156.method_654(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Iglu", "Igloo");
		hashMap.put("TeDP", "Desert_Pyramid");
		hashMap.put("TeJP", "Jungle_Pyramid");
		hashMap.put("TeSH", "Swamp_Hut");
	});
	private final boolean field_14434;
	private final Map<String, Long2ObjectMap<class_2487>> field_14432 = Maps.<String, Long2ObjectMap<class_2487>>newHashMap();
	private final Map<String, class_3440> field_14433 = Maps.<String, class_3440>newHashMap();

	public class_3360(@Nullable class_37 arg) {
		this.method_14734(arg);
		boolean bl = false;

		for (String string : this.method_14743()) {
			bl |= this.field_14432.get(string) != null;
		}

		this.field_14434 = bl;
	}

	public void method_14744(long l) {
		for (String string : this.method_14740()) {
			class_3440 lv = (class_3440)this.field_14433.get(string);
			if (lv != null && lv.method_14894(l)) {
				lv.method_14895(l);
				lv.method_80();
			}
		}
	}

	public class_2487 method_14735(class_2487 arg) {
		class_2487 lv = arg.method_10562("Level");
		class_1923 lv2 = new class_1923(lv.method_10550("xPos"), lv.method_10550("zPos"));
		if (this.method_14737(lv2.field_9181, lv2.field_9180)) {
			arg = this.method_14741(arg, lv2);
		}

		class_2487 lv3 = lv.method_10562("Structures");
		class_2487 lv4 = lv3.method_10562("References");

		for (String string : this.method_14743()) {
			class_3195<?> lv5 = (class_3195<?>)class_3031.field_13557.get(string.toLowerCase(Locale.ROOT));
			if (!lv4.method_10573(string, 12) && lv5 != null) {
				int i = lv5.method_14021();
				LongList longList = new LongArrayList();

				for (int j = lv2.field_9181 - i; j <= lv2.field_9181 + i; j++) {
					for (int k = lv2.field_9180 - i; k <= lv2.field_9180 + i; k++) {
						if (this.method_14738(j, k, string)) {
							longList.add(class_1923.method_8331(j, k));
						}
					}
				}

				lv4.method_10538(string, longList);
			}
		}

		lv3.method_10566("References", lv4);
		lv.method_10566("Structures", lv3);
		arg.method_10566("Level", lv);
		return arg;
	}

	protected abstract String[] method_14740();

	protected abstract String[] method_14743();

	private boolean method_14738(int i, int j, String string) {
		return !this.field_14434
			? false
			: this.field_14432.get(string) != null && ((class_3440)this.field_14433.get(field_14435.get(string))).method_14897(class_1923.method_8331(i, j));
	}

	private boolean method_14737(int i, int j) {
		if (!this.field_14434) {
			return false;
		} else {
			for (String string : this.method_14743()) {
				if (this.field_14432.get(string) != null && ((class_3440)this.field_14433.get(field_14435.get(string))).method_14894(class_1923.method_8331(i, j))) {
					return true;
				}
			}

			return false;
		}
	}

	private class_2487 method_14741(class_2487 arg, class_1923 arg2) {
		class_2487 lv = arg.method_10562("Level");
		class_2487 lv2 = lv.method_10562("Structures");
		class_2487 lv3 = lv2.method_10562("Starts");

		for (String string : this.method_14743()) {
			Long2ObjectMap<class_2487> long2ObjectMap = (Long2ObjectMap<class_2487>)this.field_14432.get(string);
			if (long2ObjectMap != null) {
				long l = arg2.method_8324();
				if (((class_3440)this.field_14433.get(field_14435.get(string))).method_14894(l)) {
					class_2487 lv4 = long2ObjectMap.get(l);
					if (lv4 != null) {
						lv3.method_10566(string, lv4);
					}
				}
			}
		}

		lv2.method_10566("Starts", lv3);
		lv.method_10566("Structures", lv2);
		arg.method_10566("Level", lv);
		return arg;
	}

	private void method_14734(@Nullable class_37 arg) {
		if (arg != null) {
			for (String string : this.method_14740()) {
				class_2487 lv = new class_2487();

				try {
					lv = arg.method_264(string, 1493).method_10562("data").method_10562("Features");
					if (lv.isEmpty()) {
						continue;
					}
				} catch (IOException var15) {
				}

				for (String string2 : lv.method_10541()) {
					class_2487 lv2 = lv.method_10562(string2);
					long l = class_1923.method_8331(lv2.method_10550("ChunkX"), lv2.method_10550("ChunkZ"));
					class_2499 lv3 = lv2.method_10554("Children", 10);
					if (!lv3.isEmpty()) {
						String string3 = lv3.method_10602(0).method_10558("id");
						String string4 = (String)field_14436.get(string3);
						if (string4 != null) {
							lv2.method_10582("id", string4);
						}
					}

					String string3 = lv2.method_10558("id");
					((Long2ObjectMap)this.field_14432.computeIfAbsent(string3, stringx -> new Long2ObjectOpenHashMap())).put(l, lv2);
				}

				String string5 = string + "_index";
				class_3440 lv4 = arg.method_268(class_2874.field_13072, class_3440::new, string5);
				if (lv4 != null && !lv4.method_14898().isEmpty()) {
					this.field_14433.put(string, lv4);
				} else {
					class_3440 lv5 = new class_3440(string5);
					this.field_14433.put(string, lv5);

					for (String string6 : lv.method_10541()) {
						class_2487 lv6 = lv.method_10562(string6);
						lv5.method_14896(class_1923.method_8331(lv6.method_10550("ChunkX"), lv6.method_10550("ChunkZ")));
					}

					arg.method_267(class_2874.field_13072, string5, lv5);
					lv5.method_80();
				}
			}
		}
	}

	public static class_3360 method_14745(class_2874 arg, @Nullable class_37 arg2) {
		if (arg == class_2874.field_13072) {
			return new class_3360.class_3362(arg2);
		} else if (arg == class_2874.field_13076) {
			return new class_3360.class_3361(arg2);
		} else if (arg == class_2874.field_13078) {
			return new class_3360.class_3363(arg2);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", arg));
		}
	}

	public static class class_3361 extends class_3360 {
		private static final String[] field_14438 = new String[]{"Fortress"};

		public class_3361(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14438;
		}

		@Override
		protected String[] method_14743() {
			return field_14438;
		}
	}

	public static class class_3362 extends class_3360 {
		private static final String[] field_14440 = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
		private static final String[] field_14439 = new String[]{
			"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"
		};

		public class_3362(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14440;
		}

		@Override
		protected String[] method_14743() {
			return field_14439;
		}
	}

	public static class class_3363 extends class_3360 {
		private static final String[] field_14441 = new String[]{"EndCity"};

		public class_3363(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14441;
		}

		@Override
		protected String[] method_14743() {
			return field_14441;
		}
	}
}
