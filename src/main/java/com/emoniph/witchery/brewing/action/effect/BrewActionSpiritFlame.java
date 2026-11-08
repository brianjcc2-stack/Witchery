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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class BrewActionSpiritFlame extends BrewActionEffect {

    public BrewActionSpiritFlame(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null) {
            float damage = 8.0F + (float)modifiers.getStrength() * 4.0F;
            DamageSource src = modifiers.caster != null ? DamageSource.causeIndirectMagicDamage(modifiers.caster, modifiers.caster) : DamageSource.magic;
            src.setDamageBypassesArmor();
            targetEntity.attackEntityFrom(src, damage);
            targetEntity.setFire(8 + modifiers.getStrength() * 4);
            ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, targetEntity, 1.0D, 1.5D, 20);
        }
    }
}

