package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_314 {
	field_1809(new class_1799(class_1802.field_8251)),
	field_1806(new class_1799(class_2246.field_10104)),
	field_1803(new class_1799(class_1802.field_8725)),
	field_1813(new class_1799(class_1802.field_8475), new class_1799(class_1802.field_8845)),
	field_1810(new class_1799(class_1802.field_8187), new class_1799(class_1802.field_8279)),
	field_1804(new class_1799(class_1802.field_8251)),
	field_1808(new class_1799(class_1802.field_8389)),
	field_1811(new class_1799(class_2246.field_10340)),
	field_1812(new class_1799(class_1802.field_8187), new class_1799(class_1802.field_8687)),
	field_17110(new class_1799(class_1802.field_8251)),
	field_17111(new class_1799(class_2246.field_10080)),
	field_17112(new class_1799(class_1802.field_8699), new class_1799(class_1802.field_8416)),
	field_17113(new class_1799(class_1802.field_8251)),
	field_17114(new class_1799(class_1802.field_8389)),
	field_17764(new class_1799(class_1802.field_8525)),
	field_17765(new class_1799(class_1802.field_8389));

	private final List<class_1799> field_1807;

	private class_314(class_1799... args) {
		this.field_1807 = ImmutableList.copyOf(args);
	}

	public List<class_1799> method_1623() {
		return this.field_1807;
	}
}
