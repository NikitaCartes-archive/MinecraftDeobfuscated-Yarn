package net.minecraft;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2917 implements class_2596<class_2911> {
	private byte[] field_13274 = new byte[0];
	private byte[] field_13273 = new byte[0];

	public class_2917() {
	}

	@Environment(EnvType.CLIENT)
	public class_2917(SecretKey secretKey, PublicKey publicKey, byte[] bs) {
		this.field_13274 = class_3515.method_15238(publicKey, secretKey.getEncoded());
		this.field_13273 = class_3515.method_15238(publicKey, bs);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13274 = arg.method_10795();
		this.field_13273 = arg.method_10795();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10813(this.field_13274);
		arg.method_10813(this.field_13273);
	}

	public void method_12653(class_2911 arg) {
		arg.method_12642(this);
	}

	public SecretKey method_12654(PrivateKey privateKey) {
		return class_3515.method_15234(privateKey, this.field_13274);
	}

	public byte[] method_12655(PrivateKey privateKey) {
		return privateKey == null ? this.field_13273 : class_3515.method_15243(privateKey, this.field_13273);
	}
}
