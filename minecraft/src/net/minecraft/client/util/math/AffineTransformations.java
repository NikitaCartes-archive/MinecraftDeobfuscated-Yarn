package net.minecraft.client.util.math;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class AffineTransformations {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EnumMap<Direction, AffineTransformation> DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.field_11035, AffineTransformation.identity());
		enumMap.put(Direction.field_11034, new AffineTransformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), null, null));
		enumMap.put(Direction.field_11039, new AffineTransformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.field_11043, new AffineTransformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), null, null));
		enumMap.put(Direction.field_11036, new AffineTransformation(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.field_11033, new AffineTransformation(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), null, null));
	});
	public static final EnumMap<Direction, AffineTransformation> INVERTED_DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
		for (Direction direction : Direction.values()) {
			enumMap.put(direction, ((AffineTransformation)DIRECTION_ROTATIONS.get(direction)).invert());
		}
	});

	public static AffineTransformation setupUvLock(AffineTransformation affineTransformation) {
		Matrix4f matrix4f = Matrix4f.translate(0.5F, 0.5F, 0.5F);
		matrix4f.multiply(affineTransformation.getMatrix());
		matrix4f.multiply(Matrix4f.translate(-0.5F, -0.5F, -0.5F));
		return new AffineTransformation(matrix4f);
	}

	public static AffineTransformation uvLock(AffineTransformation affineTransformation, Direction direction, Supplier<String> supplier) {
		Direction direction2 = Direction.transform(affineTransformation.getMatrix(), direction);
		AffineTransformation affineTransformation2 = affineTransformation.invert();
		if (affineTransformation2 == null) {
			LOGGER.warn((String)supplier.get());
			return new AffineTransformation(null, null, new Vector3f(0.0F, 0.0F, 0.0F), null);
		} else {
			AffineTransformation affineTransformation3 = ((AffineTransformation)INVERTED_DIRECTION_ROTATIONS.get(direction))
				.multiply(affineTransformation2)
				.multiply((AffineTransformation)DIRECTION_ROTATIONS.get(direction2));
			return setupUvLock(affineTransformation3);
		}
	}
}
