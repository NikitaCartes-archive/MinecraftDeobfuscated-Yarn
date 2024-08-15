package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;

@Environment(EnvType.CLIENT)
public class TntEntityRenderState extends EntityRenderState {
	public float fuse;
	@Nullable
	public BlockState blockState;
}
