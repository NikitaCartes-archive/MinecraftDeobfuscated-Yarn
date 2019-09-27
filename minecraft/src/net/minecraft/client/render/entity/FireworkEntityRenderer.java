package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	private final ItemRenderer itemRenderer;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3968(FireworkEntity fireworkEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22907(Vector3f.field_20705.method_23214(-this.renderManager.cameraYaw, true));
		arg.method_22907(Vector3f.field_20703.method_23214((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, true));
		if (fireworkEntity.wasShotAtAngle()) {
			arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
		} else {
			arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
		}

		this.itemRenderer.method_23178(fireworkEntity.getStack(), ModelTransformation.Type.GROUND, fireworkEntity.getLightmapCoordinates(), arg, arg2);
		arg.method_22909();
		super.render(fireworkEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_3969(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
