package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class MultipartBakedModel extends WrapperBakedModel {
	private final List<MultipartBakedModel.class_10204> components;
	private final Map<BlockState, BitSet> stateCache = new Reference2ObjectOpenHashMap<>();

	private static BakedModel method_64095(List<MultipartBakedModel.class_10204> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Model must have at least one selector");
		} else {
			return ((MultipartBakedModel.class_10204)list.getFirst()).model();
		}
	}

	public MultipartBakedModel(List<MultipartBakedModel.class_10204> components) {
		super(method_64095(components));
		this.components = components;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		if (state == null) {
			return Collections.emptyList();
		} else {
			BitSet bitSet = (BitSet)this.stateCache.get(state);
			if (bitSet == null) {
				bitSet = new BitSet();

				for (int i = 0; i < this.components.size(); i++) {
					if (((MultipartBakedModel.class_10204)this.components.get(i)).condition.test(state)) {
						bitSet.set(i);
					}
				}

				this.stateCache.put(state, bitSet);
			}

			List<BakedQuad> list = new ArrayList();
			long l = random.nextLong();

			for (int j = 0; j < bitSet.length(); j++) {
				if (bitSet.get(j)) {
					random.setSeed(l);
					list.addAll(((MultipartBakedModel.class_10204)this.components.get(j)).model.getQuads(state, face, random));
				}
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record class_10204(Predicate<BlockState> condition, BakedModel model) {
	}
}
