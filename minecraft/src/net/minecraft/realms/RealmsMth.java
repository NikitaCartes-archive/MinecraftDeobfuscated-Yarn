package net.minecraft.realms;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class RealmsMth {
	public static float sin(float f) {
		return MathHelper.sin(f);
	}

	public static double nextDouble(Random random, double d, double e) {
		return MathHelper.nextDouble(random, d, e);
	}

	public static int ceil(float f) {
		return MathHelper.ceil(f);
	}

	public static int floor(double d) {
		return MathHelper.floor(d);
	}

	public static int intFloorDiv(int i, int j) {
		return MathHelper.floorDiv(i, j);
	}

	public static float abs(float f) {
		return MathHelper.abs(f);
	}

	public static int clamp(int i, int j, int k) {
		return MathHelper.clamp(i, j, k);
	}

	public static double clampedLerp(double d, double e, double f) {
		return MathHelper.lerpClamped(d, e, f);
	}

	public static int ceil(double d) {
		return MathHelper.ceil(d);
	}

	public static boolean isEmpty(String string) {
		return StringUtils.isEmpty(string);
	}

	public static long lfloor(double d) {
		return MathHelper.lfloor(d);
	}

	public static float sqrt(double d) {
		return MathHelper.sqrt(d);
	}

	public static double clamp(double d, double e, double f) {
		return MathHelper.clamp(d, e, f);
	}

	public static int getInt(String string, int i) {
		return MathHelper.parseInt(string, i);
	}

	public static double getDouble(String string, double d) {
		return MathHelper.parseDouble(string, d);
	}

	public static int log2(int i) {
		return MathHelper.log2(i);
	}

	public static int absFloor(double d) {
		return MathHelper.absFloor(d);
	}

	public static int smallestEncompassingPowerOfTwo(int i) {
		return MathHelper.smallestEncompassingPowerOfTwo(i);
	}

	public static float sqrt(float f) {
		return MathHelper.sqrt(f);
	}

	public static float cos(float f) {
		return MathHelper.cos(f);
	}

	public static int getInt(String string, int i, int j) {
		return MathHelper.parseInt(string, i, j);
	}

	public static int fastFloor(double d) {
		return MathHelper.fastFloor(d);
	}

	public static double absMax(double d, double e) {
		return MathHelper.absMax(d, e);
	}

	public static float nextFloat(Random random, float f, float g) {
		return MathHelper.nextFloat(random, f, g);
	}

	public static double wrapDegrees(double d) {
		return MathHelper.wrapDegrees(d);
	}

	public static float wrapDegrees(float f) {
		return MathHelper.wrapDegrees(f);
	}

	public static float clamp(float f, float g, float h) {
		return MathHelper.clamp(f, g, h);
	}

	public static double getDouble(String string, double d, double e) {
		return MathHelper.parseDouble(string, d, e);
	}

	public static int roundUp(int i, int j) {
		return MathHelper.roundUp(i, j);
	}

	public static double average(long[] ls) {
		return MathHelper.average(ls);
	}

	public static int floor(float f) {
		return MathHelper.floor(f);
	}

	public static int abs(int i) {
		return MathHelper.abs(i);
	}

	public static int nextInt(Random random, int i, int j) {
		return MathHelper.nextInt(random, i, j);
	}
}
