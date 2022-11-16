package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructurePool {
	private static final int DEFAULT_Y = Integer.MIN_VALUE;
	private static final MutableObject<Codec<RegistryEntry<StructurePool>>> FALLBACK = new MutableObject<>();
	public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createLazy(FALLBACK::getValue).fieldOf("fallback").forGetter(StructurePool::getFallback),
					Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight"))
						.codec()
						.listOf()
						.fieldOf("elements")
						.forGetter(pool -> pool.elementCounts)
				)
				.apply(instance, StructurePool::new)
	);
	public static final Codec<RegistryEntry<StructurePool>> REGISTRY_CODEC = Util.make(
		RegistryElementCodec.of(RegistryKeys.TEMPLATE_POOL, CODEC), FALLBACK::setValue
	);
	private final List<Pair<StructurePoolElement, Integer>> elementCounts;
	private final ObjectArrayList<StructurePoolElement> elements;
	private final RegistryEntry<StructurePool> fallback;
	private int highestY = Integer.MIN_VALUE;

	public StructurePool(RegistryEntry<StructurePool> fallback, List<Pair<StructurePoolElement, Integer>> elementCounts) {
		this.elementCounts = elementCounts;
		this.elements = new ObjectArrayList<>();

		for (Pair<StructurePoolElement, Integer> pair : elementCounts) {
			StructurePoolElement structurePoolElement = pair.getFirst();

			for (int i = 0; i < pair.getSecond(); i++) {
				this.elements.add(structurePoolElement);
			}
		}

		this.fallback = fallback;
	}

	public StructurePool(
		RegistryEntry<StructurePool> fallback,
		List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> elementCountsByGetters,
		StructurePool.Projection projection
	) {
		this.elementCounts = Lists.<Pair<StructurePoolElement, Integer>>newArrayList();
		this.elements = new ObjectArrayList<>();

		for (Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer> pair : elementCountsByGetters) {
			StructurePoolElement structurePoolElement = (StructurePoolElement)pair.getFirst().apply(projection);
			this.elementCounts.add(Pair.of(structurePoolElement, pair.getSecond()));

			for (int i = 0; i < pair.getSecond(); i++) {
				this.elements.add(structurePoolElement);
			}
		}

		this.fallback = fallback;
	}

	public int getHighestY(StructureTemplateManager structureTemplateManager) {
		if (this.highestY == Integer.MIN_VALUE) {
			this.highestY = this.elements
				.stream()
				.filter(element -> element != EmptyPoolElement.INSTANCE)
				.mapToInt(element -> element.getBoundingBox(structureTemplateManager, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY())
				.max()
				.orElse(0);
		}

		return this.highestY;
	}

	public RegistryEntry<StructurePool> getFallback() {
		return this.fallback;
	}

	public StructurePoolElement getRandomElement(Random random) {
		return this.elements.get(random.nextInt(this.elements.size()));
	}

	public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
		return Util.copyShuffled(this.elements, random);
	}

	public int getElementCount() {
		return this.elements.size();
	}

	public static enum Projection implements StringIdentifiable {
		TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
		RIGID("rigid", ImmutableList.of());

		public static final StringIdentifiable.Codec<StructurePool.Projection> CODEC = StringIdentifiable.createCodec(StructurePool.Projection::values);
		private final String id;
		private final ImmutableList<StructureProcessor> processors;

		private Projection(String id, ImmutableList<StructureProcessor> processors) {
			this.id = id;
			this.processors = processors;
		}

		public String getId() {
			return this.id;
		}

		public static StructurePool.Projection getById(String id) {
			return (StructurePool.Projection)CODEC.byId(id);
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
