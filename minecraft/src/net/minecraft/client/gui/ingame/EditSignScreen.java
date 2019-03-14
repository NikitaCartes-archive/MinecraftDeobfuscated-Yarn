package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EditSignScreen extends Screen {
	private final SignBlockEntity sign;
	private int ticksSinceOpened;
	private int currentRow;
	private SelectionManager selectionManager;

	public EditSignScreen(SignBlockEntity signBlockEntity) {
		this.sign = signBlockEntity;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				EditSignScreen.this.finishEditing();
			}
		});
		this.sign.setEditable(false);
		this.selectionManager = new SelectionManager(
			this.client,
			() -> this.sign.getTextOnRow(this.currentRow).getString(),
			string -> this.sign.setTextOnRow(this.currentRow, new StringTextComponent(string)),
			90
		);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(
				new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3))
			);
		}

		this.sign.setEditable(true);
	}

	@Override
	public void update() {
		this.ticksSinceOpened++;
	}

	private void finishEditing() {
		this.sign.markDirty();
		this.client.openScreen(null);
	}

	@Override
	public boolean charTyped(char c, int i) {
		this.selectionManager.insert(c);
		return true;
	}

	@Override
	public void close() {
		this.finishEditing();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 265) {
			this.currentRow = this.currentRow - 1 & 3;
			this.selectionManager.moveCaretToEnd();
			return true;
		} else if (i == 264 || i == 257 || i == 335) {
			this.currentRow = this.currentRow + 1 & 3;
			this.selectionManager.moveCaretToEnd();
			return true;
		} else {
			return this.selectionManager.handleSpecialKey(i) ? true : super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("sign.edit"), this.screenWidth / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.screenWidth / 2), 0.0F, 50.0F);
		float g = 93.75F;
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		BlockState blockState = this.sign.getCachedState();
		float h;
		if (blockState.getBlock().matches(BlockTags.field_15472)) {
			h = (float)((Integer)blockState.get(StandingSignBlock.ROTATION) * 360) / 16.0F;
		} else {
			h = ((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
		}

		GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		this.sign
			.setSelectionState(this.currentRow, this.selectionManager.getSelectionStart(), this.selectionManager.getSelectionEnd(), this.ticksSinceOpened / 6 % 2 == 0);
		BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.sign, -0.5, -0.75, -0.5, 0.0F);
		this.sign.resetSelectionState();
		GlStateManager.popMatrix();
		super.draw(i, j, f);
	}
}
