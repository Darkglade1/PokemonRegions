package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.adp;

@AutoAdd.Ignore
public class Harm extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Harm.class.getSimpleName());

    private static final int HP_LOSS = 6;

    public Harm() {
        super(ID, CardType.CURSE, CardColor.CURSE);
        setMagic(HP_LOSS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        CardCrawlGame.sound.play("BLUNT_FAST");
        adp().damage(new DamageInfo(null, magicNumber));
    }
}
