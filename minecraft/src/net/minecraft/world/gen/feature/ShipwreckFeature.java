package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class ShipwreckFeature extends StructureFeature {
	public static final Codec<ShipwreckFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance)
				.and(Codec.BOOL.fieldOf("is_beached").forGetter(shipwreckFeature -> shipwreckFeature.field_37816))
				.apply(instance, ShipwreckFeature::new)
	);
	public final boolean field_37816;

	public ShipwreckFeature(
		RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl, boolean bl2
	) {
		super(registryEntryList, map, feature, bl);
		this.field_37816 = bl2;
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		Heightmap.Type type = this.field_37816 ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
		return method_41612(arg, type, structurePiecesCollector -> this.addPieces(structurePiecesCollector, arg));
	}

	private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
		BlockRotation blockRotation = BlockRotation.random(arg.random());
		BlockPos blockPos = new BlockPos(arg.chunkPos().getStartX(), 90, arg.chunkPos().getStartZ());
		ShipwreckGenerator.addParts(arg.structureTemplateManager(), blockPos, blockRotation, structurePiecesCollector, arg.random(), this.field_37816);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.SHIPWRECK;
	}
}
