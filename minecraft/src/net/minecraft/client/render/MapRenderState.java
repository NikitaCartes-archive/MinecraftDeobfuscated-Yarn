package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MapRenderState {
	@Nullable
	public Identifier texture;
	public final List<MapRenderState.Decoration> decorations = new ArrayList();

	@Environment(EnvType.CLIENT)
	public static class Decoration {
		@Nullable
		public Sprite sprite;
		public byte x;
		public byte z;
		public byte rotation;
		public boolean alwaysRendered;
		@Nullable
		public Text name;
	}
}
