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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BrewActionShadowFade extends BrewActionEffect {

    public BrewActionShadowFade(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null) {
            int duration = 140 + modifiers.getStrength() * 40;
            targetEntity.addPotionEffect(new PotionEffect(Potion.invisibility.id, duration, 1));
            targetEntity.addPotionEffect(new PotionEffect(Potion.resistance.id, duration, 4));
            targetEntity.addPotionEffect(new PotionEffect(Potion.weakness.id, duration, 5));
            targetEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, duration, 2));
            ParticleEffect.SMOKE.send(SoundEffect.RANDOM_FIZZ, targetEntity, 1.0D, 1.5D, 20);
        }
    }
}

