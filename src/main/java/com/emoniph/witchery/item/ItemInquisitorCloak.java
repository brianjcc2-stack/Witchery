package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemInquisitorCloak extends ItemHunterClothes {

   public ItemInquisitorCloak() {
      super(1, true, true);
      this.setMaxDamage(ArmorMaterial.DIAMOND.getDurability(1));
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.epic;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.inquisitorcloak.tip1"));
      list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("witchery.inquisitorcloak.tip2"));
   }

   public static boolean isInquisitorCloakWorn(EntityLivingBase entity) {
      if (entity == null) return false;
      ItemStack chest = entity.getEquipmentInSlot(3); // Slot 3 is Chestplate in vanilla getEquipmentInSlot (0: held, 1: boots, 2: legs, 3: chest, 4: helm)
      return chest != null && chest.getItem() instanceof ItemInquisitorCloak;
   }
}
