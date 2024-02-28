package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.task.CreatingSnapshotWorldTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

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

	public RealmsCreateRealmScreen(RealmsMainScreen parent, RealmsServer realmsServer) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.worldCreator = () -> this.createWorld(realmsServer);
	}

	public RealmsCreateRealmScreen(RealmsMainScreen parent, long parentId) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.worldCreator = () -> this.createSnapshotWorld(parentId);
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
		this.initTabNavigation();
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nameBox);
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	private void createWorld(RealmsServer realmsServer) {
		WorldCreationTask worldCreationTask = new WorldCreationTask(realmsServer.id, this.nameBox.getText(), this.descriptionBox.getText());
		RealmsCreateWorldScreen realmsCreateWorldScreen = RealmsCreateWorldScreen.newRealm(this, realmsServer, worldCreationTask, () -> this.client.execute(() -> {
				RealmsMainScreen.resetServerList();
				this.client.setScreen(this.parent);
			}));
		this.client.setScreen(realmsCreateWorldScreen);
	}

	private void createSnapshotWorld(long parentId) {
		Screen screen = new RealmsResetNormalWorldScreen(
			info -> {
				if (info == null) {
					this.client.setScreen(this);
				} else {
					this.client
						.setScreen(
							new RealmsLongRunningMcoTaskScreen(
								this, new CreatingSnapshotWorldTask(this.parent, parentId, info, this.nameBox.getText(), this.descriptionBox.getText())
							)
						);
				}
			},
			TITLE_TEXT
		);
		this.client.setScreen(screen);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
