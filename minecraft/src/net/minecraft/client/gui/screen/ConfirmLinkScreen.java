package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ConfirmLinkScreen extends ConfirmScreen {
	private static final Text COPY = Text.translatable("chat.copy");
	private static final Text WARNING = Text.translatable("chat.link.warning");
	private final String link;
	private final boolean drawWarning;

	public ConfirmLinkScreen(BooleanConsumer callback, String link, boolean linkTrusted) {
		this(callback, getConfirmText(linkTrusted), Text.literal(link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
	}

	public ConfirmLinkScreen(BooleanConsumer callback, Text title, String link, boolean linkTrusted) {
		this(callback, title, getConfirmText(linkTrusted, link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
	}

	public ConfirmLinkScreen(BooleanConsumer callback, Text title, URI link, boolean linkTrusted) {
		this(callback, title, link.toString(), linkTrusted);
	}

	public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, URI link, Text noText, boolean linkTrusted) {
		this(callback, title, message, link.toString(), noText, true);
	}

	public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, String link, Text noText, boolean linkTrusted) {
		super(callback, title, message);
		this.yesText = (Text)(linkTrusted ? Text.translatable("chat.link.open") : ScreenTexts.YES);
		this.noText = noText;
		this.drawWarning = !linkTrusted;
		this.link = link;
	}

	protected static MutableText getConfirmText(boolean linkTrusted, String link) {
		return getConfirmText(linkTrusted).append(ScreenTexts.SPACE).append(Text.literal(link));
	}

	protected static MutableText getConfirmText(boolean linkTrusted) {
		return Text.translatable(linkTrusted ? "chat.link.confirmTrusted" : "chat.link.confirm");
	}

	@Override
	protected void addButtons(int y) {
		this.addDrawableChild(ButtonWidget.builder(this.yesText, button -> this.callback.accept(true)).dimensions(this.width / 2 - 50 - 105, y, 100, 20).build());
		this.addDrawableChild(ButtonWidget.builder(COPY, button -> {
			this.copyToClipboard();
			this.callback.accept(false);
		}).dimensions(this.width / 2 - 50, y, 100, 20).build());
		this.addDrawableChild(ButtonWidget.builder(this.noText, button -> this.callback.accept(false)).dimensions(this.width / 2 - 50 + 105, y, 100, 20).build());
	}

	public void copyToClipboard() {
		this.client.keyboard.setClipboard(this.link);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (this.drawWarning) {
			context.drawCenteredTextWithShadow(this.textRenderer, WARNING, this.width / 2, 110, 16764108);
		}
	}

	public static void open(Screen parent, String url, boolean linkTrusted) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
			if (confirmed) {
				Util.getOperatingSystem().open(url);
			}

			minecraftClient.setScreen(parent);
		}, url, linkTrusted));
	}

	public static void open(Screen parent, URI uri, boolean linkTrusted) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
			if (confirmed) {
				Util.getOperatingSystem().open(uri);
			}

			minecraftClient.setScreen(parent);
		}, uri.toString(), linkTrusted));
	}

	public static void open(Screen parent, URI uri) {
		open(parent, uri, true);
	}

	/**
	 * Opens the confirmation screen to open {@code url}.
	 * The link is always trusted.
	 * 
	 * @see #opening
	 */
	public static void open(Screen parent, String url) {
		open(parent, url, true);
	}

	public static ButtonWidget.PressAction opening(Screen parent, String url, boolean linkTrusted) {
		return button -> open(parent, url, linkTrusted);
	}

	public static ButtonWidget.PressAction opening(Screen parent, URI uri, boolean linkTrusted) {
		return button -> open(parent, uri, linkTrusted);
	}

	/**
	 * {@return the button press action that opens the confirmation screen to open {@code url}}
	 * 
	 * <p>The link is always trusted.
	 * 
	 * @see #open
	 */
	public static ButtonWidget.PressAction opening(Screen parent, String url) {
		return opening(parent, url, true);
	}

	public static ButtonWidget.PressAction opening(Screen parent, URI uri) {
		return opening(parent, uri, true);
	}
}
