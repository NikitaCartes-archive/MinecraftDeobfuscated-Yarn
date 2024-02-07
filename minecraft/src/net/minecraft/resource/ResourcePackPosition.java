package net.minecraft.resource;

public record ResourcePackPosition(boolean required, ResourcePackProfile.InsertionPosition defaultPosition, boolean fixedPosition) {
}
