package net.minecraft;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4609 {
	private static final Logger field_21023 = LogManager.getLogger();
	public static final EnumMap<Direction, class_4590> field_21021 = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.SOUTH, class_4590.method_22931());
		enumMap.put(Direction.EAST, new class_4590(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), null, null));
		enumMap.put(Direction.WEST, new class_4590(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.NORTH, new class_4590(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), null, null));
		enumMap.put(Direction.UP, new class_4590(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), null, null));
		enumMap.put(Direction.DOWN, new class_4590(null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), null, null));
	});
	public static final EnumMap<Direction, class_4590> field_21022 = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		for (Direction direction : Direction.values()) {
			enumMap.put(direction, ((class_4590)field_21021.get(direction)).method_22935());
		}
	});

	public static class_4590 method_23220(class_4590 arg) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 3, 0.5F);
		matrix4f.set(1, 3, 0.5F);
		matrix4f.set(2, 3, 0.5F);
		matrix4f.multiply(arg.method_22936());
		Matrix4f matrix4f2 = new Matrix4f();
		matrix4f2.loadIdentity();
		matrix4f2.set(0, 3, -0.5F);
		matrix4f2.set(1, 3, -0.5F);
		matrix4f2.set(2, 3, -0.5F);
		matrix4f.multiply(matrix4f2);
		return new class_4590(matrix4f);
	}

	public static class_4590 method_23221(class_4590 arg, Direction direction, Supplier<String> supplier) {
		Direction direction2 = Direction.method_23225(arg.method_22936(), direction);
		class_4590 lv = arg.method_22935();
		if (lv == null) {
			field_21023.warn((String)supplier.get());
			return new class_4590(null, null, new Vector3f(0.0F, 0.0F, 0.0F), null);
		} else {
			class_4590 lv2 = ((class_4590)field_21022.get(direction)).method_22933(lv).method_22933((class_4590)field_21021.get(direction2));
			return method_23220(lv2);
		}
	}
}
