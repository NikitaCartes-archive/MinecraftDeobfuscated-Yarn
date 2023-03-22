package com.mojang.blaze3d.systems;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public interface VertexSorter {
	VertexSorter BY_DISTANCE = byDistance(0.0F, 0.0F, 0.0F);
	VertexSorter BY_Z = of(vec -> -vec.z());

	static VertexSorter byDistance(float originX, float originY, float originZ) {
		return byDistance(new Vector3f(originX, originY, originZ));
	}

	static VertexSorter byDistance(Vector3f origin) {
		return of(origin::distanceSquared);
	}

	static VertexSorter of(VertexSorter.SortKeyMapper mapper) {
		return vec -> {
			float[] fs = new float[vec.length];
			int[] is = new int[vec.length];

			for (int i = 0; i < vec.length; is[i] = i++) {
				fs[i] = mapper.apply(vec[i]);
			}

			IntArrays.mergeSort(is, (a, b) -> Floats.compare(fs[b], fs[a]));
			return is;
		};
	}

	int[] sort(Vector3f[] vec);

	@Environment(EnvType.CLIENT)
	public interface SortKeyMapper {
		float apply(Vector3f vec);
	}
}
