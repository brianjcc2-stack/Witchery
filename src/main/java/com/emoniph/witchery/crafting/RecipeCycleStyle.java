package com.emoniph.witchery.crafting;

import com.emoniph.witchery.item.ItemMirrorArmor;
import com.emoniph.witchery.item.ItemWitchesClothes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeCycleStyle implements IRecipe {

   @Override
   public boolean matches(InventoryCrafting inv, World world) {
      ItemStack target = null;
      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            if (target != null) {
               return false;
            }
            if (stack.getItem() instanceof ItemWitchesClothes || stack.getItem() instanceof ItemMirrorArmor) {
               target = stack;
            } else {
               return false;
            }
         }
      }
      return target != null;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack target = null;
      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null) {
            target = stack;
            break;
         }
      }
      if (target != null) {
         ItemStack result = target.copy();
         result.stackSize = 1;
         if (result.getItem() instanceof ItemMirrorArmor) {
            int style = ItemMirrorArmor.getStyle(result);
            ItemMirrorArmor.setStyle(result, (style + 1) % ItemMirrorArmor.NUM_STYLES);
         } else if (result.getItem() instanceof ItemWitchesClothes) {
            int style = ItemWitchesClothes.getStyle(result);
            ItemWitchesClothes.setStyle(result, (style + 1) % ItemWitchesClothes.NUM_STYLES);
         }
         return result;
      }
      return null;
   }

   @Override
   public int getRecipeSize() {
      return 1;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }
}
