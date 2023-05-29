package net.minecraft.util.path;

import java.nio.file.Path;

public record SymlinkEntry(Path link, Path target) {
}
