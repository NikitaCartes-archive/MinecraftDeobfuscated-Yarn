package net.minecraft;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.registry.Registry;

public class class_7416 extends AbstractTagProvider<PaintingMotive> {
	public class_7416(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.PAINTING_MOTIVE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(class_7406.PLACEABLE)
			.add(
				class_7408.KEBAB,
				class_7408.AZTEC,
				class_7408.ALBAN,
				class_7408.AZTEC2,
				class_7408.BOMB,
				class_7408.PLANT,
				class_7408.WASTELAND,
				class_7408.POOL,
				class_7408.COURBET,
				class_7408.SEA,
				class_7408.SUNSET,
				class_7408.CREEBET,
				class_7408.WANDERER,
				class_7408.GRAHAM,
				class_7408.MATCH,
				class_7408.BUST,
				class_7408.STAGE,
				class_7408.VOID,
				class_7408.SKULL_AND_ROSES,
				class_7408.WITHER,
				class_7408.FIGHTERS,
				class_7408.POINTER,
				class_7408.PIGSCENE,
				class_7408.BURNING_SKULL,
				class_7408.SKELETON,
				class_7408.DONKEY_KONG
			);
	}
}
