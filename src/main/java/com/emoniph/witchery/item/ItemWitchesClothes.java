package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.blocks.BlockVoidBramble;
import com.emoniph.witchery.client.model.ModelWitchesClothes;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.item.ItemGeneral;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.Config;
import com.emoniph.witchery.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemWitchesClothes extends ItemArmor {

   public static final int STYLE_CLASSIC = 0;
   public static final int STYLE_ARCHMAGE = 1;
   public static final int STYLE_DRUID = 2;
   public static final int STYLE_VOID = 3;
   public static final int STYLE_GOTHIC = 4;
   public static final int NUM_STYLES = 5;

   public static int getStyle(ItemStack stack) {
      if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("WitchStyle")) {
         return Math.abs(stack.getTagCompound().getInteger("WitchStyle")) % NUM_STYLES;
      }
      return STYLE_CLASSIC;
   }

   public static void setStyle(ItemStack stack, int style) {
      if(stack != null) {
         if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         stack.getTagCompound().setInteger("WitchStyle", Math.abs(style) % NUM_STYLES);
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if(player.isSneaking()) {
         if(!world.isRemote) {
            int currentStyle = getStyle(stack);
            int newStyle = (currentStyle + 1) % NUM_STYLES;
            setStyle(stack, newStyle);
            String typeKey;
            String styleKey;
            if(this.armorType == 0) {
               typeKey = "witchery.style.hat";
               styleKey = "witchery.style.hat." + newStyle;
            } else if(this.armorType == 1) {
               typeKey = "witchery.style.robe";
               styleKey = "witchery.style.robe." + newStyle;
            } else if(this.armorType == 2) {
               typeKey = "witchery.style.belt";
               styleKey = "witchery.style.belt." + newStyle;
            } else {
               typeKey = "witchery.style.shoe";
               styleKey = "witchery.style.shoe." + newStyle;
            }

            player.addChatMessage(new net.minecraft.util.ChatComponentText(
               EnumChatFormatting.LIGHT_PURPLE + "[Witchery] " + 
               EnumChatFormatting.GRAY + StatCollector.translateToLocal(typeKey) + ": " +
               EnumChatFormatting.GOLD + StatCollector.translateToLocal(styleKey)
            ));
         } else {
            world.playSound(player.posX, player.posY, player.posZ, "witchery:random.spell", 0.7F, 1.2F, false);
            for(int i = 0; i < 8; ++i) {
               world.spawnParticle("enchantmenttable", player.posX + (world.rand.nextDouble() - 0.5D), player.posY + 1.2D + (world.rand.nextDouble() - 0.5D), player.posZ + (world.rand.nextDouble() - 0.5D), (world.rand.nextDouble() - 0.5D) * 0.2D, 0.1D, (world.rand.nextDouble() - 0.5D) * 0.2D);
            }
         }
         return stack;
      }
      return super.onItemRightClick(stack, world, player);
   }

   private static final int CHARGE_PER_PIECE = 2;
   @SideOnly(Side.CLIENT)
   private ModelWitchesClothes modelClothesChest;
   @SideOnly(Side.CLIENT)
   private ModelWitchesClothes modelNecroChest;
   @SideOnly(Side.CLIENT)
   private ModelWitchesClothes modelClothesLegs;
   @SideOnly(Side.CLIENT)
   private ModelWitchesClothes modelClothesFeet;
   private static final String BIBLIOCRAFT_ARMOR_STAND_ENTITY_NAME = "AbstractSteve";
   private static String noPlaceLikeHome = null;
   private static final double WILD_EFFECT_CHANCE = 0.01D;


   public ItemWitchesClothes(int armorSlot) {
      super(ArmorMaterial.CLOTH, 1, armorSlot);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return stack != null && (stack.getItem() == Witchery.Items.WITCH_HAT || stack.getItem() == Witchery.Items.WITCH_ROBES || stack.getItem() == Witchery.Items.NECROMANCERS_ROBES || stack.getItem() == Witchery.Items.ICY_SLIPPERS || stack.getItem() == Witchery.Items.RUBY_SLIPPERS || stack.getItem() == Witchery.Items.SEEPING_SHOES || stack.getItem() == Witchery.Items.BABAS_HAT)?"witchery:textures/entities/witchclothes" + (type == null?"":"_overlay") + ".png":(stack != null && stack.getItem() == Witchery.Items.BITING_BELT?"witchery:textures/entities/witchclothes_legs" + (type == null?"":"_overlay") + ".png":(stack != null && stack.getItem() == Witchery.Items.BARK_BELT?"witchery:textures/entities/witchclothes" + (type == null?"2_legs":"_legs_overlay") + ".png":null));
   }

   public int getMaxChargeLevel(EntityLivingBase entity) {
      int level = 0;

      for(int i = 1; i <= 4; ++i) {
         ItemStack stack = entity.getEquipmentInSlot(i);
         if(stack != null && stack.getItem() instanceof ItemWitchesClothes) {
            level += 2;
         }
      }

      return level;
   }

   public void setChargeLevel(ItemStack stack, int level) {
      if(!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound nbtRoot = stack.getTagCompound();
      nbtRoot.setInteger("WITCBarkPieces", level);
   }

   public int getChargeLevel(ItemStack stack) {
      if(stack.hasTagCompound()) {
         NBTTagCompound nbtRoot = stack.getTagCompound();
         if(nbtRoot.hasKey("WITCBarkPieces")) {
            return nbtRoot.getInteger("WITCBarkPieces");
         }
      }

      return 0;
   }

   public boolean hasColor(ItemStack stack) {
      if(stack == null || stack.getItem() == Witchery.Items.BABAS_HAT) {
         return false;
      }
      return super.hasColor(stack);
   }

   public int getColor(ItemStack stack) {
      if(stack == null || stack.getItem() == Witchery.Items.BABAS_HAT) {
         return super.getColor(stack);
      }
      if(super.hasColor(stack)) {
         return super.getColor(stack);
      }
      if(stack.getItem() == Witchery.Items.RUBY_SLIPPERS) {
         return 14483456;
      } else if(stack.getItem() == Witchery.Items.ICY_SLIPPERS) {
         return 7842303;
      } else if(stack.getItem() == Witchery.Items.SEEPING_SHOES) {
         return 2254387;
      } else if(stack.getItem() == Witchery.Items.BARK_BELT) {
         return 6968628;
      } else {
         return 2628115;
      }
   }

   @SideOnly(Side.CLIENT)
   public int getColorFromItemStack(ItemStack stack, int par2) {
      if(par2 > 0) {
         return 16777215;
      }
      int color = this.getColor(stack);
      return color < 0 ? 16777215 : color;
   }

   @SideOnly(Side.CLIENT)
   public boolean requiresMultipleRenderPasses() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
      return this.getIconFromDamage(par1);
   }

   public boolean isHatWorn(EntityPlayer player) {
      return player.inventory.armorItemInSlot(3) != null && player.inventory.armorItemInSlot(3).getItem() == this;
   }

   public boolean isRobeWorn(EntityPlayer player) {
      return player.inventory.armorItemInSlot(2) != null && player.inventory.armorItemInSlot(2).getItem() == this;
   }

   public boolean isBeltWorn(EntityPlayer player) {
      return player.inventory.armorItemInSlot(1) != null && player.inventory.armorItemInSlot(1).getItem() == this;
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
      if(this.modelClothesChest == null) {
         this.modelClothesChest = new ModelWitchesClothes(0.61F, false);
      }

      if(this.modelNecroChest == null) {
         this.modelNecroChest = new ModelWitchesClothes(0.61F, true);
      }

      if(this.modelClothesLegs == null) {
         this.modelClothesLegs = new ModelWitchesClothes(0.45F, false);
      }

      if(this.modelClothesFeet == null) {
         this.modelClothesFeet = new ModelWitchesClothes(0.48F, false);
      }

      ModelWitchesClothes armorModel = null;
      if(stack != null && stack.getItem() instanceof ItemWitchesClothes) {
         int type = ((ItemArmor)stack.getItem()).armorType;
         if(type == 3) {
            armorModel = this.modelClothesFeet;
         } else if(type == 2) {
            armorModel = this.modelClothesLegs;
         } else if(type == 1) {
            armorModel = stack.getItem() == Witchery.Items.NECROMANCERS_ROBES?this.modelNecroChest:this.modelClothesChest;
         } else {
            armorModel = this.modelClothesChest;
         }

         if(armorModel != null) {
            armorModel.isHatSlot = (armorSlot == 0);
            armorModel.isRobeSlot = (armorSlot == 1);
            armorModel.isBeltSlot = (armorSlot == 2);
            armorModel.isShoeSlot = (armorSlot == 3);

            int style = getStyle(stack);
            if(armorSlot == 0) {
               armorModel.hatStyle = style;
               armorModel.isBabasHat = (stack.getItem() == Witchery.Items.BABAS_HAT);
            } else if(armorSlot == 1) {
               armorModel.robeStyle = style;
            } else if(armorSlot == 2) {
               armorModel.beltStyle = style;
            } else if(armorSlot == 3) {
               armorModel.shoeStyle = style;
            }

            boolean isVisible = true;
            if(entityLiving != null && entityLiving.isInvisible()) {
               String heldStack = entityLiving.getClass().getSimpleName();
               isVisible = heldStack == null || heldStack.isEmpty() || heldStack.equals("AbstractSteve");
            }

            armorModel.bipedHead.showModel = isVisible && armorSlot == 0;
            armorModel.bipedHeadwear.showModel = isVisible && armorSlot == 0;
            armorModel.bipedBody.showModel = isVisible && (armorSlot == 1 || armorSlot == 2);
            armorModel.bipedRightArm.showModel = isVisible && armorSlot == 1;
            armorModel.bipedLeftArm.showModel = isVisible && armorSlot == 1;
            armorModel.bipedRightLeg.showModel = isVisible && (armorSlot == 2 || armorSlot == 3);
            armorModel.bipedLeftLeg.showModel = isVisible && (armorSlot == 2 || armorSlot == 3);
            armorModel.isSneak = entityLiving.isSneaking();
            armorModel.isRiding = entityLiving.isRiding();
            armorModel.isChild = entityLiving.isChild();
            ItemStack heldStack1 = entityLiving.getEquipmentInSlot(0);
            armorModel.heldItemRight = heldStack1 != null?1:0;
            armorModel.aimedBow = false;
            if(entityLiving instanceof EntityPlayer && heldStack1 != null && ((EntityPlayer)entityLiving).getItemInUseDuration() > 0) {
               EnumAction enumaction = heldStack1.getItemUseAction();
               if(enumaction == EnumAction.block) {
                  armorModel.heldItemRight = 3;
               }

               armorModel.aimedBow = enumaction == EnumAction.bow;
            }

            return armorModel;
         }
      }

      return null;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return stack == null?EnumRarity.common:(stack.getItem() == Witchery.Items.BABAS_HAT?EnumRarity.epic:(stack.getItem() == Witchery.Items.BARK_BELT?EnumRarity.rare:EnumRarity.uncommon));
   }

   public String getItemStackDisplayName(ItemStack stack) {
      String baseName = super.getItemStackDisplayName(stack);
      return baseName;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
      int style = getStyle(stack);
      String styleName;
      if(this.armorType == 0) {
         styleName = StatCollector.translateToLocal("witchery.style.hat." + style);
      } else if(this.armorType == 1) {
         styleName = StatCollector.translateToLocal("witchery.style.robe." + style);
      } else if(this.armorType == 2) {
         styleName = StatCollector.translateToLocal("witchery.style.belt." + style);
      } else {
         styleName = StatCollector.translateToLocal("witchery.style.shoe." + style);
      }
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.style.label") + ": " + EnumChatFormatting.GOLD + styleName);
      list.add(EnumChatFormatting.DARK_GRAY + "(" + StatCollector.translateToLocal("witchery.style.sneak_click_tip") + ")");
      String localText = Witchery.resource(this.getUnlocalizedName() + ".tip");
      String s1;
      if(localText != null) {
         String[] potion = localText.split("\n");
         int effects = potion.length;

         for(int effect = 0; effect < effects; ++effect) {
            s1 = potion[effect];
            if(!s1.isEmpty()) {
               list.add(s1);
            }
         }
      }

      String s2;
      int var11;
      List var12;
      PotionEffect var13;
      if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("WITCPotion")) {
         var11 = stack.getTagCompound().getInteger("WITCPotion");
         var12 = Items.potionitem.getEffects(var11);
         if(var12 != null && !var12.isEmpty()) {
            var13 = (PotionEffect)var12.get(0);
            s1 = var13.getEffectName();
            s1 = s1 + ".postfix";
            s2 = "§6" + StatCollector.translateToLocal(s1).trim() + "§r";
            if(var13.getAmplifier() > 0) {
               s2 = s2 + " " + StatCollector.translateToLocal("potion.potency." + var13.getAmplifier()).trim();
            }

            if(var13.getDuration() > 20) {
               s2 = s2 + " [" + Potion.getDurationString(var13) + "]";
            }

            list.add(s2);
         }
      }

      if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("WITCPotion2")) {
         var11 = stack.getTagCompound().getInteger("WITCPotion2");
         var12 = Items.potionitem.getEffects(var11);
         if(var12 != null && !var12.isEmpty()) {
            var13 = (PotionEffect)var12.get(0);
            s1 = var13.getEffectName();
            s1 = s1 + ".postfix";
            s2 = "§6" + StatCollector.translateToLocal(s1).trim() + "§r";
            if(var13.getAmplifier() > 0) {
               s2 = s2 + " " + StatCollector.translateToLocal("potion.potency." + var13.getAmplifier()).trim();
            }

            if(var13.getDuration() > 20) {
               s2 = s2 + " [" + Potion.getDurationString(var13) + "]";
            }

            list.add(s2);
         }
      }

   }

   public boolean trySayTheresNoPlaceLikeHome(EntityPlayer player, String message) {
      if(player != null && !player.worldObj.isRemote) {
         if(noPlaceLikeHome == null) {
            noPlaceLikeHome = Witchery.resource("witchery.rite.noplacelikehome").toLowerCase().replace("\'", "");
         }

         if(message.toLowerCase().replace("\'", "").startsWith(noPlaceLikeHome)) {
            ItemStack stack = player.getEquipmentInSlot(1);
            if(stack != null && stack.getItem() == Witchery.Items.RUBY_SLIPPERS && player.dimension != Config.instance().dimensionDreamID) {
               NBTTagCompound nbtPlayer = Infusion.getNBT(player);
               if(nbtPlayer != null) {
                  boolean R = true;
                  AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - 3.0D, player.posY - 3.0D, player.posZ - 3.0D, player.posX + 3.0D, player.posY + 3.0D, player.posZ + 3.0D);
                  List list = player.worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);
                  Iterator coords = list.iterator();

                  long timeSince;
                  long COOLDOWN;
                  while(coords.hasNext()) {
                     Object dimension = coords.next();
                     EntityItem world = (EntityItem)dimension;
                     ItemStack waystoneStack = world.getEntityItem();
                     if(waystoneStack != null && Witchery.Items.GENERIC.itemWaystoneBound.isMatch(waystoneStack)) {
                        if(nbtPlayer.hasKey("WITCLastRubySlipperWayTime")) {
                           timeSince = nbtPlayer.getLong("WITCLastRubySlipperWayTime");
                           COOLDOWN = MinecraftServer.getSystemTimeMillis();
                           long cooldownRemaining = COOLDOWN - timeSince;
                           long COOLDOWN1 = 60000L;
                           if(cooldownRemaining < 60000L) {
                              int cooldownRemaining1 = Math.max(1, (int)(60000L - cooldownRemaining) / '\uea60');
                              ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.rite.slippersoncooldown", new Object[]{Integer.valueOf(cooldownRemaining1).toString()});
                              return true;
                           }
                        }

                        int var26 = ItemGeneral.getWaystoneDimension(waystoneStack);
                        if(Infusion.aquireEnergy(player.worldObj, player, nbtPlayer, var26 != player.dimension?35:20, true)) {
                           world.setDead();
                           nbtPlayer.setLong("WITCLastRubySlipperWayTime", MinecraftServer.getSystemTimeMillis());
                           if(player.worldObj.rand.nextDouble() < 0.01D) {
                              BlockVoidBramble.teleportRandomly(player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), player, 500);
                           } else {
                              Witchery.Items.GENERIC.teleportToLocation(player.worldObj, waystoneStack, player, 2, true);
                           }
                        }

                        return true;
                     }
                  }

                  if(nbtPlayer.hasKey("WITCLastRubySlipperTime")) {
                     long var22 = nbtPlayer.getLong("WITCLastRubySlipperTime");
                     long var24 = MinecraftServer.getSystemTimeMillis();
                     timeSince = var24 - var22;
                     COOLDOWN = 180000L;
                     if(timeSince < 180000L && !player.capabilities.isCreativeMode) {
                        int var27 = Math.max(1, (int)(180000L - timeSince) / 60000);
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.rite.slippersoncooldown", new Object[]{Integer.valueOf(var27).toString()});
                        return true;
                     }
                  }

                  ChunkCoordinates var21 = player.getBedLocation(player.dimension);
                  int var23 = player.dimension;
                  Object var25 = player.worldObj;
                  if(var21 == null) {
                     var21 = player.getBedLocation(0);
                     var23 = 0;
                     var25 = MinecraftServer.getServer().worldServerForDimension(0);
                     if(var21 == null) {
                        for(var21 = ((World)var25).getSpawnPoint(); ((World)var25).getBlock(var21.posX, var21.posY, var21.posZ).isNormalCube() && var21.posY < 255; ++var21.posY) {
                           ;
                        }
                     }
                  }

                  if(var21 != null) {
                     nbtPlayer.setLong("WITCLastRubySlipperTime", MinecraftServer.getSystemTimeMillis());
                     var21 = Blocks.bed.getBedSpawnPosition((IBlockAccess)var25, var21.posX, var21.posY, var21.posZ, (EntityPlayer)null);
                     if(var21 != null) {
                        if(Infusion.aquireEnergy(player.worldObj, player, nbtPlayer, var23 != player.dimension?50:25, true)) {
                           ItemGeneral var10000 = Witchery.Items.GENERIC;
                           ItemGeneral.teleportToLocation(player.worldObj, (double)var21.posX, (double)(var21.posY + 1), (double)var21.posZ, var23, player, true);
                        }

                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

}
