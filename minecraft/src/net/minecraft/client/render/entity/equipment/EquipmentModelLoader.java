package net.minecraft.client.render.entity.equipment;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class EquipmentModelLoader extends JsonDataLoader<EquipmentModel> {
	public static final EquipmentModel EMPTY = new EquipmentModel(Map.of());
	private Map<Identifier, EquipmentModel> models = Map.of();

	public EquipmentModelLoader() {
		super(EquipmentModel.CODEC, "models/equipment");
	}

	protected void apply(Map<Identifier, EquipmentModel> map, ResourceManager resourceManager, Profiler profiler) {
		this.models = Map.copyOf(map);
	}

	public EquipmentModel get(Identifier id) {
		return (EquipmentModel)this.models.getOrDefault(id, EMPTY);
	}
}
