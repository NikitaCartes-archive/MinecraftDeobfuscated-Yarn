package net.minecraft.client.render.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class ModelGrouper {
	static final int field_53673 = -1;
	private static final int field_53674 = 0;

	public static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.BlockStateDefinition definition) {
		Map<Block, List<Property<?>>> map = new HashMap();
		Map<ModelGrouper.GroupKey, Set<BlockState>> map2 = new HashMap();
		definition.models().forEach((modelId, model) -> {
			List<Property<?>> list = (List<Property<?>>)map.computeIfAbsent(model.state().getBlock(), block -> List.copyOf(colors.getProperties(block)));
			ModelGrouper.GroupKey groupKey = ModelGrouper.GroupKey.of(model.state(), model.model(), list);
			((Set)map2.computeIfAbsent(groupKey, key -> Sets.newIdentityHashSet())).add(model.state());
		});
		int i = 1;
		Object2IntMap<BlockState> object2IntMap = new Object2IntOpenHashMap<>();
		object2IntMap.defaultReturnValue(-1);

		for (Set<BlockState> set : map2.values()) {
			Iterator<BlockState> iterator = set.iterator();

			while (iterator.hasNext()) {
				BlockState blockState = (BlockState)iterator.next();
				if (blockState.getRenderType() != BlockRenderType.MODEL) {
					iterator.remove();
					object2IntMap.put(blockState, 0);
				}
			}

			if (set.size() > 1) {
				int j = i++;
				set.forEach(state -> object2IntMap.put(state, j));
			}
		}

		return object2IntMap;
	}

	@Environment(EnvType.CLIENT)
	static record GroupKey(Object equalityGroup, List<Object> coloringValues) {
		public static ModelGrouper.GroupKey of(BlockState state, UnbakedModel model, List<Property<?>> properties) {
			List<Object> list = getColoringValues(state, properties);
			Object object = model instanceof GroupableModel groupableModel ? groupableModel.getEqualityGroup(state) : model;
			return new ModelGrouper.GroupKey(object, list);
		}

		private static List<Object> getColoringValues(BlockState state, List<Property<?>> properties) {
			Object[] objects = new Object[properties.size()];

			for (int i = 0; i < properties.size(); i++) {
				objects[i] = state.get((Property)properties.get(i));
			}

			return List.of(objects);
		}
	}
}
