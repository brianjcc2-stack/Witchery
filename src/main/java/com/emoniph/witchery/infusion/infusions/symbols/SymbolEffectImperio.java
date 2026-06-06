package com.emoniph.witchery.infusion.infusions.symbols;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntitySpellEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class SymbolEffectImperio extends SymbolEffectProjectile {

    public static final Map<EntityPlayer, EntityLivingBase> IMPERIO_TARGETS = new WeakHashMap<EntityPlayer, EntityLivingBase>();

    public SymbolEffectImperio(int effectID, String unlocalisedName) {
        super(effectID, unlocalisedName, 200, false, false, "witchery.pott.imperio", 100);
        this.setSize(1.0f);
        this.setColor(0x00FF00);
    }

    @Override
    public void onCollision(World world, EntityLivingBase caster, MovingObjectPosition mop, EntitySpellEffect spell) {
        if (!world.isRemote && caster instanceof EntityPlayer && mop != null && mop.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) mop.entityHit;
            EntityPlayer player = (EntityPlayer) caster;

            // Apply paralysis to both for a very long time, but we will remove it manually when GUI closes
            player.addPotionEffect(new PotionEffect(Witchery.Potions.PARALYSED.id, 6000, 0, true));
            target.addPotionEffect(new PotionEffect(Witchery.Potions.PARALYSED.id, 6000, 0, true));

            IMPERIO_TARGETS.put(player, target);

            if (target instanceof EntityPlayer) {
                player.displayGUIChest(((EntityPlayer) target).inventory);
            } else {
                player.displayGUIChest(new InventoryMobEquipment(target));
            }
        }
    }
}
