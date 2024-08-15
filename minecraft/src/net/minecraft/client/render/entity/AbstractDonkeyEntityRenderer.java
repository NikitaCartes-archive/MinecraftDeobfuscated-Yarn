package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AbstractDonkeyEntityRenderer<T extends AbstractDonkeyEntity> extends AbstractHorseEntityRenderer<T, DonkeyEntityRenderState, DonkeyEntityModel> {
	public static final Identifier DONKEY_TEXTURE = Identifier.ofVanilla("textures/entity/horse/donkey.png");
	public static final Identifier MULE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/mule.png");
	private final Identifier texture;

	public AbstractDonkeyEntityRenderer(EntityRendererFactory.Context context, float scale, EntityModelLayer layer, EntityModelLayer babyLayer, boolean mule) {
		super(context, new DonkeyEntityModel(context.getPart(layer)), new DonkeyEntityModel(context.getPart(babyLayer)), scale);
		this.texture = mule ? MULE_TEXTURE : DONKEY_TEXTURE;
	}

	public Identifier getTexture(DonkeyEntityRenderState donkeyEntityRenderState) {
		return this.texture;
	}

	public DonkeyEntityRenderState getRenderState() {
		return new DonkeyEntityRenderState();
	}

	public void updateRenderState(T abstractDonkeyEntity, DonkeyEntityRenderState donkeyEntityRenderState, float f) {
		super.updateRenderState(abstractDonkeyEntity, donkeyEntityRenderState, f);
		donkeyEntityRenderState.hasChest = abstractDonkeyEntity.hasChest();
	}
}
