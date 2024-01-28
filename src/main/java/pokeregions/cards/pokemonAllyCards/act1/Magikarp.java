package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Magikarp extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Magikarp.class.getSimpleName());

    public static final int MOVE_2_DAMAGE = 4;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 2;

    public Magikarp() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2];
        this.move2Description = DESCRIPTIONS[3] + MOVE_2_DAMAGE + DESCRIPTIONS[4];
        initializeDescriptionFromMoves();
        this.cardsToPreview = new Gyarados();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act1.allyPokemon.Magikarp(x, y, this);
    }
}