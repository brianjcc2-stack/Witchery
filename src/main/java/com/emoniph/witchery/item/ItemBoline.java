package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.util.BlockUtil;
import com.emoniph.witchery.util.ItemUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class ItemBoline extends ItemSword {

   public static final Block[] blocksEffectiveAgainst = new Block[]{Blocks.planks, Blocks.bookshelf, Blocks.planks, Blocks.chest, Blocks.stone_slab, Blocks.pumpkin, Blocks.lit_pumpkin};
   private float effectiveWeaponDamage;


   public ItemBoline() {
      super(ToolMaterial.IRON);
      this.effectiveWeaponDamage = 4.0F + ToolMaterial.WOOD.getDamageVsEntity();
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.uncommon;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean moreTips) {
      String localText = Witchery.resource("item.witchery:boline.tip");
      if(localText != null) {
         String[] arr$ = localText.split("\n");
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            if(!s.isEmpty()) {
               list.add(s);
            }
         }
      }
      list.add(net.minecraft.util.EnumChatFormatting.DARK_GREEN + net.minecraft.util.StatCollector.translateToLocal("witchery.boline.tip.carve"));
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      Block block = world.getBlock(x, y, z);
      if (block == Blocks.pumpkin) {
         if (!world.isRemote) {
            world.setBlock(x, y, z, Blocks.lit_pumpkin, 0, 3);
            world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "step.wood", 1.0F, 1.2F);
            world.spawnEntityInWorld(new EntityItem(world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, new ItemStack(net.minecraft.init.Items.pumpkin_seeds, 4)));
            com.emoniph.witchery.util.ParticleEffect.CRIT.send(com.emoniph.witchery.util.SoundEffect.NONE, world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, 0.4D, 0.4D, 6);
            stack.damageItem(1, player);
         }
         return true;
      }

      if (block == Witchery.Blocks.LOG || block == Blocks.log || block == Blocks.log2) {
         if (!world.isRemote) {
            world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "dig.wood", 1.0F, 1.4F);
            com.emoniph.witchery.util.ParticleEffect.CRIT.send(com.emoniph.witchery.util.SoundEffect.NONE, world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, 0.3D, 0.3D, 5);
            if (world.rand.nextFloat() <= 0.50F) {
               ItemStack drop = (block == Witchery.Blocks.LOG) ? Witchery.Items.GENERIC.itemRowanBerries.createStack() : new ItemStack(net.minecraft.init.Items.stick, 1);
               world.spawnEntityInWorld(new EntityItem(world, (double) x + 0.5D, (double) y + 0.8D, (double) z + 0.5D, drop));
            }
            com.emoniph.witchery.util.ChatUtil.sendTranslated(net.minecraft.util.EnumChatFormatting.DARK_GREEN, player, "witchery.boline.carved");
            stack.damageItem(1, player);
         }
         return true;
      }

      return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
   }

   public Multimap getItemAttributeModifiers() {
      HashMultimap multimap = HashMultimap.create();
      multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(Item.field_111210_e, "Weapon modifier", (double)this.effectiveWeaponDamage, 0));
      return multimap;
   }

   public float func_150931_i() {
      return ToolMaterial.WOOD.getDamageVsEntity();
   }

   public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int posX, int posY, int posZ, EntityLivingBase entity) {
      if(block != null && block != Blocks.leaves && block != Blocks.web && block != Blocks.tallgrass && block != Blocks.vine && block != Blocks.tripwire && !(block instanceof IShearable) && block.getBlockHardness(world, posX, posY, posZ) != 0.0F) {
         stack.damageItem(2, entity);
      }

      return true;
   }

   public boolean canHarvestBlock(Block par1Block, ItemStack stack) {
      return par1Block == Witchery.Blocks.WEB || par1Block == Blocks.web || par1Block == Blocks.redstone_wire || par1Block == Blocks.tripwire;
   }

   public float func_150893_a(ItemStack stack, Block block) {
      return block != Witchery.Blocks.WEB && block != Blocks.web && block != Blocks.leaves?(block != Blocks.wool && block != Witchery.Blocks.TRAPPED_PLANT?super.func_150893_a(stack, block):5.0F):15.0F;
   }

   public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
      if(entity.worldObj.isRemote) {
         return false;
      } else if(!(entity instanceof IShearable)) {
         return false;
      } else {
         IShearable target = (IShearable)entity;
         if(target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
            ArrayList drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
            Random rand = new Random();

            EntityItem ent;
            for(Iterator i$ = drops.iterator(); i$.hasNext(); ent.motionZ += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)) {
               ItemStack stack = (ItemStack)i$.next();
               ent = entity.entityDropItem(stack, 1.0F);
               ent.motionY += (double)(rand.nextFloat() * 0.05F);
               ent.motionX += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
            }

            itemstack.damageItem(1, entity);
         }

         return true;
      }
   }

   public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
      if(player.worldObj.isRemote) {
         return false;
      } else {
         World world = player.worldObj;
         Block block = BlockUtil.getBlock(world, x, y, z);
         if(block == null) {
            return false;
         } else if(block == Blocks.web) {
            world.setBlockToAir(x, y, z);
            world.spawnEntityInWorld(new EntityItem(world, 0.5D + (double)x, 0.5D + (double)y, 0.5D + (double)z, new ItemStack(block)));
            this.onBlockDestroyed(itemstack, world, block, x, y, z, player);
            if(itemstack.stackSize == 0) {
               player.destroyCurrentEquippedItem();
            }

            return true;
         } else {
            int target1;
            if(block == Witchery.Blocks.TRAPPED_PLANT) {
               target1 = world.getBlockMetadata(x, y, z);
               world.setBlockToAir(x, y, z);
               world.spawnEntityInWorld(new EntityItem(world, 0.5D + (double)x, 0.5D + (double)y, 0.5D + (double)z, new ItemStack(block, 1, target1)));
               this.onBlockDestroyed(itemstack, world, block, x, y, z, player);
               if(itemstack.stackSize == 0) {
                  player.destroyCurrentEquippedItem();
               }

               return true;
            } else if(block == Witchery.Blocks.BLOOD_ROSE || block == Witchery.Blocks.MOON_ROSE || block == Witchery.Blocks.SUN_ROSE || block == Witchery.Blocks.GRAVE_ROSE || block == Witchery.Blocks.BANSHEE_ROSE || block == Witchery.Blocks.HECATES_TEARS) {
               target1 = world.getBlockMetadata(x, y, z);
               world.setBlockToAir(x, y, z);
               world.spawnEntityInWorld(new EntityItem(world, 0.5D + (double)x, 0.5D + (double)y, 0.5D + (double)z, new ItemStack(block, 1, target1)));
               this.onBlockDestroyed(itemstack, world, block, x, y, z, player);
               if(itemstack.stackSize == 0) {
                  player.destroyCurrentEquippedItem();
               }

               return true;
            } else {
               if(block instanceof IShearable) {
                  IShearable target = (IShearable)block;
                  if(target.isShearable(itemstack, player.worldObj, x, y, z)) {
                     ArrayList drops = target.onSheared(itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                     Random rand = new Random();
                     Iterator i$ = drops.iterator();

                     while(i$.hasNext()) {
                        ItemStack stack = (ItemStack)i$.next();
                        float f = 0.7F;
                        double d = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                        entityitem.delayBeforeCanPickup = 10;
                        player.worldObj.spawnEntityInWorld(entityitem);
                     }

                     itemstack.damageItem(1, player);
                  }
               }

               return false;
            }
         }
      }
   }

}
