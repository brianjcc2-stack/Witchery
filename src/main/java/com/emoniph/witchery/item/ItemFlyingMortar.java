package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.entity.EntityFlyingMortar;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

public class ItemFlyingMortar extends Item {

   public ItemFlyingMortar() {
      this.setMaxStackSize(1);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (player.ridingEntity instanceof EntityFlyingMortar) {
         EntityFlyingMortar mortar = (EntityFlyingMortar) player.ridingEntity;
         mortar.fireMortarShell(player);
         return stack;
      }

      float f = 1.0F;
      float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
      float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
      double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
      double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.yOffset;
      double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
      Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
      float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
      float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
      float f5 = -MathHelper.cos(-f1 * 0.017453292F);
      float f6 = MathHelper.sin(-f1 * 0.017453292F);
      float f7 = f4 * f5;
      float f8 = f3 * f5;
      double d3 = 5.0D;
      Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
      MovingObjectPosition mop = world.rayTraceBlocks(vec3, vec31, true);

      if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         int x = mop.blockX;
         int y = mop.blockY;
         int z = mop.blockZ;

         if (!world.isRemote) {
            EntityFlyingMortar mortar = new EntityFlyingMortar(world, (double)x + 0.5D, (double)y + 1.1D, (double)z + 0.5D);
            world.spawnEntityInWorld(mortar);
            world.playSoundAtEntity(mortar, "random.anvil_land", 0.6F, 1.4F);
         }

         if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
         }
      }

      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.epic;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.flyingmortar.tip1"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.flyingmortar.tip2"));
   }
}
