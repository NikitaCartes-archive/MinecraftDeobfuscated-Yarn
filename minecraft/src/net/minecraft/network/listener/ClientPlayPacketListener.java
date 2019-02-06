package net.minecraft.network.listener;

import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
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
import net.minecraft.client.network.packet.PlayerUseBedS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetVillagerRecipesPacket;
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
	void method_11112(EntitySpawnS2CPacket entitySpawnS2CPacket);

	void method_11091(ExperienceOrbSpawnS2CPacket experienceOrbSpawnS2CPacket);

	void method_11156(EntitySpawnGlobalS2CPacket entitySpawnGlobalS2CPacket);

	void method_11138(MobSpawnS2CPacket mobSpawnS2CPacket);

	void method_11144(ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacket);

	void method_11114(PaintingSpawnS2CPacket paintingSpawnS2CPacket);

	void method_11097(PlayerSpawnS2CPacket playerSpawnS2CPacket);

	void method_11160(EntityAnimationS2CPacket entityAnimationS2CPacket);

	void method_11129(StatisticsS2CPacket statisticsS2CPacket);

	void method_11115(UnlockRecipesS2CPacket unlockRecipesS2CPacket);

	void method_11116(BlockBreakingProgressS2CPacket blockBreakingProgressS2CPacket);

	void method_11108(SignEditorOpenS2CPacket signEditorOpenS2CPacket);

	void method_11094(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket);

	void method_11158(BlockActionS2CPacket blockActionS2CPacket);

	void method_11136(BlockUpdateS2CPacket blockUpdateS2CPacket);

	void method_11121(ChatMessageS2CPacket chatMessageS2CPacket);

	void method_11100(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket);

	void method_11088(MapUpdateS2CPacket mapUpdateS2CPacket);

	void method_11123(ConfirmGuiActionS2CPacket confirmGuiActionS2CPacket);

	void method_11102(GuiCloseS2CPacket guiCloseS2CPacket);

	void method_11153(InventoryS2CPacket inventoryS2CPacket);

	void method_11089(GuiOpenS2CPacket guiOpenS2CPacket);

	void method_11131(GuiUpdateS2CPacket guiUpdateS2CPacket);

	void method_11109(GuiSlotUpdateS2CPacket guiSlotUpdateS2CPacket);

	void method_11152(CustomPayloadS2CPacket customPayloadS2CPacket);

	void method_11083(DisconnectS2CPacket disconnectS2CPacket);

	void method_11137(PlayerUseBedS2CPacket playerUseBedS2CPacket);

	void method_11148(EntityStatusS2CPacket entityStatusS2CPacket);

	void method_11110(EntityAttachS2CPacket entityAttachS2CPacket);

	void method_11080(EntityPassengersSetS2CPacket entityPassengersSetS2CPacket);

	void method_11124(ExplosionS2CPacket explosionS2CPacket);

	void method_11085(GameStateChangeS2CPacket gameStateChangeS2CPacket);

	void method_11147(KeepAliveS2CPacket keepAliveS2CPacket);

	void method_11128(ChunkDataS2CPacket chunkDataS2CPacket);

	void method_11107(UnloadChunkS2CPacket unloadChunkS2CPacket);

	void method_11098(WorldEventS2CPacket worldEventS2CPacket);

	void method_11120(GameJoinS2CPacket gameJoinS2CPacket);

	void method_11155(EntityS2CPacket entityS2CPacket);

	void method_11157(PlayerPositionLookS2CPacket playerPositionLookS2CPacket);

	void method_11077(ParticleS2CPacket particleS2CPacket);

	void method_11154(PlayerAbilitiesS2CPacket playerAbilitiesS2CPacket);

	void method_11113(PlayerListS2CPacket playerListS2CPacket);

	void method_11095(EntitiesDestroyS2CPacket entitiesDestroyS2CPacket);

	void method_11119(RemoveEntityEffectS2CPacket removeEntityEffectS2CPacket);

	void method_11117(PlayerRespawnS2CPacket playerRespawnS2CPacket);

	void method_11139(EntitySetHeadYawS2CPacket entitySetHeadYawS2CPacket);

	void method_11135(HeldItemChangeS2CPacket heldItemChangeS2CPacket);

	void method_11159(ScoreboardDisplayS2CPacket scoreboardDisplayS2CPacket);

	void method_11093(EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket);

	void method_11132(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket);

	void method_11151(EntityEquipmentUpdateS2CPacket entityEquipmentUpdateS2CPacket);

	void method_11101(ExperienceBarUpdateS2CPacket experienceBarUpdateS2CPacket);

	void method_11122(HealthUpdateS2CPacket healthUpdateS2CPacket);

	void method_11099(TeamS2CPacket teamS2CPacket);

	void method_11118(ScoreboardPlayerUpdateS2CPacket scoreboardPlayerUpdateS2CPacket);

	void method_11142(PlayerSpawnPositionS2CPacket playerSpawnPositionS2CPacket);

	void method_11079(WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket);

	void method_11146(PlaySoundS2CPacket playSoundS2CPacket);

	void method_11125(PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket);

	void method_11104(PlaySoundIdS2CPacket playSoundIdS2CPacket);

	void method_11150(ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket);

	void method_11086(EntityPositionS2CPacket entityPositionS2CPacket);

	void method_11149(EntityAttributesS2CPacket entityAttributesS2CPacket);

	void method_11084(EntityPotionEffectS2CPacket entityPotionEffectS2CPacket);

	void method_11126(SynchronizeTagsS2CPacket synchronizeTagsS2CPacket);

	void method_11133(CombatEventS2CPacket combatEventS2CPacket);

	void method_11140(DifficultyS2CPacket difficultyS2CPacket);

	void method_11111(SetCameraEntityS2CPacket setCameraEntityS2CPacket);

	void method_11096(WorldBorderS2CPacket worldBorderS2CPacket);

	void method_11103(TitleS2CPacket titleS2CPacket);

	void method_11105(PlayerListHeaderS2CPacket playerListHeaderS2CPacket);

	void method_11141(ResourcePackSendS2CPacket resourcePackSendS2CPacket);

	void method_11078(BossBarS2CPacket bossBarS2CPacket);

	void method_11087(CooldownUpdateS2CPacket cooldownUpdateS2CPacket);

	void method_11134(VehicleMoveS2CPacket vehicleMoveS2CPacket);

	void method_11130(AdvancementUpdateS2CPacket advancementUpdateS2CPacket);

	void method_11161(SelectAdvancementTabS2CPacket selectAdvancementTabS2CPacket);

	void method_11090(CraftResponseS2CPacket craftResponseS2CPacket);

	void method_11145(CommandTreeS2CPacket commandTreeS2CPacket);

	void method_11082(StopSoundS2CPacket stopSoundS2CPacket);

	void method_11081(CommandSuggestionsS2CPacket commandSuggestionsS2CPacket);

	void method_11106(SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket);

	void method_11092(LookAtS2CPacket lookAtS2CPacket);

	void method_11127(TagQueryResponseS2CPacket tagQueryResponseS2CPacket);

	void method_11143(LightUpdateS2CPacket lightUpdateS2CPacket);

	void method_17186(OpenWrittenBookS2CPacket openWrittenBookS2CPacket);

	void onOpenContainer(OpenContainerPacket openContainerPacket);

	void onSetVillagerRecipes(SetVillagerRecipesPacket setVillagerRecipesPacket);
}
