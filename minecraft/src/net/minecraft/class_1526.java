package net.minecraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1526 {
	private static final Logger field_7066 = LogManager.getLogger();
	private final class_1510 field_7065;
	private final class_1521[] field_7064 = new class_1521[class_1527.method_6869()];
	private class_1521 field_7063;

	public class_1526(class_1510 arg) {
		this.field_7065 = arg;
		this.method_6863(class_1527.field_7075);
	}

	public void method_6863(class_1527<?> arg) {
		if (this.field_7063 == null || arg != this.field_7063.method_6849()) {
			if (this.field_7063 != null) {
				this.field_7063.method_6854();
			}

			this.field_7063 = this.method_6865((class_1527<class_1521>)arg);
			if (!this.field_7065.field_6002.field_9236) {
				this.field_7065.method_5841().method_12778(class_1510.field_7013, arg.method_6871());
			}

			field_7066.debug("Dragon is now in phase {} on the {}", arg, this.field_7065.field_6002.field_9236 ? "client" : "server");
			this.field_7063.method_6856();
		}
	}

	public class_1521 method_6864() {
		return this.field_7063;
	}

	public <T extends class_1521> T method_6865(class_1527<T> arg) {
		int i = arg.method_6871();
		if (this.field_7064[i] == null) {
			this.field_7064[i] = arg.method_6866(this.field_7065);
		}

		return (T)this.field_7064[i];
	}
}
