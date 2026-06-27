package com.iluha168.autocrafters.block;

import org.jetbrains.annotations.Nullable;

import com.iluha168.autocrafters.block_entity.BaseAutoBlockEntity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public abstract class BaseAutoBlock extends BlockWithEntity {
    // CRAFTING property does not exist in 1.20.1, so we define our own.
    public static final BooleanProperty CRAFTING = BooleanProperty.of("crafting");

    public BaseAutoBlock(Settings settings){
        super(settings);
        setDefaultState(
            getDefaultState()
            .with(Properties.TRIGGERED, false)
            .with(CRAFTING, false)
            .with(Properties.ORIENTATION, JigsawOrientation.NORTH_UP)
        );
    }

    public abstract SoundEvent getCraftSound();

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isRedstoneOn = world.isReceivingRedstonePower(pos);
        boolean isPowered = state.get(Properties.TRIGGERED);
        if (isRedstoneOn && !isPowered) {
            if(!state.get(CRAFTING))
                world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, state.with(Properties.TRIGGERED, true), Block.NOTIFY_LISTENERS);
        } else if (!isRedstoneOn && isPowered) {
            world.setBlockState(pos, state.with(Properties.TRIGGERED, false), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.get(CRAFTING)){
            world.setBlockState(pos, state
                .with(CRAFTING, false)
            );
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof BaseAutoBlockEntity)) return;
        ItemStack outputStack = ((BaseAutoBlockEntity)blockEntity).craft();
        if(outputStack.isEmpty()){
            return;
        } else { // "else" just for style points
            world.setBlockState(pos, state
                .with(CRAFTING, true)
            );
            world.scheduleBlockTick(pos, this, 4);
            world.playSound(null, pos, getCraftSound(), SoundCategory.BLOCKS);
        }

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
        if(outputStack.getCount() > 0){
            world.syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, side.getId());
            ItemDispenserBehavior.spawnItem(world, outputStack, 6, side,
                pos.toCenterPos().add(side.getOffsetX()*0.7d, side.getOffsetY()*0.7d, side.getOffsetZ()*0.7d)
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
        Direction facing = ctx.getPlayerLookDirection().getOpposite();
        return this.getDefaultState().with(Properties.ORIENTATION, JigsawOrientation.byDirections(
            facing,
            switch(facing){
                case DOWN -> ctx.getHorizontalPlayerFacing().getOpposite();
                case UP -> ctx.getHorizontalPlayerFacing();
                case NORTH, SOUTH, WEST, EAST -> Direction.UP;
            }
        ));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.TRIGGERED, CRAFTING, Properties.ORIENTATION);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(Properties.ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(Properties.ORIENTATION, mirror  .getDirectionTransformation().mapJigsawOrientation(state.get(Properties.ORIENTATION)));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
}
