package com.emoniph.witchery.client.model;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.item.ItemWitchesClothes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelWitchesClothes extends ModelBiped {

   public int hatStyle = 0;
   public int robeStyle = 0;
   public int beltStyle = 0;
   public int shoeStyle = 0;
   public boolean isBabasHat = false;
   public boolean isHatSlot = false;
   public boolean isRobeSlot = false;
   public boolean isBeltSlot = false;
   public boolean isShoeSlot = false;
   public boolean isBitingBelt = false;
   public boolean isBarkBelt = false;

   // Legacy / Bark Belt parts
   ModelRenderer torso;
   ModelRenderer bottomBack;
   ModelRenderer bottomRight;
   ModelRenderer bottomLeft;
   ModelRenderer Shape1;
   ModelRenderer Shape2;
   ModelRenderer Shape3;
   ModelRenderer headRight1;
   ModelRenderer headLeft1;
   ModelRenderer legRightF;
   ModelRenderer legLeftF;
   ModelRenderer legRightB;
   ModelRenderer legLeftB;
   ModelRenderer bodyF;
   ModelRenderer bodyB;
   ModelRenderer armRightF;
   ModelRenderer armLeftF;
   ModelRenderer armRightB;
   ModelRenderer armLeftB;
   ModelRenderer armLeftOut;
   ModelRenderer armRightOut;
   ModelRenderer spikeLowerRight;
   ModelRenderer spikeLowerLeft;
   ModelRenderer spikeUpperLeft;
   ModelRenderer spikeUpperRight;
   ModelRenderer shoulderRight;
   ModelRenderer shoulderLeft;
   private ModelRenderer babasHat;

   // ==========================================
   // --- HAT STYLES ---
   // ==========================================
   // Style 0: Classic Witch Hat
   public ModelRenderer hat;
   public ModelRenderer hatCurve1;
   public ModelRenderer hatCurve2;
   public ModelRenderer hatCurve3;

   // Style 1: Archmage Astral Wizard Hat
   public ModelRenderer archmageBrim;
   public ModelRenderer archmageCollar;
   public ModelRenderer archmageConeBase;
   public ModelRenderer archmageConeMid;
   public ModelRenderer archmageConeTop;
   public ModelRenderer archmageConeTip;
   public ModelRenderer archmageMoon;

   // Style 2: Sylvan Druid Wreath & Antlers
   public ModelRenderer druidCirclet;
   public ModelRenderer druidGem;
   public ModelRenderer druidAntlerR1;
   public ModelRenderer druidAntlerR2;
   public ModelRenderer druidAntlerR3;
   public ModelRenderer druidAntlerL1;
   public ModelRenderer druidAntlerL2;
   public ModelRenderer druidAntlerL3;

   // Style 3: Void Warlock Open-Face Cowl & Horns
   public ModelRenderer voidCowlTop;
   public ModelRenderer voidCowlLeft;
   public ModelRenderer voidCowlRight;
   public ModelRenderer voidCowlBack;
   public ModelRenderer voidCowlPeak;
   public ModelRenderer voidHornL1;
   public ModelRenderer voidHornL2;
   public ModelRenderer voidHornR1;
   public ModelRenderer voidHornR2;

   // Style 4: Gothic Aristocrat Fascinator
   public ModelRenderer gothicBrim;
   public ModelRenderer gothicCrown;
   public ModelRenderer gothicRibbon;
   public ModelRenderer gothicRose;
   public ModelRenderer gothicVeil;

   // ==========================================
   // --- ROBE STYLES ---
   // ==========================================
   // Robe Style 0: Classic Coven Robes
   public ModelRenderer covenSkirt;
   public ModelRenderer covenBelt;
   public ModelRenderer potionVial;
   public ModelRenderer pouch;
   public ModelRenderer covenCollar;

   // Robe Style 1: Imperial Archmage Robes
   public ModelRenderer archmageSkirt;
   public ModelRenderer archmageSleeveR;
   public ModelRenderer archmageSleeveL;
   public ModelRenderer archmageCollarBack;
   public ModelRenderer archmageCollarL;
   public ModelRenderer archmageCollarR;
   public ModelRenderer archmageCapelet;
   public ModelRenderer archmageBrooch;

   // Robe Style 2: Sylvan Wildwood Druid Garb
   public ModelRenderer druidSkirt;
   public ModelRenderer druidPeltR;
   public ModelRenderer druidPeltChest;
   public ModelRenderer druidTotem;
   public ModelRenderer druidVineBelt;

   // Robe Style 3: Void Warlock Shroud
   public ModelRenderer voidShroudFront;
   public ModelRenderer voidShroudBack;
   public ModelRenderer voidRibs;
   public ModelRenderer voidSoulCore;
   public ModelRenderer voidSpikeR;
   public ModelRenderer voidSpikeL;

   // Robe Style 4: Gothic Aristocrat Ball Gown
   public ModelRenderer gothicSkirtUpper;
   public ModelRenderer gothicSkirtLower;
   public ModelRenderer gothicPuffR;
   public ModelRenderer gothicPuffL;
   public ModelRenderer gothicRuff;
   public ModelRenderer gothicCorset;
   public ModelRenderer gothicRoseBrooch;

   // ==========================================
   // --- BELT STYLES ---
   // ==========================================
   public ModelRenderer bitingBuckle;

   // Belt Style 0: Classic Coven Belt
   public ModelRenderer beltCovenBand;
   public ModelRenderer beltCovenBuckle;
   public ModelRenderer beltCovenVials;
   public ModelRenderer beltCovenPouch;

   // Belt Style 1: Archmage Ceremonial Sash
   public ModelRenderer beltArchmageSash;
   public ModelRenderer beltArchmageSeal;
   public ModelRenderer beltArchmageRibbon;
   public ModelRenderer beltArchmagePendant;

   // Belt Style 2: Druidic Root Girdle
   public ModelRenderer beltDruidRoots;
   public ModelRenderer beltDruidTotem;
   public ModelRenderer beltDruidSeedPouch;
   public ModelRenderer beltDruidVines;

   // Belt Style 3: Void Skull Chained Belt
   public ModelRenderer beltVoidBand;
   public ModelRenderer beltVoidSkull;
   public ModelRenderer beltVoidChains;
   public ModelRenderer beltVoidSpurR;
   public ModelRenderer beltVoidSpurL;

   // Belt Style 4: Gothic Velvet Cincher Corset
   public ModelRenderer beltGothicCorset;
   public ModelRenderer beltGothicLaces;
   public ModelRenderer beltGothicRose;
   public ModelRenderer beltGothicBowR;
   public ModelRenderer beltGothicBowL;

   // ==========================================
   // --- SHOE / BOOT STYLES ---
   // ==========================================
   // Shoe Style 0: Classic Pointed Witch Shoes
   public ModelRenderer shoeClassicR;
   public ModelRenderer shoeClassicTipR;
   public ModelRenderer shoeClassicBuckleR;
   public ModelRenderer shoeClassicL;
   public ModelRenderer shoeClassicTipL;
   public ModelRenderer shoeClassicBuckleL;

   // Shoe Style 1: Imperial Archmage High Boots
   public ModelRenderer bootArchmageR;
   public ModelRenderer bootArchmageCuffR;
   public ModelRenderer bootArchmageSpurR;
   public ModelRenderer bootArchmageL;
   public ModelRenderer bootArchmageCuffL;
   public ModelRenderer bootArchmageSpurL;

   // Shoe Style 2: Sylvan Druid Footwraps
   public ModelRenderer wrapDruidR;
   public ModelRenderer wrapDruidStrapsR;
   public ModelRenderer wrapDruidCharmR;
   public ModelRenderer wrapDruidL;
   public ModelRenderer wrapDruidStrapsL;
   public ModelRenderer wrapDruidCharmL;

   // Shoe Style 3: Void Shadow Spurred Boots
   public ModelRenderer bootVoidR;
   public ModelRenderer bootVoidToeClawR;
   public ModelRenderer bootVoidHeelSpurR;
   public ModelRenderer bootVoidL;
   public ModelRenderer bootVoidToeClawL;
   public ModelRenderer bootVoidHeelSpurL;

   // Shoe Style 4: Gothic Victorian Heeled Booties
   public ModelRenderer bootGothicR;
   public ModelRenderer bootGothicLacesR;
   public ModelRenderer bootGothicHeelR;
   public ModelRenderer bootGothicRoseR;
   public ModelRenderer bootGothicL;
   public ModelRenderer bootGothicLacesL;
   public ModelRenderer bootGothicHeelL;
   public ModelRenderer bootGothicRoseL;

   public ModelWitchesClothes(float scale, boolean shoulders) {
      super(scale, 0.0F, 128, 64);

      // ==========================================
      // --- HAT STYLE 0: CLASSIC WITCH HAT ---
      // ==========================================
      this.setTextureOffset("hat.hatBrim", 0, 49);
      this.setTextureOffset("hat.hatCollar", 0, 36);
      this.setTextureOffset("hat.hatBody", 31, 34);
      this.hat = new ModelRenderer(this, "hat");
      this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hat.mirror = true;
      this.hat.addBox("hatBrim", -7.0F, -7.0F, -7.0F, 14, 1, 14);
      this.hat.addBox("hatCollar", -5.0F, -9.0F, -5.0F, 10, 2, 10);
      this.hat.addBox("hatBody", -3.0F, -14.0F, -3.0F, 6, 5, 6);
      super.bipedHead.addChild(this.hat);

      this.hatCurve1 = new ModelRenderer(this, 31, 34);
      this.hatCurve1.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.hatCurve1.addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4);
      this.setRotation(this.hatCurve1, -0.15F, 0.0F, 0.12F);
      this.hat.addChild(this.hatCurve1);

      this.hatCurve2 = new ModelRenderer(this, 50, 34);
      this.hatCurve2.setRotationPoint(0.0F, -4.0F, 0.0F);
      this.hatCurve2.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2);
      this.setRotation(this.hatCurve2, -0.22F, 0.0F, 0.15F);
      this.hatCurve1.addChild(this.hatCurve2);

      this.hatCurve3 = new ModelRenderer(this, 50, 34);
      this.hatCurve3.setRotationPoint(0.0F, -3.0F, 0.0F);
      this.hatCurve3.addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1);
      this.setRotation(this.hatCurve3, -0.30F, 0.0F, 0.18F);
      this.hatCurve2.addChild(this.hatCurve3);

      // ==========================================
      // --- BABA'S HAT ---
      // ==========================================
      this.babasHat = new ModelRenderer(this, 72, 48);
      this.babasHat.setRotationPoint(-7.0F, -8.0F, -7.0F);
      this.babasHat.addBox(0.0F, 0.0F, 0.0F, 14, 2, 14, 0.52F);
      this.babasHat.mirror = true;
      super.bipedHead.addChild(this.babasHat);

      ModelRenderer baba1 = new ModelRenderer(this, 83, 29);
      baba1.setRotationPoint(3.75F, -4.0F, 4.0F);
      baba1.addBox(0.0F, 0.0F, 0.0F, 7, 4, 7, 0.4F);
      baba1.rotateAngleX = -0.05235988F;
      baba1.rotateAngleZ = 0.02617994F;
      this.babasHat.addChild(baba1);

      ModelRenderer baba2 = new ModelRenderer(this, 83, 40);
      baba2.setRotationPoint(1.75F, -4.0F, 2.0F);
      baba2.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      baba2.rotateAngleX = -0.10471976F;
      baba2.rotateAngleZ = 0.05235988F;
      baba1.addChild(baba2);

      ModelRenderer baba3 = new ModelRenderer(this, 81, 48);
      baba3.setRotationPoint(1.75F, -2.0F, 2.0F);
      baba3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      baba3.rotateAngleX = -0.20943952F;
      baba3.rotateAngleZ = 0.10471976F;
      baba2.addChild(baba3);

      // ==========================================
      // --- HAT STYLE 1: ARCHMAGE ASTRAL WIZARD HAT ---
      // ==========================================
      this.archmageBrim = new ModelRenderer(this, 0, 49);
      this.archmageBrim.addBox(-7.0F, -7.0F, -7.0F, 14, 1, 14, scale + 0.05F);
      super.bipedHead.addChild(this.archmageBrim);

      this.archmageCollar = new ModelRenderer(this, 0, 36);
      this.archmageCollar.addBox(-4.5F, -9.0F, -4.5F, 9, 2, 9, scale + 0.1F);
      super.bipedHead.addChild(this.archmageCollar);

      this.archmageConeBase = new ModelRenderer(this, 31, 34);
      this.archmageConeBase.addBox(-3.5F, -14.0F, -3.5F, 7, 5, 7, scale);
      super.bipedHead.addChild(this.archmageConeBase);

      this.archmageConeMid = new ModelRenderer(this, 31, 34);
      this.archmageConeMid.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.archmageConeMid.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5, scale);
      this.setRotation(this.archmageConeMid, -0.12F, 0.0F, 0.10F);
      this.archmageConeBase.addChild(this.archmageConeMid);

      this.archmageConeTop = new ModelRenderer(this, 50, 34);
      this.archmageConeTop.setRotationPoint(0.0F, -5.0F, 0.0F);
      this.archmageConeTop.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3, scale);
      this.setRotation(this.archmageConeTop, -0.18F, 0.0F, 0.12F);
      this.archmageConeMid.addChild(this.archmageConeTop);

      this.archmageConeTip = new ModelRenderer(this, 50, 34);
      this.archmageConeTip.setRotationPoint(0.0F, -4.0F, 0.0F);
      this.archmageConeTip.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, scale);
      this.setRotation(this.archmageConeTip, -0.25F, 0.0F, 0.12F);
      this.archmageConeTop.addChild(this.archmageConeTip);

      this.archmageMoon = new ModelRenderer(this, 61, 32);
      this.archmageMoon.setRotationPoint(0.0F, -3.0F, 0.0F);
      this.archmageMoon.addBox(-1.0F, 0.0F, -0.5F, 2, 3, 1, scale);
      this.archmageConeTip.addChild(this.archmageMoon);

      // ==========================================
      // --- HAT STYLE 2: DRUIDIC HORNED WREATH ---
      // ==========================================
      this.druidCirclet = new ModelRenderer(this, 95, 0);
      this.druidCirclet.addBox(-4.5F, -7.5F, -4.5F, 9, 2, 9, scale + 0.1F);
      super.bipedHead.addChild(this.druidCirclet);

      this.druidGem = new ModelRenderer(this, 61, 32);
      this.druidGem.addBox(-1.0F, -8.0F, -5.0F, 2, 2, 1, scale + 0.05F);
      super.bipedHead.addChild(this.druidGem);

      // Right Antler
      this.druidAntlerR1 = new ModelRenderer(this, 95, 0);
      this.druidAntlerR1.setRotationPoint(-3.5F, -7.5F, -0.5F);
      this.druidAntlerR1.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, scale);
      this.setRotation(this.druidAntlerR1, -0.12F, 0.0F, -0.38F);
      super.bipedHead.addChild(this.druidAntlerR1);

      this.druidAntlerR2 = new ModelRenderer(this, 95, 0);
      this.druidAntlerR2.setRotationPoint(0.0F, -3.5F, 0.0F);
      this.druidAntlerR2.addBox(-0.5F, -3.0F, -2.5F, 1, 3, 2, scale);
      this.setRotation(this.druidAntlerR2, -0.4F, 0.0F, 0.0F);
      this.druidAntlerR1.addChild(this.druidAntlerR2);

      this.druidAntlerR3 = new ModelRenderer(this, 95, 0);
      this.druidAntlerR3.setRotationPoint(0.0F, -6.0F, 0.0F);
      this.druidAntlerR3.addBox(-1.5F, -4.0F, -0.5F, 1, 4, 1, scale);
      this.setRotation(this.druidAntlerR3, 0.0F, 0.0F, -0.25F);
      this.druidAntlerR1.addChild(this.druidAntlerR3);

      // Left Antler
      this.druidAntlerL1 = new ModelRenderer(this, 95, 0);
      this.druidAntlerL1.setRotationPoint(3.5F, -7.5F, -0.5F);
      this.druidAntlerL1.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, scale);
      this.setRotation(this.druidAntlerL1, -0.12F, 0.0F, 0.38F);
      super.bipedHead.addChild(this.druidAntlerL1);

      this.druidAntlerL2 = new ModelRenderer(this, 95, 0);
      this.druidAntlerL2.setRotationPoint(0.0F, -3.5F, 0.0F);
      this.druidAntlerL2.addBox(-0.5F, -3.0F, -2.5F, 1, 3, 2, scale);
      this.setRotation(this.druidAntlerL2, -0.4F, 0.0F, 0.0F);
      this.druidAntlerL1.addChild(this.druidAntlerL2);

      this.druidAntlerL3 = new ModelRenderer(this, 95, 0);
      this.druidAntlerL3.setRotationPoint(0.0F, -6.0F, 0.0F);
      this.druidAntlerL3.addBox(0.5F, -4.0F, -0.5F, 1, 4, 1, scale);
      this.setRotation(this.druidAntlerL3, 0.0F, 0.0F, 0.25F);
      this.druidAntlerL1.addChild(this.druidAntlerL3);

      // ==========================================
      // --- HAT STYLE 3: VOID WARLOCK OPEN-FACE COWL ---
      // ==========================================
      this.voidCowlTop = new ModelRenderer(this, 0, 36);
      this.voidCowlTop.addBox(-4.5F, -8.5F, -4.5F, 9, 2, 9, scale + 0.1F);
      super.bipedHead.addChild(this.voidCowlTop);

      this.voidCowlLeft = new ModelRenderer(this, 0, 36);
      this.voidCowlLeft.addBox(3.5F, -7.0F, -4.5F, 1, 8, 9, scale + 0.05F);
      super.bipedHead.addChild(this.voidCowlLeft);

      this.voidCowlRight = new ModelRenderer(this, 0, 36);
      this.voidCowlRight.addBox(-4.5F, -7.0F, -4.5F, 1, 8, 9, scale + 0.05F);
      super.bipedHead.addChild(this.voidCowlRight);

      this.voidCowlBack = new ModelRenderer(this, 0, 36);
      this.voidCowlBack.addBox(-4.5F, -7.0F, 3.5F, 9, 8, 1, scale + 0.05F);
      super.bipedHead.addChild(this.voidCowlBack);

      this.voidCowlPeak = new ModelRenderer(this, 0, 36);
      this.voidCowlPeak.addBox(-4.0F, -8.8F, -5.2F, 8, 2, 1, scale);
      super.bipedHead.addChild(this.voidCowlPeak);

      this.voidHornL1 = new ModelRenderer(this, 120, 0);
      this.voidHornL1.setRotationPoint(3.6F, -6.5F, -0.5F);
      this.voidHornL1.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, scale);
      this.setRotation(this.voidHornL1, -0.2F, 0.0F, 0.35F);
      super.bipedHead.addChild(this.voidHornL1);

      this.voidHornL2 = new ModelRenderer(this, 120, 0);
      this.voidHornL2.setRotationPoint(0.0F, -4.0F, 0.0F);
      this.voidHornL2.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, scale);
      this.setRotation(this.voidHornL2, -0.3F, 0.0F, -0.15F);
      this.voidHornL1.addChild(this.voidHornL2);

      this.voidHornR1 = new ModelRenderer(this, 120, 0);
      this.voidHornR1.setRotationPoint(-3.6F, -6.5F, -0.5F);
      this.voidHornR1.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, scale);
      this.setRotation(this.voidHornR1, -0.2F, 0.0F, -0.35F);
      super.bipedHead.addChild(this.voidHornR1);

      this.voidHornR2 = new ModelRenderer(this, 120, 0);
      this.voidHornR2.setRotationPoint(0.0F, -4.0F, 0.0F);
      this.voidHornR2.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, scale);
      this.setRotation(this.voidHornR2, -0.3F, 0.0F, 0.15F);
      this.voidHornR1.addChild(this.voidHornR2);

      // ==========================================
      // --- HAT STYLE 4: GOTHIC ARISTOCRAT FASCINATOR ---
      // ==========================================
      this.gothicBrim = new ModelRenderer(this, 0, 49);
      this.gothicBrim.setRotationPoint(2.5F, -8.0F, 0.0F);
      this.gothicBrim.addBox(-4.0F, -1.0F, -4.0F, 8, 1, 8, scale);
      this.setRotation(this.gothicBrim, -0.08F, 0.0F, 0.22F);
      super.bipedHead.addChild(this.gothicBrim);

      this.gothicCrown = new ModelRenderer(this, 31, 34);
      this.gothicCrown.setRotationPoint(0.0F, -1.0F, 0.0F);
      this.gothicCrown.addBox(-3.0F, -5.0F, -3.0F, 6, 5, 6, scale);
      this.gothicBrim.addChild(this.gothicCrown);

      this.gothicRibbon = new ModelRenderer(this, 0, 36);
      this.gothicRibbon.setRotationPoint(0.0F, -1.0F, 0.0F);
      this.gothicRibbon.addBox(-3.2F, -2.0F, -3.2F, 6, 2, 6, scale + 0.1F);
      this.gothicBrim.addChild(this.gothicRibbon);

      this.gothicRose = new ModelRenderer(this, 61, 39);
      this.gothicRose.setRotationPoint(3.0F, -2.0F, -1.0F);
      this.gothicRose.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, scale);
      this.gothicBrim.addChild(this.gothicRose);

      this.gothicVeil = new ModelRenderer(this, 0, 36);
      this.gothicVeil.setRotationPoint(3.2F, 0.0F, -2.0F);
      this.gothicVeil.addBox(0.0F, 0.0F, 0.0F, 1, 6, 4, scale);
      this.gothicBrim.addChild(this.gothicVeil);

      // ==========================================
      // --- ROBE STYLE 0: CLASSIC COVEN ROBES ---
      // ==========================================
      this.covenSkirt = new ModelRenderer(this, 43, 46);
      this.covenSkirt.addBox(-4.5F, 11.0F, -2.5F, 9, 7, 5, scale + 0.05F);
      super.bipedBody.addChild(this.covenSkirt);

      this.covenBelt = new ModelRenderer(this, 43, 46);
      this.covenBelt.addBox(-4.5F, 9.0F, -2.5F, 9, 2, 5, scale + 0.15F);
      super.bipedBody.addChild(this.covenBelt);

      this.potionVial = new ModelRenderer(this, 61, 32);
      this.potionVial.addBox(-5.2F, 9.5F, -1.0F, 2, 3, 2, scale);
      super.bipedBody.addChild(this.potionVial);

      this.pouch = new ModelRenderer(this, 43, 46);
      this.pouch.addBox(3.4F, 9.5F, -1.0F, 2, 3, 2, scale);
      super.bipedBody.addChild(this.pouch);

      this.covenCollar = new ModelRenderer(this, 0, 36);
      this.covenCollar.addBox(-3.0F, 0.0F, -2.3F, 6, 3, 1, scale);
      super.bipedBody.addChild(this.covenCollar);

      // ==========================================
      // --- ROBE STYLE 1: IMPERIAL ARCHMAGE ROBES ---
      // ==========================================
      this.archmageSkirt = new ModelRenderer(this, 43, 44);
      this.archmageSkirt.addBox(-4.8F, 11.0F, -2.8F, 9, 12, 5, scale + 0.1F);
      super.bipedBody.addChild(this.archmageSkirt);

      this.archmageCollarBack = new ModelRenderer(this, 43, 44);
      this.archmageCollarBack.addBox(-5.0F, -6.0F, 2.4F, 10, 6, 1, scale);
      this.setRotation(this.archmageCollarBack, 0.12F, 0.0F, 0.0F);
      super.bipedBody.addChild(this.archmageCollarBack);

      this.archmageCollarL = new ModelRenderer(this, 43, 44);
      this.archmageCollarL.addBox(4.0F, -5.5F, -0.5F, 1, 5, 3, scale);
      this.setRotation(this.archmageCollarL, 0.0F, 0.0F, 0.15F);
      super.bipedBody.addChild(this.archmageCollarL);

      this.archmageCollarR = new ModelRenderer(this, 43, 44);
      this.archmageCollarR.addBox(-5.0F, -5.5F, -0.5F, 1, 5, 3, scale);
      this.setRotation(this.archmageCollarR, 0.0F, 0.0F, -0.15F);
      super.bipedBody.addChild(this.archmageCollarR);

      this.archmageCapelet = new ModelRenderer(this, 43, 44);
      this.archmageCapelet.addBox(-5.0F, 0.0F, 2.4F, 10, 12, 1, scale);
      super.bipedBody.addChild(this.archmageCapelet);

      this.archmageBrooch = new ModelRenderer(this, 61, 32);
      this.archmageBrooch.addBox(-1.5F, 1.5F, -2.6F, 3, 3, 1, scale + 0.1F);
      super.bipedBody.addChild(this.archmageBrooch);

      this.archmageSleeveR = new ModelRenderer(this, 43, 44);
      this.archmageSleeveR.addBox(-4.5F, 4.0F, -3.0F, 6, 8, 6, scale + 0.2F);
      super.bipedRightArm.addChild(this.archmageSleeveR);

      this.archmageSleeveL = new ModelRenderer(this, 43, 44);
      this.archmageSleeveL.addBox(-1.5F, 4.0F, -3.0F, 6, 8, 6, scale + 0.2F);
      super.bipedLeftArm.addChild(this.archmageSleeveL);

      // ==========================================
      // --- ROBE STYLE 2: SYLVAN DRUID GARB ---
      // ==========================================
      this.druidSkirt = new ModelRenderer(this, 43, 46);
      this.druidSkirt.addBox(-4.5F, 11.0F, -2.5F, 9, 6, 5, scale + 0.05F);
      super.bipedBody.addChild(this.druidSkirt);

      this.druidPeltChest = new ModelRenderer(this, 95, 0);
      this.druidPeltChest.addBox(-5.0F, -0.5F, -2.8F, 7, 6, 2, scale + 0.15F);
      super.bipedBody.addChild(this.druidPeltChest);

      this.druidPeltR = new ModelRenderer(this, 95, 0);
      this.druidPeltR.addBox(-4.5F, -3.0F, -3.0F, 6, 6, 6, scale + 0.25F);
      super.bipedRightArm.addChild(this.druidPeltR);

      this.druidTotem = new ModelRenderer(this, 61, 32);
      this.druidTotem.addBox(-1.5F, 8.5F, -3.0F, 3, 4, 1, scale + 0.1F);
      super.bipedBody.addChild(this.druidTotem);

      this.druidVineBelt = new ModelRenderer(this, 95, 0);
      this.druidVineBelt.addBox(-4.5F, 8.5F, -2.5F, 9, 3, 5, scale + 0.1F);
      super.bipedBody.addChild(this.druidVineBelt);

      // ==========================================
      // --- ROBE STYLE 3: VOID WARLOCK SHROUD ---
      // ==========================================
      this.voidShroudFront = new ModelRenderer(this, 43, 46);
      this.voidShroudFront.addBox(-4.5F, 11.0F, -2.5F, 9, 13, 1, scale);
      super.bipedBody.addChild(this.voidShroudFront);

      this.voidShroudBack = new ModelRenderer(this, 43, 46);
      this.voidShroudBack.addBox(-4.5F, 11.0F, 1.8F, 9, 14, 1, scale);
      super.bipedBody.addChild(this.voidShroudBack);

      this.voidRibs = new ModelRenderer(this, 120, 0);
      this.voidRibs.addBox(-4.0F, 1.0F, -2.6F, 8, 8, 1, scale + 0.1F);
      super.bipedBody.addChild(this.voidRibs);

      this.voidSoulCore = new ModelRenderer(this, 61, 32);
      this.voidSoulCore.addBox(-1.5F, 3.5F, -2.8F, 3, 3, 1, scale + 0.15F);
      super.bipedBody.addChild(this.voidSoulCore);

      this.voidSpikeR = new ModelRenderer(this, 120, 0);
      this.voidSpikeR.setRotationPoint(-2.5F, 1.0F, 2.2F);
      this.voidSpikeR.addBox(-0.5F, -3.0F, -0.5F, 1, 6, 1, scale);
      this.setRotation(this.voidSpikeR, -0.4F, 0.0F, -0.35F);
      super.bipedBody.addChild(this.voidSpikeR);

      this.voidSpikeL = new ModelRenderer(this, 120, 0);
      this.voidSpikeL.setRotationPoint(2.5F, 1.0F, 2.2F);
      this.voidSpikeL.addBox(-0.5F, -3.0F, -0.5F, 1, 6, 1, scale);
      this.setRotation(this.voidSpikeL, -0.4F, 0.0F, 0.35F);
      super.bipedBody.addChild(this.voidSpikeL);

      // ==========================================
      // --- ROBE STYLE 4: GOTHIC ARISTOCRAT BALL GOWN ---
      // ==========================================
      this.gothicSkirtUpper = new ModelRenderer(this, 43, 44);
      this.gothicSkirtUpper.addBox(-5.0F, 10.0F, -3.0F, 10, 4, 6, scale + 0.1F);
      super.bipedBody.addChild(this.gothicSkirtUpper);

      this.gothicSkirtLower = new ModelRenderer(this, 36, 44);
      this.gothicSkirtLower.addBox(-5.5F, 13.0F, -3.5F, 11, 7, 7, scale + 0.15F);
      super.bipedBody.addChild(this.gothicSkirtLower);

      this.gothicRuff = new ModelRenderer(this, 0, 36);
      this.gothicRuff.addBox(-4.5F, -1.5F, -2.8F, 9, 2, 5, scale + 0.1F);
      super.bipedBody.addChild(this.gothicRuff);

      this.gothicCorset = new ModelRenderer(this, 43, 44);
      this.gothicCorset.addBox(-4.2F, 4.0F, -2.4F, 8, 7, 5, scale + 0.12F);
      super.bipedBody.addChild(this.gothicCorset);

      this.gothicRoseBrooch = new ModelRenderer(this, 61, 39);
      this.gothicRoseBrooch.addBox(-1.0F, 0.5F, -2.8F, 2, 2, 1, scale + 0.1F);
      super.bipedBody.addChild(this.gothicRoseBrooch);

      this.gothicPuffR = new ModelRenderer(this, 43, 44);
      this.gothicPuffR.addBox(-4.0F, -3.0F, -3.0F, 5, 5, 6, scale + 0.2F);
      super.bipedRightArm.addChild(this.gothicPuffR);

      this.gothicPuffL = new ModelRenderer(this, 43, 44);
      this.gothicPuffL.addBox(-1.0F, -3.0F, -3.0F, 5, 5, 6, scale + 0.2F);
      super.bipedLeftArm.addChild(this.gothicPuffL);

      // ==========================================
      // --- BELT STYLES (RENDERED ON BIPED BODY) ---
      // ==========================================
      this.bitingBuckle = new ModelRenderer(this, 120, 0);
      this.bitingBuckle.addBox(-2.0F, 9.0F, -2.7F, 4, 3, 1, scale + 0.2F);
      super.bipedBody.addChild(this.bitingBuckle);

      // Belt Style 0: Classic Coven Belt
      this.beltCovenBand = new ModelRenderer(this, 43, 46);
      this.beltCovenBand.addBox(-4.5F, 9.0F, -2.5F, 9, 3, 5, scale + 0.12F);
      super.bipedBody.addChild(this.beltCovenBand);

      this.beltCovenBuckle = new ModelRenderer(this, 61, 32);
      this.beltCovenBuckle.addBox(-1.5F, 9.0F, -2.8F, 3, 3, 1, scale + 0.2F);
      super.bipedBody.addChild(this.beltCovenBuckle);

      this.beltCovenVials = new ModelRenderer(this, 61, 32);
      this.beltCovenVials.addBox(-5.2F, 9.5F, -1.0F, 2, 3, 2, scale + 0.1F);
      super.bipedBody.addChild(this.beltCovenVials);

      this.beltCovenPouch = new ModelRenderer(this, 43, 46);
      this.beltCovenPouch.addBox(3.4F, 9.5F, -1.0F, 2, 3, 2, scale + 0.1F);
      super.bipedBody.addChild(this.beltCovenPouch);

      // Belt Style 1: Archmage Ceremonial Sash
      this.beltArchmageSash = new ModelRenderer(this, 43, 44);
      this.beltArchmageSash.addBox(-4.6F, 8.5F, -2.6F, 9, 4, 5, scale + 0.15F);
      super.bipedBody.addChild(this.beltArchmageSash);

      this.beltArchmageSeal = new ModelRenderer(this, 61, 32);
      this.beltArchmageSeal.addBox(-2.0F, 8.5F, -3.0F, 4, 4, 1, scale + 0.22F);
      super.bipedBody.addChild(this.beltArchmageSeal);

      this.beltArchmageRibbon = new ModelRenderer(this, 43, 44);
      this.beltArchmageRibbon.addBox(-3.0F, 12.0F, -2.7F, 2, 6, 1, scale + 0.1F);
      super.bipedBody.addChild(this.beltArchmageRibbon);

      this.beltArchmagePendant = new ModelRenderer(this, 61, 32);
      this.beltArchmagePendant.addBox(1.5F, 12.0F, -2.7F, 1, 3, 1, scale + 0.1F);
      super.bipedBody.addChild(this.beltArchmagePendant);

      // Belt Style 2: Druidic Root Girdle
      this.beltDruidRoots = new ModelRenderer(this, 95, 0);
      this.beltDruidRoots.addBox(-4.5F, 8.5F, -2.5F, 9, 3, 5, scale + 0.15F);
      super.bipedBody.addChild(this.beltDruidRoots);

      this.beltDruidTotem = new ModelRenderer(this, 61, 32);
      this.beltDruidTotem.addBox(-1.5F, 8.0F, -2.9F, 3, 4, 1, scale + 0.2F);
      super.bipedBody.addChild(this.beltDruidTotem);

      this.beltDruidSeedPouch = new ModelRenderer(this, 95, 0);
      this.beltDruidSeedPouch.addBox(3.4F, 9.5F, -1.0F, 2, 3, 2, scale + 0.1F);
      super.bipedBody.addChild(this.beltDruidSeedPouch);

      this.beltDruidVines = new ModelRenderer(this, 95, 0);
      this.beltDruidVines.addBox(-3.5F, 11.5F, -2.7F, 2, 5, 1, scale);
      super.bipedBody.addChild(this.beltDruidVines);

      // Belt Style 3: Void Skull Chained Belt
      this.beltVoidBand = new ModelRenderer(this, 120, 0);
      this.beltVoidBand.addBox(-4.5F, 9.0F, -2.5F, 9, 3, 5, scale + 0.15F);
      super.bipedBody.addChild(this.beltVoidBand);

      this.beltVoidSkull = new ModelRenderer(this, 120, 0);
      this.beltVoidSkull.addBox(-1.5F, 8.5F, -2.9F, 3, 3, 2, scale + 0.2F);
      super.bipedBody.addChild(this.beltVoidSkull);

      this.beltVoidChains = new ModelRenderer(this, 43, 46);
      this.beltVoidChains.addBox(-3.5F, 11.5F, -2.7F, 7, 2, 1, scale);
      super.bipedBody.addChild(this.beltVoidChains);

      this.beltVoidSpurR = new ModelRenderer(this, 120, 0);
      this.beltVoidSpurR.addBox(-5.4F, 9.5F, -0.5F, 1, 3, 1, scale);
      super.bipedBody.addChild(this.beltVoidSpurR);

      this.beltVoidSpurL = new ModelRenderer(this, 120, 0);
      this.beltVoidSpurL.addBox(4.4F, 9.5F, -0.5F, 1, 3, 1, scale);
      super.bipedBody.addChild(this.beltVoidSpurL);

      // Belt Style 4: Gothic Velvet Cincher Corset
      this.beltGothicCorset = new ModelRenderer(this, 43, 44);
      this.beltGothicCorset.addBox(-4.4F, 7.5F, -2.5F, 9, 5, 5, scale + 0.16F);
      super.bipedBody.addChild(this.beltGothicCorset);

      this.beltGothicLaces = new ModelRenderer(this, 0, 36);
      this.beltGothicLaces.addBox(-2.0F, 7.5F, -2.8F, 4, 5, 1, scale + 0.2F);
      super.bipedBody.addChild(this.beltGothicLaces);

      this.beltGothicRose = new ModelRenderer(this, 61, 39);
      this.beltGothicRose.addBox(-1.0F, 9.0F, -3.0F, 2, 2, 1, scale + 0.25F);
      super.bipedBody.addChild(this.beltGothicRose);

      this.beltGothicBowR = new ModelRenderer(this, 61, 39);
      this.beltGothicBowR.addBox(-5.3F, 9.5F, -0.5F, 1, 3, 2, scale);
      super.bipedBody.addChild(this.beltGothicBowR);

      this.beltGothicBowL = new ModelRenderer(this, 61, 39);
      this.beltGothicBowL.addBox(4.3F, 9.5F, -0.5F, 1, 3, 2, scale);
      super.bipedBody.addChild(this.beltGothicBowL);

      // ==========================================
      // --- SHOE / BOOT STYLES (RENDERED ON LEGS) ---
      // ==========================================
      // Shoe Style 0: Classic Pointed Witch Shoes
      this.shoeClassicR = new ModelRenderer(this, 43, 46);
      this.shoeClassicR.addBox(-2.1F, 9.0F, -2.1F, 4, 3, 5, scale + 0.05F);
      super.bipedRightLeg.addChild(this.shoeClassicR);

      this.shoeClassicTipR = new ModelRenderer(this, 43, 46);
      this.shoeClassicTipR.addBox(-1.5F, 10.0F, -3.5F, 3, 2, 2, scale);
      super.bipedRightLeg.addChild(this.shoeClassicTipR);

      this.shoeClassicBuckleR = new ModelRenderer(this, 61, 32);
      this.shoeClassicBuckleR.addBox(-1.0F, 8.8F, -2.5F, 2, 2, 1, scale + 0.1F);
      super.bipedRightLeg.addChild(this.shoeClassicBuckleR);

      this.shoeClassicL = new ModelRenderer(this, 43, 46);
      this.shoeClassicL.addBox(-1.9F, 9.0F, -2.1F, 4, 3, 5, scale + 0.05F);
      super.bipedLeftLeg.addChild(this.shoeClassicL);

      this.shoeClassicTipL = new ModelRenderer(this, 43, 46);
      this.shoeClassicTipL.addBox(-1.5F, 10.0F, -3.5F, 3, 2, 2, scale);
      super.bipedLeftLeg.addChild(this.shoeClassicTipL);

      this.shoeClassicBuckleL = new ModelRenderer(this, 61, 32);
      this.shoeClassicBuckleL.addBox(-1.0F, 8.8F, -2.5F, 2, 2, 1, scale + 0.1F);
      super.bipedLeftLeg.addChild(this.shoeClassicBuckleL);

      // Shoe Style 1: Imperial Archmage High Boots
      this.bootArchmageR = new ModelRenderer(this, 43, 44);
      this.bootArchmageR.addBox(-2.1F, 6.0F, -2.1F, 4, 6, 4, scale + 0.08F);
      super.bipedRightLeg.addChild(this.bootArchmageR);

      this.bootArchmageCuffR = new ModelRenderer(this, 0, 36);
      this.bootArchmageCuffR.addBox(-2.4F, 5.0F, -2.3F, 4, 2, 4, scale + 0.12F);
      super.bipedRightLeg.addChild(this.bootArchmageCuffR);

      this.bootArchmageSpurR = new ModelRenderer(this, 61, 32);
      this.bootArchmageSpurR.addBox(-1.0F, 9.5F, -2.4F, 2, 2, 1, scale + 0.1F);
      super.bipedRightLeg.addChild(this.bootArchmageSpurR);

      this.bootArchmageL = new ModelRenderer(this, 43, 44);
      this.bootArchmageL.addBox(-1.9F, 6.0F, -2.1F, 4, 6, 4, scale + 0.08F);
      super.bipedLeftLeg.addChild(this.bootArchmageL);

      this.bootArchmageCuffL = new ModelRenderer(this, 0, 36);
      this.bootArchmageCuffL.addBox(-1.6F, 5.0F, -2.3F, 4, 2, 4, scale + 0.12F);
      super.bipedLeftLeg.addChild(this.bootArchmageCuffL);

      this.bootArchmageSpurL = new ModelRenderer(this, 61, 32);
      this.bootArchmageSpurL.addBox(-1.0F, 9.5F, -2.4F, 2, 2, 1, scale + 0.1F);
      super.bipedLeftLeg.addChild(this.bootArchmageSpurL);

      // Shoe Style 2: Sylvan Druid Footwraps
      this.wrapDruidR = new ModelRenderer(this, 95, 0);
      this.wrapDruidR.addBox(-2.1F, 8.0F, -2.1F, 4, 4, 4, scale + 0.08F);
      super.bipedRightLeg.addChild(this.wrapDruidR);

      this.wrapDruidStrapsR = new ModelRenderer(this, 95, 0);
      this.wrapDruidStrapsR.addBox(-2.2F, 4.0F, -2.2F, 4, 4, 4, scale + 0.05F);
      super.bipedRightLeg.addChild(this.wrapDruidStrapsR);

      this.wrapDruidCharmR = new ModelRenderer(this, 61, 32);
      this.wrapDruidCharmR.addBox(-2.5F, 8.5F, -1.0F, 1, 2, 2, scale + 0.1F);
      super.bipedRightLeg.addChild(this.wrapDruidCharmR);

      this.wrapDruidL = new ModelRenderer(this, 95, 0);
      this.wrapDruidL.addBox(-1.9F, 8.0F, -2.1F, 4, 4, 4, scale + 0.08F);
      super.bipedLeftLeg.addChild(this.wrapDruidL);

      this.wrapDruidStrapsL = new ModelRenderer(this, 95, 0);
      this.wrapDruidStrapsL.addBox(-1.8F, 4.0F, -2.2F, 4, 4, 4, scale + 0.05F);
      super.bipedLeftLeg.addChild(this.wrapDruidStrapsL);

      this.wrapDruidCharmL = new ModelRenderer(this, 61, 32);
      this.wrapDruidCharmL.addBox(1.5F, 8.5F, -1.0F, 1, 2, 2, scale + 0.1F);
      super.bipedLeftLeg.addChild(this.wrapDruidCharmL);

      // Shoe Style 3: Void Shadow Spurred Boots
      this.bootVoidR = new ModelRenderer(this, 43, 46);
      this.bootVoidR.addBox(-2.1F, 7.0F, -2.1F, 4, 5, 4, scale + 0.1F);
      super.bipedRightLeg.addChild(this.bootVoidR);

      this.bootVoidToeClawR = new ModelRenderer(this, 120, 0);
      this.bootVoidToeClawR.addBox(-1.5F, 10.0F, -3.0F, 3, 2, 1, scale);
      super.bipedRightLeg.addChild(this.bootVoidToeClawR);

      this.bootVoidHeelSpurR = new ModelRenderer(this, 120, 0);
      this.bootVoidHeelSpurR.setRotationPoint(0.0F, 10.0F, 2.0F);
      this.bootVoidHeelSpurR.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, scale);
      this.setRotation(this.bootVoidHeelSpurR, 0.35F, 0.0F, 0.0F);
      super.bipedRightLeg.addChild(this.bootVoidHeelSpurR);

      this.bootVoidL = new ModelRenderer(this, 43, 46);
      this.bootVoidL.addBox(-1.9F, 7.0F, -2.1F, 4, 5, 4, scale + 0.1F);
      super.bipedLeftLeg.addChild(this.bootVoidL);

      this.bootVoidToeClawL = new ModelRenderer(this, 120, 0);
      this.bootVoidToeClawL.addBox(-1.5F, 10.0F, -3.0F, 3, 2, 1, scale);
      super.bipedLeftLeg.addChild(this.bootVoidToeClawL);

      this.bootVoidHeelSpurL = new ModelRenderer(this, 120, 0);
      this.bootVoidHeelSpurL.setRotationPoint(0.0F, 10.0F, 2.0F);
      this.bootVoidHeelSpurL.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, scale);
      this.setRotation(this.bootVoidHeelSpurL, 0.35F, 0.0F, 0.0F);
      super.bipedLeftLeg.addChild(this.bootVoidHeelSpurL);

      // Shoe Style 4: Gothic Victorian Heeled Booties
      this.bootGothicR = new ModelRenderer(this, 43, 44);
      this.bootGothicR.addBox(-2.1F, 5.0F, -2.1F, 4, 7, 4, scale + 0.08F);
      super.bipedRightLeg.addChild(this.bootGothicR);

      this.bootGothicLacesR = new ModelRenderer(this, 0, 36);
      this.bootGothicLacesR.addBox(-1.0F, 6.0F, -2.4F, 2, 5, 1, scale);
      super.bipedRightLeg.addChild(this.bootGothicLacesR);

      this.bootGothicHeelR = new ModelRenderer(this, 0, 36);
      this.bootGothicHeelR.addBox(-1.5F, 11.5F, 1.0F, 3, 1, 1, scale);
      super.bipedRightLeg.addChild(this.bootGothicHeelR);

      this.bootGothicRoseR = new ModelRenderer(this, 61, 39);
      this.bootGothicRoseR.addBox(-2.5F, 6.5F, -1.0F, 1, 2, 2, scale);
      super.bipedRightLeg.addChild(this.bootGothicRoseR);

      this.bootGothicL = new ModelRenderer(this, 43, 44);
      this.bootGothicL.addBox(-1.9F, 5.0F, -2.1F, 4, 7, 4, scale + 0.08F);
      super.bipedLeftLeg.addChild(this.bootGothicL);

      this.bootGothicLacesL = new ModelRenderer(this, 0, 36);
      this.bootGothicLacesL.addBox(-1.0F, 6.0F, -2.4F, 2, 5, 1, scale);
      super.bipedLeftLeg.addChild(this.bootGothicLacesL);

      this.bootGothicHeelL = new ModelRenderer(this, 0, 36);
      this.bootGothicHeelL.addBox(-1.5F, 11.5F, 1.0F, 3, 1, 1, scale);
      super.bipedLeftLeg.addChild(this.bootGothicHeelL);

      this.bootGothicRoseL = new ModelRenderer(this, 61, 39);
      this.bootGothicRoseL.addBox(1.5F, 6.5F, -1.0F, 1, 2, 2, scale);
      super.bipedLeftLeg.addChild(this.bootGothicRoseL);

      // ==========================================
      // --- NECROMANCER SHOULDERS ---
      // ==========================================
      if(shoulders) {
         this.Shape1 = new ModelRenderer(this, 61, 32);
         this.Shape1.addBox(0.0F, 0.0F, 0.0F, 5, 1, 6, scale + 0.1F);
         this.Shape1.setRotationPoint(-4.0F, -2.0F, -3.0F);
         this.Shape1.mirror = true;
         this.Shape2 = new ModelRenderer(this, 61, 39);
         this.Shape2.addBox(0.0F, 0.0F, 0.0F, 5, 1, 6, scale + 0.1F);
         this.Shape2.setRotationPoint(0.0F, -2.0F, -3.0F);
         this.Shape2.mirror = true;
         super.bipedRightArm.addChild(this.Shape1);
         super.bipedLeftArm.addChild(this.Shape2);
      }

      // ==========================================
      // --- BARK BELT PIECES ---
      // ==========================================
      this.headRight1 = new ModelRenderer(this, 124, 0);
      this.headRight1.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1);
      this.headRight1.setRotationPoint(-4.0F, 0.0F, 0.0F);
      this.headRight1.setTextureSize(64, 128);
      this.headRight1.mirror = true;
      this.setRotation(this.headRight1, -0.1487144F, 0.0F, -0.4089647F);
      this.headLeft1 = new ModelRenderer(this, 124, 0);
      this.headLeft1.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1);
      this.headLeft1.setRotationPoint(4.0F, 0.0F, 0.0F);
      this.headLeft1.setTextureSize(64, 128);
      this.headLeft1.mirror = true;
      this.setRotation(this.headLeft1, -0.1487144F, 0.0F, 0.4089647F);
      this.legRightF = new ModelRenderer(this, 95, 0);
      this.legRightF.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1);
      this.legRightF.setRotationPoint(-4.0F, 13.0F, -3.0F);
      this.legRightF.setTextureSize(64, 128);
      this.legRightF.mirror = true;
      this.setRotation(this.legRightF, 0.0F, 0.0F, -0.2230717F);
      this.legLeftF = new ModelRenderer(this, 95, 0);
      this.legLeftF.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1);
      this.legLeftF.setRotationPoint(1.0F, 13.0F, -3.0F);
      this.legLeftF.setTextureSize(64, 128);
      this.legLeftF.mirror = true;
      this.setRotation(this.legLeftF, 0.0F, 0.0F, 0.1230717F);
      this.legRightB = new ModelRenderer(this, 95, 0);
      this.legRightB.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1);
      this.legRightB.setRotationPoint(-4.0F, 13.0F, 2.0F);
      this.legRightB.setTextureSize(64, 128);
      this.legRightB.mirror = true;
      this.setRotation(this.legRightB, 0.0F, 0.0F, -0.2230717F);
      this.legLeftB = new ModelRenderer(this, 95, 0);
      this.legLeftB.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1);
      this.legLeftB.setRotationPoint(1.0F, 13.0F, 2.0F);
      this.legLeftB.setTextureSize(64, 128);
      this.legLeftB.mirror = true;
      this.setRotation(this.legLeftB, 0.0F, 0.0F, 0.1230717F);
      this.bodyF = new ModelRenderer(this, 95, 0);
      this.bodyF.addBox(0.0F, 0.0F, 0.0F, 4, 6, 1);
      this.bodyF.setRotationPoint(-2.0F, 11.0F, -3.0F);
      this.bodyF.setTextureSize(64, 128);
      this.bodyF.mirror = true;
      this.setRotation(this.bodyF, 0.0F, 0.0F, 0.0F);
      this.bodyB = new ModelRenderer(this, 95, 0);
      this.bodyB.addBox(0.0F, 0.0F, 0.0F, 4, 6, 1);
      this.bodyB.setRotationPoint(-2.0F, 11.0F, 2.0F);
      this.bodyB.setTextureSize(64, 128);
      this.bodyB.mirror = true;
      this.setRotation(this.bodyB, 0.0F, 0.0F, 0.0F);
      this.armRightF = new ModelRenderer(this, 95, 0);
      this.armRightF.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1);
      this.armRightF.setRotationPoint(-3.0F, 2.0F, -3.0F);
      this.armRightF.setTextureSize(64, 128);
      this.armRightF.mirror = true;
      this.setRotation(this.armRightF, 0.0F, 0.0F, 0.0F);
      this.armLeftF = new ModelRenderer(this, 95, 0);
      this.armLeftF.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1);
      this.armLeftF.setRotationPoint(2.0F, 2.0F, -3.0F);
      this.armLeftF.setTextureSize(64, 128);
      this.armLeftF.mirror = true;
      this.setRotation(this.armLeftF, 0.0F, 0.0F, 0.0F);
      this.armRightB = new ModelRenderer(this, 95, 0);
      this.armRightB.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1);
      this.armRightB.setRotationPoint(-3.0F, 2.0F, 2.0F);
      this.armRightB.setTextureSize(64, 128);
      this.armRightB.mirror = true;
      this.setRotation(this.armRightB, 0.0F, 0.0F, 0.0F);
      this.armLeftB = new ModelRenderer(this, 95, 0);
      this.armLeftB.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1);
      this.armLeftB.setRotationPoint(2.0F, 2.0F, 2.0F);
      this.armLeftB.setTextureSize(64, 128);
      this.armLeftB.mirror = true;
      this.setRotation(this.armLeftB, 0.0F, 0.0F, 0.0F);
      this.armLeftOut = new ModelRenderer(this, 95, 0);
      this.armLeftOut.addBox(0.0F, 0.0F, 0.0F, 1, 7, 2);
      this.armLeftOut.setRotationPoint(4.0F, 2.0F, -1.0F);
      this.armLeftOut.setTextureSize(64, 128);
      this.armLeftOut.mirror = true;
      this.setRotation(this.armLeftOut, 0.0F, 0.0F, 0.0F);
      this.armRightOut = new ModelRenderer(this, 95, 0);
      this.armRightOut.addBox(0.0F, 0.0F, 0.0F, 1, 7, 2);
      this.armRightOut.setRotationPoint(-5.0F, 2.0F, -1.0F);
      this.armRightOut.setTextureSize(64, 128);
      this.armRightOut.mirror = true;
      this.setRotation(this.armRightOut, 0.0F, 0.0F, 0.0F);
      this.spikeLowerRight = new ModelRenderer(this, 120, 0);
      this.spikeLowerRight.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1);
      this.spikeLowerRight.setRotationPoint(-1.0F, 7.0F, 2.0F);
      this.spikeLowerRight.setTextureSize(128, 64);
      this.spikeLowerRight.mirror = true;
      this.setRotation(this.spikeLowerRight, -0.7807508F, -0.1858931F, 0.0F);
      this.spikeLowerLeft = new ModelRenderer(this, 120, 0);
      this.spikeLowerLeft.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1);
      this.spikeLowerLeft.setRotationPoint(1.0F, 7.0F, 2.0F);
      this.spikeLowerLeft.setTextureSize(128, 64);
      this.spikeLowerLeft.mirror = true;
      this.setRotation(this.spikeLowerLeft, -0.7807508F, 0.1858931F, 0.0F);
      this.spikeUpperLeft = new ModelRenderer(this, 120, 0);
      this.spikeUpperLeft.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1);
      this.spikeUpperLeft.setRotationPoint(2.0F, 3.0F, 2.0F);
      this.spikeUpperLeft.setTextureSize(128, 64);
      this.spikeUpperLeft.mirror = true;
      this.setRotation(this.spikeUpperLeft, -0.7807508F, 0.1858931F, 0.0F);
      this.spikeUpperRight = new ModelRenderer(this, 120, 0);
      this.spikeUpperRight.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1);
      this.spikeUpperRight.setRotationPoint(-2.0F, 3.0F, 2.0F);
      this.spikeUpperRight.setTextureSize(128, 64);
      this.spikeUpperRight.mirror = true;
      this.setRotation(this.spikeUpperRight, -0.7807508F, -0.1858931F, 0.0F);
      this.shoulderRight = new ModelRenderer(this, 108, 0);
      this.shoulderRight.addBox(0.0F, 0.0F, 0.0F, 5, 1, 5);
      this.shoulderRight.setRotationPoint(-9.0F, -1.5F, -2.5F);
      this.shoulderRight.setTextureSize(128, 64);
      this.shoulderRight.mirror = true;
      this.setRotation(this.shoulderRight, 0.0371786F, -0.1115358F, -0.1230717F);
      this.shoulderLeft = new ModelRenderer(this, 108, 0);
      this.shoulderLeft.addBox(0.0F, 0.0F, 0.0F, 5, 1, 5);
      this.shoulderLeft.setRotationPoint(4.0F, -2.5F, -1.5F);
      this.shoulderLeft.setTextureSize(128, 64);
      this.shoulderLeft.mirror = true;
      this.setRotation(this.shoulderLeft, 0.0F, 0.2974289F, 0.1830717F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      EntityLivingBase living = null;
      if(entity instanceof EntityLivingBase) {
         living = (EntityLivingBase)entity;
         ItemStack helm = living.getEquipmentInSlot(4);
         if(helm != null && helm.getItem() instanceof ItemWitchesClothes) {
            this.isBabasHat = (helm.getItem() == Witchery.Items.BABAS_HAT);
            this.hatStyle = ItemWitchesClothes.getStyle(helm);
         }
         ItemStack chest = living.getEquipmentInSlot(3);
         if(chest != null && chest.getItem() instanceof ItemWitchesClothes) {
            this.robeStyle = ItemWitchesClothes.getStyle(chest);
         }
         ItemStack belt = living.getEquipmentInSlot(2);
         if(belt != null && belt.getItem() instanceof ItemWitchesClothes) {
            this.beltStyle = ItemWitchesClothes.getStyle(belt);
            this.isBitingBelt = (belt.getItem() == Witchery.Items.BITING_BELT);
            this.isBarkBelt = (belt.getItem() == Witchery.Items.BARK_BELT);
         }
         ItemStack boots = living.getEquipmentInSlot(1);
         if(boots != null && boots.getItem() instanceof ItemWitchesClothes) {
            this.shoeStyle = ItemWitchesClothes.getStyle(boots);
         }
      }

      // ==========================================
      // --- HAT VISIBILITY CONFIGURATION ---
      // ==========================================
      boolean showHead = super.bipedHead.showModel && this.isHatSlot;
      this.babasHat.showModel = showHead && this.isBabasHat;

      boolean s0 = showHead && !this.isBabasHat && this.hatStyle == ItemWitchesClothes.STYLE_CLASSIC;
      this.hat.showModel = s0;
      if(this.hatCurve1 != null) this.hatCurve1.showModel = s0;
      if(this.hatCurve2 != null) this.hatCurve2.showModel = s0;
      if(this.hatCurve3 != null) this.hatCurve3.showModel = s0;

      boolean s1 = showHead && !this.isBabasHat && this.hatStyle == ItemWitchesClothes.STYLE_ARCHMAGE;
      if(this.archmageBrim != null) this.archmageBrim.showModel = s1;
      if(this.archmageCollar != null) this.archmageCollar.showModel = s1;
      if(this.archmageConeBase != null) this.archmageConeBase.showModel = s1;

      boolean s2 = showHead && !this.isBabasHat && this.hatStyle == ItemWitchesClothes.STYLE_DRUID;
      if(this.druidCirclet != null) this.druidCirclet.showModel = s2;
      if(this.druidGem != null) this.druidGem.showModel = s2;
      if(this.druidAntlerR1 != null) this.druidAntlerR1.showModel = s2;
      if(this.druidAntlerL1 != null) this.druidAntlerL1.showModel = s2;

      boolean s3 = showHead && !this.isBabasHat && this.hatStyle == ItemWitchesClothes.STYLE_VOID;
      if(this.voidCowlTop != null) this.voidCowlTop.showModel = s3;
      if(this.voidCowlLeft != null) this.voidCowlLeft.showModel = s3;
      if(this.voidCowlRight != null) this.voidCowlRight.showModel = s3;
      if(this.voidCowlBack != null) this.voidCowlBack.showModel = s3;
      if(this.voidCowlPeak != null) this.voidCowlPeak.showModel = s3;
      if(this.voidHornL1 != null) this.voidHornL1.showModel = s3;
      if(this.voidHornR1 != null) this.voidHornR1.showModel = s3;

      boolean s4 = showHead && !this.isBabasHat && this.hatStyle == ItemWitchesClothes.STYLE_GOTHIC;
      if(this.gothicBrim != null) this.gothicBrim.showModel = s4;

      // ==========================================
      // --- BELT VISIBILITY CONFIGURATION ---
      // ==========================================
      boolean showBelt = super.bipedBody.showModel && this.isBeltSlot;
      if(this.bitingBuckle != null) {
         this.bitingBuckle.showModel = showBelt && this.isBitingBelt;
      }

      boolean bs0 = showBelt && this.beltStyle == ItemWitchesClothes.STYLE_CLASSIC;
      if(this.beltCovenBand != null) this.beltCovenBand.showModel = bs0;
      if(this.beltCovenBuckle != null) this.beltCovenBuckle.showModel = bs0 && !this.isBitingBelt;
      if(this.beltCovenVials != null) this.beltCovenVials.showModel = bs0;
      if(this.beltCovenPouch != null) this.beltCovenPouch.showModel = bs0;

      boolean bs1 = showBelt && this.beltStyle == ItemWitchesClothes.STYLE_ARCHMAGE;
      if(this.beltArchmageSash != null) this.beltArchmageSash.showModel = bs1;
      if(this.beltArchmageSeal != null) this.beltArchmageSeal.showModel = bs1 && !this.isBitingBelt;
      if(this.beltArchmageRibbon != null) this.beltArchmageRibbon.showModel = bs1;
      if(this.beltArchmagePendant != null) this.beltArchmagePendant.showModel = bs1;

      boolean bs2 = showBelt && this.beltStyle == ItemWitchesClothes.STYLE_DRUID;
      if(this.beltDruidRoots != null) this.beltDruidRoots.showModel = bs2;
      if(this.beltDruidTotem != null) this.beltDruidTotem.showModel = bs2 && !this.isBitingBelt;
      if(this.beltDruidSeedPouch != null) this.beltDruidSeedPouch.showModel = bs2;
      if(this.beltDruidVines != null) this.beltDruidVines.showModel = bs2;

      boolean bs3 = showBelt && this.beltStyle == ItemWitchesClothes.STYLE_VOID;
      if(this.beltVoidBand != null) this.beltVoidBand.showModel = bs3;
      if(this.beltVoidSkull != null) this.beltVoidSkull.showModel = bs3 && !this.isBitingBelt;
      if(this.beltVoidChains != null) this.beltVoidChains.showModel = bs3;
      if(this.beltVoidSpurR != null) this.beltVoidSpurR.showModel = bs3;
      if(this.beltVoidSpurL != null) this.beltVoidSpurL.showModel = bs3;

      boolean bs4 = showBelt && this.beltStyle == ItemWitchesClothes.STYLE_GOTHIC;
      if(this.beltGothicCorset != null) this.beltGothicCorset.showModel = bs4;
      if(this.beltGothicLaces != null) this.beltGothicLaces.showModel = bs4;
      if(this.beltGothicRose != null) this.beltGothicRose.showModel = bs4 && !this.isBitingBelt;
      if(this.beltGothicBowR != null) this.beltGothicBowR.showModel = bs4;
      if(this.beltGothicBowL != null) this.beltGothicBowL.showModel = bs4;

      // ==========================================
      // --- ROBE VISIBILITY CONFIGURATION ---
      // ==========================================
      boolean showRobe = super.bipedBody.showModel && this.isRobeSlot;

      boolean rs0 = showRobe && this.robeStyle == ItemWitchesClothes.STYLE_CLASSIC;
      if(this.covenSkirt != null) this.covenSkirt.showModel = rs0;
      if(this.covenBelt != null) this.covenBelt.showModel = rs0;
      if(this.potionVial != null) this.potionVial.showModel = rs0;
      if(this.pouch != null) this.pouch.showModel = rs0;
      if(this.covenCollar != null) this.covenCollar.showModel = rs0;

      boolean rs1 = showRobe && this.robeStyle == ItemWitchesClothes.STYLE_ARCHMAGE;
      if(this.archmageSkirt != null) this.archmageSkirt.showModel = rs1;
      if(this.archmageCollarBack != null) this.archmageCollarBack.showModel = rs1;
      if(this.archmageCollarL != null) this.archmageCollarL.showModel = rs1;
      if(this.archmageCollarR != null) this.archmageCollarR.showModel = rs1;
      if(this.archmageCapelet != null) this.archmageCapelet.showModel = rs1;
      if(this.archmageBrooch != null) this.archmageBrooch.showModel = rs1;
      if(this.archmageSleeveR != null) this.archmageSleeveR.showModel = rs1 && super.bipedRightArm.showModel;
      if(this.archmageSleeveL != null) this.archmageSleeveL.showModel = rs1 && super.bipedLeftArm.showModel;

      boolean rs2 = showRobe && this.robeStyle == ItemWitchesClothes.STYLE_DRUID;
      if(this.druidSkirt != null) this.druidSkirt.showModel = rs2;
      if(this.druidPeltChest != null) this.druidPeltChest.showModel = rs2;
      if(this.druidPeltR != null) this.druidPeltR.showModel = rs2 && super.bipedRightArm.showModel;
      if(this.druidTotem != null) this.druidTotem.showModel = rs2;
      if(this.druidVineBelt != null) this.druidVineBelt.showModel = rs2;

      boolean rs3 = showRobe && this.robeStyle == ItemWitchesClothes.STYLE_VOID;
      if(this.voidShroudFront != null) this.voidShroudFront.showModel = rs3;
      if(this.voidShroudBack != null) this.voidShroudBack.showModel = rs3;
      if(this.voidRibs != null) this.voidRibs.showModel = rs3;
      if(this.voidSoulCore != null) this.voidSoulCore.showModel = rs3;
      if(this.voidSpikeR != null) this.voidSpikeR.showModel = rs3;
      if(this.voidSpikeL != null) this.voidSpikeL.showModel = rs3;

      boolean rs4 = showRobe && this.robeStyle == ItemWitchesClothes.STYLE_GOTHIC;
      if(this.gothicSkirtUpper != null) this.gothicSkirtUpper.showModel = rs4;
      if(this.gothicSkirtLower != null) this.gothicSkirtLower.showModel = rs4;
      if(this.gothicRuff != null) this.gothicRuff.showModel = rs4;
      if(this.gothicCorset != null) this.gothicCorset.showModel = rs4;
      if(this.gothicRoseBrooch != null) this.gothicRoseBrooch.showModel = rs4;
      if(this.gothicPuffR != null) this.gothicPuffR.showModel = rs4 && super.bipedRightArm.showModel;
      if(this.gothicPuffL != null) this.gothicPuffL.showModel = rs4 && super.bipedLeftArm.showModel;

      // ==========================================
      // --- SHOE / BOOT VISIBILITY CONFIGURATION ---
      // ==========================================
      boolean showShoes = this.isShoeSlot && (super.bipedRightLeg.showModel || super.bipedLeftLeg.showModel);

      boolean ss0 = showShoes && this.shoeStyle == ItemWitchesClothes.STYLE_CLASSIC;
      if(this.shoeClassicR != null) this.shoeClassicR.showModel = ss0;
      if(this.shoeClassicTipR != null) this.shoeClassicTipR.showModel = ss0;
      if(this.shoeClassicBuckleR != null) this.shoeClassicBuckleR.showModel = ss0;
      if(this.shoeClassicL != null) this.shoeClassicL.showModel = ss0;
      if(this.shoeClassicTipL != null) this.shoeClassicTipL.showModel = ss0;
      if(this.shoeClassicBuckleL != null) this.shoeClassicBuckleL.showModel = ss0;

      boolean ss1 = showShoes && this.shoeStyle == ItemWitchesClothes.STYLE_ARCHMAGE;
      if(this.bootArchmageR != null) this.bootArchmageR.showModel = ss1;
      if(this.bootArchmageCuffR != null) this.bootArchmageCuffR.showModel = ss1;
      if(this.bootArchmageSpurR != null) this.bootArchmageSpurR.showModel = ss1;
      if(this.bootArchmageL != null) this.bootArchmageL.showModel = ss1;
      if(this.bootArchmageCuffL != null) this.bootArchmageCuffL.showModel = ss1;
      if(this.bootArchmageSpurL != null) this.bootArchmageSpurL.showModel = ss1;

      boolean ss2 = showShoes && this.shoeStyle == ItemWitchesClothes.STYLE_DRUID;
      if(this.wrapDruidR != null) this.wrapDruidR.showModel = ss2;
      if(this.wrapDruidStrapsR != null) this.wrapDruidStrapsR.showModel = ss2;
      if(this.wrapDruidCharmR != null) this.wrapDruidCharmR.showModel = ss2;
      if(this.wrapDruidL != null) this.wrapDruidL.showModel = ss2;
      if(this.wrapDruidStrapsL != null) this.wrapDruidStrapsL.showModel = ss2;
      if(this.wrapDruidCharmL != null) this.wrapDruidCharmL.showModel = ss2;

      boolean ss3 = showShoes && this.shoeStyle == ItemWitchesClothes.STYLE_VOID;
      if(this.bootVoidR != null) this.bootVoidR.showModel = ss3;
      if(this.bootVoidToeClawR != null) this.bootVoidToeClawR.showModel = ss3;
      if(this.bootVoidHeelSpurR != null) this.bootVoidHeelSpurR.showModel = ss3;
      if(this.bootVoidL != null) this.bootVoidL.showModel = ss3;
      if(this.bootVoidToeClawL != null) this.bootVoidToeClawL.showModel = ss3;
      if(this.bootVoidHeelSpurL != null) this.bootVoidHeelSpurL.showModel = ss3;

      boolean ss4 = showShoes && this.shoeStyle == ItemWitchesClothes.STYLE_GOTHIC;
      if(this.bootGothicR != null) this.bootGothicR.showModel = ss4;
      if(this.bootGothicLacesR != null) this.bootGothicLacesR.showModel = ss4;
      if(this.bootGothicHeelR != null) this.bootGothicHeelR.showModel = ss4;
      if(this.bootGothicRoseR != null) this.bootGothicRoseR.showModel = ss4;
      if(this.bootGothicL != null) this.bootGothicL.showModel = ss4;
      if(this.bootGothicLacesL != null) this.bootGothicLacesL.showModel = ss4;
      if(this.bootGothicHeelL != null) this.bootGothicHeelL.showModel = ss4;
      if(this.bootGothicRoseL != null) this.bootGothicRoseL.showModel = ss4;

      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

      // Bark Belt dynamic plate layering
      if(living != null) {
         ItemStack belt = living.getEquipmentInSlot(2);
         if(belt != null && belt.getItem() == Witchery.Items.BARK_BELT && super.bipedBody.showModel) {
            int var14 = Math.min(Witchery.Items.BARK_BELT.getChargeLevel(belt), Witchery.Items.BARK_BELT.getMaxChargeLevel(living));
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            this.renderBark(f5, super.bipedBody, this.bodyF, var14 >= 1);
            this.renderBark(f5, super.bipedBody, this.bodyB, var14 >= 1);
            --var14;
            this.renderBark(f5, super.bipedBody, this.spikeLowerLeft, var14 >= 1);
            this.renderBark(f5, super.bipedBody, this.spikeLowerRight, var14 >= 1);
            this.renderBark(f5, super.bipedBody, this.spikeUpperLeft, var14 >= 1);
            this.renderBark(f5, super.bipedBody, this.spikeUpperRight, var14 >= 1);
            --var14;
            ItemStack shoes = living.getEquipmentInSlot(1);
            if(shoes != null && shoes.getItem() instanceof ItemWitchesClothes) {
               this.renderBark(f5, super.bipedRightLeg, this.legRightF, var14 >= 1 && super.bipedBody.showModel, true);
               this.renderBark(f5, super.bipedRightLeg, this.legRightB, var14 >= 1 && super.bipedBody.showModel, true);
               --var14;
               this.renderBark(f5, super.bipedLeftLeg, this.legLeftF, var14 >= 1 && super.bipedBody.showModel, true);
               this.renderBark(f5, super.bipedLeftLeg, this.legLeftB, var14 >= 1 && super.bipedBody.showModel, true);
               --var14;
            }

            ItemStack robes = living.getEquipmentInSlot(3);
            if(robes != null && robes.getItem() instanceof ItemWitchesClothes) {
               this.renderBark(f5, super.bipedRightArm, this.armRightF, var14 >= 1);
               this.renderBark(f5, super.bipedRightArm, this.armRightOut, var14 >= 1);
               this.renderBark(f5, super.bipedRightArm, this.armRightB, var14 >= 1);
               --var14;
               this.renderBark(f5, super.bipedLeftArm, this.armLeftB, var14 >= 1);
               this.renderBark(f5, super.bipedLeftArm, this.armLeftF, var14 >= 1);
               this.renderBark(f5, super.bipedLeftArm, this.armLeftOut, var14 >= 1);
               --var14;
            }

            ItemStack hatStack = living.getEquipmentInSlot(4);
            if(hatStack != null && hatStack.getItem() instanceof ItemWitchesClothes) {
               this.renderBark(f5, super.bipedRightArm, this.headRight1, var14 >= 1);
               this.renderBark(f5, super.bipedRightArm, this.shoulderRight, var14 >= 1);
               --var14;
               this.renderBark(f5, super.bipedLeftArm, this.headLeft1, var14 >= 1);
               this.renderBark(f5, super.bipedLeftArm, this.shoulderLeft, var14 >= 1);
            }
         }
      }
   }

   private void renderBark(float f5, ModelRenderer bodyPart, ModelRenderer barkPiece, boolean visible) {
      this.renderBark(f5, bodyPart, barkPiece, visible, false);
   }

   private void renderBark(float f5, ModelRenderer bodyPart, ModelRenderer barkPiece, boolean visible, boolean leg) {
      if(visible) {
         GL11.glTranslatef(bodyPart.offsetX, bodyPart.offsetY, bodyPart.offsetZ);
         if(bodyPart.rotateAngleX == 0.0F && bodyPart.rotateAngleY == 0.0F && bodyPart.rotateAngleZ == 0.0F && !leg) {
            if(bodyPart.rotationPointX == 0.0F && bodyPart.rotationPointY == 0.0F && bodyPart.rotationPointZ == 0.0F) {
               barkPiece.render(f5);
            } else {
               GL11.glTranslatef(bodyPart.rotationPointX * f5, bodyPart.rotationPointY * f5, bodyPart.rotationPointZ * f5);
               barkPiece.render(f5);
               GL11.glTranslatef(-bodyPart.rotationPointX * f5, -bodyPart.rotationPointY * f5, -bodyPart.rotationPointZ * f5);
            }
         } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(bodyPart.rotationPointX * f5, bodyPart.rotationPointY * f5, bodyPart.rotationPointZ * f5);
            if(bodyPart.rotateAngleZ != 0.0F) {
               GL11.glRotatef(bodyPart.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            if(bodyPart.rotateAngleY != 0.0F) {
               GL11.glRotatef(bodyPart.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if(bodyPart.rotateAngleX != 0.0F) {
               GL11.glRotatef(bodyPart.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            GL11.glTranslatef(-bodyPart.rotationPointX * f5, -bodyPart.rotationPointY * f5, -bodyPart.rotationPointZ * f5);
            if(super.isSneak && leg) {
               GL11.glTranslatef(0.0F, -3.0F * f5, 4.0F * f5);
               barkPiece.render(f5);
            } else {
               barkPiece.render(f5);
            }

            GL11.glPopMatrix();
         }

         GL11.glTranslatef(-bodyPart.offsetX, -bodyPart.offsetY, -bodyPart.offsetZ);
      }
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }
}
