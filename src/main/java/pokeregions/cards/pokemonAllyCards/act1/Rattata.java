package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Rattata extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Rattata.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 8;
    public static final int MOVE_1_DRAW = 1;
    public static final int MOVE_2_BLOCK = 7;
    public static final int MOVE_2_DRAW = 1;
    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Rattata() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_DRAW + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_BLOCK + DESCRIPTIONS[6] + MOVE_2_DRAW + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act1.allyPokemon.Rattata(x, y, this);
    }
}