package pokeregions.cards.pokemonAllyCards.act1;

import basemod.cardmods.RetainMod;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Grovyle;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.TreeckoAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Treecko extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Treecko.class.getSimpleName());
    public static final int MOVE_1_EFFECT = 1;
    public static final int MOVE_2_DAMAGE = 8;
    public static final int MOVE_2_ENERGY = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Treecko() {
        super(ID, CardRarity.BASIC);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_EFFECT + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5];
        this.cardsToPreview = getShiv().makeStatEquivalentCopy();
        initializeDescriptionFromMoves();
    }

    public static AbstractCard getShiv() {
        AbstractCard shiv = new Shiv();
        CardModifierManager.addModifier(shiv, new RetainMod());
        shiv.upgrade();
        return shiv;
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Grovyle();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new TreeckoAlly(x, y, this);
    }
}