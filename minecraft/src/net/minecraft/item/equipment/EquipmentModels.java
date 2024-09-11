package net.minecraft.item.equipment;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface EquipmentModels {
	Identifier LEATHER = Identifier.ofVanilla("leather");
	Identifier CHAINMAIL = Identifier.ofVanilla("chainmail");
	Identifier IRON = Identifier.ofVanilla("iron");
	Identifier GOLD = Identifier.ofVanilla("gold");
	Identifier DIAMOND = Identifier.ofVanilla("diamond");
	Identifier TURTLE_SCUTE = Identifier.ofVanilla("turtle_scute");
	Identifier NETHERITE = Identifier.ofVanilla("netherite");
	Identifier ARMADILLO_SCUTE = Identifier.ofVanilla("armadillo_scute");
	Identifier ELYTRA = Identifier.ofVanilla("elytra");
	Map<DyeColor, Identifier> CARPET_FROM_COLOR = Util.mapEnum(DyeColor.class, color -> Identifier.ofVanilla(color.asString() + "_carpet"));
	Identifier TRADER_LLAMA = Identifier.ofVanilla("trader_llama");

	static void accept(BiConsumer<Identifier, EquipmentModel> equipmentModelBiConsumer) {
		equipmentModelBiConsumer.accept(
			LEATHER,
			EquipmentModel.builder()
				.addHumanoidLayers(Identifier.ofVanilla("leather"), true)
				.addHumanoidLayers(Identifier.ofVanilla("leather_overlay"), false)
				.addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.createDyeableLeather(Identifier.ofVanilla("leather"), true))
				.build()
		);
		equipmentModelBiConsumer.accept(CHAINMAIL, buildHumanoid("chainmail"));
		equipmentModelBiConsumer.accept(IRON, buildHumanoidAndHorse("iron"));
		equipmentModelBiConsumer.accept(GOLD, buildHumanoidAndHorse("gold"));
		equipmentModelBiConsumer.accept(DIAMOND, buildHumanoidAndHorse("diamond"));
		equipmentModelBiConsumer.accept(TURTLE_SCUTE, EquipmentModel.builder().addMainHumanoidLayer(Identifier.ofVanilla("turtle_scute"), false).build());
		equipmentModelBiConsumer.accept(NETHERITE, buildHumanoid("netherite"));
		equipmentModelBiConsumer.accept(
			ARMADILLO_SCUTE,
			EquipmentModel.builder()
				.addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.createDyeable(Identifier.ofVanilla("armadillo_scute"), false))
				.addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.createDyeable(Identifier.ofVanilla("armadillo_scute_overlay"), true))
				.build()
		);
		equipmentModelBiConsumer.accept(
			ELYTRA,
			EquipmentModel.builder().addLayers(EquipmentModel.LayerType.WINGS, new EquipmentModel.Layer(Identifier.ofVanilla("elytra"), Optional.empty(), true)).build()
		);

		for (Entry<DyeColor, Identifier> entry : CARPET_FROM_COLOR.entrySet()) {
			DyeColor dyeColor = (DyeColor)entry.getKey();
			Identifier identifier = (Identifier)entry.getValue();
			equipmentModelBiConsumer.accept(
				identifier,
				EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(Identifier.ofVanilla(dyeColor.asString()))).build()
			);
		}

		equipmentModelBiConsumer.accept(
			TRADER_LLAMA,
			EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(Identifier.ofVanilla("trader_llama"))).build()
		);
	}

	private static EquipmentModel buildHumanoid(String path) {
		return EquipmentModel.builder().addHumanoidLayers(Identifier.ofVanilla(path)).build();
	}

	private static EquipmentModel buildHumanoidAndHorse(String path) {
		return EquipmentModel.builder()
			.addHumanoidLayers(Identifier.ofVanilla(path))
			.addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.createDyeableLeather(Identifier.ofVanilla(path), false))
			.build();
	}
}
