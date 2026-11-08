package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemMirrorArmor extends ItemArmor {

   public ItemMirrorArmor(int armorSlot) {
      super(ArmorMaterial.DIAMOND, 1, armorSlot);
      this.setMaxDamage(ArmorMaterial.DIAMOND.getDurability(armorSlot));
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "witchery:textures/entities/hunterclothes_overlay.png";
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.mirrorarmor.tip1"));
      list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("witchery.mirrorarmor.tip2"));
   }

   public static boolean isMirrorArmorWorn(EntityLivingBase entity) {
      if (entity == null) return false;
      for (int i = 1; i <= 4; ++i) {
         ItemStack stack = entity.getEquipmentInSlot(i);
         if (stack != null && stack.getItem() instanceof ItemMirrorArmor) {
            return true;
         }
      }
      return false;
   }
}
