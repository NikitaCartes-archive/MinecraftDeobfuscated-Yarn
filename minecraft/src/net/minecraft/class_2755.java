package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2755 implements class_2596<class_2602> {
	private String field_12600 = "";
	private class_2561 field_12603 = new class_2585("");
	private class_2561 field_12601 = new class_2585("");
	private class_2561 field_12597 = new class_2585("");
	private String field_12604 = class_270.class_272.field_1442.field_1445;
	private String field_12605 = class_270.class_271.field_1437.field_1436;
	private class_124 field_12598 = class_124.field_1070;
	private final Collection<String> field_12602 = Lists.<String>newArrayList();
	private int field_12599;
	private int field_12606;

	public class_2755() {
	}

	public class_2755(class_268 arg, int i) {
		this.field_12600 = arg.method_1197();
		this.field_12599 = i;
		if (i == 0 || i == 2) {
			this.field_12603 = arg.method_1140();
			this.field_12606 = arg.method_1147();
			this.field_12604 = arg.method_1201().field_1445;
			this.field_12605 = arg.method_1203().field_1436;
			this.field_12598 = arg.method_1202();
			this.field_12601 = arg.method_1144();
			this.field_12597 = arg.method_1136();
		}

		if (i == 0) {
			this.field_12602.addAll(arg.method_1204());
		}
	}

	public class_2755(class_268 arg, Collection<String> collection, int i) {
		if (i != 3 && i != 4) {
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
		} else if (collection != null && !collection.isEmpty()) {
			this.field_12599 = i;
			this.field_12600 = arg.method_1197();
			this.field_12602.addAll(collection);
		} else {
			throw new IllegalArgumentException("Players cannot be null/empty");
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12600 = arg.method_10800(16);
		this.field_12599 = arg.readByte();
		if (this.field_12599 == 0 || this.field_12599 == 2) {
			this.field_12603 = arg.method_10808();
			this.field_12606 = arg.readByte();
			this.field_12604 = arg.method_10800(40);
			this.field_12605 = arg.method_10800(40);
			this.field_12598 = arg.method_10818(class_124.class);
			this.field_12601 = arg.method_10808();
			this.field_12597 = arg.method_10808();
		}

		if (this.field_12599 == 0 || this.field_12599 == 3 || this.field_12599 == 4) {
			int i = arg.method_10816();

			for (int j = 0; j < i; j++) {
				this.field_12602.add(arg.method_10800(40));
			}
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12600);
		arg.writeByte(this.field_12599);
		if (this.field_12599 == 0 || this.field_12599 == 2) {
			arg.method_10805(this.field_12603);
			arg.writeByte(this.field_12606);
			arg.method_10814(this.field_12604);
			arg.method_10814(this.field_12605);
			arg.method_10817(this.field_12598);
			arg.method_10805(this.field_12601);
			arg.method_10805(this.field_12597);
		}

		if (this.field_12599 == 0 || this.field_12599 == 3 || this.field_12599 == 4) {
			arg.method_10804(this.field_12602.size());

			for (String string : this.field_12602) {
				arg.method_10814(string);
			}
		}
	}

	public void method_11860(class_2602 arg) {
		arg.method_11099(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_11855() {
		return this.field_12600;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11859() {
		return this.field_12603;
	}

	@Environment(EnvType.CLIENT)
	public Collection<String> method_11857() {
		return this.field_12602;
	}

	@Environment(EnvType.CLIENT)
	public int method_11853() {
		return this.field_12599;
	}

	@Environment(EnvType.CLIENT)
	public int method_11852() {
		return this.field_12606;
	}

	@Environment(EnvType.CLIENT)
	public class_124 method_11858() {
		return this.field_12598;
	}

	@Environment(EnvType.CLIENT)
	public String method_11851() {
		return this.field_12604;
	}

	@Environment(EnvType.CLIENT)
	public String method_11861() {
		return this.field_12605;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11856() {
		return this.field_12601;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11854() {
		return this.field_12597;
	}
}
