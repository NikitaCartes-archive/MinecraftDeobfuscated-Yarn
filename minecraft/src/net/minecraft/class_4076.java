package net.minecraft;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4076 extends class_2382 {
	private class_4076(int i, int j, int k) {
		super(i, j, k);
	}

	public static class_4076 method_18676(int i, int j, int k) {
		return new class_4076(i, j, k);
	}

	public static class_4076 method_18682(class_2338 arg) {
		return new class_4076(method_18675(arg.method_10263()), method_18675(arg.method_10264()), method_18675(arg.method_10260()));
	}

	public static class_4076 method_18681(class_1923 arg, int i) {
		return new class_4076(arg.field_9181, i, arg.field_9180);
	}

	public static class_4076 method_18680(class_1297 arg) {
		return new class_4076(
			method_18675(class_3532.method_15357(arg.field_5987)),
			method_18675(class_3532.method_15357(arg.field_6010)),
			method_18675(class_3532.method_15357(arg.field_6035))
		);
	}

	public static class_4076 method_18677(long l) {
		return new class_4076(method_18686(l), method_18689(l), method_18690(l));
	}

	public static long method_18679(long l, class_2350 arg) {
		return method_18678(l, arg.method_10148(), arg.method_10164(), arg.method_10165());
	}

	public static long method_18678(long l, int i, int j, int k) {
		return method_18685(method_18686(l) + i, method_18689(l) + j, method_18690(l) + k);
	}

	public static int method_18675(int i) {
		return i >> 4;
	}

	public static int method_18684(int i) {
		return i & 15;
	}

	public static short method_19454(class_2338 arg) {
		int i = method_18684(arg.method_10263());
		int j = method_18684(arg.method_10264());
		int k = method_18684(arg.method_10260());
		return (short)(i << 8 | k << 4 | j);
	}

	public static int method_18688(int i) {
		return i << 4;
	}

	public static int method_18686(long l) {
		return (int)(l << 0 >> 42);
	}

	public static int method_18689(long l) {
		return (int)(l << 44 >> 44);
	}

	public static int method_18690(long l) {
		return (int)(l << 22 >> 42);
	}

	public int method_18674() {
		return this.method_10263();
	}

	public int method_18683() {
		return this.method_10264();
	}

	public int method_18687() {
		return this.method_10260();
	}

	public int method_19527() {
		return this.method_18674() << 4;
	}

	public int method_19528() {
		return this.method_18683() << 4;
	}

	public int method_19529() {
		return this.method_18687() << 4;
	}

	public int method_19530() {
		return (this.method_18674() << 4) + 15;
	}

	public int method_19531() {
		return (this.method_18683() << 4) + 15;
	}

	public int method_19532() {
		return (this.method_18687() << 4) + 15;
	}

	public static long method_18691(long l) {
		return method_18685(method_18675(class_2338.method_10061(l)), method_18675(class_2338.method_10071(l)), method_18675(class_2338.method_10083(l)));
	}

	public static long method_18693(long l) {
		return l & -1048576L;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_19767() {
		return new class_2338(method_18688(this.method_18674()), method_18688(this.method_18683()), method_18688(this.method_18687()));
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_19768() {
		int i = 8;
		return this.method_19767().method_10069(8, 8, 8);
	}

	public class_1923 method_18692() {
		return new class_1923(this.method_18674(), this.method_18687());
	}

	public static long method_18685(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & 4194303L) << 42;
		l |= ((long)j & 1048575L) << 0;
		return l | ((long)k & 4194303L) << 20;
	}

	public long method_18694() {
		return method_18685(this.method_18674(), this.method_18683(), this.method_18687());
	}

	public Stream<class_2338> method_19533() {
		return class_2338.method_17962(this.method_19527(), this.method_19528(), this.method_19529(), this.method_19530(), this.method_19531(), this.method_19532());
	}
}
