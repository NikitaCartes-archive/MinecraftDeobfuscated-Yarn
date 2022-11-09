/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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

@Environment(value=EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap(), levelToId -> {
        levelToId.put(1, new Identifier("stone"));
        levelToId.put(2, new Identifier("iron"));
        levelToId.put(3, new Identifier("gold"));
        levelToId.put(4, new Identifier("emerald"));
        levelToId.put(5, new Identifier("diamond"));
    });
    private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap<VillagerType, VillagerResourceMetadata.HatType>();
    private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap<VillagerProfession, VillagerResourceMetadata.HatType>();
    private final ResourceManager resourceManager;
    private final String entityType;

    public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context, ResourceManager resourceManager, String entityType) {
        super(context);
        this.resourceManager = resourceManager;
        this.entityType = entityType;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (((Entity)livingEntity).isInvisible()) {
            return;
        }
        VillagerData villagerData = ((VillagerDataContainer)livingEntity).getVillagerData();
        VillagerType villagerType = villagerData.getType();
        VillagerProfession villagerProfession = villagerData.getProfession();
        VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", Registries.VILLAGER_TYPE, villagerType);
        VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", Registries.VILLAGER_PROFESSION, villagerProfession);
        Object entityModel = this.getContextModel();
        ((ModelWithHat)entityModel).setHatVisible(hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL);
        Identifier identifier = this.findTexture("type", Registries.VILLAGER_TYPE.getId(villagerType));
        VillagerClothingFeatureRenderer.renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0f, 1.0f, 1.0f);
        ((ModelWithHat)entityModel).setHatVisible(true);
        if (villagerProfession != VillagerProfession.NONE && !((LivingEntity)livingEntity).isBaby()) {
            Identifier identifier2 = this.findTexture("profession", Registries.VILLAGER_PROFESSION.getId(villagerProfession));
            VillagerClothingFeatureRenderer.renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0f, 1.0f, 1.0f);
            if (villagerProfession != VillagerProfession.NITWIT) {
                Identifier identifier3 = this.findTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size())));
                VillagerClothingFeatureRenderer.renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private Identifier findTexture(String keyType, Identifier keyId) {
        return keyId.withPath(path -> "textures/entity/" + this.entityType + "/" + keyType + "/" + path + ".png");
    }

    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<K, VillagerResourceMetadata.HatType> hatLookUp, String keyType, DefaultedRegistry<K> registry, K key) {
        return hatLookUp.computeIfAbsent(key, k -> this.resourceManager.getResource(this.findTexture(keyType, registry.getId(key))).flatMap(resource -> {
            try {
                return resource.getMetadata().decode(VillagerResourceMetadata.READER).map(VillagerResourceMetadata::getHatType);
            } catch (IOException iOException) {
                return Optional.empty();
            }
        }).orElse(VillagerResourceMetadata.HatType.NONE));
    }
}

