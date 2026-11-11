package com.emoniph.witchery.common;

import com.emoniph.witchery.util.Coord;
import net.minecraft.world.World;

public class BabaYagaMobilePowerSource implements IPowerSource {

   private final World world;
   private final Coord coord;
   private float power = 4000.0F;

   public BabaYagaMobilePowerSource(World world, int x, int y, int z) {
      this.world = world;
      this.coord = new Coord(x, y, z);
   }

   @Override
   public World getWorld() {
      return this.world;
   }

   @Override
   public Coord getLocation() {
      return this.coord;
   }

   @Override
   public boolean isLocationEqual(Coord other) {
      return this.coord.equals(other);
   }

   @Override
   public boolean consumePower(float amount) {
      if (this.power >= amount) {
         this.power -= amount;
         return true;
      }
      return false;
   }

   @Override
   public float getCurrentPower() {
      return this.power;
   }

   @Override
   public float getRange() {
      return 16.0F;
   }

   @Override
   public int getEnhancementLevel() {
      return 1;
   }

   @Override
   public boolean isPowerInvalid() {
      return false;
   }
}
