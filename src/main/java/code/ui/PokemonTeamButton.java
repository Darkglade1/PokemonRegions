package code.ui;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.abstracts.CustomSavable;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.patches.PlayerSpireFields;
import code.util.PokeballMove;
import code.util.Tags;
import code.util.TexLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.List;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.adp;

public class PokemonTeamButton extends TopPanelItem implements CustomSavable<List<CardSave>> {
    private static final Texture IMG = TexLoader.getTexture(makeUIPath("PokemonTeamButton.png"));
    public static final String ID = makeID(PokemonTeamButton.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    public static final int MAX_TEAM_SIZE = 5;
    private boolean releasingPokemon = false;
    public final PokeballMove pokeballMove;

    public PokemonTeamButton() {
        super(IMG, ID);
        pokeballMove = new PokeballMove();
        pokeballMove.setX(1775.0f * Settings.scale);
        pokeballMove.setY(150.0f * Settings.scale);
    }

    @Override
    protected void onClick() {
        CardCrawlGame.sound.play("DECK_OPEN");
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        String tipMsg = TEXT[0] + MAX_TEAM_SIZE + TEXT[1];
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.gridSelectScreen.open(pokemonTeam, 999, tipMsg, false, false, true, false);
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[2]);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, Integer.toString(pokemonTeam.size()), this.x + 58.0f * Settings.scale, this.y + 25.0f * Settings.scale, Color.WHITE.cpy());
        if (pokeballMove != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            pokeballMove.render(sb);
        }
    }

    @Override
    public List<CardSave> onSave() {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        ArrayList<CardSave> retVal = new ArrayList<>();
        for (AbstractCard card : pokemonTeam.group) {
            retVal.add(new CardSave(card.cardID, card.timesUpgraded, card.misc));
        }
        return retVal;
    }

    @Override
    public void onLoad(List<CardSave> cardSaves) {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        for (CardSave s : cardSaves) {
            AbstractCard card = CardLibrary.getCopy(s.id, s.upgrades, s.misc);
            if (card instanceof AbstractAllyPokemonCard) {
                ((AbstractAllyPokemonCard) card).currentStamina = s.misc;
                ((AbstractAllyPokemonCard) card).initializeDescriptionFromMoves();
            }
            pokemonTeam.addToBottom(card);
        }
        ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
        for (TopPanelItem item : topPanelItems) {
            if (item instanceof PokemonTeamButton) {
                return;
            }
        }
        BaseMod.addTopPanelItem(this);
    }

    @Override
    protected void onHover() {
        super.onHover();
        if (this.hitbox.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
        if (this.hitbox.hovered) {
            TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, TEXT[3], TEXT[4]);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.releasingPokemon && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            this.releasingPokemon = false;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
            PlayerSpireFields.pokemonTeam.get(adp()).removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        }
        if (pokeballMove != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            pokeballMove.update();
        }
    }

    public void releaseExcessPokemon() {
        CardGroup releaseablePokemon =  new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                releaseablePokemon.addToTop(card);
            }
        }
        if (releaseablePokemon.size() > 0) {
            this.releasingPokemon = true;
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            AbstractDungeon.gridSelectScreen.open(releaseablePokemon, 1, TEXT[5], false, false, false, true);
        }
    }

    public static void teamWideHeal(float percent) {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card instanceof AbstractAllyPokemonCard) {
                AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard) card;
                int staminaHeal = Math.round(pokemonCard.maxStamina * percent);
                pokemonCard.updateStamina(pokemonCard.currentStamina + staminaHeal);
            }
        }
    }
}