package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
	private final SystemToast.Type type;
	private class_5348 title;
	private List<class_5348> field_25037;
	private long startTime;
	private boolean justUpdated;
	private final int field_25038;

	public SystemToast(SystemToast.Type type, Text text, @Nullable Text text2) {
		this(type, text, method_29626(text2), 160);
	}

	public static SystemToast method_29047(MinecraftClient minecraftClient, SystemToast.Type type, Text text, Text text2) {
		TextRenderer textRenderer = minecraftClient.textRenderer;
		List<class_5348> list = textRenderer.getTextHandler().wrapLines(text2, 200, Style.EMPTY);
		int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
		return new SystemToast(type, text, list, i + 30);
	}

	private SystemToast(SystemToast.Type type, Text text, List<class_5348> list, int i) {
		this.type = type;
		this.title = text;
		this.field_25037 = list;
		this.field_25038 = i;
	}

	private static ImmutableList<class_5348> method_29626(@Nullable Text text) {
		return text == null ? ImmutableList.of() : ImmutableList.of(text);
	}

	@Override
	public int method_29049() {
		return this.field_25038;
	}

	@Override
	public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
		if (this.justUpdated) {
			this.startTime = startTime;
			this.justUpdated = false;
		}

		manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		int i = this.method_29049();
		int j = 12;
		if (i == 160 && this.field_25037.size() <= 1) {
			manager.drawTexture(matrices, 0, 0, 0, 64, i, this.method_29050());
		} else {
			int k = this.method_29050() + Math.max(0, this.field_25037.size() - 1) * 12;
			int l = 28;
			int m = Math.min(4, k - 28);
			this.method_29046(matrices, manager, i, 0, 0, 28);

			for (int n = 28; n < k - m; n += 10) {
				this.method_29046(matrices, manager, i, 16, n, Math.min(16, k - n - m));
			}

			this.method_29046(matrices, manager, i, 32 - m, k - m, m);
		}

		if (this.field_25037 == null) {
			manager.getGame().textRenderer.draw(matrices, this.title, 18.0F, 12.0F, -256);
		} else {
			manager.getGame().textRenderer.draw(matrices, this.title, 18.0F, 7.0F, -256);

			for (int k = 0; k < this.field_25037.size(); k++) {
				manager.getGame().textRenderer.draw(matrices, (class_5348)this.field_25037.get(k), 18.0F, (float)(18 + k * 12), -1);
			}
		}

		return startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	private void method_29046(MatrixStack matrixStack, ToastManager toastManager, int i, int j, int k, int l) {
		int m = j == 0 ? 20 : 5;
		int n = Math.min(60, i - m);
		toastManager.drawTexture(matrixStack, 0, k, 0, 64 + j, m, l);

		for (int o = m; o < i - n; o += 64) {
			toastManager.drawTexture(matrixStack, o, k, 32, 64 + j, Math.min(64, i - o - n), l);
		}

		toastManager.drawTexture(matrixStack, i - n, k, 160 - n, 64 + j, n, l);
	}

	public void setContent(Text title, @Nullable Text description) {
		this.title = title;
		this.field_25037 = method_29626(description);
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

	public static void method_29627(MinecraftClient minecraftClient, String string) {
		add(minecraftClient.getToastManager(), SystemToast.Type.PACK_COPY_FAILURE, new TranslatableText("pack.copyFailure"), new LiteralText(string));
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		TUTORIAL_HINT,
		NARRATOR_TOGGLE,
		WORLD_BACKUP,
		WORLD_GEN_SETTINGS_TRANSFER,
		PACK_LOAD_FAILURE,
		WORLD_ACCESS_FAILURE,
		PACK_COPY_FAILURE;
	}
}
