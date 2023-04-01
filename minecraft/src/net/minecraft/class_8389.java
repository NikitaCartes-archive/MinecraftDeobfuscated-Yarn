package net.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.text.Text;

public class class_8389 {
	private final Map<class_8373, Map<UUID, class_8388>> field_44029 = new HashMap();

	public void method_50589(class_8373 arg, UUID uUID, Text text, int i) {
		Map<UUID, class_8388> map = (Map<UUID, class_8388>)this.field_44029.computeIfAbsent(arg, argx -> new HashMap());
		map.compute(uUID, (uUIDx, argx) -> class_8388.method_50581(argx, text, i));
	}

	public int method_50588(class_8373 arg, UUID uUID) {
		Map<UUID, class_8388> map = (Map<UUID, class_8388>)this.field_44029.get(arg);
		if (map == null) {
			return 0;
		} else {
			class_8388 lv = (class_8388)map.get(uUID);
			return lv != null ? lv.voteCount() : 0;
		}
	}

	public int method_50595(Set<class_8373> set, UUID uUID) {
		return set.stream().mapToInt(arg -> this.method_50588(arg, uUID)).sum();
	}

	public void method_50591(class_8374 arg) {
		this.field_44029.clear();
		arg.options().forEach((argx, arg2) -> this.field_44029.put(argx, new HashMap(arg2.voters())));
	}

	public class_8374 method_50585() {
		return new class_8374(
			(Map<class_8373, class_8375>)this.field_44029
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> method_50594((Map<UUID, class_8388>)entry.getValue())))
		);
	}

	private static class_8375 method_50594(Map<UUID, class_8388> map) {
		return new class_8375(Map.copyOf(map));
	}

	public class_8374 method_50592(class_8376 arg, boolean bl) {
		Map<class_8373, class_8375> map = new HashMap();
		arg.options().keySet().forEach(argx -> {
			Map<UUID, class_8388> map2 = bl ? (Map)this.field_44029.remove(argx) : (Map)this.field_44029.get(argx);
			if (map2 != null) {
				map.put(argx, method_50594(map2));
			}
		});
		return new class_8374(map);
	}

	public class_8375 method_50590(class_8373 arg, boolean bl) {
		Map<UUID, class_8388> map = bl ? (Map)this.field_44029.remove(arg) : (Map)this.field_44029.get(arg);
		return map != null ? method_50594(map) : class_8375.field_43991;
	}

	public void method_50597(UUID uUID, BiConsumer<class_8373, class_8388> biConsumer) {
		this.field_44029.forEach((arg, map) -> {
			class_8388 lv = (class_8388)map.get(uUID);
			if (lv != null) {
				biConsumer.accept(arg, lv);
			}
		});
	}
}
