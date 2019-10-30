package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>> {
	private static final Map<MooshroomEntity.Type, Identifier> SKIN = Util.create(Maps.<MooshroomEntity.Type, Identifier>newHashMap(), hashMap -> {
		hashMap.put(MooshroomEntity.Type.BROWN, new Identifier("textures/entity/cow/brown_mooshroom.png"));
		hashMap.put(MooshroomEntity.Type.RED, new Identifier("textures/entity/cow/red_mooshroom.png"));
	});

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CowEntityModel<>(), 0.7F);
		this.addFeature(new MooshroomMushroomFeatureRenderer<>(this));
	}

	public Identifier method_4066(MooshroomEntity mooshroomEntity) {
		return (Identifier)SKIN.get(mooshroomEntity.getMooshroomType());
	}
}
