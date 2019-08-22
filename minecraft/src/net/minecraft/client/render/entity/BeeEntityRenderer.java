package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeeEntityRenderer extends MobEntityRenderer<BeeEntity, BeeEntityModel<BeeEntity>> {
	private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/bee/bee_angry.png");
	private static final Identifier ANGRY_NECTAR_SKIN = new Identifier("textures/entity/bee/bee_angry_nectar.png");
	private static final Identifier PASSIVE_SKIN = new Identifier("textures/entity/bee/bee.png");
	private static final Identifier NECTAR_SKIN = new Identifier("textures/entity/bee/bee_nectar.png");

	public BeeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BeeEntityModel<>(), 0.4F);
	}

	@Nullable
	protected Identifier method_22129(BeeEntity beeEntity) {
		if (beeEntity.isAngry()) {
			return beeEntity.hasNectar() ? ANGRY_NECTAR_SKIN : ANGRY_SKIN;
		} else {
			return beeEntity.hasNectar() ? NECTAR_SKIN : PASSIVE_SKIN;
		}
	}
}
