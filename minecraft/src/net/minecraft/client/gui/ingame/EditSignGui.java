package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2877;
import net.minecraft.class_3728;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EditSignGui extends Gui {
	private final SignBlockEntity sign;
	private int field_3030;
	private int field_3029;
	private class_3728 field_3032;

	public EditSignGui(SignBlockEntity signBlockEntity) {
		this.sign = signBlockEntity;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				EditSignGui.this.method_2526();
			}
		});
		this.sign.setEditable(false);
		this.field_3032 = new class_3728(
			this.client,
			() -> this.sign.method_11302(this.field_3029).getString(),
			string -> this.sign.method_11299(this.field_3029, new StringTextComponent(string)),
			90
		);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(
				new class_2877(this.sign.getPos(), this.sign.method_11302(0), this.sign.method_11302(1), this.sign.method_11302(2), this.sign.method_11302(3))
			);
		}

		this.sign.setEditable(true);
	}

	@Override
	public void update() {
		this.field_3030++;
	}

	private void method_2526() {
		this.sign.markDirty();
		this.client.openGui(null);
	}

	@Override
	public boolean charTyped(char c, int i) {
		this.field_3032.method_16199(c);
		return true;
	}

	@Override
	public void close() {
		this.method_2526();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 265) {
			this.field_3029 = this.field_3029 - 1 & 3;
			this.field_3032.method_16204();
			return true;
		} else if (i == 264 || i == 257 || i == 335) {
			this.field_3029 = this.field_3029 + 1 & 3;
			this.field_3032.method_16204();
			return true;
		} else {
			return this.field_3032.method_16202(i) ? true : super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("sign.edit"), this.width / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
		float g = 93.75F;
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		BlockState blockState = this.sign.getCachedState();
		float h;
		if (blockState.getBlock().matches(BlockTags.field_15472)) {
			h = (float)((Integer)blockState.get(StandingSignBlock.field_11559) * 360) / 16.0F;
		} else {
			h = ((Direction)blockState.get(WallSignBlock.field_11726)).asRotation();
		}

		GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		this.sign.method_16332(this.field_3029, this.field_3032.method_16201(), this.field_3032.method_16203(), this.field_3030 / 6 % 2 == 0);
		BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.sign, -0.5, -0.75, -0.5, 0.0F);
		this.sign.method_16335();
		GlStateManager.popMatrix();
		super.draw(i, j, f);
	}
}
