package net.minecraft;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1082 {
	public static final class_1083 field_5343 = new class_1083();
	private final Collection<class_1077> field_5342;

	public class_1082(Collection<class_1077> collection) {
		this.field_5342 = collection;
	}

	public Collection<class_1077> method_4694() {
		return this.field_5342;
	}
}
