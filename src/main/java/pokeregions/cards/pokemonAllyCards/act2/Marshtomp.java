package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Swampert;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.MarshtompAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Marshtomp extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Marshtomp.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 6;
    public static final int MOVE_2_BLOCK = 12;
    public static final int MOVE_2_EFFECT = 2;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 5;

    public Marshtomp() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5] + MOVE_2_EFFECT + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Swampert();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MarshtompAlly(x, y, this);
    }
}