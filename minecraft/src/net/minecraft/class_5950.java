package net.minecraft;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_5950 {
	public static final class_5950 field_29555 = new class_5950();
	private final WeakHashMap<class_5952, Void> field_29556 = new WeakHashMap();

	private class_5950() {
	}

	public void method_34702(class_5952 arg) {
		this.field_29556.put(arg, null);
	}

	@Environment(EnvType.CLIENT)
	public Map<class_5949, List<class_5948>> method_34701() {
		return (Map<class_5949, List<class_5948>>)this.field_29556
			.keySet()
			.stream()
			.flatMap(arg -> arg.method_34705().stream())
			.collect(Collectors.collectingAndThen(Collectors.groupingBy(class_5948::method_34699), EnumMap::new));
	}
}
