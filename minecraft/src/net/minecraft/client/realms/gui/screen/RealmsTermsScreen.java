package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text TITLE = new TranslatableText("mco.terms.title");
	private static final Text SENTENCE_ONE_TEXT = new TranslatableText("mco.terms.sentence.1");
	private static final Text SENTENCE_TWO_TEXT = new LiteralText(" ")
		.append(new TranslatableText("mco.terms.sentence.2").fillStyle(Style.EMPTY.withUnderline(true)));
	private final Screen parent;
	private final RealmsMainScreen mainScreen;
	private final RealmsServer realmsServer;
	private boolean onLink;
	private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

	public RealmsTermsScreen(Screen parent, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
		this.parent = parent;
		this.mainScreen = mainScreen;
		this.realmsServer = realmsServer;
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		int i = this.width / 4 - 2;
		this.addButton(new ButtonWidget(this.width / 4, row(12), i, 20, new TranslatableText("mco.terms.buttons.agree"), buttonWidget -> this.agreedToTos()));
		this.addButton(
			new ButtonWidget(this.width / 2 + 4, row(12), i, 20, new TranslatableText("mco.terms.buttons.disagree"), buttonWidget -> this.client.openScreen(this.parent))
		);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void agreedToTos() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.agreeToTos();
			this.client
				.openScreen(
					new RealmsLongRunningMcoTaskScreen(this.parent, new RealmsGetServerDetailsTask(this.mainScreen, this.parent, this.realmsServer, new ReentrantLock()))
				);
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't agree to TOS");
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.onLink) {
			this.client.keyboard.setClipboard("https://aka.ms/MinecraftRealmsTerms");
			Util.getOperatingSystem().open("https://aka.ms/MinecraftRealmsTerms");
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + SENTENCE_ONE_TEXT.getString() + " " + SENTENCE_TWO_TEXT.getString();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, TITLE, this.width / 2, 17, 16777215);
		this.textRenderer.draw(matrices, SENTENCE_ONE_TEXT, (float)(this.width / 2 - 120), (float)row(5), 16777215);
		int i = this.textRenderer.getWidth(SENTENCE_ONE_TEXT);
		int j = this.width / 2 - 121 + i;
		int k = row(5);
		int l = j + this.textRenderer.getWidth(SENTENCE_TWO_TEXT) + 1;
		int m = k + 1 + 9;
		this.onLink = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m;
		this.textRenderer.draw(matrices, SENTENCE_TWO_TEXT, (float)(this.width / 2 - 120 + i), (float)row(5), this.onLink ? 7107012 : 3368635);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
