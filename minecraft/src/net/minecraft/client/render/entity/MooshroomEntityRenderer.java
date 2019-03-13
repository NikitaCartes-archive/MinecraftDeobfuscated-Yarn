package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>> {
	private static final Map<MooshroomEntity.Type, Identifier> SKIN = SystemUtil.consume(Maps.<MooshroomEntity.Type, Identifier>newHashMap(), hashMap -> {
		hashMap.put(MooshroomEntity.Type.field_18110, new Identifier("textures/entity/cow/brown_mooshroom.png"));
		hashMap.put(MooshroomEntity.Type.field_18109, new Identifier("textures/entity/cow/red_mooshroom.png"));
	});

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CowEntityModel<>(), 0.7F);
		this.method_4046(new MooshroomMushroomFeatureRenderer<>(this));
	}

	protected Identifier method_4066(MooshroomEntity mooshroomEntity) {
		return (Identifier)SKIN.get(mooshroomEntity.getType());
	}
}
