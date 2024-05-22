package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
	private static final Map<EntityType<?>, Identifier> TEXTURES = ImmutableMap.of(
		EntityType.PIGLIN,
		Identifier.ofVanilla("textures/entity/piglin/piglin.png"),
		EntityType.ZOMBIFIED_PIGLIN,
		Identifier.ofVanilla("textures/entity/piglin/zombified_piglin.png"),
		EntityType.PIGLIN_BRUTE,
		Identifier.ofVanilla("textures/entity/piglin/piglin_brute.png")
	);
	private static final float HORIZONTAL_SCALE = 1.0019531F;

	public PiglinEntityRenderer(
		EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer innerArmorLayer, EntityModelLayer outerArmorLayer, boolean zombie
	) {
		super(ctx, getPiglinModel(ctx.getModelLoader(), mainLayer, zombie), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this, new ArmorEntityModel(ctx.getPart(innerArmorLayer)), new ArmorEntityModel(ctx.getPart(outerArmorLayer)), ctx.getModelManager()
			)
		);
	}

	private static PiglinEntityModel<MobEntity> getPiglinModel(EntityModelLoader modelLoader, EntityModelLayer layer, boolean zombie) {
		PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<>(modelLoader.getModelPart(layer));
		if (zombie) {
			piglinEntityModel.rightEar.visible = false;
		}

		return piglinEntityModel;
	}

	public Identifier getTexture(MobEntity mobEntity) {
		Identifier identifier = (Identifier)TEXTURES.get(mobEntity.getType());
		if (identifier == null) {
			throw new IllegalArgumentException("I don't know what texture to use for " + mobEntity.getType());
		} else {
			return identifier;
		}
	}

	protected boolean isShaking(MobEntity mobEntity) {
		return super.isShaking(mobEntity) || mobEntity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)mobEntity).shouldZombify();
	}
}
