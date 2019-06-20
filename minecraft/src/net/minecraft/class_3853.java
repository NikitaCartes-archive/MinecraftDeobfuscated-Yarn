package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_3853 {
	public static final Map<class_3852, Int2ObjectMap<class_3853.class_1652[]>> field_17067 = class_156.method_654(
		Maps.<class_3852, Int2ObjectMap<class_3853.class_1652[]>>newHashMap(),
		hashMap -> {
			hashMap.put(
				class_3852.field_17056,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8861, 20, 8, 2),
							new class_3853.class_4161(class_1802.field_8567, 26, 8, 2),
							new class_3853.class_4161(class_1802.field_8179, 22, 8, 2),
							new class_3853.class_4161(class_1802.field_8186, 15, 8, 2),
							new class_3853.class_4165(class_1802.field_8229, 1, 6, 8, 1)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_2246.field_10261, 6, 6, 10),
							new class_3853.class_4165(class_1802.field_8741, 1, 4, 5),
							new class_3853.class_4165(class_1802.field_8279, 1, 4, 8, 5)
						},
						3,
						new class_3853.class_1652[]{new class_3853.class_4165(class_1802.field_8423, 3, 18, 10), new class_3853.class_4161(class_2246.field_10545, 4, 6, 20)},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4165(class_2246.field_10183, 1, 1, 6, 15),
							new class_3853.class_4166(class_1294.field_5904, 160, 15),
							new class_3853.class_4166(class_1294.field_5913, 160, 15),
							new class_3853.class_4166(class_1294.field_5911, 140, 15),
							new class_3853.class_4166(class_1294.field_5919, 120, 15),
							new class_3853.class_4166(class_1294.field_5899, 280, 15),
							new class_3853.class_4166(class_1294.field_5922, 7, 15)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4165(class_1802.field_8071, 3, 3, 30), new class_3853.class_4165(class_1802.field_8597, 4, 3, 30)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17057,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8276, 20, 8, 2),
							new class_3853.class_4161(class_1802.field_8713, 10, 8, 2),
							new class_3853.class_4164(class_1802.field_8429, 6, class_1802.field_8373, 6, 8, 1),
							new class_3853.class_4165(class_1802.field_8666, 3, 1, 8, 1)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8429, 15, 8, 10),
							new class_3853.class_4164(class_1802.field_8209, 6, class_1802.field_8509, 6, 8, 5),
							new class_3853.class_4165(class_1802.field_17346, 2, 1, 5)
						},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8209, 13, 8, 20), new class_3853.class_4163(class_1802.field_8378, 3, 2, 10, 0.2F)
						},
						4,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8846, 6, 6, 30)},
						5,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8323, 4, 6, 30),
							new class_3853.class_4162(
								1,
								6,
								30,
								ImmutableMap.<class_3854, class_1792>builder()
									.put(class_3854.field_17073, class_1802.field_8533)
									.put(class_3854.field_17077, class_1802.field_8486)
									.put(class_3854.field_17075, class_1802.field_8486)
									.put(class_3854.field_17071, class_1802.field_8730)
									.put(class_3854.field_17072, class_1802.field_8730)
									.put(class_3854.field_17074, class_1802.field_8094)
									.put(class_3854.field_17076, class_1802.field_8138)
									.build()
							)
						}
					)
				)
			);
			hashMap.put(
				class_3852.field_17063,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_2246.field_10446, 18, 8, 2),
							new class_3853.class_4161(class_2246.field_10113, 18, 8, 2),
							new class_3853.class_4161(class_2246.field_10146, 18, 8, 2),
							new class_3853.class_4161(class_2246.field_10423, 18, 8, 2),
							new class_3853.class_4165(class_1802.field_8868, 2, 1, 1)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8446, 12, 8, 10),
							new class_3853.class_4161(class_1802.field_8298, 12, 8, 10),
							new class_3853.class_4161(class_1802.field_8226, 12, 8, 10),
							new class_3853.class_4161(class_1802.field_8273, 12, 8, 10),
							new class_3853.class_4161(class_1802.field_8131, 12, 8, 10),
							new class_3853.class_4165(class_2246.field_10446, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10095, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10215, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10294, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10490, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10028, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10459, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10423, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10222, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10619, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10259, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10514, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10113, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10170, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10314, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10146, 1, 1, 8, 5),
							new class_3853.class_4165(class_2246.field_10466, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_9977, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10482, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10290, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10512, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10040, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10393, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10591, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10209, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10433, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10510, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10043, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10473, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10338, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10536, 1, 4, 8, 5),
							new class_3853.class_4165(class_2246.field_10106, 1, 4, 8, 5)
						},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8192, 12, 8, 20),
							new class_3853.class_4161(class_1802.field_8851, 12, 8, 20),
							new class_3853.class_4161(class_1802.field_8492, 12, 8, 20),
							new class_3853.class_4161(class_1802.field_8264, 12, 8, 20),
							new class_3853.class_4161(class_1802.field_8330, 12, 8, 20),
							new class_3853.class_4165(class_2246.field_10120, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10356, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10069, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10461, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10527, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10288, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10109, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10141, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10561, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10621, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10326, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10180, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10230, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10410, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10610, 3, 1, 6, 10),
							new class_3853.class_4165(class_2246.field_10019, 3, 1, 6, 10)
						},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8099, 12, 8, 30),
							new class_3853.class_4161(class_1802.field_8296, 12, 8, 30),
							new class_3853.class_4161(class_1802.field_8345, 12, 8, 30),
							new class_3853.class_4161(class_1802.field_8408, 12, 8, 30),
							new class_3853.class_4161(class_1802.field_8669, 12, 8, 30),
							new class_3853.class_4161(class_1802.field_8632, 12, 8, 30),
							new class_3853.class_4165(class_1802.field_8539, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8128, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8379, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8586, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8329, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8295, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8778, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8617, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8572, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8405, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8671, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8629, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8124, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8049, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8824, 3, 1, 6, 15),
							new class_3853.class_4165(class_1802.field_8855, 3, 1, 6, 15)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4165(class_1802.field_8892, 2, 3, 30)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17058,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8600, 32, 8, 2),
							new class_3853.class_4165(class_1802.field_8107, 1, 16, 1),
							new class_3853.class_4164(class_2246.field_10255, 10, class_1802.field_8145, 10, 6, 1)
						},
						2,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8145, 26, 6, 10), new class_3853.class_4165(class_1802.field_8102, 2, 1, 5)},
						3,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8276, 14, 8, 20), new class_3853.class_4165(class_1802.field_8399, 3, 1, 10)},
						4,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8153, 24, 8, 30), new class_3853.class_4163(class_1802.field_8102, 2, 2, 15)},
						5,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8366, 8, 6, 30),
							new class_3853.class_4163(class_1802.field_8399, 3, 2, 15),
							new class_3853.class_4167(class_1802.field_8107, 5, class_1802.field_8087, 5, 2, 6, 30)
						}
					)
				)
			);
			hashMap.put(
				class_3852.field_17060,
				method_16928(
					ImmutableMap.<Integer, class_3853.class_1652[]>builder()
						.put(
							1,
							new class_3853.class_1652[]{
								new class_3853.class_4161(class_1802.field_8407, 24, 8, 2), new class_3853.class_1648(1), new class_3853.class_4165(class_2246.field_10504, 6, 3, 6, 1)
							}
						)
						.put(
							2,
							new class_3853.class_1652[]{
								new class_3853.class_4161(class_1802.field_8529, 4, 6, 10), new class_3853.class_1648(5), new class_3853.class_4165(class_1802.field_16539, 1, 1, 5)
							}
						)
						.put(
							3,
							new class_3853.class_1652[]{
								new class_3853.class_4161(class_1802.field_8794, 5, 6, 20), new class_3853.class_1648(10), new class_3853.class_4165(class_1802.field_8280, 1, 4, 10)
							}
						)
						.put(
							4,
							new class_3853.class_1652[]{
								new class_3853.class_4161(class_1802.field_8674, 2, 6, 30),
								new class_3853.class_1648(15),
								new class_3853.class_4165(class_1802.field_8557, 5, 1, 15),
								new class_3853.class_4165(class_1802.field_8251, 4, 1, 15)
							}
						)
						.put(5, new class_3853.class_1652[]{new class_3853.class_4165(class_1802.field_8448, 20, 1, 30)})
						.build()
				)
			);
			hashMap.put(
				class_3852.field_17054,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8407, 24, 8, 2), new class_3853.class_4165(class_1802.field_8895, 7, 1, 1)},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8141, 11, 8, 10), new class_3853.class_1654(13, "Monument", class_20.class_21.field_98, 6, 5)
						},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8251, 1, 6, 20), new class_3853.class_1654(14, "Mansion", class_20.class_21.field_88, 6, 10)
						},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4165(class_1802.field_8143, 7, 1, 15),
							new class_3853.class_4165(class_1802.field_8539, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8128, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8379, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8586, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8329, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8295, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8778, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8617, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8572, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8405, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8671, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8629, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8124, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8049, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8824, 3, 1, 15),
							new class_3853.class_4165(class_1802.field_8855, 3, 1, 15)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4165(class_1802.field_18674, 8, 1, 30)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17055,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8511, 32, 8, 2), new class_3853.class_4165(class_1802.field_8725, 1, 2, 1)},
						2,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8695, 3, 6, 10), new class_3853.class_4165(class_1802.field_8759, 1, 1, 5)},
						3,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8073, 2, 6, 20), new class_3853.class_4165(class_2246.field_10171, 4, 1, 6, 10)},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8161, 4, 6, 30),
							new class_3853.class_4161(class_1802.field_8469, 9, 6, 30),
							new class_3853.class_4165(class_1802.field_8634, 5, 1, 15)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8790, 22, 6, 30), new class_3853.class_4165(class_1802.field_8287, 3, 1, 30)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17052,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8713, 15, 8, 2),
							new class_3853.class_4165(new class_1799(class_1802.field_8396), 7, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8660), 4, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8743), 5, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8523), 9, 1, 6, 1, 0.2F)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8620, 4, 6, 10),
							new class_3853.class_4165(new class_1799(class_1802.field_16315), 36, 1, 6, 5, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8313), 1, 1, 6, 5, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8218), 3, 1, 6, 5, 0.2F)
						},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8187, 1, 6, 20),
							new class_3853.class_4161(class_1802.field_8477, 1, 6, 20),
							new class_3853.class_4165(new class_1799(class_1802.field_8283), 1, 1, 6, 10, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8873), 4, 1, 6, 10, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8255), 5, 1, 6, 10, 0.2F)
						},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4163(class_1802.field_8348, 14, 2, 15, 0.2F), new class_3853.class_4163(class_1802.field_8285, 8, 2, 15, 0.2F)
						},
						5,
						new class_3853.class_1652[]{
							new class_3853.class_4163(class_1802.field_8805, 8, 2, 30, 0.2F), new class_3853.class_4163(class_1802.field_8058, 16, 2, 30, 0.2F)
						}
					)
				)
			);
			hashMap.put(
				class_3852.field_17065,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8713, 15, 8, 2),
							new class_3853.class_4165(new class_1799(class_1802.field_8475), 3, 1, 6, 1, 0.2F),
							new class_3853.class_4163(class_1802.field_8371, 2, 2, 1)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8620, 4, 6, 10), new class_3853.class_4165(new class_1799(class_1802.field_16315), 36, 1, 6, 5, 0.2F)
						},
						3,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8145, 24, 6, 20)},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8477, 1, 6, 30), new class_3853.class_4163(class_1802.field_8556, 12, 2, 15, 0.2F)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4163(class_1802.field_8802, 8, 2, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17064,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8713, 15, 8, 2),
							new class_3853.class_4165(new class_1799(class_1802.field_8062), 1, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8776), 1, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8387), 1, 1, 6, 1, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8431), 1, 1, 6, 1, 0.2F)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8620, 4, 6, 10), new class_3853.class_4165(new class_1799(class_1802.field_16315), 36, 1, 6, 5, 0.2F)
						},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8145, 30, 6, 20),
							new class_3853.class_4163(class_1802.field_8475, 1, 2, 10, 0.2F),
							new class_3853.class_4163(class_1802.field_8699, 2, 2, 10, 0.2F),
							new class_3853.class_4163(class_1802.field_8403, 3, 2, 10, 0.2F),
							new class_3853.class_4165(new class_1799(class_1802.field_8527), 4, 1, 2, 10, 0.2F)
						},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8477, 1, 6, 30),
							new class_3853.class_4163(class_1802.field_8556, 12, 2, 15, 0.2F),
							new class_3853.class_4163(class_1802.field_8250, 5, 2, 15, 0.2F)
						},
						5,
						new class_3853.class_1652[]{new class_3853.class_4163(class_1802.field_8377, 13, 2, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17053,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8726, 14, 8, 2),
							new class_3853.class_4161(class_1802.field_8389, 7, 8, 2),
							new class_3853.class_4161(class_1802.field_8504, 4, 8, 2),
							new class_3853.class_4165(class_1802.field_8308, 1, 1, 1)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8713, 15, 8, 2),
							new class_3853.class_4165(class_1802.field_8261, 1, 5, 8, 5),
							new class_3853.class_4165(class_1802.field_8544, 1, 8, 8, 5)
						},
						3,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8748, 7, 8, 20), new class_3853.class_4161(class_1802.field_8046, 10, 8, 20)},
						4,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_17533, 10, 6, 30)},
						5,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_16998, 10, 6, 30)}
					)
				)
			);
			hashMap.put(
				class_3852.field_17059,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8745, 6, 8, 2),
							new class_3853.class_4160(class_1802.field_8570, 3),
							new class_3853.class_4160(class_1802.field_8577, 7)
						},
						2,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8145, 26, 6, 10),
							new class_3853.class_4160(class_1802.field_8267, 5, 6, 5),
							new class_3853.class_4160(class_1802.field_8370, 4, 6, 5)
						},
						3,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8245, 9, 6, 20), new class_3853.class_4160(class_1802.field_8577, 7)},
						4,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8161, 4, 6, 30), new class_3853.class_4160(class_1802.field_18138, 6, 6, 15)},
						5,
						new class_3853.class_1652[]{
							new class_3853.class_4165(new class_1799(class_1802.field_8175), 6, 1, 6, 30, 0.2F), new class_3853.class_4160(class_1802.field_8267, 5, 6, 30)
						}
					)
				)
			);
			hashMap.put(
				class_3852.field_17061,
				method_16928(
					ImmutableMap.of(
						1,
						new class_3853.class_1652[]{new class_3853.class_4161(class_1802.field_8696, 10, 8, 2), new class_3853.class_4165(class_1802.field_8621, 1, 10, 8, 1)},
						2,
						new class_3853.class_1652[]{new class_3853.class_4161(class_2246.field_10340, 20, 8, 10), new class_3853.class_4165(class_2246.field_10552, 1, 4, 8, 5)},
						3,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_2246.field_10474, 16, 8, 20),
							new class_3853.class_4161(class_2246.field_10115, 16, 8, 20),
							new class_3853.class_4161(class_2246.field_10508, 16, 8, 20),
							new class_3853.class_4165(class_2246.field_10093, 1, 4, 8, 10),
							new class_3853.class_4165(class_2246.field_10346, 1, 4, 8, 10),
							new class_3853.class_4165(class_2246.field_10289, 1, 4, 8, 10)
						},
						4,
						new class_3853.class_1652[]{
							new class_3853.class_4161(class_1802.field_8155, 12, 6, 30),
							new class_3853.class_4165(class_2246.field_10184, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10611, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10409, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10325, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10349, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10590, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10626, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10328, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10444, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10015, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10014, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10526, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10235, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10570, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10143, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10123, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10280, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10595, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10550, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10345, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10220, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10052, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10501, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10383, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10567, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10538, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10046, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10475, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10078, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10426, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10096, 1, 1, 6, 15),
							new class_3853.class_4165(class_2246.field_10004, 1, 1, 6, 15)
						},
						5,
						new class_3853.class_1652[]{
							new class_3853.class_4165(class_2246.field_10437, 1, 1, 6, 30), new class_3853.class_4165(class_2246.field_10153, 1, 1, 6, 30)
						}
					)
				)
			);
		}
	);
	public static final Int2ObjectMap<class_3853.class_1652[]> field_17724 = method_16928(
		ImmutableMap.of(
			1,
			new class_3853.class_1652[]{
				new class_3853.class_4165(class_1802.field_17498, 2, 1, 5, 1),
				new class_3853.class_4165(class_1802.field_8777, 4, 1, 5, 1),
				new class_3853.class_4165(class_1802.field_8801, 2, 1, 5, 1),
				new class_3853.class_4165(class_1802.field_8864, 5, 1, 5, 1),
				new class_3853.class_4165(class_1802.field_8471, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17531, 1, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17518, 1, 1, 4, 1),
				new class_3853.class_4165(class_1802.field_17532, 3, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17520, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8491, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_8880, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17499, 1, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17500, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17501, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17502, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17509, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17510, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17511, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17512, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17513, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17514, 1, 1, 7, 1),
				new class_3853.class_4165(class_1802.field_8317, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_8309, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_8706, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_8188, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17539, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17537, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17540, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17538, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17535, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17536, 5, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8264, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8446, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8345, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8330, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8226, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8408, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8851, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8669, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8192, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8298, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8296, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8273, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8131, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8492, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8099, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8632, 1, 3, 12, 1),
				new class_3853.class_4165(class_1802.field_8474, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8883, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8278, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8104, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8402, 3, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_17523, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17516, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17517, 1, 1, 12, 1),
				new class_3853.class_4165(class_1802.field_17524, 1, 2, 5, 1),
				new class_3853.class_4165(class_1802.field_8858, 1, 8, 8, 1),
				new class_3853.class_4165(class_1802.field_8200, 1, 4, 6, 1)
			},
			2,
			new class_3853.class_1652[]{
				new class_3853.class_4165(class_1802.field_8478, 5, 1, 4, 1),
				new class_3853.class_4165(class_1802.field_8108, 5, 1, 4, 1),
				new class_3853.class_4165(class_1802.field_8081, 3, 1, 6, 1),
				new class_3853.class_4165(class_1802.field_8178, 6, 1, 6, 1),
				new class_3853.class_4165(class_1802.field_8054, 1, 1, 8, 1),
				new class_3853.class_4165(class_1802.field_8382, 3, 3, 6, 1)
			}
		)
	);

	private static Int2ObjectMap<class_3853.class_1652[]> method_16928(ImmutableMap<Integer, class_3853.class_1652[]> immutableMap) {
		return new Int2ObjectOpenHashMap<>(immutableMap);
	}

	static class class_1648 implements class_3853.class_1652 {
		private final int field_18557;

		public class_1648(int i) {
			this.field_18557 = i;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1887 lv = class_2378.field_11160.method_10240(random);
			int i = class_3532.method_15395(random, lv.method_8187(), lv.method_8183());
			class_1799 lv2 = class_1772.method_7808(new class_1889(lv, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
			if (lv.method_8193()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			return new class_1914(new class_1799(class_1802.field_8687, j), new class_1799(class_1802.field_8529), lv2, 6, this.field_18557, 0.2F);
		}
	}

	public interface class_1652 {
		@Nullable
		class_1914 method_7246(class_1297 arg, Random random);
	}

	static class class_1654 implements class_3853.class_1652 {
		private final int field_18589;
		private final String field_7474;
		private final class_20.class_21 field_7473;
		private final int field_18590;
		private final int field_18591;

		public class_1654(int i, String string, class_20.class_21 arg, int j, int k) {
			this.field_18589 = i;
			this.field_7474 = string;
			this.field_7473 = arg;
			this.field_18590 = j;
			this.field_18591 = k;
		}

		@Nullable
		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1937 lv = arg.field_6002;
			class_2338 lv2 = lv.method_8487(this.field_7474, new class_2338(arg), 100, true);
			if (lv2 != null) {
				class_1799 lv3 = class_1806.method_8005(lv, lv2.method_10263(), lv2.method_10260(), (byte)2, true, true);
				class_1806.method_8002(lv, lv3);
				class_22.method_110(lv3, lv2, "+", this.field_7473);
				lv3.method_7977(new class_2588("filled_map." + this.field_7474.toLowerCase(Locale.ROOT)));
				return new class_1914(
					new class_1799(class_1802.field_8687, this.field_18589), new class_1799(class_1802.field_8251), lv3, this.field_18590, this.field_18591, 0.2F
				);
			} else {
				return null;
			}
		}
	}

	static class class_4160 implements class_3853.class_1652 {
		private final class_1792 field_18544;
		private final int field_18545;
		private final int field_18546;
		private final int field_18547;

		public class_4160(class_1792 arg, int i) {
			this(arg, i, 6, 1);
		}

		public class_4160(class_1792 arg, int i, int j, int k) {
			this.field_18544 = arg;
			this.field_18545 = i;
			this.field_18546 = j;
			this.field_18547 = k;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1799 lv = new class_1799(class_1802.field_8687, this.field_18545);
			class_1799 lv2 = new class_1799(this.field_18544);
			if (this.field_18544 instanceof class_4057) {
				List<class_1769> list = Lists.<class_1769>newArrayList();
				list.add(method_19200(random));
				if (random.nextFloat() > 0.7F) {
					list.add(method_19200(random));
				}

				if (random.nextFloat() > 0.8F) {
					list.add(method_19200(random));
				}

				lv2 = class_1768.method_19261(lv2, list);
			}

			return new class_1914(lv, lv2, this.field_18546, this.field_18547, 0.2F);
		}

		private static class_1769 method_19200(Random random) {
			return class_1769.method_7803(class_1767.method_7791(random.nextInt(16)));
		}
	}

	static class class_4161 implements class_3853.class_1652 {
		private final class_1792 field_18548;
		private final int field_18549;
		private final int field_18550;
		private final int field_18551;
		private final float field_18552;

		public class_4161(class_1935 arg, int i, int j, int k) {
			this.field_18548 = arg.method_8389();
			this.field_18549 = i;
			this.field_18550 = j;
			this.field_18551 = k;
			this.field_18552 = 0.05F;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1799 lv = new class_1799(this.field_18548, this.field_18549);
			return new class_1914(lv, new class_1799(class_1802.field_8687), this.field_18550, this.field_18551, this.field_18552);
		}
	}

	static class class_4162 implements class_3853.class_1652 {
		private final Map<class_3854, class_1792> field_18553;
		private final int field_18554;
		private final int field_18555;
		private final int field_18556;

		public class_4162(int i, int j, int k, Map<class_3854, class_1792> map) {
			class_2378.field_17166.method_10220().filter(arg -> !map.containsKey(arg)).findAny().ifPresent(arg -> {
				throw new IllegalStateException("Missing trade for villager type: " + class_2378.field_17166.method_10221(arg));
			});
			this.field_18553 = map;
			this.field_18554 = i;
			this.field_18555 = j;
			this.field_18556 = k;
		}

		@Nullable
		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			if (arg instanceof class_3851) {
				class_1799 lv = new class_1799((class_1935)this.field_18553.get(((class_3851)arg).method_7231().method_16919()), this.field_18554);
				return new class_1914(lv, new class_1799(class_1802.field_8687), this.field_18555, this.field_18556, 0.05F);
			} else {
				return null;
			}
		}
	}

	static class class_4163 implements class_3853.class_1652 {
		private final class_1799 field_18558;
		private final int field_18559;
		private final int field_18560;
		private final int field_18561;
		private final float field_18562;

		public class_4163(class_1792 arg, int i, int j, int k) {
			this(arg, i, j, k, 0.05F);
		}

		public class_4163(class_1792 arg, int i, int j, int k, float f) {
			this.field_18558 = new class_1799(arg);
			this.field_18559 = i;
			this.field_18560 = j;
			this.field_18561 = k;
			this.field_18562 = f;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			int i = 5 + random.nextInt(15);
			class_1799 lv = class_1890.method_8233(random, new class_1799(this.field_18558.method_7909()), i, false);
			int j = Math.min(this.field_18559 + i, 64);
			class_1799 lv2 = new class_1799(class_1802.field_8687, j);
			return new class_1914(lv2, lv, this.field_18560, this.field_18561, this.field_18562);
		}
	}

	static class class_4164 implements class_3853.class_1652 {
		private final class_1799 field_18563;
		private final int field_18564;
		private final int field_18565;
		private final class_1799 field_18566;
		private final int field_18567;
		private final int field_18568;
		private final int field_18569;
		private final float field_18570;

		public class_4164(class_1935 arg, int i, class_1792 arg2, int j, int k, int l) {
			this(arg, i, 1, arg2, j, k, l);
		}

		public class_4164(class_1935 arg, int i, int j, class_1792 arg2, int k, int l, int m) {
			this.field_18563 = new class_1799(arg);
			this.field_18564 = i;
			this.field_18565 = j;
			this.field_18566 = new class_1799(arg2);
			this.field_18567 = k;
			this.field_18568 = l;
			this.field_18569 = m;
			this.field_18570 = 0.05F;
		}

		@Nullable
		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			return new class_1914(
				new class_1799(class_1802.field_8687, this.field_18565),
				new class_1799(this.field_18563.method_7909(), this.field_18564),
				new class_1799(this.field_18566.method_7909(), this.field_18567),
				this.field_18568,
				this.field_18569,
				this.field_18570
			);
		}
	}

	static class class_4165 implements class_3853.class_1652 {
		private final class_1799 field_18571;
		private final int field_18572;
		private final int field_18573;
		private final int field_18574;
		private final int field_18575;
		private final float field_18576;

		public class_4165(class_2248 arg, int i, int j, int k, int l) {
			this(new class_1799(arg), i, j, k, l);
		}

		public class_4165(class_1792 arg, int i, int j, int k) {
			this(new class_1799(arg), i, j, 6, k);
		}

		public class_4165(class_1792 arg, int i, int j, int k, int l) {
			this(new class_1799(arg), i, j, k, l);
		}

		public class_4165(class_1799 arg, int i, int j, int k, int l) {
			this(arg, i, j, k, l, 0.05F);
		}

		public class_4165(class_1799 arg, int i, int j, int k, int l, float f) {
			this.field_18571 = arg;
			this.field_18572 = i;
			this.field_18573 = j;
			this.field_18574 = k;
			this.field_18575 = l;
			this.field_18576 = f;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			return new class_1914(
				new class_1799(class_1802.field_8687, this.field_18572),
				new class_1799(this.field_18571.method_7909(), this.field_18573),
				this.field_18574,
				this.field_18575,
				this.field_18576
			);
		}
	}

	static class class_4166 implements class_3853.class_1652 {
		final class_1291 field_18577;
		final int field_18578;
		final int field_18579;
		private final float field_18580;

		public class_4166(class_1291 arg, int i, int j) {
			this.field_18577 = arg;
			this.field_18578 = i;
			this.field_18579 = j;
			this.field_18580 = 0.05F;
		}

		@Nullable
		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1799 lv = new class_1799(class_1802.field_8766, 1);
			class_1830.method_8021(lv, this.field_18577, this.field_18578);
			return new class_1914(new class_1799(class_1802.field_8687, 1), lv, 6, this.field_18579, this.field_18580);
		}
	}

	static class class_4167 implements class_3853.class_1652 {
		private final class_1799 field_18581;
		private final int field_18582;
		private final int field_18583;
		private final int field_18584;
		private final int field_18585;
		private final class_1792 field_18586;
		private final int field_18587;
		private final float field_18588;

		public class_4167(class_1792 arg, int i, class_1792 arg2, int j, int k, int l, int m) {
			this.field_18581 = new class_1799(arg2);
			this.field_18583 = k;
			this.field_18584 = l;
			this.field_18585 = m;
			this.field_18586 = arg;
			this.field_18587 = i;
			this.field_18582 = j;
			this.field_18588 = 0.05F;
		}

		@Override
		public class_1914 method_7246(class_1297 arg, Random random) {
			class_1799 lv = new class_1799(class_1802.field_8687, this.field_18583);
			List<class_1842> list = (List<class_1842>)class_2378.field_11143
				.method_10220()
				.filter(argx -> !argx.method_8049().isEmpty() && class_1845.method_20361(argx))
				.collect(Collectors.toList());
			class_1842 lv2 = (class_1842)list.get(random.nextInt(list.size()));
			class_1799 lv3 = class_1844.method_8061(new class_1799(this.field_18581.method_7909(), this.field_18582), lv2);
			return new class_1914(lv, new class_1799(this.field_18586, this.field_18587), lv3, this.field_18584, this.field_18585, this.field_18588);
		}
	}
}
