package net.minecraft;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2667 extends class_2237 {
	public static final class_2753 field_12196 = class_2671.field_10927;
	public static final class_2754<class_2764> field_12197 = class_2671.field_12224;

	public class_2667(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_12196, class_2350.field_11043).method_11657(field_12197, class_2764.field_12637));
	}

	@Nullable
	@Override
	public class_2586 method_10123(class_1922 arg) {
		return null;
	}

	public static class_2586 method_11489(class_2680 arg, class_2350 arg2, boolean bl, boolean bl2) {
		return new class_2669(arg, arg2, bl, bl2);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2669) {
				((class_2669)lv).method_11513();
			}
		}
	}

	@Override
	public void method_9585(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		class_2338 lv = arg2.method_10093(((class_2350)arg3.method_11654(field_12196)).method_10153());
		class_2680 lv2 = arg.method_8320(lv);
		if (lv2.method_11614() instanceof class_2665 && (Boolean)lv2.method_11654(class_2665.field_12191)) {
			arg.method_8650(lv, false);
		}
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (!arg2.field_9236 && arg2.method_8321(arg3) == null) {
			arg2.method_8650(arg3, false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<class_1799> method_9560(class_2680 arg, class_47.class_48 arg2) {
		class_2669 lv = this.method_11488(arg2.method_313(), arg2.method_308(class_181.field_1232));
		return lv == null ? Collections.emptyList() : lv.method_11495().method_11612(arg2);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_259.method_1073();
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_2669 lv = this.method_11488(arg2, arg3);
		return lv != null ? lv.method_11512(arg2, arg3) : class_259.method_1073();
	}

	@Nullable
	private class_2669 method_11488(class_1922 arg, class_2338 arg2) {
		class_2586 lv = arg.method_8321(arg2);
		return lv instanceof class_2669 ? (class_2669)lv : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_12196, arg2.method_10503(arg.method_11654(field_12196)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_12196)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_12196, field_12197);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
