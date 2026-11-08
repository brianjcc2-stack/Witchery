package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityBolt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemInquisitorCrossbow extends ItemHandBow {

   public ItemInquisitorCrossbow() {
      super();
      this.setMaxDamage(1536); // Twice as durable as normal handbow
   }

   @Override
   public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
      int prevBoltCount = count;
      super.onPlayerStoppedUsing(stack, world, player, count);

      if (!world.isRemote) {
         // Mark recently spawned bolts from this shooter
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 2.0D, player.posY - 1.0D, player.posZ - 2.0D, player.posX + 2.0D, player.posY + 3.0D, player.posZ + 2.0D);
         List bolts = world.getEntitiesWithinAABB(EntityBolt.class, bb);
         for (Object obj : bolts) {
            EntityBolt bolt = (EntityBolt) obj;
            if (bolt.shootingEntity == player && bolt.ticksExisted <= 2) {
               bolt.getEntityData().setBoolean("InquisitorBolt", true);
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.epic;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.inquisitorcrossbow.tip1"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.inquisitorcrossbow.tip2"));
   }
}
