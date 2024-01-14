package pokeregions.ui;

import basemod.abstracts.CustomScreen;
import pokeregions.patches.PlayerSpireFields;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class PokemonTeamViewScreen extends CustomScreen {

	public static final String ID = makeID(PokemonTeamViewScreen.class.getSimpleName());
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
	private static final String[] TEXT = uiStrings.TEXT;

	private static float drawStartX;
	private static float drawStartY;
	private static float padX;
	private static float padY;
	public CardGroup targetGroup;
	private AbstractCard hoveredCard = null;
	private static final int CARDS_PER_LINE = PokemonTeamButton.MAX_TEAM_SIZE;
    public static class Enum {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen POKEMON_TEAM_VIEW_SCREEN;
    }

	public PokemonTeamViewScreen() {
		drawStartX = (float)Settings.WIDTH;
		drawStartX -= 5.0F * AbstractCard.IMG_WIDTH * 0.75F;
		drawStartX -= 4.0F * Settings.CARD_VIEW_PAD_X;
		drawStartX /= 2.0F;
		drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;
		padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
		padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
	}

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return Enum.POKEMON_TEAM_VIEW_SCREEN;
    }


    private void open() {
        reopen();
    }

    @Override
    public void reopen() {
		targetGroup = PlayerSpireFields.pokemonTeam.get(adp());
		if (this.targetGroup.group.size() <= CARDS_PER_LINE) {
			drawStartY = (float)Settings.HEIGHT * 0.5F;
		} else {
			drawStartY = (float)Settings.HEIGHT * 0.66F;
		}
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.isScreenUp = true;
		AbstractDungeon.overlayMenu.showBlackScreen(0.75F);
		AbstractDungeon.topPanel.unhoverHitboxes();
		AbstractDungeon.overlayMenu.hideCombatPanels();
		AbstractDungeon.overlayMenu.proceedButton.hide();
		this.hideCards();
		AbstractDungeon.overlayMenu.cancelButton.show(TEXT[2]);
    }

    @Override
    public void openingSettings() {
    }

    @Override
    public void close() {
		genericScreenOverlayReset();
		AbstractDungeon.overlayMenu.hideBlackScreen();
		AbstractDungeon.overlayMenu.cancelButton.hide();
    }

    @Override
    public void update() {
		updateCardPositionsAndHoverLogic();
    }

    @Override
    public void render(SpriteBatch sb) {
		if (this.hoveredCard != null) {
			if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
				this.targetGroup.renderExceptOneCard(sb, this.hoveredCard);
			} else {
				this.targetGroup.renderExceptOneCardShowBottled(sb, this.hoveredCard);
			}
			this.hoveredCard.renderHoverShadow(sb);
			this.hoveredCard.render(sb);
			this.hoveredCard.renderCardTip(sb);
		} else if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
			this.targetGroup.render(sb);
		} else {
			this.targetGroup.renderShowBottled(sb);
		}
		FontHelper.renderDeckViewTip(sb, TEXT[0] + PokemonTeamButton.MAX_TEAM_SIZE + TEXT[1], 96.0F * Settings.scale, Settings.CREAM_COLOR);
    }

    @Override
    public boolean allowOpenDeck() {
        return true;
    }

    @Override
    public boolean allowOpenMap() {
        return true;
    }

	private void updateCardPositionsAndHoverLogic() {
		int lineNum = 0;
		ArrayList<AbstractCard> cards = this.targetGroup.group;
		for(int i = 0; i < cards.size(); ++i) {
			int mod = i % CARDS_PER_LINE;
			if (mod == 0 && i != 0) {
				++lineNum;
			}
			cards.get(i).target_x = drawStartX + (float)mod * padX;
			cards.get(i).target_y = drawStartY - (float)lineNum * padY;
			cards.get(i).fadingOut = false;
			cards.get(i).update();
			cards.get(i).updateHoverLogic();
			cards.get(i).setAngle(0.0F, true);
			this.hoveredCard = null;
			for (AbstractCard c : cards) {
				if (c.hb.hovered) {
					this.hoveredCard = c;
				}
			}
		}
	}

	private void hideCards() {
		int lineNum = 0;
		ArrayList<AbstractCard> cards = this.targetGroup.group;

		for(int i = 0; i < cards.size(); ++i) {
			cards.get(i).setAngle(0.0F, true);
			int mod = i % 5;
			if (mod == 0 && i != 0) {
				++lineNum;
			}

			cards.get(i).lighten(true);
			cards.get(i).current_x = drawStartX + (float)mod * padX;
			cards.get(i).current_y = drawStartY - (float)lineNum * padY - MathUtils.random(100.0F * Settings.scale, 200.0F * Settings.scale);
			cards.get(i).targetDrawScale = 0.75F;
			cards.get(i).drawScale = 0.75F;
		}

	}
}