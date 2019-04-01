package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.io.IOException;

public class class_2915 implements class_2596<class_2911> {
	private GameProfile field_13271;

	public class_2915() {
	}

	public class_2915(GameProfile gameProfile) {
		this.field_13271 = gameProfile;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13271 = new GameProfile(null, arg.method_10800(16));
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_13271.getName());
	}

	public void method_12649(class_2911 arg) {
		arg.method_12641(this);
	}

	public GameProfile method_12650() {
		return this.field_13271;
	}
}
