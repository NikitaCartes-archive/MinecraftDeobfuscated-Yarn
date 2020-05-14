package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
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
	public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
		if (this.justUpdated) {
			this.startTime = startTime;
			this.justUpdated = false;
		}

		manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		manager.drawTexture(matrices, 0, 0, 0, 64, 160, 32);
		if (this.description == null) {
			manager.getGame().textRenderer.draw(matrices, this.title, 18.0F, 12.0F, -256);
		} else {
			manager.getGame().textRenderer.draw(matrices, this.title, 18.0F, 7.0F, -256);
			manager.getGame().textRenderer.draw(matrices, this.description, 18.0F, 18.0F, -1);
		}

		return startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	public void setContent(Text title, @Nullable Text description) {
		this.title = title.getString();
		this.description = description == null ? null : description.getString();
		this.justUpdated = true;
	}

	public SystemToast.Type getType() {
		return this.type;
	}

	public static void add(ToastManager manager, SystemToast.Type type, Text title, @Nullable Text description) {
		manager.add(new SystemToast(type, title, description));
	}

	public static void show(ToastManager manager, SystemToast.Type type, Text title, @Nullable Text description) {
		SystemToast systemToast = manager.getToast(SystemToast.class, type);
		if (systemToast == null) {
			add(manager, type, title, description);
		} else {
			systemToast.setContent(title, description);
		}
	}

	public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
		add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.access_failure"), new LiteralText(worldName));
	}

	public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
		add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.delete_failure"), new LiteralText(worldName));
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
