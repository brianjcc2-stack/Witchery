package com.emoniph.witchery.client.model;

import com.emoniph.witchery.item.ItemMirrorArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelMirrorArmor extends ModelBiped {

   public int slotType;
   public int currentStyle = 0;

   // Base Silver Circlet Band (Symmetrical, hollow perimeter fitting outside the hair)
   private ModelRenderer circletFront;
   private ModelRenderer circletLeft;
   private ModelRenderer circletRight;
   private ModelRenderer circletBack;

   // Style 0: Corona Imperial de Cristal (Royal Imperial Spire Crown)
   private ModelRenderer centerGem;
   private ModelRenderer centerApex;
   private ModelRenderer spireFrontL;
   private ModelRenderer spireFrontR;
   private ModelRenderer spireTempleL;
   private ModelRenderer spireTempleR;

   // Orbiting Floating Crystal Shards
   private ModelRenderer shard1;
   private ModelRenderer shard2;
   private ModelRenderer shard3;
   private ModelRenderer shard4;

   // Style 1: Tiara Lunar de Cristal (Moonlit Crescent Tiara)
   private ModelRenderer tiaraCrescent;
   private ModelRenderer tiaraGem;
   private ModelRenderer tiaraWingL;
   private ModelRenderer tiaraWingR;
   private ModelRenderer tiaraTempleL;
   private ModelRenderer tiaraTempleR;

   // Style 2: Aureola Solar de Cristal (Solar Sunburst Halo)
   private ModelRenderer haloCenterGem;
   private ModelRenderer haloSpire1;
   private ModelRenderer haloSpire2L;
   private ModelRenderer haloSpire2R;
   private ModelRenderer haloSpire3L;
   private ModelRenderer haloSpire3R;
   private ModelRenderer haloSpire4L;
   private ModelRenderer haloSpire4R;

   // Chestplate: Coraza de Cristal
   private ModelRenderer heartPrism;
   private ModelRenderer collarPrism;
   private ModelRenderer girdleGem;
   private ModelRenderer pauldronRight;
   private ModelRenderer shoulderCrystalR;
   private ModelRenderer pauldronLeft;
   private ModelRenderer shoulderCrystalL;

   public ModelMirrorArmor(float scale, int slotType) {
      super(scale, 0.0F, 64, 64);
      this.slotType = slotType;

      if (slotType == 0) {
         // =========================================================================
         // --- HOLLOW SYMMETRICAL DIADEM BAND ---
         // Head size is 8x8x8 (-4 to +4 in X and Z, -8 to 0 in Y).
         // The circlet rests at brow level (Y = -8.0F to -6.0F) with outer size 9x9.
         // Corners meet squarely:
         // Front: X -4.5 to +4.5, Z -4.5 to -3.5 (Width 9, Height 2, Depth 1)
         // Back:  X -4.5 to +4.5, Z  3.5 to  4.5 (Width 9, Height 2, Depth 1)
         // Left:  X  3.5 to  4.5, Z -4.5 to  4.5 (Width 1, Height 2, Depth 9)
         // Right: X -4.5 to -3.5, Z -4.5 to  4.5 (Width 1, Height 2, Depth 9)
         // =========================================================================
         this.circletFront = new ModelRenderer(this, 0, 32);
         this.circletFront.addBox(-4.5F, -8.0F, -4.5F, 9, 2, 1, scale);
         super.bipedHead.addChild(this.circletFront);

         this.circletBack = new ModelRenderer(this, 0, 32);
         this.circletBack.addBox(-4.5F, -8.0F, 3.5F, 9, 2, 1, scale);
         super.bipedHead.addChild(this.circletBack);

         this.circletLeft = new ModelRenderer(this, 0, 32);
         this.circletLeft.addBox(3.5F, -8.0F, -4.5F, 1, 2, 9, scale);
         super.bipedHead.addChild(this.circletLeft);

         this.circletRight = new ModelRenderer(this, 0, 32);
         this.circletRight.addBox(-4.5F, -8.0F, -4.5F, 1, 2, 9, scale);
         super.bipedHead.addChild(this.circletRight);

         // =========================================================================
         // --- STYLE 0: CORONA IMPERIAL DE CRISTAL ---
         // Majestic vertical crystal spires with faceted diamond apex
         // =========================================================================
         this.centerGem = new ModelRenderer(this, 16, 32);
         this.centerGem.addBox(-1.0F, -11.0F, -4.8F, 2, 4, 1, scale);
         super.bipedHead.addChild(this.centerGem);

         this.centerApex = new ModelRenderer(this, 16, 32);
         this.centerApex.addBox(-0.5F, -13.0F, -4.7F, 1, 3, 1, scale);
         super.bipedHead.addChild(this.centerApex);

         // Symmetrical flanking spires
         this.spireFrontL = new ModelRenderer(this, 28, 32);
         this.spireFrontL.setRotationPoint(2.2F, -8.0F, -4.6F);
         this.spireFrontL.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 1, scale);
         this.spireFrontL.rotateAngleZ = 0.22F;
         super.bipedHead.addChild(this.spireFrontL);

         this.spireFrontR = new ModelRenderer(this, 28, 32);
         this.spireFrontR.setRotationPoint(-2.2F, -8.0F, -4.6F);
         this.spireFrontR.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 1, scale);
         this.spireFrontR.rotateAngleZ = -0.22F;
         super.bipedHead.addChild(this.spireFrontR);

         // Temple spires
         this.spireTempleL = new ModelRenderer(this, 28, 32);
         this.spireTempleL.setRotationPoint(4.2F, -8.0F, 0.0F);
         this.spireTempleL.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, scale);
         this.spireTempleL.rotateAngleZ = 0.22F;
         super.bipedHead.addChild(this.spireTempleL);

         this.spireTempleR = new ModelRenderer(this, 28, 32);
         this.spireTempleR.setRotationPoint(-4.2F, -8.0F, 0.0F);
         this.spireTempleR.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, scale);
         this.spireTempleR.rotateAngleZ = -0.22F;
         super.bipedHead.addChild(this.spireTempleR);

         // 4 Orbiting Floating Crystal Shards (Orbits dynamically calculated in setRotationAngles)
         this.shard1 = new ModelRenderer(this, 40, 32);
         this.shard1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, scale);
         super.bipedHead.addChild(this.shard1);

         this.shard2 = new ModelRenderer(this, 40, 32);
         this.shard2.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, scale);
         super.bipedHead.addChild(this.shard2);

         this.shard3 = new ModelRenderer(this, 40, 32);
         this.shard3.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, scale);
         super.bipedHead.addChild(this.shard3);

         this.shard4 = new ModelRenderer(this, 40, 32);
         this.shard4.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, scale);
         super.bipedHead.addChild(this.shard4);

         // =========================================================================
         // --- STYLE 1: TIARA LUNAR DE CRISTAL ---
         // Delicate crescent moon arch with elven side wings
         // =========================================================================
         this.tiaraCrescent = new ModelRenderer(this, 50, 32);
         this.tiaraCrescent.addBox(-2.0F, -9.5F, -4.8F, 4, 3, 1, scale);
         super.bipedHead.addChild(this.tiaraCrescent);

         this.tiaraGem = new ModelRenderer(this, 16, 32);
         this.tiaraGem.addBox(-0.5F, -11.0F, -4.9F, 1, 2, 1, scale);
         super.bipedHead.addChild(this.tiaraGem);

         this.tiaraWingL = new ModelRenderer(this, 50, 32);
         this.tiaraWingL.setRotationPoint(2.2F, -7.5F, -4.6F);
         this.tiaraWingL.addBox(0.0F, -2.5F, 0.0F, 2, 3, 1, scale);
         this.tiaraWingL.rotateAngleZ = 0.3F;
         super.bipedHead.addChild(this.tiaraWingL);

         this.tiaraWingR = new ModelRenderer(this, 50, 32);
         this.tiaraWingR.setRotationPoint(-2.2F, -7.5F, -4.6F);
         this.tiaraWingR.addBox(-2.0F, -2.5F, 0.0F, 2, 3, 1, scale);
         this.tiaraWingR.rotateAngleZ = -0.3F;
         super.bipedHead.addChild(this.tiaraWingR);

         this.tiaraTempleL = new ModelRenderer(this, 50, 32);
         this.tiaraTempleL.setRotationPoint(4.0F, -7.5F, -1.5F);
         this.tiaraTempleL.addBox(-0.5F, -2.0F, -0.5F, 1, 2, 3, scale);
         this.tiaraTempleL.rotateAngleZ = 0.15F;
         super.bipedHead.addChild(this.tiaraTempleL);

         this.tiaraTempleR = new ModelRenderer(this, 50, 32);
         this.tiaraTempleR.setRotationPoint(-4.0F, -7.5F, -1.5F);
         this.tiaraTempleR.addBox(-0.5F, -2.0F, -0.5F, 1, 2, 3, scale);
         this.tiaraTempleR.rotateAngleZ = -0.15F;
         super.bipedHead.addChild(this.tiaraTempleR);

         // =========================================================================
         // --- STYLE 2: AUREOLA SOLAR DE CRISTAL ---
         // Brilliant crystalline sunburst arch floating majestically behind the head
         // =========================================================================
         this.haloCenterGem = new ModelRenderer(this, 16, 32);
         this.haloCenterGem.addBox(-1.0F, -10.0F, -4.8F, 2, 3, 1, scale);
         super.bipedHead.addChild(this.haloCenterGem);

         // Center Vertical Ray
         this.haloSpire1 = new ModelRenderer(this, 0, 48);
         this.haloSpire1.setRotationPoint(0.0F, -8.0F, 4.2F);
         this.haloSpire1.addBox(-0.5F, -7.0F, -0.5F, 1, 8, 1, scale);
         super.bipedHead.addChild(this.haloSpire1);

         // Inner Ray Pair (Angle ~18 deg)
         this.haloSpire2L = new ModelRenderer(this, 0, 48);
         this.haloSpire2L.setRotationPoint(1.5F, -8.0F, 4.2F);
         this.haloSpire2L.addBox(-0.5F, -6.0F, -0.5F, 1, 7, 1, scale);
         this.haloSpire2L.rotateAngleZ = 0.32F;
         super.bipedHead.addChild(this.haloSpire2L);

         this.haloSpire2R = new ModelRenderer(this, 0, 48);
         this.haloSpire2R.setRotationPoint(-1.5F, -8.0F, 4.2F);
         this.haloSpire2R.addBox(-0.5F, -6.0F, -0.5F, 1, 7, 1, scale);
         this.haloSpire2R.rotateAngleZ = -0.32F;
         super.bipedHead.addChild(this.haloSpire2R);

         // Mid Ray Pair (Angle ~38 deg)
         this.haloSpire3L = new ModelRenderer(this, 0, 48);
         this.haloSpire3L.setRotationPoint(3.0F, -8.0F, 4.2F);
         this.haloSpire3L.addBox(-0.5F, -5.0F, -0.5F, 1, 6, 1, scale);
         this.haloSpire3L.rotateAngleZ = 0.65F;
         super.bipedHead.addChild(this.haloSpire3L);

         this.haloSpire3R = new ModelRenderer(this, 0, 48);
         this.haloSpire3R.setRotationPoint(-3.0F, -8.0F, 4.2F);
         this.haloSpire3R.addBox(-0.5F, -5.0F, -0.5F, 1, 6, 1, scale);
         this.haloSpire3R.rotateAngleZ = -0.65F;
         super.bipedHead.addChild(this.haloSpire3R);

         // Outer Ray Pair (Angle ~60 deg)
         this.haloSpire4L = new ModelRenderer(this, 0, 48);
         this.haloSpire4L.setRotationPoint(4.2F, -8.0F, 4.2F);
         this.haloSpire4L.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);
         this.haloSpire4L.rotateAngleZ = 1.05F;
         super.bipedHead.addChild(this.haloSpire4L);

         this.haloSpire4R = new ModelRenderer(this, 0, 48);
         this.haloSpire4R.setRotationPoint(-4.2F, -8.0F, 4.2F);
         this.haloSpire4R.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);
         this.haloSpire4R.rotateAngleZ = -1.05F;
         super.bipedHead.addChild(this.haloSpire4R);

      } else if (slotType == 1) {
         // =========================================================================
         // --- CORAZA DE CRISTAL (CHESTPLATE) ---
         // Faceted prismatic heart core, collar gorget, and silver crystal pauldrons
         // =========================================================================
         this.heartPrism = new ModelRenderer(this, 16, 48);
         this.heartPrism.addBox(-1.5F, 2.5F, -2.7F, 3, 3, 1, scale + 0.1F);
         super.bipedBody.addChild(this.heartPrism);

         this.collarPrism = new ModelRenderer(this, 16, 48);
         this.collarPrism.addBox(-3.0F, 0.0F, -2.4F, 6, 2, 1, scale);
         super.bipedBody.addChild(this.collarPrism);

         this.girdleGem = new ModelRenderer(this, 16, 48);
         this.girdleGem.addBox(-1.5F, 9.0F, -2.4F, 3, 2, 1, scale);
         super.bipedBody.addChild(this.girdleGem);

         // Right Pauldron
         this.pauldronRight = new ModelRenderer(this, 32, 48);
         this.pauldronRight.addBox(-3.5F, -2.5F, -2.5F, 4, 3, 5, scale + 0.1F);
         super.bipedRightArm.addChild(this.pauldronRight);

         this.shoulderCrystalR = new ModelRenderer(this, 16, 32);
         this.shoulderCrystalR.addBox(-2.5F, -4.5F, -0.5F, 1, 3, 1, scale);
         this.shoulderCrystalR.rotateAngleZ = -0.18F;
         super.bipedRightArm.addChild(this.shoulderCrystalR);

         // Left Pauldron
         this.pauldronLeft = new ModelRenderer(this, 32, 48);
         this.pauldronLeft.addBox(-0.5F, -2.5F, -2.5F, 4, 3, 5, scale + 0.1F);
         super.bipedLeftArm.addChild(this.pauldronLeft);

         this.shoulderCrystalL = new ModelRenderer(this, 16, 32);
         this.shoulderCrystalL.addBox(1.5F, -4.5F, -0.5F, 1, 3, 1, scale);
         this.shoulderCrystalL.rotateAngleZ = 0.18F;
         super.bipedLeftArm.addChild(this.shoulderCrystalL);
      }
   }

   public void prepareModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
      if (stack != null && stack.getItem() instanceof ItemMirrorArmor) {
         this.currentStyle = ItemMirrorArmor.getStyle(stack);
      }

      boolean isVisible = true;
      if (entityLiving != null && entityLiving.isInvisible()) {
         String name = entityLiving.getClass().getSimpleName();
         isVisible = name == null || name.isEmpty() || name.equals("AbstractSteve");
      }

      this.bipedHead.showModel = isVisible && (armorSlot == 0);
      this.bipedHeadwear.showModel = isVisible && (armorSlot == 0);
      this.bipedBody.showModel = isVisible && (armorSlot == 1);
      this.bipedRightArm.showModel = isVisible && (armorSlot == 1);
      this.bipedLeftArm.showModel = isVisible && (armorSlot == 1);
      this.bipedRightLeg.showModel = false;
      this.bipedLeftLeg.showModel = false;

      this.isSneak = entityLiving != null && entityLiving.isSneaking();
      this.isRiding = entityLiving != null && entityLiving.isRiding();
      this.isChild = entityLiving != null && entityLiving.isChild();

      ItemStack heldStack = entityLiving != null ? entityLiving.getEquipmentInSlot(0) : null;
      this.heldItemRight = heldStack != null ? 1 : 0;
      this.aimedBow = false;
      if (entityLiving instanceof EntityPlayer && heldStack != null && ((EntityPlayer)entityLiving).getItemInUseDuration() > 0) {
         EnumAction action = heldStack.getItemUseAction();
         if (action == EnumAction.block) {
            this.heldItemRight = 3;
         }
         this.aimedBow = (action == EnumAction.bow);
      }
   }

   @Override
   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

      if (this.slotType == 0) {
         // Smooth dynamic orbiting trigonometry for Style 0 Floating Crystal Shards
         float orbitAngle = f2 * 0.06F;
         float orbitRadius = 6.0F;

         if (this.shard1 != null) {
            this.shard1.rotationPointX = MathHelper.cos(orbitAngle) * orbitRadius;
            this.shard1.rotationPointZ = MathHelper.sin(orbitAngle) * orbitRadius;
            this.shard1.rotationPointY = -9.0F + MathHelper.sin(orbitAngle * 2.0F) * 0.7F;
            this.shard1.rotateAngleY = orbitAngle;
            this.shard1.rotateAngleX = 0.25F;
         }
         if (this.shard2 != null) {
            float a2 = orbitAngle + 1.5707963F;
            this.shard2.rotationPointX = MathHelper.cos(a2) * orbitRadius;
            this.shard2.rotationPointZ = MathHelper.sin(a2) * orbitRadius;
            this.shard2.rotationPointY = -9.0F + MathHelper.sin(a2 * 2.0F) * 0.7F;
            this.shard2.rotateAngleY = a2;
            this.shard2.rotateAngleX = 0.25F;
         }
         if (this.shard3 != null) {
            float a3 = orbitAngle + 3.1415927F;
            this.shard3.rotationPointX = MathHelper.cos(a3) * orbitRadius;
            this.shard3.rotationPointZ = MathHelper.sin(a3) * orbitRadius;
            this.shard3.rotationPointY = -9.0F + MathHelper.sin(a3 * 2.0F) * 0.7F;
            this.shard3.rotateAngleY = a3;
            this.shard3.rotateAngleX = 0.25F;
         }
         if (this.shard4 != null) {
            float a4 = orbitAngle + 4.712389F;
            this.shard4.rotationPointX = MathHelper.cos(a4) * orbitRadius;
            this.shard4.rotationPointZ = MathHelper.sin(a4) * orbitRadius;
            this.shard4.rotationPointY = -9.0F + MathHelper.sin(a4 * 2.0F) * 0.7F;
            this.shard4.rotateAngleY = a4;
            this.shard4.rotateAngleX = 0.25F;
         }
      }
   }

   @Override
   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      if (entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase) entity;
         ItemStack stack = (this.slotType == 0) ? living.getEquipmentInSlot(4) : living.getEquipmentInSlot(3);
         if (stack != null && stack.getItem() instanceof ItemMirrorArmor) {
            this.currentStyle = ItemMirrorArmor.getStyle(stack);
         }
      }

      if (this.slotType == 0) {
         boolean isImperial = (this.currentStyle == ItemMirrorArmor.STYLE_CROWN_IMPERIAL);
         boolean isTiara = (this.currentStyle == ItemMirrorArmor.STYLE_CROWN_TIARA);
         boolean isSolar = (this.currentStyle == ItemMirrorArmor.STYLE_CROWN_SOLAR);

         // Base symmetrical circlet band is always shown
         if (this.circletFront != null) this.circletFront.showModel = true;
         if (this.circletLeft != null) this.circletLeft.showModel = true;
         if (this.circletRight != null) this.circletRight.showModel = true;
         if (this.circletBack != null) this.circletBack.showModel = true;

         // Style 0 (Imperial)
         if (this.centerGem != null) this.centerGem.showModel = isImperial;
         if (this.centerApex != null) this.centerApex.showModel = isImperial;
         if (this.spireFrontL != null) this.spireFrontL.showModel = isImperial;
         if (this.spireFrontR != null) this.spireFrontR.showModel = isImperial;
         if (this.spireTempleL != null) this.spireTempleL.showModel = isImperial;
         if (this.spireTempleR != null) this.spireTempleR.showModel = isImperial;
         if (this.shard1 != null) this.shard1.showModel = isImperial;
         if (this.shard2 != null) this.shard2.showModel = isImperial;
         if (this.shard3 != null) this.shard3.showModel = isImperial;
         if (this.shard4 != null) this.shard4.showModel = isImperial;

         // Style 1 (Tiara)
         if (this.tiaraCrescent != null) this.tiaraCrescent.showModel = isTiara;
         if (this.tiaraGem != null) this.tiaraGem.showModel = isTiara;
         if (this.tiaraWingL != null) this.tiaraWingL.showModel = isTiara;
         if (this.tiaraWingR != null) this.tiaraWingR.showModel = isTiara;
         if (this.tiaraTempleL != null) this.tiaraTempleL.showModel = isTiara;
         if (this.tiaraTempleR != null) this.tiaraTempleR.showModel = isTiara;

         // Style 2 (Solar)
         if (this.haloCenterGem != null) this.haloCenterGem.showModel = isSolar;
         if (this.haloSpire1 != null) this.haloSpire1.showModel = isSolar;
         if (this.haloSpire2L != null) this.haloSpire2L.showModel = isSolar;
         if (this.haloSpire2R != null) this.haloSpire2R.showModel = isSolar;
         if (this.haloSpire3L != null) this.haloSpire3L.showModel = isSolar;
         if (this.haloSpire3R != null) this.haloSpire3R.showModel = isSolar;
         if (this.haloSpire4L != null) this.haloSpire4L.showModel = isSolar;
         if (this.haloSpire4R != null) this.haloSpire4R.showModel = isSolar;
      }

      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
   }
}
