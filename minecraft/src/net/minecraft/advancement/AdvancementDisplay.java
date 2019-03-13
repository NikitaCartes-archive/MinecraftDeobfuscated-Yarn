package net.minecraft.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
	private final TextComponent title;
	private final TextComponent description;
	private final ItemStack icon;
	private final Identifier background;
	private final AdvancementFrame field_1237;
	private final boolean showToast;
	private final boolean announceToChat;
	private final boolean hidden;
	private float xPos;
	private float yPos;

	public AdvancementDisplay(
		ItemStack itemStack,
		TextComponent textComponent,
		TextComponent textComponent2,
		@Nullable Identifier identifier,
		AdvancementFrame advancementFrame,
		boolean bl,
		boolean bl2,
		boolean bl3
	) {
		this.title = textComponent;
		this.description = textComponent2;
		this.icon = itemStack;
		this.background = identifier;
		this.field_1237 = advancementFrame;
		this.showToast = bl;
		this.announceToChat = bl2;
		this.hidden = bl3;
	}

	public void setPosition(float f, float g) {
		this.xPos = f;
		this.yPos = g;
	}

	public TextComponent getTitle() {
		return this.title;
	}

	public TextComponent getDescription() {
		return this.description;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIcon() {
		return this.icon;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier getBackground() {
		return this.background;
	}

	public AdvancementFrame method_815() {
		return this.field_1237;
	}

	@Environment(EnvType.CLIENT)
	public float getX() {
		return this.xPos;
	}

	@Environment(EnvType.CLIENT)
	public float getY() {
		return this.yPos;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldShowToast() {
		return this.showToast;
	}

	public boolean shouldAnnounceToChat() {
		return this.announceToChat;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public static AdvancementDisplay deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		TextComponent textComponent = JsonHelper.deserialize(jsonObject, "title", jsonDeserializationContext, TextComponent.class);
		TextComponent textComponent2 = JsonHelper.deserialize(jsonObject, "description", jsonDeserializationContext, TextComponent.class);
		if (textComponent != null && textComponent2 != null) {
			ItemStack itemStack = method_822(JsonHelper.getObject(jsonObject, "icon"));
			Identifier identifier = jsonObject.has("background") ? new Identifier(JsonHelper.getString(jsonObject, "background")) : null;
			AdvancementFrame advancementFrame = jsonObject.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(jsonObject, "frame")) : AdvancementFrame.TASK;
			boolean bl = JsonHelper.getBoolean(jsonObject, "show_toast", true);
			boolean bl2 = JsonHelper.getBoolean(jsonObject, "announce_to_chat", true);
			boolean bl3 = JsonHelper.getBoolean(jsonObject, "hidden", false);
			return new AdvancementDisplay(itemStack, textComponent, textComponent2, identifier, advancementFrame, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static ItemStack method_822(JsonObject jsonObject) {
		if (!jsonObject.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JsonHelper.getItem(jsonObject, "item");
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack itemStack = new ItemStack(item);
				if (jsonObject.has("nbt")) {
					try {
						CompoundTag compoundTag = JsonLikeTagParser.parse(JsonHelper.asString(jsonObject.get("nbt"), "nbt"));
						itemStack.method_7980(compoundTag);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return itemStack;
			}
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.method_10805(this.title);
		packetByteBuf.method_10805(this.description);
		packetByteBuf.writeItemStack(this.icon);
		packetByteBuf.writeEnumConstant(this.field_1237);
		int i = 0;
		if (this.background != null) {
			i |= 1;
		}

		if (this.showToast) {
			i |= 2;
		}

		if (this.hidden) {
			i |= 4;
		}

		packetByteBuf.writeInt(i);
		if (this.background != null) {
			packetByteBuf.method_10812(this.background);
		}

		packetByteBuf.writeFloat(this.xPos);
		packetByteBuf.writeFloat(this.yPos);
	}

	public static AdvancementDisplay fromPacket(PacketByteBuf packetByteBuf) {
		TextComponent textComponent = packetByteBuf.method_10808();
		TextComponent textComponent2 = packetByteBuf.method_10808();
		ItemStack itemStack = packetByteBuf.readItemStack();
		AdvancementFrame advancementFrame = packetByteBuf.readEnumConstant(AdvancementFrame.class);
		int i = packetByteBuf.readInt();
		Identifier identifier = (i & 1) != 0 ? packetByteBuf.method_10810() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, textComponent, textComponent2, identifier, advancementFrame, bl, false, bl2);
		advancementDisplay.setPosition(packetByteBuf.readFloat(), packetByteBuf.readFloat());
		return advancementDisplay;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.iconToJson());
		jsonObject.add("title", TextComponent.Serializer.toJson(this.title));
		jsonObject.add("description", TextComponent.Serializer.toJson(this.description));
		jsonObject.addProperty("frame", this.field_1237.getId());
		jsonObject.addProperty("show_toast", this.showToast);
		jsonObject.addProperty("announce_to_chat", this.announceToChat);
		jsonObject.addProperty("hidden", this.hidden);
		if (this.background != null) {
			jsonObject.addProperty("background", this.background.toString());
		}

		return jsonObject;
	}

	private JsonObject iconToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.method_10221(this.icon.getItem()).toString());
		return jsonObject;
	}
}
