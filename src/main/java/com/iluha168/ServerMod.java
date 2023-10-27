package com.iluha168;

import com.iluha168.block.AutoLoomBlock;
import com.iluha168.block_entity.AutoLoomBlockEntity;
import com.iluha168.screen_handler.AutoLoomScreenHandler;

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
		Identifier loomID = new Identifier(modId, "autoloom");
		Registry.register(Registries.BLOCK, loomID, AutoLoomBlock.BLOCK     );
		Registry.register(Registries.ITEM , loomID, AutoLoomBlock.BLOCK_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, loomID, AutoLoomBlockEntity.BLOCK_ENTITY);
        Registry.register(Registries.SCREEN_HANDLER, loomID, AutoLoomScreenHandler.SCREEN_HANDLER);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.add(AutoLoomBlock.BLOCK_ITEM);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
			content.addAfter(Items.LOOM, AutoLoomBlock.BLOCK_ITEM);
		});
	}
}