package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.FeraligatrAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Feraligatr extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Feraligatr.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 7;
    public static final int MOVE_2_DAMAGE = 8;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Feraligatr() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[2] + MOVE_2_DAMAGE + DESCRIPTIONS[3] + DESCRIPTIONS[4];
        initializeDescriptionFromMoves();
    }


    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new FeraligatrAlly(x, y, this);
    }
}