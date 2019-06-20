package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1130 extends class_3324 {
	private class_2487 field_5514;

	public class_1130(class_1132 arg) {
		super(arg, 8);
		this.method_14608(10);
	}

	@Override
	protected void method_14577(class_3222 arg) {
		if (arg.method_5477().getString().equals(this.method_4811().method_3811())) {
			this.field_5514 = arg.method_5647(new class_2487());
		}

		super.method_14577(arg);
	}

	@Override
	public class_2561 method_14586(SocketAddress socketAddress, GameProfile gameProfile) {
		return (class_2561)(gameProfile.getName().equalsIgnoreCase(this.method_4811().method_3811()) && this.method_14566(gameProfile.getName()) != null
			? new class_2588("multiplayer.disconnect.name_taken")
			: super.method_14586(socketAddress, gameProfile));
	}

	public class_1132 method_4811() {
		return (class_1132)super.method_14561();
	}

	@Override
	public class_2487 method_14567() {
		return this.field_5514;
	}
}
