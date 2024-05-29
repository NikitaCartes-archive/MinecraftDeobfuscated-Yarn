package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class JigsawBlockScreen extends Screen {
	private static final Text JOINT_LABEL_TEXT = Text.translatable("jigsaw_block.joint_label");
	private static final Text POOL_TEXT = Text.translatable("jigsaw_block.pool");
	private static final Text NAME_TEXT = Text.translatable("jigsaw_block.name");
	private static final Text TARGET_TEXT = Text.translatable("jigsaw_block.target");
	private static final Text FINAL_STATE_TEXT = Text.translatable("jigsaw_block.final_state");
	private static final Text PLACEMENT_PRIORITY_TEXT = Text.translatable("jigsaw_block.placement_priority");
	private static final Text PLACEMENT_PRIORITY_TOOLTIP = Text.translatable("jigsaw_block.placement_priority.tooltip");
	private static final Text SELECTION_PRIORITY_TEXT = Text.translatable("jigsaw_block.selection_priority");
	private static final Text SELECTION_PRIORITY_TOOLTIP = Text.translatable("jigsaw_block.selection_priority.tooltip");
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget nameField;
	private TextFieldWidget targetField;
	private TextFieldWidget poolField;
	private TextFieldWidget finalStateField;
	private TextFieldWidget selectionPriorityField;
	private TextFieldWidget placementPriorityField;
	int generationDepth;
	private boolean keepJigsaws = true;
	private CyclingButtonWidget<JigsawBlockEntity.Joint> jointRotationButton;
	private ButtonWidget doneButton;
	private ButtonWidget generateButton;
	private JigsawBlockEntity.Joint joint;

	public JigsawBlockScreen(JigsawBlockEntity jigsaw) {
		super(NarratorManager.EMPTY);
		this.jigsaw = jigsaw;
	}

	private void onDone() {
		this.updateServer();
		this.client.setScreen(null);
	}

	private void onCancel() {
		this.client.setScreen(null);
	}

	private void updateServer() {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateJigsawC2SPacket(
					this.jigsaw.getPos(),
					Identifier.of(this.nameField.getText()),
					Identifier.of(this.targetField.getText()),
					Identifier.of(this.poolField.getText()),
					this.finalStateField.getText(),
					this.joint,
					this.parseInt(this.selectionPriorityField.getText()),
					this.parseInt(this.placementPriorityField.getText())
				)
			);
	}

	private int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException var3) {
			return 0;
		}
	}

	private void generate() {
		this.client.getNetworkHandler().sendPacket(new JigsawGeneratingC2SPacket(this.jigsaw.getPos(), this.generationDepth, this.keepJigsaws));
	}

	@Override
	public void close() {
		this.onCancel();
	}

	@Override
	protected void init() {
		this.poolField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 20, 300, 20, POOL_TEXT);
		this.poolField.setMaxLength(128);
		this.poolField.setText(this.jigsaw.getPool().getValue().toString());
		this.poolField.setChangedListener(pool -> this.updateDoneButtonState());
		this.addSelectableChild(this.poolField);
		this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 55, 300, 20, NAME_TEXT);
		this.nameField.setMaxLength(128);
		this.nameField.setText(this.jigsaw.getName().toString());
		this.nameField.setChangedListener(name -> this.updateDoneButtonState());
		this.addSelectableChild(this.nameField);
		this.targetField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 90, 300, 20, TARGET_TEXT);
		this.targetField.setMaxLength(128);
		this.targetField.setText(this.jigsaw.getTarget().toString());
		this.targetField.setChangedListener(target -> this.updateDoneButtonState());
		this.addSelectableChild(this.targetField);
		this.finalStateField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 125, 300, 20, FINAL_STATE_TEXT);
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.getFinalState());
		this.addSelectableChild(this.finalStateField);
		this.selectionPriorityField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 160, 98, 20, SELECTION_PRIORITY_TEXT);
		this.selectionPriorityField.setMaxLength(3);
		this.selectionPriorityField.setText(Integer.toString(this.jigsaw.getSelectionPriority()));
		this.selectionPriorityField.setTooltip(Tooltip.of(SELECTION_PRIORITY_TOOLTIP));
		this.addSelectableChild(this.selectionPriorityField);
		this.placementPriorityField = new TextFieldWidget(this.textRenderer, this.width / 2 - 50, 160, 98, 20, PLACEMENT_PRIORITY_TEXT);
		this.placementPriorityField.setMaxLength(3);
		this.placementPriorityField.setText(Integer.toString(this.jigsaw.getPlacementPriority()));
		this.placementPriorityField.setTooltip(Tooltip.of(PLACEMENT_PRIORITY_TOOLTIP));
		this.addSelectableChild(this.placementPriorityField);
		this.joint = this.jigsaw.getJoint();
		this.jointRotationButton = this.addDrawableChild(
			CyclingButtonWidget.<JigsawBlockEntity.Joint>builder(JigsawBlockEntity.Joint::asText)
				.values(JigsawBlockEntity.Joint.values())
				.initially(this.joint)
				.omitKeyText()
				.build(this.width / 2 + 54, 160, 100, 20, JOINT_LABEL_TEXT, (button, joint) -> this.joint = joint)
		);
		boolean bl = JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical();
		this.jointRotationButton.active = bl;
		this.jointRotationButton.visible = bl;
		this.addDrawableChild(new SliderWidget(this.width / 2 - 154, 185, 100, 20, ScreenTexts.EMPTY, 0.0) {
			{
				this.updateMessage();
			}

			@Override
			protected void updateMessage() {
				this.setMessage(Text.translatable("jigsaw_block.levels", JigsawBlockScreen.this.generationDepth));
			}

			@Override
			protected void applyValue() {
				JigsawBlockScreen.this.generationDepth = MathHelper.floor(MathHelper.clampedLerp(0.0, 20.0, this.value));
			}
		});
		this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.keepJigsaws)
				.build(this.width / 2 - 50, 185, 100, 20, Text.translatable("jigsaw_block.keep_jigsaws"), (button, keepJigsaws) -> this.keepJigsaws = keepJigsaws)
		);
		this.generateButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("jigsaw_block.generate"), button -> {
			this.onDone();
			this.generate();
		}).dimensions(this.width / 2 + 54, 185, 100, 20).build());
		this.doneButton = this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, button -> this.onDone()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build()
		);
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());
		this.updateDoneButtonState();
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.poolField);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
	}

	public static boolean isValidId(String id) {
		return Identifier.tryParse(id) != null;
	}

	private void updateDoneButtonState() {
		boolean bl = isValidId(this.nameField.getText()) && isValidId(this.targetField.getText()) && isValidId(this.poolField.getText());
		this.doneButton.active = bl;
		this.generateButton.active = bl;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.nameField.getText();
		String string2 = this.targetField.getText();
		String string3 = this.poolField.getText();
		String string4 = this.finalStateField.getText();
		String string5 = this.selectionPriorityField.getText();
		String string6 = this.placementPriorityField.getText();
		int i = this.generationDepth;
		JigsawBlockEntity.Joint joint = this.joint;
		this.init(client, width, height);
		this.nameField.setText(string);
		this.targetField.setText(string2);
		this.poolField.setText(string3);
		this.finalStateField.setText(string4);
		this.generationDepth = i;
		this.joint = joint;
		this.jointRotationButton.setValue(joint);
		this.selectionPriorityField.setText(string5);
		this.placementPriorityField.setText(string6);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (!this.doneButton.active || keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return false;
		} else {
			this.onDone();
			return true;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, POOL_TEXT, this.width / 2 - 153, 10, 10526880);
		this.poolField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, NAME_TEXT, this.width / 2 - 153, 45, 10526880);
		this.nameField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, TARGET_TEXT, this.width / 2 - 153, 80, 10526880);
		this.targetField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, FINAL_STATE_TEXT, this.width / 2 - 153, 115, 10526880);
		this.finalStateField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, SELECTION_PRIORITY_TEXT, this.width / 2 - 153, 150, 10526880);
		this.placementPriorityField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, PLACEMENT_PRIORITY_TEXT, this.width / 2 - 50, 150, 10526880);
		this.selectionPriorityField.render(context, mouseX, mouseY, delta);
		if (JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical()) {
			context.drawTextWithShadow(this.textRenderer, JOINT_LABEL_TEXT, this.width / 2 + 53, 150, 10526880);
		}
	}
}
