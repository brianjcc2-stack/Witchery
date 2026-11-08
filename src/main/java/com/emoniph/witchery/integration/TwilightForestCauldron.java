package com.emoniph.witchery.integration;

import com.emoniph.witchery.Witchery;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import twilightforest.item.TFItems;

public class TwilightForestCauldron {

   public static class Result {
      public final ItemStack itemStack;
      public final int powerCost;

      public Result(ItemStack itemStack, int powerCost) {
         this.itemStack = itemStack;
         this.powerCost = powerCost;
      }
   }

   public static Result getTransmutation(ItemStack stack, Random rand) {
      if (stack == null) {
         return null;
      }
      Item item = stack.getItem();

      // 1. Sangre ígnea de Hidra o Lágrimas ígneas de Ur-Ghast -> Fuego ancestral y corazones demoníacos
      if (item == TFItems.fieryBlood || item == TFItems.fieryTears) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 35) {
            res = Witchery.Items.GENERIC.itemDemonHeart.createStack();
         } else if (roll < 70) {
            res = Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack(2);
         } else {
            res = new ItemStack(TFItems.fieryIngot, 2);
         }
         return new Result(res, 500);
      }

      // 2. Carminite (Torre oscura de Ghasts) -> Telequinesis, algodón espectral y piedras sintonizadas
      if (item == TFItems.carminite) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 40) {
            res = Witchery.Items.GENERIC.itemAttunedStoneCharged.createStack();
         } else if (roll < 75) {
            res = Witchery.Items.GENERIC.itemDisturbedCotton.createStack(2);
         } else {
            res = Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack();
         }
         return new Result(res, 400);
      }

      // 3. Escamas de Naga -> Blindaje arbóreo, diamantes o madera de hierro
      if (item == TFItems.nagaScale) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 50) {
            res = Witchery.Items.GENERIC.itemBranchEnt.createStack(2);
         } else if (roll < 80) {
            res = new ItemStack(net.minecraft.init.Items.diamond);
         } else {
            res = new ItemStack(TFItems.ironwoodIngot, 3);
         }
         return new Result(res, 250);
      }

      // 4. Raíz Viva (Live Root) -> Botánica feérica ancestral y raíces de mandrágora
      if (item == TFItems.liveRoot) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 35) {
            res = Witchery.Items.GENERIC.itemMandrakeRoot.createStack(2);
         } else if (roll < 70) {
            res = Witchery.Items.GENERIC.itemBelladonnaFlower.createStack(2);
         } else {
            res = Witchery.Items.GENERIC.itemBranchEnt.createStack();
         }
         return new Result(res, 180);
      }

      // 5. Torchberries -> Luz mística, maleza resplandeciente o gota de la suerte
      if (item == TFItems.torchberries) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 40) {
            res = new ItemStack(Witchery.Blocks.GLINT_WEED, 2);
         } else if (roll < 75) {
            res = new ItemStack(net.minecraft.init.Items.glowstone_dust, 3);
         } else {
            res = Witchery.Items.GENERIC.itemDropOfLuck.createStack();
         }
         return new Result(res, 150);
      }

      // 6. Borer Essence -> Humos alquímicos y destilación de diamante
      if (item == TFItems.borerEssence) {
         int roll = rand.nextInt(100);
         ItemStack res;
         if (roll < 50) {
            res = Witchery.Items.GENERIC.itemFoulFume.createStack(2);
         } else {
            res = Witchery.Items.GENERIC.itemDiamondVapour.createStack();
         }
         return new Result(res, 250);
      }

      // 7. Bomba de hielo -> Frío glacial / semillas de campanillas de nieve
      if (item == TFItems.iceBomb) {
         ItemStack res = new ItemStack(Witchery.Items.SEEDS_SNOWBELL, 2);
         return new Result(res, 200);
      }

      return null;
   }
}
