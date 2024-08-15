package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderState extends BipedEntityRenderState {
	public boolean angry;
	@Nullable
	public BlockState carriedBlock;
}
