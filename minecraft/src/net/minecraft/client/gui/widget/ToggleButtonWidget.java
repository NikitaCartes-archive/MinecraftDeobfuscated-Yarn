package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ToggleButtonWidget extends AbstractButtonWidget {
	protected Identifier texture;
	protected boolean toggled;
	protected int u;
	protected int v;
	protected int pressedUOffset;
	protected int hoverVOffset;

	public ToggleButtonWidget(int x, int y, int width, int height, boolean toggled) {
		super(x, y, width, height, "");
		this.toggled = toggled;
	}

	public void setTextureUV(int u, int v, int pressedUOffset, int hoverVOffset, Identifier texture) {
		this.u = u;
		this.v = v;
		this.pressedUOffset = pressedUOffset;
		this.hoverVOffset = hoverVOffset;
		this.texture = texture;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}

	public boolean isToggled() {
		return this.toggled;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		int i = this.u;
		int j = this.v;
		if (this.toggled) {
			i += this.pressedUOffset;
		}

		if (this.isHovered()) {
			j += this.hoverVOffset;
		}

		this.drawTexture(this.x, this.y, i, j, this.width, this.height);
		RenderSystem.enableDepthTest();
	}
}
