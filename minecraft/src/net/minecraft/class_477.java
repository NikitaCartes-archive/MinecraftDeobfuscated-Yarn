package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_477 extends class_463 {
	private final class_2593 field_2865;
	private class_4185 field_2869;
	private class_4185 field_2871;
	private class_4185 field_2866;
	private class_2593.class_2594 field_2870 = class_2593.class_2594.field_11924;
	private boolean field_2868;
	private boolean field_2867;

	public class_477(class_2593 arg) {
		this.field_2865 = arg;
	}

	@Override
	class_1918 method_2351() {
		return this.field_2865.method_11040();
	}

	@Override
	int method_2364() {
		return 135;
	}

	@Override
	protected void init() {
		super.init();
		this.field_2869 = this.addButton(new class_4185(this.width / 2 - 50 - 100 - 4, 165, 100, 20, class_1074.method_4662("advMode.mode.sequence"), arg -> {
			this.method_2450();
			this.method_2451();
		}));
		this.field_2871 = this.addButton(new class_4185(this.width / 2 - 50, 165, 100, 20, class_1074.method_4662("advMode.mode.unconditional"), arg -> {
			this.field_2868 = !this.field_2868;
			this.method_2452();
		}));
		this.field_2866 = this.addButton(new class_4185(this.width / 2 + 50 + 4, 165, 100, 20, class_1074.method_4662("advMode.mode.redstoneTriggered"), arg -> {
			this.field_2867 = !this.field_2867;
			this.method_2454();
		}));
		this.field_2762.active = false;
		this.field_2760.active = false;
		this.field_2869.active = false;
		this.field_2871.active = false;
		this.field_2866.active = false;
	}

	public void method_2457() {
		class_1918 lv = this.field_2865.method_11040();
		this.field_2751.method_1852(lv.method_8289());
		this.field_2752 = lv.method_8296();
		this.field_2870 = this.field_2865.method_11039();
		this.field_2868 = this.field_2865.method_11046();
		this.field_2867 = this.field_2865.method_11042();
		this.method_2368();
		this.method_2451();
		this.method_2452();
		this.method_2454();
		this.field_2762.active = true;
		this.field_2760.active = true;
		this.field_2869.active = true;
		this.field_2871.active = true;
		this.field_2866.active = true;
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		super.resize(arg, i, j);
		this.method_2368();
		this.method_2451();
		this.method_2452();
		this.method_2454();
		this.field_2762.active = true;
		this.field_2760.active = true;
		this.field_2869.active = true;
		this.field_2871.active = true;
		this.field_2866.active = true;
	}

	@Override
	protected void method_2352(class_1918 arg) {
		this.minecraft
			.method_1562()
			.method_2883(
				new class_2870(new class_2338(arg.method_8300()), this.field_2751.method_1882(), this.field_2870, arg.method_8296(), this.field_2868, this.field_2867)
			);
	}

	private void method_2451() {
		switch (this.field_2870) {
			case field_11922:
				this.field_2869.setMessage(class_1074.method_4662("advMode.mode.sequence"));
				break;
			case field_11923:
				this.field_2869.setMessage(class_1074.method_4662("advMode.mode.auto"));
				break;
			case field_11924:
				this.field_2869.setMessage(class_1074.method_4662("advMode.mode.redstone"));
		}
	}

	private void method_2450() {
		switch (this.field_2870) {
			case field_11922:
				this.field_2870 = class_2593.class_2594.field_11923;
				break;
			case field_11923:
				this.field_2870 = class_2593.class_2594.field_11924;
				break;
			case field_11924:
				this.field_2870 = class_2593.class_2594.field_11922;
		}
	}

	private void method_2452() {
		if (this.field_2868) {
			this.field_2871.setMessage(class_1074.method_4662("advMode.mode.conditional"));
		} else {
			this.field_2871.setMessage(class_1074.method_4662("advMode.mode.unconditional"));
		}
	}

	private void method_2454() {
		if (this.field_2867) {
			this.field_2866.setMessage(class_1074.method_4662("advMode.mode.autoexec.bat"));
		} else {
			this.field_2866.setMessage(class_1074.method_4662("advMode.mode.redstoneTriggered"));
		}
	}
}
