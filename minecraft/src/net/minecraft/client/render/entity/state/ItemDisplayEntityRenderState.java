package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.decoration.DisplayEntity;

@Environment(EnvType.CLIENT)
public class ItemDisplayEntityRenderState extends DisplayEntityRenderState {
	@Nullable
	public DisplayEntity.ItemDisplayEntity.Data data;
	@Nullable
	public BakedModel model;

	@Override
	public boolean canRender() {
		return this.data != null && this.model != null;
	}
}
