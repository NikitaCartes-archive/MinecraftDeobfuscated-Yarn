package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class HangingSignEditScreen extends AbstractSignEditScreen {
	public static final float BACKGROUND_SCALE = 4.5F;
	private static final Vector3f TEXT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
	private static final int field_40433 = 16;
	private static final int field_40434 = 16;
	private final Identifier texture = Identifier.ofVanilla("textures/gui/hanging_signs/" + this.signType.name() + ".png");

	public HangingSignEditScreen(SignBlockEntity signBlockEntity, boolean bl, boolean bl2) {
		super(signBlockEntity, bl, bl2, Text.translatable("hanging_sign.edit"));
	}

	@Override
	protected void translateForRender(DrawContext context, BlockState state) {
		context.getMatrices().translate((float)this.width / 2.0F, 125.0F, 50.0F);
	}

	@Override
	protected void renderSignBackground(DrawContext context, BlockState state) {
		context.getMatrices().translate(0.0F, -13.0F, 0.0F);
		context.getMatrices().scale(4.5F, 4.5F, 1.0F);
		context.drawTexture(this.texture, -8, -8, 0.0F, 0.0F, 16, 16, 16, 16);
	}

	@Override
	protected Vector3f getTextScale() {
		return TEXT_SCALE;
	}
}
