package net.minecraft;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public enum class_9064 {
	PASSENGER(class_9064.class_9065.field_47750),
	VEHICLE(class_9064.class_9065.field_47749),
	NAME_TAG(class_9064.class_9065.field_47750);

	private final class_9064.class_9065 field_47746;

	private class_9064(class_9064.class_9065 arg) {
		this.field_47746 = arg;
	}

	public List<Vec3d> method_55670(float f, float g) {
		return this.field_47746.create(f, g);
	}

	public interface class_9065 {
		List<Vec3d> field_47748 = List.of(Vec3d.ZERO);
		class_9064.class_9065 field_47749 = (f, g) -> field_47748;
		class_9064.class_9065 field_47750 = (f, g) -> List.of(new Vec3d(0.0, (double)g, 0.0));

		List<Vec3d> create(float f, float g);
	}
}
