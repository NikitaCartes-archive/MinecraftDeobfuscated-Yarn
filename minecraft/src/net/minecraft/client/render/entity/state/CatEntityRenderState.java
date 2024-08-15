package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CatEntityRenderState extends FelineEntityRenderState {
	private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/cat/tabby.png");
	public Identifier texture = DEFAULT_TEXTURE;
	public boolean nearSleepingPlayer;
	@Nullable
	public DyeColor collarColor;
}
