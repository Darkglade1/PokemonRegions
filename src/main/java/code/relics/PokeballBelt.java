package code.relics;

import basemod.BaseMod;
import basemod.helpers.CardPowerTip;
import code.actions.HealPokemonCampfireOption;
import code.cards.Pokeball;
import code.monsters.AbstractPokemonMonster;
import code.ui.PokemonTeamButton;
import code.util.TexLoader;
import code.util.Wiz;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.RelicWithButton;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.CoffeeDripper;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.adp;

public class PokeballBelt extends AbstractEasyRelic implements RelicWithButton {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());
    public static final int STARTING_POKEBALLS = 6;

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = STARTING_POKEBALLS;
        fixDescription();
    }

    @Override
    public Texture getTexture() {
        return TexLoader.getTexture(makeUIPath("SwitchPokemonButton.png"));
    }

    @Override
    public void onButtonPress() {
        Wiz.makeInHand(new Pokeball());
    }

    @Override
    public boolean isButtonDisabled() {
        if (!roomhasPokemon()) {
            //atb(new TalkAction(true, DESCRIPTIONS[2], 0.8F, 0.8F));
            return true;
        }
        if (this.counter < 1) {
            //atb(new TalkAction(true, DESCRIPTIONS[3], 0.8F, 0.8F));
            return true;
        }
        if (hasPokeball()) {
            //atb(new TalkAction(true, DESCRIPTIONS[4], 0.8F, 0.8F));
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<PowerTip> getHoverTips() {
        ArrayList<PowerTip> tips = new ArrayList<>();
        PowerTip tip = new PowerTip(DESCRIPTIONS[2], DESCRIPTIONS[3]);
        tips.add(tip);
        return tips;
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
    public void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        tips.add(new CardPowerTip(new Pokeball()));
        this.initializeTips();
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        options.add(new HealPokemonCampfireOption());
    }

    @Override
    public boolean canUseCampfireOption(AbstractCampfireOption option) {
        if (option instanceof HealPokemonCampfireOption && adp().hasRelic(CoffeeDripper.ID)) {
            ((HealPokemonCampfireOption)option).updateUsability(false);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }
}
