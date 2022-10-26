package net.minecraft.entity;

import java.util.Map;
import org.joml.Vector3f;

public interface AngledModelEntity {
	Map<String, Vector3f> getModelAngles();
}
