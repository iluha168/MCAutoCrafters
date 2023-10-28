package com.iluha168.block;

import org.jetbrains.annotations.Nullable;

import com.iluha168.block_entity.BaseAutoBlockEntity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public abstract class BaseAutoBlock extends BlockWithEntity {
    public BaseAutoBlock(Settings settings){
        super(settings);
        setDefaultState(
            getDefaultState()
            .with(Properties.POWERED, false)
            .with(Properties.TRIGGERED, false)
            .with(Properties.FACING, Direction.NORTH)
        );
    }

    public abstract SoundEvent getCraftSound();

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isRedstoneOn = world.isReceivingRedstonePower(pos);
        boolean isPowered = state.get(Properties.POWERED);
        if (isRedstoneOn && !isPowered) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, state
                .with(Properties.POWERED, true)
                .with(Properties.TRIGGERED, true)
            );
        } else if (!isRedstoneOn && isPowered) {
            world.setBlockState(pos, state.with(Properties.POWERED, false));
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state
            .with(Properties.TRIGGERED, false)
        );
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof BaseAutoBlockEntity)) return;
        ItemStack outputStack = ((BaseAutoBlockEntity)blockEntity).craft();
        if(outputStack.equals(ItemStack.EMPTY)) return;

        Direction side = state.get(Properties.FACING);
        Storage<ItemVariant> remoteInventory = ItemStorage.SIDED.find(world, pos.offset(side), side);

        if(remoteInventory != null){
            ItemVariant outputIV = ItemVariant.of(outputStack);
            try (Transaction transaction = Transaction.openOuter()){
                outputStack.decrement((int)remoteInventory.insert(outputIV, outputStack.getCount(), transaction));
                transaction.commit();
            }
        }
        if(outputStack.getCount() > 0){
            world.syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, side.getId());
            world.playSound(null, pos, getCraftSound(), SoundCategory.BLOCKS);
            ItemDispenserBehavior.spawnItem(world, outputStack, 6, side, 
                DispenserBlock.getOutputLocation(new BlockPointerImpl(world, pos))
            );
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) 
            return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.CONSUME;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BaseAutoBlockEntity) {
                ItemScatterer.spawn(world, pos, ((BaseAutoBlockEntity)blockEntity).stacks);
                world.updateComparators(pos,this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BaseAutoBlockEntity) {
           return ((BaseAutoBlockEntity)blockEntity).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        neighborUpdate(state, world, pos, this, pos, false);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWERED, Properties.TRIGGERED, Properties.FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.FACING)));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
}
