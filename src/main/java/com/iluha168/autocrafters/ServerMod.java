package com.iluha168.autocrafters;

import com.iluha168.autocrafters.block.*;
import com.iluha168.autocrafters.block_entity.*;
import com.iluha168.autocrafters.screen_handler.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ServerMod implements ModInitializer {
	public static final String modId = "autocrafters";

	@Override
	public void onInitialize() {
		Identifier loomID = new Identifier(modId, "autoloom");
		Registry.register(Registries.BLOCK, loomID, AutoLoomBlock.BLOCK     );
		Registry.register(Registries.ITEM , loomID, AutoLoomBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, loomID, AutoLoomBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, loomID, AutoLoomScreenHandler.SCREEN_HANDLER);

		// 1.21 placed these right after the vanilla Crafter in the Redstone tab; the
		// Crafter does not exist in 1.20.1, so just append to the Redstone tab instead.
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.add(AutoLoomBlock.BLOCK_ITEM);
		});

		Identifier grindstoneID = new Identifier(modId, "autogrindstone");
		Registry.register(Registries.BLOCK, grindstoneID, AutoGrindstoneBlock.BLOCK     );
		Registry.register(Registries.ITEM , grindstoneID, AutoGrindstoneBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, grindstoneID, AutoGrindstoneBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, grindstoneID, AutoGrindstoneScreenHandler.SCREEN_HANDLER);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(AutoLoomBlock.BLOCK_ITEM, AutoGrindstoneBlock.BLOCK_ITEM);
		});

		// The fletching table is an easter egg: registered, but not added to any creative tab.
		Identifier fletchingID = new Identifier(modId, "autofletching");
		Registry.register(Registries.BLOCK, fletchingID, AutoFletchingTableBlock.BLOCK     );
		Registry.register(Registries.ITEM , fletchingID, AutoFletchingTableBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, fletchingID, AutoFletchingTableBlockEntity.BLOCK_ENTITY);
	}
}
