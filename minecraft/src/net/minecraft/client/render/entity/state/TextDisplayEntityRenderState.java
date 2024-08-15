package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.DisplayEntity;

@Environment(EnvType.CLIENT)
public class TextDisplayEntityRenderState extends DisplayEntityRenderState {
	@Nullable
	public DisplayEntity.TextDisplayEntity.Data data;
	@Nullable
	public DisplayEntity.TextDisplayEntity.TextLines textLines;

	@Override
	public boolean canRender() {
		return this.data != null;
	}
}
