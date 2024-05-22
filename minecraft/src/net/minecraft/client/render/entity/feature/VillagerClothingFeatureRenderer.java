package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat>
	extends FeatureRenderer<T, M> {
	private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap<>(), levelToId -> {
		levelToId.put(1, Identifier.ofVanilla("stone"));
		levelToId.put(2, Identifier.ofVanilla("iron"));
		levelToId.put(3, Identifier.ofVanilla("gold"));
		levelToId.put(4, Identifier.ofVanilla("emerald"));
		levelToId.put(5, Identifier.ofVanilla("diamond"));
	});
	private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap<>();
	private final ResourceManager resourceManager;
	private final String entityType;

	public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context, ResourceManager resourceManager, String entityType) {
		super(context);
		this.resourceManager = resourceManager;
		this.entityType = entityType;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!livingEntity.isInvisible()) {
			VillagerData villagerData = livingEntity.getVillagerData();
			VillagerType villagerType = villagerData.getType();
			VillagerProfession villagerProfession = villagerData.getProfession();
			VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", Registries.VILLAGER_TYPE, villagerType);
			VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", Registries.VILLAGER_PROFESSION, villagerProfession);
			M entityModel = this.getContextModel();
			entityModel.setHatVisible(
				hatType2 == VillagerResourceMetadata.HatType.NONE
					|| hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL
			);
			Identifier identifier = this.findTexture("type", Registries.VILLAGER_TYPE.getId(villagerType));
			renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, -1);
			entityModel.setHatVisible(true);
			if (villagerProfession != VillagerProfession.NONE && !livingEntity.isBaby()) {
				Identifier identifier2 = this.findTexture("profession", Registries.VILLAGER_PROFESSION.getId(villagerProfession));
				renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntity, -1);
				if (villagerProfession != VillagerProfession.NITWIT) {
					Identifier identifier3 = this.findTexture("profession_level", LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size())));
					renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, i, livingEntity, -1);
				}
			}
		}
	}

	private Identifier findTexture(String keyType, Identifier keyId) {
		return keyId.withPath((UnaryOperator<String>)(path -> "textures/entity/" + this.entityType + "/" + keyType + "/" + path + ".png"));
	}

	public <K> VillagerResourceMetadata.HatType getHatType(
		Object2ObjectMap<K, VillagerResourceMetadata.HatType> hatLookUp, String keyType, DefaultedRegistry<K> registry, K key
	) {
		return hatLookUp.computeIfAbsent(
			key, k -> (VillagerResourceMetadata.HatType)this.resourceManager.getResource(this.findTexture(keyType, registry.getId(key))).flatMap(resource -> {
					try {
						return resource.getMetadata().decode(VillagerResourceMetadata.READER).map(VillagerResourceMetadata::getHatType);
					} catch (IOException var2) {
						return Optional.empty();
					}
				}).orElse(VillagerResourceMetadata.HatType.NONE)
		);
	}
}
