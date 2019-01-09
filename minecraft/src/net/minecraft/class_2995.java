package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_2995 extends class_269 {
	private final MinecraftServer field_13428;
	private final Set<class_266> field_13427 = Sets.<class_266>newHashSet();
	private Runnable[] field_13426 = new Runnable[0];

	public class_2995(MinecraftServer minecraftServer) {
		this.field_13428 = minecraftServer;
	}

	@Override
	public void method_1176(class_267 arg) {
		super.method_1176(arg);
		if (this.field_13427.contains(arg.method_1127())) {
			this.field_13428
				.method_3760()
				.method_14581(new class_2757(class_2995.class_2996.field_13431, arg.method_1127().method_1113(), arg.method_1129(), arg.method_1126()));
		}

		this.method_12941();
	}

	@Override
	public void method_1152(String string) {
		super.method_1152(string);
		this.field_13428.method_3760().method_14581(new class_2757(class_2995.class_2996.field_13430, null, string, 0));
		this.method_12941();
	}

	@Override
	public void method_1190(String string, class_266 arg) {
		super.method_1190(string, arg);
		if (this.field_13427.contains(arg)) {
			this.field_13428.method_3760().method_14581(new class_2757(class_2995.class_2996.field_13430, arg.method_1113(), string, 0));
		}

		this.method_12941();
	}

	@Override
	public void method_1158(int i, @Nullable class_266 arg) {
		class_266 lv = this.method_1189(i);
		super.method_1158(i, arg);
		if (lv != arg && lv != null) {
			if (this.method_12936(lv) > 0) {
				this.field_13428.method_3760().method_14581(new class_2736(i, arg));
			} else {
				this.method_12938(lv);
			}
		}

		if (arg != null) {
			if (this.field_13427.contains(arg)) {
				this.field_13428.method_3760().method_14581(new class_2736(i, arg));
			} else {
				this.method_12939(arg);
			}
		}

		this.method_12941();
	}

	@Override
	public boolean method_1172(String string, class_268 arg) {
		if (super.method_1172(string, arg)) {
			this.field_13428.method_3760().method_14581(new class_2755(arg, Arrays.asList(string), 3));
			this.method_12941();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_1157(String string, class_268 arg) {
		super.method_1157(string, arg);
		this.field_13428.method_3760().method_14581(new class_2755(arg, Arrays.asList(string), 4));
		this.method_12941();
	}

	@Override
	public void method_1185(class_266 arg) {
		super.method_1185(arg);
		this.method_12941();
	}

	@Override
	public void method_1175(class_266 arg) {
		super.method_1175(arg);
		if (this.field_13427.contains(arg)) {
			this.field_13428.method_3760().method_14581(new class_2751(arg, 2));
		}

		this.method_12941();
	}

	@Override
	public void method_1173(class_266 arg) {
		super.method_1173(arg);
		if (this.field_13427.contains(arg)) {
			this.method_12938(arg);
		}

		this.method_12941();
	}

	@Override
	public void method_1160(class_268 arg) {
		super.method_1160(arg);
		this.field_13428.method_3760().method_14581(new class_2755(arg, 0));
		this.method_12941();
	}

	@Override
	public void method_1154(class_268 arg) {
		super.method_1154(arg);
		this.field_13428.method_3760().method_14581(new class_2755(arg, 2));
		this.method_12941();
	}

	@Override
	public void method_1193(class_268 arg) {
		super.method_1193(arg);
		this.field_13428.method_3760().method_14581(new class_2755(arg, 1));
		this.method_12941();
	}

	public void method_12935(Runnable runnable) {
		this.field_13426 = (Runnable[])Arrays.copyOf(this.field_13426, this.field_13426.length + 1);
		this.field_13426[this.field_13426.length - 1] = runnable;
	}

	protected void method_12941() {
		for (Runnable runnable : this.field_13426) {
			runnable.run();
		}
	}

	public List<class_2596<?>> method_12937(class_266 arg) {
		List<class_2596<?>> list = Lists.<class_2596<?>>newArrayList();
		list.add(new class_2751(arg, 0));

		for (int i = 0; i < 19; i++) {
			if (this.method_1189(i) == arg) {
				list.add(new class_2736(i, arg));
			}
		}

		for (class_267 lv : this.method_1184(arg)) {
			list.add(new class_2757(class_2995.class_2996.field_13431, lv.method_1127().method_1113(), lv.method_1129(), lv.method_1126()));
		}

		return list;
	}

	public void method_12939(class_266 arg) {
		List<class_2596<?>> list = this.method_12937(arg);

		for (class_3222 lv : this.field_13428.method_3760().method_14571()) {
			for (class_2596<?> lv2 : list) {
				lv.field_13987.method_14364(lv2);
			}
		}

		this.field_13427.add(arg);
	}

	public List<class_2596<?>> method_12940(class_266 arg) {
		List<class_2596<?>> list = Lists.<class_2596<?>>newArrayList();
		list.add(new class_2751(arg, 1));

		for (int i = 0; i < 19; i++) {
			if (this.method_1189(i) == arg) {
				list.add(new class_2736(i, arg));
			}
		}

		return list;
	}

	public void method_12938(class_266 arg) {
		List<class_2596<?>> list = this.method_12940(arg);

		for (class_3222 lv : this.field_13428.method_3760().method_14571()) {
			for (class_2596<?> lv2 : list) {
				lv.field_13987.method_14364(lv2);
			}
		}

		this.field_13427.remove(arg);
	}

	public int method_12936(class_266 arg) {
		int i = 0;

		for (int j = 0; j < 19; j++) {
			if (this.method_1189(j) == arg) {
				i++;
			}
		}

		return i;
	}

	public static enum class_2996 {
		field_13431,
		field_13430;
	}
}
