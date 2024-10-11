package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends AgeableMobEntityRenderer<RabbitEntity, RabbitEntityRenderState, RabbitEntityModel> {
	private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/brown.png");
	private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white.png");
	private static final Identifier BLACK_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/black.png");
	private static final Identifier GOLD_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/gold.png");
	private static final Identifier SALT_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/salt.png");
	private static final Identifier WHITE_SPLOTCHED_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white_splotched.png");
	private static final Identifier TOAST_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/toast.png");
	private static final Identifier CAERBANNOG_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/caerbannog.png");

	public RabbitEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT)), new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT_BABY)), 0.3F);
	}

	public Identifier getTexture(RabbitEntityRenderState rabbitEntityRenderState) {
		if (rabbitEntityRenderState.isToast) {
			return TOAST_TEXTURE;
		} else {
			return switch (rabbitEntityRenderState.type) {
				case BROWN -> BROWN_TEXTURE;
				case WHITE -> WHITE_TEXTURE;
				case BLACK -> BLACK_TEXTURE;
				case GOLD -> GOLD_TEXTURE;
				case SALT -> SALT_TEXTURE;
				case WHITE_SPLOTCHED -> WHITE_SPLOTCHED_TEXTURE;
				case EVIL -> CAERBANNOG_TEXTURE;
			};
		}
	}

	public RabbitEntityRenderState createRenderState() {
		return new RabbitEntityRenderState();
	}

	public void updateRenderState(RabbitEntity rabbitEntity, RabbitEntityRenderState rabbitEntityRenderState, float f) {
		super.updateRenderState(rabbitEntity, rabbitEntityRenderState, f);
		rabbitEntityRenderState.jumpProgress = rabbitEntity.getJumpProgress(f);
		rabbitEntityRenderState.isToast = "Toast".equals(Formatting.strip(rabbitEntity.getName().getString()));
		rabbitEntityRenderState.type = rabbitEntity.getVariant();
	}
}
