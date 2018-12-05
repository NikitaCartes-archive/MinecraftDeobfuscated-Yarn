package net.minecraft;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class class_3807 {
	private final Path field_16846;
	private class_3806 field_16847;

	public class_3807(Path path) {
		this.field_16846 = path;
		this.field_16847 = class_3806.method_16714(path);
	}

	public class_3806 method_16717() {
		return this.field_16847;
	}

	public void method_16719() {
		this.field_16847.method_16728(this.field_16846);
	}

	public class_3807 method_16718(UnaryOperator<class_3806> unaryOperator) {
		(this.field_16847 = (class_3806)unaryOperator.apply(this.field_16847)).method_16728(this.field_16846);
		return this;
	}
}
