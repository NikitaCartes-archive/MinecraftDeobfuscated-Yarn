package net.minecraft;

import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4523 implements class_4531 {
	private static final Logger field_20576 = LogManager.getLogger();

	@Override
	public void method_22304(class_4517 arg) {
		if (arg.method_22183()) {
			field_20576.error(arg.method_22169() + " failed! " + SystemUtil.method_22321(arg.method_22182()));
		} else {
			field_20576.warn("(optional) " + arg.method_22169() + " failed. " + SystemUtil.method_22321(arg.method_22182()));
		}
	}

	@Override
	public void method_22305(class_4517 arg) {
	}
}
