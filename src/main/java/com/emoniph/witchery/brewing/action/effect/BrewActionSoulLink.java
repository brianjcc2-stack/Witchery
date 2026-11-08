package com.emoniph.witchery.brewing.action.effect;

import com.emoniph.witchery.brewing.AltarPower;
import com.emoniph.witchery.brewing.BrewItemKey;
import com.emoniph.witchery.brewing.BrewNamePart;
import com.emoniph.witchery.brewing.EffectLevel;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.Probability;
import com.emoniph.witchery.brewing.action.BrewActionEffect;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BrewActionSoulLink extends BrewActionEffect {

    public BrewActionSoulLink(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null) {
            long duration = 300L + (long)(modifiers.getStrength() * 100);
            targetEntity.getEntityData().setLong("WITCSoulLinkedUntil", world.getTotalWorldTime() + duration);
            ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, targetEntity, 1.0D, 1.5D, 20);
        }
    }
}

