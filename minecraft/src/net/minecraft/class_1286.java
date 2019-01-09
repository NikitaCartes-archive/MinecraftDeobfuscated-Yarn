package net.minecraft;

public class class_1286 extends class_1282 {
	protected class_1286() {
		super("netherBed");
		this.method_5516();
		this.method_5518();
	}

	@Override
	public class_2561 method_5506(class_1309 arg) {
		class_2561 lv = class_2564.method_10885(new class_2588("death.attack.netherBed.link"))
			.method_10859(
				argx -> argx.method_10958(new class_2558(class_2558.class_2559.field_11749, "https://bugs.mojang.com/browse/MCPE-28723"))
						.method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2585("MCPE-28723")))
			);
		return new class_2588("death.attack.netherBed.message", arg.method_5476(), lv);
	}
}
