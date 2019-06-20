package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_34 implements Comparable<class_34> {
	private final String field_205;
	private final String field_212;
	private final long field_211;
	private final long field_210;
	private final boolean field_209;
	private final class_1934 field_213;
	private final boolean field_207;
	private final boolean field_206;
	private final String field_214;
	private final int field_215;
	private final boolean field_216;
	private final class_1942 field_208;

	public class_34(class_31 arg, String string, String string2, long l, boolean bl) {
		this.field_205 = string;
		this.field_212 = string2;
		this.field_211 = arg.method_191();
		this.field_210 = l;
		this.field_213 = arg.method_210();
		this.field_209 = bl;
		this.field_207 = arg.method_152();
		this.field_206 = arg.method_194();
		this.field_214 = arg.method_219();
		this.field_215 = arg.method_180();
		this.field_216 = arg.method_171();
		this.field_208 = arg.method_153();
	}

	public String method_248() {
		return this.field_205;
	}

	public String method_252() {
		return this.field_212;
	}

	public long method_250() {
		return this.field_210;
	}

	public boolean method_255() {
		return this.field_209;
	}

	public long method_249() {
		return this.field_211;
	}

	public int method_251(class_34 arg) {
		if (this.field_211 < arg.field_211) {
			return 1;
		} else {
			return this.field_211 > arg.field_211 ? -1 : this.field_205.compareTo(arg.field_205);
		}
	}

	public class_1934 method_247() {
		return this.field_213;
	}

	public boolean method_257() {
		return this.field_207;
	}

	public boolean method_259() {
		return this.field_206;
	}

	public class_2561 method_258() {
		return (class_2561)(class_3544.method_15438(this.field_214) ? new class_2588("selectWorld.versionUnknown") : new class_2585(this.field_214));
	}

	public boolean method_256() {
		return this.method_260() || !class_155.method_16673().isStable() && !this.field_216 || this.method_254() || this.method_253();
	}

	public boolean method_260() {
		return this.field_215 > class_155.method_16673().getWorldVersion();
	}

	public boolean method_253() {
		return this.field_208 == class_1942.field_9278 && this.field_215 < 1466;
	}

	public boolean method_254() {
		return this.field_215 < class_155.method_16673().getWorldVersion();
	}
}
