package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
	private static final Map<EntityType<?>, Identifier> TEXTURES = ImmutableMap.of(
		EntityType.PIGLIN,
		new Identifier("textures/entity/piglin/piglin.png"),
		EntityType.ZOMBIFIED_PIGLIN,
		new Identifier("textures/entity/piglin/zombified_piglin.png"),
		EntityType.PIGLIN_BRUTE,
		new Identifier("textures/entity/piglin/piglin_brute.png")
	);

	public PiglinEntityRenderer(
		class_5617.class_5618 arg, EntityModelLayer mainLayer, EntityModelLayer innerArmorLayer, EntityModelLayer outerArmorLayer, boolean bl
	) {
		super(arg, getPiglinModel(arg.method_32170(), mainLayer, bl), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
		this.addFeature(
			new ArmorFeatureRenderer<>(this, new BipedEntityModel(arg.method_32167(innerArmorLayer)), new BipedEntityModel(arg.method_32167(outerArmorLayer)))
		);
	}

	private static PiglinEntityModel<MobEntity> getPiglinModel(class_5599 arg, EntityModelLayer entityModelLayer, boolean bl) {
		PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<>(arg.method_32072(entityModelLayer));
		if (bl) {
			piglinEntityModel.field_27464.visible = false;
		}

		return piglinEntityModel;
	}

	@Override
	public Identifier getTexture(MobEntity mobEntity) {
		Identifier identifier = (Identifier)TEXTURES.get(mobEntity.getType());
		if (identifier == null) {
			throw new IllegalArgumentException("I don't know what texture to use for " + mobEntity.getType());
		} else {
			return identifier;
		}
	}

	protected boolean isShaking(MobEntity mobEntity) {
		return mobEntity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)mobEntity).shouldZombify();
	}
}
