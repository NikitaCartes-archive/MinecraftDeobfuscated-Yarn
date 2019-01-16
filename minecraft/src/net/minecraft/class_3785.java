package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.sortme.structures.processor.AbstractStructureProcessor;
import net.minecraft.sortme.structures.processor.GravityStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.Heightmap;

public class class_3785 {
	public static final class_3785 field_16679 = new class_3785(new Identifier("empty"), new Identifier("empty"), ImmutableList.of(), class_3785.Projection.RIGID);
	public static final class_3785 field_16746 = new class_3785(
		new Identifier("invalid"), new Identifier("invalid"), ImmutableList.of(), class_3785.Projection.RIGID
	);
	private final Identifier field_16678;
	private final ImmutableList<Pair<class_3784, Integer>> field_16864;
	private final List<class_3784> field_16680;
	private final Identifier field_16681;
	private final class_3785.Projection field_16863;

	public class_3785(Identifier identifier, Identifier identifier2, List<Pair<class_3784, Integer>> list, class_3785.Projection projection) {
		this.field_16678 = identifier;
		this.field_16864 = ImmutableList.copyOf(list);
		this.field_16680 = Lists.<class_3784>newArrayList();

		for (Pair<class_3784, Integer> pair : list) {
			for (Integer integer = 0; integer < pair.getSecond(); integer = integer + 1) {
				this.field_16680.add(pair.getFirst().method_16622(projection));
			}
		}

		this.field_16681 = identifier2;
		this.field_16863 = projection;
	}

	public class_3784 method_16630(int i) {
		return (class_3784)this.field_16680.get(i);
	}

	public Identifier method_16634() {
		return this.field_16681;
	}

	public class_3784 method_16631(Random random) {
		return (class_3784)this.field_16680.get(random.nextInt(this.field_16680.size()));
	}

	public int[] method_16633(Random random) {
		int[] is = new int[this.field_16680.size()];
		int i = 0;

		while (i < is.length) {
			is[i] = i++;
		}

		IntArrays.shuffle(is, random);
		return is;
	}

	public Identifier method_16629() {
		return this.field_16678;
	}

	public int method_16632() {
		return this.field_16680.size();
	}

	public static enum Projection {
		TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
		RIGID("rigid", ImmutableList.of());

		private static final Map<String, class_3785.Projection> field_16684 = (Map<String, class_3785.Projection>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3785.Projection::method_16635, projection -> projection));
		private final String field_16682;
		private final ImmutableList<AbstractStructureProcessor> field_16685;

		private Projection(String string2, ImmutableList<AbstractStructureProcessor> immutableList) {
			this.field_16682 = string2;
			this.field_16685 = immutableList;
		}

		public String method_16635() {
			return this.field_16682;
		}

		public static class_3785.Projection method_16638(String string) {
			return (class_3785.Projection)field_16684.get(string);
		}

		public ImmutableList<AbstractStructureProcessor> method_16636() {
			return this.field_16685;
		}
	}
}
