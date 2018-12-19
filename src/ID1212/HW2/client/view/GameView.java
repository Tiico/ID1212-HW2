package ID1212.HW2.client.view;

import ID1212.HW2.shared.GameActionFeedback;
import ID1212.HW2.shared.GameInfo;

public interface GameView {
    void displayGameFeedback(GameActionFeedback gameActionFeedback);
    void displayGameInfo(GameInfo gameInfo);
    void displayTechnicalFeedback(String string);
}
