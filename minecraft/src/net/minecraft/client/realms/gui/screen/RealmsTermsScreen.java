package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE = Text.translatable("mco.terms.title");
	private static final Text SENTENCE_ONE_TEXT = Text.translatable("mco.terms.sentence.1");
	private static final Text SENTENCE_TWO_TEXT = ScreenTexts.space().append(Text.translatable("mco.terms.sentence.2").fillStyle(Style.EMPTY.withUnderline(true)));
	private final Screen parent;
	private final RealmsServer realmsServer;
	private boolean onLink;

	public RealmsTermsScreen(Screen parent, RealmsServer realmsServer) {
		super(TITLE);
		this.parent = parent;
		this.realmsServer = realmsServer;
	}

	@Override
	public void init() {
		int i = this.width / 4 - 2;
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.terms.buttons.agree"), button -> this.agreedToTos()).dimensions(this.width / 4, row(12), i, 20).build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.terms.buttons.disagree"), button -> this.client.setScreen(this.parent))
				.dimensions(this.width / 2 + 4, row(12), i, 20)
				.build()
		);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void agreedToTos() {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			realmsClient.agreeToTos();
			this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new RealmsPrepareConnectionTask(this.parent, this.realmsServer)));
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't agree to TOS", (Throwable)var3);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.onLink) {
			this.client.keyboard.setClipboard(Urls.REALMS_TERMS.toString());
			Util.getOperatingSystem().open(Urls.REALMS_TERMS);
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), SENTENCE_ONE_TEXT).append(ScreenTexts.SPACE).append(SENTENCE_TWO_TEXT);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, Colors.WHITE);
		context.drawText(this.textRenderer, SENTENCE_ONE_TEXT, this.width / 2 - 120, row(5), Colors.WHITE, false);
		int i = this.textRenderer.getWidth(SENTENCE_ONE_TEXT);
		int j = this.width / 2 - 121 + i;
		int k = row(5);
		int l = j + this.textRenderer.getWidth(SENTENCE_TWO_TEXT) + 1;
		int m = k + 1 + 9;
		this.onLink = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m;
		context.drawText(this.textRenderer, SENTENCE_TWO_TEXT, this.width / 2 - 120 + i, row(5), this.onLink ? 7107012 : 3368635, false);
	}
}
