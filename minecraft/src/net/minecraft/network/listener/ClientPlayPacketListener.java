package net.minecraft.network.listener;

import net.minecraft.client.network.packet.AdvancementUpdateClientPacket;
import net.minecraft.client.network.packet.BlockActionClientPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressClientPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateClientPacket;
import net.minecraft.client.network.packet.CombatEventClientPacket;
import net.minecraft.client.network.packet.CommandSuggestionsClientPacket;
import net.minecraft.client.network.packet.CommandTreeClientPacket;
import net.minecraft.client.network.packet.CooldownUpdateClientPacket;
import net.minecraft.client.network.packet.CraftResponseClientPacket;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.network.packet.DifficultyClientPacket;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.client.network.packet.EntitiesDestroyClientPacket;
import net.minecraft.client.network.packet.EntityAnimationClientPacket;
import net.minecraft.client.network.packet.EntityAttachClientPacket;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.client.network.packet.EntityClientPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateClientPacket;
import net.minecraft.client.network.packet.EntityPassengersSetClientPacket;
import net.minecraft.client.network.packet.EntityPositionClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawClientPacket;
import net.minecraft.client.network.packet.EntitySpawnClientPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateClientPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnClientPacket;
import net.minecraft.client.network.packet.ExplosionClientPacket;
import net.minecraft.client.network.packet.GameJoinClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.GuiActionConfirmClientPacket;
import net.minecraft.client.network.packet.GuiCloseClientPacket;
import net.minecraft.client.network.packet.GuiOpenClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.GuiUpdateClientPacket;
import net.minecraft.client.network.packet.HealthUpdateClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.InventoryClientPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationClientPacket;
import net.minecraft.client.network.packet.KeepAliveClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.client.network.packet.LookAtClientPacket;
import net.minecraft.client.network.packet.MapUpdateClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookClientPacket;
import net.minecraft.client.network.packet.PaintingSpawnClientPacket;
import net.minecraft.client.network.packet.ParticleClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityClientPacket;
import net.minecraft.client.network.packet.PlaySoundIdClientPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesClientPacket;
import net.minecraft.client.network.packet.PlayerListClientPacket;
import net.minecraft.client.network.packet.PlayerListHeaderClientPacket;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.client.network.packet.PlayerRespawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectClientPacket;
import net.minecraft.client.network.packet.ResourcePackSendClientPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayClientPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateClientPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateClientPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabClientPacket;
import net.minecraft.client.network.packet.SetCameraEntityClientPacket;
import net.minecraft.client.network.packet.SetVillagerRecipesPacket;
import net.minecraft.client.network.packet.SignEditorOpenClientPacket;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.client.network.packet.StopSoundClientPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesClientPacket;
import net.minecraft.client.network.packet.SynchronizeTagsClientPacket;
import net.minecraft.client.network.packet.TagQueryResponseClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.client.network.packet.TitleClientPacket;
import net.minecraft.client.network.packet.UnloadChunkClientPacket;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.client.network.packet.WorldBorderClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;

public interface ClientPlayPacketListener extends PacketListener {
	void onEntitySpawn(EntitySpawnClientPacket entitySpawnClientPacket);

	void onExperienceOrbSpawn(ExperienceOrbSpawnClientPacket experienceOrbSpawnClientPacket);

	void onEntitySpawnGlobal(EntitySpawnGlobalClientPacket entitySpawnGlobalClientPacket);

	void onMobSpawn(MobSpawnClientPacket mobSpawnClientPacket);

	void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateClientPacket scoreboardObjectiveUpdateClientPacket);

	void onPaintingSpawn(PaintingSpawnClientPacket paintingSpawnClientPacket);

	void onPlayerSpawn(PlayerSpawnClientPacket playerSpawnClientPacket);

	void onEntityAnimation(EntityAnimationClientPacket entityAnimationClientPacket);

	void onStatistics(StatisticsClientPacket statisticsClientPacket);

	void onUnlockRecipes(UnlockRecipesClientPacket unlockRecipesClientPacket);

	void onBlockDestroyProgress(BlockBreakingProgressClientPacket blockBreakingProgressClientPacket);

	void onSignEditorOpen(SignEditorOpenClientPacket signEditorOpenClientPacket);

	void onBlockEntityUpdate(BlockEntityUpdateClientPacket blockEntityUpdateClientPacket);

	void onBlockAction(BlockActionClientPacket blockActionClientPacket);

	void onBlockUpdate(BlockUpdateClientPacket blockUpdateClientPacket);

	void onChatMessage(ChatMessageClientPacket chatMessageClientPacket);

	void onChunkDeltaUpdate(ChunkDeltaUpdateClientPacket chunkDeltaUpdateClientPacket);

	void onMapUpdate(MapUpdateClientPacket mapUpdateClientPacket);

	void onGuiActionConfirm(GuiActionConfirmClientPacket guiActionConfirmClientPacket);

	void onGuiClose(GuiCloseClientPacket guiCloseClientPacket);

	void onInventory(InventoryClientPacket inventoryClientPacket);

	void onGuiOpen(GuiOpenClientPacket guiOpenClientPacket);

	void onGuiUpdate(GuiUpdateClientPacket guiUpdateClientPacket);

	void onGuiSlotUpdate(GuiSlotUpdateClientPacket guiSlotUpdateClientPacket);

	void onCustomPayload(CustomPayloadClientPacket customPayloadClientPacket);

	void onDisconnect(DisconnectClientPacket disconnectClientPacket);

	void onPlayerUseBed(PlayerUseBedClientPacket playerUseBedClientPacket);

	void onEntityStatus(EntityStatusClientPacket entityStatusClientPacket);

	void onEntityAttach(EntityAttachClientPacket entityAttachClientPacket);

	void onEntityPassengersSet(EntityPassengersSetClientPacket entityPassengersSetClientPacket);

	void onExplosion(ExplosionClientPacket explosionClientPacket);

	void onGameStateChange(GameStateChangeClientPacket gameStateChangeClientPacket);

	void onKeepAlive(KeepAliveClientPacket keepAliveClientPacket);

	void onChunkData(ChunkDataClientPacket chunkDataClientPacket);

	void onUnloadChunk(UnloadChunkClientPacket unloadChunkClientPacket);

	void onWorldEvent(WorldEventClientPacket worldEventClientPacket);

	void onGameJoin(GameJoinClientPacket gameJoinClientPacket);

	void onEntityUpdate(EntityClientPacket entityClientPacket);

	void onPlayerPositionLook(PlayerPositionLookClientPacket playerPositionLookClientPacket);

	void onParticle(ParticleClientPacket particleClientPacket);

	void onPlayerAbilities(PlayerAbilitiesClientPacket playerAbilitiesClientPacket);

	void onPlayerList(PlayerListClientPacket playerListClientPacket);

	void onEntitiesDestroy(EntitiesDestroyClientPacket entitiesDestroyClientPacket);

	void onRemoveEntityEffect(RemoveEntityEffectClientPacket removeEntityEffectClientPacket);

	void onPlayerRespawn(PlayerRespawnClientPacket playerRespawnClientPacket);

	void onEntitySetHeadYaw(EntitySetHeadYawClientPacket entitySetHeadYawClientPacket);

	void onHeldItemChange(HeldItemChangeClientPacket heldItemChangeClientPacket);

	void onScoreboardDisplay(ScoreboardDisplayClientPacket scoreboardDisplayClientPacket);

	void onEntityTrackerUpdate(EntityTrackerUpdateClientPacket entityTrackerUpdateClientPacket);

	void onVelocityUpdate(EntityVelocityUpdateClientPacket entityVelocityUpdateClientPacket);

	void onEquipmentUpdate(EntityEquipmentUpdateClientPacket entityEquipmentUpdateClientPacket);

	void onExperienceBarUpdate(ExperienceBarUpdateClientPacket experienceBarUpdateClientPacket);

	void onHealthUpdate(HealthUpdateClientPacket healthUpdateClientPacket);

	void onTeam(TeamClientPacket teamClientPacket);

	void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateClientPacket scoreboardPlayerUpdateClientPacket);

	void onPlayerSpawnPosition(PlayerSpawnPositionClientPacket playerSpawnPositionClientPacket);

	void onWorldTimeUpdate(WorldTimeUpdateClientPacket worldTimeUpdateClientPacket);

	void onPlaySound(PlaySoundClientPacket playSoundClientPacket);

	void onPlaySoundFromEntity(PlaySoundFromEntityClientPacket playSoundFromEntityClientPacket);

	void onPlaySoundId(PlaySoundIdClientPacket playSoundIdClientPacket);

	void onItemPickupAnimation(ItemPickupAnimationClientPacket itemPickupAnimationClientPacket);

	void onEntityPosition(EntityPositionClientPacket entityPositionClientPacket);

	void onEntityAttributes(EntityAttributesClientPacket entityAttributesClientPacket);

	void onEntityPotionEffect(EntityPotionEffectClientPacket entityPotionEffectClientPacket);

	void onSynchronizeTags(SynchronizeTagsClientPacket synchronizeTagsClientPacket);

	void onCombatEvent(CombatEventClientPacket combatEventClientPacket);

	void onDifficulty(DifficultyClientPacket difficultyClientPacket);

	void onSetCameraEntity(SetCameraEntityClientPacket setCameraEntityClientPacket);

	void onWorldBorder(WorldBorderClientPacket worldBorderClientPacket);

	void onTitle(TitleClientPacket titleClientPacket);

	void onPlayerListHeader(PlayerListHeaderClientPacket playerListHeaderClientPacket);

	void onResourcePackSend(ResourcePackSendClientPacket resourcePackSendClientPacket);

	void onBossBar(BossBarClientPacket bossBarClientPacket);

	void onCooldownUpdate(CooldownUpdateClientPacket cooldownUpdateClientPacket);

	void onVehicleMove(VehicleMoveClientPacket vehicleMoveClientPacket);

	void onAdvancements(AdvancementUpdateClientPacket advancementUpdateClientPacket);

	void onSelectAdvancementTab(SelectAdvancementTabClientPacket selectAdvancementTabClientPacket);

	void onCraftResponse(CraftResponseClientPacket craftResponseClientPacket);

	void onCommandTree(CommandTreeClientPacket commandTreeClientPacket);

	void onStopSound(StopSoundClientPacket stopSoundClientPacket);

	void onCommandSuggestions(CommandSuggestionsClientPacket commandSuggestionsClientPacket);

	void onSynchronizeRecipes(SynchronizeRecipesClientPacket synchronizeRecipesClientPacket);

	void onLookAt(LookAtClientPacket lookAtClientPacket);

	void onTagQuery(TagQueryResponseClientPacket tagQueryResponseClientPacket);

	void onLightUpdate(LightUpdateClientPacket lightUpdateClientPacket);

	void onOpenWrittenBook(OpenWrittenBookClientPacket openWrittenBookClientPacket);

	void onOpenContainer(OpenContainerPacket openContainerPacket);

	void onSetVillagerRecipes(SetVillagerRecipesPacket setVillagerRecipesPacket);
}
