package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2901 implements class_2596<class_2896> {
	private GameProfile field_13190;

	public class_2901() {
	}

	public class_2901(GameProfile gameProfile) {
		this.field_13190 = gameProfile;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		String string = arg.method_10800(36);
		String string2 = arg.method_10800(16);
		UUID uUID = UUID.fromString(string);
		this.field_13190 = new GameProfile(uUID, string2);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		UUID uUID = this.field_13190.getId();
		arg.method_10814(uUID == null ? "" : uUID.toString());
		arg.method_10814(this.field_13190.getName());
	}

	public void method_12594(class_2896 arg) {
		arg.method_12588(this);
	}

	@Environment(EnvType.CLIENT)
	public GameProfile method_12593() {
		return this.field_13190;
	}
}
