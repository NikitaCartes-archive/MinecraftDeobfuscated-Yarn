package net.minecraft.data.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentModels;
import net.minecraft.util.Identifier;

public class EquipmentModelProvider implements DataProvider {
	private final DataOutput.PathResolver pathResolver;

	public EquipmentModelProvider(DataOutput output) {
		this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models/equipment");
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Map<Identifier, EquipmentModel> map = new HashMap();
		EquipmentModels.accept((id, model) -> {
			if (map.putIfAbsent(id, model) != null) {
				throw new IllegalStateException("Tried to register equipment model twice for id: " + id);
			}
		});
		return DataProvider.writeAllToPath(writer, EquipmentModel.CODEC, this.pathResolver, map);
	}

	@Override
	public String getName() {
		return "Equipment Model Definitions";
	}
}
