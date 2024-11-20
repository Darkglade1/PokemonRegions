package pokeregions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;

public class Leaf extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(Leaf.class.getSimpleName());

    private static final int MAX_UPGRADES = 2;

    public Leaf() {
        super(ID, 0, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        baseDamage = 6;
        this.selfRetain = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int numHits = 1;
        if (this.timesUpgraded >= MAX_UPGRADES) {
            numHits = magicNumber;
        }
        for (int i = 0; i < numHits; i++) {
            if (m != null) {
                atb(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
        }
    }

    public boolean canUpgrade() {
        return timesUpgraded < MAX_UPGRADES;
    }

    public void upgrade() {
        if (canUpgrade()) {
            upgradeName();
            if (this.timesUpgraded >= MAX_UPGRADES) {
                upgradeDamage(-3);
                baseMagicNumber = magicNumber = 2;
                rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                initializeDescription();
            } else {
                upgradeDamage(2);
            }
        }
    }

}
