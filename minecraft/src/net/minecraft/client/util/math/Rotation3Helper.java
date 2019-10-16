package net.minecraft.client.util.math;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Rotation3Helper {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EnumMap<Direction, Rotation3> DIRECTION_ROTATIONS = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.SOUTH, Rotation3.identity());
		enumMap.put(Direction.EAST, new Rotation3(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), null, null));
		enumMap.put(Direction.WEST, new Rotation3(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.NORTH, new Rotation3(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), null, null));
		enumMap.put(Direction.UP, new Rotation3(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.DOWN, new Rotation3(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), null, null));
	});
	public static final EnumMap<Direction, Rotation3> INVERSED_DIRECTION_ROTATIONS = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		for (Direction direction : Direction.values()) {
			enumMap.put(direction, ((Rotation3)DIRECTION_ROTATIONS.get(direction)).invert());
		}
	});

	public static Rotation3 setupUvLock(Rotation3 rotation3) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 3, 0.5F);
		matrix4f.set(1, 3, 0.5F);
		matrix4f.set(2, 3, 0.5F);
		matrix4f.multiply(rotation3.getMatrix());
		Matrix4f matrix4f2 = new Matrix4f();
		matrix4f2.loadIdentity();
		matrix4f2.set(0, 3, -0.5F);
		matrix4f2.set(1, 3, -0.5F);
		matrix4f2.set(2, 3, -0.5F);
		matrix4f.multiply(matrix4f2);
		return new Rotation3(matrix4f);
	}

	public static Rotation3 uvLock(Rotation3 rotation3, Direction direction, Supplier<String> supplier) {
		Direction direction2 = Direction.transform(rotation3.getMatrix(), direction);
		Rotation3 rotation32 = rotation3.invert();
		if (rotation32 == null) {
			LOGGER.warn((String)supplier.get());
			return new Rotation3(null, null, new Vector3f(0.0F, 0.0F, 0.0F), null);
		} else {
			Rotation3 rotation33 = ((Rotation3)INVERSED_DIRECTION_ROTATIONS.get(direction))
				.multiply(rotation32)
				.multiply((Rotation3)DIRECTION_ROTATIONS.get(direction2));
			return setupUvLock(rotation33);
		}
	}
}
