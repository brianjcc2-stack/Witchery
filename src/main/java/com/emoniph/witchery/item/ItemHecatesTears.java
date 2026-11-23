package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemHecatesTears extends ItemColored {

   public ItemHecatesTears(Block block) {
      super(block, false);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
      if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
         int i = mop.blockX;
         int j = mop.blockY;
         int k = mop.blockZ;
         if (!world.canMineBlock(player, i, j, k)) {
            return stack;
         }
         if (!player.canPlayerEdit(i, j, k, mop.sideHit, stack)) {
            return stack;
         }
         if (world.getBlock(i, j, k).getMaterial() == Material.water && world.getBlockMetadata(i, j, k) == 0 && world.isAirBlock(i, j + 1, k)) {
            world.setBlock(i, j + 1, k, Witchery.Blocks.HECATES_TEARS);
            if (!player.capabilities.isCreativeMode) {
               --stack.stackSize;
            }
         }
      }
      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getColorFromItemStack(ItemStack stack, int pass) {
      return 16777215;
   }
}
