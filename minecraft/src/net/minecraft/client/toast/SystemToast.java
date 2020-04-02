package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
	private final SystemToast.Type type;
	private String title;
	private String description;
	private long startTime;
	private boolean justUpdated;

	public SystemToast(SystemToast.Type type, Text title, @Nullable Text description) {
		this.type = type;
		this.title = title.getString();
		this.description = description == null ? null : description.getString();
	}

	@Override
	public Toast.Visibility draw(ToastManager manager, long currentTime) {
		if (this.justUpdated) {
			this.startTime = currentTime;
			this.justUpdated = false;
		}

		manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		manager.drawTexture(0, 0, 0, 64, 160, 32);
		if (this.description == null) {
			manager.getGame().textRenderer.draw(this.title, 18.0F, 12.0F, -256);
		} else {
			manager.getGame().textRenderer.draw(this.title, 18.0F, 7.0F, -256);
			manager.getGame().textRenderer.draw(this.description, 18.0F, 18.0F, -1);
		}

		return currentTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	public void setContent(Text title, @Nullable Text description) {
		this.title = title.getString();
		this.description = description == null ? null : description.getString();
		this.justUpdated = true;
	}

	public SystemToast.Type getType() {
		return this.type;
	}

	public static void method_27024(ToastManager toastManager, SystemToast.Type type, Text text, @Nullable Text text2) {
		toastManager.add(new SystemToast(type, text, text2));
	}

	public static void show(ToastManager toastManager, SystemToast.Type type, Text title, @Nullable Text description) {
		SystemToast systemToast = toastManager.getToast(SystemToast.class, type);
		if (systemToast == null) {
			method_27024(toastManager, type, title, description);
		} else {
			systemToast.setContent(title, description);
		}
	}

	public static void method_27023(MinecraftClient minecraftClient, String string) {
		method_27024(
			minecraftClient.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.access_failure"), new LiteralText(string)
		);
	}

	public static void method_27025(MinecraftClient minecraftClient, String string) {
		method_27024(
			minecraftClient.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.delete_failure"), new LiteralText(string)
		);
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		TUTORIAL_HINT,
		NARRATOR_TOGGLE,
		WORLD_BACKUP,
		PACK_LOAD_FAILURE,
		WORLD_ACCESS_FAILURE;
	}
}
