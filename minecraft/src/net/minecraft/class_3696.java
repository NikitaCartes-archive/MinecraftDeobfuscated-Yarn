package net.minecraft;

import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3696 {
	@Environment(EnvType.CLIENT)
	List<class_3534> method_16067(String string);

	boolean method_16069(File file);

	long method_16068();

	int method_16072();

	long method_16073();

	int method_16070();

	default long method_16071() {
		return this.method_16073() - this.method_16068();
	}

	default int method_16074() {
		return this.method_16070() - this.method_16072();
	}

	String method_18052();
}
