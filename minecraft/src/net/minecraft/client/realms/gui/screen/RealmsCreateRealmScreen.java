package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
	private static final Text TITLE_TEXT = Text.translatable("mco.selectServer.create");
	private static final Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
	private static final int field_45243 = 10;
	private static final int field_45244 = 210;
	private final RealmsMainScreen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private TextFieldWidget nameBox;
	private TextFieldWidget descriptionBox;
	private final Runnable worldCreator;

	public RealmsCreateRealmScreen(RealmsMainScreen parent, RealmsServer server, boolean prerelease) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.worldCreator = () -> this.createWorld(server, prerelease);
	}

	@Override
	public void init() {
		this.layout.addHeader(this.title, this.textRenderer);
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical()).spacing(10);
		ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.CONTINUE, button -> this.worldCreator.run()).build();
		buttonWidget.active = false;
		this.nameBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_NAME_TEXT);
		this.nameBox.setChangedListener(name -> buttonWidget.active = !StringHelper.isBlank(name));
		this.descriptionBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_DESCRIPTION_TEXT);
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.nameBox, WORLD_NAME_TEXT));
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.descriptionBox, WORLD_DESCRIPTION_TEXT));
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
		directionalLayoutWidget2.add(buttonWidget);
		directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.refreshWidgetPositions();
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nameBox);
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	private void createWorld(RealmsServer realmsServer, boolean prerelease) {
		if (!realmsServer.isPrerelease() && prerelease) {
			AtomicBoolean atomicBoolean = new AtomicBoolean();
			this.client.setScreen(new NoticeScreen(() -> {
				atomicBoolean.set(true);
				this.parent.removeSelection();
				this.client.setScreen(this.parent);
			}, Text.translatable("mco.upload.preparing"), Text.empty()));
			CompletableFuture.supplyAsync(() -> createPrereleaseServer(realmsServer), Util.getMainWorkerExecutor()).thenAcceptAsync(prereleaseServer -> {
				if (!atomicBoolean.get()) {
					this.createWorld(prereleaseServer);
				}
			}, this.client).exceptionallyAsync(throwable -> {
				this.parent.removeSelection();
				Text text;
				if (throwable.getCause() instanceof RealmsServiceException realmsServiceException) {
					text = realmsServiceException.error.getText();
				} else {
					text = Text.translatable("mco.errorMessage.initialize.failed");
				}

				this.client.setScreen(new RealmsGenericErrorScreen(text, this.parent));
				return null;
			}, this.client);
		} else {
			this.createWorld(realmsServer);
		}
	}

	private static RealmsServer createPrereleaseServer(RealmsServer parent) {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			return realmsClient.createPrereleaseServer(parent.id);
		} catch (RealmsServiceException var3) {
			throw new RuntimeException(var3);
		}
	}

	private void createWorld(RealmsServer server) {
		WorldCreationTask worldCreationTask = new WorldCreationTask(server.id, this.nameBox.getText(), this.descriptionBox.getText());
		RealmsCreateWorldScreen realmsCreateWorldScreen = RealmsCreateWorldScreen.newRealm(this, server, worldCreationTask, () -> this.client.execute(() -> {
				RealmsMainScreen.resetServerList();
				this.client.setScreen(this.parent);
			}));
		this.client.setScreen(realmsCreateWorldScreen);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
