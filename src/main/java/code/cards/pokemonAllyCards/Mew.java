package code.cards.pokemonAllyCards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.PokemonRegions.makeID;

@NoPools
public class Mew extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Mew.class.getSimpleName());
    public static final int MAX_STAMINA = 6;
    public Mew() {
        super(ID, CardRarity.RARE);
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.overrideWithDescription = true;
        initializeDescriptionFromMoves();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new code.monsters.act1.allyPokemon.Mew(x, y, this);
    }
}