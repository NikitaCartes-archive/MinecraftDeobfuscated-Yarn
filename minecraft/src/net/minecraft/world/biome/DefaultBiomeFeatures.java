package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.class_4631;
import net.minecraft.class_4632;
import net.minecraft.class_4633;
import net.minecraft.class_4634;
import net.minecraft.class_4635;
import net.minecraft.class_4636;
import net.minecraft.class_4638;
import net.minecraft.class_4640;
import net.minecraft.class_4642;
import net.minecraft.class_4643;
import net.minecraft.class_4645;
import net.minecraft.class_4646;
import net.minecraft.class_4649;
import net.minecraft.class_4650;
import net.minecraft.class_4653;
import net.minecraft.class_4654;
import net.minecraft.class_4655;
import net.minecraft.class_4656;
import net.minecraft.class_4657;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class DefaultBiomeFeatures {
	private static final BlockState field_21144 = Blocks.GRASS.getDefaultState();
	private static final BlockState field_21145 = Blocks.FERN.getDefaultState();
	private static final BlockState field_21146 = Blocks.PODZOL.getDefaultState();
	private static final BlockState field_21147 = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState field_21148 = Blocks.OAK_LEAVES.getDefaultState();
	private static final BlockState field_21149 = Blocks.JUNGLE_LOG.getDefaultState();
	private static final BlockState field_21150 = Blocks.JUNGLE_LEAVES.getDefaultState();
	private static final BlockState field_21151 = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState field_21152 = Blocks.SPRUCE_LEAVES.getDefaultState();
	private static final BlockState field_21153 = Blocks.ACACIA_LOG.getDefaultState();
	private static final BlockState field_21154 = Blocks.ACACIA_LEAVES.getDefaultState();
	private static final BlockState field_21155 = Blocks.BIRCH_LOG.getDefaultState();
	private static final BlockState field_21156 = Blocks.BIRCH_LEAVES.getDefaultState();
	private static final BlockState field_21157 = Blocks.DARK_OAK_LOG.getDefaultState();
	private static final BlockState field_21158 = Blocks.DARK_OAK_LEAVES.getDefaultState();
	private static final BlockState field_21159 = Blocks.WATER.getDefaultState();
	private static final BlockState field_21160 = Blocks.LAVA.getDefaultState();
	private static final BlockState field_21161 = Blocks.DIRT.getDefaultState();
	private static final BlockState field_21162 = Blocks.GRAVEL.getDefaultState();
	private static final BlockState field_21163 = Blocks.GRANITE.getDefaultState();
	private static final BlockState field_21164 = Blocks.DIORITE.getDefaultState();
	private static final BlockState field_21165 = Blocks.ANDESITE.getDefaultState();
	private static final BlockState field_21166 = Blocks.COAL_ORE.getDefaultState();
	private static final BlockState field_21114 = Blocks.IRON_ORE.getDefaultState();
	private static final BlockState field_21115 = Blocks.GOLD_ORE.getDefaultState();
	private static final BlockState field_21116 = Blocks.REDSTONE_ORE.getDefaultState();
	private static final BlockState field_21117 = Blocks.DIAMOND_ORE.getDefaultState();
	private static final BlockState field_21118 = Blocks.LAPIS_ORE.getDefaultState();
	private static final BlockState field_21119 = Blocks.STONE.getDefaultState();
	private static final BlockState field_21120 = Blocks.EMERALD_ORE.getDefaultState();
	private static final BlockState field_21121 = Blocks.INFESTED_STONE.getDefaultState();
	private static final BlockState field_21122 = Blocks.SAND.getDefaultState();
	private static final BlockState field_21123 = Blocks.CLAY.getDefaultState();
	private static final BlockState field_21124 = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState field_21125 = Blocks.MOSSY_COBBLESTONE.getDefaultState();
	private static final BlockState field_21127 = Blocks.LARGE_FERN.getDefaultState();
	private static final BlockState field_21128 = Blocks.TALL_GRASS.getDefaultState();
	private static final BlockState field_21129 = Blocks.LILAC.getDefaultState();
	private static final BlockState field_21130 = Blocks.ROSE_BUSH.getDefaultState();
	private static final BlockState field_21131 = Blocks.PEONY.getDefaultState();
	private static final BlockState field_21132 = Blocks.BROWN_MUSHROOM.getDefaultState();
	private static final BlockState field_21133 = Blocks.RED_MUSHROOM.getDefaultState();
	private static final BlockState field_21134 = Blocks.SEAGRASS.getDefaultState();
	private static final BlockState field_21135 = Blocks.PACKED_ICE.getDefaultState();
	private static final BlockState field_21136 = Blocks.BLUE_ICE.getDefaultState();
	private static final BlockState field_21137 = Blocks.LILY_OF_THE_VALLEY.getDefaultState();
	private static final BlockState field_21138 = Blocks.BLUE_ORCHID.getDefaultState();
	private static final BlockState field_21139 = Blocks.POPPY.getDefaultState();
	private static final BlockState field_21140 = Blocks.DANDELION.getDefaultState();
	private static final BlockState field_21168 = Blocks.DEAD_BUSH.getDefaultState();
	private static final BlockState field_21169 = Blocks.MELON.getDefaultState();
	private static final BlockState field_21170 = Blocks.PUMPKIN.getDefaultState();
	private static final BlockState field_21171 = Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
	private static final BlockState field_21172 = Blocks.FIRE.getDefaultState();
	private static final BlockState field_21173 = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState field_21174 = Blocks.LILY_PAD.getDefaultState();
	private static final BlockState field_21175 = Blocks.SNOW.getDefaultState();
	private static final BlockState field_21176 = Blocks.JACK_O_LANTERN.getDefaultState();
	private static final BlockState field_21177 = Blocks.SUNFLOWER.getDefaultState();
	private static final BlockState field_21178 = Blocks.CACTUS.getDefaultState();
	private static final BlockState field_21179 = Blocks.SUGAR_CANE.getDefaultState();
	private static final BlockState field_21180 = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState field_21181 = Blocks.BROWN_MUSHROOM_BLOCK
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(true))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState field_21182 = Blocks.MUSHROOM_STEM
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(false))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	public static final class_4640 field_21126 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(2, 0))
		.method_23428(4)
		.method_23430(2)
		.method_23437(3)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21167 = new class_4640.class_4641(new class_4656(field_21149), new class_4656(field_21150), new class_4646(2, 0))
		.method_23428(4)
		.method_23430(6)
		.method_23437(3)
		.method_23429(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.method_23427()
		.method_23431();
	public static final class_4640 field_21183 = new class_4640.class_4641(new class_4656(field_21149), new class_4656(field_21150), new class_4646(2, 0))
		.method_23428(4)
		.method_23430(6)
		.method_23437(3)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21184 = new class_4640.class_4641(new class_4656(field_21151), new class_4656(field_21152), new class_4649(1, 0))
		.method_23428(7)
		.method_23430(4)
		.method_23435(1)
		.method_23437(3)
		.method_23438(1)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21185 = new class_4640.class_4641(new class_4656(field_21151), new class_4656(field_21152), new class_4650(2, 1))
		.method_23428(6)
		.method_23430(3)
		.method_23433(1)
		.method_23434(1)
		.method_23436(2)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21186 = new class_4640.class_4641(new class_4656(field_21153), new class_4656(field_21154), new class_4645(2, 0))
		.method_23428(5)
		.method_23430(2)
		.method_23432(2)
		.method_23433(0)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21187 = new class_4640.class_4641(new class_4656(field_21155), new class_4656(field_21156), new class_4646(2, 0))
		.method_23428(5)
		.method_23430(2)
		.method_23437(3)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21188 = new class_4640.class_4641(new class_4656(field_21155), new class_4656(field_21156), new class_4646(2, 0))
		.method_23428(5)
		.method_23430(2)
		.method_23432(6)
		.method_23437(3)
		.method_23427()
		.method_23431();
	public static final class_4640 field_21189 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(3, 0))
		.method_23428(5)
		.method_23430(3)
		.method_23437(3)
		.method_23439(1)
		.method_23429(ImmutableList.of(new LeaveVineTreeDecorator()))
		.method_23431();
	public static final class_4640 field_21190 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(0, 0))
		.method_23431();
	public static final class_4640 field_21191 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(2, 0))
		.method_23428(4)
		.method_23430(2)
		.method_23437(3)
		.method_23427()
		.method_23429(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.method_23431();
	public static final class_4640 field_21192 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(0, 0))
		.method_23429(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.method_23431();
	public static final class_4640 field_21193 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(2, 0))
		.method_23428(4)
		.method_23430(2)
		.method_23437(3)
		.method_23427()
		.method_23429(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.method_23431();
	public static final class_4640 field_21194 = new class_4640.class_4641(new class_4656(field_21147), new class_4656(field_21148), new class_4646(0, 0))
		.method_23429(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.method_23431();
	public static final class_4640 field_21195 = new class_4640.class_4641(new class_4656(field_21155), new class_4656(field_21156), new class_4646(2, 0))
		.method_23428(5)
		.method_23430(2)
		.method_23437(3)
		.method_23427()
		.method_23429(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.method_23431();
	public static final class_4643 field_21196 = new class_4643.class_4644(new class_4656(field_21149), new class_4656(field_21148))
		.method_23446(4)
		.method_23445();
	public static final class_4636 field_21197 = new class_4636.class_4637(new class_4656(field_21157), new class_4656(field_21158))
		.method_23410(6)
		.method_23409();
	public static final class_4636 field_21198 = new class_4636.class_4637(new class_4656(field_21151), new class_4656(field_21152))
		.method_23410(13)
		.method_23412(15)
		.method_23411(ImmutableList.of(new AlterGroundTreeDecorator(new class_4656(field_21146))))
		.method_23409();
	public static final class_4636 field_21199 = new class_4636.class_4637(new class_4656(field_21151), new class_4656(field_21152))
		.method_23410(3)
		.method_23412(15)
		.method_23411(ImmutableList.of(new AlterGroundTreeDecorator(new class_4656(field_21146))))
		.method_23409();
	public static final class_4636 field_21200 = new class_4636.class_4637(new class_4656(field_21149), new class_4656(field_21150))
		.method_23410(10)
		.method_23412(20)
		.method_23411(ImmutableList.of(new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.method_23409();
	public static final class_4638 field_21201 = new class_4638.class_4639(new class_4656(field_21144), new class_4633()).method_23417(128).method_23424();
	public static final class_4638 field_21202 = new class_4638.class_4639(
			new class_4657().method_23458(field_21144, 1).method_23458(field_21145, 4), new class_4633()
		)
		.method_23417(128)
		.method_23424();
	public static final class_4638 field_21203 = new class_4638.class_4639(
			new class_4657().method_23458(field_21144, 3).method_23458(field_21145, 1), new class_4633()
		)
		.method_23421(ImmutableSet.of(field_21146))
		.method_23417(128)
		.method_23424();
	public static final class_4638 field_21204 = new class_4638.class_4639(new class_4656(field_21137), new class_4633()).method_23417(64).method_23424();
	public static final class_4638 field_21205 = new class_4638.class_4639(new class_4656(field_21138), new class_4633()).method_23417(64).method_23424();
	public static final class_4638 field_21206 = new class_4638.class_4639(
			new class_4657().method_23458(field_21139, 2).method_23458(field_21140, 1), new class_4633()
		)
		.method_23417(64)
		.method_23424();
	public static final class_4638 field_21088 = new class_4638.class_4639(new class_4654(), new class_4633()).method_23417(64).method_23424();
	public static final class_4638 field_21089 = new class_4638.class_4639(new class_4653(), new class_4633()).method_23417(64).method_23424();
	public static final class_4638 field_21090 = new class_4638.class_4639(new class_4656(field_21168), new class_4633()).method_23417(4).method_23424();
	public static final class_4638 field_21091 = new class_4638.class_4639(new class_4656(field_21169), new class_4633())
		.method_23417(64)
		.method_23418(ImmutableSet.of(field_21124.getBlock()))
		.method_23416()
		.method_23419()
		.method_23424();
	public static final class_4638 field_21092 = new class_4638.class_4639(new class_4656(field_21170), new class_4633())
		.method_23417(64)
		.method_23418(ImmutableSet.of(field_21124.getBlock()))
		.method_23419()
		.method_23424();
	public static final class_4638 field_21093 = new class_4638.class_4639(new class_4656(field_21171), new class_4633())
		.method_23417(64)
		.method_23418(ImmutableSet.of(field_21124.getBlock()))
		.method_23419()
		.method_23424();
	public static final class_4638 field_21094 = new class_4638.class_4639(new class_4656(field_21172), new class_4633())
		.method_23417(64)
		.method_23418(ImmutableSet.of(field_21173.getBlock()))
		.method_23419()
		.method_23424();
	public static final class_4638 field_21095 = new class_4638.class_4639(new class_4656(field_21174), new class_4633()).method_23417(10).method_23424();
	public static final class_4638 field_21096 = new class_4638.class_4639(new class_4656(field_21133), new class_4633())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21097 = new class_4638.class_4639(new class_4656(field_21132), new class_4633())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21098 = new class_4638.class_4639(new class_4656(field_21129), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21099 = new class_4638.class_4639(new class_4656(field_21130), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21100 = new class_4638.class_4639(new class_4656(field_21131), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21101 = new class_4638.class_4639(new class_4656(field_21177), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21102 = new class_4638.class_4639(new class_4656(field_21128), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21103 = new class_4638.class_4639(new class_4656(field_21127), new class_4632())
		.method_23417(64)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21104 = new class_4638.class_4639(new class_4656(field_21178), new class_4631(1, 2))
		.method_23417(10)
		.method_23419()
		.method_23424();
	public static final class_4638 field_21105 = new class_4638.class_4639(new class_4656(field_21179), new class_4631(2, 2))
		.method_23417(20)
		.method_23420(4)
		.method_23423(0)
		.method_23425(4)
		.method_23419()
		.method_23422()
		.method_23424();
	public static final class_4634 field_21106 = new class_4634(new class_4655(Blocks.HAY_BLOCK));
	public static final class_4634 field_21107 = new class_4634(new class_4656(field_21175));
	public static final class_4634 field_21108 = new class_4634(new class_4656(field_21169));
	public static final class_4634 field_21109 = new class_4634(new class_4657().method_23458(field_21170, 19).method_23458(field_21176, 1));
	public static final class_4634 field_21110 = new class_4634(new class_4657().method_23458(field_21136, 1).method_23458(field_21135, 5));
	public static final class_4642 field_21111 = new class_4642(
		Fluids.WATER.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
	);
	public static final class_4642 field_21112 = new class_4642(
		Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
	);
	public static final class_4642 field_21113 = new class_4642(Fluids.LAVA.getDefaultState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
	public static final class_4642 field_21141 = new class_4642(Fluids.LAVA.getDefaultState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
	public static final class_4635 field_21142 = new class_4635(new class_4656(field_21180), new class_4656(field_21182), 2);
	public static final class_4635 field_21143 = new class_4635(new class_4656(field_21181), new class_4656(field_21182), 3);

	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
	}

	public static void addDefaultStructures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.MINESHAFT
				.method_23397(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL))
				.method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.PILLAGER_OUTPOST.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.STRONGHOLD.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.SWAMP_HUT.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.DESERT_PYRAMID.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.JUNGLE_TEMPLE.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.IGLOO.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.SHIPWRECK.method_23397(new ShipwreckFeatureConfig(false)).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.OCEAN_MONUMENT.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.WOODLAND_MANSION.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.OCEAN_RUIN
				.method_23397(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F))
				.method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.BURIED_TREASURE.method_23397(new BuriedTreasureFeatureConfig(0.01F)).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.VILLAGE.method_23397(new VillageFeatureConfig("village/plains/town_centers", 6)).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.method_23397(new BushFeatureConfig(field_21159)).method_23388(Decorator.WATER_LAKE.method_23475(new LakeDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.method_23397(new BushFeatureConfig(field_21160)).method_23388(Decorator.LAVA_LAKE.method_23475(new LakeDecoratorConfig(80)))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.method_23397(new BushFeatureConfig(field_21160)).method_23388(Decorator.LAVA_LAKE.method_23475(new LakeDecoratorConfig(80)))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.MONSTER_ROOM.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.DUNGEONS.method_23475(new LakeDecoratorConfig(8)))
		);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21161, 33))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(10, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21162, 33))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(8, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21163, 33))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21164, 33))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21165, 33))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21166, 17))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(20, 0, 0, 128)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21114, 9))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(20, 0, 0, 64)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21115, 9))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(2, 0, 0, 32)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21116, 8))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(8, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21117, 8))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(1, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21118, 7))
				.method_23388(Decorator.COUNT_DEPTH_AVERAGE.method_23475(new CountDepthDecoratorConfig(1, 16, 16)))
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21115, 9))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(20, 32, 32, 80)))
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.EMERALD_ORE
				.method_23397(new EmeraldOreFeatureConfig(field_21119, field_21120))
				.method_23388(Decorator.EMERALD_ORE.method_23475(DecoratorConfig.DEFAULT))
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.ORE
				.method_23397(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, field_21121, 9))
				.method_23388(Decorator.COUNT_RANGE.method_23475(new RangeDecoratorConfig(7, 0, 0, 64)))
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.method_23397(new DiskFeatureConfig(field_21122, 7, 2, Lists.<BlockState>newArrayList(field_21161, field_21124)))
				.method_23388(Decorator.COUNT_TOP_SOLID.method_23475(new CountDecoratorConfig(3)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.method_23397(new DiskFeatureConfig(field_21123, 4, 1, Lists.<BlockState>newArrayList(field_21161, field_21123)))
				.method_23388(Decorator.COUNT_TOP_SOLID.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.method_23397(new DiskFeatureConfig(field_21162, 6, 2, Lists.<BlockState>newArrayList(field_21161, field_21124)))
				.method_23388(Decorator.COUNT_TOP_SOLID.method_23475(new CountDecoratorConfig(1)))
		);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.method_23397(new DiskFeatureConfig(field_21123, 4, 1, Lists.<BlockState>newArrayList(field_21161, field_21123)))
				.method_23388(Decorator.COUNT_TOP_SOLID.method_23475(new CountDecoratorConfig(1)))
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.FOREST_ROCK.method_23397(new BoulderFeatureConfig(field_21125, 0)).method_23388(Decorator.FOREST_ROCK.method_23475(new CountDecoratorConfig(3)))
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21103).method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(7)))
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21093).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(12)))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21093).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.BAMBOO.method_23397(new ProbabilityConfig(0.0F)).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(16)))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.BAMBOO
				.method_23397(new ProbabilityConfig(0.2F))
				.method_23388(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.method_23475(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG))
				)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.05F),
							Feature.JUNGLE_GROUND_BUSH.method_23397(field_21196).method_23387(0.15F),
							Feature.MEGA_JUNGLE_TREE.method_23397(field_21200).method_23387(0.7F)
						),
						Feature.RANDOM_PATCH.method_23397(field_21203)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(30, 0.1F, 1)))
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.method_23397(field_21184).method_23387(0.33333334F)), Feature.NORMAL_TREE.method_23397(field_21185)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(ImmutableList.of(Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F)), Feature.NORMAL_TREE.method_23397(field_21126))
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE.method_23397(field_21187).method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.method_23397(field_21187).method_23387(0.2F), Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F)),
						Feature.NORMAL_TREE.method_23397(field_21126)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(ImmutableList.of(Feature.NORMAL_TREE.method_23397(field_21188).method_23387(0.5F)), Feature.NORMAL_TREE.method_23397(field_21187))
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(ImmutableList.of(Feature.ACACIA_TREE.method_23397(field_21186).method_23387(0.8F)), Feature.NORMAL_TREE.method_23397(field_21126))
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(1, 0.1F, 1)))
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(ImmutableList.of(Feature.ACACIA_TREE.method_23397(field_21186).method_23387(0.8F)), Feature.NORMAL_TREE.method_23397(field_21126))
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.method_23397(field_21185).method_23387(0.666F), Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F)),
						Feature.NORMAL_TREE.method_23397(field_21126)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.method_23397(field_21185).method_23387(0.666F), Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F)),
						Feature.NORMAL_TREE.method_23397(field_21126)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(3, 0.1F, 1)))
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F),
							Feature.JUNGLE_GROUND_BUSH.method_23397(field_21196).method_23387(0.5F),
							Feature.MEGA_JUNGLE_TREE.method_23397(field_21200).method_23387(0.33333334F)
						),
						Feature.NORMAL_TREE.method_23397(field_21167)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(50, 0.1F, 1)))
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.FANCY_TREE.method_23397(field_21190).method_23387(0.1F), Feature.JUNGLE_GROUND_BUSH.method_23397(field_21196).method_23387(0.5F)),
						Feature.NORMAL_TREE.method_23397(field_21167)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE.method_23397(field_21126).method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(5, 0.1F, 1)))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE.method_23397(field_21185).method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.MEGA_SPRUCE_TREE.method_23397(field_21198).method_23387(0.33333334F), Feature.NORMAL_TREE.method_23397(field_21184).method_23387(0.33333334F)
						),
						Feature.NORMAL_TREE.method_23397(field_21185)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.MEGA_SPRUCE_TREE.method_23397(field_21198).method_23387(0.025641026F),
							Feature.MEGA_SPRUCE_TREE.method_23397(field_21199).method_23387(0.30769232F),
							Feature.NORMAL_TREE.method_23397(field_21184).method_23387(0.33333334F)
						),
						Feature.NORMAL_TREE.method_23397(field_21185)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21203).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(25)))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21102).method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(7)))
		);
	}

	public static void addShatteredSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(5)))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(20)))
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21090).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(20)))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_RANDOM_SELECTOR
				.method_23397(
					new RandomRandomFeatureConfig(
						ImmutableList.of(
							Feature.RANDOM_PATCH.method_23397(field_21098),
							Feature.RANDOM_PATCH.method_23397(field_21099),
							Feature.RANDOM_PATCH.method_23397(field_21100),
							Feature.field_21219.method_23397(field_21204)
						),
						0
					)
				)
				.method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(5)))
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(2)))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE.method_23397(field_21189).method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.field_21219.method_23397(field_21205).method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(5)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21090).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21095).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21097).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP.method_23475(new CountChanceDecoratorConfig(8, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21096).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.method_23475(new CountChanceDecoratorConfig(8, 0.125F)))
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_BOOLEAN_SELECTOR
				.method_23397(new RandomBooleanFeatureConfig(Feature.HUGE_RED_MUSHROOM.method_23397(field_21142), Feature.HUGE_BROWN_MUSHROOM.method_23397(field_21143)))
				.method_23388(Decorator.COUNT_HEIGHTMAP.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21097).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP.method_23475(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21096).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.method_23475(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.FANCY_TREE.method_23397(field_21192).method_23387(0.33333334F)), Feature.NORMAL_TREE.method_23397(field_21191)
					)
				)
				.method_23388(Decorator.COUNT_EXTRA_HEIGHTMAP.method_23475(new CountExtraChanceDecoratorConfig(0, 0.05F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.field_21219.method_23397(field_21088).method_23388(Decorator.NOISE_HEIGHTMAP_32.method_23475(new NoiseHeightmapDecoratorConfig(-0.8, 15, 4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.NOISE_HEIGHTMAP_DOUBLE.method_23475(new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)))
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21090).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(2)))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21202).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(7)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21090).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21097).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP.method_23475(new CountChanceDecoratorConfig(3, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21096).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.method_23475(new CountChanceDecoratorConfig(3, 0.125F)))
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.field_21219.method_23397(field_21206).method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(2)))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.field_21219.method_23397(field_21206).method_23388(Decorator.COUNT_HEIGHTMAP_32.method_23475(new CountDecoratorConfig(4)))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21201).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21202).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21097).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP.method_23475(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21096).method_23388(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.method_23475(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21102).method_23388(Decorator.NOISE_HEIGHTMAP_32.method_23475(new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)))
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21097).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21096).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(8)))
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21105).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(10)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21092).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(32)))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21105).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(13)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21092).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21104).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(5)))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21091).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.VINES.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.COUNT_HEIGHT_64.method_23475(new CountDecoratorConfig(50)))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21105).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(60)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21092).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21104).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(10)))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21105).method_23388(Decorator.COUNT_HEIGHTMAP_DOUBLE.method_23475(new CountDecoratorConfig(20)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.method_23397(field_21092).method_23388(Decorator.CHANCE_HEIGHTMAP_DOUBLE.method_23475(new LakeDecoratorConfig(32)))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.DESERT_WELL.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.CHANCE_HEIGHTMAP.method_23475(new LakeDecoratorConfig(1000)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.FOSSIL.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.CHANCE_PASSTHROUGH.method_23475(new LakeDecoratorConfig(64)))
		);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.FOSSIL.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.CHANCE_PASSTHROUGH.method_23475(new LakeDecoratorConfig(64)))
		);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.KELP
				.method_23397(FeatureConfig.DEFAULT)
				.method_23388(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.method_23475(new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))
				)
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SIMPLE_BLOCK
				.method_23397(new SimpleBlockFeatureConfig(field_21134, new BlockState[]{field_21119}, new BlockState[]{field_21159}, new BlockState[]{field_21159}))
				.method_23388(Decorator.CARVING_MASK.method_23475(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)))
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SEAGRASS.method_23397(new SeagrassFeatureConfig(80, 0.3)).method_23388(Decorator.TOP_SOLID_HEIGHTMAP.method_23475(DecoratorConfig.DEFAULT))
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SEAGRASS.method_23397(new SeagrassFeatureConfig(80, 0.8)).method_23388(Decorator.TOP_SOLID_HEIGHTMAP.method_23475(DecoratorConfig.DEFAULT))
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.KELP
				.method_23397(FeatureConfig.DEFAULT)
				.method_23388(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.method_23475(new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))
				)
		);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE.method_23397(field_21111).method_23388(Decorator.COUNT_BIASED_RANGE.method_23475(new RangeDecoratorConfig(50, 8, 8, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE.method_23397(field_21112).method_23388(Decorator.COUNT_VERY_BIASED_RANGE.method_23475(new RangeDecoratorConfig(20, 8, 16, 256)))
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.ICEBERG.method_23397(new BushFeatureConfig(field_21135)).method_23388(Decorator.ICEBERG.method_23475(new LakeDecoratorConfig(16)))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.ICEBERG.method_23397(new BushFeatureConfig(field_21136)).method_23388(Decorator.ICEBERG.method_23475(new LakeDecoratorConfig(200)))
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.BLUE_ICE.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.RANDOM_COUNT_RANGE.method_23475(new RangeDecoratorConfig(20, 30, 32, 64)))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.TOP_LAYER_MODIFICATION,
			Feature.FREEZE_TOP_LAYER.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
	}

	public static void method_20826(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.END_CITY.method_23397(FeatureConfig.DEFAULT).method_23388(Decorator.NOPE.method_23475(DecoratorConfig.DEFAULT))
		);
	}
}
