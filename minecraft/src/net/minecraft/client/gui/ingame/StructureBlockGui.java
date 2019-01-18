package net.minecraft.client.gui.ingame;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateStructureBlockServerPacket;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class StructureBlockGui extends Gui {
	private static final Logger field_2989 = LogManager.getLogger();
	private final StructureBlockBlockEntity structureBlock;
	private Mirror mirror = Mirror.NONE;
	private Rotation rotation = Rotation.ROT_0;
	private StructureBlockMode mode = StructureBlockMode.field_12696;
	private boolean field_2985;
	private boolean field_2997;
	private boolean field_2983;
	private TextFieldWidget inputName;
	private TextFieldWidget inputPosX;
	private TextFieldWidget inputPosY;
	private TextFieldWidget inputPosZ;
	private TextFieldWidget inputSizeX;
	private TextFieldWidget inputSizeY;
	private TextFieldWidget inputSizeZ;
	private TextFieldWidget inputIntegrity;
	private TextFieldWidget inputSeed;
	private TextFieldWidget inputMetadata;
	private ButtonWidget doneButton;
	private ButtonWidget cancelButton;
	private ButtonWidget buttonSave;
	private ButtonWidget buttonLoad;
	private ButtonWidget buttonRotate0;
	private ButtonWidget buttonRotate90;
	private ButtonWidget buttonRotate180;
	private ButtonWidget buttonRotate270;
	private ButtonWidget buttonMode;
	private ButtonWidget buttonDetect;
	private ButtonWidget buttonEntities;
	private ButtonWidget buttonMirror;
	private ButtonWidget buttonShowAir;
	private ButtonWidget buttonShowBoundingBox;
	private final List<TextFieldWidget> textFields = Lists.<TextFieldWidget>newArrayList();
	private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

	public StructureBlockGui(StructureBlockBlockEntity structureBlockBlockEntity) {
		this.structureBlock = structureBlockBlockEntity;
		this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	}

	@Override
	public void update() {
		this.inputName.tick();
		this.inputPosX.tick();
		this.inputPosY.tick();
		this.inputPosZ.tick();
		this.inputSizeX.tick();
		this.inputSizeY.tick();
		this.inputSizeZ.tick();
		this.inputIntegrity.tick();
		this.inputSeed.tick();
		this.inputMetadata.tick();
	}

	private void method_2515() {
		if (this.method_2516(StructureBlockBlockEntity.Action.field_12108)) {
			this.client.openGui(null);
		}
	}

	private void method_2514() {
		this.structureBlock.setMirror(this.mirror);
		this.structureBlock.setRotation(this.rotation);
		this.structureBlock.method_11381(this.mode);
		this.structureBlock.setIgnoreEntities(this.field_2985);
		this.structureBlock.setShowAir(this.field_2997);
		this.structureBlock.setShowBoundingBox(this.field_2983);
		this.client.openGui(null);
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.method_2515();
			}
		});
		this.cancelButton = this.addButton(new ButtonWidget(1, this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.method_2514();
			}
		});
		this.buttonSave = this.addButton(new ButtonWidget(9, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.save")) {
			@Override
			public void onPressed(double d, double e) {
				if (StructureBlockGui.this.structureBlock.method_11374() == StructureBlockMode.field_12695) {
					StructureBlockGui.this.method_2516(StructureBlockBlockEntity.Action.field_12110);
					StructureBlockGui.this.client.openGui(null);
				}
			}
		});
		this.buttonLoad = this.addButton(new ButtonWidget(10, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.load")) {
			@Override
			public void onPressed(double d, double e) {
				if (StructureBlockGui.this.structureBlock.method_11374() == StructureBlockMode.field_12697) {
					StructureBlockGui.this.method_2516(StructureBlockBlockEntity.Action.field_12109);
					StructureBlockGui.this.client.openGui(null);
				}
			}
		});
		this.buttonMode = this.addButton(new ButtonWidget(18, this.width / 2 - 4 - 150, 185, 50, 20, "MODE") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.cycleMode();
				StructureBlockGui.this.updateMode();
			}
		});
		this.buttonDetect = this.addButton(new ButtonWidget(19, this.width / 2 + 4 + 100, 120, 50, 20, I18n.translate("structure_block.button.detect_size")) {
			@Override
			public void onPressed(double d, double e) {
				if (StructureBlockGui.this.structureBlock.method_11374() == StructureBlockMode.field_12695) {
					StructureBlockGui.this.method_2516(StructureBlockBlockEntity.Action.field_12106);
					StructureBlockGui.this.client.openGui(null);
				}
			}
		});
		this.buttonEntities = this.addButton(new ButtonWidget(20, this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setIgnoreEntities(!StructureBlockGui.this.structureBlock.shouldIgnoreEntities());
				StructureBlockGui.this.updateIgnoreEntitiesButton();
			}
		});
		this.buttonMirror = this.addButton(new ButtonWidget(21, this.width / 2 - 20, 185, 40, 20, "MIRROR") {
			@Override
			public void onPressed(double d, double e) {
				switch (StructureBlockGui.this.structureBlock.getMirror()) {
					case NONE:
						StructureBlockGui.this.structureBlock.setMirror(Mirror.LEFT_RIGHT);
						break;
					case LEFT_RIGHT:
						StructureBlockGui.this.structureBlock.setMirror(Mirror.FRONT_BACK);
						break;
					case FRONT_BACK:
						StructureBlockGui.this.structureBlock.setMirror(Mirror.NONE);
				}

				StructureBlockGui.this.updateMirrorButton();
			}
		});
		this.buttonShowAir = this.addButton(new ButtonWidget(22, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setShowAir(!StructureBlockGui.this.structureBlock.shouldShowAir());
				StructureBlockGui.this.updateShowAirButton();
			}
		});
		this.buttonShowBoundingBox = this.addButton(new ButtonWidget(23, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setShowBoundingBox(!StructureBlockGui.this.structureBlock.shouldShowBoundingBox());
				StructureBlockGui.this.updateShowBoundingBoxButton();
			}
		});
		this.buttonRotate0 = this.addButton(new ButtonWidget(11, this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setRotation(Rotation.ROT_0);
				StructureBlockGui.this.updateRotationButton();
			}
		});
		this.buttonRotate90 = this.addButton(new ButtonWidget(12, this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setRotation(Rotation.ROT_90);
				StructureBlockGui.this.updateRotationButton();
			}
		});
		this.buttonRotate180 = this.addButton(new ButtonWidget(13, this.width / 2 + 1 + 20, 185, 40, 20, "180") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setRotation(Rotation.ROT_180);
				StructureBlockGui.this.updateRotationButton();
			}
		});
		this.buttonRotate270 = this.addButton(new ButtonWidget(14, this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270") {
			@Override
			public void onPressed(double d, double e) {
				StructureBlockGui.this.structureBlock.setRotation(Rotation.ROT_270);
				StructureBlockGui.this.updateRotationButton();
			}
		});
		this.textFields.clear();
		this.inputName = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 40, 300, 20) {
			@Override
			public boolean charTyped(char c, int i) {
				return !StructureBlockGui.this.method_16016(this.getText(), c, this.getCursor()) ? false : super.charTyped(c, i);
			}
		};
		this.inputName.setMaxLength(64);
		this.inputName.setText(this.structureBlock.getStructureName());
		this.textFields.add(this.inputName);
		BlockPos blockPos = this.structureBlock.getOffset();
		this.inputPosX = new TextFieldWidget(3, this.fontRenderer, this.width / 2 - 152, 80, 80, 20);
		this.inputPosX.setMaxLength(15);
		this.inputPosX.setText(Integer.toString(blockPos.getX()));
		this.textFields.add(this.inputPosX);
		this.inputPosY = new TextFieldWidget(4, this.fontRenderer, this.width / 2 - 72, 80, 80, 20);
		this.inputPosY.setMaxLength(15);
		this.inputPosY.setText(Integer.toString(blockPos.getY()));
		this.textFields.add(this.inputPosY);
		this.inputPosZ = new TextFieldWidget(5, this.fontRenderer, this.width / 2 + 8, 80, 80, 20);
		this.inputPosZ.setMaxLength(15);
		this.inputPosZ.setText(Integer.toString(blockPos.getZ()));
		this.textFields.add(this.inputPosZ);
		BlockPos blockPos2 = this.structureBlock.getSize();
		this.inputSizeX = new TextFieldWidget(6, this.fontRenderer, this.width / 2 - 152, 120, 80, 20);
		this.inputSizeX.setMaxLength(15);
		this.inputSizeX.setText(Integer.toString(blockPos2.getX()));
		this.textFields.add(this.inputSizeX);
		this.inputSizeY = new TextFieldWidget(7, this.fontRenderer, this.width / 2 - 72, 120, 80, 20);
		this.inputSizeY.setMaxLength(15);
		this.inputSizeY.setText(Integer.toString(blockPos2.getY()));
		this.textFields.add(this.inputSizeY);
		this.inputSizeZ = new TextFieldWidget(8, this.fontRenderer, this.width / 2 + 8, 120, 80, 20);
		this.inputSizeZ.setMaxLength(15);
		this.inputSizeZ.setText(Integer.toString(blockPos2.getZ()));
		this.textFields.add(this.inputSizeZ);
		this.inputIntegrity = new TextFieldWidget(15, this.fontRenderer, this.width / 2 - 152, 120, 80, 20);
		this.inputIntegrity.setMaxLength(15);
		this.inputIntegrity.setText(this.decimalFormat.format((double)this.structureBlock.getIntegrity()));
		this.textFields.add(this.inputIntegrity);
		this.inputSeed = new TextFieldWidget(16, this.fontRenderer, this.width / 2 - 72, 120, 80, 20);
		this.inputSeed.setMaxLength(31);
		this.inputSeed.setText(Long.toString(this.structureBlock.getSeed()));
		this.textFields.add(this.inputSeed);
		this.inputMetadata = new TextFieldWidget(17, this.fontRenderer, this.width / 2 - 152, 120, 240, 20);
		this.inputMetadata.setMaxLength(128);
		this.inputMetadata.setText(this.structureBlock.getMetadata());
		this.textFields.add(this.inputMetadata);
		this.listeners.addAll(this.textFields);
		this.mirror = this.structureBlock.getMirror();
		this.updateMirrorButton();
		this.rotation = this.structureBlock.getRotation();
		this.updateRotationButton();
		this.mode = this.structureBlock.method_11374();
		this.updateMode();
		this.field_2985 = this.structureBlock.shouldIgnoreEntities();
		this.updateIgnoreEntitiesButton();
		this.field_2997 = this.structureBlock.shouldShowAir();
		this.updateShowAirButton();
		this.field_2983 = this.structureBlock.shouldShowBoundingBox();
		this.updateShowBoundingBoxButton();
		this.inputName.setFocused(true);
		this.setFocused(this.inputName);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.inputName.getText();
		String string2 = this.inputPosX.getText();
		String string3 = this.inputPosY.getText();
		String string4 = this.inputPosZ.getText();
		String string5 = this.inputSizeX.getText();
		String string6 = this.inputSizeY.getText();
		String string7 = this.inputSizeZ.getText();
		String string8 = this.inputIntegrity.getText();
		String string9 = this.inputSeed.getText();
		String string10 = this.inputMetadata.getText();
		this.initialize(minecraftClient, i, j);
		this.inputName.setText(string);
		this.inputPosX.setText(string2);
		this.inputPosY.setText(string3);
		this.inputPosZ.setText(string4);
		this.inputSizeX.setText(string5);
		this.inputSizeY.setText(string6);
		this.inputSizeZ.setText(string7);
		this.inputIntegrity.setText(string8);
		this.inputSeed.setText(string9);
		this.inputMetadata.setText(string10);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void updateIgnoreEntitiesButton() {
		boolean bl = !this.structureBlock.shouldIgnoreEntities();
		if (bl) {
			this.buttonEntities.text = I18n.translate("options.on");
		} else {
			this.buttonEntities.text = I18n.translate("options.off");
		}
	}

	private void updateShowAirButton() {
		boolean bl = this.structureBlock.shouldShowAir();
		if (bl) {
			this.buttonShowAir.text = I18n.translate("options.on");
		} else {
			this.buttonShowAir.text = I18n.translate("options.off");
		}
	}

	private void updateShowBoundingBoxButton() {
		boolean bl = this.structureBlock.shouldShowBoundingBox();
		if (bl) {
			this.buttonShowBoundingBox.text = I18n.translate("options.on");
		} else {
			this.buttonShowBoundingBox.text = I18n.translate("options.off");
		}
	}

	private void updateMirrorButton() {
		Mirror mirror = this.structureBlock.getMirror();
		switch (mirror) {
			case NONE:
				this.buttonMirror.text = "|";
				break;
			case LEFT_RIGHT:
				this.buttonMirror.text = "< >";
				break;
			case FRONT_BACK:
				this.buttonMirror.text = "^ v";
		}
	}

	private void updateRotationButton() {
		this.buttonRotate0.enabled = true;
		this.buttonRotate90.enabled = true;
		this.buttonRotate180.enabled = true;
		this.buttonRotate270.enabled = true;
		switch (this.structureBlock.getRotation()) {
			case ROT_0:
				this.buttonRotate0.enabled = false;
				break;
			case ROT_180:
				this.buttonRotate180.enabled = false;
				break;
			case ROT_270:
				this.buttonRotate270.enabled = false;
				break;
			case ROT_90:
				this.buttonRotate90.enabled = false;
		}
	}

	private void updateMode() {
		this.inputName.setFocused(false);
		this.inputPosX.setFocused(false);
		this.inputPosY.setFocused(false);
		this.inputPosZ.setFocused(false);
		this.inputSizeX.setFocused(false);
		this.inputSizeY.setFocused(false);
		this.inputSizeZ.setFocused(false);
		this.inputIntegrity.setFocused(false);
		this.inputSeed.setFocused(false);
		this.inputMetadata.setFocused(false);
		this.inputName.setVisible(false);
		this.inputName.setFocused(false);
		this.inputPosX.setVisible(false);
		this.inputPosY.setVisible(false);
		this.inputPosZ.setVisible(false);
		this.inputSizeX.setVisible(false);
		this.inputSizeY.setVisible(false);
		this.inputSizeZ.setVisible(false);
		this.inputIntegrity.setVisible(false);
		this.inputSeed.setVisible(false);
		this.inputMetadata.setVisible(false);
		this.buttonSave.visible = false;
		this.buttonLoad.visible = false;
		this.buttonDetect.visible = false;
		this.buttonEntities.visible = false;
		this.buttonMirror.visible = false;
		this.buttonRotate0.visible = false;
		this.buttonRotate90.visible = false;
		this.buttonRotate180.visible = false;
		this.buttonRotate270.visible = false;
		this.buttonShowAir.visible = false;
		this.buttonShowBoundingBox.visible = false;
		switch (this.structureBlock.method_11374()) {
			case field_12695:
				this.inputName.setVisible(true);
				this.inputPosX.setVisible(true);
				this.inputPosY.setVisible(true);
				this.inputPosZ.setVisible(true);
				this.inputSizeX.setVisible(true);
				this.inputSizeY.setVisible(true);
				this.inputSizeZ.setVisible(true);
				this.buttonSave.visible = true;
				this.buttonDetect.visible = true;
				this.buttonEntities.visible = true;
				this.buttonShowAir.visible = true;
				break;
			case field_12697:
				this.inputName.setVisible(true);
				this.inputPosX.setVisible(true);
				this.inputPosY.setVisible(true);
				this.inputPosZ.setVisible(true);
				this.inputIntegrity.setVisible(true);
				this.inputSeed.setVisible(true);
				this.buttonLoad.visible = true;
				this.buttonEntities.visible = true;
				this.buttonMirror.visible = true;
				this.buttonRotate0.visible = true;
				this.buttonRotate90.visible = true;
				this.buttonRotate180.visible = true;
				this.buttonRotate270.visible = true;
				this.buttonShowBoundingBox.visible = true;
				this.updateRotationButton();
				break;
			case field_12699:
				this.inputName.setVisible(true);
				break;
			case field_12696:
				this.inputMetadata.setVisible(true);
		}

		this.buttonMode.text = I18n.translate("structure_block.mode." + this.structureBlock.method_11374().asString());
	}

	private boolean method_2516(StructureBlockBlockEntity.Action action) {
		BlockPos blockPos = new BlockPos(this.parseInt(this.inputPosX.getText()), this.parseInt(this.inputPosY.getText()), this.parseInt(this.inputPosZ.getText()));
		BlockPos blockPos2 = new BlockPos(
			this.parseInt(this.inputSizeX.getText()), this.parseInt(this.inputSizeY.getText()), this.parseInt(this.inputSizeZ.getText())
		);
		float f = this.parseFloat(this.inputIntegrity.getText());
		long l = this.parseLong(this.inputSeed.getText());
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateStructureBlockServerPacket(
					this.structureBlock.getPos(),
					action,
					this.structureBlock.method_11374(),
					this.inputName.getText(),
					blockPos,
					blockPos2,
					this.structureBlock.getMirror(),
					this.structureBlock.getRotation(),
					this.inputMetadata.getText(),
					this.structureBlock.shouldIgnoreEntities(),
					this.structureBlock.shouldShowAir(),
					this.structureBlock.shouldShowBoundingBox(),
					f,
					l
				)
			);
		return true;
	}

	private long parseLong(String string) {
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException var3) {
			return 0L;
		}
	}

	private float parseFloat(String string) {
		try {
			return Float.valueOf(string);
		} catch (NumberFormatException var3) {
			return 1.0F;
		}
	}

	private int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException var3) {
			return 0;
		}
	}

	@Override
	public void close() {
		this.method_2514();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			for (TextFieldWidget textFieldWidget : this.textFields) {
				textFieldWidget.setFocused(this.getFocused() == textFieldWidget);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i != 258) {
			if (i != 257 && i != 335) {
				return super.keyPressed(i, j, k);
			} else {
				this.method_2515();
				return true;
			}
		} else {
			TextFieldWidget textFieldWidget = null;
			TextFieldWidget textFieldWidget2 = null;

			for (TextFieldWidget textFieldWidget3 : this.textFields) {
				if (textFieldWidget != null && textFieldWidget3.isVisible()) {
					textFieldWidget2 = textFieldWidget3;
					break;
				}

				if (textFieldWidget3.isFocused() && textFieldWidget3.isVisible()) {
					textFieldWidget = textFieldWidget3;
				}
			}

			if (textFieldWidget != null && textFieldWidget2 == null) {
				for (TextFieldWidget textFieldWidget3 : this.textFields) {
					if (textFieldWidget3.isVisible() && textFieldWidget3 != textFieldWidget) {
						textFieldWidget2 = textFieldWidget3;
						break;
					}
				}
			}

			if (textFieldWidget2 != null && textFieldWidget2 != textFieldWidget) {
				textFieldWidget.setFocused(false);
				textFieldWidget2.setFocused(true);
				this.setFocused(textFieldWidget2);
			}

			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		StructureBlockMode structureBlockMode = this.structureBlock.method_11374();
		this.drawStringCentered(this.fontRenderer, I18n.translate(Blocks.field_10465.getTranslationKey()), this.width / 2, 10, 16777215);
		if (structureBlockMode != StructureBlockMode.field_12696) {
			this.drawString(this.fontRenderer, I18n.translate("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
			this.inputName.render(i, j, f);
		}

		if (structureBlockMode == StructureBlockMode.field_12697 || structureBlockMode == StructureBlockMode.field_12695) {
			this.drawString(this.fontRenderer, I18n.translate("structure_block.position"), this.width / 2 - 153, 70, 10526880);
			this.inputPosX.render(i, j, f);
			this.inputPosY.render(i, j, f);
			this.inputPosZ.render(i, j, f);
			String string = I18n.translate("structure_block.include_entities");
			int k = this.fontRenderer.getStringWidth(string);
			this.drawString(this.fontRenderer, string, this.width / 2 + 154 - k, 150, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.field_12695) {
			this.drawString(this.fontRenderer, I18n.translate("structure_block.size"), this.width / 2 - 153, 110, 10526880);
			this.inputSizeX.render(i, j, f);
			this.inputSizeY.render(i, j, f);
			this.inputSizeZ.render(i, j, f);
			String string = I18n.translate("structure_block.detect_size");
			int k = this.fontRenderer.getStringWidth(string);
			this.drawString(this.fontRenderer, string, this.width / 2 + 154 - k, 110, 10526880);
			String string2 = I18n.translate("structure_block.show_air");
			int l = this.fontRenderer.getStringWidth(string2);
			this.drawString(this.fontRenderer, string2, this.width / 2 + 154 - l, 70, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.field_12697) {
			this.drawString(this.fontRenderer, I18n.translate("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
			this.inputIntegrity.render(i, j, f);
			this.inputSeed.render(i, j, f);
			String string = I18n.translate("structure_block.show_boundingbox");
			int k = this.fontRenderer.getStringWidth(string);
			this.drawString(this.fontRenderer, string, this.width / 2 + 154 - k, 70, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.field_12696) {
			this.drawString(this.fontRenderer, I18n.translate("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
			this.inputMetadata.render(i, j, f);
		}

		String string = "structure_block.mode_info." + structureBlockMode.asString();
		this.drawString(this.fontRenderer, I18n.translate(string), this.width / 2 - 153, 174, 10526880);
		super.draw(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
