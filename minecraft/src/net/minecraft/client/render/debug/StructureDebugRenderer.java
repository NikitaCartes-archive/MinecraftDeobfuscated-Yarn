package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4624;
	private final Map<DimensionType, Map<String, BlockBox>> field_4626 = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, BlockBox>> field_4627 = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.<DimensionType, Map<String, Boolean>>newIdentityHashMap();

	public StructureDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4624 = minecraftClient;
	}

	public void method_3871(BlockBox blockBox, List<BlockBox> list, List<Boolean> list2, DimensionType dimensionType) {
		if (!this.field_4626.containsKey(dimensionType)) {
			this.field_4626.put(dimensionType, Maps.newHashMap());
		}

		if (!this.field_4627.containsKey(dimensionType)) {
			this.field_4627.put(dimensionType, Maps.newHashMap());
			this.field_4625.put(dimensionType, Maps.newHashMap());
		}

		((Map)this.field_4626.get(dimensionType)).put(blockBox.toString(), blockBox);

		for (int i = 0; i < list.size(); i++) {
			BlockBox blockBox2 = (BlockBox)list.get(i);
			Boolean boolean_ = (Boolean)list2.get(i);
			((Map)this.field_4627.get(dimensionType)).put(blockBox2.toString(), blockBox2);
			((Map)this.field_4625.get(dimensionType)).put(blockBox2.toString(), boolean_);
		}
	}

	@Override
	public void clear() {
		this.field_4626.clear();
		this.field_4627.clear();
		this.field_4625.clear();
	}
}
