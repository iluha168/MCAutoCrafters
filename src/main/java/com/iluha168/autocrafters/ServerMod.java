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

public class ServerMod implements ModInitializer {
	public static final String modId = "autocrafters";

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK_ENTITY_TYPE, AutoLoomBlock.ID, AutoLoomBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, AutoLoomBlock.ID, AutoLoomScreenHandler.SCREEN_HANDLER);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, AutoGrindstoneBlock.ID, AutoGrindstoneBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, AutoGrindstoneBlock.ID, AutoGrindstoneScreenHandler.SCREEN_HANDLER);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, AutoCutterBlock.ID, AutoCutterBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, AutoCutterBlock.ID, AutoCutterScreenHandler.SCREEN_HANDLER);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, AutoFletchingTableBlock.ID, AutoFletchingTableBlockEntity.BLOCK_ENTITY);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content ->
			content.addAfter(Items.CRAFTER,
				AutoLoomBlock.ITEM,
				AutoGrindstoneBlock.ITEM,
				AutoCutterBlock.ITEM
			)
		);
	}
}