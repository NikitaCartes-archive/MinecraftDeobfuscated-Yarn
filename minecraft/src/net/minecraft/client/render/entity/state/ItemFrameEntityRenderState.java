package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderState extends EntityRenderState {
	public Direction facing = Direction.NORTH;
	public ItemStack contents = ItemStack.EMPTY;
	public int rotation;
	public boolean glow;
	@Nullable
	public BakedModel itemModel;
	@Nullable
	public MapIdComponent mapId;
	public final MapRenderState mapRenderState = new MapRenderState();
}
