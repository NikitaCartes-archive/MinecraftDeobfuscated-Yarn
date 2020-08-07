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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePool {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("name").forGetter(StructurePool::getId),
					Identifier.CODEC.fieldOf("fallback").forGetter(StructurePool::getTerminatorsId),
					Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.INT.fieldOf("weight"))
						.codec()
						.listOf()
						.promotePartial(Util.method_29188("Pool element: ", LOGGER::error))
						.fieldOf("elements")
						.forGetter(structurePool -> structurePool.elementCounts)
				)
				.apply(instance, StructurePool::new)
	);
	public static final Codec<Supplier<StructurePool>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.field_25917, CODEC);
	private final Identifier id;
	private final List<Pair<StructurePoolElement, Integer>> elementCounts;
	private final List<StructurePoolElement> elements;
	private final Identifier terminatorsId;
	private int highestY = Integer.MIN_VALUE;

	public StructurePool(Identifier identifier, Identifier identifier2, List<Pair<StructurePoolElement, Integer>> list) {
		this.id = identifier;
		this.elementCounts = list;
		this.elements = Lists.<StructurePoolElement>newArrayList();

		for (Pair<StructurePoolElement, Integer> pair : list) {
			StructurePoolElement structurePoolElement = pair.getFirst();

			for (int i = 0; i < pair.getSecond(); i++) {
				this.elements.add(structurePoolElement);
			}
		}

		this.terminatorsId = identifier2;
	}

	public StructurePool(
		Identifier identifier,
		Identifier identifier2,
		List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> list,
		StructurePool.Projection projection
	) {
		this.id = identifier;
		this.elementCounts = Lists.<Pair<StructurePoolElement, Integer>>newArrayList();
		this.elements = Lists.<StructurePoolElement>newArrayList();

		for (Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer> pair : list) {
			StructurePoolElement structurePoolElement = (StructurePoolElement)pair.getFirst().apply(projection);
			this.elementCounts.add(Pair.of(structurePoolElement, pair.getSecond()));

			for (int i = 0; i < pair.getSecond(); i++) {
				this.elements.add(structurePoolElement);
			}
		}

		this.terminatorsId = identifier2;
	}

	public int getHighestY(StructureManager structureManager) {
		if (this.highestY == Integer.MIN_VALUE) {
			this.highestY = this.elements
				.stream()
				.mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ORIGIN, BlockRotation.field_11467).getBlockCountY())
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
		field_16686("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.field_13194, -1))),
		field_16687("rigid", ImmutableList.of());

		public static final Codec<StructurePool.Projection> field_24956 = StringIdentifiable.createCodec(
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
