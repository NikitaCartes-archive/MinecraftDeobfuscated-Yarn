package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePool {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("name").forGetter(StructurePool::getId),
					Identifier.CODEC.fieldOf("fallback").forGetter(StructurePool::getTerminatorsId),
					Codec.mapPair(StructurePoolElement.field_24953.fieldOf("element"), Codec.INT.fieldOf("weight"))
						.codec()
						.listOf()
						.promotePartial(Util.method_29188("Pool element: ", LOGGER::error))
						.fieldOf("elements")
						.forGetter(structurePool -> structurePool.elementCounts),
					StructurePool.Projection.field_24956.fieldOf("projection").forGetter(structurePool -> structurePool.projection)
				)
				.apply(instance, StructurePool::new)
	);
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
	private int highestY = Integer.MIN_VALUE;

	public StructurePool(Identifier id, Identifier terminatorsId, List<Pair<StructurePoolElement, Integer>> elementCounts, StructurePool.Projection projection) {
		this.id = id;
		this.elementCounts = ImmutableList.copyOf(elementCounts);
		this.elements = Lists.<StructurePoolElement>newArrayList();

		for (Pair<StructurePoolElement, Integer> pair : elementCounts) {
			for (int i = 0; i < pair.getSecond(); i++) {
				this.elements.add(pair.getFirst().setProjection(projection));
			}
		}

		this.terminatorsId = terminatorsId;
		this.projection = projection;
	}

	public int getHighestY(StructureManager structureManager) {
		if (this.highestY == Integer.MIN_VALUE) {
			this.highestY = this.elements
				.stream()
				.mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY())
				.max()
				.orElse(0);
		}

		return this.highestY;
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

	public static enum Projection implements StringIdentifiable {
		TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
		RIGID("rigid", ImmutableList.of());

		public static final Codec<StructurePool.Projection> field_24956 = StringIdentifiable.method_28140(
			StructurePool.Projection::values, StructurePool.Projection::getById
		);
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

		public static StructurePool.Projection getById(String id) {
			return (StructurePool.Projection)PROJECTIONS_BY_ID.get(id);
		}

		public ImmutableList<StructureProcessor> getProcessors() {
			return this.processors;
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
