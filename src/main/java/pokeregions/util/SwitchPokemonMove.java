package pokeregions.util;

import basemod.ClickableUIElement;
import pokeregions.actions.SwitchPokemonAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.patches.PlayerSpireFields;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;


public class SwitchPokemonMove extends ClickableUIElement {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("SwitchPokemon"));
    private static final String[] TEXT = uiStrings.TEXT;
    private static final Texture moveImage = TexLoader.getTexture(makeUIPath("SwitchPokemonButton.png"));

    private final String ID = TEXT[3];

    public SwitchPokemonMove() {
        super(moveImage, 0, 0, 64.0f, 64.0f);
    }

    private void doMove() {
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            if (action instanceof SwitchPokemonAction) {
                return;
            }
        }
        atb(new SwitchPokemonAction());
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x, this.y - 15f * Settings.scale, this.ID, TEXT[0]);
        if (this.hitbox.justHovered && !AbstractDungeon.isScreenUp && canSwitch()) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
    }

    @Override
    protected void onUnhover() {

    }

    @Override
    protected void onClick() {
        if (!canSwitch()) {
            atb(new TalkAction(true, TEXT[1], 0.8F, 0.8F));
            return;
        }
        if(!AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard && !AbstractDungeon.isScreenUp){
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.doMove();
        }
    }

    public boolean canSwitch() {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card instanceof AbstractAllyPokemonCard) {
                int stamina = ((AbstractAllyPokemonCard) card).currentStamina;
                if (stamina > 0 && PlayerSpireFields.activePokemon.get(adp()).allyCard != card) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(SpriteBatch sb) {
        if(AbstractDungeon.actionManager.turnHasEnded || AbstractDungeon.isScreenUp || !canSwitch()){
            super.render(sb, Color.GRAY);
        } else if (this.hitbox.hovered) {
            super.render(sb, Color.GOLD);
        } else {
            super.render(sb);
        }
    }
}