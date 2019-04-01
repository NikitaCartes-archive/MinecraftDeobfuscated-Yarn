package net.minecraft.realms;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class RealmsMth {
	public static float sin(float f) {
		return class_3532.method_15374(f);
	}

	public static double nextDouble(Random random, double d, double e) {
		return class_3532.method_15366(random, d, e);
	}

	public static int ceil(float f) {
		return class_3532.method_15386(f);
	}

	public static int floor(double d) {
		return class_3532.method_15357(d);
	}

	public static int intFloorDiv(int i, int j) {
		return class_3532.method_15346(i, j);
	}

	public static float abs(float f) {
		return class_3532.method_15379(f);
	}

	public static int clamp(int i, int j, int k) {
		return class_3532.method_15340(i, j, k);
	}

	public static double clampedLerp(double d, double e, double f) {
		return class_3532.method_15390(d, e, f);
	}

	public static int ceil(double d) {
		return class_3532.method_15384(d);
	}

	public static boolean isEmpty(String string) {
		return StringUtils.isEmpty(string);
	}

	public static long lfloor(double d) {
		return class_3532.method_15372(d);
	}

	public static float sqrt(double d) {
		return class_3532.method_15368(d);
	}

	public static double clamp(double d, double e, double f) {
		return class_3532.method_15350(d, e, f);
	}

	public static int getInt(String string, int i) {
		return class_3532.method_15343(string, i);
	}

	public static double getDouble(String string, double d) {
		return class_3532.method_15361(string, d);
	}

	public static int log2(int i) {
		return class_3532.method_15351(i);
	}

	public static int absFloor(double d) {
		return class_3532.method_15380(d);
	}

	public static int smallestEncompassingPowerOfTwo(int i) {
		return class_3532.method_15339(i);
	}

	public static float sqrt(float f) {
		return class_3532.method_15355(f);
	}

	public static float cos(float f) {
		return class_3532.method_15362(f);
	}

	public static int getInt(String string, int i, int j) {
		return class_3532.method_15364(string, i, j);
	}

	public static int fastFloor(double d) {
		return class_3532.method_15365(d);
	}

	public static double absMax(double d, double e) {
		return class_3532.method_15391(d, e);
	}

	public static float nextFloat(Random random, float f, float g) {
		return class_3532.method_15344(random, f, g);
	}

	public static double wrapDegrees(double d) {
		return class_3532.method_15338(d);
	}

	public static float wrapDegrees(float f) {
		return class_3532.method_15393(f);
	}

	public static float clamp(float f, float g, float h) {
		return class_3532.method_15363(f, g, h);
	}

	public static double getDouble(String string, double d, double e) {
		return class_3532.method_15358(string, d, e);
	}

	public static int roundUp(int i, int j) {
		return class_3532.method_15359(i, j);
	}

	public static double average(long[] ls) {
		return class_3532.method_15373(ls);
	}

	public static int floor(float f) {
		return class_3532.method_15375(f);
	}

	public static int abs(int i) {
		return class_3532.method_15382(i);
	}

	public static int nextInt(Random random, int i, int j) {
		return class_3532.method_15395(random, i, j);
	}
}
