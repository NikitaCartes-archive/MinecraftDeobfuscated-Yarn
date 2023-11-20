package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
	private static final Identifier TEXTURE = new Identifier("toast/system");
	private static final int MIN_WIDTH = 200;
	private static final int LINE_HEIGHT = 12;
	private static final int PADDING_Y = 10;
	private final SystemToast.Type type;
	private Text title;
	private List<OrderedText> lines;
	private long startTime;
	private boolean justUpdated;
	private final int width;
	private boolean hidden;

	public SystemToast(SystemToast.Type type, Text title, @Nullable Text description) {
		this(
			type,
			title,
			getTextAsList(description),
			Math.max(
				160,
				30
					+ Math.max(
						MinecraftClient.getInstance().textRenderer.getWidth(title), description == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth(description)
					)
			)
		);
	}

	public static SystemToast create(MinecraftClient client, SystemToast.Type type, Text title, Text description) {
		TextRenderer textRenderer = client.textRenderer;
		List<OrderedText> list = textRenderer.wrapLines(description, 200);
		int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
		return new SystemToast(type, title, list, i + 30);
	}

	private SystemToast(SystemToast.Type type, Text title, List<OrderedText> lines, int width) {
		this.type = type;
		this.title = title;
		this.lines = lines;
		this.width = width;
	}

	private static ImmutableList<OrderedText> getTextAsList(@Nullable Text text) {
		return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return 20 + Math.max(this.lines.size(), 1) * 12;
	}

	public void hide() {
		this.hidden = true;
	}

	@Override
	public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
		if (this.justUpdated) {
			this.startTime = startTime;
			this.justUpdated = false;
		}

		int i = this.getWidth();
		if (i == 160 && this.lines.size() <= 1) {
			context.drawGuiTexture(TEXTURE, 0, 0, i, this.getHeight());
		} else {
			int j = this.getHeight();
			int k = 28;
			int l = Math.min(4, j - 28);
			this.drawPart(context, i, 0, 0, 28);

			for (int m = 28; m < j - l; m += 10) {
				this.drawPart(context, i, 16, m, Math.min(16, j - m - l));
			}

			this.drawPart(context, i, 32 - l, j - l, l);
		}

		if (this.lines.isEmpty()) {
			context.drawText(manager.getClient().textRenderer, this.title, 18, 12, -256, false);
		} else {
			context.drawText(manager.getClient().textRenderer, this.title, 18, 7, -256, false);

			for (int j = 0; j < this.lines.size(); j++) {
				context.drawText(manager.getClient().textRenderer, (OrderedText)this.lines.get(j), 18, 18 + j * 12, -1, false);
			}
		}

		double d = (double)this.type.displayDuration * manager.getNotificationDisplayTimeMultiplier();
		long n = startTime - this.startTime;
		return !this.hidden && (double)n < d ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	private void drawPart(DrawContext context, int i, int j, int k, int l) {
		int m = j == 0 ? 20 : 5;
		int n = Math.min(60, i - m);
		Identifier identifier = TEXTURE;
		context.drawGuiTexture(identifier, 160, 32, 0, j, 0, k, m, l);

		for (int o = m; o < i - n; o += 64) {
			context.drawGuiTexture(identifier, 160, 32, 32, j, o, k, Math.min(64, i - o - n), l);
		}

		context.drawGuiTexture(identifier, 160, 32, 160 - n, j, i - n, k, n, l);
	}

	public void setContent(Text title, @Nullable Text description) {
		this.title = title;
		this.lines = getTextAsList(description);
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

	public static void hide(ToastManager manager, SystemToast.Type type) {
		SystemToast systemToast = manager.getToast(SystemToast.class, type);
		if (systemToast != null) {
			systemToast.hide();
		}
	}

	public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
		add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.access_failure"), Text.literal(worldName));
	}

	public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
		add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.delete_failure"), Text.literal(worldName));
	}

	public static void addPackCopyFailure(MinecraftClient client, String directory) {
		add(client.getToastManager(), SystemToast.Type.PACK_COPY_FAILURE, Text.translatable("pack.copyFailure"), Text.literal(directory));
	}

	@Environment(EnvType.CLIENT)
	public static class Type {
		public static final SystemToast.Type NARRATOR_TOGGLE = new SystemToast.Type();
		public static final SystemToast.Type WORLD_BACKUP = new SystemToast.Type();
		public static final SystemToast.Type PACK_LOAD_FAILURE = new SystemToast.Type();
		public static final SystemToast.Type WORLD_ACCESS_FAILURE = new SystemToast.Type();
		public static final SystemToast.Type PACK_COPY_FAILURE = new SystemToast.Type();
		public static final SystemToast.Type PERIODIC_NOTIFICATION = new SystemToast.Type();
		public static final SystemToast.Type UNSECURE_SERVER_WARNING = new SystemToast.Type(10000L);
		final long displayDuration;

		public Type(long displayDuration) {
			this.displayDuration = displayDuration;
		}

		public Type() {
			this(5000L);
		}
	}
}
