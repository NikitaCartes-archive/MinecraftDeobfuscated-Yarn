package net.minecraft.resource;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourceNotFoundException extends FileNotFoundException {
	public ResourceNotFoundException(File file, String string) {
		super(String.format("'%s' in ResourcePack '%s'", string, file));
	}
}
