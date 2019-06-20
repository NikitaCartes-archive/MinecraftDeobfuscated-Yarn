package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_2821 extends class_2839 {
	private final class_2818 field_12866;

	public class_2821(class_2818 arg) {
		super(arg.method_12004(), class_2843.field_12950);
		this.field_12866 = arg;
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		return this.field_12866.method_8321(arg);
	}

	@Nullable
	@Override
	public class_2680 method_8320(class_2338 arg) {
		return this.field_12866.method_8320(arg);
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		return this.field_12866.method_8316(arg);
	}

	@Override
	public int method_8315() {
		return this.field_12866.method_8315();
	}

	@Nullable
	@Override
	public class_2680 method_12010(class_2338 arg, class_2680 arg2, boolean bl) {
		return null;
	}

	@Override
	public void method_12007(class_2338 arg, class_2586 arg2) {
	}

	@Override
	public void method_12002(class_1297 arg) {
	}

	@Override
	public void method_12308(class_2806 arg) {
	}

	@Override
	public class_2826[] method_12006() {
		return this.field_12866.method_12006();
	}

	@Nullable
	@Override
	public class_3568 method_12023() {
		return this.field_12866.method_12023();
	}

	@Override
	public void method_12037(class_2902.class_2903 arg, long[] ls) {
	}

	private class_2902.class_2903 method_12239(class_2902.class_2903 arg) {
		if (arg == class_2902.class_2903.field_13194) {
			return class_2902.class_2903.field_13202;
		} else {
			return arg == class_2902.class_2903.field_13195 ? class_2902.class_2903.field_13200 : arg;
		}
	}

	@Override
	public int method_12005(class_2902.class_2903 arg, int i, int j) {
		return this.field_12866.method_12005(this.method_12239(arg), i, j);
	}

	@Override
	public class_1923 method_12004() {
		return this.field_12866.method_12004();
	}

	@Override
	public void method_12043(long l) {
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return this.field_12866.method_12181(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return this.field_12866.method_12016();
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
	}

	@Override
	public LongSet method_12180(String string) {
		return this.field_12866.method_12180(string);
	}

	@Override
	public void method_12182(String string, long l) {
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return this.field_12866.method_12179();
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
	}

	@Override
	public class_1959[] method_12036() {
		return this.field_12866.method_12036();
	}

	@Override
	public void method_12008(boolean bl) {
	}

	@Override
	public boolean method_12044() {
		return false;
	}

	@Override
	public class_2806 method_12009() {
		return this.field_12866.method_12009();
	}

	@Override
	public void method_12041(class_2338 arg) {
	}

	@Override
	public void method_12039(class_2338 arg) {
	}

	@Override
	public void method_12042(class_2487 arg) {
	}

	@Nullable
	@Override
	public class_2487 method_12024(class_2338 arg) {
		return this.field_12866.method_12024(arg);
	}

	@Nullable
	@Override
	public class_2487 method_20598(class_2338 arg) {
		return this.field_12866.method_20598(arg);
	}

	@Override
	public void method_12022(class_1959[] args) {
	}

	@Override
	public Stream<class_2338> method_12018() {
		return this.field_12866.method_12018();
	}

	@Override
	public class_2850<class_2248> method_12303() {
		return new class_2850<>(arg -> arg.method_9564().method_11588(), this.method_12004());
	}

	@Override
	public class_2850<class_3611> method_12313() {
		return new class_2850<>(arg -> arg == class_3612.field_15906, this.method_12004());
	}

	@Override
	public BitSet method_12025(class_2893.class_2894 arg) {
		return this.field_12866.method_12025(arg);
	}

	public class_2818 method_12240() {
		return this.field_12866;
	}

	@Override
	public boolean method_12038() {
		return this.field_12866.method_12038();
	}

	@Override
	public void method_12020(boolean bl) {
		this.field_12866.method_12020(bl);
	}
}
