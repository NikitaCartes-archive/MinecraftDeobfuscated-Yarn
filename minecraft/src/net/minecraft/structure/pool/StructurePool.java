package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class StructurePool {
	public static final StructurePool EMPTY = new StructurePool(
		new Identifier("empty"), new Identifier("empty"), ImmutableList.of(), StructurePool.Projection.RIGID
	);
	public static final StructurePool INVALID = new StructurePool(
		new Identifier("invalid"), new Identifier("invalid"), ImmutableList.of(), StructurePool.Projection.RIGID
	);
	private final Identifier id;
	private final ImmutableList<Pair<StructurePoolElement, Integer>> elementCounts;
	private final List<StructurePoolElement> elements;
	private final Identifier terminatorsId;
	private final StructurePool.Projection projection;
	private int field_18707 = Integer.MIN_VALUE;

	public StructurePool(Identifier identifier, Identifier identifier2, List<Pair<StructurePoolElement, Integer>> list, StructurePool.Projection projection) {
		this.id = identifier;
		this.elementCounts = ImmutableList.copyOf(list);
		this.elements = Lists.<StructurePoolElement>newArrayList();

		for (Pair<StructurePoolElement, Integer> pair : list) {
			for (Integer integer = 0; integer < pair.getSecond(); integer = integer + 1) {
				this.elements.add(pair.getFirst().setProjection(projection));
			}
		}

		this.terminatorsId = identifier2;
		this.projection = projection;
	}

	public int method_19309(StructureManager structureManager) {
		if (this.field_18707 == Integer.MIN_VALUE) {
			this.field_18707 = this.elements
				.stream()
				.mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ORIGIN, Rotation.ROT_0).getBlockCountY())
				.max()
				.orElse(0);
		}

		return this.field_18707;
	}

	public Identifier getTerminatorsId() {
		return this.terminatorsId;
	}

	public StructurePoolElement getRandomElement(Random random) {
		return (StructurePoolElement)this.elements.get(random.nextInt(this.elements.size()));
	}

	public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
		return ImmutableList.copyOf(ObjectArrays.shuffle(this.elements.toArray(new StructurePoolElement[0]), random));
	}

	public Identifier getId() {
		return this.id;
	}

	public int getElementCount() {
		return this.elements.size();
	}

	public static enum Projection {
		TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.field_13194, -1))),
		RIGID("rigid", ImmutableList.of());

		private static final Map<String, StructurePool.Projection> PROJECTIONS_BY_ID = (Map<String, StructurePool.Projection>)Arrays.stream(values())
			.collect(Collectors.toMap(StructurePool.Projection::getId, projection -> projection));
		private final String id;
		private final ImmutableList<StructureProcessor> processors;

		private Projection(String string2, ImmutableList<StructureProcessor> immutableList) {
			this.id = string2;
			this.processors = immutableList;
		}

		public String getId() {
			return this.id;
		}

		public static StructurePool.Projection getById(String string) {
			return (StructurePool.Projection)PROJECTIONS_BY_ID.get(string);
		}

		public ImmutableList<StructureProcessor> getProcessors() {
			return this.processors;
		}
	}
}
