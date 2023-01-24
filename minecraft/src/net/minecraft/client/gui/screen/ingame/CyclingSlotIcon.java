package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CyclingSlotIcon {
	private static final int field_42039 = 30;
	private static final int field_42040 = 16;
	private static final int field_42041 = 4;
	private final int slotId;
	private List<Identifier> textures = List.of();
	private int timer;
	private int currentIndex;

	public CyclingSlotIcon(int slotId) {
		this.slotId = slotId;
	}

	public void updateTexture(List<Identifier> textures) {
		if (!this.textures.equals(textures)) {
			this.textures = textures;
			this.currentIndex = 0;
		}

		if (!this.textures.isEmpty() && ++this.timer % 30 == 0) {
			this.currentIndex = (this.currentIndex + 1) % this.textures.size();
		}
	}

	public void render(ScreenHandler screenHandler, MatrixStack matrices, float delta, int x, int y) {
		Slot slot = screenHandler.getSlot(this.slotId);
		if (!this.textures.isEmpty() && !slot.hasStack()) {
			boolean bl = this.textures.size() > 1 && this.timer >= 30;
			float f = bl ? this.computeAlpha(delta) : 1.0F;
			if (f < 1.0F) {
				int i = Math.floorMod(this.currentIndex - 1, this.textures.size());
				this.drawIcon(slot, (Identifier)this.textures.get(i), 1.0F - f, matrices, x, y);
			}

			this.drawIcon(slot, (Identifier)this.textures.get(this.currentIndex), f, matrices, x, y);
		}
	}

	private void drawIcon(Slot slot, Identifier texture, float alpha, MatrixStack matrices, int x, int y) {
		Sprite sprite = (Sprite)MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(texture);
		RenderSystem.setShaderTexture(0, sprite.getAtlasId());
		DrawableHelper.drawSprite(matrices, x + slot.x, y + slot.y, 0, 16, 16, sprite, 1.0F, 1.0F, 1.0F, alpha);
	}

	private float computeAlpha(float delta) {
		float f = (float)(this.timer % 30) + delta;
		return Math.min(f, 4.0F) / 4.0F;
	}
}
