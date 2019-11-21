package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
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
		manager.blit(0, 0, 0, 64, 160, 32);
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

	public static void show(ToastManager toastManager, SystemToast.Type type, Text title, @Nullable Text description) {
		SystemToast systemToast = toastManager.getToast(SystemToast.class, type);
		if (systemToast == null) {
			toastManager.add(new SystemToast(type, title, description));
		} else {
			systemToast.setContent(title, description);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		TUTORIAL_HINT,
		NARRATOR_TOGGLE,
		WORLD_BACKUP;
	}
}
