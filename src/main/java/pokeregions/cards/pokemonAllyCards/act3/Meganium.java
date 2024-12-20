package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.MeganiumAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Meganium extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Meganium.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 7;
    public static final int MOVE_2_HP_LOSS = 9;
    public static final int MOVE_2_TEMP_HP = 6;
    public static final int MOVE_2_HEAL = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Meganium() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_HP_LOSS + DESCRIPTIONS[5] + MOVE_2_TEMP_HP + DESCRIPTIONS[6] + MOVE_2_HEAL + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MeganiumAlly(x, y, this);
    }
}