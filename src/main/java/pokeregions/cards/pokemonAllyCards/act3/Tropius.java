package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Tropius extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Tropius.class.getSimpleName());
    public static final int MOVE_1_EFFECT = 2;
    public static final int MOVE_2_DAMAGE = 16;
    public static final int MOVE_2_DEBUFF = 2;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 6;

    public Tropius() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_EFFECT + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5] + MOVE_2_DEBUFF + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act3.allyPokemon.Tropius(x, y, this);
    }
}