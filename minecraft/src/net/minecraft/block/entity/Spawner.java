package net.minecraft.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public interface Spawner {
	void setEntityType(EntityType<?> type, Random random);

	static void appendSpawnDataToTooltip(ItemStack stack, List<Text> tooltip, String spawnDataKey) {
		Text text = getSpawnedEntityText(stack, spawnDataKey);
		if (text != null) {
			tooltip.add(text);
		} else {
			tooltip.add(ScreenTexts.EMPTY);
			tooltip.add(Text.translatable("block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
			tooltip.add(ScreenTexts.space().append(Text.translatable("block.minecraft.spawner.desc2").formatted(Formatting.BLUE)));
		}
	}

	@Nullable
	static Text getSpawnedEntityText(ItemStack stack, String spawnDataKey) {
		NbtCompound nbtCompound = stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).getNbt();
		Identifier identifier = getSpawnedEntityId(nbtCompound, spawnDataKey);
		return identifier != null
			? (Text)Registries.ENTITY_TYPE
				.getOptionalValue(identifier)
				.map(entityType -> Text.translatable(entityType.getTranslationKey()).formatted(Formatting.GRAY))
				.orElse(null)
			: null;
	}

	@Nullable
	private static Identifier getSpawnedEntityId(NbtCompound nbt, String spawnDataKey) {
		if (nbt.contains(spawnDataKey, NbtElement.COMPOUND_TYPE)) {
			String string = nbt.getCompound(spawnDataKey).getCompound("entity").getString("id");
			return Identifier.tryParse(string);
		} else {
			return null;
		}
	}
}
