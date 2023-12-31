package code.cards.pokemonAllyCards;

import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.PokemonRegions.makeID;

public class Diglett extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Diglett.class.getSimpleName());
    public static final int MOVE_2_DAMAGE = 16;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 3;

    public Diglett() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = cardStrings.EXTENDED_DESCRIPTION[2];
        this.move2Description = cardStrings.EXTENDED_DESCRIPTION[3] + MOVE_2_DAMAGE + cardStrings.EXTENDED_DESCRIPTION[4];
        initializeDescriptionFromMoves();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new code.monsters.act1.allyPokemon.Diglett(x, y, this);
    }
}