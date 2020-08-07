package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
	private static final Map<EntityType<?>, Identifier> field_25793 = ImmutableMap.of(
		EntityType.field_22281,
		new Identifier("textures/entity/piglin/piglin.png"),
		EntityType.field_6050,
		new Identifier("textures/entity/piglin/zombified_piglin.png"),
		EntityType.field_25751,
		new Identifier("textures/entity/piglin/piglin_brute.png")
	);

	public PiglinEntityRenderer(EntityRenderDispatcher dispatcher, boolean zombified) {
		super(dispatcher, getPiglinModel(zombified), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
		this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.02F)));
	}

	private static PiglinEntityModel<MobEntity> getPiglinModel(boolean zombified) {
		PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<>(0.0F, 64, 64);
		if (zombified) {
			piglinEntityModel.leftEar.visible = false;
		}

		return piglinEntityModel;
	}

	@Override
	public Identifier method_3982(MobEntity mobEntity) {
		Identifier identifier = (Identifier)field_25793.get(mobEntity.getType());
		if (identifier == null) {
			throw new IllegalArgumentException("I don't know what texture to use for " + mobEntity.getType());
		} else {
			return identifier;
		}
	}

	protected boolean method_25451(MobEntity mobEntity) {
		return mobEntity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)mobEntity).shouldZombify();
	}
}
