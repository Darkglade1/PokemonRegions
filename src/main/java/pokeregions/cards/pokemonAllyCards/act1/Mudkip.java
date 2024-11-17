package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Marshtomp;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.MudkipAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Mudkip extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Mudkip.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 5;
    public static final int MOVE_2_BLOCK = 9;
    public static final int MOVE_2_EFFECT = 2;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 5;

    public Mudkip() {
        super(ID, CardRarity.BASIC);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5] + MOVE_2_EFFECT + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Marshtomp();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MudkipAlly(x, y, this);
    }
}