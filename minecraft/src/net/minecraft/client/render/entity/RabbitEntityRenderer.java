package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends MobEntityRenderer<RabbitEntity, RabbitEntityModel<RabbitEntity>> {
	private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/brown.png");
	private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white.png");
	private static final Identifier BLACK_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/black.png");
	private static final Identifier GOLD_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/gold.png");
	private static final Identifier SALT_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/salt.png");
	private static final Identifier WHITE_SPLOTCHED_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white_splotched.png");
	private static final Identifier TOAST_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/toast.png");
	private static final Identifier CAERBANNOG_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/caerbannog.png");

	public RabbitEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RabbitEntityModel<>(context.getPart(EntityModelLayers.RABBIT)), 0.3F);
	}

	public Identifier getTexture(RabbitEntity rabbitEntity) {
		String string = Formatting.strip(rabbitEntity.getName().getString());
		if ("Toast".equals(string)) {
			return TOAST_TEXTURE;
		} else {
			return switch (rabbitEntity.getVariant()) {
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
}
