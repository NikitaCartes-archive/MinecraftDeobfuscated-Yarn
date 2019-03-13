package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.Screen;
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
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				EditSignScreen.this.method_2526();
			}
		});
		this.sign.setEditable(false);
		this.selectionManager = new SelectionManager(
			this.client,
			() -> this.sign.method_11302(this.currentRow).getString(),
			string -> this.sign.method_11299(this.currentRow, new StringTextComponent(string)),
			90
		);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.method_1562();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.method_2883(
				new UpdateSignC2SPacket(
					this.sign.method_11016(), this.sign.method_11302(0), this.sign.method_11302(1), this.sign.method_11302(2), this.sign.method_11302(3)
				)
			);
		}

		this.sign.setEditable(true);
	}

	@Override
	public void update() {
		this.ticksSinceOpened++;
	}

	private void method_2526() {
		this.sign.markDirty();
		this.client.method_1507(null);
	}

	@Override
	public boolean charTyped(char c, int i) {
		this.selectionManager.insert(c);
		return true;
	}

	@Override
	public void close() {
		this.method_2526();
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
		BlockState blockState = this.sign.method_11010();
		float h;
		if (blockState.getBlock().method_9525(BlockTags.field_15472)) {
			h = (float)((Integer)blockState.method_11654(StandingSignBlock.field_11559) * 360) / 16.0F;
		} else {
			h = ((Direction)blockState.method_11654(WallSignBlock.field_11726)).asRotation();
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
