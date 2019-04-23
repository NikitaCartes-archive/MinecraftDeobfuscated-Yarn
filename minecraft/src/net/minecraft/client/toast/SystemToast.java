package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
	private final SystemToast.Type field_2213;
	private String field_2215;
	private String field_2217;
	private long startTime;
	private boolean justUpdated;

	public SystemToast(SystemToast.Type type, Component component, @Nullable Component component2) {
		this.field_2213 = type;
		this.field_2215 = component.getString();
		this.field_2217 = component2 == null ? null : component2.getString();
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
		if (this.field_2217 == null) {
			toastManager.getGame().textRenderer.draw(this.field_2215, 18.0F, 12.0F, -256);
		} else {
			toastManager.getGame().textRenderer.draw(this.field_2215, 18.0F, 7.0F, -256);
			toastManager.getGame().textRenderer.draw(this.field_2217, 18.0F, 18.0F, -1);
		}

		return l - this.startTime < 5000L ? Toast.Visibility.field_2210 : Toast.Visibility.field_2209;
	}

	public void setContent(Component component, @Nullable Component component2) {
		this.field_2215 = component.getString();
		this.field_2217 = component2 == null ? null : component2.getString();
		this.justUpdated = true;
	}

	public SystemToast.Type method_1989() {
		return this.field_2213;
	}

	public static void show(ToastManager toastManager, SystemToast.Type type, Component component, @Nullable Component component2) {
		SystemToast systemToast = toastManager.getToast(SystemToast.class, type);
		if (systemToast == null) {
			toastManager.add(new SystemToast(type, component, component2));
		} else {
			systemToast.setContent(component, component2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		field_2218,
		field_2219,
		field_2220;
	}
}
