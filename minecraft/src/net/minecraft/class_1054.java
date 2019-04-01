package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1054 extends RuntimeException {
	public class_1054(class_1058 arg) {
		super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", arg.method_4598(), arg.method_4578(), arg.method_4595()));
	}
}
