package net.minecraft.network.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2793;
import net.minecraft.class_2795;
import net.minecraft.class_2799;
import net.minecraft.class_2805;
import net.minecraft.class_2809;
import net.minecraft.class_2813;
import net.minecraft.class_2822;
import net.minecraft.class_2827;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_2836;
import net.minecraft.class_2838;
import net.minecraft.class_2842;
import net.minecraft.class_2848;
import net.minecraft.class_2851;
import net.minecraft.class_2853;
import net.minecraft.class_2855;
import net.minecraft.class_2856;
import net.minecraft.class_2859;
import net.minecraft.class_2863;
import net.minecraft.class_2866;
import net.minecraft.class_2868;
import net.minecraft.class_2870;
import net.minecraft.class_2871;
import net.minecraft.class_2873;
import net.minecraft.class_2875;
import net.minecraft.class_2877;
import net.minecraft.class_2879;
import net.minecraft.class_2884;
import net.minecraft.class_3753;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.server.network.packet.ChatMessageServerPacket;
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.RecipeClickServerPacket;

public interface ServerPlayPacketListener extends PacketListener {
	void method_12052(class_2879 arg);

	void onChatMessage(ChatMessageServerPacket chatMessageServerPacket);

	void method_12068(class_2799 arg);

	void onClientSettings(ClientSettingsServerPacket clientSettingsServerPacket);

	void method_12079(class_2809 arg);

	void method_12055(ButtonClickServerPacket buttonClickServerPacket);

	void method_12076(class_2813 arg);

	void method_12061(RecipeClickServerPacket recipeClickServerPacket);

	void onGuiClose(GuiCloseServerPacket guiCloseServerPacket);

	void onCustomPayload(CustomPayloadServerPacket customPayloadServerPacket);

	void onPlayerInteractEntity(PlayerInteractEntityServerPacket playerInteractEntityServerPacket);

	void method_12082(class_2827 arg);

	void method_12063(class_2828 arg);

	void method_12083(class_2842 arg);

	void onPlayerAction(PlayerActionServerPacket playerActionServerPacket);

	void method_12045(class_2848 arg);

	void method_12067(class_2851 arg);

	void method_12056(class_2868 arg);

	void method_12070(class_2873 arg);

	void method_12071(class_2877 arg);

	void onPlayerInteractBlock(PlayerInteractBlockServerPacket playerInteractBlockServerPacket);

	void onPlayerInteractItem(PlayerInteractItemServerPacket playerInteractItemServerPacket);

	void method_12073(class_2884 arg);

	void method_12081(class_2856 arg);

	void method_12064(class_2836 arg);

	void method_12078(class_2833 arg);

	void method_12050(class_2793 arg);

	void method_12047(class_2853 arg);

	void method_12058(class_2859 arg);

	void method_12059(class_2805 arg);

	void method_12077(class_2870 arg);

	void method_12049(class_2871 arg);

	void method_12084(class_2838 arg);

	void method_12060(class_2855 arg);

	void method_12057(class_2866 arg);

	void method_12051(class_2875 arg);

	void method_12080(class_2863 arg);

	void onBookUpdate(BookUpdateServerPacket bookUpdateServerPacket);

	void method_12074(class_2822 arg);

	void method_12072(class_2795 arg);

	@Environment(EnvType.CLIENT)
	void method_16383(class_3753 arg);
}
