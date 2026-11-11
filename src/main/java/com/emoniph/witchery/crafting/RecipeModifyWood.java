package com.emoniph.witchery.crafting;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.item.ItemMysticBranch;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeModifyWood implements IRecipe {

   @Override
   public boolean matches(InventoryCrafting inv, World world) {
      ItemStack targetStack = null;
      int modifierWood = -1;
      int totalItems = 0;

      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            totalItems++;
            if (stack.getItem() == Witchery.Items.MYSTIC_BRANCH || 
                Witchery.Items.GENERIC.itemBroom.isMatch(stack) || 
                Witchery.Items.GENERIC.itemBroomEnchanted.isMatch(stack)) {
               if (targetStack != null) {
                  return false;
               }
               targetStack = stack;
            } else if (stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.SAPLING) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.LOG) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.PLANKS)) {
               if (modifierWood != -1) {
                  return false;
               }
               int meta = stack.getItemDamage() & 3;
               if (meta >= 0 && meta <= 2) {
                  modifierWood = meta;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      return totalItems == 2 && targetStack != null && modifierWood != -1;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack targetStack = null;
      int modifierWood = -1;

      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            if (stack.getItem() == Witchery.Items.MYSTIC_BRANCH || 
                Witchery.Items.GENERIC.itemBroom.isMatch(stack) || 
                Witchery.Items.GENERIC.itemBroomEnchanted.isMatch(stack)) {
               targetStack = stack;
            } else if (stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.SAPLING) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.LOG) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.PLANKS)) {
               int meta = stack.getItemDamage() & 3;
               if (meta >= 0 && meta <= 2) {
                  modifierWood = meta;
               }
            }
         }
      }

      if (targetStack != null && modifierWood != -1) {
         ItemStack result = targetStack.copy();
         result.stackSize = 1;
         if (result.getItem() == Witchery.Items.MYSTIC_BRANCH) {
            ItemMysticBranch.setWoodType(result, modifierWood);
         } else {
            Witchery.Items.GENERIC.setBroomWoodType(result, modifierWood);
         }
         return result;
      }

      return null;
   }

   @Override
   public int getRecipeSize() {
      return 2;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }
}
