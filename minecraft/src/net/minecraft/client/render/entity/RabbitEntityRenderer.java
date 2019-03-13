package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends MobEntityRenderer<RabbitEntity, RabbitEntityModel<RabbitEntity>> {
	private static final Identifier field_4770 = new Identifier("textures/entity/rabbit/brown.png");
	private static final Identifier field_4773 = new Identifier("textures/entity/rabbit/white.png");
	private static final Identifier field_4775 = new Identifier("textures/entity/rabbit/black.png");
	private static final Identifier field_4768 = new Identifier("textures/entity/rabbit/gold.png");
	private static final Identifier field_4774 = new Identifier("textures/entity/rabbit/salt.png");
	private static final Identifier field_4772 = new Identifier("textures/entity/rabbit/white_splotched.png");
	private static final Identifier field_4771 = new Identifier("textures/entity/rabbit/toast.png");
	private static final Identifier field_4769 = new Identifier("textures/entity/rabbit/caerbannog.png");

	public RabbitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new RabbitEntityModel<>(), 0.3F);
	}

	protected Identifier method_4102(RabbitEntity rabbitEntity) {
		String string = TextFormat.stripFormatting(rabbitEntity.method_5477().getString());
		if (string != null && "Toast".equals(string)) {
			return field_4771;
		} else {
			switch (rabbitEntity.getRabbitType()) {
				case 0:
				default:
					return field_4770;
				case 1:
					return field_4773;
				case 2:
					return field_4775;
				case 3:
					return field_4772;
				case 4:
					return field_4768;
				case 5:
					return field_4774;
				case 99:
					return field_4769;
			}
		}
	}
}
