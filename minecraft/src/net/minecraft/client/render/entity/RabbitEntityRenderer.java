package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends MobEntityRenderer<RabbitEntity, RabbitEntityModel<RabbitEntity>> {
	private static final Identifier BROWN_SKIN = new Identifier("textures/entity/rabbit/brown.png");
	private static final Identifier WHITE_SKIN = new Identifier("textures/entity/rabbit/white.png");
	private static final Identifier BLACK_SKIN = new Identifier("textures/entity/rabbit/black.png");
	private static final Identifier GOLD_SKIN = new Identifier("textures/entity/rabbit/gold.png");
	private static final Identifier SALT_SKIN = new Identifier("textures/entity/rabbit/salt.png");
	private static final Identifier WHITE_SPOTTED_SKIN = new Identifier("textures/entity/rabbit/white_splotched.png");
	private static final Identifier TOAST_SKIN = new Identifier("textures/entity/rabbit/toast.png");
	private static final Identifier CAERBANNOG_SKIN = new Identifier("textures/entity/rabbit/caerbannog.png");

	public RabbitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new RabbitEntityModel<>(), 0.3F);
	}

	protected Identifier getTexture(RabbitEntity rabbitEntity) {
		String string = Formatting.strip(rabbitEntity.getName().getString());
		if (string != null && "Toast".equals(string)) {
			return TOAST_SKIN;
		} else {
			switch (rabbitEntity.getRabbitType()) {
				case 0:
				default:
					return BROWN_SKIN;
				case 1:
					return WHITE_SKIN;
				case 2:
					return BLACK_SKIN;
				case 3:
					return WHITE_SPOTTED_SKIN;
				case 4:
					return GOLD_SKIN;
				case 5:
					return SALT_SKIN;
				case 99:
					return CAERBANNOG_SKIN;
			}
		}
	}
}
