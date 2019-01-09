package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;

public interface class_3854 {
	class_3854 field_17071 = method_16931("desert");
	class_3854 field_17072 = method_16931("jungle");
	class_3854 field_17073 = method_16931("plains");
	class_3854 field_17074 = method_16931("savanna");
	class_3854 field_17075 = method_16931("snow");
	class_3854 field_17076 = method_16931("swamp");
	class_3854 field_17077 = method_16931("taiga");
	Map<class_1959, class_3854> field_17078 = class_156.method_654(Maps.<class_1959, class_3854>newHashMap(), hashMap -> {
		hashMap.put(class_1972.field_9415, field_17071);
		hashMap.put(class_1972.field_9433, field_17071);
		hashMap.put(class_1972.field_9424, field_17071);
		hashMap.put(class_1972.field_9466, field_17071);
		hashMap.put(class_1972.field_9427, field_17071);
		hashMap.put(class_1972.field_9443, field_17071);
		hashMap.put(class_1972.field_9406, field_17071);
		hashMap.put(class_1972.field_9413, field_17071);
		hashMap.put(class_1972.field_9410, field_17071);
		hashMap.put(class_1972.field_9440, field_17072);
		hashMap.put(class_1972.field_9468, field_17072);
		hashMap.put(class_1972.field_9417, field_17072);
		hashMap.put(class_1972.field_9474, field_17072);
		hashMap.put(class_1972.field_9432, field_17072);
		hashMap.put(class_1972.field_9426, field_17072);
		hashMap.put(class_1972.field_9405, field_17072);
		hashMap.put(class_1972.field_9430, field_17074);
		hashMap.put(class_1972.field_9449, field_17074);
		hashMap.put(class_1972.field_9456, field_17074);
		hashMap.put(class_1972.field_9445, field_17074);
		hashMap.put(class_1972.field_9418, field_17075);
		hashMap.put(class_1972.field_9435, field_17075);
		hashMap.put(class_1972.field_9463, field_17075);
		hashMap.put(class_1972.field_9453, field_17075);
		hashMap.put(class_1972.field_9478, field_17075);
		hashMap.put(class_1972.field_9444, field_17075);
		hashMap.put(class_1972.field_9454, field_17075);
		hashMap.put(class_1972.field_9425, field_17075);
		hashMap.put(class_1972.field_9437, field_17075);
		hashMap.put(class_1972.field_9452, field_17075);
		hashMap.put(class_1972.field_9471, field_17076);
		hashMap.put(class_1972.field_9479, field_17076);
		hashMap.put(class_1972.field_9416, field_17077);
		hashMap.put(class_1972.field_9404, field_17077);
		hashMap.put(class_1972.field_9477, field_17077);
		hashMap.put(class_1972.field_9429, field_17077);
		hashMap.put(class_1972.field_9476, field_17077);
		hashMap.put(class_1972.field_9436, field_17077);
		hashMap.put(class_1972.field_9464, field_17077);
		hashMap.put(class_1972.field_9472, field_17077);
		hashMap.put(class_1972.field_9420, field_17077);
		hashMap.put(class_1972.field_9428, field_17077);
		hashMap.put(class_1972.field_9422, field_17077);
		hashMap.put(class_1972.field_9460, field_17077);
	});

	static class_3854 method_16931(String string) {
		return class_2378.field_17166.method_10272(new class_2960(string), new class_3854() {
			public String toString() {
				return string;
			}
		});
	}

	static class_3854 method_16930(class_1959 arg) {
		return (class_3854)field_17078.getOrDefault(arg, field_17073);
	}
}
