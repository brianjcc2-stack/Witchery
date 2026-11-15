package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.client.model.ModelMirrorArmor;
import com.emoniph.witchery.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.List;

public class ItemMirrorArmor extends ItemArmor implements ISpecialArmor {

   public static final int STYLE_CROWN_IMPERIAL = 0;
   public static final int STYLE_CROWN_TIARA = 1;
   public static final int STYLE_CROWN_SOLAR = 2;
   public static final int NUM_STYLES = 3;

   private int realDamageReduction;

   @SideOnly(Side.CLIENT)
   private ModelMirrorArmor modelHelm;
   @SideOnly(Side.CLIENT)
   private ModelMirrorArmor modelChest;

   public ItemMirrorArmor(int armorSlot) {
      super(ArmorMaterial.CLOTH, 1, armorSlot);
      this.setMaxDamage(ArmorMaterial.DIAMOND.getDurability(armorSlot));
      this.realDamageReduction = ArmorMaterial.DIAMOND.getDamageReductionAmount(armorSlot);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public int getItemEnchantability() {
      return ArmorMaterial.DIAMOND.getEnchantability();
   }

   @Override
   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      return new ArmorProperties(0, (double)this.realDamageReduction / 25.0D, armor.getMaxDamage() + 1 - armor.getItemDamage());
   }

   @Override
   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return this.realDamageReduction;
   }

   @Override
   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      stack.damageItem(damage, entity);
   }

   @Override
   public boolean hasColor(ItemStack stack) {
      if (stack == null || !stack.hasTagCompound()) {
         return false;
      }
      NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
      return display != null && display.hasKey("color", 3);
   }

   @Override
   public int getColor(ItemStack stack) {
      if (!this.hasColor(stack)) {
         return -1;
      }
      return stack.getTagCompound().getCompoundTag("display").getInteger("color");
   }

   @Override
   public void removeColor(ItemStack stack) {
      if (stack != null && stack.hasTagCompound()) {
         NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
         if (display != null && display.hasKey("color")) {
            display.removeTag("color");
         }
      }
   }

   @Override
   public void func_82813_b(ItemStack stack, int color) {
      this.setColor(stack, color);
   }

   public void setColor(ItemStack stack, int color) {
      if (stack != null) {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
         if (!stack.getTagCompound().hasKey("display", 10)) {
            stack.getTagCompound().setTag("display", display);
         }
         display.setInteger("color", color);
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getColorFromItemStack(ItemStack stack, int par2) {
      if (this.hasColor(stack)) {
         return this.getColor(stack);
      }
      return 16777215;
   }

   public static int getStyle(ItemStack stack) {
      if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("WitchStyle")) {
         return Math.abs(stack.getTagCompound().getInteger("WitchStyle")) % NUM_STYLES;
      }
      return STYLE_CROWN_IMPERIAL;
   }

   public static void setStyle(ItemStack stack, int style) {
      if (stack != null) {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         stack.getTagCompound().setInteger("WitchStyle", Math.abs(style) % NUM_STYLES);
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (player.isSneaking() && this.armorType == 0) {
         if (!world.isRemote) {
            int currentStyle = getStyle(stack);
            int newStyle = (currentStyle + 1) % NUM_STYLES;
            setStyle(stack, newStyle);
            String styleName = StatCollector.translateToLocal("witchery.mirrorarmor.style." + newStyle);
            player.addChatMessage(new ChatComponentText(
               EnumChatFormatting.LIGHT_PURPLE + "[Witchery] " + 
               EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.style.label") + ": " +
               EnumChatFormatting.AQUA + styleName
            ));
         } else {
            world.playSound(player.posX, player.posY, player.posZ, "witchery:random.spell", 0.8F, 1.3F, false);
            for (int i = 0; i < 8; i++) {
               world.spawnParticle("magicCrit", player.posX + (world.rand.nextDouble() - 0.5D), player.posY + 1.2D + (world.rand.nextDouble() - 0.5D), player.posZ + (world.rand.nextDouble() - 0.5D), 0, 0.1D, 0);
            }
         }
         return stack;
      }
      return super.onItemRightClick(stack, world, player);
   }

   @Override
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "witchery:textures/entities/mirrorarmor.png";
   }

   @SideOnly(Side.CLIENT)
   @Override
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
      if (armorSlot == 0) {
         if (this.modelHelm == null) {
            this.modelHelm = new ModelMirrorArmor(0.2F, 0);
         }
         this.modelHelm.prepareModel(entityLiving, stack, armorSlot);
         return this.modelHelm;
      } else if (armorSlot == 1) {
         if (this.modelChest == null) {
            this.modelChest = new ModelMirrorArmor(0.4F, 1);
         }
         this.modelChest.prepareModel(entityLiving, stack, armorSlot);
         return this.modelChest;
      }
      return null;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      if (this.armorType == 0) {
         int style = getStyle(stack);
         String styleName = StatCollector.translateToLocal("witchery.mirrorarmor.style." + style);
         list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.style.label") + ": " + EnumChatFormatting.AQUA + styleName);
         list.add(EnumChatFormatting.DARK_GRAY + "(" + StatCollector.translateToLocal("witchery.style.sneak_click_tip") + ")");
      }
      if (this.hasColor(stack)) {
         list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
      }
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
