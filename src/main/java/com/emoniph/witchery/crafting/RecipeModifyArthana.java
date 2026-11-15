package com.emoniph.witchery.crafting;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.item.ItemArthana;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeModifyArthana implements IRecipe {

   @Override
   public boolean matches(InventoryCrafting inv, World world) {
      ItemStack targetStack = null;
      int modifierHilt = -1;
      int totalItems = 0;

      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            totalItems++;
            if (stack.getItem() == Witchery.Items.ARTHANA) {
               if (targetStack != null) {
                  return false;
               }
               targetStack = stack;
            } else if (stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.SAPLING) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.LOG) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.PLANKS)) {
               if (modifierHilt != -1) {
                  return false;
               }
               int meta = stack.getItemDamage() & 3;
               if (meta == 0) modifierHilt = 1; // Rowan
               else if (meta == 1) modifierHilt = 2; // Alder
               else if (meta == 2) modifierHilt = 3; // Hawthorn
            } else if (Witchery.Items.GENERIC.itemDemonHeart.isMatch(stack) ||
                       stack.getItem() == Items.bone ||
                       (stack.getItem() == Items.skull && stack.getItemDamage() == 1)) {
               if (modifierHilt != -1) {
                  return false;
               }
               modifierHilt = 4; // Demon Bone
            } else if (Witchery.Items.GENERIC.itemSilverDust.isMatch(stack) ||
                       stack.getItem() == Witchery.Items.SILVER_SWORD) {
               if (modifierHilt != -1) {
                  return false;
               }
               modifierHilt = 5; // Silver
            } else if (stack.getItem() == Items.stick || stack.getItem() == Items.gold_ingot) {
               if (modifierHilt != -1) {
                  return false;
               }
               modifierHilt = 0; // Default / Reset
            } else {
               return false;
            }
         }
      }

      return totalItems == 2 && targetStack != null && modifierHilt != -1;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack targetStack = null;
      int modifierHilt = -1;

      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            if (stack.getItem() == Witchery.Items.ARTHANA) {
               targetStack = stack;
            } else if (stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.SAPLING) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.LOG) ||
                       stack.getItem() == Item.getItemFromBlock(Witchery.Blocks.PLANKS)) {
               int meta = stack.getItemDamage() & 3;
               if (meta == 0) modifierHilt = 1;
               else if (meta == 1) modifierHilt = 2;
               else if (meta == 2) modifierHilt = 3;
            } else if (Witchery.Items.GENERIC.itemDemonHeart.isMatch(stack) ||
                       stack.getItem() == Items.bone ||
                       (stack.getItem() == Items.skull && stack.getItemDamage() == 1)) {
               modifierHilt = 4;
            } else if (Witchery.Items.GENERIC.itemSilverDust.isMatch(stack) ||
                       stack.getItem() == Witchery.Items.SILVER_SWORD) {
               modifierHilt = 5;
            } else if (stack.getItem() == Items.stick || stack.getItem() == Items.gold_ingot) {
               modifierHilt = 0;
            }
         }
      }

      if (targetStack != null && modifierHilt != -1) {
         ItemStack result = targetStack.copy();
         result.stackSize = 1;
         ItemArthana.setHiltType(result, modifierHilt);
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
