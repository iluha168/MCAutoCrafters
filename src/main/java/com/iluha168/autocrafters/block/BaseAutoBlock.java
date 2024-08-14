package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.BaseAutoBlockEntity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public abstract class BaseAutoBlock extends CrafterBlock {
    public BaseAutoBlock(Settings settings){
        super(settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.get(Properties.CRAFTING)){
            world.setBlockState(pos, state.with(Properties.CRAFTING, false));
            return;
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    protected void craft(BlockState state, ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof BaseAutoBlockEntity)) return;
        ItemStack outputStack = ((BaseAutoBlockEntity)blockEntity).craft();
        if(outputStack.equals(ItemStack.EMPTY)){
            world.syncWorldEvent(WorldEvents.CRAFTER_FAILS, pos, 0);
            return;
        }
        world.setBlockState(pos, state.with(Properties.CRAFTING, true));
        world.scheduleBlockTick(pos, this, 6);
        outputStack.onCraftByCrafter(world);

        // Transfer output
        Direction side = state.get(Properties.ORIENTATION).getFacing();
        Storage<ItemVariant> remoteInventory = ItemStorage.SIDED.find(world, pos.offset(side), side.getOpposite());

        if(remoteInventory != null){
            ItemVariant outputIV = ItemVariant.of(outputStack);
            try (Transaction transaction = Transaction.openOuter()){
                outputStack.decrement((int)remoteInventory.insert(outputIV, outputStack.getCount(), transaction));
                transaction.commit();
            }
        }
        // Throw output on the ground
        if(!outputStack.isEmpty()){
            world.syncWorldEvent(WorldEvents.CRAFTER_CRAFTS, pos, 0);
            world.syncWorldEvent(WorldEvents.CRAFTER_SHOOTS, pos, side.getId());
            ItemDispenserBehavior.spawnItem(world, outputStack, 6, side, pos.toCenterPos().offset(side, 0.7d));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) 
            return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BaseAutoBlockEntity) {
           return ((BaseAutoBlockEntity)blockEntity).getComparatorOutput();
        }
        return 0;
    }
}
