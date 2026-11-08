package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.entity.EntityHellhound;
import com.emoniph.witchery.entity.EntityWitchHunter;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RiteInquisitionTrial extends Rite {

   public RiteInquisitionTrial() {}

   @Override
   public void addSteps(ArrayList steps, int initialStage) {
      steps.add(new StepInquisitionTrial(this));
   }

   private static class StepInquisitionTrial extends RitualStep {

      public StepInquisitionTrial(RiteInquisitionTrial rite) {
         super(false);
      }

      @Override
      public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
         if (ticks % 20L != 0L) {
            return RitualStep.Result.STARTING;
         }

         if (!world.isRemote) {
            EntityPlayer player = ritual.getInitiatingPlayer(world);

            // Announce trial
            if (player != null) {
               ChatUtil.sendTranslated(EnumChatFormatting.GOLD, player, "witchery.rite.heretictrial.summoned");
            }

            // Spawn Grand Inquisitor
            EntityWitchHunter grandInquisitor = new EntityWitchHunter(world);
            grandInquisitor.setPosition(posX + 0.5D, posY + 1.0D, posZ + 0.5D);
            grandInquisitor.setCustomNameTag(EnumChatFormatting.GOLD + "Gran Inquisidor");
            grandInquisitor.func_110163_bv(); // persistent
            grandInquisitor.setCurrentItemOrArmor(0, new ItemStack(Witchery.Items.INQUISITOR_CROSSBOW));
            grandInquisitor.setCurrentItemOrArmor(3, new ItemStack(Witchery.Items.INQUISITOR_CLOAK));
            grandInquisitor.setCurrentItemOrArmor(4, new ItemStack(Witchery.Items.HUNTER_HAT_SILVERED));
            grandInquisitor.setEquipmentDropChance(0, 1.0F); // 100% drops crossbow
            grandInquisitor.setEquipmentDropChance(3, 1.0F); // 100% drops cloak
            if (player != null) {
               grandInquisitor.setAttackTarget(player);
            }
            world.spawnEntityInWorld(grandInquisitor);

            // Spawn 2 Hunter guards
            for (int i = -1; i <= 1; i += 2) {
               EntityWitchHunter guard = new EntityWitchHunter(world);
               guard.setPosition(posX + (double)i * 3.0D, posY + 1.0D, posZ + 2.0D);
               guard.setCurrentItemOrArmor(0, new ItemStack(Witchery.Items.CROSSBOW_PISTOL));
               guard.setCurrentItemOrArmor(3, new ItemStack(Witchery.Items.HUNTER_COAT_SILVERED));
               if (player != null) {
                  guard.setAttackTarget(player);
               }
               world.spawnEntityInWorld(guard);
            }

            // Spawn 2 Hellhounds
            for (int i = -1; i <= 1; i += 2) {
               EntityHellhound hound = new EntityHellhound(world);
               hound.setPosition(posX + (double)i * 2.0D, posY + 1.0D, posZ - 2.0D);
               if (player != null) {
                  hound.setAttackTarget(player);
               }
               world.spawnEntityInWorld(hound);
            }

            ParticleEffect.EXPLODE.send(SoundEffect.RANDOM_EXPLODE, world, posX + 0.5D, posY + 1.0D, posZ + 0.5D, 2.0D, 2.0D, 24);
         }

         return RitualStep.Result.COMPLETED;
      }
   }
}
