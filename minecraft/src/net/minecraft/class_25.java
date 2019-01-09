package net.minecraft;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_25 extends class_29 {
	public class_25(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		super(file, string, minecraftServer, dataFixer);
	}

	@Override
	public class_2858 method_135(class_2869 arg) {
		File file = arg.method_12460().method_12488(this.method_132());
		file.mkdirs();
		return new class_2852(file, this.field_148);
	}

	@Override
	public void method_131(class_31 arg, @Nullable class_2487 arg2) {
		arg.method_142(19133);
		super.method_131(arg, arg2);
	}
}
