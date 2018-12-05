package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public final class class_3682 implements AutoCloseable {
	private final MinecraftClient field_16256;
	private final class_323 field_16255;

	public class_3682(MinecraftClient minecraftClient) {
		this.field_16256 = minecraftClient;
		this.field_16255 = new class_323(this::method_16039);
	}

	public Monitor method_16039(long l) {
		return new Monitor(this.field_16255, l);
	}

	public Window method_16038(WindowSettings windowSettings, String string, String string2) {
		return new Window(this.field_16256, this.field_16255, windowSettings, string, string2);
	}

	public void close() {
		this.field_16255.method_15992();
	}
}
