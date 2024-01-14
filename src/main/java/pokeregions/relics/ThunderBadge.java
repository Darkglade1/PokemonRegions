package pokeregions.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.FiredUp;

import static pokeregions.PokemonRegions.makeID;

public class ThunderBadge extends AbstractEasyRelic {
    public static final String ID = makeID(ThunderBadge.class.getSimpleName());
    private static final int DAMAGE_BOOST = 25;

    public ThunderBadge() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        if (monster instanceof AbstractPokemonAlly) {
            monster.addPower(new FiredUp(monster, DAMAGE_BOOST));
            AbstractDungeon.onModifyPower();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAMAGE_BOOST + DESCRIPTIONS[1];
    }
}
