package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
	private final SystemToast.Type type;
	private String title;
	private String description;
	private long startTime;
	private boolean justUpdated;

	public SystemToast(SystemToast.Type type, Text text, @Nullable Text text2) {
		this.type = type;
		this.title = text.getString();
		this.description = text2 == null ? null : text2.getString();
	}

	@Override
	public Toast.Visibility draw(ToastManager toastManager, long l) {
		if (this.justUpdated) {
			this.startTime = l;
			this.justUpdated = false;
		}

		toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		toastManager.blit(0, 0, 0, 64, 160, 32);
		if (this.description == null) {
			toastManager.getGame().textRenderer.draw(this.title, 18.0F, 12.0F, -256);
		} else {
			toastManager.getGame().textRenderer.draw(this.title, 18.0F, 7.0F, -256);
			toastManager.getGame().textRenderer.draw(this.description, 18.0F, 18.0F, -1);
		}

		return l - this.startTime < 5000L ? Toast.Visibility.field_2210 : Toast.Visibility.field_2209;
	}

	public void setContent(Text text, @Nullable Text text2) {
		this.title = text.getString();
		this.description = text2 == null ? null : text2.getString();
		this.justUpdated = true;
	}

	public SystemToast.Type method_1989() {
		return this.type;
	}

	public static void show(ToastManager toastManager, SystemToast.Type type, Text text, @Nullable Text text2) {
		SystemToast systemToast = toastManager.getToast(SystemToast.class, type);
		if (systemToast == null) {
			toastManager.add(new SystemToast(type, text, text2));
		} else {
			systemToast.setContent(text, text2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		field_2218,
		field_2219,
		field_2220;
	}
}
