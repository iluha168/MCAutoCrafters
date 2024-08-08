package com.iluha168.autocrafters;

import com.iluha168.autocrafters.block.*;
import com.iluha168.autocrafters.block_entity.*;
import com.iluha168.autocrafters.screen_handler.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ServerMod implements ModInitializer {
	public static final String modId = "autocrafters";

	@Override
	public void onInitialize() {
		Identifier loomID = Identifier.of(modId, "autoloom");
		Registry.register(Registries.BLOCK, loomID, AutoLoomBlock.BLOCK     );
		Registry.register(Registries.ITEM , loomID, AutoLoomBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, loomID, AutoLoomBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, loomID, AutoLoomScreenHandler.SCREEN_HANDLER);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(Items.CRAFTER, AutoLoomBlock.BLOCK_ITEM);
		});

		Identifier grindstoneID = Identifier.of(modId, "autogrindstone");
		Registry.register(Registries.BLOCK, grindstoneID, AutoGrindstoneBlock.BLOCK     );
		Registry.register(Registries.ITEM , grindstoneID, AutoGrindstoneBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, grindstoneID, AutoGrindstoneBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, grindstoneID, AutoGrindstoneScreenHandler.SCREEN_HANDLER);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(AutoLoomBlock.BLOCK_ITEM, AutoGrindstoneBlock.BLOCK_ITEM);
		});

		Identifier fletchingID = Identifier.of(modId, "autofletching");
		Registry.register(Registries.BLOCK, fletchingID, AutoFletchingTableBlock.BLOCK     );
		Registry.register(Registries.ITEM , fletchingID, AutoFletchingTableBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, fletchingID, AutoFletchingTableBlockEntity.BLOCK_ENTITY);
	}
}