package code.relics;

import basemod.BaseMod;
import basemod.helpers.CardPowerTip;
import code.cards.Pokeball;
import code.monsters.AbstractPokemonMonster;
import code.ui.PokemonTeamButton;
import code.util.Wiz;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;
import static code.util.Wiz.atb;

public class PokeballBelt extends AbstractEasyRelic implements ClickableRelic {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());
    public static final int STARTING_POKEBALLS = 6;
    private AbstractCard pokeball = new Pokeball();

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = STARTING_POKEBALLS;
        fixDescription();
    }

    @Override
    public void onRightClick()
    {
        if (!roomhasPokemon()) {
            atb(new TalkAction(true, DESCRIPTIONS[2], 0.8F, 0.8F));
            return;
        }
        if (this.counter < 1) {
            atb(new TalkAction(true, DESCRIPTIONS[3], 0.8F, 0.8F));
            return;
        }
        if (hasPokeball()) {
            atb(new TalkAction(true, DESCRIPTIONS[4], 0.8F, 0.8F));
            return;
        }
        Wiz.makeInHand(new Pokeball());
    }

    private boolean roomhasPokemon() {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof AbstractPokemonMonster) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPokeball() {
        for (AbstractCard card : adp().hand.group) {
            if (card.cardID.equals(Pokeball.ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }
}
