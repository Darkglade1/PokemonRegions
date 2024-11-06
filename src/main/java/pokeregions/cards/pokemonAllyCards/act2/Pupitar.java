package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Pupitar extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Pupitar.class.getSimpleName());
    public static final int MOVE_1_DRAW = 4;
    public static final int MOVE_1_DISCARD = 1;
    public static final int MOVE_2_DAMAGE = 8;
    public static final int MOVE_2_BLOCK = 8;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Pupitar() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DRAW + DESCRIPTIONS[3] + MOVE_1_DISCARD + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_BLOCK + DESCRIPTIONS[6] + MOVE_2_DAMAGE + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act2.allyPokemon.Pupitar(x, y, this);
    }
}