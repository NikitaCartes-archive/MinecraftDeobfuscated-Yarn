package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DonkeyEntityRenderer<T extends AbstractDonkeyEntity> extends HorseBaseEntityRenderer<T, DonkeyEntityModel<T>> {
	private static final Map<Class<?>, Identifier> TEXTURES = Maps.<Class<?>, Identifier>newHashMap(
		ImmutableMap.of(DonkeyEntity.class, new Identifier("textures/entity/horse/donkey.png"), MuleEntity.class, new Identifier("textures/entity/horse/mule.png"))
	);

	public DonkeyEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher, new DonkeyEntityModel<>(0.0F), f);
	}

	protected Identifier getTexture(T abstractDonkeyEntity) {
		return (Identifier)TEXTURES.get(abstractDonkeyEntity.getClass());
	}
}
