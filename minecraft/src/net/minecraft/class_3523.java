package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3523<C extends class_3531> {
	public static final class_2680 field_15685 = class_2246.field_10124.method_9564();
	public static final class_2680 field_15696 = class_2246.field_10566.method_9564();
	public static final class_2680 field_15703 = class_2246.field_10219.method_9564();
	public static final class_2680 field_15669 = class_2246.field_10520.method_9564();
	public static final class_2680 field_15679 = class_2246.field_10255.method_9564();
	public static final class_2680 field_15706 = class_2246.field_10340.method_9564();
	public static final class_2680 field_15668 = class_2246.field_10253.method_9564();
	public static final class_2680 field_15682 = class_2246.field_10102.method_9564();
	public static final class_2680 field_15704 = class_2246.field_10534.method_9564();
	public static final class_2680 field_15675 = class_2246.field_10611.method_9564();
	public static final class_2680 field_15686 = class_2246.field_10402.method_9564();
	public static final class_2680 field_15700 = class_2246.field_10515.method_9564();
	public static final class_2680 field_15667 = class_2246.field_10471.method_9564();
	public static final class_3527 field_15676 = new class_3527(field_15685, field_15685, field_15685);
	public static final class_3527 field_15691 = new class_3527(field_15669, field_15696, field_15679);
	public static final class_3527 field_15673 = new class_3527(field_15679, field_15679, field_15679);
	public static final class_3527 field_15677 = new class_3527(field_15703, field_15696, field_15679);
	public static final class_3527 field_15695 = new class_3527(field_15696, field_15696, field_15679);
	public static final class_3527 field_15670 = new class_3527(field_15706, field_15706, field_15679);
	public static final class_3527 field_15678 = new class_3527(field_15668, field_15696, field_15679);
	public static final class_3527 field_15694 = new class_3527(field_15682, field_15682, field_15679);
	public static final class_3527 field_15697 = new class_3527(field_15703, field_15696, field_15682);
	public static final class_3527 field_15687 = new class_3527(field_15682, field_15682, field_15682);
	public static final class_3527 field_15672 = new class_3527(field_15704, field_15675, field_15679);
	public static final class_3527 field_15705 = new class_3527(field_15686, field_15696, field_15679);
	public static final class_3527 field_15690 = new class_3527(field_15700, field_15700, field_15700);
	public static final class_3527 field_15671 = new class_3527(field_15667, field_15667, field_15667);
	public static final class_3523<class_3527> field_15701 = method_15307("default", new class_3510(class_3527::method_15331));
	public static final class_3523<class_3527> field_15692 = method_15307("mountain", new class_3514(class_3527::method_15331));
	public static final class_3523<class_3527> field_15680 = method_15307("shattered_savanna", new class_3524(class_3527::method_15331));
	public static final class_3523<class_3527> field_15702 = method_15307("gravelly_mountain", new class_3516(class_3527::method_15331));
	public static final class_3523<class_3527> field_15688 = method_15307("giant_tree_taiga", new class_3511(class_3527::method_15331));
	public static final class_3523<class_3527> field_15681 = method_15307("swamp", new class_3529(class_3527::method_15331));
	public static final class_3523<class_3527> field_15698 = method_15307("badlands", new class_3506(class_3527::method_15331));
	public static final class_3523<class_3527> field_15689 = method_15307("wooded_badlands", new class_3535(class_3527::method_15331));
	public static final class_3523<class_3527> field_15684 = method_15307("eroded_badlands", new class_3507(class_3527::method_15331));
	public static final class_3523<class_3527> field_15699 = method_15307("frozen_ocean", new class_3512(class_3527::method_15331));
	public static final class_3523<class_3527> field_15693 = method_15307("nether", new class_3520(class_3527::method_15331));
	public static final class_3523<class_3527> field_15683 = method_15307("nope", new class_3519(class_3527::method_15331));
	private final Function<Dynamic<?>, ? extends C> field_15674;

	private static <C extends class_3531, F extends class_3523<C>> F method_15307(String string, F arg) {
		return class_2378.method_10226(class_2378.field_11147, string, arg);
	}

	public class_3523(Function<Dynamic<?>, ? extends C> function) {
		this.field_15674 = function;
	}

	public abstract void method_15305(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, C arg5
	);

	public void method_15306(long l) {
	}
}
