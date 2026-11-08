package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.entity.EntityMirrorClone;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

public class ItemQuicksilverMirror extends Item {

   private static final int COOLDOWN_TICKS = 900; // 45 seconds

   public ItemQuicksilverMirror() {
      this.setMaxStackSize(1);
      this.setMaxDamage(64);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
         }

         long readyTime = stack.getTagCompound().getLong("ReadyTime");
         long now = world.getTotalWorldTime();

         if (now < readyTime && !player.capabilities.isCreativeMode) {
            long remainingSecs = (readyTime - now) / 20L + 1L;
            SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.5F);
            return stack;
         }

         EntityMirrorClone clone = new EntityMirrorClone(world, player);
         world.spawnEntityInWorld(clone);

         ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_GLASS, world, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.2D, 16, 0x88CCFF);
         stack.getTagCompound().setLong("ReadyTime", now + COOLDOWN_TICKS);
         stack.damageItem(1, player);
      }

      player.swingItem();
      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.quicksilvermirror.tip1"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.quicksilvermirror.tip2"));

      if (stack.getTagCompound() != null) {
         long readyTime = stack.getTagCompound().getLong("ReadyTime");
         long now = player.worldObj.getTotalWorldTime();
         if (now < readyTime) {
            long remaining = (readyTime - now) / 20L + 1L;
            list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("witchery.cooldown.remaining", remaining));
         }
      }
   }
}
