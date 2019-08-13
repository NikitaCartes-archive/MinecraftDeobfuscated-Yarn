package net.minecraft.network.listener;

import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockPlayerActionS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;

public interface ClientPlayPacketListener extends PacketListener {
	void onEntitySpawn(EntitySpawnS2CPacket entitySpawnS2CPacket);

	void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket experienceOrbSpawnS2CPacket);

	void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket entitySpawnGlobalS2CPacket);

	void onMobSpawn(MobSpawnS2CPacket mobSpawnS2CPacket);

	void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacket);

	void onPaintingSpawn(PaintingSpawnS2CPacket paintingSpawnS2CPacket);

	void onPlayerSpawn(PlayerSpawnS2CPacket playerSpawnS2CPacket);

	void onEntityAnimation(EntityAnimationS2CPacket entityAnimationS2CPacket);

	void onStatistics(StatisticsS2CPacket statisticsS2CPacket);

	void onUnlockRecipes(UnlockRecipesS2CPacket unlockRecipesS2CPacket);

	void onBlockDestroyProgress(BlockBreakingProgressS2CPacket blockBreakingProgressS2CPacket);

	void onSignEditorOpen(SignEditorOpenS2CPacket signEditorOpenS2CPacket);

	void onBlockEntityUpdate(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket);

	void onBlockAction(BlockActionS2CPacket blockActionS2CPacket);

	void onBlockUpdate(BlockUpdateS2CPacket blockUpdateS2CPacket);

	void onChatMessage(ChatMessageS2CPacket chatMessageS2CPacket);

	void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket);

	void onMapUpdate(MapUpdateS2CPacket mapUpdateS2CPacket);

	void onGuiActionConfirm(ConfirmGuiActionS2CPacket confirmGuiActionS2CPacket);

	void onGuiClose(GuiCloseS2CPacket guiCloseS2CPacket);

	void onInventory(InventoryS2CPacket inventoryS2CPacket);

	void onGuiOpen(GuiOpenS2CPacket guiOpenS2CPacket);

	void onGuiUpdate(GuiUpdateS2CPacket guiUpdateS2CPacket);

	void onGuiSlotUpdate(GuiSlotUpdateS2CPacket guiSlotUpdateS2CPacket);

	void onCustomPayload(CustomPayloadS2CPacket customPayloadS2CPacket);

	void onDisconnect(DisconnectS2CPacket disconnectS2CPacket);

	void onEntityStatus(EntityStatusS2CPacket entityStatusS2CPacket);

	void onEntityAttach(EntityAttachS2CPacket entityAttachS2CPacket);

	void onEntityPassengersSet(EntityPassengersSetS2CPacket entityPassengersSetS2CPacket);

	void onExplosion(ExplosionS2CPacket explosionS2CPacket);

	void onGameStateChange(GameStateChangeS2CPacket gameStateChangeS2CPacket);

	void onKeepAlive(KeepAliveS2CPacket keepAliveS2CPacket);

	void onChunkData(ChunkDataS2CPacket chunkDataS2CPacket);

	void onUnloadChunk(UnloadChunkS2CPacket unloadChunkS2CPacket);

	void onWorldEvent(WorldEventS2CPacket worldEventS2CPacket);

	void onGameJoin(GameJoinS2CPacket gameJoinS2CPacket);

	void onEntityUpdate(EntityS2CPacket entityS2CPacket);

	void onPlayerPositionLook(PlayerPositionLookS2CPacket playerPositionLookS2CPacket);

	void onParticle(ParticleS2CPacket particleS2CPacket);

	void onPlayerAbilities(PlayerAbilitiesS2CPacket playerAbilitiesS2CPacket);

	void onPlayerList(PlayerListS2CPacket playerListS2CPacket);

	void onEntitiesDestroy(EntitiesDestroyS2CPacket entitiesDestroyS2CPacket);

	void onRemoveEntityEffect(RemoveEntityEffectS2CPacket removeEntityEffectS2CPacket);

	void onPlayerRespawn(PlayerRespawnS2CPacket playerRespawnS2CPacket);

	void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket entitySetHeadYawS2CPacket);

	void onHeldItemChange(HeldItemChangeS2CPacket heldItemChangeS2CPacket);

	void onScoreboardDisplay(ScoreboardDisplayS2CPacket scoreboardDisplayS2CPacket);

	void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket);

	void onVelocityUpdate(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket);

	void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket entityEquipmentUpdateS2CPacket);

	void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket experienceBarUpdateS2CPacket);

	void onHealthUpdate(HealthUpdateS2CPacket healthUpdateS2CPacket);

	void onTeam(TeamS2CPacket teamS2CPacket);

	void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket scoreboardPlayerUpdateS2CPacket);

	void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket playerSpawnPositionS2CPacket);

	void onWorldTimeUpdate(WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket);

	void onPlaySound(PlaySoundS2CPacket playSoundS2CPacket);

	void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket);

	void onPlaySoundId(PlaySoundIdS2CPacket playSoundIdS2CPacket);

	void onItemPickupAnimation(ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket);

	void onEntityPosition(EntityPositionS2CPacket entityPositionS2CPacket);

	void onEntityAttributes(EntityAttributesS2CPacket entityAttributesS2CPacket);

	void onEntityPotionEffect(EntityPotionEffectS2CPacket entityPotionEffectS2CPacket);

	void onSynchronizeTags(SynchronizeTagsS2CPacket synchronizeTagsS2CPacket);

	void onCombatEvent(CombatEventS2CPacket combatEventS2CPacket);

	void onDifficulty(DifficultyS2CPacket difficultyS2CPacket);

	void onSetCameraEntity(SetCameraEntityS2CPacket setCameraEntityS2CPacket);

	void onWorldBorder(WorldBorderS2CPacket worldBorderS2CPacket);

	void onTitle(TitleS2CPacket titleS2CPacket);

	void onPlayerListHeader(PlayerListHeaderS2CPacket playerListHeaderS2CPacket);

	void onResourcePackSend(ResourcePackSendS2CPacket resourcePackSendS2CPacket);

	void onBossBar(BossBarS2CPacket bossBarS2CPacket);

	void onCooldownUpdate(CooldownUpdateS2CPacket cooldownUpdateS2CPacket);

	void onVehicleMove(VehicleMoveS2CPacket vehicleMoveS2CPacket);

	void onAdvancements(AdvancementUpdateS2CPacket advancementUpdateS2CPacket);

	void onSelectAdvancementTab(SelectAdvancementTabS2CPacket selectAdvancementTabS2CPacket);

	void onCraftResponse(CraftResponseS2CPacket craftResponseS2CPacket);

	void onCommandTree(CommandTreeS2CPacket commandTreeS2CPacket);

	void onStopSound(StopSoundS2CPacket stopSoundS2CPacket);

	void onCommandSuggestions(CommandSuggestionsS2CPacket commandSuggestionsS2CPacket);

	void onSynchronizeRecipes(SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket);

	void onLookAt(LookAtS2CPacket lookAtS2CPacket);

	void onTagQuery(TagQueryResponseS2CPacket tagQueryResponseS2CPacket);

	void onLightUpdate(LightUpdateS2CPacket lightUpdateS2CPacket);

	void onOpenWrittenBook(OpenWrittenBookS2CPacket openWrittenBookS2CPacket);

	void onOpenContainer(OpenContainerPacket openContainerPacket);

	void onSetTradeOffers(SetTradeOffersPacket setTradeOffersPacket);

	void handleChunkLoadDistance(ChunkLoadDistanceS2CPacket chunkLoadDistanceS2CPacket);

	void handleChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket chunkRenderDistanceCenterS2CPacket);

	void method_21707(BlockPlayerActionS2CPacket blockPlayerActionS2CPacket);
}
