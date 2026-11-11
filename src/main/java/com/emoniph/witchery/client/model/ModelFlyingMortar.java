package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelFlyingMortar extends ModelBase {

   public ModelRenderer bottom;
   public ModelRenderer wallFront;
   public ModelRenderer wallBack;
   public ModelRenderer wallLeft;
   public ModelRenderer wallRight;
   public ModelRenderer pestle;

   public ModelFlyingMortar() {
      super.textureWidth = 64;
      super.textureHeight = 64;

      // Bottom base of the mortar (16x2x16)
      this.bottom = new ModelRenderer(this, 0, 0);
      this.bottom.addBox(-8.0F, 0.0F, -8.0F, 16, 2, 16);
      this.bottom.setRotationPoint(0.0F, 22.0F, 0.0F);

      // Front wall
      this.wallFront = new ModelRenderer(this, 0, 18);
      this.wallFront.addBox(-8.0F, -10.0F, -8.0F, 16, 10, 2);
      this.wallFront.setRotationPoint(0.0F, 22.0F, 0.0F);

      // Back wall
      this.wallBack = new ModelRenderer(this, 0, 18);
      this.wallBack.addBox(-8.0F, -10.0F, 6.0F, 16, 10, 2);
      this.wallBack.setRotationPoint(0.0F, 22.0F, 0.0F);

      // Left wall
      this.wallLeft = new ModelRenderer(this, 36, 18);
      this.wallLeft.addBox(-8.0F, -10.0F, -6.0F, 2, 10, 12);
      this.wallLeft.setRotationPoint(0.0F, 22.0F, 0.0F);

      // Right wall
      this.wallRight = new ModelRenderer(this, 36, 18);
      this.wallRight.addBox(6.0F, -10.0F, -6.0F, 2, 10, 12);
      this.wallRight.setRotationPoint(0.0F, 22.0F, 0.0F);

      // Pestle (Mano de mortero mística que sobresale)
      this.pestle = new ModelRenderer(this, 0, 32);
      this.pestle.addBox(-1.5F, -16.0F, -1.5F, 3, 18, 3);
      this.pestle.setRotationPoint(3.0F, 20.0F, 3.0F);
      this.pestle.rotateAngleX = 0.35F;
      this.pestle.rotateAngleZ = -0.35F;
   }

   @Override
   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.bottom.render(f5);
      this.wallFront.render(f5);
      this.wallBack.render(f5);
      this.wallLeft.render(f5);
      this.wallRight.render(f5);
      this.pestle.render(f5);
   }
}

