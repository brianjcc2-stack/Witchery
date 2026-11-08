package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemVoidLocket extends Item {

   public ItemVoidLocket() {
      this.setMaxStackSize(1);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         ExtendedPlayer playerEx = ExtendedPlayer.get(player);
         if (playerEx != null) {
            player.displayGUIChest(playerEx.getVoidVault());
            SoundEffect.MOB_ENDERMEN_PORTAL.playOnlyTo(player, 0.8F, 1.4F);
            ParticleEffect.PORTAL.send(SoundEffect.NONE, player, 0.5D, 1.0D, 16);
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
      list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("witchery.voidlocket.tip1"));
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.voidlocket.tip2"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.voidlocket.tip3"));
   }
}
