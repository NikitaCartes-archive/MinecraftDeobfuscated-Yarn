package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2625 extends class_2586 {
	public final class_2561[] field_12050 = new class_2561[]{new class_2585(""), new class_2585(""), new class_2585(""), new class_2585("")};
	@Environment(EnvType.CLIENT)
	private boolean field_16502;
	private int field_16501 = -1;
	private int field_16500 = -1;
	private int field_16499 = -1;
	private boolean field_12048 = true;
	private class_1657 field_12046;
	private final String[] field_12049 = new String[4];
	private class_1767 field_16419 = class_1767.field_7963;

	public class_2625() {
		super(class_2591.field_11911);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);

		for (int i = 0; i < 4; i++) {
			String string = class_2561.class_2562.method_10867(this.field_12050[i]);
			arg.method_10582("Text" + (i + 1), string);
		}

		arg.method_10582("Color", this.field_16419.method_7792());
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		this.field_12048 = false;
		super.method_11014(arg);
		this.field_16419 = class_1767.method_7793(arg.method_10558("Color"), class_1767.field_7963);

		for (int i = 0; i < 4; i++) {
			String string = arg.method_10558("Text" + (i + 1));
			class_2561 lv = class_2561.class_2562.method_10877(string.isEmpty() ? "\"\"" : string);
			if (this.field_11863 instanceof class_3218) {
				try {
					this.field_12050[i] = class_2564.method_10881(this.method_11304(null), lv, null, 0);
				} catch (CommandSyntaxException var6) {
					this.field_12050[i] = lv;
				}
			} else {
				this.field_12050[i] = lv;
			}

			this.field_12049[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11302(int i) {
		return this.field_12050[i];
	}

	public void method_11299(int i, class_2561 arg) {
		this.field_12050[i] = arg;
		this.field_12049[i] = null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11300(int i, Function<class_2561, String> function) {
		if (this.field_12049[i] == null && this.field_12050[i] != null) {
			this.field_12049[i] = (String)function.apply(this.field_12050[i]);
		}

		return this.field_12049[i];
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 9, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	@Override
	public boolean method_11011() {
		return true;
	}

	public boolean method_11307() {
		return this.field_12048;
	}

	@Environment(EnvType.CLIENT)
	public void method_11303(boolean bl) {
		this.field_12048 = bl;
		if (!bl) {
			this.field_12046 = null;
		}
	}

	public void method_11306(class_1657 arg) {
		this.field_12046 = arg;
	}

	public class_1657 method_11305() {
		return this.field_12046;
	}

	public boolean method_11301(class_1657 arg) {
		for (class_2561 lv : this.field_12050) {
			class_2583 lv2 = lv == null ? null : lv.method_10866();
			if (lv2 != null && lv2.method_10970() != null) {
				class_2558 lv3 = lv2.method_10970();
				if (lv3.method_10845() == class_2558.class_2559.field_11750) {
					arg.method_5682().method_3734().method_9249(this.method_11304((class_3222)arg), lv3.method_10844());
				}
			}
		}

		return true;
	}

	public class_2168 method_11304(@Nullable class_3222 arg) {
		String string = arg == null ? "Sign" : arg.method_5477().getString();
		class_2561 lv = (class_2561)(arg == null ? new class_2585("Sign") : arg.method_5476());
		return new class_2168(
			class_2165.field_17395,
			new class_243((double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5),
			class_241.field_1340,
			(class_3218)this.field_11863,
			2,
			string,
			lv,
			this.field_11863.method_8503(),
			arg
		);
	}

	public class_1767 method_16126() {
		return this.field_16419;
	}

	public boolean method_16127(class_1767 arg) {
		if (arg != this.method_16126()) {
			this.field_16419 = arg;
			this.method_5431();
			this.field_11863.method_8413(this.method_11016(), this.method_11010(), this.method_11010(), 3);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_16332(int i, int j, int k, boolean bl) {
		this.field_16501 = i;
		this.field_16500 = j;
		this.field_16499 = k;
		this.field_16502 = bl;
	}

	@Environment(EnvType.CLIENT)
	public void method_16335() {
		this.field_16501 = -1;
		this.field_16500 = -1;
		this.field_16499 = -1;
		this.field_16502 = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_16331() {
		return this.field_16502;
	}

	@Environment(EnvType.CLIENT)
	public int method_16334() {
		return this.field_16501;
	}

	@Environment(EnvType.CLIENT)
	public int method_16336() {
		return this.field_16500;
	}

	@Environment(EnvType.CLIENT)
	public int method_16333() {
		return this.field_16499;
	}
}
