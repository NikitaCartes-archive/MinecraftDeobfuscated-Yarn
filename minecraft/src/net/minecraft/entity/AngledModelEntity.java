package net.minecraft.entity;

import java.util.Map;
import net.minecraft.util.math.Vec3f;

public interface AngledModelEntity {
	Map<String, Vec3f> getModelAngles();
}
