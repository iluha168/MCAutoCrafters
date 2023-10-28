package com.iluha168.block_entity;

import java.util.Map;
import java.util.stream.Collectors;

import com.iluha168.block.AutoGrindstoneBlock;
import com.iluha168.screen_handler.AutoGrindstoneScreenHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoGrindstoneBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0,1};
    public static final BlockEntityType<AutoGrindstoneBlockEntity> BLOCK_ENTITY = FabricBlockEntityTypeBuilder
        .create(AutoGrindstoneBlockEntity::new, AutoGrindstoneBlock.BLOCK)
        .build();

    public AutoGrindstoneBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, 2);
    }

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
			if(index != 0) throw new ArrayIndexOutOfBoundsException();
            return world.getBlockState(pos).get(Properties.POWERED)? 1:0;
        }

        @Override
        public void set(int index, int value) {
        	throw new ArrayIndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 1;
        }
    };

	public static boolean canBeGrinded(ItemStack stack){
		return stack.isEmpty() || stack.hasEnchantments() || stack.isOf(Items.ENCHANTED_BOOK)
            || stack.isDamageable();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return canBeGrinded(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ALL_SLOTS;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoGrindstoneScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

    public static ItemStack transferEnchantments(ItemStack target, ItemStack source) {
        ItemStack itemStack = target.copy();
        for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.get(source).entrySet()){
            Enchantment enchantment = entry.getKey();
            if(enchantment.isCursed() || EnchantmentHelper.getLevel(enchantment, itemStack) == 0)
                continue;
            itemStack.addEnchantment(enchantment, entry.getValue());
        }
        return itemStack;
    }

    public static int getGrindXPValue(ItemStack stack) {
        int xp = 0;
        for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.get(stack).entrySet()){
            Enchantment enchantment = entry.getKey();
            if(enchantment.isCursed())
                continue;
            xp += enchantment.getMinPower(EnchantmentHelper.getLevel(enchantment, stack));
        }
        return xp;
    }

    public static ItemStack grind(ItemStack stack) {
        ItemStack outStack = stack.copy();
        outStack.removeSubNbt("Enchantments");
        outStack.removeSubNbt("StoredEnchantments");
        Map<Enchantment, Integer> map =
            EnchantmentHelper.get(stack)
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().isCursed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        EnchantmentHelper.set(map, outStack);

        outStack.setRepairCost(0);
        if(outStack.isOf(Items.ENCHANTED_BOOK) && map.size() == 0) {
            outStack = new ItemStack(Items.BOOK, outStack.getCount());
            if (stack.hasCustomName())
                outStack.setCustomName(stack.getName());
        }

        for(int i = 0; i < map.size(); i++)
           outStack.setRepairCost(AnvilScreenHandler.getNextCost(outStack.getRepairCost()));
        return outStack;
    }

    public static ItemStack grind(ItemStack stack1, ItemStack stack2){
        boolean isAStackEmpty = stack1.isEmpty() || stack2.isEmpty();
        if(stack1.isEmpty() && stack2.isEmpty())
            return ItemStack.EMPTY;
        if(stack1.isEmpty() && stack2.isEmpty() ||
           stack1.getCount() > 1 || stack2.getCount() > 1 ||
           !canBeGrinded(stack1) || !canBeGrinded(stack2)
        )   return ItemStack.EMPTY;
        
        int newDamage;
        ItemStack outStack;
        if (!isAStackEmpty) {
            if(!stack1.isOf(stack2.getItem()))
                return ItemStack.EMPTY;
            Item item = stack1.getItem();
            newDamage = Math.max(stack1.getDamage()+stack2.getDamage()-item.getMaxDamage()*21/20, 0);
            outStack = transferEnchantments(stack1, stack2);
            if(!outStack.isDamageable()) {
                if (!ItemStack.areEqual(stack1, stack2))
                    return ItemStack.EMPTY;
                outStack.setCount(2);
            }
        } else {
            boolean isStack1Empty = stack1.isEmpty();
            newDamage = isStack1Empty ? stack2.getDamage() : stack1.getDamage();
            outStack = isStack1Empty ? stack2 : stack1;
        }  
        if(newDamage > 0) outStack.setDamage(newDamage);
        else outStack.removeSubNbt("Damage");
        return grind(outStack);
    }

	@Override
	public ItemStack craft() {
        ItemStack inputStack0 = getStack(0);
        ItemStack inputStack1 = getStack(1);
        int xp = (int) Math.ceil((float)(getGrindXPValue(inputStack0)+getGrindXPValue(inputStack1)) * 0.5f);
        if(xp > 0){
            xp = world.getRandom().nextBetween(xp, 2*xp-1);
            ExperienceOrbEntity.spawn((ServerWorld)getWorld(), pos.toCenterPos().offset(world.getBlockState(pos).get(Properties.FACING), 0.4d), xp);
        }
        ItemStack outStack = grind(inputStack0, inputStack1);
        if(!outStack.equals(ItemStack.EMPTY)){
            inputStack0.split(1);
            inputStack1.split(1);
        }
        return outStack;
	}
}
