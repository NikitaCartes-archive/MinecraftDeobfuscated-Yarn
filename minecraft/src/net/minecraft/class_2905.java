package net.minecraft;

import java.io.IOException;
import java.security.PublicKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2905 implements class_2596<class_2896> {
	private String field_13209;
	private PublicKey field_13211;
	private byte[] field_13210;

	public class_2905() {
	}

	public class_2905(String string, PublicKey publicKey, byte[] bs) {
		this.field_13209 = string;
		this.field_13211 = publicKey;
		this.field_13210 = bs;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13209 = arg.method_10800(20);
		this.field_13211 = class_3515.method_15242(arg.method_10795());
		this.field_13210 = arg.method_10795();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_13209);
		arg.method_10813(this.field_13211.getEncoded());
		arg.method_10813(this.field_13210);
	}

	public void method_12612(class_2896 arg) {
		arg.method_12587(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_12610() {
		return this.field_13209;
	}

	@Environment(EnvType.CLIENT)
	public PublicKey method_12611() {
		return this.field_13211;
	}

	@Environment(EnvType.CLIENT)
	public byte[] method_12613() {
		return this.field_13210;
	}
}
