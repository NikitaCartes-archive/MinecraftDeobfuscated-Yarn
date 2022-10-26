package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class HangingSignEditScreen extends AbstractSignEditScreen {
	public static final float BACKGROUND_SCALE = 4.0F;
	private static final Vector3f TEXT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
	private static final int field_40433 = 16;
	private static final int field_40434 = 16;
	private final Identifier texture = new Identifier("textures/gui/hanging_signs/" + this.signType.getName() + ".png");

	public HangingSignEditScreen(SignBlockEntity signBlockEntity, boolean bl) {
		super(signBlockEntity, bl, Text.translatable("hanging_sign.edit"));
	}

	@Override
	protected void translateForRender(MatrixStack matrices, BlockState state) {
		matrices.translate((float)this.width / 2.0F, 125.0F, 50.0F);
	}

	@Override
	protected void renderSignBackground(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, BlockState state) {
		matrices.translate(0.0F, -13.0F, 0.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		matrices.scale(4.0F, 4.0F, 1.0F);
		drawTexture(matrices, -8, -8, 0.0F, 0.0F, 16, 16, 16, 16);
	}

	@Override
	protected Vector3f getTextScale() {
		return TEXT_SCALE;
	}
}
